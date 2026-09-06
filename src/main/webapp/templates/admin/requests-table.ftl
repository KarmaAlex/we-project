<#include "/filter-table.ftl">

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
        "field":"email",
        "label":"Email"
    },
    {
        "field":"posizione",
        "label":"Posizione"
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
                "value":"IN_ATTESA",
                "label":"In attesa"
            },
            {
                "value":"IN_CORSO",
                "label":"In Corso"
            },
            {
                "value":"COMPLETATA",
                "label":"Completata"
            },
            {
                "value":"CHIUSA",
                "label":"Chiusa"
            },
            {
                "value":"RIFIUTATA",
                "label":"Rifiutata"
            },
            {
                "value":"ANNULLATA",
                "label":"Annullata"
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
        "label":"Data inizio"
    },
    {
        "type":"date",
        "name":"data_to",
        "label":"Data fine"
    }
]>

<@filtertable
    section="requests"
    items=richieste![]
    columns=columns
    filters=filters
    hasDetails=true
    detailModalTitle="Dettagli richieata"
    editModalTitle="Modifica richiesta"
    page=page!1
    totalPages=totalPages!1
/>

<@modal id="createMissionModal" title="Crea missione" >
        <p id="createMissionModalContent">Caricamento...</p>
    </@modal>