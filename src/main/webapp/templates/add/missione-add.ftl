<#include "/form.ftl">
<#assign squadraOptions = []>
<#if squadreDisponibili??>
    <#list squadreDisponibili as squadra>
        <#assign squadraOptions = squadraOptions + [
            {
                "value":"${squadra.id}",
                "label":"${squadra.id}"
            }
        ]>
    </#list>
</#if>

<#assign mezzoOptions = []>
<#if mezziDisponibili??>
    <#list mezziDisponibili as mezzo>
        <#assign mezzoOptions = mezzoOptions + [
            {
                "value":"${mezzo.id}",
                "label":"${mezzo.id}"
            }
        ]>
    </#list>
</#if>

<#assign materialeOptions = []>
<#if materialiDisponibili??>
    <#list materialiDisponibili as materiale>
        <#assign materialeOptions = materialeOptions + [
            {
                "value":"${materiale.id}",
                "label":"${materiale.id}"
            }
        ]>
    </#list>
</#if>

<#assign missioneFields = [
{
    "type":"text",
    "name":"richiesta_id",
    "label":"Richiesta",
    "value":'',
    "readonly":true
},
{
    "type":"select",
    "name":"stato",
    "label":"Stato",
    "value":'PIANIFICATA',
    "required":true,
    "options":[
        {
            "value":"PIANIFICATA",
            "label":"Pianificata"
        },
        {
            "value":"IN_CORSO",
            "label":"In corso"
        },
        {
            "value":"COMPLETATA",
            "label":"Completata"
        },
        {
            "value":"ANNULLATA",
            "label":"Annullata"
        }
    ]
},
{
    "type":"date",
    "name":"data_inizio",
    "label":"Data Inizio",
    "value":'',
    "required":true
},
{
    "type":"date",
    "name":"data_fine",
    "label":"Data Fine",
    "value":''
},
{
    "type":"textarea",
    "name":"obiettivo",
    "label":"Obiettivo",
    "value":'',
    "rows":3,
    "required":true
},
{
    "type":"text",
    "name":"indirizzo",
    "label":"Indirizzo",
    "value":'',
    "required":true
},
{
    "type":"select",
    "name":"squadra",
    "label":"Squadra",
    "value":'',
    "required":true,
    "options":squadraOptions
},
{
    "type":"multiselect",
    "name":"mezzi",
    "label":"Mezzi",
    "values": [],
    "options": mezzoOptions
},
{
    "type":"multiselect",
    "name":"materiali",
    "label":"Materiali",
    "values": [],
    "options": materialeOptions
}

]>

<@form
    id="missioneForm"
    action="${ctx}/api/add/missioni"
    method="post"
    fields=missioneFields
    submitLabel="Crea missione"
    resetLabel="Annulla"
    cssClass="edit-modal"
/>