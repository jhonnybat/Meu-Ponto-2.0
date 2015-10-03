package com.Funcoes;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Entidades.ListImage;
import com.MeuPonto.R;

public class ListImg extends BaseAdapter {

	private Context context;
	private List<ListImage> list;
	public ListImg(Context context, List<ListImage> listImages) {
		this.list = listImages;
		this.context = context;
	}
	
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListImage image = list.get(position);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View v = inflater.inflate(R.layout.list_img, null);
		
		TextView textNome = (TextView)v.findViewById(R.id.textView1);
		textNome.setText(image.getNameActivity());
		
		ImageView img = (ImageView)v.findViewById(R.id.imageView1);
		img.setImageResource(image.getImage());
		return v;
	}
	
	

}
