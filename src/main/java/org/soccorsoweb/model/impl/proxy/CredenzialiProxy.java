package org.soccorsoweb.model.impl.proxy;

import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataItemProxy;
import org.soccorsoweb.model.impl.CredenzialiImpl;

public class CredenzialiProxy extends CredenzialiImpl implements DataItemProxy {

    private boolean modified;

    public CredenzialiProxy(DataLayer d) {
        super();
        this.modified = false;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public void setModified(boolean dirty) {
        this.modified = dirty;
    }

    @Override
    public void setEmail(String email) {
        super.setEmail(email);
        this.modified = true;
    }

    @Override
    public void setPasswordHash(byte[] passwordHash) {
        super.setPasswordHash(passwordHash);
        this.modified = true;
    }
}