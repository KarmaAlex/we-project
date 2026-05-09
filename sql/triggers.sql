-- Procedura per verificare che il livello di successo di una missione sia compreso tra 0 e 5
create procedure verifica_livello(livello smallint)
begin
if livello<0 and livello>5
then begin
signal sqlstate '45000'
set message_text='Livello di successo non valido';
end;
end if;
end$
create trigger livello_valido_ins before insert on Missione
for each row begin
call verifica_livello(New.successo);
end$
create trigger livello_valido_upd before update on Missione
for each row begin
call verifica_livello(New.successo);
end$

-- trigger per coerenza stato e flag di verifica delle richieste
create trigger stato_richiesta_valido_ins before insert on richiesta
for each row begin
if new.stato!='in attesa' and new.verificato=false
then begin
signal sqlstate '45000'
set message_text='Stato non valido';
end;
end if;
end$
create trigger stato_richiesta_valido_upd before update on Richiesta
for each row begin
if new.stato!='in corso' and new.stato!='attiva' and new.stato!='chiusa' and
new.stato!='annullata' and new.verificato=true
then begin
signal sqlstate '45000'
set message_text='Stato non valido';
end;
end if;
end$

-- controllo coerenza flag di completamento e stato delle missioni
create trigger stato_missione_ins before insert on Missione
for each row begin
declare s varchar(9);
select stato from Richiesta where ID=new.ID_RICHIESTA INTO s;
if s!='attiva' and new.completata=false
then begin
signal sqlstate '45000'
set message_text='Richiesta non attiva';
end;
end if;
end$

-- procedura per ottenere lo stato di una missione
create procedure get_conclusione(idm integer unsigned, out compl boolean)
begin
declare s boolean;
set compl=(select completata from Missione where ID=idm);
end$
create trigger insertC before insert on Commento
for each row begin
call get_conclusione(new.ID_MISSIONE,@concl);
if @concl=false
then begin
signal sqlstate '45000'
set message_text='Missione in corso';
end;
end if;
end$
create trigger updateC before update on Commento
for each row begin
call get_conclusione(new.ID_MISSIONE,@concl);
if @concl=false
then begin
signal sqlstate '45000'
set message_text='Missione in corso';
end;
end if;
end$

-- trigger aggiornamenti
create trigger updateA before update on Aggiornamento
for each row begin
call get_conclusione(new.ID_MISSIONE,@concl);
if @concl=false
then begin
signal sqlstate '45000'
set message_text='Missione chiusa';
end;
end if;
end$
create trigger insertA before insert on Aggiornamento
for each row begin
call get_conclusione(new.ID_MISSIONE,@concl);
if @concl=true
then begin
signal sqlstate '45000'
set message_text='Missione chiusa';
end;
end if;
end$

-- trigger controllo mezzi in missione
create trigger mezzo_in_missione before insert on assegna_mezzo
for each row begin
if new.ID_MEZZO in (select ID_MEZZO from missione m join assegna_mezzo am on
m.ID=am.ID_MISSIONE where m.completata=false)
then begin
signal sqlstate '45000'
set message_text='Mezzo in uso';
end;
end if;
end$
--trigger controllo materiale coinvolto in missione
create trigger materiale_in_missione before insert on assegna_materiale
for each row begin
if new.ID_MATERIALE in (select ID_MATERIALE from missione m join assegna_materiale
am on m.ID=am.ID_MISSIONE where m.completata=false )
then begin
signal sqlstate '45000'
set message_text='Materiale in uso';
end;
end if;
end$

-- trigger per l'assegnazione di una patente ad un unico utente
create trigger patente_assegnata before insert on assegna_patente
for each row begin
if new.ID_PATENTE in (select ID_PATENTE from Utente u join assegna_patente ap on
u.ID=ap.ID_UTENTE)
then begin
signal sqlstate '45000'
set message_text='Patente già assegnata';
end;
end if;
end$

-- trigger controllo squadra
create trigger is_capo before insert on assegna_squadra
for each row begin
if new.ID_UTENTE in (select ID_CAPO from Squadra where ID=new.ID_SQUADRA)
then begin
signal sqlstate '45000'
set message_text='Utente è il capo della squadra';
end;
end if;
end$

create trigger membri_disponibili before insert on Missione
for each row begin
declare in_m int default 0;
-- Controllo sul caposquadra
select count(*) into in_m
from Missione m
join Squadra s on m.ID_SQUADRA = s.ID
where s.ID_CAPO = (select ID_CAPO from Squadra where ID = new.ID_SQUADRA)
and m.completata = false;
if in_m > 0 then
signal sqlstate '45000'
set message_text = 'Il caposquadra è impegnato in una missione attiva';
end if;

-- Controllo sui membri della squadra
select count(*) into in_m from Missione m
join assegna_squadra asq on m.ID_SQUADRA = asq.ID_SQUADRA
where asq.ID_UTENTE in (
select ID_UTENTE
from assegna_squadra
where ID_SQUADRA = new.ID_SQUADRA
)
and m.completata = false;
if in_m > 0 then
signal sqlstate '45000'
set message_text= 'Uno o più membri della squadra sono già in missione
attiva';
end if;
end$