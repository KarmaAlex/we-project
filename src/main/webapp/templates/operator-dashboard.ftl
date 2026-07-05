<#-- Operator Dashboard Entry Point -->
<#include "dashboard.ftl">
<#include "table.ftl">
<#include "pagination.ftl">
<#include "modal.ftl">

<#assign section = section?default("missions")>
<#assign page = page?default(1)>
<#assign user = user!''>

<@layout title="Operatore Dashboard" section=section user=user>

<#-- Breadcrumb -->
<div class="breadcrumb">
  <a href="/operator-dashboard">Dashboard</a>
  <span>›</span>
  <#switch section>
    <#case "missions">
      Missioni
      <#break>
    <#case "profile">
      Profilo
      <#break>
    <#default>
      Dashboard
  </#switch>
</div>

<#-- Missions List Section -->
<#if section == "missions">
  <#include "operator/missions-list.ftl">

<#-- Profile Section -->
<#elseif section == "profile">
  <#include "operator/profile.ftl">

<#else>
  <div class="empty-state">
    <p>Sezione non trovata</p>
  </div>
</#if>

</@layout>
