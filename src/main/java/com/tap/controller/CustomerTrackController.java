
package com.tap.controller;

import java.io.IOException;

import com.tap.DAOImpl.CustomerTrackingDAOImpl;
import com.tap.model.TrackInfo;
import com.tap.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/track")
public class CustomerTrackController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final CustomerTrackingDAOImpl trackingDAO = new CustomerTrackingDAOImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setCharacterEncoding("UTF-8");

		String contextPath = req.getContextPath();

		// Get the existing session without creating a new one.
		HttpSession session = req.getSession(false);

		System.out.println("========== TRACK DEBUG ==========");
		System.out.println("Track URL: " + req.getRequestURL());
		System.out.println("Session exists: " + (session != null));

		if (session != null) {
			System.out.println("Session ID: " + session.getId());
			System.out.println("user attribute exists: " + (session.getAttribute("user") != null));
			System.out.println("loggedInUser attribute exists: " + (session.getAttribute("loggedInUser") != null));
		}

		// Support both session attribute names.
		User user = null;

		if (session != null) {

			Object loggedInUser = session.getAttribute("loggedInUser");
			Object sessionUser = session.getAttribute("user");

			if (loggedInUser instanceof User) {
				user = (User) loggedInUser;
			} else if (sessionUser instanceof User) {
				user = (User) sessionUser;
			}
		}

		// Redirect only if no valid logged-in user exists.
		if (user == null) {
			resp.sendRedirect(contextPath + "/login");
			return;
		}

		// Validate order ID.
		String orderIdParam = req.getParameter("orderId");

		if (orderIdParam == null || orderIdParam.trim().isEmpty()) {
			resp.sendRedirect(contextPath + "/orders");
			return;
		}

		int orderId;

		try {
			orderId = Integer.parseInt(orderIdParam.trim());

			if (orderId <= 0) {
				resp.sendRedirect(contextPath + "/orders");
				return;
			}

		} catch (NumberFormatException e) {
			resp.sendRedirect(contextPath + "/orders");
			return;
		}

		// Fetch tracking information for this customer and order.
		TrackInfo info = trackingDAO.getTracking(orderId, user.getUserId());

		if (info == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Order tracking information was not found.");
			return;
		}

		req.setAttribute("info", info);

		req.getRequestDispatcher("/WEB-INF/views/customer/track.jsp").forward(req, resp);
	}
}
