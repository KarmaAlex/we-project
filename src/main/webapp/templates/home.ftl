<#-- Use the shared base layout -->
<#include "base.ftl">
<@basePage title="SoccorsoWeb - Home" css=["/style/home.css"]>
  <section class="hero" id="main-content" aria-labelledby="request-heading">
    <div class="container">
      <h2 id="request-heading">Invia una richiesta di soccorso</h2>
      <p id="form-description">Compila i campi sottostanti per inviare una richiesta di soccorso.</p>
      <form class="request-form" id="main_form" method="post" enctype="multipart/form-data" aria-labelledby="request-heading" aria-describedby="form-description" tabindex="0">
        <label for="nome">Nome
          <input type="text" name="nome" required aria-required="true" autocomplete="name"/>
        </label>
        <label for="email">Email
          <input type="email" name="email" required aria-required="true" autocomplete="email"/>
        </label>
        <label for="posizione">Posizione
          <input type="text" name="posizione" placeholder="Indirizzo o coordinate" required aria-required="true" autocomplete="street-address" aria-describedby="location-help" />
          <small id="location-help">Inserisci un indirizzo oppure coordinate GPS.</small>
        </label>
        <label for="descrizione">Descrizione
          <textarea name="descrizione" rows="5" required aria-required="true"></textarea>
        </label>
        <label for="foto">Foto (opzionale)
          <input type="file" name="foto" accept="image/*" aria-describedby="photo-help"/>
					<small id="photo-help">È possibile allegare una foto dell'emergenza.</small>
        </label>
        <div class="captcha" id="captcha">
          <label>Solve: 3 + 1 = <input type="text" name="captcha" required /></label>
          <small class="hint">Usato per ridurre lo spam (mockup)</small>
        </div>
        <div class="form-actions">
          <button type="submit" class="btn primary">Invia richiesta</button>
          <button type="reset" class="btn">Annulla</button>
        </div>
      </form>
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
