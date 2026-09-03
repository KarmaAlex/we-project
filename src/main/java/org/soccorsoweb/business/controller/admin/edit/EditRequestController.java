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
import org.soccorsoweb.data.dao.RichiestaDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Richiesta;
import org.soccorsoweb.model.enums.StatoRichiesta;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class EditRequestController extends SoccorsoBaseController {

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

		Integer richiestaId = parseId(request);
		if (richiestaId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			DataLayer dataLayer = (DataLayer) request.getAttribute("datalayer");
			if (dataLayer == null) {
				throw new ServletException("DataLayer non inizializzato");
			}
			if ("GET".equalsIgnoreCase(request.getMethod())) {
				renderEditRequest(request, response, dataLayer, richiestaId);
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
			editRequest(request, response, dataLayer, richiestaId);
		} catch (IOException | DataException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderEditRequest(HttpServletRequest request, HttpServletResponse response,
			DataLayer dataLayer, int richiestaId) throws IOException, DataException, ServletException {
		Richiesta richiesta = ((RichiestaDAO) dataLayer.getDAO(Richiesta.class)).getRichiesta(richiestaId);
		if (richiesta == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Map<String, Object> dettaglio = new HashMap<>();
		dettaglio.put("id", richiesta.getKey());
		dettaglio.put("stato", richiesta.getStato() == null ? "IN_ATTESA" : richiesta.getStato().name());
		dettaglio.put("verificato", richiesta.isVerificato());

		Map<String, Object> model = new HashMap<>();
		model.put("ctx", request.getContextPath());
		model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
		model.put("dettaglio", dettaglio);

		response.setContentType("text/html;charset=UTF-8");
		try {
			Template template = cfg.getTemplate("edit/richiesta-edit.ftl");
			template.process(model, response.getWriter());
		} catch (TemplateException ex) {
			throw new ServletException("Errore durante il rendering del form richiesta", ex);
		}
	}

	private void editRequest(HttpServletRequest request, HttpServletResponse response,
			DataLayer dataLayer, int richiestaId) throws IOException, DataException {
		Richiesta richiesta = ((RichiestaDAO) dataLayer.getDAO(Richiesta.class)).getRichiesta(richiestaId);
		if (richiesta == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write("{\"error\":\"Richiesta non trovata\"}");
			return;
		}

		StatoRichiesta stato = parseStato(request.getParameter("stato"));
		if (stato == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("{\"error\":\"Stato richiesta non valido\"}");
			return;
		}

		richiesta.setStato(stato);
		richiesta.setVerificato(request.getParameter("verificato") != null);
		((RichiestaDAO) dataLayer.getDAO(Richiesta.class)).storeRichiesta(richiesta);

		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write("{\"success\":true,\"message\":\"Richiesta modificata con successo\"}");
	}


	private StatoRichiesta parseStato(String value) {
		try {
			return value == null ? null : StatoRichiesta.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException ex) {
			return null;
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
