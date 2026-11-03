package com.tap.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import com.tap.DAO.DeliveryAgentDAO;
import com.tap.DAOImpl.DeliveryAgentDAOImpl;
import com.tap.model.DeliveryAgent;
import com.tap.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/agent/login")
public class AgentLoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String PAGE = "/WEB-INF/agent/login.jsp";

	private final DeliveryAgentDAO agentDao = new DeliveryAgentDAOImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession s = req.getSession(false);
		if (s != null && s.getAttribute("agent") != null) {
			resp.sendRedirect(req.getContextPath() + "/agent/dashboard");
			return;
		}
		req.getRequestDispatcher(PAGE).forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String email = req.getParameter("email") == null ? "" : req.getParameter("email").trim().toLowerCase();
		String password = req.getParameter("password") == null ? "" : req.getParameter("password");

		try {
			DeliveryAgent a = email.isEmpty() ? null : agentDao.getByEmail(email);
			boolean ok = false;

			if (a != null && a.isActive() && !password.isEmpty()) {
				String stored = a.getPassword();
				if (PasswordUtil.isHashed(stored)) {
					ok = PasswordUtil.verify(password, stored);
				} else if (stored != null && MessageDigest.isEqual(stored.getBytes(StandardCharsets.UTF_8),
						password.getBytes(StandardCharsets.UTF_8))) {
					// an old plain-text password: let the agent in once and store a proper hash
					ok = true;
					agentDao.updatePassword(a.getAgentId(), PasswordUtil.hash(password));
				}
			}

			if (!ok) {
				req.setAttribute("error", "Invalid email or password, or the account is inactive.");
				req.getRequestDispatcher(PAGE).forward(req, resp);
				return;
			}

			a.setPassword(null); // never keep the hash in the session
			HttpSession session = req.getSession();
			req.changeSessionId(); // prevent session fixation
			session.setAttribute("agent", a);
			resp.sendRedirect(req.getContextPath() + "/agent/dashboard");

		} catch (RuntimeException e) {
			getServletContext().log("Agent login failed", e);
			req.setAttribute("error", "Something went wrong. Please try again.");
			req.getRequestDispatcher(PAGE).forward(req, resp);
		}
	}
}