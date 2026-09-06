<#include "/form.ftl">

<#assign materialeFields = [
{
    "type":"text",
    "name":"nome",
    "label":"Nome",
    "value":dettaglio.nome!'',
    "required":true,
    "placeholder":"Nome del materiale"
},
{
    "type":"textarea",
    "name":"descrizione",
    "label":"Descrizione",
    "value":dettaglio.descrizione!'',
    "rows":4,
    "required":true,
    "placeholder":"Descrizione del materiale"
},
{
    "type":"hidden",
    "name":"id",
    "value":dettaglio.id!''
}
]>

<@form
    id="materialeForm"
    csrfToken=csrfToken
    action="${ctx}/api/edit/materiali"
    method="post"
    fields=materialeFields
    submitLabel="Salva"
    resetLabel="Annulla"
    cssClass="edit-modal"
/>