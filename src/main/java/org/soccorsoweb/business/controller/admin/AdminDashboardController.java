package org.soccorsoweb.business.controller.admin;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.soccorsoweb.business.controller.SoccorsoBaseController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.CredenzialiDAO;
import org.soccorsoweb.data.dao.AbilitaDAO;
import org.soccorsoweb.data.dao.MaterialeDAO;
import org.soccorsoweb.data.dao.MezzoDAO;
import org.soccorsoweb.data.dao.MissioneDAO;
import org.soccorsoweb.data.dao.PatenteDAO;
import org.soccorsoweb.data.dao.RichiestaDAO;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Anagrafica;
import org.soccorsoweb.model.Abilita;
import org.soccorsoweb.model.Credenziali;
import org.soccorsoweb.model.Materiale;
import org.soccorsoweb.model.Mezzo;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.Patente;
import org.soccorsoweb.model.Richiesta;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.model.enums.EsitoMissione;
import org.soccorsoweb.model.enums.StatoRichiesta;

/** Admin dashboard backed by the current session and database. */
public class AdminDashboardController extends SoccorsoBaseController {

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SecurityHelpers.isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String section = req.getParameter("section");
        if (section == null || section.isEmpty()) {
            section = "requests";
        }

        Map<String, Object> model = new HashMap<>();
        model.put("ctx", req.getContextPath());
        Map<String, Object> currentUser = buildCurrentUser(req.getSession(false));
        model.put("user", currentUser);
        model.put("currentUser", currentUser);
        model.put("section", section);
        model.put("page", getIntParameter(req, "page", 1));

        try {
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
                case "abilities":
                    loadAbilitiesSection(model, req);
                    break;
                case "licenses":
                    loadLicensesSection(model, req);
                    break;
                default:
                    break;
            }

            resp.setContentType("text/html;charset=UTF-8");
            Template tpl = cfg.getTemplate("admin-dashboard.ftl");
            tpl.process(model, resp.getWriter());
        } catch (DataException | TemplateException ex) {
            throw new ServletException("Error while loading admin dashboard", ex);
        }
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

    private int getIntParameter(HttpServletRequest req, String name, int defaultValue) {
        try {
            String value = req.getParameter(name);
            return value == null || value.isBlank() ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private DataLayer dl(HttpServletRequest req) throws ServletException {
        
        if (this.dl == null) {
            throw new ServletException("DataLayer non inizializzato");
        }
        return this.dl;
    }

    private void loadRequestsSection(Map<String, Object> model, HttpServletRequest req)
            throws DataException, ServletException {
        RichiestaDAO dao = (RichiestaDAO) this.dl(req).getDAO(Richiesta.class);
        List<Richiesta> richieste = dao.getRichiesteFiltrate(
                parseDate(req.getParameter("data_from"), false),
                parseDate(req.getParameter("data_to"), true),
                parseRequestStatus(req.getParameter("status")),
                blankToNull(req.getParameter("segnalante")));
        model.put("richieste", richieste.stream().map(this::requestRow).toList());
    }

    private void loadMissionsSection(Map<String, Object> model, HttpServletRequest req)
            throws DataException, ServletException {
        MissioneDAO dao = (MissioneDAO) this.dl(req).getDAO(Missione.class);
        List<Missione> missioni = dao.getMissioniFiltrate(
                parseDate(req.getParameter("data_from"), false),
                parseDate(req.getParameter("data_to"), true),
                parseMissionStatus(req.getParameter("status")));
        model.put("missioni", missioni.stream().map(this::missionRow).toList());
    }

    private void loadOperatorsSection(Map<String, Object> model, HttpServletRequest req)
            throws DataException, ServletException {
        
        UtenteDAO dao = (UtenteDAO) this.dl.getDAO(Utente.class);
        MissioneDAO missioneDAO = (MissioneDAO) this.dl.getDAO(Missione.class);
        CredenzialiDAO credenzialiDAO = (CredenzialiDAO) this.dl.getDAO(Credenziali.class);
        String status = blankToNull(req.getParameter("stato"));
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Utente operator : dao.getUtenti()) {
            boolean busy = !missioneDAO.getMissioniByUtente(operator).stream().allMatch(Missione::isCompletata);
            String actualStatus = busy ? "OCCUPATO" : "DISPONIBILE";
            if (status != null && !status.equals(actualStatus)) {
                continue;
            }
            Map<String, Object> row = new HashMap<>();
            Anagrafica anagrafica = operator.getAnagrafica();
            row.put("id", operator.getKey());
            row.put("nome", anagrafica == null ? operator.getNomeUtente()
                    : anagrafica.getNome() + " " + anagrafica.getCognome());
            row.put("email", credenzialiDAO.getCredenzialiByUtente(operator).getEmail());
            row.put("stato", actualStatus);
            row.put("missione_corrente", busy ? "In missione" : "-");
            row.put("monte_ore", operator.getMonteOre());
            rows.add(row);
        }
        model.put("operatori", rows);
    }

    private void loadVehiclesSection(Map<String, Object> model, HttpServletRequest req)
            throws DataException, ServletException {
        MezzoDAO dao = (MezzoDAO) this.dl(req).getDAO(Mezzo.class);
        String status = blankToNull(req.getParameter("stato"));
        model.put("mezzi", dao.getMezziConStato().stream()
                .map(this::vehicleRow)
                .filter(row -> status == null || status.equals(row.get("stato")))
                .toList());
    }

    private void loadMaterialsSection(Map<String, Object> model, HttpServletRequest req)
            throws DataException, ServletException {
        MaterialeDAO dao = (MaterialeDAO) this.dl(req).getDAO(Materiale.class);
        String status = blankToNull(req.getParameter("status"));
        Set<Integer> availableIds = new HashSet<>(dao.getMaterialiDisponibili().stream()
            .map(Materiale::getKey)
            .toList());
        model.put("materiali", dao.getMateriali().stream()
            .map(material -> materialRow(material, availableIds.contains(material.getKey())))
                .filter(row -> status == null || status.equals(row.get("stato")))
                .toList());
    }

            private void loadAbilitiesSection(Map<String, Object> model, HttpServletRequest req)
                throws DataException, ServletException {
            AbilitaDAO dao = (AbilitaDAO) this.dl(req).getDAO(Abilita.class);
            String search = blankToNull(req.getParameter("nome"));
            model.put("abilita", dao.getAbilitaList().stream()
                .map(this::abilityRow)
                .filter(row -> search == null || row.get("nome").toString().toLowerCase().contains(search.toLowerCase()))
                .toList());
            }

            private void loadLicensesSection(Map<String, Object> model, HttpServletRequest req)
                throws DataException, ServletException {
            PatenteDAO dao = (PatenteDAO) this.dl(req).getDAO(Patente.class);
            String search = blankToNull(req.getParameter("numero"));
            model.put("patenti", dao.getPatenti().stream()
                .map(this::licenseRow)
                .filter(row -> search == null || row.get("numero").toString().toLowerCase().contains(search.toLowerCase()))
                .toList());
            }

    private Map<String, Object> requestRow(Richiesta request) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", request.getKey());
        row.put("segnalante", request.getNome());
        row.put("email", request.getEmail());
        row.put("indirizzo", request.getString());
        row.put("stato", request.getStato() == null ? "" : request.getStato().name());
        row.put("data_creazione", request.getData());
        return row;
    }

    private Map<String, Object> missionRow(Missione mission) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", mission.getKey());
        row.put("richiesta_id", mission.getRichiesta() == null ? "" : mission.getRichiesta().getKey());
        row.put("squadra", mission.getSquadra() == null ? "" : mission.getSquadra().getKey());
        row.put("obiettivo", mission.getObiettivo());
        row.put("stato", mission.isCompletata() && mission.getEsito() != null
                ? mission.getEsito().name() : "IN_CORSO");
        row.put("data_inizio", mission.getInizio());
        return row;
    }

    private Map<String, Object> vehicleRow(Mezzo vehicle) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", vehicle.getKey());
        row.put("nome", vehicle.getNome());
        row.put("descrizione", vehicle.getDesc());
        row.put("targa", vehicle.getTarga());
        row.put("stato", vehicle.isAssegnato() ? "OCCUPATO" : "DISPONIBILE");
        row.put("missione_corrente", vehicle.getMissioneKey() == null ? "-" : vehicle.getMissioneKey());
        return row;
    }

    private Map<String, Object> materialRow(Materiale material, boolean available) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", material.getKey());
        row.put("stato", available ? "DISPONIBILE" : "OCCUPATO");
        row.put("nome", material.getNome());
        return row;
    }

    private Map<String, Object> abilityRow(Abilita ability) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", ability.getKey());
        row.put("nome", ability.getNome());
        row.put("descrizione", ability.getDesc());
        return row;
    }

    private Map<String, Object> licenseRow(Patente license) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", license.getKey());
        row.put("numero", license.getNumero());
        row.put("tipo", license.getTipo() == null ? "" : license.getTipo().name());
        return row;
    }

    private StatoRichiesta parseRequestStatus(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return StatoRichiesta.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private EsitoMissione parseMissionStatus(String value) {
        if (value == null || value.isBlank() || "IN_CORSO".equals(value)) {
            return null;
        }
        try {
            return EsitoMissione.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
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
