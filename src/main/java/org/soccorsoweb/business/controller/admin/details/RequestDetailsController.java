package org.soccorsoweb.business.controller.admin.details;


import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.soccorsoweb.business.controller.AbstractIdRequiredController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.DescRichiestaDAO;
import org.soccorsoweb.data.dao.RichiestaDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.DescRichiesta;
import org.soccorsoweb.model.Richiesta;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestDetailsController extends AbstractIdRequiredController {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		Logger.getLogger(RequestDetailsController.class.getName()).info("Requesting info for request id:");
		if (!SecurityHelpers.isAdmin(request)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		Integer richiestaId = getRequestId(request);
		if (richiestaId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		Logger.getLogger(RequestDetailsController.class.getName()).info("Requesting info for request id: "+richiestaId);
		try {
			
			if (this.dl == null) {
				throw new ServletException("DataLayer non inizializzato");
			}
			Logger.getLogger(RequestDetailsController.class.getName()).info("Requesting info for request id: "+richiestaId);
			Richiesta richiesta = ((RichiestaDAO) this.dl.getDAO(Richiesta.class)).getRichiesta(richiestaId);
			if (richiesta == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			renderRichiestaDetails(request, response, this.dl, richiesta);
		} catch (DataException | IOException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderRichiestaDetails(HttpServletRequest request, HttpServletResponse response,
			DataLayer dl, Richiesta richiesta) throws IOException, DataException, ServletException {

		DescRichiesta descRichiesta = ((DescRichiestaDAO) this.dl.getDAO(DescRichiesta.class))
				.getDescRichiestaByRichiesta(richiesta);

		Map<String, Object> dettaglio = new HashMap<>();
		dettaglio.put("id", richiesta.getKey());
		dettaglio.put("segnalante", richiesta.getNome());
		dettaglio.put("email", richiesta.getEmail());
		dettaglio.put("indirizzo", descRichiesta == null ? "" : descRichiesta.getPosizione());
		dettaglio.put("posizione", descRichiesta == null ? "" : descRichiesta.getPosizione());
		dettaglio.put("stato", richiesta.getStato() == null ? "" : richiesta.getStato().name());
		dettaglio.put("data_creazione", richiesta.getData() == null ? "" : richiesta.getData());
		dettaglio.put("verificato", richiesta.isVerificato());
		dettaglio.put("descrizione", descRichiesta == null ? "" : descRichiesta.getDescrizione());
		if (descRichiesta != null && descRichiesta.getFoto() != null && !descRichiesta.getFoto().isBlank()) {
			dettaglio.put("foto_url", descRichiesta.getFoto());
		}

		Map<String, Object> model = new HashMap<>();
		model.put("ctx", request.getContextPath());
		model.put("dettaglio", dettaglio);

		response.setContentType("text/html;charset=UTF-8");
		try {
			Template template = cfg.getTemplate("details/richiesta-detail.ftl");
			template.process(model, response.getWriter());
		} catch (TemplateException ex) {
			throw new ServletException("Errore durante il rendering del dettaglio richiesta", ex);
		}
	}
}
