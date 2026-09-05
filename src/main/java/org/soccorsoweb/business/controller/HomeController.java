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
import org.soccorsoweb.framework.security.SecurityHelpers;

public class HomeController extends SoccorsoBaseController {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String ctx = request.getContextPath();
        String path = request.getRequestURI().substring(ctx.length());
        if((!"/".equals(path)) && (!"/home".equals(path))){
            try {
                getServletContext().getNamedDispatcher("default").forward(request, response);
                return;
            } catch (IOException e) {
                throw new ServletException(e);
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put("ctx", ctx);
        model.put("currentUser", buildCurrentUser(request));
        model.put("isLoggedIn", isUserLoggedIn(request));
        model.put("isAdmin", SecurityHelpers.isAdmin(request));
        model.put("isOperator", SecurityHelpers.isOperator(request));
        model.put("successMessage", request.getParameter("success"));
        model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
        model.put("success", request.getParameter("success") != null ? true : false);
        model.put("error", request.getParameter("error") != null ? true : false);

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


    private boolean isUserLoggedIn(HttpServletRequest request) {
        return SecurityHelpers.checkSession(request) != null;
    }

    private Map<String, Object> buildCurrentUser(HttpServletRequest request) {
        Map<String, Object> currentUser = new HashMap<>();
        HttpSession session = request.getSession(false);

        if (session == null) {
            currentUser.put("authenticated", false);
            SecurityHelpers.createAnonymousSession(request);
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
