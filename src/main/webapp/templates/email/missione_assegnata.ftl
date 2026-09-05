<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Nuova missione assegnata</title>
</head>
<body>

<h1>Email simulata</h1>

<p>Questa email sarebbe stata inviata a:</p>
<ul>
    <#list destinatari as destinatario>
        <li>${destinatario}</li>
    </#list>
</ul>

<h2>Nuova missione assegnata</h2>

<p>
    Ti è stata assegnata una nuova missione.
</p>

<h3>Dettagli della missione</h3>

<table>
    <tr>
        <td><strong>Posizione:</strong></td>
        <td>${missione.posizione}</td>
    </tr>
    <tr>
        <td><strong>Caposquadra:</strong></td>
        <td>${missione.caposquadra}</td>
    </tr>
    <tr>
        <td><strong>Inizio:</strong></td>
        <td>${missione.inizio?datetime("yyyy-MM-dd'T'HH:mm")?string("dd/MM/yyyy HH:mm")}</td>
    </tr>
</table>

<h3>Obiettivo</h3>

<p>
    ${missione.obiettivo}
</p>

<p>
    Ti ricordiamo di verificare i dettagli della richiesta e di procedere
    secondo le indicazioni previste per la missione.
</p>

<p>
    Cordiali saluti,<br>
    Amministrazione
</p>

</body>
</html>
