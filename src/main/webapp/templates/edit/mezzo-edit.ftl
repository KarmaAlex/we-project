<#include "/form.ftl">

<#assign mezzoFields = [
{
    "type":"hidden",
    "name":"id",
    "value":dettaglio.id!''
},
{
    "type":"text",
    "name":"nome",
    "label":"Nome",
    "value":dettaglio.nome!'',
    "required":true,
    "placeholder":"Nome del mezzo"
},
{
    "type":"textarea",
    "name":"descrizione",
    "label":"Descrizione",
    "value":dettaglio.descrizione!'',
    "rows":4,
    "required":true,
    "placeholder":"Descrizione del mezzo"
},
{
    "type":"text",
    "name":"targa",
    "label":"Targa",
    "value":dettaglio.targa!'',
    "required":true,
    "placeholder":"AB123CD"
}
]>

<@form
    id="mezzoForm"
    csrfToken=csrfToken
    action="${ctx}/api/edit/mezzi"
    method="post"
    fields=mezzoFields
    submitLabel="Salva"
    resetLabel="Annulla"
    cssClass="edit-modal"
/>