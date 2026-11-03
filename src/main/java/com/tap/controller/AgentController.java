package com.tap.controller;

import java.io.IOException;

import com.tap.DAO.AdminDAO;
import com.tap.DAO.DeliveryAgentDAO;
import com.tap.DAO.OrderDAO;
import com.tap.DAOImpl.AdminDAOImpl;
import com.tap.DAOImpl.DeliveryAgentDAOImpl;
import com.tap.DAOImpl.OrderDAOImpl;
import com.tap.model.DeliveryAgent;
import com.tap.model.Order;
import com.tap.util.Csrf;
import com.tap.util.OrderStatus;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** The delivery agent's pages. AgentFilter makes sure an agent is logged in. */
@WebServlet({ "/agent/dashboard", "/agent/deliver", "/agent/logout" })
public class AgentController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final AdminDAO adminDao = new AdminDAOImpl();
	private final OrderDAO orderDao = new OrderDAOImpl();
	private final DeliveryAgentDAO agentDao = new DeliveryAgentDAOImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String ctx = req.getContextPath();
		HttpSession s = req.getSession(false);

		if ("/agent/logout".equals(req.getServletPath())) {
			if (s != null) {
				s.removeAttribute("agent"); // only the agent login: a customer login in the same browser stays
			}
			resp.sendRedirect(ctx + "/agent/login");
			return;
		}
		if (!"/agent/dashboard".equals(req.getServletPath())) {
			resp.sendRedirect(ctx + "/agent/dashboard");
			return;
		}

		DeliveryAgent sessionAgent = (s == null) ? null : (DeliveryAgent) s.getAttribute("agent");
		DeliveryAgent agent = (sessionAgent == null) ? null : agentDao.getById(sessionAgent.getAgentId());
		if (agent == null || !agent.isActive()) { // removed or deactivated while logged in
			if (s != null) {
				s.removeAttribute("agent");
			}
			resp.sendRedirect(ctx + "/agent/login");
			return;
		}

		req.setAttribute("agentName", agent.getName());
		req.setAttribute("orders", adminDao.getOrdersByAgent(agent.getAgentId()));
		req.setAttribute("csrf", Csrf.token(s));
		req.setAttribute("flash", s.getAttribute("agentFlash"));
		req.setAttribute("err", s.getAttribute("agentErr"));
		s.removeAttribute("agentFlash");
		s.removeAttribute("agentErr");
		req.getRequestDispatcher("/WEB-INF/agent/dashboard.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		HttpSession s = req.getSession(false);
		DeliveryAgent agent = (s == null) ? null : (DeliveryAgent) s.getAttribute("agent");
		if (agent == null || !"/agent/deliver".equals(req.getServletPath())) {
			resp.sendRedirect(req.getContextPath() + "/agent/login");
			return;
		}
		if (!Csrf.valid(req)) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid request.");
			return;
		}

		int orderId = -1;
		try {
			orderId = Integer.parseInt(req.getParameter("orderId").trim());
		} catch (RuntimeException e) {
			// stays -1 and is rejected below
		}

		try {
			Order o = orderId > 0 ? orderDao.getById(orderId) : null;
			if (o == null || o.getAgentId() == null || o.getAgentId() != agent.getAgentId()) {
				s.setAttribute("agentErr", "That order is not assigned to you.");
			} else if (!OrderStatus.OUT_FOR_DELIVERY.equals(o.getStatus())) {
				s.setAttribute("agentErr", "Order #" + orderId + " is not out for delivery.");
			} else {
				orderDao.updateStatus(orderId, OrderStatus.DELIVERED);
				s.setAttribute("agentFlash", "Order #" + orderId + " marked as delivered.");
			}
		} catch (RuntimeException e) {
			getServletContext().log("Mark delivered failed", e);
			s.setAttribute("agentErr", "Could not update the order. Please try again.");
		}
		resp.sendRedirect(req.getContextPath() + "/agent/dashboard");
	}
}