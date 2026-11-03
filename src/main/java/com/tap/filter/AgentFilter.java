package com.tap.filter;

import java.io.IOException;

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
 * Everything under /agent needs an agent session, except the login page itself.
 */
@WebFilter("/agent/*")
public class AgentFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		resp.setHeader("Pragma", "no-cache");

		String path = req.getRequestURI().substring(req.getContextPath().length());
		if (path.equals("/agent/login")) {
			chain.doFilter(request, response);
			return;
		}
		HttpSession s = req.getSession(false);
		if (s == null || s.getAttribute("agent") == null) {
			resp.sendRedirect(req.getContextPath() + "/agent/login");
			return;
		}
		chain.doFilter(request, response);
	}
}