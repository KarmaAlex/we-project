package org.dellapenna.we.test;

import org.dellapenna.we.data.dao.impl.UtenteDAO_MySQL;
import org.dellapenna.we.data.dao.impl.AnagraficaDAO_MySQL;
import org.dellapenna.we.data.dao.impl.RichiestaDAO_MySQL;
import org.dellapenna.we.data.dao.impl.DescRichiestaDAO_MySQL;
import org.dellapenna.we.data.dao.impl.PatenteDAO_MySQL;
import org.dellapenna.we.data.dao.impl.AbilitaDAO_MySQL;
import org.dellapenna.we.data.DataLayer;
import org.dellapenna.we.data.DataException;
import org.dellapenna.we.model.Utente;
import org.dellapenna.we.model.Anagrafica;
import org.dellapenna.we.model.Richiesta;
import org.dellapenna.we.model.DescRichiesta;
import org.dellapenna.we.model.Patente;
import org.dellapenna.we.model.Abilita;
import org.dellapenna.we.model.enums.StatoRichiesta;
import org.dellapenna.we.model.enums.TipoPatente;

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

            out.println("<h2>Esecuzione Test dei DAO con Relazioni, Credenziali, Patente e Abilità su MySQL</h2>");
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
                
                dataLayer.registerDAO(Utente.class, utenteDAO);
                dataLayer.registerDAO(Anagrafica.class, anagraficaDAO);
                dataLayer.registerDAO(Richiesta.class, richiestaDAO);
                dataLayer.registerDAO(DescRichiesta.class, descRichiestaDAO);
                dataLayer.registerDAO(Patente.class, patenteDAO);
                dataLayer.registerDAO(Abilita.class, abilitaDAO);
                out.println("<p class='success'><b>[OK]</b> DAO inizializzati e registrati nel DataLayer.</p>");

                out.println("<p>3. Test Inserimento Utente con Credenziali (INSERT)... </p>");
                Utente u = utenteDAO.createUtente();
                u.setNomeUtente("usr" + String.valueOf(System.currentTimeMillis()).substring(9));
                u.setAdmin(false);
                u.setMonteOre(150);
                u.setEmail("utente" + String.valueOf(System.currentTimeMillis()).substring(10) + "@test.it");
                u.setHashedPassword("7528852395684348576238475263485726348572364857236485723648572364");
                
                utenteDAO.storeUtente(u);
                out.println("<p class='success'><b>[OK]</b> Utente salvato! ID Generato: " + u.getKey() + "</p>");

                out.println("<p>4. Test Inserimento Anagrafica (INSERT)... </p>");
                Anagrafica a = anagraficaDAO.createAnagrafica();
                a.setNome("TestNome");
                a.setCognome("TestCognome");
                
                String cfUnico = generaCodiceFiscaleRandom();
                a.setCf(cfUnico);
                
                a.setLuogoNasc("Roma");
                a.setDataNasc(LocalDate.of(2000, 5, 15));
                anagraficaDAO.storeAnagrafica(a, u);
                out.println("<p class='success'><b>[OK]</b> Anagrafica salvata con CF Unico (<b>" + cfUnico + "</b>)! ID Generato: " + a.getKey() + "</p>");

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

                out.println("<p>9. Test Caricamento Relazioni e Cache Pulita (SELECT)... </p>");
                dataLayer.getCache().clear();
                out.println("<p>Cache del DataLayer ripulita con successo.</p>");

                Utente checkUtente = utenteDAO.getUtente(u.getKey());
                out.println("<p>Utente ricaricato da DB: <b>" + checkUtente.getNomeUtente() + "</b></p>");

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

                out.println("<p>Estrazione delle patenti associate all'utente ricaricato...</p>");
                List<Patente> patentiUtente = patenteDAO.getPatentiByUtente(checkUtente);
                if (patentiUtente != null && !patentiUtente.isEmpty()) {
                    Patente patenteCaricata = patentiUtente.get(0);
                    out.println("<p class='success'><b>[OK]</b> Estrazione Patenti riuscita! Trovate " + patentiUtente.size() + " patenti. Prima patente -> Numero: " + patenteCaricata.getNumero() + ", Tipo: " + patenteCaricata.getTipo() + "</p>");
                } else {
                    out.println("<p class='error'><b>[FALLITO]</b> Nessuna patente associata all'utente nel database.</p>");
                }

                out.println("<p>Estrazione delle abilità associate all'utente ricaricato...</p>");
                List<Abilita> abilitaUtente = abilitaDAO.getAbilitaByUtente(checkUtente);
                if (abilitaUtente != null && !abilitaUtente.isEmpty()) {
                    Abilita abilitaCaricata = abilitaUtente.get(0);
                    out.println("<p class='success'><b>[OK]</b> Estrazione Abilità riuscita! Trovate " + abilitaUtente.size() + " abilità. Prima abilità -> Nome: " + abilitaCaricata.getNome() + ", Desc: " + abilitaCaricata.getDesc() + "</p>");
                } else {
                    out.println("<p class='error'><b>[FALLITO]</b> Nessuna abilità associata all'utente nel database.</p>");
                }

                utenteDAO.destroy();
                anagraficaDAO.destroy();
                richiestaDAO.destroy();
                descRichiestaDAO.destroy();
                patenteDAO.destroy();
                abilitaDAO.destroy();

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
            out.println("<h2>Request Diagnostics Originali</h2>");
            out.println("<ul>");
            out.println("<li><strong>Request URL</strong>: " + request.getRequestURL() + "</li>");
            out.println("<li><strong>Request Context</strong>: " + request.getContextPath() + "</li>");
            out.println("<li><strong>Request Timestamp</strong>: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + "</li>");
            out.println("</ul>");

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
        return "AServlet aggiornata per il corretto testing dei DAO relazionali con controllo credenziali, patenti e abilità";
    }
}