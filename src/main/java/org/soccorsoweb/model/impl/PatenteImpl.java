package org.soccorsoweb.model.impl;

import org.soccorsoweb.data.DataItemImpl;
import org.soccorsoweb.model.Patente;
import org.soccorsoweb.model.enums.TipoPatente;

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