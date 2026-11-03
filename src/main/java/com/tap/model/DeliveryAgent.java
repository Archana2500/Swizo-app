package com.tap.model;

public class DeliveryAgent {
	private int agentId;
	private String name, phone, email, password;
	private boolean active;

	public DeliveryAgent(int agentId, String name, String phone, String email, String password, boolean active) {
		super();
		this.agentId = agentId;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.password = password;
		this.active = active;
	}

	public DeliveryAgent() {
		// TODO Auto-generated constructor stub
	}
	
	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "DeliveryAgent [agentId=" + agentId + ", name=" + name + ", phone=" + phone + ", email=" + email
				+ ", password=" + password + ", active=" + active + "]";
	}

}