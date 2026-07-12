package org.soccorsoweb.data.dao;

import org.soccorsoweb.model.Utente;
import org.soccorsoweb.data.DataException;
import java.util.List;

public interface UtenteDAO {
    Utente createUtente();
    
    Utente getUtente(int utente_key) throws DataException;
    
    Utente getUtenteByUsername(String username) throws DataException;
    
    List<Utente> getUtenti() throws DataException;
    
    void storeUtente(Utente utente) throws DataException;
}
