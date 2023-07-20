package com.github.joelgodofwar.mmh.util;

import org.bukkit.ChatColor;

public class StrUtils {
	/** StringRight */
	public static String Right(String input, int chars){
		if (input.length() > chars){
			//System.out.println("Right input=" + input);
			return input.substring(input.length() - chars);
		}
		else{
			return input;
		}
	}

	/** StringLeft */
	public static String Left(String input, int chars){
		if (input.length() > chars){
			//System.out.println("Left input=" + input);
			//System.out.println("Left chars=" + chars);
			return input.substring(0, chars);
		}
		else{
			return input;
		}
	}

	/** */
	public  static boolean stringContains(String string, String string2){
		String[] string3 = string.toUpperCase().split(", ");
		for(int i = 0; i < string3.length; i++){
			if(string3[i].equals(string2.toUpperCase())){
				return true;
			}
		}
		return false;
	}

	public static boolean stringEquals(String string1, String string2) {
		//string1 = ChatColor.stripColor(string1);
		//string2 = ChatColor.stripColor(string2);
		if(ChatColor.stripColor(string1).equals(ChatColor.stripColor(string2))) {
			return true;
		}
		return false;
	}

	public static String toTitleCase(String input) {
		if ((input == null) || input.isEmpty()) {
			return input;
		}

		return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
	}
}
