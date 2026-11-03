package com.tap.util;

/**
 * Holds the Razorpay keys. Environment variables win, so production can keep
 * keys out of the code.
 */
public class RazorpayConfig {

	// For LOCAL TESTING paste your TEST keys here (they start with rzp_test_).
	// Never commit or upload this file with real keys in it, and never use
	// rzp_live_ keys here.
	private static final String KEY_ID = "your_key_id";
	private static final String KEY_SECRET = "your_secret_key";

	private RazorpayConfig() {
	}

	/** public key: safe to send to the browser */
	public static String keyId() {
		return pick(System.getenv("RAZORPAY_KEY_ID"), KEY_ID);
	}

	/** secret key: must NEVER leave the server */
	public static String keySecret() {
		return pick(System.getenv("RAZORPAY_KEY_SECRET"), KEY_SECRET);
	}

	public static boolean isConfigured() {
		return !keyId().isEmpty() && !keySecret().isEmpty();
	}

	private static String pick(String env, String fallback) {
		String v = (env == null || env.trim().isEmpty()) ? fallback : env;
		return v == null ? "" : v.trim();
	}
}
