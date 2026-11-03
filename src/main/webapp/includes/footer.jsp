
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">

<footer class="swizo-footer">

	<div class="footer-container">

		<!-- Brand -->
		<div class="footer-brand">

			<a href="${pageContext.request.contextPath}/restaurant"
				class="footer-logo"> swizo<span>.</span>
			</a>

			<p>Good food. Great mood. Delicious meals delivered with love,
				right to your doorstep.</p>

			<div class="footer-socials">

				<a href="https://www.instagram.com/" target="_blank"
					rel="noopener noreferrer" aria-label="Instagram"> <i
					class="fa-brands fa-instagram"></i>
				</a> <a href="https://www.facebook.com/" target="_blank"
					rel="noopener noreferrer" aria-label="Facebook"> <i
					class="fa-brands fa-facebook-f"></i>
				</a> <a href="https://www.linkedin.com/" target="_blank"
					rel="noopener noreferrer" aria-label="LinkedIn"> <i
					class="fa-brands fa-linkedin-in"></i>
				</a>

			</div>
		</div>

		<!-- Quick Links -->
		<div class="footer-column">

			<h3>Explore</h3>

			<a href="${pageContext.request.contextPath}/restaurant">Home</a> <a
				href="${pageContext.request.contextPath}/restaurant">Restaurants</a>
			<a href="${pageContext.request.contextPath}/orders">My Orders</a> <a
				href="${pageContext.request.contextPath}/cart">My Cart</a>

		</div>

		<!-- Customer Support -->
		<div class="footer-column">

			<h3>Customer Support</h3>

			<a href="${pageContext.request.contextPath}/profile">My Profile</a> <a
				href="${pageContext.request.contextPath}/orders">Track Order</a> <a
				href="mailto:support@swizo.example">Contact Us</a>

			<!-- Admin Login -->
			<a href="${pageContext.request.contextPath}/admin/login"
				class="admin-login-link"> <i class="fa-solid fa-user-shield"></i>
				Admin Login
			</a>

		</div>

		<!-- About -->
		<div class="footer-column">

			<h3>About Swizo</h3>

			<p>Made for food lovers.</p>
			<p>Fresh food, happy moments!</p>

		</div>

	</div>

	<!-- Footer Bottom -->
	<div class="footer-bottom">

		<p>
			&copy;
			<%=java.time.Year.now().getValue()%>
			Swizo. All Rights Reserved.
		</p>

		<p>
			Made with <i class="fa-solid fa-heart"></i> for food lovers.
		</p>

	</div>

</footer>

<style>
.swizo-footer {
	background: #24212a;
	color: #ffffff;
	margin-top: 70px;
	padding: 55px 5% 20px;
}

.footer-container {
	max-width: 1350px;
	margin: auto;
	display: grid;
	grid-template-columns: 1.5fr 1fr 1fr 1.2fr;
	gap: 45px;
	padding-bottom: 40px;
}

.footer-logo {
	font-size: 34px;
	font-weight: 900;
	color: #ffffff;
	text-decoration: none;
}

.footer-logo span {
	color: #fc8019;
}

.footer-brand p {
	color: #c4c0ca;
	line-height: 1.8;
	max-width: 280px;
	font-size: 14px;
	margin: 15px 0;
}

.footer-socials {
	display: flex;
	gap: 14px;
	margin-top: 20px;
}

.footer-socials a {
	width: 37px;
	height: 37px;
	display: flex;
	justify-content: center;
	align-items: center;
	background: #35313d;
	border-radius: 50%;
	color: white;
	text-decoration: none;
	transition: 0.3s;
}

.footer-socials a:hover {
	background: #fc8019;
	transform: translateY(-4px);
}

.footer-column {
	display: flex;
	flex-direction: column;
	gap: 13px;
}

.footer-column h3 {
	font-size: 17px;
	margin-bottom: 8px;
	color: #ffffff;
}

.footer-column a, .footer-column p {
	color: #c4c0ca;
	font-size: 14px;
	text-decoration: none;
	line-height: 1.7;
	margin: 0;
	transition: 0.3s;
}

.footer-column a:hover {
	color: #fc8019;
}

/* Admin Login Link */
.footer-column .admin-login-link {
	display: inline-flex;
	align-items: center;
	gap: 9px;
	width: fit-content;
	color: #fc8019;
	font-weight: 600;
	margin-top: 5px;
	transition: 0.3s;
}

.footer-column .admin-login-link:hover {
	color: #ffffff;
	transform: translateX(4px);
}

.footer-column .admin-login-link i {
	font-size: 14px;
}

.footer-bottom {
	max-width: 1350px;
	margin: auto;
	border-top: 1px solid #45414b;
	padding-top: 20px;
	display: flex;
	justify-content: space-between;
	flex-wrap: wrap;
	gap: 12px;
	color: #aaa5b0;
	font-size: 13px;
}

.footer-bottom p {
	margin: 0;
}

.footer-bottom i {
	color: #fc8019;
}

@media ( max-width : 850px) {
	.footer-container {
		grid-template-columns: repeat(2, 1fr);
	}
}

@media ( max-width : 500px) {
	.footer-container {
		grid-template-columns: 1fr;
		gap: 30px;
	}
	.footer-bottom {
		flex-direction: column;
		text-align: center;
	}
}
</style>
