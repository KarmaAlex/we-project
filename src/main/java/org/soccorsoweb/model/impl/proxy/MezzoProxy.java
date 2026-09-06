package org.soccorsoweb.model.impl.proxy;

import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataItemProxy;
import org.soccorsoweb.model.impl.MezzoImpl;

public class MezzoProxy extends MezzoImpl implements DataItemProxy {

    private boolean modified;

    public MezzoProxy(DataLayer d) {
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

    @Override
    public void setMissioneKey(Integer missioneKey) {
        super.setMissioneKey(missioneKey);
        this.modified = true;
    }
}