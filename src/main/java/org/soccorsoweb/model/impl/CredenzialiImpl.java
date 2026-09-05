package org.soccorsoweb.model.impl;

import org.soccorsoweb.data.DataItemImpl;
import org.soccorsoweb.model.Credenziali;

public class CredenzialiImpl extends DataItemImpl<Integer> implements Credenziali {

    private String email;
    private byte[] passwordHash;
    private int version;

    public CredenzialiImpl() {
        super();
        this.email = "";
        this.passwordHash = new byte[96];
        this.version = 0;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public byte[] getPasswordHash() {
        return passwordHash;
    }

    @Override
    public void setPasswordHash(byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}