package org.soccorsoweb.business.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.dao.AbilitaDAO;
import org.soccorsoweb.data.dao.AnagraficaDAO;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Abilita;
import org.soccorsoweb.model.Anagrafica;
import org.soccorsoweb.model.Utente;

public class OperatorProfileController extends SoccorsoBaseController {
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SecurityHelpers.isOperator(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        if (!SecurityHelpers.isValidCsrfToken(request, request.getParameter("csrf"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            Utente operator = currentUser(request);
            AbilitaDAO abilitaDAO = (AbilitaDAO) dl.getDAO(Abilita.class);
            if (request.getRequestURI().endsWith("/abilities")) {
                createAbility(request, response, operator, abilitaDAO);
            } else {
                updateProfile(request, response, operator, abilitaDAO);
            }
        } catch (DataException ex) {
            throw new ServletException("Errore durante l'aggiornamento del profilo", ex);
        }
    }

    private Utente currentUser(HttpServletRequest request) throws DataException, ServletException {
        HttpSession session = request.getSession(false);
        Integer userId = parseUserId(session == null ? null : session.getAttribute("userid"));
        if (userId == null) {
            throw new ServletException("Sessione utente non valida");
        }
        Utente operator = ((UtenteDAO) dl.getDAO(Utente.class)).getUtente(userId);
        if (operator == null) {
            throw new ServletException("Operatore non trovato");
        }
        return operator;
    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response,
            Utente operator, AbilitaDAO abilitaDAO) throws DataException, IOException, ServletException {
        String nome = SecurityHelpers.sanitizeTextInput(request.getParameter("nome"));
        String cognome = SecurityHelpers.sanitizeTextInput(request.getParameter("cognome"));
        if (nome.isBlank() || cognome.isBlank()) {
            redirect(response, request, "error=Dati+anagrafici+obbligatori");
            return;
        }
        AnagraficaDAO anagraficaDAO = (AnagraficaDAO) dl.getDAO(Anagrafica.class);
        Anagrafica anagrafica = anagraficaDAO.getAnagraficaByUtente(operator);
        if (anagrafica == null) {
            anagrafica = anagraficaDAO.createAnagrafica();
        }
        anagrafica.setNome(nome);
        anagrafica.setCognome(cognome);
        anagraficaDAO.storeAnagrafica(anagrafica, operator);

        Set<Integer> requested = parseIds(request.getParameterValues("abilita"));
        Set<Integer> available = new HashSet<>();
        for (Abilita ability : abilitaDAO.getAbilitaList()) {
            available.add(ability.getKey());
        }
        if (!available.containsAll(requested)) {
            redirect(response, request, "error=Abilita+non+valida");
            return;
        }
        Set<Integer> current = new HashSet<>();
        for (Abilita ability : abilitaDAO.getAbilitaByUtente(operator)) {
            current.add(ability.getKey());
        }
        for (Integer id : requested) {
            if (!current.contains(id)) {
                abilitaDAO.legaAbilitaAUtente(abilitaDAO.getAbilita(id), operator);
            }
        }
        for (Integer id : current) {
            if (!requested.contains(id)) {
                abilitaDAO.slegaAbilitaDaUtente(abilitaDAO.getAbilita(id), operator);
            }
        }
        redirect(response, request, "success=Profilo+aggiornato");
    }

    private void createAbility(HttpServletRequest request, HttpServletResponse response,
            Utente operator, AbilitaDAO abilitaDAO) throws DataException, IOException {
        String name = SecurityHelpers.sanitizeTextInput(request.getParameter("nome"));
        if (name.isBlank()) {
            redirect(response, request, "error=Nome+abilita+obbligatorio");
            return;
        }
        Abilita ability = abilitaDAO.createAbilita();
        ability.setNome(name);
        ability.setDesc(name);
        abilitaDAO.storeAbilita(ability);
        abilitaDAO.legaAbilitaAUtente(ability, operator);
        redirect(response, request, "success=Abilita+aggiunta");
    }

    private void redirect(HttpServletResponse response, HttpServletRequest request, String query)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/operator-dashboard?section=profile&" + query);
    }

    private Set<Integer> parseIds(String[] values) throws ServletException {
        Set<Integer> ids = new HashSet<>();
        if (values == null) {
            return ids;
        }
        try {
            for (String value : values) {
                ids.add(Integer.valueOf(value));
            }
        } catch (NumberFormatException ex) {
            throw new ServletException("Identificativo abilita non valido", ex);
        }
        return ids;
    }

    private Integer parseUserId(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return value == null ? null : Integer.valueOf(value.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}