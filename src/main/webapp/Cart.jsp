
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page
	import="java.util.Map,com.tap.model.Cart,com.tap.model.CartItem"%>

<%!private String esc(Object value) {
		if (value == null)
			return "";

		return String.valueOf(value).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&#39;");
	}

	private String imgUrl(String ctx, String path) {
		if (path == null || path.trim().isEmpty())
			return "";

		path = path.trim();

		if (path.startsWith("http://") || path.startsWith("https://")) {
			return path;
		}

		if (path.startsWith("/")) {
			return ctx + path;
		}

		return ctx + "/" + path;
	}%>

<%
String ctx = request.getContextPath();

Cart cart = (Cart) session.getAttribute("cart");

boolean isEmpty = (cart == null || cart.isEmpty());

String cartMsg = (String) session.getAttribute("cartMsg");

session.removeAttribute("cartMsg");
%>

<!DOCTYPE html>
<html lang="en">

<head>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Your Cart | Swizo</title>

<style>
:root {
	--orange: #fc8019;
	--orange-dark: #e56f0c;
	--orange-soft: #fff3e8;
	--red: #e53935;
	--ink: #24212a;
	--muted: #777582;
	--line: #eeeef1;
}

* {
	box-sizing: border-box;
}

body {
	margin: 0;
	background: #fffaf5;
	color: var(--ink);
	font-family: "Segoe UI", Arial, sans-serif;
}

.page {
	max-width: 1250px;
	width: 92%;
	margin: 35px auto 60px;
}

.narrow {
	max-width: 950px;
}

/* Page heading */
.page-title {
	text-align: center;
	font-size: 2rem;
	font-weight: 800;
	color: var(--orange-dark);
	margin: 0 0 28px;
}

/* Notice */
.notice {
	background: #fff3cd;
	border: 1px solid #ffe08a;
	color: #7a5b00;
	border-radius: 12px;
	padding: 12px 16px;
	margin-bottom: 16px;
	font-size: .92rem;
}

/* Cart item */
.cart-row {
	display: flex;
	align-items: center;
	gap: 16px;
	background: #fff;
	border: 1px solid var(--line);
	border-radius: 18px;
	padding: 16px;
	margin-bottom: 15px;
	box-shadow: 0 3px 12px rgba(31, 36, 48, .05);
	transition: box-shadow .2s, transform .2s;
}

.cart-row:hover {
	box-shadow: 0 8px 22px rgba(31, 36, 48, .09);
	transform: translateY(-2px);
}

.thumb {
	width: 100px;
	height: 100px;
	border-radius: 15px;
	object-fit: cover;
	background: var(--orange-soft);
	flex-shrink: 0;
}

.thumb-fallback {
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 2.5rem;
}

.info {
	flex: 1;
	min-width: 0;
}

.nm {
	font-weight: 800;
	font-size: 1.08rem;
	margin-bottom: 6px;
}

.each {
	color: var(--orange-dark);
	font-weight: 700;
	font-size: .9rem;
	margin-bottom: 6px;
}

.qtytxt {
	color: var(--muted);
	font-size: .88rem;
}

.qtytxt b {
	color: #2563eb;
	font-weight: 700;
}

/* Quantity buttons */
.stepper {
	display: flex;
	align-items: center;
	gap: 12px;
	flex-shrink: 0;
}

.stepper button {
	width: 32px;
	height: 32px;
	border: none;
	border-radius: 50%;
	background: var(--orange);
	color: #fff;
	font-size: 1.1rem;
	font-weight: 800;
	cursor: pointer;
}

.stepper button:hover {
	background: var(--orange-dark);
}

.stepper button:disabled, .trash:disabled, .btn-clear:disabled {
	opacity: .5;
	cursor: not-allowed;
}

.stepper span {
	min-width: 20px;
	text-align: center;
	font-weight: 800;
}

/* Item total */
.line-total {
	min-width: 100px;
	text-align: right;
	font-weight: 800;
	font-size: 1.05rem;
	flex-shrink: 0;
}

/* Remove button */
.trash {
	width: 36px;
	height: 36px;
	border: none;
	border-radius: 50%;
	background: #fdecec;
	color: var(--red);
	cursor: pointer;
	font-size: 1.05rem;
	flex-shrink: 0;
}

.trash:hover {
	background: #fbd5d5;
}

/* Total section */
.totalbox {
	background: #fff;
	border: 1px solid var(--line);
	border-radius: 18px;
	padding: 24px;
	margin-top: 22px;
	box-shadow: 0 3px 12px rgba(31, 36, 48, .05);
}

.total-line {
	text-align: right;
	font-size: 1.5rem;
	font-weight: 800;
	margin-bottom: 20px;
}

.total-actions {
	display: flex;
	justify-content: space-between;
	align-items: center;
	gap: 12px;
	flex-wrap: wrap;
}

.btn-clear {
	background: #fff;
	border: 1px solid #c9ccd3;
	color: #4b5160;
	font-weight: 700;
	padding: 12px 22px;
	border-radius: 12px;
	cursor: pointer;
	font-size: .95rem;
}

.btn-clear:hover {
	background: #f5f5f5;
}

.btn-go {
	display: inline-block;
	background: var(--orange);
	color: #fff;
	font-weight: 800;
	padding: 14px 28px;
	border-radius: 999px;
	text-decoration: none;
	font-size: 1rem;
	transition: background .2s, transform .2s;
}

.btn-go:hover {
	background: var(--orange-dark);
	transform: translateY(-2px);
}

/* Empty cart */
.empty {
	max-width: 620px;
	margin: 25px auto 0;
	background: #fff;
	border-radius: 22px;
	padding: 45px 20px;
	text-align: center;
	box-shadow: 0 3px 15px rgba(31, 36, 48, .06);
}

.empty-icon {
	font-size: 5rem;
	margin-bottom: 12px;
}

.empty h2 {
	font-size: 1.5rem;
	font-weight: 800;
	margin: 12px 0 8px;
}

.empty p {
	color: var(--muted);
	margin-bottom: 25px;
	line-height: 1.6;
}

/* Responsive */
@media ( max-width : 700px) {
	.page {
		width: 94%;
		margin-top: 22px;
	}
	.cart-row {
		flex-wrap: wrap;
		gap: 12px;
	}
	.thumb {
		width: 82px;
		height: 82px;
	}
	.info {
		flex: 1;
	}
	.stepper {
		margin-left: 94px;
	}
	.line-total {
		margin-left: auto;
		min-width: auto;
	}
	.total-actions {
		flex-direction: column;
		align-items: stretch;
	}
	.btn-clear, .btn-go {
		text-align: center;
		width: 100%;
	}
	.page-title {
		font-size: 1.7rem;
	}
}
</style>

</head>

<body>

	<!-- Shared Navbar -->
	<jsp:include page="/includes/navbar.jsp" />

	<main class="page narrow">

		<%
		if (cartMsg != null && !cartMsg.trim().isEmpty()) {
		%>

		<div class="notice">
			<%=esc(cartMsg)%>
		</div>

		<%
		}
		%>

		<%
		if (isEmpty) {
		%>

		<!-- Empty Cart -->

		<div class="empty">

			<div class="empty-icon">&#128722;</div>

			<h2>Your cart is empty</h2>

			<p>Good food is always cooking! Add something tasty to get
				started.</p>

			<a class="btn-go" href="<%=ctx%>/restaurant"> Explore
				Restaurants </a>

		</div>

		<%
		} else {
		%>

		<h1 class="page-title">Your Cart</h1>

		<%
		for (CartItem item : cart.getItems().values()) {

			String imgSrc = imgUrl(ctx, item.getImagePath());
		%>

		<div class="cart-row">

			<!-- Food Image -->

			<%
			if (!imgSrc.isEmpty()) {
			%>

			<img class="thumb" src="<%=esc(imgSrc)%>"
				alt="<%=esc(item.getItemName())%>"
				onerror="this.outerHTML='<div class=\'thumbthumb-fallback\'>&#127858;
		</div>
		'">

		<%
		} else {
		%>

		<div class="thumb thumb-fallback">&#127858;</div>

		<%
		}
		%>

		<!-- Food Details -->

		<div class="info">

			<div class="nm">
				<%=esc(item.getItemName())%>
			</div>

			<div class="each">
				&#8377;<%=String.format("%.2f", item.getPrice())%>
				each
			</div>

			<div class="qtytxt">
				Quantity: <b><%=item.getQuantity()%></b>
			</div>

		</div>

		<!-- Quantity Controls -->

		<div class="stepper">

			<button type="button" data-act="dec"
				data-menu="<%=item.getMenuId()%>" aria-label="Decrease quantity">
				&minus;</button>

			<span><%=item.getQuantity()%></span>

			<button type="button" data-act="inc"
				data-menu="<%=item.getMenuId()%>" aria-label="Increase quantity">
				+</button>

		</div>

		<!-- Line Total -->

		<div class="line-total">
			&#8377;<%=String.format("%.2f", item.getLineTotal())%>
		</div>

		<!-- Remove Item -->

		<button type="button" class="trash" data-act="remove"
			data-menu="<%=item.getMenuId()%>" title="Remove item"
			aria-label="Remove item">&#128465;</button>

		</div>

		<%
		}
		%>

		<!-- Cart Total -->

		<div class="totalbox">

			<div class="total-line">
				Total: &#8377;<%=String.format("%.2f", cart.getTotal())%>
			</div>

			<div class="total-actions">
				<button type="button" class="btn-clear" data-act="clear">Clear
					cart</button>
				<a class="btn-go" href="<%=ctx%>/checkout">Proceed to checkout</a>
			</div>

		</div>

		<%
		}
		%>

	</main>

	<!-- Shared Footer -->
	<jsp:include page="/includes/footer.jsp" />

	<script>
	var CTX = '<%=esc(ctx)%>';

	document.addEventListener('click', function (e) {
		var b = e.target.closest('[data-act]');
		if (!b || b.disabled) return;
		var act = b.dataset.act;
		if (act === 'clear' && !confirm('Remove all items from your cart?')) return;

		b.disabled = true;
		fetch(CTX + '/cart', {
			method: 'POST',
			credentials: 'same-origin',
			headers: { 'Content-Type': 'application/x-www-form-urlencoded', 'X-Requested-With': 'fetch' },
			body: new URLSearchParams({ action: act, menuId: b.dataset.menu || '' }).toString()
		})
		.then(function (r) { return r.json(); })
		.then(function (d) {
			if (d.ok) { location.reload(); }
			else { alert(d.message || 'Something went wrong.'); b.disabled = false; }
		})
		.catch(function () { alert('Could not reach the server. Please try again.'); b.disabled = false; });
	});

	window.addEventListener('pageshow', function (e) { if (e.persisted) location.reload(); });
	</script>

</body>
</html>