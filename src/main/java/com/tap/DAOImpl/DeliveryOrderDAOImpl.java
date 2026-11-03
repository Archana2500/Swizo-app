
package com.tap.DAOImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.tap.model.Order;
import com.tap.util.DBConnection;

public class DeliveryOrderDAOImpl {

	private static final String BY_AGENT = "SELECT * FROM `order` WHERE agentId = ? "
			+ "AND status IN ('PREPARING','OUT FOR DELIVERY','DELIVERED') "
			+ "ORDER BY FIELD(status,'PREPARING','OUT FOR DELIVERY','DELIVERED'), " + "order_Date DESC";

	private static final String PICKUP = "UPDATE `order` SET status = 'OUT FOR DELIVERY' "
			+ "WHERE orderId = ? AND agentId = ? AND status = 'PREPARING'";

	private static final String DELIVER = "UPDATE `order` SET status = 'DELIVERED' "
			+ "WHERE orderId = ? AND agentId = ? AND status = 'OUT FOR DELIVERY'";

	// Get orders assigned to a delivery agent
	public List<Order> getOrdersByAgent(int agentId) {

		List<Order> list = new ArrayList<>();

		try (Connection con = DBConnection.getConnection()) {

			if (con == null) {
				return list;
			}

			try (PreparedStatement ps = con.prepareStatement(BY_AGENT)) {

				ps.setInt(1, agentId);

				try (ResultSet rs = ps.executeQuery()) {

					while (rs.next()) {

						Order order = new Order();

						order.setOrderId(rs.getInt("orderId"));
						order.setRestaurantId(rs.getInt("RestaurantId"));
						order.setUserId(rs.getInt("userId"));
						order.setOrderDate(rs.getTimestamp("order_Date"));
						order.setTotalAmount(rs.getDouble("totalamount"));
						order.setStatus(rs.getString("status"));
						order.setPaymentMode(rs.getString("paymentMode"));

						int agent = rs.getInt("agentId");

						order.setAgentId(rs.wasNull() ? null : agent);

						list.add(order);
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	// Mark order as picked up
	public boolean pickUp(int orderId, int agentId) {

		return update(PICKUP, orderId, agentId);
	}

	// Mark order as delivered
	public boolean markDelivered(int orderId, int agentId) {

		return update(DELIVER, orderId, agentId);
	}

	// Common update method
	private boolean update(String sql, int orderId, int agentId) {

		try (Connection con = DBConnection.getConnection()) {

			if (con == null) {
				return false;
			}

			try (PreparedStatement ps = con.prepareStatement(sql)) {

				ps.setInt(1, orderId);
				ps.setInt(2, agentId);

				return ps.executeUpdate() == 1;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
