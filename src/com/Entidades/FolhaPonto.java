package com.Entidades;

import java.io.Serializable;


public class FolhaPonto implements Serializable {

	/**
	 * 
	 */
	private int id;
	private String hora; 
	private int idData;
	public static String NOME_BUNDLE = "FOLHAPONTO";
	private static final long serialVersionUID = 1L;

	public FolhaPonto() {
		// TODO Auto-generated constructor stub
	}

	public FolhaPonto(String horaInicio, int data) {
		this.hora = horaInicio;
		this.idData= data;
	}
	
	public FolhaPonto(int id ,String horaInicio, int data) {
		this.id = id;
		this.hora = horaInicio;
		this.idData= data;
	}
	
	

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getHoraInicio() {
		return hora;
	}

	public void setHoraInicio(String horaInicio) {
		this.hora = horaInicio;
	}

	
	public int getData() {
		return idData;
	}
	
	public void setData(int data) {
		this.idData = data;
	}
	@Override
	public String toString() {
		return hora;
	}
}
