<#if hasDetails>
    <td>
        <div class="action-buttons">
         <a class="btn modal-trigger" href="${ctx}/api/detail/${section}?id=${item.id}" data-modal="${section}DetailModal" data-toggle="modal">Dettagli</a>
        <#if item.editable?? && item.editable><a class="btn modal-trigger" href="${ctx}/api/edit/${section}?id=${item.id}" data-modal="${section}EditModal" data-toggle="modal">🖉</a></#if>
        <#if item.canDelete?? && item.canDelete><a class="btn modal-trigger" href="${ctx}/api/delete/${section}?id=${item.id}" data-modal="${section}DeleteModal" data-toggle="modal">🗑</a></#if>
        </div>
    </td>
</#if>