/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.dellapenna.we.data;

/**
 *
 * @author Mattia Nanni
 */
public interface DataItemProxy {
    boolean isModified();

    void setModified(boolean dirty);
}
