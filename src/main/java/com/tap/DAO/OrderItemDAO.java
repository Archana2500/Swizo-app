package com.tap.DAO;

import com.tap.model.OrderItem;
import java.util.List;

public interface OrderItemDAO {
	List<OrderItem> getByOrder(int orderId);

	OrderItem getById(int orderItemId);

	List<OrderItem> getAll();

	boolean deleteOrderItem(int orderItemId);

	boolean updateOrderItem(OrderItem item);

	int addOrderItem(OrderItem item);
}
