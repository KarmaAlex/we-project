package org.dellapenna.we.data.dao;

import org.dellapenna.we.data.DataException;
import org.dellapenna.we.model.Patente;
import org.dellapenna.we.model.Utente;
import java.util.List;

public interface PatenteDAO {
    
    Patente createPatente();
    
    Patente getPatente(int patente_key) throws DataException;
    
    List<Patente> getPatenti() throws DataException;
    
    List<Patente> getPatentiByUtente(Utente utente) throws DataException;
    
    void storePatente(Patente patente) throws DataException;
    
    void legaPatenteAUtente(Patente patente, Utente utente) throws DataException;
    
    void slegaPatenteDaUtente(Patente patente, Utente utente) throws DataException;
}