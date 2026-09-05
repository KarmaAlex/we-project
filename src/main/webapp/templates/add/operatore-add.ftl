<#include "/form.ftl">

<#assign operatoreFields = [
{
    "type":"text",
    "name":"nome",
    "label":"Nome",
    "value":"",
    "required":true
},
{
    "type":"text",
    "name":"cognome",
    "label":"Cognome",
    "value":"",
    "required":true
},
{
    "type":"text",
    "name":"nome_utente",
    "label":"Nome utente",
    "value":"",
    "required":true,
    "autocomplete":"username"
},
{
    "type":"email",
    "name":"email",
    "label":"Email",
    "value":"",
    "required":true
},
{
    "type":"select",
    "name":"patente",
    "label":"Patente",
    "options":patentiDisponibili![],
    "placeholder":"Nessuna patente"
}
]>

<@form
    id="operatoreForm"
    csrfToken=csrfToken
    action="${ctx}/api/add/operators"
    method="post"
    fields=operatoreFields
    submitLabel="Crea operatore"
    resetLabel="Annulla"
    cssClass="edit-modal"
/>
