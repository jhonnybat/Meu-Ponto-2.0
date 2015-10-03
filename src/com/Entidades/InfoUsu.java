package com.Entidades;

public class InfoUsu {

	public static final String CARGAHORARIA = "CARGAHORARIA";
	private int cargaHoraria;

	public InfoUsu(){}
	public InfoUsu(int cargaHoraria){
		this.cargaHoraria = cargaHoraria;
	}
	
	public int getCargaHoraria() {
		return cargaHoraria;
	}

	public void setCargaHoraria(int cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}
}
