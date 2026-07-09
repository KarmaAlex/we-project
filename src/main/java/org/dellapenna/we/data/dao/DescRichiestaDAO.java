package org.dellapenna.we.data.dao;

import org.dellapenna.we.data.DataException;
import org.dellapenna.we.model.DescRichiesta;
import org.dellapenna.we.model.Richiesta;

public interface DescRichiestaDAO {
    
    DescRichiesta createDescRichiesta();
    
    DescRichiesta getDescRichiesta(int descRichiesta_key) throws DataException;
    
    DescRichiesta getDescRichiestaByRichiesta(Richiesta richiesta) throws DataException;
    
    void storeDescRichiesta(DescRichiesta descRichiesta, Richiesta richiesta) throws DataException;
}