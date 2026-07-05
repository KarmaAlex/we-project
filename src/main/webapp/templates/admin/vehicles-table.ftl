<#include "/dataTable.ftl">

<#assign columns = [
    {
        "field":"id",
        "label":"ID",
        "sortable":true
    },
    {
        "field":"nome",
        "label":"Nome",
        "sortable":true
    },
    {
        "field":"descrizione",
        "label":"Descrizione"
    },
    {
        "field":"targa",
        "label":"Targa"
    },
    {
        "field":"stato",
        "label":"Stato",
        "sortable":true,
        "type":"badge"
    },
    {
        "field":"missione_corrente",
        "label":"Missione"
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
        "placeholder":"Nome mezzo"
    },
    {
        "type":"select",
        "name":"stato",
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
    section="vehicles"
    items=mezzi![]
    columns=columns
    filters=filters
    detailEndpoint="/api/admin/vehicles"
    modalId="vehicleDetailModal"
    modalTitle="Dettagli mezzo"
    page=page!1
    totalPages=totalPages!1
/>