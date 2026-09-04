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
import org.soccorsoweb.data.dao.MaterialeDAO;
import org.soccorsoweb.data.dao.MissioneDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Materiale;
import org.soccorsoweb.model.Missione;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MaterialDetailsController extends SoccorsoBaseController {

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

		Integer materialeId = parseMaterialeId(request);
		if (materialeId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			DataLayer dataLayer = (DataLayer) request.getAttribute("datalayer");
			if (dataLayer == null) {
				throw new ServletException("DataLayer non inizializzato");
			}

			Materiale materiale = ((MaterialeDAO) dataLayer.getDAO(Materiale.class)).getMateriale(materialeId);
			if (materiale == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			renderMaterialDetails(request, response, dataLayer, materiale);
		} catch (DataException | IOException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderMaterialDetails(HttpServletRequest request, HttpServletResponse response,
			DataLayer dataLayer, Materiale materiale) throws IOException, DataException, ServletException {
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
		List<Missione> missioni = ((MissioneDAO) dataLayer.getDAO(Missione.class))
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

	private Integer parseMaterialeId(HttpServletRequest request) {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		String[] segments = path.split("/");
		if (segments.length == 0) {
			return null;
		}
		try {
			return Integer.valueOf(segments[segments.length - 1]);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

}
