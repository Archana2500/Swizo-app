package com.tap.DAO;

import java.util.List;

import com.tap.model.AdminOrderRow;

public interface AdminDAO {
	int countRestaurants();

	int countMenuItems();

	int countOrders();

	List<AdminOrderRow> getOrders(String statusOrNull, int limit);

	AdminOrderRow getOrder(int orderId);

	List<AdminOrderRow> getOrdersByAgent(int agentId);

	int countOrderItemsForMenu(int id);
}
