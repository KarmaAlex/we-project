package org.soccorsoweb.business.controller.admin.delete;

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

public class DeleteMaterialController extends SoccorsoBaseController {

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
		Integer materialeId = parseId(request);
		if (materialeId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			DataLayer dataLayer = (DataLayer) request.getAttribute("datalayer");
			if (dataLayer == null) {
				throw new ServletException("DataLayer non inizializzato");
			}
			MaterialeDAO materialeDAO = (MaterialeDAO) dataLayer.getDAO(Materiale.class);
			Materiale materiale = materialeDAO.getMateriale(materialeId);
			if (materiale == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			if ("GET".equalsIgnoreCase(request.getMethod())) {
				renderDeleteMaterial(request, response, materiale);
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

			materialeDAO.deleteMateriale(materialeId);
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write("{\"success\":true,\"message\":\"Materiale eliminato con successo\"}");
		} catch (DataException ex) {
			if ("POST".equalsIgnoreCase(request.getMethod())) {
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				response.setContentType("application/json;charset=UTF-8");
				response.getWriter().write("{\"error\":\"Materiale non eliminabile perché associato a una missione\"}");
			} else {
				handleError(ex, request, response);
			}
		}
	}

	private void renderDeleteMaterial(HttpServletRequest request, HttpServletResponse response,
			Materiale materiale) throws IOException, ServletException {
		Map<String, Object> dettaglio = new HashMap<>();
		dettaglio.put("id", materiale.getKey());
		dettaglio.put("nome", materiale.getNome());
		dettaglio.put("descrizione", materiale.getDesc());
		dettaglio.put("stato", materiale.isAssegnato() ? "Occupato" : "Disponibile");
		dettaglio.put("missione_corrente", materiale.isAssegnato() ? materiale.getMissioneKey() : "");

		Map<String, Object> model = new HashMap<>();
		model.put("ctx", request.getContextPath());
		model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
		model.put("dettaglio", dettaglio);

		response.setContentType("text/html;charset=UTF-8");
		try {
			Template template = cfg.getTemplate("delete/materiale-delete.ftl");
			template.process(model, response.getWriter());
		} catch (TemplateException ex) {
			throw new ServletException("Errore durante il rendering del form di eliminazione materiale", ex);
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
