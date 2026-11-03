package com.tap.controller;

import java.io.IOException;
import java.util.Locale;

import com.tap.DAO.MenuDAO;
import com.tap.DAOImpl.MenuDAOImpl;
import com.tap.model.Cart;
import com.tap.model.Menu;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** POST /cart action = add | inc | dec | remove | clear | replace ; menuId */
@WebServlet("/cart")
public class CartController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final MenuDAO menuDao = new MenuDAOImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("/Cart.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		boolean ajax = "fetch".equals(req.getHeader("X-Requested-With"));

		HttpSession session = req.getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		if (cart == null) {
			cart = new Cart();
			session.setAttribute("cart", cart);
		}

		String action = req.getParameter("action");
		if ("increase".equals(action))
			action = "inc";
		if ("decrease".equals(action))
			action = "dec";
		if (action == null) {
			fail(resp, ajax, 400, "BAD_REQUEST", "Missing action.");
			return;
		}

		int menuId = 0;
		if (!"clear".equals(action)) {
			try {
				menuId = Integer.parseInt(req.getParameter("menuId"));
			} catch (NumberFormatException e) {
				fail(resp, ajax, 400, "BAD_REQUEST", "Invalid item.");
				return;
			}
		}

		int restaurantForRedirect = cart.getRestaurantId();

		synchronized (cart) {
			switch (action) {
			case "add":
			case "replace": {
				Menu m = menuDao.getById(menuId); // price always comes from the DB
				if (m == null || !m.isAvailable()) {
					fail(resp, ajax, 400, "UNAVAILABLE", "This item is not available right now.");
					return;
				}
				if ("replace".equals(action)) {
					cart.clear();
				} else if (!cart.isEmpty() && cart.getRestaurantId() != m.getRestaurantId()) {
					fail(resp, ajax, 409, "OTHER_RESTAURANT", "Your cart already has items from another restaurant.");
					return;
				}
				cart.add(m);
				restaurantForRedirect = m.getRestaurantId();
				break;
			}
			case "inc":
				cart.increase(menuId);
				break;
			case "dec":
				cart.decrease(menuId);
				break;
			case "remove":
				cart.remove(menuId);
				break;
			case "clear":
				cart.clear();
				break;
			default:
				fail(resp, ajax, 400, "BAD_REQUEST", "Unknown action.");
				return;
			}
		}

		if (ajax) {
			int qty = cart.getQuantities().getOrDefault(menuId, 0);
			write(resp, 200,
					String.format(Locale.ROOT,
							"{\"ok\":true,\"qty\":%d,\"cartCount\":%d,\"subtotal\":%.2f,\"total\":%.2f}", qty,
							cart.getTotalQuantity(), cart.getSubtotal(), cart.getTotal()));
			return;
		}

		// plain form post (no JavaScript): redirect back
		String ctx = req.getContextPath();
		if ("menu".equals(req.getParameter("from")) && restaurantForRedirect > 0) {
			resp.sendRedirect(ctx + "/menu?restaurantId=" + restaurantForRedirect);
		} else {
			resp.sendRedirect(ctx + "/cart");
		}
	}

	private void fail(HttpServletResponse resp, boolean ajax, int status, String code, String message)
			throws IOException {
		if (ajax) {
			write(resp, status, "{\"ok\":false,\"code\":\"" + code + "\",\"message\":\"" + message + "\"}");
		} else {
			resp.sendError(status, message);
		}
	}

	private void write(HttpServletResponse resp, int status, String json) throws IOException {
		resp.setStatus(status);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("Cache-Control", "no-store");
		resp.getWriter().write(json);
	}
}