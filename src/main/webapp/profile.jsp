
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

String csrf = (String) request.getAttribute("csrf");
String flash = (String) request.getAttribute("flash");
String error = (String) request.getAttribute("error");
String errorSection = (String) request.getAttribute("errorSection");

Integer oc = (Integer) request.getAttribute("orderCount");
int orderCount = (oc == null) ? 0 : oc;

String dbName = p.getName() == null ? "" : p.getName();
String dbPhone = p.getPhone() == null ? "" : p.getPhone();
String dbAddress = p.getAddress() == null ? "" : p.getAddress();

String vName = request.getParameter("name") != null ? request.getParameter("name") : dbName;

String vPhone = request.getParameter("phone") != null ? request.getParameter("phone") : dbPhone;

String vAddress = request.getParameter("address") != null ? request.getParameter("address") : dbAddress;

String initial = dbName.isEmpty() ? "U" : dbName.substring(0, 1).toUpperCase();

String profileImageUrl = (String) request.getAttribute("profileImageUrl");

boolean hasProfileImage = profileImageUrl != null && !profileImageUrl.trim().isEmpty();

java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd MMM yyyy");

String since = p.getCreatedDate() == null ? "-" : df.format(p.getCreatedDate());
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>My Profile | Swizo</title>

<style>
:root {
	--orange: #fc8019;
	--orange-dark: #e56f0c;
	--orange-soft: #fff3e8;
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
	line-height: 1.45;
}

.profile-container {
	max-width: 600px;
	margin: 40px auto;
	background: #fff;
	border: 1px solid var(--line);
	border-radius: 24px;
	padding: 40px;
	box-shadow: 0 10px 30px rgba(31, 36, 48, 0.04);
}

.profile-header {
	text-align: center;
	margin-bottom: 26px;
}

/* Clickable circular profile photo */
.avatar {
	position: relative;
	display: block;
	width: 120px;
	height: 120px;
	border-radius: 50%;
	background: var(--orange-soft);
	color: var(--orange);
	font-size: 2.5rem;
	font-weight: 800;
	border: 3px solid var(--orange);
	margin: 0 auto 16px;
	overflow: hidden;
	cursor: pointer;
	transition: transform .2s, box-shadow .2s;
}

.avatar:hover {
	transform: scale(1.04);
	box-shadow: 0 6px 20px rgba(252, 128, 25, .25);
}

/* Initial shown when no photo exists */
.avatar-initial {
	position: absolute;
	inset: 0;
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 0;
}

/* Photo fills the complete circle */
.avatar img {
	position: absolute;
	inset: 0;
	width: 100%;
	height: 100%;
	object-fit: cover;
	object-position: center;
	border-radius: 50%;
	display: block;
	z-index: 1;
}

.avatar-overlay {
	position: absolute;
	inset: 0;
	z-index: 2;
	background: rgba(31, 36, 48, .55);
	color: #fff;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: .8rem;
	font-weight: 700;
	opacity: 0;
	transition: opacity .2s;
}

.avatar:hover .avatar-overlay {
	opacity: 1;
}

.photo-input {
	position: absolute;
	width: 1px;
	height: 1px;
	opacity: 0;
	pointer-events: none;
}

.profile-header h1 {
	font-size: 1.6rem;
	font-weight: 800;
	overflow-wrap: anywhere;
}

.profile-header p {
	color: var(--muted);
	font-size: .95rem;
}

.meta {
	display: flex;
	justify-content: center;
	gap: 28px;
	margin-top: 16px;
	color: var(--muted);
	font-size: .85rem;
}

.meta b {
	display: block;
	color: var(--ink);
	font-size: 1.05rem;
}

.divider {
	border: none;
	border-top: 1px solid var(--line);
	margin: 30px 0;
}

h2.sec {
	font-size: 1.15rem;
	font-weight: 800;
	margin-bottom: 4px;
}

p.sec-sub {
	color: var(--muted);
	font-size: .85rem;
	margin-bottom: 18px;
}

.alert {
	padding: 12px 16px;
	border-radius: 12px;
	margin-bottom: 20px;
	font-size: .9rem;
	font-weight: 600;
	text-align: center;
}

.alert-success {
	background: #e7f8ed;
	color: #20a45b;
	border: 1px solid #c6ebd2;
}

.alert-error {
	background: #fde8e8;
	color: #c53030;
	border: 1px solid #f5c6cb;
}

.form-group {
	margin-bottom: 20px;
}

label.field-label {
	display: block;
	font-weight: 700;
	font-size: .9rem;
	margin-bottom: 8px;
}

input:not([type="hidden"]):not([type="file"]), textarea {
	width: 100%;
	padding: 12px 16px;
	border: 1px solid #d1d5db;
	border-radius: 12px;
	font-size: .95rem;
	font-family: inherit;
	outline: none;
	transition: border-color .2s;
}

input:focus, textarea:focus {
	border-color: var(--orange);
}

input:disabled {
	background: #f9fafb;
	color: var(--muted);
	cursor: not-allowed;
}

textarea {
	resize: vertical;
	min-height: 90px;
}

.hint {
	color: var(--muted);
	font-size: .78rem;
	margin-top: 5px;
}

/* Save Changes button */
.save-btn {
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
	transition: background .15s;
	margin-top: 10px;
}

.save-btn:hover {
	background: var(--orange-dark);
}

/* View Profile button */
.view-profile-btn {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 10px;
	width: 100%;
	padding: 14px;
	margin-top: 12px;
	border: 1px solid var(--orange);
	border-radius: 14px;
	color: var(--orange-dark);
	background: #fff;
	text-decoration: none;
	font-weight: 800;
	transition: .2s;
}

.view-profile-btn:hover {
	background: var(--orange-soft);
}

@media ( max-width : 640px) {
	.profile-container {
		margin: 20px 12px;
		padding: 24px;
	}
}
</style>
</head>

<body>

	<jsp:include page="/includes/navbar.jsp" />

	<div class="profile-container">

		<div class="profile-header">

			<!-- Click the circular image to upload a photo -->
			<form id="photoForm" action="<%=ctx%>/profile" method="POST"
				enctype="multipart/form-data">

				<input type="hidden" name="csrf" value="<%=esc(csrf)%>"> <input
					type="hidden" name="action" value="uploadPhoto"> <label
					class="avatar" for="profileImage"
					title="Click to change profile photo"> <span
					id="avatarInitial" class="avatar-initial"
					style="<%=hasProfileImage ? "display:none;" : ""%>"> <%=esc(initial)%>
				</span> <img id="profilePreview" alt="Profile photo"
					src="<%=hasProfileImage ? ctx + "/" + esc(profileImageUrl) : ""%>"
					style="<%=hasProfileImage ? "" : "display:none;"%>"
					onerror="this.style.display='none'; document.getElementById('avatarInitial').style.display='flex';">

					<span class="avatar-overlay"> Change Photo </span>

				</label> <input type="file" id="profileImage" name="profileImage"
					class="photo-input" accept="image/jpeg,image/png,image/webp">

			</form>

			<h1><%=esc(dbName)%></h1>

			<p>Manage your Swizo account</p>

			<div class="meta">
				<div>
					<b><%=orderCount%></b> Orders
				</div>

				<div>
					<b><%=esc(since)%></b> Member since
				</div>
			</div>

		</div>

		<%
		if (flash != null) {
		%>
		<div class="alert alert-success">
			<%=esc(flash)%>
		</div>
		<%
		}
		%>

		<%
		if (error != null && "photo".equals(errorSection)) {
		%>
		<div class="alert alert-error">
			<%=esc(error)%>
		</div>
		<%
		}
		%>

		<hr class="divider">

		<h2 class="sec">Personal details</h2>

		<p class="sec-sub">These details fill in your delivery form at
			checkout.</p>

		<%
		if (error != null && "details".equals(errorSection)) {
		%>
		<div class="alert alert-error">
			<%=esc(error)%>
		</div>
		<%
		}
		%>

		<form action="<%=ctx%>/profile" method="POST">

			<input type="hidden" name="csrf" value="<%=esc(csrf)%>"> <input
				type="hidden" name="action" value="update">

			<div class="form-group">
				<label class="field-label" for="name">Full Name</label> <input
					id="name" type="text" name="name" value="<%=esc(vName)%>"
					maxlength="45" required>
			</div>

			<div class="form-group">
				<label class="field-label" for="email">Email Address</label> <input
					id="email" type="email" value="<%=esc(p.getEmail())%>" disabled>

				<div class="hint">Your email is your login and can't be
					changed here.</div>
			</div>

			<div class="form-group">
				<label class="field-label" for="phone">Phone Number</label> <input
					id="phone" type="text" name="phone" value="<%=esc(vPhone)%>"
					pattern="[0-9]{10}" maxlength="10" inputmode="numeric"
					title="10-digit phone number" required>
			</div>

			<div class="form-group">
				<label class="field-label" for="address"> Delivery Address </label>

				<textarea id="address" name="address" maxlength="120"
					placeholder="House / street / landmark, city, PIN"><%=esc(vAddress)%></textarea>
			</div>

			<button type="submit" class="save-btn">Save Changes</button>

		</form>

		<!-- Separate View Profile page -->
		<a href="<%=ctx%>/profile/view" class="view-profile-btn"> <span>View
				Profile</span> <span>→</span>
		</a>

	</div>

	<jsp:include page="/includes/footer.jsp" />

	<script>
		const photoInput = document.getElementById("profileImage");
		const photoForm = document.getElementById("photoForm");
		const preview = document.getElementById("profilePreview");
		const avatarInitial = document.getElementById("avatarInitial");

		photoInput.addEventListener("change", function() {

			const file = this.files[0];

			if (!file) {
				return;
			}

			const allowedTypes = [ "image/jpeg", "image/png", "image/webp" ];

			if (!allowedTypes.includes(file.type)) {
				alert("Please choose a JPG, PNG or WEBP image.");
				this.value = "";
				return;
			}

			if (file.size > 2 * 1024 * 1024) {
				alert("Image size must be less than 2 MB.");
				this.value = "";
				return;
			}

			const reader = new FileReader();

			reader.onload = function(e) {
				preview.src = e.target.result;
				preview.style.display = "block";
				avatarInitial.style.display = "none";
			};

			reader.readAsDataURL(file);

			// Automatically upload the selected photo.
			photoForm.requestSubmit();
		});
	</script>

</body>
</html>
