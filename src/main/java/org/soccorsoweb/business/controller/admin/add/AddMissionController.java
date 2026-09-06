package org.soccorsoweb.business.controller.admin.add;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.soccorsoweb.business.controller.SoccorsoBaseController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.MaterialeDAO;
import org.soccorsoweb.data.dao.MezzoDAO;
import org.soccorsoweb.data.dao.MissioneDAO;
import org.soccorsoweb.data.dao.RichiestaDAO;
import org.soccorsoweb.data.dao.SquadraDAO;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.framework.util.ServletHelpers;
import org.soccorsoweb.model.Anagrafica;
import org.soccorsoweb.model.Materiale;
import org.soccorsoweb.model.Mezzo;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.Richiesta;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
 
public class AddMissionController extends SoccorsoBaseController {

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		if (!SecurityHelpers.isAdmin(request)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		if (request.getRequestURI().endsWith("/mail")) {
			renderMissionMail(request, response);
			return;
		}

		if ("GET".equalsIgnoreCase(request.getMethod())) {
			renderAddMission(request, response);
			return;
		}
		if (!"POST".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}
		if (!SecurityHelpers.isValidCsrfToken(request, request.getParameter("csrf"))) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		try {
			
			if (this.dl == null) {
				throw new ServletException("DataLayer non inizializzato");
			}
			createMission(request, response, this.dl);
		} catch (DataException | IOException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderAddMission(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try{
			Map<String, Object> model = new HashMap<>();
			RichiestaDAO richestaDAO = (RichiestaDAO)this.dl.getDAO(Richiesta.class);
			Richiesta richiesta = richestaDAO.getRichiesta(Integer.parseInt(request.getParameter("richiesta_id")));
			SquadraDAO squadraDAO = (SquadraDAO)this.dl.getDAO(Squadra.class);
			List<Map<String, Object>> squadre = new ArrayList<>();
			for(Squadra s: squadraDAO.getSquadreDisponibili()){
				Map<String, Object> sMap = new HashMap<>();
				sMap.put("id", s.getKey());
				Anagrafica capoAnag = s.getCapoSquadra().getAnagrafica();
				sMap.put("caposquadra", capoAnag.getNome() + " " + capoAnag.getCognome());
				squadre.add(sMap);
			}

			MezzoDAO mezzoDAO = (MezzoDAO)this.dl.getDAO(Mezzo.class);
			List<Map<String, Object>> mezzi = new ArrayList<>();
			for(Mezzo m: mezzoDAO.getMezziDisponibili()){
				Map<String, Object> mMap = new HashMap<>();
				mMap.put("id", m.getKey());
				mMap.put("nome", m.getNome());
				mezzi.add(mMap);
			}

			MaterialeDAO materialeDAO = (MaterialeDAO)this.dl.getDAO(Materiale.class);
			List<Map<String, Object>> materiali = new ArrayList<>();
			for(Materiale m: materialeDAO.getMaterialiDisponibili()){
				Map<String, Object> mMap = new HashMap<>();
				mMap.put("id", m.getKey());
				mMap.put("nome", m.getNome());
				materiali.add(mMap);
			}

			model.put("ctx", request.getContextPath());
			model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
			model.put("currentTime", LocalDateTime.now().withSecond(0).withNano(0).toString());
			model.put("richiestaId", request.getParameter("richiesta_id"));
			Map<String, Object> item = new HashMap<>();
			item.put("id", request.getParameter("richiesta_id"));
			item.put("indirizzo", richiesta.getDescrizioneDettaglio().getPosizione());
			model.put("item", item);
			model.put("squadreDisponibili", squadre);
			model.put("mezziDisponibili", mezzi);
			model.put("materialiDisponibili", materiali);
			renderTemplate(response, "add/missione-add.ftl", model,
					"Errore durante il rendering del form missione");
		} catch(DataException e){
			throw new ServletException(e);
		}
	}

	private void createMission(HttpServletRequest request, HttpServletResponse response, DataLayer dl)
			throws IOException, DataException {
		Integer richiestaId = parseId(request.getParameter("richiesta_id"));
		Integer squadraId = parseId(request.getParameter("squadra"));
		String obiettivo = SecurityHelpers.sanitizeTextInput(request.getParameter("obiettivo"));
		String dataInizio = request.getParameter("data_inizio");
		if (richiestaId == null || squadraId == null || obiettivo.isBlank() || dataInizio == null
				|| dataInizio.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("{\"error\":\"Dati missione obbligatori non validi\"}");
			return;
		}

		Richiesta richiesta = ((RichiestaDAO) this.dl.getDAO(Richiesta.class)).getRichiesta(richiestaId);
		Squadra squadra = ((SquadraDAO) this.dl.getDAO(Squadra.class)).getSquadra(squadraId);
		Integer adminId = getSessionUserId(request);
		Utente admin = adminId == null ? null
				: ((UtenteDAO) this.dl.getDAO(Utente.class)).getUtente(adminId);
		if (richiesta == null || squadra == null || admin == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write("{\"error\":\"Richiesta, squadra o amministratore non trovato\"}");
			return;
		}

		Missione missione = ((MissioneDAO) this.dl.getDAO(Missione.class)).createMissione();
		missione.setRichiesta(richiesta);
		missione.setSquadra(squadra);
		missione.setAdmin(admin);
		missione.setObiettivo(obiettivo);
		missione.setInizio(LocalDateTime.parse(dataInizio));
		missione.setCompletata(false);
		((MissioneDAO) this.dl.getDAO(Missione.class)).storeMissione(missione);

		List<Utente> componenti = ((SquadraDAO) this.dl.getDAO(Squadra.class)).getOperatoriSquadra(squadraId);
		Map<String, Object> missioneMail = new HashMap<>();
		missioneMail.put("posizione", richiesta.getDescrizioneDettaglio() == null
				? "Non specificata" : richiesta.getDescrizioneDettaglio().getPosizione());
		Anagrafica anagCapo = squadra.getCapoSquadra().getAnagrafica();
		missioneMail.put("caposquadra", anagCapo.getNome() + " " + anagCapo.getCognome());
		missioneMail.put("inizio", missione.getInizio());
		missioneMail.put("obiettivo", missione.getObiettivo());
		request.getSession(true).setAttribute("mission.mail.missione", missioneMail);
		request.getSession().setAttribute("mission.mail.recipients", getRecipientEmails(componenti, this.dl));
		ServletHelpers.redirectAndOpenTab(response, request.getContextPath() + "/admin-dashboard?section=missions", request.getContextPath() + "/api/add/missioni/mail");
	}

	private void renderMissionMail(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		var session = request.getSession(false);
		if (session == null || session.getAttribute("mission.mail.missione") == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		Map<String, Object> model = new HashMap<>();
		model.put("missione", session.getAttribute("mission.mail.missione"));
		model.put("destinatari", session.getAttribute("mission.mail.recipients"));
		session.removeAttribute("mission.mail.missione");
		session.removeAttribute("mission.mail.recipients");
		renderTemplate(response, "email/missione_assegnata.ftl", model,
				"Errore durante il rendering delle email missione");
	}

	private List<String> getRecipientEmails(List<Utente> componenti, DataLayer dl) throws DataException {
		List<String> recipients = new ArrayList<>();
		var credenzialiDAO = (org.soccorsoweb.data.dao.CredenzialiDAO) this.dl.getDAO(org.soccorsoweb.model.Credenziali.class);
		for (Utente componente : componenti) {
			var credenziali = credenzialiDAO.getCredenzialiByUtente(componente);
			if (credenziali != null && credenziali.getEmail() != null && !credenziali.getEmail().isBlank()) {
				recipients.add(credenziali.getEmail());
			}
		}
		return recipients;
	}

	private void renderTemplate(HttpServletResponse response, String templateName, Map<String, Object> model,
			String errorMessage) throws ServletException {
		response.setContentType("text/html;charset=UTF-8");
		try {
			Template template = cfg.getTemplate(templateName);
			template.process(model, response.getWriter());
		} catch (IOException | TemplateException ex) {
			throw new ServletException(errorMessage, ex);
		}
	}

	private Integer parseId(String value) {
		try {
			return value == null ? null : Integer.valueOf(value);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private Integer getSessionUserId(HttpServletRequest request) {
		Object value = request.getSession(false) == null ? null
				: request.getSession(false).getAttribute("userid");
		return value instanceof Number ? ((Number) value).intValue() : parseId(String.valueOf(value));
	}

}
