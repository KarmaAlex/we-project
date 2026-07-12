package org.soccorsoweb.data.dao.impl;

import org.soccorsoweb.data.dao.MaterialeDAO;
import org.soccorsoweb.model.Materiale;
import org.soccorsoweb.model.impl.proxy.MaterialeProxy;
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

public class MaterialeDAO_MySQL extends Dao implements MaterialeDAO {
    private PreparedStatement sMaterialeByID;
    private PreparedStatement sMateriali;
    private PreparedStatement iMateriale;
    private PreparedStatement uMateriale;

    public MaterialeDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sMaterialeByID = connection.prepareStatement("SELECT * FROM Materiale WHERE ID=?");
            sMateriali = connection.prepareStatement("SELECT * FROM Materiale");

            iMateriale = connection.prepareStatement(
                "INSERT INTO Materiale (nome, `desc`, cod_mat) VALUES(?,?,?)",
                Statement.RETURN_GENERATED_KEYS
            );

            uMateriale = connection.prepareStatement(
                "UPDATE Materiale SET nome=?, `desc`=?, cod_mat=? WHERE ID=?"
            );

        } catch (SQLException ex) {
            throw new DataException("Error initializing materiale data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sMaterialeByID != null) sMaterialeByID.close();
            if (sMateriali != null) sMateriali.close();
            if (iMateriale != null) iMateriale.close();
            if (uMateriale != null) uMateriale.close();
        } catch (SQLException ex) {
            // chiusura silente
        }
        super.destroy();
    }

    @Override
    public Materiale createMateriale() {
        return new MaterialeProxy(getDataLayer());
    }

    private MaterialeProxy createMateriale(ResultSet rs) throws DataException {
        MaterialeProxy m = (MaterialeProxy) createMateriale();
        try {
            m.setKey(rs.getInt("ID"));
            m.setNome(rs.getString("nome"));
            m.setDesc(rs.getString("desc"));
            m.setCodMat(rs.getString("cod_mat"));
            m.setVersion(0);
        } catch (SQLException ex) {
            throw new DataException("Unable to create materiale object from ResultSet", ex);
        }
        return m;
    }

    @Override
    public Materiale getMateriale(int materiale_key) throws DataException {
        Materiale m = null;
        if (getDataLayer().getCache().has(Materiale.class, materiale_key)) {
            m = getDataLayer().getCache().get(Materiale.class, materiale_key);
        } else {
            try {
                sMaterialeByID.setInt(1, materiale_key);
                try (ResultSet rs = sMaterialeByID.executeQuery()) {
                    if (rs.next()) {
                        m = createMateriale(rs);
                        getDataLayer().getCache().add(Materiale.class, m);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load materiale by ID", ex);
            }
        }
        return m;
    }

    @Override
    public List<Materiale> getMateriali() throws DataException {
        List<Materiale> result = new ArrayList<>();
        try (ResultSet rs = sMateriali.executeQuery()) {
            while (rs.next()) {
                Materiale m = createMateriale(rs);
                getDataLayer().getCache().add(Materiale.class, m);
                result.add(m);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load materiale list", ex);
        }
        return result;
    }

    @Override
    public void storeMateriale(Materiale materiale) throws DataException {
        try {
            if (materiale.getKey() != null && materiale.getKey() > 0) { // UPDATE
                if (materiale instanceof DataItemProxy && !((DataItemProxy) materiale).isModified()) {
                    return;
                }

                uMateriale.setString(1, materiale.getNome());
                uMateriale.setString(2, materiale.getDesc());
                uMateriale.setString(3, materiale.getCodMat());
                uMateriale.setInt(4, materiale.getKey());

                if (uMateriale.executeUpdate() == 0) {
                    throw new DataException("Unable to update materiale: record not found");
                }
            } else { // INSERT
                iMateriale.setString(1, materiale.getNome());
                iMateriale.setString(2, materiale.getDesc());
                iMateriale.setString(3, materiale.getCodMat());

                if (iMateriale.executeUpdate() == 1) {
                    try (ResultSet keys = iMateriale.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            materiale.setKey(key);
                            getDataLayer().getCache().add(Materiale.class, materiale);
                        }
                    }
                }
            }

            if (materiale instanceof DataItemProxy) {
                ((DataItemProxy) materiale).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store materiale", ex);
        }
    }
}