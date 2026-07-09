package org.dellapenna.we.model.impl.proxy;

import org.dellapenna.we.data.DataLayer;
import org.dellapenna.we.data.DataItemProxy;
import org.dellapenna.we.model.impl.PatenteImpl;
import org.dellapenna.we.model.enums.TipoPatente;

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