package com.tap.DAOImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tap.model.FeedbackView;
import com.tap.util.DBConnection;

public class AdminFeedbackDAOImpl {

	private static final String ALL = "SELECT f.orderId, u.name AS customer, r.name AS restaurant, f.rating, f.comment, f.createdDate "
			+ "FROM feedback f JOIN `user` u ON f.userId = u.userId "
			+ "JOIN restaurant r ON f.RestaurantId = r.RestaurantId ORDER BY f.createdDate DESC";

	private static final String AVG = "SELECT r.name, ROUND(AVG(f.rating), 1) AS avg_rating, COUNT(*) AS cnt "
			+ "FROM feedback f JOIN restaurant r ON f.RestaurantId = r.RestaurantId "
			+ "GROUP BY r.RestaurantId, r.name ORDER BY avg_rating DESC";

	public List<FeedbackView> getAll() {
		List<FeedbackView> list = new ArrayList<>();
		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(ALL);
				ResultSet r = ps.executeQuery()) {
			while (r.next()) {
				list.add(new FeedbackView(r.getInt("orderId"), r.getString("customer"), r.getString("restaurant"),
						r.getInt("rating"), r.getString("comment"), r.getTimestamp("createdDate")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/** restaurant name -> "4.5 (12 ratings)" */
	public Map<String, String> getAverages() {
		Map<String, String> map = new LinkedHashMap<>();
		try (Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(AVG);
				ResultSet r = ps.executeQuery()) {
			while (r.next()) {
				map.put(r.getString("name"), r.getDouble("avg_rating") + " (" + r.getInt("cnt") + " ratings)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}
}
