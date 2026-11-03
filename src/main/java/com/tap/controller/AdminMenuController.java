package com.tap.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tap.DAO.MenuDAO;
import com.tap.DAO.RestaurantDAO;
import com.tap.DAOImpl.MenuDAOImpl;
import com.tap.DAOImpl.RestaurantDAOImpl;
import com.tap.model.Menu;
import com.tap.model.Restaurant;
import com.tap.model.User;
import com.tap.util.Csrf;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/menu")
public class AdminMenuController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final MenuDAO menuDAO = new MenuDAOImpl();
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

	private Menu getAccessibleMenu(int menuId, User user) {
		Menu menu = menuDAO.getById(menuId);

		if (menu == null) {
			return null;
		}

		return getAccessibleRestaurant(menu.getRestaurantId(), user) == null ? null : menu;
	}

	private void flash(HttpServletRequest req, String message, boolean error) {
		HttpSession session = req.getSession();

		session.setAttribute("adminFlash", error ? null : message);
		session.setAttribute("adminErr", error ? message : null);
	}

	private int getFilter(HttpServletRequest req) {
		String value = req.getParameter("restaurantId");

		if (value == null || value.trim().isEmpty()) {
			value = req.getParameter("filter");
		}

		if (value == null || value.trim().isEmpty()) {
			return 0;
		}

		int id = Integer.parseInt(value);

		if (id < 0) {
			throw new NumberFormatException("Invalid restaurant filter.");
		}

		return id;
	}

	private void redirect(HttpServletRequest req, HttpServletResponse resp, int filter) throws IOException {

		String url = req.getContextPath() + "/admin/menu";

		if (filter > 0) {
			url += "?restaurantId=" + filter;
		}

		resp.sendRedirect(url);
	}

	private List<Menu> getVisibleMenuItems(User admin, List<Restaurant> restaurants, int filter) {

		List<Menu> result = new ArrayList<>();

		if (filter > 0) {
			Restaurant selected = getAccessibleRestaurant(filter, admin);

			if (selected == null) {
				return result;
			}

			return menuDAO.getByRestaurant(filter, false);
		}

		for (Restaurant restaurant : restaurants) {
			result.addAll(menuDAO.getByRestaurant(restaurant.getRestaurantId(), false));
		}

		return result;
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
			int filter = getFilter(req);

			if (filter > 0 && getAccessibleRestaurant(filter, admin) == null) {
				flash(req, "Restaurant not found or access denied.", true);
				filter = 0;
			}

			req.setAttribute("restaurants", restaurants);
			req.setAttribute("items", getVisibleMenuItems(admin, restaurants, filter));
			req.setAttribute("filter", filter);

			String edit = req.getParameter("edit");

			if (edit != null && !edit.trim().isEmpty()) {
				int menuId = Integer.parseInt(edit);
				Menu editing = getAccessibleMenu(menuId, admin);

				if (editing == null) {
					flash(req, "Menu item not found or access denied.", true);
				} else {
					req.setAttribute("editing", editing);
				}
			}

			req.getRequestDispatcher("/WEB-INF/admin/menu.jsp").forward(req, resp);

		} catch (NumberFormatException e) {
			flash(req, "Invalid menu item or restaurant ID.", true);
			req.setAttribute("restaurants", getAdminRestaurants(admin));
			req.setAttribute("items", getVisibleMenuItems(admin, getAdminRestaurants(admin), 0));
			req.setAttribute("filter", 0);

			req.getRequestDispatcher("/WEB-INF/admin/menu.jsp").forward(req, resp);

		} catch (RuntimeException e) {
			throw new ServletException("Unable to load menu items.", e);
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
		int filter = 0;

		try {
			filter = getFilter(req);

			if ("add".equals(action)) {

				int restaurantId = positiveInt(req, "restaurantId");

				if (getAccessibleRestaurant(restaurantId, admin) == null) {
					flash(req, "Restaurant not found or access denied.", true);
					redirect(req, resp, filter);
					return;
				}

				Menu menu = new Menu();

				menu.setRestaurantId(restaurantId);
				menu.setItemName(required(req, "itemName"));
				menu.setDescription(req.getParameter("description"));
				menu.setPrice(positivePrice(req));
				menu.setImagePath(req.getParameter("imagePath"));
				menu.setAvailable("on".equals(req.getParameter("available")));
				menu.setRatings(0.0);

				int id = menuDAO.addMenu(menu);

				if (id > 0) {
					flash(req, "Menu item added successfully.", false);
				} else {
					flash(req, "Unable to add menu item.", true);
				}

				filter = restaurantId;

			} else if ("update".equals(action)) {

				int menuId = positiveInt(req, "menuId");

				Menu menu = getAccessibleMenu(menuId, admin);

				if (menu == null) {
					flash(req, "Menu item not found or access denied.", true);
					redirect(req, resp, filter);
					return;
				}

				menu.setItemName(required(req, "itemName"));
				menu.setDescription(req.getParameter("description"));
				menu.setPrice(positivePrice(req));
				menu.setImagePath(req.getParameter("imagePath"));
				menu.setAvailable("on".equals(req.getParameter("available")));

				if (menuDAO.updateMenu(menu)) {
					flash(req, "Menu item updated successfully.", false);
				} else {
					flash(req, "No menu item details were changed.", true);
				}

				filter = menu.getRestaurantId();

			} else if ("toggle".equals(action)) {

				int menuId = positiveInt(req, "menuId");

				Menu menu = getAccessibleMenu(menuId, admin);

				if (menu == null) {
					flash(req, "Menu item not found or access denied.", true);
					redirect(req, resp, filter);
					return;
				}

				menu.setAvailable("1".equals(req.getParameter("available")));

				if (menuDAO.updateMenu(menu)) {
					flash(req, "Menu item availability updated.", false);
				} else {
					flash(req, "Unable to update menu item availability.", true);
				}

				filter = menu.getRestaurantId();

			} else if ("delete".equals(action)) {

				int menuId = positiveInt(req, "menuId");

				Menu menu = getAccessibleMenu(menuId, admin);

				if (menu == null) {
					flash(req, "Menu item not found or access denied.", true);
					redirect(req, resp, filter);
					return;
				}

				filter = menu.getRestaurantId();

				if (menuDAO.deleteMenu(menuId)) {
					flash(req, "Menu item deleted successfully.", false);
				} else {
					flash(req, "Unable to delete menu item.", true);
				}

			} else {
				flash(req, "Invalid menu action.", true);
			}

		} catch (NumberFormatException e) {
			flash(req, "Please enter valid restaurant, menu and price values.", true);

		} catch (IllegalArgumentException e) {
			flash(req, e.getMessage(), true);

		} catch (RuntimeException e) {
			flash(req, "Menu operation failed. Please check the details and database relationships.", true);
			getServletContext().log("Menu administration error", e);
		}

		redirect(req, resp, filter);
	}

	private String required(HttpServletRequest req, String name) {
		String value = req.getParameter(name);

		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException("Please enter the menu item " + name + ".");
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

	private double positivePrice(HttpServletRequest req) {
		String value = required(req, "price");
		double price = Double.parseDouble(value);

		if (!Double.isFinite(price) || price <= 0 || price > 99999.99) {
			throw new IllegalArgumentException("Price must be between 0.01 and 99999.99.");
		}

		return price;
	}
}