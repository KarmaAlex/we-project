<#-- Use the shared base layout -->
<#include "base.ftl">
<#include "form.ftl">
<@basePage title="SoccorsoWeb - Login" css=["/style/login.css"]>
  <section class="container">
    <div class="card login-card">
      <h2>Accedi al pannello</h2>
      <#assign loginFields = [
      {
          "type":"text",
          "name":"username",
          "label":"Username",
          "required":true
      },
      {
          "type":"password",
          "name":"password"
      }
      ]>

      <@form
          id="login-form"
          cssClass="login-form"
          fields=loginFields
          csrfToken=csrfToken
          submitLabel="Accedi"
          resetLabel="Annulla"
      />
      <p class="error" id="error_msg" <#if (!errorMessage??) && errorMessage != "">hidden</#if>><#if errorMessage?? && errorMessage!="">${errorMessage}</#if></p>
    </div>
  </section>
</@basePage>
