<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.tap.model.User"%>

<%
String ctx = request.getContextPath();
String active = request.getParameter("active");
if (active == null)
	active = "dashboard";

User admin = (User) session.getAttribute("loggedInUser");
String adminName = (admin != null && admin.getName() != null) ? admin.getName() : "Admin";
String adminEmail = (admin != null && admin.getEmail() != null) ? admin.getEmail() : "admin@swizo.com";
String initials = adminName.length() >= 2 ? adminName.substring(0, 2).toUpperCase() : "AD";
%>

<aside class="sidebar">
	<div class="sidebar-top">
		<!-- Brand & Search -->
		<div class="brand-area">
			<a href="<%=ctx%>/admin/dashboard" class="brand"> <span
				class="brand-icon">&#127828;</span> <span class="brand-text">swizo
					<span>admin</span>
			</span>
			</a>
		</div>

		<div class="search-box">
			<svg width="18" height="18" viewBox="0 0 24 24" fill="none"
				stroke="currentColor" stroke-width="2">
				<circle cx="11" cy="11" r="8" />
				<line x1="21" y1="21" x2="16.65" y2="16.65" /></svg>
			<input type="text" placeholder="Search...">
		</div>

		<div class="menu-label">Menu</div>

		<!-- Navigation Links with Icons -->
		<nav class="nav-links">
			<a href="<%=ctx%>/admin/dashboard"
				class="nav-item <%="dashboard".equals(active) ? "active" : ""%>"
				title="Dashboard"> <svg viewBox="0 0 24 24">
					<path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
					<polyline points="9 22 9 12 15 12 15 22" /></svg> <span class="nav-text">Dashboard</span>
			</a> <a href="<%=ctx%>/admin/restaurants"
				class="nav-item <%="restaurants".equals(active) ? "active" : ""%>"
				title="Restaurants"> <svg viewBox="0 0 24 24">
					<path d="M18 8h1a4 4 0 0 1 0 8h-1" />
					<path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z" />
					<line x1="6" y1="1" x2="6" y2="4" />
					<line x1="10" y1="1" x2="10" y2="4" />
					<line x1="14" y1="1" x2="14" y2="4" /></svg> <span class="nav-text">Restaurants</span>
			</a> <a href="<%=ctx%>/admin/menu"
				class="nav-item <%="menu".equals(active) ? "active" : ""%>"
				title="Menu items"> <svg viewBox="0 0 24 24">
					<path
						d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1-2.5-2.5Z" />
					<path d="M6 6h10" />
					<path d="M6 10h10" /></svg> <span class="nav-text">Menu items</span>
			</a> <a href="<%=ctx%>/admin/orders"
				class="nav-item <%="orders".equals(active) ? "active" : ""%>"
				title="Orders"> <svg viewBox="0 0 24 24">
					<rect x="1" y="3" width="15" height="13" rx="2" ry="2" />
					<line x1="16" y1="8" x2="20" y2="8" />
					<line x1="16" y1="12" x2="20" y2="12" />
					<line x1="16" y1="16" x2="20" y2="16" />
					<line x1="5" y1="20" x2="15" y2="20" />
					<line x1="10" y1="16" x2="10" y2="20" /></svg> <span class="nav-text">Orders</span>
			</a> <a href="<%=ctx%>/admin/agents"
				class="nav-item <%="agents".equals(active) ? "active" : ""%>"
				title="Delivery agents"> <svg viewBox="0 0 24 24">
					<path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
					<circle cx="9" cy="7" r="4" />
					<path d="M22 21v-2a4 4 0 0 0-3-3.87" />
					<path d="M16 3.13a4 4 0 0 1 0 7.75" /></svg> <span class="nav-text">Delivery
					agents</span>
			</a> <a href="<%=ctx%>/admin/feedback"
				class="nav-item <%="feedback".equals(active) ? "active" : ""%>"
				title="Feedback"> <svg viewBox="0 0 24 24">
					<path
						d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" /></svg>
				<span class="nav-text">Feedback</span>
			</a>
		</nav>
	</div>

	<!-- Sidebar Footer: Profile with Inline Logout Button -->
	<div class="sidebar-footer">
		<div class="user-profile">
			<div class="user-avatar"><%=initials%></div>
			<div class="user-info">
				<div class="user-name"><%=adminName%></div>
				<div class="user-email"><%=adminEmail%></div>
			</div>
			<a href="<%=ctx%>/logout" class="logout-icon" title="Log out"> <svg
					width="18" height="18" viewBox="0 0 24 24" fill="none"
					stroke="currentColor" stroke-width="2" stroke-linecap="round"
					stroke-linejoin="round">
					<path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
					<polyline points="16 17 21 12 16 7"></polyline>
					<line x1="21" y1="12" x2="9" y2="12"></line></svg>
			</a>
		</div>
	</div>
</aside>

<style>
/* Collapsible Hover Sidebar CSS */
.sidebar {
	width: 78px;
	background: #ffffff;
	border-right: 1px solid #eceef2;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	padding: 20px 12px;
	height: 100vh;
	position: sticky;
	top: 0;
	transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1), padding 0.3s
		cubic-bezier(0.4, 0, 0.2, 1);
	overflow: hidden;
	white-space: nowrap;
	z-index: 1000;
	box-shadow: 4px 0 20px rgba(0, 0, 0, 0.02);
}

.sidebar:hover {
	width: 260px;
	padding: 24px 16px;
}

.sidebar-top {
	display: flex;
	flex-direction: column;
	gap: 16px;
}

.brand-area {
	padding-left: 4px;
	display: flex;
	align-items: center;
}

.brand {
	font-size: 1.4rem;
	font-weight: 800;
	color: #fc8019;
	text-decoration: none;
	display: flex;
	align-items: center;
	gap: 12px;
}

.brand-icon {
	font-size: 1.5rem;
	min-width: 32px;
	text-align: center;
}

.brand-text {
	opacity: 0;
	transition: opacity 0.2s ease;
}

.sidebar:hover .brand-text {
	opacity: 1;
}

.brand-text span {
	color: #1f2430;
	font-weight: 500;
	font-size: 1rem;
}

.search-box {
	display: flex;
	align-items: center;
	background: #f8f9fc;
	border: 1px solid #eceef2;
	border-radius: 12px;
	padding: 10px 12px;
	gap: 12px;
	color: #7a8090;
	overflow: hidden;
}

.search-box svg {
	min-width: 18px;
}

.search-box input {
	border: none;
	background: transparent;
	outline: none;
	color: inherit;
	font-size: 0.9rem;
	width: 150px;
	opacity: 0;
	transition: opacity 0.2s ease;
}

.sidebar:hover .search-box input {
	opacity: 1;
}

.menu-label {
	font-size: 0.72rem;
	text-transform: uppercase;
	letter-spacing: 1px;
	color: #7a8090;
	padding-left: 12px;
	margin-top: 10px;
	font-weight: 700;
	opacity: 0;
	transition: opacity 0.2s ease;
}

.sidebar:hover .menu-label {
	opacity: 1;
}

.nav-links {
	display: flex;
	flex-direction: column;
	gap: 6px;
}

.nav-item {
	display: flex;
	align-items: center;
	gap: 16px;
	padding: 12px;
	border-radius: 12px;
	color: #1f2430;
	text-decoration: none;
	font-weight: 600;
	font-size: 0.93rem;
	transition: background 0.2s, color 0.2s;
}

.nav-item:hover, .nav-item.active {
	background: #fff3e8;
	color: #fc8019;
}

.nav-item svg {
	min-width: 22px;
	height: 22px;
	stroke: currentColor;
	fill: none;
	stroke-width: 2;
	stroke-linecap: round;
	stroke-linejoin: round;
}

.nav-text {
	opacity: 0;
	transition: opacity 0.2s ease;
}

.sidebar:hover .nav-text {
	opacity: 1;
}

.sidebar-footer {
	border-top: 1px solid #eceef2;
	padding-top: 16px;
}

.user-profile {
	display: flex;
	align-items: center;
	gap: 10px;
	padding: 4px;
	overflow: hidden;
	width: 100%;
	justify-content: space-between;
}

.user-avatar {
	width: 38px;
	height: 38px;
	background: #fc8019;
	color: #fff;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-weight: 700;
	font-size: 0.88rem;
	flex-shrink: 0;
}

.user-info {
	display: flex;
	flex-direction: column;
	opacity: 0;
	width: 0;
	overflow: hidden;
	transition: opacity 0.2s ease, width 0.2s ease;
	flex: 1;
}

.sidebar:hover .user-info {
	opacity: 1;
	width: auto;
}

.user-name {
	font-weight: 700;
	font-size: 0.85rem;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
	color: #1f2430;
}

.user-email {
	font-size: 0.72rem;
	color: #7a8090;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
	max-width: 120px;
}

.logout-icon {
	color: #e53935;
	padding: 8px;
	border-radius: 8px;
	display: flex;
	align-items: center;
	justify-content: center;
	opacity: 0;
	transition: opacity 0.2s ease, background 0.2s ease;
	flex-shrink: 0;
	text-decoration: none;
}

.sidebar:hover .logout-icon {
	opacity: 1;
}

.logout-icon:hover {
	background: #fde8e8;
}
</style>