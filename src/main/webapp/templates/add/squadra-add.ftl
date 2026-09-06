<#include "/form.ftl">

<#assign operatorOptions = []>
<#list operatori![] as operatore>
    <#assign operatorOptions = operatorOptions + [{
        "value":"${operatore.id}",
        "label":"${operatore.label}"
    }]>
</#list>

<#assign teamFields = [
    {
        "type":"select",
        "name":"capo",
        "label":"Caposquadra",
        "placeholder":"Seleziona il caposquadra",
        "required":true,
        "options":operatorOptions
    },
    {
        "type":"multiselect",
        "name":"membri",
        "label":"Membri",
        "values":[],
        "options":operatorOptions
    }
]>

<@form
    id="squadraForm"
    csrfToken=csrfToken
    action="${ctx}/api/add/teams"
    method="post"
    fields=teamFields
    submitLabel="Crea squadra"
    resetLabel="Annulla"
    cssClass="add-modal"
/>