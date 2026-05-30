package org.dellapenna.we.model;

import org.dellapenna.we.data.DataItem;

public interface Commento extends DataItem<Integer>{

    Utente getAdmin();
    void setAdmin(Utente admin);

    String getTesto();
    void setTesto(String testo);
    
}
