package org.dellapenna.we.data.dao;

import org.dellapenna.we.data.DataException;
import org.dellapenna.we.model.Credenziali;
import org.dellapenna.we.model.Utente;

public interface CredenzialiDAO {

    Credenziali createCredenziali();

    Credenziali getCredenziali(int credenziali_key) throws DataException;

    Credenziali getCredenzialiByUtente(Utente utente) throws DataException;

    void storeCredenziali(Credenziali credenziali) throws DataException;

    void legaCredenzialiAUtente(Credenziali credenziali, Utente utente) throws DataException;
}