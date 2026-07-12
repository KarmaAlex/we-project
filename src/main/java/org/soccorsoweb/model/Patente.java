package org.soccorsoweb.model;

import org.soccorsoweb.data.DataItem;
import org.soccorsoweb.model.enums.TipoPatente;

public interface Patente extends DataItem<Integer>{
    
    String getNumero();
    void setNumero(String numero);

    TipoPatente getTipo();
    void setTipo(TipoPatente tipo);
}
