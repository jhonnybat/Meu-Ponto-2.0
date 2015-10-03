package com.MeuPonto;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.Entidades.Datas;
import com.Entidades.FolhaPonto;
import com.Persistence.PersistenceDatas;
import com.Persistence.PersistenceFolhaPonto;

@SuppressLint("SimpleDateFormat")
public class HomeFragment extends Fragment {

	EditText horaCheckIn;
	SimpleDateFormat sdfHora;
	SimpleDateFormat sdfData;
	Datas data = new Datas();
	Date date = new Date();
	public HomeFragment(){
		sdfHora = new SimpleDateFormat("HH:mm");
		sdfData = new SimpleDateFormat("dd/MM/yyyy");
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_home, container, false);
		setHasOptionsMenu(true);
		Button btnPonto = (Button)rootView.findViewById(R.id.btnSalvar);
		btnPonto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				salvar(v);
			}
		});	
		
		horaCheckIn =  (EditText)rootView.findViewById(R.id.editText1);
		
		return rootView;
	}	
	

	private void salvar(View view){
		insereDatas(view.getContext());
		PersistenceFolhaPonto pFolhaPonto = new PersistenceFolhaPonto(view.getContext());
		FolhaPonto fpt = new FolhaPonto();
		try{
			date = new Date();
			fpt.setData(data.getId());
			horaCheckIn.setText(""+data.getData() + " - " + sdfHora.format(date.getTime()));
			fpt.setHoraInicio(sdfHora.format(date.getTime()));
			int idPonto =pFolhaPonto.inserir(fpt);
			fpt.setId(idPonto);
		}catch (Exception e) {
			Log.e("ERRO - INSERIR HORA", e.toString());
			Log.e("ERRO",""+ e.getCause());
		}finally{
			pFolhaPonto.close();
		}
	}
	
	private void insereDatas(Context context) {
		if(data.getId() == 0){
			PersistenceDatas pDatas = new PersistenceDatas(context);
			data = new Datas(sdfData.format(date));
			try{
				int id = pDatas.inserirOuEditar(data);
				if(id != -1)
					data.setId(id);
			}catch (Exception e) {
				Log.e("ERROR- INSERIR DATA", e.toString());
			}finally{
				pDatas.close();
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){		
		inflater.inflate(R.menu.menu_nova_jornada,menu);
		 super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.nova_jornada:
			//Funcoes.getInstance().proximaTela(TelaPrincipal.this, TelaNovaJornada.class);
			// update the main content by replacing fragments
			FragmentTransaction tx = getFragmentManager().beginTransaction();
			Fragment fragment = new NovaJornadaFragment();
			tx.replace(R.id.frame_container, fragment);
		    tx.addToBackStack(null);
		    tx.commit();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
