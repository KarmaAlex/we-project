package org.dellapenna.we.model.impl.proxy;

import org.dellapenna.we.data.DataLayer;
import org.dellapenna.we.data.DataItemProxy;
import org.dellapenna.we.model.impl.MezzoImpl;

public class MezzoProxy extends MezzoImpl implements DataItemProxy {

    private boolean modified;
    private final DataLayer dataLayer;

    public MezzoProxy(DataLayer d) {
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
    public void setNome(String nome) {
        super.setNome(nome);
        this.modified = true;
    }

    @Override
    public void setDesc(String desc) {
        super.setDesc(desc);
        this.modified = true;
    }

    @Override
    public void setTarga(String targa) {
        super.setTarga(targa);
        this.modified = true;
    }
}