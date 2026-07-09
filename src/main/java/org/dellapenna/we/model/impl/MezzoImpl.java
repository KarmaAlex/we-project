package org.dellapenna.we.model.impl;

import org.dellapenna.we.data.DataItemImpl;
import org.dellapenna.we.model.Mezzo;

public class MezzoImpl extends DataItemImpl<Integer> implements Mezzo {

    private String nome;
    private String desc;
    private String targa;
    private long version;

    public MezzoImpl() {
        super();
        this.nome = "";
        this.desc = "";
        this.targa = "";
        this.version = 0L;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String getTarga() {
        return targa;
    }

    @Override
    public void setTarga(String targa) {
        this.targa = targa;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}