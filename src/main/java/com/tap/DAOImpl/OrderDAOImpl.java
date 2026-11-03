
package com.tap.DAOImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tap.DAO.OrderDAO;
import com.tap.model.Order;
import com.tap.model.OrderItem;
import com.tap.model.OrderItemView;
import com.tap.model.OrderView;
import com.tap.util.DBConnection;

public class OrderDAOImpl implements OrderDAO {

	// Use the actual database table name: `order`
	// order_Date is filled by the database default.

	private static final String INSERT_ORDER_QUERY = "INSERT INTO `order` (RestaurantId, userId, totalamount, status, paymentMode) "
			+ "VALUES (?, ?, ?, ?, ?)";

	private static final String INSERT_ITEM_QUERY = "INSERT INTO orderitem (orderId, MenuId, quantity, totalamount) "
			+ "VALUES (?, ?, ?, ?)";

	private static final String SELECT_BY_ID_QUERY = "SELECT * FROM `order` WHERE orderId = ?";

	private static final String SELECT_ALL_QUERY = "SELECT * FROM `order` ORDER BY order_Date DESC";

	private static final String SELECT_BY_USER_QUERY = "SELECT * FROM `order` WHERE userId = ? ORDER BY order_Date DESC";

	private static final String SELECT_BY_RESTAURANT_QUERY = "SELECT * FROM `order` WHERE RestaurantId = ? ORDER BY order_Date DESC";

	private static final String SELECT_BY_AGENT_QUERY = "SELECT * FROM `order` WHERE agentId = ? ORDER BY order_Date DESC";

	private static final String UPDATE_STATUS_QUERY = "UPDATE `order` SET status = ? WHERE orderId = ?";

	private static final String ASSIGN_AGENT_QUERY = "UPDATE `order` SET agentId = ? WHERE orderId = ?";

	private static final String DELETE_QUERY = "DELETE FROM `order` WHERE orderId = ?";

	// Display-ready queries (restaurant + delivery agent)

	private static final String VIEW_BASE_QUERY = "SELECT o.orderId, o.RestaurantId, r.name AS restaurantName, "
			+ "r.ImagePath AS restaurantImage, o.userId, o.order_Date, " + "o.totalamount, o.status, o.paymentMode, "
			+ "a.name AS agentName, a.phone AS agentPhone " + "FROM `order` o "
			+ "JOIN restaurant r ON r.RestaurantId = o.RestaurantId "
			+ "LEFT JOIN delivery_agent a ON a.agentId = o.agentId ";

	private static final String VIEW_BY_USER_QUERY = VIEW_BASE_QUERY + "WHERE o.userId = ? ORDER BY o.order_Date DESC";

	private static final String VIEW_BY_RESTAURANT_QUERY = VIEW_BASE_QUERY
			+ "WHERE o.RestaurantId = ? ORDER BY o.order_Date DESC";

	private static final String VIEW_BY_ID_QUERY = VIEW_BASE_QUERY + "WHERE o.orderId = ?";

	// --------------------------------------------------
	// PLACE ORDER
	// --------------------------------------------------

	@Override
	public int placeOrder(Order order, List<OrderItem> items) {

		if (items == null || items.isEmpty()) {
			throw new IllegalArgumentException("An order needs at least one item");
		}

		try (Connection connection = DBConnection.getConnection()) {

			connection.setAutoCommit(false);

			try {

				int orderId;

				// Insert order into `order` table

				try (PreparedStatement pstmt = connection.prepareStatement(INSERT_ORDER_QUERY,
						Statement.RETURN_GENERATED_KEYS)) {

					pstmt.setInt(1, order.getRestaurantId());
					pstmt.setInt(2, order.getUserId());
					pstmt.setDouble(3, order.getTotalAmount());

					pstmt.setString(4, order.getStatus() == null ? "PLACED" : order.getStatus());

					pstmt.setString(5, order.getPaymentMode());

					pstmt.executeUpdate();

					try (ResultSet keys = pstmt.getGeneratedKeys()) {

						if (!keys.next()) {
							throw new SQLException("No orderId generated");
						}

						orderId = keys.getInt(1);
					}
				}

				// Insert order items

				try (PreparedStatement ipstmt = connection.prepareStatement(INSERT_ITEM_QUERY)) {

					for (OrderItem item : items) {

						ipstmt.setInt(1, orderId);
						ipstmt.setInt(2, item.getMenuId());
						ipstmt.setInt(3, item.getQuantity());
						ipstmt.setDouble(4, item.getTotalAmount());

						ipstmt.addBatch();
					}

					ipstmt.executeBatch();
				}

				// Commit transaction

				connection.commit();

				return orderId;

			} catch (SQLException e) {

				connection.rollback();

				throw e;

			} finally {

				connection.setAutoCommit(true);
			}

		} catch (SQLException e) {

			throw new RuntimeException("placeOrder failed", e);
		}
	}

	// --------------------------------------------------
	// GET ORDER BY ID
	// --------------------------------------------------

	@Override
	public Order getById(int orderId) {

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(SELECT_BY_ID_QUERY)) {

			pstmt.setInt(1, orderId);

			try (ResultSet res = pstmt.executeQuery()) {

				return res.next() ? extractOrderFromResultSet(res) : null;
			}

		} catch (SQLException e) {

			throw new RuntimeException("getById failed", e);
		}
	}

	// --------------------------------------------------
	// GET ALL ORDERS
	// --------------------------------------------------

	@Override
	public List<Order> getAll() {

		return queryList(SELECT_ALL_QUERY, null);
	}

	// --------------------------------------------------
	// GET ORDERS BY USER
	// --------------------------------------------------

	@Override
	public List<Order> getByUser(int userId) {

		return queryList(SELECT_BY_USER_QUERY, userId);
	}

	// --------------------------------------------------
	// GET ORDERS BY RESTAURANT
	// --------------------------------------------------

	@Override
	public List<Order> getByRestaurant(int restaurantId) {

		return queryList(SELECT_BY_RESTAURANT_QUERY, restaurantId);
	}

	// --------------------------------------------------
	// GET ORDERS BY DELIVERY AGENT
	// --------------------------------------------------

	@Override
	public List<Order> getByAgent(int agentId) {

		return queryList(SELECT_BY_AGENT_QUERY, agentId);
	}

	// --------------------------------------------------
	// COMMON ORDER QUERY
	// --------------------------------------------------

	private List<Order> queryList(String sql, Integer param) {

		List<Order> list = new ArrayList<>();

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql)) {

			if (param != null) {
				pstmt.setInt(1, param);
			}

			try (ResultSet res = pstmt.executeQuery()) {

				while (res.next()) {

					list.add(extractOrderFromResultSet(res));
				}
			}

		} catch (SQLException e) {

			throw new RuntimeException("order query failed", e);
		}

		return list;
	}

	// --------------------------------------------------
	// UPDATE ORDER STATUS
	// --------------------------------------------------

	@Override
	public boolean updateStatus(int orderId, String status) {

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(UPDATE_STATUS_QUERY)) {

			pstmt.setString(1, status);
			pstmt.setInt(2, orderId);

			return pstmt.executeUpdate() > 0;

		} catch (SQLException e) {

			throw new RuntimeException("updateStatus failed", e);
		}
	}

	// --------------------------------------------------
	// ASSIGN DELIVERY AGENT
	// --------------------------------------------------

	@Override
	public boolean assignAgent(int orderId, int agentId) {

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(ASSIGN_AGENT_QUERY)) {

			pstmt.setInt(1, agentId);
			pstmt.setInt(2, orderId);

			return pstmt.executeUpdate() > 0;

		} catch (SQLException e) {

			throw new RuntimeException("assignAgent failed", e);
		}
	}

	// --------------------------------------------------
	// DELETE ORDER
	// --------------------------------------------------

	@Override
	public boolean deleteOrder(int orderId) {

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(DELETE_QUERY)) {

			pstmt.setInt(1, orderId);

			return pstmt.executeUpdate() > 0;

		} catch (SQLException e) {

			throw new RuntimeException("deleteOrder failed", e);
		}
	}

	// --------------------------------------------------
	// EXTRACT ORDER FROM RESULT SET
	// --------------------------------------------------

	private Order extractOrderFromResultSet(ResultSet res) throws SQLException {

		Order order = new Order();

		order.setOrderId(res.getInt("orderId"));
		order.setRestaurantId(res.getInt("RestaurantId"));
		order.setUserId(res.getInt("userId"));
		order.setOrderDate(res.getTimestamp("order_Date"));
		order.setTotalAmount(res.getDouble("totalamount"));
		order.setStatus(res.getString("status"));
		order.setPaymentMode(res.getString("paymentMode"));

		int agent = res.getInt("agentId");

		order.setAgentId(res.wasNull() ? null : agent);

		return order;
	}

	// --------------------------------------------------
	// DISPLAY-READY ORDER VIEWS
	// --------------------------------------------------

	@Override
	public List<OrderView> getViewsByUser(int userId) {

		return queryViews(VIEW_BY_USER_QUERY, userId);
	}

	@Override
	public List<OrderView> getViewsByRestaurant(int restaurantId) {

		return queryViews(VIEW_BY_RESTAURANT_QUERY, restaurantId);
	}

	// --------------------------------------------------
	// COMMON VIEW QUERY
	// --------------------------------------------------

	private List<OrderView> queryViews(String sql, int param) {

		List<OrderView> list = new ArrayList<>();

		try (Connection connection = DBConnection.getConnection()) {

			try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

				pstmt.setInt(1, param);

				try (ResultSet res = pstmt.executeQuery()) {

					while (res.next()) {

						list.add(extractViewFromResultSet(res));
					}
				}
			}

			loadItems(connection, list);

		} catch (SQLException e) {

			throw new RuntimeException("order view query failed", e);
		}

		return list;
	}

	// --------------------------------------------------
	// GET ORDER VIEW BY ID
	// --------------------------------------------------

	@Override
	public OrderView getViewById(int orderId) {

		try (Connection connection = DBConnection.getConnection()) {

			OrderView view = null;

			try (PreparedStatement pstmt = connection.prepareStatement(VIEW_BY_ID_QUERY)) {

				pstmt.setInt(1, orderId);

				try (ResultSet res = pstmt.executeQuery()) {

					if (res.next()) {

						view = extractViewFromResultSet(res);
					}
				}
			}

			if (view == null) {
				return null;
			}

			List<OrderView> one = new ArrayList<>();

			one.add(view);

			loadItems(connection, one);

			return view;

		} catch (SQLException e) {

			throw new RuntimeException("getViewById failed", e);
		}
	}

	// --------------------------------------------------
	// LOAD ITEMS FOR ORDERS
	// --------------------------------------------------

	private void loadItems(Connection connection, List<OrderView> views) throws SQLException {

		if (views.isEmpty()) {
			return;
		}

		Map<Integer, OrderView> byId = new HashMap<>();

		StringBuilder marks = new StringBuilder();

		for (OrderView v : views) {

			byId.put(v.getOrderId(), v);

			if (marks.length() > 0) {
				marks.append(',');
			}

			marks.append('?');
		}

		String sql = "SELECT oi.orderId, m.ItemName, oi.quantity, oi.totalamount " + "FROM orderitem oi "
				+ "JOIN menu m ON m.MenuId = oi.MenuId " + "WHERE oi.orderId IN (" + marks + ") "
				+ "ORDER BY oi.orderitemid";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

			int i = 1;

			for (OrderView v : views) {

				pstmt.setInt(i++, v.getOrderId());
			}

			try (ResultSet res = pstmt.executeQuery()) {

				while (res.next()) {

					OrderItemView item = new OrderItemView();

					item.setItemName(res.getString("ItemName"));
					item.setQuantity(res.getInt("quantity"));
					item.setItemTotal(res.getDouble("totalamount"));

					byId.get(res.getInt("orderId")).getItems().add(item);
				}
			}
		}
	}

	// --------------------------------------------------
	// EXTRACT ORDER VIEW
	// --------------------------------------------------

	private OrderView extractViewFromResultSet(ResultSet res) throws SQLException {

		OrderView v = new OrderView();

		v.setOrderId(res.getInt("orderId"));
		v.setRestaurantId(res.getInt("RestaurantId"));
		v.setRestaurantName(res.getString("restaurantName"));
		v.setRestaurantImage(res.getString("restaurantImage"));
		v.setUserId(res.getInt("userId"));
		v.setOrderDate(res.getTimestamp("order_Date"));
		v.setTotalAmount(res.getDouble("totalamount"));
		v.setStatus(res.getString("status"));
		v.setPaymentMode(res.getString("paymentMode"));
		v.setAgentName(res.getString("agentName"));
		v.setAgentPhone(res.getString("agentPhone"));

		return v;
	}
}
