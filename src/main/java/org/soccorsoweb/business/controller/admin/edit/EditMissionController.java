package org.soccorsoweb.business.controller.admin.edit;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.soccorsoweb.business.controller.AbstractIdRequiredController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.AggiornamentoDAO;
import org.soccorsoweb.data.dao.CommentoDAO;
import org.soccorsoweb.data.dao.MaterialeDAO;
import org.soccorsoweb.data.dao.MezzoDAO;
import org.soccorsoweb.data.dao.MissioneDAO;
import org.soccorsoweb.data.dao.SquadraDAO;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.Aggiornamento;
import org.soccorsoweb.model.Commento;
import org.soccorsoweb.model.Materiale;
import org.soccorsoweb.model.Mezzo;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.model.enums.EsitoMissione;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class EditMissionController extends AbstractIdRequiredController {

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		if (!SecurityHelpers.isAdmin(request)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		Integer missioneId = getRequestId(request);
		if (missioneId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			
			if (this.dl == null) {
				throw new ServletException("DataLayer non inizializzato");
			}
			if ("GET".equalsIgnoreCase(request.getMethod())) {
				renderEditMission(request, response, this.dl, missioneId);
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
			editMission(request, response, this.dl, missioneId);
		} catch (IOException | DataException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderEditMission(HttpServletRequest request, HttpServletResponse response,
			DataLayer dl, int missioneId) throws IOException, DataException, ServletException {
		Missione missione = ((MissioneDAO) this.dl.getDAO(Missione.class)).getMissione(missioneId);;
		if (missione == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Map<String, Object> dettaglio = new HashMap<>();
		dettaglio.put("id", missione.getKey());
		dettaglio.put("richiesta_id", missione.getRichiesta() == null ? "" : missione.getRichiesta().getKey());
		dettaglio.put("stato", missione.isCompletata() ? "COMPLETATA" : "IN_CORSO");
		dettaglio.put("esito", missione.getEsito() == null ? "" : String.valueOf(missione.getEsito().ordinal()));
		dettaglio.put("data_inizio", missione.getInizio() == null ? "" : missione.getInizio().toLocalDate());
		dettaglio.put("data_fine", missione.getFine() == null ? "" : missione.getFine().toLocalDate());
		dettaglio.put("obiettivo", missione.getObiettivo());
		dettaglio.put("indirizzo", missione.getRichiesta() == null || missione.getRichiesta().getDescrizioneDettaglio() == null
				? "" : missione.getRichiesta().getDescrizioneDettaglio().getPosizione());
		dettaglio.put("idSquadra", missione.getSquadra() == null ? ""
				: String.valueOf(missione.getSquadra().getKey()));
		MezzoDAO mezzoDAO = (MezzoDAO) this.dl.getDAO(Mezzo.class);
		MaterialeDAO materialeDAO = (MaterialeDAO) this.dl.getDAO(Materiale.class);
		List<Mezzo> assignedMezzi = assignedVehicles(mezzoDAO, missione);
		List<Materiale> assignedMateriali = assignedMaterials(materialeDAO, missione);
		dettaglio.put("mezzi", assignedMezzi.stream()
				.map(mezzo -> String.valueOf(mezzo.getKey())).toList());
		dettaglio.put("materiali", assignedMateriali.stream()
				.map(materiale -> String.valueOf(materiale.getKey())).toList());

		Map<String, Object> model = new HashMap<>();
		model.put("ctx", request.getContextPath());
		model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
		model.put("dettaglio", dettaglio);
		SquadraDAO squadraDAO = (SquadraDAO) this.dl.getDAO(Squadra.class);
		model.put("squadreDisponibili", teamOptions(squadraDAO, missione.getSquadra()));
		model.put("mezziDisponibili", vehicleOptions(mezzoDAO, assignedMezzi));
		model.put("materialiDisponibili", materialOptions(materialeDAO, assignedMateriali));
		renderTemplate(response, "edit/missione-edit.ftl", model);
	}

	private void editMission(HttpServletRequest request, HttpServletResponse response,
			DataLayer dl, int missioneId) throws IOException, DataException, ServletException {
		Missione missione = ((MissioneDAO) this.dl.getDAO(Missione.class)).getMissione(missioneId);;
		Integer squadraId = parseId(request.getParameter("squadra"), "Squadra");
		Set<Integer> requestedMezzi = parseIds(request.getParameterValues("mezzi"), "Mezzo");
		Set<Integer> requestedMateriali = parseIds(request.getParameterValues("materiali"), "Materiale");
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

		SquadraDAO squadraDAO = (SquadraDAO) this.dl.getDAO(Squadra.class);
		Squadra squadra = squadraId == null ? null : squadraDAO.getSquadra(squadraId);
		String dataInizio = request.getParameter("data_inizio");
		String dataFine = request.getParameter("data_fine");
		if (squadra == null || dataInizio == null || dataInizio.isBlank()
				|| !isTeamAssignable(squadraDAO, squadra, missione.getSquadra())) {
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
					: ((UtenteDAO) this.dl.getDAO(Utente.class)).getUtente(adminId);
			if (admin == null) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			Commento commento = ((CommentoDAO) this.dl.getDAO(Commento.class)).createCommento();
			commento.setAdmin(admin);
			commento.setTesto(testoCommento);
			((CommentoDAO) this.dl.getDAO(Commento.class)).storeCommento(commento, missioneId);
		} else if ("IN_CORSO".equalsIgnoreCase(stato)) {
			String testoAggiornamento = SecurityHelpers.sanitizeTextInput(
					request.getParameter("nuovo_aggiornamento"));
			if (!testoAggiornamento.isBlank()) {
				Integer adminId = getSessionUserId(request);
				Utente admin = adminId == null ? null
						: ((UtenteDAO) this.dl.getDAO(Utente.class)).getUtente(adminId);
				if (admin == null) {
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
				Aggiornamento aggiornamento = ((AggiornamentoDAO) this.dl
						.getDAO(Aggiornamento.class)).createAggiornamento();
				aggiornamento.setAdmin(admin);
				aggiornamento.setTimestamp(LocalDateTime.now());
				aggiornamento.setTesto(testoAggiornamento);
				((AggiornamentoDAO) this.dl.getDAO(Aggiornamento.class))
						.storeAggiornamento(aggiornamento, missioneId);
			}
		} else {
			missione.setSuccesso(null);
		}
		MezzoDAO mezzoDAO = (MezzoDAO) this.dl.getDAO(Mezzo.class);
		MaterialeDAO materialeDAO = (MaterialeDAO) this.dl.getDAO(Materiale.class);
		updateVehicles(mezzoDAO, missione, requestedMezzi);
		updateMaterials(materialeDAO, missione, requestedMateriali);
		((MissioneDAO) this.dl.getDAO(Missione.class)).storeMissione(missione);

		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write("{\"success\":true,\"message\":\"Missione modificata con successo\"}");
	}

	private List<Mezzo> assignedVehicles(MezzoDAO dao, Missione missione) throws DataException {
		return dao.getMezziConStato().stream()
				.filter(mezzo -> missione.getKey().equals(mezzo.getMissioneKey())).toList();
	}

	private List<Materiale> assignedMaterials(MaterialeDAO dao, Missione missione) throws DataException {
		return dao.getMaterialiConStato().stream()
				.filter(materiale -> missione.getKey().equals(materiale.getMissioneKey())).toList();
	}

	private List<Map<String, Object>> teamOptions(SquadraDAO dao, Squadra assigned) throws DataException {
		Map<Integer, Squadra> options = new LinkedHashMap<>();
		for (Squadra squadra : dao.getSquadreDisponibili()) {
			options.put(squadra.getKey(), squadra);
		}
		if (assigned != null) {
			options.putIfAbsent(assigned.getKey(), assigned);
		}
		return options.values().stream().map(this::teamOption).toList();
	}

	private List<Map<String, Object>> vehicleOptions(MezzoDAO dao, List<Mezzo> assigned) throws DataException {
		Map<Integer, Mezzo> options = new LinkedHashMap<>();
		for (Mezzo mezzo : dao.getMezziDisponibili()) {
			options.put(mezzo.getKey(), mezzo);
		}
		for (Mezzo mezzo : assigned) {
			options.putIfAbsent(mezzo.getKey(), mezzo);
		}
		return options.values().stream().map(this::vehicleOption).toList();
	}

	private List<Map<String, Object>> materialOptions(MaterialeDAO dao, List<Materiale> assigned)
			throws DataException {
		Map<Integer, Materiale> options = new LinkedHashMap<>();
		for (Materiale materiale : dao.getMaterialiDisponibili()) {
			options.put(materiale.getKey(), materiale);
		}
		for (Materiale materiale : assigned) {
			options.putIfAbsent(materiale.getKey(), materiale);
		}
		return options.values().stream().map(this::materialOption).toList();
	}

	private void updateVehicles(MezzoDAO dao, Missione missione, Set<Integer> requested) throws DataException {
		Set<Integer> current = assignedVehicles(dao, missione).stream()
				.map(Mezzo::getKey).collect(java.util.stream.Collectors.toSet());
		Set<Integer> valid = new HashSet<>();
		for (Mezzo mezzo : dao.getMezziDisponibili()) {
			valid.add(mezzo.getKey());
		}
		valid.addAll(current);
		if (!valid.containsAll(requested)) {
			throw new DataException("Uno o più mezzi non sono disponibili");
		}
		for (Integer id : requested) {
			if (!current.contains(id)) {
				dao.assegnaMezzoAMissione(dao.getMezzo(id), missione);
			}
		}
		for (Integer id : current) {
			if (!requested.contains(id)) {
				dao.slegaMezzoDaMissione(dao.getMezzo(id), missione);
			}
		}
	}

	private void updateMaterials(MaterialeDAO dao, Missione missione, Set<Integer> requested) throws DataException {
		Set<Integer> current = assignedMaterials(dao, missione).stream()
				.map(Materiale::getKey).collect(java.util.stream.Collectors.toSet());
		Set<Integer> valid = new HashSet<>();
		for (Materiale materiale : dao.getMaterialiDisponibili()) {
			valid.add(materiale.getKey());
		}
		valid.addAll(current);
		if (!valid.containsAll(requested)) {
			throw new DataException("Uno o più materiali non sono disponibili");
		}
		for (Integer id : requested) {
			if (!current.contains(id)) {
				dao.assegnaMaterialeAMissione(dao.getMateriale(id), missione);
			}
		}
		for (Integer id : current) {
			if (!requested.contains(id)) {
				dao.slegaMaterialeDaMissione(dao.getMateriale(id), missione);
			}
		}
	}

	private boolean isTeamAssignable(SquadraDAO dao, Squadra selected, Squadra current) throws DataException {
		if (current != null && current.getKey().equals(selected.getKey())) {
			return true;
		}
		return dao.getSquadreDisponibili().stream()
				.anyMatch(squadra -> squadra.getKey().equals(selected.getKey()));
	}

	private Integer parseId(String value, String label) throws ServletException {
		try {
			return value == null || value.isBlank() ? null : Integer.valueOf(value);
		} catch (NumberFormatException ex) {
			throw new ServletException(label + " non valida", ex);
		}
	}

	private Set<Integer> parseIds(String[] values, String label) throws ServletException {
		Set<Integer> ids = new HashSet<>();
		if (values == null) {
			return ids;
		}
		for (String value : values) {
			Integer id = parseId(value, label);
			if (id != null) {
				ids.add(id);
			}
		}
		return ids;
	}

	private Map<String, Object> teamOption(Squadra squadra) {
		Map<String, Object> option = new HashMap<>();
		option.put("value", String.valueOf(squadra.getKey()));
		option.put("label", "Squadra #" + squadra.getKey());
		return option;
	}

	private Map<String, Object> vehicleOption(Mezzo mezzo) {
		Map<String, Object> option = new HashMap<>();
		option.put("value", String.valueOf(mezzo.getKey()));
		option.put("label", mezzo.getNome() + " (" + mezzo.getTarga() + ")");
		return option;
	}

	private Map<String, Object> materialOption(Materiale materiale) {
		Map<String, Object> option = new HashMap<>();
		option.put("value", String.valueOf(materiale.getKey()));
		option.put("label", materiale.getNome() + " (" + materiale.getCodMat() + ")");
		return option;
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

	private EsitoMissione parseEsito(String value) {
		try {
			if (value == null || value.isBlank()) {
				return null;
			}
			int level = Integer.parseInt(value);
			return level >= 1 && level <= 5 ? EsitoMissione.values()[level] : null;
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private Integer getSessionUserId(HttpServletRequest request) {
		Object value = request.getSession(false) == null ? null
				: request.getSession(false).getAttribute("userid");
		return value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(String.valueOf(value));
	}

}
