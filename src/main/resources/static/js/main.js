// =========================
// SIDEBAR FUNCTIONS
// =========================

// Toggle sidebar collapse
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) sidebar.classList.toggle('collapsed');
}

// Open the "Create" panel
function openCreatePanel() {
    document.getElementById('sidebarPanel')?.classList.add('open');
    document.getElementById('overlay')?.classList.add('show');
}

// Close the "Create" panel
function closePanel() {
    document.getElementById('sidebarPanel')?.classList.remove('open');
    document.getElementById('overlay')?.classList.remove('show');
}

// Set minimum selectable date to today
document.addEventListener('DOMContentLoaded', () => {
    const dateInput = document.getElementById('date');
    if (dateInput) {
        const today = new Date().toISOString().split('T')[0];
        dateInput.setAttribute('min', today);
    }
});

// =========================
// NAVBAR FUNCTIONS
// =========================

// Toggle dropdown (profile menu)
function toggleDropdown() {
    const dropdownMenu = document.getElementById('dropdownMenu');
    if (dropdownMenu) dropdownMenu.classList.toggle('show');
}

// Close dropdown when clicking outside
window.addEventListener('click', (e) => {
    if (!e.target.closest('.dropdown')) {
        document.querySelectorAll('.dropdown-menu').forEach(menu => menu.classList.remove('show'));
    }
});

// Optional notification example
function showNotification(message) {
    alert(message || 'You have a new notification!');
}