
package com.tap.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.UUID;

import com.tap.DAO.OrderDAO;
import com.tap.DAO.UserDAO;
import com.tap.DAOImpl.OrderDAOImpl;
import com.tap.DAOImpl.UserDAOImpl;
import com.tap.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet({ "/profile", "/profile/view" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 2 * 1024 * 1024, maxRequestSize = 3 * 1024 * 1024)
public class ProfileController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String K_CSRF = "csrf";
	private static final String K_FLASH = "profileFlash";

	private final UserDAO userDao = new UserDAOImpl();
	private final OrderDAO orderDao = new OrderDAOImpl();
	private final SecureRandom random = new SecureRandom();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		User su = currentUser(req);

		if (su == null) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}

		// Separate View Profile page
		// Separate View Profile page
		if ("/profile/view".equals(req.getServletPath())) {
			showViewProfile(req, resp, su);
			return;
		}

		show(req, resp, su, null, null);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		User su = currentUser(req);

		if (su == null) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}

		HttpSession session = req.getSession(false);

		String expected = (String) session.getAttribute(K_CSRF);
		String sent = trim(req.getParameter("csrf"));

		if (expected == null || !MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
				sent.getBytes(StandardCharsets.UTF_8))) {

			resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid request.");
			return;
		}

		String action = trim(req.getParameter("action"));

		if ("update".equals(action)) {
			handleUpdate(req, resp, su, session);

		} else if ("uploadPhoto".equals(action)) {
			handlePhotoUpload(req, resp, su, session);

		} else {
			resp.sendRedirect(req.getContextPath() + "/profile");
		}
	}

	// -----------------------------------------
	// UPDATE PERSONAL DETAILS
	// -----------------------------------------

	private void handleUpdate(HttpServletRequest req, HttpServletResponse resp, User su, HttpSession session)
			throws ServletException, IOException {

		String name = trim(req.getParameter("name"));
		String phone = trim(req.getParameter("phone"));
		String address = trim(req.getParameter("address"));

		String error = null;

		if (name.isEmpty() || name.length() > 45) {
			error = "Please enter your name (up to 45 characters).";

		} else if (!phone.matches("\\d{10}")) {
			error = "Enter a valid 10-digit phone number.";

		} else if (address.length() > 120) {
			error = "Address is too long (maximum 120 characters).";
		}

		if (error != null) {
			show(req, resp, su, error, "details");
			return;
		}

		try {
			User user = userDao.getById(su.getUserId());

			if (user == null) {
				session.invalidate();
				resp.sendRedirect(req.getContextPath() + "/login");
				return;
			}

			user.setName(name);
			user.setPhone(phone);
			user.setAddress(address.isEmpty() ? null : address);

			boolean updated = userDao.updateUser(user);

			if (!updated) {
				show(req, resp, su, "Could not save your details. Please try again.", "details");
				return;
			}

			su.setName(name);
			su.setPhone(phone);
			su.setAddress(user.getAddress());
			su.setProfileImage(user.getProfileImage());

			session.setAttribute("user", su);
			session.setAttribute(K_FLASH, "Your details have been updated.");

			resp.sendRedirect(req.getContextPath() + "/profile");

		} catch (RuntimeException e) {
			getServletContext().log("Profile update failed", e);

			show(req, resp, su, "Could not save your details. Please try again.", "details");
		}
	}

	// -----------------------------------------
	// UPLOAD PROFILE PHOTO
	// -----------------------------------------

	private void handlePhotoUpload(HttpServletRequest req, HttpServletResponse resp, User su, HttpSession session)
			throws ServletException, IOException {

		Path savedFile = null;

		try {
			Part part = req.getPart("profileImage");

			if (part == null || part.getSize() == 0) {
				show(req, resp, su, "Please choose a profile image.", "photo");
				return;
			}

			if (part.getSize() > 2 * 1024 * 1024) {
				show(req, resp, su, "Image size must be less than 2 MB.", "photo");
				return;
			}

			byte[] imageBytes;

			try (var input = part.getInputStream()) {
				imageBytes = input.readAllBytes();
			}

			String extension = getImageExtension(imageBytes);

			if (extension == null) {
				show(req, resp, su, "Please upload a valid JPG, PNG or WEBP image.", "photo");
				return;
			}

			User user = userDao.getById(su.getUserId());

			if (user == null) {
				session.invalidate();
				resp.sendRedirect(req.getContextPath() + "/login");
				return;
			}

			String uploadFolder = getServletContext().getRealPath("/uploads/profile-images");

			if (uploadFolder == null) {
				throw new IOException("Could not locate the profile image upload folder.");
			}

			Path directory = Paths.get(uploadFolder);
			Files.createDirectories(directory);

			String fileName = "profile_" + su.getUserId() + "_" + UUID.randomUUID() + extension;

			savedFile = directory.resolve(fileName);

			Files.write(savedFile, imageBytes);

			String relativePath = "uploads/profile-images/" + fileName;

			user.setProfileImage(relativePath);

			boolean updated = userDao.updateUser(user);

			if (!updated) {
				Files.deleteIfExists(savedFile);

				show(req, resp, su, "Could not save your profile photo. Please try again.", "photo");
				return;
			}

			su.setProfileImage(relativePath);
			session.setAttribute("user", su);

			session.setAttribute(K_FLASH, "Your profile photo has been updated successfully.");

			resp.sendRedirect(req.getContextPath() + "/profile");

		} catch (IllegalStateException e) {
			show(req, resp, su, "Image is too large. Please choose an image below 2 MB.", "photo");

		} catch (RuntimeException | IOException e) {

			if (savedFile != null) {
				try {
					Files.deleteIfExists(savedFile);
				} catch (IOException ignored) {
					getServletContext().log("Could not remove unused profile image.", ignored);
				}
			}

			getServletContext().log("Profile photo upload failed", e);

			show(req, resp, su, "Could not upload your profile photo. Please try again.", "photo");
		}
	}

	// -----------------------------------------
	// CHECK IMAGE FILE TYPE
	// -----------------------------------------

	private String getImageExtension(byte[] data) {

		if (data == null) {
			return null;
		}

		// JPEG
		if (data.length >= 3 && (data[0] & 0xFF) == 0xFF && (data[1] & 0xFF) == 0xD8 && (data[2] & 0xFF) == 0xFF) {
			return ".jpg";
		}

		// PNG
		if (data.length >= 8 && (data[0] & 0xFF) == 0x89 && data[1] == 0x50 && data[2] == 0x4E && data[3] == 0x47
				&& data[4] == 0x0D && data[5] == 0x0A && data[6] == 0x1A && data[7] == 0x0A) {
			return ".png";
		}

		// WEBP
		if (data.length >= 12 && data[0] == 'R' && data[1] == 'I' && data[2] == 'F' && data[3] == 'F' && data[8] == 'W'
				&& data[9] == 'E' && data[10] == 'B' && data[11] == 'P') {
			return ".webp";
		}

		return null;
	}

	// -----------------------------------------
	// DISPLAY EDIT PROFILE PAGE
	// -----------------------------------------

	private void show(HttpServletRequest req, HttpServletResponse resp, User su, String error, String section)
			throws ServletException, IOException {

		HttpSession session = req.getSession();

		User fresh;

		try {
			fresh = userDao.getById(su.getUserId());

		} catch (RuntimeException e) {
			getServletContext().log("Profile load failed", e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		if (fresh == null) {
			session.invalidate();
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}

		fresh.setPassword(null);

		int orderCount = getOrderCount(fresh);

		String flash = (String) session.getAttribute(K_FLASH);

		if (flash != null) {
			req.setAttribute("flash", flash);
			session.removeAttribute(K_FLASH);
		}

		req.setAttribute("profile", fresh);
		req.setAttribute("profileImageUrl", fresh.getProfileImage());
		req.setAttribute("orderCount", orderCount);
		req.setAttribute("csrf", csrf(session));
		req.setAttribute("error", error);
		req.setAttribute("errorSection", section);

		setNoCacheHeaders(resp);

		req.getRequestDispatcher("/profile.jsp").forward(req, resp);
	}

	// -----------------------------------------
	// DISPLAY VIEW PROFILE PAGE
	// -----------------------------------------

	private void showViewProfile(HttpServletRequest req, HttpServletResponse resp, User su)
			throws ServletException, IOException {

		HttpSession session = req.getSession();

		User fresh;

		try {
			fresh = userDao.getById(su.getUserId());

		} catch (RuntimeException e) {
			getServletContext().log("View profile load failed", e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		if (fresh == null) {
			session.invalidate();
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}

		fresh.setPassword(null);

		req.setAttribute("profile", fresh);
		req.setAttribute("profileImageUrl", fresh.getProfileImage());
		req.setAttribute("orderCount", getOrderCount(fresh));

		setNoCacheHeaders(resp);

		req.getRequestDispatcher("/view-profile.jsp").forward(req, resp);
	}

	// -----------------------------------------
	// ORDER COUNT
	// -----------------------------------------

	private int getOrderCount(User user) {

		try {
			return orderDao.getByUser(user.getUserId()).size();

		} catch (RuntimeException e) {
			getServletContext().log("Could not count orders", e);
			return 0;
		}
	}

	// -----------------------------------------
	// NO CACHE HEADERS
	// -----------------------------------------

	private void setNoCacheHeaders(HttpServletResponse resp) {
		resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		resp.setHeader("Pragma", "no-cache");
	}

	// -----------------------------------------
	// CSRF TOKEN
	// -----------------------------------------

	private String csrf(HttpSession session) {

		String token = (String) session.getAttribute(K_CSRF);

		if (token == null) {
			byte[] bytes = new byte[16];
			random.nextBytes(bytes);

			StringBuilder sb = new StringBuilder();

			for (byte b : bytes) {
				sb.append(String.format("%02x", b));
			}

			token = sb.toString();
			session.setAttribute(K_CSRF, token);
		}

		return token;
	}

	// -----------------------------------------
	// CURRENT LOGGED-IN USER
	// -----------------------------------------

	private User currentUser(HttpServletRequest req) {

		HttpSession session = req.getSession(false);

		return session == null ? null : (User) session.getAttribute("user");
	}

	private static String trim(String value) {
		return value == null ? "" : value.trim();
	}
}
