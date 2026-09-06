<#if dettaglio??>
  <div style="padding: 1rem;">
    <h4 style="margin-top: 0;">Squadra #${dettaglio.id!''}</h4>
    <div style="margin-bottom: 1.5rem; background: #f9fafb; padding: 1rem; border-radius: 4px;">
      <strong>Caposquadra:</strong> ${dettaglio.capo!'-'}<br>
      <strong>Stato:</strong> <span class="badge ${(dettaglio.stato!'')?lower_case}">${dettaglio.stato!''}</span><br>
      <#if dettaglio.missione_corrente??>
        <strong>Missione corrente:</strong>
        <a href="${ctx}/admin-dashboard?section=missions">${dettaglio.missione_corrente}</a>
      </#if>
    </div>
    <div>
      <strong>Membri:</strong>
      <#if dettaglio.membri?has_content>
        <ul style="margin: 0.5rem 0; padding-left: 1.5rem;">
          <#list dettaglio.membri as membro>
            <li>${membro}</li>
          </#list>
        </ul>
      <#else>
        <p>Nessun membro assegnato.</p>
      </#if>
    </div>
  </div>
<#else>
  <p>Dati non disponibili</p>
</#if>