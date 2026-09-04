package org.soccorsoweb.business.controller.admin.details;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.soccorsoweb.business.controller.SoccorsoBaseController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.AbilitaDAO;
import org.soccorsoweb.data.dao.AnagraficaDAO;
import org.soccorsoweb.data.dao.MissioneDAO;
import org.soccorsoweb.data.dao.PatenteDAO;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Abilita;
import org.soccorsoweb.model.Anagrafica;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.Patente;
import org.soccorsoweb.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class OperatorDetailsController extends SoccorsoBaseController {

	private Configuration cfg;

	@Override
	public void init() throws ServletException {
		super.init();
		cfg = new Configuration(Configuration.VERSION_2_3_34);
		cfg.setServletContextForTemplateLoading(getServletContext(), "/templates");
		cfg.setDefaultEncoding("UTF-8");
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		if (!SecurityHelpers.isAdmin(request)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		Integer operatoreId = parseId(request);
		if (operatoreId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			DataLayer dataLayer = (DataLayer) request.getAttribute("datalayer");
			if (dataLayer == null) {
				throw new ServletException("DataLayer non inizializzato");
			}

			Utente utente = ((UtenteDAO) dataLayer.getDAO(Utente.class)).getUtente(operatoreId);
			if (utente == null || utente.isAdmin()) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			renderOperatorDetails(request, response, dataLayer, utente);
		} catch (DataException | IOException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderOperatorDetails(HttpServletRequest request, HttpServletResponse response,
			DataLayer dataLayer, Utente utente) throws IOException, DataException, ServletException {
		Anagrafica anagrafica = ((AnagraficaDAO) dataLayer.getDAO(Anagrafica.class))
				.getAnagraficaByUtente(utente);
		List<Patente> patenti = ((PatenteDAO) dataLayer.getDAO(Patente.class))
				.getPatentiByUtente(utente);
		List<Abilita> abilita = ((AbilitaDAO) dataLayer.getDAO(Abilita.class))
				.getAbilitaByUtente(utente);

		List<Missione> missioniAttive = ((MissioneDAO) dataLayer.getDAO(Missione.class))
				.getMissioniByUtente(utente);
		List<Missione> storicoMissioni = ((MissioneDAO) dataLayer.getDAO(Missione.class))
				.getStoricoMissioniByUtente(utente);

		Map<String, Object> dettaglio = new HashMap<>();
		dettaglio.put("id", utente.getKey());
		dettaglio.put("nome", anagrafica == null ? "" : anagrafica.getNome());
		dettaglio.put("cognome", anagrafica == null ? "" : anagrafica.getCognome());
		dettaglio.put("email", utente.getEmail());
		dettaglio.put("telefono", "");
		dettaglio.put("stato", missioniAttive.isEmpty() ? "Disponibile" : "In missione");
		dettaglio.put("missione_corrente", missioniAttive.isEmpty() ? "" : missioniAttive.get(0).getKey());

		List<String> patentiView = new ArrayList<>();
		for (Patente patente : patenti) {
			patentiView.add(patente.getNumero() + " (" + patente.getTipo() + ")");
		}
		dettaglio.put("patenti", patentiView);

		List<String> abilitaView = new ArrayList<>();
		for (Abilita abilitaItem : abilita) {
			abilitaView.add(abilitaItem.getNome());
		}
		dettaglio.put("abilita", abilitaView);

		List<Map<String, Object>> storicoView = new ArrayList<>();
		for (Missione missione : storicoMissioni) {
			Map<String, Object> storico = new HashMap<>();
			storico.put("missione_id", missione.getKey());
			storico.put("data", missione.getInizio() == null ? "" : missione.getInizio().toLocalDate());
			storico.put("descrizione", missione.getObiettivo());
			storico.put("esito", missione.getEsito() == null ? "" : missione.getEsito().name());
			storicoView.add(storico);
		}
		dettaglio.put("storico_missioni", storicoView);

		Map<String, Object> model = new HashMap<>();
		model.put("ctx", request.getContextPath());
		model.put("dettaglio", dettaglio);

		response.setContentType("text/html;charset=UTF-8");
		try {
			Template template = cfg.getTemplate("details/operatore-detail.ftl");
			template.process(model, response.getWriter());
		} catch (TemplateException ex) {
			throw new ServletException("Errore durante il rendering del dettaglio operatore", ex);
		}
	}

	private Integer parseId(HttpServletRequest request) {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		try {
			return Integer.valueOf(path.substring(path.lastIndexOf('/') + 1));
		} catch (NumberFormatException ex) {
			return null;
		}
	}

}
