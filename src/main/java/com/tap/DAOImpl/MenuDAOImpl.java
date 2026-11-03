package com.tap.DAOImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tap.DAO.MenuDAO;
import com.tap.model.Menu;
import com.tap.util.DBConnection;

public class MenuDAOImpl implements MenuDAO {

	private static final String INSERT_QUERY = "INSERT INTO menu(RestaurantId, ItemName, Description, Price, IsAvailable, ratings, ImagePath) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static final String SELECT_BY_ID_QUERY = "SELECT * FROM menu WHERE MenuId = ?";
	private static final String SELECT_ALL_QUERY = "SELECT * FROM menu";
	private static final String SELECT_BY_RESTAURANT_QUERY = "SELECT * FROM menu WHERE RestaurantId = ?";
	private static final String SELECT_AVAILABLE_BY_RESTAURANT_QUERY = "SELECT * FROM menu WHERE RestaurantId = ? AND IsAvailable = 1";
	private static final String UPDATE_QUERY = "UPDATE menu SET ItemName = ?, Description = ?, Price = ?, IsAvailable = ?, ImagePath = ? "
			+ "WHERE MenuId = ?";
	private static final String DELETE_QUERY = "DELETE FROM menu WHERE MenuId = ?";

	@Override
	public int addMenu(Menu menu) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setInt(1, menu.getRestaurantId());
			pstmt.setString(2, menu.getItemName());
			pstmt.setString(3, menu.getDescription());
			pstmt.setDouble(4, menu.getPrice());
			pstmt.setBoolean(5, menu.isAvailable());
			pstmt.setDouble(6, menu.getRatings());
			pstmt.setString(7, menu.getImagePath());
			pstmt.executeUpdate();

			try (ResultSet keys = pstmt.getGeneratedKeys()) {
				return keys.next() ? keys.getInt(1) : 0;
			}
		} catch (SQLException e) {
			throw new RuntimeException("addMenu failed", e);
		}
	}

	@Override
	public Menu getById(int menuId) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_ID_QUERY)) {

			pstmt.setInt(1, menuId);
			try (ResultSet res = pstmt.executeQuery()) {
				return res.next() ? extractMenuFromResultSet(res) : null;
			}
		} catch (SQLException e) {
			throw new RuntimeException("getById failed", e);
		}
	}

	@Override
	public List<Menu> getByRestaurant(int restaurantId, boolean onlyAvailable) {
		List<Menu> list = new ArrayList<>();
		String sql = onlyAvailable ? SELECT_AVAILABLE_BY_RESTAURANT_QUERY : SELECT_BY_RESTAURANT_QUERY;
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql)) {

			pstmt.setInt(1, restaurantId);
			try (ResultSet res = pstmt.executeQuery()) {
				while (res.next()) {
					list.add(extractMenuFromResultSet(res));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("getByRestaurant failed", e);
		}
		return list;
	}

	@Override
	public List<Menu> getAll() {
		List<Menu> list = new ArrayList<>();
		try (Connection connection = DBConnection.getConnection();
				Statement stmt = connection.createStatement();
				ResultSet res = stmt.executeQuery(SELECT_ALL_QUERY)) {

			while (res.next()) {
				list.add(extractMenuFromResultSet(res));
			}
		} catch (SQLException e) {
			throw new RuntimeException("getAll failed", e);
		}
		return list;
	}

	@Override
	public boolean updateMenu(Menu menu) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(UPDATE_QUERY)) {

			pstmt.setString(1, menu.getItemName());
			pstmt.setString(2, menu.getDescription());
			pstmt.setDouble(3, menu.getPrice());
			pstmt.setBoolean(4, menu.isAvailable());
			pstmt.setString(5, menu.getImagePath());
			pstmt.setInt(6, menu.getMenuId());
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException("updateMenu failed", e);
		}
	}

	@Override
	public boolean deleteMenu(int menuId) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(DELETE_QUERY)) {

			pstmt.setInt(1, menuId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException("deleteMenu failed", e);
		}
	}

	// reads ONE row; the caller moves the cursor with res.next()
	private Menu extractMenuFromResultSet(ResultSet res) throws SQLException {
		Menu menu = new Menu();
		menu.setMenuId(res.getInt("MenuId"));
		menu.setRestaurantId(res.getInt("RestaurantId"));
		menu.setItemName(res.getString("ItemName"));
		menu.setDescription(res.getString("Description"));
		menu.setPrice(res.getDouble("Price"));
		menu.setAvailable(res.getBoolean("IsAvailable"));
		menu.setRatings(res.getDouble("ratings"));
		menu.setImagePath(res.getString("ImagePath"));
		return menu;
	}

	public List<Menu> getMenuByRestaurantId(int id) {
		return getByRestaurant(id, false);
	}

	@Override
	public List<Menu> getAllMenu() {
		List<Menu> allMenu = new ArrayList<>();
		try (Connection connection = DBConnection.getConnection();
				Statement stmt = connection.createStatement();
				ResultSet res = stmt.executeQuery(SELECT_ALL_QUERY)) {

			while (res.next()) {
				allMenu.add(extractMenuFromResultSet(res));
			}
		} catch (SQLException e) {
			throw new RuntimeException("getAllMenu failed", e);
		}
		return allMenu;
	}
}