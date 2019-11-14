package com.common.share;

public class SpellIt {
	public String number(double i) {
	    
	    double num = Math.floor(i);
	    if (num == i) {
	        if (num == 0)
	            return "Taka zero";
	        else
	            return "Taka " + decimalPart(i);
	    }
	    else {
	        double f = i - num;
	        if (num == 0)
	            return "Taka zero and Paisa " + decimalPart(Math.round(f * 100));
	        else
	            return "Taka "+decimalPart(num) + " and Paisa " + decimalPart(Math.round(f * 100));
	    }
	}
	private String decimalPart(double i) {
	    if (i >= 10000000) {
	        return decimalPart(Math.floor(i / 10000000)) + " crore " + decimalPart(i%10000000);
	    } else if (i >= 100000) {
	    return decimalPart(Math.floor(i / 100000)) + " lac " + decimalPart(i % 100000);
	    } else if (i >= 1000) {
	        return decimalPart(Math.floor(i / 1000)) + " thousand " + decimalPart(i % 1000);
	    } else if (i >= 100) {
	        return decimalPart(Math.floor(i / 100)) + " hundred " + decimalPart(i % 100);
	    } else
	        return lessHundred((int)i);
	}
	private String lessHundred(int i) {
	    if (i < 20)
	        return twentyPart(i);
	    else if (i % 10 == 0)
	        return tensPart((int)(Math.floor(i / 10)));
	    else
	        return tensPart((int)(Math.floor(i / 10))) + " " + lessHundred(i % 10);
	}
	private String twentyPart(int i) {
	    if (i == 0)
	        return "";
	    else if (i == 1)
	        return "one";
	    else if (i == 2)
	        return "two";
	    else if (i == 3)
	        return "three";
	    else if (i == 4)
	        return "four";
	    else if (i == 5)
	        return "five";
	    else if (i == 6)
	        return "six";
	    else if (i == 7)
	        return "seven";
	    else if (i == 8)
	        return "eight";
	    else if (i == 9)
	        return "nien";
	    else if (i == 10)
	        return "ten";
	    else if (i == 11)
	        return "eleven";
	    else if (i == 12)
	        return "twelve";
	    else if (i == 13)
	        return "thirteen";
	    else if (i == 14)
	        return "fourteen";
	    else if (i == 15)
	        return "fifteen";
	    else if (i == 16)
	        return "sixteen";
	    else if (i == 17)
	        return "seventeen";
	    else if (i == 18)
	        return "eigthteen";
	    else if (i == 19)
	        return "nineten";
	    else
	        return i + "";
	}
	private String tensPart(int i) {
	    if (i == 1)
	        return "ten";
	    else if (i == 2)
	        return "twenty";
	    else if (i == 3)
	        return "thirty";
	    else if (i == 4)
	        return "forty";
	    else if (i == 5)
	        return "fifty";
	    else if (i == 6)
	        return "sixty";
	    else if (i == 7)
	        return "seventy";
	    else if (i == 8)
	        return "eighty";
	    else if (i == 9)
	        return "ninety";
	    else
	        return "none decimal";
	}
}
