package org.soccorsoweb.test;

import org.soccorsoweb.data.dao.UtenteDAO;
import org.soccorsoweb.data.dao.RichiestaDAO;
import org.soccorsoweb.data.dao.MaterialeDAO;
import org.soccorsoweb.data.dao.MezzoDAO;
import org.soccorsoweb.data.dao.SquadraDAO;
import org.soccorsoweb.data.dao.MissioneDAO;

import org.soccorsoweb.data.dao.impl.UtenteDAO_MySQL;
import org.soccorsoweb.data.dao.impl.RichiestaDAO_MySQL;
import org.soccorsoweb.data.dao.impl.MaterialeDAO_MySQL;
import org.soccorsoweb.data.dao.impl.MezzoDAO_MySQL;
import org.soccorsoweb.data.dao.impl.SquadraDAO_MySQL;
import org.soccorsoweb.data.dao.impl.MissioneDAO_MySQL;

import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.model.Richiesta;
import org.soccorsoweb.model.Materiale;
import org.soccorsoweb.model.Mezzo;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.enums.StatoRichiesta;
import org.soccorsoweb.model.enums.EsitoMissione;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.Random;

public class AServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Test Query Disponibilita/Filtri</title>");
            out.println("<style>body { font-family: sans-serif; padding: 20px; } .success { color: green; } .error { color: red; }</style>");
            out.println("</head><body>");
            out.println("<h1>Test DAO — Disponibilità e Filtri Missioni</h1><hr/>");

            DataLayer dataLayer = null;
            try {
                out.println("<p>1. Connessione JNDI e inizializzazione DAO...</p>");
                Context initContext = new InitialContext();
                Context envContext  = (Context) initContext.lookup("java:comp/env");
                DataSource dataSource = (DataSource) envContext.lookup("jdbc/soccorso");
                dataLayer = new DataLayer(dataSource);

                UtenteDAO utenteDAO = new UtenteDAO_MySQL(dataLayer);
                RichiestaDAO richiestaDAO = new RichiestaDAO_MySQL(dataLayer);
                MaterialeDAO materialeDAO = new MaterialeDAO_MySQL(dataLayer);
                MezzoDAO mezzoDAO = new MezzoDAO_MySQL(dataLayer);
                SquadraDAO squadraDAO = new SquadraDAO_MySQL(dataLayer);
                MissioneDAO missioneDAO = new MissioneDAO_MySQL(dataLayer);

                dataLayer.registerDAO(Utente.class, utenteDAO);
                dataLayer.registerDAO(Richiesta.class, richiestaDAO);
                dataLayer.registerDAO(Materiale.class, materialeDAO);
                dataLayer.registerDAO(Mezzo.class, mezzoDAO);
                dataLayer.registerDAO(Squadra.class, squadraDAO);
                dataLayer.registerDAO(Missione.class, missioneDAO);
                out.println("<p class='success'><b>[OK]</b> DAO registrati.</p>");

                // --- Setup minimo dati di prova ---

                out.println("<p>2. Creazione Utente operatore...</p>");
                Utente u = utenteDAO.createUtente();
                u.setNomeUtente("usr" + String.valueOf(System.currentTimeMillis()).substring(9));
                u.setAdmin(true);
                u.setMonteOre(100);
                utenteDAO.storeUtente(u);
                out.println("<p class='success'><b>[OK]</b> Utente creato, ID: " + u.getKey() + "</p>");

                out.println("<p>3. Creazione Richiesta (necessaria per Missione)...</p>");
                Richiesta r = richiestaDAO.createRichiesta();
                r.setNome("Emergenza Test");
                r.setEmail("test@email.com");
                r.setIP("127.0.0.1");
                r.setStato(StatoRichiesta.IN_ATTESA);
                r.setString("str" + String.valueOf(System.currentTimeMillis()).substring(5));
                r.setVerificato(true);
                r.setData(LocalDateTime.now());
                richiestaDAO.storeRichiesta(r);
                out.println("<p class='success'><b>[OK]</b> Richiesta creata, ID: " + r.getKey() + "</p>");

                out.println("<p>4. Creazione Squadra con capo = Utente...</p>");
                Squadra sq = squadraDAO.createSquadra();
                sq.setCapoSquadra(u);
                squadraDAO.storeSquadra(sq);
                squadraDAO.aggiungiMembroASquadra(sq, u);
                out.println("<p class='success'><b>[OK]</b> Squadra creata, ID: " + sq.getKey() + "</p>");

                out.println("<p>5. Creazione Materiale e Mezzo (per test disponibilità)...</p>");
                Materiale m = materialeDAO.createMateriale();
                m.setNome("Defibrillatore");
                m.setDesc("DAE portatile");
                m.setCodMat("DAE" + String.valueOf(System.currentTimeMillis()).substring(10));
                materialeDAO.storeMateriale(m);

                Mezzo mz = mezzoDAO.createMezzo();
                mz.setNome("Ambulanza Test");
                mz.setDesc("Unità mobile");
                mz.setTarga("ZA" + String.valueOf(System.currentTimeMillis()).substring(11) + "BA");
                mezzoDAO.storeMezzo(mz);
                out.println("<p class='success'><b>[OK]</b> Materiale ID: " + m.getKey() + ", Mezzo ID: " + mz.getKey() + "</p>");

                out.println("<p>6. Creazione Missione ATTIVA (completata=false) associata a Squadra/Materiale/Mezzo...</p>");
                Missione mis = missioneDAO.createMissione();
                mis.setObiettivo("Soccorso stradale");
                mis.setInizio(LocalDateTime.now());
                mis.setFine(LocalDateTime.now().plusHours(2));
                mis.setCompletata(false); // volutamente attiva, per rendere u/mz/m "occupati"
                mis.setDurata(Duration.ofHours(2));
                mis.setRichiesta(r);
                mis.setAdmin(u);
                mis.setSquadra(sq);
                missioneDAO.storeMissione(mis);
                materialeDAO.assegnaMaterialeAMissione(m, mis);
                mezzoDAO.assegnaMezzoAMissione(mz, mis);
                out.println("<p class='success'><b>[OK]</b> Missione ID: " + mis.getKey() + " creata e collegata.</p>");

                dataLayer.getCache().clear();
                out.println("<hr/><h2>Test delle 5 query nuove</h2>");

                // --- 1. Operatori disponibili: l'utente u NON deve comparire, essendo in missione attiva ---
                out.println("<p>Test getUtentiDisponibili()...</p>");
                List<Utente> utentiDisp = utenteDAO.getUtentiDisponibili();
                boolean uPresente = utentiDisp.stream().anyMatch(x -> x.getKey().equals(u.getKey()));
                out.println("<p class='" + (!uPresente ? "success" : "error") + "'>" +
                    (!uPresente ? "[OK] " : "[FALLITO] ") +
                    "Utenti disponibili trovati: " + utentiDisp.size() +
                    " — utente di test " + (uPresente ? "ERRONEAMENTE presente" : "correttamente escluso") + "</p>");
                
                // --- Squadre disponibili: la squadra sq NON deve comparire, essendo assegnata alla missione attiva ---
                out.println("<p>Test getSquadreDisponibili()...</p>");
                List<Squadra> squadreDisp = squadraDAO.getSquadreDisponibili();
                boolean sqPresente = squadreDisp.stream().anyMatch(x -> x.getKey().equals(sq.getKey()));
                out.println("<p class='" + (!sqPresente ? "success" : "error") + "'>" +
                    (!sqPresente ? "[OK] " : "[FALLITO] ") +
                    "Squadre disponibili trovate: " + squadreDisp.size() +
                    " — squadra di test " + (sqPresente ? "ERRONEAMENTE presente" : "correttamente esclusa") + "</p>");

                // --- 2. Mezzi disponibili: mz NON deve comparire ---
                out.println("<p>Test getMezziDisponibili()...</p>");
                List<Mezzo> mezziDisp = mezzoDAO.getMezziDisponibili();
                boolean mzPresente = mezziDisp.stream().anyMatch(x -> x.getKey().equals(mz.getKey()));
                out.println("<p class='" + (!mzPresente ? "success" : "error") + "'>" +
                    (!mzPresente ? "[OK] " : "[FALLITO] ") +
                    "Mezzi disponibili trovati: " + mezziDisp.size() +
                    " — mezzo di test " + (mzPresente ? "ERRONEAMENTE presente" : "correttamente escluso") + "</p>");

                // --- 3. Materiali disponibili: m NON deve comparire ---
                out.println("<p>Test getMaterialiDisponibili()...</p>");
                List<Materiale> materialiDisp = materialeDAO.getMaterialiDisponibili();
                boolean mPresente = materialiDisp.stream().anyMatch(x -> x.getKey().equals(m.getKey()));
                out.println("<p class='" + (!mPresente ? "success" : "error") + "'>" +
                    (!mPresente ? "[OK] " : "[FALLITO] ") +
                    "Materiali disponibili trovati: " + materialiDisp.size() +
                    " — materiale di test " + (mPresente ? "ERRONEAMENTE presente" : "correttamente escluso") + "</p>");

                // --- 4. Missioni per operatore: deve trovare mis, sia come membro sia come capo ---
                out.println("<p>Test getMissioniByUtente(u)...</p>");
                List<Missione> missioniUtente = missioneDAO.getMissioniByUtente(u);
                boolean misTrovata = missioniUtente.stream().anyMatch(x -> x.getKey().equals(mis.getKey()));
                out.println("<p class='" + (misTrovata ? "success" : "error") + "'>" +
                    (misTrovata ? "[OK] " : "[FALLITO] ") +
                    "Missioni trovate per l'utente: " + missioniUtente.size() +
                    " — missione di test " + (misTrovata ? "trovata correttamente" : "NON trovata") + "</p>");

                // --- 5. Filtro missioni per data/esito ---
                out.println("<p>Test getMissioniFiltrate() per intervallo data odierno...</p>");
                LocalDateTime oggiInizio = LocalDateTime.now().minusDays(1);
                LocalDateTime oggiFine = LocalDateTime.now().plusDays(1);
                List<Missione> missioniData = missioneDAO.getMissioniFiltrate(oggiInizio, oggiFine, null, null);
                boolean misInFiltro = missioniData.stream().anyMatch(x -> x.getKey().equals(mis.getKey()));
                out.println("<p class='" + (misInFiltro ? "success" : "error") + "'>" +
                    (misInFiltro ? "[OK] " : "[FALLITO] ") +
                    "Missioni nell'intervallo: " + missioniData.size() +
                    " — missione di test " + (misInFiltro ? "trovata" : "NON trovata") + "</p>");

                out.println("<p>Test getMissioniFiltrate() per missioni completate...</p>");
                List<Missione> missioniCompletate = missioneDAO.getMissioniFiltrate(null, null, true, null);
                out.println("<p>Missioni completate trovate: " + missioniCompletate.size() + "</p>");
                
                
                // --- 6. Filtro Richieste per data/stato/email ---
                out.println("<hr/><h2>Test getRichiesteFiltrate()</h2>");

                out.println("<p>Test filtro per intervallo data odierno (nessun filtro su stato/email)...</p>");
                List<Richiesta> richiesteData = richiestaDAO.getRichiesteFiltrate(oggiInizio, oggiFine, null, null);
                boolean rTrovataData = richiesteData.stream().anyMatch(x -> x.getKey().equals(r.getKey()));
                out.println("<p class='" + (rTrovataData ? "success" : "error") + "'>" +
                    (rTrovataData ? "[OK] " : "[FALLITO] ") +
                    "Richieste nell'intervallo: " + richiesteData.size() +
                    " — richiesta di test " + (rTrovataData ? "trovata" : "NON trovata") + "</p>");

                out.println("<p>Test filtro per stato IN_ATTESA (deve trovare la richiesta di test)...</p>");
                List<Richiesta> richiesteStato = richiestaDAO.getRichiesteFiltrate(null, null, StatoRichiesta.IN_ATTESA, null);
                boolean rTrovataStato = richiesteStato.stream().anyMatch(x -> x.getKey().equals(r.getKey()));
                out.println("<p class='" + (rTrovataStato ? "success" : "error") + "'>" +
                    (rTrovataStato ? "[OK] " : "[FALLITO] ") +
                    "Richieste con stato IN_ATTESA: " + richiesteStato.size() +
                    " — richiesta di test " + (rTrovataStato ? "trovata" : "NON trovata") + "</p>");

                out.println("<p>Test filtro per stato COMPLETATA (nessun match atteso)...</p>");
                List<Richiesta> richiesteStatoErrato = richiestaDAO.getRichiesteFiltrate(null, null, StatoRichiesta.COMPLETATA, null);
                out.println("<p>Richieste con stato COMPLETATA trovate: " + richiesteStatoErrato.size() + "</p>");

                out.println("<p>Test filtro per email (ricerca parziale su 'test')...</p>");
                List<Richiesta> richiesteEmail = richiestaDAO.getRichiesteFiltrate(null, null, null, "test");
                boolean rTrovataEmail = richiesteEmail.stream().anyMatch(x -> x.getKey().equals(r.getKey()));
                out.println("<p class='" + (rTrovataEmail ? "success" : "error") + "'>" +
                    (rTrovataEmail ? "[OK] " : "[FALLITO] ") +
                    "Richieste con email contenente 'test': " + richiesteEmail.size() +
                    " — richiesta di test " + (rTrovataEmail ? "trovata" : "NON trovata") + "</p>");

                out.println("<p>Test filtro per email inesistente (nessun match atteso)...</p>");
                List<Richiesta> richiesteEmailVuoto = richiestaDAO.getRichiesteFiltrate(null, null, null, "xxxxxnonesiste");
                out.println("<p>Richieste con email inesistente trovate: " + richiesteEmailVuoto.size() + "</p>");

                out.println("<p>Test filtro combinato (data + stato + email)...</p>");
                List<Richiesta> richiesteCombo = richiestaDAO.getRichiesteFiltrate(oggiInizio, oggiFine, StatoRichiesta.IN_ATTESA, "test");
                boolean rTrovataCombo = richiesteCombo.stream().anyMatch(x -> x.getKey().equals(r.getKey()));
                out.println("<p class='" + (rTrovataCombo ? "success" : "error") + "'>" +
                    (rTrovataCombo ? "[OK] " : "[FALLITO] ") +
                    "Richieste con filtro combinato: " + richiesteCombo.size() +
                    " — richiesta di test " + (rTrovataCombo ? "trovata" : "NON trovata") + "</p>");
                
                
                // --- 7. Mezzi con stato (occupati e disponibili insieme) ---
                out.println("<hr/><h2>Test getMezziConStato()</h2>");

                List<Mezzo> mezziConStato = mezzoDAO.getMezziConStato();
                out.println("<p>Totale mezzi trovati (occupati + disponibili): " + mezziConStato.size() + "</p>");

                out.println("<table border='1' cellpadding='5' style='border-collapse: collapse;'>");
                out.println("<tr><th>ID</th><th>Nome</th><th>Targa</th><th>Stato</th><th>Missione</th></tr>");
                for (Mezzo mConStato : mezziConStato) {
                    String stato = mConStato.isAssegnato() ? "Occupato" : "Disponibile";
                    String missioneInfo = mConStato.isAssegnato() ? String.valueOf(mConStato.getMissioneKey()) : "-";
                    out.println("<tr><td>" + mConStato.getKey() + "</td><td>" + mConStato.getNome() + "</td><td>" +
                        mConStato.getTarga() + "</td><td>" + stato + "</td><td>" + missioneInfo + "</td></tr>");
                }
                out.println("</table>");

                // Verifica che il mezzo di test debba risultare OCCUPATO, essendo assegnato alla missione attiva creata sopra
                Mezzo mzTrovato = mezziConStato.stream()
                    .filter(x -> x.getKey().equals(mz.getKey()))
                    .findFirst()
                    .orElse(null);

                if (mzTrovato != null && mzTrovato.isAssegnato() && mzTrovato.getMissioneKey().equals(mis.getKey())) {
                    out.println("<p class='success'><b>[OK]</b> Il mezzo di test risulta correttamente OCCUPATO, missione ID: " + mzTrovato.getMissioneKey() + "</p>");
                } else if (mzTrovato != null && !mzTrovato.isAssegnato()) {
                    out.println("<p class='error'><b>[FALLITO]</b> Il mezzo di test risulta DISPONIBILE, ma dovrebbe essere occupato dalla missione attiva.</p>");
                } else {
                    out.println("<p class='error'><b>[FALLITO]</b> Mezzo di test non trovato nella lista.</p>");
                }

                utenteDAO.destroy();
                richiestaDAO.destroy();
                materialeDAO.destroy();
                mezzoDAO.destroy();
                squadraDAO.destroy();
                missioneDAO.destroy();

            } catch (NamingException ex) {
                out.println("<p class='error'><b>[ERRORE JNDI]</b> " + ex.getMessage() + "</p>");
                ex.printStackTrace(out);
            } catch (SQLException ex) {
                out.println("<p class='error'><b>[ERRORE SQL]</b> " + ex.getMessage() + "</p>");
                ex.printStackTrace(out);
            } catch (DataException ex) {
                out.println("<p class='error'><b>[ERRORE DATALAYER]</b> " + ex.getMessage() + "</p>");
                ex.printStackTrace(out);
            } finally {
                if (dataLayer != null) {
                    dataLayer.destroy();
                }
            }

            out.println("<hr/></body></html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "AServlet — test delle query di disponibilità e filtro missioni";
    }
}