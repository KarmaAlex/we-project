package org.soccorsoweb.business.controller.admin.edit;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.soccorsoweb.business.controller.AbstractIdRequiredController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.MezzoDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Mezzo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class EditVehicleController extends AbstractIdRequiredController {

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		if (!SecurityHelpers.isAdmin(request)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
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

			if ("GET".equalsIgnoreCase(request.getMethod())) {
				renderEditVehiclePage(request, response, this.dl, mezzoId);
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

			editMezzo(request, response, this.dl, mezzoId);
		} catch (IOException | DataException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderEditVehiclePage(HttpServletRequest request, HttpServletResponse response,
			DataLayer dl, int mezzoId) throws IOException, DataException, ServletException {
		Mezzo mezzo = getMezzo(this.dl, mezzoId);
		if (mezzo == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Map<String, Object> dettaglio = new HashMap<>();
		dettaglio.put("id", mezzo.getKey());
		dettaglio.put("nome", mezzo.getNome());
		dettaglio.put("descrizione", mezzo.getDesc());
		dettaglio.put("targa", mezzo.getTarga());

		Map<String, Object> model = new HashMap<>();
		model.put("ctx", request.getContextPath());
		model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
		model.put("dettaglio", dettaglio);

		response.setContentType("text/html;charset=UTF-8");
		try {
			Template template = cfg.getTemplate("edit/mezzo-edit.ftl");
			template.process(model, response.getWriter());
		} catch (TemplateException ex) {
			throw new ServletException("Errore durante il rendering del form mezzo", ex);
		}
	}

	private void editMezzo(HttpServletRequest request, HttpServletResponse response,
			DataLayer dl, int mezzoId) throws IOException, DataException {
		String nome = SecurityHelpers.sanitizeTextInput(request.getParameter("nome"));
		String descrizione = SecurityHelpers.sanitizeTextInput(request.getParameter("descrizione"));
		String targa = SecurityHelpers.sanitizeTextInput(request.getParameter("targa"));
		if (nome.isBlank() || descrizione.isBlank() || targa.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("{\"error\":\"Nome, descrizione e targa sono obbligatori\"}");
			return;
		}

		Mezzo mezzo = getMezzo(this.dl, mezzoId);
		if (mezzo == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write("{\"error\":\"Mezzo non trovato\"}");
			return;
		}

		mezzo.setNome(nome);
		mezzo.setDesc(descrizione);
		mezzo.setTarga(targa);
		((MezzoDAO) this.dl.getDAO(Mezzo.class)).storeMezzo(mezzo);

		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write("{\"success\":true,\"message\":\"Mezzo modificato con successo\"}");
	}

	private Mezzo getMezzo(DataLayer dl, int mezzoId) throws DataException {
		return ((MezzoDAO) this.dl.getDAO(Mezzo.class)).getMezzo(mezzoId);
	}
}
