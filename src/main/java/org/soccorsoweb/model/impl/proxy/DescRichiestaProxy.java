package org.soccorsoweb.model.impl.proxy;

import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataItemProxy;
import org.soccorsoweb.model.impl.DescRichiestaImpl;

public class DescRichiestaProxy extends DescRichiestaImpl implements DataItemProxy {

    private boolean modified;
    private int richiestaKey; // Identifica la FK numerica verso Richiesta

    public DescRichiestaProxy(DataLayer d) {
        super();
        this.modified = false;
        this.richiestaKey = 0;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public void setModified(boolean dirty) {
        this.modified = dirty;
    }

    public int getRichiestaKey() {
        return richiestaKey;
    }

    public void setRichiestaKey(int richiestaKey) {
        this.richiestaKey = richiestaKey;
    }

    @Override
    public void setPosizione(String posizione) {
        super.setPosizione(posizione);
        this.modified = true;
    }

    @Override
    public void setFoto(String foto) {
        super.setFoto(foto);
        this.modified = true;
    }

    @Override
    public void setDescrizione(String descrizione) {
        super.setDescrizione(descrizione);
        this.modified = true;
    }
}