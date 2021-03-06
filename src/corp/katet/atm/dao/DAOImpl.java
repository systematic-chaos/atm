package corp.katet.atm.dao;

import corp.katet.atm.util.DBOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class DAOImpl {

	protected Context context;
	protected SQLiteDatabase db;
	
	protected void obtainReadableDatabase() {
		db = DBOpenHelper.getInstance(context).getReadableDatabase();
	}
	
	protected void obtainWritableDatabase() {
		db = DBOpenHelper.getInstance(context).getWritableDatabase();
	}
}
