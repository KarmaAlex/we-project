
package org.soccorsoweb.business.controller;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.framework.controller.AbstractBaseController;

/**
 *
 * @author Aurora
 */
public abstract class SoccorsoBaseController extends AbstractBaseController {
    @Override
    protected DataLayer createDataLayer(DataSource ds) throws ServletException {
        try {
            return new NewspaperDataLayer(ds);
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

}
}
