package org.soccorsoweb.data.dao.impl;

import org.soccorsoweb.data.dao.MezzoDAO;
import org.soccorsoweb.model.Mezzo;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.impl.proxy.MezzoProxy;
import org.soccorsoweb.data.Dao;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataItemProxy;
import org.soccorsoweb.data.DataLayer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MezzoDAO_MySQL extends Dao implements MezzoDAO {
    private PreparedStatement sMezzoByID;
    private PreparedStatement sMezzi;
    private PreparedStatement sMezziDisponibili;
    private PreparedStatement iMezzo;
    private PreparedStatement uMezzo;
    private PreparedStatement dMezzo;
    private PreparedStatement iAssegnaMezzo;
    private PreparedStatement sMezziConStato;

    public MezzoDAO_MySQL(DataLayer d) { super(d); }

    @Override
    public void init() throws DataException {
        try {
                super.init();
                sMezzoByID = connection.prepareStatement("SELECT * FROM Mezzo WHERE ID=?");
                sMezzi = connection.prepareStatement("SELECT * FROM Mezzo");
                sMezziDisponibili = connection.prepareStatement(
                    "SELECT * FROM Mezzo m WHERE NOT EXISTS (" +
                    "  SELECT 1 FROM assegna_mezzo am " +
                    "  JOIN Missione mi ON am.ID_MISSIONE = mi.ID " +
                    "  WHERE am.ID_MEZZO = m.ID AND mi.completata = false" +
                    ")"
                );
                iMezzo = connection.prepareStatement(
                    "INSERT INTO Mezzo (nome, `desc`, targa) VALUES(?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
                uMezzo = connection.prepareStatement(
                    "UPDATE Mezzo SET nome=?, `desc`=?, targa=? WHERE ID=?");
                dMezzo = connection.prepareStatement("DELETE FROM Mezzo WHERE ID=?");
                iAssegnaMezzo = connection.prepareStatement(
                    "INSERT INTO assegna_mezzo (ID_MEZZO, ID_MISSIONE) VALUES (?,?)");
                sMezziConStato = connection.prepareStatement(
                    "SELECT m.*, (" +
                    "  SELECT mi.ID FROM assegna_mezzo am " +
                    "  JOIN Missione mi ON mi.ID = am.ID_MISSIONE " +
                    "  WHERE am.ID_MEZZO = m.ID AND mi.completata = false " +
                    "  ORDER BY mi.ID LIMIT 1" +
                    ") AS missione_id " +
                    "FROM Mezzo m"
                );
        } catch (SQLException ex) {
            throw new DataException("Error initializing mezzo data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sMezzoByID != null) sMezzoByID.close();
            if (sMezzi != null) sMezzi.close();
            if (sMezziDisponibili != null) sMezziDisponibili.close();
            if (iMezzo != null) iMezzo.close();
            if (uMezzo != null) uMezzo.close();
            if (dMezzo != null) dMezzo.close();
            if (iAssegnaMezzo != null) iAssegnaMezzo.close();
            if (sMezziConStato != null) sMezziConStato.close();
        } catch (SQLException ex) { }
        super.destroy();
    }

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
        } catch (SQLException ex) {
            throw new DataException("Unable to create mezzo object", ex);
        }
        return m;
    }

    @Override
    public Mezzo getMezzo(int mezzo_key) throws DataException {
        if (getDataLayer().getCache().has(Mezzo.class, mezzo_key))
            return getDataLayer().getCache().get(Mezzo.class, mezzo_key);
        try {
            sMezzoByID.setInt(1, mezzo_key);
            try (ResultSet rs = sMezzoByID.executeQuery()) {
                if (rs.next()) {
                    Mezzo m = createMezzo(rs);
                    getDataLayer().getCache().add(Mezzo.class, m);
                    return m;
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load mezzo", ex);
        }
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
        } catch (SQLException ex) {
            throw new DataException("Unable to load mezzi list", ex);
        }
        return result;
    }

    @Override
    public List<Mezzo> getMezziDisponibili() throws DataException {
        List<Mezzo> result = new ArrayList<>();
        try (ResultSet rs = sMezziDisponibili.executeQuery()) {
            while (rs.next()) {
                Mezzo m = createMezzo(rs);
                getDataLayer().getCache().add(Mezzo.class, m);
                result.add(m);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load mezzi disponibili", ex);
        }
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
        } catch (SQLException ex) {
            throw new DataException("Unable to store mezzo", ex);
        }
    }

    @Override
    public void deleteMezzo(int mezzo_key) throws DataException {
        try {
            dMezzo.setInt(1, mezzo_key);
            if (dMezzo.executeUpdate() == 0) {
                throw new DataException("Mezzo non trovato");
            }
            getDataLayer().getCache().delete(Mezzo.class, mezzo_key);
        } catch (SQLException ex) {
            throw new DataException("Impossibile eliminare il mezzo: potrebbe essere associato a una missione", ex);
        }
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
    
    
        @Override
        public List<Mezzo> getMezziConStato() throws DataException {
            List<Mezzo> result = new ArrayList<>();
            try (ResultSet rs = sMezziConStato.executeQuery()) {
                while (rs.next()) {
                    MezzoProxy m = (MezzoProxy) createMezzo(); // solo istanzia l'oggetto Java vuoto
                    m.setKey(rs.getInt("ID"));
                    m.setNome(rs.getString("nome"));
                    m.setDesc(rs.getString("desc"));
                    m.setTarga(rs.getString("targa"));
                    m.setVersion(0);

                    int missioneId = rs.getInt("missione_id");
                    m.setMissioneKey(rs.wasNull() ? null : missioneId);

                    result.add(m);
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load mezzi con stato", ex);
            }
            return result;
        }
}