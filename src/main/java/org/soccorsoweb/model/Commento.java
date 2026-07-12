package org.soccorsoweb.model;

import org.soccorsoweb.data.DataItem;

public interface Commento extends DataItem<Integer>{

    Utente getAdmin();
    void setAdmin(Utente admin);

    String getTesto();
    void setTesto(String testo);
    
}
