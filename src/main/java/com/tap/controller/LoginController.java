
package com.tap.controller;

import java.io.IOException;
import java.security.MessageDigest;

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

@WebServlet("/login")
public class LoginController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final UserDAO userDao = new UserDAOImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.getRequestDispatcher("/login.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");

		String email = req.getParameter("email");
		String password = req.getParameter("password");

		email = (email == null) ? "" : email.trim().toLowerCase();
		password = (password == null) ? "" : password;

		try {
			User user = email.isEmpty() ? null : userDao.getByEmail(email);

			boolean validLogin = false;

			if (user != null && !password.isEmpty()) {

				String storedPassword = user.getPassword();

				if (PasswordUtil.isHashed(storedPassword)) {

					validLogin = PasswordUtil.verify(password, storedPassword);

				} else if (storedPassword != null) {

					validLogin = MessageDigest.isEqual(password.getBytes("UTF-8"), storedPassword.getBytes("UTF-8"));

					// Upgrade an old plain-text password to a hash.
					if (validLogin) {
						userDao.updatePassword(user.getUserId(), PasswordUtil.hash(password));
					}
				}
			}

			if (!validLogin) {
				req.setAttribute("errorMessage", "Invalid email or password. Please try again.");

				req.getRequestDispatcher("/login.jsp").forward(req, resp);
				return;
			}

			userDao.updateLastLogin(user.getUserId());

			// Do not keep the password in the session.
			user.setPassword(null);

			HttpSession session = req.getSession(true);

			// Change the session ID after successful login.
			req.changeSessionId();

			// Store both names for compatibility with existing controllers.
			session.setAttribute("user", user);
			session.setAttribute("loggedInUser", user);

			resp.sendRedirect(req.getContextPath() + "/restaurant");

		} catch (Exception e) {

			log("Login failed", e);

			req.setAttribute("errorMessage", "Something went wrong during login. Please try again.");

			req.getRequestDispatcher("/login.jsp").forward(req, resp);
		}
	}
}
