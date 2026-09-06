<#-- Detail: Richiesta -->
<#if dettaglio??>
  <div style="padding: 1rem;">
    
    <h4 style="margin-top: 0;">ID Richiesta: <strong>${dettaglio.id!''}</strong></h4>
    
    <div style="margin-bottom: 1rem;">
      <strong>Segnalante:</strong> ${dettaglio.segnalante!''}<br>
      <strong>Email:</strong> ${dettaglio.email!''}<br>
      <strong>Posizione:</strong> ${dettaglio.posizione!''}<br>
      <strong>Stato:</strong> <span class="badge ${(dettaglio.stato!'')?lower_case}">${dettaglio.stato!''}</span><br>
      <strong>Data:</strong> ${dettaglio.data_creazione!''}<br>
      <strong>Verificato:</strong> <#if dettaglio.verificato??><#if dettaglio.verificato>Sì<#else>No</#if><#else>N/A</#if>
    </div>

    <div style="margin-bottom: 1rem;">
      <strong>Descrizione:</strong><br>
      <p style="background: #f9fafb; padding: 0.75rem; border-radius: 4px; color: #555;">
        ${dettaglio.descrizione!''}
      </p>
    </div>

    <#if dettaglio.foto_url??>
      <div style="margin-bottom: 1rem;">
        <strong>Foto:</strong><br>
        <img src="uploads/${dettaglio.foto_url}" alt="Foto richiesta" style="max-width: 100%; max-height: 300px; border-radius: 4px;">
      </div>
    </#if>

  </div>
<#else>
  <p>Dati non disponibili</p>
</#if>
