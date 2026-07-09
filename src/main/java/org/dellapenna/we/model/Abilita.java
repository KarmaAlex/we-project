package org.dellapenna.we.model;

import org.dellapenna.we.data.DataItem;

public interface Abilita extends DataItem<Integer>{
    
    String getNome();
    void setNome(String nome);
    
    String getDesc();
    void setDesc(String desc);
}
