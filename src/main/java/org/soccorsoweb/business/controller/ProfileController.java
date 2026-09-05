package org.soccorsoweb.business.controller;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.model.Utente;

public class ProfileController extends SoccorsoBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userid") == null) {
            try {
                response.sendRedirect(request.getContextPath() + "/login");
            } catch (IOException ex) {
                throw new ServletException("Errore nel redirect al login", ex);
            }
            return;
        }

        Integer userId = parseUserId(session.getAttribute("userid"));
        if (userId == null) {
            try {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Sessione utente non valida");
            } catch (IOException ex) {
                throw new ServletException("Errore nella risposta 403", ex);
            }
            return;
        }

        
        if (this.dl == null) {
            throw new ServletException("DataLayer non inizializzato");
        }

        try {
            UtenteDAO utenteDAO = (UtenteDAO) this.dl.getDAO(Utente.class);
            Utente utente = utenteDAO.getUtente(userId);

            if (utente == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Utente non trovato nel database");
                return;
            }

            Map<String, Object> model = new HashMap<>();
            model.put("ctx", request.getContextPath());
            model.put("user", utente);
            model.put("currentUser", buildCurrentUser(session));
            model.put("successMessage", request.getParameter("success"));
            model.put("errorMessage", request.getParameter("error"));

            response.setContentType("text/html;charset=UTF-8");
            Template tpl = cfg.getTemplate("operator/profile.ftl");
            tpl.process(model, response.getWriter());
        } catch (DataException ex) {
            throw new ServletException("Errore nel caricamento dell'utente dal database", ex);
        } catch (TemplateException ex) {
            throw new ServletException("Errore nel rendering del template profile", ex);
        } catch (IOException ex) {
            throw new ServletException("Errore di I/O nel rendering del profilo", ex);
        }
    }


    private Integer parseUserId(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Map<String, Object> buildCurrentUser(HttpSession session) {
        Map<String, Object> currentUser = new HashMap<>();
        currentUser.put("authenticated", true);
        currentUser.put("userid", session.getAttribute("userid"));
        currentUser.put("username", session.getAttribute("username"));
        currentUser.put("ruolo", session.getAttribute("ruolo"));
        return currentUser;
    }
}
