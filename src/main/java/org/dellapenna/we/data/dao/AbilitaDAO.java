package org.dellapenna.we.data.dao;

import org.dellapenna.we.data.DataException;
import org.dellapenna.we.model.Abilita;
import org.dellapenna.we.model.Utente;
import java.util.List;

public interface AbilitaDAO {
    
    Abilita createAbilita();
    
    Abilita getAbilita(int abilita_key) throws DataException;
    
    List<Abilita> getAbilitaList() throws DataException;
    
    List<Abilita> getAbilitaByUtente(Utente utente) throws DataException;
    
    void storeAbilita(Abilita abilita) throws DataException;
    
    void legaAbilitaAUtente(Abilita abilita, Utente utente) throws DataException;
    
    void slegaAbilitaDaUtente(Abilita abilita, Utente utente) throws DataException;
}