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
import org.soccorsoweb.data.dao.MezzoDAO;
import org.soccorsoweb.data.dao.MissioneDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Mezzo;
import org.soccorsoweb.model.Missione;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class VehicleDetailsController extends AbstractIdRequiredController {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!SecurityHelpers.isAdmin(request)) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		Integer mezzoId = getRequestId(request);
		if (mezzoId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			
			if (this.dl == null) {
				throw new ServletException("DataLayer non inizializzato");
			}

			Mezzo mezzo = findMezzo((MezzoDAO) this.dl.getDAO(Mezzo.class), mezzoId);
			if (mezzo == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			renderVehicleDetails(request, response, this.dl, mezzo);
		} catch (DataException | IOException ex) {
			handleError(ex, request, response);
		}
	}

	private Mezzo findMezzo(MezzoDAO mezzoDAO, int mezzoId) throws DataException {
		for (Mezzo mezzo : mezzoDAO.getMezziConStato()) {
			if (mezzo.getKey() != null && mezzo.getKey() == mezzoId) {
				return mezzo;
			}
		}
		return null;
	}

	private void renderVehicleDetails(HttpServletRequest request, HttpServletResponse response, DataLayer dl, Mezzo mezzo)
			throws ServletException, IOException, DataException {
		Map<String, Object> dettaglio = new HashMap<>();
		dettaglio.put("id", mezzo.getKey());
		dettaglio.put("nome", mezzo.getNome());
		dettaglio.put("descrizione", mezzo.getDesc());
		dettaglio.put("targa", mezzo.getTarga());
		dettaglio.put("stato", mezzo.isAssegnato() ? "Occupato" : "Disponibile");
		if (mezzo.isAssegnato()) {
			dettaglio.put("missione_corrente", mezzo.getMissioneKey());
		}
		List<Map<String, Object>> storicoMissioni = new java.util.ArrayList<>();
		List<Missione> missioni = ((MissioneDAO) this.dl.getDAO(Missione.class)).getStoricoMissioniByMezzo(mezzo);
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
			Template template = cfg.getTemplate("details/mezzo-detail.ftl");
			template.process(model, response.getWriter());
		} catch (TemplateException ex) {
			throw new ServletException("Errore durante il rendering del dettaglio mezzo", ex);
		}
	}

}
