<#include "/form.ftl">
<#assign richiestaFields = [

{
    "type":"hidden",
    "name":"id",
    "value":dettaglio.id!''
},

{
    "type":"select",
    "name":"stato",
    "label":"Stato",
    "value":dettaglio.stato!'IN_ATTESA',
    "required":true,
    "options":[
        {
            "value":"IN_ATTESA",
            "label":"In attesa"
        },
        {
            "value":"IN_LAVORAZIONE",
            "label":"In lavorazione"
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
    "type":"checkbox",
    "name":"verificato",
    "label":"Verificato",
    "checked":dettaglio.verificato!false
}

]>

<@form
    id="richiestaForm"
    action="="${ctx}/api/edit/richieste/${dettaglio.id}"
    method="post"
    fields=richiestaFields
    submitLabel="Salva"
    resetLabel="Annulla"
/>