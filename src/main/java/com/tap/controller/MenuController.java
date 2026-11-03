package com.tap.controller;

import java.io.IOException;
import java.util.List;

import com.tap.DAOImpl.MenuDAOImpl;
import com.tap.DAOImpl.RestaurantDAOImpl;
import com.tap.model.Menu;
import com.tap.model.Restaurant;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/menu")
public class MenuController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// accept ?id=3 or ?restaurantId=3
		String idParam = req.getParameter("id");
		if (idParam == null) {
			idParam = req.getParameter("restaurantId");
		}
		if (idParam == null) {
			resp.sendRedirect(req.getContextPath() + "/restaurant");
			return;
		}

		int id;
		try {
			id = Integer.parseInt(idParam);
		} catch (NumberFormatException e) {
			resp.sendRedirect(req.getContextPath() + "/restaurant");
			return;
		}

		Restaurant restaurant = new RestaurantDAOImpl().getById(id);
		if (restaurant == null) {
			resp.sendRedirect(req.getContextPath() + "/restaurant");
			return;
		}
		List<Menu> menuList = new MenuDAOImpl().getMenuByRestaurantId(id);

		req.setAttribute("restaurant", restaurant); // the banner and title need this
		req.setAttribute("allMenues", menuList);
		req.getRequestDispatcher("menu.jsp").forward(req, resp);
	}
}