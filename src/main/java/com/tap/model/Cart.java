package com.tap.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final double DELIVERY_FEE = 9.0;
	private static final int MAX_QTY = 20;

	private int restaurantId;
	private Map<Integer, CartItem> items; // key = menuId, value = CartItem

	public Cart() {
		this.restaurantId = 0;
		this.items = new LinkedHashMap<>(); // maintains insertion order of items
	}

	// Add item or increment quantity if it already exists
	public void add(Menu menu) {
		if (items.isEmpty()) {
			restaurantId = menu.getRestaurantId(); // first item locks the restaurant
		}

		CartItem existing = items.get(menu.getMenuId());
		if (existing != null) {
			existing.setQuantity(Math.min(existing.getQuantity() + 1, MAX_QTY));
		} else {
			CartItem item = new CartItem();
			item.setMenuId(menu.getMenuId());
			item.setRestaurantId(menu.getRestaurantId());
			item.setItemName(menu.getItemName());
			item.setImagePath(menu.getImagePath());
			item.setPrice(menu.getPrice());
			item.setQuantity(1);
			items.put(menu.getMenuId(), item);
		}
	}

	// Increase quantity for an existing item
	public void increase(int menuId) {
		CartItem item = items.get(menuId);
		if (item != null) {
			item.setQuantity(Math.min(item.getQuantity() + 1, MAX_QTY));
		}
	}

	// Decrease quantity; if quantity is 1, remove the item completely
	public void decrease(int menuId) {
		CartItem item = items.get(menuId);
		if (item == null)
			return;

		if (item.getQuantity() <= 1) {
			remove(menuId);
		} else {
			item.setQuantity(item.getQuantity() - 1);
		}
	}

	// Remove item from cart
	public void remove(int menuId) {
		items.remove(menuId);
		if (items.isEmpty()) {
			restaurantId = 0; // reset restaurant lock when cart is empty
		}
	}

	// Clear the entire cart
	public void clear() {
		items.clear();
		restaurantId = 0;
	}

	public Map<Integer, CartItem> getItems() {
		return items;
	}

	public int getRestaurantId() {
		return restaurantId;
	}

	public boolean isEmpty() {
		return items == null || items.isEmpty();
	}

	// total number of units in the cart (this is the number on the cart badge)
	public int getTotalQuantity() {
		int n = 0;
		for (CartItem item : items.values()) {
			n += item.getQuantity();
		}
		return n;
	}

	// menuId -> quantity, so the menu page can show "+ Add" or the "- 1 +" stepper
	public Map<Integer, Integer> getQuantities() {
		Map<Integer, Integer> q = new HashMap<>();
		for (Map.Entry<Integer, CartItem> e : items.entrySet()) {
			q.put(e.getKey(), e.getValue().getQuantity());
		}
		return q;
	}

	// items only (what the cart page shows as "Total")
	public double getSubtotal() {
		double total = 0;
		for (CartItem item : items.values()) {
			total += item.getLineTotal();
		}
		return total;
	}

	// kept so your existing code that calls getTotal() still works: same as
	// getSubtotal()
	public double getTotal() {
		return getSubtotal();
	}

	public double getDeliveryFee() {
		return isEmpty() ? 0 : DELIVERY_FEE;
	}

	// items + delivery fee (what the checkout page shows as "Total")
	public double getGrandTotal() {
		return getSubtotal() + getDeliveryFee();
	}
}