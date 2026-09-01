package org.soccorsoweb.business.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.AnagraficaDAO;
import org.soccorsoweb.data.dao.CredenzialiDAO;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Anagrafica;
import org.soccorsoweb.model.Credenziali;
import org.soccorsoweb.model.Utente;


public class LoginServlet extends SoccorsoBaseController {

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
        String ctx = request.getContextPath();
        String path = request.getRequestURI().substring(ctx.length());

        if ("/login".equals(path) || "/login.html".equals(path)) {
            if ("POST".equalsIgnoreCase(request.getMethod())) {
                handleLogin(request, response);
            } else {
                renderLoginPage(request, response, null);
            }
            return;
        }

        jakarta.servlet.RequestDispatcher rd = getServletContext().getNamedDispatcher("default");
        if (rd != null) {
            try {
                rd.forward(request, response);
            } catch (IOException ex) {
                throw new ServletException("Errore nel forward delle risorse statiche", ex);
            }
            return;
        }

        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException ex) {
            throw new ServletException("Errore 404 dal login servlet", ex);
        }
    }

    @Override
    protected DataLayer createDataLayer(DataSource ds) throws ServletException {
        try {
            return new DataLayer(ds) {
                @Override
                public void init() throws DataException {
                    registerDAO(Utente.class, new org.soccorsoweb.data.dao.impl.UtenteDAO_MySQL(this));
                    registerDAO(Credenziali.class, new org.soccorsoweb.data.dao.impl.CredenzialiDAO_MySQL(this));
                    registerDAO(Anagrafica.class, new org.soccorsoweb.data.dao.impl.AnagraficaDAO_MySQL(this));
                }
            };
        } catch (Exception ex) {
            throw new ServletException("Impossibile inizializzare il DataLayer per il login", ex);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            renderLoginPage(request, response, "Inserisci username e password");
            return;
        }

        DataLayer dataLayer = (DataLayer) request.getAttribute("datalayer");
        if (dataLayer == null) {
            throw new ServletException("DataLayer non inizializzato");
        }

        try {
            UtenteDAO utenteDAO = (UtenteDAO) dataLayer.getDAO(Utente.class);
            CredenzialiDAO credenzialiDAO = (CredenzialiDAO) dataLayer.getDAO(Credenziali.class);

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

            String storedHash = SecurityHelpers.toHexString(credenziali.getPasswordHash());
            boolean passwordOk = SecurityHelpers.checkPasswordHashPBKDF2(password, storedHash);
            if (!passwordOk) {
                renderLoginPage(request, response, "Password errata");
                return;
            }

            HttpSession session = SecurityHelpers.createSession(request, utente.getNomeUtente(), utente.getKey());
            session.setAttribute("ruolo", utente.isAdmin() ? "ADMIN" : "OPERATOR");
            session.setAttribute("admin", utente.isAdmin());

            AnagraficaDAO anagraficaDAO = (AnagraficaDAO) dataLayer.getDAO(Anagrafica.class);
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
            tpl.process(model, response.getWriter());
        } catch (TemplateException ex) {
            throw new ServletException("Errore nel rendering del template login", ex);
        } catch (IOException ex) {
            throw new ServletException("Errore nel rendering del login", ex);
        }
    }

}
