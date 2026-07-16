<#include "/filter-table.ftl">

<#assign columns = [
    {
        "field":"id",
        "label":"ID",
        "sortable":true
    }
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
    detailEndpoint="/api/admin/materials"
    modalId="materialsDetailModal"
    modalTitle="Dettagli materiale"
    page=page!1
    totalPages=totalPages!1
/>