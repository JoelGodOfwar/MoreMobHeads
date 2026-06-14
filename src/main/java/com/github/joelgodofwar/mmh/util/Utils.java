package com.github.joelgodofwar.mmh.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
