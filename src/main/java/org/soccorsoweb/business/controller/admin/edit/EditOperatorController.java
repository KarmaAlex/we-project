package org.soccorsoweb.business.controller.admin.edit;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.soccorsoweb.business.controller.AbstractIdRequiredController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.AnagraficaDAO;
import org.soccorsoweb.data.dao.AbilitaDAO;
import org.soccorsoweb.data.dao.CredenzialiDAO;
import org.soccorsoweb.data.dao.PatenteDAO;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Anagrafica;
import org.soccorsoweb.model.Abilita;
import org.soccorsoweb.model.Credenziali;
import org.soccorsoweb.model.Patente;
import org.soccorsoweb.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class EditOperatorController extends AbstractIdRequiredController {

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		if (!SecurityHelpers.isAdmin(request)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		Integer operatorId = getRequestId(request);
		if (operatorId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			
			if (this.dl == null) {
				throw new ServletException("DataLayer non inizializzato");
			}

			if ("GET".equalsIgnoreCase(request.getMethod())) {
				renderEditOperator(request, response, this.dl, operatorId);
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

			editOperator(request, response, this.dl, operatorId);
		} catch (IOException | DataException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderEditOperator(HttpServletRequest request, HttpServletResponse response,
			DataLayer dl, int operatorId) throws IOException, DataException, ServletException {
		Utente utente = ((UtenteDAO) this.dl.getDAO(Utente.class)).getUtente(operatorId);
		if (utente == null || utente.isAdmin()) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Anagrafica anagrafica = ((AnagraficaDAO) this.dl.getDAO(Anagrafica.class)).getAnagraficaByUtente(utente);
		Credenziali credenziali = ((CredenzialiDAO) this.dl.getDAO(Credenziali.class)).getCredenzialiByUtente(utente);
		PatenteDAO patenteDAO = (PatenteDAO) this.dl.getDAO(Patente.class);
		AbilitaDAO abilitaDAO = (AbilitaDAO) this.dl.getDAO(Abilita.class);
		List<Patente> patenti = patenteDAO.getPatentiByUtente(utente);
		List<Abilita> abilita = abilitaDAO.getAbilitaByUtente(utente);
		Map<String, Object> dettaglio = new HashMap<>();
		dettaglio.put("id", utente.getKey());
		dettaglio.put("nome", anagrafica == null ? "" : anagrafica.getNome());
		dettaglio.put("cognome", anagrafica == null ? "" : anagrafica.getCognome());
		dettaglio.put("email", credenziali == null ? "" : credenziali.getEmail());
		dettaglio.put("patenti", patenti.stream().map(patente -> String.valueOf(patente.getKey())).toList());
		dettaglio.put("abilita", abilita.stream().map(abilitaItem -> String.valueOf(abilitaItem.getKey())).toList());

		Map<String, Object> model = new HashMap<>();
		model.put("ctx", request.getContextPath());
		model.put("csrfToken", SecurityHelpers.createCsrfToken(request));
		model.put("dettaglio", dettaglio);
		model.put("patentiDisponibili", licenseOptions(patenteDAO, patenti));
		model.put("abilitaDisponibili", abilitaDAO.getAbilitaList().stream().map(this::abilityOption).toList());

		response.setContentType("text/html;charset=UTF-8");
		try {
			Template template = cfg.getTemplate("edit/operatore-edit.ftl");
			template.process(model, response.getWriter());
		} catch (TemplateException ex) {
			throw new ServletException("Errore durante il rendering del form operatore", ex);
		}
	}

	private void editOperator(HttpServletRequest request, HttpServletResponse response,
			DataLayer dl, int operatorId) throws IOException, DataException, ServletException {
		String nome = SecurityHelpers.sanitizeTextInput(request.getParameter("nome"));
		String cognome = SecurityHelpers.sanitizeTextInput(request.getParameter("cognome"));
		String email = SecurityHelpers.sanitizeTextInput(request.getParameter("email"));
		Set<Integer> requestedPatenti = parseIds(request.getParameterValues("patenti"), "Patente");
		Set<Integer> requestedAbilita = parseIds(request.getParameterValues("abilita"), "Abilita");
		if (nome.isBlank() || cognome.isBlank() || email.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("{\"error\":\"Nome, cognome e email sono obbligatori\"}");
			return;
		}

		Utente utente = ((UtenteDAO) this.dl.getDAO(Utente.class)).getUtente(operatorId);
		if (utente == null || utente.isAdmin()) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write("{\"error\":\"Operatore non trovato\"}");
			return;
		}

		AnagraficaDAO anagraficaDAO = (AnagraficaDAO) this.dl.getDAO(Anagrafica.class);
		Anagrafica anagrafica = anagraficaDAO.getAnagraficaByUtente(utente);
		if (anagrafica == null) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			response.getWriter().write("{\"error\":\"Anagrafica operatore non presente\"}");
			return;
		}
		anagrafica.setNome(nome);
		anagrafica.setCognome(cognome);
		anagraficaDAO.storeAnagrafica(anagrafica, utente);

		CredenzialiDAO credenzialiDAO = (CredenzialiDAO) this.dl.getDAO(Credenziali.class);
		Credenziali credenziali = credenzialiDAO.getCredenzialiByUtente(utente);
		if (credenziali == null) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			response.getWriter().write("{\"error\":\"Credenziali operatore non presenti\"}");
			return;
		}
		credenziali.setEmail(email);
		credenzialiDAO.storeCredenziali(credenziali);

		PatenteDAO patenteDAO = (PatenteDAO) this.dl.getDAO(Patente.class);
		updateLicenses(patenteDAO, utente, requestedPatenti);
		AbilitaDAO abilitaDAO = (AbilitaDAO) this.dl.getDAO(Abilita.class);
		updateAbilities(abilitaDAO, utente, requestedAbilita);

		response.sendRedirect(request.getContextPath()+ "/admin-dashboard?section=operators");
	}

	private void updateLicenses(PatenteDAO dao, Utente operator, Set<Integer> requested) throws DataException {
		Set<Integer> current = dao.getPatentiByUtente(operator).stream()
				.map(Patente::getKey).collect(java.util.stream.Collectors.toSet());
		Set<Integer> valid = new HashSet<>();
		for (Patente patente : dao.getPatenti()) {
			if (current.contains(patente.getKey()) || dao.isPatenteDisponibile(patente.getKey())) {
				valid.add(patente.getKey());
			}
		}
		if (!valid.containsAll(requested)) {
			throw new DataException("Una o più patenti non sono valide o sono già assegnate");
		}
		for (Integer id : requested) {
			if (!current.contains(id)) {
				dao.legaPatenteAUtente(dao.getPatente(id), operator);
			}
		}
		for (Integer id : current) {
			if (!requested.contains(id)) {
				dao.slegaPatenteDaUtente(dao.getPatente(id), operator);
			}
		}
	}

	private void updateAbilities(AbilitaDAO dao, Utente operator, Set<Integer> requested) throws DataException {
		Set<Integer> valid = dao.getAbilitaList().stream()
				.map(Abilita::getKey).collect(java.util.stream.Collectors.toSet());
		if (!valid.containsAll(requested)) {
			throw new DataException("Una o più abilità non sono valide");
		}
		Set<Integer> current = dao.getAbilitaByUtente(operator).stream()
				.map(Abilita::getKey).collect(java.util.stream.Collectors.toSet());
		for (Integer id : requested) {
			if (!current.contains(id)) {
				dao.legaAbilitaAUtente(dao.getAbilita(id), operator);
			}
		}
		for (Integer id : current) {
			if (!requested.contains(id)) {
				dao.slegaAbilitaDaUtente(dao.getAbilita(id), operator);
			}
		}
	}

	private Set<Integer> parseIds(String[] values, String label) throws ServletException {
		Set<Integer> ids = new HashSet<>();
		if (values == null) {
			return ids;
		}
		try {
			for (String value : values) {
				ids.add(Integer.valueOf(value));
			}
		} catch (NumberFormatException ex) {
			throw new ServletException(label + " non valida", ex);
		}
		return ids;
	}

	private Map<String, Object> licenseOption(Patente patente) {
		Map<String, Object> option = new HashMap<>();
		option.put("value", String.valueOf(patente.getKey()));
		option.put("label", patente.getNumero() + " (" + patente.getTipo() + ")");
		return option;
	}

	private List<Map<String, Object>> licenseOptions(PatenteDAO dao, List<Patente> assigned)
			throws DataException {
		Map<Integer, Patente> assignable = new LinkedHashMap<>();
		for (Patente patente : dao.getPatentiDisponibili()) {
			assignable.put(patente.getKey(), patente);
		}
		for (Patente patente : assigned) {
			assignable.putIfAbsent(patente.getKey(), patente);
		}
		return assignable.values().stream().map(this::licenseOption).toList();
	}

	private Map<String, Object> abilityOption(Abilita ability) {
		Map<String, Object> option = new HashMap<>();
		option.put("value", String.valueOf(ability.getKey()));
		option.put("label", ability.getNome());
		return option;
	}

}
