package org.soccorsoweb.business.controller.admin.delete;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.soccorsoweb.business.controller.AbstractIdRequiredController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.dao.MezzoDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Mezzo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DeleteVehicleController extends AbstractIdRequiredController {

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
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
			MezzoDAO mezzoDAO = (MezzoDAO) this.dl.getDAO(Mezzo.class);
			Mezzo mezzo = mezzoDAO.getMezzo(mezzoId);
			if (mezzo == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			if ("GET".equalsIgnoreCase(request.getMethod())) {
				renderDeleteMezzo(request, response, mezzo);
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

			mezzoDAO.deleteMezzo(mezzoId);
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write("{\"success\":true,\"message\":\"Mezzo eliminato con successo\"}");
		} catch (DataException ex) {
			if ("POST".equalsIgnoreCase(request.getMethod())) {
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				response.setContentType("application/json;charset=UTF-8");
				response.getWriter().write("{\"error\":\"Mezzo non eliminabile perché associato a una missione\"}");
			} else {
				handleError(ex, request, response);
			}
		}
	}

	private void renderDeleteMezzo(HttpServletRequest request, HttpServletResponse response,
			Mezzo mezzo) throws IOException, ServletException {
		Map<String, Object> dettaglio = new HashMap<>();
		dettaglio.put("id", mezzo.getKey());
		dettaglio.put("nome", mezzo.getNome());
		dettaglio.put("descrizione", mezzo.getDesc());
		dettaglio.put("targa", mezzo.getTarga());
		dettaglio.put("stato", mezzo.isAssegnato() ? "Occupato" : "Disponibile");
		dettaglio.put("missione_corrente", mezzo.isAssegnato() ? mezzo.getMissioneKey() : "");

		Map<String, Object> model = new HashMap<>();
		model.put("ctx", request.getContextPath());
		model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
		model.put("dettaglio", dettaglio);

		response.setContentType("text/html;charset=UTF-8");
		try {
			Template template = cfg.getTemplate("delete/mezzo-delete.ftl");
			template.process(model, response.getWriter());
		} catch (TemplateException ex) {
			throw new ServletException("Errore durante il rendering del form di eliminazione mezzo", ex);
		}
	}

}