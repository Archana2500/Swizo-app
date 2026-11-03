package com.tap.model;

import java.sql.Timestamp;

public class Feedback {
	private int feedbackId, orderId, userId, restaurantId, rating;
	private String comment;
	private java.sql.Timestamp createdDate;

	public Feedback(int feedbackId, int orderId, int userId, int restaurantId, int rating, String comment,
			Timestamp createdDate) {
		super();
		this.feedbackId = feedbackId;
		this.orderId = orderId;
		this.userId = userId;
		this.restaurantId = restaurantId;
		this.rating = rating;
		this.comment = comment;
		this.createdDate = createdDate;
	}

	

	public Feedback() {
		// TODO Auto-generated constructor stub
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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(int restaurantId) {
		this.restaurantId = restaurantId;
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

	public java.sql.Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(java.sql.Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public String toString() {
		return "Feedback [feedbackId=" + feedbackId + ", orderId=" + orderId + ", userId=" + userId + ", restaurantId="
				+ restaurantId + ", rating=" + rating + ", comment=" + comment + ", createdDate=" + createdDate + "]";
	}

}