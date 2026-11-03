<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page import="java.util.List"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.nio.charset.StandardCharsets"%>
<%@ page import="com.tap.model.Restaurant"%>

<%!private String esc(Object value) {
		if (value == null)
			return "";
		return String.valueOf(value).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&#39;");
	}%>

<%
String ctx = request.getContextPath();

List<Restaurant> restaurants = (List<Restaurant>) request.getAttribute("restaurants");
String selectedCuisine = (String) request.getAttribute("selectedCuisine");

if (selectedCuisine == null || selectedCuisine.trim().isEmpty()) {
	selectedCuisine = "All";
}

String[] cuisines = { "All", "Pizza", "Burger", "South Indian", "Chicken", "Biryani", "Desserts" };
String[] emojis = { "🍽️", "🍕", "🍔", "🥘", "🍗", "🍛", "🍰" };
%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><%=selectedCuisine.equalsIgnoreCase("All") ? "Explore Restaurants" : esc(selectedCuisine) + " Restaurants"%>
	| Swizo</title>
<style>
:root {
	--orange: #fc8019;
	--orange-dark: #e56f0c;
	--orange-soft: #fff3e8;
	--dark: #1f2430;
	--muted: #7a8090;
	--background: #fffbf7;
	--card-border: #eceef2;
}

* {
	margin: 0;
	padding: 0;
	box-sizing: border-box;
}

body {
	font-family: "Segoe UI", Arial, sans-serif;
	background: var(--background);
	color: var(--dark);
}

a {
	text-decoration: none;
	color: inherit;
}

/* PROMOTIONAL BANNER */
.offer-banner {
	width: 86%;
	max-width: 1200px;
	margin: 28px auto 38px;
	border-radius: 16px;
	overflow: hidden;
	box-shadow: 0 8px 25px rgba(0, 0, 0, .06);
}

.offer-banner a {
	display: block;
}

.offer-banner img {
	display: block;
	width: 100%;
	height: auto;
	transition: transform .35s ease;
}

.offer-banner a:hover img {
	transform: scale(1.01);
}

/* MAIN CONTENT CONTAINER */
.container {
	width: 86%;
	max-width: 1200px;
	margin: 35px auto 75px;
}

.heading {
	margin-bottom: 24px;
}

.heading h2 {
	font-size: 26px;
	font-weight: 800;
	margin-bottom: 6px;
}

.heading p {
	font-size: 14px;
	color: var(--muted);
}

/* CUISINE FILTERS */
.cuisine-list {
	display: flex;
	flex-wrap: wrap;
	gap: 12px;
	margin-bottom: 40px;
}

.cuisine-item {
	display: inline-flex;
	align-items: center;
	gap: 8px;
	background: white;
	border: 1px solid var(--card-border);
	padding: 12px 20px;
	border-radius: 30px;
	font-size: 13px;
	font-weight: 700;
	transition: all 0.2s ease;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

.cuisine-item:hover {
	border-color: var(--orange);
	color: var(--orange);
	transform: translateY(-2px);
}

.cuisine-item.active {
	background: var(--orange);
	border-color: var(--orange);
	color: white;
	box-shadow: 0 4px 12px rgba(252, 128, 25, 0.25);
}

/* RESTAURANT GRID */
.restaurant-grid {
	display: grid;
	grid-template-columns: repeat(3, minmax(0, 1fr));
	gap: 28px;
}

/* RESTAURANT CARD WITH GLOW HOVER EFFECT */
.restaurant-card {
	background: white;
	border-radius: 18px;
	overflow: hidden;
	border: 1px solid var(--card-border);
	box-shadow: 0 6px 20px rgba(0, 0, 0, .04);
	transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1), box-shadow 0.3s
		cubic-bezier(0.4, 0, 0.2, 1), border-color 0.3s ease;
}

.restaurant-card:hover {
	transform: translateY(-8px);
	border-color: rgba(252, 128, 25, 0.4);
	box-shadow: 0 16px 36px rgba(252, 128, 25, 0.22), 0 0 15px
		rgba(252, 128, 25, 0.1);
}

.restaurant-image {
	height: 210px;
	width: 100%;
	overflow: hidden;
	background: var(--orange-soft);
	position: relative;
}

.restaurant-image img {
	width: 100%;
	height: 100%;
	object-fit: cover;
	display: block;
	transition: transform .4s ease;
}

.restaurant-card:hover .restaurant-image img {
	transform: scale(1.06);
}

.fallback {
	width: 100%;
	height: 100%;
	display: flex;
	justify-content: center;
	align-items: center;
	font-size: 50px;
	background: var(--orange-soft);
}

/* CARD DETAILS */
.restaurant-info {
	padding: 20px;
}

.restaurant-name {
	font-size: 18px;
	font-weight: 800;
	margin-bottom: 6px;
}

.restaurant-cuisine {
	color: var(--muted);
	font-size: 13px;
	margin-bottom: 12px;
}

.restaurant-meta {
	display: flex;
	justify-content: space-between;
	align-items: center;
	gap: 8px;
	margin-bottom: 12px;
	font-size: 12px;
}

.rating {
	background: #e7f6e9;
	color: #16833b;
	padding: 6px 10px;
	border-radius: 8px;
	font-weight: 800;
}

.delivery {
	color: var(--muted);
	font-weight: 600;
}

.restaurant-address {
	color: var(--muted);
	font-size: 12px;
	line-height: 1.5;
	margin-bottom: 18px;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

/* VIBRANT VIEW MENU BUTTON */
.view-menu {
	display: block;
	text-align: center;
	background: var(--orange);
	color: white;
	padding: 12px;
	border-radius: 12px;
	font-size: 14px;
	font-weight: 800;
	transition: background 0.2s, transform 0.2s, box-shadow 0.2s;
	box-shadow: 0 4px 12px rgba(252, 128, 25, 0.25);
}

.view-menu:hover {
	background: var(--orange-dark);
	transform: translateY(-1px);
	box-shadow: 0 6px 16px rgba(252, 128, 25, 0.4);
}

/* EMPTY STATE */
.empty-state {
	grid-column: 1/-1;
	text-align: center;
	background: white;
	padding: 65px 20px;
	border-radius: 18px;
	border: 1px solid var(--card-border);
}

.empty-state .emoji {
	font-size: 50px;
	margin-bottom: 15px;
}

.empty-state p {
	color: var(--muted);
	margin-top: 8px;
}

/* RESPONSIVE */
@media ( max-width : 900px) {
	.restaurant-grid {
		grid-template-columns: repeat(2, minmax(0, 1fr));
	}
}

@media ( max-width : 600px) {
	.offer-banner, .container {
		width: 92%;
	}
	.restaurant-grid {
		grid-template-columns: 1fr;
	}
}
</style>
</head>

<body>

	<!-- COMMON NAVBAR -->
	<%@ include file="/includes/navbar.jsp"%>

	<!-- PROMOTIONAL BANNER -->
	<section class="offer-banner">
		<a href="<%=ctx%>/restaurant" title="Explore all restaurants"> <img
			src="<%=ctx%>/images/swizo-banner-app.png"
			alt="Get up to 50% off on your dining bills with Swizo">
		</a>
	</section>

	<!-- MAIN CONTENT CONTAINER -->
	<main class="container">

		<div class="heading">
			<h2>Explore Food Categories</h2>
			<p>Choose your favourite cuisine and discover delicious
				restaurants.</p>
		</div>

		<!-- CUISINE FILTERS -->
		<div class="cuisine-list">
			<%
			for (int i = 0; i < cuisines.length; i++) {
				String cuisine = cuisines[i];
				String url = ctx + "/restaurant?cuisine=" + URLEncoder.encode(cuisine, StandardCharsets.UTF_8);
				boolean active = selectedCuisine.equalsIgnoreCase(cuisine);
			%>
			<a href="<%=url%>" class="cuisine-item <%=active ? "active" : ""%>">
				<%=emojis[i]%> <%=esc(cuisine)%>
			</a>
			<%
			}
			%>
		</div>

		<!-- RESTAURANT HEADING -->
		<div class="heading">
			<h2><%=selectedCuisine.equalsIgnoreCase("All") ? "All Restaurants" : esc(selectedCuisine) + " Restaurants"%></h2>
			<p>Discover your next favourite restaurant with Swizo.</p>
		</div>

		<!-- RESTAURANT CARDS GRID -->
		<div class="restaurant-grid">
			<%
			if (restaurants != null && !restaurants.isEmpty()) {
				for (Restaurant restaurant : restaurants) {
					String img = restaurant.getImagePath();
					String imgSrc = "";

					if (img != null && !img.trim().isEmpty()) {
				img = img.trim();
				if (img.startsWith("http://") || img.startsWith("https://")) {
					imgSrc = img;
				} else if (img.startsWith(ctx + "/")) {
					imgSrc = img;
				} else if (img.startsWith("/")) {
					imgSrc = ctx + img;
				} else {
					imgSrc = ctx + "/" + img;
				}
					}
			%>

			<article class="restaurant-card">
				<!-- RESTAURANT IMAGE -->
				<div class="restaurant-image">
					<%
					if (!imgSrc.isEmpty()) {
					%>
					<img src="<%=esc(imgSrc)%>" alt="<%=esc(restaurant.getName())%>"
						loading="lazy"
						onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
					<div class="fallback" style="display: none;">🍽️</div>
					<%
					} else {
					%>
					<div class="fallback">🍽️</div>
					<%
					}
					%>
				</div>

				<!-- RESTAURANT INFORMATION -->
				<div class="restaurant-info">
					<h3 class="restaurant-name"><%=esc(restaurant.getName())%></h3>
					<p class="restaurant-cuisine"><%=esc(restaurant.getCuisineType())%></p>

					<div class="restaurant-meta">
						<span class="rating">★ <%=restaurant.getRating()%></span> <span
							class="delivery">🕒 <%=esc(restaurant.getDeliveryTime())%></span>
					</div>

					<p class="restaurant-address">
						📍
						<%=esc(restaurant.getAddress())%></p>

					<!-- VIEW MENU BUTTON -->
					<a class="view-menu"
						href="<%=ctx%>/menu?id=<%=restaurant.getRestaurantId()%>">
						View Menu → </a>
				</div>
			</article>

			<%
			}
			} else {
			%>
			<!-- EMPTY STATE -->
			<div class="empty-state">
				<div class="emoji">🍽️</div>
				<h3>No restaurants found</h3>
				<p>Try selecting another food category.</p>
			</div>
			<%
			}
			%>
		</div>
	</main>

	<!-- COMMON FOOTER -->
	<%@ include file="/includes/footer.jsp"%>

</body>
</html>