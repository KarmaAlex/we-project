package org.soccorsoweb.model.impl;

import org.soccorsoweb.data.DataItemImpl;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.model.Utente;

import java.util.ArrayList;
import java.util.List;

public class SquadraImpl extends DataItemImpl<Integer> implements Squadra {

    private Utente capo;
    private List<Utente> operatori;
    private long version;

    public SquadraImpl() {
        super();
        this.capo = null;
        this.operatori = new ArrayList<>();
        this.version = 0L;
    }

    @Override
    public Utente getCapoSquadra() {
        return capo;
    }

    @Override
    public void setCapoSquadra(Utente capo) {
        this.capo = capo;
    }

    @Override
    public List<Utente> getOperatori() {
        return operatori;
    }

    @Override
    public void setOperatori(List<Utente> operatori) {
        this.operatori = operatori;
    }

    @Override
    public void addOperatore(Utente operatore) {
        if (!this.operatori.contains(operatore)) {
            this.operatori.add(operatore);
        }
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}