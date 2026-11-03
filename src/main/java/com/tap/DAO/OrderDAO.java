package com.tap.DAO;

import java.util.List;
import com.tap.model.Order;
import com.tap.model.OrderItem;
import com.tap.model.OrderView;

public interface OrderDAO {
	int placeOrder(Order order, List<OrderItem> items);

	Order getById(int orderId);

	List<Order> getAll();

	List<Order> getByUser(int userId);

	List<Order> getByRestaurant(int restaurantId);

	List<Order> getByAgent(int agentId);

	boolean updateStatus(int orderId, String status);

	boolean assignAgent(int orderId, int agentId);

	boolean deleteOrder(int orderId);

	List<OrderView> getViewsByUser(int userId);

	List<OrderView> getViewsByRestaurant(int restaurantId);

	OrderView getViewById(int orderId);
}