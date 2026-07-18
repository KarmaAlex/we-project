package org.soccorsoweb.data.dao;

import java.time.LocalDateTime;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.GenericDao;
import java.util.List;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.enums.EsitoMissione;

public interface UtenteDAO extends GenericDao{
    Utente createUtente();
    
    Utente getUtente(int utente_key) throws DataException;
    
    Utente getUtenteByUsername(String username) throws DataException;
    
    List<Utente> getUtenti() throws DataException; //qui tutti gli operatori
    
    List<Utente> getUtentiDisponibili() throws DataException;//qui solo quelli che non sono in missione
    
    void storeUtente(Utente utente) throws DataException;
}
