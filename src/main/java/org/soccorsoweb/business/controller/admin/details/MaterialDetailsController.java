package org.soccorsoweb.business.controller.admin.details;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.soccorsoweb.business.controller.AbstractIdRequiredController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.MaterialeDAO;
import org.soccorsoweb.data.dao.MissioneDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Materiale;
import org.soccorsoweb.model.Missione;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MaterialDetailsController extends AbstractIdRequiredController {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		if (!SecurityHelpers.isAdmin(request)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		Integer materialeId = getRequestId(request);
		if (materialeId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			
			if (this.dl == null) {
				throw new ServletException("DataLayer non inizializzato");
			}

			Materiale materiale = findMateriale((MaterialeDAO) this.dl.getDAO(Materiale.class), materialeId);
			if (materiale == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			renderMaterialDetails(request, response, this.dl, materiale);
		} catch (DataException | IOException ex) {
			handleError(ex, request, response);
		}
	}

	private Materiale findMateriale(MaterialeDAO materialeDAO, int materialeId) throws DataException {
		for (Materiale materiale : materialeDAO.getMaterialiConStato()) {
			if (materiale.getKey() != null && materiale.getKey() == materialeId) {
				return materiale;
			}
		}
		return null;
	}

	private void renderMaterialDetails(HttpServletRequest request, HttpServletResponse response,
			DataLayer dl, Materiale materiale) throws IOException, DataException, ServletException {
		Map<String, Object> dettaglio = new HashMap<>();
		dettaglio.put("id", materiale.getKey());
		dettaglio.put("nome", materiale.getNome());
		dettaglio.put("descrizione", materiale.getDesc());
		dettaglio.put("quantita", 0);
		dettaglio.put("stato", materiale.isAssegnato() ? "Occupato" : "Disponibile");
		if (materiale.isAssegnato()) {
			dettaglio.put("missione_corrente", materiale.getMissioneKey());
		}

		List<Map<String, Object>> storicoMissioni = new java.util.ArrayList<>();
		List<Missione> missioni = ((MissioneDAO) this.dl.getDAO(Missione.class))
				.getStoricoMissioniByMateriale(materiale);
		for (Missione missione : missioni) {
			Map<String, Object> storico = new HashMap<>();
			storico.put("missione_id", missione.getKey());
			storico.put("data", missione.getInizio() == null ? "" : missione.getInizio().toLocalDate());
			storico.put("descrizione", missione.getObiettivo());
			storicoMissioni.add(storico);
		}
		dettaglio.put("storico_missioni", storicoMissioni);

		Map<String, Object> model = new HashMap<>();
		model.put("ctx", request.getContextPath());
		model.put("dettaglio", dettaglio);

		response.setContentType("text/html;charset=UTF-8");
		try {
			Template template = cfg.getTemplate("details/materiale-detail.ftl");
			template.process(model, response.getWriter());
		} catch (TemplateException ex) {
			throw new ServletException("Errore durante il rendering del dettaglio materiale", ex);
		}
	}

}
