package com.github.joelgodofwar.mmh.util;

public class ConfigHelper {
	
	public static Double Double(YmlConfiguration config, String get, Double def) {
		String str = config.getString(get);
		if (str == null || str.isEmpty()) {
			if(def != null) {
				return def;
			}else {
				return 0.00;
			}
		}
		if(str.indexOf('\'') >= 0) {
			str = removeQuotes(str);
			return Double.parseDouble(str);
		}
		return Double.parseDouble(str);
	}
	
	public static String removeQuotes(String string) {
		if (string.length() >= 2 && string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"'){
		    string = string.substring(1, string.length() - 1);
		}
		if (string.length() >= 2 && string.charAt(0) == '\'' && string.charAt(string.length() - 1) == '\''){
		    string = string.substring(1, string.length() - 1);
		}
		return string;
	}
}
