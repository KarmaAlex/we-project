package org.soccorsoweb.data.dao;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.Materiale;
import org.soccorsoweb.model.Missione; // Import necessario
import org.soccorsoweb.data.GenericDao;
import java.util.List;

public interface MaterialeDAO extends GenericDao {
    Materiale createMateriale();
    Materiale getMateriale(int materiale_key) throws DataException;
    List<Materiale> getMateriali() throws DataException;
    void storeMateriale(Materiale materiale) throws DataException;
    List<Materiale> getMaterialiDisponibili() throws DataException;
    
    // Metodo aggiunto per la gestione dell'associazione
    void assegnaMaterialeAMissione(Materiale mat, Missione mis) throws DataException;
}