package org.dellapenna.we.model.impl;

import org.dellapenna.we.model.Utente;
import org.dellapenna.we.model.Anagrafica;
import org.dellapenna.we.data.DataItemImpl;

public class UtenteImpl extends DataItemImpl<Integer> implements Utente{
    private String nomeUtente;
    private boolean admin;
    private int monteOre;
    private Anagrafica anagrafica;
    private String hashedPassword;
    private String email;

    public UtenteImpl() {
        super();
        this.nomeUtente = "";
        this.admin = false;
        this.monteOre = 0;
        this.anagrafica = null;
        this.hashedPassword = "";
        this.email = "";
    }

    @Override
    public String getNomeUtente() {
        return nomeUtente;
    }

    @Override
    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    @Override
    public boolean isAdmin() {
        return admin;
    }

    @Override
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public int getMonteOre() {
        return monteOre;
    }

    @Override
    public void setMonteOre(int monteOre) {
        this.monteOre = monteOre;
    }

    @Override
    public Anagrafica getAnagrafica() {
        return anagrafica;
    }

    @Override
    public void setAnagrafica(Anagrafica anagrafica) {
        this.anagrafica = anagrafica;
    }

    @Override
    public String getHashedPassword() {
        return hashedPassword;
    }

    @Override
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }
}
