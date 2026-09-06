<#include "/filter-table.ftl">

<#assign columns = [
    {"field":"id", "label":"ID", "sortable":true},
    {"field":"numero", "label":"Numero", "sortable":true},
    {"field":"tipo", "label":"Tipo", "sortable":true}
]>

<#assign filters = [
    {"type":"text", "name":"numero", "label":"Numero", "placeholder":"Numero patente"},
    {
        "type":"select",
        "name":"tipo",
        "label":"Tipo",
        "options":[
            {"value":"", "label":"Tutti"},
            {"value":"AM", "label":"AM"},
            {"value":"A1", "label":"A1"},
            {"value":"A2", "label":"A2"},
            {"value":"A", "label":"A"},
            {"value":"B1", "label":"B1"},
            {"value":"B", "label":"B"},
            {"value":"BE", "label":"BE"},
            {"value":"C1", "label":"C1"},
            {"value":"C", "label":"C"},
            {"value":"CE", "label":"CE"},
            {"value":"C1E", "label":"C1E"},
            {"value":"D", "label":"D"},
            {"value":"D1", "label":"D1"},
            {"value":"D1E", "label":"D1E"},
            {"value":"DE", "label":"DE"},
            {"value":"NAU", "label":"NAU"}
        ]
    }
]>

<@filtertable
    section="licenses"
    items=patenti![]
    columns=columns
    filters=filters
    hasAddBtn=true
    addModalTitle="Aggiungi patente"
    addModalClass="add-modal"
    page=page!1
    totalPages=totalPages!1
/>