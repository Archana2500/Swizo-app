package com.tap.DAOImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tap.DAO.FeedbackDAO;
import com.tap.model.Feedback;
import com.tap.model.FeedbackView;
import com.tap.util.DBConnection;

public class FeedbackDAOImpl implements FeedbackDAO {

	private static final String INSERT_QUERY = "INSERT INTO feedback(orderId, userId, RestaurantId, rating, comment) "
			+ "VALUES (?, ?, ?, ?, ?)";

	private static final String SELECT_BY_ORDER_QUERY = "SELECT feedbackId, orderId, userId, RestaurantId, rating, comment, createdDate "
			+ "FROM feedback WHERE orderId = ?";

	private static final String VIEWS_QUERY = "SELECT f.feedbackId, f.orderId, f.rating, f.comment, f.createdDate, "
			+ "u.name AS customerName, " + "r.name AS restaurantName, " + "r.ImagePath AS restaurantImage "
			+ "FROM feedback f " + "JOIN user u ON u.userId = f.userId "
			+ "JOIN restaurant r ON r.RestaurantId = f.RestaurantId " + "ORDER BY f.createdDate DESC";

	@Override
	public int addFeedback(Feedback f) {

		try (Connection c = DBConnection.getConnection();
				PreparedStatement ps = c.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {

			ps.setInt(1, f.getOrderId());
			ps.setInt(2, f.getUserId());
			ps.setInt(3, f.getRestaurantId());
			ps.setInt(4, f.getRating());
			ps.setString(5, f.getComment());

			ps.executeUpdate();

			try (ResultSet keys = ps.getGeneratedKeys()) {
				return keys.next() ? keys.getInt(1) : 0;
			}

		} catch (SQLException e) {
			throw new RuntimeException("addFeedback failed", e);
		}
	}

	@Override
	public Feedback getByOrder(int orderId) {

		try (Connection c = DBConnection.getConnection();
				PreparedStatement ps = c.prepareStatement(SELECT_BY_ORDER_QUERY)) {

			ps.setInt(1, orderId);

			try (ResultSet rs = ps.executeQuery()) {

				if (!rs.next()) {
					return null;
				}

				Feedback f = new Feedback();

				f.setFeedbackId(rs.getInt("feedbackId"));
				f.setOrderId(rs.getInt("orderId"));
				f.setUserId(rs.getInt("userId"));
				f.setRestaurantId(rs.getInt("RestaurantId"));
				f.setRating(rs.getInt("rating"));
				f.setComment(rs.getString("comment"));
				f.setCreatedDate(rs.getTimestamp("createdDate"));

				return f;
			}

		} catch (SQLException e) {
			throw new RuntimeException("getByOrder failed", e);
		}
	}

	@Override
	public List<FeedbackView> getAllViews() {

		List<FeedbackView> out = new ArrayList<>();

		try (Connection c = DBConnection.getConnection();
				PreparedStatement ps = c.prepareStatement(VIEWS_QUERY);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {

				FeedbackView v = new FeedbackView();

				v.setFeedbackId(rs.getInt("feedbackId"));
				v.setOrderId(rs.getInt("orderId"));
				v.setRating(rs.getInt("rating"));
				v.setComment(rs.getString("comment"));
				v.setCreatedDate(rs.getTimestamp("createdDate"));
				v.setCustomerName(rs.getString("customerName"));
				v.setRestaurantName(rs.getString("restaurantName"));
				v.setRestaurantImage(rs.getString("restaurantImage"));

				out.add(v);
			}

		} catch (SQLException e) {
			throw new RuntimeException("getAllViews failed", e);
		}

		return out;
	}

	@Override
	public double getAverageRating() {

		try (Connection c = DBConnection.getConnection();
				PreparedStatement ps = c.prepareStatement("SELECT AVG(rating) FROM feedback");
				ResultSet rs = ps.executeQuery()) {

			return rs.next() ? rs.getDouble(1) : 0;

		} catch (SQLException e) {
			throw new RuntimeException("getAverageRating failed", e);
		}
	}
}