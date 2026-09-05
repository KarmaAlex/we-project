<#macro form
    id
    fields
    csrfToken
    action=""
    method="post"
    cssClass="request-form"
    enctype="application/x-www-form-urlencoded"
    submitLabel="Submit"
    resetLabel="Reset">

<form
    id="${id}"
    class="${cssClass}"
    action="${action}"
    method="${method}"
    enctype="${enctype}"
>

<#list fields as field>

    <#if field.type != "hidden"><div class="form-field">
        <label for="${field.name}">
        ${field.label!field.name}

        <#switch field.type>
        <#case "text">
        <#case "email">
        
        <#case "number">
        <#case "date">
        <#case "datetime-local">
        <#case "tel">
        <#case "url">
        <#case "file">
            <input
                type="${field.type}"
                id="${field.name}"
                name="${field.name}"
                value="${field.value!''}"
                <#if field.placeholder??>placeholder="${field.placeholder}"</#if>
                <#if field.required?? && field.required>required aria-required="true"</#if>
                <#if field.accept??>accept="${field.accept}"</#if>
                <#if field.autocomplete??>autocomplete="${field.autocomplete}"</#if>
                <#if field.help??>aria-describedby="${field.name}-help"</#if>
                <#if field.readonly?? && field.readonly>readonly</#if>
                <#if field.attrs??>
                    <#list field.attrs?keys as key>
                        ${key}="${field.attrs[key]}"
                    </#list>
                </#if>
            />
            <#break>
        <#case "textarea">
            <textarea
                id="${field.name}"
                name="${field.name}"
                rows="${field.rows!4}"
                <#if field.required?? && field.required>required aria-required="true"</#if>
                <#if field.placeholder??>placeholder="${field.placeholder}"</#if>
                <#if field.help??>aria-describedby="${field.name}-help"</#if>
                <#if field.attrs??>
                    <#list field.attrs?keys as key>
                        ${key}="${field.attrs[key]}"
                    </#list>
                </#if>
            >${field.value!''}</textarea>
            <#break>
        <#case "select">
            <select
                id="${field.name}"
                name="${field.name}"
                <#if field.required?? && field.required>required</#if>
            >
                <#if field.placeholder??>
                    <option value="">${field.placeholder}</option>
                </#if>
                <#list field.options as option>
                    <option
                        value="${option.value}"
                        <#if field.value?? && field.value == option.value>selected</#if>
                    >
                    ${option.label}</option>
                </#list>
            </select>
            <#break>
        <#case "checkbox">
            <input
                type="checkbox"
                id="${field.name}"
                name="${field.name}"

                <#if field.checked?? && field.checked>checked</#if>
            />
            <#break>
        <#case "radio">
            <#list field.options as option>
                <label class="radio-option">
                    <input
                        type="radio"
                        name="${field.name}"
                        value="${option.value}"
                        <#if field.value?? && field.value == option.value>checked</#if>
                    />
                    ${option.label}
                </label>
            </#list>
            <#break>
        <#case "multiselect">
            <select
                id="${field.name}"
                name="${field.name}"
                multiple
                <#if field.required?? && field.required>required</#if>
                <#if field.attrs??>
                    <#list field.attrs?keys as key>
                        ${key}="${field.attrs[key]}"
                    </#list>
                </#if>
            >
                <#list field.options as option>
                    <option
                        value="${option.value}"
                        <#if field.values?? && field.values?seq_contains(option.value)>selected</#if>>
                        ${option.label}
                    </option>
                </#list>
            </select>
        <#break>
        <#case "password">
            <input type="password" name="password" autocomplete="password" required />
        <#break>
        </#switch>
        <#if field.help??>
            <small id="${field.name}-help">
                ${field.help}
            </small>
        </#if>
        <#if field.type != "hidden">
            </label>
        </#if>
    </div>
    <#else>
    <input
            type="hidden"
            name="${field.name}"
            value="${field.value!''}"
        />
    </#if>

</#list>
<input type="hidden" name="csrf" value="${csrfToken}" />

<div class="form-actions">
    <button type="submit" class="btn primary">
        ${submitLabel}
    </button>
    <button type="reset" class="btn">
        ${resetLabel}
    </button>
</div>
</form>

</#macro>