
package com.tap.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AdminOrderRow {

	private int orderId;
	private int restaurantId;
	private String restaurantName;
	private String restaurantImage;

	private int userId;
	private String customerName;
	private String customerPhone;

	private Timestamp orderDate;
	private double total;

	private String status;
	private String paymentMode;

	private Integer agentId;
	private String agentName;

	private int itemCount;

	private final List<Item> items = new ArrayList<>();

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(int restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public String getRestaurantImage() {
		return restaurantImage;
	}

	public void setRestaurantImage(String restaurantImage) {
		this.restaurantImage = restaurantImage;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public Timestamp getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Timestamp orderDate) {
		this.orderDate = orderDate;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	// Compatibility with existing JSP pages
	public double getTotalAmount() {
		return total;
	}

	public void setTotalAmount(double totalAmount) {
		this.total = totalAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public Integer getAgentId() {
		return agentId;
	}

	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	public List<Item> getItems() {
		return items;
	}

	public static class Item {

		private final String name;
		private final double price;
		private final int quantity;
		private final double lineTotal;

		public Item(String name, double price, int quantity, double lineTotal) {
			this.name = name;
			this.price = price;
			this.quantity = quantity;
			this.lineTotal = lineTotal;
		}

		public String getName() {
			return name;
		}

		public double getPrice() {
			return price;
		}

		public int getQuantity() {
			return quantity;
		}

		public double getLineTotal() {
			return lineTotal;
		}
	}
}
