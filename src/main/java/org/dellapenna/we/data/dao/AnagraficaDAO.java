package org.dellapenna.we.data.dao;

import org.dellapenna.we.model.Anagrafica;
import org.dellapenna.we.model.Utente;
import org.dellapenna.we.data.DataException;

public interface AnagraficaDAO {
    Anagrafica createAnagrafica();

    Anagrafica getAnagrafica(int anagrafica_key) throws DataException;

    Anagrafica getAnagraficaByUtente(Utente utente) throws DataException;

    void storeAnagrafica(Anagrafica anagrafica, Utente utente) throws DataException;
}
