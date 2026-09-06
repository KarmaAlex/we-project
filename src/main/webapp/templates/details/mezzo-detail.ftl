<#-- Detail: Mezzo -->
<#if dettaglio??>
  <div style="padding: 1rem;">
    
    <h4 style="margin-top: 0;">Dettagli Mezzo</h4>

    <div style="margin-bottom: 1.5rem; background: #f9fafb; padding: 1rem; border-radius: 4px;">
      <strong>Nome:</strong> ${dettaglio.nome!''}<br>
      <strong>Descrizione:</strong> ${dettaglio.descrizione!''}<br>
      <strong>Targa:</strong> ${dettaglio.targa!''}<br>
      <strong>Stato:</strong> <span class="badge ${(dettaglio.stato!'')?lower_case}">${dettaglio.stato!''}</span><br>
      <#-- TODO: Add ref to current mission, go to missions tab and open detail for that mission  -->
      <#if dettaglio.missione_corrente?has_content>
        <strong>Missione Corrente:</strong> <a href="${ctx}/admin-dashboard?section=missions">${dettaglio.missione_corrente}</a>
      </#if>
    </div>

    <#if dettaglio.storico_missioni??>
      <div>
        <strong>Storico Missioni:</strong>
        <table style="width: 100%; margin-top: 0.5rem; border-collapse: collapse;">
          <thead>
            <tr style="border-bottom: 2px solid #eef0f4;">
              <th style="text-align: left; padding: 0.5rem; font-weight: 600;">Missione</th>
              <th style="text-align: left; padding: 0.5rem; font-weight: 600;">Data</th>
              <th style="text-align: left; padding: 0.5rem; font-weight: 600;">Descrizione</th>
            </tr>
          </thead>
          <tbody>
            <#list dettaglio.storico_missioni as hist>
              <tr style="border-bottom: 1px solid #eef0f4;">
                <td style="padding: 0.5rem;"><strong>${hist.missione_id!''}</strong></td>
                <td style="padding: 0.5rem;">${hist.data!''}</td>
                <td style="padding: 0.5rem;">${hist.descrizione!''}</td>
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
