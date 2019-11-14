package com.common.share;


import com.example.banglacurrency.CommaSeparator;

@SuppressWarnings("serial")
public class AmountCommaSeperator extends CommaSeparator{

	public AmountCommaSeperator(String caption) 
	{
		this.setCaption(caption);
		this.setStyleName("fright");
	}
	
	public AmountCommaSeperator() 
	{	
		this.setStyleName("fright");
	}
	
	public String getValue()
	{
		return super.getValue().toString().replaceAll(",", "");
	}
}