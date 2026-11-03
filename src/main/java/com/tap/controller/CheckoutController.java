package com.tap.controller;

import java.io.IOException;
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

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Cash on Delivery checkout. Online (Razorpay) orders are created only by
 * RazorpayController.
 */
@WebServlet("/checkout")
public class CheckoutController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String CHECKOUT_PAGE = "/checkout.jsp";

	private final MenuDAO menuDao = new MenuDAOImpl();
	private final OrderDAO orderDao = new OrderDAOImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(false);
		String ctx = req.getContextPath();

		if (session == null || session.getAttribute("user") == null) {
			resp.sendRedirect(ctx + "/login");
			return;
		}

		Cart cart = (Cart) session.getAttribute("cart");

		if (cart == null || cart.isEmpty()) {
			resp.sendRedirect(ctx + "/cart");
			return;
		}

		req.getRequestDispatcher(CHECKOUT_PAGE).forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");

		HttpSession session = req.getSession(false);
		String ctx = req.getContextPath();

		User user = (session == null) ? null : (User) session.getAttribute("user");

		if (user == null) {
			resp.sendRedirect(ctx + "/login");
			return;
		}

		Cart cart = (Cart) session.getAttribute("cart");

		if (cart == null || cart.isEmpty()) {
			resp.sendRedirect(ctx + "/cart");
			return;
		}

		// Read checkout form values
		String fullName = trim(req.getParameter("fullName"));
		String phone = trim(req.getParameter("phone"));
		String address = trim(req.getParameter("address"));
		String city = trim(req.getParameter("city"));
		String pin = trim(req.getParameter("pin"));
		String pay = trim(req.getParameter("paymentMode"));

		// Validate checkout details
		String error = null;

		if (fullName.isEmpty()) {
			error = "Please enter your full name.";
		} else if (!phone.matches("\\d{10}")) {
			error = "Enter a valid 10-digit phone number.";
		} else if (address.isEmpty()) {
			error = "Please enter your delivery address.";
		} else if (city.isEmpty()) {
			error = "Please enter your city.";
		} else if (!pin.matches("\\d{6}")) {
			error = "Enter a valid 6-digit PIN code.";
		}

		if (error != null) {
			showCheckout(req, resp, error);
			return;
		}

		// online orders are created only by RazorpayController, after the payment is
		// verified
		if ("online".equalsIgnoreCase(pay)) {
			showCheckout(req, resp, "Online payment must be completed in the payment window. Please try again.");
			return;
		}

		synchronized (cart) {

			// Rebuild order items using current database prices
			List<OrderItem> items = new ArrayList<>();
			double subtotal = 0;

			for (CartItem ci : cart.getItems().values()) {

				Menu m = menuDao.getById(ci.getMenuId());

				if (m == null || !m.isAvailable() || m.getRestaurantId() != cart.getRestaurantId()) {

					session.setAttribute("cartMsg",
							"\"" + ci.getItemName() + "\" is no longer available. Please update your cart.");

					resp.sendRedirect(ctx + "/cart");
					return;
				}

				OrderItem oi = new OrderItem();

				oi.setMenuId(m.getMenuId());
				oi.setQuantity(ci.getQuantity());
				oi.setTotalAmount(m.getPrice() * ci.getQuantity());

				subtotal += oi.getTotalAmount();

				items.add(oi);
			}

			// Create order (Cash on Delivery)
			Order order = new Order();

			order.setRestaurantId(cart.getRestaurantId());
			order.setUserId(user.getUserId());
			order.setTotalAmount(subtotal + Cart.DELIVERY_FEE);
			order.setStatus("PLACED");
			order.setPaymentMode("Cash on Delivery");

			try {

				int orderId = orderDao.placeOrder(order, items);

				// Clear cart after successful order placement
				cart.clear();
				session.removeAttribute("cart");

				// Save order ID for processing and success pages
				session.setAttribute("lastOrderId", orderId);

				// Open animated order processing page
				resp.sendRedirect(ctx + "/orderProcessing");

			} catch (RuntimeException e) {

				getServletContext().log("Checkout failed", e);

				showCheckout(req, resp, "Failed to place order. Please try again.");
			}
		}
	}

	private void showCheckout(HttpServletRequest req, HttpServletResponse resp, String msg)
			throws ServletException, IOException {

		req.setAttribute("error", msg);
		req.setAttribute("errorMessage", msg);

		req.getRequestDispatcher(CHECKOUT_PAGE).forward(req, resp);
	}

	private static String trim(String s) {
		return s == null ? "" : s.trim();
	}
}