<#-- Operator: Profile Section -->

<div class="card" style="max-width: 800px;">
  <h3>Profilo Personale</h3>
  
  <form method="POST" action="${ctx}/api/operator/profile/update" class="profile-form">
    <input type="hidden" name="csrf" value="${csrfToken!''}">
    
    <fieldset style="border: 1px solid var(--card-border); padding: 1rem; border-radius: var(--radius); margin-bottom: 1.5rem;">
      <legend style="padding: 0 0.5rem; font-weight: 600;">Dati Anagrafici</legend>
      
      <div style="margin-bottom: 1rem;">
        <label for="nome">Nome:</label>
        <input type="text" id="nome" name="nome" value="${(anagrafica.nome)!''}" required>
      </div>

      <div style="margin-bottom: 1rem;">
        <label for="cognome">Cognome:</label>
        <input type="text" id="cognome" name="cognome" value="${(anagrafica.cognome)!''}" required>
      </div>

      <div style="margin-bottom: 1rem;">
        <label for="email">Email:</label>
        <input type="email" id="email" name="email" value="${email!''}" required disabled>
        <small style="color: var(--muted);">Non modificabile</small>
      </div>

      <div style="margin-bottom: 1rem;">
        <label for="cf">Codice fiscale:</label>
        <input type="text" id="cf" name="cf" value="${(anagrafica.cf)!''}" readonly disabled>
        <small style="color: var(--muted);">Non modificabile</small>
      </div>

    <fieldset style="border: 1px solid var(--card-border); padding: 1rem; border-radius: var(--radius); margin-bottom: 1.5rem;">
      <legend style="padding: 0 0.5rem; font-weight: 600;">Patenti</legend>
      <div style="display: grid; gap: 0.5rem;">
        <#list patenti![] as patente>
          <span>Patente ${patente.numero!''} (${patente.tipo!''})</span>
        <#else>
          <span>Nessuna patente registrata</span>
        </#list>
      </div>
      <small style="color: var(--muted);">Le patenti sono assegnate esclusivamente dall'amministratore.</small>
    </fieldset>

    <fieldset style="border: 1px solid var(--card-border); padding: 1rem; border-radius: var(--radius); margin-bottom: 1.5rem;">
      <legend style="padding: 0 0.5rem; font-weight: 600;">Abilità Professionali</legend>
      
      <p style="font-size: 0.9rem; color: var(--muted); margin: 0 0 1rem 0;">
        Seleziona le abilità che possiedi
      </p>

      <div style="display: grid; gap: 0.5rem;">
        <select id="abilita" name="abilita" multiple size="6">
          <#list abilitaDisponibili![] as abilitaItem>
            <option value="${abilitaItem.key}" <#if (abilitaSelezionate![])?seq_contains(abilitaItem.key?string)>selected</#if>>${abilitaItem.nome!abilitaItem.desc!''}</option>
          </#list>
        </select>
        <#if !(abilitaDisponibili![])?has_content>
          <span>Nessuna abilità registrata</span>
        </#if>
      </div>
      <div style="display: flex; gap: 1rem; align-items: end; margin-top: 1rem;">
        <label for="nuovaAbilita">Aggiungi abilità mancante:
          <input type="text" id="nuovaAbilita" name="nome" form="addAbilityForm" required>
        </label>
        <button type="submit" form="addAbilityForm" class="btn">Crea e aggiungi</button>
      </div>
    </fieldset>

    <div style="display: flex; gap: 1rem; justify-content: flex-start;">
      <button type="submit" class="btn primary">Salva Modifiche</button>
      <button type="reset" class="btn">Annulla</button>
    </div>

  </form>

  <form id="addAbilityForm" method="POST" action="${ctx}/api/operator/profile/abilities">
    <input type="hidden" name="csrf" value="${csrfToken!''}">
  </form>

</div>

<#-- Success message (can be shown/hidden by servlet) -->
<#if successMessage??>
  <div class="profile-alert profile-alert-success">
    ${successMessage}
  </div>
</#if>

<#-- Error message (can be shown/hidden by servlet) -->
<#if errorMessage??>
  <div class="profile-alert profile-alert-error">
    ${errorMessage}
  </div>
</#if>
