package org.soccorsoweb.data.dao;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.GenericDao;
import org.soccorsoweb.model.Commento;
import org.soccorsoweb.model.Missione;

import java.util.List;

public interface CommentoDAO extends GenericDao {

    Commento createCommento();

    Commento getCommento(int commento_key) throws DataException;

    List<Commento> getCommentiByMissione(Missione missione) throws DataException;

    // explicitly pass the key 
    void storeCommento(Commento commento, int missione_key) throws DataException;
}