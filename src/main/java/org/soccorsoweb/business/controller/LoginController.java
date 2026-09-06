package org.soccorsoweb.business.controller;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.dao.AnagraficaDAO;
import org.soccorsoweb.data.dao.CredenzialiDAO;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Anagrafica;
import org.soccorsoweb.model.Credenziali;
import org.soccorsoweb.model.Utente;


public class LoginController extends SoccorsoBaseController {

    @Override 
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException{
        SecurityHelpers.checkOrCreateSession(request);
        renderLoginPage(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException{
        SecurityHelpers.checkOrCreateSession(request);
        handleLogin(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String csrfToken = request.getParameter("csrf");
        if(csrfToken == null || csrfToken.isBlank() || !SecurityHelpers.isValidCsrfToken(request, csrfToken)) throw new ServletException("Invalid csrf token");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            renderLoginPage(request, response, "Inserisci username e password");
            return;
        }

        
        if (this.dl == null) {
            throw new ServletException("DataLayer non inizializzato");
        }

        try {
            UtenteDAO utenteDAO = (UtenteDAO) this.dl.getDAO(Utente.class);
            CredenzialiDAO credenzialiDAO = (CredenzialiDAO) this.dl.getDAO(Credenziali.class);

            Utente utente = utenteDAO.getUtenteByUsername(username);
            if (utente == null) {
                renderLoginPage(request, response, "Credenziali non valide");
                return;
            }

            Credenziali credenziali = credenzialiDAO.getCredenzialiByUtente(utente);
            if (credenziali == null || credenziali.getPasswordHash() == null) {
                renderLoginPage(request, response, "Credenziali non valide");
                return;
            }

            String storedHash = new String(credenziali.getPasswordHash(), StandardCharsets.UTF_8);
            boolean passwordOk = SecurityHelpers.checkPasswordHashPBKDF2(password, storedHash);
            if (!passwordOk) {
                renderLoginPage(request, response, "Password errata");
                return;
            }

            HttpSession session = SecurityHelpers.createSession(request, utente.getNomeUtente(), utente.getKey());
            session.setAttribute("ruolo", utente.isAdmin() ? "ADMIN" : "OPERATOR");
            session.setAttribute("admin", utente.isAdmin());

            AnagraficaDAO anagraficaDAO = (AnagraficaDAO) this.dl.getDAO(Anagrafica.class);
            Anagrafica anagrafica = anagraficaDAO.getAnagraficaByUtente(utente);
            boolean incompleteRegistration = SecurityHelpers.isIncompleteRegistration(anagrafica);
            if (incompleteRegistration) {
                session.setAttribute("needsCompleteProfile", true);
                session.setAttribute("pendingUsername", utente.getNomeUtente());
                response.sendRedirect(request.getContextPath() + "/signup");
                return;
            }

            String redirectTarget = utente.isAdmin() ? request.getContextPath() + "/admin-dashboard" : request.getContextPath() + "/operator-dashboard";
            response.sendRedirect(redirectTarget);
        } catch (DataException ex) {
            throw new ServletException("Errore durante il controllo delle credenziali", ex);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new ServletException("Errore nella verifica della password", ex);
        } catch (IOException ex) {
            throw new ServletException("Errore nel redirect dopo il login", ex);
        }
    }

    private void renderLoginPage(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException {
        try {
            response.setContentType("text/html;charset=UTF-8");
            Template tpl = cfg.getTemplate("login.ftl");
            Map<String, Object> model = new HashMap<>();
            model.put("ctx", request.getContextPath());
            model.put("errorMessage", errorMessage);
            model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
            tpl.process(model, response.getWriter());
        } catch (TemplateException ex) {
            throw new ServletException("Errore nel rendering del template login", ex);
        } catch (IOException ex) {
            throw new ServletException("Errore nel rendering del login", ex);
        }
    }

}
