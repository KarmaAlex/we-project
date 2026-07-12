package org.soccorsoweb.data.dao.impl;

import org.soccorsoweb.data.dao.PatenteDAO;
import org.soccorsoweb.model.Patente;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.model.enums.TipoPatente;
import org.soccorsoweb.model.impl.proxy.PatenteProxy;
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

public class PatenteDAO_MySQL extends Dao implements PatenteDAO {
    private PreparedStatement sPatenteByID;
    private PreparedStatement sPatenti;
    private PreparedStatement sPatentiByUtente;
    private PreparedStatement iPatente;
    private PreparedStatement uPatente;
    private PreparedStatement iAssegnaPatente;
    private PreparedStatement dAssegnaPatente;

    public PatenteDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sPatenteByID = connection.prepareStatement("SELECT * FROM Patente WHERE ID=?");
            sPatenti = connection.prepareStatement("SELECT * FROM Patente");
            sPatentiByUtente = connection.prepareStatement(
                "SELECT p.* FROM Patente p JOIN assegna_patente ap ON p.ID = ap.ID_PATENTE WHERE ap.ID_UTENTE=?"
            );

            iPatente = connection.prepareStatement(
                "INSERT INTO Patente (numero, tipo) VALUES(?,?)",
                Statement.RETURN_GENERATED_KEYS
            );

            uPatente = connection.prepareStatement(
                "UPDATE Patente SET numero=?, tipo=? WHERE ID=?"
            );

            iAssegnaPatente = connection.prepareStatement(
                "INSERT IGNORE INTO assegna_patente (ID_UTENTE, ID_PATENTE) VALUES(?,?)"
            );

            dAssegnaPatente = connection.prepareStatement(
                "DELETE FROM assegna_patente WHERE ID_UTENTE=? AND ID_PATENTE=?"
            );

        } catch (SQLException ex) {
            throw new DataException("Error initializing patente data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sPatenteByID != null) sPatenteByID.close();
            if (sPatenti != null) sPatenti.close();
            if (sPatentiByUtente != null) sPatentiByUtente.close();
            if (iPatente != null) iPatente.close();
            if (uPatente != null) uPatente.close();
            if (iAssegnaPatente != null) iAssegnaPatente.close();
            if (dAssegnaPatente != null) dAssegnaPatente.close();
        } catch (SQLException ex) {
            // chiusura silente
        }
        super.destroy();
    }

    @Override
    public Patente createPatente() {
        return new PatenteProxy(getDataLayer());
    }

    private PatenteProxy createPatente(ResultSet rs) throws DataException {
        PatenteProxy p = (PatenteProxy) createPatente();
        try {
            p.setKey(rs.getInt("ID"));
            p.setNumero(rs.getString("numero"));
            
            String tipoDb = rs.getString("tipo");
            if (tipoDb != null) {
                p.setTipo(TipoPatente.valueOf(tipoDb.toUpperCase().trim()));
            }
            
            p.setVersion(0);
        } catch (SQLException | IllegalArgumentException ex) {
            throw new DataException("Unable to create patente object from ResultSet", ex);
        }
        return p;
    }

    @Override
    public Patente getPatente(int patente_key) throws DataException {
        Patente p = null;
        if (getDataLayer().getCache().has(Patente.class, patente_key)) {
            p = getDataLayer().getCache().get(Patente.class, patente_key);
        } else {
            try {
                sPatenteByID.setInt(1, patente_key);
                try (ResultSet rs = sPatenteByID.executeQuery()) {
                    if (rs.next()) {
                        p = createPatente(rs);
                        getDataLayer().getCache().add(Patente.class, p);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load patente by ID", ex);
            }
        }
        return p;
    }

    @Override
    public List<Patente> getPatenti() throws DataException {
        List<Patente> result = new ArrayList<>();
        try (ResultSet rs = sPatenti.executeQuery()) {
            while (rs.next()) {
                Patente p = createPatente(rs);
                getDataLayer().getCache().add(Patente.class, p);
                result.add(p);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load patenti list", ex);
        }
        return result;
    }

    @Override
    public List<Patente> getPatentiByUtente(Utente utente) throws DataException {
        List<Patente> result = new ArrayList<>();
        try {
            sPatentiByUtente.setInt(1, utente.getKey());
            try (ResultSet rs = sPatentiByUtente.executeQuery()) {
                while (rs.next()) {
                    Patente p = createPatente(rs);
                    getDataLayer().getCache().add(Patente.class, p);
                    result.add(p);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load patenti by utente", ex);
        }
        return result;
    }

    @Override
    public void storePatente(Patente patente) throws DataException {
        try {
            String tipoDb = patente.getTipo() != null ? patente.getTipo().name() : null;

            if (patente.getKey() != null && patente.getKey() > 0) { // UPDATE
                if (patente instanceof DataItemProxy && !((DataItemProxy) patente).isModified()) {
                    return;
                }

                uPatente.setString(1, patente.getNumero());
                uPatente.setString(2, tipoDb);
                uPatente.setInt(3, patente.getKey());

                if (uPatente.executeUpdate() == 0) {
                    throw new DataException("Unable to update patente: record not found");
                }
            } else { // INSERT
                iPatente.setString(1, patente.getNumero());
                iPatente.setString(2, tipoDb);

                if (iPatente.executeUpdate() == 1) {
                    try (ResultSet keys = iPatente.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            patente.setKey(key);
                            getDataLayer().getCache().add(Patente.class, patente);
                        }
                    }
                }
            }

            if (patente instanceof DataItemProxy) {
                ((DataItemProxy) patente).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store patente", ex);
        }
    }

    @Override
    public void legaPatenteAUtente(Patente patente, Utente utente) throws DataException {
        try {
            iAssegnaPatente.setInt(1, utente.getKey());
            iAssegnaPatente.setInt(2, patente.getKey());
            iAssegnaPatente.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to link patente to utente", ex);
        }
    }

    @Override
    public void slegaPatenteDaUtente(Patente patente, Utente utente) throws DataException {
        try {
            dAssegnaPatente.setInt(1, utente.getKey());
            dAssegnaPatente.setInt(2, patente.getKey());
            dAssegnaPatente.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to unlink patente from utente", ex);
        }
    }
}