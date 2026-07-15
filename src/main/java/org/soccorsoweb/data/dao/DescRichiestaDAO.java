package org.soccorsoweb.data.dao;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.DescRichiesta;
import org.soccorsoweb.model.Richiesta;
import org.soccorsoweb.data.GenericDao;

public interface DescRichiestaDAO extends GenericDao{
    
    DescRichiesta createDescRichiesta();
    
    DescRichiesta getDescRichiesta(int descRichiesta_key) throws DataException;
    
    DescRichiesta getDescRichiestaByRichiesta(Richiesta richiesta) throws DataException;
    
    void storeDescRichiesta(DescRichiesta descRichiesta, Richiesta richiesta) throws DataException;
}