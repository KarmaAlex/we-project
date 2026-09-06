package org.soccorsoweb.business.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.dao.AnagraficaDAO;
import org.soccorsoweb.data.dao.CredenzialiDAO;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Anagrafica;
import org.soccorsoweb.model.Credenziali;
import org.soccorsoweb.model.Utente;

public class SignupController extends SoccorsoBaseController {

    private Configuration cfg;

    @Override
    public void init() throws ServletException {
        super.init();
        cfg = new Configuration(Configuration.VERSION_2_3_34);
        cfg.setServletContextForTemplateLoading(getServletContext(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        renderSignupPage(req, resp, null, resolvePendingUsername(req));
    }

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = resolvePendingUsername(req);
        if (username == null || username.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            
            if (this.dl == null) {
                throw new ServletException("DataLayer non inizializzato");
            }

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("nome", SecurityHelpers.sanitizeTextInput(req.getParameter("nome")));
            payload.put("cognome", SecurityHelpers.sanitizeTextInput(req.getParameter("cognome")));
            payload.put("cf", SecurityHelpers.sanitizeTextInput(req.getParameter("cf")));
            payload.put("luogo_nasc", SecurityHelpers.sanitizeTextInput(req.getParameter("luogo_nasc")));
            payload.put("data_nasc", SecurityHelpers.sanitizeTextInput(req.getParameter("data_nasc")));
            payload.put("new_password", SecurityHelpers.sanitizeTextInput(req.getParameter("new_password")));
            payload.put("confirm_password", SecurityHelpers.sanitizeTextInput(req.getParameter("confirm_password")));

            String nome = asNonEmptyString(payload.get("nome"));
            String cognome = asNonEmptyString(payload.get("cognome"));
            String cf = asNonEmptyString(payload.get("cf"));
            String luogoNasc = asNonEmptyString(payload.get("luogo_nasc"));
            String dataNascParam = asNonEmptyString(payload.get("data_nasc"));
            String newPassword = asNonEmptyString(payload.get("new_password"));
            String confirmPassword = asNonEmptyString(payload.get("confirm_password"));

            if (nome == null || cognome == null || cf == null || luogoNasc == null || dataNascParam == null
                    || newPassword == null || confirmPassword == null) {
                renderSignupPage(req, resp, "Compila tutti i campi obbligatori.", username);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                renderSignupPage(req, resp, "Le password non coincidono.", username);
                return;
            }

            if (newPassword.length() < 8) {
                renderSignupPage(req, resp, "La password deve contenere almeno 8 caratteri.", username);
                return;
            }

            LocalDate birthDate;
            try {
                birthDate = LocalDate.parse(dataNascParam);
            } catch (RuntimeException ex) {
                renderSignupPage(req, resp, "La data di nascita non è valida.", username);
                return;
            }
            if (!SecurityHelpers.isValidBirthDate(birthDate)) {
                renderSignupPage(req, resp,
                        "La data di nascita deve indicare un'età compresa tra 4 e 105 anni.", username);
                return;
            }

            String signature = SecurityHelpers.buildRequestSignature(
                    nome,
                    cognome,
                    cf,
                    luogoNasc,
                    dataNascParam,
                    newPassword
            );
            req.getSession(true).setAttribute("signup.signature", signature);

            UtenteDAO utenteDAO = (UtenteDAO) this.dl.getDAO(Utente.class);
            Utente utente = utenteDAO.getUtenteByUsername(username);
            if (utente == null) {
                throw new ServletException("Utente non trovato per il completamento del profilo.");
            }

            AnagraficaDAO anagraficaDAO = (AnagraficaDAO) this.dl.getDAO(Anagrafica.class);
            Anagrafica anagrafica = anagraficaDAO.getAnagraficaByUtente(utente);
            if (anagrafica == null) {
                anagrafica = anagraficaDAO.createAnagrafica();
            }

            anagrafica.setNome(nome);
            anagrafica.setCognome(cognome);
            anagrafica.setCf(cf);
            anagrafica.setLuogoNasc(luogoNasc);
            anagrafica.setDataNasc(birthDate);
            anagraficaDAO.storeAnagrafica(anagrafica, utente);

            CredenzialiDAO credenzialiDAO = (CredenzialiDAO) this.dl.getDAO(Credenziali.class);
            Credenziali credenziali = credenzialiDAO.getCredenzialiByUtente(utente);
            if (credenziali == null) {
                credenziali = credenzialiDAO.createCredenziali();
            }
            if (credenziali.getEmail() == null || credenziali.getEmail().isBlank()) {
                credenziali.setEmail(utente.getNomeUtente() + "@soccorsoweb.local");
            }

            String passwordHash = SecurityHelpers.getPasswordHashPBKDF2(newPassword);
            credenziali.setPasswordHash(passwordHash.getBytes());
            credenzialiDAO.storeCredenziali(credenziali);
            if (credenziali.getKey() != null && credenziali.getKey() > 0) {
                credenzialiDAO.legaCredenzialiAUtente(credenziali, utente);
            }

            HttpSession session = req.getSession(false);
            if (session != null) {
                session.removeAttribute("pendingUsername");
                session.removeAttribute("needsCompleteProfile");
                session.removeAttribute("signup.signature");
            }

            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (DataException ex) {
            throw new ServletException("Errore durante il salvataggio del profilo", ex);
        } catch (Exception ex) {
            renderSignupPage(req, resp, "Dati non validi. Controlla il formato della data, del codice fiscale o della password.", username);
        }
    }

    private void renderSignupPage(HttpServletRequest req, HttpServletResponse resp, String errorMessage, String username)
            throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        try {
            if (username != null) {
                req.setAttribute("username", username);
            }

            Template tpl = cfg.getTemplate("signup.ftl");
            Map<String, Object> model = new HashMap<>();
            model.put("ctx", req.getContextPath());
            model.put("username", username);
            model.put("errorMessage", errorMessage);
            tpl.process(model, resp.getWriter());
        } catch (TemplateException ex) {
            throw new ServletException("Error while processing Freemarker template", ex);
        }
    }

    private String resolvePendingUsername(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }

        Object pendingUsername = session.getAttribute("pendingUsername");
        return pendingUsername == null ? null : String.valueOf(pendingUsername);
    }

    private String asNonEmptyString(Object value) {
        if (value == null) {
            return null;
        }

        String text = value.toString().trim();
        return text.isEmpty() ? null : text;
    }


}
