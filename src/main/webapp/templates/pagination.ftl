<#-- Pagination macro -->
<#macro pagination currentPage totalPages section="list" filters={} path="">
<div class="pagination-section">
  <div class="pagination-info">
    Pagina ${currentPage} di ${totalPages}
  </div>
  <div class="pagination-controls">
    <#-- Previous button -->
    <#if currentPage gt 1>
      <a href="?section=${section}&page=${currentPage - 1}" class="page-link" data-page="${currentPage - 1}" data-section="${section}">← Precedente</a>
    <#else>
      <span class="page-link disabled">← Precedente</span>
    </#if>

    <#-- Page numbers -->
    <#assign startPage = ((currentPage - 2)?int)?max(1)>
    <#assign endPage = ((currentPage + 2)?int)?min(totalPages)>
    
    <#if startPage gt 1>
      <a href="?section=${section}&page=1" class="page-link" data-page="1" data-section="${section}">1</a>
      <#if startPage gt 2>
        <span class="page-link">...</span>
      </#if>
    </#if>

    <#list startPage..endPage as page>
      <#if page == currentPage>
        <span class="page-link current">${page}</span>
      <#else>
        <a href="?section=${section}&page=${page}" class="page-link" data-page="${page}" data-section="${section}">${page}</a>
      </#if>
    </#list>

    <#if endPage lt totalPages>
      <#if endPage lt (totalPages - 1)>
        <span class="page-link">...</span>
      </#if>
      <a href="?section=${section}&page=${totalPages}" class="page-link" data-page="${totalPages}" data-section="${section}">${totalPages}</a>
    </#if>

    <#-- Next button -->
    <#if currentPage lt totalPages>
      <a href="?section=${section}&page=${currentPage + 1}" class="page-link" data-page="${currentPage + 1}" data-section="${section}">Successiva →</a>
    <#else>
      <span class="page-link disabled">Successiva →</span>
    </#if>
  </div>
</div>
</#macro>
