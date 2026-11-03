package com.tap.controller;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.tap.DAO.MenuDAO;
import com.tap.DAO.OrderDAO;
import com.tap.DAOImpl.MenuDAOImpl;
import com.tap.DAOImpl.OrderDAOImpl;
import com.tap.model.Cart;
import com.tap.model.CartItem;
import com.tap.model.Menu;
import com.tap.model.Order;
import com.tap.model.OrderItem;
import com.tap.model.User;
import com.tap.util.RazorpayUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * POST /razorpay action=create (start a payment) | action=verify (confirm it
 * and save the order)
 */
@WebServlet("/razorpay")
public class RazorpayController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String K_PENDING = "rzpPending";
	private static final String STATUS_PAID = "PAID"; // must match a value of your order.status ENUM
	private static final long PENDING_TTL_MS = 30 * 60 * 1000L;

	private final MenuDAO menuDao = new MenuDAOImpl();
	private final OrderDAO orderDao = new OrderDAOImpl();

	/**
	 * what the customer is paying for, frozen at the moment the Razorpay order was
	 * created
	 */
	private static class Pending implements Serializable {
		private static final long serialVersionUID = 1L;
		String rzpOrderId;
		long amountPaise;
		double total;
		int restaurantId;
		long createdAt;
		boolean done;
		int[] menuIds, quantities;
		double[] lineTotals;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("Cache-Control", "no-store");

		// only our own page (fetch with this header) may call this; a form on another
		// site cannot
		if (!"fetch".equals(req.getHeader("X-Requested-With"))) {
			fail(resp, 400, "Bad request.");
			return;
		}

		HttpSession session = req.getSession(false);
		User user = (session == null) ? null : (User) session.getAttribute("user");
		if (user == null) {
			send(resp, 401, "{\"ok\":false,\"message\":\"Please log in again.\",\"redirect\":\""
					+ js(req.getContextPath() + "/login") + "\"}");
			return;
		}

		String action = req.getParameter("action");
		if ("create".equals(action)) {
			create(req, resp, session, user);
		} else if ("verify".equals(action)) {
			verify(req, resp, session, user);
		} else {
			fail(resp, 400, "Unknown action.");
		}
	}

	// ---------- step 1: start the payment ----------
	private void create(HttpServletRequest req, HttpServletResponse resp, HttpSession session, User user)
			throws IOException {
		if (!RazorpayUtil.isConfigured()) {
			fail(resp, 503, "Online payment is not available right now. Please choose Cash on Delivery.");
			return;
		}
		Cart cart = (Cart) session.getAttribute("cart");
		if (cart == null || cart.isEmpty()) {
			fail(resp, 400, "Your cart is empty.");
			return;
		}

		String fullName = trim(req.getParameter("fullName"));
		String phone = trim(req.getParameter("phone"));
		String address = trim(req.getParameter("address"));
		String city = trim(req.getParameter("city"));
		String pin = trim(req.getParameter("pin"));
		String error = null;
		if (fullName.isEmpty())
			error = "Please enter your full name.";
		else if (!phone.matches("\\d{10}"))
			error = "Enter a valid 10-digit phone number.";
		else if (address.isEmpty())
			error = "Please enter your delivery address.";
		else if (city.isEmpty())
			error = "Please enter your city.";
		else if (!pin.matches("\\d{6}"))
			error = "Enter a valid 6-digit PIN code.";
		if (error != null) {
			fail(resp, 400, error);
			return;
		}

		Pending p = new Pending();
		synchronized (cart) {
			int n = cart.getItems().size();
			p.menuIds = new int[n];
			p.quantities = new int[n];
			p.lineTotals = new double[n];
			int i = 0;
			double subtotal = 0;
			// prices always come from the database, never from the browser or the session
			// cart
			for (CartItem ci : cart.getItems().values()) {
				Menu m = menuDao.getById(ci.getMenuId());
				if (m == null || !m.isAvailable() || m.getRestaurantId() != cart.getRestaurantId()) {
					fail(resp, 400, "\"" + ci.getItemName() + "\" is no longer available. Please update your cart.");
					return;
				}
				p.menuIds[i] = m.getMenuId();
				p.quantities[i] = ci.getQuantity();
				p.lineTotals[i] = m.getPrice() * ci.getQuantity();
				subtotal += p.lineTotals[i];
				i++;
			}
			p.restaurantId = cart.getRestaurantId();
			p.total = subtotal + Cart.DELIVERY_FEE;
		}
		p.amountPaise = BigDecimal.valueOf(p.total).movePointRight(2).setScale(0, RoundingMode.HALF_UP)
				.longValueExact();
		if (p.amountPaise < 100) {
			fail(resp, 400, "The order total is too small for online payment.");
			return;
		}

		try {
			p.rzpOrderId = RazorpayUtil.createOrder(p.amountPaise,
					"swz_" + user.getUserId() + "_" + System.currentTimeMillis());
		} catch (IOException | RuntimeException e) {
			getServletContext().log("Razorpay order creation failed", e);
			fail(resp, 502, "We couldn't start the payment. Please try again or choose Cash on Delivery.");
			return;
		}
		p.createdAt = System.currentTimeMillis();
		session.setAttribute(K_PENDING, p);

		send(resp, 200,
				"{\"ok\":true,\"keyId\":\"" + js(RazorpayUtil.keyId()) + "\",\"orderId\":\"" + js(p.rzpOrderId)
						+ "\",\"amount\":" + p.amountPaise + ",\"currency\":\"INR\",\"name\":\"" + js(fullName)
						+ "\",\"email\":\"" + js(user.getEmail()) + "\",\"contact\":\"" + js(phone) + "\"}");
	}

	// ---------- step 2: confirm the payment and save the order ----------
	private void verify(HttpServletRequest req, HttpServletResponse resp, HttpSession session, User user)
			throws IOException {
		Pending p = (Pending) session.getAttribute(K_PENDING);
		if (p == null || System.currentTimeMillis() - p.createdAt > PENDING_TTL_MS) {
			session.removeAttribute(K_PENDING);
			fail(resp, 400, "This payment session has expired. If money was deducted, check My Orders "
					+ "or contact support before paying again.");
			return;
		}

		String orderIdFromBrowser = trim(req.getParameter("razorpay_order_id"));
		String paymentId = trim(req.getParameter("razorpay_payment_id"));
		String signature = trim(req.getParameter("razorpay_signature"));

		// use the order id WE stored, as Razorpay's guide requires
		if (!p.rzpOrderId.equals(orderIdFromBrowser)
				|| !RazorpayUtil.verifySignature(p.rzpOrderId, paymentId, signature)) {
			getServletContext()
					.log("Razorpay signature check FAILED for user " + user.getUserId() + ", order " + p.rzpOrderId);
			fail(resp, 400, "We couldn't verify your payment. If money was deducted, please contact support.");
			return;
		}

		String ctx = req.getContextPath();
		synchronized (p) {
			if (p.done) { // a second callback for the same payment
				send(resp, 200, "{\"ok\":true,\"redirect\":\"" + js(ctx + "/orders") + "\"}");
				return;
			}

			Order order = new Order();
			order.setRestaurantId(p.restaurantId);
			order.setUserId(user.getUserId());
			order.setTotalAmount(p.total);
			order.setStatus(STATUS_PAID);
			order.setPaymentMode("Online (Razorpay)");

			List<OrderItem> items = new ArrayList<>();
			for (int i = 0; i < p.menuIds.length; i++) {
				OrderItem oi = new OrderItem();
				oi.setMenuId(p.menuIds[i]);
				oi.setQuantity(p.quantities[i]);
				oi.setTotalAmount(p.lineTotals[i]);
				items.add(oi);
			}

			try {
				int orderId = orderDao.placeOrder(order, items);
				p.done = true;
				session.removeAttribute(K_PENDING);

				Cart cart = (Cart) session.getAttribute("cart");
				if (cart != null) {
					synchronized (cart) {
						cart.clear();
					}
				}
				session.removeAttribute("cart");
				session.setAttribute("lastOrderId", orderId);

				getServletContext().log("Razorpay payment " + paymentId + " saved as order #" + orderId);
				send(resp, 200, "{\"ok\":true,\"redirect\":\"" + js(ctx + "/orderProcessing") + "\"}");
			} catch (RuntimeException e) {
				// money was taken but the order was not saved: the log line below is how you
				// find and refund it
				getServletContext().log("PAID BUT NOT SAVED: razorpay payment " + paymentId + " order " + p.rzpOrderId
						+ " user " + user.getUserId(), e);
				fail(resp, 500, "Your payment was received, but we couldn't save your order. Please don't pay "
						+ "again - contact support with payment id " + paymentId + ".");
			}
		}
	}

	// ---------- helpers ----------
	private void fail(HttpServletResponse resp, int status, String message) throws IOException {
		send(resp, status, "{\"ok\":false,\"message\":\"" + js(message) + "\"}");
	}

	private void send(HttpServletResponse resp, int status, String json) throws IOException {
		resp.setStatus(status);
		resp.getWriter().write(json);
	}

	private static String trim(String s) {
		return s == null ? "" : s.trim();
	}

	/** escapes text for use inside a JSON string */
	private static String js(String s) {
		if (s == null) {
			return "";
		}
		StringBuilder b = new StringBuilder();
		for (char c : s.toCharArray()) {
			switch (c) {
			case '"':
				b.append("\\\"");
				break;
			case '\\':
				b.append("\\\\");
				break;
			case '\n':
				b.append("\\n");
				break;
			case '\r':
				b.append("\\r");
				break;
			case '\t':
				b.append("\\t");
				break;
			case '<':
				b.append("\\u003c");
				break;
			default:
				if (c < 0x20) {
					b.append(String.format("\\u%04x", (int) c));
				} else {
					b.append(c);
				}
			}
		}
		return b.toString();
	}
}