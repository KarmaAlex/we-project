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
    "name":"id",
    "value":dettaglio.id!''
},
{
    "type":"text",
    "name":"richiesta_id",
    "label":"Richiesta",
    "value":dettaglio.richiesta_id!'',
    "readonly":true
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
    "value":dettaglio.esito!'NON_DEFINITO',
    "options":[
        {"value":"NON_DEFINITO", "label":"Non definito"},
        {"value":"SUCCESSO", "label":"Successo"},
        {"value":"PARZIALE", "label":"Parziale"},
        {"value":"FALLIMENTO", "label":"Fallimento"}
    ]
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
    "label":"Indirizzo",
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
    action="${ctx}/api/edit/missioni/${dettaglio.id}"
    method="post"
    fields=missioneFields
    submitLabel="Salva"
    resetLabel="Annulla"
    cssClass="edit-modal"
/>