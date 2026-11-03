package com.tap.DAO;

import java.util.List;
import com.tap.model.Restaurant;

public interface RestaurantDAO {
	int addRestaurant(Restaurant restaurant); // returns the new RestaurantId

	Restaurant getById(int restaurantId);

	List<Restaurant> getAll(); // every restaurant (admin)

	List<Restaurant> getAllActive(); // customer listing

	List<Restaurant> getByAdmin(int adminUserId);

	boolean updateRestaurant(Restaurant restaurant);

	boolean deleteRestaurant(int restaurantId);

	void refreshRating(int restaurantId); // recompute from the feedback table
}