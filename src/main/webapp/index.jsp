
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Swizo | Food Delivered With Love</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/index.css">

<style>

/* ABOUT US SECTION */
.mission {
	position: relative;
	padding: 100px 20px;
	text-align: center;
	background: #fff;
	overflow: hidden;
}

.mission-text {
	max-width: 680px;
	margin: 0 auto;
	position: relative;
	z-index: 2;
}

.mission-text h2 {
	font-size: 2.8rem;
	font-weight: 800;
	color: #fc8019;
	margin-bottom: 20px;
}

.mission-text p {
	font-size: 1.12rem;
	color: #777582;
	line-height: 1.8;
}

/* FLOATING FOOD EMOJIS */
.float {
	position: absolute;
	font-size: 3.5rem;
	user-select: none;
	z-index: 1;
	animation: floatAnim 4s ease-in-out infinite alternate;
}

.f-leaf {
	top: 18%;
	left: 10%;
	font-size: 2.5rem;
}

.f-burger {
	top: 22%;
	left: 16%;
	font-size: 5rem;
	animation-delay: 1s;
}

.f-dumpling {
	bottom: 20%;
	left: 14%;
	display: none;
}

.f-pizza {
	bottom: 15%;
	right: 12%;
	font-size: 5rem;
	animation-delay: 1.5s;
}

.f-tomato-one {
	bottom: 25%;
	left: 12%;
	font-size: 2.2rem;
	animation-delay: 0.5s;
}

.f-tomato-two {
	top: 20%;
	right: 10%;
	font-size: 2.5rem;
	animation-delay: 2.5s;
}

/* FLOATING ANIMATION */
@
keyframes floatAnim { 0% {
	transform: translateY(0) rotate(0deg);
}

100
%
{
transform
:
translateY(
-12px
)
rotate(
5deg
);
}
}

/* CONTACT US INSIDE PHONE */
.contact-phone-content {
	width: 100%;
	height: 100%;
	min-height: 260px;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	text-align: center;
	padding: 15px;
	background: #fff;
	border-radius: 24px;
}

.contact-phone-icon {
	width: 76px;
	height: 76px;
	border-radius: 22px;
	background: #fff0e5;
	color: #fc8019;
	display: flex;
	justify-content: center;
	align-items: center;
	font-size: 38px;
	margin-bottom: 18px;
}

.contact-phone-content h3 {
	color: #24212a;
	font-size: 21px;
	margin-bottom: 10px;
}

.contact-phone-content p {
	color: #777582;
	font-size: 13px;
	line-height: 1.6;
	margin-bottom: 17px;
}

/* EMAIL ADDRESS */
.contact-phone-content .contact-email {
	color: #fc8019;
	font-size: 13px;
	font-weight: 700;
	margin-bottom: 17px;
	word-break: break-word;
}

.contact-phone-button {
	display: inline-block;
	padding: 10px 20px;
	background: #fc8019;
	color: white;
	border-radius: 25px;
	font-size: 13px;
	font-weight: 700;
	text-decoration: none;
	transition: 0.3s;
}

.contact-phone-button:hover {
	background: #e56f0c;
	transform: translateY(-2px);
}

@media ( max-width : 900px) {
	.float {
		display: none;
	}
}

@media ( max-width : 760px) {
	.mission {
		padding: 75px 22px;
	}
	.mission-text h2 {
		font-size: 2.1rem;
	}
	.mission-text p {
		font-size: 1rem;
	}
}
</style>
</head>

<body>

	<!-- NAVBAR -->
	<header class="navbar" id="navbar">

		<a class="logo" href="${pageContext.request.contextPath}/"> Swi<span>zo</span>
		</a>

		<nav class="nav-links">
			<a href="#categories">Explore Food</a> <a class="nav-button"
				href="${pageContext.request.contextPath}/restaurant">
				Restaurants <span>&rarr;</span>
			</a>
		</nav>

	</header>

	<main>

		<!-- VIDEO HERO -->
		<section class="hero" aria-label="Discover delicious food">

			<video class="hero-video" id="heroVideo" autoplay muted loop
				playsinline preload="auto">

				<source
					src="${pageContext.request.contextPath}/videos/swizo-app.mp4"
					type="video/mp4">

			</video>

			<div class="hero-overlay"></div>

			<div class="hero-content">

				<h1>
					Swizo, your #1 <br> food delivery app
				</h1>

				<p>
					Experience fast &amp; easy online ordering<br> on the Swizo
					app
				</p>

			</div>

			<a class="scroll-down" href="#mission"> <span>Scroll down</span>

				<svg width="22" height="22" viewBox="0 0 24 24" fill="none"
					stroke="currentColor" stroke-width="2" stroke-linecap="round"
					stroke-linejoin="round" aria-hidden="true">

                    <polyline points="6 9 12 15 18 9"></polyline>

                </svg>

			</a>

		</section>


		<!-- ABOUT US SECTION -->
		<section class="mission" id="mission">

			<span class="float f-leaf">🌿</span> <span class="float f-burger">🍔</span>
			<span class="float f-dumpling">🥟</span> <span class="float f-pizza">🍕</span>
			<span class="float f-tomato-one">🍅</span> <span
				class="float f-tomato-two">🍅</span>

			<div class="mission-text reveal">

				<h2>About Us</h2>

				<p>Welcome to Swizo, where great food meets convenience. We
					bring your favourite meals closer to you by helping you discover
					restaurants, explore delicious menus and enjoy food delivered right
					to your doorstep. Whether it is a quick bite or a family feast,
					Swizo makes every food experience special.</p>

			</div>

		</section>


		<!-- APP FEATURES SECTION -->
		<section class="app-section">

			<div class="app-heading reveal">

				<h2>What's waiting for you on the app?</h2>

				<p>Our app is packed with features that make ordering food
					easier and more enjoyable than ever.</p>

			</div>


			<div class="app-showcase reveal">

				<!-- LEFT FEATURE CARDS -->
				<div class="feature-side">

					<div class="feature-card">
						<span class="feature-icon">🥗</span> Healthy
					</div>

					<div class="feature-card">
						<span class="feature-icon">🌱</span> Veg Mode
					</div>

					<div class="feature-card">
						<span class="feature-icon">🎉</span> Plan a Party
					</div>

					<div class="feature-card">
						<span class="feature-icon">🎁</span> Gift Cards
					</div>

				</div>


				<!-- CENTRAL PHONE -->
				<div class="phone">

					<div class="phone-notch"></div>

					<div class="phone-screen">

						<!-- CONTACT US -->
						<div class="contact-phone-content">

							<div class="contact-phone-icon">☎</div>

							<h3>Contact Us</h3>

							<p>Need help with your order or have a question? We're here
								to help make your Swizo experience better.</p>

							<p class="contact-email">archanak2074@gmail.com</p>

							<a href="mailto:archanak2074@gmail.com"
								class="contact-phone-button"> Get in Touch </a>

						</div>

					</div>

				</div>


				<!-- RIGHT FEATURE CARDS -->
				<div class="feature-side">

					<div class="feature-card">
						<span class="feature-icon">🍝</span> Gourmet
					</div>

					<div class="feature-card">
						<span class="feature-icon">🏷️</span> Offers
					</div>

					<div class="feature-card">
						<span class="feature-icon">🚆</span> Food on Train
					</div>

					<div class="feature-card">
						<span class="feature-icon">🍔</span> Collections
					</div>

				</div>

			</div>

		</section>


		<!-- BENEFITS -->
		<section class="benefits">

			<article class="benefit">

				<span class="benefit-icon">🍽️</span>

				<div>
					<h3>Something for everyone</h3>
					<p>Explore delicious favourites</p>
				</div>

			</article>


			<article class="benefit">

				<span class="benefit-icon">🛵</span>

				<div>
					<h3>Easy restaurant discovery</h3>
					<p>Find your next meal in a few clicks</p>
				</div>

			</article>


			<article class="benefit">

				<span class="benefit-icon">❤️</span>

				<div>
					<h3>Made with love</h3>
					<p>Good food brings us together</p>
				</div>

			</article>

		</section>


		<!-- FOOD CATEGORIES -->
		<section class="section" id="categories">

			<div class="section-heading reveal">

				<span class="eyebrow">A WORLD OF FLAVOUR</span>

				<h2>What are you craving today?</h2>

				<p>Find something delicious for every mood.</p>

			</div>


			<div class="category-grid">

				<article class="category-card reveal">

					<img
						src="https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&amp;fit=crop&amp;w=700&amp;q=80"
						alt="Juicy burger" loading="lazy">

					<div class="category-info">

						<div>
							<h3>Burgers</h3>
							<p>Big bites, big smiles</p>
						</div>

						<span class="category-arrow">&nearr;</span>

					</div>

				</article>


				<article class="category-card reveal">

					<img
						src="https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&amp;fit=crop&amp;w=700&amp;q=80"
						alt="Freshly baked pizza" loading="lazy">

					<div class="category-info">

						<div>
							<h3>Pizza</h3>
							<p>A slice of happiness</p>
						</div>

						<span class="category-arrow">&nearr;</span>

					</div>

				</article>


				<article class="category-card reveal">

					<img
						src="https://images.unsplash.com/photo-1589302168068-964664d93dc0?auto=format&amp;fit=crop&amp;w=700&amp;q=80"
						alt="Delicious Indian biryani" loading="lazy"
						onerror="this.onerror=null; this.src='https://placehold.co/700x450/F8E8D0/6B3218?text=Delicious+Biryani';">

					<div class="category-info">

						<div>
							<h3>Biryani</h3>
							<p>Rich and full of flavour</p>
						</div>

						<span class="category-arrow">&nearr;</span>

					</div>

				</article>


				<article class="category-card reveal">

					<img
						src="https://images.unsplash.com/photo-1563805042-7684c019e1cb?auto=format&amp;fit=crop&amp;w=700&amp;q=80"
						alt="Delicious dessert" loading="lazy">

					<div class="category-info">

						<div>
							<h3>Desserts</h3>
							<p>A sweet little treat</p>
						</div>

						<span class="category-arrow">&nearr;</span>

					</div>

				</article>

			</div>

		</section>


		<!-- RESTAURANT CALL TO ACTION -->
		<section class="section restaurant-section">

			<div class="restaurant-banner reveal">

				<div>

					<span class="eyebrow">YOUR NEXT MEAL AWAITS</span>

					<h2>Good food is just a click away.</h2>

					<p>Discover restaurants, explore menus, and find something you
						will love.</p>

				</div>

				<a class="explore-btn"
					href="${pageContext.request.contextPath}/restaurant"> Explore
					Restaurants <span>&rarr;</span>

				</a>

			</div>

		</section>

	</main>


	<!-- FOOTER -->
	<footer>

		<a class="logo footer-logo" href="${pageContext.request.contextPath}/">
			Swi<span>zo</span>
		</a>

		<p>Made with ❤️ for food lovers. &copy; Swizo</p>

	</footer>


	<!-- HOMEPAGE JAVASCRIPT -->
	<script src="${pageContext.request.contextPath}/js/swizo-index.js"
		defer></script>

</body>
</html>

