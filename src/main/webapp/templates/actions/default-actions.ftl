<#if hasDetails>
    <td>
        <div class="action-buttons">
         <a class="btn modal-trigger" href="${ctx}/api/detail/${section}/${item.id}" data-modal="${section}DetailModal" data-toggle="modal">Dettagli</a>
        <#if item.editable?? && item.editable><a class="btn modal-trigger" href="${ctx}/api/edit/${section}/${item.id}" data-modal="${section}EditModal" data-toggle="modal">🖉</a></#if>
        </div>
    </td>
</#if>