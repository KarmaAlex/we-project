<#-- Operator: Profile Section -->

<div class="card" style="max-width: 800px;">
  <h3>Profilo Personale</h3>
  
  <form method="POST" action="/api/operator/profile/update" class="profile-form">
    
    <fieldset style="border: 1px solid var(--card-border); padding: 1rem; border-radius: var(--radius); margin-bottom: 1.5rem;">
      <legend style="padding: 0 0.5rem; font-weight: 600;">Dati Anagrafici</legend>
      
      <div style="margin-bottom: 1rem;">
        <label for="nome">Nome:</label>
        <input type="text" id="nome" name="nome" value="Marco Bianchi" required>
      </div>

      <div style="margin-bottom: 1rem;">
        <label for="cognome">Cognome:</label>
        <input type="text" id="cognome" name="cognome" value="Bianchi" required>
      </div>

      <div style="margin-bottom: 1rem;">
        <label for="email">Email:</label>
        <input type="email" id="email" name="email" value="marco@soccorsoweb.it" required disabled>
        <small style="color: var(--muted);">Non modificabile</small>
      </div>

      <div style="margin-bottom: 1rem;">
        <label for="telefono">Telefono:</label>
        <input type="tel" id="telefono" name="telefono" value="+39 333 123 4567" required>
      </div>
    </fieldset>

    <fieldset style="border: 1px solid var(--card-border); padding: 1rem; border-radius: var(--radius); margin-bottom: 1.5rem;">
      <legend style="padding: 0 0.5rem; font-weight: 600;">Patenti</legend>
      
      <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(100px, 1fr)); gap: 1rem;">
        <label style="display: flex; align-items: center; gap: 0.5rem;">
          <input type="checkbox" name="patenti" value="A" checked>
          <span>Patente A</span>
        </label>
        <label style="display: flex; align-items: center; gap: 0.5rem;">
          <input type="checkbox" name="patenti" value="B" checked>
          <span>Patente B</span>
        </label>
        <label style="display: flex; align-items: center; gap: 0.5rem;">
          <input type="checkbox" name="patenti" value="C">
          <span>Patente C</span>
        </label>
        <label style="display: flex; align-items: center; gap: 0.5rem;">
          <input type="checkbox" name="patenti" value="D">
          <span>Patente D</span>
        </label>
        <label style="display: flex; align-items: center; gap: 0.5rem;">
          <input type="checkbox" name="patenti" value="NAUTICA">
          <span>Patente Nautica</span>
        </label>
      </div>
    </fieldset>

    <fieldset style="border: 1px solid var(--card-border); padding: 1rem; border-radius: var(--radius); margin-bottom: 1.5rem;">
      <legend style="padding: 0 0.5rem; font-weight: 600;">Abilità Professionali</legend>
      
      <p style="font-size: 0.9rem; color: var(--muted); margin: 0 0 1rem 0;">
        Seleziona le abilità che possiedi
      </p>

      <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); gap: 1rem;">
        <label style="display: flex; align-items: center; gap: 0.5rem;">
          <input type="checkbox" name="abilita" value="SOCCORRITORE" checked>
          <span>Soccorritore</span>
        </label>
        <label style="display: flex; align-items: center; gap: 0.5rem;">
          <input type="checkbox" name="abilita" value="INFERMIERE">
          <span>Infermiere</span>
        </label>
        <label style="display: flex; align-items: center; gap: 0.5rem;">
          <input type="checkbox" name="abilita" value="MEDICO">
          <span>Medico</span>
        </label>
        <label style="display: flex; align-items: center; gap: 0.5rem;">
          <input type="checkbox" name="abilita" value="AUTISTA">
          <span>Autista</span>
        </label>
        <label style="display: flex; align-items: center; gap: 0.5rem;">
          <input type="checkbox" name="abilita" value="ARTIFICIERE">
          <span>Artificiere</span>
        </label>
        <label style="display: flex; align-items: center; gap: 0.5rem;">
          <input type="checkbox" name="abilita" value="OPERATORE_HVAC">
          <span>Operatore HVAC</span>
        </label>
      </div>
    </fieldset>

    <div style="display: flex; gap: 1rem; justify-content: flex-start;">
      <button type="submit" class="btn primary">Salva Modifiche</button>
      <button type="reset" class="btn">Annulla</button>
    </div>

  </form>

</div>

<#-- Success message (can be shown/hidden by servlet) -->
<#if successMessage??>
  <div style="margin-top: 1rem; padding: 1rem; background: #d4edda; border: 1px solid #c3e6cb; border-radius: var(--radius); color: #155724;">
    ${successMessage}
  </div>
</#if>

<#-- Error message (can be shown/hidden by servlet) -->
<#if errorMessage??>
  <div style="margin-top: 1rem; padding: 1rem; background: #f8d7da; border: 1px solid #f5c6cb; border-radius: var(--radius); color: #721c24;">
    ${errorMessage}
  </div>
</#if>
