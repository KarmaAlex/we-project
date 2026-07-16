<#include "/form.ftl">
<#assign missioneOptions = [
    {
        "value":"",
        "label":"Nessuna"
    }
]>

<#assign patenteOptions = []>
<#if patentiDisponibili??>
    <#list patentiDisponibili as patente>
        <#assign patenteOptions = patenteOptions + [
            {
                "value":"${patente}",
                "label":"${patente}"
            }
        ]>
    </#list>
</#if>

<#assign abilitaOptions = []>
<#if abilitaDisponibili??>
    <#list abilitaDisponibili as abilita>
        <#assign abilitaOptions = abilitaOptions + [
            {
                "value":"${abilita}",
                "label":"${abilita}"
            }
        ]>
    </#list>
</#if>

<#assign operatoreFields = [

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
    "required":true
},

{
    "type":"text",
    "name":"cognome",
    "label":"Cognome",
    "value":dettaglio.cognome!'',
    "required":true
},

{
    "type":"email",
    "name":"email",
    "label":"Email",
    "value":dettaglio.email!'',
    "required":true
},

{
    "type":"tel",
    "name":"telefono",
    "label":"Telefono",
    "value":dettaglio.telefono!'',
    "required":true
},
{
    "type":"multiselect",
    "name":"patenti",
    "label":"Patenti",
    "values":dettaglio.patenti![],
    "options":patenteOptions
},

{
    "type":"multiselect",
    "name":"abilita",
    "label":"Abilità Professionali",
    "values":dettaglio.abilita![],
    "options":abilitaOptions
}

]>

<@form
    id="operatoreForm"
    action="${ctx}/api/edit/operatori/${dettaglio.id}"
    method="post"
    fields=operatoreFields
    submitLabel="Salva"
    resetLabel="Annulla"
/>