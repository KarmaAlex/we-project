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

import org.soccorsoweb.test.MockDataProvider;

import java.util.List;

/**
 * AdminDashboardServlet: Gestisce la dashboard amministratore
 * Route: /admin-dashboard?section={requests|missions|operators|vehicles|materials}
 */
public class AdminDashboardServlet extends HttpServlet {

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

        if ("/admin-dashboard".equals(path)) {
            resp.setContentType("text/html;charset=UTF-8");
            
            String section = req.getParameter("section");
            if (section == null || section.isEmpty()) {
                section = "requests";
            }

            try {
                Template tpl = cfg.getTemplate("admin-dashboard.ftl");
                Map<String, Object> model = new HashMap<>();
                model.put("ctx", ctx);

                // Mock user
                Map<String, Object> user = new HashMap<>();
                user.put("id", 1);
                user.put("nome", "Admin Test");
                user.put("email", "admin@soccorsoweb.it");
                user.put("ruolo", "ADMIN");
                model.put("user", user);

                model.put("section", section);
                model.put("page", getIntParameter(req, "page", 1));

                // Load data based on section
                switch (section) {
                    case "requests":
                        model.put("richieste", filterMockRichieste(req));
                        break;
                    case "missions":
                        model.put("missioni", filterMockMissioni(req));
                        break;
                    case "operators":
                        model.put("operatori", MockDataProvider.getMockOperatori());
                        break;
                    case "vehicles":
                        model.put("mezzi", MockDataProvider.getMockMezzi());
                        break;
                    case "materials":
                        model.put("materiali", MockDataProvider.getMockMateriali());
                        break;
                }

                tpl.process(model, resp.getWriter());

            } catch (TemplateException ex) {
                throw new ServletException("Error while processing Freemarker template", ex);
            }
            return;
        }

        // Forward to default servlet for static files
        jakarta.servlet.RequestDispatcher rd = getServletContext().getNamedDispatcher("default");
        if (rd != null) {
            rd.forward(req, resp);
        }
    }

    private int getIntParameter(HttpServletRequest req, String paramName, int defaultValue) {
        String param = req.getParameter(paramName);
        if (param != null && !param.isEmpty()) {
            try {
                return Integer.parseInt(param);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private List<Map<String, Object>> filterMockRichieste(HttpServletRequest req) {
        List<Map<String, Object>> all = MockDataProvider.getMockRichieste();
        String status = req.getParameter("status");
        String segnalante = req.getParameter("segnalante");
        
        System.out.println("[FilterRichieste] Original count: " + all.size());
        System.out.println("[FilterRichieste] Status filter: " + status);
        System.out.println("[FilterRichieste] Segnalante filter: " + segnalante);

        List<Map<String, Object>> filtered = all.stream()
            .filter(r -> status == null || status.isEmpty() || status.equals(r.get("stato")))
            .filter(r -> segnalante == null || segnalante.isEmpty() || 
                        ((String)r.get("email")).toLowerCase().contains(segnalante.toLowerCase()))
            .toList();
        
        System.out.println("[FilterRichieste] Filtered count: " + filtered.size());
        return filtered;
    }

    private List<Map<String, Object>> filterMockMissioni(HttpServletRequest req) {
        List<Map<String, Object>> all = MockDataProvider.getMockMissioni();
        String status = req.getParameter("status");

        return all.stream()
            .filter(m -> status == null || status.isEmpty() || status.equals(m.get("stato")))
            .toList();
    }
}
