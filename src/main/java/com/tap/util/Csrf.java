package com.tap.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Per-session token that every POST form must send back (blocks forged requests
 * from other sites).
 */
public final class Csrf {
	private static final String KEY = "csrf"; // same key the profile page uses
	private static final SecureRandom RANDOM = new SecureRandom();

	private Csrf() {
	}

	public static String token(HttpSession session) {
		String t = (String) session.getAttribute(KEY);
		if (t == null) {
			byte[] b = new byte[16];
			RANDOM.nextBytes(b);
			StringBuilder sb = new StringBuilder();
			for (byte x : b) {
				sb.append(String.format("%02x", x));
			}
			t = sb.toString();
			session.setAttribute(KEY, t);
		}
		return t;
	}

	public static boolean valid(HttpServletRequest req) {
		HttpSession s = req.getSession(false);
		if (s == null) {
			return false;
		}
		String expected = (String) s.getAttribute(KEY);
		String sent = req.getParameter("csrf");
		if (expected == null || sent == null) {
			return false;
		}
		return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), sent.getBytes(StandardCharsets.UTF_8));
	}
}