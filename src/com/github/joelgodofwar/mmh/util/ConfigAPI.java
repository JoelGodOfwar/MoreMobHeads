package com.github.joelgodofwar.mmh.util;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import com.github.joelgodofwar.mmh.MoreMobHeads;

public class ConfigAPI  {

	public final static Logger logger = Logger.getLogger("Minecraft");
	
	public static  void CheckForConfig(Plugin plugin){
		try{
			if(!plugin.getDataFolder().exists()){
				log(": Data Folder doesn't exist");
				log(": Creating Data Folder");
				plugin.getDataFolder().mkdirs();
				log(": Data Folder Created at " + plugin.getDataFolder());
			}
			File  file = new File(plugin.getDataFolder(), "config.yml");
			plugin.getLogger().info("" + file);
			if(!file.exists()){
				log("config.yml not found, creating!");
				plugin.saveDefaultConfig();
				FileConfiguration config = plugin.getConfig();
				
				config.options().copyDefaults(true);
				plugin.saveConfig();
			}
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	public static void Reloadconfig(Plugin plugin){
		// Load config.
		FileConfiguration config = plugin.getConfig();
		String daString = config.getString("debug").replace("'", "") + ",";
		
		if(daString.contains("true")){
			MoreMobHeads.debug = true;
		}else{
			MoreMobHeads.debug = false;
		}
		String daString2 = config.getString("auto_update_check").replace("'", "") + ",";
		if(daString2.contains("true")){
			MoreMobHeads.UpdateCheck = true;
		}else{
			MoreMobHeads.UpdateCheck = false;
		}
		
		if(MoreMobHeads.debug){log("UpdateCheck = " + MoreMobHeads.UpdateCheck);} //TODO: Logger
	}
	public static  void log(String dalog){
		Bukkit.getLogger().info(dalog);
	}
}
