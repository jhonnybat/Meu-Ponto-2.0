package com.Persistence;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.Entidades.Datas;
import com.Entidades.FolhaPonto;

public class MeuPontoProvider extends ContentProvider {
	
	PersistenceFolhaPonto pFolhaPonto;
	PersistenceDatas pDatas;
	PersistenceInfoUsu pInfoUsu;
	SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
	private static final String BASE_PATH = "FolhaPonto";
	private static final int FOLHAPONTO = 1;
	private static final String AUTHORITY = "com.MeuPonto.MeuPontoProvider";
	private static final UriMatcher sUriMatcher;

	static{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, BASE_PATH, FOLHAPONTO);
	}
	

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (sUriMatcher.match(uri)) {
		case FOLHAPONTO:
			pFolhaPonto = new PersistenceFolhaPonto(this.getContext());
			pDatas = new PersistenceDatas(this.getContext());
			
			SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
			SimpleDateFormat sdfData = new SimpleDateFormat("dd/MM/yyyy");
			Date dia = new Date();
			Datas data = new Datas(sdfData.format(dia));
			try{
				int id = pDatas.inserirOuEditar(data);
				Log.i("INFO", "Valor de id = "+id);
				if(id != -1)
					data.setId(id);
				FolhaPonto fpt = new FolhaPonto();
				fpt.setData(data.getId());
				fpt.setHoraInicio(sdfHora.format(dia.getTime()));
				int idPonto =pFolhaPonto.inserir(fpt);
				Log.i("INFO", "Valor de idPonto = "+idPonto);
				fpt.setId(idPonto);
			}catch (Exception e1) {
				Log.e("ERRO - INSERIR HORA", e1.toString());
				Log.e("ERRO",""+ e1.getCause());
			}finally{
				pDatas.close();
				pFolhaPonto.close();
			}
			//SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
			//Log.i("INFO", pFolhaPonto.toString());
			//pFolhaPonto.inserir(new FolhaPonto(sdfHora.format(new Date()), 1));
			break;
		default:
			Log.e("ERROR", "NÌO DEU!!!");
			break;
		}
		
		
		
		return uri;
	}

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
}
