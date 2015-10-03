package com.MeuPonto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.Entidades.Datas;
import com.Funcoes.Funcoes;
import com.Persistence.PersistenceDatas;
import com.Persistence.PersistenceFolhaPonto;

public class ReportFragment extends Fragment {
	EditText edtObrigatorias;
	EditText edtFeitas;
	EditText edtTotal;
	Spinner spnMes;

	String efetuadas = "00:00";
	String obrigatorias = "00:00";
	Calendar datas = Calendar.getInstance();
	GregorianCalendar gc = new GregorianCalendar();
	int mes;
	Context context;
	public ReportFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.fragment_report, container, false);
		setHasOptionsMenu(true);
		context = rootView.getContext();
		edtObrigatorias = (EditText) rootView.findViewById(R.id.edt_obrigatorias);
		edtFeitas = (EditText) rootView.findViewById(R.id.edt_feitas);
		edtTotal = (EditText) rootView.findViewById(R.id.edt_total);
		spnMes = (Spinner) rootView.findViewById(R.id.spn_mes);

		spnMes.setSelection(datas.get(Calendar.MONTH));

		mes = datas.get(Calendar.MONTH);

		spnMes.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				efetuadas = "00:00";
				obrigatorias = "00:00";
				mes = spnMes.getSelectedItemPosition();
				datas.set(Calendar.DATE, 1);
				datas.set(Calendar.MONTH,mes);
				calculaHoraObrigatoria();
				calculaHoraEfetuada();
				edtTotal.setText(calculaTotal());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		return rootView;
	}
	


	private void calculaHoraObrigatoria() {
		int diasUteis = 0;
		gc = (GregorianCalendar) datas;
		int diasMes = getDiasMes(datas.get(Calendar.MONTH));
		for(int i = 1;i <= diasMes;i++){
			gc.set(Calendar.DATE, i);

			int diaDaSemana = gc.get(Calendar.DAY_OF_WEEK);	
			if(diaDaSemana != 1 && diaDaSemana != 7 )
				diasUteis++;
		}
		obrigatorias = Funcoes.getInstance().retornaHora(diasUteis * Funcoes.getInstance().getCargaHoraria());
		edtObrigatorias.setText(obrigatorias);

	}

	private String calculaTotal() {
		int obr  = Funcoes.getInstance().getHoraMin(obrigatorias,context);
		int efet = Funcoes.getInstance().getHoraMin(efetuadas,context); 
		return  Funcoes.getInstance().retornaHora(efet - obr);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SimpleDateFormat")
	private void calculaHoraEfetuada() {
		PersistenceFolhaPonto pFolhaPonto = new PersistenceFolhaPonto(context);
		PersistenceDatas pDatas = new PersistenceDatas(context);
		try{
			SimpleDateFormat sdfData = new SimpleDateFormat("MM/yyyy");
			Date d = new Date();
			d.setMonth(mes);
			ArrayList<Datas> listDatas = pDatas.recuperaMes("%/"+sdfData.format(d));
			int minutosEfetuadas = pFolhaPonto.calculaHoras(listDatas);
			efetuadas = Funcoes.getInstance().retornaHora(minutosEfetuadas);
			edtFeitas.setText(efetuadas);
		}catch (Exception e) {
			Log.e("ERRO","CalculaHoraFeita "+ e.toString());
		}finally{
			pDatas.close();
			pFolhaPonto.close();
		}
	}

	private int getDiasMes(int mes){
		switch (mes) {
		case 0:
		case 2:
		case 4:
		case 6:
		case 7:
		case 9:
		case 11:
			return 31;
		case 3:
		case 5:
		case 8:
		case 10:
			return 30;
		default:
			if(datas.get(Calendar.YEAR)%4 ==0)
				return 29;
			else
				return 28;
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_limpa_banco, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.limpa_banco:
			PersistenceFolhaPonto pFolhaPonto = new PersistenceFolhaPonto(context);
			PersistenceDatas pDatas = new PersistenceDatas(context);
			try {
				pFolhaPonto.deletePontos();
				pDatas.deleteDatas();
				calculaHoraObrigatoria();
				calculaHoraEfetuada();
				edtTotal.setText(calculaTotal());
			} catch (Exception e) {
				Log.e("ERRO", "Limpar Banco "+e.toString());
			}finally{
				pFolhaPonto.close();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
