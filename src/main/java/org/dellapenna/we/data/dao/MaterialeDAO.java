package org.dellapenna.we.data.dao;

import org.dellapenna.we.data.DataException;
import org.dellapenna.we.model.Materiale;
import java.util.List;

public interface MaterialeDAO {

    Materiale createMateriale();

    Materiale getMateriale(int materiale_key) throws DataException;

    List<Materiale> getMateriali() throws DataException;

    void storeMateriale(Materiale materiale) throws DataException;
}