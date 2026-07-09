package org.dellapenna.we.model;

import org.dellapenna.we.data.DataItem;

public interface Credenziali extends DataItem<Integer> {
    
    String getEmail();
    void setEmail(String email);

    byte[] getPasswordHash();
    void setPasswordHash(byte[] passwordHash);
}