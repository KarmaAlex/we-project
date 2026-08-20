<#include "base.ftl">
<@basePage title="SoccorsoWeb - Completa profilo" css=["/style/login.css"]>
  <section class="container">
    <div class="card login-card">
      <h2>Completa il profilo</h2>
      <form action="#" method="post" class="login-form">
        <label>Nome
          <input type="text" name="nome" autocomplete="given-name" required />
        </label>
        <label>Cognome
          <input type="text" name="cognome" autocomplete="family-name" required />
        </label>
        <label>Codice fiscale
          <input type="text" name="cf" maxlength="16" autocomplete="off" required />
        </label>
        <label>Luogo di nascita
          <input type="text" name="luogo_nasc" autocomplete="off" required />
        </label>
        <label>Data di nascita
          <input type="date" name="data_nasc" autocomplete="bday" required />
        </label>
        <div class="form-actions">
          <button type="submit" class="btn primary">Salva profilo</button>
        </div>
      </form>
    </div>
  </section>
</@basePage>
