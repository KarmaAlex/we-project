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
import org.soccorsoweb.data.dao.PatenteDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Patente;
import org.soccorsoweb.model.enums.TipoPatente;

public class AddLicenseController extends SoccorsoBaseController {
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
                String number = SecurityHelpers.sanitizeTextInput(request.getParameter("numero"));
                String type = SecurityHelpers.sanitizeTextInput(request.getParameter("tipo"));
                if (number == null || number.isBlank() || type == null || type.isBlank()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Numero e tipo sono obbligatori");
                    return;
                }
                TipoPatente licenseType;
                try {
                    licenseType = TipoPatente.valueOf(type.toUpperCase());
                } catch (IllegalArgumentException ex) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tipo patente non valido");
                    return;
                }
                PatenteDAO dao = (PatenteDAO) dl.getDAO(Patente.class);
                Patente license = dao.createPatente();
                license.setNumero(number);
                license.setTipo(licenseType);
                dao.storePatente(license);
                response.sendRedirect(request.getContextPath() + "/admin-dashboard?section=licenses");
            } else {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        } catch (IOException | DataException | TemplateException ex) {
            throw new ServletException("Errore nella gestione della patente", ex);
        }
    }

    private void render(HttpServletRequest request, HttpServletResponse response)
            throws IOException, TemplateException {
        response.setContentType("text/html;charset=UTF-8");
        Map<String, Object> model = new HashMap<>();
        model.put("ctx", request.getContextPath());
        model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
        model.put("tipiPatente", TipoPatente.values());
        Template template = cfg.getTemplate("add/patente-add.ftl");
        template.process(model, response.getWriter());
    }
}