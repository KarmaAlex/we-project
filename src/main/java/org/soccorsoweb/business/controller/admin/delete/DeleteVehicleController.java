package org.soccorsoweb.business.controller.admin.delete;

import java.io.IOException;
import org.soccorsoweb.business.controller.SoccorsoBaseController;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.MezzoDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.Mezzo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DeleteVehicleController extends SoccorsoBaseController {

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!SecurityHelpers.isAdmin(request)) {
			response.sendRedirect(request.getContextPath() + "/login");
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

		Integer mezzoId = parseId(request);
		if (mezzoId == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			DataLayer dataLayer = (DataLayer) request.getAttribute("datalayer");
			if (dataLayer == null) {
				throw new ServletException("DataLayer non inizializzato");
			}
			MezzoDAO mezzoDAO = (MezzoDAO) dataLayer.getDAO(Mezzo.class);
			if (mezzoDAO.getMezzo(mezzoId) == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			mezzoDAO.deleteMezzo(mezzoId);
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write("{\"success\":true,\"message\":\"Mezzo eliminato con successo\"}");
		} catch (DataException ex) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write("{\"error\":\"Mezzo non eliminabile perché associato a una missione\"}");
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
