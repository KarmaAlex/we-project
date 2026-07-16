package org.soccorsoweb.model;

import org.soccorsoweb.data.DataItem;

public interface Mezzo extends DataItem<Integer>{
    
    String getNome();
    void setNome(String nome);

    String getDesc();
    void setDesc(String desc);

    String getTarga();
    void setTarga(String targa);
    
   Integer getMissioneKey();
   
    void setMissioneKey(Integer missioneKey);
    
    boolean isAssegnato();
}