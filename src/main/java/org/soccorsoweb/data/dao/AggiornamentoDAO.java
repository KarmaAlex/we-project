package org.soccorsoweb.data.dao;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.Aggiornamento;
import org.soccorsoweb.model.Utente;
import java.util.List;

public interface AggiornamentoDAO {
    
    Aggiornamento createAggiornamento();
    
    Aggiornamento getAggiornamento(int update_key) throws DataException;
    
    List<Aggiornamento> getAggiornamentiByAdmin(Utente admin) throws DataException;
    
    void storeAggiornamento(Aggiornamento aggiornamento) throws DataException;
    
    void destroy() throws DataException;
}