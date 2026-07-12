package org.soccorsoweb.model.impl;

import org.soccorsoweb.data.DataItemImpl;
import org.soccorsoweb.model.Abilita;

public class AbilitaImpl extends DataItemImpl<Integer> implements Abilita {

    private String nome;
    private String descrizione;

    public AbilitaImpl() {
        super();
        this.nome = "";
        this.descrizione = "";
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
        return descrizione;
    }

    @Override
    public void setDesc(String descrizione) {
        this.descrizione = descrizione;
    }
}