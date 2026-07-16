package org.soccorsoweb.test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * MockDataProvider: Fornisce dati finti per test dashboard
 */
public class MockDataProvider {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static List<Map<String, Object>> getMockRichieste() {
        List<Map<String, Object>> richieste = new ArrayList<>();
        
        Map<String, Object> r1 = new HashMap<>();
        r1.put("id", "REQ-001");
        r1.put("segnalante", "Mario Rossi");
        r1.put("email", "mario@example.com");
        r1.put("indirizzo", "Via Roma 10, Milano");
        r1.put("stato", "ATTIVA");
        r1.put("data_creazione", "2026-07-04 14:30");
        r1.put("descrizione", "Incidente stradale con 2 feriti");
        r1.put("coordinate", "45.4642, 9.1900");
        r1.put("foto_url", null);
        r1.put("editable", "ATTIVA".equals(r1.get("stato")));
        richieste.add(r1);

        Map<String, Object> r2 = new HashMap<>();
        r2.put("id", "REQ-002");
        r2.put("segnalante", "Giulia Verdi");
        r2.put("email", "giulia@example.com");
        r2.put("indirizzo", "Via Milano 5, Roma");
        r2.put("stato", "CHIUSA");
        r2.put("data_creazione", "2026-07-03 10:15");
        r2.put("descrizione", "Persona svenuta in strada");
        r2.put("coordinate", "41.9028, 12.4964");
        r2.put("foto_url", null);
        r2.put("editable", "ATTIVA".equals(r2.get("stato")));
        richieste.add(r2);

        Map<String, Object> r3 = new HashMap<>();
        r3.put("id", "REQ-003");
        r3.put("segnalante", "Luca Neri");
        r3.put("email", "luca@example.com");
        r3.put("indirizzo", "Piazza Duomo, Milano");
        r3.put("stato", "IGNORATA");
        r3.put("data_creazione", "2026-07-02 09:20");
        r3.put("descrizione", "Allarme falso");
        r3.put("coordinate", "45.4642, 9.1900");
        r3.put("foto_url", null);
        r3.put("editable", "ATTIVA".equals(r3.get("stato")));
        richieste.add(r3);

        return richieste;
    }

    public static List<Map<String, Object>> getMockMissioni() {
        List<Map<String, Object>> missioni = new ArrayList<>();

        Map<String, Object> m1 = new HashMap<>();
        m1.put("id", "MISS-001");
        m1.put("richiesta_id", "REQ-001");
        m1.put("squadra", "SQU-001");
        m1.put("obiettivo", "Soccorso incidente stradale");
        m1.put("stato", "IN_CORSO");
        m1.put("data_inizio", "2026-07-04 14:45");
        m1.put("data_fine", "");
        m1.put("mezzi", Arrays.asList("Ambulanza A1", "Auto Soccorso"));
        m1.put("materiali", Arrays.asList("Kit medico base", "Barella"));
        m1.put("editable", "IN_CORSO".equals(m1.get("stato")));
        missioni.add(m1);

        Map<String, Object> m2 = new HashMap<>();
        m2.put("id", "MISS-002");
        m2.put("richiesta_id", "REQ-002");
        m2.put("squadra", "SQU-003");
        m2.put("obiettivo", "Persona svenuta");
        m2.put("stato", "CHIUSA");
        m2.put("data_inizio", "2026-07-03 10:30");
        m2.put("data_fine", "2026-07-03 11:15");
        m2.put("mezzi", Arrays.asList("Ambulanza B2"));
        m2.put("materiali", Arrays.asList("Kit medico base"));
        m2.put("editable", "IN_CORSO".equals(m2.get("stato")));
        missioni.add(m2);

        return missioni;
    }

    public static List<Map<String, Object>> getMockOperatori() {
        List<Map<String, Object>> operatori = new ArrayList<>();

        Map<String, Object> o1 = new HashMap<>();
        o1.put("id", "OP-001");
        o1.put("nome", "Marco Bianchi");
        o1.put("email", "marco@soccorsoweb.it");
        o1.put("telefono", "+39 333 123 4567");
        o1.put("stato", "OCCUPATO");
        o1.put("missione_corrente", "MISS-001");
        o1.put("patenti", Arrays.asList("A", "B", "C"));
        o1.put("abilita", Arrays.asList("Soccorritore", "Autista"));
        o1.put("editable", true);
        operatori.add(o1);

        Map<String, Object> o2 = new HashMap<>();
        o2.put("id", "OP-002");
        o2.put("nome", "Francesca Giallo");
        o2.put("email", "francesca@soccorsoweb.it");
        o2.put("telefono", "+39 333 987 6543");
        o2.put("stato", "DISPONIBILE");
        o2.put("missione_corrente", "");
        o2.put("patenti", Arrays.asList("B"));
        o2.put("abilita", Arrays.asList("Infermiera"));
        operatori.add(o2);

        Map<String, Object> o3 = new HashMap<>();
        o3.put("id", "OP-003");
        o3.put("nome", "Paolo Neri");
        o3.put("email", "paolo@soccorsoweb.it");
        o3.put("telefono", "+39 333 555 6789");
        o3.put("stato", "OCCUPATO");
        o3.put("missione_corrente", "MISS-002");
        o3.put("patenti", Arrays.asList("B", "C"));
        o3.put("abilita", Arrays.asList("Soccorritore"));
        operatori.add(o3);

        return operatori;
    }

    public static List<Map<String, Object>> getMockMezzi() {
        List<Map<String, Object>> mezzi = new ArrayList<>();

        Map<String, Object> v1 = new HashMap<>();
        v1.put("id", "VEH-001");
        v1.put("nome", "Ambulanza A1");
        v1.put("descrizione", "Ambulanza di tipo B");
        v1.put("targa", "AA123BB");
        v1.put("stato", "OCCUPATO");
        v1.put("missione_corrente", "MISS-001");
        v1.put("editable", true);
        mezzi.add(v1);

        Map<String, Object> v2 = new HashMap<>();
        v2.put("id", "VEH-002");
        v2.put("nome", "Auto Soccorso");
        v2.put("descrizione", "Furgone con attrezzature");
        v2.put("targa", "BB456CC");
        v2.put("stato", "DISPONIBILE");
        v2.put("missione_corrente", "");
        v2.put("editable", true);
        mezzi.add(v2);

        Map<String, Object> v3 = new HashMap<>();
        v3.put("id", "VEH-003");
        v3.put("nome", "Ambulanza B2");
        v3.put("descrizione", "Ambulanza di tipo B");
        v3.put("targa", "CC789DD");
        v3.put("stato", "DISPONIBILE");
        v3.put("missione_corrente", "");
        mezzi.add(v3);

        return mezzi;
    }

    public static List<Map<String, Object>> getMockMateriali() {
        List<Map<String, Object>> materiali = new ArrayList<>();

        Map<String, Object> m1 = new HashMap<>();
        m1.put("id", "MAT-001");
        m1.put("nome", "Kit Medico Base");
        m1.put("descrizione", "Kit di primo soccorso completo");
        m1.put("quantita", 5);
        m1.put("stato", "OCCUPATO");
        m1.put("idMissione", "MISS-001");
        m1.put("editable", true);
        materiali.add(m1);

        Map<String, Object> m2 = new HashMap<>();
        m2.put("id", "MAT-002");
        m2.put("nome", "Barella Pieghevole");
        m2.put("descrizione", "Barella portatile");
        m2.put("quantita", 10);
        m2.put("stato", "DISPONIBILE");
        m2.put("idMissione", "");
        m2.put("editable", true);
        materiali.add(m2);

        Map<String, Object> m3 = new HashMap<>();
        m3.put("id", "MAT-003");
        m3.put("nome", "Estintore");
        m3.put("descrizione", "Estintore polvere 6kg");
        m3.put("quantita", 8);
        m3.put("stato", "DISPONIBILE");
        m3.put("idMissione", "");
        materiali.add(m3);

        return materiali;
    }

    public static List<Map<String, Object>> getMockSquadre() {
        List<Map<String, Object>> squadre = new ArrayList<>();

        Map<String, Object> s1 = new HashMap<>();
        s1.put("id", "SQU-001");
        s1.put("caposquadra", "Tizio");
        s1.put("membri", List.of("Tizio", "Caio", "Sempronio"));
        s1.put("idMissione", "MISS-001");
        s1.put("editable", true);
        squadre.add(s1);

        Map<String, Object> s2 = new HashMap<>();
        s2.put("id", "SQU-002");
        s2.put("caposquadra", "Caio");
        s2.put("membri", List.of("Caio", "Sempronio"));
        s2.put("idMissione", "MISS-002");
        s2.put("editable", true);
        squadre.add(s2);

        Map<String, Object> s3 = new HashMap<>();
        s3.put("id", "SQU-003");
        s3.put("caposquadra", "Sempronio");
        s3.put("membri", List.of("Tizio", "Caio"));
        s3.put("idMissione", "MISS-001");
        s3.put("editable", true);
        squadre.add(s3);

        return squadre;
    }

    public static Map<String, Object> getRichiestaDetail(String id) {
        Map<String, Object> richiesta = new HashMap<>();
        richiesta.put("id", id);
        richiesta.put("segnalante", "Mario Rossi");
        richiesta.put("email", "mario@example.com");
        richiesta.put("indirizzo", "Via Roma 10, Milano");
        richiesta.put("coordinate", "45.4642, 9.1900");
        richiesta.put("stato", "ATTIVA");
        richiesta.put("data_creazione", "2026-07-04 14:30");
        richiesta.put("descrizione", "Incidente stradale con 2 feriti. Auto ribaltata.");
        richiesta.put("foto_url", null);
        richiesta.put("verificato", true);
        return richiesta;
    }

    public static Map<String, Object> getMissioneDetail(String id) {
        Map<String, Object> missione = new HashMap<>();
        missione.put("id", id);
        missione.put("richiesta_id", "REQ-001");
        missione.put("squadra", "Squadra A");
        missione.put("caposquadra", "Marco Bianchi");
        missione.put("obiettivo", "Soccorso incidente stradale");
        missione.put("indirizzo", "Via Roma 10, Milano");
        missione.put("stato", "IN_CORSO");
        missione.put("data_inizio", "2026-07-04 14:45");
        missione.put("data_fine", "");

        List<Map<String, Object>> operatori = new ArrayList<>();
        Map<String, Object> op1 = new HashMap<>();
        op1.put("nome", "Marco Bianchi");
        op1.put("ruolo", "Caposquadra");
        operatori.add(op1);
        Map<String, Object> op2 = new HashMap<>();
        op2.put("nome", "Francesca Giallo");
        op2.put("ruolo", "Operatore");
        operatori.add(op2);
        missione.put("operatori", operatori);

        missione.put("mezzi", Arrays.asList("Ambulanza A1", "Auto Soccorso"));
        missione.put("materiali", Arrays.asList("Kit medico base", "Barella"));

        List<Map<String, Object>> aggiornamenti = new ArrayList<>();
        Map<String, Object> agg1 = new HashMap<>();
        agg1.put("timestamp", "2026-07-04 14:45");
        agg1.put("title", "Missione avviata");
        agg1.put("content", "Squadra partita dalla sede");
        aggiornamenti.add(agg1);
        Map<String, Object> agg2 = new HashMap<>();
        agg2.put("timestamp", "2026-07-04 14:55");
        agg2.put("title", "Arrivo al luogo");
        agg2.put("content", "Squadra arrivata al luogo dell'incidente");
        aggiornamenti.add(agg2);
        Map<String, Object> agg3 = new HashMap<>();
        agg3.put("timestamp", "2026-07-04 15:10");
        agg3.put("title", "Pazienti in carico");
        agg3.put("content", "2 pazienti soccorsi e in trasporto all'ospedale");
        aggiornamenti.add(agg3);
        missione.put("aggiornamenti", aggiornamenti);

        return missione;
    }

    public static Map<String, Object> getOperatoreDetail(String id) {
        Map<String, Object> operatore = new HashMap<>();
        operatore.put("id", id);
        operatore.put("nome", "Marco");
        operatore.put("cognome", "Bianchi");
        operatore.put("email", "marco@soccorsoweb.it");
        operatore.put("telefono", "+39 333 123 4567");
        operatore.put("stato", "OCCUPATO");
        operatore.put("idMissione", "MISS-001");
        operatore.put("patenti", Arrays.asList("A", "B", "C"));
        operatore.put("abilita", Arrays.asList("Soccorritore", "Autista", "Autista di ambulanza"));

        List<Map<String, Object>> storici = new ArrayList<>();
        Map<String, Object> hist1 = new HashMap<>();
        hist1.put("missione_id", "MISS-001");
        hist1.put("data", "2026-07-04");
        hist1.put("descrizione", "Incidente stradale");
        hist1.put("esito", "SUCCESSO");
        storici.add(hist1);
        Map<String, Object> hist2 = new HashMap<>();
        hist2.put("missione_id", "MISS-002");
        hist2.put("data", "2026-07-03");
        hist2.put("descrizione", "Persona svenuta");
        hist2.put("esito", "SUCCESSO");
        storici.add(hist2);
        operatore.put("storico_missioni", storici);

        return operatore;
    }

    public static Map<String, Object> getMezzoDetail(String id) {
        Map<String, Object> mezzo = new HashMap<>();
        mezzo.put("id", id);
        mezzo.put("nome", "Ambulanza A1");
        mezzo.put("descrizione", "Ambulanza di tipo B attrezzata");
        mezzo.put("targa", "AA123BB");
        mezzo.put("stato", "OCCUPATO");
        mezzo.put("idMissione", "MISS-001");

        List<Map<String, Object>> storici = new ArrayList<>();
        Map<String, Object> hist1 = new HashMap<>();
        hist1.put("missione_id", "MISS-001");
        hist1.put("data", "2026-07-04");
        hist1.put("descrizione", "Incidente stradale");
        storici.add(hist1);
        mezzo.put("storico_missioni", storici);

        return mezzo;
    }

    public static Map<String, Object> getMaterialeDetail(String id) {
        Map<String, Object> materiale = new HashMap<>();
        materiale.put("id", id);
        materiale.put("nome", "Kit Medico Base");
        materiale.put("descrizione", "Kit di primo soccorso completo con medicinali e bende");
        materiale.put("quantita", 5);
        materiale.put("stato", "OCCUPATO");
        materiale.put("missioneId", "MISS-001");

        List<Map<String, Object>> storici = new ArrayList<>();
        Map<String, Object> hist1 = new HashMap<>();
        hist1.put("missione_id", "MISS-001");
        hist1.put("data", "2026-07-04");
        hist1.put("descrizione", "Incidente stradale");
        storici.add(hist1);
        materiale.put("storico_missioni", storici);

        return materiale;
    }
}
