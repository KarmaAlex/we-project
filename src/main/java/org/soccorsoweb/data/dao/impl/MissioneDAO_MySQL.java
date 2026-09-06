package org.soccorsoweb.data.dao.impl;

import org.soccorsoweb.data.Dao;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.dao.MissioneDAO;
import org.soccorsoweb.model.impl.proxy.MissioneProxy;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.model.enums.EsitoMissione;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.soccorsoweb.model.Materiale;
import org.soccorsoweb.model.Mezzo;

public class MissioneDAO_MySQL extends Dao implements MissioneDAO {

    private PreparedStatement sMissioneByID;
    private PreparedStatement sMissioniBySquadra;
    private PreparedStatement sMissioniByUtente;
    private PreparedStatement iMissione;
    private PreparedStatement uMissione;
    private PreparedStatement sStoricoByUtente;
    private PreparedStatement sStoricoByMezzo;
    private PreparedStatement sStoricoByMateriale;

    public MissioneDAO_MySQL(DataLayer dlayer) {
        super(dlayer);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sMissioneByID = connection.prepareStatement("SELECT * FROM Missione WHERE ID=?");
            sMissioniBySquadra = connection.prepareStatement("SELECT * FROM Missione WHERE ID_SQUADRA=?");

            // Copre sia il caso "utente membro della squadra" sia "utente caposquadra"
            sMissioniByUtente = connection.prepareStatement(
                "SELECT DISTINCT mi.* FROM Missione mi " +
                "JOIN assegna_squadra asq ON asq.ID_SQUADRA = mi.ID_SQUADRA " +
                "WHERE asq.ID_UTENTE = ? " +
                "UNION " +
                "SELECT DISTINCT mi.* FROM Missione mi " +
                "JOIN Squadra s ON s.ID = mi.ID_SQUADRA " +
                "WHERE s.ID_CAPO = ?"
            );

            iMissione = connection.prepareStatement(
                "INSERT INTO Missione (ID_RICHIESTA, ID_SQUADRA, ID_ADMIN, obiettivo, inizio, fine, completata, successo, durata) VALUES (?,?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
            );
            uMissione = connection.prepareStatement(
                "UPDATE Missione SET ID_RICHIESTA=?, ID_SQUADRA=?, ID_ADMIN=?, obiettivo=?, inizio=?, fine=?, completata=?, successo=?, durata=? WHERE ID=?"
            );
            
            sStoricoByUtente = connection.prepareStatement(
                "SELECT DISTINCT mi.* FROM Missione mi " +
                "JOIN assegna_squadra asq ON asq.ID_SQUADRA = mi.ID_SQUADRA " +
                "WHERE asq.ID_UTENTE = ? AND mi.completata = true " +
                "UNION " +
                "SELECT DISTINCT mi.* FROM Missione mi " +
                "JOIN Squadra s ON s.ID = mi.ID_SQUADRA " +
                "WHERE s.ID_CAPO = ? AND mi.completata = true"
            );

            sStoricoByMezzo = connection.prepareStatement(
                "SELECT mi.* FROM Missione mi " +
                "JOIN assegna_mezzo am ON am.ID_MISSIONE = mi.ID " +
                "WHERE am.ID_MEZZO = ? AND mi.completata = true"
            );

            sStoricoByMateriale = connection.prepareStatement(
                "SELECT mi.* FROM Missione mi " +
                "JOIN assegna_materiale amat ON amat.ID_MISSIONE = mi.ID " +
                "WHERE amat.ID_MATERIALE = ? AND mi.completata = true"
            );
            
        } catch (SQLException ex) {
            throw new DataException("Errore nell'inizializzazione dei prepared statements di Missione", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sMissioneByID != null) sMissioneByID.close();
            if (sMissioniBySquadra != null) sMissioniBySquadra.close();
            if (sMissioniByUtente != null) sMissioniByUtente.close();
            if (iMissione != null) iMissione.close();
            if (uMissione != null) uMissione.close();
            if (sStoricoByUtente != null) sStoricoByUtente.close();
            if (sStoricoByMezzo != null) sStoricoByMezzo.close();
            if (sStoricoByMateriale != null) sStoricoByMateriale.close();
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
    public List<Missione> getMissioniByUtente(Utente utente) throws DataException {
        List<Missione> res = new ArrayList<>();
        try {
            sMissioniByUtente.setInt(1, utente.getKey());
            sMissioniByUtente.setInt(2, utente.getKey());
            try (ResultSet rs = sMissioniByUtente.executeQuery()) {
                while (rs.next()) {
                    res.add(getMissione(rs.getInt("ID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore nel recupero delle missioni per utente", ex);
        }
        return res;
    }

    @Override
    public List<Missione> getMissioniFiltrate(LocalDateTime inizio, LocalDateTime fine, Boolean completata, Integer successo) throws DataException {
        StringBuilder sql = new StringBuilder("SELECT * FROM Missione WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (inizio != null) {
            sql.append(" AND (fine IS NULL OR fine >= ?)");
            params.add(Timestamp.valueOf(inizio));
        }
        if (fine != null) {
            sql.append(" AND inizio <= ?");
            params.add(Timestamp.valueOf(fine));
        }
        if (completata != null) {
            sql.append(" AND completata = ?");
            params.add(completata);
        }
        if (successo != null) {
            sql.append(" AND successo = ?");
            params.add(successo);
        }

        List<Missione> res = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Missione missione = createMissione(rs);
                    dataLayer.getCache().add(Missione.class, missione);
                    res.add(missione);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore nel recupero delle missioni filtrate", ex);
        }
        return res;
    }

    @Override
    public void storeMissione(Missione missione) throws DataException {
        try {
            int squadraKey = getSquadraKey(missione);
            int adminKey = getAdminKey(missione);
            int richiestaKey = getRichiestaKey(missione);
            int esitoOrdinale = missione.getEsito() != null ? missione.getEsito().ordinal() : 0;
            Timestamp inizioTs = missione.getInizio() != null ? Timestamp.valueOf(missione.getInizio()) : null;
            Timestamp fineTs = missione.getFine() != null ? Timestamp.valueOf(missione.getFine()) : null;
            Time durataTime = missione.getDurata() != null ? new Time(missione.getDurata().toMillis()) : null;

            if (missione.getKey() != null && missione.getKey() > 0) {
                // UPDATE
                uMissione.setInt(1, richiestaKey);
                uMissione.setInt(2, squadraKey);
                uMissione.setInt(3, adminKey);
                uMissione.setString(4, missione.getObiettivo());
                uMissione.setTimestamp(5, inizioTs);
                uMissione.setTimestamp(6, fineTs);
                uMissione.setBoolean(7, missione.isCompletata());
                uMissione.setInt(8, esitoOrdinale);
                uMissione.setTime(9, durataTime);
                uMissione.setInt(10, missione.getKey());

                if (uMissione.executeUpdate() == 0) {
                    throw new DataException("Unable to update missione: record not found");
                }
            } else {
                // INSERT
                iMissione.setInt(1, richiestaKey);
                iMissione.setInt(2, squadraKey);
                iMissione.setInt(3, adminKey);
                iMissione.setString(4, missione.getObiettivo());
                iMissione.setTimestamp(5, inizioTs);
                iMissione.setTimestamp(6, fineTs);
                iMissione.setBoolean(7, missione.isCompletata());
                iMissione.setInt(8, esitoOrdinale);
                iMissione.setTime(9, durataTime);

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

    // --- helper privati per estrarre le chiavi delle FK, sia da proxy (lazy) che da oggetto pieno ---

    private int getSquadraKey(Missione m) {
        if (m instanceof MissioneProxy) {
            int k = ((MissioneProxy) m).getSquadraKey();
            if (k > 0) return k;
        }
        return m.getSquadra() != null ? m.getSquadra().getKey() : 0;
    }

    private int getAdminKey(Missione m) {
        if (m instanceof MissioneProxy) {
            int k = ((MissioneProxy) m).getAdminKey();
            if (k > 0) return k;
        }
        return m.getAdmin() != null ? m.getAdmin().getKey() : 0;
    }

    private int getRichiestaKey(Missione m) {
        if (m instanceof MissioneProxy) {
            int k = ((MissioneProxy) m).getRichiestaKey();
            if (k > 0) return k;
        }
        return m.getRichiesta() != null ? m.getRichiesta().getKey() : 0;
    }
    
    @Override
    public List<Missione> getStoricoMissioniByUtente(Utente utente) throws DataException {
        List<Missione> res = new ArrayList<>();
        try {
            sStoricoByUtente.setInt(1, utente.getKey());
            sStoricoByUtente.setInt(2, utente.getKey());
            try (ResultSet rs = sStoricoByUtente.executeQuery()) {
                while (rs.next()) {
                    res.add(getMissione(rs.getInt("ID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore nel recupero dello storico missioni per utente", ex);
        }
        return res;
    }

    @Override
    public List<Missione> getStoricoMissioniByMezzo(Mezzo mezzo) throws DataException {
        List<Missione> res = new ArrayList<>();
        try {
            sStoricoByMezzo.setInt(1, mezzo.getKey());
            try (ResultSet rs = sStoricoByMezzo.executeQuery()) {
                while (rs.next()) {
                    res.add(getMissione(rs.getInt("ID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore nel recupero dello storico missioni per mezzo", ex);
        }
        return res;
    }

    @Override
    public List<Missione> getStoricoMissioniByMateriale(Materiale materiale) throws DataException {
        List<Missione> res = new ArrayList<>();
        try {
            sStoricoByMateriale.setInt(1, materiale.getKey());
            try (ResultSet rs = sStoricoByMateriale.executeQuery()) {
                while (rs.next()) {
                    res.add(getMissione(rs.getInt("ID")));
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore nel recupero dello storico missioni per materiale", ex);
        }
        return res;
    }
}