package com.github.joelgodofwar.mmh.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

//This class is where all formatting methods are stored.
@SuppressWarnings("unused")
public class Format { 
	//private static MineverseChat plugin = MineverseChat.getInstance();
	
	public static final int LEGACY_COLOR_CODE_LENGTH = 2;
	public static final int HEX_COLOR_CODE_LENGTH = 14;
	public static final String DEFAULT_COLOR_CODE = String.valueOf(ChatColor.WHITE);
	public static final String HEX_COLOR_CODE_PREFIX = "#";
	public static final String BUKKIT_COLOR_CODE_PREFIX = "�";
	public static final String BUKKIT_HEX_COLOR_CODE_PREFIX = "x";
	
	public static String getLastCode(String s) {
		String ts = "";
		char[] ch = s.toCharArray();
		for(int a = 0; a < s.length() - 1; a ++) {
			if(String.valueOf(ch[a + 1]).matches("[lkomnLKOMN]") && ch[a] == '�') {
				ts += String.valueOf(ch[a]) + ch[a + 1];
				a ++;
			}
			else if(String.valueOf(ch[a + 1]).matches("[0123456789abcdefrABCDEFR]") && ch[a] == '�') {
				ts = String.valueOf(ch[a]) + ch[a + 1];
				a ++;
			}
			else if(ch[a + 1] == 'x' && ch[a] == '�') {
				if(ch.length > a + 13) {
					if(String.valueOf(ch[a + 3]).matches("[0123456789abcdefABCDEF]") && String.valueOf(ch[a + 5]).matches("[0123456789abcdefABCDEF]") && String.valueOf(ch[a + 7]).matches("[0123456789abcdefABCDEF]") && String.valueOf(ch[a + 9]).matches("[0123456789abcdefABCDEF]") && String.valueOf(ch[a + 11]).matches("[0123456789abcdefABCDEF]") && String.valueOf(ch[a + 13]).matches("[0123456789abcdefABCDEF]") && ch[a + 2] == '�' && ch[a + 4] == '�' && ch[a + 6] == '�' && ch[a + 8] == '�' && ch[a + 10] == '�' && ch[a + 12] == '�') {
						ts = String.valueOf(ch[a]) + ch[a + 1] + ch[a + 2] + ch[a + 3] + ch[a + 4] + ch[a + 5] + ch[a + 6] + ch[a + 7] + ch[a + 8] + ch[a + 9] + ch[a + 10] + ch[a + 11] + ch[a + 12] + ch[a + 13];
						a += 13;
					}
				}
			}
		}
		return ts;
	}
	 
	
	private static String convertToJsonColors(String s) {
		return convertToJsonColors(s, "");
	}
	
	private static String convertToJsonColors(String s, String extensions) {
		String remaining = s;
		String temp = "";
		int indexColor = -1;
		int indexNextColor = -1;
		String color = "";
		String modifier = "";
		boolean bold = false;
		boolean obfuscated = false;
		boolean italic = false;
		boolean strikethrough = false;
		boolean underlined = false;
		String previousColor = "";
		int colorLength = LEGACY_COLOR_CODE_LENGTH;
		do {
			if(remaining.length() < LEGACY_COLOR_CODE_LENGTH) {
				temp = "{\"text\":\"" + remaining + "\"},";
				break;
			}
			modifier = "";
			indexColor = remaining.indexOf(BUKKIT_COLOR_CODE_PREFIX);	
			previousColor = color;			
			
			color = remaining.substring(1, indexColor + LEGACY_COLOR_CODE_LENGTH);
			if(color.equals(BUKKIT_HEX_COLOR_CODE_PREFIX)) {
				if(remaining.length() >= HEX_COLOR_CODE_LENGTH) {
					color = HEX_COLOR_CODE_PREFIX + remaining.substring(LEGACY_COLOR_CODE_LENGTH, indexColor + HEX_COLOR_CODE_LENGTH).replace(BUKKIT_COLOR_CODE_PREFIX, "");
					colorLength = HEX_COLOR_CODE_LENGTH;
					bold = false;
					obfuscated = false;
					italic = false;
					strikethrough = false;
					underlined = false;
				}
			}
			else if(!color.matches("[0123456789abcdefABCDEF]")) {				
				switch(color) {
					case "l":
					case "L": {
						bold = true;
						break;
					}
					case "k":
					case "K": {
						obfuscated = true;
						break;
					}
					case "o":
					case "O": {
						italic = true;
						break;
					}
					case "m":
					case "M": {
						strikethrough = true;
						break;
					}
					case "n":
					case "N": {
						underlined = true;
						break;
					}
					case "r":
					case "R": {
						bold = false;
						obfuscated = false;
						italic = false;
						strikethrough = false;
						underlined = false;
						color = "f";
						break;
					}
				}
				if(!color.equals("f"))
					color = previousColor;
				if(color.length() == 0)
					color = "f";
			}
			else {				
				bold = false;
				obfuscated = false;
				italic = false;
				strikethrough = false;
				underlined = false;
			}		
			if(bold)
				modifier += ",\"bold\":\"true\"";	
			if(obfuscated)
				modifier += ",\"obfuscated\":\"true\"";	
			if(italic)
				modifier += ",\"italic\":\"true\"";		
			if(underlined)
				modifier += ",\"underlined\":\"true\"";		
			if(strikethrough)
				modifier += ",\"strikethrough\":\"true\"";	
			remaining = remaining.substring(colorLength);
			colorLength = LEGACY_COLOR_CODE_LENGTH;
			indexNextColor = remaining.indexOf(BUKKIT_COLOR_CODE_PREFIX);
			if(indexNextColor == -1) {
				indexNextColor = remaining.length();
			}
			temp += "{\"text\":\"" + remaining.substring(0, indexNextColor) + "\",\"color\":\"" + hexidecimalToJsonColorRGB(color) + "\"" + modifier + extensions + "},";
			remaining = remaining.substring(indexNextColor);
		} 
		while(remaining.length() > 1 && indexColor != -1); 
		if(temp.length() > 1)
			temp = temp.substring(0, temp.length() - 1);
		return temp;
	}
	
	private static String hexidecimalToJsonColorRGB(String c) {
		if(c.length() == 1) {
			switch(c) {
				case "0": return "black";
				case "1": return "dark_blue";
				case "2": return "dark_green";
				case "3": return "dark_aqua";
				case "4": return "dark_red";
				case "5": return "dark_purple";
				case "6": return "gold";
				case "7": return "gray";
				case "8": return "dark_gray";
				case "9": return "blue";
				case "a":
				case "A": return "green";
				case "b":
				case "B": return "aqua";
				case "c":
				case "C": return "red";
				case "d":
				case "D": return "light_purple";
				case "e":
				case "E": return "yellow";
				case "f":
				case "F": return "white";
				default: return "white";
			}
		}
		if(isValidHexColor(c)) {
			return c;
		}
		return "white";
	}
	
	protected static Pattern chatHexColorPattern = Pattern.compile("(?i)&([X])");
	protected static Pattern chatColorPattern = Pattern.compile("(?i)&([0-9A-F])");
	protected static Pattern chatMagicPattern = Pattern.compile("(?i)&([K])");
	protected static Pattern chatBoldPattern = Pattern.compile("(?i)&([L])");
	protected static Pattern chatStrikethroughPattern = Pattern.compile("(?i)&([M])");
	protected static Pattern chatUnderlinePattern = Pattern.compile("(?i)&([N])");
	protected static Pattern chatItalicPattern = Pattern.compile("(?i)&([O])");
	protected static Pattern chatResetPattern = Pattern.compile("(?i)&([R])");

	public static String FormatStringColor(String string) {
		String allFormated = string;
		allFormated = chatColorPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = chatHexColorPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = allFormated.replaceAll("%", "\\%");
		
		allFormated = convertHexColorCodeStringToBukkitColorCodeString(allFormated);
		return allFormated;
	}

	public static String FormatString(String string) {
		String allFormated = string;
		allFormated = chatMagicPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = chatBoldPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = chatStrikethroughPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = chatUnderlinePattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = chatItalicPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = chatResetPattern.matcher(allFormated).replaceAll("\u00A7$1");
		allFormated = allFormated.replaceAll("%", "\\%");
		return allFormated;
	}
	
	public static String FormatStringAll(String string) {
		String allFormated = Format.FormatString(string);
		allFormated = Format.FormatStringColor(allFormated);
		return allFormated;
	}

	public static String FormatPlayerName(String playerPrefix, String playerDisplayName, String playerSuffix) {
		playerPrefix = chatColorPattern.matcher(playerPrefix).replaceAll("\u00A7$1");
		playerPrefix = chatMagicPattern.matcher(playerPrefix).replaceAll("\u00A7$1");
		playerPrefix = chatBoldPattern.matcher(playerPrefix).replaceAll("\u00A7$1");
		playerPrefix = chatStrikethroughPattern.matcher(playerPrefix).replaceAll("\u00A7$1");
		playerPrefix = chatUnderlinePattern.matcher(playerPrefix).replaceAll("\u00A7$1");
		playerPrefix = chatItalicPattern.matcher(playerPrefix).replaceAll("\u00A7$1");
		playerPrefix = chatResetPattern.matcher(playerPrefix).replaceAll("\u00A7$1");

		playerSuffix = chatColorPattern.matcher(playerSuffix).replaceAll("\u00A7$1");
		playerSuffix = chatMagicPattern.matcher(playerSuffix).replaceAll("\u00A7$1");
		playerSuffix = chatBoldPattern.matcher(playerSuffix).replaceAll("\u00A7$1");
		playerSuffix = chatStrikethroughPattern.matcher(playerSuffix).replaceAll("\u00A7$1");
		playerSuffix = chatUnderlinePattern.matcher(playerSuffix).replaceAll("\u00A7$1");
		playerSuffix = chatItalicPattern.matcher(playerSuffix).replaceAll("\u00A7$1");
		playerSuffix = chatResetPattern.matcher(playerSuffix).replaceAll("\u00A7$1");
		return playerPrefix + playerDisplayName.trim() + playerSuffix;
	}
	
	public static boolean isValidColor(String color) {
		Boolean bFound = false;
		for(ChatColor bkColors : ChatColor.values()) {
			if(color.equalsIgnoreCase(bkColors.name())) {
				bFound = true;
			}
		}
		return bFound;
	}
	
	public static boolean isValidHexColor(String color) {
		Pattern pattern = Pattern.compile("(^#[0-9a-fA-F]{6}\\b)");
		Matcher matcher = pattern.matcher(color);
		return matcher.find();
	}
	
	public static String convertHexColorCodeToBukkitColorCode(String color) {
		StringBuilder bukkitColorCode = new StringBuilder(BUKKIT_COLOR_CODE_PREFIX + BUKKIT_HEX_COLOR_CODE_PREFIX);
		for(int a = 1; a < color.length(); a++) {
			bukkitColorCode.append(BUKKIT_COLOR_CODE_PREFIX + color.charAt(a));
		}
		return bukkitColorCode.toString();
	}
	
	public static String convertHexColorCodeStringToBukkitColorCodeString(String string) {
		Pattern pattern = Pattern.compile("(#[0-9a-fA-F]{6})");
		Matcher matcher = pattern.matcher(string);
		while(matcher.find()) {
			int indexStart = matcher.start();
			int indexEnd = matcher.end();
			String hexColor = string.substring(indexStart, indexEnd);	
			String bukkitColor = convertHexColorCodeToBukkitColorCode(hexColor);
			string = string.replaceAll(hexColor, bukkitColor);
			matcher.reset(string);
		}
		return string;
	}
	
	public static String escapeAllRegex(String input) {
		return input.replace("[", "\\[").replace("]", "\\]").replace("{", "\\{").replace("}", "\\}").replace("(", "\\(").replace(")", "\\)").replace("|", "\\|").replace("+", "\\+").replace("*", "\\*");
	}
	
	/**public static boolean underlineURLs() {
		return plugin.getConfig().getBoolean("underlineurls", true);
	}*/
	public static final char colorChar = '\u00a7';
	//code taken from bukkit, so it can work on bungee too
	public static String color(String textToTranslate){
		if (textToTranslate == null) return null;
		if (!textToTranslate.contains("&")) return textToTranslate;
		char[] b = textToTranslate.toCharArray();
		for (int i = 0; i < b.length - 1; i++) {
			if ((b[i] == '&') && ("0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[(i + 1)]) > -1)){
				b[i] = colorChar;
				b[(i + 1)] = Character.toLowerCase(b[(i + 1)]);
			}
		}
		return new String(b);
	}
}