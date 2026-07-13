package org.soccorsoweb.data.dao;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.Squadra;
import java.util.List;

public interface MissioneDAO {
    
    Missione createMissione();
    
    Missione getMissione(int missione_key) throws DataException;
    
    List<Missione> getMissioniBySquadra(Squadra squadra) throws DataException;
    
    void storeMissione(Missione missione) throws DataException;
    
    void destroy() throws DataException;
}