package com.Funcoes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.MeuPonto.R;

import android.content.Context;
import android.util.Log;

public class ImportBancoDados {

	private static final String BANCO_DADOS = "FolhaPonto";
	Context myContext;
	public ImportBancoDados(Context contex) {
		myContext = contex;
		
	}

	public void copiaBanco(){ 
		try {
			copyDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void copyDataBase() throws IOException{

		InputStream myInput = null;
		OutputStream myOutput = null;
		try{

			String []arquivos = myContext.getAssets().list("");
			if(arquivos.length != 0){
				if(existeDB(BANCO_DADOS, arquivos)){
					myOutput = new FileOutputStream(myContext.getString(R.string.db_path));
					myInput= myContext.getAssets().open(BANCO_DADOS);
					copiaBancoToApp(myInput,myOutput);
					myOutput.flush();
				}
			}
		}catch (IOException e){
			Log.e("ERRO"," Não achei nenhum arquivo FolhaPonto");
		}
		finally{
			if(myOutput!= null)
				myOutput.close();
			if(myInput!= null)
				myInput.close();	
		}
	}


	private void copiaBancoToApp(InputStream myInput, OutputStream myOutput ) throws IOException{

		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}
	}

	private boolean existeDB(String name, String[] lista) {

		for(int i = 0; i < lista.length; i++)
			if(lista[i].equals(name))
				return true;

		return false;
	}

}
