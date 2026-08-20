<#include "/filter-table.ftl">

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

<@filtertable
    section="vehicles"
    items=mezzi![]
    columns=columns
    filters=filters
    hasDetails=true
    detailModalTitle="Dettagli operatore"
    editModalTitle="Modifica operatore"
    hasAddBtn=true
    addModalTitle="Aggiungi mezzo"
    addModalClass="add-modal"
    page=page!1
    totalPages=totalPages!1
/>