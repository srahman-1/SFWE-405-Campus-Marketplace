let currentEditUserId = null
let editUserModal = null

function checkAdminAccess() {
    const role = localStorage.getItem('campus_marketplace_role')
    if (role !== 'admin') {
        window.location.href = 'index.html'
        return false
    }
    return true
}

async function loadUsers() {
    if (!checkAdminAccess()) return

    try {
        const users = await request('/users', {
            method: 'GET',
            headers: authHeaders()
        })

        const tbody = document.getElementById('usersTableBody')
        if (!users || users.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No users found</td></tr>'
            return
        }

        tbody.innerHTML = users.map(user => `
      <tr>
        <td>${user.id}</td>
        <td>${user.email}</td>
        <td><span class="badge bg-${user.role === 'admin' ? 'danger' : 'secondary'}">${user.role}</span></td>
        <td>${user.createdAt ? new Date(user.createdAt).toLocaleDateString() : 'N/A'}</td>
        <td>
          <button class="btn btn-sm btn-outline-primary" onclick="editUser(${user.id}, '${user.email}', '${user.role}')">Edit</button>
          <button class="btn btn-sm btn-outline-danger" onclick="deleteUser(${user.id}, '${user.email}')">Delete</button>
        </td>
      </tr>
    `).join('')
    } catch (error) {
        showAlert('adminMessage', 'Failed to load users: ' + error.message, 'danger')
    }
}

function editUser(id, email, role) {
    currentEditUserId = id
    document.getElementById('editUserEmail').value = email
    document.getElementById('editUserRole').value = role
    editUserModal.show()
}

async function saveUserChanges() {
    const newRole = document.getElementById('editUserRole').value

    try {
        await request(`/users/${currentEditUserId}/role`, {
            method: 'PUT',
            headers: { ...authHeaders(), 'Content-Type': 'application/json' },
            body: JSON.stringify({ role: newRole })
        })

        showAlert('adminMessage', 'User role updated successfully', 'success')
        editUserModal.hide()
        loadUsers()
    } catch (error) {
        showAlert('adminMessage', 'Failed to update user: ' + error.message, 'danger')
    }
}

async function deleteUser(id, email) {
    if (!confirm(`Are you sure you want to delete user ${email}?`)) return

    try {
        await request(`/users/${id}`, {
            method: 'DELETE',
            headers: authHeaders()
        })

        showAlert('adminMessage', 'User deleted successfully', 'success')
        loadUsers()
    } catch (error) {
        showAlert('adminMessage', 'Failed to delete user: ' + error.message, 'danger')
    }
}

document.addEventListener('DOMContentLoaded', () => {
    checkAdminAccess()
    editUserModal = new bootstrap.Modal(document.getElementById('editUserModal'))

    document.getElementById('saveUserBtn').addEventListener('click', saveUserChanges)

    loadUsers()
    updateAuthUI()
})