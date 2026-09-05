<#include "/filter-table.ftl">

<#assign columns = [
    {"field":"id", "label":"ID", "sortable":true},
    {"field":"numero", "label":"Numero", "sortable":true},
    {"field":"tipo", "label":"Tipo", "sortable":true}
]>

<#assign filters = [
    {"type":"text", "name":"numero", "label":"Numero", "placeholder":"Numero patente"}
]>

<@filtertable
    section="licenses"
    items=patenti![]
    columns=columns
    filters=filters
    hasAddBtn=true
    addModalTitle="Aggiungi patente"
    addModalClass="add-modal"
    page=page!1
    totalPages=totalPages!1
/>