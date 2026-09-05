<#include "/filter-table.ftl">

<#assign columns = [
    {"field":"id", "label":"ID", "sortable":true},
    {"field":"nome", "label":"Nome", "sortable":true},
    {"field":"descrizione", "label":"Descrizione"}
]>

<#assign filters = [
    {"type":"text", "name":"nome", "label":"Nome", "placeholder":"Nome abilità"}
]>

<@filtertable
    section="abilities"
    items=abilita![]
    columns=columns
    filters=filters
    hasAddBtn=true
    addModalTitle="Aggiungi abilità"
    addModalClass="add-modal"
    page=page!1
    totalPages=totalPages!1
/>