<#include "/form.ftl">

<#assign tipoOptions = []>
<#list tipiPatente as tipo>
    <#assign tipoOptions = tipoOptions + [{"value":tipo, "label":tipo}]>
</#list>

<#assign fields = [
    {
        "type":"text",
        "name":"numero", 
        "label":"Numero", 
        "required":true, 
        "placeholder":"Numero patente",
        "maxlength":10
    },
    {
        "type":"select",
        "name":"tipo",
        "label":"Tipo",
        "required":true,
        "options":tipoOptions,
        "placeholder":"Seleziona tipo"
    }
]>

<@form id="patenteForm" csrfToken=csrfToken action="${ctx}/api/add/licenses" method="post" fields=fields submitLabel="Crea patente" resetLabel="Annulla" cssClass="edit-modal" />