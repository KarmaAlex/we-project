package org.soccorsoweb.data.dao;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.Patente;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.data.GenericDao;
import java.util.List;

public interface PatenteDAO extends GenericDao{
    
    Patente createPatente();
    
    Patente getPatente(int patente_key) throws DataException;
    
    List<Patente> getPatenti() throws DataException;
    
    List<Patente> getPatentiByUtente(Utente utente) throws DataException;
    
    void storePatente(Patente patente) throws DataException;
    
    void legaPatenteAUtente(Patente patente, Utente utente) throws DataException;
    
    void slegaPatenteDaUtente(Patente patente, Utente utente) throws DataException;
}