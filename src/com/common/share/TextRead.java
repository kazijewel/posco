package com.common.share;

import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Label;

public class TextRead extends Label{	
	public TextRead(){
		this.setStyleName("white");
		this.setValue("&nbsp;");
		this.setContentMode(Label.CONTENT_XHTML);
	}
	/**
	 * 
	 * Note: Use an Integer Parameter to Define Amount Field
	 */
	public TextRead(int i){
		this.setStyleName("white fright flowbox");
		this.setContentMode(Label.CONTENT_XHTML);
		this.setValue("&nbsp;");
	}
	/**
	 * 
	 * Note: Use a String Parameter to Define Text Field
	 */
	public TextRead(String str){
		this.setCaption(str);
		this.setValue("&nbsp;");
		this.setContentMode(Label.CONTENT_XHTML);
		this.setStyleName("white flowbox");
	}
	/**
	 * 
	 * @param str
	 * @param i
	 */
	public TextRead(String str,int i){
		this.setCaption(str);
		this.setValue("&nbsp;");
		this.setContentMode(Label.CONTENT_XHTML);
		this.setStyleName("white fright");
	}
	public void setValue(String str){
		if(str.trim().length()>0){
			super.setValue(str);
		}else{
			super.setValue("&nbsp;");			
		}
	}
	public Object getValue()
	{
		if (super.getValue().toString().trim().equals("&nbsp;"))		
			return "";		
		else
			return super.getValue();
	}
	public void setTextChangeEventMode(TextChangeEventMode lazy) {
		// TODO Auto-generated method stub
		
	}
}
