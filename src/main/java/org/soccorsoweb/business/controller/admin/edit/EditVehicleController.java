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
import org.soccorsoweb.data.dao.MezzoDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Mezzo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class EditVehicleController extends SoccorsoBaseController {

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

		Integer mezzoId = parseMezzoId(request);
		if (mezzoId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			DataLayer dataLayer = (DataLayer) request.getAttribute("datalayer");
			if (dataLayer == null) {
				throw new ServletException("DataLayer non inizializzato");
			}

			if ("GET".equalsIgnoreCase(request.getMethod())) {
				renderEditVehiclePage(request, response, dataLayer, mezzoId);
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

			editMezzo(request, response, dataLayer, mezzoId);
		} catch (IOException | DataException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderEditVehiclePage(HttpServletRequest request, HttpServletResponse response,
			DataLayer dataLayer, int mezzoId) throws IOException, DataException, ServletException {
		Mezzo mezzo = getMezzo(dataLayer, mezzoId);
		if (mezzo == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Map<String, Object> dettaglio = new HashMap<>();
		dettaglio.put("id", mezzo.getKey());
		dettaglio.put("nome", mezzo.getNome());
		dettaglio.put("descrizione", mezzo.getDesc());
		dettaglio.put("targa", mezzo.getTarga());
		dettaglio.put("missione_corrente", mezzo.isAssegnato() ? mezzo.getMissioneKey() : "");

		Map<String, Object> model = new HashMap<>();
		model.put("ctx", request.getContextPath());
		model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
		model.put("dettaglio", dettaglio);
		model.put("missioniAperte", java.util.List.of());

		response.setContentType("text/html;charset=UTF-8");
		try {
			Template template = cfg.getTemplate("edit/mezzo-edit.ftl");
			template.process(model, response.getWriter());
		} catch (TemplateException ex) {
			throw new ServletException("Errore durante il rendering del form mezzo", ex);
		}
	}

	private void editMezzo(HttpServletRequest request, HttpServletResponse response,
			DataLayer dataLayer, int mezzoId) throws IOException, DataException {
		String nome = SecurityHelpers.sanitizeTextInput(request.getParameter("nome"));
		String descrizione = SecurityHelpers.sanitizeTextInput(request.getParameter("descrizione"));
		String targa = SecurityHelpers.sanitizeTextInput(request.getParameter("targa"));
		if (nome.isBlank() || descrizione.isBlank() || targa.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("{\"error\":\"Nome, descrizione e targa sono obbligatori\"}");
			return;
		}

		Mezzo mezzo = getMezzo(dataLayer, mezzoId);
		if (mezzo == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write("{\"error\":\"Mezzo non trovato\"}");
			return;
		}

		mezzo.setNome(nome);
		mezzo.setDesc(descrizione);
		mezzo.setTarga(targa);
		((MezzoDAO) dataLayer.getDAO(Mezzo.class)).storeMezzo(mezzo);

		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write("{\"success\":true,\"message\":\"Mezzo modificato con successo\"}");
	}

	private Mezzo getMezzo(DataLayer dataLayer, int mezzoId) throws DataException {
		return ((MezzoDAO) dataLayer.getDAO(Mezzo.class)).getMezzo(mezzoId);
	}

	private Integer parseMezzoId(HttpServletRequest request) {
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
