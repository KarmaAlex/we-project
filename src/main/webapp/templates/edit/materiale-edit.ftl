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
    "type":"number",
    "name":"quantita",
    "label":"Quantità",
    "value":dettaglio.quantita!0,
    "required":true,
    "attrs":{
        "min":"0",
        "step":"1"
    }
},
{
    "type":"select",
    "name":"missione_corrente",
    "label":"Missione Corrente",
    "value":dettaglio.missioneId!'',
    "required":false,
    "options":missioneOptions
},
{
    "type":"hidden",
    "name":"id",
    "value":dettaglio.id!''
}
]>

<@form
    id="materialeForm"
    action="${ctx}/api/edit/materiali/${dettaglio.id}"
    method="post"
    fields=materialeFields
    submitLabel="Salva"
    resetLabel="Annulla"
/>