package org.dellapenna.we.model;

import org.dellapenna.we.data.DataItem;
import org.dellapenna.we.model.enums.EsitoMissione;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface Missione extends DataItem<Integer>{
    
    Richiesta getRichiesta();
    void setRichiesta(Richiesta richiesta);

    Squadra getSquadra();
    void setSquadra(Squadra squadra);

    Utente getAdmin();
    void setAdmin(Utente admin);

    String getObiettivo();
    void setObiettivo(String obiettivo);

    LocalDateTime getInizio();
    void setInizio(LocalDateTime inizio);

    LocalDateTime getFine();
    void setFine(LocalDateTime fine);

    boolean isCompletata();
    void setCompletata(boolean completata);

    EsitoMissione getEsito();
    void setSuccesso(EsitoMissione successo);

    Duration getDurata(); 
    void setDurata(Duration durata);

    List<Aggiornamento> getAggiornamenti();
    void setAggiornamenti(List<Aggiornamento> aggiornamenti);
    void addAggiornamento(Aggiornamento aggiornamento);

    List<Commento> getCommenti();
    void setCommenti(List<Commento> commenti);
    void addCommento(Commento commento);
}
