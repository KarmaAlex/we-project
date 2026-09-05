<#include "/form.ftl">

<#assign fields = [
    {"type":"text", "name":"nome", "label":"Nome", "required":true, "placeholder":"Descrizione abilità"}
]>

<@form id="abilitaForm" csrfToken=csrfToken action="${ctx}/api/add/abilities" method="post" fields=fields submitLabel="Crea abilità" resetLabel="Annulla" cssClass="edit-modal" />