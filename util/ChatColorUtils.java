package com.github.joelgodofwar.mmh.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

public class ChatColorUtils 
{	
	public static String setNametoRGB(String s){
		String prefix = "§x";
		s = s.replace("<BLACK>", prefix + "000000").replace("<DARK_BLUE>", prefix + "0000AA")
				.replace("<DARK_GREEN>", prefix + "00AA00").replace("<DARK_AQUA>", prefix + "00AAAA")
				.replace("<DARK_RED>", prefix + "AA0000").replace("<DARK_PURPLE>", prefix + "AA00AA")
				.replace("<GOLD>", prefix + "FFAA00").replace("<GRAY>", prefix + "AAAAAA")
				.replace("<DARK_GRAY>", prefix + "555555").replace("<BLUE>", prefix + "5555FF")
				.replace("<GREEN>", prefix + "55FF55").replace("<AQUA>", prefix + "55FFFF")
				.replace("<RED>", prefix + "FF5555").replace("<LIGHT_PURPLE>", prefix + "FF55FF")
				.replace("<YELLOW>", prefix + "FFFF55").replace("<WHITE>", prefix + "FFFFFF");
		return s;
	}
	
	public static String setColorsByCode(String s)
	{
	    s = s.replace("&0", "" + ChatColor.BLACK).replace("&1", "" + ChatColor.DARK_BLUE)
	    		.replace("&2", "" + ChatColor.DARK_GREEN).replace("&3", "" + ChatColor.DARK_AQUA)
	    		.replace("&4", "" + ChatColor.DARK_RED).replace("&5", "" + ChatColor.DARK_PURPLE)
	    		.replace("&6", "" + ChatColor.GOLD).replace("&7", "" + ChatColor.GRAY)
	    		.replace("&8", "" + ChatColor.DARK_GRAY).replace("&9", "" + ChatColor.BLUE)
	    		.replace("&a", "" + ChatColor.GREEN).replace("&b", "" + ChatColor.AQUA)
	    		.replace("&c", "" + ChatColor.RED).replace("&d", "" + ChatColor.LIGHT_PURPLE)
	    		.replace("&e", "" + ChatColor.YELLOW).replace("&f", "" + ChatColor.WHITE)
	    		.replace("&k", "" + ChatColor.MAGIC).replace("&l", "" + ChatColor.BOLD)
	    		.replace("&m", "" + ChatColor.STRIKETHROUGH).replace("&n", "" + ChatColor.UNDERLINE)
	    		.replace("&o", "" + ChatColor.ITALIC).replace("&r", "" + ChatColor.RESET)
	    		.replace("&A", "" + ChatColor.GREEN).replace("&B", "" + ChatColor.AQUA)
	    		.replace("&C", "" + ChatColor.RED).replace("&D", "" + ChatColor.LIGHT_PURPLE)
	    		.replace("&E", "" + ChatColor.YELLOW).replace("&F", "" + ChatColor.WHITE)
	    		.replace("&K", "" + ChatColor.MAGIC).replace("&L", "" + ChatColor.BOLD)
	    		.replace("&M", "" + ChatColor.STRIKETHROUGH).replace("&N", "" + ChatColor.UNDERLINE)
	    		.replace("&O", "" + ChatColor.ITALIC).replace("&R", "" + ChatColor.RESET);
	    return s;
	}
	public static String setColorsByName(String s){
	  s = s.replace("<BLACK>", "" + ChatColor.BLACK).replace("<DARK_BLUE>", "" + ChatColor.DARK_BLUE)
	    		.replace("<DARK_GREEN>", "" + ChatColor.DARK_GREEN).replace("<DARK_AQUA>", "" + ChatColor.DARK_AQUA)
	    		.replace("<DARK_RED>", "" + ChatColor.DARK_RED).replace("<DARK_PURPLE>", "" + ChatColor.DARK_PURPLE)
	    		.replace("<GOLD>", "" + ChatColor.GOLD).replace("<GRAY>", "" + ChatColor.GRAY)
	    		.replace("<DARK_GRAY>", "" + ChatColor.DARK_GRAY).replace("<BLUE>", "" + ChatColor.BLUE)
	    		.replace("<GREEN>", "" + ChatColor.GREEN).replace("<AQUA>", "" + ChatColor.AQUA)
	    		.replace("<RED>", "" + ChatColor.RED).replace("<LIGHT_PURPLE>", "" + ChatColor.LIGHT_PURPLE)
	    		.replace("<YELLOW>", "" + ChatColor.YELLOW).replace("<WHITE>", "" + ChatColor.WHITE)
	    		.replace("<MAGIC>", "" + ChatColor.MAGIC).replace("<BOLD>", "" + ChatColor.BOLD)
	    		.replace("<STRIKETHROUGH>", "" + ChatColor.STRIKETHROUGH).replace("<UNDERLINE>", "" + ChatColor.UNDERLINE)
	    		.replace("<ITALIC>", "" + ChatColor.ITALIC).replace("<RESET>", "" + ChatColor.RESET);
	  return s;
	}
	public static String setColors(String string) {
	    // Replace color placeholders while ignoring case
	    Pattern pattern = Pattern.compile("<(\\w+)>", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(string);
	    while (matcher.find()) {
	        String colorPlaceholder = matcher.group(1);
	        String color = setColors2("<" + colorPlaceholder.toUpperCase() + ">");
	        if (color != null) {
	            string = string.replaceFirst(Pattern.quote(matcher.group()), color);
	        }
	    }

	    // Apply color formatting
	    string = ChatColor.translateAlternateColorCodes('&', string);

	    return string;
	}
	public static String setColors2(String string)	{
	    string = 	string.replace("<BLACK>", "&0" )					.replace("<DARK_BLUE>", "&1" )
	    		.replace("<DARK_GREEN>", "&2" )				.replace("<DARK_AQUA>", "&3" )
	    		.replace("<DARK_RED>", "&4" )				.replace("<DARK_PURPLE>", "&5" )
	    		.replace("<GOLD>", "&6" )					.replace("<GRAY>", "&7" )
	    		.replace("<DARK_GRAY>", "&8" )				.replace("<BLUE>", "&9" )
	    		.replace("<GREEN>", "&a" )					.replace("<AQUA>", "&b" )
	    		.replace("<RED>", "&c" )					.replace("<LIGHT_PURPLE>", "&d" )
	    		.replace("<YELLOW>", "&e" )					.replace("<WHITE>", "&f" )
	    		.replace("<MAGIC>", "&k" )					.replace("<BOLD>", "&l" )
	    		.replace("<STRIKETHROUGH>", "&m" )			.replace("<UNDERLINE>", "&n" )
	    		.replace("<ITALIC>", "&o" )					.replace("<RESET>", "&r" );
	    return string;
	}
}