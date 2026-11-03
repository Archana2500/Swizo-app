package com.tap.DAO;

import com.tap.model.Menu;
import java.util.List;

public interface MenuDAO {
	int addMenu(Menu m);

	Menu getById(int menuId);

	List<Menu> getByRestaurant(int restaurantId, boolean onlyAvailable);

	List<Menu> getAll();

	boolean updateMenu(Menu m);

	boolean deleteMenu(int menuId);

	List<Menu> getAllMenu();
}