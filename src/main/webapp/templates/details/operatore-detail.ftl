<#-- Detail: Operatore -->
<#if dettaglio??>
  <div style="padding: 1rem;">
    
    <h4 style="margin-top: 0;">Profilo Operatore</h4>

    <div style="margin-bottom: 1.5rem; background: #f9fafb; padding: 1rem; border-radius: 4px;">
      <strong>Nome:</strong> ${dettaglio.nome!''} ${dettaglio.cognome!''}<br>
      <strong>Email:</strong> ${dettaglio.email!''}<br>
      <strong>Telefono:</strong> ${dettaglio.telefono!''}<br>
      <strong>Stato:</strong> <span class="badge ${(dettaglio.stato!'')?lower_case}">${dettaglio.stato!''}</span><br>
      <#if dettaglio.missione_corrente?has_content>
        <strong>Missione Corrente:</strong> <a class="btn modal-trigger" href="${ctx}/api/detail/missions?id=${dettaglio.missione_corrente}"  data-modal="missionsDetailModal" data-toggle="modal">${dettaglio.missione_corrente}</a>
      </#if>
    </div>

    <#if dettaglio.patenti??>
      <div style="margin-bottom: 1.5rem;">
        <strong>Patenti:</strong><br>
        <div style="display: flex; gap: 0.5rem; flex-wrap: wrap; margin-top: 0.5rem;">
          <#list dettaglio.patenti as p>
            <span style="background: var(--accent); color: white; padding: 0.25rem 0.75rem; border-radius: 12px; font-size: 0.85rem; font-weight: 600;">
              ${p}
            </span>
          </#list>
        </div>
      </div>
    </#if>

    <#if dettaglio.abilita??>
      <div style="margin-bottom: 1.5rem;">
        <strong>Abilità Professionali:</strong><br>
        <ul style="margin: 0.5rem 0; padding-left: 1.5rem;">
          <#list dettaglio.abilita as a>
            <li>${a}</li>
          </#list>
        </ul>
      </div>
    </#if>

    <#if dettaglio.storico_missioni??>
      <div>
        <strong>Storico Missioni:</strong>
        <table style="width: 100%; margin-top: 0.5rem; border-collapse: collapse;">
          <thead>
            <tr style="border-bottom: 2px solid #eef0f4;">
              <th style="text-align: left; padding: 0.5rem; font-weight: 600;">Missione</th>
              <th style="text-align: left; padding: 0.5rem; font-weight: 600;">Data</th>
              <th style="text-align: left; padding: 0.5rem; font-weight: 600;">Descrizione</th>
              <th style="text-align: left; padding: 0.5rem; font-weight: 600;">Esito</th>
            </tr>
          </thead>
          <tbody>
            <#list dettaglio.storico_missioni as hist>
              <tr style="border-bottom: 1px solid #eef0f4;">
                <td style="padding: 0.5rem;"><strong>${hist.missione_id!''}</strong></td>
                <td style="padding: 0.5rem;">${hist.data!''}</td>
                <td style="padding: 0.5rem;">${hist.descrizione!''}</td>
                <td style="padding: 0.5rem;"><span class="badge">${hist.esito!''}</span></td>
              </tr>
            </#list>
          </tbody>
        </table>
      </div>
    </#if>

  </div>
<#else>
  <p>Dati non disponibili</p>
</#if>
