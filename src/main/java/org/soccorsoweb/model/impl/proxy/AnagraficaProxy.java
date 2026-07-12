package org.soccorsoweb.model.impl.proxy;

import org.soccorsoweb.model.impl.AnagraficaImpl;
import org.soccorsoweb.data.DataItemProxy;
import org.soccorsoweb.data.DataLayer;
import java.time.LocalDate;

public class AnagraficaProxy extends AnagraficaImpl implements DataItemProxy {
    
    private boolean modified;
    private int utenteKey;
    protected DataLayer dataLayer;

    public AnagraficaProxy(DataLayer d) {
        super();
        this.dataLayer = d;
        this.modified = false;
        this.utenteKey = 0;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public void setNome(String nome) {
        super.setNome(nome);
        this.modified = true;
    }

    @Override
    public void setCognome(String cognome) {
        super.setCognome(cognome);
        this.modified = true;
    }

    @Override
    public void setCf(String cf) {
        super.setCf(cf);
        this.modified = true;
    }

    @Override
    public void setLuogoNasc(String luogoNasc) {
        super.setLuogoNasc(luogoNasc);
        this.modified = true;
    }

    @Override
    public void setDataNasc(LocalDate dataNasc) {
        super.setDataNasc(dataNasc);
        this.modified = true;
    }

    // Metodi dell'interfaccia DataItemProxy per tracciare lo stato "dirty"
    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    // Getter e Setter ausiliari per iniettare la chiave della relazione dal DAO
    public int getUtenteKey() {
        return utenteKey;
    }

    public void setUtenteKey(int utenteKey) {
        this.utenteKey = utenteKey;
    }
    
}
