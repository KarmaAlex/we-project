package org.dellapenna.we.model;

import org.dellapenna.we.data.DataItem;

public interface DescRichiesta extends DataItem<Integer>{
   
    String getPosizione();
    void setPosizione(String posizione);

    String getFoto();
    void setFoto(String foto);

    String getDescrizione();
    void setDescrizione(String descrizione);
}
