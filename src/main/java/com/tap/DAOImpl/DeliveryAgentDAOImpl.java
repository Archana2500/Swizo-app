
package com.tap.DAOImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tap.DAO.DeliveryAgentDAO;
import com.tap.model.DeliveryAgent;
import com.tap.util.DBConnection;

public class DeliveryAgentDAOImpl implements DeliveryAgentDAO {

	private static final String COLS = "agentId, name, phone, email, password, active";

	private static final String INSERT_QUERY = "INSERT INTO delivery_agent(name, phone, email, password, active) "
			+ "VALUES (?, ?, ?, ?, ?)";

	private static final String SELECT_BY_ID_QUERY = "SELECT " + COLS + " FROM delivery_agent WHERE agentId = ?";

	private static final String SELECT_BY_EMAIL_QUERY = "SELECT " + COLS + " FROM delivery_agent WHERE email = ?";

	private static final String SELECT_ALL_QUERY = "SELECT " + COLS + " FROM delivery_agent ORDER BY name";

	private static final String SELECT_ACTIVE_QUERY = "SELECT " + COLS
			+ " FROM delivery_agent WHERE active = 1 ORDER BY name";

	private static final String SET_ACTIVE_QUERY = "UPDATE delivery_agent SET active = ? WHERE agentId = ?";

	private static final String UPDATE_PASSWORD_QUERY = "UPDATE delivery_agent SET password = ? WHERE agentId = ?";

	// ADD DELIVERY AGENT
	@Override
	public int addAgent(DeliveryAgent a) {

		try (Connection c = DBConnection.getConnection()) {

			if (c == null) {
				return 0;
			}

			try (PreparedStatement ps = c.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {

				ps.setString(1, a.getName());
				ps.setString(2, a.getPhone());
				ps.setString(3, a.getEmail());
				ps.setString(4, a.getPassword());
				ps.setBoolean(5, a.isActive());

				ps.executeUpdate();

				try (ResultSet keys = ps.getGeneratedKeys()) {
					return keys.next() ? keys.getInt(1) : 0;
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException("addAgent failed", e);
		}
	}

	// GET AGENT BY ID
	@Override
	public DeliveryAgent getById(int agentId) {

		try (Connection c = DBConnection.getConnection()) {

			if (c == null)
				return null;

			try (PreparedStatement ps = c.prepareStatement(SELECT_BY_ID_QUERY)) {

				ps.setInt(1, agentId);

				try (ResultSet rs = ps.executeQuery()) {
					return rs.next() ? map(rs) : null;
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException("getById failed", e);
		}
	}

	// GET AGENT BY EMAIL
	@Override
	public DeliveryAgent getByEmail(String email) {

		try (Connection c = DBConnection.getConnection()) {

			if (c == null)
				return null;

			try (PreparedStatement ps = c.prepareStatement(SELECT_BY_EMAIL_QUERY)) {

				ps.setString(1, email);

				try (ResultSet rs = ps.executeQuery()) {
					return rs.next() ? map(rs) : null;
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException("getByEmail failed", e);
		}
	}

	// GET ALL AGENTS
	@Override
	public List<DeliveryAgent> getAll() {
		return list(SELECT_ALL_QUERY);
	}

	// GET ACTIVE AGENTS
	@Override
	public List<DeliveryAgent> getActive() {
		return list(SELECT_ACTIVE_QUERY);
	}

	// GET ACTIVE AGENTS (ALIAS)
	@Override
	public List<DeliveryAgent> getActiveAgents() {
		return getActive();
	}

	// COMMON LIST METHOD
	private List<DeliveryAgent> list(String sql) {

		List<DeliveryAgent> out = new ArrayList<>();

		try (Connection c = DBConnection.getConnection()) {

			if (c == null)
				return out;

			try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

				while (rs.next()) {
					out.add(map(rs));
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException("agent query failed", e);
		}

		return out;
	}

	// ACTIVATE / DEACTIVATE AGENT
	@Override
	public boolean setActive(int agentId, boolean active) {

		try (Connection c = DBConnection.getConnection()) {

			if (c == null)
				return false;

			try (PreparedStatement ps = c.prepareStatement(SET_ACTIVE_QUERY)) {

				ps.setBoolean(1, active);
				ps.setInt(2, agentId);

				return ps.executeUpdate() > 0;
			}

		} catch (SQLException e) {
			throw new RuntimeException("setActive failed", e);
		}
	}

	// UPDATE PASSWORD
	@Override
	public boolean updatePassword(int agentId, String hashedPassword) {

		try (Connection c = DBConnection.getConnection()) {

			if (c == null)
				return false;

			try (PreparedStatement ps = c.prepareStatement(UPDATE_PASSWORD_QUERY)) {

				ps.setString(1, hashedPassword);
				ps.setInt(2, agentId);

				return ps.executeUpdate() > 0;
			}

		} catch (SQLException e) {
			throw new RuntimeException("updatePassword failed", e);
		}
	}

	// DELETE AGENT AND UNASSIGN ORDERS
	@Override
	public boolean deleteAgent(int agentId) {

		try (Connection c = DBConnection.getConnection()) {

			if (c == null)
				return false;

			c.setAutoCommit(false);

			try {

				try (PreparedStatement unassign = c
						.prepareStatement("UPDATE `order` SET agentId = NULL WHERE agentId = ?")) {

					unassign.setInt(1, agentId);
					unassign.executeUpdate();
				}

				boolean deleted;

				try (PreparedStatement delete = c.prepareStatement("DELETE FROM delivery_agent WHERE agentId = ?")) {

					delete.setInt(1, agentId);
					deleted = delete.executeUpdate() > 0;
				}

				c.commit();

				return deleted;

			} catch (SQLException e) {

				c.rollback();
				throw e;

			} finally {

				c.setAutoCommit(true);
			}

		} catch (SQLException e) {
			throw new RuntimeException("deleteAgent failed", e);
		}
	}

	// MAP RESULT SET
	private DeliveryAgent map(ResultSet rs) throws SQLException {

		DeliveryAgent a = new DeliveryAgent();

		a.setAgentId(rs.getInt("agentId"));
		a.setName(rs.getString("name"));
		a.setPhone(rs.getString("phone"));
		a.setEmail(rs.getString("email"));
		a.setPassword(rs.getString("password"));
		a.setActive(rs.getBoolean("active"));

		return a;
	}
}
