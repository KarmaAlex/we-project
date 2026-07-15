package org.soccorsoweb.test;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DetailApiServlet: Fornisce dettagli per i modali
 * Routes:
 * /api/admin/requests/{id}/detail
 * /api/admin/missions/{id}/detail
 * /api/admin/operators/{id}/detail
 * /api/admin/vehicles/{id}/detail
 * /api/admin/materials/{id}/detail
 * /api/operator/missions/{id}/detail
 */
public class DetailApiServlet extends HttpServlet {

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

        // Parse path: /api/TYPE/RESOURCE/{id}/detail
        Pattern pattern = Pattern.compile("/api/([^/]+)/([^/]+)/([^/]+)/detail");
        Matcher matcher = pattern.matcher(path);

        if (matcher.matches()) {
            String role = matcher.group(1); // admin or operator
            String resource = matcher.group(2); // requests, missions, etc
            String id = matcher.group(3); // ID

            resp.setContentType("text/html;charset=UTF-8");

            try {
                Map<String, Object> model = new HashMap<>();
                model.put("ctx", ctx);

                // Route to correct detail and template
                if ("admin".equals(role)) {
                    switch (resource) {
                        case "requests":
                            model.put("dettaglio", MockDataProvider.getRichiestaDetail(id));
                            renderDetailTemplate(resp, "details/richiesta-detail.ftl", model);
                            break;
                        case "missions":
                            model.put("dettaglio", MockDataProvider.getMissioneDetail(id));
                            renderDetailTemplate(resp, "details/missione-detail.ftl", model);
                            break;
                        case "operators":
                            model.put("dettaglio", MockDataProvider.getOperatoreDetail(id));
                            renderDetailTemplate(resp, "details/operatore-detail.ftl", model);
                            break;
                        case "vehicles":
                            model.put("dettaglio", MockDataProvider.getMezzoDetail(id));
                            renderDetailTemplate(resp, "details/mezzo-detail.ftl", model);
                            break;
                        case "materials":
                            model.put("dettaglio", MockDataProvider.getMaterialeDetail(id));
                            renderDetailTemplate(resp, "details/materiale-detail.ftl", model);
                            break;
                        default:
                            resp.setStatus(404);
                            resp.getWriter().write("<p>Risorsa non trovata</p>");
                    }
                } else if ("operator".equals(role)) {
                    if ("missions".equals(resource)) {
                        model.put("dettaglio", MockDataProvider.getMissioneDetail(id));
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
                resp.getWriter().write("<p>Errore nel caricamento dei dati</p>");
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
