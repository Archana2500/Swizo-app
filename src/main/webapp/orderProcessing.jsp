<style>
/* Universal Reset */
*, *::before, *::after {
	margin: 0;
	padding: 0;
	box-sizing: border-box;
}

body {
	min-height: 100vh;
	display: flex;
	align-items: center;
	justify-content: center;
	font-family: "Segoe UI", Arial, sans-serif;
	background: linear-gradient(135deg, #fff8f1, #fff0df);
	color: #24212a;
	padding: 20px;
}

.processing-container {
	width: 92%;
	max-width: 650px;
	min-height: 440px;
	text-align: center;
	padding: 40px 25px;
	background: #ffffff;
	border-radius: 25px;
	box-shadow: 0 15px 45px rgba(70, 40, 15, 0.10);
}

.logo {
	font-size: 34px;
	font-weight: 800;
	color: #fc8019;
	letter-spacing: -1px;
	margin-bottom: 18px;
}

h1 {
	font-size: 28px;
	margin-bottom: 12px;
	transition: color 0.3s ease;
}

.subtitle {
	color: #777582;
	font-size: 15px;
	line-height: 1.6;
}

/* SAME DISPLAY AREA FOR BIKE AND SUCCESS */
.animation-stage {
	position: relative;
	width: 100%;
	height: 245px;
	margin: 25px auto 10px;
	overflow: hidden;
}

/* DELIVERY ANIMATION */
.delivery-scene {
	position: absolute;
	inset: 0;
	opacity: 1;
	visibility: visible;
	transition: opacity 0.6s ease, visibility 0.6s ease;
}

.road-area {
	position: absolute;
	inset: 0;
	overflow: hidden;
}

.road {
	position: absolute;
	bottom: 28px;
	left: 0;
	width: 100%;
	height: 5px;
	background: #dedede;
	border-radius: 10px;
}

.road::after {
	content: "";
	position: absolute;
	top: 2px;
	left: 0;
	width: 100%;
	border-top: 2px dashed #aaa;
}

/* BIKE */
.delivery-bike {
	position: absolute;
	bottom: 33px;
	left: -150px;
	width: 125px;
	height: 100px;
	animation: ride 4s ease-in-out forwards;
}

.bike {
	position: relative;
	width: 125px;
	height: 85px;
}

.wheel {
	position: absolute;
	bottom: 0;
	width: 43px;
	height: 43px;
	border: 5px solid #292631;
	border-radius: 50%;
	background: white;
	animation: spin 0.5s linear infinite;
}

.wheel::after {
	content: "";
	position: absolute;
	width: 9px;
	height: 9px;
	background: #fc8019;
	border-radius: 50%;
	top: 12px;
	left: 12px;
}

.wheel.left {
	left: 2px;
}

.wheel.right {
	right: 2px;
}

.bike-frame {
	position: absolute;
	left: 20px;
	bottom: 20px;
	width: 85px;
	height: 45px;
	border-bottom: 5px solid #fc8019;
	border-left: 5px solid #fc8019;
	border-right: 5px solid #fc8019;
	transform: skew(-12deg);
}

.bike-frame::before {
	content: "";
	position: absolute;
	width: 45px;
	height: 5px;
	background: #fc8019;
	top: 8px;
	left: 20px;
	transform: rotate(-35deg);
}

.bike-seat {
	position: absolute;
	width: 28px;
	height: 6px;
	background: #292631;
	left: 43px;
	top: 15px;
	border-radius: 5px;
}

.bike-handle {
	position: absolute;
	width: 22px;
	height: 5px;
	background: #292631;
	right: 3px;
	top: 26px;
	transform: rotate(-20deg);
}

.rider {
	position: absolute;
	left: 48px;
	top: -4px;
	width: 15px;
	height: 28px;
	background: #fc8019;
	border-radius: 10px;
	transform: rotate(20deg);
}

.rider::before {
	content: "";
	position: absolute;
	width: 17px;
	height: 17px;
	border-radius: 50%;
	background: #292631;
	top: -15px;
	left: 2px;
}

.delivery-box {
	position: absolute;
	top: 12px;
	left: 4px;
	width: 25px;
	height: 23px;
	border-radius: 5px;
	background: #fc8019;
	color: white;
	font-size: 14px;
	font-weight: bold;
	display: flex;
	align-items: center;
	justify-content: center;
}

@keyframes ride {
	0% { left: -150px; }
	65% { left: 48%; }
	100% { left: calc(100% + 20px); }
}

@keyframes spin {
	to { transform: rotate(360deg); }
}

/* SUCCESS APPEARS IN THE SAME DISPLAY AREA */
.success-scene {
	position: absolute;
	inset: 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	gap: 10px;
	opacity: 0;
	visibility: hidden;
	transform: scale(0.92);
	transition: opacity 0.7s ease, transform 0.7s ease, visibility 0.7s ease;
}

.success-icon {
	width: 85px;
	height: 85px;
	border-radius: 50%;
	background: #e7f8ed;
	color: #20a45b;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 49px;
	font-weight: bold;
	box-shadow: 0 5px 20px rgba(32, 164, 91, 0.12);
}

.success-scene h2 {
	font-size: 23px;
	color: #24212a;
}

.order-number {
	font-size: 15px;
	color: #777582;
}

.orders-btn {
	display: inline-block;
	margin-top: 8px;
	padding: 12px 28px;
	background: #fc8019;
	color: white;
	text-decoration: none;
	border-radius: 9px;
	font-weight: 700;
	transition: 0.3s;
}

.orders-btn:hover {
	background: #e96d0b;
	transform: translateY(-2px);
}

/* COMPLETED STATE */
.processing-container.completed .delivery-scene {
	opacity: 0;
	visibility: hidden;
}

.processing-container.completed .success-scene {
	opacity: 1;
	visibility: visible;
	transform: scale(1);
}

.processing-message {
	margin-top: 5px;
	color: #fc8019;
	font-weight: 600;
	font-size: 14px;
	transition: opacity 0.4s ease;
}

.processing-container.completed .processing-message {
	opacity: 0;
	visibility: hidden;
}

@media (max-width: 480px) {
	.processing-container {
		padding: 35px 17px;
		min-height: 420px;
	}
	h1 {
		font-size: 23px;
	}
	.animation-stage {
		height: 225px;
	}
	.success-scene h2 {
		font-size: 20px;
	}
}

@media (prefers-reduced-motion: reduce) {
	.delivery-bike, .wheel {
		animation-duration: 0.01ms;
	}
	.delivery-bike {
		left: calc(100% + 20px);
	}
	.processing-container.completed .success-scene {
		transition-duration: 0.01ms;
	}
}
</style>