package org.soccorsoweb.business.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginServlet extends HttpServlet {

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
        String ctx = req.getContextPath();
        String path = req.getRequestURI().substring(ctx.length());

        // serve the login template for /login or /login.html
        if ("/login".equals(path) || "/login.html".equals(path)) {
            resp.setContentType("text/html;charset=UTF-8");
            try {
                Template tpl = cfg.getTemplate("login.ftl");
                Map<String, Object> model = new HashMap<>();
                model.put("ctx", ctx);
                tpl.process(model, resp.getWriter());
            } catch (TemplateException ex) {
                throw new ServletException("Error while processing Freemarker template", ex);
            }
            return;
        }

        // forward other requests (static assets) to default servlet
        jakarta.servlet.RequestDispatcher rd = getServletContext().getNamedDispatcher("default");
        if (rd != null) {
            rd.forward(req, resp);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
