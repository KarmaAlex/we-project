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
import java.util.List;
import java.util.Map;

import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.test.MockDataProvider;

/**
 * OperatorDashboardServlet: Gestisce la dashboard operatore
 * Route: /operator-dashboard?section={missions|profile}
 */
public class OperatorDashboardServlet extends HttpServlet {

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

        if ("/operator-dashboard".equals(path)) {
            if (!SecurityHelpers.isOperator(req)) {
                resp.sendRedirect(ctx + "/login");
                return;
            }

            resp.setContentType("text/html;charset=UTF-8");

            String section = req.getParameter("section");
            if (section == null || section.isEmpty()) {
                section = "missions";
            }

            try {
                Map<String, Object> model = new HashMap<>();
                model.put("ctx", ctx);

                // Mock operator user
                Map<String, Object> user = new HashMap<>();
                user.put("id", 1);
                user.put("nome", "Marco Bianchi");
                user.put("email", "marco@soccorsoweb.it");
                user.put("ruolo", "OPERATOR");
                model.put("user", user);
                user.put("authenticated", true);
                model.put("currentUser", user);

                model.put("section", section);
                model.put("page", getIntParameter(req, "page", 1));

                // Load data based on section
                switch (section) {
                    case "missions":
                        loadMissionsSection(model, req);
                        break;
                    case "profile":
                        loadProfileSection(model, req);
                        break;
                }

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

    private List<Map<String, Object>> filterMockMissioni(HttpServletRequest req) {
        List<Map<String, Object>> all = MockDataProvider.getMockMissioni();
        String status = req.getParameter("status");

        return all.stream()
                .filter(m -> "IN_CORSO".equals(m.get("stato")) || "CHIUSA".equals(m.get("stato")))
                .filter(m -> status == null || status.isEmpty() || status.equals(m.get("stato")))
                .limit(10)
                .toList();
    }

    private void loadMissionsSection(Map<String, Object> model, HttpServletRequest req) {
        model.put("missioni", filterMockMissioni(req));
    }

    private void loadProfileSection(Map<String, Object> model, HttpServletRequest req) {
        model.put("operatore", getMockOperatorProfile());
    }

    private Map<String, Object> getMockOperatorProfile() {
        return MockDataProvider.getOperatoreDetail("OP-001");
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

    private String getTemplateNameForSection(String section) {
        return switch (section) {
            case "missions" -> "operator-dashboard.ftl";
            case "profile" -> "operator-dashboard.ftl";
            default -> "operator-dashboard.ftl";
        };
    }
}
