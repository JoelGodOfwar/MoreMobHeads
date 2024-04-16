package com.github.joelgodofwar.mmh.util;

import org.bukkit.ChatColor;

public class StrUtils {
	/**
	 * Returns the rightmost characters of a string up to the specified length.
	 *
	 * @param input The input string.
	 * @param chars The number of characters to retrieve from the right.
	 * @return The rightmost characters of the input string, or the full string if its length is less than or equal to the specified length.
	 */
	public static String Right(String input, int chars){
		if (input.length() > chars){
			//System.out.println("Right input=" + input);
			return input.substring(input.length() - chars);
		}
		else{
			return input;
		}
	}

	/**
	 * Returns the leftmost characters of a string up to the specified length.
	 *
	 * @param input The input string.
	 * @param chars The number of characters to retrieve from the left.
	 * @return The leftmost characters of the input string, or the full string if its length is less than or equal to the specified length.
	 */
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

	/**
	 * Checks if a string contains a specified substring, ignoring case.
	 *
	 * @param string  The input string to search within (comma-separated values).
	 * @param string2 The substring to check for.
	 * @return True if the substring is found in the input string; otherwise, false.
	 */
	public  static boolean stringContains(String string, String string2){
		String[] string3 = string.toUpperCase().split(", ");
		for(int i = 0; i < string3.length; i++){
			if(string3[i].equals(string2.toUpperCase())){
				return true;
			}
		}
		return false;
	}

	/**
	 * Compares two strings for equality after removing color codes.
	 *
	 * @param string1 The first string to compare.
	 * @param string2 The second string to compare.
	 * @return True if the strings are equal (ignoring color codes); otherwise, false.
	 */
	public static boolean stringEquals(String string1, String string2) {
		//string1 = ChatColor.stripColor(string1);
		//string2 = ChatColor.stripColor(string2);
		if(ChatColor.stripColor(string1).equals(ChatColor.stripColor(string2))) {
			return true;
		}
		return false;
	}

	/**
	 * Converts the first character of a string to uppercase and the remaining characters to lowercase,
	 * creating a title case format.
	 *
	 * @param input The input string to convert to title case.
	 * @return The input string in Title Case.
	 */
	public static String toTitleCase(String input) {
		if ((input == null) || input.isEmpty()) {
			return input;
		}

		return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
	}
}
