package org.soccorsoweb.business.controller.admin.add;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.soccorsoweb.business.controller.SoccorsoBaseController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.SquadraDAO;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AddTeamController extends SoccorsoBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        if (!SecurityHelpers.isAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            if ("GET".equalsIgnoreCase(request.getMethod())) {
                renderAddTeam(request, response);
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
            createTeam(request, response);
        } catch (DataException | IOException ex) {
            handleError(ex, request, response);
        }
    }

    private void renderAddTeam(HttpServletRequest request, HttpServletResponse response)
            throws DataException, ServletException {
        List<Map<String, Object>> operators = new ArrayList<>();
        for (Utente operator : availableOperators()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", operator.getKey());
            item.put("label", displayName(operator));
            operators.add(item);
        }
        Map<String, Object> model = new HashMap<>();
        model.put("ctx", request.getContextPath());
        model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
        model.put("operatori", operators);
        response.setContentType("text/html;charset=UTF-8");
        try {
            Template template = cfg.getTemplate("add/squadra-add.ftl");
            template.process(model, response.getWriter());
        } catch (IOException | TemplateException ex) {
            throw new ServletException("Errore durante il rendering del form squadra", ex);
        }
    }

    private void createTeam(HttpServletRequest request, HttpServletResponse response)
            throws IOException, DataException {
        Integer capoId = parseId(request.getParameter("capo"));
        String[] memberValues = request.getParameterValues("membri");
        if (capoId == null) {
            writeBadRequest(response, "Caposquadra obbligatorio");
            return;
        }

        Map<Integer, Utente> eligible = new HashMap<>();
        for (Utente operator : availableOperators()) {
            eligible.put(operator.getKey(), operator);
        }
        Utente capo = eligible.get(capoId);
        Set<Integer> memberIds = new HashSet<>();
        if (memberValues != null) {
            for (String value : memberValues) {
                Integer memberId = parseId(value);
                if (memberId == null || !memberIds.add(memberId) || !eligible.containsKey(memberId)) {
                    writeBadRequest(response, "Membro squadra non valido");
                    return;
                }
            }
        }
        if (capo == null || memberIds.contains(capoId)) {
            writeBadRequest(response, "Caposquadra o membri non validi");
            return;
        }

        DataLayer dataLayer = this.dl;
        SquadraDAO squadraDAO = (SquadraDAO) dataLayer.getDAO(Squadra.class);
        boolean originalAutoCommit;
        try {
            originalAutoCommit = dataLayer.getConnection().getAutoCommit();
            dataLayer.getConnection().setAutoCommit(false);
            Squadra squadra = squadraDAO.createSquadra();
            squadra.setCapoSquadra(capo);
            squadraDAO.storeSquadra(squadra);
            for (Integer memberId : memberIds) {
                squadraDAO.aggiungiMembroASquadra(squadra, eligible.get(memberId));
            }
            dataLayer.getConnection().commit();
            dataLayer.getConnection().setAutoCommit(originalAutoCommit);
        } catch (SQLException | RuntimeException ex) {
            rollback(dataLayer);
            throw new DataException("Unable to create squadra", ex);
        } catch (DataException ex) {
            rollback(dataLayer);
            throw ex;
        }
        response.sendRedirect(request.getContextPath() + "/admin-dashboard?section=teams");
    }

    private List<Utente> availableOperators() throws DataException {
        return ((UtenteDAO) this.dl.getDAO(Utente.class)).getUtentiDisponibili();
    }

    private void rollback(DataLayer dataLayer) {
        try {
            dataLayer.getConnection().rollback();
            dataLayer.getConnection().setAutoCommit(true);
        } catch (SQLException ignored) {
            // The original persistence error is more useful to the caller.
        }
    }

    private void writeBadRequest(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }

    private Integer parseId(String value) {
        try {
            return value == null ? null : Integer.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String displayName(Utente user) {
        return user.getAnagrafica() == null ? user.getNomeUtente()
                : user.getAnagrafica().getNome() + " " + user.getAnagrafica().getCognome();
    }
}