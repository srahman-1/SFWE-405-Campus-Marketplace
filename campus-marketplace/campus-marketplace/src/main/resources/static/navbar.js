function loadNavbar() {
  const isAdmin = localStorage.getItem('campus_marketplace_role') === 'admin'
  const navbarHTML = `
  <nav class="navbar navbar-expand-lg navbar-light bg-light border-bottom">
    <div class="container d-flex align-items-center">
      <a class="navbar-brand" href="index.html">Campus Marketplace</a>

      <div class="d-flex gap-2 ms-auto">
        <a class="btn btn-outline-primary btn-sm" data-auth="signed-out" href="login.html">Log In</a>
        <a class="btn btn-primary btn-sm" data-auth="signed-out" href="signup.html">Sign Up</a>

        <a class="btn btn-outline-primary btn-sm position-relative d-none" data-auth="signed-in" href="/cart.html">
          Cart
          <span id="cartCount" class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger d-none">0</span>
        </a>

        <a class="btn btn-outline-primary btn-sm d-none" data-auth="signed-in" href="/products.html">Your Listings</a>
        <a class="btn btn-outline-primary btn-sm d-none" data-auth="signed-in" href="/orders.html">Orders & Sales</a>
        ${isAdmin ? '<a class="btn btn-outline-danger btn-sm d-none" data-auth="signed-in" href="/admin.html">Admin</a>' : ''}
        <button id="settingsBtn" class="btn btn-outline-secondary btn-sm d-none" data-auth="signed-in" type="button">Settings</button>
        <button class="btn btn-outline-secondary btn-sm d-none" data-auth="signed-in" data-action="logout" type="button">Log Out</button>
      </div>
    </div>
  </nav>`

  document.getElementById('navbar-container').innerHTML = navbarHTML
}

document.addEventListener('DOMContentLoaded', loadNavbar)
