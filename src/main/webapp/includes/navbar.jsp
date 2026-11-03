
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.tap.model.User"%>
<%@ page import="com.tap.model.Cart"%>

<%
String navCtx = request.getContextPath();

Cart navCart = (Cart) session.getAttribute("cart");

int navCartCount = (navCart == null) ? 0 : navCart.getTotalQuantity();

User navUser = (User) session.getAttribute("user");

boolean navLoggedIn = navUser != null;

String navPhoto = "";

if (navUser != null && navUser.getProfileImage() != null) {
	navPhoto = navUser.getProfileImage().trim();
}

String safeNavPhoto = navPhoto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
		.replace("'", "&#39;");
%>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">

<header class="swizo-navbar">

	<div class="navbar-container">

		<!-- LOGO -->

		<a href="<%=navCtx%>/restaurant" class="swizo-logo"> swizo<span>.</span>
		</a>

		<!-- MOBILE MENU BUTTON -->

		<button class="mobile-menu-btn" type="button"
			onclick="document.querySelector('.navbar-links').classList.toggle('active')">

			<i class="fa-solid fa-bars"></i>

		</button>

		<!-- NAVIGATION LINKS -->

		<nav class="navbar-links">

			<a href="<%=navCtx%>/restaurant" class="nav-link"> <i
				class="fa-solid fa-house"></i> Home
			</a> <a href="<%=navCtx%>/restaurant" class="nav-link"> <i
				class="fa-solid fa-utensils"></i> Explore Restaurants
			</a> <a href="<%=navCtx%>/restaurant?cuisine=Pizza" class="nav-link"> <i
				class="fa-solid fa-pizza-slice"></i> Pizza

			</a> <a href="<%=navCtx%>/restaurant?cuisine=Biryani" class="nav-link">

				<i class="fa-solid fa-bowl-food"></i> Biryani

			</a> <a href="<%=navCtx%>/orders" class="nav-link"> <i
				class="fa-solid fa-box"></i> My Orders

			</a>

		</nav>

		<!-- RIGHT SIDE ACTIONS -->

		<div class="navbar-actions">

			<!-- CART -->

			<a href="<%=navCtx%>/cart" class="nav-icon" title="Cart"> <i
				class="fa-solid fa-cart-shopping"></i> <span id="cartBadge"
				class="cart-badge"
				style="display:<%=navCartCount > 0 ? "inline-block" : "none"%>">

					<%=navCartCount%>

			</span>

			</a>

			<%
			if (navLoggedIn) {
			%>

			<!-- PROFILE WITH UPLOADED PHOTO -->

			<a href="<%=navCtx%>/profile" class="profile-link" title="My Profile">

				<%
				if (!navPhoto.isEmpty()) {
				%> <img src="<%=navCtx%>/<%=safeNavPhoto%>" class="nav-profile-img"
				alt="Profile"
				onerror="this.style.display='none';this.nextElementSibling.style.display='inline-block';">

				<i class="fa-solid fa-circle-user profile-fallback"
				style="display: none;"></i> <%
 } else {
 %> <i class="fa-solid fa-circle-user"></i> <%
 }
 %> <span>Profile</span>

			</a>

			<!-- LOGOUT -->

			<a href="<%=navCtx%>/logout" class="profile-link" title="Logout"> <i
				class="fa-solid fa-right-from-bracket"></i> <span>Logout</span>

			</a>

			<%
			} else {
			%>

			<!-- LOGIN -->

			<a href="<%=navCtx%>/login" class="profile-link" title="Login"> <i
				class="fa-solid fa-right-to-bracket"></i> <span>Login</span>

			</a>

			<%
			}
			%>

		</div>

	</div>

</header>

<style>
.swizo-navbar {
	width: 100%;
	background: #ffffff;
	box-shadow: 0 3px 18px rgba(0, 0, 0, 0.06);
	position: sticky;
	top: 0;
	z-index: 1000;
}

.navbar-container {
	max-width: 1350px;
	margin: auto;
	padding: 15px 5%;
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 25px;
}

.swizo-logo {
	font-size: 32px;
	font-weight: 900;
	color: #24212a;
	text-decoration: none;
	letter-spacing: -1.5px;
}

.swizo-logo span {
	color: #fc8019;
}

.navbar-links {
	display: flex;
	align-items: center;
	gap: 27px;
}

.nav-link {
	text-decoration: none;
	color: #55515c;
	font-size: 14px;
	font-weight: 600;
	transition: 0.3s;
	white-space: nowrap;
}

.nav-link i {
	margin-right: 5px;
	color: #fc8019;
}

.nav-link:hover {
	color: #fc8019;
}

.navbar-actions {
	display: flex;
	align-items: center;
	gap: 22px;
}

.nav-icon {
	position: relative;
	color: #24212a;
	font-size: 20px;
	text-decoration: none;
}

.cart-badge {
	position: absolute;
	top: -9px;
	right: -11px;
	background: #fc8019;
	color: #fff;
	font-size: 11px;
	font-weight: 800;
	border-radius: 999px;
	padding: 1px 6px;
	line-height: 1.4;
}

.profile-link {
	display: flex;
	align-items: center;
	gap: 7px;
	color: #24212a;
	font-weight: 600;
	font-size: 14px;
	text-decoration: none;
	white-space: nowrap;
}

.profile-link:hover {
	color: #fc8019;
}

.profile-link i {
	color: #fc8019;
	font-size: 23px;
}

.nav-profile-img {
	width: 34px;
	height: 34px;
	border-radius: 50%;
	object-fit: cover;
	border: 2px solid #fc8019;
	display: inline-block;
}

.mobile-menu-btn {
	display: none;
	background: none;
	border: none;
	font-size: 23px;
	color: #24212a;
	cursor: pointer;
}

@media ( max-width : 950px) {
	.navbar-container {
		flex-wrap: wrap;
	}
	.navbar-links {
		display: none;
		flex-direction: column;
		align-items: flex-start;
		width: 100%;
		gap: 20px;
		padding: 15px 0;
	}
	.navbar-links.active {
		display: flex;
	}
	.mobile-menu-btn {
		display: block;
		margin-left: auto;
	}
}

@media ( max-width : 480px) {
	.navbar-container {
		padding: 13px 5%;
	}
	.swizo-logo {
		font-size: 27px;
	}
	.navbar-actions {
		gap: 12px;
	}
	.profile-link span {
		font-size: 12px;
	}
}
</style>

