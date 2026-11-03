<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="java.util.List,com.tap.model.Restaurant,com.tap.util.Csrf"%>
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

	// what to show in a form field: what was just typed, else the stored value
	private static String val(jakarta.servlet.http.HttpServletRequest rq, String name, String fallback) {
		String v = rq.getParameter(name);
		return v != null ? v : (fallback == null ? "" : fallback);
	}%>
<%
String ctx = request.getContextPath();
String csrf = Csrf.token(session);
@SuppressWarnings("unchecked")
List<Restaurant> restaurants = (List<Restaurant>) request.getAttribute("restaurants");
Restaurant editing = (Restaurant) request.getAttribute("editing");

boolean submitted = request.getParameter("name") != null;
boolean activeVal = submitted ? "on".equals(request.getParameter("active")) : (editing == null || editing.isActive());
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Restaurants | Swizo Admin</title>
<link rel="stylesheet" href="<%=ctx%>/css/admin.css">
</head>
<body>
	<div class="shell">

		<jsp:include page="/WEB-INF/admin/sidebar.jsp">
			<jsp:param name="active" value="restaurants" />
		</jsp:include>

		<main class="main">
			<jsp:include page="/WEB-INF/admin/messages.jsp" />

			<h1 class="h1">Restaurants</h1>
			<p class="sub">Add restaurants, change their details, or hide
				them from customers.</p>

			<section class="card">
				<h2 class="h2" style="margin-top: 0"><%=editing == null ? "Add a restaurant" : "Edit " + esc(editing.getName())%></h2>

				<form method="post" action="<%=ctx%>/admin/restaurants"
					class="fgrid">
					<input type="hidden" name="csrf" value="<%=esc(csrf)%>">
					<%
					if (editing == null) {
					%>
					<input type="hidden" name="action" value="add">
					<%
					} else {
					%>
					<input type="hidden" name="action" value="update"> <input
						type="hidden" name="restaurantId"
						value="<%=editing.getRestaurantId()%>">
					<%
					}
					%>

					<div>
						<label for="name">Name *</label> <input id="name" type="text"
							name="name" required maxlength="50"
							value="<%=esc(val(request, "name", editing == null ? "" : editing.getName()))%>">
					</div>
					<div>
						<label for="cuisineType">Cuisine</label> <input id="cuisineType"
							type="text" name="cuisineType" maxlength="45"
							placeholder="North Indian, Punjabi"
							value="<%=esc(val(request, "cuisineType", editing == null ? "" : editing.getCuisineType()))%>">
					</div>
					<div>
						<label for="phone">Phone</label> <input id="phone" type="text"
							name="phone" maxlength="15"
							value="<%=esc(val(request, "phone", editing == null ? "" : editing.getPhone()))%>">
					</div>
					<div>
						<label for="deliveryTime">Delivery time</label> <input
							id="deliveryTime" type="text" name="deliveryTime" maxlength="20"
							placeholder="35 mins"
							value="<%=esc(val(request, "deliveryTime", editing == null ? "" : editing.getDeliveryTime()))%>">
					</div>
					<div class="span2">
						<label for="address">Address</label> <input id="address"
							type="text" name="address" maxlength="100"
							placeholder="BTM Layout, Bengaluru"
							value="<%=esc(val(request, "address", editing == null ? "" : editing.getAddress()))%>">
					</div>
					<div class="span2">
						<label for="imagePath">Image</label> <input id="imagePath"
							type="text" name="imagePath" maxlength="255"
							placeholder="https://... or a file name like bathinda.jpg"
							value="<%=esc(val(request, "imagePath", editing == null ? "" : editing.getImagePath()))%>">
						<div class="hint">A full https:// link, or a file name from
							your webapp/images folder.</div>
					</div>
					<div class="span2">
						<label class="chk"><input type="checkbox" name="active"
							<%=activeVal ? "checked" : ""%>> Visible to customers</label>
					</div>
					<div class="span2 acts">
						<button class="btn" type="submit"><%=editing == null ? "Add restaurant" : "Save changes"%></button>
						<%
						if (editing != null) {
						%>
						<a class="link" href="<%=ctx%>/admin/restaurants">Cancel</a>
						<%
						}
						%>
					</div>
				</form>
			</section>

			<div class="card tbl">
				<table>
					<tr>
						<th>Restaurant</th>
						<th>Cuisine</th>
						<th>Address</th>
						<th>Delivery</th>
						<th>Rating</th>
						<th>Status</th>
						<th>Actions</th>
					</tr>
					<%
					if (restaurants == null || restaurants.isEmpty()) {
					%>
					<tr>
						<td colspan="7" class="muted">No restaurants yet. Add the
							first one above.</td>
					</tr>
					<%
					} else {
					for (Restaurant r : restaurants) {
						String img = imgUrl(ctx, r.getImagePath());
					%>
					<tr>
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
								<%=esc(r.getName())%>
							</div></td>
						<td><%=esc(r.getCuisineType())%></td>
						<td><%=esc(r.getAddress())%></td>
						<td><%=esc(r.getDeliveryTime())%></td>
						<td><span class="stars">&#9733;</span> <%=String.format("%.1f", r.getRating())%></td>
						<td><span class="pill <%=r.isActive() ? "on" : "off"%>"><%=r.isActive() ? "Visible" : "Hidden"%></span></td>
						<td>
							<div class="acts">
								<a class="btn ghost small" style="text-decoration: none"
									href="<%=ctx%>/admin/restaurants?edit=<%=r.getRestaurantId()%>">Edit</a>
								<a class="btn ghost small" style="text-decoration: none"
									href="<%=ctx%>/admin/menu?restaurantId=<%=r.getRestaurantId()%>">Menu</a>
								<form method="post" action="<%=ctx%>/admin/restaurants"
									class="inline">
									<input type="hidden" name="csrf" value="<%=esc(csrf)%>">
									<input type="hidden" name="action" value="toggle"> <input
										type="hidden" name="restaurantId"
										value="<%=r.getRestaurantId()%>"> <input type="hidden"
										name="active" value="<%=r.isActive() ? "0" : "1"%>">
									<button class="btn ghost small" type="submit"><%=r.isActive() ? "Hide" : "Show"%></button>
								</form>
								<form method="post" action="<%=ctx%>/admin/restaurants"
									class="inline"
									onsubmit="return confirm('Delete this restaurant and all of its menu items? This cannot be undone.');">
									<input type="hidden" name="csrf" value="<%=esc(csrf)%>">
									<input type="hidden" name="action" value="delete"> <input
										type="hidden" name="restaurantId"
										value="<%=r.getRestaurantId()%>">
									<button class="btn danger small" type="submit">Delete</button>
								</form>
							</div>
						</td>
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