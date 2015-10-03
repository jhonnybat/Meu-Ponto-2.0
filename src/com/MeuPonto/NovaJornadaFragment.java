package com.MeuPonto;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.Funcoes.Funcoes;

@SuppressLint("ValidFragment")
public class NovaJornadaFragment extends Fragment {


	private EditText edtHoraPicker;
	private Button btnSalvar;
	private CheckBox chkSabado;
	private EditText edtHoraSabado;
	
	private int mHour;
	private int mMinutes;
	private StringBuilder cargaHoraria; 
	private boolean sabadoClick = false;
	Context context;
	public NovaJornadaFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final View rootView = inflater.inflate(R.layout.info, container, false);
		context = rootView.getContext();
		
		edtHoraPicker = (EditText) rootView.findViewById(R.id.edt_hora_picker);
		btnSalvar = (Button)rootView.findViewById(R.id.btn_salvar);
		chkSabado = (CheckBox)rootView.findViewById(R.id.checkBox1);
		edtHoraSabado = (EditText)rootView.findViewById(R.id.edt_hora_sabado_picker);
			
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
							horaNoraml = f.getHoraMin(edtHoraPicker.getText().toString(),context) * 5;
							horaSabado = f.getHoraMin(edtHoraSabado.getText().toString(),context);
						}else{
							Toast.makeText(context, context.getText(R.string.hora_invalida), Toast.LENGTH_SHORT).show();
						}
						int total = (horaNoraml +  horaSabado) / 5;
						if(total < TelaInfoUsu.MAX_HORA_DIA  &&  total > 0){
							f.setCargaHoraria(total);
							finish = true;
						}
							
					}else{
						if(edtHoraPicker.getText().toString().contains(":")){
							horaNoraml = f.getHoraMin(edtHoraPicker.getText().toString(),context);
						}else{
							Toast.makeText(context, context.getText(R.string.hora_invalida), Toast.LENGTH_SHORT).show();
						}
						if(horaNoraml < TelaInfoUsu.MAX_HORA_DIA &&  horaNoraml > 0 ){
							f.setCargaHoraria(horaNoraml);
							finish = true;
						}
					}
					if(finish){
						f.salvaCargaHoraria(context);
						Toast.makeText(context, getString(R.string.nova_jornada_salva), Toast.LENGTH_SHORT).show();
						closeFragment();
					}
				}else
					Funcoes.getInstance().alertaErro(getString(R.string.atencao), R.string.informa_jornada,context);
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
		
		return rootView;
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
	
	private void closeFragment() {
        getActivity().getFragmentManager().beginTransaction().remove(NovaJornadaFragment.this).commit();
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
