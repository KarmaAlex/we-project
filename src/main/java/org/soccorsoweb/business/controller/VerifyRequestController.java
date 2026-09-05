package org.soccorsoweb.business.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.RichiestaDAO;
import org.soccorsoweb.model.Richiesta;
import org.soccorsoweb.model.enums.StatoRichiesta;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class VerifyRequestController extends SoccorsoBaseController {

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		if (this.dl == null) {
			throw new ServletException("DataLayer non inizializzato");
		}

		String token = request.getParameter("token");
		if (token == null || token.isBlank()) {
			renderEmailPreview(request, response);
			return;
		}

		try {
			RichiestaDAO richiestaDAO = (RichiestaDAO) this.dl.getDAO(Richiesta.class);
			Richiesta richiesta = findByToken(richiestaDAO, token);
			if (richiesta == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Richiesta non trovata");
				return;
			}

			richiesta.setVerificato(true);
			richiesta.setStato(StatoRichiesta.IN_ATTESA);
			richiestaDAO.storeRichiesta(richiesta);
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write("<h2>Richiesta attivata</h2>"
					+ "<p>La richiesta è stata verificata e resa attiva.</p>");
		} catch (DataException ex) {
			throw new ServletException("Errore durante la verifica della richiesta", ex);
		}
	}

	private void renderEmailPreview(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		var session = request.getSession(false);
		if (session == null || session.getAttribute("verification.link") == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email di verifica non disponibile");
			return;
		}

		String email = String.valueOf(session.getAttribute("verification.email"));
		String link = String.valueOf(session.getAttribute("verification.link"));
		session.removeAttribute("verification.email");
		session.removeAttribute("verification.link");

		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write("<h2>Email di verifica</h2>"
				+ "<p>Email simulata inviata a: <strong>" + escapeHtml(email) + "</strong></p>"
				+ "<p><a href=\"" + escapeHtml(link) + "\" target=\"_blank\" rel=\"noopener noreferrer\">"
				+ "Clicca qui per rendere attiva la richiesta</a></p>");
	}

	private String escapeHtml(String value) {
		return value.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}

	private Richiesta findByToken(RichiestaDAO richiestaDAO, String token) throws DataException {
		for (Richiesta richiesta : richiestaDAO.getRichieste()) {
			if (token.equals(richiesta.getString())) {
				return richiesta;
			}
		}
		return null;
	}

	public static String buildVerificationLink(HttpServletRequest request, String token) {
		return request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort() + request.getContextPath()
				+ "/verify-request?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
	}

}
