
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page
	import="java.util.List,com.tap.model.AdminOrderRow,com.tap.model.DeliveryAgent,com.tap.model.OrderItemView,com.tap.util.Csrf,com.tap.util.OrderStatus"%>

<%!private static String esc(String s) {
		if (s == null)
			return "";

		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'",
				"&#39;");
	}

	private static String fmt(java.util.Date d) {
		return d == null ? "-" : new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a").format(d);
	}

	private static String cls(String s) {
		return s == null ? "" : s.toLowerCase().replace('_', '-');
	}

	private static String lab(String s) {
		return s == null ? "" : s.replace('_', ' ');
	}%>

<%
String ctx = request.getContextPath();
String csrf = Csrf.token(session);

AdminOrderRow order = (AdminOrderRow) request.getAttribute("order");
%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Orders | Swizo Admin</title>

<link rel="stylesheet" href="<%=ctx%>/css/admin.css">

<style>
.restaurant-info {
	display: flex;
	align-items: center;
	gap: 12px;
	min-width: 170px;
}

.restaurant-thumb {
	width: 48px;
	height: 48px;
	object-fit: cover;
	border-radius: 10px;
	flex-shrink: 0;
	border: 1px solid #eee;
}
</style>
</head>

<body>

	<div class="shell">

		<jsp:include page="/WEB-INF/admin/sidebar.jsp">
			<jsp:param name="active" value="orders" />
		</jsp:include>

		<main class="main">

			<jsp:include page="/WEB-INF/admin/messages.jsp" />

			<%
			if (order != null) {

				@SuppressWarnings("unchecked")
				List<OrderItemView> items = (List<OrderItemView>) request.getAttribute("items");

				@SuppressWarnings("unchecked")
				List<DeliveryAgent> agents = (List<DeliveryAgent>) request.getAttribute("agents");

				boolean locked = OrderStatus.isFinal(order.getStatus());
			%>

			<p>
				<a class="link" href="<%=ctx%>/admin/orders"> &larr; All orders
				</a>
			</p>

			<h1 class="h1" style="margin-top: 8px">
				Order #<%=order.getOrderId()%>

				<span class="pill <%=cls(order.getStatus())%>"
					style="vertical-align: middle"> <%=esc(lab(order.getStatus()))%>
				</span>
			</h1>

			<p class="sub">
				Placed on
				<%=esc(fmt(order.getOrderDate()))%>
			</p>

			<div class="card">

				<div class="kv">

					<div>
						<small>Customer</small> <b><%=esc(order.getCustomerName())%></b> <br>
						<span class="muted"> <%=esc(order.getCustomerPhone())%>
						</span>
					</div>

					<div>
						<small>Restaurant</small>

						<div class="restaurant-info">

							<img class="restaurant-thumb"
								src="<%=ctx%>/<%=esc(order.getRestaurantImage())%>"
								alt="<%=esc(order.getRestaurantName())%>"
								onerror="this.style.display='none'"> <b><%=esc(order.getRestaurantName())%></b>

						</div>
					</div>

					<div>
						<small>Payment</small> <b><%=esc(order.getPaymentMode())%></b>
					</div>

					<div>
						<small>Delivery agent</small> <b> <%=order.getAgentName() == null ? "Not assigned" : esc(order.getAgentName())%>
						</b>
					</div>

				</div>

			</div>

			<div class="card tbl">

				<table>

					<tr>
						<th>Item</th>
						<th>Qty</th>
						<th>Amount</th>
					</tr>

					<%
					if (items != null) {
						for (OrderItemView it : items) {
					%>

					<tr>
						<td><%=esc(it.getItemName())%></td>

						<td>&times; <%=it.getQuantity()%>
						</td>

						<td>&#8377;<%=String.format("%.2f", it.getItemTotal())%>
						</td>
					</tr>

					<%
					}
					}
					%>

					<tr>
						<td colspan="2"><b>Total (including delivery fee)</b></td>

						<td><b> &#8377;<%=String.format("%.2f", order.getTotalAmount())%>
						</b></td>
					</tr>

				</table>

			</div>

			<%
			if (locked) {
			%>

			<div class="banner ok">
				This order is
				<%=esc(lab(order.getStatus()).toLowerCase())%>, so it can no longer
				be changed.
			</div>

			<%
			} else {
			%>

			<div class="card">

				<h2 class="h2" style="margin-top: 0">Update this order</h2>

				<div class="acts" style="gap: 28px; align-items: flex-end">

					<form method="post" action="<%=ctx%>/admin/orders" class="acts">

						<input type="hidden" name="csrf" value="<%=esc(csrf)%>"> <input
							type="hidden" name="action" value="status"> <input
							type="hidden" name="orderId" value="<%=order.getOrderId()%>">

						<select name="status">

							<%
							for (String st : OrderStatus.ALL) {
							%>

							<option value="<%=st%>"
								<%=st.equals(order.getStatus()) ? "selected" : ""%>>
								<%=esc(lab(st))%>
							</option>

							<%
							}
							%>

						</select>

						<button class="btn" type="submit">Update status</button>

					</form>

					<form method="post" action="<%=ctx%>/admin/orders" class="acts">

						<input type="hidden" name="csrf" value="<%=esc(csrf)%>"> <input
							type="hidden" name="action" value="assign"> <input
							type="hidden" name="orderId" value="<%=order.getOrderId()%>">

						<select name="agentId" required>

							<option value="">Choose delivery agent</option>

							<%
							if (agents != null) {
								for (DeliveryAgent a : agents) {
							%>

							<option value="<%=a.getAgentId()%>"
								<%=(order.getAgentId() != null && order.getAgentId() == a.getAgentId()) ? "selected" : ""%>>
								<%=esc(a.getName())%>
							</option>

							<%
							}
							}
							%>

						</select>

						<button class="btn ghost" type="submit">Assign agent</button>

					</form>

				</div>

				<p class="muted" style="margin-top: 12px; font-size: .85rem;">

					An order needs an assigned agent before it can be set to OUT FOR
					DELIVERY. The agent then sees it in their portal and marks it
					delivered.</p>

			</div>

			<%
			}
			} else {

			@SuppressWarnings("unchecked")
			List<AdminOrderRow> orders = (List<AdminOrderRow>) request.getAttribute("orders");

			String filter = (String) request.getAttribute("statusFilter");
			%>

			<h1 class="h1">Orders</h1>

			<p class="sub">Every order placed on Swizo. Open one to change
				its status or assign a delivery agent.</p>

			<div class="filters">

				<a href="<%=ctx%>/admin/orders"
					class="<%=filter == null ? "on" : ""%>"> All </a>

				<%
				for (String st : OrderStatus.ALL) {
				%>

				<a href="<%=ctx%>/admin/orders?status=<%=st%>"
					class="<%=st.equals(filter) ? "on" : ""%>"> <%=esc(lab(st))%>
				</a>

				<%
				}
				%>

			</div>

			<div class="card tbl">

				<table>

					<tr>
						<th>Order</th>
						<th>Date</th>
						<th>Customer</th>
						<th>Restaurant</th>
						<th>Total</th>
						<th>Payment</th>
						<th>Status</th>
						<th>Agent</th>
						<th></th>
					</tr>

					<%
					if (orders == null || orders.isEmpty()) {
					%>

					<tr>
						<td colspan="9" class="muted">No orders found.</td>
					</tr>

					<%
					} else {
					for (AdminOrderRow r : orders) {
					%>

					<tr>

						<td><b>#<%=r.getOrderId()%></b></td>

						<td><%=esc(fmt(r.getOrderDate()))%></td>

						<td><%=esc(r.getCustomerName())%></td>

						<td>

							<div class="restaurant-info">

								<img class="restaurant-thumb"
									src="<%=ctx%>/<%=esc(r.getRestaurantImage())%>"
									alt="<%=esc(r.getRestaurantName())%>"
									onerror="this.style.display='none'"> <span> <%=esc(r.getRestaurantName())%>
								</span>

							</div>

						</td>

						<td><b> &#8377;<%=String.format("%.2f", r.getTotalAmount())%>
						</b></td>

						<td><%=esc(r.getPaymentMode())%></td>

						<td><span class="pill <%=cls(r.getStatus())%>"> <%=esc(lab(r.getStatus()))%>
						</span></td>

						<td><%=r.getAgentName() == null ? "<span class='muted'>-</span>" : esc(r.getAgentName())%></td>

						<td><a class="link"
							href="<%=ctx%>/admin/orders?id=<%=r.getOrderId()%>"> View </a></td>

					</tr>

					<%
					}
					}
					%>

				</table>

			</div>

			<%
			}
			%>

		</main>

	</div>

</body>
</html>
