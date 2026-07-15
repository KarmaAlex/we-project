package org.soccorsoweb.data.dao;

import org.soccorsoweb.model.Anagrafica;
import org.soccorsoweb.data.GenericDao;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.data.DataException;

public interface AnagraficaDAO extends GenericDao{
    Anagrafica createAnagrafica();

    Anagrafica getAnagrafica(int anagrafica_key) throws DataException;

    Anagrafica getAnagraficaByUtente(Utente utente) throws DataException;

    void storeAnagrafica(Anagrafica anagrafica, Utente utente) throws DataException;
}
