package org.soccorsoweb.business.controller;
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.soccorsoweb.framework.security.SecurityHelpers;
/**
 *
 * @author Aurora
 */
public class LogoutController extends SoccorsoBaseController {

    private void action_logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SecurityHelpers.disposeSession(request);
        //se è stato trasmesso un URL di origine, torniamo a quell'indirizzo
        if (request.getParameter("referrer") != null) {
            response.sendRedirect(request.getParameter("referrer"));
        } else {
            String home = request.getContextPath() + "/home";
            response.sendRedirect(home);
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws jakarta.servlet.ServletException
     */
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        try {
            action_logout(request, response);
        } catch (IOException ex) {
            handleError(ex, request, response);
        }
    }    
}

