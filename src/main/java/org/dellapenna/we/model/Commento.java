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
public interface Commento extends DataItem<Integer>{
    
    Missione getMissione();
    void setMissione(Missione missione);

    Utente getAdmin();
    void setAdmin(Utente admin);

    String getTesto();
    void setTesto(String testo);
    
}
