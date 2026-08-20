<#include "/form.ftl">

<#assign materialeFields = [
{
    "type":"text",
    "name":"nome",
    "label":"Nome",
    "value":"",
    "required":true,
    "placeholder":"Nome del materiale"
},
{
    "type":"textarea",
    "name":"descrizione",
    "label":"Descrizione",
    "value":"",
    "rows":4,
    "required":true,
    "placeholder":"Descrizione del materiale"
},
{
    "type":"number",
    "name":"quantita",
    "label":"Quantità",
    "value":0,
    "required":true,
    "attrs":{
        "min":"0",
        "step":"1"
    }
}
]>

<@form
    id="materialeForm"
    action="${ctx}/api/add/materials"
    method="post"
    fields=materialeFields
    submitLabel="Crea materiale"
    resetLabel="Annulla"
    cssClass="edit-modal"
/>
