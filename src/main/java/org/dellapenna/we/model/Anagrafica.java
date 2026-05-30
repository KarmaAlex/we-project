package org.dellapenna.we.model;

import org.dellapenna.we.data.DataItem;
import java.time.LocalDate;

/**
 *
 * @author Mattia Nanni
 */
public interface Anagrafica extends DataItem<Integer>{

    String getNome();
    void setNome(String nome);

    String getCognome();
    void setCognome(String cognome);

    String getCf();
    void setCf(String cf);

    String getLuogoNasc();
    void setLuogoNasc(String luogoNasc);

    LocalDate getDataNasc();
    void setDataNasc(LocalDate dataNasc);
}
