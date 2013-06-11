package com.robertolosanno.cdt_maven;
public class node {

	String type; //i possibili tipi
	String label;
	
	public node(String type, String label){
		setType(type);
		this.label=label;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	public String getLabel(){
		return label;
	}
}
