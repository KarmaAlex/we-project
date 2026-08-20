<#include "/filter-table.ftl">

<#assign columns = [
    {
        "field":"id",
        "label":"ID",
        "sortable":true
    },
    {
        "field":"stato",
        "label":"Stato",
        "sortable": true,
        "type": "badge"
    }
]>

<#assign filters = [
    {
        "type":"text",
        "name":"nome",
        "label":"Nome",
        "placeholder":"Nome materiale"
    },
    {
        "type":"select",
        "name":"status",
        "label":"Stato",
        "options":[
            {
                "value":"",
                "label":"Tutti"
            },
            {
                "value":"DISPONIBILE",
                "label":"Disponibile"
            },
            {
                "value":"OCCUPATO",
                "label":"In missione"
            }
        ]
    }
]>

<@filtertable
    section="materials"
    items=materiali![]
    columns=columns
    filters=filters
    hasDetails=true
    detailModalTitle="Dettagli materiale"
    editModalTitle="Modifica materiale"
    hasAddBtn=true
    addModalTitle="Aggiungi materiale"
    addModalClass="add-modal"
    page=page!1
    totalPages=totalPages!1
/>