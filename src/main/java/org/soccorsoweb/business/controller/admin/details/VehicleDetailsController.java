package org.soccorsoweb.business.controller.admin.details;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.soccorsoweb.business.controller.SoccorsoBaseController;
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

public class VehicleDetailsController extends SoccorsoBaseController {

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
			throws ServletException, IOException {
		if (!SecurityHelpers.isAdmin(request)) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		Integer mezzoId = parseId(request);
		if (mezzoId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			DataLayer dataLayer = (DataLayer) request.getAttribute("datalayer");
			if (dataLayer == null) {
				throw new ServletException("DataLayer non inizializzato");
			}

			Mezzo mezzo = findMezzo((MezzoDAO) dataLayer.getDAO(Mezzo.class), mezzoId);
			if (mezzo == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			renderVehicleDetails(request, response, dataLayer, mezzo);
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

	private void renderVehicleDetails(HttpServletRequest request, HttpServletResponse response, DataLayer dataLayer, Mezzo mezzo)
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
		List<Missione> missioni = ((MissioneDAO) dataLayer.getDAO(Missione.class)).getStoricoMissioniByMezzo(mezzo);
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

	private Integer parseId(HttpServletRequest request) {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		try {
			return Integer.valueOf(path.substring(path.lastIndexOf('/') + 1));
		} catch (NumberFormatException ex) {
			return null;
		}
	}
}
