package org.soccorsoweb.business.controller;

import jakarta.servlet.http.HttpServletRequest;

public abstract class AbstractIdRequiredController extends SoccorsoBaseController {
    
    protected Integer getRequestId(HttpServletRequest request){
        return Integer.parseInt(request.getParameter("id"));
    }
}
