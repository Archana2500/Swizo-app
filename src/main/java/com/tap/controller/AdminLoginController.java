
package com.tap.controller;

import java.io.IOException;

import com.tap.DAO.UserDAO;
import com.tap.DAOImpl.UserDAOImpl;
import com.tap.model.User;
import com.tap.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/login")
public class AdminLoginController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String LOGIN_PAGE = "/WEB-INF/admin/login.jsp";

	private final UserDAO userDao = new UserDAOImpl();

	// Display admin login page
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(false);

		if (session != null) {
			User loggedInUser = (User) session.getAttribute("loggedInUser");

			if (isAdmin(loggedInUser)) {
				resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
				return;
			}
		}

		req.getRequestDispatcher(LOGIN_PAGE).forward(req, resp);
	}

	// Process admin login
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");

		String email = trim(req.getParameter("email")).toLowerCase();
		String password = req.getParameter("password");

		if (email.isEmpty() || password == null || password.isEmpty()) {
			showError(req, resp, "Please enter your email and password.");
			return;
		}

		try {

			User user = userDao.getByEmail(email);

			// Check user existence and admin role
			if (!isAdmin(user)) {
				showError(req, resp, "Invalid admin email or password.");
				return;
			}

			// Verify BCrypt password
			if (!PasswordUtil.verify(password, user.getPassword())) {
				showError(req, resp, "Invalid admin email or password.");
				return;
			}

			// Update last login date
			userDao.updateLastLogin(user.getUserId());

			// Prevent session fixation
			HttpSession oldSession = req.getSession(false);

			if (oldSession != null) {
				oldSession.invalidate();
			}

			// Create a fresh admin session
			HttpSession session = req.getSession(true);

			session.setAttribute("loggedInUser", user);
			session.setAttribute("adminLoggedIn", true);

			// Redirect to admin dashboard
			resp.sendRedirect(req.getContextPath() + "/admin/dashboard");

		} catch (RuntimeException e) {

			getServletContext().log("Admin login failed", e);

			showError(req, resp, "Unable to log in right now. Please try again.");
		}
	}

	private boolean isAdmin(User user) {

		if (user == null || user.getRole() == null) {
			return false;
		}

		String role = user.getRole();

		return "admin".equalsIgnoreCase(role) || "superadmin".equalsIgnoreCase(role);
	}

	private void showError(HttpServletRequest req, HttpServletResponse resp, String message)
			throws ServletException, IOException {

		req.setAttribute("error", message);

		req.getRequestDispatcher(LOGIN_PAGE).forward(req, resp);
	}

	private static String trim(String value) {
		return value == null ? "" : value.trim();
	}
}
