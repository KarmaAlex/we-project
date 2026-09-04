package org.soccorsoweb.business.controller.admin.details;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.soccorsoweb.business.controller.SoccorsoBaseController;
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

public class RequestDetailsController extends SoccorsoBaseController {
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

		Integer richiestaId = parseId(request);
		if (richiestaId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			DataLayer dataLayer = (DataLayer) request.getAttribute("datalayer");
			if (dataLayer == null) {
				throw new ServletException("DataLayer non inizializzato");
			}

			Richiesta richiesta = ((RichiestaDAO) dataLayer.getDAO(Richiesta.class)).getRichiesta(richiestaId);
			if (richiesta == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			renderRichiestaDetails(request, response, dataLayer, richiesta);
		} catch (DataException | IOException ex) {
			handleError(ex, request, response);
		}
	}

	private void renderRichiestaDetails(HttpServletRequest request, HttpServletResponse response,
			DataLayer dataLayer, Richiesta richiesta) throws IOException, DataException, ServletException {

		DescRichiesta descRichiesta = ((DescRichiestaDAO) dataLayer.getDAO(DescRichiesta.class))
				.getDescRichiestaByRichiesta(richiesta);

		Map<String, Object> dettaglio = new HashMap<>();
		dettaglio.put("id", richiesta.getKey());
		dettaglio.put("segnalante", richiesta.getNome());
		dettaglio.put("email", richiesta.getEmail());
		dettaglio.put("indirizzo", descRichiesta == null ? "" : descRichiesta.getPosizione());
		dettaglio.put("coordinate", richiesta.getString());
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

	private Integer parseId(HttpServletRequest request) {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		try {
			return Integer.valueOf(path.substring(path.lastIndexOf('/') + 1));
		} catch (NumberFormatException ex) {
			return null;
		}
	}
}
