package org.soccorsoweb.data.dao;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.Materiale;
import java.util.List;

public interface MaterialeDAO {

    Materiale createMateriale();

    Materiale getMateriale(int materiale_key) throws DataException;

    List<Materiale> getMateriali() throws DataException;

    void storeMateriale(Materiale materiale) throws DataException;
}