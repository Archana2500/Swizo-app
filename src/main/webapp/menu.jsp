<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="java.util.List,java.util.Map,java.util.HashMap,com.tap.model.Restaurant,com.tap.model.Menu,com.tap.model.Cart"%>

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

Restaurant restaurant = (Restaurant) request.getAttribute("restaurant");

List<Menu> menuList = (List<Menu>) request.getAttribute("allMenues");

Cart cart = (Cart) session.getAttribute("cart");

Map<Integer, Integer> qtyMap = (cart == null) ? new HashMap<Integer, Integer>() : cart.getQuantities();

String restName = (restaurant != null && restaurant.getName() != null) ? restaurant.getName() : "Restaurant Menu";

String restImg = (restaurant != null) ? restaurant.getImagePath() : "";

String bannerSrc = imgUrl(ctx, restImg);
%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title><%=esc(restName)%> Menu | Swizo</title>

<style>
:root {
	--orange: #fc8019;
	--orange-dark: #e56f0c;
	--orange-soft: #fff3e8;
	--green: #168a45;
	--green-soft: #e8f7ee;
	--ink: #1f2430;
	--muted: #7a8090;
	--line: #eceef2;
	--background: #fffbf7;
}

* {
	box-sizing: border-box;
	margin: 0;
	padding: 0;
}

body {
	background: var(--background);
	color: var(--ink);
	font-family: "Segoe UI", Arial, sans-serif;
}

.page {
	max-width: 1200px;
	width: 86%;
	margin: 35px auto 60px;
}

/* Restaurant hero */
.hero {
	width: 100%;
	height: 320px;
	border-radius: 20px;
	overflow: hidden;
	position: relative;
	margin-bottom: 40px;
	box-shadow: 0 10px 30px rgba(0, 0, 0, .12);
	background: #2a303c;
}

.hero img {
	width: 100%;
	height: 100%;
	object-fit: cover;
	display: block;
	filter: brightness(.8);
}

.hero .ph {
	width: 100%;
	height: 100%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 4rem;
}

.hero-overlay {
	position: absolute;
	bottom: 0;
	left: 0;
	right: 0;
	padding: 30px;
	color: #fff;
	background: linear-gradient(to top, rgba(0, 0, 0, .85), transparent);
}

.hero-overlay h1 {
	font-size: 2.4rem;
	font-weight: 800;
	letter-spacing: -.5px;
	margin: 0;
}

/* Menu heading */
.section-title {
	font-size: 26px;
	font-weight: 800;
	margin-bottom: 6px;
}

.section-subtitle {
	color: var(--muted);
	font-size: 14px;
	margin-bottom: 28px;
}

/* Menu grid with strict row alignment */
.grid {
	display: grid;
	grid-template-columns: repeat(3, minmax(0, 1fr));
	gap: 28px;
}

/* FIXED CARD SIZE & ALIGNMENT */
.card {
	background: #fff;
	border: 1px solid var(--line);
	border-radius: 18px;
	overflow: hidden;
	display: flex;
	flex-direction: column;
	height: 100%;
	/* Ensures all cards in a row stretch to exact same height */
	box-shadow: 0 6px 20px rgba(0, 0, 0, .04);
	transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1), box-shadow 0.3s
		cubic-bezier(0.4, 0, 0.2, 1), border-color 0.3s ease;
}

.card:hover {
	transform: translateY(-8px);
	border-color: rgba(252, 128, 25, 0.4);
	box-shadow: 0 16px 36px rgba(252, 128, 25, 0.22), 0 0 15px
		rgba(252, 128, 25, 0.1);
}

.thumb {
	position: relative;
	width: 100%;
	height: 190px;
	background: var(--orange-soft);
	overflow: hidden;
	flex-shrink: 0;
}

.thumb img {
	width: 100%;
	height: 190px;
	object-fit: cover;
	display: block;
	transition: transform .4s ease;
}

.card:hover .thumb img {
	transform: scale(1.06);
}

.thumb .fallback {
	position: absolute;
	inset: 0;
	display: none;
	align-items: center;
	justify-content: center;
	font-size: 2.8rem;
	background: var(--orange-soft);
}

.avail {
	position: absolute;
	top: 12px;
	right: 12px;
	background: var(--green-soft);
	color: var(--green);
	font-size: .75rem;
	font-weight: 700;
	padding: 4px 10px;
	border-radius: 999px;
	z-index: 2;
}

.avail.off {
	background: #f1f2f5;
	color: var(--muted);
}

.body {
	padding: 20px;
	display: flex;
	flex-direction: column;
	flex-grow: 1;
	/* Fills remaining height so footer-row is pinned at the bottom */
}

.name {
	font-size: 18px;
	font-weight: 800;
	margin: 0 0 8px 0;
}

.desc {
	color: var(--muted);
	font-size: 13px;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
	margin: 0 0 16px 0;
	line-height: 1.4;
}

.footer-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	border-top: 1px solid var(--line);
	padding-top: 14px;
	margin-top: auto; /* Pins footer cleanly to the bottom */
	gap: 10px;
}

.price {
	font-size: 1.1rem;
	font-weight: 800;
}

.add-btn {
	background: var(--orange);
	color: #fff;
	border: none;
	border-radius: 12px;
	padding: 10px 18px;
	font-size: 14px;
	font-weight: 800;
	cursor: pointer;
	transition: background .2s, box-shadow .2s;
	box-shadow: 0 4px 12px rgba(252, 128, 25, 0.25);
}

.add-btn:hover {
	background: var(--orange-dark);
	box-shadow: 0 6px 16px rgba(252, 128, 25, 0.4);
}

.add-btn:disabled {
	background: #d9dce3;
	box-shadow: none;
	cursor: not-allowed;
}

.qty-control {
	display: flex;
	align-items: center;
	background: var(--orange-soft);
	border: 1.5px solid var(--orange);
	border-radius: 999px;
	padding: 2px;
	gap: 2px;
}

.qty-btn {
	background: #fff;
	color: var(--orange);
	border: 1.5px solid var(--orange);
	border-radius: 50%;
	width: 28px;
	height: 28px;
	font-size: 1.1rem;
	font-weight: 800;
	line-height: 1;
	cursor: pointer;
}

.qty-btn:hover {
	background: var(--orange);
	color: #fff;
}

.qty-display {
	min-width: 30px;
	text-align: center;
	font-weight: 800;
	color: var(--ink);
}

.empty {
	background: #fff;
	border: 1px dashed #ffd3ad;
	border-radius: 20px;
	padding: 48px 20px;
	text-align: center;
	color: var(--muted);
	grid-column: 1/-1;
}

/* Responsive */
@media ( max-width : 900px) {
	.grid {
		grid-template-columns: repeat(2, minmax(0, 1fr));
	}
}

@media ( max-width : 600px) {
	.page {
		width: 92%;
		margin-top: 20px;
	}
	.hero {
		height: 230px;
		border-radius: 16px;
	}
	.hero-overlay h1 {
		font-size: 1.7rem;
	}
	.hero-overlay {
		padding: 20px;
	}
	.grid {
		grid-template-columns: 1fr;
		gap: 18px;
	}
}
</style>
</head>

<body>

	<!-- Shared Navbar -->
	<jsp:include page="/includes/navbar.jsp" />

	<main class="page">

		<!-- Restaurant Banner -->
		<div class="hero">

			<%
			if (!bannerSrc.isEmpty()) {
			%>

			<img src="<%=esc(bannerSrc)%>" alt="<%=esc(restName)%>"
				onerror="this.style.display='none';">

			<%
			} else {
			%>

			<div class="ph">&#127869;</div>

			<%
			}
			%>

			<div class="hero-overlay">
				<h1><%=esc(restName)%></h1>
			</div>

		</div>

		<h2 class="section-title">Our Menu</h2>

		<p class="section-subtitle">Discover your favourites and add them
			to your cart.</p>

		<section class="grid">

			<%
			if (menuList == null || menuList.isEmpty()) {
			%>

			<div class="empty">
				<p>No menu items available for this restaurant yet.</p>

				<p style="margin-top: 8px;">
					<a href="<%=ctx%>/restaurant"
						style="color: var(--orange-dark); font-weight: 700; text-decoration: none;">
						&larr; Back to Restaurants </a>
				</p>
			</div>

			<%
			} else {

			for (Menu item : menuList) {

				String imgSrc = imgUrl(ctx, item.getImagePath());

				int q = (qtyMap != null && qtyMap.containsKey(item.getMenuId())) ? qtyMap.get(item.getMenuId()) : 0;
			%>

			<article class="card">

				<div class="thumb">

					<%
					if (!imgSrc.isEmpty()) {
					%>

					<img src="<%=esc(imgSrc)%>" alt="<%=esc(item.getItemName())%>"
						loading="lazy"
						onerror="this.style.display='none';this.nextElementSibling.style.display='flex';">

					<div class="fallback">&#127858;</div>

					<%
					} else {
					%>

					<div class="fallback" style="display: flex;">&#127858;</div>

					<%
					}
					%>

					<span class="avail <%=item.isAvailable() ? "" : "off"%>"> <%=item.isAvailable() ? "Available" : "Unavailable"%>
					</span>

				</div>

				<div class="body">

					<h3 class="name"><%=esc(item.getItemName())%></h3>

					<p class="desc"><%=esc(item.getDescription())%></p>

					<div class="footer-row">

						<span class="price">&#8377;<%=String.format("%.2f", item.getPrice())%></span>

						<div class="action" data-menu="<%=item.getMenuId()%>"
							data-qty="<%=q%>" data-available="<%=item.isAvailable()%>">
						</div>

					</div>

				</div>

			</article>

			<%
			}
			}
			%>

		</section>

	</main>

	<!-- Shared Footer -->
	<jsp:include page="/includes/footer.jsp" />

	<script>
var CTX = '<%=esc(ctx)%>';

function render(box) {

    var qty = parseInt(box.dataset.qty, 10) || 0;

    if (box.dataset.available !== 'true') {

        box.innerHTML =
            '<button type="button" class="add-btn" disabled>Unavailable</button>';

    } else if (qty > 0) {

        box.innerHTML =
            '<div class="qty-control">'
            + '<button type="button" class="qty-btn" data-act="dec" aria-label="Decrease">&minus;</button>'
            + '<span class="qty-display">' + qty + '</span>'
            + '<button type="button" class="qty-btn" data-act="inc" aria-label="Increase">+</button>'
            + '</div>';

    } else {

        box.innerHTML =
            '<button type="button" class="add-btn" data-act="add">+ Add</button>';
    }
}

function setBadge(n) {

    var b = document.getElementById('cartBadge');

    if (!b) return;

    b.textContent = n;
    b.style.display = n > 0 ? 'inline-block' : 'none';
}

function post(params) {

    return fetch(CTX + '/cart', {

        method: 'POST',

        credentials: 'same-origin',

        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'fetch'
        },

        body: new URLSearchParams(params).toString()

    }).then(function (r) {

        return r.json();

    });
}

function applyResult(box, d) {

    box.dataset.qty = d.qty;

    render(box);

    setBadge(d.cartCount);
}

document.addEventListener('click', function (e) {

    var btn = e.target.closest('[data-act]');

    if (!btn) return;

    var box = btn.closest('.action');

    if (!box || box.dataset.busy === '1') return;

    box.dataset.busy = '1';

    post({
        action: btn.dataset.act,
        menuId: box.dataset.menu
    })

    .then(function (d) {

        if (d.ok) {

            applyResult(box, d);

        } else if (d.code === 'OTHER_RESTAURANT') {

            if (confirm(d.message + '\n\nClear your cart and add this item instead?')) {

                return post({
                    action: 'replace',
                    menuId: box.dataset.menu
                })

                .then(function (d2) {

                    if (d2.ok) {

                        document.querySelectorAll('.action').forEach(function (b) {

                            b.dataset.qty = 0;
                            render(b);

                        });

                        applyResult(box, d2);

                    } else {

                        alert(d2.message || 'Could not add this item.');

                    }

                });

            }

        } else {

            alert(d.message || 'Something went wrong.');

        }

    })

    .catch(function () {

        alert('Could not reach the server. Please try again.');

    })

    .then(function () {

        box.dataset.busy = '0';

    });

});

document.querySelectorAll('.action').forEach(render);

window.addEventListener('pageshow', function (e) {

    if (e.persisted) {
        location.reload();
    }

});
</script>

</body>
</html>