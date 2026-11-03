package com.tap.util;

import java.util.List;

/**
 * The order.status values the app uses. All of them must exist in your
 * order.status ENUM.
 */
public final class OrderStatus {
	public static final String PLACED = "PLACED";
	public static final String PAID = "PAID";
	public static final String PREPARING = "PREPARING";
	public static final String OUT_FOR_DELIVERY = "OUT_FOR_DELIVERY";
	public static final String DELIVERED = "DELIVERED";
	public static final String CANCELLED = "CANCELLED";

	public static final List<String> ALL = List.of(PLACED, PAID, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED);

	private OrderStatus() {
	}

	public static boolean isValid(String s) {
		return s != null && ALL.contains(s);
	}

	/** delivered and cancelled orders can't be changed any more */
	public static boolean isFinal(String s) {
		return DELIVERED.equals(s) || CANCELLED.equals(s);
	}

	public static String label(String s) {
		return s == null ? "" : s.replace('_', ' ');
	}
}