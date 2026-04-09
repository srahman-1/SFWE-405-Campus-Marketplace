function handle(res) {
  if (!res.ok) throw new Error("Server error");
  return res.json();
}

/* USERS */

function createUser() {
  fetch('/users', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
      email: document.getElementById('email').value,
      passwordHash: document.getElementById('password').value,
      role: "STUDENT"
    })
  })
  .then(handle)
  .then(data => alert("User created — ID: " + data.id))
  .catch(err => alert(err.message));
}

function loadUsers() {
  fetch('/users')
    .then(handle)
    .then(data => {
      const list = document.getElementById('users');
      if (!list) return;
      list.innerHTML =
        data.map(u => `<li>${u.id} — ${u.email}</li>`).join('');
    })
    .catch(err => alert(err.message));
}


/* PRODUCTS */

function createProduct() {
  fetch('/products', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
      name: document.getElementById('name').value,
      description: document.getElementById('description').value,
      price: parseFloat(document.getElementById('price').value)
    })
  })
  .then(handle)
  .then(data => alert("Product added — ID: " + data.id))
  .catch(err => alert(err.message));
}

function loadProducts() {
  fetch('/products')
    .then(handle)
    .then(data => {
      const list = document.getElementById('products');
      if (!list) return;
      list.innerHTML =
        data.map(p => `<li>${p.id} — ${p.name} — $${p.price}</li>`).join('');
    })
    .catch(err => alert(err.message));
}


/* ORDERS */

function placeOrder() {
  fetch('/orders', {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
      buyerId: parseInt(document.getElementById('buyerId').value),
      productId: parseInt(document.getElementById('productId').value)
    })
  })
  .then(handle)
  .then(data => {
    const msg = document.getElementById("orderMsg");
    if (msg) {
      msg.innerHTML = "Order placed successfully — Order ID: " + data.id;
    } else {
      alert("Order placed — ID: " + data.id);
    }
  })
  .catch(err => alert(err.message));
}

function payOrder() {
  const id = document.getElementById('orderId').value;

  fetch(`/orders/${id}/pay`, { method: 'POST' })
    .then(handle)
    .then(() => {
      const msg = document.getElementById("payMsg");
      if (msg) {
        msg.innerHTML = "Payment successful — Thank you for shopping!";
      } else {
        alert("Payment successful");
      }
    })
    .catch(err => alert(err.message));
}
