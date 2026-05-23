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
public interface Mezzo extends DataItem<Integer>{
    
    String getNome();
    void setNome(String nome);

    String getDesc();
    void setDesc(String desc);

    String getTarga();
    void setTarga(String targa);
    
}
