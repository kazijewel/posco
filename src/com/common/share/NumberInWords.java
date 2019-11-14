package com.common.share;


public class NumberInWords {

	public String setWords(String tamount){

		String str = String.valueOf(tamount);
		String line = "";

		double val = Double.parseDouble(str);

		double num = Math.floor(val);

		if(num==val){

			if(num==0.00){

				line += "Zero";

			}
			else{

				line +=  decimalPart(val);
			}

		}
		else{

			double f = (val - num);
			if (num == 0.0)
				line += "Zero and Paisa " + decimalPart(Math.round(f * 100));
			else
				line += decimalPart(num) + " and Paisa " + decimalPart(Math.round(f * 100));

		}
		
		return line+" Only.";

	}

	private static String decimalPart(double i){


		if (i >= 10000000) 
			return decimalPart(Math.floor(i / 10000000)) + " Crore " + decimalPart(i%10000000);
		else if (i >= 100000)
			return decimalPart(Math.floor(i / 100000)) + " Lac " + decimalPart(i % 100000);
		else if (i >= 1000)
			return decimalPart(Math.floor(i / 1000)) + " Thousand " + decimalPart(i % 1000);
		else if (i >= 100)
			return decimalPart(Math.floor(i / 100)) + " Hundred " + decimalPart(i % 100);

		return lessHundred(i);

	}

	private static String lessHundred(double i){

		if (i < 20)
			return twentyPart(i);
		else if (i % 10 == 0)
			return tensPart(Math.floor(i / 10));

		return tensPart(Math.floor(i / 10)) + ' ' + lessHundred(i % 10);

	}

	private static String twentyPart(double i){

		if (i == 0)
			return "";
		else if (i == 1)
			return "One";
		else if (i == 2)
			return "Two";
		else if (i == 3)
			return "Three";
		else if (i == 4)
			return "Four";
		else if (i == 5)
			return "Five";
		else if (i == 6)
			return "Six";
		else if (i == 7)
			return "Seven";
		else if (i == 8)
			return "Eight";
		else if (i == 9)
			return "Nine";
		else if (i == 10)
			return "Ten";
		else if (i == 11)
			return "Eleven";
		else if (i == 12)
			return "Twelve";
		else if (i == 13)
			return "Thirteen";
		else if (i == 14)
			return "Fourteen";
		else if (i == 15)
			return "Fifteen";
		else if (i == 16)
			return "Sixteen";
		else if (i == 17)
			return "Seventeen";
		else if (i == 18)
			return "Eigthteen";
		else if (i == 19)
			return "Nineten";

		return i + "";
	}

	private static String tensPart(double i){

		if (i == 1)
			return "Ten";
		else if (i == 2)
			return "Twenty";
		else if (i == 3)
			return "Thirty";
		else if (i == 4)
			return "Forty";
		else if (i == 5)
			return "Fifty";
		else if (i == 6)
			return "Sixty";
		else if (i == 7)
			return "Seventy";
		else if (i == 8)
			return "Eighty";
		else if (i == 9)
			return "Ninety";

		return "none decimal";

	}

}
