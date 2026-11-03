
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

@WebServlet("/placeOrder")
public class PlaceOrderController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final MenuDAO menuDao = new MenuDAOImpl();
	private final OrderDAO orderDao = new OrderDAOImpl();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(false);
		String ctx = req.getContextPath();

		// Check logged-in customer
		User user = session == null ? null : (User) session.getAttribute("loggedInUser");

		if (user == null) {
			resp.sendRedirect(ctx + "/login");
			return;
		}

		// Get cart
		Cart cart = (Cart) session.getAttribute("cart");

		if (cart == null || cart.isEmpty()) {
			resp.sendRedirect(ctx + "/cart");
			return;
		}

		// Checkout form fields
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

		// Rebuild order items using current database prices
		List<OrderItem> items = new ArrayList<>();
		double subtotal = 0;

		// IMPORTANT: getItems() returns a Map.
		// Therefore, use values() to access CartItem objects.
		for (CartItem ci : cart.getItems().values()) {

			Menu menu = menuDao.getById(ci.getMenuId());

			if (menu == null || !menu.isAvailable() || menu.getRestaurantId() != cart.getRestaurantId()) {

				session.setAttribute("cartMsg",
						"\"" + ci.getItemName() + "\" is no longer available. Please update your cart.");

				resp.sendRedirect(ctx + "/cart");
				return;
			}

			int quantity = ci.getQuantity();

			if (quantity < 1 || quantity > 20) {
				showCheckout(req, resp, "Invalid item quantity in your cart.");
				return;
			}

			OrderItem orderItem = new OrderItem();

			orderItem.setMenuId(menu.getMenuId());
			orderItem.setQuantity(quantity);

			double itemTotal = menu.getPrice() * quantity;

			orderItem.setTotalAmount(itemTotal);

			subtotal += itemTotal;
			items.add(orderItem);
		}

		// Create order
		Order order = new Order();

		order.setRestaurantId(cart.getRestaurantId());
		order.setUserId(user.getUserId());
		order.setTotalAmount(subtotal + Cart.DELIVERY_FEE);
		order.setStatus("PLACED");

		if ("online".equalsIgnoreCase(pay)) {
			order.setPaymentMode("Online (Razorpay)");
		} else {
			order.setPaymentMode("Cash on Delivery");
		}

		// Save order
		try {

			int orderId = orderDao.placeOrder(order, items);

			if (orderId <= 0) {
				showCheckout(req, resp, "Could not place your order. Please try again.");
				return;
			}

			cart.clear();

			session.setAttribute("lastOrderId", orderId);

			// Show order processing video first
			resp.sendRedirect(ctx + "/orderProcessing");

		} catch (RuntimeException e) {

			getServletContext().log("placeOrder failed", e);

			showCheckout(req, resp, "Could not place your order. Please try again.");
		}
	}

	private void showCheckout(HttpServletRequest req, HttpServletResponse resp, String msg)
			throws ServletException, IOException {

		req.setAttribute("error", msg);

		req.getRequestDispatcher("/checkout.jsp").forward(req, resp);
	}

	private static String trim(String value) {
		return value == null ? "" : value.trim();
	}
}
