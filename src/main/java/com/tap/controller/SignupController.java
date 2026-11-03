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

@WebServlet("/signup")
public class SignupController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final UserDAO userDao = new UserDAOImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("/signup.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");

		String name = trim(req.getParameter("name"));
		String email = trim(req.getParameter("email")).toLowerCase();
		String phone = trim(req.getParameter("phone"));
		String password = req.getParameter("password") == null ? "" : req.getParameter("password");

		if (name.isEmpty() || name.length() > 45) {
			fail(req, resp, "Please enter your name (up to 45 characters).");
			return;
		}
		if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") || email.length() > 120) {
			fail(req, resp, "Please enter a valid email address.");
			return;
		}
		if (!phone.matches("\\d{10}")) {
			fail(req, resp, "Enter a valid 10-digit phone number.");
			return;
		}
		if (password.length() < 6) {
			fail(req, resp, "Password must be at least 6 characters.");
			return;
		}
		if (!PasswordUtil.fitsBcrypt(password)) {
			fail(req, resp, "Password is too long (maximum 72 characters).");
			return;
		}

		try {
			if (userDao.getByEmail(email) != null) {
				fail(req, resp, "An account with this email already exists. Please log in.");
				return;
			}

			User user = new User();
			user.setName(name);
			user.setEmail(email);
			user.setPhone(phone);
			user.setPassword(PasswordUtil.hash(password));
			user.setRole("customer"); // never take the role from the form

			int userId = userDao.addUser(user);
			if (userId <= 0) {
				fail(req, resp, "Registration failed. Please try again.");
				return;
			}

			// log the new user in
			user.setUserId(userId);
			user.setPassword(null); // never keep the hash in the session
			HttpSession session = req.getSession();
			req.changeSessionId(); // prevent session fixation
			session.setAttribute("user", user);

			resp.sendRedirect(req.getContextPath() + "/restaurant");

		} catch (RuntimeException e) {
			getServletContext().log("Signup failed", e);
			fail(req, resp, "Could not create your account. Please try again.");
		}
	}

	private static String trim(String s) {
		return s == null ? "" : s.trim();
	}

	private void fail(HttpServletRequest req, HttpServletResponse resp, String msg)
			throws ServletException, IOException {
		req.setAttribute("errorMessage", msg);
		req.getRequestDispatcher("/signup.jsp").forward(req, resp);
	}
}