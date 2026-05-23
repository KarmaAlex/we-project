/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.dellapenna.we.model;
import org.dellapenna.we.data.DataItem;
import java.util.Date;

/**
 *
 * @author Mattia Nanni
 */
public interface Anagrafica extends DataItem<Integer>{
    Utente getUtente();
    void setUtente(Utente utente);

    String getNome();
    void setNome(String nome);

    String getCognome();
    void setCognome(String cognome);

    String getCf();
    void setCf(String cf);

    String getLuogoNasc();
    void setLuogoNasc(String luogoNasc);

    Date getDataNasc();
    void setDataNasc(Date dataNasc);
}
