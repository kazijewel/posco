package com.common.share;

import org.vaadin.autoreplacefield.NumberField;

import com.vaadin.ui.Button.ClickListener;

public class TimeField extends NumberField{
	public TimeField(){
		this.setStyleName("time");
	}
	public TimeField(String s){
		this.setCaption(s);
		this.setStyleName("time");
	}
}
