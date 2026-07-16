package org.soccorsoweb.model;

import org.soccorsoweb.data.DataItem;
import java.util.List;

public interface Squadra extends DataItem<Integer>{
    Utente getCapoSquadra();
    void setCapoSquadra(Utente capo);

    // Here will be mapped the team members from the assegna_squadra table.
    List<Utente> getOperatori();
    void setOperatori(List<Utente> operatori);
    void addOperatore(Utente operatore);

    Missione getMissione();
}
