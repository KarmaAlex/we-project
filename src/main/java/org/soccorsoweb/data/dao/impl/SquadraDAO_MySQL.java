package org.soccorsoweb.data.dao.impl;

import org.soccorsoweb.data.dao.SquadraDAO;
import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.model.impl.proxy.SquadraProxy;
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

public class SquadraDAO_MySQL extends Dao implements SquadraDAO {
    private PreparedStatement sSquadraByID;
    private PreparedStatement sSquadre;
    private PreparedStatement iSquadra;
    private PreparedStatement uSquadra;
    private PreparedStatement iAssegnaSquadra;
    private PreparedStatement dAssegnaSquadra;
    private PreparedStatement sOperatoriSquadra;
    private PreparedStatement sSquadreDisponibili;

    public SquadraDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sSquadraByID = connection.prepareStatement("SELECT * FROM Squadra WHERE ID=?");
            sSquadre = connection.prepareStatement("SELECT * FROM Squadra ORDER BY ID");
            
            iSquadra = connection.prepareStatement(
                "INSERT INTO Squadra (ID_CAPO) VALUES(?)",
                Statement.RETURN_GENERATED_KEYS
            );

            uSquadra = connection.prepareStatement(
                "UPDATE Squadra SET ID_CAPO=? WHERE ID=?"
            );

            iAssegnaSquadra = connection.prepareStatement(
                "INSERT IGNORE INTO assegna_squadra (ID_SQUADRA, ID_UTENTE) VALUES(?,?)"
            );

            dAssegnaSquadra = connection.prepareStatement(
                "DELETE FROM assegna_squadra WHERE ID_SQUADRA=? AND ID_UTENTE=?"
            );

            sOperatoriSquadra = connection.prepareStatement(
                "SELECT ID_UTENTE FROM assegna_squadra WHERE ID_SQUADRA=?"
            );
            
            sSquadreDisponibili = connection.prepareStatement(
                "SELECT * FROM Squadra s WHERE NOT EXISTS (" +
                "  SELECT 1 FROM Missione mi " +
                "  WHERE mi.ID_SQUADRA = s.ID AND mi.completata = false" +
                ")"
            );

        } catch (SQLException ex) {
            throw new DataException("Error initializing squadra data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sSquadraByID != null) sSquadraByID.close();
            if (sSquadre != null) sSquadre.close();
            if (iSquadra != null) iSquadra.close();
            if (uSquadra != null) uSquadra.close();
            if (iAssegnaSquadra != null) iAssegnaSquadra.close();
            if (dAssegnaSquadra != null) dAssegnaSquadra.close();
            if (sOperatoriSquadra != null) sOperatoriSquadra.close();
            if (sSquadreDisponibili != null) sSquadreDisponibili.close();
        } catch (SQLException ex) {
            // chiusura silente
        }
        super.destroy();
    }

    @Override
    public Squadra createSquadra() {
        return new SquadraProxy(getDataLayer());
    }

    private SquadraProxy createSquadra(ResultSet rs) throws DataException {
        SquadraProxy s = (SquadraProxy) createSquadra();
        try {
            s.setKey(rs.getInt("ID"));
            s.setCapoKey(rs.getInt("ID_CAPO"));
            s.setVersion(0);
        } catch (SQLException ex) {
            throw new DataException("Unable to create squadra object from ResultSet", ex);
        }
        return s;
    }

    @Override
    public List<Squadra> getSquadre() throws DataException {
        List<Squadra> result = new ArrayList<>();
        try (ResultSet rs = sSquadre.executeQuery()) {
            while (rs.next()) {
                Squadra squadra = createSquadra(rs);
                getDataLayer().getCache().add(Squadra.class, squadra);
                result.add(squadra);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load squadre", ex);
        }
        return result;
    }

    @Override
    public Squadra getSquadra(int squadra_key) throws DataException {
        Squadra s = null;
        if (getDataLayer().getCache().has(Squadra.class, squadra_key)) {
            s = getDataLayer().getCache().get(Squadra.class, squadra_key);
        } else {
            try {
                sSquadraByID.setInt(1, squadra_key);
                try (ResultSet rs = sSquadraByID.executeQuery()) {
                    if (rs.next()) {
                        s = createSquadra(rs);
                        getDataLayer().getCache().add(Squadra.class, s);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load squadra by ID", ex);
            }
        }
        return s;
    }

    @Override
    public void storeSquadra(Squadra squadra) throws DataException {
        try {
            if (squadra.getKey() != null && squadra.getKey() > 0) {
                if (squadra instanceof DataItemProxy && !((DataItemProxy) squadra).isModified()) {
                    return;
                }

                uSquadra.setInt(1, squadra.getCapoSquadra().getKey());
                uSquadra.setInt(2, squadra.getKey());

                if (uSquadra.executeUpdate() == 0) {
                    throw new DataException("Unable to update squadra: record not found");
                }
            } else {
                iSquadra.setInt(1, squadra.getCapoSquadra().getKey());

                if (iSquadra.executeUpdate() == 1) {
                    try (ResultSet keys = iSquadra.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            squadra.setKey(key);
                            getDataLayer().getCache().add(Squadra.class, squadra);
                        }
                    }
                }
            }

            if (squadra instanceof DataItemProxy) {
                ((DataItemProxy) squadra).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store squadra", ex);
        }
    }

    @Override
    public void aggiungiMembroASquadra(Squadra squadra, Utente utente) throws DataException {
        try {
            iAssegnaSquadra.setInt(1, squadra.getKey());
            iAssegnaSquadra.setInt(2, utente.getKey());
            iAssegnaSquadra.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to add member to squadra", ex);
        }
    }

    @Override
    public void rimuoviMembroDaSquadra(Squadra squadra, Utente utente) throws DataException {
        try {
            dAssegnaSquadra.setInt(1, squadra.getKey());
            dAssegnaSquadra.setInt(2, utente.getKey());
            dAssegnaSquadra.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to remove member from squadra", ex);
        }
    }

    @Override
    public List<Utente> getOperatoriSquadra(int squadra_key) throws DataException {
        List<Utente> result = new ArrayList<>();
        UtenteDAO utenteDAO = (UtenteDAO) getDataLayer().getDAO(Utente.class);
        try {
            sOperatoriSquadra.setInt(1, squadra_key);
            try (ResultSet rs = sOperatoriSquadra.executeQuery()) {
                while (rs.next()) {
                    int idUtente = rs.getInt("ID_UTENTE");
                    Utente u = utenteDAO.getUtente(idUtente);
                    if (u != null) {
                        result.add(u);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load operatori della squadra", ex);
        }
        return result;
    }
    
    @Override
    public List<Squadra> getSquadreDisponibili() throws DataException {
        List<Squadra> result = new ArrayList<>();
        try (ResultSet rs = sSquadreDisponibili.executeQuery()) {
            while (rs.next()) {
                Squadra s = createSquadra(rs);
                getDataLayer().getCache().add(Squadra.class, s);
                result.add(s);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load squadre disponibili", ex);
        }
        return result;
    }
}