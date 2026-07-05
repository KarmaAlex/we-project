<#include "/dataTable.ftl">

<#assign columns = [
    {
        "field":"id",
        "label":"ID",
        "sortable":true
    },
    {
        "field":"segnalante",
        "label":"Segnalante",
        "sortable":true
    },
    {
        "field":"indirizzo",
        "label":"Indirizzo"
    },
    {
        "field":"stato",
        "label":"Stato",
        "sortable": true,
        "type": "badge"
    },
    {
        "field":"data_creazione",
        "label":"Data",
        "sortable":true
    },
    {
        "field":"actions",
        "label":"Azioni",
        "type":"actions"
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

<@dataTable
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