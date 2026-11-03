package com.tap.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

import com.tap.DAO.UserDAO;
import com.tap.DAOImpl.UserDAOImpl;
import com.tap.model.User;
import com.tap.util.EmailUtil;
import com.tap.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/forgot-password")
public class ForgotPasswordController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String PATH = "/forgot-password"; // the servlet URL (used for every redirect)
	private static final String VIEW = "/forgot-password.jsp"; // the page that draws the three cards

	private static final long CODE_TTL_MS = 10 * 60 * 1000L; // code (and the new-password step) valid 10 min
	private static final long RESEND_COOLDOWN_MS = 60 * 1000L; // wait before asking for another code
	private static final int MAX_TRIES = 5; // wrong codes allowed per code

	private static final String K_EMAIL = "fpEmail", K_CODE = "fpCode", K_EXP = "fpExpires", K_TRIES = "fpTries",
			K_SENT = "fpLastSent", K_OK = "fpVerified", K_FLASH = "fpFlash";

	private final UserDAO userDao = new UserDAOImpl();
	private final SecureRandom random = new SecureRandom();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if ("1".equals(req.getParameter("restart"))) {
			clearState(req.getSession(false));
			resp.sendRedirect(req.getContextPath() + PATH);
			return;
		}
		show(req, resp, null);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String action = req.getParameter("action") == null ? "" : req.getParameter("action");
		switch (action) {
		case "send":
			handleSend(req, resp);
			break;
		case "verify":
			handleVerify(req, resp);
			break;
		case "resend":
			handleResend(req, resp);
			break;
		case "reset":
			handleReset(req, resp);
			break;
		default:
			resp.sendRedirect(req.getContextPath() + PATH);
		}
	}

	// ---------- card 1: email ----------
	private void handleSend(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String email = trim(req.getParameter("email")).toLowerCase();
		req.setAttribute("email", email);

		if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") || email.length() > 120) {
			show(req, resp, "Please enter a valid email address.");
			return;
		}

		HttpSession s = req.getSession();

		// same email asked again inside the cooldown: don't send a second mail, just go
		// to the code card
		Long last = (Long) s.getAttribute(K_SENT);
		if (last != null && email.equals(s.getAttribute(K_EMAIL)) && currentStep(s) == 2
				&& System.currentTimeMillis() - last < RESEND_COOLDOWN_MS) {
			resp.sendRedirect(req.getContextPath() + PATH);
			return;
		}

		if (!issueCode(s, email)) {
			show(req, resp, "We couldn't send the email right now. Please try again in a moment.");
			return;
		}
		s.setAttribute(K_FLASH, "If an account exists for " + email + ", we've sent it a 4-digit code.");
		resp.sendRedirect(req.getContextPath() + PATH);
	}

	// ---------- card 2: code ----------
	private void handleVerify(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession s = req.getSession(false);
		if (currentStep(s) != 2) {
			resp.sendRedirect(req.getContextPath() + PATH);
			return;
		}

		int tries = ((Integer) s.getAttribute(K_TRIES)) + 1;
		s.setAttribute(K_TRIES, tries);

		String code = trim(req.getParameter("code"));
		String expected = (String) s.getAttribute(K_CODE);
		boolean ok = code.matches("\\d{4}") && expected != null && MessageDigest
				.isEqual(expected.getBytes(StandardCharsets.UTF_8), code.getBytes(StandardCharsets.UTF_8));

		if (!ok) {
			if (tries >= MAX_TRIES) {
				clearState(s);
				show(req, resp, "Too many wrong attempts. Please request a new code.");
			} else {
				show(req, resp, "Incorrect code. " + (MAX_TRIES - tries) + " attempt(s) left.");
			}
			return;
		}

		s.setAttribute(K_OK, Boolean.TRUE);
		s.setAttribute(K_EXP, System.currentTimeMillis() + CODE_TTL_MS); // time allowed to set the new password
		s.removeAttribute(K_CODE);
		resp.sendRedirect(req.getContextPath() + PATH);
	}

	private void handleResend(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession s = req.getSession(false);
		if (currentStep(s) != 2) {
			resp.sendRedirect(req.getContextPath() + PATH);
			return;
		}
		long wait = resendWaitSeconds(s);
		if (wait > 0) {
			show(req, resp, "Please wait " + wait + " seconds before asking for a new code.");
			return;
		}
		String email = (String) s.getAttribute(K_EMAIL);
		if (!issueCode(s, email)) {
			show(req, resp, "We couldn't send the email right now. Please try again in a moment.");
			return;
		}
		s.setAttribute(K_FLASH, "A new code has been sent.");
		resp.sendRedirect(req.getContextPath() + PATH);
	}

	// ---------- card 3: new password ----------
	private void handleReset(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession s = req.getSession(false);
		if (currentStep(s) != 3) {
			resp.sendRedirect(req.getContextPath() + PATH);
			return;
		}

		String p1 = req.getParameter("password") == null ? "" : req.getParameter("password");
		String p2 = req.getParameter("confirm") == null ? "" : req.getParameter("confirm");

		if (p1.length() < 6) {
			show(req, resp, "Password must be at least 6 characters.");
			return;
		}
		if (!PasswordUtil.fitsBcrypt(p1)) {
			show(req, resp, "Password is too long (maximum 72 characters).");
			return;
		}
		if (!p1.equals(p2)) {
			show(req, resp, "The two passwords don't match.");
			return;
		}

		try {
			User user = userDao.getByEmail((String) s.getAttribute(K_EMAIL));
			if (user == null) { // cannot happen for a real code, but be safe
				clearState(s);
				show(req, resp, "Something went wrong. Please start again.");
				return;
			}
			if (PasswordUtil.verify(p1, user.getPassword())) {
				show(req, resp, "Your new password must be different from your previous password.");
				return;
			}
			userDao.updatePassword(user.getUserId(), PasswordUtil.hash(p1));
			clearState(s);
			resp.sendRedirect(req.getContextPath() + "/login?reset=1");
		} catch (RuntimeException e) {
			getServletContext().log("Password reset failed", e);
			show(req, resp, "Could not change your password. Please try again.");
		}
	}

	// ---------- helpers ----------

	/**
	 * makes a new code, stores it in the session, and e-mails it ONLY if the
	 * account exists
	 */
	private boolean issueCode(HttpSession s, String email) {
		String code = String.format("%04d", random.nextInt(10000));
		User user;
		try {
			user = userDao.getByEmail(email);
		} catch (RuntimeException e) {
			getServletContext().log("Reset lookup failed", e);
			return false;
		}

		long now = System.currentTimeMillis();
		s.setAttribute(K_EMAIL, email);
		s.setAttribute(K_CODE, code);
		s.setAttribute(K_EXP, now + CODE_TTL_MS);
		s.setAttribute(K_TRIES, 0);
		s.setAttribute(K_SENT, now);
		s.removeAttribute(K_OK);

		if (user != null && !EmailUtil.sendResetCode(email, user.getName(), code)) {
			clearState(s);
			return false;
		}
		return true; // unknown emails get the same answer as known ones
	}

	/** 1 = ask for email, 2 = enter code, 3 = new password */
	private int currentStep(HttpSession s) {
		if (s == null) {
			return 1;
		}
		Long exp = (Long) s.getAttribute(K_EXP);
		if (s.getAttribute(K_EMAIL) == null || exp == null || System.currentTimeMillis() > exp) {
			clearState(s);
			return 1;
		}
		return Boolean.TRUE.equals(s.getAttribute(K_OK)) ? 3 : 2;
	}

	private long resendWaitSeconds(HttpSession s) {
		Long last = (Long) s.getAttribute(K_SENT);
		if (last == null) {
			return 0;
		}
		long left = last + RESEND_COOLDOWN_MS - System.currentTimeMillis();
		return left <= 0 ? 0 : (left + 999) / 1000;
	}

	private void clearState(HttpSession s) {
		if (s == null) {
			return;
		}
		for (String k : new String[] { K_EMAIL, K_CODE, K_EXP, K_TRIES, K_SENT, K_OK }) {
			s.removeAttribute(k);
		}
	}

	private void show(HttpServletRequest req, HttpServletResponse resp, String error)
			throws ServletException, IOException {
		HttpSession s = req.getSession(false);
		int step = currentStep(s);
		req.setAttribute("step", step);
		if (step > 1) {
			req.setAttribute("email", s.getAttribute(K_EMAIL));
			req.setAttribute("resendWait", (int) resendWaitSeconds(s));
			String flash = (String) s.getAttribute(K_FLASH);
			if (flash != null) {
				req.setAttribute("info", flash);
				s.removeAttribute(K_FLASH);
			}
		}
		req.setAttribute("error", error);
		resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		resp.setHeader("Pragma", "no-cache");
		req.getRequestDispatcher(VIEW).forward(req, resp);
	}

	private static String trim(String s) {
		return s == null ? "" : s.trim();
	}
}