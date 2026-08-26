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
    "type":"hidden",
    "name":"richiesta_id",
    "value":richiestaId!item.id
},
{
    "type":"datetime-local",
    "name":"data_inizio",
    "label":"Data Inizio",
    "value":currentTime,
    "required":true
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
    "value":item.indirizzo,
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