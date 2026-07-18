package org.soccorsoweb.data.dao.impl;

import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.data.Dao;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.model.impl.proxy.UtenteProxy;
import org.soccorsoweb.data.DataItemProxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO_MySQL extends Dao implements UtenteDAO {
    private PreparedStatement sUtenteByID;
    private PreparedStatement sUtenteByUsername;
    private PreparedStatement sUtenti;
    private PreparedStatement iUtente;
    private PreparedStatement uUtente;
    private PreparedStatement sAnagraficaByUtente;
    private PreparedStatement sCredenzialiByUtente;
    private PreparedStatement sUtentiDisponibili;

    public UtenteDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sUtenteByID = connection.prepareStatement("SELECT * FROM Utente WHERE ID=?");
            sUtenteByUsername = connection.prepareStatement("SELECT * FROM Utente WHERE nome_utente=?");
            sUtenti = connection.prepareStatement("SELECT * FROM Utente");

            sAnagraficaByUtente = connection.prepareStatement("SELECT ID FROM Anagrafica WHERE ID_UTENTE=?");
            sCredenzialiByUtente = connection.prepareStatement(
                "SELECT c.email, c.passwordhash FROM Credenziali c " +
                "JOIN assegna_credenziali ac ON c.ID = ac.ID_CREDENZIALI WHERE ac.ID_UTENTE=?"
            );

            iUtente = connection.prepareStatement(
                "INSERT INTO Utente (nome_utente, admin, monte_ore) VALUES(?,?,?)", 
                Statement.RETURN_GENERATED_KEYS
            );
            
            uUtente = connection.prepareStatement(
                "UPDATE Utente SET nome_utente=?, admin=?, monte_ore=? WHERE ID=?"
            );

            sUtentiDisponibili = connection.prepareStatement(
                "SELECT * FROM Utente u WHERE NOT EXISTS (" +
                "  SELECT 1 FROM assegna_squadra asq " +
                "  JOIN Missione mi ON mi.ID_SQUADRA = asq.ID_SQUADRA " +
                "  WHERE asq.ID_UTENTE = u.ID AND mi.completata = false" +
                ") AND NOT EXISTS (" +
                "  SELECT 1 FROM Squadra s " +
                "  JOIN Missione mi ON mi.ID_SQUADRA = s.ID " +
                "  WHERE s.ID_CAPO = u.ID AND mi.completata = false" +
                ")"
            );

        } catch (SQLException ex) {
            throw new DataException("Error initializing utente data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sUtenteByID != null) sUtenteByID.close();
            if (sUtenteByUsername != null) sUtenteByUsername.close();
            if (sUtenti != null) sUtenti.close();
            if (sAnagraficaByUtente != null) sAnagraficaByUtente.close();
            if (sCredenzialiByUtente != null) sCredenzialiByUtente.close();
            if (iUtente != null) iUtente.close();
            if (uUtente != null) uUtente.close();
            if (sUtentiDisponibili != null) sUtentiDisponibili.close();
        } catch (SQLException ex) {
            // chiusura silente
        }
        super.destroy();
    }

    @Override
    public Utente createUtente() {
        return new UtenteProxy(getDataLayer());
    }

    private UtenteProxy createUtente(ResultSet rs) throws DataException {
        UtenteProxy u = (UtenteProxy) createUtente();
        try {
            int utenteId = rs.getInt("ID");
            u.setKey(utenteId);
            u.setNomeUtente(rs.getString("nome_utente"));
            u.setAdmin(rs.getBoolean("admin"));
            u.setMonteOre(rs.getInt("monte_ore"));
            u.setVersion(0);

            sAnagraficaByUtente.setInt(1, utenteId);
            try (ResultSet rsAnag = sAnagraficaByUtente.executeQuery()) {
                if (rsAnag.next()) {
                    u.setAnagraficaKey(rsAnag.getInt("ID")); 
                }
            }

            u.setEmail("");
            u.setHashedPassword(""); 

        } catch (SQLException ex) {
            throw new DataException("Unable to create utente object from ResultSet", ex);
        }
        return u;
    }

    @Override
    public Utente getUtente(int utente_key) throws DataException {
        Utente u = null;
        if (getDataLayer().getCache().has(Utente.class, utente_key)) {
            u = getDataLayer().getCache().get(Utente.class, utente_key);
        } else {
            try {
                sUtenteByID.setInt(1, utente_key);
                try (ResultSet rs = sUtenteByID.executeQuery()) {
                    if (rs.next()) {
                        u = createUtente(rs);
                        getDataLayer().getCache().add(Utente.class, u);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load utente by ID", ex);
            }
        }
        return u;
    }

    @Override
    public List<Utente> getUtentiDisponibili() throws DataException {
        List<Utente> result = new ArrayList<>();
        try (ResultSet rs = sUtentiDisponibili.executeQuery()) {
            while (rs.next()) {
                Utente u = createUtente(rs);
                getDataLayer().getCache().add(Utente.class, u);
                result.add(u);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load utenti disponibili", ex);
        }
        return result;
    }

    public Utente getUtenteByUsername(String username) throws DataException {
        try {
            sUtenteByUsername.setString(1, username);
            try (ResultSet rs = sUtenteByUsername.executeQuery()) {
                if (rs.next()) {
                    Utente u = createUtente(rs);
                    getDataLayer().getCache().add(Utente.class, u);
                    return u;
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load utente by username", ex);
        }
        return null;
    }

    @Override
    public List<Utente> getUtenti() throws DataException {
        List<Utente> result = new ArrayList<>();
        try (ResultSet rs = sUtenti.executeQuery()) {
            while (rs.next()) {
                Utente u = createUtente(rs);
                getDataLayer().getCache().add(Utente.class, u);
                result.add(u);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load utenti list", ex);
        }
        return result;
    }

    @Override
    public void storeUtente(Utente utente) throws DataException {
        try {
            if (utente.getKey() != null && utente.getKey() > 0) {
                if (utente instanceof DataItemProxy && !((DataItemProxy) utente).isModified()) {
                    return;
                }
                uUtente.setString(1, utente.getNomeUtente());
                uUtente.setBoolean(2, utente.isAdmin());
                uUtente.setInt(3, utente.getMonteOre());
                uUtente.setInt(4, utente.getKey());

                if (uUtente.executeUpdate() == 0) {
                    throw new DataException("Unable to update utente: record not found");
                }
            } else {
                iUtente.setString(1, utente.getNomeUtente());
                iUtente.setBoolean(2, utente.isAdmin());
                iUtente.setInt(3, utente.getMonteOre());
                
                if (iUtente.executeUpdate() == 1) {
                    try (ResultSet keys = iUtente.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            utente.setKey(key);
                            getDataLayer().getCache().add(Utente.class, utente);
                        }
                    }
                }
            }

            if (utente instanceof DataItemProxy) {
                ((DataItemProxy) utente).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store utente", ex);
        }
    }    
}