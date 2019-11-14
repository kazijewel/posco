package com.common.share;

import java.text.DecimalFormat;

public class CommaSeparatorRound
{
	DecimalFormat dfmt=new DecimalFormat("#0");
	public String t,totalFinal;
	String totalAmt;

	public String setComma(double amount)
	{
		double tamount = Math.abs(amount);
		totalFinal = "";
		char tArray[]=new char[90];
		char tArray2[]=new char[90];
		int len;
		int j=0;
		totalAmt=String.valueOf(dfmt.format(tamount));
		len=totalAmt.length();
		for(int i=len-1;i>=0;i--)
		{
			//System.out.print(totalAmt.charAt(i));
			tArray[j]=totalAmt.charAt(i);
			if(j==2)
			{
				j++;
				tArray[j]=',';
			}

			if(j==5 || j==8 || j==12)
			{
				j++;
				tArray[j]=',';
			}
			j++;
		}
		t=String.valueOf(tArray, 0, j);
		len=t.length();
		j=0;
		for(int i=len-1;i>=0;i--)
		{
			tArray2[j]=t.charAt(i);
			j++;
		}

		totalFinal=String.valueOf(tArray2, 0,j);
		if(totalFinal.charAt(0)==',')
			totalFinal=totalFinal.substring(1,totalFinal.length());
		tamount=0.0;
		//txtTotalQty.setValue(totalFinal);
		if (amount < 0)
			return "-" + totalFinal;
		else
			return totalFinal;
	}

}
