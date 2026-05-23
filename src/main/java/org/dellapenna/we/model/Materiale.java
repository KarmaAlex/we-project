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
public interface Materiale extends DataItem<Integer>{
    String getNome();
    void setNome(String nome);

    String getDesc();
    void setDesc(String desc);

    String getCodMat();
    void setCodMat(String codMat); //codMat stands for code material
}
