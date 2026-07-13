package org.soccorsoweb.model.impl;

import org.soccorsoweb.data.DataItemImpl;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.Richiesta;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.model.Aggiornamento;
import org.soccorsoweb.model.Commento;
import org.soccorsoweb.model.enums.EsitoMissione;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MissioneImpl extends DataItemImpl<Integer> implements Missione {

    private Richiesta richiesta;
    private Squadra squadra;
    private Utente admin;
    private String obiettivo;
    private LocalDateTime inizio;
    private LocalDateTime fine;
    private boolean completata;
    private EsitoMissione esito;
    private Duration durata;
    private List<Aggiornamento> aggiornamenti;
    private List<Commento> commenti;

    public MissioneImpl() {
        super();
        this.richiesta = null;
        this.squadra = null;
        this.admin = null;
        this.obiettivo = "";
        this.inizio = null;
        this.fine = null;
        this.completata = false;
        this.esito = null;
        this.durata = null;
        this.aggiornamenti = new ArrayList<>();
        this.commenti = new ArrayList<>();
    }

    @Override
    public Richiesta getRichiesta() { return richiesta; }
    @Override
    public void setRichiesta(Richiesta richiesta) { this.richiesta = richiesta; }

    @Override
    public Squadra getSquadra() { return squadra; }
    @Override
    public void setSquadra(Squadra squadra) { this.squadra = squadra; }

    @Override
    public Utente getAdmin() { return admin; }
    @Override
    public void setAdmin(Utente admin) { this.admin = admin; }

    @Override
    public String getObiettivo() { return obiettivo; }
    @Override
    public void setObiettivo(String obiettivo) { this.obiettivo = obiettivo; }

    @Override
    public LocalDateTime getInizio() { return inizio; }
    @Override
    public void setInizio(LocalDateTime inizio) { this.inizio = inizio; }

    @Override
    public LocalDateTime getFine() { return fine; }
    @Override
    public void setFine(LocalDateTime fine) { this.fine = fine; }

    @Override
    public boolean isCompletata() { return completata; }
    @Override
    public void setCompletata(boolean completata) { this.completata = completata; }

    @Override
    public EsitoMissione getEsito() { return esito; }
    @Override
    public void setSuccesso(EsitoMissione successo) { this.esito = successo; }

    @Override
    public Duration getDurata() { return durata; }
    @Override
    public void setDurata(Duration durata) { this.durata = durata; }

    @Override
    public List<Aggiornamento> getAggiornamenti() { return aggiornamenti; }
    @Override
    public void setAggiornamenti(List<Aggiornamento> aggiornamenti) { this.aggiornamenti = aggiornamenti; }
    @Override
    public void addAggiornamento(Aggiornamento aggiornamento) { this.aggiornamenti.add(aggiornamento); }

    @Override
    public List<Commento> getCommenti() { return commenti; }
    @Override
    public void setCommenti(List<Commento> commenti) { this.commenti = commenti; }
    @Override
    public void addCommento(Commento commento) { this.commenti.add(commento); }
}