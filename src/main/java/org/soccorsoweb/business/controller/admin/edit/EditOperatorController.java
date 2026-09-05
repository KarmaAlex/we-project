package org.soccorsoweb.business.controller.admin.edit;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.soccorsoweb.business.controller.AbstractIdRequiredController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.AnagraficaDAO;
import org.soccorsoweb.data.dao.CredenzialiDAO;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Anagrafica;
import org.soccorsoweb.model.Credenziali;
import org.soccorsoweb.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class EditOperatorController extends AbstractIdRequiredController {

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		if (!SecurityHelpers.isAdmin(request)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		Integer operatorId = getRequestId(request);
		if (operatorId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			
			if (this.dl == null) {
				throw new ServletException("DataLayer non inizializzato");
			}

			if ("GET".equalsIgnoreCase(request.getMethod())) {
				renderEditOperator(request, response, this.dl, operatorId);
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

			editOperator(request, response, this.dl, operatorId);
		} catch (IOException | DataException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderEditOperator(HttpServletRequest request, HttpServletResponse response,
			DataLayer dl, int operatorId) throws IOException, DataException, ServletException {
		Utente utente = ((UtenteDAO) this.dl.getDAO(Utente.class)).getUtente(operatorId);
		if (utente == null || utente.isAdmin()) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Anagrafica anagrafica = ((AnagraficaDAO) this.dl.getDAO(Anagrafica.class)).getAnagraficaByUtente(utente);
		Credenziali credenziali = ((CredenzialiDAO) this.dl.getDAO(Credenziali.class)).getCredenzialiByUtente(utente);
		Map<String, Object> dettaglio = new HashMap<>();
		dettaglio.put("id", utente.getKey());
		dettaglio.put("nome", anagrafica == null ? "" : anagrafica.getNome());
		dettaglio.put("cognome", anagrafica == null ? "" : anagrafica.getCognome());
		dettaglio.put("email", credenziali == null ? "" : credenziali.getEmail());
		dettaglio.put("telefono", "");
		dettaglio.put("patenti", java.util.List.of());
		dettaglio.put("abilita", java.util.List.of());

		Map<String, Object> model = new HashMap<>();
		model.put("ctx", request.getContextPath());
		model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
		model.put("dettaglio", dettaglio);
		model.put("patentiDisponibili", java.util.List.of());
		model.put("abilitaDisponibili", java.util.List.of());

		response.setContentType("text/html;charset=UTF-8");
		try {
			Template template = cfg.getTemplate("edit/operatore-edit.ftl");
			template.process(model, response.getWriter());
		} catch (TemplateException ex) {
			throw new ServletException("Errore durante il rendering del form operatore", ex);
		}
	}

	private void editOperator(HttpServletRequest request, HttpServletResponse response,
			DataLayer dl, int operatorId) throws IOException, DataException {
		String nome = SecurityHelpers.sanitizeTextInput(request.getParameter("nome"));
		String cognome = SecurityHelpers.sanitizeTextInput(request.getParameter("cognome"));
		String email = SecurityHelpers.sanitizeTextInput(request.getParameter("email"));
		if (nome.isBlank() || cognome.isBlank() || email.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("{\"error\":\"Nome, cognome e email sono obbligatori\"}");
			return;
		}

		Utente utente = ((UtenteDAO) this.dl.getDAO(Utente.class)).getUtente(operatorId);
		if (utente == null || utente.isAdmin()) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write("{\"error\":\"Operatore non trovato\"}");
			return;
		}

		AnagraficaDAO anagraficaDAO = (AnagraficaDAO) this.dl.getDAO(Anagrafica.class);
		Anagrafica anagrafica = anagraficaDAO.getAnagraficaByUtente(utente);
		if (anagrafica == null) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			response.getWriter().write("{\"error\":\"Anagrafica operatore non presente\"}");
			return;
		}
		anagrafica.setNome(nome);
		anagrafica.setCognome(cognome);
		anagraficaDAO.storeAnagrafica(anagrafica, utente);

		CredenzialiDAO credenzialiDAO = (CredenzialiDAO) this.dl.getDAO(Credenziali.class);
		Credenziali credenziali = credenzialiDAO.getCredenzialiByUtente(utente);
		if (credenziali == null) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			response.getWriter().write("{\"error\":\"Credenziali operatore non presenti\"}");
			return;
		}
		credenziali.setEmail(email);
		credenzialiDAO.storeCredenziali(credenziali);

		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write("{\"success\":true,\"message\":\"Operatore modificato con successo\"}");
	}

}
