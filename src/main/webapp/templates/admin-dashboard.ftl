<#-- Admin Dashboard Entry Point -->
<#include "dashboard.ftl">
<#include "table.ftl">
<#include "pagination.ftl">
<#include "modal.ftl">

<#assign section = section?default("requests")>
<#assign page = page?default(1)>
<#assign user = user!''>

<@layout title="Admin Dashboard" section=section user=user>

<#switch section>
    <#case "requests">
        <#include "admin/requests-table.ftl">
    <#break>
    <#case "missions">
        <#include "admin/missions-table.ftl">
    <#break>
    <#case "operators">
        <#include "admin/operators-table.ftl">
    <#break>
    <#case "vehicles">
        <#include "admin/vehicles-table.ftl">
    <#break>
    <#case "materials">
        <#include "admin/materials-table.ftl">
    <#break>
    <#default>
        <div class="empty-state">
            <p>Sezione non trovata</p>
        </div>
  </#switch>
</@layout>
