/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.dellapenna.we.data;

/**
 *
 * @author Mattia Nanni
 */
public class DataItemProxyImpl extends DataItemImpl implements DataItemProxy {
    
     private boolean modified;

    public DataItemProxyImpl() {
        this.modified = false;
    }

    @Override
    public void setModified(boolean dirty) {
        this.modified = dirty;
    }

    @Override
    public boolean isModified() {
        return modified;
    }
}
