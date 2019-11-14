package com.common.share;

import org.vaadin.autoreplacefield.NumberField;

public class AmountField extends NumberField{
	public AmountField(){
		this.setStyleName("fright");
	}
	public AmountField(String s){
		this.setCaption(s);
		this.setStyleName("fright");
	}
}
