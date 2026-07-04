package org.dellapenna.we.data.dao.impl;

import org.dellapenna.we.data.dao.AnagraficaDAO;
import org.dellapenna.we.model.Anagrafica;
import org.dellapenna.we.model.Utente;
import org.dellapenna.we.model.impl.proxy.AnagraficaProxy;
import org.dellapenna.we.data.Dao;
import org.dellapenna.we.data.DataException;
import org.dellapenna.we.data.DataItemProxy;
import org.dellapenna.we.data.DataLayer;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AnagraficaDAO_MySQL extends Dao implements AnagraficaDAO{
    private PreparedStatement sAnagraficaByID;
    private PreparedStatement sAnagraficaByUtente;
    private PreparedStatement iAnagrafica;
    private PreparedStatement uAnagrafica;

    public AnagraficaDAO_MySQL(DataLayer d) {
        super(d); // Passa il DataLayer al costruttore di Dao, impostando dataLayer e connection
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sAnagraficaByID = connection.prepareStatement("SELECT * FROM Anagrafica WHERE ID=?");
            sAnagraficaByUtente = connection.prepareStatement("SELECT * FROM Anagrafica WHERE ID_UTENTE=?");

            iAnagrafica = connection.prepareStatement(
                "INSERT INTO Anagrafica (ID_UTENTE, nome, cognome, cf, luogo_nasc, data_nasc) VALUES(?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
            );

            uAnagrafica = connection.prepareStatement(
                "UPDATE Anagrafica SET ID_UTENTE=?, nome=?, cognome=?, cf=?, luogo_nasc=?, data_nasc=? WHERE ID=?"
            );

        } catch (SQLException ex) {
            throw new DataException("Error initializing anagrafica data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sAnagraficaByID != null) sAnagraficaByID.close();
            if (sAnagraficaByUtente != null) sAnagraficaByUtente.close();
            if (iAnagrafica != null) iAnagrafica.close();
            if (uAnagrafica != null) uAnagrafica.close();
        } catch (SQLException ex) {
            // chiusura silente
        }
        super.destroy();
    }

    @Override
    public Anagrafica createAnagrafica() {
        return new AnagraficaProxy(getDataLayer());
    }

    private AnagraficaProxy createAnagrafica(ResultSet rs) throws DataException {
        AnagraficaProxy a = (AnagraficaProxy) createAnagrafica();
        try {
            a.setKey(rs.getInt("ID"));
            a.setNome(rs.getString("nome"));
            a.setCognome(rs.getString("cognome"));
            a.setCf(rs.getString("cf"));
            a.setLuogoNasc(rs.getString("luogo_nasc"));
            
            Date date = rs.getDate("data_nasc");
            if (date != null) {
                a.setDataNasc(date.toLocalDate());
            }
            
            a.setUtenteKey(rs.getInt("ID_UTENTE"));
            a.setVersion(0); // Neutralizzato impostandolo a 0 in memoria per soddisfare il DataItem
        } catch (SQLException ex) {
            throw new DataException("Unable to create anagrafica object from ResultSet", ex);
        }
        return a;
    }

    @Override
    public Anagrafica getAnagrafica(int anagrafica_key) throws DataException {
        Anagrafica a = null;
        if (getDataLayer().getCache().has(Anagrafica.class, anagrafica_key)) {
            a = getDataLayer().getCache().get(Anagrafica.class, anagrafica_key);
        } else {
            try {
                sAnagraficaByID.setInt(1, anagrafica_key);
                try (ResultSet rs = sAnagraficaByID.executeQuery()) {
                    if (rs.next()) {
                        a = createAnagrafica(rs);
                        getDataLayer().getCache().add(Anagrafica.class, a);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load anagrafica by ID", ex);
            }
        }
        return a;
    }

    @Override
    public Anagrafica getAnagraficaByUtente(Utente utente) throws DataException {
        try {
            sAnagraficaByUtente.setInt(1, utente.getKey());
            try (ResultSet rs = sAnagraficaByUtente.executeQuery()) {
                if (rs.next()) {
                    Anagrafica a = createAnagrafica(rs);
                    getDataLayer().getCache().add(Anagrafica.class, a);
                    return a;
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load anagrafica by utente", ex);
        }
        return null;
    }

    @Override
    public void storeAnagrafica(Anagrafica anagrafica, Utente utente) throws DataException {
        try {
            if (anagrafica.getKey() != null && anagrafica.getKey() > 0) { // UPDATE
                if (anagrafica instanceof DataItemProxy && !((DataItemProxy) anagrafica).isModified()) {
                    return;
                }

                uAnagrafica.setInt(1, utente.getKey());
                uAnagrafica.setString(2, anagrafica.getNome());
                uAnagrafica.setString(3, anagrafica.getCognome());
                uAnagrafica.setString(4, anagrafica.getCf());
                uAnagrafica.setString(5, anagrafica.getLuogoNasc());
                uAnagrafica.setDate(6, Date.valueOf(anagrafica.getDataNasc()));
                uAnagrafica.setInt(7, anagrafica.getKey());

                if (uAnagrafica.executeUpdate() == 0) {
                    throw new DataException("Unable to update anagrafica: record not found");
                }

            } else { // INSERT
                iAnagrafica.setInt(1, utente.getKey());
                iAnagrafica.setString(2, anagrafica.getNome());
                iAnagrafica.setString(3, anagrafica.getCognome());
                iAnagrafica.setString(4, anagrafica.getCf());
                iAnagrafica.setString(5, anagrafica.getLuogoNasc());
                iAnagrafica.setDate(6, Date.valueOf(anagrafica.getDataNasc()));

                if (iAnagrafica.executeUpdate() == 1) {
                    try (ResultSet keys = iAnagrafica.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            anagrafica.setKey(key);
                            getDataLayer().getCache().add(Anagrafica.class, anagrafica);
                        }
                    }
                }
            }

            if (anagrafica instanceof DataItemProxy) {
                ((DataItemProxy) anagrafica).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store anagrafica", ex);
        }
    }
}