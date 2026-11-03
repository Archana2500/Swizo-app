package com.tap.model;

public class Restaurant {
	private int restaurantId, adminUserId;
	private String name, address, phone, cuisineType, deliveryTime, imagePath;
	private double rating;
	private boolean active;

	// Default Constructor
	public Restaurant() {
		super();
	}

	// Parameterized Constructor
	public Restaurant(int restaurantId, int adminUserId, String name, String address, String phone, String cuisineType,
			String deliveryTime, String imagePath, double rating, boolean active) {
		this.restaurantId = restaurantId;
		this.adminUserId = adminUserId;
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.cuisineType = cuisineType;
		this.deliveryTime = deliveryTime;
		this.imagePath = imagePath;
		this.rating = rating;
		this.active = active;
	}

	// Getters and Setters
	public int getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(int restaurantId) {
		this.restaurantId = restaurantId;
	}

	public int getAdminUserId() {
		return adminUserId;
	}

	public void setAdminUserId(int adminUserId) {
		this.adminUserId = adminUserId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCuisineType() {
		return cuisineType;
	}

	public void setCuisineType(String cuisineType) {
		this.cuisineType = cuisineType;
	}

	public String getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "Restaurant [restaurantId=" + restaurantId + ", name=" + name + ", cuisineType=" + cuisineType + "]";
	}
}