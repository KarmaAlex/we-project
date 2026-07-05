<#macro dataTable
    section
    items
    columns
    filters=[]
    detailEndpoint=""
    page=1
    totalPages=1
    modalId="detailModal"
    modalTitle="Dettagli"
>

    <#if filters?has_content>
        <div class="filters-section">
            <h3>Filtri</h3>
            <form method="GET" class="filters-form">
                <input
                    type="hidden"
                    name="section"
                    value="${section}"
                >
                <div class="filter-group">
                    <#list filters as filter>
                        <div class="filter-item">
                            <label for="${filter.name}">
                                ${filter.label}
                            </label>
                            <#switch filter.type>
                                <#case "text">
                                    <input
                                        type="text"
                                        id="filter${filter.name}"
                                        name="${filter.name}"
                                        placeholder="${filter.placeholder!''}"
                                    >
                                    <#-- TODO: ricorda il valore dei filtri passandoli dal contesto value="${RequestParameters[filter.name]!}" -->
                                    <#break>
                                <#case "date">
                                    <input
                                        type="date"
                                        id="filter${filter.name}"
                                        name="${filter.name}"
                                    >
                                    <#-- value="${RequestParameters[filter.name]!}" -->
                                    <#break>
                                <#case "select">
                                    <select
                                        id="filter${filter.name}"
                                        name="${filter.name}"
                                    >
                                        <#list filter.options as option>
                                            <option
                                                value="${option.value}"
                                            >
                                            <#--<#if RequestParameters[filter.name]! == option.value>
                                                    selected
                                                </#if>-->
                                                ${option.label}
                                            </option>
                                        </#list>
                                    </select>
                                    <#break>
                                <#case "email">
                                <input
                                        type="email"
                                        id="filter${filter.name}"
                                        name="${filter.name}"
                                        placeholder="${filter.placeholder!''}"
                                    >
                                    <#break>
                            </#switch>
                        </div>
                    </#list>
                </div>

                <div class="filter-actions">
                    <button
                        class="btn primary"
                        type="submit"
                    >
                        Filtra
                    </button>
                    <button
                        class="btn"
                        type="reset"
                    >
                        Azzera
                    </button>
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
    />

    <@modal
        id=modalId
        title=modalTitle
    >
        <p id="modalContent">
            Caricamento...
        </p>
    </@modal>

    <#if detailEndpoint?has_content>
        <script>
        (() => {
            const endpoint = '${ctx}${detailEndpoint}';
            const modal = '${modalId}';
            document
                .querySelectorAll("table.data-table tbody tr")
                .forEach(row => {
                    const id = row.dataset.id;
                    if (!id) return;
                    const actionCell = row.querySelector("td:last-child");
                    if (!actionCell) return;
                    const button = document.createElement("a");
                    button.href = endpoint + "/" + id + "/detail";
                    button.className = "btn modal-trigger";
                    button.dataset.modal = modal;
                    button.dataset.toggle = "modal";
                    button.textContent = "Visualizza";
                    actionCell.replaceChildren(button);
                });
        })();
        </script>

    </#if>

</#macro>