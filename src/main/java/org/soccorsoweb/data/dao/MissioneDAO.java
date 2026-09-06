package org.soccorsoweb.data.dao;

import java.time.LocalDateTime;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.data.GenericDao;
import java.util.List;
import org.soccorsoweb.model.Materiale;
import org.soccorsoweb.model.Mezzo;
import org.soccorsoweb.model.Utente;

public interface MissioneDAO extends GenericDao{
    
    Missione createMissione();
    
    Missione getMissione(int missione_key) throws DataException;
    
    List<Missione> getMissioniBySquadra(Squadra squadra) throws DataException;
    
    void storeMissione(Missione missione) throws DataException;
    
    List<Missione> getMissioniByUtente(Utente utente) throws DataException; //lista delle missioni associate ad un operatore
    
    List<Missione> getMissioniFiltrate(LocalDateTime inizio, LocalDateTime fine, Boolean completata, Integer successo) throws DataException; //missioni filtrate per data, completamento e successo
    
    List<Missione> getStoricoMissioniByUtente(Utente utente) throws DataException;
    
    List<Missione> getStoricoMissioniByMezzo(Mezzo mezzo) throws DataException;
    
    List<Missione> getStoricoMissioniByMateriale(Materiale materiale) throws DataException;
    
}