package org.soccorsoweb.business.controller.admin.add;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.soccorsoweb.business.controller.SoccorsoBaseController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.CredenzialiDAO;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Credenziali;
import org.soccorsoweb.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AddOperatorController extends SoccorsoBaseController {

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		if (!SecurityHelpers.isAdmin(request)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		if (isMailConfirmationRequest(request)) {
			renderMailConfirmation(request, response);
			return;
		}

		if ("GET".equalsIgnoreCase(request.getMethod())) {
			renderAddOperatorPage(request, response);
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

		try {
			
			if (this.dl == null) {
				throw new ServletException("DataLayer non inizializzato");
			}
			addOperator(request, response, this.dl);
		} catch (DataException | IOException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderAddOperatorPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		Map<String, Object> model = new HashMap<>();
		model.put("ctx", request.getContextPath());
		model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
		renderTemplate(request, response, "templates/add/operatore-add.ftl", model, "Errore durante il rendering del form operatore");
	}

	private void addOperator(HttpServletRequest request, HttpServletResponse response, DataLayer dl)
		    throws IOException, DataException, ServletException, NoSuchAlgorithmException, InvalidKeySpecException {
		String nome = SecurityHelpers.sanitizeTextInput(request.getParameter("nome"));
		String cognome = SecurityHelpers.sanitizeTextInput(request.getParameter("cognome"));
		String username = SecurityHelpers.sanitizeTextInput(request.getParameter("nome_utente"));
		String email = SecurityHelpers.sanitizeTextInput(request.getParameter("email"));

		if (nome.isBlank() || cognome.isBlank() || username.isBlank() || email.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("{\"error\":\"Tutti i campi sono obbligatori\"}");
			return;
		}

		UtenteDAO utenteDAO = (UtenteDAO) this.dl.getDAO(Utente.class);
		if (utenteDAO.getUtenteByUsername(username) != null) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			response.getWriter().write("{\"error\":\"Nome utente già in uso\"}");
			return;
		}

		String temporaryPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
		Utente utente = utenteDAO.createUtente();
		utente.setNomeUtente(username);
		utente.setAdmin(false);
		utente.setMonteOre(0);
		utenteDAO.storeUtente(utente);

		CredenzialiDAO credenzialiDAO = (CredenzialiDAO) this.dl.getDAO(Credenziali.class);
		Credenziali credenziali = credenzialiDAO.createCredenziali();
		credenziali.setEmail(email);
		credenziali.setPasswordHash(SecurityHelpers.getPasswordHashPBKDF2(temporaryPassword).getBytes());
		credenzialiDAO.storeCredenziali(credenziali);
		credenzialiDAO.legaCredenzialiAUtente(credenziali, utente);

		request.getSession(true).setAttribute("operator.mail.username", username);
		request.getSession(true).setAttribute("operator.mail.email", email);
		request.getSession(true).setAttribute("operator.mail.password", temporaryPassword);
		response.sendRedirect(request.getContextPath() + "/api/add/operators/mail");
	}

	private void renderMailConfirmation(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		var session = request.getSession(false);
		if (session == null || session.getAttribute("operator.mail.email") == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Map<String, Object> model = new HashMap<>();
		model.put("username", session.getAttribute("operator.mail.username"));
		model.put("email", session.getAttribute("operator.mail.email"));
		model.put("password", session.getAttribute("operator.mail.password"));
		model.put("loginUrl", request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort() + request.getContextPath() + "/login");
		session.removeAttribute("operator.mail.username");
		session.removeAttribute("operator.mail.email");
		session.removeAttribute("operator.mail.password");
		renderTemplate(request, response, "email/temp_password.ftl", model, "Errore durante il rendering della email temporanea");
	}

	private void renderTemplate(HttpServletRequest request, HttpServletResponse response, String templateName,
			Map<String, Object> model, String errorMessage) throws ServletException {
		response.setContentType("text/html;charset=UTF-8");
		try {
			Template template = cfg.getTemplate(templateName);
			template.process(model, response.getWriter());
		} catch (IOException | TemplateException ex) {
			throw new ServletException(errorMessage, ex);
		}
	}

	private boolean isMailConfirmationRequest(HttpServletRequest request) {
		return request.getRequestURI().endsWith("/mail");
	}

}
