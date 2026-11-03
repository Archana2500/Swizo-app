
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
String ctx = request.getContextPath();
String error = (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Swizo | Admin Login</title>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

<style>
* {
	margin: 0;
	padding: 0;
	box-sizing: border-box;
	font-family: 'Segoe UI', Arial, sans-serif;
}

body {
	min-height: 100vh;
	background: #fff8f1;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 25px;
}

.login-wrapper {
	width: 100%;
	max-width: 1000px;
	min-height: 570px;
	display: grid;
	grid-template-columns: 1fr 1fr;
	background: #fff;
	border-radius: 22px;
	overflow: hidden;
	box-shadow: 0 15px 55px rgba(36, 33, 42, 0.13);
}

/* LEFT PANEL */
.brand-panel {
	background: #24212a;
	color: #fff;
	padding: 55px 45px;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.logo {
	font-size: 35px;
	font-weight: 800;
	letter-spacing: -1px;
}

.logo i {
	color: #fc8019;
	margin-right: 8px;
}

.brand-content {
	margin-top: 45px;
}

.brand-icon {
	width: 75px;
	height: 75px;
	border-radius: 20px;
	background: rgba(252, 128, 25, 0.15);
	color: #fc8019;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 35px;
	margin-bottom: 28px;
}

.brand-content h1 {
	font-size: 34px;
	margin-bottom: 16px;
}

.brand-content p {
	color: #c2bec7;
	line-height: 1.8;
	font-size: 15px;
}

.brand-footer {
	color: #aaa5b0;
	font-size: 12px;
}

/* RIGHT PANEL */
.form-panel {
	padding: 65px 55px;
	display: flex;
	flex-direction: column;
	justify-content: center;
}

.admin-label {
	color: #e56f0c;
	font-size: 12px;
	font-weight: 800;
	letter-spacing: 2px;
	margin-bottom: 13px;
}

.form-panel h2 {
	color: #24212a;
	font-size: 31px;
	margin-bottom: 10px;
}

.subtitle {
	color: #777582;
	font-size: 14px;
	margin-bottom: 35px;
}

.form-group {
	margin-bottom: 22px;
}

.form-group label {
	display: block;
	color: #24212a;
	font-size: 14px;
	font-weight: 600;
	margin-bottom: 9px;
}

.input-box {
	display: flex;
	align-items: center;
	border: 1px solid #e5e1dc;
	border-radius: 10px;
	padding: 0 15px;
	height: 51px;
	transition: 0.3s;
}

.input-box:focus-within {
	border-color: #fc8019;
	box-shadow: 0 0 0 3px rgba(252, 128, 25, 0.10);
}

.input-box i {
	color: #777582;
	margin-right: 12px;
}

.input-box input {
	width: 100%;
	height: 100%;
	border: none;
	outline: none;
	font-size: 14px;
	color: #24212a;
	background: transparent;
}

.login-btn {
	width: 100%;
	height: 52px;
	border: none;
	border-radius: 10px;
	background: #fc8019;
	color: #fff;
	font-size: 15px;
	font-weight: 700;
	cursor: pointer;
	transition: 0.3s;
	margin-top: 8px;
}

.login-btn:hover {
	background: #e56f0c;
	transform: translateY(-2px);
	box-shadow: 0 7px 18px rgba(252, 128, 25, 0.25);
}

.error-message {
	background: #fff0ed;
	border: 1px solid #ffd0c8;
	color: #c0392b;
	border-radius: 8px;
	padding: 12px;
	font-size: 13px;
	margin-bottom: 20px;
}

.security-note {
	text-align: center;
	color: #777582;
	font-size: 12px;
	margin-top: 25px;
}

.security-note i {
	color: #fc8019;
	margin-right: 5px;
}

/* GO TO HOME LINK */
.home-link {
	display: block;
	text-align: center;
	margin-top: 18px;
	color: #e56f0c;
	font-size: 14px;
	font-weight: 600;
	text-decoration: none;
	transition: 0.3s;
}

.home-link:hover {
	color: #24212a;
	transform: translateY(-2px);
}

@media ( max-width : 750px) {
	.login-wrapper {
		grid-template-columns: 1fr;
	}
	.brand-panel {
		padding: 30px;
	}
	.brand-content {
		margin: 30px 0;
	}
	.brand-content h1 {
		font-size: 27px;
	}
	.brand-footer {
		margin-top: 20px;
	}
	.form-panel {
		padding: 40px 28px;
	}
}
</style>
</head>

<body>

	<div class="login-wrapper">

		<!-- LEFT BRAND PANEL -->
		<section class="brand-panel">

			<div class="logo">
				<i class="fa-solid fa-utensils"></i>swizo
			</div>

			<div class="brand-content">

				<div class="brand-icon">
					<i class="fa-solid fa-shield-halved"></i>
				</div>

				<h1>Admin Portal</h1>

				<p>Manage restaurants, monitor orders, coordinate delivery
					agents and keep your Swizo operations running smoothly.</p>

			</div>

			<div class="brand-footer">© 2026 Swizo. All rights reserved.</div>

		</section>

		<!-- RIGHT LOGIN PANEL -->
		<section class="form-panel">

			<div class="admin-label">ADMINISTRATOR ACCESS</div>

			<h2>Welcome Back!</h2>

			<p class="subtitle">Sign in to continue to your admin dashboard.
			</p>

			<%
			if (error != null && !error.isEmpty()) {
			%>

			<div class="error-message">
				<i class="fa-solid fa-circle-exclamation"></i>
				<%=error%>
			</div>

			<%
			}
			%>

			<form action="<%=ctx%>/admin/login" method="post">

				<div class="form-group">

					<label for="email">Email Address</label>

					<div class="input-box">
						<i class="fa-regular fa-envelope"></i> <input type="email"
							id="email" name="email" placeholder="Enter admin email" required>
					</div>

				</div>

				<div class="form-group">

					<label for="password">Password</label>

					<div class="input-box">
						<i class="fa-solid fa-lock"></i> <input type="password"
							id="password" name="password" placeholder="Enter your password"
							required>
					</div>

				</div>

				<button type="submit" class="login-btn">
					Secure Admin Login <i class="fa-solid fa-arrow-right"></i>
				</button>

			</form>

			<div class="security-note">
				<i class="fa-solid fa-shield-halved"></i> Authorized administrators
				only
			</div>

			<!-- GO TO HOME -->
			<a href="<%=ctx%>/index.jsp" class="home-link"> <i
				class="fa-solid fa-arrow-left"></i> Go to Home
			</a>

		</section>

	</div>

</body>
</html>

