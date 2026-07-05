<#assign columns = [
  {"field": "id", "label": "ID", "sortable": true},
  {"field": "data", "label": "Data", "sortable": true},
  {"field": "caposquadra", "label": "Caposquadra", "sortable": false},
  {"field": "obiettivo", "label": "Obiettivo", "sortable": false},
  {"field": "stato", "label": "Stato", "sortable": true, "type": "badge"},
  {"field": "actions", "label": "Azioni", "type": "actions"}
]>

<#if !missioni?? || !missioni?has_content>
  <#assign missioni = []>
</#if>

<@table items=missioni columns=columns currentPage=page totalPages=1 section="missions" />

<@modal id="missionDetailModal" title="Dettagli Missione">
  <p id="modalContent">Caricamento...</p>
</@modal>

<script>
  document.querySelectorAll('table.data-table tbody tr').forEach(row => {
    const id = row.dataset.id;
    if (id) {
      const viewBtn = document.createElement('a');
      viewBtn.href = '${ctx}/api/operator/missions/' + id + '/detail';
      viewBtn.className = 'btn modal-trigger';
      viewBtn.dataset.modal = 'missionDetailModal';
      viewBtn.dataset.toggle = 'modal';
      viewBtn.textContent = 'Visualizza';
      
      const actionCell = row.querySelector('td:last-child');
      if (actionCell) {
        actionCell.innerHTML = '';
        actionCell.appendChild(viewBtn);
      }
    }
  });
</script>
