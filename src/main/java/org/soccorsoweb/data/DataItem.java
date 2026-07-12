/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.soccorsoweb.data;

/**
 *
 * @author Mattia Nanni
 */
public interface DataItem<KeyType> {
    
    KeyType getKey();

    long getVersion();

    void setKey(KeyType key);

    void setVersion(long version);
}
