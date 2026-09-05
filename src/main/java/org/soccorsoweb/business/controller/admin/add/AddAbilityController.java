package org.soccorsoweb.business.controller.admin.add;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.soccorsoweb.business.controller.SoccorsoBaseController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.dao.AbilitaDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Abilita;

public class AddAbilityController extends SoccorsoBaseController {
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if (!SecurityHelpers.isAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            if ("GET".equalsIgnoreCase(request.getMethod())) {
                render(request, response);
            } else if ("POST".equalsIgnoreCase(request.getMethod())) {
                if (!SecurityHelpers.isValidCsrfToken(request, request.getParameter("csrf"))) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                String name = SecurityHelpers.sanitizeTextInput(request.getParameter("nome"));
                if (name == null || name.isBlank()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nome abilità obbligatorio");
                    return;
                }
                AbilitaDAO dao = (AbilitaDAO) dl.getDAO(Abilita.class);
                Abilita ability = dao.createAbilita();
                ability.setNome(name);
                ability.setDesc(name);
                dao.storeAbilita(ability);
                response.sendRedirect(request.getContextPath() + "/admin-dashboard?section=abilities");
            } else {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        } catch (IOException | DataException | TemplateException ex) {
            throw new ServletException("Errore nella gestione dell'abilità", ex);
        }
    }

    private void render(HttpServletRequest request, HttpServletResponse response)
            throws IOException, TemplateException {
        response.setContentType("text/html;charset=UTF-8");
        Map<String, Object> model = new HashMap<>();
        model.put("ctx", request.getContextPath());
        model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
        Template template = cfg.getTemplate("add/abilita-add.ftl");
        template.process(model, response.getWriter());
    }
}