package com.tap.controller;

import java.io.IOException;

import com.tap.DAO.OrderDAO;
import com.tap.DAOImpl.OrderDAOImpl;
import com.tap.model.Order;
import com.tap.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/order-success")
public class OrderSuccessController extends HttpServlet {
	private final OrderDAO orderDao = new OrderDAOImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		String ctx = req.getContextPath();

		User user = session == null ? null : (User) session.getAttribute("user");
		Integer orderId = session == null ? null : (Integer) session.getAttribute("lastOrderId");
		if (user == null || orderId == null) {
			resp.sendRedirect(ctx + "/orders");
			return;
		}

		Order order = orderDao.getById(orderId);
		if (order == null || order.getUserId() != user.getUserId()) { // only the buyer sees this page
			resp.sendRedirect(ctx + "/orders");
			return;
		}
		session.removeAttribute("lastOrderId"); // refreshing this page later goes to My Orders
		req.setAttribute("orderId", orderId);
		req.getRequestDispatcher("/order-success.jsp").forward(req, resp);
	}
}