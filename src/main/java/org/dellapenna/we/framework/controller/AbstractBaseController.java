/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.dellapenna.we.framework.controller;

import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBaseController extends HttpServlet {

    private Configuration cfg;

    protected abstract void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception;

    @Override
    public void init() throws ServletException {
        super.init();

        cfg = new Configuration(Configuration.VERSION_2_3_34);
        cfg.setServletContextForTemplateLoading(getServletContext(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setOutputEncoding("UTF-8");
        cfg.setOutputFormat(HTMLOutputFormat.INSTANCE);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
    }

    protected void initRequest(HttpServletRequest request) {
        String url = request.getRequestURL()
                + (request.getQueryString() != null ? "?" + request.getQueryString() : "");

        request.setAttribute("thispageurl", url);
        request.setAttribute("ctx", request.getContextPath());
    }

    protected boolean hasLoggedAccess(HttpServletRequest request) {
        return false;
    }

    protected void accessCheckLoginFailed(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String url = request.getRequestURL()
                + (request.getQueryString() != null ? "?" + request.getQueryString() : "");

        response.sendRedirect(request.getContextPath()
                + "/login?referrer="
                + URLEncoder.encode(url, "UTF-8"));
    }

    protected void accessCheckSuccessful(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            Map<String, Object> loginInfo = new HashMap<>();
            loginInfo.put("username", session.getAttribute("username"));
            loginInfo.put("userid", session.getAttribute("userid"));
            loginInfo.put("role", session.getAttribute("role"));

            request.setAttribute("logininfo", loginInfo);
        }
    }

    private void processBaseRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            initRequest(request);

            if (hasLoggedAccess(request) && request.getSession(false) == null) {
                accessCheckLoginFailed(request, response);
                return;
            }

            accessCheckSuccessful(request);
            processRequest(request, response);

        } catch (Exception ex) {
            handleError(ex, request, response);
        }
    }

    protected Map<String, Object> createDataModel(HttpServletRequest request) {
        Map<String, Object> data = new HashMap<>();
        data.put("ctx", request.getContextPath());
        data.put("thispageurl", request.getAttribute("thispageurl"));
        data.put("logininfo", request.getAttribute("logininfo"));
        return data;
    }

    protected void render(String templateName, Map<String, Object> data, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try {
            Template template = cfg.getTemplate(templateName);
            template.process(data, response.getWriter());
        } catch (TemplateException ex) {
            throw new ServletException("Errore nel template " + templateName, ex);
        }
    }

    protected void handleError(Exception exception, HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            Map<String, Object> data = createDataModel(request);
            data.put("title", "Errore");
            data.put("message", exception.getMessage());

            render("error.html.ftl", data, response);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processBaseRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processBaseRequest(request, response);
    }
}
