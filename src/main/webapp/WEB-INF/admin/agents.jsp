<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="java.util.List,com.tap.model.DeliveryAgent,com.tap.util.Csrf"%>
<%!private static String esc(String s) {
		if (s == null)
			return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'",
				"&#39;");
	}%>
<%
String ctx = request.getContextPath();
String csrf = Csrf.token(session);
@SuppressWarnings("unchecked")
List<DeliveryAgent> agents = (List<DeliveryAgent>) request.getAttribute("agents");
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Delivery agents | Swizo Admin</title>
<link rel="stylesheet" href="<%=ctx%>/css/admin.css">
</head>
<body>
	<div class="shell">

		<jsp:include page="/WEB-INF/admin/sidebar.jsp">
			<jsp:param name="active" value="agents" />
		</jsp:include>

		<main class="main">
			<jsp:include page="/WEB-INF/admin/messages.jsp" />

			<h1 class="h1">Delivery agents</h1>
			<p class="sub">
				Add the people who deliver orders. They log in at <b><%=ctx%>/agent/login</b>
				with the email and password you set here.
			</p>

			<section class="card">
				<h2 class="h2" style="margin-top: 0">Add a delivery agent</h2>
				<form method="post" action="<%=ctx%>/admin/agents" class="grid4">
					<input type="hidden" name="csrf" value="<%=esc(csrf)%>"> <input
						type="hidden" name="action" value="add"> <input
						name="name" placeholder="Full name" required maxlength="100">
					<input name="phone" placeholder="10-digit phone" required
						pattern="[0-9]{10}" maxlength="10" inputmode="numeric"> <input
						type="email" name="email" placeholder="Email (their login)"
						required maxlength="120"> <input type="password"
						name="password" placeholder="Password (min 6)" required
						minlength="6" maxlength="72" autocomplete="new-password">
					<button class="btn" type="submit">Add agent</button>
				</form>
			</section>

			<div class="card tbl">
				<table>
					<tr>
						<th>Name</th>
						<th>Email</th>
						<th>Phone</th>
						<th>Status</th>
						<th>Actions</th>
					</tr>
					<%
					if (agents == null || agents.isEmpty()) {
					%>
					<tr>
						<td colspan="5" class="muted">No delivery agents yet. Add the
							first one above.</td>
					</tr>
					<%
					} else {
					for (DeliveryAgent a : agents) {
					%>
					<tr>
						<td><b><%=esc(a.getName())%></b></td>
						<td><%=esc(a.getEmail())%></td>
						<td><%=esc(a.getPhone())%></td>
						<td><span class="pill <%=a.isActive() ? "on" : "off"%>"><%=a.isActive() ? "Active" : "Inactive"%></span></td>
						<td>
							<div class="acts">
								<form method="post" action="<%=ctx%>/admin/agents"
									class="inline">
									<input type="hidden" name="csrf" value="<%=esc(csrf)%>">
									<input type="hidden" name="action" value="toggle"> <input
										type="hidden" name="agentId" value="<%=a.getAgentId()%>">
									<input type="hidden" name="active"
										value="<%=a.isActive() ? "0" : "1"%>">
									<button class="btn ghost small" type="submit"><%=a.isActive() ? "Deactivate" : "Activate"%></button>
								</form>
								<form method="post" action="<%=ctx%>/admin/agents"
									class="inline"
									onsubmit="return confirm('Delete this delivery agent?');">
									<input type="hidden" name="csrf" value="<%=esc(csrf)%>">
									<input type="hidden" name="action" value="delete"> <input
										type="hidden" name="agentId" value="<%=a.getAgentId()%>">
									<button class="btn danger small" type="submit">Delete</button>
								</form>
								<details>
									<summary>Reset password</summary>
									<form method="post" action="<%=ctx%>/admin/agents">
										<input type="hidden" name="csrf" value="<%=esc(csrf)%>">
										<input type="hidden" name="action" value="reset"> <input
											type="hidden" name="agentId" value="<%=a.getAgentId()%>">
										<input type="password" name="password"
											placeholder="New password" required minlength="6"
											maxlength="72" autocomplete="new-password">
										<button class="btn small" type="submit">Save</button>
									</form>
								</details>
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