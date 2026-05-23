/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.dellapenna.we.model;
import org.dellapenna.we.data.DataItem;
import java.util.List;
import java.time.LocalTime;
import java.util.Date;


/**
 *
 * @author Mattia Nanni
 */
public interface Missione extends DataItem<Integer>{
    Richiesta getRichiesta();
    void setRichiesta(Richiesta richiesta);

    Squadra getSquadra();
    void setSquadra(Squadra squadra);

    Utente getAdmin();
    void setAdmin(Utente admin);

    String getObiettivo();
    void setObiettivo(String obiettivo);

    Date getInizio();
    void setInizio(Date inizio);

    Date getFine();
    void setFine(Date fine);

    boolean isCompletata();
    void setCompletata(boolean completata);

    int getSuccesso();
    void setSuccesso(int successo);

    LocalTime getDurata(); 
    void setDurata(LocalTime durata);

    List<Aggiornamento> getAggiornamenti();
    void setAggiornamenti(List<Aggiornamento> aggiornamenti);
    void addAggiornamento(Aggiornamento aggiornamento);

    List<Commento> getCommenti();
    void setCommenti(List<Commento> commenti);
    void addCommento(Commento commento);
}
