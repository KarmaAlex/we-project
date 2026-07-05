<#-- Detail: Missione -->
<#if dettaglio??>
  <div style="padding: 1rem;">
    
    <h4 style="margin-top: 0;">ID Missione: <strong>${dettaglio.id!''}</strong></h4>

    <div style="margin-bottom: 1.5rem; background: #f9fafb; padding: 1rem; border-radius: 4px;">
      <strong>Richiesta:</strong> ${dettaglio.richiesta_id!''}<br>
      <strong>Stato:</strong> <span class="badge ${(dettaglio.stato!'')?lower_case}">${dettaglio.stato!''}</span><br>
      <strong>Data Inizio:</strong> ${dettaglio.data_inizio!''}<br>
      <#if dettaglio.data_fine?has_content>
        <strong>Data Fine:</strong> ${dettaglio.data_fine!''}<br>
      </#if>
      <strong>Obiettivo:</strong> ${dettaglio.obiettivo!''}<br>
      <strong>Indirizzo:</strong> ${dettaglio.indirizzo!''}
    </div>

    <div style="margin-bottom: 1.5rem;">
      <strong>Squadra:</strong><br>
      <strong>Caposquadra:</strong> ${dettaglio.caposquadra!''}<br>
      <#if dettaglio.operatori??>
        <ul style="margin: 0.5rem 0; padding-left: 1.5rem;">
          <#list dettaglio.operatori as op>
            <li>${op.nome!''} (${op.ruolo!''})</li>
          </#list>
        </ul>
      </#if>
    </div>

    <#if dettaglio.mezzi??>
      <div style="margin-bottom: 1.5rem;">
        <strong>Mezzi:</strong><br>
        <ul style="margin: 0.5rem 0; padding-left: 1.5rem;">
          <#list dettaglio.mezzi as m>
            <li>${m}</li>
          </#list>
        </ul>
      </div>
    </#if>

    <#if dettaglio.materiali??>
      <div style="margin-bottom: 1.5rem;">
        <strong>Materiali:</strong><br>
        <ul style="margin: 0.5rem 0; padding-left: 1.5rem;">
          <#list dettaglio.materiali as mat>
            <li>${mat}</li>
          </#list>
        </ul>
      </div>
    </#if>

    <#-- TODO: updates are misaligned, fix -->
    <#if dettaglio.aggiornamenti??>
      <div style="margin-bottom: 1rem;">
        <strong>Aggiornamenti:</strong>
        <div class="timeline" style="margin-top: 1rem;">
          <#list dettaglio.aggiornamenti as agg>
            <div class="timeline-item">
              <div class="timeline-dot"></div>
              <div class="timeline-time">${agg.timestamp!''}</div>
              <#if agg.title??>
                <div style="font-weight: 600; margin-top: 0.25rem;">${agg.title}</div>
              </#if>
              <div class="timeline-content">${agg.content!''}</div>
            </div>
          </#list>
        </div>
      </div>
    </#if>

  </div>
<#else>
  <p>Dati non disponibili</p>
</#if>
