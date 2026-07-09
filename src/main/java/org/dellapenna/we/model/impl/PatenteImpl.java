package org.dellapenna.we.model.impl;

import org.dellapenna.we.data.DataItemImpl;
import org.dellapenna.we.model.Patente;
import org.dellapenna.we.model.enums.TipoPatente;

public class PatenteImpl extends DataItemImpl<Integer> implements Patente {

    private String numero;
    private TipoPatente tipo;

    public PatenteImpl() {
        super();
        this.numero = "";
        this.tipo = null;
    }

    @Override
    public String getNumero() {
        return numero;
    }

    @Override
    public void setNumero(String numero) {
        this.numero = numero;
    }

    @Override
    public TipoPatente getTipo() {
        return tipo;
    }

    @Override
    public void setTipo(TipoPatente tipo) {
        this.tipo = tipo;
    }
}