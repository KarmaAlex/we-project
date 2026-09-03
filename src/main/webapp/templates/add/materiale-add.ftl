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
}
]>

<@form
    id="materialeForm"
    csrfToken=csrfToken
    action="${ctx}/api/add/materials"
    method="post"
    fields=materialeFields
    submitLabel="Crea materiale"
    resetLabel="Annulla"
    cssClass="edit-modal"
/>
