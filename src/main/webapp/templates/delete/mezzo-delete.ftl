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
    "name":"targa",
    "label":"Targa",
    "value":dettaglio.targa!'',
    "readonly":true
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
    id="mezzoDeleteForm"
    csrfToken=csrfToken
    action="${ctx}/api/delete/mezzi"
    method="post"
    fields=mezzoFields
    submitLabel="Elimina"
    resetLabel="Annulla"
    cssClass="delete-modal"
/>
