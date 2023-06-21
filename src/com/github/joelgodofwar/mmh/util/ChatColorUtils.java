package com.github.joelgodofwar.mmh.util;

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
	public static String setColors(String s)
	{
	    s = s.replace("&0", ChatColor.BLACK.toString() )							.replace("&1", ChatColor.DARK_BLUE.toString() )
	    		.replace("&2", ChatColor.DARK_GREEN.toString() )					.replace("&3", ChatColor.DARK_AQUA.toString() )
	    		.replace("&4", ChatColor.DARK_RED.toString() )						.replace("&5", ChatColor.DARK_PURPLE.toString() )
	    		.replace("&6", ChatColor.GOLD.toString() )							.replace("&7", ChatColor.GRAY.toString() )
	    		.replace("&8", ChatColor.DARK_GRAY.toString() )						.replace("&9", ChatColor.BLUE.toString() )
	    		.replace("&a", ChatColor.GREEN.toString() )							.replace("&b", ChatColor.AQUA.toString() )
	    		.replace("&c", ChatColor.RED.toString() )							.replace("&d", ChatColor.LIGHT_PURPLE.toString() )
	    		.replace("&e", ChatColor.YELLOW.toString() )						.replace("&f", ChatColor.WHITE.toString() )
	    		.replace("&k", ChatColor.MAGIC.toString() )							.replace("&l", ChatColor.BOLD.toString() )
	    		.replace("&m", ChatColor.STRIKETHROUGH.toString() )					.replace("&n", ChatColor.UNDERLINE.toString() )
	    		.replace("&o", ChatColor.ITALIC.toString() )						.replace("&r", ChatColor.RESET.toString() )
	    		.replace("&A", ChatColor.GREEN.toString() )							.replace("&B", ChatColor.AQUA.toString() )
	    		.replace("&C", ChatColor.RED.toString() )							.replace("&D", ChatColor.LIGHT_PURPLE.toString() )
	    		.replace("&E", ChatColor.YELLOW.toString() )						.replace("&F", ChatColor.WHITE.toString() )
	    		.replace("&K", ChatColor.MAGIC.toString() )							.replace("&L", ChatColor.BOLD.toString() )
	    		.replace("&M", ChatColor.STRIKETHROUGH.toString() )					.replace("&N", ChatColor.UNDERLINE.toString() )
	    		.replace("&O", ChatColor.ITALIC.toString() )						.replace("&R", ChatColor.RESET.toString() )
	    		.replace("<BLACK>", ChatColor.BLACK.toString() )					.replace("<DARK_BLUE>", ChatColor.DARK_BLUE.toString() )
	    		.replace("<DARK_GREEN>", ChatColor.DARK_GREEN.toString() )			.replace("<DARK_AQUA>", ChatColor.DARK_AQUA.toString() )
	    		.replace("<DARK_RED>", ChatColor.DARK_RED.toString() )				.replace("<DARK_PURPLE>", ChatColor.DARK_PURPLE.toString() )
	    		.replace("<GOLD>", ChatColor.GOLD.toString() )						.replace("<GRAY>", ChatColor.GRAY.toString() )
	    		.replace("<DARK_GRAY>", ChatColor.DARK_GRAY.toString() )			.replace("<BLUE>", ChatColor.BLUE.toString() )
	    		.replace("<GREEN>", ChatColor.GREEN.toString() )					.replace("<AQUA>", ChatColor.AQUA.toString() )
	    		.replace("<RED>", ChatColor.RED.toString() )						.replace("<LIGHT_PURPLE>", ChatColor.LIGHT_PURPLE.toString() )
	    		.replace("<YELLOW>", ChatColor.YELLOW.toString() )					.replace("<WHITE>", ChatColor.WHITE.toString() )
	    		.replace("<MAGIC>", ChatColor.MAGIC.toString() )					.replace("<BOLD>", ChatColor.BOLD.toString() )
	    		.replace("<STRIKETHROUGH>", ChatColor.STRIKETHROUGH.toString() )	.replace("<UNDERLINE>", ChatColor.UNDERLINE.toString() )
	    		.replace("<ITALIC>", ChatColor.ITALIC.toString() )					.replace("<RESET>", ChatColor.RESET.toString() );
	    return s;
	}
}