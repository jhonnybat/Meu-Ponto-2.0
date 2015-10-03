package com.MeuPonto;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.Funcoes.Funcoes;
import com.Persistence.PersistenceInfoUsu;

@SuppressLint("ValidFragment")
public class TelaInfoUsu extends Activity {


	private EditText edtHoraPicker;
	private Button btnSalvar;
	private CheckBox chkSabado;
	private EditText edtHoraSabado;
	
	private int mHour;
	private int mMinutes;
	private StringBuilder cargaHoraria; 
	public static final int TIME_DIALOG_ID = 0;
	public static final int MAX_HORA_DIA = 1440;
	private boolean sabadoClick = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);

		if(getInfoUsu()){
			Funcoes.getInstance().setCargaHoraria(recuperaJornadaTrabalho());
			Funcoes.getInstance().proximaTela(TelaInfoUsu.this, MainActivity.class);
			finish();
		}else{
			edtHoraPicker = (EditText) findViewById(R.id.edt_hora_picker);
			btnSalvar = (Button)findViewById(R.id.btn_salvar);
			chkSabado = (CheckBox)findViewById(R.id.checkBox1);
			edtHoraSabado = (EditText)findViewById(R.id.edt_hora_sabado_picker);
				
			chkSabado.setOnCheckedChangeListener( new MyCheckBoxListener());
			
			
			btnSalvar.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int horaNoraml = 0;
					int horaSabado = 0;
					boolean finish = false;
					if(edtHoraPicker.getText().toString().length() > 0){
						Funcoes f =Funcoes.getInstance(); 
						if(chkSabado.isChecked()){
							if(edtHoraPicker.getText().toString().contains(":") && edtHoraSabado.getText().toString().contains(":")
									&& edtHoraPicker.getText().toString().length() == 5 && edtHoraSabado.getText().toString().length() == 5){
								horaNoraml = f.getHoraMin(edtHoraPicker.getText().toString(),TelaInfoUsu.this) * 5;
								horaSabado = f.getHoraMin(edtHoraSabado.getText().toString(),TelaInfoUsu.this);
							}else{
								Toast.makeText(TelaInfoUsu.this, TelaInfoUsu.this.getText(R.string.hora_invalida), Toast.LENGTH_SHORT).show();
							}
							int total = (horaNoraml +  horaSabado) / 5;
							if(total < TelaInfoUsu.MAX_HORA_DIA && total > 0 ){
								f.setCargaHoraria(total);
								finish = true;
							}
								
						}else{
							if(edtHoraPicker.getText().toString().contains(":")){
								horaNoraml = f.getHoraMin(edtHoraPicker.getText().toString(),TelaInfoUsu.this);
							}else{
								Toast.makeText(TelaInfoUsu.this, TelaInfoUsu.this.getText(R.string.hora_invalida), Toast.LENGTH_SHORT).show();
							}
							if(horaNoraml < TelaInfoUsu.MAX_HORA_DIA && horaNoraml > 0 ){
								f.setCargaHoraria(horaNoraml);
								finish = true;
							}
						}
						if(finish){
							f.salvaCargaHoraria(TelaInfoUsu.this);
							Toast.makeText(TelaInfoUsu.this, getString(R.string.nova_jornada_salva), Toast.LENGTH_SHORT).show();
							f.proximaTela(TelaInfoUsu.this, MainActivity.class);
							finish();
						}
					}else
						Funcoes.getInstance().alertaErro(getString(R.string.atencao), R.string.informa_jornada, TelaInfoUsu.this);
				}
			});


			// add a click listener to the button
			edtHoraPicker.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					sabadoClick = false;
					DialogFragment newFragment = new TimePickerFragment();
					newFragment.show(getFragmentManager(), "timePicker");
				}
			});
			
			// add a click listener to the button
			edtHoraSabado.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					sabadoClick = true;
					DialogFragment newFragment = new TimePickerFragment();
					newFragment.show(getFragmentManager(), "timePicker");
				}
			});
			
		}
	}
	
	private int recuperaJornadaTrabalho() {
		PersistenceInfoUsu pInfoUsu = new PersistenceInfoUsu(TelaInfoUsu.this);
		int cargaHoraria = 0;
		try {
			cargaHoraria = pInfoUsu.recuperaJornadaTrabalho();	
		} catch (Exception e) {
			Log.e("ERRO", e.toString());
		}finally{
			pInfoUsu.close();
		}
		return cargaHoraria ;
	}

	private boolean getInfoUsu() {
		PersistenceInfoUsu pInfo = new PersistenceInfoUsu(TelaInfoUsu.this);
		boolean retorno = false;
		try{
			retorno = pInfo.busca();
		}catch (Exception e) {
			Log.e("ERRO",""+ e.toString());
		}finally{
			pInfo.close();
		}
		return retorno;
	}
	
	// updates the time we display in the TextView
	private void updateDisplay() {

		cargaHoraria = new StringBuilder()
		.append(pad(mHour))
		.append(":")
		.append(pad(mMinutes));
		if(!sabadoClick)
			edtHoraPicker.setText(cargaHoraria.toString());
		else
			edtHoraSabado.setText(cargaHoraria.toString());
	}

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}
	
	

	class MyCheckBoxListener implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if(chkSabado.isChecked()){
				edtHoraSabado.setEnabled(true);
				edtHoraSabado.setClickable(true);
			}else{
				edtHoraSabado.setEnabled(false);
			edtHoraSabado.setClickable(false);
			}
			
		}
	
	}
	
	private  class TimePickerFragment extends DialogFragment
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
			mHour = hourOfDay;
			mMinutes = minute;
			updateDisplay();
		}
	}

}
