let products = []

async function request(url, options = {}) {
  const headers = { ...(options.headers || {}) }

  if (options.body && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json'
  }

  const response = await fetch(url, {
    method: options.method,
    headers,
    body: options.body,
  })

  const text = await response.text()
  let data = null

  if (text) {
    try {
      data = JSON.parse(text)
    } catch (_error) {
      data = text
    }
  }

  if (!response.ok) {
    throw new Error((data && data.message) || (data && data.error) || text || 'Request failed')
  }

  return data
}

function showAlert(id, message, type = 'success') {
  const el = document.getElementById(id)
  if (!el) return

  el.className = `alert alert-${type}`
  el.textContent = message
  el.hidden = false
}

function hideAlert(id) {
  const el = document.getElementById(id)
  if (!el) return

  el.hidden = true
  el.textContent = ''
  el.className = 'alert d-none'
}

async function signUp(event) {
  event.preventDefault()
  hideAlert('signupAlert')

  const email = document.getElementById('signupEmail')?.value.trim().toLowerCase() || ''
  const password = document.getElementById('signupPassword')?.value || ''
  const confirm = document.getElementById('signupConfirmPassword')?.value || ''

  if (!email || !password) {
    showAlert('signupAlert', 'Please fill out both fields.', 'warning')
    return
  }

  if (password !== confirm) {
    showAlert('signupAlert', 'Passwords do not match.', 'warning')
    return
  }

  try {
    const user = await request('/auth/register', {
      method: 'POST',
      body: JSON.stringify({
        email,
        password,
        role: 'CUSTOMER',
      }),
    })

    showAlert('signupAlert', `Account created for ${user.email}.`, 'success')

    const link = document.getElementById('loginLink')
    if (link) {
      link.href = `login.html?email=${encodeURIComponent(user.email)}`
    }
  } catch (error) {
    showAlert('signupAlert', error.message, 'danger')
  }
}

async function signIn(event) {
  event.preventDefault()
  hideAlert('loginAlert')

  const email = document.getElementById('loginEmail')?.value.trim().toLowerCase() || ''
  const password = document.getElementById('loginPassword')?.value || ''

  if (!email || !password) {
    showAlert('loginAlert', 'Enter email and password.', 'warning')
    return
  }

  try {
    const data = await request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    })

    localStorage.setItem('campus_marketplace_token', data.token)
    localStorage.setItem('campus_marketplace_email', data.email)
    localStorage.setItem('campus_marketplace_role', data.role)
    localStorage.setItem('campus_marketplace_user_id', data.id)
    showAlert('loginAlert', 'Logged in. Redirecting...', 'success')

    setTimeout(() => {
      window.location.href = 'index.html'
    }, 400)
  } catch (error) {
    showAlert('loginAlert', error.message, 'danger')
  }
}

function renderProducts(items) {
  const grid = document.getElementById('listingsGrid')
  const empty = document.getElementById('noListings')
  if (!grid) return

  if (!items.length) {
    grid.innerHTML = ''
    if (empty) empty.classList.remove('d-none')
    return
  }

  if (empty) empty.classList.add('d-none')

  grid.innerHTML = items
    .map(item => {
      const available = Number(item.stock || 0) > 0
      const price = Number(item.price || 0).toFixed(2)
      const description = item.description || 'Campus listing'

      return `
        <div class="col-12 col-md-6 col-xl-4">
          <div class="card h-100 shadow-sm">
            <div class="card-body d-flex flex-column">
              <div class="d-flex justify-content-between gap-3">
                <div>
                  <div class="text-muted small">Listing #${item.id}</div>
                  <h2 class="h5 mb-1">${item.name}</h2>
                </div>
                <span class="badge ${available ? 'text-bg-success' : 'text-bg-secondary'}">
                  ${available ? `${item.stock} available` : 'Sold out'}
                </span>
              </div>
              <p class="text-muted mt-3 mb-4">${description}</p>
              <div class="mt-auto d-flex justify-content-between align-items-center">
                <strong>$${price}</strong>
                <button class="btn btn-outline-primary btn-sm" type="button" disabled>Browse only</button>
              </div>
            </div>
          </div>
        </div>
      `
    })
    .join('')
}

function filterProducts() {
  const query = document.getElementById('searchListings')?.value.trim().toLowerCase() || ''
  const filtered = query
    ? products.filter(item => `${item.name} ${item.description || ''}`.toLowerCase().includes(query))
    : products

  renderProducts(filtered)

  const status = document.getElementById('listingsStatus')
  if (status) {
    status.textContent = `${filtered.length} listing${filtered.length === 1 ? '' : 's'} available`
  }
}

async function loadProducts() {
  const status = document.getElementById('listingsStatus')
  if (status) status.textContent = 'Loading listings...'

  try {
    products = await request('/products')
    filterProducts()
  } catch (error) {
    if (status) status.textContent = error.message
    const grid = document.getElementById('listingsGrid')
    if (grid) {
      grid.innerHTML = '<div class="col-12"><div class="alert alert-danger mb-0">Could not load listings.</div></div>'
    }
  }
}

document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('signupForm')?.addEventListener('submit', signUp)
  document.getElementById('loginForm')?.addEventListener('submit', signIn)
  document.getElementById('searchListings')?.addEventListener('input', filterProducts)

  document.querySelectorAll("[data-action='logout']").forEach(el => {
    el.addEventListener('click', () => {
      localStorage.removeItem('campus_marketplace_token')
      localStorage.removeItem('campus_marketplace_email')
      localStorage.removeItem('campus_marketplace_role')
      localStorage.removeItem('campus_marketplace_user_id')
      window.location.href = 'index.html'
    })
  })

  const email = new URLSearchParams(window.location.search).get('email')
  const loginEmail = document.getElementById('loginEmail')
  if (email && loginEmail) {
    loginEmail.value = email
  }

  const signedIn = Boolean(localStorage.getItem('campus_marketplace_token'))
  document.querySelectorAll("[data-auth='signed-in']").forEach(el => {
    el.classList.toggle('d-none', !signedIn)
  })
  document.querySelectorAll("[data-auth='signed-out']").forEach(el => {
    el.classList.toggle('d-none', signedIn)
  })

  const note = document.getElementById('accountNote')
  if (note) {
    note.textContent = signedIn
      ? `Signed in as ${localStorage.getItem('campus_marketplace_email')}`
      : 'Browse the marketplace or create an account.'
  }

  if (document.getElementById('listingsGrid')) {
    loadProducts()
  }
})
