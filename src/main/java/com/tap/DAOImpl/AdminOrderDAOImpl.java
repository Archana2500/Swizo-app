package com.tap.DAOImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.tap.model.AdminOrderRow;
import com.tap.util.DBConnection;

public class AdminOrderDAOImpl {

	private static final String LIST_SQL = "SELECT o.orderId, o.RestaurantId, r.name AS restaurant_name, o.userId, "
			+ "o.order_Date, o.totalamount, o.status, o.paymentMode, "
			+ "(SELECT COALESCE(SUM(oi.quantity), 0) FROM orderitem oi WHERE oi.orderId = o.orderId) AS item_count "
			+ "FROM `order` o LEFT JOIN restaurant r ON r.RestaurantId = o.RestaurantId ";

	private static final String ITEMS_SQL = "SELECT m.ItemName, m.Price, oi.quantity, oi.totalamount "
			+ "FROM orderitem oi LEFT JOIN menu m ON m.MenuId = oi.MenuId WHERE oi.orderId = ?";

	private static final String UPDATE_STATUS_SQL = "UPDATE `order` SET status = ? WHERE orderId = ?";

	private static final String COUNT_SQL = "SELECT COUNT(*) FROM `order`";

	/* newest first, optionally only one status (pass null or "" for all) */
	public List<AdminOrderRow> findAll(String status) {
		boolean filter = status != null && !status.isEmpty();
		String sql = LIST_SQL + (filter ? "WHERE o.status = ? " : "")
				+ "ORDER BY o.order_Date DESC, o.orderId DESC LIMIT 200";

		List<AdminOrderRow> list = new ArrayList<>();

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			if (filter) {
				ps.setString(1, status);
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(map(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	/* one order including its items, or null */
	public AdminOrderRow findById(int id) {
		AdminOrderRow order = null;

		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(LIST_SQL + "WHERE o.orderId = ?")) {

			ps.setInt(1, id);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					order = map(rs);
				}
			}

			if (order != null) {
				try (PreparedStatement ips = con.prepareStatement(ITEMS_SQL)) {
					ips.setInt(1, id);

					try (ResultSet irs = ips.executeQuery()) {
						while (irs.next()) {
							String name = irs.getString("ItemName");
							order.getItems().add(new AdminOrderRow.Item(name == null ? "(removed dish)" : name,
									irs.getDouble("Price"), irs.getInt("quantity"), irs.getDouble("totalamount")));
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return order;
	}

	/* changes only the status, the order date stays as it was */
	public boolean updateStatus(int id, String status) {
		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(UPDATE_STATUS_SQL)) {

			ps.setString(1, status);
			ps.setInt(2, id);

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public int count() {
		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(COUNT_SQL);
				ResultSet rs = ps.executeQuery()) {

			return rs.next() ? rs.getInt(1) : 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private AdminOrderRow map(ResultSet rs) throws SQLException {
		AdminOrderRow o = new AdminOrderRow();

		o.setOrderId(rs.getInt("orderId"));
		o.setRestaurantId(rs.getInt("RestaurantId"));
		o.setRestaurantName(rs.getString("restaurant_name"));
		o.setUserId(rs.getInt("userId"));
		o.setOrderDate(rs.getTimestamp("order_Date"));
		o.setTotal(rs.getDouble("totalamount"));
		o.setStatus(rs.getString("status"));
		o.setPaymentMode(rs.getString("paymentMode"));
		o.setItemCount(rs.getInt("item_count"));

		return o;
	}
}