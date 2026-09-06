<#-- Table macro for paginated data lists -->
<#macro table items columns currentPage totalPages section="list" filters={} path="" hasDetails=false sort="" direction="asc">
<div class="table-container">
  <table class="data-table" data-section="${section}" data-path="${path}" data-sort="${sort}" data-direction="${direction}">
    <thead>
      <tr>
        <#list columns as col>
          <th <#if col.sortable?? && col.sortable>class="sortable" tabindex="0" role="button"</#if> data-field="${col.field}">
            ${col.label}
            <#if col.sortable?? && col.sortable>
              <span class="sort-icon" data-field="${col.field}" aria-hidden="true">⇅</span>
            </#if>
          </th>
        </#list>
        <#if hasDetails>
        <th>Azioni</th>
        </#if>
      </tr>
    </thead>
    <tbody>
      <#if items?has_content>
        <#list items as item>
          <tr data-id="${item.id!''}" class="table-row">
            <#list columns as col>
              <td>
                <#if item[col.field]?? && item[col.field]?is_boolean>
                  <span class="badge ${item[col.field]?string('true', 'false')}">${item[col.field]?string('Si', 'No')}</span>
                <#elseif col.type?? && col.type == "badge">
                  <span class="badge ${col.badgeClass!(item[col.field]!'')?lower_case}">${item[col.field]!'-'}</span>
                <#else>
                  ${item[col.field]!''}
                </#if>
              </td>
            </#list>
            <#switch section>
            <#case "requests">
            <#include "/actions/requests-actions.ftl">
            <#break>
            <#default>
            <#include "/actions/default-actions.ftl">
            </#switch>
          </tr>
        </#list>
      <#else>
        <tr>
          <td colspan="${columns?size}" style="text-align: center; padding: 2rem; color: var(--muted);">
            Nessun risultato trovato
          </td>
        </tr>
      </#if>
    </tbody>
  </table>
</div>

<#-- Pagination footer -->
<#if totalPages gt 1>
  <@pagination currentPage=currentPage totalPages=totalPages section=section filters=filters sort=sort direction=direction />
</#if>
</#macro>

<#-- Macro for individual row rendering (for AJAX updates) -->
<#macro tableRow item columns>
  <tr data-id="${item.id!''}" class="table-row">
    <#list columns as col>
      <td>
        <#if col.type?? && col.type == "badge">
          <span class="badge ${col.badgeClass!(item[col.field]!'')?lower_case}">${item[col.field]!'-'}</span>
        <#else>
          ${item[col.field]!''}
        </#if>
      </td>
    </#list>
  </tr>
</#macro>
