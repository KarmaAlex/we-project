package org.dellapenna.we.data.dao.impl;

import org.dellapenna.we.data.dao.AbilitaDAO;
import org.dellapenna.we.model.Abilita;
import org.dellapenna.we.model.Utente;
import org.dellapenna.we.model.impl.proxy.AbilitaProxy;
import org.dellapenna.we.data.Dao;
import org.dellapenna.we.data.DataException;
import org.dellapenna.we.data.DataItemProxy;
import org.dellapenna.we.data.DataLayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AbilitaDAO_MySQL extends Dao implements AbilitaDAO {
    private PreparedStatement sAbilitaByID;
    private PreparedStatement sAbilitaList;
    private PreparedStatement sAbilitaByUtente;
    private PreparedStatement iAbilita;
    private PreparedStatement uAbilita;
    private PreparedStatement iAssegnaAbilita;
    private PreparedStatement dAssegnaAbilita;

    public AbilitaDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sAbilitaByID = connection.prepareStatement("SELECT * FROM Abilita WHERE ID=?");
            sAbilitaList = connection.prepareStatement("SELECT * FROM Abilita");
            sAbilitaByUtente = connection.prepareStatement(
                "SELECT a.* FROM Abilita a JOIN assegna_abilita aa ON a.ID = aa.ID_ABILITA WHERE aa.ID_UTENTE=?"
            );

            // Mappatura corretta: la tabella ha solo la colonna 'desc'
            iAbilita = connection.prepareStatement(
                "INSERT INTO Abilita (`desc`) VALUES(?)",
                Statement.RETURN_GENERATED_KEYS
            );

            uAbilita = connection.prepareStatement(
                "UPDATE Abilita SET `desc`=? WHERE ID=?"
            );

            iAssegnaAbilita = connection.prepareStatement(
                "INSERT IGNORE INTO assegna_abilita (ID_UTENTE, ID_ABILITA) VALUES(?,?)"
            );

            dAssegnaAbilita = connection.prepareStatement(
                "DELETE FROM assegna_abilita WHERE ID_UTENTE=? AND ID_ABILITA=?"
            );

        } catch (SQLException ex) {
            throw new DataException("Error initializing abilita data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sAbilitaByID != null) sAbilitaByID.close();
            if (sAbilitaList != null) sAbilitaList.close();
            if (sAbilitaByUtente != null) sAbilitaByUtente.close();
            if (iAbilita != null) iAbilita.close();
            if (uAbilita != null) uAbilita.close();
            if (iAssegnaAbilita != null) iAssegnaAbilita.close();
            if (dAssegnaAbilita != null) dAssegnaAbilita.close();
        } catch (SQLException ex) {
            // chiusura silente
        }
        super.destroy();
    }

    @Override
    public Abilita createAbilita() {
        return new AbilitaProxy(getDataLayer());
    }

    private AbilitaProxy createAbilita(ResultSet rs) throws DataException {
        AbilitaProxy a = (AbilitaProxy) createAbilita();
        try {
            a.setKey(rs.getInt("ID"));
            // Il valore della colonna 'desc' valorizza sia il Nome che la Desc dell'oggetto Java
            String descrizioneEstremi = rs.getString("desc");
            a.setNome(descrizioneEstremi);
            a.setDesc(descrizioneEstremi);
            a.setVersion(0);
        } catch (SQLException ex) {
            throw new DataException("Unable to create abilita object from ResultSet", ex);
        }
        return a;
    }

    @Override
    public Abilita getAbilita(int abilita_key) throws DataException {
        Abilita a = null;
        if (getDataLayer().getCache().has(Abilita.class, abilita_key)) {
            a = getDataLayer().getCache().get(Abilita.class, abilita_key);
        } else {
            try {
                sAbilitaByID.setInt(1, abilita_key);
                try (ResultSet rs = sAbilitaByID.executeQuery()) {
                    if (rs.next()) {
                        a = createAbilita(rs);
                        getDataLayer().getCache().add(Abilita.class, a);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load abilita by ID", ex);
            }
        }
        return a;
    }

    @Override
    public List<Abilita> getAbilitaList() throws DataException {
        List<Abilita> result = new ArrayList<>();
        try (ResultSet rs = sAbilitaList.executeQuery()) {
            while (rs.next()) {
                Abilita a = createAbilita(rs);
                getDataLayer().getCache().add(Abilita.class, a);
                result.add(a);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load abilita list", ex);
        }
        return result;
    }

    @Override
    public List<Abilita> getAbilitaByUtente(Utente utente) throws DataException {
        List<Abilita> result = new ArrayList<>();
        try {
            sAbilitaByUtente.setInt(1, utente.getKey());
            try (ResultSet rs = sAbilitaByUtente.executeQuery()) {
                while (rs.next()) {
                    Abilita a = createAbilita(rs);
                    getDataLayer().getCache().add(Abilita.class, a);
                    result.add(a);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load abilita by utente", ex);
        }
        return result;
    }

    @Override
    public void storeAbilita(Abilita abilita) throws DataException {
        try {
            if (abilita.getKey() != null && abilita.getKey() > 0) { // UPDATE
                if (abilita instanceof DataItemProxy && !((DataItemProxy) abilita).isModified()) {
                    return;
                }

                uAbilita.setString(1, abilita.getDesc());
                uAbilita.setInt(2, abilita.getKey());

                if (uAbilita.executeUpdate() == 0) {
                    throw new DataException("Unable to update abilita: record not found");
                }
            } else { // INSERT
                iAbilita.setString(1, abilita.getDesc());

                if (iAbilita.executeUpdate() == 1) {
                    try (ResultSet keys = iAbilita.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            abilita.setKey(key);
                            getDataLayer().getCache().add(Abilita.class, abilita);
                        }
                    }
                }
            }

            if (abilita instanceof DataItemProxy) {
                ((DataItemProxy) abilita).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store abilita", ex);
        }
    }

    @Override
    public void legaAbilitaAUtente(Abilita abilita, Utente utente) throws DataException {
        try {
            iAssegnaAbilita.setInt(1, utente.getKey());
            iAssegnaAbilita.setInt(2, abilita.getKey());
            iAssegnaAbilita.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to link abilita to utente", ex);
        }
    }

    @Override
    public void slegaAbilitaDaUtente(Abilita abilita, Utente utente) throws DataException {
        try {
            dAssegnaAbilita.setInt(1, utente.getKey());
            dAssegnaAbilita.setInt(2, abilita.getKey());
            dAssegnaAbilita.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to unlink abilita from utente", ex);
        }
    }
}