package com.tap.DAOImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tap.DAO.RestaurantDAO;
import com.tap.model.Restaurant;
import com.tap.util.DBConnection;

public class RestaurantDAOImpl implements RestaurantDAO {

	private static final String INSERT_QUERY = "INSERT INTO restaurant(name, address, phone, CuisineType, DeliveryTime, AdminUserId, Rating, IsActive, ImagePath) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String SELECT_BY_ID_QUERY = "SELECT * FROM restaurant WHERE RestaurantId = ?";
	private static final String SELECT_ALL_QUERY = "SELECT * FROM restaurant";
	private static final String SELECT_ACTIVE_QUERY = "SELECT * FROM restaurant WHERE IsActive = 1 ORDER BY Rating DESC";
	private static final String SELECT_BY_ADMIN_QUERY = "SELECT * FROM restaurant WHERE AdminUserId = ?";
	private static final String UPDATE_QUERY = "UPDATE restaurant SET name = ?, address = ?, phone = ?, CuisineType = ?, DeliveryTime = ?, IsActive = ?, ImagePath = ? "
			+ "WHERE RestaurantId = ?";
	private static final String DELETE_QUERY = "DELETE FROM restaurant WHERE RestaurantId = ?";
	private static final String REFRESH_RATING_QUERY = "UPDATE restaurant SET Rating = COALESCE((SELECT ROUND(AVG(rating), 1) FROM feedback WHERE RestaurantId = ?), 0) "
			+ "WHERE RestaurantId = ?";

	@Override
	public int addRestaurant(Restaurant restaurant) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, restaurant.getName());
			pstmt.setString(2, restaurant.getAddress());
			pstmt.setString(3, restaurant.getPhone());
			pstmt.setString(4, restaurant.getCuisineType());
			pstmt.setString(5, restaurant.getDeliveryTime());
			pstmt.setInt(6, restaurant.getAdminUserId());
			pstmt.setDouble(7, restaurant.getRating());
			pstmt.setBoolean(8, restaurant.isActive());
			pstmt.setString(9, restaurant.getImagePath());
			pstmt.executeUpdate();

			try (ResultSet keys = pstmt.getGeneratedKeys()) {
				return keys.next() ? keys.getInt(1) : 0;
			}
		} catch (SQLException e) {
			throw new RuntimeException("addRestaurant failed", e);
		}
	}

	@Override
	public Restaurant getById(int restaurantId) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_ID_QUERY)) {

			pstmt.setInt(1, restaurantId);
			try (ResultSet res = pstmt.executeQuery()) {
				return res.next() ? extractRestaurantFromResultSet(res) : null;
			}
		} catch (SQLException e) {
			throw new RuntimeException("getById failed", e);
		}
	}

	@Override
	public List<Restaurant> getAll() {
		return queryList(SELECT_ALL_QUERY, null);
	}

	@Override
	public List<Restaurant> getAllActive() {
		return queryList(SELECT_ACTIVE_QUERY, null);
	}

	@Override
	public List<Restaurant> getByAdmin(int adminUserId) {
		return queryList(SELECT_BY_ADMIN_QUERY, adminUserId);
	}

	private List<Restaurant> queryList(String sql, Integer param) {
		List<Restaurant> list = new ArrayList<>();
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql)) {

			if (param != null) {
				pstmt.setInt(1, param);
			}
			try (ResultSet res = pstmt.executeQuery()) {
				while (res.next()) {
					list.add(extractRestaurantFromResultSet(res));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("restaurant query failed", e);
		}
		return list;
	}

	// reads ONE row only; the caller moves the cursor with res.next()
	private Restaurant extractRestaurantFromResultSet(ResultSet res) throws SQLException {
		Restaurant r = new Restaurant();
		r.setRestaurantId(res.getInt("RestaurantId"));
		r.setName(res.getString("name"));
		r.setAddress(res.getString("address"));
		r.setPhone(res.getString("phone"));
		r.setCuisineType(res.getString("CuisineType"));
		r.setDeliveryTime(res.getString("DeliveryTime"));
		r.setAdminUserId(res.getInt("AdminUserId"));
		r.setRating(res.getDouble("Rating"));
		r.setActive(res.getBoolean("IsActive"));
		r.setImagePath(res.getString("ImagePath"));
		return r;
	}

	@Override
	public boolean updateRestaurant(Restaurant restaurant) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(UPDATE_QUERY)) {

			pstmt.setString(1, restaurant.getName());
			pstmt.setString(2, restaurant.getAddress());
			pstmt.setString(3, restaurant.getPhone());
			pstmt.setString(4, restaurant.getCuisineType());
			pstmt.setString(5, restaurant.getDeliveryTime());
			pstmt.setBoolean(6, restaurant.isActive());
			pstmt.setString(7, restaurant.getImagePath());
			pstmt.setInt(8, restaurant.getRestaurantId());
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException("updateRestaurant failed", e);
		}
	}

	@Override
	public boolean deleteRestaurant(int restaurantId) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(DELETE_QUERY)) {

			pstmt.setInt(1, restaurantId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException("deleteRestaurant failed", e);
		}
	}

	@Override
	public void refreshRating(int restaurantId) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(REFRESH_RATING_QUERY)) {

			pstmt.setInt(1, restaurantId);
			pstmt.setInt(2, restaurantId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("refreshRating failed", e);
		}
	}
}