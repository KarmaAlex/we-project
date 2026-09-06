package org.soccorsoweb.business.controller.admin.details;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.soccorsoweb.business.controller.AbstractIdRequiredController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.dao.MissioneDAO;
import org.soccorsoweb.data.dao.SquadraDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TeamDetailsController extends AbstractIdRequiredController {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        if (!SecurityHelpers.isAdmin(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Integer teamId = getRequestId(request);
        if (teamId == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            if (dl == null) {
                throw new ServletException("DataLayer non inizializzato");
            }
            Squadra squadra = ((SquadraDAO) dl.getDAO(Squadra.class)).getSquadra(teamId);
            if (squadra == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            renderDetails(request, response, squadra);
        } catch (DataException | IOException ex) {
            handleError(ex, request, response);
        }
    }

    private void renderDetails(HttpServletRequest request, HttpServletResponse response,
            Squadra squadra) throws IOException, DataException, ServletException {
        Missione currentMission = ((MissioneDAO) dl.getDAO(Missione.class))
                .getMissioniBySquadra(squadra).stream()
                .filter(mission -> !mission.isCompletata())
                .findFirst()
                .orElse(null);

        Map<String, Object> detail = new HashMap<>();
        detail.put("id", squadra.getKey());
        detail.put("capo", displayName(squadra.getCapoSquadra()));
        detail.put("stato", currentMission == null ? "DISPONIBILE" : "OCCUPATA");
        detail.put("missione_corrente", currentMission == null ? null : currentMission.getKey());

        List<String> members = new ArrayList<>();
        for (Utente member : squadra.getOperatori()) {
            members.add(displayName(member));
        }
        detail.put("membri", members);

        Map<String, Object> model = new HashMap<>();
        model.put("ctx", request.getContextPath());
        model.put("dettaglio", detail);
        response.setContentType("text/html;charset=UTF-8");
        try {
            Template template = cfg.getTemplate("details/squadra-detail.ftl");
            template.process(model, response.getWriter());
        } catch (TemplateException ex) {
            throw new ServletException("Errore durante il rendering del dettaglio squadra", ex);
        }
    }

    private String displayName(Utente user) {
        if (user == null) {
            return "-";
        }
        if (user.getAnagrafica() == null) {
            return user.getNomeUtente();
        }
        return user.getAnagrafica().getNome() + " " + user.getAnagrafica().getCognome();
    }
}