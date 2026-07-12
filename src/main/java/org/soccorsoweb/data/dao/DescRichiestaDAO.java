package org.soccorsoweb.data.dao;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.DescRichiesta;
import org.soccorsoweb.model.Richiesta;

public interface DescRichiestaDAO {
    
    DescRichiesta createDescRichiesta();
    
    DescRichiesta getDescRichiesta(int descRichiesta_key) throws DataException;
    
    DescRichiesta getDescRichiestaByRichiesta(Richiesta richiesta) throws DataException;
    
    void storeDescRichiesta(DescRichiesta descRichiesta, Richiesta richiesta) throws DataException;
}