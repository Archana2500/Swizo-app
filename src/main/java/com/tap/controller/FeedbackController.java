package com.tap.controller;

import java.io.IOException;

import com.tap.DAO.FeedbackDAO;
import com.tap.DAO.OrderDAO;
import com.tap.DAO.RestaurantDAO;
import com.tap.DAOImpl.FeedbackDAOImpl;
import com.tap.DAOImpl.OrderDAOImpl;
import com.tap.DAOImpl.RestaurantDAOImpl;
import com.tap.model.Feedback;
import com.tap.model.Order;
import com.tap.model.OrderView;
import com.tap.model.User;
import com.tap.util.Csrf;
import com.tap.util.OrderStatus;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * GET /feedback?orderId=N shows the rating form; POST saves it. Only for your
 * own delivered orders.
 */
@WebServlet("/feedback")
public class FeedbackController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final FeedbackDAO feedbackDao = new FeedbackDAOImpl();
	private final OrderDAO orderDao = new OrderDAOImpl();
	private final RestaurantDAO restaurantDao = new RestaurantDAOImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		User user = currentUser(req);
		if (user == null) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}
		int orderId = num(req.getParameter("orderId"));
		show(req, resp, user, orderId, null);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		User user = currentUser(req);
		if (user == null) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}
		if (!Csrf.valid(req)) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid request.");
			return;
		}

		int orderId = num(req.getParameter("orderId"));
		int rating = num(req.getParameter("rating"));
		String comment = req.getParameter("comment") == null ? "" : req.getParameter("comment").trim();

		Order o = orderId > 0 ? orderDao.getById(orderId) : null;
		if (o == null || o.getUserId() != user.getUserId()) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		if (!OrderStatus.DELIVERED.equals(o.getStatus())) {
			resp.sendRedirect(req.getContextPath() + "/orders");
			return;
		}
		if (feedbackDao.getByOrder(orderId) != null) { // already rated
			resp.sendRedirect(req.getContextPath() + "/orders?rated=1");
			return;
		}
		if (rating < 1 || rating > 5) {
			show(req, resp, user, orderId, "Please choose a rating from 1 to 5 stars.");
			return;
		}
		if (comment.length() > 500) {
			show(req, resp, user, orderId, "Your comment is too long (maximum 500 characters).");
			return;
		}

		try {
			Feedback f = new Feedback();
			f.setOrderId(orderId);
			f.setUserId(user.getUserId());
			f.setRestaurantId(o.getRestaurantId()); // taken from the order, not from the form
			f.setRating(rating);
			f.setComment(comment.isEmpty() ? null : comment);
			feedbackDao.addFeedback(f);
			restaurantDao.refreshRating(o.getRestaurantId());
			resp.sendRedirect(req.getContextPath() + "/orders?rated=1");
		} catch (RuntimeException e) {
			getServletContext().log("Feedback failed", e);
			show(req, resp, user, orderId, "Could not save your feedback. Please try again.");
		}
	}

	private void show(HttpServletRequest req, HttpServletResponse resp, User user, int orderId, String error)
			throws ServletException, IOException {
		Order o = orderId > 0 ? orderDao.getById(orderId) : null;
		if (o == null || o.getUserId() != user.getUserId()) { // can only open your own orders
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		Feedback existing = feedbackDao.getByOrder(orderId);
		if (existing == null && !OrderStatus.DELIVERED.equals(o.getStatus())) {
			resp.sendRedirect(req.getContextPath() + "/orders"); // not delivered yet: nothing to rate
			return;
		}
		OrderView view = orderDao.getViewById(orderId);
		req.setAttribute("order", view);
		req.setAttribute("feedback", existing);
		req.setAttribute("csrf", Csrf.token(req.getSession()));
		req.setAttribute("error", error);
		req.getRequestDispatcher("/feedback.jsp").forward(req, resp);
	}

	private static User currentUser(HttpServletRequest req) {
		HttpSession s = req.getSession(false);
		return s == null ? null : (User) s.getAttribute("user");
	}

	private static int num(String s) {
		try {
			return Integer.parseInt(s == null ? "" : s.trim());
		} catch (NumberFormatException e) {
			return -1;
		}
	}
}