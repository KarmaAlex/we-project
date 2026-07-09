package org.dellapenna.we.data.dao;

import org.dellapenna.we.data.DataException;
import org.dellapenna.we.model.Mezzo;
import java.util.List;

public interface MezzoDAO {

    Mezzo createMezzo();

    Mezzo getMezzo(int mezzo_key) throws DataException;

    List<Mezzo> getMezzi() throws DataException;

    void storeMezzo(Mezzo mezzo) throws DataException;
}