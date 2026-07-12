package org.soccorsoweb.model.impl.proxy;

import org.soccorsoweb.model.impl.UtenteImpl;
import org.soccorsoweb.model.Anagrafica;
import org.soccorsoweb.data.DataItemProxy;
import org.soccorsoweb.data.dao.AnagraficaDAO;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataItemProxy;
import org.soccorsoweb.data.DataException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UtenteProxy extends UtenteImpl implements DataItemProxy {
    private boolean modified;
    protected DataLayer dataLayer;
    private int anagraficaKey;

    public UtenteProxy(DataLayer d) {
        super();
        this.dataLayer = d;
        this.modified = false;
        this.anagraficaKey = 0;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public void setNomeUtente(String nomeUtente) {
        super.setNomeUtente(nomeUtente);
        this.modified = true;
    }

    @Override
    public void setAdmin(boolean admin) {
        super.setAdmin(admin);
        this.modified = true;
    }

    @Override
    public void setMonteOre(int monteOre) {
        super.setMonteOre(monteOre);
        this.modified = true;
    }

    @Override
    public void setEmail(String email) {
        super.setEmail(email);
        this.modified = true;
    }

    @Override
    public void setHashedPassword(String hashedPassword) {
        super.setHashedPassword(hashedPassword);
        this.modified = true;
    }

    @Override
    public Anagrafica getAnagrafica() {
        if (super.getAnagrafica() == null && this.anagraficaKey > 0) {
            try {
                AnagraficaDAO anagraficaDAO = (AnagraficaDAO) dataLayer.getDAO(Anagrafica.class);
                super.setAnagrafica(anagraficaDAO.getAnagrafica(this.anagraficaKey));
            } catch (DataException ex) {
                Logger.getLogger(UtenteProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getAnagrafica();
    }

    @Override
    public void setAnagrafica(Anagrafica anagrafica) {
        super.setAnagrafica(anagrafica);
        if (anagrafica != null) {
            this.anagraficaKey = anagrafica.getKey();
        } else {
            this.anagraficaKey = 0;
        }
        this.modified = true;
    }

    // Metodi dell'interfaccia DataItemProxy
    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public int getAnagraficaKey() {
        return anagraficaKey;
    }

    public void setAnagraficaKey(int anagraficaKey) {
        this.anagraficaKey = anagraficaKey;
        if (anagraficaKey == 0) {
            super.setAnagrafica(null);
        }
    }
    
}
