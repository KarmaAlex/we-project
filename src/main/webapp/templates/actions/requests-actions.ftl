<#if hasDetails>
    <td>
        <div class="action-buttons">
         <a class="btn modal-trigger" href="${ctx}/api/detail/${section}/${item.id}" data-modal="${section}DetailModal" data-toggle="modal">Dettagli</a>
        <#if item.editable?? && item.editable><a class="btn modal-trigger" href="${ctx}/api/edit/${section}/${item.id}" data-modal="${section}EditModal" data-toggle="modal">🖉</a></#if>
        <#if item.stato?? && item.stato == "ATTIVA"><a class="btn modal-trigger" href="${ctx}/api/add/missions?richiesta_id=${item.id}" data-modal="createMissionModal" data-toggle="modal">+</a></#if>
        </div>
    </td>
</#if>