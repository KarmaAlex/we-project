<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Account operatore creato</title>
</head>
<body>

<h2>Benvenuto!</h2>

<p>
    Il tuo account operatore è stato creato con successo.
    Di seguito trovi le credenziali temporanee necessarie per effettuare il primo accesso.
</p>

<h3>Credenziali di accesso</h3>

<table>
    <tr>
        <td><strong>Username:</strong></td>
        <td>${username}</td>
    </tr>
    <tr>
        <td><strong>Password temporanea:</strong></td>
        <td>${password}</td>
    </tr>
</table>

<p>
    Puoi effettuare il login utilizzando il seguente link:
</p>

<p>
    <a href="${loginUrl}">${loginUrl}</a>
</p>

<p>
    <strong>Importante:</strong> la password fornita è temporanea.
    Ti consigliamo di modificarla al primo accesso e di non condividere
    le tue credenziali con altre persone.
</p>

<p>
    Cordiali saluti,<br>
    Amministrazione
</p>

</body>
</html>
