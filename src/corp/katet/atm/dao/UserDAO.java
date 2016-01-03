package corp.katet.atm.dao;

import java.util.Set;

import corp.katet.atm.domain.User;

public interface UserDAO {

	boolean insertUser(User newUser);

	boolean updateUser(User user);

	boolean deleteUser(User user);

	User getUserFromId(long userId);

	User getUserFromAccount(String userAccount);

	boolean updatePin(User user);

	Set<User> queryAllUsers();
}
