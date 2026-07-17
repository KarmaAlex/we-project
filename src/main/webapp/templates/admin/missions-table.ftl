<#include "/filter-table.ftl">

<#assign columns = [
    {
        "field":"id",
        "label":"ID",
        "sortable":true
    },
    {
        "field":"richiesta_id",
        "label":"Richiesta"
    },
    {
        "field":"squadra",
        "label":"Squadra"
    },
    {
        "field":"obiettivo",
        "label":"Obiettivo"
    },
    {
        "field":"stato",
        "label":"Stato",
        "sortable":true,
        "type":"badge"
    },
    {
        "field": "data_inizio",
        "label": "Data Inizio",
        "sortable": true
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
                "value":"IN_CORSO",
                "label":"In Corso"
            },
            {
                "value":"CHIUSA",
                "label":"Chiusa"
            }
        ]
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

<@filtertable
    section="missions"
    items=missioni![]
    columns=columns
    filters=filters
    hasDetails=true
    detailModalTitle="Dettagli missione"
    editModalTitle="Modifica missione"
    page=page!1
    totalPages=totalPages!1
    hasAddBtn=true
    addModalTitle="Aggiungi missione"
    addModalClass="add-modal"
/>