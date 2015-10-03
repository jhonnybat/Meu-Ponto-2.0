package com.Entidades;

import java.io.Serializable;

public class Datas implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String NOME_BUNDLE = "DATAS";
	String data;
	int id;
	
	public Datas() {
		// TODO Auto-generated constructor stub
	}
	
	public Datas(String data) {
		this.data = data;
	}
	
	public Datas(int id, String data) {
		this.id = id;
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return data;
	}
}
