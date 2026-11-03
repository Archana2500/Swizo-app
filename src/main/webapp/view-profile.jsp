
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.tap.model.User"%>

<%!private static String esc(String s) {
		if (s == null)
			return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'",
				"&#39;");
	}%>

<%
String ctx = request.getContextPath();

User p = (User) request.getAttribute("profile");

if (p == null) {
	response.sendRedirect(ctx + "/profile");
	return;
}

String name = p.getName() == null ? "" : p.getName();
String email = p.getEmail() == null ? "" : p.getEmail();
String phone = p.getPhone() == null ? "" : p.getPhone();
String address = p.getAddress() == null ? "Address not added yet" : p.getAddress();

String image = (String) request.getAttribute("profileImageUrl");

Integer oc = (Integer) request.getAttribute("orderCount");
int orderCount = oc == null ? 0 : oc;

String initial = name.isEmpty() ? "U" : name.substring(0, 1).toUpperCase();

java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd MMM yyyy");

String since = p.getCreatedDate() == null ? "-" : df.format(p.getCreatedDate());

boolean hasImage = image != null && !image.trim().isEmpty();
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>View Profile | Swizo</title>

<style>
:root {
	--orange: #fc8019;
	--orange-dark: #e56f0c;
	--orange-soft: #fff3e8;
	--ink: #24212a;
	--muted: #777582;
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
}

.view-container {
	max-width: 650px;
	margin: 40px auto;
	padding: 35px;
	background: white;
	border: 1px solid var(--line);
	border-radius: 24px;
	box-shadow: 0 10px 30px rgba(31, 36, 48, .05);
}

.profile-top {
	text-align: center;
	padding-bottom: 25px;
	border-bottom: 1px solid var(--line);
}

.profile-avatar {
	width: 125px;
	height: 125px;
	margin: 0 auto 16px;
	border-radius: 50%;
	overflow: hidden;
	border: 4px solid var(--orange);
	background: var(--orange-soft);
	color: var(--orange);
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 2.8rem;
	font-weight: 800;
}

.profile-avatar img {
	width: 100%;
	height: 100%;
	object-fit: cover;
	object-position: center;
	display: block;
}

.profile-top h1 {
	font-size: 1.7rem;
	font-weight: 800;
	overflow-wrap: anywhere;
}

.profile-top p {
	color: var(--muted);
	margin-top: 5px;
}

.stats {
	display: flex;
	justify-content: center;
	gap: 45px;
	margin-top: 20px;
}

.stat-item {
	color: var(--muted);
	font-size: .88rem;
}

.stat-item strong {
	display: block;
	color: var(--ink);
	font-size: 1.25rem;
}

.details-title {
	font-size: 1.15rem;
	font-weight: 800;
	margin: 25px 0 16px;
}

.detail-row {
	padding: 15px 0;
	border-bottom: 1px solid var(--line);
}

.detail-label {
	display: block;
	color: var(--muted);
	font-size: .82rem;
	margin-bottom: 5px;
}

.detail-value {
	display: block;
	font-size: .98rem;
	font-weight: 650;
	overflow-wrap: anywhere;
	white-space: pre-wrap;
}

.member-card {
	margin-top: 22px;
	padding: 16px;
	background: var(--orange-soft);
	border-radius: 14px;
	display: flex;
	justify-content: space-between;
	gap: 10px;
}

.member-card span {
	color: var(--muted);
	font-size: .85rem;
}

.member-card strong {
	color: var(--orange-dark);
}

.back-btn {
	display: block;
	width: 100%;
	margin-top: 25px;
	padding: 14px;
	border-radius: 14px;
	text-align: center;
	text-decoration: none;
	background: var(--orange);
	color: white;
	font-weight: 800;
	transition: .2s;
}

.back-btn:hover {
	background: var(--orange-dark);
}

@media ( max-width : 640px) {
	.view-container {
		margin: 20px 12px;
		padding: 24px;
	}
}
</style>
</head>

<body>

	<jsp:include page="/includes/navbar.jsp" />

	<div class="view-container">

		<div class="profile-top">

			<div class="profile-avatar">

				<%
				if (hasImage) {
				%>

				<img src="<%=ctx%>/<%=esc(image)%>" alt="Profile photo"
					onerror="this.style.display='none'; this.parentElement.textContent='<%=esc(initial)%>';">

				<%
				} else {
				%>

				<%=esc(initial)%>

				<%
				}
				%>

			</div>

			<h1><%=esc(name)%></h1>

			<p>Swizo Member</p>

			<div class="stats">

				<div class="stat-item">
					<strong><%=orderCount%></strong> Orders
				</div>

				<div class="stat-item">
					<strong><%=esc(since)%></strong> Member Since
				</div>

			</div>

		</div>

		<h2 class="details-title">Personal Information</h2>

		<div class="detail-row">
			<span class="detail-label">Full Name</span> <span
				class="detail-value"><%=esc(name)%></span>
		</div>

		<div class="detail-row">
			<span class="detail-label">Email Address</span> <span
				class="detail-value"><%=esc(email)%></span>
		</div>

		<div class="detail-row">
			<span class="detail-label">Phone Number</span> <span
				class="detail-value"><%=esc(phone.isEmpty() ? "Not added" : phone)%></span>
		</div>

		<div class="detail-row">
			<span class="detail-label">Delivery Address</span> <span
				class="detail-value"><%=esc(address)%></span>
		</div>

		<div class="member-card">
			<span>Your Swizo membership</span> <strong>Active Member</strong>
		</div>

		<a href="<%=ctx%>/profile" class="back-btn"> ← Back to Edit
			Profile </a>

	</div>

	<jsp:include page="/includes/footer.jsp" />

</body>
</html>
