package com.Persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.DataBase.DataBaseHandler;
import com.Entidades.InfoUsu;

public class PersistenceInfoUsu extends DataBaseHandler {
	private static final String TABELA = "INFOUSUARIO";
	SQLiteDatabase db;
	public PersistenceInfoUsu(Context context) {
		super(context);
		db = this.getWritableDatabase();
	}

	public int  inserir(InfoUsu info){
		return (int)db.insert(TABELA, null, getContentValues(info.getCargaHoraria()));
	}
	@Override
	public int inserirOuEditar(Object object) {
		InfoUsu info = (InfoUsu) object;
		int retorno = -1;
		//db.beginTransaction();
		try{
		if(busca()){
			retorno = db.update(TABELA, getContentValues(info.getCargaHoraria()), null , null);
		}else
			retorno = (int)db.insert(TABELA, null, getContentValues(info.getCargaHoraria()));
		}catch (Exception e) {
			//Log.e("ERRO","Inserir ou Editar info usu " + e.toString());
			//db.endTransaction();
		}finally{
		//	db.endTransaction();
		}
		return retorno;
	}

	private ContentValues getContentValues(int cargaHoraria) {
		ContentValues content = new ContentValues();
		content.put("cargahoraria", cargaHoraria);
		return content;
	}

	public boolean busca() {
		Cursor c = db.query(TABELA,null, null ,null,null,null,null);
		boolean retorno = false;
		retorno = c.getCount() > 0;
		c.close();
		return retorno;
	}
	
	public int recuperaJornadaTrabalho() {
		Cursor c = db.query(TABELA,  null, null ,null,null,null,null);
		int retorno = 0;
		c.moveToFirst();
		if(c.getCount() > 0)
		retorno = c.getInt(c.getColumnIndex("cargahoraria"));
		c.close();
		return retorno;
	}

	public void editar(InfoUsu info) {
		 db.update(TABELA, getContentValues(info.getCargaHoraria()), null , null);
	}
	

}