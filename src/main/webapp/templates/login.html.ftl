<#-- Use the shared base layout -->
<#include "base.ftl">
<@layout title="SoccorsoWeb - Login" css=["/style/base.css","/style/login.css"]>
  <section class="container">
    <div class="card login-card">
      <h2>Accedi al pannello</h2>
      <form action="#" method="post" class="login-form">
        <label>Nome utente
          <input type="text" name="username" autocomplete="username" required />
        </label>
        <label>Password
          <input type="password" name="password" autocomplete="current-password" required />
        </label>
        <label class="checkbox-row"><input type="checkbox" name="remember"/> Ricordami</label>
        <div class="form-actions">
          <button type="submit" class="btn primary">Accedi</button>
          <a href="index.html" class="btn">Annulla</a>
        </div>
        <p class="muted note">Se non hai accesso contatta un amministratore.</p>
      </form>
    </div>
  </section>
</@layout>
