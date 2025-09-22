package com.github.joelgodofwar.mmh.util;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class Utils {

	public String parsePAPI(String string) {
		if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
			return PlaceholderAPI.setPlaceholders(null, string);
		}else {
			return string;
		}
	}

	public String parsePAPI(Player player, String string) {
		if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
			return PlaceholderAPI.setPlaceholders(player, string);
		}else {
			return string;
		}
	}

	public String parsePAPI(OfflinePlayer player, String string) {
		if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
			return PlaceholderAPI.setPlaceholders(player, string);
		}else {
			return string;
		}
	}

}
