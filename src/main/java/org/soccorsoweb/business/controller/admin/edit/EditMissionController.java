package org.soccorsoweb.business.controller.admin.edit;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.soccorsoweb.business.controller.SoccorsoBaseController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.AggiornamentoDAO;
import org.soccorsoweb.data.dao.CommentoDAO;
import org.soccorsoweb.data.dao.MissioneDAO;
import org.soccorsoweb.data.dao.SquadraDAO;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.Aggiornamento;
import org.soccorsoweb.model.Commento;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.model.enums.EsitoMissione;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class EditMissionController extends SoccorsoBaseController {

	private Configuration cfg;

	@Override
	public void init() throws ServletException {
		super.init();
		cfg = new Configuration(Configuration.VERSION_2_3_34);
		cfg.setServletContextForTemplateLoading(getServletContext(), "/templates");
		cfg.setDefaultEncoding("UTF-8");
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		if (!SecurityHelpers.isAdmin(request)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		Integer missioneId = parseId(request);
		if (missioneId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			DataLayer dataLayer = (DataLayer) request.getAttribute("datalayer");
			if (dataLayer == null) {
				throw new ServletException("DataLayer non inizializzato");
			}
			if ("GET".equalsIgnoreCase(request.getMethod())) {
				renderEditMission(request, response, dataLayer, missioneId);
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
			editMission(request, response, dataLayer, missioneId);
		} catch (IOException | DataException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderEditMission(HttpServletRequest request, HttpServletResponse response,
			DataLayer dataLayer, int missioneId) throws IOException, DataException, ServletException {
		Missione missione = ((MissioneDAO) dataLayer.getDAO(Missione.class)).getMissione(missioneId);;
		if (missione == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Map<String, Object> dettaglio = new HashMap<>();
		dettaglio.put("id", missione.getKey());
		dettaglio.put("richiesta_id", missione.getRichiesta() == null ? "" : missione.getRichiesta().getKey());
		dettaglio.put("stato", missione.isCompletata() ? "COMPLETATA" : "IN_CORSO");
		dettaglio.put("esito", missione.getEsito() == null ? "NON_DEFINITO" : missione.getEsito().name());
		dettaglio.put("data_inizio", missione.getInizio() == null ? "" : missione.getInizio().toLocalDate());
		dettaglio.put("data_fine", missione.getFine() == null ? "" : missione.getFine().toLocalDate());
		dettaglio.put("obiettivo", missione.getObiettivo());
		dettaglio.put("indirizzo", missione.getRichiesta() == null || missione.getRichiesta().getDescrizioneDettaglio() == null
				? "" : missione.getRichiesta().getDescrizioneDettaglio().getPosizione());
		dettaglio.put("idSquadra", missione.getSquadra() == null ? "" : missione.getSquadra().getKey());
		dettaglio.put("mezzi", java.util.List.of());
		dettaglio.put("materiali", java.util.List.of());

		Map<String, Object> model = new HashMap<>();
		model.put("ctx", request.getContextPath());
		model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
		model.put("dettaglio", dettaglio);
		model.put("squadreDisponibili", java.util.List.of());
		model.put("mezziDisponibili", java.util.List.of());
		model.put("materialiDisponibili", java.util.List.of());
		renderTemplate(response, "edit/missione-edit.ftl", model);
	}

	private void editMission(HttpServletRequest request, HttpServletResponse response,
			DataLayer dataLayer, int missioneId) throws IOException, DataException {
		Missione missione = ((MissioneDAO) dataLayer.getDAO(Missione.class)).getMissione(missioneId);;
		Integer squadraId = parseId(request.getParameter("squadra"));
		String obiettivo = SecurityHelpers.sanitizeTextInput(request.getParameter("obiettivo"));
		if (missione == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		if (obiettivo.isBlank() || squadraId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("{\"error\":\"Obiettivo e squadra sono obbligatori\"}");
			return;
		}

		Squadra squadra = ((SquadraDAO) dataLayer.getDAO(Squadra.class)).getSquadra(squadraId);
		String dataInizio = request.getParameter("data_inizio");
		String dataFine = request.getParameter("data_fine");
		if (squadra == null || dataInizio == null || dataInizio.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("{\"error\":\"Dati missione non validi\"}");
			return;
		}

		missione.setSquadra(squadra);
		missione.setObiettivo(obiettivo);
		missione.setInizio(LocalDate.parse(dataInizio).atStartOfDay());
		missione.setFine(dataFine == null || dataFine.isBlank() ? null : LocalDate.parse(dataFine).atStartOfDay());
		String stato = request.getParameter("stato");
		missione.setCompletata("COMPLETATA".equalsIgnoreCase(stato));
		if (missione.isCompletata()) {
			EsitoMissione esito = parseEsito(request.getParameter("esito"));
			if (esito == null || esito == EsitoMissione.NON_DEFINITO) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write("{\"error\":\"L'esito è obbligatorio per una missione completata\"}");
				return;
			}
			missione.setSuccesso(esito);

			String testoCommento = SecurityHelpers.sanitizeTextInput(request.getParameter("commento"));
			if (testoCommento.isBlank()) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write("{\"error\":\"Il commento è obbligatorio per una missione completata\"}");
				return;
			}
			Integer adminId = getSessionUserId(request);
			Utente admin = adminId == null
					? null
					: ((UtenteDAO) dataLayer.getDAO(Utente.class)).getUtente(adminId);
			if (admin == null) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			Commento commento = ((CommentoDAO) dataLayer.getDAO(Commento.class)).createCommento();
			commento.setAdmin(admin);
			commento.setTesto(testoCommento);
			((CommentoDAO) dataLayer.getDAO(Commento.class)).storeCommento(commento, missioneId);
		} else if ("IN_CORSO".equalsIgnoreCase(stato)) {
			String testoAggiornamento = SecurityHelpers.sanitizeTextInput(
					request.getParameter("nuovo_aggiornamento"));
			if (!testoAggiornamento.isBlank()) {
				Integer adminId = getSessionUserId(request);
				Utente admin = adminId == null ? null
						: ((UtenteDAO) dataLayer.getDAO(Utente.class)).getUtente(adminId);
				if (admin == null) {
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
				Aggiornamento aggiornamento = ((AggiornamentoDAO) dataLayer
						.getDAO(Aggiornamento.class)).createAggiornamento();
				aggiornamento.setAdmin(admin);
				aggiornamento.setTimestamp(LocalDateTime.now());
				aggiornamento.setTesto(testoAggiornamento);
				((AggiornamentoDAO) dataLayer.getDAO(Aggiornamento.class))
						.storeAggiornamento(aggiornamento, missioneId);
			}
		} else {
			missione.setSuccesso(null);
		}
		((MissioneDAO) dataLayer.getDAO(Missione.class)).storeMissione(missione);

		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write("{\"success\":true,\"message\":\"Missione modificata con successo\"}");
	}


	private void renderTemplate(HttpServletResponse response, String templateName, Map<String, Object> model)
			throws ServletException {
		response.setContentType("text/html;charset=UTF-8");
		try {
			Template template = cfg.getTemplate(templateName);
			template.process(model, response.getWriter());
		} catch (IOException | TemplateException ex) {
			throw new ServletException("Errore durante il rendering del form missione", ex);
		}
	}

	private Integer parseId(HttpServletRequest request) {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		return parseId(path.substring(path.lastIndexOf('/') + 1));
	}

	private Integer parseId(String value) {
		try {
			return value == null ? null : Integer.valueOf(value);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private EsitoMissione parseEsito(String value) {
		try {
			return value == null || value.isBlank() ? null : EsitoMissione.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	private Integer getSessionUserId(HttpServletRequest request) {
		Object value = request.getSession(false) == null ? null
				: request.getSession(false).getAttribute("userid");
		return value instanceof Number ? ((Number) value).intValue() : parseId(String.valueOf(value));
	}

}
