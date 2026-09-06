package org.soccorsoweb.business.controller.admin.edit;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.soccorsoweb.business.controller.AbstractIdRequiredController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.RichiestaDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Richiesta;
import org.soccorsoweb.model.enums.StatoRichiesta;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class EditRequestController extends AbstractIdRequiredController {
	
	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		if (!SecurityHelpers.isAdmin(request)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		Integer richiestaId = getRequestId(request);
		if (richiestaId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			
			if (this.dl == null) {
				throw new ServletException("DataLayer non inizializzato");
			}
			if ("GET".equalsIgnoreCase(request.getMethod())) {
				renderEditRequest(request, response, this.dl, richiestaId);
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
			editRequest(request, response, this.dl, richiestaId);
		} catch (IOException | DataException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderEditRequest(HttpServletRequest request, HttpServletResponse response,
			DataLayer dl, int richiestaId) throws IOException, DataException, ServletException {
		Richiesta richiesta = ((RichiestaDAO) this.dl.getDAO(Richiesta.class)).getRichiesta(richiestaId);
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
			DataLayer dl, int richiestaId) throws IOException, DataException {
		Richiesta richiesta = ((RichiestaDAO) this.dl.getDAO(Richiesta.class)).getRichiesta(richiestaId);
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
		((RichiestaDAO) this.dl.getDAO(Richiesta.class)).storeRichiesta(richiesta);

		response.setContentType("application/json;charset=UTF-8");
		response.sendRedirect(request.getContextPath() + "/admin-dashboard?section=requests");
	}


	private StatoRichiesta parseStato(String value) {
		try {
			return value == null ? null : StatoRichiesta.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

}
