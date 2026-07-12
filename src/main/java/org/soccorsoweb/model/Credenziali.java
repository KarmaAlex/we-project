package org.soccorsoweb.model;

import org.soccorsoweb.data.DataItem;

public interface Credenziali extends DataItem<Integer> {
    
    String getEmail();
    void setEmail(String email);

    byte[] getPasswordHash();
    void setPasswordHash(byte[] passwordHash);
}