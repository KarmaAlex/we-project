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
        "field":"email",
        "label":"Email"
    },
    {
        "field":"telefono",
        "label":"Telefono"
    },
    {
        "field":"stato",
        "label":"Stato",
        "sortable":true,
        "type": "badge"
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
        "placeholder":"Cognome o nome"
    },
    {
        "type":"select",
        "name":"stato",
        "label":"Email"
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
    section="operators"
    items=operatori![]
    columns=columns
    filters=filters
    detailEndpoint="/api/admin/operators"
    modalId="operatorsDetailModal"
    modalTitle="Dettagli operatore"
    page=page!1
    totalPages=totalPages!1
/>