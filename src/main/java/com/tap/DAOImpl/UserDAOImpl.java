package com.tap.DAOImpl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tap.DAO.UserDAO;
import com.tap.model.User;
import com.tap.util.DBConnection;

public class UserDAOImpl implements UserDAO {
	private static final String INSERT_QUERY = "INSERT INTO user(name, email, password, phone, role, createdDate, lastloginDate, address, profileImage) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String SELECT_BY_ID_QUERY = "SELECT * FROM user WHERE userId = ?";
	private static final String SELECT_BY_EMAIL_QUERY = "SELECT * FROM user WHERE email = ?";
	private static final String SELECT_ALL_QUERY = "SELECT * FROM user";
	private static final String UPDATE_QUERY = "UPDATE user SET name = ?, phone = ?, address = ?, profileImage = ? WHERE userId = ?";
	private static final String UPDATE_PASSWORD_QUERY = "UPDATE user SET password = ? WHERE userId = ?";
	private static final String UPDATE_LAST_LOGIN_QUERY = "UPDATE user SET lastloginDate = ? WHERE userId = ?";
	private static final String DELETE_QUERY = "DELETE FROM user WHERE userId = ?";

	@Override
	public int addUser(User user) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {

			Date today = new Date(System.currentTimeMillis());
			pstmt.setString(1, user.getName());
			pstmt.setString(2, user.getEmail());
			pstmt.setString(3, user.getPassword()); // must already be hashed
			pstmt.setString(4, user.getPhone());
			pstmt.setString(5, user.getRole() == null ? "customer" : user.getRole());
			pstmt.setDate(6, today);
			pstmt.setDate(7, today);
			pstmt.setString(8, user.getAddress());
			pstmt.setString(9, user.getProfileImage());
			pstmt.executeUpdate();

			try (ResultSet keys = pstmt.getGeneratedKeys()) {
				return keys.next() ? keys.getInt(1) : 0;
			}
		} catch (SQLException e) {
			throw new RuntimeException("addUser failed", e);
		}
	}

	@Override
	public User getById(int userId) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_ID_QUERY)) {

			pstmt.setInt(1, userId);
			try (ResultSet res = pstmt.executeQuery()) {
				return res.next() ? extractUserFromResultSet(res) : null;
			}
		} catch (SQLException e) {
			throw new RuntimeException("getById failed", e);
		}
	}

	@Override
	public User getByEmail(String email) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_EMAIL_QUERY)) {

			pstmt.setString(1, email);
			try (ResultSet res = pstmt.executeQuery()) {
				return res.next() ? extractUserFromResultSet(res) : null;
			}
		} catch (SQLException e) {
			throw new RuntimeException("getByEmail failed", e);
		}
	}

	@Override
	public List<User> getAll() {
		List<User> allUsers = new ArrayList<>();
		try (Connection connection = DBConnection.getConnection();
				Statement stmt = connection.createStatement();
				ResultSet res = stmt.executeQuery(SELECT_ALL_QUERY)) {

			while (res.next()) {
				allUsers.add(extractUserFromResultSet(res));
			}
		} catch (SQLException e) {
			throw new RuntimeException("getAll failed", e);
		}
		return allUsers;
	}

	@Override
	public boolean updateUser(User user) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(UPDATE_QUERY)) {

			pstmt.setString(1, user.getName());
			pstmt.setString(2, user.getPhone());
			pstmt.setString(3, user.getAddress());
			pstmt.setString(4, user.getProfileImage());
			pstmt.setInt(5, user.getUserId());
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException("updateUser failed", e);
		}
	}

	@Override
	public boolean updatePassword(int userId, String hashedPassword) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(UPDATE_PASSWORD_QUERY)) {

			pstmt.setString(1, hashedPassword);
			pstmt.setInt(2, userId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException("updatePassword failed", e);
		}
	}

	@Override
	public void updateLastLogin(int userId) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(UPDATE_LAST_LOGIN_QUERY)) {

			pstmt.setDate(1, new Date(System.currentTimeMillis()));
			pstmt.setInt(2, userId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("updateLastLogin failed", e);
		}
	}

	@Override
	public boolean deleteUser(int userId) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(DELETE_QUERY)) {

			pstmt.setInt(1, userId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException("deleteUser failed", e);
		}
	}

	private User extractUserFromResultSet(ResultSet res) throws SQLException {
		User user = new User();
		user.setUserId(res.getInt("userId"));
		user.setName(res.getString("name"));
		user.setEmail(res.getString("email"));
		user.setPassword(res.getString("password"));
		user.setPhone(res.getString("phone"));
		user.setRole(res.getString("role"));
		user.setCreatedDate(res.getDate("createdDate"));
		user.setLastLoginDate(res.getDate("lastloginDate"));
		user.setAddress(res.getString("address"));
		user.setProfileImage(res.getString("profileImage"));
		return user;
	}

}