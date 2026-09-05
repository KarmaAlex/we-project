package org.soccorsoweb.business.controller.admin.details;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.soccorsoweb.business.controller.AbstractIdRequiredController;
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

public class MissionDetailsController extends AbstractIdRequiredController {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
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

			Missione missione = ((MissioneDAO) this.dl.getDAO(Missione.class)).getMissione(missioneId);
			if (missione == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			renderMissionDetails(request, response, this.dl, missione);
		} catch (DataException | IOException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderMissionDetails(HttpServletRequest request, HttpServletResponse response,
			DataLayer dl, Missione missione) throws IOException, DataException, ServletException {
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
		dettaglio.put("mezzi", getMezzi(this.dl, missione));
		dettaglio.put("materiali", getMateriali(this.dl, missione));
		dettaglio.put("aggiornamenti", getAggiornamenti(this.dl, missione));
		getCommenti(this.dl, missione);

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

	private List<String> getMezzi(DataLayer dl, Missione missione) throws DataException {
		List<String> mezzi = new ArrayList<>();
		for (Mezzo mezzo : ((MezzoDAO) this.dl.getDAO(Mezzo.class)).getMezziConStato()) {
			if (missione.getKey().equals(mezzo.getMissioneKey())) {
				mezzi.add(mezzo.getNome());
			}
		}
		return mezzi;
	}

	private List<String> getMateriali(DataLayer dl, Missione missione) throws DataException {
		List<String> materiali = new ArrayList<>();
		for (Materiale materiale : ((MaterialeDAO) this.dl.getDAO(Materiale.class)).getMateriali()) {
			if (missione.getKey().equals(materiale.getMissioneKey())) {
				materiali.add(materiale.getNome());
			}
		}
		return materiali;
	}

	private List<Map<String, Object>> getAggiornamenti(DataLayer dl, Missione missione) throws DataException {
		List<Map<String, Object>> aggiornamenti = new ArrayList<>();
		for (Aggiornamento aggiornamento : ((AggiornamentoDAO) this.dl.getDAO(Aggiornamento.class))
				.getAggiornamentiByMissione(missione)) {
			Map<String, Object> item = new HashMap<>();
			item.put("timestamp", aggiornamento.getTimestamp() == null ? "" : aggiornamento.getTimestamp());
			item.put("content", aggiornamento.getTesto());
			aggiornamenti.add(item);
		}
		return aggiornamenti;
	}

	private void getCommenti(DataLayer dl, Missione missione) throws DataException {
		List<Commento> commenti = ((CommentoDAO) this.dl.getDAO(Commento.class)).getCommentiByMissione(missione);
		missione.setCommenti(commenti);
	}


}
