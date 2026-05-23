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
public interface DescRichiesta extends DataItem<Integer>{
    
    Richiesta getRichiesta();
    void setRichiesta(Richiesta richiesta);

    String getPosizione();
    void setPosizione(String posizione);

    String getFoto();
    void setFoto(String foto);

    String getDescrizione();
    void setDescrizione(String descrizione);
}
