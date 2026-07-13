package org.soccorsoweb.model.impl;

import org.soccorsoweb.data.DataItemImpl;
import org.soccorsoweb.model.Aggiornamento;
import org.soccorsoweb.model.Utente;
import java.time.LocalDateTime;

public class AggiornamentoImpl extends DataItemImpl<Integer> implements Aggiornamento {

    private String testo;
    private LocalDateTime timestamp;
    private Utente admin;

    public AggiornamentoImpl() {
        super();
        this.testo = "";
        this.timestamp = null;
        this.admin = null;
    }

    @Override
    public Utente getAdmin() {
        return this.admin;
    }

    @Override
    public void setAdmin(Utente admin) {
        this.admin = admin;
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
}