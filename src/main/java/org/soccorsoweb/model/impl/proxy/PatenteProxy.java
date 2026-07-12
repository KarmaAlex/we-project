package org.soccorsoweb.model.impl.proxy;

import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataItemProxy;
import org.soccorsoweb.model.impl.PatenteImpl;
import org.soccorsoweb.model.enums.TipoPatente;

public class PatenteProxy extends PatenteImpl implements DataItemProxy {

    private boolean modified;
    private final DataLayer dataLayer;

    public PatenteProxy(DataLayer d) {
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
    public void setNumero(String numero) {
        super.setNumero(numero);
        this.modified = true;
    }

    @Override
    public void setTipo(TipoPatente tipo) {
        super.setTipo(tipo);
        this.modified = true;
    }
}