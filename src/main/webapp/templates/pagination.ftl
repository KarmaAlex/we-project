<#-- Pagination macro -->
<#macro pagination currentPage totalPages section="list" filters={} path="" sort="" direction="asc">
<#assign query = "section=" + section>
<#list filters as key, value>
  <#if value?? && value?has_content>
    <#assign query = query + "&" + key + "=" + value?url>
  </#if>
</#list>
<#if sort?has_content>
  <#assign query = query + "&sort=" + sort?url + "&direction=" + direction?url>
</#if>
<div class="pagination-section">
  <div class="pagination-info">
    Pagina ${currentPage} di ${totalPages}
  </div>
  <div class="pagination-controls">
    <#-- Previous button -->
    <#if currentPage gt 1>
      <a href="?${query}&page=${currentPage - 1}" class="page-link" data-page="${currentPage - 1}" data-section="${section}">← Precedente</a>
    <#else>
      <span class="page-link disabled">← Precedente</span>
    </#if>

    <#-- Page numbers -->
    <#assign startPage = currentPage - 2>
    <#if startPage lt 1>
      <#assign startPage = 1>
    </#if>
    <#assign endPage = currentPage + 2>
    <#if endPage gt totalPages>
      <#assign endPage = totalPages>
    </#if>
    
    <#if startPage gt 1>
      <a href="?${query}&page=1" class="page-link" data-page="1" data-section="${section}">1</a>
      <#if startPage gt 2>
        <span class="page-link">...</span>
      </#if>
    </#if>

    <#list startPage..endPage as page>
      <#if page == currentPage>
        <span class="page-link current">${page}</span>
      <#else>
        <a href="?${query}&page=${page}" class="page-link" data-page="${page}" data-section="${section}">${page}</a>
      </#if>
    </#list>

    <#if endPage lt totalPages>
      <#if endPage lt (totalPages - 1)>
        <span class="page-link">...</span>
      </#if>
      <a href="?${query}&page=${totalPages}" class="page-link" data-page="${totalPages}" data-section="${section}">${totalPages}</a>
    </#if>

    <#-- Next button -->
    <#if currentPage lt totalPages>
      <a href="?${query}&page=${currentPage + 1}" class="page-link" data-page="${currentPage + 1}" data-section="${section}">Successiva →</a>
    <#else>
      <span class="page-link disabled">Successiva →</span>
    </#if>
  </div>
</div>
</#macro>
