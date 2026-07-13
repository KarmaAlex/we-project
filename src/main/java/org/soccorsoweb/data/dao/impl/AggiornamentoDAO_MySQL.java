package org.soccorsoweb.data.dao.impl;

import org.soccorsoweb.data.Dao;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.dao.AggiornamentoDAO;
import org.soccorsoweb.data.proxy.AggiornamentoProxy;
import org.soccorsoweb.model.Aggiornamento;
import org.soccorsoweb.model.Utente;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AggiornamentoDAO_MySQL extends Dao implements AggiornamentoDAO {

    private PreparedStatement sAggiornamentoByID;
    private PreparedStatement sAggiornamentiByAdmin;
    private PreparedStatement iAggiornamento;
    private PreparedStatement uAggiornamento;

    public AggiornamentoDAO_MySQL(DataLayer dlayer) {
        super(dlayer);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            // Allineato alle colonne esatte del DB: ID, ID_ADMIN, ID_MISSIONE
            sAggiornamentoByID = connection.prepareStatement("SELECT * FROM Aggiornamento WHERE ID=?");
            sAggiornamentiByAdmin = connection.prepareStatement("SELECT * FROM Aggiornamento WHERE ID_ADMIN=? ORDER BY timestamp DESC");
            iAggiornamento = connection.prepareStatement("INSERT INTO Aggiornamento (testo, timestamp, ID_ADMIN, ID_MISSIONE) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            uAggiornamento = connection.prepareStatement("UPDATE Aggiornamento SET testo=?, timestamp=?, ID_ADMIN=?, ID_MISSIONE=? WHERE ID=?");
        } catch (SQLException ex) {
            throw new DataException("Errore nell'inizializzazione dei prepared statements di Aggiornamento", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sAggiornamentoByID != null) sAggiornamentoByID.close();
            if (sAggiornamentiByAdmin != null) sAggiornamentiByAdmin.close();
            if (iAggiornamento != null) iAggiornamento.close();
            if (uAggiornamento != null) uAggiornamento.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        super.destroy();
    }

    @Override
    public Aggiornamento createAggiornamento() {
        return new AggiornamentoProxy(dataLayer);
    }

    private AggiornamentoProxy createAggiornamento(ResultSet rs) throws DataException {
        try {
            AggiornamentoProxy p = (AggiornamentoProxy) createAggiornamento();
            // Prende il valore dalla colonna "ID" del database
            p.setKey(rs.getInt("ID"));
            p.setTesto(rs.getString("testo"));
            Timestamp ts = rs.getTimestamp("timestamp");
            if (ts != null) {
                p.setTimestamp(ts.toLocalDateTime());
            }
            p.setAdminKey(rs.getInt("ID_ADMIN"));
            return p;
        } catch (SQLException ex) {
            throw new DataException("Errore nel recupero dell'aggiornamento dal ResultSet", ex);
        }
    }

    @Override
    public Aggiornamento getAggiornamento(int update_key) throws DataException {
        if (dataLayer.getCache().has(Aggiornamento.class, update_key)) {
            return (Aggiornamento) dataLayer.getCache().get(Aggiornamento.class, update_key);
        }
        try {
            sAggiornamentoByID.setInt(1, update_key);
            try (ResultSet rs = sAggiornamentoByID.executeQuery()) {
                if (rs.next()) {
                    AggiornamentoProxy p = createAggiornamento(rs);
                    dataLayer.getCache().add(Aggiornamento.class, p);
                    return p;
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore nel caricamento dell'aggiornamento tramite ID", ex);
        }
        return null;
    }

    @Override
    public List<Aggiornamento> getAggiornamentiByAdmin(Utente admin) throws DataException {
        List<Aggiornamento> result = new ArrayList<>();
        try {
            sAggiornamentiByAdmin.setInt(1, admin.getKey());
            try (ResultSet rs = sAggiornamentiByAdmin.executeQuery()) {
                while (rs.next()) {
                    int key = rs.getInt("ID");
                    if (dataLayer.getCache().has(Aggiornamento.class, key)) {
                        result.add((Aggiornamento) dataLayer.getCache().get(Aggiornamento.class, key));
                    } else {
                        AggiornamentoProxy p = createAggiornamento(rs);
                        dataLayer.getCache().add(Aggiornamento.class, p);
                        result.add(p);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore nel caricamento degli aggiornamenti dell'admin", ex);
        }
        return result;
    }

    public void storeAggiornamento(Aggiornamento aggiornamento, int idMissione) throws DataException {
        try {
            if (aggiornamento.getKey() != null && aggiornamento.getKey() > 0) {
                uAggiornamento.setString(1, aggiornamento.getTesto());
                uAggiornamento.setTimestamp(2, aggiornamento.getTimestamp() != null ? Timestamp.valueOf(aggiornamento.getTimestamp()) : null);
                uAggiornamento.setInt(3, aggiornamento.getAdmin() != null ? aggiornamento.getAdmin().getKey() : 0);
                uAggiornamento.setInt(4, idMissione);
                uAggiornamento.setInt(5, aggiornamento.getKey());
                uAggiornamento.executeUpdate();
            } else {
                iAggiornamento.setString(1, aggiornamento.getTesto());
                iAggiornamento.setTimestamp(2, aggiornamento.getTimestamp() != null ? Timestamp.valueOf(aggiornamento.getTimestamp()) : null);
                iAggiornamento.setInt(3, aggiornamento.getAdmin() != null ? aggiornamento.getAdmin().getKey() : 0);
                iAggiornamento.setInt(4, idMissione);
                if (iAggiornamento.executeUpdate() == 1) {
                    try (ResultSet keys = iAggiornamento.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            aggiornamento.setKey(key);
                            dataLayer.getCache().add(Aggiornamento.class, aggiornamento);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Errore nel salvataggio dell'aggiornamento con missione", ex);
        }
    }

    @Override
    public void storeAggiornamento(Aggiornamento aggiornamento) throws DataException {
        this.storeAggiornamento(aggiornamento, 0);
    }
}