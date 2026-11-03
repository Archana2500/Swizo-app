package com.tap.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Read-only, display-ready order: orders + restaurant + delivery agent + item rows. */
public class OrderView {
	private int orderId, restaurantId, userId;
	private String restaurantName, restaurantImage, status, paymentMode, agentName, agentPhone;
	private Timestamp orderDate;
	private double totalAmount;
	private List<OrderItemView> items = new ArrayList<>();

	public OrderView() {
	}

	public int getOrderId() { return orderId; }
	public void setOrderId(int orderId) { this.orderId = orderId; }

	public int getRestaurantId() { return restaurantId; }
	public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }

	public int getUserId() { return userId; }
	public void setUserId(int userId) { this.userId = userId; }

	public String getRestaurantName() { return restaurantName; }
	public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

	public String getRestaurantImage() { return restaurantImage; }
	public void setRestaurantImage(String restaurantImage) { this.restaurantImage = restaurantImage; }

	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }

	public String getPaymentMode() { return paymentMode; }
	public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

	public String getAgentName() { return agentName; }       // null until an agent is assigned
	public void setAgentName(String agentName) { this.agentName = agentName; }

	public String getAgentPhone() { return agentPhone; }
	public void setAgentPhone(String agentPhone) { this.agentPhone = agentPhone; }

	public Timestamp getOrderDate() { return orderDate; }
	public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

	public double getTotalAmount() { return totalAmount; }
	public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

	public List<OrderItemView> getItems() { return items; }

	public void setItems(List<OrderItemView> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "OrderView [orderId=" + orderId + ", restaurantName=" + restaurantName + ", status=" + status
				+ ", totalAmount=" + totalAmount + "]";
	}
}