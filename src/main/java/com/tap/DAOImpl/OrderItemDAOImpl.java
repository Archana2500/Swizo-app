package com.tap.DAOImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tap.DAO.OrderItemDAO;
import com.tap.model.OrderItem;
import com.tap.util.DBConnection;

public class OrderItemDAOImpl implements OrderItemDAO {

	private static final String INSERT_QUERY = "INSERT INTO orderitem(orderId, MenuId, quantity, totalamount) VALUES (?, ?, ?, ?)";
	private static final String SELECT_BY_ID_QUERY = "SELECT * FROM orderitem WHERE orderitemid = ?";
	private static final String SELECT_BY_ORDER_QUERY = "SELECT * FROM orderitem WHERE orderId = ?";
	private static final String SELECT_ALL_QUERY = "SELECT * FROM orderitem";
	private static final String UPDATE_QUERY = "UPDATE orderitem SET MenuId = ?, quantity = ?, totalamount = ? WHERE orderitemid = ?";
	private static final String DELETE_QUERY = "DELETE FROM orderitem WHERE orderitemid = ?";

	@Override
	public int addOrderItem(OrderItem item) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setInt(1, item.getOrderId());
			pstmt.setInt(2, item.getMenuId());
			pstmt.setInt(3, item.getQuantity());
			pstmt.setDouble(4, item.getTotalAmount());
			pstmt.executeUpdate();

			try (ResultSet keys = pstmt.getGeneratedKeys()) {
				return keys.next() ? keys.getInt(1) : 0;
			}
		} catch (SQLException e) {
			throw new RuntimeException("addOrderItem failed", e);
		}
	}

	@Override
	public OrderItem getById(int orderItemId) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_ID_QUERY)) {

			pstmt.setInt(1, orderItemId);
			try (ResultSet res = pstmt.executeQuery()) {
				return res.next() ? extractOrderItemFromResultSet(res) : null;
			}
		} catch (SQLException e) {
			throw new RuntimeException("getById failed", e);
		}
	}

	@Override
	public List<OrderItem> getByOrder(int orderId) {
		List<OrderItem> list = new ArrayList<>();
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_ORDER_QUERY)) {

			pstmt.setInt(1, orderId);
			try (ResultSet res = pstmt.executeQuery()) {
				while (res.next()) {
					list.add(extractOrderItemFromResultSet(res));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("getByOrder failed", e);
		}
		return list;
	}

	@Override
	public List<OrderItem> getAll() {
		List<OrderItem> list = new ArrayList<>();
		try (Connection connection = DBConnection.getConnection();
				Statement stmt = connection.createStatement();
				ResultSet res = stmt.executeQuery(SELECT_ALL_QUERY)) {

			while (res.next()) {
				list.add(extractOrderItemFromResultSet(res));
			}
		} catch (SQLException e) {
			throw new RuntimeException("getAll failed", e);
		}
		return list;
	}

	@Override
	public boolean updateOrderItem(OrderItem item) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(UPDATE_QUERY)) {

			pstmt.setInt(1, item.getMenuId());
			pstmt.setInt(2, item.getQuantity());
			pstmt.setDouble(3, item.getTotalAmount());
			pstmt.setInt(4, item.getOrderItemId());
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException("updateOrderItem failed", e);
		}
	}

	@Override
	public boolean deleteOrderItem(int orderItemId) {
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(DELETE_QUERY)) {

			pstmt.setInt(1, orderItemId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException("deleteOrderItem failed", e);
		}
	}

	// reads ONE row; the caller moves the cursor with res.next()
	private OrderItem extractOrderItemFromResultSet(ResultSet res) throws SQLException {
		OrderItem item = new OrderItem();
		item.setOrderItemId(res.getInt("orderitemid"));
		item.setOrderId(res.getInt("orderId"));
		item.setMenuId(res.getInt("MenuId"));
		item.setQuantity(res.getInt("quantity"));
		item.setTotalAmount(res.getDouble("totalamount"));
		return item;
	}
}