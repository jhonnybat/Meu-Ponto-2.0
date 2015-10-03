package com.Entidades;

public class ListImage{
	private int image;
	private String activity;
	
	public ListImage(String activity,int image) {
		this.image = image;
		this.activity = activity;
	}
	
	public int getImage() {
		return image;
	}
	
	public String getNameActivity() {
		return activity;
	}
	
}
