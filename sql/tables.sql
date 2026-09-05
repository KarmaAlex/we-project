drop schema if exists soccorso;
create schema soccorso;
use soccorso;
#Relazioni
drop table if exists assegna_squadra;
drop table if exists assegna_mezzo;
drop table if exists assegna_materiale;
drop table if exists assegna_patente;
drop table if exists assegna_abilita;
drop table if exists assegna_credenziali;
#Entità
drop table if exists Desc_richiesta;
drop table if exists Richiesta;
drop table if exists Anagrafica;
drop table if exists Utente;
drop table if exists Patente;
drop table if exists Abilita;
drop table if exists Mezzo;
drop table if exists Materiale;
drop table if exists Aggiornamento;
drop table if exists Missione;
drop table if exists Commento;
drop table if exists Squadra;
drop table if exists Credenziali;
#Creazione tabelle
create table Richiesta(
ID int unsigned auto_increment primary key,
nome varchar(50) not null,
email varchar(40) not null,
IP varchar(16) not null,
stato varchar(9) not null default 'in attesa',
string varchar(32) not null,
verificato boolean not null default false,
`data` datetime not null,
constraint string_unica unique(string)
);
create table Desc_richiesta(
ID int unsigned auto_increment primary key,
ID_RICHIESTA int unsigned not null,
posizione varchar(50) not null,
foto varchar(255),
descrizione text NOT NULL,
constraint desc_richiesta foreign key(ID_RICHIESTA) references Richiesta(ID) on
update cascade on delete cascade
);
create table Utente(
ID int unsigned auto_increment primary key,
nome_utente varchar(20) not null,
admin boolean not null default false,
monte_ore int unsigned not null default 0,
constraint username_unico unique(nome_utente)
);
create table Anagrafica(
ID int unsigned auto_increment primary key,
ID_UTENTE int unsigned not null,
nome varchar(20) not null,
cognome varchar(30) not null,
cf varchar(16) not null,
luogo_nasc varchar(30) not null,
data_nasc date not null,
constraint cf_unico unique(cf),
constraint anagrafica_utente foreign key(ID_UTENTE) references Utente(ID) on
update cascade on delete cascade
);
create table Patente(
ID int unsigned auto_increment primary key,
numero varchar(10) not null,
tipo varchar(3) not null,
constraint patente_unica unique(numero, tipo)
);
create table Abilita(
ID int unsigned auto_increment primary key,
`desc` varchar(50) not null,
constraint abilita_unica unique(`desc`)
);
create table Mezzo(
ID int unsigned auto_increment primary key,
nome varchar(20) not null,
`desc` text not null,
targa varchar(10) not null,
constraint targa_unica unique(targa)
);
create table Materiale(
ID int unsigned auto_increment primary key,
nome varchar(20) not null,
`desc` text not null,
cod_mat varchar(10) not null,
constraint cod_mat_unico unique(cod_mat)
);
create table Squadra(
ID int unsigned auto_increment primary key,
ID_CAPO int unsigned not null,
constraint caposquadra foreign key(ID_CAPO) references Utente(ID) on update
cascade on delete restrict
);
create table Missione(
ID int unsigned auto_increment primary key,
ID_RICHIESTA int unsigned not null,
ID_SQUADRA int unsigned not null,
ID_ADMIN int unsigned not null,
obiettivo varchar(100) not null,
inizio datetime not null,
fine datetime,
completata boolean not null default false,
successo tinyint not null default 0,
durata time,
constraint missione_richiesta foreign key(ID_RICHIESTA) references Richiesta(ID)
on update cascade on delete restrict,
constraint missione_squadra foreign key(ID_SQUADRA) references Squadra(ID) on
update cascade on delete restrict, #una squadra non può essere eliminata se è stata associata ad una missione
constraint missione_admin foreign key(ID_ADMIN) references Utente(ID) on update
cascade on delete restrict
);
create table Commento(
ID int unsigned auto_increment primary key,
ID_MISSIONE int unsigned not null,
ID_ADMIN int unsigned not null,
testo text not null,
constraint commento_missione foreign key(ID_MISSIONE) references Missione(ID) on
update cascade on delete cascade, #se una missione viene eliminata eliminiamo anche commenti e aggiornamenti
constraint commento_admin foreign key(ID_ADMIN) references Utente(ID) on update
cascade on delete restrict
);
create table Aggiornamento(
ID int unsigned auto_increment primary key,
ID_MISSIONE int unsigned not null,
ID_ADMIN int unsigned not null,
`timestamp` datetime not null,
testo text not null,
constraint agg_missione foreign key(ID_MISSIONE) references Missione(ID) on update
cascade on delete cascade,
constraint agg_admin foreign key(ID_ADMIN) references Utente(ID) on update cascade
on delete restrict
);
create table Credenziali(
ID int unsigned auto_increment primary key,
email varchar(255) not null,
passwordhash binary(96) not null,
constraint email_unica unique(email)
);

# Tabelle Relazioni
create table assegna_squadra(
ID_SQUADRA int unsigned not null,
ID_UTENTE int unsigned not null,
constraint squadra foreign key(ID_SQUADRA) references Squadra(ID) on update
cascade on delete restrict, # prima di eliminare una squadra dobbiamo eliminare i membri
constraint utente foreign key(ID_UTENTE) references Utente(ID) on update cascade
on delete restrict # prima di eliminare un utente dobbiamo rimuoverlo dalle squadre di cui fa parte
);
create table assegna_mezzo(
ID_MEZZO int unsigned not null,
ID_MISSIONE int unsigned not null,
constraint mezzo foreign key(ID_MEZZO) references Mezzo(ID) on update cascade on
delete restrict, # prima di eliminare un mezzo dobbiamo eliminarlo da tutte le missioni
constraint mezzo_missione foreign key(ID_MISSIONE) references Missione(ID) on
update cascade on delete cascade # Quando eliminiamo una missione eliminiamo anche tutte le associazioni con i veicoli
);
create table assegna_materiale(
ID_MATERIALE int unsigned not null,
ID_MISSIONE int unsigned not null,
constraint materiale foreign key(ID_MATERIALE) references Materiale(ID) on update
cascade on delete restrict,
constraint materiale_missione foreign key(ID_MISSIONE) references Missione(ID) on
update cascade on delete cascade
);
create table assegna_patente(
ID_UTENTE int unsigned not null,
ID_PATENTE int unsigned not null,
constraint patente foreign key(ID_PATENTE) references Patente(ID) on update
cascade on delete restrict,
constraint patente_utente foreign key(ID_UTENTE) references Utente(ID) on update
cascade on delete cascade,
constraint ass_patente_unico unique(ID_UTENTE, ID_PATENTE)
);
create table assegna_abilita(
ID_UTENTE int unsigned not null,
ID_ABILITA int unsigned not null,
constraint abilita foreign key(ID_ABILITA) references Abilita(ID) on update
cascade on delete restrict,
constraint abilita_utente foreign key(ID_UTENTE) references Utente(ID) on update
cascade on delete cascade,
constraint ass_abilita_unica unique(ID_UTENTE, ID_ABILITA)
);
create table assegna_credenziali(
ID_UTENTE int unsigned not null,
ID_CREDENZIALI int unsigned not null,
constraint credenziali_utente foreign key(ID_UTENTE) references Utente(ID) on update cascade on delete restrict,
constraint credenziali_cred foreign key(ID_CREDENZIALI) references Credenziali(ID) on update
cascade on delete restrict,
constraint ass_credenziali_unica unique(ID_UTENTE, ID_CREDENZIALI)
);
    
#Utenti
drop user if exists "user"@"localhost";
create user "user"@"localhost" identified by
"04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb"; -- Hash SHA256 di'user', usato nel login dall'interfaccia
grant execute on * to "user"@"localhost"; #Per eseguire le procedure associate alle query
grant select on Abilita to "user"@"localhost";
grant select on Aggiornamento to "user"@"localhost";
grant select on Commento to "user"@"localhost";
grant select on Desc_richiesta to "user"@"localhost";
grant select on Materiale to "user"@"localhost";
grant select on Mezzo to "user"@"localhost";
grant select on Missione to "user"@"localhost";
grant select on Richiesta to "user"@"localhost";
grant select on Squadra to "user"@"localhost";
grant select on Utente to "user"@"localhost";
grant select on assegna_materiale to "user"@"localhost";
grant select on assegna_mezzo to "user"@"localhost";
grant select on assegna_squadra to "user"@"localhost";
grant select on assegna_abilita to "user"@"localhost";
grant insert on Richiesta to "user"@"localhost";
grant update on Richiesta to "user"@"localhost";
drop user if exists "admin"@"localhost";
create user "admin"@"localhost" identified by
"8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918"; -- Hash SHA256 di'admin', usato nel login dall'interfaccia
grant all on * to "admin"@"localhost";
CREATE USER IF NOT EXISTS 'soccorso_app'@'localhost' IDENTIFIED BY 'soccorsoapp';
GRANT ALL PRIVILEGES ON `soccorso`.* TO "soccorso_app"@"localhost";
FLUSH PRIVILEGES;