package org.soccorsoweb.business.controller.admin.details;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.soccorsoweb.business.controller.SoccorsoBaseController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.AggiornamentoDAO;
import org.soccorsoweb.data.dao.CommentoDAO;
import org.soccorsoweb.data.dao.MaterialeDAO;
import org.soccorsoweb.data.dao.MissioneDAO;
import org.soccorsoweb.data.dao.MezzoDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Aggiornamento;
import org.soccorsoweb.model.Commento;
import org.soccorsoweb.model.Materiale;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.Mezzo;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MissionDetailsController extends SoccorsoBaseController {

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

			Missione missione = ((MissioneDAO) dataLayer.getDAO(Missione.class)).getMissione(missioneId);
			if (missione == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			renderMissionDetails(request, response, dataLayer, missione);
		} catch (DataException | IOException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderMissionDetails(HttpServletRequest request, HttpServletResponse response,
			DataLayer dataLayer, Missione missione) throws IOException, DataException, ServletException {
		Map<String, Object> dettaglio = new HashMap<>();
		dettaglio.put("id", missione.getKey());
		dettaglio.put("richiesta_id", missione.getRichiesta() == null ? "" : missione.getRichiesta().getKey());
		dettaglio.put("stato", missione.isCompletata() ? "COMPLETATA" : "IN_CORSO");
		dettaglio.put("data_inizio", missione.getInizio() == null ? "" : missione.getInizio().toLocalDate());
		dettaglio.put("data_fine", missione.getFine() == null ? "" : missione.getFine().toLocalDate());
		dettaglio.put("obiettivo", missione.getObiettivo());
		dettaglio.put("indirizzo", getIndirizzo(missione));

		Squadra squadra = missione.getSquadra();
		dettaglio.put("caposquadra", squadra == null || squadra.getCapoSquadra() == null
				? "" : squadra.getCapoSquadra().getNomeUtente());
		dettaglio.put("operatori", getOperatori(squadra));
		dettaglio.put("mezzi", getMezzi(dataLayer, missione));
		dettaglio.put("materiali", getMateriali(dataLayer, missione));
		dettaglio.put("aggiornamenti", getAggiornamenti(dataLayer, missione));
		getCommenti(dataLayer, missione);

		Map<String, Object> model = new HashMap<>();
		model.put("ctx", request.getContextPath());
		model.put("dettaglio", dettaglio);

		response.setContentType("text/html;charset=UTF-8");
		try {
			Template template = cfg.getTemplate("details/missione-detail.ftl");
			template.process(model, response.getWriter());
		} catch (TemplateException ex) {
			throw new ServletException("Errore durante il rendering del dettaglio missione", ex);
		}
	}

	private String getIndirizzo(Missione missione) {
		return missione.getRichiesta() == null || missione.getRichiesta().getDescrizioneDettaglio() == null
				? "" : missione.getRichiesta().getDescrizioneDettaglio().getPosizione();
	}

	private List<Map<String, Object>> getOperatori(Squadra squadra) {
		List<Map<String, Object>> operatori = new ArrayList<>();
		if (squadra == null || squadra.getOperatori() == null) {
			return operatori;
		}
		for (Utente operatore : squadra.getOperatori()) {
			Map<String, Object> item = new HashMap<>();
			item.put("nome", operatore.getNomeUtente());
			Utente capoSquadra = squadra.getCapoSquadra();
			item.put("ruolo", capoSquadra != null && operatore.getKey().equals(capoSquadra.getKey())
					? "Caposquadra" : "Operatore");
			operatori.add(item);
		}
		return operatori;
	}

	private List<String> getMezzi(DataLayer dataLayer, Missione missione) throws DataException {
		List<String> mezzi = new ArrayList<>();
		for (Mezzo mezzo : ((MezzoDAO) dataLayer.getDAO(Mezzo.class)).getMezziConStato()) {
			if (missione.getKey().equals(mezzo.getMissioneKey())) {
				mezzi.add(mezzo.getNome());
			}
		}
		return mezzi;
	}

	private List<String> getMateriali(DataLayer dataLayer, Missione missione) throws DataException {
		List<String> materiali = new ArrayList<>();
		for (Materiale materiale : ((MaterialeDAO) dataLayer.getDAO(Materiale.class)).getMateriali()) {
			if (missione.getKey().equals(materiale.getMissioneKey())) {
				materiali.add(materiale.getNome());
			}
		}
		return materiali;
	}

	private List<Map<String, Object>> getAggiornamenti(DataLayer dataLayer, Missione missione) throws DataException {
		List<Map<String, Object>> aggiornamenti = new ArrayList<>();
		for (Aggiornamento aggiornamento : ((AggiornamentoDAO) dataLayer.getDAO(Aggiornamento.class))
				.getAggiornamentiByMissione(missione)) {
			Map<String, Object> item = new HashMap<>();
			item.put("timestamp", aggiornamento.getTimestamp() == null ? "" : aggiornamento.getTimestamp());
			item.put("content", aggiornamento.getTesto());
			aggiornamenti.add(item);
		}
		return aggiornamenti;
	}

	private void getCommenti(DataLayer dataLayer, Missione missione) throws DataException {
		List<Commento> commenti = ((CommentoDAO) dataLayer.getDAO(Commento.class)).getCommentiByMissione(missione);
		missione.setCommenti(commenti);
	}

	private Integer parseId(HttpServletRequest request) {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		try {
			return Integer.valueOf(path.substring(path.lastIndexOf('/') + 1));
		} catch (NumberFormatException ex) {
			return null;
		}
	}

}
