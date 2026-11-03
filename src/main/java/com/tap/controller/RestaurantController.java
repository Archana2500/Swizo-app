package com.tap.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.tap.DAO.RestaurantDAO;
import com.tap.DAOImpl.RestaurantDAOImpl;
import com.tap.model.Restaurant;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/restaurant")
public class RestaurantController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private RestaurantDAO restaurantDAO;

	@Override
	public void init() throws ServletException {
		restaurantDAO = new RestaurantDAOImpl();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			List<Restaurant> allRestaurants = restaurantDAO.getAllActive();

			String cuisine = request.getParameter("cuisine");

			List<Restaurant> displayedRestaurants = new ArrayList<>();

			for (Restaurant restaurant : allRestaurants) {
				if (cuisine == null || cuisine.isBlank() || cuisine.equalsIgnoreCase("All")) {
					displayedRestaurants.add(restaurant);
				} else if (matchesCuisine(restaurant.getCuisineType(), cuisine)) {
					displayedRestaurants.add(restaurant);
				}
			}

			request.setAttribute("restaurants", displayedRestaurants);
			request.setAttribute("selectedCuisine", cuisine == null ? "All" : cuisine);

			request.getRequestDispatcher("/restaurant.jsp").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException("Unable to load restaurants", e);
		}
	}

	private boolean matchesCuisine(String restaurantCuisine, String selectedCuisine) {

		if (restaurantCuisine == null || selectedCuisine == null) {
			return false;
		}

		String actual = restaurantCuisine.toLowerCase(Locale.ROOT);
		String selected = selectedCuisine.toLowerCase(Locale.ROOT);

		return actual.contains(selected);
	}
}