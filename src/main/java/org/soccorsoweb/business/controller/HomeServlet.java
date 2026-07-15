package org.soccorsoweb.business.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HomeServlet extends HttpServlet {

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
        // If the request targets a static resource (css, js, images, etc.),
        // forward to the container's default servlet so static files are served
        String ctx = req.getContextPath();
        String path = req.getRequestURI().substring(ctx.length());
        if (path == null || path.isEmpty() || "/".equals(path) || "/home".equals(path)) {
            resp.setContentType("text/html;charset=UTF-8");
            try {
                Template tpl = cfg.getTemplate("home.ftl");
                Map<String, Object> model = new HashMap<>();
                model.put("ctx", req.getContextPath());
                tpl.process(model, resp.getWriter());
            } catch (TemplateException ex) {
                throw new ServletException("Error while processing Freemarker template", ex);
            }
            return;
        }

        // forward other requests (static assets) to default servlet
        RequestDispatcher rd = getServletContext().getNamedDispatcher("default");
        if (rd != null) {
            rd.forward(req, resp);
            return;
        }

        // fallback: send 404
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
