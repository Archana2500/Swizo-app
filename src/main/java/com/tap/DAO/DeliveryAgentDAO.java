package com.tap.DAO;

import com.tap.model.DeliveryAgent;
import java.util.List;

public interface DeliveryAgentDAO {
	int addAgent(DeliveryAgent a);

	DeliveryAgent getById(int agentId);

	DeliveryAgent getByEmail(String email);

	List<DeliveryAgent> getActiveAgents();

	boolean updatePassword(int agentId, String hashedPassword);

	boolean setActive(int agentId, boolean active);

	boolean deleteAgent(int agentId);

	List<DeliveryAgent> getActive();

	List<DeliveryAgent> getAll();
}
