package org.soccorsoweb.data.dao;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.data.GenericDao;
import java.util.List;

public interface SquadraDAO extends GenericDao{

    Squadra createSquadra();

    Squadra getSquadra(int squadra_key) throws DataException;

    List<Squadra> getSquadre() throws DataException;

    void storeSquadra(Squadra squadra) throws DataException;

    void aggiungiMembroASquadra(Squadra squadra, Utente utente) throws DataException;

    void rimuoviMembroDaSquadra(Squadra squadra, Utente utente) throws DataException;

    List<Utente> getOperatoriSquadra(int squadra_key) throws DataException;
    
    List<Squadra> getSquadreDisponibili() throws DataException;
}