package com.tap.model;

import java.sql.Timestamp;

public class FeedbackView {

	private int feedbackId;
	private int orderId;
	private String customerName;
	private String restaurantName;
	private String restaurantImage;
	private int rating;
	private String comment;
	private Timestamp createdDate;

	public FeedbackView(int orderId, String customerName, String restaurantName, int rating, String comment,
			Timestamp createdDate) {

		this.orderId = orderId;
		this.customerName = customerName;
		this.restaurantName = restaurantName;
		this.rating = rating;
		this.comment = comment;
		this.createdDate = createdDate;
	}

	public FeedbackView(int feedbackId, int orderId, String customerName, String restaurantName, int rating,
			String comment, Timestamp createdDate) {

		this.feedbackId = feedbackId;
		this.orderId = orderId;
		this.customerName = customerName;
		this.restaurantName = restaurantName;
		this.rating = rating;
		this.comment = comment;
		this.createdDate = createdDate;
	}

	// Constructor including restaurant image
	public FeedbackView(int feedbackId, int orderId, String customerName, String restaurantName, String restaurantImage,
			int rating, String comment, Timestamp createdDate) {

		this.feedbackId = feedbackId;
		this.orderId = orderId;
		this.customerName = customerName;
		this.restaurantName = restaurantName;
		this.restaurantImage = restaurantImage;
		this.rating = rating;
		this.comment = comment;
		this.createdDate = createdDate;
	}

	public FeedbackView() {
	}

	public int getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(int feedbackId) {
		this.feedbackId = feedbackId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}
}