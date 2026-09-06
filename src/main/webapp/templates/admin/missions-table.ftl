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
        "field":"successo",
        "label":"Successo",
        "sortable":true
    },
    {
        "field":"completata",
        "label":"Completata",
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
        "name":"completata",
        "label":"Completata",
        "options":[
            {
                "value":"",
                "label":"Tutti"
            },
            {
                "value":"false",
                "label":"In corso"
            },
            {
                "value":"true",
                "label":"Completata"
            }
        ]
    },
    {
        "type":"select",
        "name":"successo",
        "label":"Successo",
        "options":[
            {
                "value":"",
                "label":"Tutti"
            },
            {
                "value":"0",
                "label":"0"
            },
            {
                "value":"1",
                "label":"1"
            },
            {
                "value":"2",
                "label":"2"
            },
            {
                "value":"3",
                "label":"3"
            },
            {
                "value":"4",
                "label":"4"
            },
            {
                "value":"5",
                "label":"5"
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
/>