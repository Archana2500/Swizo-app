<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="java.util.List,java.util.Map,java.util.HashMap,com.tap.model.Menu,com.tap.model.Restaurant,com.tap.util.Csrf"%>
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

	private static String val(jakarta.servlet.http.HttpServletRequest rq, String name, String fallback) {
		String v = rq.getParameter(name);
		return v != null ? v : (fallback == null ? "" : fallback);
	}%>
<%
String ctx = request.getContextPath();
String csrf = Csrf.token(session);
@SuppressWarnings("unchecked")
List<Restaurant> restaurants = (List<Restaurant>) request.getAttribute("restaurants");
@SuppressWarnings("unchecked")
List<Menu> items = (List<Menu>) request.getAttribute("items");
Menu editing = (Menu) request.getAttribute("editing");
Integer fObj = (Integer) request.getAttribute("filter");
int filter = (fObj == null) ? 0 : fObj;

Map<Integer, String> names = new HashMap<Integer, String>();
if (restaurants != null) {
	for (Restaurant r : restaurants) {
		names.put(r.getRestaurantId(), r.getName());
	}
}

boolean submitted = request.getParameter("itemName") != null;
boolean availVal = submitted ? "on".equals(request.getParameter("available"))
		: (editing == null || editing.isAvailable());

// which restaurant the form shows: the one being edited, else what was picked, else the list filter
int formRestaurant = 0;
if (editing != null) {
	formRestaurant = editing.getRestaurantId();
} else {
	try {
		formRestaurant = Integer.parseInt(request.getParameter("restaurantId"));
	} catch (Exception e) {
		formRestaurant = filter;
	}
}
String priceText = editing == null ? "" : String.format(java.util.Locale.ROOT, "%.2f", editing.getPrice());
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Menu items | Swizo Admin</title>
<link rel="stylesheet" href="<%=ctx%>/css/admin.css">
</head>
<body>
	<div class="shell">

		<jsp:include page="/WEB-INF/admin/sidebar.jsp">
			<jsp:param name="active" value="menu" />
		</jsp:include>

		<main class="main">
			<jsp:include page="/WEB-INF/admin/messages.jsp" />

			<h1 class="h1">Menu items</h1>
			<p class="sub">Add dishes, change prices, or mark a dish
				unavailable.</p>

			<section class="card">
				<h2 class="h2" style="margin-top: 0"><%=editing == null ? "Add a menu item" : "Edit " + esc(editing.getItemName())%></h2>

				<form method="post" action="<%=ctx%>/admin/menu" class="fgrid">
					<input type="hidden" name="csrf" value="<%=esc(csrf)%>"> <input
						type="hidden" name="filter" value="<%=filter%>">
					<%
					if (editing == null) {
					%>
					<input type="hidden" name="action" value="add">
					<div>
						<label for="restaurantId">Restaurant *</label> <select
							id="restaurantId" name="restaurantId" required>
							<option value="">Choose restaurant</option>
							<%
							if (restaurants != null) {
								for (Restaurant r : restaurants) {
							%>
							<option value="<%=r.getRestaurantId()%>"
								<%=r.getRestaurantId() == formRestaurant ? "selected" : ""%>><%=esc(r.getName())%></option>
							<%
							}
							}
							%>
						</select>
					</div>
					<%
					} else {
					%>
					<input type="hidden" name="action" value="update"> <input
						type="hidden" name="menuId" value="<%=editing.getMenuId()%>">
					<div>
						<label>Restaurant</label> <input type="text" disabled
							value="<%=esc(names.get(editing.getRestaurantId()))%>">
					</div>
					<%
					}
					%>
					<div>
						<label for="itemName">Item name *</label> <input id="itemName"
							type="text" name="itemName" required maxlength="50"
							value="<%=esc(val(request, "itemName", editing == null ? "" : editing.getItemName()))%>">
					</div>
					<div>
						<label for="price">Price (&#8377;) *</label> <input id="price"
							type="number" name="price" required min="0.01" max="99999.99"
							step="0.01" value="<%=esc(val(request, "price", priceText))%>">
					</div>
					<div class="span2">
						<label for="imagePath">Image</label> <input id="imagePath"
							type="text" name="imagePath" maxlength="2000"
							placeholder="https://... or a file name like biryani.jpg"
							value="<%=esc(val(request, "imagePath", editing == null ? "" : editing.getImagePath()))%>">
						<div class="hint">A full https:// link, or a file name from
							your webapp/images folder.</div>
					</div>
					<div class="span2">
						<label for="description">Description</label>
						<textarea id="description" name="description" maxlength="200"><%=esc(val(request, "description", editing == null ? "" : editing.getDescription()))%></textarea>
					</div>
					<div class="span2">
						<label class="chk"><input type="checkbox" name="available"
							<%=availVal ? "checked" : ""%>> Available to order</label>
					</div>
					<div class="span2 acts">
						<button class="btn" type="submit"><%=editing == null ? "Add item" : "Save changes"%></button>
						<%
						if (editing != null) {
						%>
						<a class="link"
							href="<%=ctx%>/admin/menu<%=filter > 0 ? "?restaurantId=" + filter : ""%>">Cancel</a>
						<%
						}
						%>
					</div>
				</form>
			</section>

			<form method="get" action="<%=ctx%>/admin/menu" class="acts"
				style="margin-bottom: 16px">
				<label for="fr" style="font-weight: 700">Show items of</label> <select
					id="fr" name="restaurantId" onchange="this.form.submit()">
					<option value="0" <%=filter <= 0 ? "selected" : ""%>>All
						restaurants</option>
					<%
					if (restaurants != null) {
						for (Restaurant r : restaurants) {
					%>
					<option value="<%=r.getRestaurantId()%>"
						<%=r.getRestaurantId() == filter ? "selected" : ""%>><%=esc(r.getName())%></option>
					<%
					}
					}
					%>
				</select>
				<noscript>
					<button class="btn small" type="submit">Show</button>
				</noscript>
			</form>

			<div class="card tbl">
				<table>
					<tr>
						<th>Item</th>
						<th>Restaurant</th>
						<th>Price</th>
						<th>Status</th>
						<th>Actions</th>
					</tr>
					<%
					if (items == null || items.isEmpty()) {
					%>
					<tr>
						<td colspan="5" class="muted">No menu items here yet.</td>
					</tr>
					<%
					} else {
					for (Menu m : items) {
						String img = imgUrl(ctx, m.getImagePath());
						String back = filter > 0 ? "&restaurantId=" + filter : "";
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
								<div class="ph">&#127858;</div>
								<%
								}
								%>
								<div>
									<%=esc(m.getItemName())%>
									<div class="desc"><%=esc(m.getDescription())%></div>
								</div>
							</div></td>
						<td><%=esc(names.get(m.getRestaurantId()))%></td>
						<td><b>&#8377;<%=String.format("%.2f", m.getPrice())%></b></td>
						<td><span class="pill <%=m.isAvailable() ? "on" : "off"%>"><%=m.isAvailable() ? "Available" : "Unavailable"%></span></td>
						<td>
							<div class="acts">
								<a class="btn ghost small" style="text-decoration: none"
									href="<%=ctx%>/admin/menu?edit=<%=m.getMenuId()%><%=back%>">Edit</a>
								<form method="post" action="<%=ctx%>/admin/menu" class="inline">
									<input type="hidden" name="csrf" value="<%=esc(csrf)%>">
									<input type="hidden" name="filter" value="<%=filter%>">
									<input type="hidden" name="action" value="toggle"> <input
										type="hidden" name="menuId" value="<%=m.getMenuId()%>">
									<input type="hidden" name="available"
										value="<%=m.isAvailable() ? "0" : "1"%>">
									<button class="btn ghost small" type="submit"><%=m.isAvailable() ? "Make unavailable" : "Make available"%></button>
								</form>
								<form method="post" action="<%=ctx%>/admin/menu" class="inline"
									onsubmit="return confirm('Delete this menu item?');">
									<input type="hidden" name="csrf" value="<%=esc(csrf)%>">
									<input type="hidden" name="filter" value="<%=filter%>">
									<input type="hidden" name="action" value="delete"> <input
										type="hidden" name="menuId" value="<%=m.getMenuId()%>">
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