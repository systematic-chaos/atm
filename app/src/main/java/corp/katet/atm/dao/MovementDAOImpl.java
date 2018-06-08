package corp.katet.atm.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import corp.katet.atm.domain.Movement;
import corp.katet.atm.domain.User;

public class MovementDAOImpl extends DAOImpl implements MovementDAO {

	private static MovementDAOImpl daoi = null;

	private MovementDAOImpl(Context context) {
		this.context = context;
		obtainWritableDatabase();
	}

	public static MovementDAOImpl getInstance(Context context) {
		if (daoi == null) {
			daoi = new MovementDAOImpl(context);
		}
		return daoi;
	}

	@Override
	public boolean insertMovement(Movement newMovement) {
		boolean result;
		ContentValues columnValues = new ContentValues();
		if (newMovement.getUserTo() != null) {
			columnValues.put("id_usr_to", newMovement.getUserTo().getIdUser());
		}
		if (newMovement.getUserFrom() != null) {
			columnValues.put("id_usr_from", newMovement.getUserFrom()
					.getIdUser());
		}
		columnValues.put("date", dateToString(newMovement.getDate()));
		columnValues.put("amount", newMovement.getAmount());
		long id = db.insert("movement", null, columnValues);
		if (result = (id > 0)) {
			newMovement.setIdMovement(id);
		}
		return result;
	}

	@Override
	public boolean updateMovement(Movement movement) {
		ContentValues columnValues = new ContentValues();
		columnValues.put("id_usr_to", movement.getUserFrom().getIdUser());
		columnValues.put("id_usr_from", movement.getUserTo().getIdUser());
		columnValues.put("date", dateToString(movement.getDate()));
		columnValues.put("amount", movement.getAmount());
		String[] params = { movement.getIdMovement().toString() };
		return db.update("movement", columnValues, "id_movement = ?", params) > 0;
	}

	@Override
	public boolean deleteMovement(Movement movement) {
		String[] params = { movement.getIdMovement().toString() };
		return db.delete("movement", "id_movement = ?", params) > 0;
	}

	@Override
	public Movement getMovementFromId(long idMovement) {
		Movement resultMovement = null;
		String[] params = { String.valueOf(idMovement) };
		Cursor c = db.query("movement", null, "id_movement = ?", params, null,
				null, null);
		if (c.moveToFirst()) {
			List<String> columnNames = Arrays.asList(c.getColumnNames());
			UserDAO userDao = UserDAOImpl.getInstance(context);
			User userFrom = userDao.getUserFromId(c.getLong(columnNames
					.indexOf("id_usr_from")));
			User userTo = userDao.getUserFromId(c.getLong(columnNames
					.indexOf("id_usr_to")));
			Date date = stringToDate(c.getString(columnNames.indexOf("date")));
			float amount = c.getFloat(columnNames.indexOf("amount"));
			resultMovement = new Movement(idMovement, userFrom, userTo, date,
					amount);
		}
		return resultMovement;
	}

	@Override
	public List<Movement> queryMovements(User user) {
		List<Movement> result = new ArrayList<Movement>();
		String[] params = { String.valueOf(user.getIdUser()),
				String.valueOf(user.getIdUser()) };
		Cursor c = db.query("movement", null,
				"id_usr_from = ? OR id_usr_to = ?", params, null, null,
				"date DESC");
		while (c.moveToNext()) {
			List<String> columnNames = Arrays.asList(c.getColumnNames());
			long idMovement = c.getLong(columnNames.indexOf("id_movement"));
			UserDAO userDao = UserDAOImpl.getInstance(context);
			User userFrom = userDao.getUserFromId(c.getLong(columnNames
					.indexOf("id_usr_from")));
			User userTo = userDao.getUserFromId(c.getLong(columnNames
					.indexOf("id_usr_to")));
			Date date = stringToDate(c.getString(columnNames.indexOf("date")));
			float amount = c.getFloat(columnNames.indexOf("amount"));
			result.add(new Movement(idMovement, userFrom, userTo, date, amount));
		}
		return result;
	}

	@Override
	public List<Movement> queryMovements(User user, Date startDate, Date endDate) {
		List<Movement> result = new ArrayList<Movement>();
		String[] params = { String.valueOf(user.getIdUser()),
				String.valueOf(user.getIdUser()), dateToString(startDate),
				dateToString(endDate) };
		Cursor c = db
				.query("movement",
						null,
						"(id_usr_from = ? OR id_usr_to = ?) AND (date BETWEEN ? AND ?)",
						params, null, null, "date DESC");
		while (c.moveToNext()) {
			List<String> columnNames = Arrays.asList(c.getColumnNames());
			long idMovement = c.getLong(columnNames.indexOf("id_movement"));
			UserDAO userDao = UserDAOImpl.getInstance(context);
			User userFrom = userDao.getUserFromId(c.getLong(columnNames
					.indexOf("id_usr_from")));
			User userTo = userDao.getUserFromId(c.getLong(columnNames
					.indexOf("id_usr_to")));
			Date date = stringToDate(c.getString(columnNames.indexOf("date")));
			float amount = c.getFloat(columnNames.indexOf("amount"));
			result.add(new Movement(idMovement, userFrom, userTo, date, amount));
		}
		return result;
	}

	@Override
	public List<Movement> queryOutMovements(User outUser) {
		List<Movement> result = new ArrayList<Movement>();
		String[] params = { String.valueOf(outUser.getIdUser()) };
		Cursor c = db.query("movement", null, "id_usr_from = ?", params, null,
				null, "date DESC");
		while (c.moveToNext()) {
			List<String> columnNames = Arrays.asList(c.getColumnNames());
			long idMovement = c.getLong(columnNames.indexOf("id_movement"));
			UserDAO userDao = UserDAOImpl.getInstance(context);
			User userFrom = userDao.getUserFromId(c.getLong(columnNames
					.indexOf("id_usr_from")));
			User userTo = userDao.getUserFromId(c.getLong(columnNames
					.indexOf("id_usr_to")));
			Date date = stringToDate(c.getString(columnNames.indexOf("date")));
			float amount = c.getFloat(columnNames.indexOf("amount"));
			result.add(new Movement(idMovement, userFrom, userTo, date, amount));
		}
		return result;
	}

	@Override
	public List<Movement> queryOutMovements(User outUser, Date startDate,
			Date endDate) {
		List<Movement> result = new ArrayList<Movement>();
		String[] params = { String.valueOf(outUser.getIdUser()),
				dateToString(startDate), dateToString(endDate) };
		Cursor c = db.query("movement", null,
				"id_usr_from = ? AND (date BETWEEN ? AND ?)", params, null,
				null, "date DESC");
		while (c.moveToNext()) {
			List<String> columnNames = Arrays.asList(c.getColumnNames());
			long idMovement = c.getLong(columnNames.indexOf("id_movement"));
			UserDAO userDao = UserDAOImpl.getInstance(context);
			User userFrom = userDao.getUserFromId(c.getLong(columnNames
					.indexOf("id_usr_from")));
			User userTo = userDao.getUserFromId(c.getLong(columnNames
					.indexOf("id_usr_to")));
			Date date = stringToDate(c.getString(columnNames.indexOf("date")));
			float amount = c.getFloat(columnNames.indexOf("amount"));
			result.add(new Movement(idMovement, userFrom, userTo, date, amount));
		}
		return result;
	}

	@Override
	public List<Movement> queryInMovements(User inUser) {
		List<Movement> result = new ArrayList<Movement>();
		String[] params = { String.valueOf(inUser.getIdUser()) };
		Cursor c = db.query("movement", null, "id_usr_to = ?", params, null,
				null, "date DESC");
		while (c.moveToNext()) {
			List<String> columnNames = Arrays.asList(c.getColumnNames());
			long idMovement = c.getLong(columnNames.indexOf("id_movement"));
			UserDAO userDao = UserDAOImpl.getInstance(context);
			User userFrom = userDao.getUserFromId(c.getLong(columnNames
					.indexOf("id_usr_from")));
			User userTo = userDao.getUserFromId(c.getLong(columnNames
					.indexOf("id_usr_to")));
			Date date = stringToDate(c.getString(columnNames.indexOf("date")));
			float amount = c.getFloat(columnNames.indexOf("amount"));
			result.add(new Movement(idMovement, userFrom, userTo, date, amount));
		}
		return result;
	}

	@Override
	public List<Movement> queryInMovements(User inUser, Date startDate,
			Date endDate) {
		List<Movement> result = new ArrayList<Movement>();
		String[] params = { String.valueOf(inUser.getIdUser()),
				dateToString(startDate), dateToString(endDate) };
		Cursor c = db.query("movement", null,
				"id_usr_to = ? AND (date BETWEEN ? AND ?)", params, null, null,
				"date DESC");
		while (c.moveToNext()) {
			List<String> columnNames = Arrays.asList(c.getColumnNames());
			long idMovement = c.getLong(columnNames.indexOf("id_movement"));
			UserDAO userDao = UserDAOImpl.getInstance(context);
			User userFrom = userDao.getUserFromId(c.getLong(columnNames
					.indexOf("id_usr_from")));
			User userTo = userDao.getUserFromId(c.getLong(columnNames
					.indexOf("id_usr_to")));
			Date date = stringToDate(c.getString(columnNames.indexOf("date")));
			float amount = c.getFloat(columnNames.indexOf("amount"));
			result.add(new Movement(idMovement, userFrom, userTo, date, amount));
		}
		return result;
	}

	@Override
	public List<Movement> queryAllMovements() {
		List<Movement> result = new ArrayList<Movement>();
		Cursor c = db.query("movement", null, null, null, null, null,
				"date DESC");
		while (c.moveToNext()) {
			List<String> columnNames = Arrays.asList(c.getColumnNames());
			long idMovement = c.getLong(columnNames.indexOf("id_movement"));
			UserDAO userDao = UserDAOImpl.getInstance(context);
			User userFrom = userDao.getUserFromId(c.getLong(columnNames
					.indexOf("id_usr_from")));
			User userTo = userDao.getUserFromId(c.getLong(columnNames
					.indexOf("id_usr_to")));
			Date date = stringToDate(c.getString(columnNames.indexOf("date")));
			float amount = c.getFloat(columnNames.indexOf("amount"));
			result.add(new Movement(idMovement, userFrom, userTo, date, amount));
		}
		return result;
	}

	public static String dateToString(Date date) {
		String result = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",
				Locale.getDefault());
		result = sdf.format(date);
		return result;
	}

	public static Date stringToDate(String dateStr) {
		Date result = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",
				Locale.getDefault());
		try {
			result = sdf.parse(dateStr);
		} catch (ParseException e) {
		}
		return result;
	}
}
