<#include "/form.ftl">

<#assign mezzoFields = [
{
    "type":"text",
    "name":"nome",
    "label":"Nome",
    "value":"",
    "required":true,
    "placeholder":"Nome del mezzo"
},
{
    "type":"textarea",
    "name":"descrizione",
    "label":"Descrizione",
    "value":"",
    "rows":4,
    "required":true,
    "placeholder":"Descrizione del mezzo"
},
{
    "type":"text",
    "name":"targa",
    "label":"Targa",
    "value":"",
    "required":true,
    "placeholder":"AB123CD",
    "maxlength":10
}
]>

<@form
    id="mezzoForm"
    csrfToken=csrfToken
    action="${ctx}/api/add/vehicles"
    method="post"
    fields=mezzoFields
    submitLabel="Crea mezzo"
    resetLabel="Annulla"
    cssClass="edit-modal"
/>
