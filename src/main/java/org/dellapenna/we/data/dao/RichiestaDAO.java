package org.dellapenna.we.data.dao;

import org.dellapenna.we.data.DataException;
import org.dellapenna.we.model.Richiesta;
import java.util.List;

public interface RichiestaDAO {
    
    Richiesta createRichiesta();
    
    Richiesta getRichiesta(int richiesta_key) throws DataException;
    
    List<Richiesta> getRichieste() throws DataException;
    
    void storeRichiesta(Richiesta richiesta) throws DataException;
}