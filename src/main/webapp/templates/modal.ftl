<#-- Modal macro for popups -->
<#macro modal id="detailModal" title="Dettagli">
<div class="modal-overlay" id="${id}">
  <div class="modal-dialog">
    <div class="modal-header">
      <h3>${title}</h3>
      <button class="modal-close" data-dismiss="modal" aria-label="Chiudi">&times;</button>
    </div>
    <div class="modal-body">
      <#nested />
    </div>
  </div>
</div>
</#macro>

<#-- Macro for opening modal via AJAX -->
<#macro modalTrigger id="detailModal" href="#" label="Visualizza">
<a href="${href}" class="btn modal-trigger" data-modal="${id}" data-toggle="modal">
  ${label}
</a>
</#macro>

<#-- Timeline component for modal details -->
<#macro timeline items>
<div class="timeline">
  <#list items as item>
    <div class="timeline-item">
      <div class="timeline-dot"></div>
      <div class="timeline-time">${item.timestamp!''}</div>
      <div class="timeline-content">
        <#if item.title??>
          <strong>${item.title}</strong><br />
        </#if>
        ${item.content!''}
      </div>
    </div>
  </#list>
</div>
</#macro>
