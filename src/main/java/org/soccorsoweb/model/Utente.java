package org.soccorsoweb.model;

import org.soccorsoweb.data.DataItem;

public interface Utente extends DataItem<Integer>{
    
    String getNomeUtente();
    void setNomeUtente(String nomeUtente);

    boolean isAdmin();
    void setAdmin(boolean admin);

    int getMonteOre();
    void setMonteOre(int monteOre);

    Anagrafica getAnagrafica();
    void setAnagrafica(Anagrafica anagrafica);
    
    String getHashedPassword();
    void setHashedPassword(String hashedPassword);
    
    String getEmail();
    void setEmail(String email);

}
