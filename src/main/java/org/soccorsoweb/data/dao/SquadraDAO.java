package org.soccorsoweb.data.dao;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.model.Utente;
import java.util.List;

public interface SquadraDAO {

    Squadra createSquadra();

    Squadra getSquadra(int squadra_key) throws DataException;

    void storeSquadra(Squadra squadra) throws DataException;

    void aggiungiMembroASquadra(Squadra squadra, Utente utente) throws DataException;

    void rimuoviMembroDaSquadra(Squadra squadra, Utente utente) throws DataException;

    List<Utente> getOperatoriSquadra(int squadra_key) throws DataException;
}