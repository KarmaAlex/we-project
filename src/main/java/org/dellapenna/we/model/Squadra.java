/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.dellapenna.we.model;
import org.dellapenna.we.data.DataItem;
import java.util.List;

/**
 *
 * @author Mattia Nanni
 */
public interface Squadra extends DataItem<Integer>{
    Utente getCapoSquadra();
    void setCapoSquadra(Utente capo);

    // Here will be mapped the team members from the assegna_squadra table.
    List<Utente> getOperatori();
    void setOperatori(List<Utente> operatori);
    void addOperatore(Utente operatore);
}
