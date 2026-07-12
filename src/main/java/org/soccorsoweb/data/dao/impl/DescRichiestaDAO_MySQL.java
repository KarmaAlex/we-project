package org.soccorsoweb.data.dao.impl;

import org.soccorsoweb.data.dao.DescRichiestaDAO;
import org.soccorsoweb.model.DescRichiesta;
import org.soccorsoweb.model.Richiesta;
import org.soccorsoweb.model.impl.proxy.DescRichiestaProxy;
import org.soccorsoweb.data.Dao;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataItemProxy;
import org.soccorsoweb.data.DataLayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DescRichiestaDAO_MySQL extends Dao implements DescRichiestaDAO {
    private PreparedStatement sDescRichiestaByID;
    private PreparedStatement sDescRichiestaByRichiesta;
    private PreparedStatement iDescRichiesta;
    private PreparedStatement uDescRichiesta;

    public DescRichiestaDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sDescRichiestaByID = connection.prepareStatement("SELECT * FROM Desc_richiesta WHERE ID=?");
            sDescRichiestaByRichiesta = connection.prepareStatement("SELECT * FROM Desc_richiesta WHERE ID_RICHIESTA=?");

            iDescRichiesta = connection.prepareStatement(
                "INSERT INTO Desc_richiesta (ID_RICHIESTA, posizione, foto, descrizione) VALUES(?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
            );

            uDescRichiesta = connection.prepareStatement(
                "UPDATE Desc_richiesta SET ID_RICHIESTA=?, posizione=?, foto=?, descrizione=? WHERE ID=?"
            );

        } catch (SQLException ex) {
            throw new DataException("Error initializing desc_richiesta data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sDescRichiestaByID != null) sDescRichiestaByID.close();
            if (sDescRichiestaByRichiesta != null) sDescRichiestaByRichiesta.close();
            if (iDescRichiesta != null) iDescRichiesta.close();
            if (uDescRichiesta != null) uDescRichiesta.close();
        } catch (SQLException ex) {
            // chiusura silente
        }
        super.destroy();
    }

    @Override
    public DescRichiesta createDescRichiesta() {
        return new DescRichiestaProxy(getDataLayer());
    }

    private DescRichiestaProxy createDescRichiesta(ResultSet rs) throws DataException {
        DescRichiestaProxy dr = (DescRichiestaProxy) createDescRichiesta();
        try {
            dr.setKey(rs.getInt("ID"));
            dr.setPosizione(rs.getString("posizione"));
            dr.setFoto(rs.getString("foto"));
            dr.setDescrizione(rs.getString("descrizione"));
            dr.setRichiestaKey(rs.getInt("ID_RICHIESTA"));
            dr.setVersion(0); // Neutralizzato per il framework
        } catch (SQLException ex) {
            throw new DataException("Unable to create desc_richiesta object from ResultSet", ex);
        }
        return dr;
    }

    @Override
    public DescRichiesta getDescRichiesta(int descRichiesta_key) throws DataException {
        DescRichiesta dr = null;
        if (getDataLayer().getCache().has(DescRichiesta.class, descRichiesta_key)) {
            dr = getDataLayer().getCache().get(DescRichiesta.class, descRichiesta_key);
        } else {
            try {
                sDescRichiestaByID.setInt(1, descRichiesta_key);
                try (ResultSet rs = sDescRichiestaByID.executeQuery()) {
                    if (rs.next()) {
                        dr = createDescRichiesta(rs);
                        getDataLayer().getCache().add(DescRichiesta.class, dr);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load desc_richiesta by ID", ex);
            }
        }
        return dr;
    }

    @Override
    public DescRichiesta getDescRichiestaByRichiesta(Richiesta richiesta) throws DataException {
        try {
            sDescRichiestaByRichiesta.setInt(1, richiesta.getKey());
            try (ResultSet rs = sDescRichiestaByRichiesta.executeQuery()) {
                if (rs.next()) {
                    DescRichiesta dr = createDescRichiesta(rs);
                    getDataLayer().getCache().add(DescRichiesta.class, dr);
                    return dr;
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load desc_richiesta by richiesta", ex);
        }
        return null;
    }

    @Override
    public void storeDescRichiesta(DescRichiesta descRichiesta, Richiesta richiesta) throws DataException {
        try {
            if (descRichiesta.getKey() != null && descRichiesta.getKey() > 0) { // UPDATE
                if (descRichiesta instanceof DataItemProxy && !((DataItemProxy) descRichiesta).isModified()) {
                    return;
                }

                uDescRichiesta.setInt(1, richiesta.getKey());
                uDescRichiesta.setString(2, descRichiesta.getPosizione());
                uDescRichiesta.setString(3, descRichiesta.getFoto());
                uDescRichiesta.setString(4, descRichiesta.getDescrizione());
                uDescRichiesta.setInt(5, descRichiesta.getKey());

                if (uDescRichiesta.executeUpdate() == 0) {
                    throw new DataException("Unable to update desc_richiesta: record not found");
                }

            } else { // INSERT
                iDescRichiesta.setInt(1, richiesta.getKey());
                iDescRichiesta.setString(2, descRichiesta.getPosizione());
                iDescRichiesta.setString(3, descRichiesta.getFoto());
                iDescRichiesta.setString(4, descRichiesta.getDescrizione());

                if (iDescRichiesta.executeUpdate() == 1) {
                    try (ResultSet keys = iDescRichiesta.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            descRichiesta.setKey(key);
                            getDataLayer().getCache().add(DescRichiesta.class, descRichiesta);
                        }
                    }
                }
            }

            if (descRichiesta instanceof DataItemProxy) {
                ((DataItemProxy) descRichiesta).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store desc_richiesta", ex);
        }
    }
}