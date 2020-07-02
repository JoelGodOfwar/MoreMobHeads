package com.github.joelgodofwar.mmh.api;

public class StrUtils {
	/** StringRight */
	public String Right(String input, int chars){
		if (input.length() > chars){
			return input.substring(input.length() - chars);
		} 
		else{
			return input;
		}
	}
	
	/** StringLeft */
	public String Left(String input, int chars){
		if (input.length() > chars){
			return input.substring(0, chars);
		} 
		else{
			return input;
		}
	}
}
