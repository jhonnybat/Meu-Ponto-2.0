package com.Funcoes;

import java.io.Serializable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.Entidades.InfoUsu;
import com.MeuPonto.R;
import com.Persistence.PersistenceInfoUsu;

public class Funcoes {

	private int cargaHorariaMinutos;
	
	public  int getCargaHoraria() {
		return cargaHorariaMinutos;
	}

	public  void setCargaHoraria(int cargaHoraria) {
		this.cargaHorariaMinutos = cargaHoraria;
	}
	
	private static Funcoes func = new Funcoes();
	private Funcoes(){}
	
	public static Funcoes getInstance(){
		return func;
	}
	
	
	public void alertaErro(String title, int message,Context context) {	
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(context.getString(message))
		.setCancelable(false)
		.setPositiveButton(context.getText(R.string.sim), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.setTitle(title);
		alert.show();
	}
	
	public void proximaTela(Context context , Class<?> proxima, Serializable serializable, String nomeBundle){
		Intent intent = new Intent(context,proxima);
		Bundle bundle = new Bundle();
		bundle.putSerializable(nomeBundle, serializable);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}
	
	public void proximaTela(Context context , Class<?> proxima){
		Intent intent = new Intent(context,proxima);
		context.startActivity(intent);
	}
	public int getHoraMin(String hora,Context context) {
		if(hora != null){
			if((hora.length() >= 5) && hora.contains(":")){
				String[] spliter = hora.split(":");
				int horas = Integer.parseInt(spliter[0]);
				int min = Integer.parseInt(spliter[1]);
				return (horas*60)+ min;
			}else{
				Toast.makeText(context, context.getText(R.string.hora_invalida), Toast.LENGTH_SHORT).show();
			}
		}
		return 0;
	}
	
	public int getHoraMin(String hora) {
		if(hora != null){
			if((hora.length() >= 4) && hora.contains(":")){
				String[] spliter = hora.split(":");
				int horas = Integer.parseInt(spliter[0]);
				int min = Integer.parseInt(spliter[1]);
				return (horas*60)+ min;
			}
		}
		return 0;
	}
	
	public void salvaCargaHoraria(Context context) {
		InfoUsu info = new InfoUsu();
		PersistenceInfoUsu infoUsu = new PersistenceInfoUsu(context);
		info.setCargaHoraria(getCargaHoraria());
		try {
			infoUsu.inserir(info);
		} catch (Exception e) {
			Log.e("ERRO", e.getCause().toString());
		}finally{
			infoUsu.close();
		}
	}
	

	public void editarCargaHoraria(String cargaHoraria, Context context) {
		InfoUsu info = new InfoUsu();
		PersistenceInfoUsu infoUsu = new PersistenceInfoUsu(context);
		setCargaHoraria(getHoraMin(cargaHoraria,context));
		info.setCargaHoraria(getCargaHoraria());
		try {
			infoUsu.editar(info);
		} catch (Exception e) {
			Log.e("ERRO", e.getCause().toString());
		}finally{
			infoUsu.close();
		}
	}
	
	public String retornaHora(int numMin) {
		int hr = (numMin / 60);
		int mn = (Math.abs(numMin) % 60);
		String hora =  (hr < 0 || hr > 9) ?  hr+"": ("0"+hr+"") ;
		String min = (mn > 9) ?  mn+"":  "0"+mn ;
		return  hora + ":" + min;
	}
}