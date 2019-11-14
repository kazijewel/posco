package com.common.share;

public class ReportOption
{
	public boolean Radio;

	public ReportOption(String radio)
	{
		if(radio.equals("PDF"))
		{
			Radio= true;
		}
		
		else
		{
			Radio= false;
		}
	}
}
