
package com.tap.DAOImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.tap.DAO.AdminDAO;
import com.tap.model.AdminOrderRow;
import com.tap.util.DBConnection;

public class AdminDAOImpl implements AdminDAO {

	private static final String ROW_SELECT = "SELECT o.orderId, o.RestaurantId, o.userId, "
			+ "o.order_Date, o.totalamount, o.status, " + "o.paymentMode, o.agentId, " + "r.name AS restaurantName, "
			+ "r.ImagePath AS restaurantImage, " + "u.name AS customerName, " + "u.phone AS customerPhone, "
			+ "a.name AS agentName " + "FROM `order` o " + "JOIN restaurant r ON r.RestaurantId = o.RestaurantId "
			+ "JOIN user u ON u.userId = o.userId " + "LEFT JOIN delivery_agent a ON a.agentId = o.agentId ";

	@Override
	public int countRestaurants() {
		return count("SELECT COUNT(*) FROM restaurant");
	}

	@Override
	public int countMenuItems() {
		return count("SELECT COUNT(*) FROM menu");
	}

	@Override
	public int countOrders() {
		return count("SELECT COUNT(*) FROM `order`");
	}

	private int count(String sql) {

		try (Connection c = DBConnection.getConnection();
				PreparedStatement ps = c.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			return rs.next() ? rs.getInt(1) : 0;

		} catch (SQLException e) {
			throw new RuntimeException("count failed", e);
		}
	}

	@Override
	public List<AdminOrderRow> getOrders(String status, int limit) {

		if (status == null || status.trim().isEmpty()) {
			return query(ROW_SELECT + "ORDER BY o.order_Date DESC LIMIT ?", limit);
		}

		return query(ROW_SELECT + "WHERE o.status = ? " + "ORDER BY o.order_Date DESC LIMIT ?", status, limit);
	}

	@Override
	public AdminOrderRow getOrder(int orderId) {

		List<AdminOrderRow> rows = query(ROW_SELECT + "WHERE o.orderId = ?", orderId);

		return rows.isEmpty() ? null : rows.get(0);
	}

	@Override
	public List<AdminOrderRow> getOrdersByAgent(int agentId) {

		return query(ROW_SELECT + "WHERE o.agentId = ? " + "ORDER BY o.order_Date DESC", agentId);
	}

	private List<AdminOrderRow> query(String sql, Object... params) {

		List<AdminOrderRow> list = new ArrayList<>();

		try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {

			for (int i = 0; i < params.length; i++) {

				if (params[i] instanceof Integer) {
					ps.setInt(i + 1, (Integer) params[i]);
				} else {
					ps.setString(i + 1, String.valueOf(params[i]));
				}
			}

			try (ResultSet rs = ps.executeQuery()) {

				while (rs.next()) {
					list.add(map(rs));
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException("admin order query failed", e);
		}

		return list;
	}

	private AdminOrderRow map(ResultSet rs) throws SQLException {

		AdminOrderRow r = new AdminOrderRow();

		r.setOrderId(rs.getInt("orderId"));
		r.setRestaurantId(rs.getInt("RestaurantId"));
		r.setUserId(rs.getInt("userId"));

		r.setOrderDate(rs.getTimestamp("order_Date"));
		r.setTotal(rs.getDouble("totalamount"));
		r.setStatus(rs.getString("status"));
		r.setPaymentMode(rs.getString("paymentMode"));

		int agent = rs.getInt("agentId");
		r.setAgentId(rs.wasNull() ? null : agent);

		r.setRestaurantName(rs.getString("restaurantName"));
		r.setRestaurantImage(rs.getString("restaurantImage"));

		r.setCustomerName(rs.getString("customerName"));
		r.setCustomerPhone(rs.getString("customerPhone"));
		r.setAgentName(rs.getString("agentName"));

		return r;
	}

	@Override
	public int countOrderItemsForMenu(int id) {

		String sql = "SELECT COALESCE(SUM(quantity), 0) " + "FROM order_items WHERE MenuId = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException("Failed to count menu item orders", e);
		}

		return 0;
	}

}
