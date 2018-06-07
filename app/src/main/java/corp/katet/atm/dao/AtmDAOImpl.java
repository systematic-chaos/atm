package corp.katet.atm.dao;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import corp.katet.atm.domain.Atm;

public class AtmDAOImpl extends DAOImpl implements AtmDAO {

	private static AtmDAOImpl daoi = null;

	private AtmDAOImpl(Context context) {
		this.context = context;
		obtainWritableDatabase();
	}

	public static AtmDAOImpl getInstance(Context context) {
		if (daoi == null) {
			daoi = new AtmDAOImpl(context);
		}
		return daoi;
	}

	@Override
	public boolean insertAtm(Atm newAtm) {
		ContentValues columnValues = new ContentValues();
		columnValues.put("id_atm", newAtm.getId());
		columnValues.put("name", newAtm.getName());
		columnValues.put("address", newAtm.getAddress());
		columnValues.put("lat", newAtm.getCoords().latitude);
		columnValues.put("lng", newAtm.getCoords().longitude);
		return db.insert("atm", null, columnValues) > 0;
	}

	@Override
	public boolean updateAtm(Atm atm) {
		ContentValues columnValues = new ContentValues();
		columnValues.put("name", atm.getName());
		columnValues.put("address", atm.getAddress());
		columnValues.put("lat", atm.getCoords().latitude);
		columnValues.put("lng", atm.getCoords().longitude);
		String[] params = { atm.getId() };
		return db.update("atm", columnValues, "id_atm = ?", params) > 0;
	}

	@Override
	public boolean deleteAtm(Atm atm) {
		String[] params = { atm.getId() };
		return db.delete("atm", "id_atm = ?", params) > 0;
	}

	@Override
	public Atm getAtmFromId(String idAtm) {
		Atm resultAtm = null;
		String[] params = { idAtm };
		Cursor c = db
				.query("atm", null, "id_atm = ?", params, null, null, null);
		if (c.moveToFirst()) {
			List<String> columnNames = Arrays.asList(c.getColumnNames());
			String name = c.getString(columnNames.indexOf("name"));
			String address = c.getString(columnNames.indexOf("address"));
			LatLng coords = new LatLng(c.getFloat(columnNames.indexOf("lat")),
					c.getFloat(columnNames.indexOf("lng")));
			resultAtm = new Atm(idAtm, name, address, coords);
		}
		return resultAtm;
	}

	@Override
	public Set<Atm> queryAllAtms() {
		Set<Atm> result = new HashSet<Atm>();
		Cursor c = db.query("atm", null, null, null, null, null, null);
		if (c.moveToFirst()) {
			List<String> columnNames = Arrays.asList(c.getColumnNames());
			String id = c.getString(columnNames.indexOf("id_atm"));
			String name = c.getString(columnNames.indexOf("name"));
			String address = c.getString(columnNames.indexOf("address"));
			LatLng coords = new LatLng(c.getFloat(columnNames.indexOf("lat")),
					c.getFloat(columnNames.indexOf("lng")));
			result.add(new Atm(id, name, address, coords));
		}
		return result;
	}

	@Override
	public Set<Atm> queryAtmsInBounds(LatLngBounds bounds) {
		Set<Atm> result = new HashSet<Atm>();
		String[] params = { String.valueOf(bounds.northeast.latitude),
				String.valueOf(bounds.southwest.latitude),
				String.valueOf(bounds.northeast.longitude),
				String.valueOf(bounds.southwest.longitude) };
		Cursor c = db.query("atm", null,
				"(lat BETWEEN ? AND ?) AND (lng BETWEEN ? AND ?)", params,
				null, null, null);
		if (c.moveToFirst()) {
			List<String> columnNames = Arrays.asList(c.getColumnNames());
			String id = c.getString(columnNames.indexOf("id_atm"));
			String name = c.getString(columnNames.indexOf("name"));
			String address = c.getString(columnNames.indexOf("address"));
			LatLng coords = new LatLng(c.getFloat(columnNames.indexOf("lat")),
					c.getFloat(columnNames.indexOf("lng")));
			result.add(new Atm(id, name, address, coords));
		}
		return result;
	}
}
