package com.Persistence;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.DataBase.DataBaseHandler;
import com.Entidades.Datas;
import com.Entidades.FolhaPonto;
import com.Funcoes.Funcoes;


public class PersistenceFolhaPonto extends DataBaseHandler{

	public static final String TABELA = "FOLHAPONTO";
	public static final String ContentUri = "content://com.MeuPonto.com.Persistence/";
	SQLiteDatabase db;
	public PersistenceFolhaPonto(Context context) {
		super(context);
		db = this.getWritableDatabase();
	}


	public int  inserir(FolhaPonto fpt){
		return (int)db.insert(TABELA, null, getContentValues(fpt));
	}

	@Override
	public int inserirOuEditar(Object object) {
		FolhaPonto fpt = (FolhaPonto) object;
		int retorno = -1;
		db.beginTransaction();
		try {
			if(buscar(fpt))
				retorno = db.update(TABELA,getContentValuesUpdate(fpt),"_id = ?",new String[]{fpt.getId()+""});
			else
				retorno = (int)db.insert(TABELA, null, getContentValues(fpt));
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

	public boolean buscar(FolhaPonto fpt ) {
		Cursor c = db.query(TABELA,  null, "idData = ? and _id = ?" ,new String[]{fpt.getData()+"" , fpt.getId()+""},null,null,null);
		boolean retorno = false;
		retorno = c.getCount() >0;
		c.close();
		return retorno;
	}

	public FolhaPonto buscar(int data) {			
		Cursor c = db.query(TABELA,  null, "idData = ?" ,new String[]{data+""},null,null,"_id DESC");
		FolhaPonto retorno = new FolhaPonto(); 
		c.moveToFirst();
		if(c.getCount() > 0){
			retorno = new FolhaPonto(c.getInt(c.getColumnIndex("_id")), c.getString(c.getColumnIndex("HORAINICIO")),
					c.getInt(c.getColumnIndex("idDATA")));
		}

		c.close();
		return retorno;
	}




	public FolhaPonto buscarAnterior(String data) {
		Cursor c = db.query(TABELA, null, "idData <= ?", new String[]{data},null, null, "_id desc");
		FolhaPonto retorno = new FolhaPonto(); 
		c.moveToFirst();
		if(c.getCount() > 0){
			retorno = new FolhaPonto(c.getString(c.getColumnIndex("HORAINICIO")),
					c.getInt(c.getColumnIndex("idDATA")));
		}

		c.close();
		return retorno;
	}

	private ContentValues getContentValues(FolhaPonto fpt){
		ContentValues val = new ContentValues();
		String id = null;
		val.put("_id", id);
		val.put("idDATA", fpt.getData());
		val.put("HORAINICIO", fpt.getHoraInicio());

		return val;
	}
	private ContentValues getContentValuesUpdate(FolhaPonto fpt){
		ContentValues val = new ContentValues();
		val.put("idDATA", fpt.getData());
		val.put("HORAINICIO", fpt.getHoraInicio());

		return val;
	}

	public ArrayList<FolhaPonto> recuperaListaMes(int mes) {
		Cursor c = db.query(TABELA,  null, null ,null,null,null,null);
		ArrayList<FolhaPonto> retorno = new ArrayList<FolhaPonto>(); 
		c.moveToFirst();
		for(int i = 0; i < c.getCount();i++){
			retorno.add(new FolhaPonto(c.getInt(c.getColumnIndex("_id")),c.getString(c.getColumnIndex("HORAINICIO"))
					, c.getInt(c.getColumnIndex("idDATA"))));
			c.moveToNext();
		}

		c.close();
		return retorno;
	}


	public List<FolhaPonto> buscarListaDia(int idData) {
		Cursor c = db.rawQuery("Select * from FolhaPonto where idData = ? order by HORAINICIO desc", new String[]{""+idData});
		ArrayList<FolhaPonto> retorno = new ArrayList<FolhaPonto>();
		c.moveToFirst();
		for(int i = 0; i < c.getCount();i++){
			retorno.add(new FolhaPonto(c.getInt(c.getColumnIndex("_id")),c.getString(c.getColumnIndex("HORAINICIO")),
					c.getInt(c.getColumnIndex("idDATA"))));
			c.moveToNext();
		}
		c.close();
		return retorno;
	}
	
	public List<FolhaPonto> buscarFolhaPonto() {
		Cursor c = db.query(TABELA,  null, null ,null,null,null,null);
		ArrayList<FolhaPonto> retorno = new ArrayList<FolhaPonto>();
		c.moveToFirst();
		for(int i = 0; i < c.getCount();i++){
			retorno.add(new FolhaPonto(c.getInt(c.getColumnIndex("_id")),c.getString(c.getColumnIndex("HORAINICIO")),
					c.getInt(c.getColumnIndex("idDATA"))));
			c.moveToNext();
		}
		c.close();
		return retorno;
	}
	

	public boolean deletePonto(FolhaPonto fpt) {
		int result = db.delete(TABELA, "idData = ? and _id = ?", new String[]{fpt.getData()+"" , fpt.getId()+""});

		return result > 0;

	}


	public int calculaHoras(ArrayList<Datas> listDatas) {
		long horaTotal = 0; 
		db.beginTransaction();
		try {
			for(Datas d : listDatas){
				horaTotal += calculaHoraDia(d.getId());
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			db.endTransaction();
			Log.e("ERRO", "Calcular Datas");
			return -1;
		}finally{
			db.endTransaction();
		}
		return (int)horaTotal;
	}

	public long calculaHoraDia(int idData){
		int horaInicio = 0, horaFim = 0;
		long horaTotal = 0; 
		Cursor c = db.rawQuery("Select HORAINICIO as HORA from FolhaPonto where idData = ? order by HORAINICIO desc", new String[]{""+idData});
		int totalRegistro =  c.getCount();
		c.moveToFirst();
		for(int i=0;i<totalRegistro; i++){
			if(i%2 != 0){
				horaFim = Funcoes.getInstance().getHoraMin(c.getString(c.getColumnIndex("HORA")));
				horaTotal += horaFim - horaInicio;
			}
			else
				horaInicio = Funcoes.getInstance().getHoraMin(c.getString(c.getColumnIndex("HORA")));
			c.moveToNext();
		}
		return horaTotal;
	}
	
	

	public void deletePontos() {
		db.delete(TABELA, null, null);
		
	}


}
