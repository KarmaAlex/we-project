package org.soccorsoweb.data.dao;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.Credenziali;
import org.soccorsoweb.data.GenericDao;
import org.soccorsoweb.model.Utente;

public interface CredenzialiDAO extends GenericDao{

    Credenziali createCredenziali();

    Credenziali getCredenziali(int credenziali_key) throws DataException;

    Credenziali getCredenzialiByUtente(Utente utente) throws DataException;

    void storeCredenziali(Credenziali credenziali) throws DataException;

    void legaCredenzialiAUtente(Credenziali credenziali, Utente utente) throws DataException;
}