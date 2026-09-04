<#include "/form.ftl">

<#assign materialeFields = [
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
    "readonly":true
},
{
    "type":"textarea",
    "name":"descrizione",
    "label":"Descrizione",
    "value":dettaglio.descrizione!'',
    "rows":4,
    "attrs":{"readonly":"readonly"}
},
{
    "type":"text",
    "name":"stato",
    "label":"Stato",
    "value":dettaglio.stato!'',
    "readonly":true
},
{
    "type":"text",
    "name":"missione_corrente",
    "label":"Missione Corrente",
    "value":dettaglio.missione_corrente!'',
    "readonly":true
}
]>

<@form
    id="materialeDeleteForm"
    csrfToken=csrfToken
    action="${ctx}/api/delete/materiali"
    method="post"
    fields=materialeFields
    submitLabel="Elimina"
    resetLabel="Annulla"
    cssClass="delete-modal"
/>
