/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.dellapenna.we.model;
import org.dellapenna.we.data.DataItem;

/**
 *
 * @author Mattia Nanni
 */
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
