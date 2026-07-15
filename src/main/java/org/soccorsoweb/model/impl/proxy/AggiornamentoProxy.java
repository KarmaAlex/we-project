package org.soccorsoweb.model.impl.proxy;

import org.soccorsoweb.data.DataItemImpl;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.model.Aggiornamento;
import org.soccorsoweb.model.Utente;
import java.time.LocalDateTime;

public class AggiornamentoProxy extends DataItemImpl<Integer> implements Aggiornamento {

    private String testo;
    private LocalDateTime timestamp;
    private int admin_key;
    private Utente admin;
    
    protected DataLayer dataLayer;

    public AggiornamentoProxy(DataLayer dlayer) {
        super();
        this.dataLayer = dlayer;
        this.testo = "";
        this.timestamp = null;
        this.admin_key = 0;
        this.admin = null;
    }

    @Override
    public Utente getAdmin() {
        if (this.admin == null && this.admin_key > 0) {
            try {
                this.admin = ((UtenteDAO) dataLayer.getDAO(Utente.class)).getUtente(this.admin_key);
            } catch (DataException e) {
                e.printStackTrace();
            }
        }
        return this.admin;
    }

    @Override
    public void setAdmin(Utente admin) {
        this.admin = admin;
        this.admin_key = (admin != null) ? admin.getKey() : 0;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    @Override
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getTesto() {
        return this.testo;
    }

    @Override
    public void setTesto(String testo) {
        this.testo = testo;
    }

    public int getAdminKey() {
        return this.admin_key;
    }

    public void setAdminKey(int admin_key) {
        this.admin_key = admin_key;
        this.admin = null; 
    }
}