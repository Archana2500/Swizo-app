<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="com.tap.model.Cart,com.tap.model.CartItem,com.tap.model.User"%>

<%!private static String esc(String s) {
		if (s == null)
			return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'",
				"&#39;");
	}

	// image path from the DB -> a URL the browser can load
	private static String imgUrl(String ctx, String p) {
		if (p == null || p.trim().isEmpty())
			return "";
		p = p.trim();
		if (p.startsWith("http://") || p.startsWith("https://"))
			return p;
		if (p.startsWith("/"))
			return ctx + p;
		if (p.startsWith("images/"))
			return ctx + "/" + p;
		return ctx + "/images/" + p;
	}%>

<%
Cart cart = (Cart) session.getAttribute("cart");
User user = (User) session.getAttribute("user");
String ctx = request.getContextPath();

if (user == null) {
	response.sendRedirect(ctx + "/login");
	return;
}

if (cart == null || cart.isEmpty()) {
	response.sendRedirect(ctx + "/cart");
	return;
}

double subtotal = cart.getSubtotal();
double deliveryFee = Cart.DELIVERY_FEE;
double totalAmount = subtotal + deliveryFee;

int totalItems = cart.getTotalQuantity();

String errorMessage = (String) request.getAttribute("errorMessage");

String fullName = request.getParameter("fullName");
String phone = request.getParameter("phone");
String address = request.getParameter("address");
String city = request.getParameter("city");
String pin = request.getParameter("pin");
String paymentMode = request.getParameter("paymentMode");

if (fullName == null)
	fullName = user.getName();
if (phone == null)
	phone = user.getPhone();
if (address == null)
	address = user.getAddress();
if (paymentMode == null)
	paymentMode = "COD";
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Checkout | Swizo</title>

<style>
:root {
	--orange: #fc8019;
	--orange-dark: #e56f0c;
	--orange-soft: #fff3e8;
	--ink: #1f2430;
	--muted: #7a8090;
	--line: #eceef2;
	--bg: #fffbf7;
}

* {
	box-sizing: border-box;
	margin: 0;
	padding: 0;
}

body {
	font-family: "Segoe UI", system-ui, sans-serif;
	background: var(--bg);
	color: var(--ink);
	line-height: 1.45;
}

.topbar {
	background: #fff;
	border-bottom: 1px solid var(--line);
	position: sticky;
	top: 0;
	z-index: 10;
}

.topbar-inner {
	max-width: 1180px;
	margin: auto;
	padding: 14px 20px;
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.brand {
	font-size: 1.5rem;
	font-weight: 800;
	color: var(--orange);
	text-decoration: none;
}

.nav {
	display: flex;
	align-items: center;
	gap: 8px;
}

.nav a {
	color: var(--ink);
	text-decoration: none;
	font-weight: 600;
	padding: 8px 16px;
	border-radius: 999px;
}

.nav a:hover {
	background: var(--orange-soft);
	color: var(--orange-dark);
}

.page {
	max-width: 1100px;
	margin: auto;
	padding: 36px 20px 60px;
}

.page-title {
	font-size: 2.2rem;
	font-weight: 800;
	margin-bottom: 30px;
}

.checkout-grid {
	display: grid;
	grid-template-columns: 1fr 380px;
	gap: 30px;
	align-items: start;
}

.section-card {
	background: #fff;
	border: 1px solid var(--line);
	border-radius: 20px;
	padding: 28px;
	margin-bottom: 24px;
	box-shadow: 0 4px 16px rgba(31, 36, 48, .04);
}

.section-header {
	display: flex;
	align-items: center;
	gap: 14px;
	margin-bottom: 20px;
}

.step-number {
	width: 32px;
	height: 32px;
	background: var(--orange);
	color: #fff;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-weight: 800;
}

.section-title-text h3 {
	font-size: 1.25rem;
	font-weight: 800;
}

.section-title-text p {
	color: var(--muted);
	font-size: .88rem;
}

.form-group {
	margin-bottom: 18px;
}

.form-row {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 16px;
}

label {
	display: block;
	font-weight: 700;
	font-size: .88rem;
	margin-bottom: 6px;
}

input[type="text"], textarea {
	width: 100%;
	padding: 12px 16px;
	border: 1px solid #d1d5db;
	border-radius: 12px;
	font-size: .95rem;
	outline: none;
	font-family: inherit;
}

input:focus, textarea:focus {
	border-color: var(--orange);
}

textarea {
	resize: vertical;
	height: 90px;
}

.payment-option {
	display: flex;
	align-items: center;
	gap: 14px;
	border: 1px solid #d1d5db;
	padding: 16px;
	border-radius: 14px;
	margin-bottom: 12px;
	cursor: pointer;
}

.payment-option:hover, .payment-option.selected {
	border-color: var(--orange);
	background: var(--orange-soft);
}

.payment-option input {
	accent-color: var(--orange);
	width: 18px;
	height: 18px;
}

.order-item-row {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 14px 0;
	border-bottom: 1px solid var(--line);
	gap: 12px;
}

.order-item-row:last-child {
	border-bottom: none;
}

.item-left {
	display: flex;
	align-items: center;
	gap: 14px;
}

.item-thumb {
	width: 56px;
	height: 56px;
	border-radius: 12px;
	object-fit: cover;
	background: var(--orange-soft);
}

.summary-box {
	background: #fff;
	border: 1px solid var(--line);
	border-radius: 20px;
	padding: 28px;
	box-shadow: 0 4px 16px rgba(31, 36, 48, .04);
	position: sticky;
	top: 90px;
}

.summary-title {
	font-size: 1.25rem;
	font-weight: 800;
	margin-bottom: 18px;
	border-bottom: 1px solid var(--line);
	padding-bottom: 12px;
}

.summary-row {
	display: flex;
	justify-content: space-between;
	margin-bottom: 12px;
	color: var(--muted);
	font-size: .95rem;
}

.summary-row.total {
	color: var(--ink);
	font-weight: 800;
	font-size: 1.25rem;
	border-top: 1px solid var(--line);
	padding-top: 16px;
	margin-top: 16px;
}

.place-order-btn {
	width: 100%;
	background: var(--orange);
	color: #fff;
	border: none;
	padding: 14px;
	border-radius: 14px;
	font-weight: 800;
	font-size: 1rem;
	cursor: pointer;
	margin-top: 22px;
}

.place-order-btn:hover {
	background: var(--orange-dark);
}

.place-order-btn:disabled {
	background: #d9dce3;
	cursor: wait;
}

.secure-text {
	text-align: center;
	font-size: .78rem;
	color: var(--muted);
	margin-top: 10px;
}

.error-message {
	background: #fff0f0;
	color: #c62828;
	padding: 14px 18px;
	border-radius: 12px;
	margin-bottom: 22px;
	border: 1px solid #ffcaca;
}

@media ( max-width : 768px) {
	.checkout-grid {
		grid-template-columns: 1fr;
	}
	.form-row {
		grid-template-columns: 1fr;
	}
	.summary-box {
		position: static;
	}
	.nav a {
		padding: 6px;
		font-size: .85rem;
	}
}
</style>
</head>

<body>

	<header class="topbar">
		<div class="topbar-inner">

			<a class="brand" href="<%=ctx%>/restaurant">swizo</a>

			<nav class="nav">
				<a href="<%=ctx%>/restaurant">Restaurants</a> <a
					href="<%=ctx%>/orders">Orders</a> <a href="<%=ctx%>/cart">Cart</a>
				<a href="<%=ctx%>/logout" style="color: #e53935;">Logout</a>
			</nav>

		</div>
	</header>

	<main class="page">

		<h1 class="page-title">Checkout</h1>

		<%
		if (errorMessage != null) {
		%>
		<div class="error-message">
			<%=esc(errorMessage)%>
		</div>
		<%
		}
		%>
		<!-- filled by the script when an online payment fails or is cancelled -->
		<div class="error-message" id="payErr" style="display: none;"></div>

		<form id="checkoutForm" action="<%=ctx%>/checkout" method="POST">

			<div class="checkout-grid">

				<div class="left-col">

					<!-- DELIVERY ADDRESS -->

					<div class="section-card">

						<div class="section-header">
							<div class="step-number">1</div>

							<div class="section-title-text">
								<h3>Delivery Address</h3>
								<p>Where should we deliver your order?</p>
							</div>
						</div>

						<div class="form-row">

							<div class="form-group">
								<label>Full Name</label> <input type="text" name="fullName"
									value="<%=esc(fullName)%>" maxlength="60" required>
							</div>

							<div class="form-group">
								<label>Phone Number</label> <input type="text" name="phone"
									value="<%=esc(phone)%>" pattern="[0-9]{10}" maxlength="10"
									inputmode="numeric" title="10-digit phone number" required>
							</div>

						</div>

						<div class="form-group">
							<label>Delivery Address</label>
							<textarea name="address" required maxlength="200"
								placeholder="Enter street, apartment, or landmark"><%=esc(address)%></textarea>
						</div>

						<div class="form-row">

							<div class="form-group">
								<label>City</label> <input type="text" name="city"
									value="<%=esc(city)%>" placeholder="City" maxlength="60"
									required>
							</div>

							<div class="form-group">
								<label>PIN Code</label> <input type="text" name="pin"
									value="<%=esc(pin)%>" pattern="[0-9]{6}" maxlength="6"
									inputmode="numeric" placeholder="6-digit PIN"
									title="6-digit PIN code" required>
							</div>

						</div>

					</div>

					<!-- PAYMENT METHOD -->

					<div class="section-card">

						<div class="section-header">
							<div class="step-number">2</div>

							<div class="section-title-text">
								<h3>Payment Method</h3>
								<p>Choose how you would like to pay</p>
							</div>
						</div>

						<label class="payment-option"> <input type="radio"
							name="paymentMode" value="COD"
							<%="COD".equals(paymentMode) ? "checked" : ""%>>
							<div>
								<strong>Cash on Delivery</strong>
								<div style="font-size: .85rem; color: var(--muted);">Pay
									when your order arrives</div>
							</div>
						</label> <label class="payment-option"> <input type="radio"
							name="paymentMode" value="Online"
							<%="Online".equals(paymentMode) ? "checked" : ""%>>
							<div>
								<strong>Pay Online (Razorpay / UPI)</strong>
								<div style="font-size: .85rem; color: var(--muted);">UPI,
									cards, netbanking and wallets</div>
							</div>
						</label>

					</div>

					<!-- ORDER ITEMS -->

					<div class="section-card">

						<div class="section-header">
							<div class="step-number">3</div>

							<div class="section-title-text">
								<h3>Your Order</h3>
								<p>Review the items you are ordering</p>
							</div>
						</div>

						<%
						for (CartItem item : cart.getItems().values()) {
							String imgSrc = imgUrl(ctx, item.getImagePath());
						%>

						<div class="order-item-row">

							<div class="item-left">

								<%
								if (!imgSrc.isEmpty()) {
								%>

								<img src="<%=esc(imgSrc)%>" class="item-thumb" alt="Food item"
									onerror="this.style.display='none';">

								<%
								} else {
								%>

								<div class="item-thumb"
									style="display: flex; align-items: center; justify-content: center; font-size: 1.5rem;">
									&#127858;</div>

								<%
								}
								%>

								<div>
									<div style="font-weight: 700;">
										<%=esc(item.getItemName())%>
									</div>

									<div
										style="color: var(--orange-dark); font-size: .88rem; font-weight: 600;">
										&#8377;<%=String.format("%.2f", item.getPrice())%>
									</div>

									<div style="color: var(--muted); font-size: .82rem;">
										Quantity:
										<%=item.getQuantity()%>
									</div>
								</div>

							</div>

							<div style="font-weight: 800;">
								&#8377;<%=String.format("%.2f", item.getLineTotal())%>
							</div>

						</div>

						<%
						}
						%>

					</div>

				</div>

				<!-- ORDER SUMMARY -->

				<div class="right-col">

					<div class="summary-box">

						<div class="summary-title">Order Summary</div>

						<div class="summary-row">
							<span>Items</span> <span><%=totalItems%></span>
						</div>

						<div class="summary-row">
							<span>Subtotal</span> <span>&#8377;<%=String.format("%.2f", subtotal)%></span>
						</div>

						<div class="summary-row">
							<span>Delivery Fee</span> <span>&#8377;<%=String.format("%.2f", deliveryFee)%></span>
						</div>

						<div class="summary-row total">
							<span>Total</span> <span style="color: var(--orange);">
								&#8377;<%=String.format("%.2f", totalAmount)%>
							</span>
						</div>

						<button type="submit" class="place-order-btn" id="placeBtn">Place
							Order</button>

						<div class="secure-text">&#128274; Secure Checkout</div>

					</div>

				</div>

			</div>

		</form>

	</main>

	<script src="https://checkout.razorpay.com/v1/checkout.js"></script>
	<script>
	// no dollar-brace and no backtick strings in here: a JSP would treat them as server-side EL
	(function () {
		var CTX = '<%=esc(ctx)%>';
		var form = document.getElementById('checkoutForm');
		var btn = document.getElementById('placeBtn');
		var errBox = document.getElementById('payErr');
		var paying = false;                    // true once Razorpay reported a successful payment

		function showError(msg) { errBox.textContent = msg; errBox.style.display = 'block'; window.scrollTo(0, 0); }
		function clearError() { errBox.style.display = 'none'; }
		function busy(text) { btn.disabled = true; btn.textContent = text; }
		function reset() { btn.disabled = false; btn.textContent = 'Place Order'; }
		function isOnline() {
			var r = document.querySelector('input[name="paymentMode"]:checked');
			return !!r && r.value.toLowerCase() === 'online';
		}

		// highlight the chosen payment box
		document.querySelectorAll('input[name="paymentMode"]').forEach(function (radio) {
			radio.addEventListener('change', function () {
				document.querySelectorAll('.payment-option').forEach(function (o) { o.classList.remove('selected'); });
				this.closest('.payment-option').classList.add('selected');
			});
		});
		document.querySelectorAll('input[name="paymentMode"]:checked').forEach(function (radio) {
			radio.closest('.payment-option').classList.add('selected');
		});

		function post(params) {
			return fetch(CTX + '/razorpay', {
				method: 'POST',
				credentials: 'same-origin',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded', 'X-Requested-With': 'fetch' },
				body: (params instanceof URLSearchParams ? params : new URLSearchParams(params)).toString()
			}).then(function (r) { return r.json(); });
		}

		// the browser fires "submit" only after its own field checks (required, 10-digit phone, 6-digit PIN) pass
		form.addEventListener('submit', function (e) {
			clearError();
			if (!isOnline()) {                         // Cash on Delivery: normal form post to /checkout
				busy('Placing order...');
				return;
			}
			e.preventDefault();
			if (typeof Razorpay === 'undefined') {
				showError('Could not load the payment window. Check your internet connection or choose Cash on Delivery.');
				return;
			}
			startOnline();
		});

		function startOnline() {
			busy('Starting payment...');
			var p = new URLSearchParams(new FormData(form));
			p.set('action', 'create');
			post(p).then(function (d) {
				if (!d.ok && d.redirect) { location.href = d.redirect; return; }
				if (!d.ok) { showError(d.message || 'Could not start the payment.'); reset(); return; }
				openCheckout(d);
			}).catch(function () {
				showError('Could not reach the server. Please try again.');
				reset();
			});
		}

		function openCheckout(d) {
			var rzp = new Razorpay({
				key: d.keyId,
				amount: d.amount,
				currency: d.currency,
				order_id: d.orderId,
				name: 'Swizo',
				description: 'Food order',
				prefill: { name: d.name, email: d.email, contact: d.contact },
				theme: { color: '#fc8019' },
				handler: function (resp) { verifyPayment(resp); },
				modal: {
					ondismiss: function () {
						if (!paying) { reset(); showError('Payment cancelled. You have not been charged.'); }
					}
				}
			});
			rzp.on('payment.failed', function (resp) {
				// Razorpay shows the reason inside its window and lets the customer retry there
				if (window.console && resp && resp.error) { console.log('Payment failed:', resp.error.description); }
			});
			busy('Waiting for payment...');
			rzp.open();
		}

		function verifyPayment(r) {
			paying = true;
			busy('Confirming your payment...');
			post({
				action: 'verify',
				razorpay_order_id: r.razorpay_order_id,
				razorpay_payment_id: r.razorpay_payment_id,
				razorpay_signature: r.razorpay_signature
			}).then(function (d) {
				if (d.ok && d.redirect) { location.href = d.redirect; return; }
				paying = false;
				reset();
				showError(d.message || 'We could not confirm your payment.');
			}).catch(function () {
				paying = false;
				reset();
				showError('We could not confirm your payment because the connection dropped. '
					+ 'If money was deducted, check My Orders before paying again.');
			});
		}

		// Back button: the page can come back from cache with a disabled button
		window.addEventListener('pageshow', function (e) { if (e.persisted && !paying) { reset(); } });
	})();
	</script>

</body>
</html>