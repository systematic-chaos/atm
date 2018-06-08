package corp.katet.atm.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import corp.katet.atm.domain.User;

public class UserDAOImpl extends DAOImpl implements UserDAO {

	private static UserDAOImpl daoi = null;

	private UserDAOImpl(Context context) {
		this.context = context;
		obtainWritableDatabase();
	}

	public static UserDAOImpl getInstance(Context context) {
		if (daoi == null) {
			daoi = new UserDAOImpl(context);
		}
		return daoi;
	}

	@Override
	public boolean insertUser(User newUser) {
		boolean result;
		ContentValues columnValues = new ContentValues();
		columnValues.put("account_number", newUser.getAccountNumber());
		columnValues.put("pin", newUser.getPin());
		long id = db.insert("user", null, columnValues);
		if (result = (id > 0)) {
			newUser.setIdUser(id);
		}
		return result;
	}

	@Override
	public boolean updateUser(User user) {
		ContentValues columnValues = new ContentValues();
		columnValues.put("account_number", user.getAccountNumber());
		columnValues.put("pin", user.getPin());
		columnValues.put("balance", user.getCurrentBalance());
		String[] params = { user.getIdUser().toString() };
		return db.update("user", columnValues, "id_user = ?", params) > 0;
	}

	@Override
	public boolean deleteUser(User user) {
		String[] params = { user.getIdUser().toString() };
		return db.delete("user", "id_user = ?", params) > 0;
	}

	@Override
	public User getUserFromId(long userId) {
		User resultUser = null;
		String[] params = { String.valueOf(userId) };
		Cursor c = db.query("user", null, "id_user = ?", params, null, null,
				null);
		if (c.moveToFirst()) {
			List<String> columnNames = Arrays.asList(c.getColumnNames());
			String accountNumber = c.getString(columnNames
					.indexOf("account_number"));
			int pin = c.getInt(columnNames.indexOf("pin"));
			float balance = c.getFloat(columnNames.indexOf("balance"));
			resultUser = new User(userId, accountNumber, pin, balance);
		}
		c.close();
		return resultUser;
	}

	@Override
	public User getUserFromAccount(String userAccount) {
		User resultUser = null;
		String[] params = { userAccount };
		Cursor c = db.query("user", null, "account_number = ?", params, null,
				null, null);
		if (c.moveToFirst()) {
			List<String> columnNames = Arrays.asList(c.getColumnNames());
			long userId = c.getLong(columnNames.indexOf("id_user"));
			int pin = c.getInt(columnNames.indexOf("pin"));
			float balance = c.getFloat(columnNames.indexOf("balance"));
			resultUser = new User(userId, userAccount, pin, balance);
		}
		c.close();
		return resultUser;
	}

	@Override
	public boolean updatePin(User user) {
		ContentValues columnValues = new ContentValues();
		columnValues.put("pin", user.getPin());
		String[] params = { user.getIdUser().toString() };
		return db.update("user", columnValues, "id_user = ?", params) > 0;
	}

	@Override
	public Set<User> queryAllUsers() {
		Set<User> result = new HashSet<User>();
		Cursor c = db.query("user", null, null, null, null, null, null);
		while (c.moveToNext()) {
			List<String> columnNames = Arrays.asList(c.getColumnNames());
			long idUser = c.getLong(columnNames.indexOf("id_user"));
			String accountNumber = c.getString(columnNames
					.indexOf("account_number"));
			int pin = c.getInt(columnNames.indexOf("pin"));
			float balance = c.getFloat(columnNames.indexOf("balance"));
			result.add(new User(idUser, accountNumber, pin, balance));
		}
		c.close();
		return result;
	}
}
