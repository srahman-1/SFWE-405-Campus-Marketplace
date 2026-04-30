let products = []
let myProducts = []
let cartItems = []
let listingSelections = {}
let listingDetail = null
let listingDetailQuantity = 1
let editingProductId = null
let deletingProductId = null

const onListingsPage =
  window.location.pathname.endsWith('/listings.html') || window.location.pathname.endsWith('listings.html')
const onOrdersPage =
  window.location.pathname.endsWith('/orders.html') || window.location.pathname.endsWith('orders.html')

function request(url, options = {}) {
  const headers = { ...(options.headers || {}) }

  if (options.body && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json'
  }

  return fetch(url, {
    method: options.method,
    headers,
    body: options.body,
  }).then(async response => {
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
  })
}

function authHeaders() {
  const token = localStorage.getItem('campus_marketplace_token')
  return token ? { Authorization: `Bearer ${token}` } : {}
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

function showToast(message, type = 'success') {
  let wrap = document.getElementById('toastWrap')

  if (!wrap) {
    wrap = document.createElement('div')
    wrap.id = 'toastWrap'
    wrap.className = 'toast-container position-fixed top-0 end-0 p-3'
    wrap.style.zIndex = '1080'
    document.body.appendChild(wrap)
  }

  const toast = document.createElement('div')
  toast.className = `toast align-items-center text-bg-${type} border-0 show`
  toast.setAttribute('role', 'alert')
  toast.setAttribute('aria-live', 'assertive')
  toast.setAttribute('aria-atomic', 'true')
  const row = document.createElement('div')
  row.className = 'd-flex'

  const body = document.createElement('div')
  body.className = 'toast-body'
  body.textContent = message

  const close = document.createElement('button')
  close.type = 'button'
  close.className = 'btn-close btn-close-white me-2 m-auto'
  close.setAttribute('aria-label', 'Close')

  row.append(body, close)
  toast.appendChild(row)

  wrap.appendChild(toast)

  const closeToast = () => {
    toast.remove()
    if (!wrap.children.length) {
      wrap.remove()
    }
  }

  close.addEventListener('click', closeToast)
  setTimeout(closeToast, 2200)
}

function determineCartQtyChange(quantity, stock) {
  const max = Number(stock || 0)
  if (max <= 0) return 0

  const value = Number(quantity || 1)
  if (value < 1) return 1
  if (value > max) return max
  return value
}

function listingQuantity(productId, stock) {
  return determineCartQtyChange(listingSelections[productId], stock) || 1
}

function currentUserId() {
  return localStorage.getItem('campus_marketplace_user_id')
}

function isOwnedListing(item) {
  const userId = currentUserId()
  return Boolean(userId) && String(item.ownerId || '') === String(userId)
}

function updateNav() {
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
}

function formatDateTime(value) {
  if (!value) return 'Unknown date'
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? String(value) : date.toLocaleString()
}

function updateCartCount() {
  const badge = document.getElementById('cartCount')
  if (!badge) return

  const buyerId = localStorage.getItem('campus_marketplace_user_id')
  if (!buyerId || !localStorage.getItem('campus_marketplace_token')) {
    badge.textContent = '0'
    badge.classList.add('d-none')
    return
  }

  request(`/cart/${buyerId}`, { headers: authHeaders() })
    .then(cart => {
      const count = (cart.items || []).reduce((sum, item) => sum + item.quantity, 0)
      badge.textContent = String(count)
      badge.classList.toggle('d-none', count === 0)
    })
    .catch(() => {
      badge.textContent = '0'
      badge.classList.add('d-none')
    })
}

function logout() {
  localStorage.removeItem('campus_marketplace_token')
  localStorage.removeItem('campus_marketplace_email')
  localStorage.removeItem('campus_marketplace_user_id')
  localStorage.removeItem('campus_marketplace_role')
  window.location.href = 'index.html'
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
      body: JSON.stringify({ email, password }),
    })

    showAlert('signupAlert', `Account created for ${user.email}.`, 'success')

    const link = document.getElementById('loginLink')
    if (link) link.href = `login.html?email=${encodeURIComponent(user.email)}`
  } catch (error) {
    if ((error.message || '').toLowerCase().includes('already registered')) {
      showAlert('signupAlert', 'That email is already registered. Try logging in instead.', 'danger')
      return
    }

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
    localStorage.setItem('campus_marketplace_user_id', data.id)
    localStorage.setItem('campus_marketplace_role', data.role)
    showAlert('loginAlert', 'Logged in. Redirecting...', 'success')

    updateNav()
    updateCartCount()

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
  const signedIn = Boolean(localStorage.getItem('campus_marketplace_token'))

  if (!items.length) {
    grid.innerHTML = ''
    if (empty) empty.classList.remove('d-none')
    return
  }

  if (empty) empty.classList.add('d-none')

  grid.innerHTML = items
    .map(item => {
      const available = Number(item.stock || 0) > 0
      const mine = isOwnedListing(item)
      const quantity = listingQuantity(item.id, item.stock)
      const price = Number(item.price || 0).toFixed(2)
      const description = item.description || 'Campus listing'

      return `
        <div class="col-12 col-md-6 col-xl-4">
          <div class="card h-100 shadow-sm">
            <div class="card-body d-flex flex-column">
              <div class="d-flex justify-content-between gap-3">
                <div>
                  <div class="text-muted small">Listing #${item.id}</div>
                  <div class="d-flex align-items-center gap-2 flex-wrap">
                    <h2 class="h5 mb-1">${item.name}</h2>
                    ${mine ? '<span class="badge text-bg-info">Yours</span>' : ''}
                  </div>
                </div>
                <span class="badge ${available ? 'text-bg-success' : 'text-bg-secondary'}">
                  ${available ? `${item.stock} available` : 'Sold out'}
                </span>
              </div>
              <p class="text-muted mt-3 mb-4">${description}</p>
              <div class="mt-auto d-flex justify-content-between align-items-center gap-2 flex-wrap">
                <strong>$${price}</strong>
                <a class="btn btn-outline-primary btn-sm" href="listing.html?id=${item.id}">View item</a>
              </div>
              ${
                mine
                  ? `
                <div class="d-flex gap-2 justify-content-end flex-wrap mt-3">
                  <button class="btn btn-outline-secondary btn-sm" type="button" data-edit-product="${item.id}">Edit</button>
                  <button class="btn btn-outline-danger btn-sm" type="button" data-delete-product="${item.id}">Delete</button>
                </div>
              `
                  : ''
              }
              ${
                onListingsPage && signedIn
                  ? `
                <div class="d-flex align-items-center justify-content-end gap-2 flex-wrap mt-3">
                  <div class="d-inline-flex align-items-center border rounded ${available ? '' : 'opacity-50'}">
                    <button class="btn btn-light btn-sm border-0" type="button" data-qty-source="listing" data-product-id="${item.id}" data-stock="${item.stock}" data-qty-action="minus" ${available && quantity > 1 ? '' : 'disabled'}>-</button>
                    <span class="px-3 fw-semibold">${available ? quantity : 0}</span>
                    <button class="btn btn-light btn-sm border-0" type="button" data-qty-source="listing" data-product-id="${item.id}" data-stock="${item.stock}" data-qty-action="plus" ${available && quantity < item.stock ? '' : 'disabled'}>+</button>
                  </div>
                  <button class="btn btn-primary btn-sm" type="button" data-add-to-cart="${item.id}" data-quantity="${quantity}" data-stock="${item.stock}" ${available ? '' : 'disabled'}>
                    Add ${available ? quantity : 0} to cart
                  </button>
                </div>
              `
                  : ''
              }
            </div>
          </div>
        </div>
      `
    })
    .join('')
}

function renderMyListings(items) {
  const grid = document.getElementById('myListingsGrid')
  const empty = document.getElementById('noMyListings')
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
      const mine = isOwnedListing(item)
      const price = Number(item.price || 0).toFixed(2)
      const description = item.description || 'Campus listing'

      return `
        <div class="col-12">
          <div class="card h-100 shadow-sm">
            <div class="card-body d-flex flex-column flex-md-row justify-content-between gap-3">
              <div>
                <div class="text-muted small">Listing #${item.id}</div>
                <div class="d-flex align-items-center gap-2 flex-wrap">
                  <h3 class="h5 mb-1">${item.name}</h3>
                  ${mine ? '<span class="badge text-bg-info">Yours</span>' : ''}
                </div>
                <p class="text-muted mb-0">${description}</p>
              </div>
              <div class="text-md-end">
                <div class="fw-semibold">$${price}</div>
                <span class="badge ${available ? 'text-bg-success' : 'text-bg-secondary'}">
                  ${available ? `${item.stock} available` : 'Sold out'}
                </span>
              </div>
            </div>
            ${
              mine
                ? `
              <div class="card-footer bg-white border-0 pt-0">
                <div class="d-flex gap-2 justify-content-end flex-wrap">
                  <button class="btn btn-outline-secondary btn-sm" type="button" data-edit-product="${item.id}">Edit</button>
                  <button class="btn btn-outline-danger btn-sm" type="button" data-delete-product="${item.id}">Delete</button>
                </div>
              </div>
            `
                : ''
            }
          </div>
        </div>
      `
    })
    .join('')

  // Inject modals if not already present
  if (!document.getElementById('editModal')) {
    document.body.insertAdjacentHTML(
      'beforeend',
      `
      <div class="modal fade" id="editModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Edit Listing</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label class="form-label">Name</label>
                <input id="editName" class="form-control">
              </div>
              <div class="mb-3">
                <label class="form-label">Description</label>
                <input id="editDescription" class="form-control">
              </div>
              <div class="mb-3">
                <label class="form-label">Price</label>
                <input id="editPrice" class="form-control" type="number" step="0.01" min="0.01">
              </div>
              <div class="mb-3">
                <label class="form-label">Quantity available</label>
                <input id="editStock" class="form-control" type="number" min="0" step="1">
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
              <button type="button" class="btn btn-primary" id="editModalSave">Save changes</button>
            </div>
          </div>
        </div>
      </div>

      <div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-sm">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Delete Listing</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              Delete <strong id="deleteModalName"></strong>? This cannot be undone.
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
              <button type="button" class="btn btn-danger" id="deleteModalConfirm">Delete</button>
            </div>
          </div>
        </div>
      </div>
    `
    )
  }
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

async function loadMyListings() {
  const status = document.getElementById('myListingsStatus')
  const grid = document.getElementById('myListingsGrid')
  if (!grid) return

  const token = localStorage.getItem('campus_marketplace_token')
  if (!token) {
    grid.innerHTML = `
      <div class="col-12">
        <div class="alert alert-info mb-0">
          Please <a href="login.html" class="alert-link">log in</a> to view your listings.
        </div>
      </div>
    `
    if (status) status.textContent = 'Sign in to manage your listings.'
    return
  }

  if (status) status.textContent = 'Loading your listings...'

  try {
    const items = await request('/products/me', { headers: authHeaders() })
    myProducts = items
    renderMyListings(items)

    if (status) {
      status.textContent = `${items.length} listing${items.length === 1 ? '' : 's'} posted`
    }
  } catch (error) {
    if (status) status.textContent = error.message
    grid.innerHTML = `
      <div class="col-12">
        <div class="alert alert-danger mb-0">Could not load your listings.</div>
      </div>
    `
  }
}

async function createProduct(event) {
  event?.preventDefault?.()

  const token = localStorage.getItem('campus_marketplace_token')
  if (!token) {
    window.location.href = 'login.html'
    return
  }

  const name = document.getElementById('name')?.value.trim() || ''
  const description = document.getElementById('description')?.value.trim() || ''
  const price = Number(document.getElementById('price')?.value || 0)
  const stockField = document.getElementById('stock')
  const stockValue = stockField?.value
  const stock = stockValue === '' || stockValue == null ? undefined : Number(stockValue)

  if (!name || !Number.isFinite(price) || price <= 0) {
    showToast('Enter a name and a valid price.', 'warning')
    return
  }

  try {
    await request('/products', {
      method: 'POST',
      headers: authHeaders(),
      body: JSON.stringify({
        name,
        description,
        price,
        stock,
      }),
    })

    showToast('Listing created.', 'success')

    if (stockField) stockField.value = ''
    if (document.getElementById('name')) document.getElementById('name').value = ''
    if (document.getElementById('description')) document.getElementById('description').value = ''
    if (document.getElementById('price')) document.getElementById('price').value = ''

    if (document.getElementById('myListingsGrid')) {
      loadMyListings()
    }
    if (document.getElementById('listingsGrid')) {
      loadProducts()
    }
  } catch (error) {
    showToast(error.message, 'danger')
  }
}

async function editProduct(productId) {
  const item =
    myProducts.find(p => String(p.id) === String(productId)) || products.find(p => String(p.id) === String(productId))
  if (!item) {
    showToast('Listing not found.', 'danger')
    return
  }

  document.getElementById('editName').value = item.name || ''
  document.getElementById('editDescription').value = item.description || ''
  document.getElementById('editPrice').value = item.price ?? ''
  document.getElementById('editStock').value = item.stock ?? 0

  const modalEl = document.getElementById('editModal')
  const modal = new bootstrap.Modal(modalEl)
  modal.show()

  modalEl.querySelector('#editModalSave').onclick = async () => {
    const name = document.getElementById('editName').value.trim()
    const description = document.getElementById('editDescription').value.trim()
    const price = Number(document.getElementById('editPrice').value)
    const stock = Number(document.getElementById('editStock').value)

    if (!name || !Number.isFinite(price) || price <= 0 || !Number.isInteger(stock) || stock < 0) {
      showToast('Please enter a valid name, price, and stock.', 'warning')
      return
    }

    try {
      await request(`/products/${productId}`, {
        method: 'PUT',
        headers: authHeaders(),
        body: JSON.stringify({ name, description, price, stock }),
      })
      modal.hide()
      showToast('Listing updated.', 'success')
      loadProducts()
      loadMyListings()
    } catch (error) {
      showToast(error.message, 'danger')
    }
  }
}

async function deleteProduct(productId) {
  const item =
    myProducts.find(p => String(p.id) === String(productId)) || products.find(p => String(p.id) === String(productId))

  document.getElementById('deleteModalName').textContent = item?.name || `#${productId}`

  const modalEl = document.getElementById('deleteModal')
  const modal = new bootstrap.Modal(modalEl)
  modal.show()

  modalEl.querySelector('#deleteModalConfirm').onclick = async () => {
    try {
      await request(`/products/${productId}`, { method: 'DELETE', headers: authHeaders() })
      modal.hide()
      showToast('Listing deleted.', 'success')
      loadProducts()
      loadMyListings()
    } catch (error) {
      showToast(error.message, 'danger')
    }
  }
}

async function addToCart(productId, quantity, stock) {
  const amount = determineCartQtyChange(quantity, stock)
  const buyerId = localStorage.getItem('campus_marketplace_user_id')
  if (!buyerId || !Boolean(localStorage.getItem('campus_marketplace_token'))) {
    window.location.href = 'login.html'
    return
  }

  if (!amount) {
    showToast('That item is sold out.', 'warning')
    return
  }

  try {
    await request(`/cart/${buyerId}/items`, {
      method: 'POST',
      headers: authHeaders(),
      body: JSON.stringify({ productId, quantity: amount }),
    })

    updateCartCount()
    showToast(`Added ${amount} to cart.`, 'success')
  } catch (error) {
    showToast(error.message, 'danger')
  }
}

async function loadListingDetail() {
  const container = document.getElementById('listingDetail')
  if (!container) return

  const id = new URLSearchParams(window.location.search).get('id')
  if (!id) {
    container.innerHTML = '<div class="alert alert-danger">No listing selected.</div>'
    return
  }

  try {
    const allProducts = await request('/products')
    const item = allProducts.find(product => String(product.id) === String(id))

    if (!item) {
      container.innerHTML = '<div class="alert alert-warning">Listing not found.</div>'
      return
    }

    listingDetail = item
    listingDetailQuantity = 1

    renderListingDetail(container, item)
  } catch (error) {
    container.innerHTML = `<div class="alert alert-danger">${error.message}</div>`
  }
}

function renderListingDetail(container, item) {
  const available = Number(item.stock || 0) > 0
  const quantity = determineCartQtyChange(listingDetailQuantity, item.stock)
  listingDetailQuantity = quantity || 1
  const signedIn = Boolean(localStorage.getItem('campus_marketplace_token'))

  container.innerHTML = `
      <div class="card shadow-sm">
        <div class="card-body p-4 p-md-5">
          <div class="row g-4 align-items-start">
            <div class="col-12 col-lg-8">
              <div class="text-muted small mb-2">Listing #${item.id}</div>
              <h1 class="h3 mb-3">${item.name}</h1>
              <p class="text-muted mb-4">${item.description || 'Campus listing'}</p>
              <div class="d-flex gap-2 flex-wrap">
                <span class="badge ${available ? 'text-bg-success' : 'text-bg-secondary'}">
                  ${available ? `${item.stock} available` : 'Sold out'}
                </span>
                <span class="badge text-bg-light text-dark">$${Number(item.price || 0).toFixed(2)}</span>
              </div>
            </div>
            <div class="col-12 col-lg-4">
              <div class="card bg-light border-0">
                <div class="card-body">
                  <h2 class="h5 mb-3">Buy this item</h2>
                  <p class="text-muted small mb-3">Choose how many you want before adding it to your cart.</p>
                  <div class="mb-3">
                    <span class="text-muted small">${available ? `${item.stock} available` : 'Sold out'}</span>
                  </div>
                  ${
                    signedIn
                      ? `
                  <div class="d-flex justify-content-end mb-3">
                    <div class="d-inline-flex align-items-center border rounded ${available ? '' : 'opacity-50'}">
                      <button class="btn btn-light btn-sm border-0" type="button" data-qty-source="detail" data-product-id="${item.id}" data-stock="${item.stock}" data-qty-action="minus" ${available && quantity > 1 ? '' : 'disabled'}>-</button>
                      <span class="px-3 fw-semibold">${available ? quantity : 0}</span>
                      <button class="btn btn-light btn-sm border-0" type="button" data-qty-source="detail" data-product-id="${item.id}" data-stock="${item.stock}" data-qty-action="plus" ${available && quantity < item.stock ? '' : 'disabled'}>+</button>
                    </div>
                  </div>
                    <button class="btn btn-primary w-100" type="button" data-add-to-cart="${item.id}" data-quantity="${quantity}" data-stock="${item.stock}" ${available ? '' : 'disabled'}>
                      Add ${available ? quantity : 0} to cart
                    </button>
                  `
                      : `
                    <a class="btn btn-primary w-100" href="login.html">Log in to add</a>
                  `
                  }
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    `
  return container
}

async function loadCart() {
  const container = document.getElementById('cartContent')
  if (!container) return

  const buyerId = localStorage.getItem('campus_marketplace_user_id')
  if (!buyerId || !localStorage.getItem('campus_marketplace_token')) {
    container.innerHTML = `
      <div class="alert alert-info">
        Please <a href="login.html" class="alert-link">log in</a> to view your cart.
      </div>
    `
    return
  }

  try {
    const [cart, allProducts] = await Promise.all([
      request(`/cart/${buyerId}`, { headers: authHeaders() }),
      request('/products'),
    ])
    cartItems = cart.items || []
    const stockById = {}
    allProducts.forEach(product => {
      stockById[String(product.id)] = Number(product.stock || 0)
    })

    if (!cartItems.length) {
      container.innerHTML = '<div class="alert alert-light border">Your cart is empty.</div>'
      return
    }

    container.innerHTML = `
      <div class="card shadow-sm">
        <div class="card-body p-0">
          <div class="table-responsive">
            <table class="table mb-0 align-middle">
              <thead class="table-light">
                <tr>
                  <th>Item</th>
                  <th class="text-center">Qty</th>
                  <th class="text-end">Price</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                ${cartItems
                  .map(item => {
                    const stock = Number(stockById[String(item.productId)] || 0)
                    const nextUp = item.quantity + 1
                    return `
                  <tr>
                    <td>
                      <div class="fw-semibold">${item.productName}</div>
                      <div class="text-muted small">Listing #${item.productId}</div>
                    </td>
                    <td class="text-center">
                      <div class="d-inline-flex align-items-center gap-2">
                        <button class="btn btn-outline-secondary btn-sm" type="button" data-cart-change="${item.productId}" data-cart-qty="${item.quantity - 1}">-</button>
                        <span class="fw-semibold" style="min-width: 1.5rem;">${item.quantity}</span>
                        <button class="btn btn-outline-secondary btn-sm" type="button" data-cart-change="${item.productId}" data-cart-qty="${nextUp}" data-cart-stock="${stock}" ${stock > 0 && item.quantity < stock ? '' : 'disabled'}>+</button>
                      </div>
                      <div class="text-muted small mt-1">${stock ? `${stock} available` : 'Sold out'}</div>
                    </td>
                    <td class="text-end">$${Number(item.lineTotal || 0).toFixed(2)}</td>
                    <td class="text-end">
                      <button class="btn btn-outline-danger btn-sm" type="button" data-remove-from-cart="${item.productId}">Remove</button>
                    </td>
                  </tr>
                  `
                  })
                  .join('')}
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <div class="card shadow-sm mt-4">
        <div class="card-body d-flex justify-content-between align-items-center">
          <div>
            <div class="text-muted small">Cart total</div>
            <div class="h4 mb-0">$${Number(cart.total || 0).toFixed(2)}</div>
          </div>
          <div class="d-flex gap-2 flex-wrap justify-content-end">
            <button class="btn btn-primary" type="button" data-checkout-cart>Checkout</button>
            <a class="btn btn-outline-secondary" href="index.html">Keep shopping</a>
          </div>
        </div>
      </div>
    `

    document.querySelectorAll('[data-remove-from-cart]').forEach(button => {
      button.addEventListener('click', async () => {
        try {
          await request(`/cart/${buyerId}/items/${button.dataset.removeFromCart}`, {
            method: 'DELETE',
            headers: authHeaders(),
          })
          loadCart()
          updateCartCount()
        } catch (error) {
          showToast(error.message, 'danger')
        }
      })
    })

    document.querySelectorAll('[data-cart-change]').forEach(button => {
      button.addEventListener('click', async () => {
        const nextQuantity = Number(button.dataset.cartQty || 0)
        const productId = button.dataset.cartChange
        const stock = Number(button.dataset.cartStock || 0)

        if (stock > 0 && nextQuantity > stock) {
          showToast('That is all the stock that is left.', 'warning')
          return
        }

        try {
          if (nextQuantity <= 0) {
            await request(`/cart/${buyerId}/items/${productId}`, {
              method: 'DELETE',
              headers: authHeaders(),
            })
          } else {
            await request(`/cart/${buyerId}/items/${productId}?quantity=${nextQuantity}`, {
              method: 'PUT',
              headers: authHeaders(),
            })
          }

          loadCart()
          updateCartCount()
        } catch (error) {
          showToast(error.message, 'danger')
        }
      })
    })

    document.querySelectorAll('[data-checkout-cart]').forEach(button => {
      button.addEventListener('click', async () => {
        try {
          const response = await request(`/cart/${buyerId}/checkout`, {
            method: 'POST',
            headers: authHeaders(),
            body: JSON.stringify({
              paymentMethod: 'Card',
              forcePaymentFailure: false,
            }),
          })

          if (!response.success) {
            showToast(response.message || 'Checkout failed.', 'danger')
            return
          }

          updateCartCount()
          showToast('Purchase successful. Opening your orders...', 'success')
          setTimeout(() => {
            window.location.href = 'orders.html'
          }, 500)
        } catch (error) {
          showToast(error.message, 'danger')
        }
      })
    })
  } catch (error) {
    container.innerHTML = `<div class="alert alert-danger">${error.message}</div>`
  }
}

async function loadOrders() {
  const container = document.getElementById('ordersContent')
  if (!container) return

  if (!localStorage.getItem('campus_marketplace_token')) {
    container.innerHTML = `
      <div class="alert alert-info">
        Please <a href="login.html" class="alert-link">log in</a> to view your order history.
      </div>
    `
    return
  }

  try {
    const orders = await request('/orders/me', { headers: authHeaders() })

    if (!orders.length) {
      container.innerHTML = `
        <div class="alert alert-light border">
          You have not placed any orders yet.
          <a class="alert-link" href="index.html">Start shopping</a>.
        </div>
      `
      return
    }

    container.innerHTML = `
      <div class="row g-3">
        ${orders
          .map(
            order => `
            <div class="col-12">
              <div class="card shadow-sm">
                <div class="card-body">
                  <div class="d-flex flex-wrap justify-content-between gap-3 align-items-start">
                    <div>
                      <div class="text-muted small">Order #${order.orderId}</div>
                      <h2 class="h5 mb-1">${order.productName}</h2>
                      <div class="text-muted small">Placed ${formatDateTime(order.createdAt)}</div>
                    </div>
                    <span class="badge ${order.transactionType === 'SALE' ? 'text-bg-primary' : 'text-bg-success'}">
                      ${order.transactionType === 'SALE' ? 'Sale' : 'Purchase'}
                    </span>
                  </div>
                  <hr>
                  <div class="row g-3 small">
                    <div class="col-12 col-md-3">
                      <div class="text-muted">Product</div>
                      <div class="fw-semibold">#${order.productId}</div>
                    </div>
                    <div class="col-12 col-md-3">
                      <div class="text-muted">${order.transactionType === 'SALE' ? 'Sold to' : 'Buyer'}</div>
                      <div class="fw-semibold">${order.buyerEmail || 'Unknown buyer'}</div>
                    </div>
                    <div class="col-12 col-md-3">
                      <div class="text-muted">Quantity</div>
                      <div class="fw-semibold">${Number(order.quantity || 1)}</div>
                    </div>
                    <div class="col-12 col-md-3">
                      <div class="text-muted">Total</div>
                      <div class="fw-semibold">$${(Number(order.productPrice || 0) * Number(order.quantity || 1)).toFixed(2)}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          `
          )
          .join('')}
      </div>
    `
  } catch (error) {
    container.innerHTML = `<div class="alert alert-danger">${error.message}</div>`
  }
}

function openSettingsModal() {
  const modalElement = document.getElementById('settingsModal')
  const emailInput = document.getElementById('settingsEmail')
  const passwordInput = document.getElementById('settingsPassword')
  const message = document.getElementById('settingsMessage')

  if (!modalElement) return

  if (emailInput) {
    emailInput.value = localStorage.getItem('campus_marketplace_email') || ''
  }

  if (passwordInput) {
    passwordInput.value = ''
  }

  if (message) {
    message.textContent = ''
    message.className = 'mt-3 text-muted small'
  }

  const modal = new bootstrap.Modal(modalElement)
  modal.show()
}

async function saveAccountSettings() {
  const email = document.getElementById('settingsEmail')?.value.trim().toLowerCase() || ''
  const password = document.getElementById('settingsPassword')?.value || ''
  const message = document.getElementById('settingsMessage')

  if (!email && !password) {
    if (message) {
      message.textContent = 'Enter a new email or password.'
      message.className = 'mt-3 text-warning small'
    }
    return
  }

  try {
    const updated = await request('/users/me', {
      method: 'PUT',
      headers: authHeaders(),
      body: JSON.stringify({ email, password }),
    })

    if (updated && updated.email) {
      localStorage.setItem('campus_marketplace_email', updated.email)
    } else if (email) {
      localStorage.setItem('campus_marketplace_email', email)
    }

    updateNav()

    if (message) {
      message.textContent = 'Account updated successfully.'
      message.className = 'mt-3 text-success small'
    }

    showToast('Account settings updated.', 'success')
  } catch (error) {
    if (message) {
      message.textContent = error.message || 'Could not update account.'
      message.className = 'mt-3 text-danger small'
    }
  }
}

async function deleteCurrentAccount() {
  const confirmed = confirm('Are you sure you want to delete your account? This cannot be undone.')

  if (!confirmed) {
    return
  }

  try {
    await request('/users/me', {
      method: 'DELETE',
      headers: authHeaders(),
    })

    alert('Your account has been deleted.')

    localStorage.removeItem('campus_marketplace_token')
    localStorage.removeItem('campus_marketplace_email')
    localStorage.removeItem('campus_marketplace_role')
    localStorage.removeItem('campus_marketplace_user_id')

    window.location.href = 'signup.html'
  } catch (error) {
    showToast(error.message || 'Could not delete account.', 'danger')
  }
}

document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('signupForm')?.addEventListener('submit', signUp)
  document.getElementById('loginForm')?.addEventListener('submit', signIn)
  document.getElementById('searchListings')?.addEventListener('input', filterProducts)

  // Settings menu buttons
  document.getElementById('settingsBtn')?.addEventListener('click', openSettingsModal)
  document.getElementById('saveSettingsBtn')?.addEventListener('click', saveAccountSettings)
  document.getElementById('deleteAccountBtn')?.addEventListener('click', deleteCurrentAccount)

  document.querySelectorAll("[data-action='logout']").forEach(el => {
    el.addEventListener('click', logout)
  })

  document.addEventListener('click', event => {
    const addButton = event.target.closest('[data-add-to-cart]')
    if (addButton) {
      addToCart(addButton.dataset.addToCart, addButton.dataset.quantity, addButton.dataset.stock)
      return
    }

    const editButton = event.target.closest('[data-edit-product]')
    if (editButton) {
      editProduct(editButton.dataset.editProduct)
      return
    }

    const deleteButton = event.target.closest('[data-delete-product]')
    if (deleteButton) {
      deleteProduct(deleteButton.dataset.deleteProduct)
      return
    }

    const qtyButton = event.target.closest('[data-qty-action]')
    if (!qtyButton) return

    const action = qtyButton.dataset.qtyAction
    const source = qtyButton.dataset.qtySource
    const productId = qtyButton.dataset.productId
    const stock = Number(qtyButton.dataset.stock || 0)

    if (source === 'listing') {
      const current = listingQuantity(productId, stock)
      if (action === 'plus') {
        listingSelections[productId] = determineCartQtyChange(current + 1, stock)
      } else {
        listingSelections[productId] = determineCartQtyChange(current - 1, stock)
      }
      filterProducts()
      return
    }

    if (source === 'detail' && listingDetail && String(listingDetail.id) === String(productId)) {
      if (action === 'plus') {
        listingDetailQuantity = determineCartQtyChange(listingDetailQuantity + 1, stock)
      } else {
        listingDetailQuantity = determineCartQtyChange(listingDetailQuantity - 1, stock)
      }
      renderListingDetail(document.getElementById('listingDetail'), listingDetail)
    }
  })

  const email = new URLSearchParams(window.location.search).get('email')
  const loginEmail = document.getElementById('loginEmail')
  if (email && loginEmail) {
    loginEmail.value = email
  }

  updateNav()
  updateCartCount()

  if (document.getElementById('listingsGrid')) {
    loadProducts()
  }

  if (document.getElementById('listingDetail')) {
    loadListingDetail()
  }

  if (document.getElementById('cartContent')) {
    loadCart().then(updateCartCount)
  }

  if (document.getElementById('myListingsGrid')) {
    loadMyListings()
  }

  if (onOrdersPage) {
    loadOrders()
  }
})