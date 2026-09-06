<#include "/form.ftl">
<#assign squadraOptions = []>
<#if squadreDisponibili??>
    <#list squadreDisponibili as squadra>
        <#assign squadraOptions = squadraOptions + [
            {
                "value":"${squadra.value}",
                "label":"${squadra.label}"
            }
        ]>
    </#list>
</#if>

<#assign mezzoOptions = []>
<#if mezziDisponibili??>
    <#list mezziDisponibili as mezzo>
        <#assign mezzoOptions = mezzoOptions + [
            {
                "value":"${mezzo.value}",
                "label":"${mezzo.label}"
            }
        ]>
    </#list>
</#if>

<#assign materialeOptions = []>
<#if materialiDisponibili??>
    <#list materialiDisponibili as materiale>
        <#assign materialeOptions = materialeOptions + [
            {
                "value":"${materiale.value}",
                "label":"${materiale.label}"
            }
        ]>
    </#list>
</#if>

<#assign missioneFields = [
{
    "type":"hidden",
    "name":"id",
    "value":dettaglio.id!''
},
{
    "type":"text",
    "name":"richiesta_id",
    "label":"Richiesta",
    "value":dettaglio.richiesta_id!'',
    "readonly":true,
    "disabled":true
},
{
    "type":"select",
    "name":"stato",
    "label":"Stato",
    "value":dettaglio.stato!'PIANIFICATA',
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
    "type":"select",
    "name":"esito",
    "label":"Esito",
    "value":dettaglio.esito!'',
    "options":[
        {"value":"1", "label":"1"},
        {"value":"2", "label":"2"},
        {"value":"3", "label":"3"},
        {"value":"4", "label":"4"},
        {"value":"5", "label":"5"}
    ]
},
{
    "type":"textarea",
    "name":"commento",
    "label":"Commento conclusivo",
    "rows":5,
    "placeholder":"Inserisci il commento conclusivo..."
},
{
    "type":"date",
    "name":"data_inizio",
    "label":"Data Inizio",
    "value":dettaglio.data_inizio!'',
    "required":true
},
{
    "type":"date",
    "name":"data_fine",
    "label":"Data Fine",
    "value":dettaglio.data_fine!''
},
{
    "type":"textarea",
    "name":"obiettivo",
    "label":"Obiettivo",
    "value":dettaglio.obiettivo!'',
    "rows":3,
    "required":true
},
{
    "type":"text",
    "name":"indirizzo",
    "label":"Posizione",
    "value":dettaglio.indirizzo!'',
    "required":true
},
{
    "type":"select",
    "name":"squadra",
    "label":"Squadra",
    "value":dettaglio.idSquadra!'',
    "required":true,
    "options":squadraOptions
},
{
    "type":"multiselect",
    "name":"mezzi",
    "label":"Mezzi",
    "values": dettaglio.mezzi,
    "options": mezzoOptions
},
{
    "type":"multiselect",
    "name":"materiali",
    "label":"Materiali",
    "values": dettaglio.materiali,
    "options": materialeOptions
},
{
    "type":"textarea",
    "name":"nuovo_aggiornamento",
    "label":"Nuovo aggiornamento",
    "rows":5,
    "placeholder":"Inserisci un aggiornamento..."
}

]>

<@form
    id="missioneForm"
    csrfToken=csrfToken
    action="${ctx}/api/edit/missioni"
    method="post"
    fields=missioneFields
    submitLabel="Salva"
    resetLabel="Annulla"
    cssClass="edit-modal"
/>