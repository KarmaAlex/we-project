package org.dellapenna.we.test;

import org.dellapenna.we.data.dao.impl.UtenteDAO_MySQL;
import org.dellapenna.we.data.dao.impl.AnagraficaDAO_MySQL;
import org.dellapenna.we.data.DataLayer;
import org.dellapenna.we.data.DataException;
import org.dellapenna.we.model.Utente;
import org.dellapenna.we.model.Anagrafica;

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

/**
 *
 * @author GDP
 */
public class AServlet extends HttpServlet {

    // Configura i parametri reali del tuo schema su MySQL Workbench
    private static final String DB_SERVER = "localhost";
    private static final int DB_PORT = 3306;
    private static final String DB_NAME = "soccorso"; 
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet " + getServletName() + " - DAO Test</title>");
            out.println("<style>body { font-family: sans-serif; padding: 20px; } .success { color: green; } .error { color: red; }</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>This is servlet <em>" + getServletName() + "</em> (implemented in class <em>" + getClass().getSimpleName() + "</em>)" + "</h1>");

            out.println("<h2>Esecuzione Test dei DAO su MySQL</h2>");
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

                // Creazione del DataLayer passando il DataSource[cite: 5]
                dataLayer = new DataLayer(dataSource);
                out.println("<p class='success'><b>[OK]</b> DataLayer istanziato correttamente con il DataSource.</p>");

                // Registrazione e inizializzazione dei DAO concreti
                out.println("<p>2. Inizializzazione dei DAO... </p>");
                UtenteDAO_MySQL utenteDAO = new UtenteDAO_MySQL(dataLayer);
                AnagraficaDAO_MySQL anagraficaDAO = new AnagraficaDAO_MySQL(dataLayer);
                
                // Registrazione tramite il metodo nativo del tuo DataLayer[cite: 5]
                dataLayer.registerDAO(Utente.class, utenteDAO);
                dataLayer.registerDAO(Anagrafica.class, anagraficaDAO);
                out.println("<p class='success'><b>[OK]</b> DAO inizializzati e registrati nel DataLayer.</p>");

                out.println("<p>3. Test Inserimento Utente (INSERT)... </p>");
                Utente u = utenteDAO.createUtente();
                u.setNomeUtente("usr" + String.valueOf(System.currentTimeMillis()).substring(9));
                u.setAdmin(false);
                u.setMonteOre(150);
                utenteDAO.storeUtente(u);
                out.println("<p class='success'><b>[OK]</b> Utente salvato! ID Generato: " + u.getKey() + "</p>");

                out.println("<p>4. Test Inserimento Anagrafica (INSERT)... </p>");
                Anagrafica a = anagraficaDAO.createAnagrafica();
                a.setNome("TestNome");
                a.setCognome("TestCognome");
                a.setCf("UXTNME00B01H501M");
                a.setLuogoNasc("Roma");
                a.setDataNasc(LocalDate.of(2000, 5, 15));
                anagraficaDAO.storeAnagrafica(a, u);
                out.println("<p class='success'><b>[OK]</b> Anagrafica salvata! ID Generato: " + a.getKey() + "</p>");

                out.println("<p>5. Test Lazy Loading con Cache Pulita (SELECT)... </p>");

                // 1. Svuota la cache interna
                dataLayer.getCache().clear();
                out.println("<p>Cache del DataLayer ripulita con successo.</p>");

                // 2. Ricarica l'utente dal database (ora sarà un Proxy pulito)
                Utente checkUtente = utenteDAO.getUtente(u.getKey());
                out.println("<p>Utente ricaricato da DB: " + checkUtente.getNomeUtente() + ". Estrazione dell'Anagrafica correlata tramite Proxy...</p>");

                // 3. Verifica se il Lazy Loading estrae correttamente l'anagrafica
                Anagrafica checkAnagrafica = checkUtente.getAnagrafica();
                if (checkAnagrafica != null) {
                    out.println("<p class='success'><b>[OK]</b> Lazy Loading riuscito grazie alla pulizia della cache! Nome estratto: " + checkAnagrafica.getNome() + " " + checkAnagrafica.getCognome() + "</p>");
                } else {
                    out.println("<p class='error'><b>[FALLITO]</b> L'anagrafica associata è risultata ancora NULL. Controlla l'inizializzazione del Proxy in UtenteDAO_MySQL.</p>");
                }

                utenteDAO.destroy();
                anagraficaDAO.destroy();

            } catch (SQLException ex) {
                out.println("<p class='error'><b>[ERRORE SQL]</b> Errore di connettività o vincolo del DB: " + ex.getMessage() + "</p>");
                ex.printStackTrace(out);
            } catch (DataException ex) {
                out.println("<p class='error'><b>[ERRORE DATALAYER]</b> Eccezione sollevata dal framework dei DAO: " + ex.getMessage() + "</p>");
                ex.printStackTrace(out);
            } finally {
                if (dataLayer != null) {
                    dataLayer.destroy(); // Distruzione sicura tramite il metodo nativo del tuo DataLayer[cite: 5]
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
        return "AServlet aggiornata per il corretto testing dei DAO relazionali";
    }
}