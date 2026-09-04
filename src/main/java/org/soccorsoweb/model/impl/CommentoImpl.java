package org.soccorsoweb.model.impl;

import org.soccorsoweb.data.DataItemImpl;
import org.soccorsoweb.model.Commento;
import org.soccorsoweb.model.Utente;

public class CommentoImpl extends DataItemImpl<Integer> implements Commento {

    private String testo;
    private Utente admin;

    public CommentoImpl() {
        super();
        this.testo = "";
        this.admin = null;
    }

    @Override
    public String getTesto() {
        return testo;
    }

    @Override
    public void setTesto(String testo) {
        this.testo = testo;
    }

    @Override
    public Utente getAdmin() {
        return admin;
    }

    @Override
    public void setAdmin(Utente admin) {
        this.admin = admin;
    }
}