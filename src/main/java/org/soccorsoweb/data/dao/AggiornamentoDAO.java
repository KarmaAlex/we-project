package org.soccorsoweb.data.dao;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.Aggiornamento;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.data.GenericDao;
import java.util.List;

public interface AggiornamentoDAO extends GenericDao{
    
    Aggiornamento createAggiornamento();
    
    Aggiornamento getAggiornamento(int update_key) throws DataException;
    
    List<Aggiornamento> getAggiornamenti(Utente admin) throws DataException;

    List<Aggiornamento> getAggiornamentiByMissione(Missione missione) throws DataException;
    
   void storeAggiornamento(Aggiornamento aggiornamento) throws DataException;
    
    void storeAggiornamento(Aggiornamento aggiornamento, int missione_key) throws DataException;
}