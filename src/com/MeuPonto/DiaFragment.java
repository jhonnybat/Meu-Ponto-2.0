package com.MeuPonto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.Entidades.Datas;
import com.Entidades.FolhaPonto;
import com.Entidades.ListImage;
import com.Funcoes.Funcoes;
import com.Funcoes.ListImg;
import com.Persistence.PersistenceFolhaPonto;

@SuppressLint("ValidFragment")
public class DiaFragment extends Fragment {

	List<FolhaPonto> lisFolhaPontos;
	BaseAdapter adapter = null;
	List<ListImage> listImages = new ArrayList<ListImage>();

	ListView listView;
	TextView txtHoras;
	String novaHora = "";

	int mHour = 0, mMinutes = 0;
	int position = -1;
	int idData;
	public DiaFragment() {}
	Context context;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_dia, container, false);
		setHasOptionsMenu(true);
		context = rootView.getContext();
		listView = (ListView)rootView.findViewById(R.id.listView1);
		txtHoras = (TextView)rootView.findViewById(R.id.txt_horas);
		Bundle bundle = this.getArguments();
		idData = bundle.getInt(Datas.NOME_BUNDLE);
		if(idData != -1){
			getListaPonto(idData);
			getHoras();
			calculaHoras(idData);
		}
		listView.setAdapter(adapter);
		listView.setItemsCanFocus(true);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int posit,long arg3) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setSingleChoiceItems(R.array.opcoes, -1, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int item) {
						if(item == 0){
							String hora = lisFolhaPontos.get(position).getHoraInicio();
							String[] spliter = hora.split(":");
							mHour = Integer.parseInt(spliter[0]);
							mMinutes = Integer.parseInt(spliter[1]);
							DialogFragment newFragment = new TimePickerFragment();
							newFragment.show(getFragmentManager(), "timePicker");
							//showDialog(0);
						}else{
							deletePonto();
						}
						dialog.dismiss();
					}
				});
				position = posit;
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		return rootView;
	}

	private void calculaHoras(int idDatas) {
		PersistenceFolhaPonto pFolhaPonto = new PersistenceFolhaPonto(context);
		try {
			long horaTotal = pFolhaPonto.calculaHoraDia(idDatas);
			if(horaTotal < Funcoes.getInstance().getCargaHoraria())
				txtHoras.setTextColor(Color.RED);
			else
				txtHoras.setTextColor(Color.GREEN);

			txtHoras.setText(Funcoes.getInstance().retornaHora((int)horaTotal));
		} catch (Exception e) {
			Log.e("ERRO", "GetHoraDia");
		}finally{
			pFolhaPonto.close();
		}


	}

	private void  getHoras() {
		for(int i =0; i < lisFolhaPontos.size();i++){
			if(i %2 == 0)
				listImages.add(new ListImage(lisFolhaPontos.get(i).getHoraInicio(), R.drawable.checkin));
			else
				listImages.add(new ListImage(lisFolhaPontos.get(i).getHoraInicio(), R.drawable.checkout));
		}
		adapter = new ListImg(context, listImages);
	}

	private void getListaPonto(int datas) {
		PersistenceFolhaPonto pFolhaPonto = new PersistenceFolhaPonto(context);
		try{
			lisFolhaPontos = pFolhaPonto.buscarListaDia(datas);
		}catch (Exception e) {
			Log.e("ERRO - RECUPERA MES", e.toString());
		}finally{
			pFolhaPonto.close();
		}
	}


	// updates the time we display in the TextView
	private void updateDisplay(int hour, int minute) {
		
		String hours = hour < 10 ? "0"+hour : ""+hour ;
		String minutes = minute < 10 ? "0"+minute : ""+minute ;
		
		novaHora = "" +	(new StringBuilder()
		.append(hours).append(":")
		.append(minutes)).toString();
		if(position != -1){
			if(position % 2 == 0)
				listImages.set(position,  new ListImage(novaHora, R.drawable.checkin));
			else
				listImages.set(position,  new ListImage(novaHora, R.drawable.checkout));

			adapter.notifyDataSetChanged();
			listView.setAdapter(adapter);
			lisFolhaPontos.get(position).setHoraInicio(novaHora);
			updateHora();
		}else{
			if(listImages.size() % 2 == 0)
				listImages.add(new ListImage(novaHora, R.drawable.checkin));
			else
				listImages.add(new ListImage(novaHora, R.drawable.checkout));

			FolhaPonto fpt = new FolhaPonto(novaHora,idData);
			adapter.notifyDataSetChanged();
			listView.setAdapter(adapter);
			lisFolhaPontos.add(fpt);
			inserirPonto(fpt,context);
		}
	}

	private void updateHora() {
		PersistenceFolhaPonto pFolha = new PersistenceFolhaPonto(context);
		try {
			pFolha.inserirOuEditar(lisFolhaPontos.get(position));
		} catch (Exception e) {
			Log.e("ERRO", "Update DATA");
		}finally{
			pFolha.close();
		}
	}


	private void deletePonto() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(getString(R.string.deseja_apagar_hora))
		.setCancelable(false)
		.setPositiveButton(getString(R.string.sim), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				delConfirm();

			}			
		})
		.setNegativeButton(getString(R.string.nao),  new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		AlertDialog alert = builder.create();
		alert.setTitle(this.getString(R.string.atencao));
		alert.show();
	}

	private void delConfirm() {
		PersistenceFolhaPonto pFolhaPonto = new PersistenceFolhaPonto(context);
		try {
			pFolhaPonto.deletePonto(lisFolhaPontos.get(position));
		} catch (Exception e) {
			Log.e("ERRO", "Erro Deletar Ponto");
		}finally{
			pFolhaPonto.close();

		}
		listImages.remove(position);
		listView.setAdapter(adapter);
	}

	private void inserirPonto(FolhaPonto fpt, Context context) {
		PersistenceFolhaPonto pFolhaPonto = new PersistenceFolhaPonto(context);
		try {
			pFolhaPonto.inserir(fpt);
		} catch (Exception e) {
			Log.e("ERRO", "Erro Inserir Ponto");
		}finally{
			pFolhaPonto.close();

		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_add_hora, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.add_hora:
			position = -1;
			DialogFragment newFragment = new TimePickerFragment();
			newFragment.show(getFragmentManager(), "timePicker");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public  class TimePickerFragment extends DialogFragment
	implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Do something with the time chosen by the user
			updateDisplay(hourOfDay, minute);
		}
	}
}
