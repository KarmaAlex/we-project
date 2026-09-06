package org.soccorsoweb.data.dao.impl;

import org.soccorsoweb.data.dao.RichiestaDAO;
import org.soccorsoweb.model.Richiesta;
import org.soccorsoweb.model.enums.StatoRichiesta;
import org.soccorsoweb.model.impl.proxy.RichiestaProxy;
import org.soccorsoweb.data.Dao;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.DataItemProxy;
import org.soccorsoweb.data.DataLayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RichiestaDAO_MySQL extends Dao implements RichiestaDAO {
    private PreparedStatement sRichiestaByID;
    private PreparedStatement sRichieste;
    private PreparedStatement iRichiesta;
    private PreparedStatement uRichiesta;

    public RichiestaDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            sRichiestaByID = connection.prepareStatement("SELECT * FROM Richiesta WHERE ID=?");
            sRichieste = connection.prepareStatement("SELECT * FROM Richiesta");

            iRichiesta = connection.prepareStatement(
                "INSERT INTO Richiesta (nome, email, IP, stato, string, verificato, data) VALUES(?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
            );

            uRichiesta = connection.prepareStatement(
                "UPDATE Richiesta SET nome=?, email=?, IP=?, stato=?, string=?, verificato=?, data=? WHERE ID=?"
            );

        } catch (SQLException ex) {
            throw new DataException("Error initializing richiesta data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (sRichiestaByID != null) sRichiestaByID.close();
            if (sRichieste != null) sRichieste.close();
            if (iRichiesta != null) iRichiesta.close();
            if (uRichiesta != null) uRichiesta.close();
        } catch (SQLException ex) {
            // chiusura silente
        }
        super.destroy();
    }

    @Override
    public Richiesta createRichiesta() {
        return new RichiestaProxy(getDataLayer());
    }

    private RichiestaProxy createRichiesta(ResultSet rs) throws DataException {
        RichiestaProxy r = (RichiestaProxy) createRichiesta();
        try {
            r.setKey(rs.getInt("ID"));
            r.setNome(rs.getString("nome"));
            r.setEmail(rs.getString("email"));
            r.setIP(rs.getString("IP"));
            
            // Gestione Enum StatoRichiesta dal database varchar
            String statoDb = rs.getString("stato");
            if (statoDb != null) {
                // Sostituisce eventuali spazi con underscore per mappare l'enum (es. "in attesa" -> "IN_ATTESA")
                r.setStato(StatoRichiesta.valueOf(statoDb.toUpperCase().replace(" ", "_")));
            }
            
            r.setString(rs.getString("string"));
            r.setVerificato(rs.getBoolean("verificato"));
            
            Timestamp ts = rs.getTimestamp("data");
            if (ts != null) {
                r.setData(ts.toLocalDateTime());
            }
            r.setVersion(0); // Neutralizzato per il framework
        } catch (SQLException | IllegalArgumentException ex) {
            throw new DataException("Unable to create richiesta object from ResultSet", ex);
        }
        return r;
    }

    @Override
    public Richiesta getRichiesta(int richiesta_key) throws DataException {
        Richiesta r = null;
        if (getDataLayer().getCache().has(Richiesta.class, richiesta_key)) {
            r = getDataLayer().getCache().get(Richiesta.class, richiesta_key);
        } else {
            try {
                sRichiestaByID.setInt(1, richiesta_key);
                try (ResultSet rs = sRichiestaByID.executeQuery()) {
                    if (rs.next()) {
                        r = createRichiesta(rs);
                        getDataLayer().getCache().add(Richiesta.class, r);
                    }
                }
            } catch (SQLException ex) {
                throw new DataException("Unable to load richiesta by ID", ex);
            }
        }
        return r;
    }

    @Override
    public List<Richiesta> getRichieste() throws DataException {
        List<Richiesta> result = new ArrayList<>();
        try (ResultSet rs = sRichieste.executeQuery()) {
            while (rs.next()) {
                Richiesta r = createRichiesta(rs);
                getDataLayer().getCache().add(Richiesta.class, r);
                result.add(r);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load richieste list", ex);
        }
        return result;
    }

    @Override
    public void storeRichiesta(Richiesta richiesta) throws DataException {
        try {
            // Conversione Enum in Stringa formato database (es. IN_ATTESA -> "in attesa")
            String statoDb = richiesta.getStato() != null ? richiesta.getStato().name().toLowerCase().replace("_", " ") : "in attesa";

            if (richiesta.getKey() != null && richiesta.getKey() > 0) { // UPDATE
                if (richiesta instanceof DataItemProxy && !((DataItemProxy) richiesta).isModified()) {
                    return;
                }

                uRichiesta.setString(1, richiesta.getNome());
                uRichiesta.setString(2, richiesta.getEmail());
                uRichiesta.setString(3, richiesta.getIP());
                uRichiesta.setString(4, statoDb);
                uRichiesta.setString(5, richiesta.getString());
                uRichiesta.setBoolean(6, richiesta.isVerificato());
                uRichiesta.setTimestamp(7, Timestamp.valueOf(richiesta.getData()));
                uRichiesta.setInt(8, richiesta.getKey());

                if (uRichiesta.executeUpdate() == 0) {
                    throw new DataException("Unable to update richiesta: record not found");
                }

            } else { // INSERT
                iRichiesta.setString(1, richiesta.getNome());
                iRichiesta.setString(2, richiesta.getEmail());
                iRichiesta.setString(3, richiesta.getIP());
                iRichiesta.setString(4, statoDb);
                iRichiesta.setString(5, richiesta.getString());
                iRichiesta.setBoolean(6, richiesta.isVerificato());
                iRichiesta.setTimestamp(7, Timestamp.valueOf(richiesta.getData()));

                if (iRichiesta.executeUpdate() == 1) {
                    try (ResultSet keys = iRichiesta.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            richiesta.setKey(key);
                            getDataLayer().getCache().add(Richiesta.class, richiesta);
                        }
                    }
                }
            }

            if (richiesta instanceof DataItemProxy) {
                ((DataItemProxy) richiesta).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store richiesta", ex);
        }
    }
    
    @Override
public List<Richiesta> getRichiesteFiltrate(LocalDateTime dataInizio, LocalDateTime dataFine, StatoRichiesta stato, String email) throws DataException {
    StringBuilder sql = new StringBuilder("SELECT * FROM Richiesta WHERE 1=1");
    List<Object> params = new ArrayList<>();

    if (dataInizio != null) {
        sql.append(" AND data >= ?");
        params.add(Timestamp.valueOf(dataInizio));
    }
    if (dataFine != null) {
        sql.append(" AND data <= ?");
        params.add(Timestamp.valueOf(dataFine));
    }
    if (stato != null) {
        sql.append(" AND stato = ?");
        params.add(stato.name().toLowerCase().replace("_", " ")); // permette alla query di cercare la stringa associata all'enum
    }
    if (email != null && !email.isBlank()) {
        sql.append(" AND email LIKE ?");
        params.add("%" + email + "%"); // ricerca parziale
    }

    List<Richiesta> res = new ArrayList<>();
    try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Richiesta richiesta = createRichiesta(rs);
                getDataLayer().getCache().add(Richiesta.class, richiesta);
                res.add(richiesta);
            }
        }
    } catch (SQLException ex) {
        throw new DataException("Errore nel recupero delle richieste filtrate", ex);
    }
    return res;
}
}