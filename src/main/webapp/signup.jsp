<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
String ctx = request.getContextPath();
String error = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Sign Up | Swizo</title>
<style>
:root {
	--orange: #fc8019;
	--orange-dark: #e56f0c;
	--orange-soft: #fff3e8;
	--red-soft: #fde8e8;
	--red-text: #c53030;
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
	display: flex;
	min-height: 100vh;
	overflow: hidden;
}

.auth-container {
	display: flex;
	width: 100vw;
	height: 100vh;
}

/* Left Brand/Image Panel */
.auth-sidebar {
	flex: 1.2;
	background: linear-gradient(135deg, #1f2430 0%, #12141c 100%);
	position: relative;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	padding: 50px;
	color: #fff;
	overflow: hidden;
}

.auth-sidebar::before {
	content: '';
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: url('<%=ctx%>/images/swizo-app-promo.png') center/cover
		no-repeat;
	opacity: 0.85;
}

.sidebar-brand {
	font-size: 2.2rem;
	font-weight: 800;
	color: var(--orange);
	text-decoration: none;
	z-index: 2;
	text-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
}

/* Right Form Panel */
.auth-main {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 40px;
	background: #fff;
	overflow-y: auto;
}

.signup-card {
	width: 100%;
	max-width: 420px;
}

.signup-header {
	margin-bottom: 28px;
}

.signup-header h1 {
	font-size: 2rem;
	font-weight: 800;
	color: var(--ink);
	margin-bottom: 8px;
}

.signup-header p {
	color: var(--muted);
	font-size: 0.98rem;
}

.error-banner {
	background: var(--red-soft);
	border: 1px solid #f5c6cb;
	color: var(--red-text);
	border-radius: 12px;
	padding: 12px 16px;
	margin-bottom: 20px;
	font-size: 0.90rem;
	text-align: center;
	font-weight: 600;
}

.form-group {
	margin-bottom: 16px;
}

label {
	display: block;
	font-weight: 700;
	font-size: 0.88rem;
	margin-bottom: 6px;
	color: var(--ink);
}

input {
	width: 100%;
	padding: 12px 16px;
	border: 1px solid #d1d5db;
	border-radius: 12px;
	font-size: 0.95rem;
	outline: none;
	transition: border-color .2s;
}

input:focus {
	border-color: var(--orange);
}

.signup-btn {
	display: block;
	width: 100%;
	background: var(--orange);
	color: #fff;
	border: none;
	padding: 14px;
	border-radius: 14px;
	font-weight: 800;
	font-size: 1rem;
	cursor: pointer;
	margin-top: 20px;
	transition: background .15s;
}

.signup-btn:hover {
	background: var(--orange-dark);
}

.login-footer {
	text-align: center;
	margin-top: 24px;
	font-size: 0.92rem;
	color: var(--muted);
}

.login-footer a {
	color: var(--orange-dark);
	font-weight: 700;
	text-decoration: none;
}

.login-footer a:hover {
	text-decoration: underline;
}

@media ( max-width : 900px) {
	.auth-sidebar {
		display: none;
	}
	.auth-main {
		background: var(--bg);
	}
}
</style>
</head>
<body>
	<div class="auth-container">
		<!-- Left Hero Panel with Image Only -->
		<div class="auth-sidebar">
			<a href="<%=ctx%>/restaurant" class="sidebar-brand">swizo</a>
			<div
				style="font-size: 0.85rem; color: #ffffff; z-index: 2; text-shadow: 0 1px 6px rgba(0, 0, 0, 0.8);">&copy;
				2026 Swizo Technologies</div>
		</div>

		<!-- Right Form Panel -->
		<div class="auth-main">
			<div class="signup-card">
				<div class="signup-header">
					<h1>Create Account</h1>
					<p>Fill in your details to get started.</p>
				</div>
				<%
				if (error != null && !error.isEmpty()) {
				%>
				<div class="error-banner"><%=error%></div>
				<%
				}
				%>
				<form action="<%=ctx%>/signup" method="POST">
					<div class="form-group">
						<label>Full Name</label> <input type="text" name="name" required
							placeholder="John Doe">
					</div>
					<div class="form-group">
						<label>Email Address</label> <input type="email" name="email"
							required placeholder="name@example.com">
					</div>
					<div class="form-group">
						<label>Phone Number</label> <input type="text" name="phone"
							required placeholder="9876543210">
					</div>
					<div class="form-group">
						<label>Password</label> <input type="password" name="password"
							required placeholder="Create a secure password">
					</div>
					<button type="submit" class="signup-btn">Sign Up</button>
				</form>
				<div class="login-footer">
					Already have an account? <a href="<%=ctx%>/login">Login here</a>
				</div>
			</div>
		</div>
	</div>
</body>
</html>