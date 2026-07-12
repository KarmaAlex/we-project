package org.soccorsoweb.model;

import org.soccorsoweb.data.DataItem;

public interface Materiale extends DataItem<Integer>{
    String getNome();
    void setNome(String nome);

    String getDesc();
    void setDesc(String desc);

    String getCodMat();
    void setCodMat(String codMat); //codMat stands for code material
}
