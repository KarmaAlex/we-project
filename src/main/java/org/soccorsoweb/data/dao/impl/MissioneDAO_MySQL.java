package org.soccorsoweb.data.dao.impl;

import org.soccorsoweb.data.Dao;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.dao.MissioneDAO;
import org.soccorsoweb.model.impl.proxy.MissioneProxy;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.model.enums.EsitoMissione;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class MissioneDAO_MySQL extends Dao implements MissioneDAO {

    private PreparedStatement sMissioneByID;
    private PreparedStatement sMissioniBySquadra;
    private PreparedStatement iMissione;
    private PreparedStatement uMissione;

    public MissioneDAO_MySQL(DataLayer dlayer) {
        super(dlayer);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sMissioneByID = connection.prepareStatement("SELECT * FROM Missione WHERE ID=?");
            sMissioniBySquadra = connection.prepareStatement("SELECT * FROM Missione WHERE ID_SQUADRA=?");
            iMissione = connection.prepareStatement("INSERT INTO Missione (ID_RICHIESTA, ID_SQUADRA, ID_ADMIN, obiettivo, inizio, fine, completata, successo, durata) VALUES (?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            uMissione = connection.prepareStatement("UPDATE Missione SET ID_RICHIESTA=?, ID_SQUADRA=?, ID_ADMIN=?, obiettivo=?, inizio=?, fine=?, completata=?, successo=?, durata=? WHERE ID=?");
        } catch (SQLException ex) {
            throw new DataException("Errore nell'inizializzazione dei prepared statements di Missione", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sMissioneByID != null) sMissioneByID.close();
            if (sMissioniBySquadra != null) sMissioniBySquadra.close();
            if (iMissione != null) iMissione.close();
            if (uMissione != null) uMissione.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        super.destroy();
    }

    @Override
    public Missione createMissione() {
        return new MissioneProxy(dataLayer);
    }

    private MissioneProxy createMissione(ResultSet rs) throws DataException {
        try {
            MissioneProxy p = (MissioneProxy) createMissione();
            p.setKey(rs.getInt("ID"));
            p.setObiettivo(rs.getString("obiettivo"));
            
            Timestamp inizio = rs.getTimestamp("inizio");
            if (inizio != null) p.setInizio(inizio.toLocalDateTime());
            
            Timestamp fine = rs.getTimestamp("fine");
            if (fine != null) p.setFine(fine.toLocalDateTime());
            
            p.setCompletata(rs.getBoolean("completata"));
            
            int successoInt = rs.getInt("successo");
            if (!rs.wasNull() && successoInt >= 0 && successoInt < EsitoMissione.values().length) {
                p.setSuccesso(EsitoMissione.values()[successoInt]);
            }
            
            Time durata = rs.getTime("durata");
            if (durata != null) {
                p.setDurata(java.time.Duration.ofMillis(durata.getTime()));
            }
            
            p.setRichiestaKey(rs.getInt("ID_RICHIESTA"));
            p.setSquadraKey(rs.getInt("ID_SQUADRA"));
            p.setAdminKey(rs.getInt("ID_ADMIN"));
            
            return p;
        } catch (SQLException ex) {
            throw new DataException("Errore nel recupero della missione dal ResultSet", ex);
        }
    }

    @Override
    public Missione getMissione(int missione_key) throws DataException {
        if (dataLayer.getCache().has(Missione.class, missione_key)) {
            return (Missione) dataLayer.getCache().get(Missione.class, missione_key);
        }
        try {
            sMissioneByID.setInt(1, missione_key);
            try (ResultSet rs = sMissioneByID.executeQuery()) {
                if (rs.next()) {
                    MissioneProxy p = createMissione(rs);
                    dataLayer.getCache().add(Missione.class, p);
                    return p;
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore nel caricamento della missione tramite ID", ex);
        }
        return null;
    }

    @Override
    public List<Missione> getMissioniBySquadra(Squadra squadra) throws DataException {
        List<Missione> res = new ArrayList<>();
        try {
            sMissioniBySquadra.setInt(1, squadra.getKey());
            try (ResultSet rs = sMissioniBySquadra.executeQuery()) {
                while (rs.next()) {
                    res.add(getMissione(rs.getInt("ID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore nel recupero delle missioni per squadra", ex);
        }
        return res;
    }

    @Override
    public void storeMissione(Missione missione) throws DataException {
        try {
            if (missione.getKey() != null && RichmondKey(missione.getKey()) > 0) {
                // UPDATE
                uMissione.setInt(1, missione.getRichiesta() != null ? missione.getRichiesta().getKey() : 0);
                uMissione.setInt(2, RichmondSquadraKey(missione));
                uMissione.setInt(3, RichmondAdminKey(missione));
                uMissione.setString(4, missione.getObiettivo());
                uMissione.setTimestamp(5, missione.getInizio() != null ? Timestamp.valueOf(missione.getInizio()) : null);
                uMissione.setTimestamp(6, missione.getFine() != null ? Timestamp.valueOf(missione.getFine()) : null);
                uMissione.setBoolean(7, RichmondCompletata(missione));
                uMissione.setInt(8, missione.getEsito() != null ? missione.getEsito().ordinal() : 0);
                uMissione.setTime(9, missione.getDurata() != null ? new Time(missione.getDurata().toMillis()) : null);
                uMissione.setInt(10, missione.getKey());
                uMissione.executeUpdate();
            } else {
                // INSERT
                iMissione.setInt(1, missione.getRichiesta() != null ? RichmondRichiestaKey(missione) : 0);
                iMissione.setInt(2, RichmondSquadraKey(missione));
                iMissione.setInt(3, RichmondAdminKey(missione));
                iMissione.setString(4, missione.getObiettivo());
                iMissione.setTimestamp(5, missione.getInizio() != null ? Timestamp.valueOf(missione.getInizio()) : null);
                iMissione.setTimestamp(6, missione.getFine() != null ? Timestamp.valueOf(missione.getFine()) : null);
                iMissione.setBoolean(7, RichmondCompletata(missione));
                iMissione.setInt(8, missione.getEsito() != null ? missione.getEsito().ordinal() : 0);
                iMissione.setTime(9, missione.getDurata() != null ? new Time(missione.getDurata().toMillis()) : null);
                
                if (iMissione.executeUpdate() == 1) {
                    try (ResultSet keys = iMissione.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            missione.setKey(key);
                            dataLayer.getCache().add(Missione.class, missione);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore nel salvataggio della missione", ex);
        }
    }

    private int RichmondKey(Integer key) {
        return key != null ? key : 0;
    }

    private int RichmondSquadraKey(Missione m) {
        if (m instanceof MissioneProxy) {
            return ((MissioneProxy) m).getSquadraKey() > 0 ? ((MissioneProxy) m).getSquadraKey() : (m.getSquadra() != null ? m.getSquadra().getKey() : 0);
        }
        return m.getSquadra() != null ? m.getSquadra().getKey() : 0;
    }

    private int RichmondAdminKey(Missione m) {
        if (m instanceof MissioneProxy) {
            return ((MissioneProxy) m).getAdminKey() > 0 ? ((MissioneProxy) m).getAdminKey() : (m.getAdmin() != null ? m.getAdmin().getKey() : 0);
        }
        return m.getAdmin() != null ? m.getAdmin().getKey() : 0;
    }

    private int RichmondRichiestaKey(Missione m) {
        if (m instanceof MissioneProxy) {
            return ((MissioneProxy) m).getRichiestaKey() > 0 ? ((MissioneProxy) m).getRichiestaKey() : (m.getRichiesta() != null ? m.getRichiesta().getKey() : 0);
        }
        return m.getRichiesta() != null ? m.getRichiesta().getKey() : 0;
    }

    private boolean RichmondCompletata(Missione m) {
        try {
            return m.isCompletata();
        } catch (Exception e) {
            return false;
        }
    }
}