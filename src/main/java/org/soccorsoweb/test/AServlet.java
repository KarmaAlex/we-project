package org.soccorsoweb.test;

import org.soccorsoweb.data.dao.impl.UtenteDAO_MySQL;
import org.soccorsoweb.data.dao.impl.AnagraficaDAO_MySQL;
import org.soccorsoweb.data.dao.impl.RichiestaDAO_MySQL;
import org.soccorsoweb.data.dao.impl.DescRichiestaDAO_MySQL;
import org.soccorsoweb.data.dao.impl.PatenteDAO_MySQL;
import org.soccorsoweb.data.dao.impl.AbilitaDAO_MySQL;
import org.soccorsoweb.data.dao.impl.CredenzialiDAO_MySQL;
import org.soccorsoweb.data.dao.impl.MaterialeDAO_MySQL;
import org.soccorsoweb.data.dao.impl.MezzoDAO_MySQL;
import org.soccorsoweb.data.dao.impl.SquadraDAO_MySQL;
import org.soccorsoweb.data.dao.impl.MissioneDAO_MySQL;
import org.soccorsoweb.data.dao.impl.AggiornamentoDAO_MySQL;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.model.Utente;
import org.soccorsoweb.model.Anagrafica;
import org.soccorsoweb.model.Richiesta;
import org.soccorsoweb.model.DescRichiesta;
import org.soccorsoweb.model.Patente;
import org.soccorsoweb.model.Abilita;
import org.soccorsoweb.model.Credenziali;
import org.soccorsoweb.model.Materiale;
import org.soccorsoweb.model.Mezzo;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.Aggiornamento;
import org.soccorsoweb.model.enums.StatoRichiesta;
import org.soccorsoweb.model.enums.TipoPatente;
import org.soccorsoweb.model.enums.EsitoMissione;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.Duration;
import java.util.List;
import java.util.Random;

/**
 *
 * @author GDP
 */
public class AServlet extends HttpServlet {

    private static final String DB_SERVER = "localhost";
    private static final int DB_PORT = 3306;
    private static final String DB_NAME = "soccorso"; 
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private String generaCodiceFiscaleRandom() {
        String lettere = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numeri = "0123456789";
        Random r = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 6; i++) sb.append(lettere.charAt(r.nextInt(lettere.length())));
        for (int i = 0; i < 2; i++) sb.append(numeri.charAt(r.nextInt(numeri.length())));
        sb.append(lettere.charAt(r.nextInt(lettere.length())));
        for (int i = 0; i < 2; i++) sb.append(numeri.charAt(r.nextInt(numeri.length())));
        sb.append(lettere.charAt(r.nextInt(lettere.length())));
        for (int i = 0; i < 3; i++) sb.append(numeri.charAt(r.nextInt(numeri.length())));
        sb.append(lettere.charAt(r.nextInt(lettere.length())));

        return sb.toString();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet " + getServletName() + " - DAO Test Completo</title>");
            out.println("<style>body { font-family: sans-serif; padding: 20px; } .success { color: green; } .error { color: red; }</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>This is servlet <em>" + getServletName() + "</em> (implemented in class <em>" + getClass().getSimpleName() + "</em>)" + "</h1>");

            out.println("<h2>Esecuzione Test dei DAO con Relazioni, Credenziali, Patente, Abilità, Materiale, Mezzo, Squadra, Missione e Aggiornamento su MySQL</h2>");
            out.println("<hr/>");

            DataLayer dataLayer = null;
            try {
                out.println("<p>1. Inizializzazione DataSource MySQL... </p>");
                MysqlDataSource dataSource = new MysqlDataSource();
                dataSource.setServerName(DB_SERVER);
                dataSource.setPortNumber(DB_PORT);
                dataSource.setDatabaseName(DB_NAME);
                dataSource.setUser(DB_USER);
                dataSource.setPassword(DB_PASSWORD);
                dataSource.setServerTimezone("UTC");

                dataLayer = new DataLayer(dataSource);
                out.println("<p class='success'><b>[OK]</b> DataLayer istanziato correttamente con il DataSource.</p>");

                out.println("<p>2. Inizializzazione dei DAO... </p>");
                UtenteDAO_MySQL utenteDAO = new UtenteDAO_MySQL(dataLayer);
                AnagraficaDAO_MySQL anagraficaDAO = new AnagraficaDAO_MySQL(dataLayer);
                RichiestaDAO_MySQL richiestaDAO = new RichiestaDAO_MySQL(dataLayer);
                DescRichiestaDAO_MySQL descRichiestaDAO = new DescRichiestaDAO_MySQL(dataLayer);
                PatenteDAO_MySQL patenteDAO = new PatenteDAO_MySQL(dataLayer);
                AbilitaDAO_MySQL abilitaDAO = new AbilitaDAO_MySQL(dataLayer);
                CredenzialiDAO_MySQL credenzialiDAO = new CredenzialiDAO_MySQL(dataLayer);
                MaterialeDAO_MySQL materialeDAO = new MaterialeDAO_MySQL(dataLayer);
                MezzoDAO_MySQL mezzoDAO = new MezzoDAO_MySQL(dataLayer);
                SquadraDAO_MySQL squadraDAO = new SquadraDAO_MySQL(dataLayer);
                MissioneDAO_MySQL missioneDAO = new MissioneDAO_MySQL(dataLayer);
                AggiornamentoDAO_MySQL aggiornamentoDAO = new AggiornamentoDAO_MySQL(dataLayer);
                
                dataLayer.registerDAO(Utente.class, utenteDAO);
                dataLayer.registerDAO(Anagrafica.class, anagraficaDAO);
                dataLayer.registerDAO(Richiesta.class, richiestaDAO);
                dataLayer.registerDAO(DescRichiesta.class, descRichiestaDAO);
                dataLayer.registerDAO(Patente.class, patenteDAO);
                dataLayer.registerDAO(Abilita.class, abilitaDAO);
                dataLayer.registerDAO(Credenziali.class, credenzialiDAO);
                dataLayer.registerDAO(Materiale.class, materialeDAO);
                dataLayer.registerDAO(Mezzo.class, mezzoDAO);
                dataLayer.registerDAO(Squadra.class, squadraDAO);
                dataLayer.registerDAO(Missione.class, missioneDAO);
                dataLayer.registerDAO(Aggiornamento.class, aggiornamentoDAO);
                out.println("<p class='success'><b>[OK]</b> DAO inizializzati e registrati nel DataLayer.</p>");

                out.println("<p>3. Test Inserimento Utente (INSERT)... </p>");
                Utente u = utenteDAO.createUtente();
                u.setNomeUtente("usr" + String.valueOf(System.currentTimeMillis()).substring(9));
                u.setAdmin(true); 
                u.setMonteOre(150);
                
                utenteDAO.storeUtente(u);
                out.println("<p class='success'><b>[OK]</b> Utente salvato! ID Generato: " + u.getKey() + "</p>");

                out.println("<p>3b. Test Inserimento ed Associazione Credenziali (INSERT + ASSOCIAZIONE)... </p>");
                Credenziali cb = credenzialiDAO.createCredenziali();
                cb.setEmail("utente" + String.valueOf(System.currentTimeMillis()).substring(10) + "@test.it");
                byte[] mockHash = new byte[64];
                new Random().nextBytes(mockHash);
                cb.setPasswordHash(mockHash);
                credenzialiDAO.storeCredenziali(cb);
                out.println("<p>Credenziali salvate! ID Generato: " + cb.getKey() + ". Collegamento all'utente in corso...</p>");
                
                credenzialiDAO.legaCredenzialiAUtente(cb, u);
                out.println("<p class='success'><b>[OK]</b> Credenziali associate all'utente correttamente nel database (assegna_credenziali).</p>");

                out.println("<p>4. Test Inserimento Anagrafica (INSERT)... </p>");
                Anagrafica a = anagraficaDAO.createAnagrafica();
                a.setNome("TestNome");
                a.setCognome("TestCognome");
                
                String cfUnico = generaCodiceFiscaleRandom();
                a.setCf(cfUnico);
                
                a.setLuogoNasc("Roma");
                a.setDataNasc(LocalDate.of(2000, 5, 15));
                anagraficaDAO.storeAnagrafica(a, u);
                out.println("<p class='success'><b>[OK]</b> Anagrafica salvana con CF Unico (<b>" + cfUnico + "</b>)! ID Generato: " + a.getKey() + "</p>");

                out.println("<p>5. Test Inserimento Richiesta (INSERT)... </p>");
                Richiesta r = richiestaDAO.createRichiesta();
                r.setNome("Emergenza Test");
                r.setEmail("test@email.com");
                r.setIP("127.0.0.1");
                r.setStato(StatoRichiesta.IN_ATTESA);
                r.setString("str" + String.valueOf(System.currentTimeMillis()).substring(5));
                r.setVerificato(true);
                r.setData(LocalDateTime.now());
                richiestaDAO.storeRichiesta(r);
                out.println("<p class='success'><b>[OK]</b> Richiesta salvata! ID Generato: " + r.getKey() + "</p>");

                out.println("<p>6. Test Inserimento DescRichiesta (INSERT)... </p>");
                DescRichiesta dr = descRichiestaDAO.createDescRichiesta();
                dr.setPosizione("Via Roma 10");
                dr.setFoto("foto_test.png");
                dr.setDescrizione("Dettaglio della richiesta di soccorso per test");
                descRichiestaDAO.storeDescRichiesta(dr, r);
                out.println("<p class='success'><b>[OK]</b> DescRichiesta salvata! ID Generato: " + dr.getKey() + "</p>");

                out.println("<p>7. Test Inserimento e Associazione Patente (INSERT + ASSOCIAZIONE)... </p>");
                Patente p = patenteDAO.createPatente();
                p.setNumero("RM" + String.valueOf(System.currentTimeMillis()).substring(7) + "X");
                p.setTipo(TipoPatente.B); 
                patenteDAO.storePatente(p);
                out.println("<p>Patente creata correttamente. ID Generato: " + p.getKey() + ". Collegamento all'utente in corso...</p>");
                
                patenteDAO.legaPatenteAUtente(p, u);
                out.println("<p class='success'><b>[OK]</b> Patente associata correttamente all'utente nel database (assegna_patente).</p>");

                out.println("<p>8. Test Inserimento e Associazione Abilità (INSERT + ASSOCIAZIONE)... </p>");
                Abilita ab = abilitaDAO.createAbilita();
                ab.setDesc("Soccorso " + String.valueOf(System.currentTimeMillis()).substring(7));
                abilitaDAO.storeAbilita(ab);
                out.println("<p>Abilità creata correttamente. ID Generato: " + ab.getKey() + ". Collegamento all'utente in corso...</p>");
                
                abilitaDAO.legaAbilitaAUtente(ab, u);
                out.println("<p class='success'><b>[OK]</b> Abilità associata correttamente all'utente nel database (assegna_abilita).</p>");

                out.println("<p>8b. Test Inserimento Materiale (INSERT)... </p>");
                Materiale m = materialeDAO.createMateriale();
                m.setNome("Defibrillatore");
                m.setDesc("DAA semiautomatico esterno professionale");
                m.setCodMat("DAE" + String.valueOf(System.currentTimeMillis()).substring(10));
                materialeDAO.storeMateriale(m);
                out.println("<p class='success'><b>[OK]</b> Materiale salvato con cod_mat unico (<b>" + m.getCodMat() + "</b>)! ID Generato: " + m.getKey() + "</p>");

                out.println("<p>8c. Test Inserimento Mezzo (INSERT)... </p>");
                Mezzo mz = mezzoDAO.createMezzo();
                mz.setNome("Ambulanza Tipo A");
                mz.setDesc("Unità mobile di rianimazione");
                mz.setTarga("ZA" + String.valueOf(System.currentTimeMillis()).substring(11) + "AA");
                mezzoDAO.storeMezzo(mz);
                out.println("<p class='success'><b>[OK]</b> Mezzo salvato con targa unica (<b>" + mz.getTarga() + "</b>)! ID Generato: " + mz.getKey() + "</p>");

                out.println("<p>8d. Test Inserimento Squadra e Associazione Operatori (INSERT + ASSOCIAZIONE)... </p>");
                Squadra sq = squadraDAO.createSquadra();
                sq.setCapoSquadra(u); 
                squadraDAO.storeSquadra(sq);
                out.println("<p>Squadra creata con successo! ID Generato: " + sq.getKey() + " (Caposquadra ID: " + u.getKey() + ")</p>");
                squadraDAO.aggiungiMembroASquadra(sq, u);
                out.println("<p class='success'><b>[OK]</b> Operatore associato alla squadra correttamente (assegna_squadra).</p>");

                // --- INSERIMENTO DELLA MISSIONE OBBLIGATORIA PER EVITARE L'ERRORE DI COSTRUTTO ---
                out.println("<p>8e. Test Inserimento Missione (INSERT)... </p>");
                Missione mis = missioneDAO.createMissione();
                mis.setObiettivo("Soccorso stradale codice rosso");
                mis.setInizio(LocalDateTime.now());
                mis.setFine(LocalDateTime.now().plusHours(2));
                mis.setCompletata(true);
                mis.setSuccesso(EsitoMissione.SUCCESSO);
                mis.setDurata(Duration.ofHours(2));
                mis.setRichiesta(r);
                mis.setSquadra(sq);
                mis.setAdmin(u);
                
                missioneDAO.storeMissione(mis);
                out.println("<p class='success'><b>[OK]</b> Missione salvata! ID Generato: " + mis.getKey() + "</p>");

                // --- SALVATAGGIO DELL'AGGIORNAMENTO PASSANDO L'ID DELLA MISSIONE DIRETTAMENTE ---
                out.println("<p>8f. Test Inserimento Aggiornamento legato alla Missione (INSERT)... </p>");
                Aggiornamento agg = aggiornamentoDAO.createAggiornamento();
                agg.setTesto("Nuova linea guida per la gestione delle emergenze su strada.");
                agg.setTimestamp(LocalDateTime.now());
                agg.setAdmin(u);

                // Eseguiamo lo store passando esplicitamente la chiave esterna della missione
                aggiornamentoDAO.storeAggiornamento(agg, mis.getKey());
                out.println("<p class='success'><b>[OK]</b> Aggiornamento salvato! ID Generato: " + agg.getKey() + " associato all'admin ID: " + u.getKey() + " e alla missione ID: " + mis.getKey() + "</p>");

                out.println("<p>9. Test Caricamento Relazioni e Cache Pulita (SELECT)... </p>");
                dataLayer.getCache().clear();
                out.println("<p>Cache del DataLayer ripulita con successo.</p>");

                Utente checkUtente = utenteDAO.getUtente(u.getKey());
                out.println("<p>Utente ricaricato da DB: <b>" + checkUtente.getNomeUtente() + "</b></p>");

                CheckCredenziali: {
                    Credenziali checkCred = credenzialiDAO.getCredenzialiByUtente(checkUtente);
                    if (checkCred != null) {
                        out.println("<p class='success'><b>[OK]</b> Lazy Loading Credenziali riuscito! Email estratta: " + checkCred.getEmail() + "</p>");
                    } else {
                        out.println("<p class='error'><b>[FALLITO]</b> Credenziali correlate non trovate.</p>");
                    }
                }

                Anagrafica checkAnagrafica = checkUtente.getAnagrafica();
                if (checkAnagrafica != null) {
                    out.println("<p class='success'><b>[OK]</b> Lazy Loading Anagrafica riuscito! Nome completo: " + checkAnagrafica.getNome() + " " + checkAnagrafica.getCognome() + " [CF caricato: " + checkAnagrafica.getCf() + "]</p>");
                } else {
                    out.println("<p class='error'><b>[FALLITO]</b> Anagrafica correlata non trovata.</p>");
                }

                Richiesta checkRichiesta = richiestaDAO.getRichiesta(r.getKey());
                DescRichiesta checkDesc = checkRichiesta.getDescrizioneDettaglio();
                if (checkDesc != null) {
                    out.println("<p class='success'><b>[OK]</b> Lazy Loading DescRichiesta riuscito! Posizione estratta: " + checkDesc.getPosizione() + "</p>");
                } else {
                    out.println("<p class='error'><b>[FALLITO]</b> DescRichiesta correlata non trovata.</p>");
                }

                List<Patente> patentiUtente = patenteDAO.getPatentiByUtente(checkUtente);
                if (patentiUtente != null && !patentiUtente.isEmpty()) {
                    Patente patenteCaricata = patentiUtente.get(0);
                    out.println("<p class='success'><b>[OK]</b> Estrazione Patenti riuscita! Trovate " + patentiUtente.size() + " patenti. Prima patente -> Numero: " + patenteCaricata.getNumero() + ", Tipo: " + patenteCaricata.getTipo() + "</p>");
                } else {
                    out.println("<p class='error'><b>[FALLITO]</b> Nessuna patente associata all'utente nel database.</p>");
                }

                List<Abilita> abilitaUtente = abilitaDAO.getAbilitaByUtente(checkUtente);
                if (abilitaUtente != null && !abilitaUtente.isEmpty()) {
                    Abilita abilitaCaricata = abilitaUtente.get(0);
                    out.println("<p class='success'><b>[OK]</b> Estrazione Abilità riuscita! Trovate " + abilitaUtente.size() + " abilità. Prima abilità -> Desc: " + abilitaCaricata.getDesc() + "</p>");
                } else {
                    out.println("<p class='error'><b>[FALLITO]</b> Nessuna abilità associata all'utente nel database.</p>");
                }

                Materiale checkMateriale = materialeDAO.getMateriale(m.getKey());
                if (checkMateriale != null) {
                    out.println("<p class='success'><b>[OK]</b> Lettura Materiale riuscito! Nome: " + checkMateriale.getNome() + ", Cod: " + checkMateriale.getCodMat() + "</p>");
                } else {
                    out.println("<p class='error'><b>[FALLITO]</b> Impossibile caricare il record Materiale salvato.</p>");
                }

                Mezzo checkMezzo = mezzoDAO.getMezzo(mz.getKey());
                if (checkMezzo != null) {
                    out.println("<p class='success'><b>[OK]</b> Lettura Mezzo riuscito! Nome: " + checkMezzo.getNome() + ", Targa: " + checkMezzo.getTarga() + "</p>");
                } else {
                    out.println("<p class='error'><b>[FALLITO]</b> Impossibile caricare il record Mezzo salvato.</p>");
                }

                Squadra checkSquadra = squadraDAO.getSquadra(sq.getKey());
                if (checkSquadra != null) {
                    Utente checkCapo = checkSquadra.getCapoSquadra();
                    out.println("<p class='success'><b>[OK]</b> Lettura Squadra riuscita! Caposquadra caricato correttamente: " + (checkCapo != null ? checkCapo.getNomeUtente() : "NULL") + "</p>");
                    
                    List<Utente> operatoriSquadra = checkSquadra.getOperatori();
                    if (operatoriSquadra != null && !operatoriSquadra.isEmpty()) {
                        out.println("<p class='success'><b>[OK]</b> Lazy Loading Operatori riuscito! Trovati " + operatoriSquadra.size() + " operatori.</p>");
                    }
                }

                out.println("<p>Estrazione e verifica della Missione inserita da DB...</p>");
                Missione checkMis = missioneDAO.getMissione(mis.getKey());
                if (checkMis != null) {
                    out.println("<p class='success'><b>[OK]</b> Lettura Missione riuscita! Obiettivo: \"" + checkMis.getObiettivo() + "\" Esito: " + checkMis.getEsito() + "</p>");
                }

                out.println("<p>Estrazione e verifica dell'Aggiornamento inserito da DB...</p>");
                Aggiornamento checkAggiornamento = aggiornamentoDAO.getAggiornamento(agg.getKey());
                if (checkAggiornamento != null) {
                    out.println("<p class='success'><b>[OK]</b> Lettura Aggiornamento riuscita! Testo: \"" + checkAggiornamento.getTesto() + "\"</p>");
                }

                utenteDAO.destroy();
                anagraficaDAO.destroy();
                richiestaDAO.destroy();
                descRichiestaDAO.destroy();
                patenteDAO.destroy();
                abilitaDAO.destroy();
                credenzialiDAO.destroy();
                materialeDAO.destroy();
                mezzoDAO.destroy();
                squadraDAO.destroy();
                missioneDAO.destroy();
                aggiornamentoDAO.destroy();

            } catch (SQLException ex) {
                out.println("<p class='error'><b>[ERRORE SQL]</b> Errore di connettività o vincolo del DB: " + ex.getMessage() + "</p>");
                ex.printStackTrace(out);
            } catch (DataException ex) {
                out.println("<p class='error'><b>[ERRORE DATALAYER]</b> Eccezione sollevata dal framework dei DAO: " + ex.getMessage() + "</p>");
                ex.printStackTrace(out);
            } finally {
                if (dataLayer != null) {
                    dataLayer.destroy();
                }
            }

            out.println("<hr/>");
            out.println("</body>");
            out.println("</html>");
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
        return "AServlet aggiornata per includere l'entità Missione integrata con la tabella Aggiornamenti";
    }
}