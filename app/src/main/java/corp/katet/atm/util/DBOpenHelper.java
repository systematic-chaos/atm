package corp.katet.atm.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import corp.katet.atm.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "katet_corp_db";

	private Context context;
	private static DBOpenHelper dbOpen;

	/**
	 * Constructor privado
	 * 
	 * @param context
	 */
	private DBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		dbOpen = this;
	}

	/**
	 * Obtener una instancia, patrón Singleton
	 * 
	 * @param context
	 *            contexto
	 * @return instancia de la clase
	 */
	public static DBOpenHelper getInstance(Context context) {
		if (dbOpen == null) {
			dbOpen = new DBOpenHelper(context);
		}

		return dbOpen;
	}

	/**
	 * Llamado de forma automática cuando se accede por primera vez. Incluir
	 * aquí todas las creaciones de tablas e inicializaciones
	 * 
	 * @param db
	 *            objeto para manejar la base de datos
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String[] statements = parseScript(context);
		for (String stmt : statements) {
			db.execSQL(stmt);
		}

		insertDummyUser(db);
	}

	/**
	 * Llamado de forma automática cuando se actualiza la versión. Incluir aquí
	 * todas las alteraciones de tablas y datos
	 * 
	 * @param db
	 *            objeto para manejar la base de datos
	 * @param oldVer
	 *            número de versión anterior
	 * @param newVer
	 *            número de nueva versión
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	private String[] parseScript(Context context) {
		ArrayList<String> statements = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		String line;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(context
					.getResources().openRawResource(R.raw.atm)));
			while ((line = br.readLine()) != null) {
				if (line.trim().length() > 0) {
					sb.append(line);
				} else {
					statements.add(sb.toString());
					sb = new StringBuilder();
				}
			}
		} catch (FileNotFoundException fnfe) {
			System.err.println("FileNotFoundException opening file: "
					+ fnfe.getMessage());
		} catch (IOException ioe) {
			System.err.println("IOException reading file lines: "
					+ ioe.getMessage());
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ioe) {
				System.err.println("IOException closing resources: "
						+ ioe.getMessage());
			}
		}
		return statements.toArray(new String[statements.size()]);
	}

	private void insertDummyUser(SQLiteDatabase db) {
		ContentValues columnValues = new ContentValues();
		columnValues.put("account_number", "377437-3224652-7323552");
		columnValues.put("pin", 1234);
		db.insert("user", null, columnValues);
	}
}
