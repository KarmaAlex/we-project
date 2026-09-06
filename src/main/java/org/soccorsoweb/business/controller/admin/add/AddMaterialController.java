package org.soccorsoweb.business.controller.admin.add;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.soccorsoweb.business.controller.SoccorsoBaseController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.dao.MaterialeDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Materiale;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AddMaterialController extends SoccorsoBaseController {

    private static final SecureRandom RANDOM = new SecureRandom();
    
    @Override 
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        if (!SecurityHelpers.isAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            renderAddMaterialPage(request, response);
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
            
            addMateriale(request, response);
        } catch (IOException | DataException ex) {
            handleError(ex, request, response);
        }
    }

    private void renderAddMaterialPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        response.setContentType("text/html;charset=UTF-8");
        Map<String, Object> model = new HashMap<>();
        model.put("ctx", request.getContextPath());
        model.put("csrfToken", SecurityHelpers.createCsrfToken(request));

        try {
            Template template = cfg.getTemplate("add/materiale-add.ftl");
            template.process(model, response.getWriter());
        } catch (IOException | TemplateException ex) {
            throw new ServletException("Errore durante il rendering del form materiale", ex);
        }
    }


    private void addMateriale(HttpServletRequest request, HttpServletResponse response)
            throws IOException, DataException {
        response.setContentType("application/json;charset=UTF-8");

        // Validazione e sanificazione input
        String nome = SecurityHelpers.sanitizeTextInput(request.getParameter("nome"));
        String descrizione = SecurityHelpers.sanitizeTextInput(request.getParameter("descrizione"));

        // Validazione campi obbligatori
        if (nome == null || nome.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Nome materiale obbligatorio\"}");
            return;
        }

        if (descrizione == null || descrizione.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Descrizione obbligatoria\"}");
            return;
        }

        // Creazione e salvataggio materiale
        MaterialeDAO materialeDAO = (MaterialeDAO) this.dl.getDAO(Materiale.class);
        Materiale materiale = materialeDAO.createMateriale();
        
        // Impostiamo i dati
        materiale.setNome(nome);
        materiale.setDesc(descrizione);
        materiale.setCodMat(generateCodiceMateriale());
        
        // Salviamo il materiale
        materialeDAO.storeMateriale(materiale);
        
        // Validazione chiave generata
        if (materiale.getKey() == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Errore: materiale non salvato correttamente\"}");
            return;
        }
        
        // Risposta di successo
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.sendRedirect(request.getContextPath() + "/admin-dashboard?section=materials");
    }

    private String generateCodiceMateriale() {
        return String.format("MAT-%06d", RANDOM.nextInt(1000000));
    }
}
