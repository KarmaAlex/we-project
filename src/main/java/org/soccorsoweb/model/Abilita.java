package org.soccorsoweb.model;

import org.soccorsoweb.data.DataItem;

public interface Abilita extends DataItem<Integer>{
    
    String getNome();
    void setNome(String nome);
    
    String getDesc();
    void setDesc(String desc);
}
