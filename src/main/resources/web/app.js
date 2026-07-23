const API = '/api';

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('nav button').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('nav button').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
            document.getElementById(btn.dataset.tab).classList.add('active');
            loadTab(btn.dataset.tab);
        });
    });
    loadTab('dashboard');
});

function loadTab(tab) {
    if (tab === 'dashboard') loadDashboard();
    else if (tab === 'packages') loadPackages();
    else if (tab === 'drivers') loadDrivers();
    else if (tab === 'customers') loadCustomers();
}

async function api(path, options = {}) {
    const res = await fetch(API + path, {
        headers: { 'Content-Type': 'application/json' },
        ...options
    });
    if (!res.ok) { const e = await res.json(); throw new Error(e.error); }
    return res.json();
}

// DASHBOARD
async function loadDashboard() {
    const stats = await api('/stats');
    document.getElementById('stats').innerHTML =
        `<div class="stat-card"><div class="num">${stats.total}</div><div class="label">Total Packages</div></div>
         <div class="stat-card pending"><div class="num">${stats.pending}</div><div class="label">Pending</div></div>
         <div class="stat-card transit"><div class="num">${stats.inTransit}</div><div class="label">In Transit</div></div>
         <div class="stat-card done"><div class="num">${stats.delivered}</div><div class="label">Delivered</div></div>`;

    const pkgs = await api('/packages');
    const recent = pkgs.slice(0, 5);
    let html = '<table><tr><th>ID</th><th>Description</th><th>Status</th><th>Created</th></tr>';
    recent.forEach(p => {
        html += `<tr><td>#${p.id}</td><td>${p.description}</td><td><span class="status ${p.status.toLowerCase()}">${p.status}</span></td><td>${p.createdAt || '-'}</td></tr>`;
    });
    document.getElementById('recent-packages').innerHTML = recent.length ? html + '</table>' : '<div class="empty">No packages yet</div>';
}

// PACKAGES
async function loadPackages() {
    const filter = document.getElementById('status-filter').value;
    const url = filter ? `/packages?status=${filter}` : '/packages';
    const pkgs = await api(url);
    const [drivers, customers] = await Promise.all([api('/drivers'), api('/customers')]);
    const dMap = Object.fromEntries(drivers.map(d => [d.id, d.name]));
    const cMap = Object.fromEntries(customers.map(c => [c.id, c.name]));

    let html = `<div class="data-row header"><span>Description</span><span>Customer</span><span>Driver</span><span>Status</span><span>Actions</span></div>`;
    if (!pkgs.length) html += '<div class="empty">No packages</div>';
    pkgs.forEach(p => {
        const statusClass = p.status.toLowerCase().replace('_', '-');
        html += `<div class="data-row">
            <span><strong>${p.description}</strong><br><small>${p.address}</small></span>
            <span>${cMap[p.customerId] || '-'}</span>
            <span>${dMap[p.driverId] || 'Unassigned'}</span>
            <span><span class="status ${statusClass}">${p.status}</span></span>
            <span class="actions">
                ${p.status === 'PENDING' ? `<button onclick="showAssign(${p.id})" class="success">Assign</button>` : ''}
                ${p.status === 'IN_TRANSIT' ? `<button onclick="deliver(${p.id})" class="success">Deliver</button>` : ''}
                ${p.status === 'DELIVERED' ? `<span style="font-size:12px;color:#10b981">Done</span>` : ''}
            </span>
        </div>`;
    });
    document.getElementById('package-list').innerHTML = html;
}

function searchPackages() {
    const term = document.getElementById('search-input').value;
    if (!term) return loadPackages();
    api(`/packages?search=${encodeURIComponent(term)}`).then(pkgs => {
        let html = `<div class="data-row header"><span>Description</span><span>Customer</span><span>Driver</span><span>Status</span><span>Actions</span></div>`;
        if (!pkgs.length) html += '<div class="empty">No results</div>';
        pkgs.forEach(p => {
            html += `<div class="data-row">
                <span><strong>${p.description}</strong><br><small>${p.address}</small></span>
                <span>-</span><span>-</span>
                <span><span class="status ${p.status.toLowerCase()}">${p.status}</span></span>
                <span></span>
            </div>`;
        });
        document.getElementById('package-list').innerHTML = html;
    });
}

function showAddPackage() {
    Promise.all([api('/customers'), api('/drivers')]).then(([customers]) => {
        document.getElementById('modal-body').innerHTML = `
            <h3>Add Package</h3>
            <form onsubmit="addPackage(event)">
                <input type="text" id="pkg-desc" placeholder="Description" required>
                <input type="number" step="0.1" id="pkg-weight" placeholder="Weight (kg)" required>
                <input type="text" id="pkg-address" placeholder="Delivery address" required>
                <select id="pkg-customer">
                    <option value="">No customer</option>
                    ${customers.map(c => `<option value="${c.id}">${c.name}</option>`).join('')}
                </select>
                <button type="submit">Add Package</button>
            </form>`;
        document.getElementById('modal').classList.remove('hidden');
    });
}

async function addPackage(e) {
    e.preventDefault();
    await api('/packages', {
        method: 'POST',
        body: JSON.stringify({
            description: document.getElementById('pkg-desc').value,
            weight: parseFloat(document.getElementById('pkg-weight').value),
            address: document.getElementById('pkg-address').value,
            customerId: document.getElementById('pkg-customer').value || null
        })
    });
    closeModal();
    loadPackages();
}

function showAssign(pkgId) {
    api('/drivers').then(drivers => {
        document.getElementById('modal-body').innerHTML = `
            <h3>Assign Driver</h3>
            <form onsubmit="assignDriver(event, ${pkgId})">
                <select id="assign-driver" required>
                    <option value="">Select driver</option>
                    ${drivers.map(d => `<option value="${d.id}">${d.name} (${d.licensePlate})</option>`).join('')}
                </select>
                <button type="submit">Assign</button>
            </form>`;
        document.getElementById('modal').classList.remove('hidden');
    });
}

async function assignDriver(e, pkgId) {
    e.preventDefault();
    const driverId = document.getElementById('assign-driver').value;
    await api(`/packages/${pkgId}/assign?driverId=${driverId}`, { method: 'PUT' });
    closeModal();
    loadPackages();
}

async function deliver(pkgId) {
    if (!confirm('Mark this package as delivered?')) return;
    await api(`/packages/${pkgId}/deliver`, { method: 'PUT' });
    loadPackages();
}

// DRIVERS
async function loadDrivers() {
    const list = await api('/drivers');
    let html = `<div class="data-row header"><span>Name</span><span>Phone</span><span>License Plate</span><span>ID</span></div>`;
    if (!list.length) html += '<div class="empty">No drivers</div>';
    list.forEach(d => {
        html += `<div class="data-row"><span>${d.name}</span><span>${d.phone}</span><span>${d.licensePlate}</span><span>#${d.id}</span></div>`;
    });
    document.getElementById('driver-list').innerHTML = html;
}

function showAddDriver() {
    document.getElementById('modal-body').innerHTML = `
        <h3>Add Driver</h3>
        <form onsubmit="addDriver(event)">
            <input type="text" id="driver-name" placeholder="Name" required>
            <input type="text" id="driver-phone" placeholder="Phone" required>
            <input type="text" id="driver-plate" placeholder="License plate" required>
            <button type="submit">Add Driver</button>
        </form>`;
    document.getElementById('modal').classList.remove('hidden');
}

async function addDriver(e) {
    e.preventDefault();
    await api('/drivers', {
        method: 'POST',
        body: JSON.stringify({
            name: document.getElementById('driver-name').value,
            phone: document.getElementById('driver-phone').value,
            licensePlate: document.getElementById('driver-plate').value
        })
    });
    closeModal();
    loadDrivers();
}

// CUSTOMERS
async function loadCustomers() {
    const list = await api('/customers');
    let html = `<div class="data-row header"><span>Name</span><span>Phone</span><span>Address</span><span>ID</span></div>`;
    if (!list.length) html += '<div class="empty">No customers</div>';
    list.forEach(c => {
        html += `<div class="data-row"><span>${c.name}</span><span>${c.phone}</span><span>${c.address}</span><span>#${c.id}</span></div>`;
    });
    document.getElementById('customer-list').innerHTML = html;
}

function showAddCustomer() {
    document.getElementById('modal-body').innerHTML = `
        <h3>Add Customer</h3>
        <form onsubmit="addCustomer(event)">
            <input type="text" id="cust-name" placeholder="Name" required>
            <input type="text" id="cust-phone" placeholder="Phone" required>
            <input type="text" id="cust-address" placeholder="Address" required>
            <button type="submit">Add Customer</button>
        </form>`;
    document.getElementById('modal').classList.remove('hidden');
}

async function addCustomer(e) {
    e.preventDefault();
    await api('/customers', {
        method: 'POST',
        body: JSON.stringify({
            name: document.getElementById('cust-name').value,
            phone: document.getElementById('cust-phone').value,
            address: document.getElementById('cust-address').value
        })
    });
    closeModal();
    loadCustomers();
}

function closeModal() {
    document.getElementById('modal').classList.add('hidden');
}
