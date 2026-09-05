package org.soccorsoweb.business.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.AbilitaDAO;
import org.soccorsoweb.data.dao.AnagraficaDAO;
import org.soccorsoweb.data.dao.CredenzialiDAO;
import org.soccorsoweb.data.dao.MissioneDAO;
import org.soccorsoweb.data.dao.PatenteDAO;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Abilita;
import org.soccorsoweb.model.Anagrafica;
import org.soccorsoweb.model.Credenziali;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.Patente;
import org.soccorsoweb.model.Richiesta;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.model.Utente;

/** Operator dashboard backed by the current session and database. */
public class OperatorDashboardController extends SoccorsoBaseController {

    private Configuration cfg;

    @Override
    public void init() throws ServletException {
        super.init();
        cfg = new Configuration(Configuration.VERSION_2_3_34);
        cfg.setServletContextForTemplateLoading(getServletContext(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
    }

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SecurityHelpers.isOperator(req)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String section = req.getParameter("section");
        if (section == null || section.isEmpty()) {
            section = "missions";
        }

        Map<String, Object> model = new HashMap<>();
        model.put("ctx", req.getContextPath());
        Map<String, Object> currentUser = buildCurrentUser(req.getSession(false));
        model.put("user", currentUser);
        model.put("currentUser", currentUser);
        model.put("section", section);
        model.put("page", getIntParameter(req, "page", 1));

        try {
            Utente operator = loadCurrentUser(req);
            switch (section) {
                case "missions" -> loadMissionsSection(model, req, operator);
                case "profile" -> loadProfileSection(model, req, operator);
                default -> { }
            }

            resp.setContentType("text/html;charset=UTF-8");
            Template tpl = cfg.getTemplate("operator-dashboard.ftl");
            tpl.process(model, resp.getWriter());
        } catch (DataException | TemplateException ex) {
            throw new ServletException("Error while loading operator dashboard", ex);
        }
    }

    private Utente loadCurrentUser(HttpServletRequest req) throws DataException, ServletException {
        HttpSession session = req.getSession(false);
        Integer userId = parseUserId(session == null ? null : session.getAttribute("userid"));
        if (userId == null) {
            throw new ServletException("Sessione utente non valida");
        }

        DataLayer dataLayer = (DataLayer) req.getAttribute("datalayer");
        if (dataLayer == null) {
            throw new ServletException("DataLayer non inizializzato");
        }
        Utente operator = ((UtenteDAO) dataLayer.getDAO(Utente.class)).getUtente(userId);
        if (operator == null) {
            throw new ServletException("Operatore non trovato nel database");
        }
        return operator;
    }

    private void loadMissionsSection(Map<String, Object> model, HttpServletRequest req, Utente operator)
            throws DataException, ServletException {
        DataLayer dataLayer = (DataLayer) req.getAttribute("datalayer");
        if (dataLayer == null) {
            throw new ServletException("DataLayer non inizializzato");
        }

        String status = blankToNull(req.getParameter("status"));
        LocalDateTime from = parseDate(req.getParameter("data_from"), false);
        LocalDateTime to = parseDate(req.getParameter("data_to"), true);
        List<Map<String, Object>> rows = ((MissioneDAO) dataLayer.getDAO(Missione.class))
                .getMissioniByUtente(operator).stream()
                .filter(mission -> from == null || mission.getInizio() == null || !mission.getInizio().isBefore(from))
                .filter(mission -> to == null || mission.getInizio() == null || !mission.getInizio().isAfter(to))
                .map(this::missionRow)
                .filter(row -> status == null || status.equals(row.get("stato")))
                .limit(10)
                .toList();
        model.put("missioni", rows);
    }

        private void loadProfileSection(Map<String, Object> model, HttpServletRequest req, Utente operator)
            throws DataException, ServletException {
        DataLayer dataLayer = (DataLayer) req.getAttribute("datalayer");
        if (dataLayer == null) {
            throw new ServletException("DataLayer non inizializzato");
        }
        model.put("operatore", operator);
        model.put("anagrafica", ((AnagraficaDAO) dataLayer.getDAO(Anagrafica.class))
            .getAnagraficaByUtente(operator));
        var credenziali = ((CredenzialiDAO) dataLayer.getDAO(Credenziali.class))
            .getCredenzialiByUtente(operator);
        model.put("email", credenziali == null ? "" : credenziali.getEmail());
        model.put("patenti", ((PatenteDAO) dataLayer.getDAO(Patente.class))
            .getPatentiByUtente(operator));
        List<Abilita> selected = ((AbilitaDAO) dataLayer.getDAO(Abilita.class))
            .getAbilitaByUtente(operator);
        model.put("abilita", selected);
        model.put("abilitaDisponibili", ((AbilitaDAO) dataLayer.getDAO(Abilita.class))
            .getAbilitaList());
        model.put("abilitaSelezionate", selected.stream()
            .map(item -> String.valueOf(item.getKey())).collect(Collectors.toList()));
        model.put("csrfToken", SecurityHelpers.createCsrfToken(req));
        model.put("successMessage", req.getParameter("success"));
        model.put("errorMessage", req.getParameter("error"));
        }

    private Map<String, Object> missionRow(Missione mission) {
        Richiesta richiesta = mission.getRichiesta();
        Squadra squadra = mission.getSquadra();
        Map<String, Object> row = new HashMap<>();
        row.put("id", mission.getKey());
        row.put("richiesta_id", richiesta == null ? "" : richiesta.getKey());
        row.put("squadra", squadra == null ? "" : squadra.getKey());
        row.put("obiettivo", mission.getObiettivo());
        row.put("stato", mission.isCompletata() ? "CHIUSA" : "IN_CORSO");
        row.put("data_inizio", mission.getInizio());
        return row;
    }

    private Map<String, Object> buildCurrentUser(HttpSession session) {
        Map<String, Object> user = new HashMap<>();
        user.put("authenticated", session != null && session.getAttribute("userid") != null);
        if (session != null) {
            user.put("userid", session.getAttribute("userid"));
            user.put("username", session.getAttribute("username"));
            user.put("nome", session.getAttribute("username"));
            user.put("ruolo", session.getAttribute("ruolo"));
        }
        return user;
    }

    private Integer parseUserId(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private int getIntParameter(HttpServletRequest req, String name, int defaultValue) {
        try {
            String value = req.getParameter(name);
            return value == null || value.isBlank() ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private LocalDateTime parseDate(String value, boolean endOfDay) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(value);
            return endOfDay ? date.plusDays(1).atStartOfDay().minusNanos(1) : date.atStartOfDay();
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
