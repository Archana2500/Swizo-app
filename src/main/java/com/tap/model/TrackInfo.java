package com.tap.model;

import java.sql.Timestamp;

public class TrackInfo {
	private int orderId;
	private Timestamp orderDate;
	private double total;
	private String status;
	private String paymentMode;
	private String restaurantName;
	private String agentName;
	private String agentPhone;

	public TrackInfo(int orderId, Timestamp orderDate, double total, String status, String paymentMode,
			String restaurantName, String agentName, String agentPhone) {
		this.orderId = orderId;
		this.orderDate = orderDate;
		this.total = total;
		this.status = status;
		this.paymentMode = paymentMode;
		this.restaurantName = restaurantName;
		this.agentName = agentName;
		this.agentPhone = agentPhone;
	}

	public int getOrderId() {
		return orderId;
	}

	public Timestamp getOrderDate() {
		return orderDate;
	}

	public double getTotal() {
		return total;
	}

	public String getStatus() {
		return status;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public String getAgentName() {
		return agentName;
	}

	public String getAgentPhone() {
		return agentPhone;
	}
}