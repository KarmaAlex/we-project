package org.soccorsoweb.model;

import org.soccorsoweb.data.DataItem;
import java.time.LocalDateTime;

/**
 *
 * @author Mattia Nanni
 */
public interface Aggiornamento extends DataItem<Integer>{

    Utente getAdmin();
    void setAdmin(Utente admin);

    LocalDateTime getTimestamp();
    void setTimestamp(LocalDateTime timestamp);

    String getTesto();
    void setTesto(String testo);
}
