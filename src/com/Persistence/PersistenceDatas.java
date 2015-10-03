package com.Persistence;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.DataBase.DataBaseHandler;
import com.Entidades.Datas;

public class PersistenceDatas extends DataBaseHandler {

	public static final String TABELA = "DATAS";
	SQLiteDatabase db;
	public PersistenceDatas(Context context) {
	   super(context);
	   db = this.getWritableDatabase();
	}

	@Override
	public int inserirOuEditar(Object object) {
		Datas dat = (Datas) object;
		int retorno = -1;
		try {
			db.beginTransaction();
			retorno = buscaId(dat);
			if(retorno == -1)
				retorno = (int)db.insert(TABELA, null, getContentValues(dat));	
			db.setTransactionSuccessful();
		} catch (Exception e) {
			db.endTransaction();
			Log.e("ERRO", e.toString());
			return retorno;
		}finally{
			db.endTransaction();
		}
		return retorno;
	}

		
	public boolean buscar(Datas dat ) {
		Cursor c = db.query(TABELA,  null, "DATAS like ?" ,new String[]{dat.getData()},null,null,null);
		boolean retorno = false;
		retorno = c.getCount() > 0;
		c.close();
		return retorno;
	}

	private ContentValues getContentValues(Datas dat){
		ContentValues val = new ContentValues();
		String id = null;
		val.put("_id", id);
		val.put("DATAS", dat.getData());
		
		return val;
	}

	public int buscaId(Datas dat) {
		Cursor c = db.query(TABELA,  null, "DATAS like ?" ,new String[]{dat.getData()},null,null,null);
		int retorno = -1;
		c.moveToFirst();
		if(c.getCount() > 0)
			retorno = c.getInt(c.getColumnIndex("_id"));
		c.close();
		return retorno;
	}
	
	public ArrayList<Datas> buscarDatas() {
		Cursor c = db.query(TABELA,  null, null ,null,null,null,null);
		ArrayList<Datas> retorno = new ArrayList<Datas>();
		c.moveToFirst();
		if(c.getCount() > 0)
			retorno.add(new Datas(c.getInt(c.getColumnIndex("_id")),c.getString(c.getColumnIndex("DATAS"))));
		c.close();
		return retorno;
	}

	public ArrayList<Datas> recuperaMes(String data) {
		Cursor c = db.query(TABELA,  null, "DATAS like ?" ,new String[]{data},null,null,"DATAS desc");
		ArrayList<Datas> retorno = new ArrayList<Datas>();
		c.moveToFirst();
		for(int i =0;i < c.getCount();i++){
			retorno.add(new Datas(c.getInt(c.getColumnIndex("_id")),c.getString(c.getColumnIndex("DATAS"))));
			c.moveToNext();
		}
		c.close();
		return retorno;
	}

	public void deleteDatas() {
		db.delete(TABELA, null, null);
		
	}
}
