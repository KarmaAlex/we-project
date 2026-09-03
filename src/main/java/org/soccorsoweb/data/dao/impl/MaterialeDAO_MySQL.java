package org.soccorsoweb.data.dao.impl;

import org.soccorsoweb.data.dao.MaterialeDAO;
import org.soccorsoweb.model.Materiale;
import org.soccorsoweb.model.Missione;
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
    private PreparedStatement dMateriale;
    private PreparedStatement iAssegnaMateriale; 
    private PreparedStatement sMaterialiDisponibili;

    public MaterialeDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            sMaterialeByID = connection.prepareStatement("SELECT * FROM Materiale WHERE ID=?");
            sMateriali = connection.prepareStatement("SELECT * FROM Materiale");
            iMateriale = connection.prepareStatement("INSERT INTO Materiale (nome, `desc`, cod_mat) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
            uMateriale = connection.prepareStatement("UPDATE Materiale SET nome=?, `desc`=?, cod_mat=? WHERE ID=?");
            dMateriale = connection.prepareStatement("DELETE FROM Materiale WHERE ID=?");
            sMaterialiDisponibili = connection.prepareStatement("SELECT * FROM Materiale m WHERE NOT EXISTS (" + "  SELECT 1 FROM assegna_materiale am " + "  JOIN Missione mi ON am.ID_MISSIONE = mi.ID " +"WHERE am.ID_MATERIALE = m.ID AND mi.completata = false" +")");
            iAssegnaMateriale = connection.prepareStatement("INSERT INTO assegna_materiale (ID_MATERIALE, ID_MISSIONE) VALUES (?, ?)");
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
            if (dMateriale != null) dMateriale.close();
            if (iAssegnaMateriale != null) iAssegnaMateriale.close();
            if (sMaterialiDisponibili != null) sMaterialiDisponibili.close();
        } catch (SQLException ex) { }
        super.destroy();
    }

    @Override
    public void assegnaMaterialeAMissione(Materiale mat, Missione mis) throws DataException {
        try {
            iAssegnaMateriale.setInt(1, mat.getKey());
            iAssegnaMateriale.setInt(2, mis.getKey());
            iAssegnaMateriale.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Errore nell'assegnazione materiale alla missione", ex);
        }
    }

  @Override
    public Materiale createMateriale() { return new MaterialeProxy(getDataLayer()); }

    private MaterialeProxy createMateriale(ResultSet rs) throws DataException {
        MaterialeProxy m = (MaterialeProxy) createMateriale();
        try {
            m.setKey(rs.getInt("ID"));
            m.setNome(rs.getString("nome"));
            m.setDesc(rs.getString("desc"));
            m.setCodMat(rs.getString("cod_mat"));
            m.setVersion(0);
        } catch (SQLException ex) { throw new DataException("Unable to create materiale object", ex); }
        return m;
    }

    @Override
    public Materiale getMateriale(int materiale_key) throws DataException {
        if (getDataLayer().getCache().has(Materiale.class, materiale_key)) return getDataLayer().getCache().get(Materiale.class, materiale_key);
        try {
            sMaterialeByID.setInt(1, materiale_key);
            try (ResultSet rs = sMaterialeByID.executeQuery()) {
                if (rs.next()) {
                    Materiale m = createMateriale(rs);
                    getDataLayer().getCache().add(Materiale.class, m);
                    return m;
                }
            }
        } catch (SQLException ex) { throw new DataException("Unable to load materiale", ex); }
        return null;
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
        } catch (SQLException ex) { throw new DataException("Unable to load materiale list", ex); }
        return result;
    }
    
        @Override
        public List<Materiale> getMaterialiDisponibili() throws DataException {
            List<Materiale> result = new ArrayList<>();
            try (ResultSet rs = sMaterialiDisponibili.executeQuery()) {
                while (rs.next()) {
                    Materiale m = createMateriale(rs);
                    getDataLayer().getCache().add(Materiale.class, m);
                    result.add(m);
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load materiali disponibili", ex);
            }
            return result;
        }

    @Override
    public void storeMateriale(Materiale materiale) throws DataException {
        try {
            if (materiale.getKey() != null && materiale.getKey() > 0) {
                if (materiale instanceof DataItemProxy && !((DataItemProxy) materiale).isModified()) return;
                uMateriale.setString(1, materiale.getNome());
                uMateriale.setString(2, materiale.getDesc());
                uMateriale.setString(3, materiale.getCodMat());
                uMateriale.setInt(4, materiale.getKey());
                uMateriale.executeUpdate();
            } else {
                iMateriale.setString(1, materiale.getNome());
                iMateriale.setString(2, materiale.getDesc());
                iMateriale.setString(3, materiale.getCodMat());
                if (iMateriale.executeUpdate() == 1) {
                    try (ResultSet keys = iMateriale.getGeneratedKeys()) {
                        if (keys.next()) {
                            materiale.setKey(keys.getInt(1));
                            getDataLayer().getCache().add(Materiale.class, materiale);
                        }
                    }
                }
            }
            if (materiale instanceof DataItemProxy) ((DataItemProxy) materiale).setModified(false);
        } catch (SQLException ex) { throw new DataException("Unable to store materiale", ex); }
    }

    @Override
    public void deleteMateriale(int materiale_key) throws DataException {
        try {
            dMateriale.setInt(1, materiale_key);
            if (dMateriale.executeUpdate() == 0) {
                throw new DataException("Materiale non trovato");
            }
            getDataLayer().getCache().delete(Materiale.class, materiale_key);
        } catch (SQLException ex) {
            throw new DataException("Impossibile eliminare il materiale: potrebbe essere associato a una missione", ex);
        }
    }
}