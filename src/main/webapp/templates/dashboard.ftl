<#-- Dashboard layout macro: usage: <#include "dashboard.ftl"> then <@layout title="..." section="..." user=...>...content...</@layout> -->
<#include "base.ftl">

<#macro layout title section user css=[] scripts=[]>
<#assign base_css = ["/style/dashboard.css"] + css>
<#assign base_scripts = ["/scripts/dashboard.js"] + scripts>
<@basePage title=title css=base_css scripts=base_scripts>
  <div class="dashboard">
    <!-- Sidebar Navigation -->
    <aside class="dashboard-sidebar">
      <button class="sidebar-toggle" id="sidebarToggle" aria-label="Toggle sidebar">☰</button>
      <nav>
        <ul class="sidebar-menu">
          <#if user.ruolo == "ADMIN"> <#-- TODO: update with actual values from User class -->
            <li><a href="${ctx}/admin-dashboard?section=requests" class="menu-link ${(section == 'requests')?string('active', '')}" data-section="requests">Richieste</a></li>
            <li><a href="${ctx}/admin-dashboard?section=missions" class="menu-link ${(section == 'missions')?string('active', '')}" data-section="missions">Missioni</a></li>
            <li><a href="${ctx}/admin-dashboard?section=operators" class="menu-link ${(section == 'operators')?string('active', '')}" data-section="operators">Operatori</a></li>
            <li><a href="${ctx}/admin-dashboard?section=vehicles" class="menu-link ${(section == 'vehicles')?string('active', '')}" data-section="vehicles">Mezzi</a></li>
            <li><a href="${ctx}/admin-dashboard?section=teams" class="menu-link ${(section == 'teams')?string('active', '')}" data-section="teams">Squadre</a></li>
            <li><a href="${ctx}/admin-dashboard?section=materials" class="menu-link ${(section == 'materials')?string('active', '')}" data-section="materials">Materiali</a></li>
            <li><a href="${ctx}/admin-dashboard?section=abilities" class="menu-link ${(section == 'abilities')?string('active', '')}" data-section="abilities">Abilità</a></li>
            <li><a href="${ctx}/admin-dashboard?section=licenses" class="menu-link ${(section == 'licenses')?string('active', '')}" data-section="licenses">Patenti</a></li>
          <#elseif user.ruolo == "OPERATOR">
            <li><a href="${ctx}/operator-dashboard?section=missions" class="menu-link ${(section == 'missions')?string('active', '')}" data-section="missions">Missioni</a></li>
            <li><a href="${ctx}/operator-dashboard?section=profile" class="menu-link ${(section == 'profile')?string('active', '')}" data-section="profile">Profilo</a></li>
          </#if>
          <li style="border-top: 1px solid #eef0f4; margin-top: 1rem; padding-top: 1rem;">
            <a href="${ctx}/logout" class="menu-link">Esci</a>
          </li>
        </ul>
      </nav>
    </aside>

    <!-- Main Content -->
    <main class="dashboard-main">
      <div class="dashboard-header">
        <h2>${title}</h2>
      </div>
      
      <div id="main-content">
        <#nested />
      </div>
    </main>
  </div>

  <script>
    // Mobile sidebar toggle
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebar = document.querySelector('.dashboard-sidebar');
    if (sidebarToggle) {
      sidebarToggle.addEventListener('click', () => {
        sidebar.classList.toggle('show');
      });
      // Close sidebar when menu item clicked (mobile)
      document.querySelectorAll('.menu-link').forEach(link => {
        link.addEventListener('click', () => {
          if (window.innerWidth <= 768) {
            sidebar.classList.remove('show');
          }
        });
      });
    }
  </script>
</@basePage>
</#macro>
