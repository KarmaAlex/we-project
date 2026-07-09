package org.dellapenna.we.model.impl.proxy;

import org.dellapenna.we.data.DataLayer;
import org.dellapenna.we.data.DataItemProxy;
import org.dellapenna.we.model.impl.DescRichiestaImpl;

public class DescRichiestaProxy extends DescRichiestaImpl implements DataItemProxy {

    private boolean modified;
    private final DataLayer dataLayer;
    private int richiestaKey; // Identifica la FK numerica verso Richiesta

    public DescRichiestaProxy(DataLayer d) {
        super();
        this.dataLayer = d;
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