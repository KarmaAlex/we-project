package org.soccorsoweb.data.dao;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.Mezzo;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.data.GenericDao;
import java.util.List;

public interface MezzoDAO extends GenericDao {
    Mezzo createMezzo();
    Mezzo getMezzo(int mezzo_key) throws DataException;
    List<Mezzo> getMezzi() throws DataException;
    void storeMezzo(Mezzo mezzo) throws DataException;
    void deleteMezzo(int mezzo_key) throws DataException;
    List<Mezzo> getMezziDisponibili() throws DataException;
    
    // Metodo aggiunto per la gestione dell'associazione
    void assegnaMezzoAMissione(Mezzo m, Missione mis) throws DataException;
    
    List<Mezzo> getMezziConStato() throws DataException;
}