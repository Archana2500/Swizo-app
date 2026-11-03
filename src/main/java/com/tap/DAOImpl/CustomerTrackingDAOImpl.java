
package com.tap.DAOImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.tap.model.TrackInfo;
import com.tap.util.DBConnection;

public class CustomerTrackingDAOImpl {

	public TrackInfo getTracking(int orderId, int userId) {

		TrackInfo info = null;

		String sql = "SELECT " + "o.orderId, " + "o.order_Date, " + "o.totalamount, " + "o.status, " + "o.paymentMode, "
				+ "r.name AS restaurantName, " + "da.name AS agentName, " + "da.phone AS agentPhone " +

				"FROM `order` o " +

				"LEFT JOIN restaurant r " + "ON o.RestaurantId = r.RestaurantId " +

				"LEFT JOIN delivery_agent da " + "ON o.agentId = da.agentId " +

				"WHERE o.orderId = ? " + "AND o.userId = ?";

		try (Connection con = DBConnection.getConnection()) {

			if (con == null) {
				System.err.println("Database connection failed.");
				return null;
			}

			try (PreparedStatement ps = con.prepareStatement(sql)) {

				ps.setInt(1, orderId);
				ps.setInt(2, userId);

				try (ResultSet rs = ps.executeQuery()) {

					if (rs.next()) {

						info = new TrackInfo(rs.getInt("orderId"), rs.getTimestamp("order_Date"),
								rs.getDouble("totalamount"), rs.getString("status"), rs.getString("paymentMode"),
								rs.getString("restaurantName"), rs.getString("agentName"), rs.getString("agentPhone"));
					}
				}
			}

		} catch (SQLException e) {

			System.err.println("Error fetching customer tracking details:");

			e.printStackTrace();
		}

		return info;
	}
}
