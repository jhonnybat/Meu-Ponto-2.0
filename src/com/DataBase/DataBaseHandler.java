package com.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHandler extends SQLiteOpenHelper {

	// Logcat tag
//	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "FolhaPonto";

	// Table Names
	private static final String TABLE_PONTO = "PONTO";
	private static final String TABLE_DATAS = "DATAS";
	private static final String TABLE_INFO_USUARIO = "INFOUSUARIO";

	// Table Create Statements
	// PONTO table create statement
	private static final String CREATE_TABLE_PONTO = "CREATE TABLE IF NOT EXISTS FOLHAPONTO (_id INTEGER PRIMARY KEY AUTOINCREMENT, idDATA INT NOT NULL REFERENCES DATA(_id), HORAINICIO VARCHAR ( 8 ))";
	// Tag table create statement
	private static final String CREATE_TABLE_DATAS = "CREATE TABLE IF NOT EXISTS DATAS ( _id INTEGER PRIMARY KEY AUTOINCREMENT , DATAS VARCHAR ( 10 ) NOT NULL )";
	// todo_tag table create statement
	private static final String CREATE_TABLE_INFO_USUARIO = "CREATE TABLE IF NOT EXISTS INFOUSUARIO (_id INTEGER PRIMARY KEY NOT NULL, cargahoraria INTEGER NOT NULL)";
			

	public DataBaseHandler(Context context) {
		super(context, DATABASE_NAME,null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
		db.execSQL(CREATE_TABLE_PONTO);
		db.execSQL(CREATE_TABLE_DATAS);
		db.execSQL(CREATE_TABLE_INFO_USUARIO);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PONTO);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATAS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_INFO_USUARIO);

		// create new tables
		onCreate(db);
	}

	// closing database
	public void close() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	public int inserirOuEditar(Object object) {
		// TODO Auto-generated method stub
		return 0;
	}
}
