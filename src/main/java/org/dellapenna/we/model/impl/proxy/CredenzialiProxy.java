package org.dellapenna.we.model.impl.proxy;

import org.dellapenna.we.data.DataLayer;
import org.dellapenna.we.data.DataItemProxy;
import org.dellapenna.we.model.impl.CredenzialiImpl;

public class CredenzialiProxy extends CredenzialiImpl implements DataItemProxy {

    private boolean modified;
    private final DataLayer dataLayer;

    public CredenzialiProxy(DataLayer d) {
        super();
        this.dataLayer = d;
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