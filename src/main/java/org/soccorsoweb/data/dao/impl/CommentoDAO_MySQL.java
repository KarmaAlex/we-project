package org.soccorsoweb.data.dao.impl;

import org.soccorsoweb.data.Dao;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataItemProxy;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.dao.CommentoDAO;
import org.soccorsoweb.model.Commento;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.impl.proxy.CommentoProxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CommentoDAO_MySQL extends Dao implements CommentoDAO {

    private PreparedStatement sCommentoByID;
    private PreparedStatement sCommentiByMissione;
    private PreparedStatement iCommento;
    private PreparedStatement uCommento;

    public CommentoDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sCommentoByID = connection.prepareStatement("SELECT * FROM Commento WHERE ID=?");
            sCommentiByMissione = connection.prepareStatement("SELECT * FROM Commento WHERE ID_MISSIONE=?");

            iCommento = connection.prepareStatement(
                "INSERT INTO Commento (ID_MISSIONE, ID_ADMIN, testo) VALUES (?,?,?)",
                Statement.RETURN_GENERATED_KEYS
            );
            uCommento = connection.prepareStatement(
                "UPDATE Commento SET ID_MISSIONE=?, ID_ADMIN=?, testo=? WHERE ID=?"
            );

        } catch (SQLException ex) {
            throw new DataException("Error initializing commento data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sCommentoByID != null) sCommentoByID.close();
            if (sCommentiByMissione != null) sCommentiByMissione.close();
            if (iCommento != null) iCommento.close();
            if (uCommento != null) uCommento.close();
        } catch (SQLException ex) {
            // chiusura silente
        }
        super.destroy();
    }

    @Override
    public Commento createCommento() {
        return new CommentoProxy(getDataLayer());
    }

    private CommentoProxy createCommento(ResultSet rs) throws DataException {
        CommentoProxy c = (CommentoProxy) createCommento();
        try {
            c.setKey(rs.getInt("ID"));
            c.setTesto(rs.getString("testo"));
            c.setAdminKey(rs.getInt("ID_ADMIN"));
            c.setVersion(0);
        } catch (SQLException ex) {
            throw new DataException("Unable to create commento object from ResultSet", ex);
        }
        return c;
    }

    @Override
    public Commento getCommento(int commento_key) throws DataException {
        Commento c = null;
        if (getDataLayer().getCache().has(Commento.class, commento_key)) {
            c = getDataLayer().getCache().get(Commento.class, commento_key);
        } else {
            try {
                sCommentoByID.setInt(1, commento_key);
                try (ResultSet rs = sCommentoByID.executeQuery()) {
                    if (rs.next()) {
                        c = createCommento(rs);
                        getDataLayer().getCache().add(Commento.class, c);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load commento by ID", ex);
            }
        }
        return c;
    }

    @Override
    public List<Commento> getCommentiByMissione(Missione missione) throws DataException {
        List<Commento> result = new ArrayList<>();
        try {
            sCommentiByMissione.setInt(1, missione.getKey());
            try (ResultSet rs = sCommentiByMissione.executeQuery()) {
                while (rs.next()) {
                    Commento c = createCommento(rs);
                    getDataLayer().getCache().add(Commento.class, c);
                    result.add(c);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load commenti by missione", ex);
        }
        return result;
    }

    @Override
    public void storeCommento(Commento commento, int missione_key) throws DataException {
        try {   //recover admin id
            int adminKey = (commento instanceof CommentoProxy)
                ? ((CommentoProxy) commento).getAdminKey()
                : (commento.getAdmin() != null ? commento.getAdmin().getKey() : 0);

            if (commento.getKey() != null && commento.getKey() > 0) { // UPDATE
                if (commento instanceof DataItemProxy && !((DataItemProxy) commento).isModified()) {
                    return;
                }

                uCommento.setInt(1, missione_key);
                uCommento.setInt(2, adminKey);
                uCommento.setString(3, commento.getTesto());
                uCommento.setInt(4, commento.getKey());

                if (uCommento.executeUpdate() == 0) {
                    throw new DataException("Unable to update commento: record not found");
                }

            } else { // INSERT
                iCommento.setInt(1, missione_key);
                iCommento.setInt(2, adminKey);
                iCommento.setString(3, commento.getTesto());

                if (iCommento.executeUpdate() == 1) {
                    try (ResultSet keys = iCommento.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            commento.setKey(key);
                            getDataLayer().getCache().add(Commento.class, commento);
                        }
                    }
                }
            }

            if (commento instanceof DataItemProxy) {
                ((DataItemProxy) commento).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store commento", ex);
        }
    }
}