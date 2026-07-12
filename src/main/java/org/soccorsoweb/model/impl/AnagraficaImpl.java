package org.soccorsoweb.model.impl;

import org.soccorsoweb.model.Anagrafica;
import org.soccorsoweb.data.DataItemImpl;
import java.time.LocalDate;

public class AnagraficaImpl extends DataItemImpl<Integer> implements Anagrafica{
    private String nome;
    private String cognome;
    private String cf;
    private String luogoNasc;
    private LocalDate dataNasc;

    public AnagraficaImpl() {
        super();
        this.nome = "";
        this.cognome = "";
        this.cf = "";
        this.luogoNasc = "";
        this.dataNasc = null;
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
    public String getCognome() {
        return cognome;
    }

    @Override
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    @Override
    public String getCf() {
        return cf;
    }

    @Override
    public void setCf(String cf) {
        this.cf = cf;
    }

    @Override
    public String getLuogoNasc() {
        return luogoNasc;
    }

    @Override
    public void setLuogoNasc(String luogoNasc) {
        this.luogoNasc = luogoNasc;
    }

    @Override
    public LocalDate getDataNasc() {
        return dataNasc;
    }

    @Override
    public void setDataNasc(LocalDate dataNasc) {
        this.dataNasc = dataNasc;
    }
    
}
