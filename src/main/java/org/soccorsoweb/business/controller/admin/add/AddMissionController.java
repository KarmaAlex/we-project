package org.soccorsoweb.business.controller.admin.add;

import freemarker.template.Configuration;
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
import org.soccorsoweb.data.dao.MissioneDAO;
import org.soccorsoweb.data.dao.RichiestaDAO;
import org.soccorsoweb.data.dao.SquadraDAO;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.Richiesta;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
 
public class AddMissionController extends SoccorsoBaseController {

	private Configuration cfg;
	private Configuration emailCfg;

	@Override
	public void init() throws ServletException {
		super.init();
		cfg = new Configuration(Configuration.VERSION_2_3_34);
		cfg.setServletContextForTemplateLoading(getServletContext(), "/templates");
		cfg.setDefaultEncoding("UTF-8");
		emailCfg = new Configuration(Configuration.VERSION_2_3_34);
		emailCfg.setServletContextForTemplateLoading(getServletContext(), "/");
		emailCfg.setDefaultEncoding("UTF-8");
	}

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
			DataLayer dataLayer = (DataLayer) request.getAttribute("datalayer");
			if (dataLayer == null) {
				throw new ServletException("DataLayer non inizializzato");
			}
			createMission(request, response, dataLayer);
		} catch (DataException | IOException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderAddMission(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		Map<String, Object> model = new HashMap<>();
		model.put("ctx", request.getContextPath());
		model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
		model.put("currentTime", LocalDateTime.now().withSecond(0).withNano(0).toString());
		model.put("richiestaId", request.getParameter("richiesta_id"));
		Map<String, Object> item = new HashMap<>();
		item.put("id", request.getParameter("richiesta_id"));
		item.put("indirizzo", "");
		model.put("item", item);
		model.put("squadreDisponibili", List.of());
		model.put("mezziDisponibili", List.of());
		model.put("materialiDisponibili", List.of());
		renderTemplate(response, "add/missione-add.ftl", model,
				"Errore durante il rendering del form missione");
	}

	private void createMission(HttpServletRequest request, HttpServletResponse response, DataLayer dataLayer)
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

		Richiesta richiesta = ((RichiestaDAO) dataLayer.getDAO(Richiesta.class)).getRichiesta(richiestaId);
		Squadra squadra = ((SquadraDAO) dataLayer.getDAO(Squadra.class)).getSquadra(squadraId);
		Integer adminId = getSessionUserId(request);
		Utente admin = adminId == null ? null
				: ((UtenteDAO) dataLayer.getDAO(Utente.class)).getUtente(adminId);
		if (richiesta == null || squadra == null || admin == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write("{\"error\":\"Richiesta, squadra o amministratore non trovato\"}");
			return;
		}

		Missione missione = ((MissioneDAO) dataLayer.getDAO(Missione.class)).createMissione();
		missione.setRichiesta(richiesta);
		missione.setSquadra(squadra);
		missione.setAdmin(admin);
		missione.setObiettivo(obiettivo);
		missione.setInizio(LocalDateTime.parse(dataInizio));
		missione.setCompletata(false);
		((MissioneDAO) dataLayer.getDAO(Missione.class)).storeMissione(missione);

		List<Utente> componenti = ((SquadraDAO) dataLayer.getDAO(Squadra.class)).getOperatoriSquadra(squadraId);
		Map<String, Object> missioneMail = new HashMap<>();
		missioneMail.put("posizione", richiesta.getDescrizioneDettaglio() == null
				? "Non specificata" : richiesta.getDescrizioneDettaglio().getPosizione());
		missioneMail.put("ID_SQUADRA", squadra.getKey());
		missioneMail.put("inizio", missione.getInizio());
		missioneMail.put("obiettivo", missione.getObiettivo());
		request.getSession(true).setAttribute("mission.mail.missione", missioneMail);
		request.getSession().setAttribute("mission.mail.recipients", getRecipientEmails(componenti, dataLayer));
		response.sendRedirect(request.getContextPath() + "/api/add/missioni/mail");
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

	private List<String> getRecipientEmails(List<Utente> componenti, DataLayer dataLayer) throws DataException {
		List<String> recipients = new ArrayList<>();
		var credenzialiDAO = (org.soccorsoweb.data.dao.CredenzialiDAO) dataLayer.getDAO(org.soccorsoweb.model.Credenziali.class);
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
			Configuration templateConfiguration = templateName.startsWith("email/") ? emailCfg : cfg;
			Template template = templateConfiguration.getTemplate(templateName);
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
