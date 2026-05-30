package org.dellapenna.we.model;

import org.dellapenna.we.data.DataItem;
import org.dellapenna.we.model.enums.StatoRichiesta;
import java.time.LocalDateTime;

public interface Richiesta extends DataItem<Integer>{
    
    String getNome();
    void setNome(String nome);

    String getEmail();
    void setEmail(String email);

    String getIP();
    void setIP(String ip);

    StatoRichiesta getStato();
    void setStato(StatoRichiesta stato);

    String getString();
    void setString(String string);

    boolean isVerificato();
    void setVerificato(boolean verificato);

    LocalDateTime getData();
    void setData(LocalDateTime data);

    DescRichiesta getDescrizioneDettaglio();
    void setDescrizioneDettaglio(DescRichiesta descrizioneDettaglio);
}
