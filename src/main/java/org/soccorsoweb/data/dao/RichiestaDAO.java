package org.soccorsoweb.data.dao;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.Richiesta;
import org.soccorsoweb.data.GenericDao;
import java.util.List;

public interface RichiestaDAO extends GenericDao{
    
    Richiesta createRichiesta();
    
    Richiesta getRichiesta(int richiesta_key) throws DataException;
    
    List<Richiesta> getRichieste() throws DataException;
    
    void storeRichiesta(Richiesta richiesta) throws DataException;
}