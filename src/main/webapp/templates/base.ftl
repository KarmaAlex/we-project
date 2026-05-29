<#-- Base layout macro for pages: usage: <#include "base.ftl"> then <@layout title="..." css=["..."] scripts=["..."]>...content...</@layout> -->
<#macro layout title css=[] scripts=[]>
<!doctype html>
<html lang="it">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <title>${title!"SoccorsoWeb"}</title>
  <link rel="stylesheet" href="/style/base.css">
  <#if css?has_content>
    <#list css as c>
      <link rel="stylesheet" href="${(ctx!"") + c}" />
    </#list>
  </#if>
  <#if scripts?has_content>
    <#list scripts as s>
      <script src="${(ctx!"") + s}"></script>
    </#list>
  </#if>
</head>
<body>
  <header class="site-header">
    <div class="container header-inner">
      <h1 class="logo"><a href="${(ctx!"") + '/'}">SoccorsoWeb</a></h1>
      <nav class="top-nav">
        <a href="${(ctx!"") + '/login.html'}" class="btn login">Accedi</a>
      </nav>
    </div>
  </header>

  <main>
    <#nested />
  </main>

  <footer class="site-footer">
    <div class="container">
      <p>© SoccorsoWeb - Progetto WE</p>
    </div>
  </footer>
</body>
</html>
</#macro>
