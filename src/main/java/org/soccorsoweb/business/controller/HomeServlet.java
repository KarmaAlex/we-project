package org.soccorsoweb.business.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.soccorsoweb.framework.security.SecurityHelpers;

public class HomeServlet extends SoccorsoBaseController {

    private Configuration cfg;

    @Override
    public void init() throws ServletException {
        super.init();
        cfg = new Configuration(Configuration.VERSION_2_3_34);
        cfg.setServletContextForTemplateLoading(getServletContext(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (hasFormSubmission(request)) {
            CreaRichiestaServlet creaRichiestaServlet = new CreaRichiestaServlet();
            creaRichiestaServlet.init(getServletConfig());
            creaRichiestaServlet.processRequest(request, response);
            return;
        }

        processRequest(request, response);
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String ctx = request.getContextPath();
        Map<String, Object> model = new HashMap<>();
        model.put("ctx", ctx);
        model.put("currentUser", buildCurrentUser(request));
        model.put("isLoggedIn", isUserLoggedIn(request));
        model.put("successMessage", request.getParameter("success"));

        response.setContentType("text/html;charset=UTF-8");

        try {
            Template tpl = cfg.getTemplate("home.ftl");
            tpl.process(model, response.getWriter());
        } catch (TemplateException ex) {
            throw new ServletException("Error while processing Freemarker template", ex);
        } catch (IOException ex) {
            throw new ServletException("Error while rendering home page", ex);
        }
    }

    private boolean hasFormSubmission(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase(Locale.ROOT).contains("multipart/form-data")) {
            return true;
        }

        return request.getParameter("nome") != null
                || request.getParameter("email") != null
                || request.getParameter("posizione") != null
                || request.getParameter("descrizione") != null;
    }

    private boolean isUserLoggedIn(HttpServletRequest request) {
        return SecurityHelpers.checkSession(request) != null;
    }

    private Map<String, Object> buildCurrentUser(HttpServletRequest request) {
        Map<String, Object> currentUser = new HashMap<>();
        HttpSession session = request.getSession(false);

        if (session == null) {
            currentUser.put("authenticated", false);
            return currentUser;
        }

        currentUser.put("authenticated", session.getAttribute("userid") != null);
        currentUser.put("username", session.getAttribute("username"));
        currentUser.put("nome", session.getAttribute("username"));
        currentUser.put("userid", session.getAttribute("userid"));
        currentUser.put("ruolo", session.getAttribute("ruolo"));

        return currentUser;
    }
}
