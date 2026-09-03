package org.soccorsoweb.business.controller.admin.edit;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.soccorsoweb.business.controller.SoccorsoBaseController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.MaterialeDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Materiale;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class EditMaterialController extends SoccorsoBaseController {

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

			if ("GET".equalsIgnoreCase(request.getMethod())) {
				renderEditMaterialPage(request, response, dataLayer, materialeId);
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

			editMateriale(request, response, dataLayer, materialeId);
		} catch (IOException | DataException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderEditMaterialPage(HttpServletRequest request, HttpServletResponse response,
			DataLayer dataLayer, int materialeId) throws IOException, DataException, ServletException {
		Materiale materiale = getMateriale(dataLayer, materialeId);
		if (materiale == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Map<String, Object> dettaglio = new HashMap<>();
		dettaglio.put("id", materiale.getKey());
		dettaglio.put("nome", materiale.getNome());
		dettaglio.put("descrizione", materiale.getDesc());
		dettaglio.put("quantita", 0);
		dettaglio.put("missioneId", "");

		Map<String, Object> model = new HashMap<>();
		model.put("ctx", request.getContextPath());
		model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
		model.put("dettaglio", dettaglio);
		model.put("missioniAperte", java.util.List.of());

		response.setContentType("text/html;charset=UTF-8");
		try {
			Template template = cfg.getTemplate("edit/materiale-edit.ftl");
			template.process(model, response.getWriter());
		} catch (TemplateException ex) {
			throw new ServletException("Errore durante il rendering del form materiale", ex);
		}
	}

	private void editMateriale(HttpServletRequest request, HttpServletResponse response,
			DataLayer dataLayer, int materialeId) throws IOException, DataException {
		String nome = SecurityHelpers.sanitizeTextInput(request.getParameter("nome"));
		String descrizione = SecurityHelpers.sanitizeTextInput(request.getParameter("descrizione"));
		if (nome == null || nome.isBlank() || descrizione == null || descrizione.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("{\"error\":\"Nome e descrizione sono obbligatori\"}");
			return;
		}

		Materiale materiale = getMateriale(dataLayer, materialeId);
		if (materiale == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write("{\"error\":\"Materiale non trovato\"}");
			return;
		}

		materiale.setNome(nome);
		materiale.setDesc(descrizione);
		((MaterialeDAO) dataLayer.getDAO(Materiale.class)).storeMateriale(materiale);

		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write("{\"success\":true,\"message\":\"Materiale modificato con successo\"}");
	}

	private Materiale getMateriale(DataLayer dataLayer, int materialeId) throws DataException {
		return ((MaterialeDAO) dataLayer.getDAO(Materiale.class)).getMateriale(materialeId);
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
