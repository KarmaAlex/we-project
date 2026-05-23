/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.dellapenna.we.model;
import org.dellapenna.we.data.DataItem;

/**
 *
 * @author Mattia Nanni
 */
public interface Richiesta extends DataItem<Integer>{
    String getNome();
    void setNome(String nome);

    String getEmail();
    void setEmail(String email);

    String getIP();
    void setIP(String ip);

    String getStato();
    void setStato(String stato);

    String getString();
    void setString(String string);

    boolean isVerificato();
    void setVerificato(boolean verificato);

    Date getData();
    void setData(Date data);

    DescRichiesta getDescrizioneDettaglio();
    void setDescrizioneDettaglio(DescRichiesta descrizioneDettaglio);
}
