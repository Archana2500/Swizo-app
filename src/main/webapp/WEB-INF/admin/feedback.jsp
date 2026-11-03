<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List,com.tap.model.FeedbackView"%>
<%!private static String esc(String s) {
		if (s == null)
			return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'",
				"&#39;");
	}

	private static String fmt(java.util.Date d) {
		return d == null ? "-" : new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a").format(d);
	}

	private static String stars(int n) {
		StringBuilder b = new StringBuilder();
		for (int i = 1; i <= 5; i++)
			b.append(i <= n ? "&#9733;" : "&#9734;");
		return b.toString();
	}%>
<%
String ctx = request.getContextPath();
@SuppressWarnings("unchecked")
List<FeedbackView> feedbacks = (List<FeedbackView>) request.getAttribute("feedbacks");
Double avg = (Double) request.getAttribute("average");
int total = (feedbacks == null) ? 0 : feedbacks.size();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Feedback | Swizo Admin</title>
<link rel="stylesheet" href="<%=ctx%>/css/admin.css">
</head>
<body>
	<div class="shell">

		<jsp:include page="/WEB-INF/admin/sidebar.jsp">
			<jsp:param name="active" value="feedback" />
		</jsp:include>

		<main class="main">
			<jsp:include page="/WEB-INF/admin/messages.jsp" />

			<h1 class="h1">Feedback</h1>
			<p class="sub">What customers say about their delivered orders.</p>

			<div class="stats" style="margin-bottom: 24px">
				<div class="stat">
					<span>Reviews</span><b><%=total%></b>
				</div>
				<div class="stat">
					<span>Average rating</span> <b><%=total == 0 ? "-" : String.format("%.1f", avg == null ? 0.0 : avg)%>
						<span class="stars" style="font-size: 1.2rem">&#9733;</span></b>
				</div>
			</div>

			<div class="card tbl">
				<table>
					<tr>
						<th>Date</th>
						<th>Order</th>
						<th>Customer</th>
						<th>Restaurant</th>
						<th>Rating</th>
						<th>Comment</th>
					</tr>
					<%
					if (feedbacks == null || feedbacks.isEmpty()) {
					%>
					<tr>
						<td colspan="6" class="muted">No feedback yet.</td>
					</tr>
					<%
					} else {
					for (FeedbackView f : feedbacks) {
					%>
					<tr>
						<td><%=esc(fmt(f.getCreatedDate()))%></td>
						<td><a class="link"
							href="<%=ctx%>/admin/orders?id=<%=f.getOrderId()%>">#<%=f.getOrderId()%></a></td>
						<td><%=esc(f.getCustomerName())%></td>
						<td>

							<div class="restaurant-info">
								<img src="<%=ctx%>/<%=esc(f.getRestaurantImage())%>"
									alt="<%=esc(f.getRestaurantName())%>" class="restaurant-thumb"
									onerror="this.style.display='none'"> <span><%=esc(f.getRestaurantName())%></span>
							</div>

						</td>
						<td><span class="stars"><%=stars(f.getRating())%></span></td>
						<td style="max-width: 360px; overflow-wrap: anywhere;"><%=f.getComment() == null ? "<span class='muted'>-</span>" : esc(f.getComment())%></td>
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