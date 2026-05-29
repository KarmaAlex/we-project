<#-- Use the shared base layout -->
<#include "base.ftl">
<@layout title="SoccorsoWeb - Home" css=["/style/home.css"]>
  <section class="hero">
    <div class="container">
      <h2>Invia una richiesta di soccorso</h2>
      <form class="request-form" id="main_form" method="post" enctype="multipart/form-data">
        <label>Nome
          <input type="text" name="nome" required />
        </label>
        <label>Email
          <input type="email" name="email" required />
        </label>
        <label>Posizione
          <input type="text" name="posizione" placeholder="Indirizzo o coordinate" required />
        </label>
        <label>Descrizione
          <textarea name="descrizione" rows="5" required></textarea>
        </label>
        <label>Foto (opzionale)
          <input type="file" name="foto" accept="image/*" />
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

  <section class="info container">
    <h3>Come funziona</h3>
    <p>SoccorsoWeb riceve le segnalazioni pubbliche e le rende visibili agli amministratori solo dopo conferma via email.</p>
    <div class="cards">
      <div class="card"><h4>Amministratori</h4><p>Gestione richieste, missioni e risorse.</p></div>
      <div class="card"><h4>Operatori</h4><p>Visualizzazione missioni e storico personale.</p></div>
      <div class="card"><h4>Risorse</h4><p>Mezzi e materiali disponibili con storico missioni.</p></div>
    </div>
  </section>
</@layout>
