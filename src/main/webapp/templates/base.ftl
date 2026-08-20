<#-- Utilizzo: <#include "base.ftl"> poi <@basePage title="..." css=["..."] scripts=["..."]>...contenuto...</@basePage> -->
<#macro basePage title css=[] scripts=[]>
<!doctype html>
<html lang="it">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <title>${title!"SoccorsoWeb"}</title>
  <link rel="stylesheet" href="${(ctx!"") + "/style/base.css"}">
  <#if css?has_content>
    <#list css as c>
      <#if c?starts_with("/")>
        <link rel="stylesheet" href="${(ctx!"") + c}" />
      <#else>
        <link rel="stylesheet" href="${(ctx!"") + "/" + c}" />
      </#if>
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
      <a href="#main-content" class="skip-link">Skip to main content</a>
      <nav class="top-nav">
        <#if currentUser?? && currentUser.authenticated?? && currentUser.authenticated>
          <details class="user-menu">
            <summary class="user-menu-toggle" aria-label="Menu utente">
              <img src="${(ctx!"") + '/assets/circle-user.svg'}" alt="" aria-hidden="true" />
              <#if currentUser.nome??>
                <span>${currentUser.nome}</span>
              <#elseif currentUser.username??>
                <span>${currentUser.username}</span>
              </#if>
            </summary>
            <div class="user-menu-dropdown">
              <#if currentUser.ruolo?? && currentUser.ruolo == "ADMIN">
                <a href="${(ctx!"") + '/admin-dashboard'}">Dashboard</a>
              <#elseif currentUser.ruolo?? && currentUser.ruolo == "OPERATOR">
                <a href="${(ctx!"") + '/operator-dashboard'}">Dashboard</a>
              </#if>
              <a href="${(ctx!"") + '/logout'}" class="logout">Logout</a>
            </div>
          </details>
        <#else>
          <a href="${(ctx!"") + '/login'}" class="btn login">Accedi</a>
        </#if>
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

  <script>
    document.addEventListener('click', (event) => {
      document.querySelectorAll('.user-menu[open]').forEach((menu) => {
        if (!menu.contains(event.target)) {
          menu.removeAttribute('open');
        }
      });
    });
  </script>
</body>
</html>
</#macro>
