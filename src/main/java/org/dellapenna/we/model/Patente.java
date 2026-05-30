package org.dellapenna.we.model;

import org.dellapenna.we.data.DataItem;
import org.dellapenna.we.model.enums.TipoPatente;

public interface Patente extends DataItem<Integer>{
    
    String getNumero();
    void setNumero(String numero);

    TipoPatente getTipo();
    void setTipo(TipoPatente tipo);
}
