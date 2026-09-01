package org.soccorsoweb.business.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.soccorsoweb.business.controller.SoccorsoBaseController;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.dao.MaterialeDAO;
import org.soccorsoweb.data.dao.MezzoDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Materiale;
import org.soccorsoweb.model.Mezzo;

/**
 * AggiuntaRisorseController: Gestisce l'aggiunta di risorse
 * Route: POST /admin-dashboard/add?section={materials|missions|operators|vehicles}
 */
public class AggiuntaRisorseController extends SoccorsoBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        // Verifica permessi admin
        if (!SecurityHelpers.isAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String section = request.getParameter("section");
        if (section == null || section.isEmpty()) {
            section = "materials";
        }

        try {
            DataLayer dataLayer = (DataLayer) request.getAttribute("datalayer");

            switch (section) {
                case "materials":
                    addMateriale(request, response, dataLayer);
                    break;
                case "missions":
                    response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
                    response.getWriter().write("{\"error\":\"Non implementato\"}");
                    break;
                case "operators":
                    response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
                    response.getWriter().write("{\"error\":\"Non implementato\"}");
                    break;
                case "vehicles":
                    addMezzo(request, response, dataLayer);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\":\"Sezione non supportata\"}");
            }

        } catch (IOException | DataException ex) {
            handleError(ex, request, response);
        }
    }

    /**
     * Aggiunge un nuovo materiale
     */
    private void addMateriale(HttpServletRequest request, HttpServletResponse response, DataLayer dataLayer)
            throws IOException, DataException {
        
        response.setContentType("application/json;charset=UTF-8");

        // Validazione e sanificazione input
        String nome = SecurityHelpers.sanitizeTextInput(request.getParameter("nome"));
        String descrizione = SecurityHelpers.sanitizeTextInput(request.getParameter("descrizione"));
        String quantitaStr = request.getParameter("quantita");

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

        if (quantitaStr == null || quantitaStr.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Quantità obbligatoria\"}");
            return;
        }

        // Validazione quantità (numero positivo)
        int quantita;
        try {
            quantita = SecurityHelpers.checkNumeric(quantitaStr);
            if (quantita < 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Quantità deve essere positiva\"}");
                return;
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Quantità non valida\"}");
            return;
        }

        // Creazione e salvataggio materiale
        MaterialeDAO materialeDAO = (MaterialeDAO) dataLayer.getDAO(Materiale.class);
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
        response.getWriter().write("{\"success\":true,\"message\":\"Materiale aggiunto con successo\",\"id\":" + materiale.getKey() + "}");
    }

    /**
     * Aggiunge un nuovo mezzo
     */
    private void addMezzo(HttpServletRequest request, HttpServletResponse response, DataLayer dataLayer)
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

        MezzoDAO mezzoDAO = (MezzoDAO) dataLayer.getDAO(Mezzo.class);
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
        response.getWriter().write("{\"success\":true,\"message\":\"Mezzo aggiunto con successo\",\"id\":" + mezzo.getKey() + "}");
    }

    /**
     * Genera un codice materiale univoco
     */
    private String generateCodiceMateriale() {
        return "MAT-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
    }
}
