package org.soccorsoweb.model.impl.proxy;

import org.soccorsoweb.data.DataItemImpl;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.dao.RichiestaDAO;
import org.soccorsoweb.data.dao.SquadraDAO;
import org.soccorsoweb.data.dao.UtenteDAO;
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

public class MissioneProxy extends DataItemImpl<Integer> implements Missione {

    private String obiettivo;
    private LocalDateTime inizio;
    private LocalDateTime fine;
    private boolean completata;
    private EsitoMissione esito;
    private Duration durata;

    private int richiesta_key;
    private Richiesta richiesta;
    private int squadra_key;
    private Squadra squadra;
    private int admin_key;
    private Utente admin;

    private List<Aggiornamento> aggiornamenti;
    private List<Commento> commenti;

    protected DataLayer dataLayer;

    public MissioneProxy(DataLayer dlayer) {
        super();
        this.dataLayer = dlayer;
        this.obiettivo = "";
        this.inizio = null;
        this.fine = null;
        this.completata = false;
        this.esito = null;
        this.durata = null;
        this.richiesta_key = 0;
        this.richiesta = null;
        this.squadra_key = 0;
        this.squadra = null;
        this.admin_key = 0;
        this.admin = null;
        this.aggiornamenti = null;
        this.commenti = null;
    }

    @Override
    public Richiesta getRichiesta() {
        if (this.richiesta == null && this.richiesta_key > 0) {
            try {
                this.richiesta = ((RichiestaDAO) dataLayer.getDAO(Richiesta.class)).getRichiesta(this.richiesta_key);
            } catch (DataException e) { e.printStackTrace(); }
        }
        return this.richiesta;
    }

    @Override
    public void setRichiesta(Richiesta richiesta) {
        this.richiesta = richiesta;
        this.richiesta_key = (richiesta != null) ? richiesta.getKey() : 0;
    }

    @Override
    public Squadra getSquadra() {
        if (this.squadra == null && this.squadra_key > 0) {
            try {
                this.squadra = ((SquadraDAO) dataLayer.getDAO(Squadra.class)).getSquadra(this.squadra_key);
            } catch (DataException e) { e.printStackTrace(); }
        }
        return this.squadra;
    }

    @Override
    public void setSquadra(Squadra squadra) {
        this.squadra = squadra;
        this.squadra_key = (squadra != null) ? squadra.getKey() : 0;
    }

    @Override
    public Utente getAdmin() {
        if (this.admin == null && this.admin_key > 0) {
            try {
                this.admin = ((UtenteDAO) dataLayer.getDAO(Utente.class)).getUtente(this.admin_key);
            } catch (DataException e) { e.printStackTrace(); }
        }
        return this.admin;
    }

    @Override
    public void setAdmin(Utente admin) {
        this.admin = admin;
        this.admin_key = (admin != null) ? admin.getKey() : 0;
    }

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
    public List<Aggiornamento> getAggiornamenti() {
        if (this.aggiornamenti == null) {
            // Verrà popolata via DAO se necessario, o lasciata lazy caricando gli aggiornamenti associati a questa missione
            this.aggiornamenti = new ArrayList<>(); 
        }
        return this.aggiornamenti;
    }

    @Override
    public void setAggiornamenti(List<Aggiornamento> aggiornamenti) { this.aggiornamenti = aggiornamenti; }
    @Override
    public void addAggiornamento(Aggiornamento aggiornamento) { getAggiornamenti().add(aggiornamento); }

    @Override
    public List<Commento> getCommenti() {
        if (this.commenti == null) { this.commenti = new ArrayList<>(); }
        return this.commenti;
    }

    @Override
    public void setCommenti(List<Commento> commenti) { this.commenti = commenti; }
    @Override
    public void addCommento(Commento commento) { getCommenti().add(commento); }

    public int getRichiestaKey() { return richiesta_key; }
    public void setRichiestaKey(int richiesta_key) { this.richiesta_key = richiesta_key; this.richiesta = null; }

    public int getSquadraKey() { return squadra_key; }
    public void setSquadraKey(int squadra_key) { this.squadra_key = squadra_key; this.squadra = null; }

    public int getAdminKey() { return admin_key; }
    public void setAdminKey(int admin_key) { this.admin_key = admin_key; this.admin = null; }
}