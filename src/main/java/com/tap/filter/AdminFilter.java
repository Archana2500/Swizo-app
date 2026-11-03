
package com.tap.filter;

import java.io.IOException;

import com.tap.model.User;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Protects admin pages. Admin login is accessible without authentication. Only
 * admin and superadmin users can access the admin panel.
 */

@WebFilter("/admin/*")
public class AdminFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		String servletPath = req.getServletPath();
		String requestURI = req.getRequestURI();

		// Allow admin login page without authentication
		if ("/admin/login".equals(servletPath) || requestURI.endsWith("/admin/login")) {

			chain.doFilter(request, response);
			return;
		}

		HttpSession session = req.getSession(false);

		User user = (session == null) ? null : (User) session.getAttribute("loggedInUser");

		if (user == null) {
			resp.sendRedirect(req.getContextPath() + "/admin/login");
			return;
		}

		String role = user.getRole();

		if (role == null || (!"admin".equalsIgnoreCase(role) && !"superadmin".equalsIgnoreCase(role))) {

			resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins only.");
			return;
		}

		resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		resp.setHeader("Pragma", "no-cache");

		chain.doFilter(request, response);
	}

}
