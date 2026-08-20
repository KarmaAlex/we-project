/**
 * Dashboard interactions: AJAX pagination, filtering, modals
 * Graceful degradation: Forms work without JavaScript
 */

//TODO: fix filtering

// Debounce to prevent spam filtering
function debounce(func, delay) {
  let timeoutId;
  return function (...args) {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => func.apply(this, args), delay);
  };
}

// Modal management
const ModalManager = {
  openModal(id, content = null) {
    const modal = document.getElementById(id);
    if (!modal) return;
    modal.classList.add('show');
    if (content) {
      const body = modal.querySelector('.modal-body');
      if (body) body.innerHTML = content;
    }
    document.body.style.overflow = 'hidden';
  },

  closeModal(id) {
    const modal = document.getElementById(id);
    if (!modal) return;
    modal.classList.remove('show');
    document.body.style.overflow = '';
  },

  init() {
    // Close button
    document.querySelectorAll('[data-dismiss="modal"]').forEach(btn => {
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        const modal = btn.closest('.modal-overlay');
        if (modal) this.closeModal(modal.id);
      });
    });

    // Close by clicking outside modal
    document.querySelectorAll('.modal-overlay').forEach(overlay => {
      overlay.addEventListener('click', (e) => {
        if (e.target === overlay) {
          this.closeModal(overlay.id);
        }
      });
    });

    // Close with ESC
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') {
        document.querySelectorAll('.modal-overlay.show').forEach(modal => {
          this.closeModal(modal.id);
        });
      }
    });
  }
};

// Table and pagination AJAX loader
const DataLoader = {
  async loadSection(section, page = 1, filters = {}) {
    try {
      const params = new URLSearchParams();
      params.append('section', section);
      params.append('page', page);
      Object.entries(filters).forEach(([key, value]) => {
        if (value) params.append(key, value);
      });

      const response = await fetch(`?${params.toString()}`, {
        headers: { 'X-Requested-With': 'XMLHttpRequest' }
      });

      if (!response.ok) throw new Error('Network error');

      const html = await response.text();
      const parser = new DOMParser();
      const doc = parser.parseFromString(html, 'text/html');
      const newContent = doc.querySelector('.dashboard-main');
      
      // Update page
      const container = document.querySelector('.dashboard-main');
      if (container && newContent) {
        container.innerHTML = newContent.innerHTML;
        
        // Restore filter values after content is loaded
        Object.entries(filters).forEach(([key, value]) => {
          const field = document.querySelector(`[data-filter-field="${key}"]`);
          if (field) {
            field.value = value;
            console.log(`Restored filter ${key}:`, value);
          }
        });
        
        this.attachHandlers();
        console.log('Section loaded:', section, 'Filters:', filters);
      }
    } catch (error) {
      console.error('Failed to load section:', error);
      // Fallback: submit form normally
    }
  },

  async loadModalContent(url) {
    try {
      const response = await fetch(url, {
        headers: { 'X-Requested-With': 'XMLHttpRequest' }
      });

      if (!response.ok) throw new Error('Network error');
      return await response.text();
    } catch (error) {
      console.error('Failed to load modal content:', error);
      return '<p>Errore nel caricamento dei dati. Riprova più tardi.</p>';
    }
  },

  attachHandlers() {
    // Pagination links
    document.querySelectorAll('.page-link:not(.disabled)').forEach(link => {
      link.addEventListener('click', (e) => {
        e.preventDefault();
        const page = link.dataset.page;
        const section = link.dataset.section || document.querySelector('.data-table')?.dataset.section;
        const filters = this.getFilters();
        this.loadSection(section, page, filters);
      });
    });

    // Modal triggers
    document.querySelectorAll('.modal-trigger').forEach(trigger => {
      trigger.addEventListener('click', async (e) => {
        e.preventDefault();
        const modalId = trigger.dataset.modal;
        const href = trigger.href;
        const content = await this.loadModalContent(href);
        ModalManager.openModal(modalId, content);
      });
    });

    // Re-setup filter handlers after content update
    setupFilterHandlers();
  },

  getFilters() {
    const filters = {};
    document.querySelectorAll('[data-filter-field]').forEach(field => {
      const key = field.dataset.filterField;
      const value = field.value;
      console.log(value)
      if (value) filters[key] = value;
    });
    console.log('Extracted filters:', filters);
    return filters;
  }
};

// Filter form handler
function setupFilterHandlers() {
  const filterForm = document.querySelector('.filters-section form');
  if (!filterForm) return;

  const inputs = filterForm.querySelectorAll('input[type="text"], input[type="email"], input[type="date"], select');
  inputs.forEach(input => {
    input.addEventListener('change', debounce(() => {
      const section = document.querySelector('.data-table')?.dataset.section;
      if (section) {
        const filters = DataLoader.getFilters();
        DataLoader.loadSection(section, 1, filters);
      }
    }, 300));
  });

  // Form submit handler
  filterForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const section = document.querySelector('.data-table')?.dataset.section;
    if (section) {
      const filters = DataLoader.getFilters();
      DataLoader.loadSection(section, 1, filters);
    }
  });

  // Reset button
  const resetBtn = filterForm.querySelector('[type="reset"]');
  if (resetBtn) {
    resetBtn.addEventListener('click', () => {
      setTimeout(() => {
        const section = document.querySelector('.data-table')?.dataset.section;
        if (section) DataLoader.loadSection(section, 1, {});
      }, 50);
    });
  }
}

// Initialize on DOM ready
document.addEventListener('DOMContentLoaded', () => {
  ModalManager.init();
  DataLoader.attachHandlers();
  setupFilterHandlers();
  // Hide submit button if javascript is enabled since in that case we use AJAX
  document.getElementById("filter-submit").style.display = "none";
});

// Exports for opening modals from the table buttons
window.Dashboard = {
  openModal: (id, url) => ModalManager.openModal(id, url),
  closeModal: (id) => ModalManager.closeModal(id),
  loadSection: (section, page, filters) => DataLoader.loadSection(section, page, filters),
  loadModalContent: (url) => DataLoader.loadModalContent(url)
};