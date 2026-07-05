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
        "type":"select",
        "name":"status",
        "label":"Stato",
        "options":[
            {
                "value":"",
                "label":"Tutti"
            },
            {
                "value":"ATTIVA",
                "label":"Attiva"
            },
            {
                "value":"IN_CORSO",
                "label":"In Corso"
            },
            {
                "value":"CHIUSA",
                "label":"Chiusa"
            },
            {
                "value":"IGNORATA",
                "label":"Ignorata"
            }
        ]
    },
    {
        "type":"email",
        "name":"segnalante",
        "placeholder":"Email",
        "label":"Email"
    },
    {
        "type":"date",
        "name":"data_from",
        "label":"Data da"
    },
    {
        "type":"date",
        "name":"data_to",
        "label":"Data a"
    }
]>

<@dataTable
    section="requests"
    items=richieste![]
    columns=columns
    filters=filters
    detailEndpoint="/api/admin/requests"
    modalId="requestsDetailModal"
    modalTitle="Dettagli richiesta"
    page=page!1
    totalPages=totalPages!1
/>