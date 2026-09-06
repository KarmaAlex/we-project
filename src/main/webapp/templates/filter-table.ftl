<#include "table.ftl">
<#include "modal.ftl">
<#macro filtertable
    section
    items
    columns
    filters=[]
    hasDetails=false
    page=1
    totalPages=1
    detailModalTitle="Dettagli"
    editModalTitle="Modifica"
    hasAddBtn=false
    addModalTitle="Aggiungi"
    addModalClass="modal-dialog"
>
    <#assign activeFilters = {}>
    <#list filters as filter>
        <#assign activeValue = (RequestParameters[filter.name])!''>
        <#if activeValue?has_content>
            <#assign activeFilters = activeFilters + {filter.name: activeValue}>
        </#if>
    </#list>

    <#if filters?has_content>
        <div class="filters-section">
            <h3>Filtri</h3>
            <form method="GET" class="filters-form">
                <input type="hidden" name="section" value="${section}">
                <div class="filter-group">
                    <#list filters as filter>
                        <div class="filter-item">
                            <label for="${filter.name}">
                                ${filter.label}
                            </label>
                            <#switch filter.type>
                                <#case "text">
                                    <input type="text" id="filter${filter.name}" name="${filter.name}" data-filter-field="${filter.name}" placeholder="${filter.placeholder!''}" value="${(RequestParameters[filter.name])!''}" >
                                    <#-- TODO: ricorda il valore dei filtri passandoli dal contesto value="${RequestParameters[filter.name]!}" -->
                                    <#break>
                                <#case "date">
                                    <input type="date" id="filter${filter.name}" name="${filter.name}" data-filter-field="${filter.name}" value="${(RequestParameters[filter.name])!''}" >
                                    <#-- value="${RequestParameters[filter.name]!}" -->
                                    <#break>
                                <#case "select">
                                    <select id="filter${filter.name}" name="${filter.name}" data-filter-field="${filter.name}" >
                                        <#list filter.options as option>
                                            <option value="${option.value}" <#if (RequestParameters[filter.name])!'' == option.value>selected</#if>>
                                                ${option.label}
                                            </option>
                                        </#list>
                                    </select>
                                    <#break>
                                <#case "email">
                                <input type="email" id="filter${filter.name}" name="${filter.name}" placeholder="${filter.placeholder!''}" >
                                    <#break>
                            </#switch>
                        </div>
                    </#list>
                </div>

                <div class="filter-actions">
                    <button id="filter-submit" class="btn primary" type="submit">Filtra</button>
                    <button class="btn" type="reset">Reset</button>
                </div>
            </form>
        </div>
    </#if>

    <@table
        items=items
        columns=columns
        currentPage=page
        totalPages=totalPages
        section=section
        hasDetails=hasDetails
        filters=activeFilters
        sort=sort!''
        direction=direction!'asc'
    />
    <#if hasAddBtn>
        <div class="add-section">
            <a class="btn primary modal-trigger" href="${ctx}/api/add/${section}" data-modal="${section}AddModal" data-toggle="modal">Aggiungi</a>
        </div>
        <@modal id=section+"AddModal" title=addModalTitle cssClass=addModalClass>
            <p id="addModalContent">Caricamento...</p>
        </@modal>
    </#if>

    <#if hasDetails>
        <@modal id=section+"DetailModal" title=detailModalTitle >
            <p id="detailModalContent">Caricamento...</p>
        </@modal>
    </#if>

    <#if section == "vehicles" || section == "materials">
        <@modal id=section+"DeleteModal" title="Elimina">
            <p id="deleteModalContent">Caricamento...</p>
        </@modal>
    </#if>
    

    <@modal id=section+"EditModal" title=editModalTitle >
        <p id="editModalContent">Caricamento...</p>
    </@modal>

</#macro>