/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.soccorsoweb.framework.security;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.soccorsoweb.model.Anagrafica;
/**
 *
 * @author Aurora
 */
public class  SecurityHelpers {

    private static final String CAPTCHA_SUM = "home.captcha.sum";
    private static final String CAPTCHA_EXPIRES = "home.captcha.expires";
    private static final SecureRandom CAPTCHA_RANDOM = new SecureRandom();
    private static final Duration CAPTCHA_LIFETIME = Duration.ofMinutes(10);

    public static int[] createCaptcha(HttpServletRequest request) {
        int first = 10 + CAPTCHA_RANDOM.nextInt(90);
        int second = 10 + CAPTCHA_RANDOM.nextInt(90);
        HttpSession session = request.getSession(true);
        session.setAttribute(CAPTCHA_SUM, first + second);
        session.setAttribute(CAPTCHA_EXPIRES, LocalDateTime.now().plus(CAPTCHA_LIFETIME));
        return new int[]{first, second};
    }

    public static boolean consumeCaptcha(HttpServletRequest request, String answer) {
        HttpSession session = request.getSession(false);
        Object expected = session == null ? null : session.getAttribute(CAPTCHA_SUM);
        Object expires = session == null ? null : session.getAttribute(CAPTCHA_EXPIRES);

        if (session != null) {
            session.removeAttribute(CAPTCHA_SUM);
            session.removeAttribute(CAPTCHA_EXPIRES);
        }

        if (!(expected instanceof Integer) || !(expires instanceof LocalDateTime)
                || answer == null || !answer.matches("\\d+")) {
            return false;
        }
        try {
            return !LocalDateTime.now().isAfter((LocalDateTime) expires)
                    && Integer.parseInt(answer) == (Integer) expected;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static HttpSession checkSession(HttpServletRequest r) {
        return checkSession(r, false);
    }

    public static HttpSession checkOrCreateSession(HttpServletRequest r){
        HttpSession session = checkSession(r,false);
        return session != null ? session : createAnonymousSession(r);
    }

    public static HttpSession checkSession(HttpServletRequest r, boolean loginAgeCheck) {
        boolean check = true;

        HttpSession s = r.getSession(false);
        //per prima cosa vediamo se la sessione è attiva
        //first, let's see is the sessione is active
        if (s == null) {
            return null;
        }
        //data/ora correnti
        //current timestamp
        LocalDateTime now_ts = LocalDateTime.now();
        //inizio sessione
        //session start timestamp
        LocalDateTime start_ts = (LocalDateTime) s.getAttribute("session-start-ts");
        //ultima azione
        //last action timestamp
        LocalDateTime action_ts = (LocalDateTime) s.getAttribute("last-action-ts");
        if (action_ts == null) {
            action_ts = now_ts;
        }
        //ultima rigenerazione dell'ID
        //last session ID regeneration timestamp
        LocalDateTime refresh_ts = (LocalDateTime) s.getAttribute("session-refresh-ts");
        if (refresh_ts == null) {
            refresh_ts = start_ts;
        }
        //secondi trascorsi dall'inizio della sessione
        //seconds from the session start           
        long seconds_from_start = start_ts != null ? Duration.between(start_ts, now_ts).abs().getSeconds() : 0;
        //secondi trascorsi dall'ultima azione
        //seconds from the last valid action
        long seconds_from_action = Duration.between(action_ts, now_ts).abs().getSeconds();
        //secondi trascorsi dall'ultimo refresh della sessione
        //seconds from the last session refresh
        long seconds_from_refresh = Duration.between(refresh_ts, now_ts).abs().getSeconds();
        Boolean authBool = (Boolean)s.getAttribute("authenticated");
        boolean auth = authBool != null ? authBool.booleanValue() : false;
        Object uid = s.getAttribute("userid");
        //
        if ((uid == null && auth == true) || start_ts == null) {
            //check sulla validità della sessione
            //second, check is the session contains valid data
            //nota: oltre a controllare se la sessione contiene un userid, 
            //dovremmo anche controllere che lo userid sia valido, probabilmente 
            //consultando il database utenti
            //note: besides checking if the session contains an userid, we should 
            //check if the userid is valid, possibly querying the user database
            check = false;
        } else if ((s.getAttribute("ip") == null) || !((String) s.getAttribute("ip")).equals(r.getRemoteHost())) {
            //check sull'ip del client
            //check if the client ip chaged
            check = false;
        } else if (seconds_from_start > 3 * 60 * 60) {
            //dopo tre ore la sessione scade
            //after three hours the session is invalidated
            check = false;
        } else if (seconds_from_action > 30 * 60) {
            //dopo trenta minuti dall'ultima operazione la sessione è invalidata
            //after 30 minutes since the last action the session is invalidated                    
            check = false;
        }
        //
        if (!check) {
            s.invalidate();
            return null;
        } else {
            //ogni 120 secondi, rigeneriamo la sessione per cambiarne l'ID
            //every 120 seconds, we regenerate the session to change its ID
            if (seconds_from_refresh >= 120) {
                s = regenerateSession(r);
                s.setAttribute("session-refresh-ts", now_ts);
            }
            //reimpostiamo la data/ora dell'ultima azione
            //if che checks are ok, update the last action timestamp
            s.setAttribute("last-action-ts", now_ts);
            return s;
        }
    }

    public static HttpSession createSession(HttpServletRequest request, String username, int userid) {
        //se una sessione è già attiva, rimuoviamola e creiamone una nuova
        //if a session already exists, remove it and recreate a new one
        disposeSession(request);
        HttpSession s = request.getSession(true);
        s.setAttribute("username", username);
        s.setAttribute("userid", userid);
        //
        s.setAttribute("ip", request.getRemoteHost());
        //
        s.setAttribute("session-start-ts", LocalDateTime.now());
        return s;
    }

    public static HttpSession createAnonymousSession(HttpServletRequest request){
        disposeSession(request);
        HttpSession s = request.getSession();
        s.setAttribute("ip", request.getRemoteHost());
        s.setAttribute("session-start-ts", LocalDateTime.now());
        return s;
    }

    public static void disposeSession(HttpServletRequest request) {
        HttpSession s = request.getSession(false);
        if (s != null) {
            s.invalidate();
        }
    }

    //questo metodo rigenera la sessione invalidando quella corrente e
    //creandone una nuova con gli stessi attributi. Può essere utile per 
    //prevenire il session hijacking, perchè modifica il session identifier
    //this method regenerates the session by invalidating the current one
    //and creating a new one with the same attributes. It may be useful
    //to prevent session hijacking, since it changes the session identifier
    public static HttpSession regenerateSession(HttpServletRequest request) {
        Map<String, Object> attributes = new HashMap<>();
        HttpSession s = request.getSession(false);
        if (s != null) {
            Enumeration<String> attributeNames = s.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String key = attributeNames.nextElement();
                Object value = s.getAttribute(key);
                attributes.put(key, value);
            }
            s.invalidate();
        }
        s = request.getSession(true);
        for (String key : attributes.keySet()) {
            Object value = attributes.get(key);
            s.setAttribute(key, value);
        }
        return s;
    }

    //--------- CONNECTION SECURITY ------------
    public static String checkHttps(HttpServletRequest request) {
        //possiamo usare questa tecnica per controllare se la richiesta è
        //stata effettuata in https e, in caso contrario, costruire la URL
        //necessaria a ridirezionare il browser verso l'https
        //we can use this technique to check if the request was made in https 
        //and, if not, build the URL needed to redirect the browser to https
        if (request.getScheme().equals("http")) {
            String url = "https://" + request.getServerName()
                    + ":" + request.getServerPort()
                    + request.getRequestURI() //request.getContextPath() + request.getServletPath() +  (request.getPathInfo() != null) ? request.getPathInfo() : ""
                    + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
            return url;
        } else {
            return null;
        }
    }

    //--------- DATA SECURITY ------------
    //questa funzione aggiunge un backslash davanti a
    //tutti i caratteri "pericolosi", usati per eseguire
    //SQL injection attraverso i parametri delle form
    //this function adds backslashes in front of
    //all the "malicious" charcaters, usually exploited
    //to perform SQL injection through form parameters
    public static String addSlashes(String s) {
        if (s == null) {
            return "";
        }
        return s.replaceAll("(['\"\\\\])", "\\\\$1");
    }

    public static String sanitizeTextInput(String value) {
        return value == null ? "" : value.trim();
    }

    public static boolean isValidBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        return !birthDate.isAfter(today.minusYears(4))
                && !birthDate.isBefore(today.minusYears(105));
    }

    public static String sanitizeFileLink(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return sanitizeFilename(value).replace('\\', '/');
    }

    public static String buildRequestSignature(String... values) {
        String raw = String.join("|", values);
        return md5Hex(raw);
    }

    public static String md5Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            return String.valueOf(value.hashCode());
        }
    }

    //questa funzione rimuove gli slash aggiunti da addSlashes
    //this function removes the slashes added by addSlashes
    public static String stripSlashes(String s) {
        return s.replaceAll("\\\\(['\"\\\\])", "$1");
    }

    public static boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        Object adminAttr = session.getAttribute("admin");
        if (adminAttr instanceof Boolean) {
            return Boolean.TRUE.equals(adminAttr);
        }

        Object role = session.getAttribute("ruolo");
        return role != null && "ADMIN".equalsIgnoreCase(String.valueOf(role));
    }

    public static boolean isOperator(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        if (isAdmin(request)) {
            return false;
        }

        Object adminAttr = session.getAttribute("admin");
        if (adminAttr instanceof Boolean) {
            return !Boolean.TRUE.equals(adminAttr);
        }

        Object role = session.getAttribute("ruolo");
        return role != null && "OPERATOR".equalsIgnoreCase(String.valueOf(role));
    }

    public static boolean isIncompleteRegistration(Anagrafica anagrafica) {
        if (anagrafica == null) {
            return true;
        }

        return isBlank(anagrafica.getNome())
                || isBlank(anagrafica.getCognome())
                || isBlank(anagrafica.getCf())
                || isBlank(anagrafica.getLuogoNasc())
                || anagrafica.getDataNasc() == null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static int checkNumeric(String s) throws NumberFormatException {
        //convertiamo la stringa in numero, ma assicuriamoci prima che sia valida
        //convert the string to a number, ensuring its validity
        if (s != null) {
            //se la conversione fallisce, viene generata un'eccezione
            //if the conversion fails, an exception is raised
            return Integer.parseInt(s);
        } else {
            throw new NumberFormatException("String argument is null");
        }
    }

    public static String sanitizeFilename(String name) {
        if (name == null) {
            return "";
        }
        String normalized = name.replace('\\', '/');
        int lastSeparator = normalized.lastIndexOf('/');
        String fileName = lastSeparator >= 0 ? normalized.substring(lastSeparator + 1) : normalized;
        return fileName.replaceAll("[^a-zA-Z0-9_.-]", "_");
    }

    //--------- PASSWORD SECURITY ------------
    //support functions for the password hashing functions
    private static String bytesToHexString(byte[] byteArray) {
        StringBuilder hexStringBuffer = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            char[] hexDigits = new char[2];
            hexDigits[0] = Character.forDigit((byteArray[i] >> 4) & 0xF, 16);
            hexDigits[1] = Character.forDigit((byteArray[i] & 0xF), 16);
            hexStringBuffer.append(new String(hexDigits));
        }
        return hexStringBuffer.toString();
    }
    
    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] hexStringToBytes(String hexString) {
        byte[] byteArray = new byte[hexString.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {
            int val = Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16);
            byteArray[i] = (byte) val;
        }
        return byteArray;
    }

    //password hashing with SHA-512 + salt
    private static String getPasswordHashSHA(String password, byte[] salt) throws NoSuchAlgorithmException {
        if (salt.length != 16) {
            throw new IllegalArgumentException("Salt must be 16 bytes");
        }
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);
        md.update(password.getBytes());
        byte[] digest = md.digest();
        return bytesToHexString(salt) + bytesToHexString(digest);
    }

    public static String getPasswordHashSHA(String password) throws NoSuchAlgorithmException {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return getPasswordHashSHA(password, salt);
    }

    //check password hashed by getPasswordHashSHA
    public static boolean checkPasswordHashSHA(String password, String passwordhash) throws NoSuchAlgorithmException {
        byte[] salt = new byte[16];
        System.arraycopy(hexStringToBytes(passwordhash), 0, salt, 0, 16);
        return getPasswordHashSHA(password, salt).equals(passwordhash);
    }

    //password hashing with PBKDF2 + salt
    private static String getPasswordHashPBKDF2(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (salt.length != 16) {
            throw new IllegalArgumentException("Salt must be 16 bytes");
        }
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        byte[] digest = factory.generateSecret(spec).getEncoded();
        return bytesToHexString(salt) + bytesToHexString(digest);
    }

    public static String getPasswordHashPBKDF2(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return getPasswordHashPBKDF2(password, salt);
    }

    //check password hashed by getPasswordHashPBKDF2
    public static boolean checkPasswordHashPBKDF2(String password, String passwordhash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = new byte[16];
        System.arraycopy(hexStringToBytes(passwordhash), 0, salt, 0, 16);
        return (getPasswordHashPBKDF2(password, salt)).equals(passwordhash);
    }

private static final String CSRF_SESSION_ATTR = "csrfToken";

public static String createCsrfToken(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    byte[] bytes = new byte[32];
    new SecureRandom().nextBytes(bytes);
    String token = toHexString(bytes);
    session.setAttribute(CSRF_SESSION_ATTR, token);   // sovrascrive sempre il precedente
    return token;
}

public static boolean isValidCsrfToken(HttpServletRequest request, String submittedToken) {
    HttpSession session = request.getSession(false);
    if (session == null || submittedToken == null || submittedToken.isBlank()) {
        return false;
    }
    String sessionToken = (String) session.getAttribute(CSRF_SESSION_ATTR);
    boolean valid = sessionToken != null && MessageDigest.isEqual(
            sessionToken.getBytes(StandardCharsets.UTF_8),
            submittedToken.getBytes(StandardCharsets.UTF_8)
    );
    session.removeAttribute(CSRF_SESSION_ATTR);   // monouso: valido o no, va scartato dopo il controllo
    return valid;
}

private static final Map<String, LocalDateTime> ULTIMA_RICHIESTA_PER_IP = new ConcurrentHashMap<>();
private static final long RICHIESTA_COOLDOWN_SECONDS = 60;
public static boolean isRichiestaRateLimited(HttpServletRequest request) {
    String ip = request.getRemoteHost();
    LocalDateTime now = LocalDateTime.now();

    LocalDateTime ultima = ULTIMA_RICHIESTA_PER_IP.get(ip);
    if (ultima != null && Duration.between(ultima, now).getSeconds() < RICHIESTA_COOLDOWN_SECONDS) {
        return true;
    }

    ULTIMA_RICHIESTA_PER_IP.put(ip, now);
    return false;
}
}
