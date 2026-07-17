package org.soccorsoweb.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AddApiServlet extends HttpServlet{
    private Configuration cfg;

    @Override
    public void init() throws ServletException {
        super.init();
        cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setServletContextForTemplateLoading(getServletContext(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ctx = req.getContextPath();
        String path = req.getRequestURI().substring(ctx.length());

        // Parse path: /api/edit/{resource}/{id}
        Pattern pattern = Pattern.compile("/api/add/([^/]+)");
        Matcher matcher = pattern.matcher(path);

        if (matcher.matches()) {
            String role = "admin"; // TODO: replace with role from current session
            String resource = matcher.group(1); // requests, missions, etc

            resp.setContentType("text/html;charset=UTF-8");

            try {
                Map<String, Object> model = new HashMap<>();
                model.put("ctx", ctx);

                // Route to correct detail and template
                if ("admin".equals(role)) {
                    switch (resource) {
                        case "requests":
                            renderDetailTemplate(resp, "edit/richiesta-edit.ftl", model);
                            break;
                        case "missions":
                            model.put("squadreDisponibili", MockDataProvider.getMockSquadre());
                            model.put("materialiDisponibili", MockDataProvider.getMockMateriali());
                            model.put("mezziDisponibili", MockDataProvider.getMockMezzi());
                            model.put("operatoriDisponibili", MockDataProvider.getMockOperatori());
                            renderDetailTemplate(resp, "add/missione-add.ftl", model);
                            break;
                        case "operators":
                            renderDetailTemplate(resp, "edit/operatore-edit.ftl", model);
                            break;
                        case "vehicles":
                            renderDetailTemplate(resp, "edit/mezzo-edit.ftl", model);
                            break;
                        case "materials":
                            renderDetailTemplate(resp, "edit/materiale-edit.ftl", model);
                            break;
                        default:
                            resp.setStatus(404);
                            resp.getWriter().write("<p>Risorsa non trovata</p>");
                    }
                } else if ("operator".equals(role)) {
                    if ("missions".equals(resource)) {
                        renderDetailTemplate(resp, "details/missione-detail.ftl", model);
                    } else {
                        resp.setStatus(404);
                        resp.getWriter().write("<p>Risorsa non trovata</p>");
                    }
                } else {
                    resp.setStatus(404);
                    resp.getWriter().write("<p>API endpoint non trovato</p>");
                }

            } catch (Exception ex) {
                resp.setStatus(500);
                resp.getWriter().write("<p>"+ex.getMessage()+"</p>");
            }
            return;
        }

        resp.setStatus(404);
        resp.getWriter().write("<p>API endpoint non trovato</p>");
    }

    private void renderDetailTemplate(HttpServletResponse resp, String templatePath, Map<String, Object> model)
            throws TemplateException, IOException {
        Template tpl = cfg.getTemplate(templatePath);
        tpl.process(model, resp.getWriter());
    }
}
