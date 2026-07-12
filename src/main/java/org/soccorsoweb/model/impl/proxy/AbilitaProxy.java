package org.soccorsoweb.model.impl.proxy;

import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataItemProxy;
import org.soccorsoweb.model.impl.AbilitaImpl;

public class AbilitaProxy extends AbilitaImpl implements DataItemProxy {

    private boolean modified;
    private final DataLayer dataLayer;

    public AbilitaProxy(DataLayer d) {
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
    public void setDesc(String descrizione) {
        super.setDesc(descrizione);
        this.modified = true;
    }
}