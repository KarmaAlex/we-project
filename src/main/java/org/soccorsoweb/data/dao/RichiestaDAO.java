package org.soccorsoweb.data.dao;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.Richiesta;
import org.soccorsoweb.data.GenericDao;
import java.time.LocalDateTime;
import org.soccorsoweb.model.enums.StatoRichiesta;
import java.util.List;

public interface RichiestaDAO extends GenericDao{
    
    Richiesta createRichiesta();
    
    Richiesta getRichiesta(int richiesta_key) throws DataException;
    
    List<Richiesta> getRichieste() throws DataException;
    
    void storeRichiesta(Richiesta richiesta) throws DataException;
    
    List<Richiesta> getRichiesteFiltrate(LocalDateTime dataInizio, LocalDateTime dataFine, StatoRichiesta stato, String email) throws DataException;
}