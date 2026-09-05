<!DOCTYPE html>
<html lang="it">
<head>
	<meta charset="UTF-8">
	<title>Verifica richiesta di soccorso</title>
</head>
<body>

<h1>Email di verifica</h1>

<p>Email simulata inviata a:${email}</p>

<h2>Richiesta ricevuta</h2>

<p>
	Abbiamo ricevuto la tua richiesta di soccorso.
	Per completare l'invio e permetterne la presa in carico, verifica la richiesta
	utilizzando il link riportato di seguito.
</p>

<p>
	<a href="${link}">Verifica la richiesta</a>
</p>

<p>
	Oppure copia e incolla questo indirizzo nel tuo browser:
</p>

<p>
	<a href="${link}">${link}</a>
</p>

<p>
	<strong>Importante:</strong> la richiesta non sarà accettata finché non sarà verificata.
	Se non hai effettuato tu questa richiesta, puoi ignorare questa email.
</p>

<p>
	Cordiali saluti,<br>
	Amministrazione
</p>

</body>
</html>
