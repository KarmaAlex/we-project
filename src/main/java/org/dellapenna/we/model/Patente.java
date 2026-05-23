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
public interface Patente extends DataItem<Integer>{
    String getNumero();
    void setNumero(String numero);

    String getTipo();
    void setTipo(String tipo);
}
