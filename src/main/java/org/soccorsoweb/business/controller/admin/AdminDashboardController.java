package org.soccorsoweb.business.controller.admin;

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

import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.test.MockDataProvider;

import java.util.List;

/**
 * AdminDashboardServlet: Gestisce la dashboard amministratore
 * Route: /admin-dashboard?section={requests|missions|operators|vehicles|materials}
 */
public class AdminDashboardController extends HttpServlet {

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
            if (!SecurityHelpers.isAdmin(req)) {
                resp.sendRedirect(ctx + "/login");
                return;
            }

            resp.setContentType("text/html;charset=UTF-8");
            
            String section = req.getParameter("section");
            if (section == null || section.isEmpty()) {
                section = "requests";
            }

            try {
                // Preparare il modello comune
                Map<String, Object> model = new HashMap<>();
                model.put("ctx", ctx);

                // Mock user
                Map<String, Object> user = new HashMap<>();
                user.put("id", 1);
                user.put("nome", "Admin Test");
                user.put("email", "admin@soccorsoweb.it");
                user.put("ruolo", "ADMIN");
                model.put("user", user);
                user.put("authenticated", true);
                model.put("currentUser", user);

                model.put("section", section);
                model.put("page", getIntParameter(req, "page", 1));

                // Load data based on section
                switch (section) {
                    case "requests":
                        loadRequestsSection(model, req);
                        break;
                    case "missions":
                        loadMissionsSection(model, req);
                        break;
                    case "operators":
                        loadOperatorsSection(model, req);
                        break;
                    case "vehicles":
                        loadVehiclesSection(model, req);
                        break;
                    case "materials":
                        loadMaterialsSection(model, req);
                        break;
                }

                // Renderizzare il template specifico della sezione
                String templateName = getTemplateNameForSection(section);
                Template tpl = cfg.getTemplate(templateName);
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

    /**
     * Carica la sezione delle richieste
     */
    private void loadRequestsSection(Map<String, Object> model, HttpServletRequest req) {
        model.put("richieste", filterMockRichieste(req));
    }

    /**
     * Carica la sezione delle missioni
     */
    private void loadMissionsSection(Map<String, Object> model, HttpServletRequest req) {
        model.put("missioni", filterMockMissioni(req));
    }

    /**
     * Carica la sezione degli operatori
     */
    private void loadOperatorsSection(Map<String, Object> model, HttpServletRequest req) {
        model.put("operatori", filterMockOperatori(req));
    }

    /**
     * Carica la sezione dei mezzi
     */
    private void loadVehiclesSection(Map<String, Object> model, HttpServletRequest req) {
        model.put("mezzi", filterMockMezzi(req));
    }

    /**
     * Carica la sezione dei materiali
     */
    private void loadMaterialsSection(Map<String, Object> model, HttpServletRequest req) {
        model.put("materiali", filterMockMateriali(req));
    }

    /**
     * Filtra gli operatori in base ai parametri della richiesta
     */
    private List<Map<String, Object>> filterMockOperatori(HttpServletRequest req) {
        List<Map<String, Object>> all = MockDataProvider.getMockOperatori();
        String ruolo = req.getParameter("ruolo");
        String stato = req.getParameter("stato");

        return all.stream()
            .filter(o -> ruolo == null || ruolo.isEmpty() || ruolo.equals(o.get("ruolo")))
            .filter(o -> stato == null || stato.isEmpty() || stato.equals(o.get("stato")))
            .toList();
    }

    /**
     * Filtra i mezzi in base ai parametri della richiesta
     */
    private List<Map<String, Object>> filterMockMezzi(HttpServletRequest req) {
        List<Map<String, Object>> all = MockDataProvider.getMockMezzi();
        String tipo = req.getParameter("tipo");
        String stato = req.getParameter("stato");

        return all.stream()
            .filter(m -> tipo == null || tipo.isEmpty() || tipo.equals(m.get("tipo")))
            .filter(m -> stato == null || stato.isEmpty() || stato.equals(m.get("stato")))
            .toList();
    }

    /**
     * Filtra i materiali in base ai parametri della richiesta
     */
    private List<Map<String, Object>> filterMockMateriali(HttpServletRequest req) {
        List<Map<String, Object>> all = MockDataProvider.getMockMateriali();
        String tipo = req.getParameter("tipo");
        String categoria = req.getParameter("categoria");

        return all.stream()
            .filter(m -> tipo == null || tipo.isEmpty() || tipo.equals(m.get("tipo")))
            .filter(m -> categoria == null || categoria.isEmpty() || categoria.equals(m.get("categoria")))
            .toList();
    }

    /**
     * Restituisce il nome del template da renderizzare in base alla sezione
     */
    private String getTemplateNameForSection(String section) {
        return switch (section) {
            case "requests" -> "admin/requests-table.ftl";
            case "missions" -> "admin/missions-table.ftl";
            case "operators" -> "admin/operators-table.ftl";
            case "vehicles" -> "admin/vehicles-table.ftl";
            case "materials" -> "admin/materials-table.ftl";
            default -> "admin-dashboard.ftl";
        };
    }
}
