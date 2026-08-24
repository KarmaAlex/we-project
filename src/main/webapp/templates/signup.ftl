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
        <label>Nuova password
          <input type="password" name="new_password" autocomplete="new-password" minlength="8" required />
        </label>
        <label>Conferma password
          <input type="password" name="confirm_password" autocomplete="new-password" minlength="8" required />
        </label>
        <div class="form-actions">
          <button type="submit" class="btn primary">Salva profilo</button>
        </div>
      </form>
    </div>
  </section>

  <script>
    const signupForm = document.querySelector('.login-form');
    const newPasswordInput = signupForm?.querySelector('input[name="new_password"]');
    const confirmPasswordInput = signupForm?.querySelector('input[name="confirm_password"]');

    const validatePasswordMatch = () => {
      if (!newPasswordInput || !confirmPasswordInput) return;
      if (confirmPasswordInput.value && newPasswordInput.value !== confirmPasswordInput.value) {
        confirmPasswordInput.setCustomValidity('Le password non coincidono.');
      } else {
        confirmPasswordInput.setCustomValidity('');
      }
    };

    if (signupForm && newPasswordInput && confirmPasswordInput) {
      newPasswordInput.addEventListener('input', validatePasswordMatch);
      confirmPasswordInput.addEventListener('input', validatePasswordMatch);
      signupForm.addEventListener('submit', (event) => {
        validatePasswordMatch();
        if (!signupForm.checkValidity()) {
          event.preventDefault();
          signupForm.reportValidity();
        }
      });
    }
  </script>
</@basePage>
