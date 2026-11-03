
package com.tap.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.tap.DAO.AdminDAO;
import com.tap.DAO.DeliveryAgentDAO;
import com.tap.DAO.FeedbackDAO;
import com.tap.DAO.OrderDAO;

import com.tap.DAOImpl.AdminDAOImpl;
import com.tap.DAOImpl.DeliveryAgentDAOImpl;
import com.tap.DAOImpl.FeedbackDAOImpl;
import com.tap.DAOImpl.OrderDAOImpl;

import com.tap.model.AdminOrderRow;
import com.tap.model.DeliveryAgent;
import com.tap.model.FeedbackView;
import com.tap.model.Order;
import com.tap.model.OrderItemView;
import com.tap.model.OrderView;

import com.tap.util.Csrf;
import com.tap.util.OrderStatus;
import com.tap.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Admin panel controller. Access is controlled by AdminFilter (/admin/*).
 */
@WebServlet({ "/admin", "/admin/dashboard", "/admin/orders", "/admin/agents", "/admin/feedback" })
public class AdminController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

	private final AdminDAO adminDao = new AdminDAOImpl();
	private final OrderDAO orderDao = new OrderDAOImpl();
	private final DeliveryAgentDAO agentDao = new DeliveryAgentDAOImpl();
	private final FeedbackDAO feedbackDao = new FeedbackDAOImpl();

	// ================= GET =================

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		switch (req.getServletPath()) {

		case "/admin/orders":
			showOrders(req, resp);
			break;

		case "/admin/agents":
			req.setAttribute("agents", agentDao.getAll());
			view(req, resp, "agents");
			break;

		case "/admin/feedback":

			List<FeedbackView> all = feedbackDao.getAllViews();

			req.setAttribute("feedbacks", all);
			req.setAttribute("average", feedbackDao.getAverageRating());

			view(req, resp, "feedback");
			break;

		// DASHBOARD
		default:

			req.setAttribute("restaurantCount", adminDao.countRestaurants());

			req.setAttribute("menuCount", adminDao.countMenuItems());

			req.setAttribute("orderCount", adminDao.countOrders());

			req.setAttribute("recent", adminDao.getOrders(null, 8));

			view(req, resp, "dashboard");
			break;
		}
	}

	private void showOrders(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String idParam = req.getParameter("id");

		if (idParam != null) {

			int id = num(idParam);

			AdminOrderRow row = id > 0 ? adminDao.getOrder(id) : null;

			if (row == null) {

				bad(req, "Order not found.");

				resp.sendRedirect(req.getContextPath() + "/admin/orders");

				return;
			}

			OrderView v = orderDao.getViewById(id);

			req.setAttribute("order", row);

			req.setAttribute("items", v == null ? new ArrayList<OrderItemView>() : v.getItems());

			req.setAttribute("agents", agentDao.getActive());

			view(req, resp, "orders");

			return;
		}

		String status = req.getParameter("status");

		if (!OrderStatus.isValid(status)) {
			status = null;
		}

		req.setAttribute("orders", adminDao.getOrders(status, 200));

		req.setAttribute("statusFilter", status);

		view(req, resp, "orders");
	}

	// ================= POST =================

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");

		if (!Csrf.valid(req)) {

			resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid request.");

			return;
		}

		switch (req.getServletPath()) {

		case "/admin/orders":
			changeOrder(req, resp);
			break;

		case "/admin/agents":
			changeAgents(req, resp);
			break;

		default:
			resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
	}

	// ================= ORDER MANAGEMENT =================

	private void changeOrder(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String ctx = req.getContextPath();

		int orderId = num(req.getParameter("orderId"));

		Order o = orderId > 0 ? orderDao.getById(orderId) : null;

		if (o == null) {

			bad(req, "Order not found.");

			resp.sendRedirect(ctx + "/admin/orders");

			return;
		}

		String back = ctx + "/admin/orders?id=" + orderId;

		if (OrderStatus.isFinal(o.getStatus())) {

			bad(req, "Order #" + orderId + " is already " + OrderStatus.label(o.getStatus()).toLowerCase()
					+ " and can't be changed.");

			resp.sendRedirect(back);

			return;
		}

		try {

			String action = trim(req.getParameter("action"));

			if ("status".equals(action)) {

				String status = trim(req.getParameter("status"));

				if (!OrderStatus.isValid(status)) {

					bad(req, "Choose a valid status.");

				} else if (OrderStatus.OUT_FOR_DELIVERY.equals(status) && o.getAgentId() == null) {

					bad(req, "Assign a delivery agent before sending the order out for delivery.");

				} else {

					orderDao.updateStatus(orderId, status);

					ok(req, "Order #" + orderId + " is now " + OrderStatus.label(status) + ".");
				}

			} else if ("assign".equals(action)) {

				int agentId = num(req.getParameter("agentId"));

				DeliveryAgent a = agentId > 0 ? agentDao.getById(agentId) : null;

				if (a == null || !a.isActive()) {

					bad(req, "Choose an active delivery agent.");

				} else {

					orderDao.assignAgent(orderId, agentId);

					ok(req, "Order #" + orderId + " assigned to " + a.getName() + ".");
				}
			}

		} catch (RuntimeException e) {

			getServletContext().log("Order update failed", e);

			bad(req, "Could not update the order. Check that the order.status ENUM contains every status value.");
		}

		resp.sendRedirect(back);
	}

	// ================= DELIVERY AGENT MANAGEMENT =================

	private void changeAgents(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		try {

			switch (trim(req.getParameter("action"))) {

			case "add":
				addAgent(req);
				break;

			case "toggle":
				toggleAgent(req);
				break;

			case "reset":
				resetAgentPassword(req);
				break;

			case "delete":
				deleteAgent(req);
				break;

			default:
				bad(req, "Unknown action.");
			}

		} catch (RuntimeException e) {

			getServletContext().log("Agent action failed", e);

			bad(req, "Something went wrong. Please try again.");
		}

		resp.sendRedirect(req.getContextPath() + "/admin/agents");
	}

	private void addAgent(HttpServletRequest req) {

		String name = trim(req.getParameter("name"));
		String phone = trim(req.getParameter("phone"));
		String email = trim(req.getParameter("email")).toLowerCase();

		String pw = req.getParameter("password") == null ? "" : req.getParameter("password");

		String error = null;

		if (name.isEmpty() || name.length() > 100) {

			error = "Enter the agent's name (up to 100 characters).";

		} else if (!phone.matches("\\d{10}")) {

			error = "Enter a valid 10-digit phone number.";

		} else if (!EMAIL.matcher(email).matches() || email.length() > 120) {

			error = "Enter a valid email address.";

		} else if (pw.length() < 6 || !PasswordUtil.fitsBcrypt(pw)) {

			error = "Password must be 6 to 72 characters.";

		} else if (agentDao.getByEmail(email) != null) {

			error = "A delivery agent with this email already exists.";
		}

		if (error != null) {

			bad(req, error);

			return;
		}

		DeliveryAgent a = new DeliveryAgent();

		a.setName(name);
		a.setPhone(phone);
		a.setEmail(email);
		a.setPassword(PasswordUtil.hash(pw));
		a.setActive(true);

		agentDao.addAgent(a);

		ok(req, name + " was added. They can log in at /agent/login.");
	}

	private void toggleAgent(HttpServletRequest req) {

		int id = num(req.getParameter("agentId"));

		DeliveryAgent a = id > 0 ? agentDao.getById(id) : null;

		if (a == null) {

			bad(req, "Agent not found.");

			return;
		}

		boolean on = "1".equals(req.getParameter("active"));

		agentDao.setActive(id, on);

		ok(req, a.getName() + (on ? " is now active." : " has been deactivated and can no longer log in."));
	}

	private void resetAgentPassword(HttpServletRequest req) {

		int id = num(req.getParameter("agentId"));

		DeliveryAgent a = id > 0 ? agentDao.getById(id) : null;

		String pw = req.getParameter("password") == null ? "" : req.getParameter("password");

		if (a == null) {

			bad(req, "Agent not found.");

		} else if (pw.length() < 6 || !PasswordUtil.fitsBcrypt(pw)) {

			bad(req, "Password must be 6 to 72 characters.");

		} else {

			agentDao.updatePassword(id, PasswordUtil.hash(pw));

			ok(req, "Password changed for " + a.getName() + ".");
		}
	}

	private void deleteAgent(HttpServletRequest req) {

		int id = num(req.getParameter("agentId"));

		DeliveryAgent a = id > 0 ? agentDao.getById(id) : null;

		if (a == null) {

			bad(req, "Agent not found.");

			return;
		}

		for (AdminOrderRow r : adminDao.getOrdersByAgent(id)) {

			if (!OrderStatus.isFinal(r.getStatus())) {

				bad(req, a.getName() + " still has orders in progress. Deactivate the agent instead.");

				return;
			}
		}

		agentDao.deleteAgent(id);

		ok(req, a.getName() + " was deleted.");
	}

	// ================= HELPERS =================

	private void view(HttpServletRequest req, HttpServletResponse resp, String page)
			throws ServletException, IOException {

		req.getRequestDispatcher("/WEB-INF/admin/" + page + ".jsp").forward(req, resp);
	}

	private static void ok(HttpServletRequest req, String msg) {

		req.getSession().setAttribute("adminFlash", msg);
	}

	private static void bad(HttpServletRequest req, String msg) {

		req.getSession().setAttribute("adminErr", msg);
	}

	private static int num(String s) {

		try {

			return Integer.parseInt(s == null ? "" : s.trim());

		} catch (NumberFormatException e) {

			return -1;
		}
	}

	private static String trim(String s) {

		return s == null ? "" : s.trim();
	}
}
