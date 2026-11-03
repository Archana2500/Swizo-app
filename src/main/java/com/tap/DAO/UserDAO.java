package com.tap.DAO;

import java.util.List;
import com.tap.model.User;

public interface UserDAO {
	int addUser(User user);

	User getById(int userId);

	User getByEmail(String email);

	List<User> getAll();

	boolean updateUser(User user);

	boolean updatePassword(int userId, String hashedPassword);

	void updateLastLogin(int userId);

	boolean deleteUser(int userId);
}