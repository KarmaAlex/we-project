package org.soccorsoweb.data.dao.impl;

import org.soccorsoweb.data.dao.CredenzialiDAO;
import org.soccorsoweb.model.Credenziali;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.model.impl.proxy.CredenzialiProxy;
import org.soccorsoweb.data.Dao;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataItemProxy;
import org.soccorsoweb.data.DataLayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CredenzialiDAO_MySQL extends Dao implements CredenzialiDAO {
    private PreparedStatement sCredenzialiByID;
    private PreparedStatement sCredenzialiByUtente;
    private PreparedStatement iCredenziali;
    private PreparedStatement uCredenziali;
    private PreparedStatement iAssegnaCredenziali;

    public CredenzialiDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sCredenzialiByID = connection.prepareStatement("SELECT * FROM Credenziali WHERE ID=?");
            sCredenzialiByUtente = connection.prepareStatement(
                "SELECT c.* FROM Credenziali c JOIN assegna_credenziali ac ON c.ID = ac.ID_CREDENZIALI WHERE ac.ID_UTENTE=?"
            );

            iCredenziali = connection.prepareStatement(
                "INSERT INTO Credenziali (email, passwordhash) VALUES(?,?)",
                Statement.RETURN_GENERATED_KEYS
            );

            uCredenziali = connection.prepareStatement(
                "UPDATE Credenziali SET email=?, passwordhash=? WHERE ID=?"
            );

            iAssegnaCredenziali = connection.prepareStatement(
                "INSERT IGNORE INTO assegna_credenziali (ID_UTENTE, ID_CREDENZIALI) VALUES(?,?)"
            );

        } catch (SQLException ex) {
            throw new DataException("Error initializing credenziali data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sCredenzialiByID != null) sCredenzialiByID.close();
            if (sCredenzialiByUtente != null) sCredenzialiByUtente.close();
            if (iCredenziali != null) iCredenziali.close();
            if (uCredenziali != null) uCredenziali.close();
            if (iAssegnaCredenziali != null) iAssegnaCredenziali.close();
        } catch (SQLException ex) {
            // chiusura silente
        }
        super.destroy();
    }

    @Override
    public Credenziali createCredenziali() {
        return new CredenzialiProxy(getDataLayer());
    }

    private CredenzialiProxy createCredenziali(ResultSet rs) throws DataException {
        CredenzialiProxy c = (CredenzialiProxy) createCredenziali();
        try {
            c.setKey(rs.getInt("ID"));
            c.setEmail(rs.getString("email"));
            c.setPasswordHash(rs.getBytes("passwordhash"));
            c.setVersion(0);
        } catch (SQLException ex) {
            throw new DataException("Unable to create credenziali object from ResultSet", ex);
        }
        return c;
    }

    @Override
    public Credenziali getCredenziali(int credenziali_key) throws DataException {
        Credenziali c = null;
        if (getDataLayer().getCache().has(Credenziali.class, credenziali_key)) {
            c = getDataLayer().getCache().get(Credenziali.class, credenziali_key);
        } else {
            try {
                sCredenzialiByID.setInt(1, credenziali_key);
                try (ResultSet rs = sCredenzialiByID.executeQuery()) {
                    if (rs.next()) {
                        c = createCredenziali(rs);
                        getDataLayer().getCache().add(Credenziali.class, c);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load credenziali by ID", ex);
            }
        }
        return c;
    }

    @Override
    public Credenziali getCredenzialiByUtente(Utente utente) throws DataException {
        Credenziali c = null;
        try {
            sCredenzialiByUtente.setInt(1, utente.getKey());
            try (ResultSet rs = sCredenzialiByUtente.executeQuery()) {
                if (rs.next()) {
                    c = createCredenziali(rs);
                    getDataLayer().getCache().add(Credenziali.class, c);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load credenziali by utente", ex);
        }
        return c;
    }

    @Override
    public void storeCredenziali(Credenziali credenziali) throws DataException {
        try {
            if (credenziali.getKey() != null && credenziali.getKey() > 0) {
                if (credenziali instanceof DataItemProxy && !((DataItemProxy) credenziali).isModified()) {
                    return;
                }

                uCredenziali.setString(1, credenziali.getEmail());
                uCredenziali.setBytes(2, credenziali.getPasswordHash());
                uCredenziali.setInt(3, credenziali.getKey());

                if (uCredenziali.executeUpdate() == 0) {
                    throw new DataException("Unable to update credenziali: record not found");
                }
            } else {
                iCredenziali.setString(1, credenziali.getEmail());
                iCredenziali.setBytes(2, credenziali.getPasswordHash());

                if (iCredenziali.executeUpdate() == 1) {
                    try (ResultSet keys = iCredenziali.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            credenziali.setKey(key);
                            getDataLayer().getCache().add(Credenziali.class, credenziali);
                        }
                    }
                }
            }

            if (credenziali instanceof DataItemProxy) {
                ((DataItemProxy) credenziali).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store credenziali", ex);
        }
    }

    @Override
    public void legaCredenzialiAUtente(Credenziali credenziali, Utente utente) throws DataException {
        try {
            iAssegnaCredenziali.setInt(1, utente.getKey());
            iAssegnaCredenziali.setInt(2, credenziali.getKey());
            iAssegnaCredenziali.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to link credenziali to utente", ex);
        }
    }
}