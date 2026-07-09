use soccorso;
-- svuotamento tabelle relazioni
delete from assegna_squadra;
delete from assegna_mezzo;
delete from assegna_materiale;
delete from assegna_patente;
delete from assegna_abilita;
-- svuotamento tabelle entita'
delete from Desc_richiesta;
delete from Richiesta;
delete from Anagrafica;
delete from Utente;
delete from Patente;
delete from Abilita;
delete from Mezzo;
delete from Materiale;
delete from Aggiornamento;
delete from Missione;
delete from Commento;
delete from Squadra;

-- proceedure
DELIMITER $$

CREATE PROCEDURE InserisciRichiestaConCodice(
    IN p_nome VARCHAR(50),
    IN p_email VARCHAR(40),
    IN p_ip VARCHAR(16),
    IN p_posizione VARCHAR(50),
    IN p_foto VARCHAR(255),
    IN p_descrizione TEXT
)
BEGIN
    DECLARE v_string VARCHAR(32);
    DECLARE v_id INT UNSIGNED;

    -- genera un codice casuale univoco
    SET v_string = SUBSTRING(MD5(RAND()), 1, 32);

    INSERT INTO Richiesta (nome, email, IP, stato, string, verificato, `data`)
    VALUES (p_nome, p_email, p_ip, 'in attesa', v_string, false, NOW());

    SET v_id = LAST_INSERT_ID();

    INSERT INTO Desc_richiesta (ID_RICHIESTA, posizione, foto, descrizione)
    VALUES (v_id, p_posizione, p_foto, p_descrizione);
END$$

DELIMITER ;

-- dati richiesta
insert into Richiesta
values(1,"Antonio", 'antonio@gmail.com','192.168.10.10','chiusa','123456789',true,'25-06-25 13:40:01');
insert into Desc_richiesta
values(1,1,'Via Aldo Moro 33, Roma',null, 'Incidente stradale, ferito lieve');
insert into Richiesta
values(2,"Giuseppa",
'giuseppa@gmail.com','192.168.15.10','chiusa','asdfghjklo',true,'19-06-25 13:40:01');
insert into Desc_richiesta
values(2,2,'Via dei caduti 55 , Milano',null, 'Incidente domestico');
insert into Richiesta
values(3,"Sara", 'saretta@gmail.com','183.168.15.10','chiusa','567892hydb',true,'23-01-25 23:40:01');
insert into Desc_richiesta
values(3,3,'Piazza del Duomo , Firenze','https://archivio_soc/driveR3', 'Caduta albero
su strada, ferito grave');
insert into Richiesta values (4, "Lorenzo", 'Iollo@gmail.com', '183.168.15.10',
'attiva', 'nsur78baud', true, '25-08-31 12:40:01');
insert into Desc_richiesta
values (4,4, 'Parco del sole, Macerata' ,null, 'Gatto bloccato su albero');
insert into Richiesta
values(5, 'Gino', 'gino22@gmail.com', '183,178.15.10', 'in corso', 'cnosin83hb'
,false, '25-08-30 11:45:01');
insert into Desc_richiesta
values(5,5, 'Via Solaria 34',null, 'Incidente domestico' );
insert into Richiesta
values (6, "Andrea", 'andwicks@gmail.com', '183.169.15.10',
'annullata','nsur83bcud',true, '25-08-30 09:40:52');
insert into Desc_richiesta
values (6,6, 'Via Solaria 53, Parma', null,'Bloccato in ascensore');
insert into Richiesta
values (7, "Lorenzo",'lollo@gmail.com', '183.168.15.10', 'in corso', 'nsur83nuad',
true, '25-08-30 12:27:01');
insert into Desc_richiesta
values(7,7, 'Parco del sole, Macerata' ,null, 'Malore anziano');
insert into Richiesta
values (8, "Maria", 'marylou@gmail.com', '183.168.25.10', 'in attesa',
'anidsbcyen',true, '25-08-30 10:30:37');
insert into Desc_richiesta
values(8,8, 'Centro commerciale le palme, messina', 'https://archivio_soc/driveR9',
'Guasto elettrico');
insert into Richiesta
values(9, "Cinzia", 'cindi@gmail.com', '183,168.25.19', 'in corso', 'naucbh29g1',
true, '25-08-29 16:30:56');
insert into Desc_richiesta
values (9,9, 'Centro commerciale porto solare, Napoli',
'https://archivio_soc/driveR7', 'Parcheggio allagato');
Call InserisciRichiestaConCodice("Giovanni", "giovanni1@gmail.com", "179.241.75.226",
"20.35112,122.25039",null,'Bloccati in mezzo al mare');
Call InserisciRichiestaConCodice("Marta", "martilla@gmail.com", "179.251.75.226","
20.35532,122.2039",null,'Incendio');
-- Utenti
insert into Utente(nome_utente, admin)
values('Giorgio1',true);
insert into Anagrafica
values(last_insert_id(),1,"Giorgio","Valle","1234567890asdfgh",'Venezia','89/03/12');
insert into Utente(nome_utente)
values('Armando');
insert into Anagrafica
values(last_insert_id(), 2,
'Armando','Casadei','zxcvb65432yuiopl','Rimini','84/08/18');
insert into Utente(nome_utente, admin)
values('Anna',true);
insert into Anagrafica
values(last_insert_id(), 3, 'Anna','Rossi','zxche98132yuiopl','Isernia','95/01/08');
insert into Utente(nome_utente)
values('Gioia');
insert into Anagrafica
values(last_insert_id(), 4, 'Gioia','Verdi','kijst65432yuiopl','Bolzano','01/08/04');
insert into Utente(nome_utente)
values ('Nino');
insert into Anagrafica(ID_UTENTE, nome, cognome, cf, luogo_nasc, data_nasc)
values (LAST_INSERT_ID(), 'Nino', 'Merlo', 'bidhy71946pdbua', 'Bari', '78/11/28');
insert into Utente(nome_utente, admin)
values ('Alba', true);
insert into Anagrafica(ID_UTENTE, nome, cognome, cf, luogo_nasc, data_nasc)
values (LAST_INSERT_ID(), 'Alba', 'Norri','zjens02753snirs', 'Bologna', '92/02/15');
insert into Utente(nome_utente, admin)
values ('Stefano2', true);
insert into Anagrafica (ID_UTENTE, nome, cognome, cf, luogo_nasc, data_nasc)
values (LAST_INSERT_ID(), 'Stefano', 'Lucci' ,'anfux27493abiem', 'Macerata',
'88/05/13');
insert into Utente(nome_utente)
values ('Mery');
insert into Anagrafica (ID_UTENTE, nome, cognome, cf, luogo_nasc, data_nasc)
values (LAST_INSERT_ID(), 'Maria', 'Lilla', 'anige27562anofe', 'Palermo', '95/07/21');
insert into Utente(nome_utente)
values ('Lauretta') ;
insert into Anagrafica(ID_UTENTE, nome, cognome, cf, luogo_nasc, data_nasc)
values (LAST_INSERT_ID(), 'Laura', 'Verdi', 'dnusv67391hisbe', 'Sondrio', '97/06/05');
insert into Utente(nome_utente)
values ('John');
insert into Anagrafica(ID_UTENTE, nome, cognome, cf, luogo_nasc, data_nasc)
values(LAST_INSERT_ID(), 'John', 'Smith', 'dnusv67391brdvi', 'Denver', '95/08/05');
-- patenti
insert into Patente(tipo, numero)
values('B', 'ABCD123456');
insert into Patente(tipo, numero)
values('B', 'AGDS642375');
insert into Patente(tipo, numero)
values('B', 'BJAQ723754');
insert into Patente(tipo, numero)
values('A', 'OBJA653012');
insert into Patente(tipo, numero)
values('NAU', 'OBYW750182');
insert into Patente(tipo, numero)
values('C', 'NBKI917583');
insert into Patente(tipo, numero)
values('A', 'NISU073876');
insert into Patente(tipo, numero)
values('B', 'NIDG638945');
insert into Patente(tipo, numero)
values('B', 'PODY386705');
insert into Patente(tipo, numero)
values('C', 'DYNT689360');
-- Abilità
insert into Abilita(`desc`)
values('Primo soccorso');
insert into Abilita(`desc`)
values('Medico');
insert into Abilita(`desc`)
values('Bagnino');
insert into Abilita(`desc`)
values('Rianimazione');
insert into Abilita(`desc`)
values('Elettricista');
insert into Abilita(`desc`)
values('OSS');
insert into Abilita(`desc`)
values('Autista');
insert into Abilita(`desc`)
values('Infermiere');
-- Mezzi
insert into Mezzo(nome,`desc`,targa)
values('ducato','ambulanza','aa000aa');
insert into Mezzo(nome,`desc`,targa)
values('Panda','fiat panda','aa000ab');
insert into Mezzo(nome,`desc`,targa)
values('Solcaonde','barca a motore','aa000ac');
insert into Mezzo(nome,`desc`,targa)
values('Autopompa','autopompa','aa000ad');
insert into Mezzo(nome,`desc`,targa)
values('ducato','ambulanza','aa089aa');
insert into Mezzo(nome,`desc`,targa)
values('Saltascogli','scialuppa di salvataggio','an348va');
insert into Mezzo(nome,`desc`,targa)
values('Elicottero','elicottero','ab258aa');
insert into Mezzo(nome,`desc`,targa)
values('ducato','ambulanza','ae500aa');
-- Materiale
insert into Materiale(nome,`desc`, cod_mat)
values('Barella', 'portata massima 100 kg','0000000000');
insert into Materiale(nome,`desc`, cod_mat)
values('kit pronto soccorso', 'cerotti, bende, disinfettante','0000000001');
insert into Materiale(nome,`desc`, cod_mat)
values('Defibrillatore', 'funzionante','0000000023');
insert into Materiale(nome,`desc`, cod_mat)
values('Salvagente', 'arancione','000000025');
insert into Materiale(nome,`desc`, cod_mat)
values('corda','canapa','0000000875');
insert into Materiale(nome,`desc`, cod_mat)
values('corda','plastica','0000003875');
insert into Materiale(nome,`desc`, cod_mat)
values('Casco','Casco protettivo','000000485');
insert into Materiale(nome,`desc`, cod_mat)
values('Scala','3m metallo','000022485');
insert into Materiale(nome,`desc`, cod_mat)
values('Guanti','Gomma taglia m','000024485');
-- Squadre
insert into Squadra(ID_CAPO)
values(1); #Squadra 1 capo- Giorgio
insert into Squadra(ID_CAPO)
values(1); #Squadra 2 capo- Giorgio
insert into Squadra(ID_CAPO)
values(3);#Squadra 3 capo- Anna
insert into Squadra(ID_CAPO)
values(4);#Squadra 4 capo- Gioia
insert into Squadra(ID_CAPO)
values(5);#Squadra 5 capo- Nino
insert into Squadra(ID_CAPO)
values(6);#Squadra 6 capo- Stefano
insert into Squadra(ID_CAPO)
values(7);#Squadra 7 capo- Mery
-- assegnazioni squadra
-- Squadra 1
insert into assegna_squadra
values(1,3);
-- Squadra 2
insert into assegna_squadra
values(2,1);
insert into assegna_squadra
values(2,2);
-- Squadra 3
insert into assegna_squadra
values(3,4);
insert into assegna_squadra
values(3,5);
-- Squadra 4
insert into assegna_squadra
values(4,2);
-- Squadra 5
insert into assegna_squadra
values(5,6);
-- Squadra 6
insert into assegna_squadra
values(6,10);
insert into assegna_squadra
values(6,7);
-- Squadra 7
insert into assegna_squadra
values(7,8);
insert into assegna_squadra
values(7,9);
-- Missioni
insert into Missione(ID_RICHIESTA, ID_SQUADRA, ID_ADMIN, obiettivo, inizio, fine,
completata, successo, durata)
values(1,1,1,'Soccorso feriti','25-06-25 11:45:18','25-06-25
14:30:54',true,5,timediff('25-06-25 14:30:54', '25-06-25 11:45:18') );
-- Visto che abbiamo inserito una missione già completata aggiorniamo manualmente il
-- campo monte_ore dei partecipanti
UPDATE Utente u JOIN assegna_squadra asq ON u.ID = asq.ID_UTENTE JOIN Squadra s ON
s.ID = asq.ID_SQUADRA OR s.ID_CAPO = u.ID SET u.monte_ore = u.monte_ore + (SELECT
FLOOR(TIME_TO_SEC(m.durata) / 3600) FROM Missione m WHERE m.ID = LAST_INSERT_ID())
WHERE asq.ID_SQUADRA = (SELECT m.ID_SQUADRA FROM Missione m WHERE m.ID =
LAST_INSERT_ID());
-- Facciamo lo stesso per il caposquadra
UPDATE Utente u JOIN Squadra s ON s.ID_CAPO = u.ID SET u.monte_ore = u.monte_ore +
(SELECT FLOOR(TIME_TO_SEC(m.durata) / 3600) FROM Missione m WHERE m.ID =
LAST_INSERT_ID()) WHERE s.ID = (SELECT m.ID_SQUADRA FROM Missione m WHERE m.ID =
LAST_INSERT_ID());
insert into Missione(ID_RICHIESTA, ID_SQUADRA, ID_ADMIN, obiettivo, inizio, fine,
completata, successo, durata)
values(2,2,1,'Soccorso feriti','25-06-19 10:45:38','19-06-25 14:15:54',true
,3,timediff('19-06-25 14:15:54', '19-06-25 10:45:38') );
UPDATE Utente u JOIN assegna_squadra asq ON u.ID = asq.ID_UTENTE JOIN Squadra s ON
s.ID = asq.ID_SQUADRA OR s.ID_CAPO = u.ID SET u.monte_ore = u.monte_ore + (SELECT
FLOOR(TIME_TO_SEC(m.durata) / 3600) FROM Missione m WHERE m.ID = LAST_INSERT_ID())
WHERE asq.ID_SQUADRA = (SELECT m.ID_SQUADRA FROM Missione m WHERE m.ID =
LAST_INSERT_ID());
UPDATE Utente u JOIN Squadra s ON s.ID_CAPO = u.ID SET u.monte_ore = u.monte_ore +
(SELECT FLOOR(TIME_TO_SEC(m.durata) / 3600) FROM Missione m WHERE m.ID =
LAST_INSERT_ID()) WHERE s.ID = (SELECT m.ID_SQUADRA FROM Missione m WHERE m.ID =
LAST_INSERT_ID());
insert into Missione(ID_RICHIESTA, ID_SQUADRA, ID_ADMIN, obiettivo, inizio, fine,
completata, successo, durata)
values(3,3,2,'Rimozione ostacolo','23-01-25 23:45:38','23-01-26
01:30:54',true,2,timediff('23-01-26 01:30:54', '23-01-25 23:45:38') );
UPDATE Utente u JOIN assegna_squadra asq ON u.ID = asq.ID_UTENTE JOIN Squadra s ON
s.ID = asq.ID_SQUADRA OR s.ID_CAPO = u.ID SET u.monte_ore = u.monte_ore + (SELECT
FLOOR(TIME_TO_SEC(m.durata) / 3600) FROM Missione m WHERE m.ID = LAST_INSERT_ID())
WHERE asq.ID_SQUADRA = (SELECT m.ID_SQUADRA FROM Missione m WHERE m.ID =
LAST_INSERT_ID());
UPDATE Utente u JOIN Squadra s ON s.ID_CAPO = u.ID SET u.monte_ore = u.monte_ore +
(SELECT FLOOR(TIME_TO_SEC(m.durata) / 3600) FROM Missione m WHERE m.ID =
LAST_INSERT_ID()) WHERE s.ID = (SELECT m.ID_SQUADRA FROM Missione m WHERE m.ID =
LAST_INSERT_ID());
insert into Missione(ID_RICHIESTA,ID_SQUADRA, ID_ADMIN,obiettivo, inizio)
values(5,5,6,'Soccorso anziano','25-08-30 11:47:38');
insert into Missione(ID_RICHIESTA,ID_SQUADRA, ID_ADMIN,obiettivo, inizio)
values(7,2,7,'Soccorso anziano','25-08-30 12:30:38');
insert into Missione(ID_RICHIESTA,ID_SQUADRA, ID_ADMIN,obiettivo, inizio)
values(9,6,1,'Salvataggio persone bloccate','25-08-29 16:35:38');
-- Aggiornamenti
insert into Aggiornamento(ID_MISSIONE, ID_ADMIN, `timestamp`, testo)
values(2,1,'19-06-25 13:49:38','Chiamata ambulanza');
-- Commenti
insert into Commento(ID_MISSIONE, ID_ADMIN, testo)
values(1,1,'Frontale');
insert into Commento(ID_MISSIONE, ID_ADMIN, testo)
values(3,2,'Mancanza di mezzo adeguato');
-- Assegnazione mezzo
insert into assegna_mezzo
values(1,1);
insert into assegna_mezzo
values(1,2);
insert into assegna_mezzo
values(2,3);
insert into assegna_mezzo
values(1,3);
insert into assegna_mezzo
values(1,4);
insert into assegna_mezzo
values(5,5);
insert into assegna_mezzo
values(6,6);
insert into assegna_mezzo
values(8,6);
-- Assegnazione materiale
insert into assegna_materiale
values(1,1);
insert into assegna_materiale
values(2,1);
insert into assegna_materiale
values(2,2);
insert into assegna_materiale
values(1,2);
insert into assegna_materiale
values(3,1);
insert into assegna_materiale
values(1,3);
insert into assegna_materiale
values(2,3);
insert into assegna_materiale
values(3,3);
insert into assegna_materiale
values(3,5);
insert into assegna_materiale
values(1,5);
insert into assegna_materiale
values(4,6);
-- Assegnazione patente
insert into assegna_patente
values(1,1);
insert into assegna_patente
values(2,2);
insert into assegna_patente
values(3,3);
insert into assegna_patente
values(4,4);
insert into assegna_patente
values(5,5);
insert into assegna_patente
values(6,6);
insert into assegna_patente
values(7,7);
insert into assegna_patente
values(8,8);
insert into assegna_patente
values(9,9);
insert into assegna_patente
values(10,10);
-- assegnazione abilità
insert into assegna_abilita
values(1,1);
insert into assegna_abilita
values(2,2);
insert into assegna_abilita
values(4,3);
insert into assegna_abilita
values(3,4);
insert into assegna_abilita
values(2,5);
insert into assegna_abilita
values(5,8);
insert into assegna_abilita
values(5,4);
insert into assegna_abilita
values(6,2);
insert into assegna_abilita
values(7,5);
insert into assegna_abilita
values(7,8);
insert into assegna_abilita
values(8,7);
insert into assegna_abilita
values(8,1);
insert into assegna_abilita
values(9,2);
insert into assegna_abilita
values(10,3);
insert into assegna_abilita
values(10,4);