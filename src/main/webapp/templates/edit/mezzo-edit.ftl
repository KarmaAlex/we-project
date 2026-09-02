<#include "/form.ftl">
<#assign missioneOptions = [
    {
        "value":"",
        "label":"Nessuna"
    }
]>

<#if missioniAperte??>
    <#list missioniAperte as missione>
        <#assign missioneOptions = missioneOptions + [
            {
                "value":"${missione.id}",
                "label":"${missione.id}"
            }
        ]>
    </#list>
</#if>

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
},
{
    "type":"select",
    "name":"missione_corrente",
    "label":"Missione Corrente",
    "value":dettaglio.missione_corrente!'',
    "required":false,
    "options":missioneOptions
}
]>

<@form
    id="mezzoForm"
    csrfToken=csrfToken
    action="${ctx}/api/edit/mezzi/${dettaglio.id}"
    method="post"
    fields=mezzoFields
    submitLabel="Salva"
    resetLabel="Annulla"
    cssClass="edit-modal"
/>