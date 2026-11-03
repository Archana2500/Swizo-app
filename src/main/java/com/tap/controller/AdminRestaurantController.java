package com.tap.controller;

import java.io.IOException;
import java.util.List;

import com.tap.DAO.RestaurantDAO;
import com.tap.DAOImpl.RestaurantDAOImpl;
import com.tap.model.Restaurant;
import com.tap.model.User;
import com.tap.util.Csrf;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/restaurants")
public class AdminRestaurantController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final RestaurantDAO restaurantDAO = new RestaurantDAOImpl();

	private User getAdmin(HttpServletRequest req) {
		HttpSession session = req.getSession(false);

		if (session == null) {
			return null;
		}

		return (User) session.getAttribute("loggedInUser");
	}

	private boolean isSuperAdmin(User user) {
		return user != null && "superadmin".equalsIgnoreCase(user.getRole());
	}

	private List<Restaurant> getAdminRestaurants(User user) {
		if (isSuperAdmin(user)) {
			return restaurantDAO.getAll();
		}

		return restaurantDAO.getByAdmin(user.getUserId());
	}

	private Restaurant getAccessibleRestaurant(int restaurantId, User user) {
		Restaurant restaurant = restaurantDAO.getById(restaurantId);

		if (restaurant == null) {
			return null;
		}

		if (isSuperAdmin(user) || restaurant.getAdminUserId() == user.getUserId()) {
			return restaurant;
		}

		return null;
	}

	private void flash(HttpServletRequest req, String message, boolean error) {
		HttpSession session = req.getSession();

		session.setAttribute("adminFlash", error ? null : message);
		session.setAttribute("adminErr", error ? message : null);
	}

	private void redirect(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.sendRedirect(req.getContextPath() + "/admin/restaurants");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		User admin = getAdmin(req);

		if (admin == null) {
			resp.sendRedirect(req.getContextPath() + "/admin/login");
			return;
		}

		try {
			List<Restaurant> restaurants = getAdminRestaurants(admin);
			req.setAttribute("restaurants", restaurants);

			String edit = req.getParameter("edit");

			if (edit != null && !edit.trim().isEmpty()) {
				int restaurantId = Integer.parseInt(edit);
				Restaurant editing = getAccessibleRestaurant(restaurantId, admin);

				if (editing == null) {
					flash(req, "Restaurant not found or access denied.", true);
				} else {
					req.setAttribute("editing", editing);
				}
			}

			req.getRequestDispatcher("/WEB-INF/admin/restaurants.jsp").forward(req, resp);

		} catch (NumberFormatException e) {
			flash(req, "Invalid restaurant ID.", true);
			req.setAttribute("restaurants", getAdminRestaurants(admin));

			req.getRequestDispatcher("/WEB-INF/admin/restaurants.jsp").forward(req, resp);

		} catch (RuntimeException e) {
			throw new ServletException("Unable to load restaurants.", e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		User admin = getAdmin(req);

		if (admin == null) {
			resp.sendRedirect(req.getContextPath() + "/admin/login");
			return;
		}

		if (!Csrf.valid(req)) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid CSRF token.");
			return;
		}

		String action = req.getParameter("action");

		try {
			if ("add".equals(action)) {

				Restaurant restaurant = new Restaurant();

				restaurant.setName(required(req, "name"));
				restaurant.setCuisineType(req.getParameter("cuisineType"));
				restaurant.setPhone(req.getParameter("phone"));
				restaurant.setDeliveryTime(req.getParameter("deliveryTime"));
				restaurant.setAddress(req.getParameter("address"));
				restaurant.setImagePath(req.getParameter("imagePath"));
				restaurant.setAdminUserId(admin.getUserId());
				restaurant.setActive("on".equals(req.getParameter("active")));
				restaurant.setRating(0.0);

				int id = restaurantDAO.addRestaurant(restaurant);

				if (id > 0) {
					flash(req, "Restaurant added successfully.", false);
				} else {
					flash(req, "Unable to add restaurant.", true);
				}

			} else if ("update".equals(action)) {

				int restaurantId = positiveInt(req, "restaurantId");

				Restaurant restaurant = getAccessibleRestaurant(restaurantId, admin);

				if (restaurant == null) {
					flash(req, "Restaurant not found or access denied.", true);
					redirect(req, resp);
					return;
				}

				restaurant.setName(required(req, "name"));
				restaurant.setCuisineType(req.getParameter("cuisineType"));
				restaurant.setPhone(req.getParameter("phone"));
				restaurant.setDeliveryTime(req.getParameter("deliveryTime"));
				restaurant.setAddress(req.getParameter("address"));
				restaurant.setImagePath(req.getParameter("imagePath"));
				restaurant.setActive("on".equals(req.getParameter("active")));

				if (restaurantDAO.updateRestaurant(restaurant)) {
					flash(req, "Restaurant updated successfully.", false);
				} else {
					flash(req, "No restaurant details were changed.", true);
				}

			} else if ("toggle".equals(action)) {

				int restaurantId = positiveInt(req, "restaurantId");

				Restaurant restaurant = getAccessibleRestaurant(restaurantId, admin);

				if (restaurant == null) {
					flash(req, "Restaurant not found or access denied.", true);
					redirect(req, resp);
					return;
				}

				restaurant.setActive("1".equals(req.getParameter("active")));

				if (restaurantDAO.updateRestaurant(restaurant)) {
					flash(req, "Restaurant visibility updated.", false);
				} else {
					flash(req, "Unable to update restaurant visibility.", true);
				}

			} else if ("delete".equals(action)) {

				int restaurantId = positiveInt(req, "restaurantId");

				Restaurant restaurant = getAccessibleRestaurant(restaurantId, admin);

				if (restaurant == null) {
					flash(req, "Restaurant not found or access denied.", true);
					redirect(req, resp);
					return;
				}

				if (restaurantDAO.deleteRestaurant(restaurantId)) {
					flash(req, "Restaurant deleted successfully.", false);
				} else {
					flash(req, "Unable to delete restaurant.", true);
				}

			} else {
				flash(req, "Invalid restaurant action.", true);
			}

		} catch (NumberFormatException e) {
			flash(req, "Invalid restaurant ID.", true);

		} catch (IllegalArgumentException e) {
			flash(req, e.getMessage(), true);

		} catch (RuntimeException e) {
			flash(req, "Operation failed. Please check the restaurant details and database relationships.", true);
			getServletContext().log("Restaurant administration error", e);
		}

		redirect(req, resp);
	}

	private String required(HttpServletRequest req, String name) {
		String value = req.getParameter(name);

		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException("Please enter the restaurant " + name + ".");
		}

		return value.trim();
	}

	private int positiveInt(HttpServletRequest req, String name) {
		int value = Integer.parseInt(req.getParameter(name));

		if (value <= 0) {
			throw new NumberFormatException("ID must be positive.");
		}

		return value;
	}
}