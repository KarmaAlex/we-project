package org.soccorsoweb.model.impl.proxy;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataItemProxy;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.model.impl.CommentoImpl;

public class CommentoProxy extends CommentoImpl implements DataItemProxy {

    private boolean modified;
    private final DataLayer dataLayer;
    private int adminKey;

    public CommentoProxy(DataLayer d) {
        super();
        this.dataLayer = d;
        this.modified = false;
        this.adminKey = 0;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public void setModified(boolean dirty) {
        this.modified = dirty;
    }

    @Override
    public void setTesto(String testo) {
        super.setTesto(testo);
        this.modified = true;
    }

    @Override
    public Utente getAdmin() {
        if (super.getAdmin() == null && this.adminKey > 0) {
            try {
                UtenteDAO utenteDAO = (UtenteDAO) dataLayer.getDAO(Utente.class);
                super.setAdmin(utenteDAO.getUtente(this.adminKey));
            } catch (DataException ex) {
                ex.printStackTrace();
            }
        }
        return super.getAdmin();
    }

    @Override
    public void setAdmin(Utente admin) {
        super.setAdmin(admin);
        this.adminKey = (admin != null) ? admin.getKey() : 0;
        this.modified = true;
    }

    public int getAdminKey() {
        return adminKey;
    }

    public void setAdminKey(int adminKey) {
        this.adminKey = adminKey;
        if (adminKey == 0) {
            super.setAdmin(null);
        }
    }
}