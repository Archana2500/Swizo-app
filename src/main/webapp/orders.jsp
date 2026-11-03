
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="java.util.List, com.tap.model.OrderView, com.tap.model.OrderItemView"%>

<%!private static String esc(String s) {
		if (s == null)
			return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
	}%>

<%
String ctx = request.getContextPath();

@SuppressWarnings("unchecked")
List<OrderView> orders = (List<OrderView>) request.getAttribute("orders");

java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a");

final int VISIBLE = 3;
%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>My Orders - Swizo</title>

<link rel="stylesheet" href="<%=ctx%>/css/common.css">

<link rel="stylesheet"
	href="<%=ctx%>/css/orders.css?v=<%=System.currentTimeMillis()%>">

<style>

/* ================= ORDER ACTION BUTTONS ================= */
.or-actions {
	display: flex;
	align-items: center;
	justify-content: flex-end;
	gap: 12px;
	flex-wrap: wrap;
	margin-top: 18px;
}

/* Order Again */
.or-reorder-btn {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	gap: 7px;
	padding: 11px 18px;
	border-radius: 10px;
	text-decoration: none;
	font-size: 13px;
	font-weight: 700;
	transition: all 0.25s ease;
	cursor: pointer;
	background: #fff3e8;
	color: #e96d0b;
	border: 1px solid #ffe0c2;
}

.or-reorder-btn:hover {
	background: #ffe5cf;
	color: #d95e00;
	transform: translateY(-2px);
}

/* Mobile */
@media ( max-width : 480px) {
	.or-actions {
		justify-content: stretch;
	}
	.or-reorder-btn {
		flex: 1;
		padding: 11px 10px;
		font-size: 12px;
	}
}
</style>

</head>

<body class="orders-body">

	<jsp:include page="/includes/navbar.jsp" />

	<div class="or-layout">

		<!-- ================= LEFT PANEL ================= -->

		<aside class="or-side">

			<div class="or-hungry">

				<div class="or-hungry-text">

					<h2>Hungry Again?</h2>

					<p>Your favourite meals are just a few clicks away.</p>

					<a class="or-reorder" href="<%=ctx%>/restaurant"> &#8635; Order
						now </a>

				</div>

				<img
					src="https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&amp;fit=crop&amp;w=500&amp;q=80"
					alt="Juicy burger">

			</div>

			<!-- OFFER SECTION -->

			<div class="or-offer">

				<h3>Something Delicious Awaits!</h3>

				<div class="or-code-row">

					<span class="or-code" id="offerCode"> SWIZO50 </span>

					<button type="button" class="or-copy" id="copyCode"
						aria-label="Copy offer code">Copy</button>

				</div>

				<p>Discover your next favourite meal.</p>

				<a class="or-offer-btn" href="<%=ctx%>/restaurant"> Explore
					Offers </a>

			</div>

			<!-- POPULAR RESTAURANTS -->

			<div class="or-near">

				<div class="or-near-head">

					<h3>Popular Near You</h3>

					<a href="<%=ctx%>/restaurant">View all</a>

				</div>

				<a class="or-near-item" href="<%=ctx%>/restaurant"> <img
					src="https://images.unsplash.com/photo-1589302168068-964664d93dc0?auto=format&amp;fit=crop&amp;w=300&amp;q=80"
					alt="Biryani">

					<div>
						<strong>Spice Route Biryani</strong> <span>Biryani, North
							Indian</span> <em>&#9733; 4.4 &bull; 30-35 mins</em>
					</div>

				</a> <a class="or-near-item" href="<%=ctx%>/restaurant"> <img
					src="https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&amp;fit=crop&amp;w=300&amp;q=80"
					alt="Pizza">

					<div>
						<strong>Slice House Pizza</strong> <span>Pizza, Italian</span> <em>&#9733;
							4.2 &bull; 25-30 mins</em>
					</div>

				</a> <a class="or-near-item" href="<%=ctx%>/restaurant"> <img
					src="https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&amp;fit=crop&amp;w=300&amp;q=80"
					alt="Burgers">

					<div>
						<strong>Burger Barn</strong> <span>Burgers, American</span> <em>&#9733;
							4.1 &bull; 20-25 mins</em>
					</div>

				</a> <a class="or-near-item" href="<%=ctx%>/restaurant"> <img
					src="https://images.unsplash.com/photo-1563805042-7684c019e1cb?auto=format&amp;fit=crop&amp;w=300&amp;q=80"
					alt="Desserts">

					<div>
						<strong>Sweet Tooth</strong> <span>Desserts, Bakery</span> <em>&#9733;
							4.3 &bull; 20-25 mins</em>
					</div>

				</a>

			</div>

			<a class="or-explore" href="<%=ctx%>/restaurant"> Explore
				Restaurants </a>

		</aside>

		<!-- ================= RIGHT: MY ORDERS ================= -->

		<main class="or-wrap">

			<h1 class="or-title">My Orders</h1>

			<p class="or-subtitle">Your orders, all in one place.</p>

			<%
			if (orders == null || orders.isEmpty()) {
			%>

			<div class="or-empty">

				<p>You haven't placed any orders yet.</p>

				<a href="<%=ctx%>/restaurant"> Explore Restaurants </a>

			</div>

			<%
			} else {
			%>

			<!-- ORDER FILTERS -->

			<div class="or-pills">

				<button type="button" class="or-pill active" data-filter="all">
					All Orders</button>

				<button type="button" class="or-pill" data-filter="active">
					Active</button>

				<button type="button" class="or-pill" data-filter="delivered">
					Delivered</button>

				<button type="button" class="or-pill" data-filter="cancelled">
					Cancelled</button>

			</div>

			<%
			int idx = 0;

			for (OrderView o : orders) {

				String st = o.getStatus() == null ? "" : o.getStatus().toLowerCase().trim();

				String stClass = st.replace(" ", "-").replace("_", "-");

				String extra = idx >= VISIBLE ? " or-hidden" : "";

				idx++;

				String imgPath = o.getRestaurantImage() == null ? "" : o.getRestaurantImage().trim();

				String imgSrc;

				if (imgPath.startsWith("http://") || imgPath.startsWith("https://")) {

					imgSrc = imgPath;

				} else if (imgPath.isEmpty()) {

					imgSrc = "";

				} else {

					if (imgPath.startsWith("/")) {
				imgPath = imgPath.substring(1);
					}

					imgSrc = ctx + "/" + imgPath;
				}

				String statusText = o.getStatus() == null ? "" : o.getStatus().replace("_", " ");
			%>

			<!-- INDIVIDUAL ORDER CARD -->

			<div class="or-card or-card-<%=esc(stClass)%><%=extra%>"
				data-status="<%=esc(st)%>">

				<!-- ORDER HEADER -->

				<div class="or-head">

					<div class="or-rest">

						<%
						if (!imgSrc.isEmpty()) {
						%>

						<img class="or-rest-img" src="<%=esc(imgSrc)%>"
							alt="<%=esc(o.getRestaurantName())%>">

						<%
						} else {
						%>

						<div class="or-rest-img or-rest-ph">&#127869;</div>

						<%
						}
						%>

						<div>

							<div class="or-restaurant">
								<%=esc(o.getRestaurantName())%>
							</div>

							<div class="or-id">
								Order #<%=o.getOrderId()%>
							</div>

						</div>

					</div>

					<span class="or-status or-status-<%=esc(stClass)%>"> <%=esc(statusText)%>
					</span>

				</div>

				<!-- ORDER ITEMS -->

				<ul class="or-items">

					<%
					if (o.getItems() != null) {

						for (OrderItemView item : o.getItems()) {
					%>

					<li><span class="or-item-name"> <%=esc(item.getItemName())%>
					</span> <span class="or-item-qty"> &times; <%=item.getQuantity()%>
					</span> <span class="or-item-price"> &#8377;<%=String.format("%.2f", item.getItemTotal())%>
					</span></li>

					<%
					}
					}
					%>

				</ul>

				<!-- ORDER FOOTER -->

				<div class="or-foot">

					<div class="or-meta">

						<%=o.getOrderDate() == null ? "" : fmt.format(o.getOrderDate())%>

						&bull;

						<%=esc(o.getPaymentMode())%>

					</div>

					<div class="or-amount">

						Total: <strong> &#8377;<%=String.format("%.2f", o.getTotalAmount())%>
						</strong>

					</div>

				</div>

				<!-- ORDER AGAIN BUTTON ONLY -->

				<div class="or-actions">

					<a class="or-reorder-btn"
						href="<%=ctx%>/menu?restaurantId=<%=o.getRestaurantId()%>">

						&#8635; Order Again </a>

				</div>

			</div>

			<%
			}
			%>

			<!-- NO ORDERS IN SELECTED FILTER -->

			<div class="or-none" id="orNone">No orders in this filter.</div>

			<!-- SHOW OLDER ORDERS -->

			<%
			if (orders.size() > VISIBLE) {
			%>

			<button type="button" id="toggleOlder" class="or-more">

				Show older orders (<%=orders.size() - VISIBLE%>)

			</button>

			<%
			}
			%>

			<%
			}
			%>

		</main>

	</div>

	<jsp:include page="/includes/footer.jsp" />

	<!-- ================= JAVASCRIPT ================= -->

	<script>
		// ======================================
		// SHOW / HIDE OLDER ORDERS
		// ======================================

		var btn = document.getElementById("toggleOlder");

		var total =
	<%=orders == null ? 0 : Math.max(0, orders.size() - VISIBLE)%>
		;

		var expanded = false;

		var extras = document.querySelectorAll(".or-card.or-hidden");

		if (btn) {

			btn.addEventListener("click", function() {

				expanded = !expanded;

				extras.forEach(function(c) {

					c.classList.toggle("or-hidden", !expanded);

				});

				btn.textContent = expanded ? "Hide older orders"
						: "Show older orders (" + total + ")";

			});

		}

		// ======================================
		// ORDER FILTERS
		// ======================================

		var pills = document.querySelectorAll(".or-pill");

		var cards = document.querySelectorAll(".or-card");

		var none = document.getElementById("orNone");

		function matches(card, f) {

			var s = card.getAttribute("data-status");

			if (f === "all") {
				return true;
			}

			if (f === "active") {
				return s !== "delivered" && s !== "cancelled";
			}

			return s === f;

		}

		pills.forEach(function(p) {

			p.addEventListener("click", function() {

				var f = p.getAttribute("data-filter");

				pills.forEach(function(x) {

					x.classList.toggle("active", x === p);

				});

				document.body.classList.toggle("or-filtering", f !== "all");

				var shown = 0;

				cards.forEach(function(c) {

					var ok = matches(c, f);

					c.classList.toggle("or-out", !ok);

					if (ok) {
						shown++;
					}

				});

				if (btn) {
					btn.style.display = f === "all" ? "" : "none";
				}

				if (none) {
					none.style.display = shown === 0 ? "block" : "none";
				}

			});

		});

		// ======================================
		// COPY OFFER CODE
		// ======================================

		var copyBtn = document.getElementById("copyCode");

		if (copyBtn) {

			copyBtn.addEventListener("click", function() {

				var code = document.getElementById("offerCode").textContent
						.trim();

				function done() {

					copyBtn.textContent = "Copied";

					setTimeout(function() {
						copyBtn.textContent = "Copy";
					}, 1500);

				}

				if (navigator.clipboard && navigator.clipboard.writeText) {

					navigator.clipboard.writeText(code).then(done);

				} else {

					var t = document.createElement("textarea");

					t.value = code;

					document.body.appendChild(t);

					t.select();

					document.execCommand("copy");

					document.body.removeChild(t);

					done();

				}

			});

		}
	</script>

</body>
</html>

