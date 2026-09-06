<#include "/filter-table.ftl">

<#assign columns = [
    {"field":"id", "label":"ID", "sortable":true},
    {"field":"capo", "label":"Capo squadra", "sortable":true},
    {"field":"membri", "label":"Membri", "sortable":true},
    {"field":"stato", "label":"Stato", "sortable":true, "type":"badge"},
    {"field":"missione_corrente", "label":"Missione"}
]>

<#assign filters = [
    {"type":"text", "name":"capo", "label":"Capo squadra", "placeholder":"Nome o cognome"},
    {"type":"select", "name":"stato", "label":"Stato", "options":[
        {"value":"", "label":"Tutte"},
        {"value":"DISPONIBILE", "label":"Disponibile"},
        {"value":"OCCUPATA", "label":"In missione"}
    ]}
]>

<@filtertable
    section="teams"
    items=squadre![]
    columns=columns
    filters=filters
    hasDetails=true
    detailModalTitle="Dettagli squadra"
    hasAddBtn=true
    addModalTitle="Aggiungi squadra"
    addModalClass="add-modal"
    page=page!1
    totalPages=totalPages!1
/>