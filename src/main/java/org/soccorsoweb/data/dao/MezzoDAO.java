package org.soccorsoweb.data.dao;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.Mezzo;
import org.soccorsoweb.data.GenericDao;
import java.util.List;

public interface MezzoDAO extends GenericDao{

    Mezzo createMezzo();

    Mezzo getMezzo(int mezzo_key) throws DataException;

    List<Mezzo> getMezzi() throws DataException;

    void storeMezzo(Mezzo mezzo) throws DataException;
}