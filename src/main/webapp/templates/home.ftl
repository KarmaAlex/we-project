<#include "base.ftl">
<#include "form.ftl">
<@basePage title="SoccorsoWeb - Home" css=["/style/home.css"]>
  <section class="hero" id="main-content" aria-labelledby="request-heading">
    <div class="container">
      <h2 id="request-heading">Invia una richiesta di soccorso</h2>
      <p id="form-description">Compila i campi sottostanti per inviare una richiesta di soccorso.</p>
      <#assign emergencyFields = [
      {
          "type":"text",
          "name":"nome",
          "label":"Nome",
          "required":true,
          "autocomplete":"name"
      },
      {
          "type":"email",
          "name":"email",
          "label":"Email",
          "required":true,
          "autocomplete":"email"
      },
      {
          "type":"text",
          "name":"posizione",
          "label":"Posizione",
          "placeholder":"Indirizzo o coordinate",
          "required":true,
          "autocomplete":"street-address",
          "help":"Inserisci un indirizzo oppure coordinate GPS."
      },
      {
          "type":"textarea",
          "name":"descrizione",
          "label":"Descrizione",
          "rows":5,
          "required":true
      },
      {
          "type":"file",
          "name":"foto",
          "label":"Foto (opzionale)",
          "accept":"image/*",
          "help":"È possibile allegare una foto dell'emergenza."
      },
      {
          "type":"text",
          "name":"captcha",
          "label":"Solve: 3 + 1 =",
          "required":true,
          "help":"Usato per ridurre lo spam (mockup)"
      }
      ]>

      <@form
          id="main_form"
          fields=emergencyFields
          csrfToken=csrfToken
          submitLabel="Invia richiesta"
          resetLabel="Annulla"
      />
    </div>
  </section>

  <section class="container" aria-labelledby="how-heading" role="region">
    <h3 id="how-heading">Come funziona</h3>
    <p>SoccorsoWeb riceve le segnalazioni pubbliche e le rende visibili agli amministratori solo dopo conferma via email.</p>
    <div class="cards">
      <div class="card"><h4>Amministratori</h4><p>Gestione richieste, missioni e risorse.</p></div>
      <div class="card"><h4>Operatori</h4><p>Visualizzazione missioni e storico personale.</p></div>
      <div class="card"><h4>Risorse</h4><p>Mezzi e materiali disponibili con storico missioni.</p></div>
    </div>
  </section>
</@basePage>
