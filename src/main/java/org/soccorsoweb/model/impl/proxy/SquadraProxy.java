package org.soccorsoweb.model.impl.proxy;

import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataItemProxy;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.impl.SquadraImpl;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.data.dao.SquadraDAO;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.soccorsoweb.model.Squadra;

public class SquadraProxy extends SquadraImpl implements DataItemProxy {

    private boolean modified;
    private final DataLayer dataLayer;
    
    private Integer capoKey = null; 
    private boolean operatoriLoaded = false;

    public SquadraProxy(DataLayer d) {
        super();
        this.dataLayer = d;
        this.modified = false;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public void setModified(boolean dirty) {
        this.modified = dirty;
    }

    public void setCapoKey(Integer capoKey) {
        this.capoKey = capoKey;
    }

    @Override
    public Utente getCapoSquadra() {
        if (super.getCapoSquadra() == null && this.capoKey != null && this.capoKey > 0) {
            try {
                super.setCapoSquadra(((UtenteDAO) dataLayer.getDAO(Utente.class)).getUtente(this.capoKey));
            } catch (DataException ex) {
                Logger.getLogger(SquadraProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getCapoSquadra();
    }

    @Override
    public void setCapoSquadra(Utente capo) {
        super.setCapoSquadra(capo);
        this.capoKey = (capo != null) ? capo.getKey() : null;
        this.modified = true;
    }

    @Override
    public List<Utente> getOperatori() {
        if (!operatoriLoaded) {
            if (this.getKey() != null) {
                try {
                    super.setOperatori(((SquadraDAO) dataLayer.getDAO(Squadra.class)).getOperatoriSquadra(this.getKey()));
                } catch (DataException ex) {
                    Logger.getLogger(SquadraProxy.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            this.operatoriLoaded = true;
        }
        return super.getOperatori();
    }

    @Override
    public void setOperatori(List<Utente> operatori) {
        super.setOperatori(operatori);
        this.operatoriLoaded = true;
        this.modified = true;
    }

    @Override
    public void addOperatore(Utente operatore) {
        getOperatori(); 
        super.addOperatore(operatore);
        this.modified = true;
    }
}