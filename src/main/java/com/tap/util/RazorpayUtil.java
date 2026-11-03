package com.tap.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class RazorpayUtil {

	private static final Pattern ORDER_ID = Pattern.compile("\"id\"\\s*:\\s*\"(order_[A-Za-z0-9]+)\"");
	private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();

	private RazorpayUtil() {
	}

	/** public key: safe to send to the browser */
	public static String keyId() {
		return RazorpayConfig.keyId();
	}

	public static boolean isConfigured() {
		return RazorpayConfig.isConfigured();
	}

	/**
	 * creates an order on Razorpay (amount in paise) and returns its id, e.g.
	 * order_Abc123
	 */
	public static String createOrder(long amountPaise, String receipt) throws IOException {
		String body = "{\"amount\":" + amountPaise + ",\"currency\":\"INR\",\"receipt\":\"" + receipt + "\"}";
		String auth = Base64.getEncoder().encodeToString(
				(RazorpayConfig.keyId() + ":" + RazorpayConfig.keySecret()).getBytes(StandardCharsets.UTF_8));

		HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.razorpay.com/v1/orders"))
				.timeout(Duration.ofSeconds(15)).header("Content-Type", "application/json")
				.header("Authorization", "Basic " + auth).POST(HttpRequest.BodyPublishers.ofString(body)).build();
		try {
			HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
			if (res.statusCode() != 200) {
				throw new IOException("Razorpay order creation failed: HTTP " + res.statusCode() + " " + res.body());
			}
			Matcher m = ORDER_ID.matcher(res.body());
			if (!m.find()) {
				throw new IOException("Razorpay reply had no order id: " + res.body());
			}
			return m.group(1);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted while contacting Razorpay", e);
		}
	}

	/**
	 * true only if signature == HMAC_SHA256(orderId + "|" + paymentId, keySecret)
	 */
	public static boolean verifySignature(String orderId, String paymentId, String signature) {
		if (orderId == null || paymentId == null || signature == null || signature.trim().isEmpty()) {
			return false;
		}
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(RazorpayConfig.keySecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
			byte[] raw = mac.doFinal((orderId + "|" + paymentId).getBytes(StandardCharsets.UTF_8));
			StringBuilder hex = new StringBuilder();
			for (byte b : raw) {
				hex.append(String.format("%02x", b));
			}
			return MessageDigest.isEqual(hex.toString().getBytes(StandardCharsets.UTF_8),
					signature.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			return false;
		}
	}
}