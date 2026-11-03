<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List,com.tap.model.AdminOrderRow"%>
<%!private static String esc(String s) {
		if (s == null)
			return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'",
				"&#39;");
	}

	private static String imgUrl(String ctx, String p) {
		if (p == null || p.trim().isEmpty())
			return "";
		p = p.trim();
		if (p.startsWith("http://") || p.startsWith("https://"))
			return p;
		if (p.startsWith("/"))
			return ctx + p;
		if (p.startsWith("images/"))
			return ctx + "/" + p;
		return ctx + "/images/" + p;
	}

	private static String fmt(java.util.Date d) {
		return d == null ? "-" : new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a").format(d);
	}

	private static String cls(String s) {
		return s == null ? "" : s.toLowerCase().replace('_', '-');
	}

	private static String lab(String s) {
		return s == null ? "" : s.replace('_', ' ');
	}

	private static int n(Integer i) {
		return i == null ? 0 : i;
	}%>
<%
String ctx = request.getContextPath();
@SuppressWarnings("unchecked")
List<AdminOrderRow> recent = (List<AdminOrderRow>) request.getAttribute("recent");
int restaurantCount = n((Integer) request.getAttribute("restaurantCount"));
int menuCount = n((Integer) request.getAttribute("menuCount"));
int orderCount = n((Integer) request.getAttribute("orderCount"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Dashboard | Swizo Admin</title>
<link rel="stylesheet" href="<%=ctx%>/css/admin.css">
</head>
<body>
	<div class="shell">

		<jsp:include page="/WEB-INF/admin/sidebar.jsp">
			<jsp:param name="active" value="dashboard" />
		</jsp:include>

		<main class="main">
			<jsp:include page="/WEB-INF/admin/messages.jsp" />

			<h1 class="h1">Dashboard</h1>
			<p class="sub">Welcome back, here's a quick look at Swizo.</p>

			<div class="stats">
				<div class="stat">
					<span>Restaurants</span><b><%=restaurantCount%></b>
				</div>
				<div class="stat">
					<span>Menu items</span><b><%=menuCount%></b>
				</div>
				<div class="stat">
					<span>Orders</span><b><%=orderCount%></b>
				</div>
			</div>

			<h2 class="h2">Recent orders</h2>
			<div class="card tbl">
				<table>
					<tr>
						<th>Order</th>
						<th>Date</th>
						<th>Restaurant</th>
						<th>Total</th>
						<th>Status</th>
						<th></th>
					</tr>
					<%
					if (recent == null || recent.isEmpty()) {
					%>
					<tr>
						<td colspan="6" class="muted">No orders yet.</td>
					</tr>
					<%
					} else {
					for (AdminOrderRow r : recent) {
						String img = imgUrl(ctx, r.getRestaurantImage());
					%>
					<tr>
						<td><b>#<%=r.getOrderId()%></b></td>
						<td><%=esc(fmt(r.getOrderDate()))%></td>
						<td><div class="rest">
								<%
								if (!img.isEmpty()) {
								%>
								<img src="<%=esc(img)%>" alt=""
									onerror="this.style.display='none';">
								<%
								} else {
								%>
								<div class="ph">&#127869;</div>
								<%
								}
								%>
								<%=esc(r.getRestaurantName())%>
							</div></td>
						<td><b>&#8377;<%=String.format("%.2f", r.getTotalAmount())%></b></td>
						<td><span class="pill <%=cls(r.getStatus())%>"><%=esc(lab(r.getStatus()))%></span></td>
						<td><a class="link"
							href="<%=ctx%>/admin/orders?id=<%=r.getOrderId()%>">View</a></td>
					</tr>
					<%
					}
					}
					%>
				</table>
			</div>
		</main>
	</div>
</body>
</html>