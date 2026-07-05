<#-- Admin Dashboard Entry Point -->
<#include "dashboard.ftl">
<#include "table.ftl">
<#include "pagination.ftl">
<#include "modal.ftl">

<#assign section = section?default("requests")>
<#assign page = page?default(1)>
<#assign user = user!''>

<@layout title="Admin Dashboard" section=section user=user>

<#-- Breadcrumb -->
<div class="breadcrumb">
    <a href="/admin-dashboard">Dashboard</a>
    <span>›</span>
    <#switch section>
        <#case "requests">
            Richieste
        <#break>
        <#case "missions">
            Missioni
        <#break>
        <#case "operators">
            Operatori
        <#break>
        <#case "vehicles">
            Mezzi
        <#break>
        <#case "materials">
            Materiali
        <#break>
        <#default>
            Dashboard
    </#switch>
</div>

<#switch section>
    <#case "requests">
        <#include "admin/requests-table.ftl">
    <#break>
    <#case "missions">
        <#include "admin/missions-list.ftl">
    <#break>
    <#case "operators">
        <#include "admin/operators-list.ftl">
    <#break>
    <#case "vehicles">
        <#include "admin/vehicles-table.ftl">
    <#break>
    <#case "materials">
        <#include "admin/materials-list.ftl">
    <#break>
    <#default>
        <div class="empty-state">
            <p>Sezione non trovata</p>
        </div>
  </#switch>
</@layout>
