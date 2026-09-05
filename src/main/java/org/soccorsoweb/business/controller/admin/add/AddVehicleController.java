package org.soccorsoweb.business.controller.admin.add;

import java.io.IOException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.util.HashMap;
import java.util.Map;

import org.soccorsoweb.business.controller.SoccorsoBaseController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.MezzoDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Mezzo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AddVehicleController extends SoccorsoBaseController {
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        if (!SecurityHelpers.isAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            renderAddVehiclePage(request, response);
            return;
        }

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        if (!SecurityHelpers.isValidCsrfToken(request, request.getParameter("csrf"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }



        try {
            
            addMezzo(request, response, this.dl);
        } catch (IOException | DataException ex) {
            handleError(ex, request, response);
        }
    }


       /**
     * Aggiunge un nuovo mezzo
     */
    private void addMezzo(HttpServletRequest request, HttpServletResponse response, DataLayer dl)
            throws IOException, DataException {

        response.setContentType("application/json;charset=UTF-8");

        String nome = SecurityHelpers.sanitizeTextInput(request.getParameter("nome"));
        String descrizione = SecurityHelpers.sanitizeTextInput(request.getParameter("descrizione"));
        String targa = SecurityHelpers.sanitizeTextInput(request.getParameter("targa"));

        if (nome == null || nome.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Nome mezzo obbligatorio\"}");
            return;
        }

        if (descrizione == null || descrizione.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Descrizione obbligatoria\"}");
            return;
        }

        if (targa == null || targa.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Targa obbligatoria\"}");
            return;
        }

        if(targa.length() > 10){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Targa non valida\"}");
            return;
        }

        MezzoDAO mezzoDAO = (MezzoDAO) this.dl.getDAO(Mezzo.class);
        Mezzo mezzo = mezzoDAO.createMezzo();

        mezzo.setNome(nome);
        mezzo.setDesc(descrizione);
        mezzo.setTarga(targa);

        mezzoDAO.storeMezzo(mezzo);

        if (mezzo.getKey() == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Errore: mezzo non salvato correttamente\"}");
            return;
        }

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.sendRedirect(request.getContextPath() + "/admin-dashboard?section=vehicles");
    }
        private void renderAddVehiclePage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        response.setContentType("text/html;charset=UTF-8");
        Map<String, Object> model = new HashMap<>();
        model.put("ctx", request.getContextPath());
        model.put("csrfToken", SecurityHelpers.createCsrfToken(request));

        try {
            Template template = cfg.getTemplate("add/mezzo-add.ftl");
            template.process(model, response.getWriter());
        } catch (IOException | TemplateException ex) {
            throw new ServletException("Errore durante il rendering del form mezzo", ex);
        }
    }
}
