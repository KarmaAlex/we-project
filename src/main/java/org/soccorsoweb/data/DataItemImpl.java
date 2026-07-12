/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.soccorsoweb.data;

/**
 *
 * @author Mattia Nanni
 */
public class DataItemImpl<KeyType> implements DataItem<KeyType>{
    
     private KeyType key;
    private long version;

    public DataItemImpl() {
        version = 0;
    }

    @Override
    public KeyType getKey() {
        return key;
    }

    @Override
    public void setKey(KeyType key) {
        this.key = key;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public void setVersion(long version) {
        this.version = version;
    }
    
}
