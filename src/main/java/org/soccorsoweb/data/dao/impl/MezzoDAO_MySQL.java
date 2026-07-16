package org.soccorsoweb.data.dao.impl;

import org.soccorsoweb.data.dao.MezzoDAO;
import org.soccorsoweb.model.Mezzo;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.impl.proxy.MezzoProxy;
import org.soccorsoweb.data.Dao;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataItemProxy;
import org.soccorsoweb.data.DataLayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MezzoDAO_MySQL extends Dao implements MezzoDAO {
    private PreparedStatement sMezzoByID;
    private PreparedStatement sMezzi;
    private PreparedStatement iMezzo;
    private PreparedStatement uMezzo;
    private PreparedStatement iAssegnaMezzo; // Nuova query

    public MezzoDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sMezzoByID = connection.prepareStatement("SELECT * FROM Mezzo WHERE ID=?");
            sMezzi = connection.prepareStatement("SELECT * FROM Mezzo");
            iMezzo = connection.prepareStatement("INSERT INTO Mezzo (nome, `desc`, targa) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
            uMezzo = connection.prepareStatement("UPDATE Mezzo SET nome=?, `desc`=?, targa=? WHERE ID=?");
            
            // Inizializzazione query per tabella associazione[cite: 24]
            iAssegnaMezzo = connection.prepareStatement("INSERT INTO assegna_mezzo (ID_MEZZO, ID_MISSIONE) VALUES (?, ?)");
        } catch (SQLException ex) {
            throw new DataException("Error initializing mezzo data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sMezzoByID != null) sMezzoByID.close();
            if (sMezzi != null) sMezzi.close();
            if (iMezzo != null) iMezzo.close();
            if (uMezzo != null) uMezzo.close();
            if (iAssegnaMezzo != null) iAssegnaMezzo.close(); // Chiusura
        } catch (SQLException ex) { }
        super.destroy();
    }

    @Override
    public void assegnaMezzoAMissione(Mezzo m, Missione mis) throws DataException {
        try {
            iAssegnaMezzo.setInt(1, m.getKey());
            iAssegnaMezzo.setInt(2, mis.getKey());
            iAssegnaMezzo.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Errore nell'assegnazione mezzo alla missione", ex);
        }
    }

    // ... (metodi createMezzo, getMezzo, getMezzi, storeMezzo invariati rispetto all'originale)
    @Override
    public Mezzo createMezzo() { return new MezzoProxy(getDataLayer()); }

    private MezzoProxy createMezzo(ResultSet rs) throws DataException {
        MezzoProxy m = (MezzoProxy) createMezzo();
        try {
            m.setKey(rs.getInt("ID"));
            m.setNome(rs.getString("nome"));
            m.setDesc(rs.getString("desc"));
            m.setTarga(rs.getString("targa"));
            m.setVersion(0);
        } catch (SQLException ex) { throw new DataException("Unable to create mezzo object", ex); }
        return m;
    }

    @Override
    public Mezzo getMezzo(int mezzo_key) throws DataException {
        if (getDataLayer().getCache().has(Mezzo.class, mezzo_key)) return getDataLayer().getCache().get(Mezzo.class, mezzo_key);
        try {
            sMezzoByID.setInt(1, mezzo_key);
            try (ResultSet rs = sMezzoByID.executeQuery()) {
                if (rs.next()) {
                    Mezzo m = createMezzo(rs);
                    getDataLayer().getCache().add(Mezzo.class, m);
                    return m;
                }
            }
        } catch (SQLException ex) { throw new DataException("Unable to load mezzo", ex); }
        return null;
    }

    @Override
    public List<Mezzo> getMezzi() throws DataException {
        List<Mezzo> result = new ArrayList<>();
        try (ResultSet rs = sMezzi.executeQuery()) {
            while (rs.next()) {
                Mezzo m = createMezzo(rs);
                getDataLayer().getCache().add(Mezzo.class, m);
                result.add(m);
            }
        } catch (SQLException ex) { throw new DataException("Unable to load mezzo list", ex); }
        return result;
    }

    @Override
    public void storeMezzo(Mezzo mezzo) throws DataException {
        try {
            if (mezzo.getKey() != null && mezzo.getKey() > 0) {
                if (mezzo instanceof DataItemProxy && !((DataItemProxy) mezzo).isModified()) return;
                uMezzo.setString(1, mezzo.getNome());
                uMezzo.setString(2, mezzo.getDesc());
                uMezzo.setString(3, mezzo.getTarga());
                uMezzo.setInt(4, mezzo.getKey());
                uMezzo.executeUpdate();
            } else {
                iMezzo.setString(1, mezzo.getNome());
                iMezzo.setString(2, mezzo.getDesc());
                iMezzo.setString(3, mezzo.getTarga());
                if (iMezzo.executeUpdate() == 1) {
                    try (ResultSet keys = iMezzo.getGeneratedKeys()) {
                        if (keys.next()) {
                            mezzo.setKey(keys.getInt(1));
                            getDataLayer().getCache().add(Mezzo.class, mezzo);
                        }
                    }
                }
            }
            if (mezzo instanceof DataItemProxy) ((DataItemProxy) mezzo).setModified(false);
        } catch (SQLException ex) { throw new DataException("Unable to store mezzo", ex); }
    }
}