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

public class EditApiServlet extends HttpServlet{
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
        Pattern pattern = Pattern.compile("/api/edit/([^/]+)/([^/]+)");
        Matcher matcher = pattern.matcher(path);

        if (matcher.matches()) {
            String role = "admin"; // TODO: replace with role from current session
            String resource = matcher.group(1); // requests, missions, etc
            String id = matcher.group(2); // ID

            resp.setContentType("text/html;charset=UTF-8");

            try {
                Map<String, Object> model = new HashMap<>();
                model.put("ctx", ctx);

                // Route to correct detail and template
                if ("admin".equals(role)) {
                    switch (resource) {
                        case "requests":
                            model.put("dettaglio", MockDataProvider.getRichiestaDetail(id));
                            renderDetailTemplate(resp, "edit/richiesta-edit.ftl", model);
                            break;
                        case "missions":
                            model.put("dettaglio", MockDataProvider.getMissioneDetail(id));
                            model.put("squadreDisponibili", MockDataProvider.getMockSquadre());
                            model.put("materialiDisponibili", MockDataProvider.getMockMateriali());
                            model.put("mezziDisponibili", MockDataProvider.getMockMezzi());
                            model.put("operatoriDisponibili", MockDataProvider.getMockOperatori());
                            renderDetailTemplate(resp, "edit/missione-edit.ftl", model);
                            break;
                        case "operators":
                            model.put("dettaglio", MockDataProvider.getOperatoreDetail(id));
                            model.put("patentiDisponibili", List.of("A", "B", "C"));
                            model.put("abilitaDisponibili", List.of("Medico", "Paramedico", "Bagnino"));
                            renderDetailTemplate(resp, "edit/operatore-edit.ftl", model);
                            break;
                        case "vehicles":
                            model.put("dettaglio", MockDataProvider.getMezzoDetail(id));
                            model.put("missioniAperte", MockDataProvider.getMockMissioni());
                            renderDetailTemplate(resp, "edit/mezzo-edit.ftl", model);
                            break;
                        case "materials":
                            model.put("dettaglio", MockDataProvider.getMaterialeDetail(id));
                            model.put("missioniAperte", MockDataProvider.getMockMissioni());
                            renderDetailTemplate(resp, "edit/materiale-edit.ftl", model);
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
