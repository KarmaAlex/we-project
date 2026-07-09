package org.dellapenna.we.model.impl;

import org.dellapenna.we.data.DataItemImpl;
import org.dellapenna.we.model.DescRichiesta;

public class DescRichiestaImpl extends DataItemImpl<Integer> implements DescRichiesta {

    private String posizione;
    private String foto;
    private String descrizione;

    public DescRichiestaImpl() {
        super();
        this.posizione = "";
        this.foto = "";
        this.descrizione = "";
    }

    @Override
    public String getPosizione() {
        return posizione;
    }

    @Override
    public void setPosizione(String posizione) {
        this.posizione = posizione;
    }

    @Override
    public String getFoto() {
        return foto;
    }

    @Override
    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String getDescrizione() {
        return descrizione;
    }

    @Override
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}