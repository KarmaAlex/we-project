package org.soccorsoweb.model.impl.proxy;

import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataItemProxy;
import org.soccorsoweb.data.dao.DescRichiestaDAO;
import org.soccorsoweb.model.DescRichiesta;
import org.soccorsoweb.model.enums.StatoRichiesta;
import org.soccorsoweb.model.impl.RichiestaImpl;
import java.time.LocalDateTime;

public class RichiestaProxy extends RichiestaImpl implements DataItemProxy {

    private boolean modified;
    private final DataLayer dataLayer;

    public RichiestaProxy(DataLayer d) {
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

    // Lazy Loading della relazione 1-a-1 usando il metodo getDescrizioneDettaglio()
    @Override
    public DescRichiesta getDescrizioneDettaglio() {
        if (super.getDescrizioneDettaglio() == null) {
            try {
                DescRichiestaDAO descDAO = (DescRichiestaDAO) dataLayer.getDAO(DescRichiesta.class);
                super.setDescrizioneDettaglio(descDAO.getDescRichiestaByRichiesta(this));
            } catch (DataException e) {
                e.printStackTrace();
            }
        }
        return super.getDescrizioneDettaglio();
    }

    @Override
    public void setNome(String nome) {
        super.setNome(nome);
        this.modified = true;
    }

    @Override
    public void setEmail(String email) {
        super.setEmail(email);
        this.modified = true;
    }

    @Override
    public void setIP(String ip) {
        super.setIP(ip);
        this.modified = true;
    }

    @Override
    public void setStato(StatoRichiesta stato) {
        super.setStato(stato);
        this.modified = true;
    }

    @Override
    public void setString(String string) {
        super.setString(string);
        this.modified = true;
    }

    @Override
    public void setVerificato(boolean verificato) {
        super.setVerificato(verificato);
        this.modified = true;
    }

    @Override
    public void setData(LocalDateTime data) {
        super.setData(data);
        this.modified = true;
    }

    @Override
    public void setDescrizioneDettaglio(DescRichiesta descrizioneDettaglio) {
        super.setDescrizioneDettaglio(descrizioneDettaglio);
        this.modified = true;
    }
}