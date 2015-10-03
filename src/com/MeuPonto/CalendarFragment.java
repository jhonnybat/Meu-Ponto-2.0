package com.MeuPonto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.Toast;

import com.Entidades.Datas;
import com.Persistence.PersistenceDatas;

@SuppressLint("SimpleDateFormat")
public class CalendarFragment extends Fragment {

	CalendarView cal;
	ArrayList<Datas> listDatas;
	
	public CalendarFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
		
		//final View rootView = inflater.inflate(R.layout.calendar, container, false);
		cal = (CalendarView) rootView.findViewById(R.id.calendarView1);
		
		cal.setOnDateChangeListener(new OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month,
					int dayOfMonth) {
					Calendar cal = Calendar.getInstance();
					cal.set(year,month,dayOfMonth);
					int idDatas = getDiaPonto(rootView.getContext(),cal.getTime());
					if(idDatas != -1){
						displayView(idDatas);
					}else{
						Toast toast = Toast.makeText(rootView.getContext(),getResources().getString(R.string.data_nao_registrada), Toast.LENGTH_LONG);
						toast.show();

					}
			}
		});
		return rootView;
	}


	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int idDatas) {
		// update the main content by replacing fragments
		FragmentTransaction tx = getFragmentManager().beginTransaction();
		Fragment fragment = new DiaFragment();
		//FragmentManager fragmentManager = getFragmentManager();
		Bundle bundle = new Bundle();
		bundle.putInt(Datas.NOME_BUNDLE, idDatas);
		fragment.setArguments(bundle);
		//fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
		tx.replace(R.id.frame_container, fragment);
	    tx.addToBackStack(null);
	    tx.commit();
	}
	
	public int getDiaPonto(Context context, Date d)
	{
		int idDatas= -1;
		PersistenceDatas pDatas = new PersistenceDatas(context);
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			idDatas = pDatas.buscaId(new Datas(sdf.format(d)));
			
		}catch (Exception e) {
			Log.e(" ERRO ", " getListaPonto DEU PAU!!");
		}finally{
			pDatas.close();
		}
		return idDatas;
	}
	

}
