package com.github.joelgodofwar.mmh;
//1.14
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.joelgodofwar.mmh.api.CatHeads;
import com.github.joelgodofwar.mmh.api.HorseHeads;
import com.github.joelgodofwar.mmh.api.LlamaHeads;
import com.github.joelgodofwar.mmh.api.Metrics;
import com.github.joelgodofwar.mmh.api.MobHeads;
import com.github.joelgodofwar.mmh.api.RabbitHeads;
import com.github.joelgodofwar.mmh.api.SheepHeads;
import com.github.joelgodofwar.mmh.api.UpdateChecker;
import com.github.joelgodofwar.mmh.api.VillagerHeads;
import com.github.joelgodofwar.mmh.api.YmlConfiguration;
import com.github.joelgodofwar.mmh.api.ZombieVillagerHeads;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;


@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
public class MoreMobHeads	extends JavaPlugin implements Listener{
	
	public final static Logger logger = Logger.getLogger("Minecraft");
	public static boolean UpdateCheck;
	public static boolean debug;
	public static String daLang;
	String updateURL = "https://github.com/JoelGodOfwar/MoreMobHeads/raw/master/versioncheck/1.14/version.txt";
	
	File langFile;
	FileConfiguration lang;
	
	File langNameFile;
	FileConfiguration langName;
	
	File playerFile;
	FileConfiguration playerHeads;
	
	File blockFile;
	File blockFile116;
	FileConfiguration blockHeads  = new YamlConfiguration();
	
	File customFile;
	FileConfiguration traderCustom;
	
	File chanceFile;
	YmlConfiguration chanceConfig;
		
	File mobnameFile;
	FileConfiguration mobname;
	boolean UpdateAvailable =	false;
	double defpercent = 0.013;
	//static boolean showkiller;
	//static boolean showpluginname;
	YmlConfiguration config = new YmlConfiguration();
	YamlConfiguration oldconfig = new YamlConfiguration();
	
	@Override // TODO: onEnable
	public void onEnable(){
		UpdateCheck = getConfig().getBoolean("auto_update_check");
		//showkiller = getConfig().getBoolean("lore.show_killer", true);
		//showpluginname = getConfig().getBoolean("lore.show_plugin_name", true);
		debug = getConfig().getBoolean("debug", false);
		daLang = getConfig().getString("lang", "en_US");
		oldconfig = new YamlConfiguration();
		
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info("**************************************");
		logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " Loading...");
				
		/** DEV check **/
		File jarfile = this.getFile().getAbsoluteFile();
		if(jarfile.toString().contains("-DEV")){
			debug = true;
			logDebug("Jar file contains -DEV, debug set to true");
			//log("jarfile contains dev, debug set to true.");
		}
		if(debug){logDebug("datafolder=" + getDataFolder());}
		langFile = new File(getDataFolder() + "" + File.separatorChar + "lang" + File.separatorChar, daLang + ".yml");//\
		if(debug){logDebug("langFilePath=" + langFile.getPath());}
		if(!langFile.exists()){																	// checks if the yaml does not exist
			langFile.getParentFile().mkdirs();									// creates the /plugins/<pluginName>/ directory if not found
			saveResource("lang" + File.separatorChar + "cs_CZ.yml", true);
			saveResource("lang" + File.separatorChar + "de_DE.yml", true);
			saveResource("lang" + File.separatorChar + "en_US.yml", true);
			saveResource("lang" + File.separatorChar + "es_MX.yml", true);
			saveResource("lang" + File.separatorChar + "fr_FR.yml", true);
			saveResource("lang" + File.separatorChar + "nl_NL.yml", true);
			saveResource("lang" + File.separatorChar + "pt_BR.yml", true);
			saveResource("lang" + File.separatorChar + "zh_CN.yml", true);
			log("lang file not found! copied cs_CZ.yml, de_DE.yml, en_US.yml, es_MX.yml, fr_FR.yml, nl_NL.yml, pt_BR.yml, and zh_CN.yml to " + getDataFolder() + "" + File.separatorChar + "lang");
			//ConfigAPI.copy(getResource("lang.yml"), langFile); // copies the yaml from your jar to the folder /plugin/<pluginName>
		}
	log("Loading language file...");
		lang = new YamlConfiguration();
		try {
			lang.load(langFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		String checklangversion = lang.getString("langversion", "1.0.0");
		if(checklangversion != null){
			if(!checklangversion.equalsIgnoreCase("1.0.13")){
				saveResource("lang" + File.separatorChar + "cs_CZ.yml", true);
				saveResource("lang" + File.separatorChar + "de_DE.yml", true);
				saveResource("lang" + File.separatorChar + "en_US.yml", true);
				saveResource("lang" + File.separatorChar + "es_MX.yml", true);
				saveResource("lang" + File.separatorChar + "fr_FR.yml", true);
				saveResource("lang" + File.separatorChar + "nl_NL.yml", true);
				saveResource("lang" + File.separatorChar + "pt_BR.yml", true);
				saveResource("lang" + File.separatorChar + "zh_CN.yml", true);
				log("Updating lang files! copied cs_CZ.yml, de_DE.yml, en_US.yml, es_MX.yml, fr_FR.yml, nl_NL.yml, pt_BR.yml, and zh_CN.yml to "
						+ getDataFolder() + "" + File.separatorChar + "lang");
			}
		}else{
			saveResource("lang" + File.separatorChar + "cs_CZ.yml", true);
			saveResource("lang" + File.separatorChar + "de_DE.yml", true);
			saveResource("lang" + File.separatorChar + "en_US.yml", true);
			saveResource("lang" + File.separatorChar + "es_MX.yml", true);
			saveResource("lang" + File.separatorChar + "fr_FR.yml", true);
			saveResource("lang" + File.separatorChar + "nl_NL.yml", true);
			saveResource("lang" + File.separatorChar + "pt_BR.yml", true);
			saveResource("lang" + File.separatorChar + "zh_CN.yml", true);
			log("Updating lang files! copied cs_CZ.yml, de_DE.yml, en_US.yml, es_MX.yml, fr_FR.yml, nl_NL.yml, pt_BR.yml, and zh_CN.yml to "
					+ getDataFolder() + "" + File.separatorChar + "lang");
		}
		
		/** Version Check */
		if(!getMCVersion().startsWith("1.14")&&!getMCVersion().startsWith("1.15")&&!getMCVersion().startsWith("1.16")
				&&!getMCVersion().startsWith("1.17")){
			logger.info("WARNING! *!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!");
			logger.info("WARNING! " + lang.get("server_not_version"));
			logger.info("WARNING! " + this.getName() + " v" + this.getDescription().getVersion() + " disabling.");
			logger.info("WARNING! *!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		/**	Check for config */
		try{
			if(!getDataFolder().exists()){
				log("Data Folder doesn't exist");
				log("Creating Data Folder");
				getDataFolder().mkdirs();
				log("Data Folder Created at " + getDataFolder());
			}
			File	file = new File(getDataFolder(), "config.yml");
			if(debug){logDebug("" + file);}
			if(!file.exists()){
				log("config.yml not found, creating!");
				saveResource("config.yml", true);
				saveResource("chance_config.yml", true);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	log("Loading config file...");
		try {
			oldconfig.load(new File(getDataFolder() + "" + File.separatorChar + "config.yml"));
		} catch (Exception e2) {
			logWarn("Could not load config.yml");
			e2.printStackTrace();
		}
		String checkconfigversion = oldconfig.getString("version", "1.0.0");
		if(checkconfigversion != null){
			if(!checkconfigversion.equalsIgnoreCase("1.0.11")){
				try {
					copyFile_Java7(getDataFolder() + "" + File.separatorChar + "config.yml",getDataFolder() + "" + File.separatorChar + "old_config.yml");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				saveResource("config.yml", true);
				
				try {
					config.load(new File(getDataFolder(), "config.yml"));
				} catch (IOException | InvalidConfigurationException e1) {
					logWarn("Could not load config.yml");
					e1.printStackTrace();
				}
				try {
					oldconfig.load(new File(getDataFolder(), "old_config.yml"));
				} catch (IOException | InvalidConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				config.set("auto_update_check", oldconfig.get("auto_update_check", true));
				config.set("debug", oldconfig.get("debug", false));
				config.set("lang", oldconfig.get("lang", "en_US"));
				config.set("vanilla_heads.creepers", oldconfig.get("creeper_vanilla_heads", false));
				config.set("vanilla_heads.ender_dragon", oldconfig.get("ender_dragon_vanilla_heads", false));
				config.set("vanilla_heads.skeleton", oldconfig.get("skeleton_vanilla_heads", false));
				config.set("vanilla_heads.wither_skeleton", oldconfig.get("wither_skeleton_vanilla_heads", false));
				config.set("vanilla_heads.zombie", oldconfig.get("zombie_vanilla_heads", false));
				config.set("lore.show_killer", oldconfig.get("show_killer", true));
				config.set("lore.show_plugin_name", oldconfig.get("show_plugin_name", true));
				config.set("wandering_trades.custom_wandering_trader", oldconfig.get("custom_wandering_trader", true));
				config.set("wandering_trades.player_heads.enabled", oldconfig.get("wandering_trades.player_heads.enabled", true));
				config.set("wandering_trades.player_heads.min", oldconfig.get("wandering_trader_min_player_heads", 0));
				config.set("wandering_trades.player_heads.max", oldconfig.get("wandering_trader_max_player_heads", 3));
				config.set("wandering_trades.block_heads.enabled", oldconfig.get("wandering_trades.block_heads.enabled", true));
				config.set("wandering_trades.block_heads.min", oldconfig.get("wandering_trader_min_block_heads", 0));
				config.set("wandering_trades.block_heads.max", oldconfig.get("wandering_trader_max_block_heads", 3));
				config.set("wandering_trades.custom_trades.enabled", oldconfig.get("wandering_trader_custom_trades_enabled", false));
				config.set("wandering_trades.custom_trades.min", oldconfig.get("wandering_trader_min_custom_trades", 0));
				config.set("wandering_trades.custom_trades.max", oldconfig.get("wandering_trader_max_custom_trades", 3));
				config.set("apply_looting", oldconfig.get("apply_looting", true));
				config.set("whitelist.enforce", oldconfig.get("enforce_whitelist", true));
				config.set("whitelist.player_head_whitelist", oldconfig.get("player_head_whitelist", "names_go_here"));
				config.set("blacklist.enforce", oldconfig.get("enforce_blacklist", true));
				config.set("blacklist.player_head_blacklist", oldconfig.get("player_head_blacklist", "names_go_here"));
				//config.set("", oldconfig.get("", true));
				
				try {
					config.save(new File(getDataFolder(), "config.yml"));
				} catch (IOException e) {
					logWarn("Could not save old settings to config.yml");
					e.printStackTrace();
				}
				saveResource("chance_config.yml", true);
				log("config.yml Updated! old config saved as old_config.yml");
				log("chance_config.yml saved.");
			}else{
				try {
					config.load(new File(getDataFolder(), "config.yml"));
				} catch (IOException | InvalidConfigurationException e1) {
					logWarn("Could not load config.yml");
					e1.printStackTrace();
				}
			}
			oldconfig = null;
		}
		/** end config check */
		
		if(getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)){
			/** Trader heads load */
			playerFile = new File(getDataFolder() + "" + File.separatorChar + "player_heads.yml");//\
			if(debug){logDebug("player_heads=" + playerFile.getPath());}
			if(!playerFile.exists()){																	// checks if the yaml does not exist
				saveResource("player_heads.yml", true);
				log("player_heads.yml not found! copied player_heads.yml to " + getDataFolder() + "");
				//ConfigAPI.copy(getResource("lang.yml"), langFile); // copies the yaml from your jar to the folder /plugin/<pluginName>
			}
		log("Loading player_heads file...");
			playerHeads = new YamlConfiguration();
			try {
				playerHeads.load(playerFile);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
			if(!getMCVersion().startsWith("1.16")&&!getMCVersion().startsWith("1.17")){
				blockFile = new File(getDataFolder() + "" + File.separatorChar + "block_heads.yml");//\
				if(debug){logDebug("block_heads=" + blockFile.getPath());}
				if(!blockFile.exists()){																	// checks if the yaml does not exist
					saveResource("block_heads.yml", true);
					log("block_heads.yml not found! copied block_heads.yml to " + getDataFolder() + "");
					//ConfigAPI.copy(getResource("lang.yml"), langFile); // copies the yaml from your jar to the folder /plugin/<pluginName>
				}
			}
			if(getMCVersion().startsWith("1.16")||getMCVersion().startsWith("1.17")){
				blockFile116 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16.yml");//\
				if(debug){logDebug("block_heads_1_16=" + blockFile116.getPath());}
				if(!blockFile116.exists()){																	// checks if the yaml does not exist
					saveResource("block_heads_1_16.yml", true);
					log("block_heads_1_16.yml not found! copied block_heads_1_16.yml to " + getDataFolder() + "");
					//ConfigAPI.copy(getResource("lang.yml"), langFile); // copies the yaml from your jar to the folder /plugin/<pluginName>
				}
				blockFile = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16.yml");
			log("Loading block_heads_1_16 file...");
			}else{
			log("Loading block_heads file...");
			}
		
			blockHeads = new YamlConfiguration();
			try {
				blockHeads.load(blockFile);
			} catch (IOException | InvalidConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	
			/** Custom Trades load */
			customFile = new File(getDataFolder() + "" + File.separatorChar + "custom_trades.yml");//\
			if(debug){logDebug("customFile=" + customFile.getPath());}
			if(!customFile.exists()){																	// checks if the yaml does not exist
				saveResource("custom_trades.yml", true);
				log("custom_trades.yml not found! copied custom_trades.yml to " + getDataFolder() + "");
				//ConfigAPI.copy(getResource("lang.yml"), langFile); // copies the yaml from your jar to the folder /plugin/<pluginName>
			}
		log("Loading custom_trades file...");
			traderCustom = new YamlConfiguration();
			try {
				traderCustom.load(customFile);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
		
		/** chanceConfig load */
		chanceFile = new File(getDataFolder() + "" + File.separatorChar + "chance_config.yml");//\
		if(debug){logDebug("chanceFile=" + chanceFile.getPath());}
		if(!chanceFile.exists()){																	// checks if the yaml does not exist
			saveResource("chance_config.yml", true);
			log("chance_config.yml not found! copied chance_config.yml to " + getDataFolder() + "");
			//ConfigAPI.copy(getResource("lang.yml"), langFile); // copies the yaml from your jar to the folder /plugin/<pluginName>
		}
	log("Loading chance_config file...");
		chanceConfig = new YmlConfiguration();
		try {
			chanceConfig.load(chanceFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		/** chanceConfig update check */
		String checkchanceConfigversion = chanceConfig.getString("version", "1.0.0");
		if(checkchanceConfigversion != null){
			if(!checkchanceConfigversion.equalsIgnoreCase("1.0.12")){
				saveResource("chance_config.yml", true);
				copyChance(chanceConfig, chanceFile);
				log("chance_percent.yml updated.");
			}
		}
		
		
		/** Mob names translation */
		langNameFile = new File(getDataFolder() + "" + File.separatorChar + "lang" + File.separatorChar, daLang + "_mobnames.yml");//\
		if(debug){logDebug("langFilePath=" + langNameFile.getPath());}
		if(!langNameFile.exists()){																	// checks if the yaml does not exist
			saveResource("lang" + File.separatorChar + "cs_CZ_mobnames.yml", true);
			saveResource("lang" + File.separatorChar + "de_DE_mobnames.yml", true);
			saveResource("lang" + File.separatorChar + "en_US_mobnames.yml", true);
			saveResource("lang" + File.separatorChar + "es_MX_mobnames.yml", true);
			saveResource("lang" + File.separatorChar + "fr_FR_mobnames.yml", true);
			saveResource("lang" + File.separatorChar + "nl_NL_mobnames.yml", true);
			saveResource("lang" + File.separatorChar + "pt_BR_mobnames.yml", true);
			saveResource("lang" + File.separatorChar + "zh_CN_mobnames.yml", true);
			log("lang_mobnames file not found! copied cs_CZ_mobnames.yml, de_DE_mobnames.yml, en_US_mobnames.yml, es_MX_mobnames.yml, fr_FR_mobnames.yml, nl_NL_mobnames.yml, pt_BR_mobnames.yml, and zh_CN_mobnames.yml to " + getDataFolder() + "" + File.separatorChar + "lang");
			//ConfigAPI.copy(getResource("lang.yml"), langFile); // copies the yaml from your jar to the folder /plugin/<pluginName>
		}
		log("Loading language based mobnames file...");
		langName = new YamlConfiguration();
		try {
			langName.load(langNameFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		/** Mob Names update check */
		String checklangnameConfigversion = langName.getString("zombified_piglin", "outdated");
		if(checklangnameConfigversion != null){
			if(checklangnameConfigversion.equalsIgnoreCase("outdated")){
				log("lang_mobnames file outdated! Updating.");
				saveResource("lang" + File.separatorChar + "cs_CZ_mobnames.yml", true);
				saveResource("lang" + File.separatorChar + "de_DE_mobnames.yml", true);
				saveResource("lang" + File.separatorChar + "en_US_mobnames.yml", true);
				saveResource("lang" + File.separatorChar + "es_MX_mobnames.yml", true);
				saveResource("lang" + File.separatorChar + "fr_FR_mobnames.yml", true);
				saveResource("lang" + File.separatorChar + "nl_NL_mobnames.yml", true);
				saveResource("lang" + File.separatorChar + "pt_BR_mobnames.yml", true);
				saveResource("lang" + File.separatorChar + "zh_CN_mobnames.yml", true);
				log("cs_CZ_mobnames.yml, de_DE_mobnames.yml, en_US_mobnames.yml, es_MX_mobnames.yml, fr_FR_mobnames.yml, nl_NL_mobnames.yml, pt_BR_mobnames.yml, and zh_CN_mobnames.yml updated.");
				try {
					langName.load(langNameFile);
				} catch (IOException | InvalidConfigurationException e) {
					e.printStackTrace();
				}
			}
		}
		/** end Mob names translation */
		
		/** Update Checker */
		if(UpdateCheck){
			try {
						Bukkit.getConsoleSender().sendMessage("Checking for updates...");
						UpdateChecker updater = new UpdateChecker(this, 73997);
				if(updater.checkForUpdates()) {
							UpdateAvailable = true;
							Bukkit.getConsoleSender().sendMessage(this.getName() + " " + lang.get("newvers"));
							Bukkit.getConsoleSender().sendMessage(UpdateChecker.getResourceUrl());
						}else{
							UpdateAvailable = false;
						}
				}catch(Exception e) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not process update check");
				}
		}
		/** end update checker */
		
		getServer().getPluginManager().registerEvents(this, this);
		
		consoleInfo("Enabled");
		
		Metrics metrics	= new Metrics(this);
		// New chart here
		// myPlugins()
		metrics.addCustomChart(new Metrics.AdvancedPie("my_other_plugins", new Callable<Map<String, Integer>>() {
					@Override
					public Map<String, Integer> call() throws Exception {
							Map<String, Integer> valueMap = new HashMap<>();
							//int varTotal = myPlugins();
							if(getServer().getPluginManager().getPlugin("DragonDropElytra") != null){valueMap.put("DragonDropElytra", 1);}
					if(getServer().getPluginManager().getPlugin("NoEndermanGrief") != null){valueMap.put("NoEndermanGrief", 1);}
					if(getServer().getPluginManager().getPlugin("PortalHelper") != null){valueMap.put("PortalHelper", 1);}
					if(getServer().getPluginManager().getPlugin("ShulkerRespawner") != null){valueMap.put("ShulkerRespawner", 1);}
					if(getServer().getPluginManager().getPlugin("SinglePlayerSleep") != null){valueMap.put("SinglePlayerSleep", 1);}
					if(getServer().getPluginManager().getPlugin("SilenceMobs") != null){valueMap.put("SilenceMobs", 1);}
							return valueMap;
					}
			}));
		metrics.addCustomChart(new Metrics.AdvancedPie("vanilla_heads", new Callable<Map<String, Integer>>() {
			@Override
			public Map<String, Integer> call() throws Exception {
					Map<String, Integer> valueMap = new HashMap<>();
					//int varTotal = myPlugins();
					valueMap.put("CREEPER " + getConfig().getString("vanilla_heads.creeper").toUpperCase(), 1);
					valueMap.put("ENDER_DRAGON " + getConfig().getString("vanilla_heads.ender_dragon").toUpperCase(), 1);
					valueMap.put("SKELETON " + getConfig().getString("vanilla_heads.skeleton").toUpperCase(), 1);
					valueMap.put("WITHER_SKELETON " + getConfig().getString("vanilla_heads.wither_skeleton").toUpperCase(), 1);
					valueMap.put("ZOMBIE " + getConfig().getString("vanilla_heads.zombie").toUpperCase(), 1);
					return valueMap;
					}
			}));
		metrics.addCustomChart(new Metrics.SimplePie("auto_update_check", new Callable<String>() {
					@Override
					public String call() throws Exception {
							return "" + getConfig().getString("auto_update_check").toUpperCase();
					}
			}));
		// add to site
		metrics.addCustomChart(new Metrics.SimplePie("var_debug", new Callable<String>() {
					@Override
					public String call() throws Exception {
							return "" + getConfig().getString("debug").toUpperCase();
					}
			}));
		metrics.addCustomChart(new Metrics.SimplePie("var_lang", new Callable<String>() {
					@Override
					public String call() throws Exception {
							return "" + getConfig().getString("lang").toUpperCase();
					}
			}));
		metrics.addCustomChart(new Metrics.SimplePie("whitelist.enforce", new Callable<String>() {
					@Override
					public String call() throws Exception {
							return "" + getConfig().getString("whitelist.enforce").toUpperCase();
					}
			}));
		metrics.addCustomChart(new Metrics.SimplePie("blacklist.enforce", new Callable<String>() {
					@Override
					public String call() throws Exception {
							return "" + getConfig().getString("blacklist.enforce").toUpperCase();
					}
			}));
		metrics.addCustomChart(new Metrics.SimplePie("custom_wandering_trader", new Callable<String>() {
			@Override
			public String call() throws Exception {
					return "" + getConfig().getString("wandering_trades.custom_wandering_trader").toUpperCase();
			}
			}));
		metrics.addCustomChart(new Metrics.SimplePie("player_heads", new Callable<String>() {
			@Override
			public String call() throws Exception {
					return "" + getConfig().getString("wandering_trades.player_heads.enabled").toUpperCase();
			}
			}));
		metrics.addCustomChart(new Metrics.SimplePie("block_heads", new Callable<String>() {
			@Override
			public String call() throws Exception {
					return "" + getConfig().getString("wandering_trades.block_heads.enabled").toUpperCase();
			}
			}));
		metrics.addCustomChart(new Metrics.SimplePie("custom_trades", new Callable<String>() {
			@Override
			public String call() throws Exception {
					return "" + getConfig().getString("wandering_trades.custom_trades.enabled").toUpperCase();
			}
			}));
		metrics.addCustomChart(new Metrics.SimplePie("apply_looting", new Callable<String>() {
			@Override
			public String call() throws Exception {
					return "" + getConfig().getString("apply_looting").toUpperCase();
			}
			}));
		metrics.addCustomChart(new Metrics.SimplePie("show_killer", new Callable<String>() {
			@Override
			public String call() throws Exception {
					return "" + getConfig().getString("lore.show_killer").toUpperCase();
			}
			}));
		metrics.addCustomChart(new Metrics.SimplePie("show_plugin_name", new Callable<String>() {
			@Override
			public String call() throws Exception {
					return "" + getConfig().getString("lore.show_plugin_name").toUpperCase();
			}
			}));
	}
	
	@Override // TODO: onDisable
	public void onDisable(){
		
		consoleInfo("Disabled");
	}
	
	public void giveMobHead(LivingEntity mob, String name){
		ItemStack helmet = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
		SkullMeta meta = (SkullMeta)helmet.getItemMeta();
			meta.setDisplayName(name + "'s Head");
		meta.setOwner(name); //.setOwner(name);
			helmet.setItemMeta(meta);//																	 e2d4c388-42d5-4a96-b4c9-623df7f5e026
		mob.getEquipment().setHelmet(helmet);
		helmet.setItemMeta(meta);
		mob.getEquipment().setHelmet(helmet);
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEntityEvent event){// TODO:
		if(!(event.getPlayer() instanceof Player))
			return;
		try{
			Player player = event.getPlayer();
			if(player.hasPermission("moremobheads.nametag")){
				Material material = player.getInventory().getItemInMainHand().getType();
				Material material2 = player.getInventory().getItemInOffHand().getType();
				String name = null;
				if(material.equals(Material.NAME_TAG)){
					name = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
					if(debug){logDebug("PIEE" + player.getDisplayName() + " Main hand name=" + name);};
				}
				if(material2.equals(Material.NAME_TAG)){
					name = player.getInventory().getItemInOffHand().getItemMeta().getDisplayName();
					if(debug){logDebug("PIEE " + player.getDisplayName() + " Off hand name=" + name);};
				}
				if(getServer().getPluginManager().getPlugin("SilenceMobs") != null){
					if(name.toLowerCase().contains("silenceme")||name.toLowerCase().contains("silence me")){
						return;
					}
				}
				
				
	/** experimental code */
				/*ItemStack itemstack = player.getInventory().getItemInOffHand();
				getConfig().set("itemstack", itemstack);
				saveConfig();*/
		//		log("itemstack set");

				//player.getInventory().addItem(getConfig().getItemStack("itemstack"));
				LivingEntity mob = (LivingEntity) event.getRightClicked();
				/*Villager villager = (Villager) mob;
				List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();
				MerchantRecipe recipe = new MerchantRecipe(getConfig().getItemStack("itemstack"), 1);
				recipe.addIngredient(new ItemStack(Material.EMERALD));
								recipes.add(recipe);
								villager.setRecipes(recipes);*/
		/** experimental code */
								
				//player.sendMessage("Testing");
				if(material.equals(Material.NAME_TAG)||material2.equals(Material.NAME_TAG)){
					//player.sendMessage("Testing 2");
					if(mob instanceof Skeleton||mob instanceof Zombie||mob instanceof PigZombie||mob instanceof Villager){
						/**if(getConfig().getBoolean("enforce_whitelist", false)){ //
							if(getConfig().getString("whitelist.player_head_whitelist", "").toLowerCase().contains(name.toLowerCase())){
								// on whitelist make the head.
								giveMobHead(mob, name);
							}else{
								player.sendMessage("\"" + name + "\" " + lang.get("not_on_whitelist"));
								event.setCancelled(true);
							}
						}else if(getConfig().getBoolean("blacklist.enforce", false)){
							if(!getConfig().getString("blacklist.player_head_blacklist", "").toLowerCase().contains(name.toLowerCase())){
								// not on blacklist, make the head.
								giveMobHead(mob, name);
							}else{
								player.sendMessage("\"" + name + "\" " + lang.get("on_blacklist"));
								event.setCancelled(true);
							}
						}*/
						boolean enforcewhitelist = getConfig().getBoolean("whitelist.enforce", false);
						boolean enforceblacklist = getConfig().getBoolean("blacklist.enforce", false);
						boolean onwhitelist = getConfig().getString("whitelist.player_head_whitelist", "").toLowerCase().contains(name.toLowerCase());
						boolean onblacklist = getConfig().getString("blacklist.player_head_blacklist", "").toLowerCase().contains(name.toLowerCase());
						if(enforcewhitelist&&enforceblacklist){
							if(onwhitelist&&!(onblacklist)){
								giveMobHead(mob, name);
							}else{
								event.setCancelled(true);
							}
						}else if(enforcewhitelist&&!enforceblacklist){
							if(onwhitelist){
								giveMobHead(mob, name);
							}else{
								event.setCancelled(true);
							}
						}else if(!enforcewhitelist&&enforceblacklist){
							if(!onblacklist){
								giveMobHead(mob, name);
							}else{
								event.setCancelled(true);
							}
						}else{
							giveMobHead(mob, name);
						}
					}
				}
			}else{
				//ZombieVillager mob = (ZombieVillager) event.getRightClicked();
				
				//player.sendMessage(mob.getName() + " profession= " + mob.getVillagerProfession());
			}
			//ZombieVillager mob = (ZombieVillager) event.getRightClicked();
			
			//player.sendMessage(mob.getName() + " profession= " + mob.getVillagerProfession());
		}catch (Exception e){
			//e.printStackTrace();
		}

	}
	 
	public void dropMobHead(Entity entity, String name, Player killer){// TODO:
		ItemStack helmet = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
		SkullMeta meta = (SkullMeta)helmet.getItemMeta();
		meta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(name)); //.setOwner(name);
		meta.setDisplayName(name + "'s Head");
		ArrayList<String> lore = new ArrayList();
		if(getConfig().getBoolean("lore.show_killer", true)){
			lore.add(ChatColor.RESET + "Killed by " + ChatColor.RESET + ChatColor.YELLOW + killer.getDisplayName());
		}
		if(getConfig().getBoolean("lore.show_plugin_name", true)){
			lore.add(ChatColor.AQUA + "" + this.getName());
		}
		meta.setLore(lore);
		meta.setLore(lore);
		helmet.setItemMeta(meta);//																	 e2d4c388-42d5-4a96-b4c9-623df7f5e026
		helmet.setItemMeta(meta);
		entity.getWorld().dropItemNaturally(entity.getLocation(), helmet);
	}
	 
	public boolean DropIt(EntityDeathEvent event, double chancepercent){// TODO:
		ItemStack itemstack = event.getEntity().getKiller().getInventory().getItemInMainHand();
		if(itemstack != null){
				if(debug){logDebug("DI itemstack=" + itemstack.getType().toString() + " line:463");}
			int enchantmentlevel = 0;
			if(getConfig().getBoolean("apply_looting", true)){
				enchantmentlevel = itemstack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
			}
			if(chancepercent == 0){
				enchantmentlevel = 0;
			}
				if(debug){logDebug("DI enchantmentlevel=" + enchantmentlevel + " line:465");}
			double enchantmentlevelpercent = ((double)enchantmentlevel / 100);
				if(debug){logDebug("DI enchantmentlevelpercent=" + enchantmentlevelpercent + " line:467");}
			double chance = Math.random();
				if(debug){logDebug("DI chance=" + chance + " line:469");}
			
				if(debug){logDebug("DI chancepercent=" + chancepercent + " line:471");}
			chancepercent = chancepercent + enchantmentlevelpercent;
				if(debug){logDebug("DI chancepercent2=" + chancepercent + " line:473");}
			if (chancepercent > chance){
				return true;
			}
		}
		return false;
	}
	
	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event){// TODO:
		LivingEntity entity = event.getEntity();
		
			if(entity instanceof Player){
				if(debug){logDebug("EDE Entity is Player line:497");}
				if(entity.getKiller() instanceof Player){
					if(entity.getKiller().hasPermission("moremobheads.players")){
						if(DropIt(event, chanceConfig.getDouble("player_chance_percent", 0.50))){
							//Player daKiller = entity.getKiller();
							if(debug){logDebug("EDE Killer is Player line:501");}
							ItemStack helmet = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
							SkullMeta meta = (SkullMeta)helmet.getItemMeta();
							meta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(((Player) entity).getUniqueId()));
							meta.setDisplayName(((Player) entity).getDisplayName() + "'s Head");
							ArrayList<String> lore = new ArrayList();
							if(getConfig().getBoolean("lore.show_killer", true)){
								lore.add(ChatColor.RESET + "Killed by " + ChatColor.RESET + ChatColor.YELLOW + entity.getKiller().getDisplayName());
							}
							if(getConfig().getBoolean("lore.show_plugin_name", true)){
								lore.add(ChatColor.AQUA + "" + this.getName());
							}
							meta.setLore(lore);
								helmet.setItemMeta(meta);//																	 e2d4c388-42d5-4a96-b4c9-623df7f5e026
							helmet.setItemMeta(meta);
						
							entity.getWorld().dropItemNaturally(entity.getLocation(), helmet);
							if(debug){logDebug("EDE " + ((Player) entity).getDisplayName().toString() + " Player Head Dropped");}
						}
						return;
					}
				}
			}else if(event.getEntity() instanceof LivingEntity){
				/** Move this higher 
				double chancepercent = 0.50; //** Set to check config.yml later/
				String s = Double.toString(chancepercent);
				log("chancepercent=" + s.length());
				/** Move this higher */
				if(entity.getKiller() instanceof Player){
		 			//ItemStack itemstack = event.getEntity().getKiller().getInventory().getItemInMainHand();
					//if(itemstack != null){
						/**if(debug){logDebug("itemstack=" + itemstack.getType().toString() + " line:159");}
						int enchantmentlevel = itemstack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);//.containsEnchantment(Enchantment.LOOT_BONUS_MOBS);
						if(debug){logDebug("enchantmentlevel=" + enchantmentlevel + " line:161");}
						double enchantmentlevelpercent = ((double)enchantmentlevel / 100);
						if(debug){logDebug("enchantmentlevelpercent=" + enchantmentlevelpercent + " line:163");}
						double chance = Math.random();
						if(debug){logDebug("chance=" + chance + " line:165");}
						
						if(debug){logDebug("chancepercent=" + chancepercent + " line:167");}
						chancepercent = chancepercent + enchantmentlevelpercent;
						if(debug){logDebug("chancepercent2=" + chancepercent + " line:169");}*/
						//if(chancepercent > 0.00 && chancepercent < 0.99){
								//if (chancepercent > chance){
									//event.getDrops().add(new ItemStack(Material.CREEPER_HEAD, 1));
							if(entity.getKiller().hasPermission("moremobheads.mobs")){
								if(entity.getKiller().hasPermission("moremobheads.nametag")){
									if(entity.getCustomName() != null&&!(entity.getCustomName().contains("jeb_"))
												&&!(entity.getCustomName().contains("Toast"))){
											if(debug){logDebug("EDE customname=" + entity.getCustomName().toString());}
											if(entity instanceof Skeleton||entity instanceof Zombie||entity instanceof PigZombie){
												if(getServer().getPluginManager().getPlugin("SilenceMobs") != null){
													if(entity.getCustomName().toLowerCase().contains("silenceme")||entity.getCustomName().toLowerCase().contains("silence me")){
													return;
												}
												}
												boolean enforcewhitelist = getConfig().getBoolean("whitelist.enforce", false);
												boolean enforceblacklist = getConfig().getBoolean("blacklist.enforce", false);
												boolean onwhitelist = getConfig().getString("whitelist.player_head_whitelist", "").toLowerCase().contains(entity.getCustomName().toLowerCase());
												boolean onblacklist = getConfig().getString("blacklist.player_head_blacklist", "").toLowerCase().contains(entity.getCustomName().toLowerCase());
												if(DropIt(event, chanceConfig.getDouble("named_mob_chance_percent", 0.10))){
													if(enforcewhitelist&&enforceblacklist){
														if(onwhitelist&&!(onblacklist)){
															dropMobHead(entity, entity.getCustomName(), entity.getKiller());
														if(debug){logDebug("EDE " + entity.getCustomName().toString() + " Head Dropped");}
														}
													}else if(enforcewhitelist&&!enforceblacklist){
														if(onwhitelist){
															dropMobHead(entity, entity.getCustomName(), entity.getKiller());
														if(debug){logDebug("EDE " +entity.getCustomName().toString() + " Head Dropped");}
														}
													}else if(!enforcewhitelist&&enforceblacklist){
														if(!onblacklist){
															dropMobHead(entity, entity.getCustomName(), entity.getKiller());
														if(debug){logDebug("EDE " +entity.getCustomName().toString() + " Head Dropped");}
														}
													}else{
														dropMobHead(entity, entity.getCustomName(), entity.getKiller());
													if(debug){logDebug("EDE " +entity.getCustomName().toString() + " Head Dropped");}
													}
												}
											}
										return;
									}
								}
					 			//String name = event.getEntity().getName().toUpperCase().replace(" ", "_");
					 			String name = event.getEntityType().toString().replace(" ", "_");
					 			if(debug){logDebug("EDE name=" + name);}
								switch (name) {
					 			case "CREEPER":
					 				if(DropIt(event, chanceConfig.getDouble("creeper_chance_percent", defpercent))){
						 				if(getConfig().getBoolean("vanilla_heads.creeper", false)){
						 					entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.CREEPER_HEAD));
						 				}else{ // langName
						 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
						 							langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
						 				} // MobHeads.valueOf(name).getName() + " Head"
						 				if(debug){logDebug("EDE Creeper vanilla=" + getConfig().getBoolean("vanilla_heads.creeper", false));}
						 				if(debug){logDebug("EDE Creeper Head Dropped");}
					 				}
					 				break;
					 			case "ZOMBIE":
					 				if(DropIt(event, chanceConfig.getDouble("zombie_chance_percent", defpercent))){
					 					if(getConfig().getBoolean("vanilla_heads.zombie", false)){
						 					entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.ZOMBIE_HEAD));
						 				}else{
						 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
						 							langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
						 				}
					 					if(debug){logDebug("EDE Zombie vanilla=" + getConfig().getBoolean("vanilla_heads.zombie", false));}
					 					if(debug){logDebug("EDE Zombie Head Dropped");}
					 				}
					 				break;
					 			case "SKELETON":
					 				if(DropIt(event, chanceConfig.getDouble("skeleton_chance_percent", defpercent))){
					 					if(getConfig().getBoolean("vanilla_heads.skeleton", false)){
						 					entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.SKELETON_SKULL));
						 				}else{
						 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
						 							langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
						 				}
					 					if(debug){logDebug("EDE Skeleton vanilla=" + getConfig().getBoolean("vanilla_heads.skeleton", false));}
					 					if(debug){logDebug("EDE Skeleton Head Dropped");}
					 				}
					 				break;
					 			case "WITHER_SKELETON":
					 				if(DropIt(event, chanceConfig.getDouble("wither_skeleton_chance_percent", defpercent))){
					 					if(getConfig().getBoolean("vanilla_heads.wither_skeleton", false)){
						 					entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.WITHER_SKELETON_SKULL));
						 				}else{
						 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
						 							langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
						 				}
					 					if(debug){logDebug("EDE Wither Skeleton Head Dropped");}
					 				}
					 				break;
					 			case "ENDER_DRAGON":
					 				if(DropIt(event, chanceConfig.getDouble("ender_dragon_chance_percent", defpercent))){
					 					if(getConfig().getBoolean("vanilla_heads.ender_dragon", false)){
						 					entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.DRAGON_HEAD));
						 				}else{
						 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
						 							langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
						 				}
					 					if(debug){logDebug("EDE Ender Dragon Head Dropped");}
					 				}
					 				break;
					 			/**case "TROPICAL_FISH":
					 				TropicalFish daFish = (TropicalFish) entity;
					 				DyeColor daFishBody = daFish.getBodyColor();
					 				DyeColor daFishPatternColor = daFish.getPatternColor();
					 				Pattern	daFishType = daFish.getPattern();
					 				log("bodycolor=" + daFishBody.toString() + "\nPatternColor=" + daFishPatternColor.toString() + "\nPattern=" + daFishType.toString());
					 				//TropicalFishHeads daFishEnum = TropicalFishHeads.getIfPresent(name);
					 				
					 				if(DropIt(event, getConfig().getDouble(name + "_" +	daFishType + "_chance_percent", defpercent))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name + "_" +	daFishType).getTexture(), MobHeads.valueOf(name + "_" +	daFishType).getName(), entity.getKiller()));
					 				}
					 				if(debug){logDebug("Skeleton Head Dropped");}
					 				break;*/
					 			case "WITHER":
									//Wither wither = (Wither) event.getEntity();
									int random = randomBetween(1, 4);
									if(DropIt(event, chanceConfig.getDouble("wither_chance_percent", defpercent))){
										entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name + "_" + random).getTexture().toString(), 
					 							langName.getString(name.toLowerCase() + "." + random, MobHeads.valueOf(name + "_" + random).getName() + " Head"), entity.getKiller()));
										if(debug){logDebug("EDE Wither_" + random + " Head Dropped");}
									}
									break;
					 			case "WOLF":
					 				Wolf wolf = (Wolf) event.getEntity();
					 				if(DropIt(event, chanceConfig.getDouble(name.toLowerCase() + "_chance_percent", defpercent))){
						 				if(wolf.isAngry()){
						 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name + "_ANGRY").getTexture().toString(), 
						 							langName.getString(name.toLowerCase() + "_angry", MobHeads.valueOf(name + "_ANGRY").getName() + " Head"), entity.getKiller()));
						 					if(debug){logDebug("EDE Angry Wolf Head Dropped");}
						 				}else{
						 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
						 							langName.getString(name.toLowerCase(), event.getEntity().getName() + " Head"), entity.getKiller()));
						 					if(debug){logDebug("EDE Wolf Head Dropped");}
						 				}
					 				}
					 				break;
					 			case "FOX":
					 				Fox dafox = (Fox) entity;
					 				String dafoxtype = dafox.getFoxType().toString();
					 				if(DropIt(event, chanceConfig.getDouble("fox." + dafoxtype.toString() + "_chance_percent", defpercent))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name + "_" + dafoxtype).getTexture().toString(), 
					 							langName.getString(name.toLowerCase() + "." + dafoxtype, MobHeads.valueOf(name + "_" + dafoxtype).getName() + " Head"), entity.getKiller()));
					 				}
					 				if(debug){logDebug("EDE Fox Head Dropped");}
					 				break;
					 			case "CAT":
					 				Cat dacat = (Cat) entity;
					 				String dacattype = dacat.getCatType().toString();
					 				if(debug){logDebug("entity cat=" + dacat.getCatType());}
					 				if(DropIt(event, chanceConfig.getDouble("cat." + dacattype.toLowerCase() + "_chance_percent", defpercent))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(),
				 							makeSkull(CatHeads.valueOf(dacattype).getTexture().toString(), 
				 									langName.getString(name.toLowerCase() + "." + dacattype, CatHeads.valueOf(dacattype).getName() + " Head"), entity.getKiller()));
					 					if(debug){logDebug("EDE Cat Head Dropped");}
					 				}
					 				break;
					 			case "BEE":
					 				Bee daBee = (Bee) entity;
					 				int daAnger = daBee.getAnger();
					 				boolean daNectar = daBee.hasNectar();
						 				if(daAnger == 1&&daNectar == true){
						 					if(DropIt(event, chanceConfig.getDouble("bee.angry_pollinated_chance_percent", defpercent))){
						 						entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf("BEE_ANGRY_POLLINATED").getTexture().toString(), 
						 								langName.getString(name.toLowerCase() + ".angry_pollinated", "Angry Pollinated Bee Head"), entity.getKiller()));
						 						if(debug){logDebug("EDE Angry Pollinated Bee Head Dropped");}
						 					}
						 				}else if(daAnger == 1&&daNectar == false){
						 					if(DropIt(event, chanceConfig.getDouble("bee.angry_chance_percent", defpercent))){
						 						entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf("BEE_ANGRY").getTexture().toString(), 
						 								langName.getString(name.toLowerCase() + ".angry", "Angry Bee Head"), entity.getKiller()));
						 						if(debug){logDebug("EDE Angry Bee Head Dropped");}
						 					}
						 				}else if(daAnger == 0&&daNectar == true){
						 					if(DropIt(event, chanceConfig.getDouble("bee.pollinated_chance_percent", defpercent))){
						 						entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf("BEE_POLLINATED").getTexture().toString(), 
						 								langName.getString(name.toLowerCase() + ".pollinated", "Pollinated Bee Head"), entity.getKiller()));
						 						if(debug){logDebug("EDE Pollinated Bee Head Dropped");}
						 					}
						 				}else if(daAnger == 0&&daNectar == false){
						 					if(DropIt(event, chanceConfig.getDouble("bee.chance_percent", defpercent))){
						 						entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf("BEE").getTexture().toString(), 
						 								langName.getString(name.toLowerCase() + ".none", "Bee Head"), entity.getKiller()));
						 						if(debug){logDebug("EDE Bee Head Dropped");}
						 					}
						 				}
					 				break;
					 			case "LLAMA":
					 				Llama daLlama = (Llama) entity;
					 				String daLlamaColor = daLlama.getColor().toString();
					 				String daLlamaName = LlamaHeads.valueOf(name + "_" + daLlamaColor).getName() + " Head";//daLlamaColor.toLowerCase().replace("b", "B").replace("c", "C").replace("g", "G").replace("wh", "Wh") + " Llama Head";
					 				//log(name + "_" + daLlamaColor);
					 				if(DropIt(event, chanceConfig.getDouble("llama." + daLlamaColor.toLowerCase() + "_chance_percent", defpercent))){		
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(LlamaHeads.valueOf(name + "_" + daLlamaColor).getTexture().toString(), 
					 							langName.getString(name.toLowerCase() + "." + daLlamaColor.toLowerCase(), daLlamaName), entity.getKiller()));
					 					if(debug){logDebug("EDE Llama Head Dropped");}
					 				}
					 				break;
					 			case "HORSE":
					 				Horse daHorse = (Horse) entity;
					 				String daHorseColor = daHorse.getColor().toString();
					 				String daHorseName = HorseHeads.valueOf(name + "_" + daHorseColor).getName() + " Head";//daHorseColor.toLowerCase().replace("b", "B").replace("ch", "Ch").replace("cr", "Cr").replace("d", "D")
					 						//.replace("g", "G").replace("wh", "Wh").replace("_", " ") + " Horse Head";
					 				if(DropIt(event, chanceConfig.getDouble("horse." + daHorseColor.toLowerCase() + "_chance_percent", defpercent))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(HorseHeads.valueOf(name + "_" + daHorseColor).getTexture().toString(), 
					 							langName.getString(name.toLowerCase() + "." + daHorseColor.toLowerCase(), daHorseName), entity.getKiller()));
					 					if(debug){logDebug("EDE Horse Head Dropped");}
					 				}
					 				break;
					 			case "MUSHROOM_COW":
					 				MushroomCow daMushroom = (MushroomCow) entity;
					 				String daCowVariant = daMushroom.getVariant().toString();
					 				String daCowName = daCowVariant.toLowerCase().replace("br", "Br").replace("re", "Re") + " Mooshroom Head";
					 				if(debug){logDebug("EDE " + name + "_" + daCowVariant);}
					 				if(DropIt(event, chanceConfig.getDouble("mushroom_cow." + daCowVariant.toLowerCase() + "_chance_percent", defpercent))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name + "_" + daCowVariant).getTexture().toString(), 
					 							langName.getString(name.toLowerCase() + "." + daCowVariant.toLowerCase(), daCowName), entity.getKiller()));
					 					if(debug){logDebug("EDE Mooshroom Head Dropped");}
					 				}
					 				break;
					 			case "PANDA":
					 				Panda daPanda = (Panda) entity;
					 				String daPandaGene = daPanda.getMainGene().toString();
					 				String daPandaName = daPandaGene.toLowerCase().replace("br", "Br").replace("ag", "Ag").replace("la", "La")
					 						.replace("no", "No").replace("p", "P").replace("we", "We").replace("wo", "Wo") + " Panda Head";
					 				if(daPandaGene.equalsIgnoreCase("normal")){daPandaName.replace("normal ", "");}
					 				if(debug){logDebug("EDE " + name + "_" + daPandaGene);}
					 				if(DropIt(event, chanceConfig.getDouble("panda." + daPandaGene.toLowerCase() + "_chance_percent", defpercent))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name + "_" + daPandaGene).getTexture().toString(), 
					 							langName.getString(name.toLowerCase() + "." + daPandaGene.toLowerCase(), daPandaName), entity.getKiller()));
					 					if(debug){logDebug("EDE Panda Head Dropped");}
					 				}
					 				break;
					 			case "PARROT":
					 				Parrot daParrot = (Parrot) entity;
					 				String daParrotVariant = daParrot.getVariant().toString();
					 				String daParrotName = daParrotVariant.toLowerCase().replace("b", "B").replace("c", "C").replace("g", "G")
					 						.replace("red", "Red") + " Parrot Head";
					 				if(debug){logDebug("EDE " + name + "_" + daParrotVariant);}
					 				if(DropIt(event, chanceConfig.getDouble("parrot." + daParrotVariant.toLowerCase() + "_chance_percent", defpercent))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name + "_" + daParrotVariant).getTexture().toString(), 
					 							langName.getString(name.toLowerCase() + "." + daParrotVariant.toLowerCase(), daParrotName), entity.getKiller()));
					 					if(debug){logDebug("EDE Parrot Head Dropped");}
					 				}
					 				break;
					 			case "RABBIT":
					 				String daRabbitType;
					 				Rabbit daRabbit = (Rabbit) entity;
					 				daRabbitType = daRabbit.getRabbitType().toString();
					 				if(daRabbit.getCustomName() != null){
					 					if(daRabbit.getCustomName().contains("Toast")){
						 					daRabbitType = "Toast";
						 				}
					 				}
					 				String daRabbitName = RabbitHeads.valueOf(name + "_" + daRabbitType).getName() + " Head";
					 				if(debug){logDebug("EDE " + name + "_" + daRabbitType);}
					 				if(DropIt(event, chanceConfig.getDouble("rabbit." + daRabbitType.toLowerCase() + "_chance_percent", defpercent))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(RabbitHeads.valueOf(name + "_" + daRabbitType).getTexture().toString(), 
					 							langName.getString(name.toLowerCase() + "." + daRabbitType.toLowerCase(), daRabbitName), entity.getKiller()));
					 					if(debug){logDebug("EDE Rabbit Head Dropped");}
					 				}
					 				break;
					 			case "VILLAGER":
					 				Villager daVillager = (Villager) entity; // Location jobsite = daVillager.getMemory(MemoryKey.JOB_SITE);
					 				String daVillagerType = daVillager.getVillagerType().toString();
					 				String daVillagerProfession = daVillager.getProfession().toString();
					 				if(debug){logDebug("EDE name=" + name);}
					 				if(debug){logDebug("EDE profession=" + daVillagerProfession);}
					 				if(debug){logDebug("EDE type=" + daVillagerType);}
					 				String daName = name + "_" + daVillagerProfession + "_" + daVillagerType;
					 				if(debug){logDebug("EDE " + daName + "		 " + name + "_" + daVillagerProfession + "_" + daVillagerType);}
					 				String daVillagerName = VillagerHeads.valueOf(daName).getName() + " Head";
					 				if(DropIt(event, chanceConfig.getDouble("villager." + daVillagerType.toLowerCase() + "." + daVillagerProfession.toLowerCase() + "_chance_percent", defpercent))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(VillagerHeads.valueOf(name + "_" + daVillagerProfession + "_" + daVillagerType).getTexture().toString(), 
					 							langName.getString(name.toLowerCase() + "." + daVillagerType.toLowerCase() + "." + daVillagerProfession.toLowerCase()
					 									, daVillagerName), entity.getKiller()));
					 					if(debug){logDebug("EDE Villager Head Dropped");}
					 				}
					 				break;
					 			case "ZOMBIE_VILLAGER":
					 				ZombieVillager daZombieVillager = (ZombieVillager) entity;
					 				String daZombieVillagerProfession = daZombieVillager.getVillagerProfession().toString();
					 				String daZombieVillagerName = ZombieVillagerHeads.valueOf(name + "_" + daZombieVillagerProfession).getName() + " Head";
					 				if(debug){logDebug("EDE " + name + "_" + daZombieVillagerProfession);}
					 				if(DropIt(event, chanceConfig.getDouble("zombie_villager_chance_percent", defpercent))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(ZombieVillagerHeads.valueOf(name + "_" + daZombieVillagerProfession).getTexture().toString(), 
					 							langName.getString(name.toLowerCase() + "." + daZombieVillagerProfession.toLowerCase(), daZombieVillagerName), entity.getKiller()));
					 					if(debug){logDebug("EDE Zombie Villager Head Dropped");}
					 				}
					 				break;
					 			case "SHEEP":
					 				Sheep daSheep = (Sheep) entity;
					 				String daSheepColor = daSheep.getColor().toString();
					 				String daSheepName;
					 				
					 				if(daSheep.getCustomName() != null){
						 				if(daSheep.getCustomName().contains("jeb_")){
						 					daSheepColor = "jeb_";
						 				}else{
						 					daSheepColor = daSheep.getColor().toString();
						 				}
					 				}
					 				daSheepName = SheepHeads.valueOf(name + "_" + daSheepColor).getName() + " Head";
					 				if(debug){logDebug("EDE " + daSheepColor + "_" + name);}
					 				if(DropIt(event, chanceConfig.getDouble("sheep." + daSheepColor.toLowerCase() + "_chance_percent", defpercent))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(SheepHeads.valueOf(name + "_" + daSheepColor).getTexture().toString(), 
					 							langName.getString(name.toLowerCase() + "." + daSheepColor.toLowerCase(), daSheepName), entity.getKiller()));
					 					if(debug){logDebug("EDE Sheep Head Dropped");}
					 				}
					 				break;
					 			/**case "STRIDER":
					 				Strider strider = (Strider) entity;
					 				
					 				break;*/
					 			case "TRADER_LLAMA":
					 				TraderLlama daTraderLlama = (TraderLlama) entity;
					 				String daTraderLlamaColor = daTraderLlama.getColor().toString();
					 				String daTraderLlamaName = LlamaHeads.valueOf(name + "_" + daTraderLlamaColor).getName() + " Head";
					 				if(debug){logDebug("EDE " + daTraderLlamaColor + "_" + name);}
					 				if(DropIt(event, chanceConfig.getDouble("trader_llama." + daTraderLlamaColor + "_chance_percent", defpercent))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(LlamaHeads.valueOf(name + "_" + daTraderLlamaColor).getTexture().toString(), 
					 							langName.getString(name.toLowerCase() + "." + daTraderLlamaColor.toLowerCase(), daTraderLlamaName), entity.getKiller()));
					 					if(debug){logDebug("EDE Trader Llama Head Dropped");}
					 				}
					 				break;
					 			default:
					 				//makeSkull(MobHeads.valueOf(name).getTexture(), name);
					 				if(debug){logDebug("EDE name=" + name + " line:1005");}
					 				if(debug){logDebug("EDE texture=" + MobHeads.valueOf(name).getTexture().toString() + " line:1006");}
					 				if(debug){logDebug("EDE location=" + entity.getLocation().toString() + " line:1007");}
					 				if(debug){logDebug("EDE getName=" + event.getEntity().getName() + " line:1008");}
					 				if(debug){logDebug("EDE killer=" + entity.getKiller().toString() + " line:1009");}
					 				if(DropIt(event, chanceConfig.getDouble(name.toLowerCase() + "_chance_percent", defpercent))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
					 							langName.getString(name.toLowerCase(), event.getEntity().getName() + " Head"), entity.getKiller()));
					 					if(debug){logDebug("EDE " + name + " Head Dropped");}
					 				}
					 				if(debug){logDebug("EDE " + MobHeads.valueOf(name) + " killed");}
					 				break;
					 			}
							}
						//}
					//}
					return;
				}
			}
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event){ //onEntitySpawn(EntitySpawnEvent e) {
				if(getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)){
			Entity entity = event.getEntity();
					if(entity instanceof WanderingTrader){
						//traderHeads2 = YamlConfiguration.loadConfiguration(traderFile2);
						if(debug){logDebug("CSE WanderingTrader spawned");}
						WanderingTrader trader = (WanderingTrader) entity;
						List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();
						List<MerchantRecipe> oldRecipes = new ArrayList<MerchantRecipe>();
						oldRecipes = trader.getRecipes();
						// Loop through player heads
						
						/**int playernum = traderHeads.getInt("players.number") + 1;
						for(int i=1; i<playernum; i++){
							String texture = traderHeads.getString("players.player_" + i + ".texture");
							String name = traderHeads.getString("players.player_" + i + ".name");
							String uuid = traderHeads.getString("players.player_" + i + ".uuid");
							Player player = Bukkit.getPlayer("JoelYahwehOfWar");
							ItemStack itemstack = makeTraderSkull(texture, name, uuid, 1);
							player.getInventory().setItem(1, itemstack);
							itemstack = player.getInventory().getItem(1);
							player.getInventory().setItem(1, new ItemStack(Material.AIR, 1));
							ItemStack price1 = new ItemStack(Material.EMERALD);
							ItemStack price2 = new ItemStack(Material.AIR);
							// save item to traderheads2
							traderHeads2.set("players.player_" + i + ".price_1", price1);
							traderHeads2.set("players.player_" + i + ".price_2", price2);
							traderHeads2.set("players.player_" + i + ".itemstack", itemstack);
							log("player_" + i + " has been updated.");
						}
						// save number to trader_heads2
						traderHeads2.set("players.number", playernum - 1);
						log("traderFile2=" + traderFile2.getPath());
						if(traderHeads2 == null){
							log("null");
						}
						try {
							this.traderHeads2.save(traderFile2);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						log("players saved");
						
						int blocknum = traderHeads.getInt("blocks.number") + 1;
						for(int i=1; i<blocknum; i++){
							String texture = traderHeads.getString("blocks.block_" + i + ".texture");
							String name = traderHeads.getString("blocks.block_" + i + ".name");
							String uuid = traderHeads.getString("blocks.block_" + i + ".uuid");
							Material material = Material.matchMaterial(traderHeads.getString("blocks.block_" + i + ".material"));
							Player player = Bukkit.getPlayer("JoelYahwehOfWar");
							ItemStack itemstack = makeTraderSkull(texture, name, uuid, 1);
							player.getInventory().setItem(1, itemstack);
							itemstack = player.getInventory().getItem(1);
							player.getInventory().setItem(1, new ItemStack(Material.AIR, 1));
							ItemStack price1 = new ItemStack(Material.EMERALD);
							ItemStack price2 = new ItemStack(material);
							traderHeads2.set("blocks.block_" + i + ".price_1", price1);
							traderHeads2.set("blocks.block_" + i + ".price_2", price2);
							traderHeads2.set("blocks.block_" + i + ".itemstack", itemstack);
							log("block_" + i + " has been updated.");
						}
						traderHeads2.set("blocks.number", blocknum - 1);
						try {
							this.traderHeads2.save(traderFile2);
							saveConfig();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						log("blocks saved");*/
						
						/**
						 *  Player Heads
						 */ 
						if(getConfig().getBoolean("wandering_trades.player_heads.enabled", true)){
							int playerRandom = randomBetween(getConfig().getInt("wandering_trades.player_heads.min", 0), getConfig().getInt("wandering_trades.player_heads.max", 3));
							if(debug){logDebug("CSE playerRandom=" + playerRandom);}
							if(playerRandom > 0){
								if(debug){logDebug("CSE playerRandom > 0");}
								int numOfplayerheads = playerHeads.getInt("players.number");
								if(debug){logDebug("CSE numOfplayerheads=" + numOfplayerheads);}
								for(int i=0; i<playerRandom; i++){
									int randomPlayerHead = randomBetween(1, numOfplayerheads);
										if(debug){logDebug("CSE randomPlayerHead=" + randomPlayerHead);}
									ItemStack price1 = playerHeads.getItemStack("players.player_" + randomPlayerHead + ".price_1", new ItemStack(Material.AIR));
										if(debug){logDebug("CSE price1=" + price1);}
									ItemStack price2 = playerHeads.getItemStack("players.player_" + randomPlayerHead + ".price_2", new ItemStack(Material.AIR));
										if(debug){logDebug("CSE price2=" + price2);}
									ItemStack itemstack = playerHeads.getItemStack("players.player_" + randomPlayerHead + ".itemstack", new ItemStack(Material.AIR));
										if(debug){logDebug("CSE itemstack=" + itemstack);}
									MerchantRecipe recipe = new MerchantRecipe(itemstack, playerHeads.getInt("players.player_" + randomPlayerHead + ".quantity", (int) 3));
									recipe.setExperienceReward(true);
									recipe.addIngredient(price1);
									recipe.addIngredient(price2);
									recipes.add(recipe);
								}
							}
						}
						/**
						 *  Block Heads
						 */
						if(getConfig().getBoolean("wandering_trades.block_heads.enabled", true)){
							int blockRandom = randomBetween(getConfig().getInt("wandering_trades.block_heads.min", 0), getConfig().getInt("wandering_trades.block_heads.max", 5));
								if(debug){logDebug("CSE blockRandom=" + blockRandom);}
							if(blockRandom > 0){
									if(debug){logDebug("CSE blockRandom > 0");}
								int numOfblockheads = blockHeads.getInt("blocks.number");
									if(debug){logDebug("CSE numOfblockheads=" + numOfblockheads);}
								for(int i=0; i<blockRandom; i++){
										if(debug){logDebug("CSE i=" + i);}
									int randomBlockHead = randomBetween(1, numOfblockheads);
										if(debug){logDebug("CSE randomBlockHead=" + randomBlockHead);}
									ItemStack price1 = blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".price_1", new ItemStack(Material.AIR));
										if(debug){logDebug("CSE price1=" + price1);}
									ItemStack price2 = blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".price_2", new ItemStack(Material.AIR));
										if(debug){logDebug("CSE price2=" + price2);}
									ItemStack itemstack = blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
										if(debug){logDebug("CSE itemstack=" + itemstack);}
									MerchantRecipe recipe = new MerchantRecipe(itemstack, blockHeads.getInt("blocks.block_" + randomBlockHead + ".quantity", (int) 1));
										recipe.setExperienceReward(true);
										recipe.addIngredient(price1);
										recipe.addIngredient(price2);
										recipes.add(recipe);
								}
							}
						}
						
						/**
						 *  Custom Trades
						 */
						if(getConfig().getBoolean("wandering_trades.custom_trades.enabled", false)){
							int numOfCustomTrades = traderCustom.getInt("custom_trades.number") + 1;
								if(debug){logDebug("CSE numOfCustomTrades=" + numOfCustomTrades);}
							/**int customRandom = randomBetween(getConfig().getInt("wandering_trades.min_custom_trades", 0), getConfig().getInt("wandering_trades.max_custom_trades", 3));
								if(debug){logDebug("CSE customRandom=" + customRandom);}
							if(customRandom > 0){
									if(debug){logDebug("CSE customRandom > 0");}*/
								for(int randomCustomTrade=1; randomCustomTrade<numOfCustomTrades; randomCustomTrade++){
										if(debug){logDebug("CSE randomCustomTrade=" + randomCustomTrade);}
									//int randomCustomTrade = randomBetween(1, numOfCustomTrades);
									/** Fix chance later */
									double chance = Math.random();
										if(debug){logDebug("CSE chance=" + chance + " line:982");}
									if(traderCustom.getDouble("custom_trades.trade_" + randomCustomTrade + ".chance", 0.002) > chance){
											if(debug){logDebug("CSE randomCustomTrade=" + randomCustomTrade);}
										ItemStack price1 = traderCustom.getItemStack("custom_trades.trade_" + randomCustomTrade + ".price_1", new ItemStack(Material.AIR));
											if(debug){logDebug("CSE price1=" + price1.toString());}
										ItemStack price2 = traderCustom.getItemStack("custom_trades.trade_" + randomCustomTrade + ".price_2", new ItemStack(Material.AIR));
											if(debug){logDebug("CSE price2=" + price2.toString());}
										ItemStack itemstack = traderCustom.getItemStack("custom_trades.trade_" + randomCustomTrade + ".itemstack", new ItemStack(Material.AIR));
											if(debug){logDebug("CSE itemstack=" + itemstack.toString());}
										MerchantRecipe recipe = new MerchantRecipe(itemstack, traderCustom.getInt("custom_trades.trade_" + randomCustomTrade + ".quantity", (int) 1));
												recipe.setExperienceReward(true);
												recipe.addIngredient(price1);
												//log("price 1=" + price1.toString());
												recipe.addIngredient(price2);
												//log("price 2=" + price2.toString());
												recipes.add(recipe);
												//log("recipe=" + recipe.getIngredients().toString());
									}
								}
							//}
						}
						
						
	/** experimental code 
				log("itemstack set");
				//LivingEntity mob = (LivingEntity) event.getRightClicked();
				//Villager villager = (Villager) mob;
				//List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();
				MerchantRecipe recipe = new MerchantRecipe(getConfig().getItemStack("itemstack"), 1);
				recipe.addIngredient(new ItemStack(Material.EMERALD));
								recipes.add(recipe);
								//villager.setRecipes(recipes);
		/** experimental code */
								
						/*ItemStack itemstack = new ItemStack(Material.BOW);
						Map<Enchantment, Integer> enchantments = null;
				itemstack.addEnchantments(enchantments);
				*/
						
							//MerchantRecipe recipe = new MerchantRecipe(makeTraderSkull(traderHeads.getString("player1.texture"), traderHeads.getString("player1.name"), traderHeads.getString("player1.uuid")), 3);
							//recipe.setExperienceReward(true);
							//recipe.addIngredient(new ItemStack(Material.EMERALD));
							// then loop through block heads
							//recipes.add(recipe);
				//Material.getMaterial("ender_chest");
				//traderHeads.getString("block" + randomPlayerHead + ".texture");
					if(getConfig().getBoolean("wandering_trades.keep_default_trades", true)){
						recipes.addAll(oldRecipes);
					}
					trader.setRecipes(recipes);
					}
				}
				
	}
	
	public int randomBetween(int min, int max){
		Random r = new Random();
		int random = r.nextInt(max + 1);
		if((min + random) > max){
			return max;
		}
		return min + random;
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event)
		{
			Player p = event.getPlayer();
			//if(p.isOp() && UpdateCheck||p.hasPermission("moremobheads.showUpdateAvailable")){	
			/** Notify Ops */
			if(UpdateAvailable&&(p.isOp()||p.hasPermission("moremobheads.showUpdateAvailable"))){
				p.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("newvers") + " \n" + ChatColor.GREEN + UpdateChecker.getResourceUrl() + ChatColor.RESET);
			}

			if(p.getDisplayName().equals("JoelYahwehOfWar")||p.getDisplayName().equals("JoelGodOfWar")){
				p.sendMessage(this.getName() + " " + this.getDescription().getVersion() + " Hello father!");
				//p.sendMessage("seed=" + p.getWorld().getSeed());
			}
	}
	
	public void makeHead(EntityDeathEvent event, Material material){// TODO:
		 ItemStack itemstack = event.getEntity().getKiller().getInventory().getItemInMainHand();
			if(itemstack != null){
					if(debug){logDebug("itemstack=" + itemstack.getType().toString() + " line:954");}
				int enchantmentlevel = itemstack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);//.containsEnchantment(Enchantment.LOOT_BONUS_MOBS);
					if(debug){logDebug("enchantmentlevel=" + enchantmentlevel + " line:956");}
				double enchantmentlevelpercent = ((double)enchantmentlevel / 100);
					if(debug){logDebug("enchantmentlevelpercent=" + enchantmentlevelpercent + " line:958");}
				double chance = Math.random();
					if(debug){logDebug("chance=" + chance + " line:960");}
				double chancepercent = 0.25; /** Set to check config.yml later*/
					if(debug){logDebug("chancepercent=" + chancepercent + " line:962");}
				chancepercent = chancepercent + enchantmentlevelpercent;
					if(debug){logDebug("chancepercent2=" + chancepercent + " line:964");}
				if(chancepercent > 0.00 && chancepercent < 0.99){
						if (chancepercent > chance){
							event.getDrops().add(new ItemStack(material, 1));
						}
				}
			}
	}
	
	public ItemStack makeTraderSkull(String textureCode, String headName, String uuid, int amount){// TODO:
		ItemStack item = new ItemStack(Material.PLAYER_HEAD, amount);
		if(textureCode == null) return item;
		SkullMeta meta = (SkullMeta) item.getItemMeta();

		GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(textureCode.getBytes()), textureCode);
		profile.getProperties().put("textures", new Property("textures", textureCode));
		profile.getProperties().put("SkullOwner", new Property("id", uuid));
		profile.getProperties().put("display", new Property("Name", headName));
		setGameProfile(meta, profile);
		ArrayList<String> lore = new ArrayList();
		if(getConfig().getBoolean("lore.show_plugin_name", true)){
			lore.add(ChatColor.AQUA + "MoreMobHeads");
		}
		meta.setLore(lore);
		meta.setLore(lore);
		
		//meta.setOwningPlayer(Bukkit.getOfflinePlayer(ownerUUID));
		meta.setDisplayName(headName);
		item.setItemMeta(meta);
		return item;
}
	
	public ItemStack makeSkull(String textureCode, String headName, Player killer){// TODO:
			ItemStack item = new ItemStack(Material.PLAYER_HEAD);
			if(textureCode == null) return item;
			SkullMeta meta = (SkullMeta) item.getItemMeta();

			GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(textureCode.getBytes()), textureCode);
			profile.getProperties().put("textures", new Property("textures", textureCode));
			profile.getProperties().put("display", new Property("Name", headName));
			setGameProfile(meta, profile);
			ArrayList<String> lore = new ArrayList();
			
			if(getConfig().getBoolean("lore.show_killer", true)){
				lore.add(ChatColor.RESET + "Killed by " + ChatColor.RESET + ChatColor.YELLOW + killer.getDisplayName());
			}
			if(getConfig().getBoolean("lore.show_plugin_name", true)){
				lore.add(ChatColor.AQUA + "MoreMobHeads");
			}
				meta.setLore(lore);
			meta.setLore(lore);
			
			//meta.setOwningPlayer(Bukkit.getOfflinePlayer(ownerUUID));
			meta.setDisplayName(headName);
			item.setItemMeta(meta);
			return item;
	}
	private static Field fieldProfileItem;
	public static void setGameProfile(SkullMeta meta, GameProfile profile){// TODO:
			try{
				if(fieldProfileItem == null) fieldProfileItem = meta.getClass().getDeclaredField("profile");
				fieldProfileItem.setAccessible(true);
				fieldProfileItem.set(meta, profile);
			}
			catch(NoSuchFieldException e){e.printStackTrace();}
			catch(SecurityException e){e.printStackTrace();}
			catch(IllegalArgumentException e){e.printStackTrace();}
			catch(IllegalAccessException e){e.printStackTrace();}
	}
	public void consoleInfo(String state) {// TODO:
			PluginDescriptionFile pdfFile = this.getDescription();
			logger.info("**************************************");
			logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " is " + state);
			logger.info("**************************************");
	}
		
	public	void log(String dalog){// TODO:
		logger.info("" + this.getName() + " " + dalog );
	}
	public	void logDebug(String dalog){
		log(" " + this.getDescription().getVersion() + " [DEBUG] " + dalog);
	}
	public void logWarn(String dalog){
		log(" " + this.getDescription().getVersion() + " [WARN] " + dalog);
	}
	
	public static String getMCVersion() {
		String strVersion = Bukkit.getVersion();
		strVersion = strVersion.substring(strVersion.indexOf("MC: "), strVersion.length());
		strVersion = strVersion.replace("MC: ", "").replace(")", "");
		return strVersion;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
			//log("command=" + cmd.getName() + " args=" + args[0] + args[1]);
			if (cmd.getName().equalsIgnoreCase("MMH")){
				if (args.length == 0){
					sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
					sender.sendMessage(ChatColor.WHITE + " ");
					sender.sendMessage(ChatColor.WHITE + " /mmh reload - " + lang.get("reload", "Reloads this plugin."));//subject to server admin approval");
					sender.sendMessage(ChatColor.WHITE + " /mmh toggledebug - " + lang.get("srdebuguse", "Temporarily toggles debug."));//Cancels SinglePlayerSleep");
					if(getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)){
						sender.sendMessage(ChatColor.WHITE + " /mmh playerheads - " + lang.get("playerheads", "Shows how to use the playerheads commands"));
						sender.sendMessage(ChatColor.WHITE + " /mmh blockheads - " + lang.get("blockheads", "Shows how to use the blockheads commands"));
						sender.sendMessage(ChatColor.WHITE + " /mmh customtrader - " + lang.get("customtrader", "Shows how to use the customtrader commands"));
					}
					sender.sendMessage(ChatColor.WHITE + " ");
					sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
					return true;
				}
				if(args[0].equalsIgnoreCase("reload")){
					if(sender.isOp()||sender.hasPermission("moremobheads.reload")||!(sender instanceof Player)){
						log("Loading config file...");
						try {
							getConfig().load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							config.load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e1) {
							logWarn("Could not load config.yml");
							e1.printStackTrace();
						}
						if(getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)){
							/** Trader heads load */
							playerFile = new File(getDataFolder() + "" + File.separatorChar + "player_heads.yml");//\
							if(debug){logDebug("player_heads=" + playerFile.getPath());}
							if(!playerFile.exists()){																	// checks if the yaml does not exist
								saveResource("player_heads.yml", true);
								log("player_heads.yml not found! copied player_heads.yml to " + getDataFolder() + "");
								//ConfigAPI.copy(getResource("lang.yml"), langFile); // copies the yaml from your jar to the folder /plugin/<pluginName>
							}
						log("Loading player_heads file...");
							playerHeads = new YamlConfiguration();
							try {
								playerHeads.load(playerFile);
							} catch (IOException | InvalidConfigurationException e) {
								e.printStackTrace();
							}
							blockFile = new File(getDataFolder() + "" + File.separatorChar + "block_heads.yml");//\
							if(debug){logDebug("block_heads=" + blockFile.getPath());}
							if(!blockFile.exists()){																	// checks if the yaml does not exist
								saveResource("block_heads.yml", true);
								log("block_heads.yml not found! copied block_heads.yml to " + getDataFolder() + "");
								//ConfigAPI.copy(getResource("lang.yml"), langFile); // copies the yaml from your jar to the folder /plugin/<pluginName>
							}
						log("Loading block_heads file...");
							blockHeads = new YamlConfiguration();
							try {
								blockHeads.load(blockFile);
							} catch (IOException | InvalidConfigurationException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
					
							/** Custom Trades load */
							customFile = new File(getDataFolder() + "" + File.separatorChar + "custom_trades.yml");//\
							if(debug){logDebug("customFile=" + customFile.getPath());}
							if(!customFile.exists()){																	// checks if the yaml does not exist
								saveResource("custom_trades.yml", true);
								log("custom_trades.yml not found! copied custom_trades.yml to " + getDataFolder() + "");
								//ConfigAPI.copy(getResource("lang.yml"), langFile); // copies the yaml from your jar to the folder /plugin/<pluginName>
							}
						log("Loading custom_trades file...");
							traderCustom = new YamlConfiguration();
							try {
								traderCustom.load(customFile);
							} catch (IOException | InvalidConfigurationException e) {
								e.printStackTrace();
							}
						}
						//showkiller = getConfig().getBoolean("lore.show_killer", true);
						//showpluginname = getConfig().getBoolean("lore.show_plugin_name", true);
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("reloaded"));
						return true;
					}else if(!sender.hasPermission("moremobheads.reload")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("noperm"));
						return false;
					}
					}
				if(args[0].equalsIgnoreCase("toggledebug")||args[0].equalsIgnoreCase("td")){
				if(sender.isOp()||sender.hasPermission("moremobheads.toggledebug")||!(sender instanceof Player)){
					debug = !debug;
					sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("debugtrue").toString().replace("boolean", "" + debug));
					return true;
				}else if(!sender.hasPermission("moremobheads.toggledebug")){
					sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("noperm"));
					return false;
				}
				}
				if(args[0].equalsIgnoreCase("customtrader")||args[0].equalsIgnoreCase("ct")){
					if(sender.hasPermission("moremobheads.customtrader")&&sender instanceof Player
							&&getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)){
						log("has permission");
						Player player = (Player) sender;
						if(!(args.length >= 2)){
							sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
							sender.sendMessage(ChatColor.WHITE + " ");
							sender.sendMessage(ChatColor.WHITE + " /mmh ct - " + lang.getString("cthelp"));
							sender.sendMessage(ChatColor.WHITE + " /mmh ct add - " + lang.getString("ctadd") + "custom_trades.yml");
							sender.sendMessage(ChatColor.WHITE + " /mmh ct remove # - " + lang.getString("ctremove"));
							sender.sendMessage(ChatColor.WHITE + " /mmh ct replace # - " + lang.getString("ctreplace").replace("<num>", "#"));
							//sender.sendMessage(ChatColor.WHITE + " ");
							sender.sendMessage(ChatColor.WHITE + " ");
							sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
							return true;
						}else if(args[1].equalsIgnoreCase("add")){
							log("add");
							ItemStack itemstack = player.getInventory().getItemInOffHand();
							ItemStack price1 = player.getInventory().getItem(0);
							ItemStack price2 = player.getInventory().getItem(1);
							if(price1 == null){price1 = new ItemStack(Material.AIR);}
							if(price2 == null){price2 = new ItemStack(Material.AIR);}
							//Material price1 = item1.getType();
							//Material price2 = item2.getType();
							
							if(itemstack.getType() == Material.AIR||price1 == null||price1.getType() == Material.AIR){
								log("error air");
								sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
								sender.sendMessage(ChatColor.WHITE + " ");
								sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline1") + "custom_trades.yml");
								sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline2"));
								sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline3"));
								sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline4") + "/mmh ct add");
								sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline5") + "custom trade.");
								sender.sendMessage(ChatColor.WHITE + " ");
								sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
								return false;
							}
							int tradeNumber = (int) traderCustom.get("custom_trades.number", 1);
							traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".price_1", price1);
							traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".price_2", price2);
							traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".itemstack", itemstack);
							traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".quantity", itemstack.getAmount());
							traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".chance", 0.002);
							traderCustom.set("custom_trades.number", (tradeNumber + 1));
							//log("customFile=" + customFile);
							try {
								traderCustom.save(customFile);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.WHITE + " trade_" + (tradeNumber + 1) + " " + lang.get("ctsuccessadd"));
							return true;
						}else if(args[1].equalsIgnoreCase("remove")){
							if(!(args.length >= 3)){
								sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("ctargument"));
								return false;
							}else{
								if(isInteger(args[2])){
									traderCustom.set("custom_trades.trade_" + args[2] + ".price_1", "");
									traderCustom.set("custom_trades.trade_" + args[2] + ".price_2", "");
									traderCustom.set("custom_trades.trade_" + args[2] + ".itemstack", "");
									traderCustom.set("custom_trades.trade_" + args[2] + ".quantity", "");
									traderCustom.set("custom_trades.trade_" + args[2] + ".chance", "");
									if(debug){logDebug("customFile=" + customFile);}
									try {
										traderCustom.save(customFile);
									} catch (IOException e) {
										sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("cterror"));
										return false;
										//e.printStackTrace();
									}
									sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.WHITE + " trade_" + args[2] + " " + lang.get("ctsuccessrem"));
									return true;
								}else{
									sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("ctnumreq"));
								}
							}
						}else if(args[1].equalsIgnoreCase("replace")){
							if(args.length != 3){
								sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("ctargument"));
								return false;
							}else{
								if(isInteger(args[2])){
									ItemStack itemstack = player.getInventory().getItemInOffHand();
									ItemStack price1 = player.getInventory().getItem(0);
									ItemStack price2 = player.getInventory().getItem(1);
									if(price1 == null){price1 = new ItemStack(Material.AIR);}
									if(price2 == null){price2 = new ItemStack(Material.AIR);}
									if(itemstack.getType() == Material.AIR||price1 == null||price1.getType() == Material.AIR){
										sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
										sender.sendMessage(ChatColor.WHITE + " ");
										sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline1") + "custom_trades.yml");
										sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline2"));
										sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline3"));
										sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline4") + "/mmh ct add");
										sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline5") + "custom trade.");
										sender.sendMessage(ChatColor.WHITE + " ");
										sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
										return false;
									}
									int tradeNumber = Integer.parseInt(args[2]);
									traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".price_1", price1);
									traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".price_2", price2);
									traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".itemstack", itemstack);
									traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".quantity", itemstack.getAmount());
									traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".chance", 0.002);
									//log("customFile=" + customFile);
									try {
										traderCustom.save(customFile);
									} catch (IOException e) {
										sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("cterror"));
										return false;
										//e.printStackTrace();
									}
									sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.WHITE + " trade_" + args[2] + " " + lang.get("ctsuccessrep"));
									return true;
								}else{
									sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("ctnumreq"));
									return false;
								}
							}
						}
					}else if(!(sender instanceof Player)){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("noconsole"));
						return false;
					}else if(!sender.hasPermission("moremobheads.customtrader")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("nopermordisabled"));
					return false;
					}
				}
				if(args[0].equalsIgnoreCase("playerheads")||args[0].equalsIgnoreCase("ph")){
					if(sender.hasPermission("moremobheads.playerheads")&&sender instanceof Player
							&&getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)){
						Player player = (Player) sender;
						if(!(args.length >= 2)){
							sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
							sender.sendMessage(ChatColor.WHITE + " ");
							sender.sendMessage(ChatColor.WHITE + " /mmh ph - " + lang.getString("cthelp"));
							sender.sendMessage(ChatColor.WHITE + " /mmh ph add - " + lang.getString("ctadd") + "player_heads.yml");
							sender.sendMessage(ChatColor.WHITE + " /mmh ph remove # - " + lang.getString("ctremove"));
							sender.sendMessage(ChatColor.WHITE + " /mmh ph replace # - " + lang.getString("ctreplace").replace("<num>", "#"));
							//sender.sendMessage(ChatColor.WHITE + " ");
							sender.sendMessage(ChatColor.WHITE + " ");
							sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
							return true;
						}else if(args[1].equalsIgnoreCase("add")){
							ItemStack itemstack = player.getInventory().getItemInOffHand();
							ItemStack price1 = player.getInventory().getItem(0);
							ItemStack price2 = player.getInventory().getItem(1);
							if(price1 == null){price1 = new ItemStack(Material.AIR);}
							if(price2 == null){price2 = new ItemStack(Material.AIR);}
							//Material price1 = item1.getType();
							//Material price2 = item2.getType();
							
							if(itemstack.getType() == Material.AIR||price1 == null||price1.getType() == Material.AIR||itemstack.getType() != Material.PLAYER_HEAD){
								sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
								sender.sendMessage(ChatColor.WHITE + " ");
								if(itemstack.getType() != Material.PLAYER_HEAD){
									sender.sendMessage(ChatColor.RED + " MUST BE PLAYERHEAD");
									sender.sendMessage(ChatColor.WHITE + " ");
								}
								sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline1") + "player_heads.yml");
								sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline2"));
								sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline3"));
								sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline4") + "/mmh ph add");
								sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline5") + "player head.");
								sender.sendMessage(ChatColor.WHITE + " ");
								sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
								return false;
							}
							int tradeNumber = (int) playerHeads.get("players.number", 1);
							playerHeads.set("players.player_" + (tradeNumber + 1) + ".price_1", price1);
							playerHeads.set("players.player_" + (tradeNumber + 1) + ".price_2", price2);
							playerHeads.set("players.player_" + (tradeNumber + 1) + ".itemstack", itemstack);
							playerHeads.set("players.player_" + (tradeNumber + 1) + ".quantity", itemstack.getAmount());
							//playerHeads.set("players.player_" + (tradeNumber + 1) + ".chance", 0.002);
							playerHeads.set("players.number", (tradeNumber + 1));
							//log("customFile=" + customFile);
							try {
								playerHeads.save(playerFile);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.WHITE + " player_" + (tradeNumber + 1) + " " + lang.get("ctsuccessadd"));
							return true;
						}else if(args[1].equalsIgnoreCase("remove")){
							if(!(args.length >= 3)){
								sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("ctargument"));
								return false;
							}else{
								if(isInteger(args[2])){
									playerHeads.set("players.player_" + args[2] + ".price_1", "");
									playerHeads.set("players.player_" + args[2] + ".price_2", "");
									playerHeads.set("players.player_" + args[2] + ".itemstack", "");
									playerHeads.set("players.player_" + args[2] + ".quantity", "");
									//playerHeads.set("custom_trades.trade_" + args[2] + ".chance", "");
									if(debug){logDebug("playerFile=" + playerFile);}
									try {
										playerHeads.save(playerFile);
									} catch (IOException e) {
										sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("cterror") + "custom_trades.yml!");
										return false;
										//e.printStackTrace();
									}
									sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.WHITE + " player_" + args[2] + " " + lang.get("ctsuccessrem"));
									return true;
								}else{
									sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("ctnumreq"));
								}
							}
						}else if(args[1].equalsIgnoreCase("replace")){
							if(args.length != 3){
								sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("ctargument"));
								return false;
							}else{
								if(isInteger(args[2])){
									ItemStack itemstack = player.getInventory().getItemInOffHand();
									ItemStack price1 = player.getInventory().getItem(0);
									ItemStack price2 = player.getInventory().getItem(1);
									if(price1 == null){price1 = new ItemStack(Material.AIR);}
									if(price2 == null){price2 = new ItemStack(Material.AIR);}
									if(itemstack.getType() == Material.AIR||price1 == null||price1.getType() == Material.AIR||itemstack.getType() != Material.PLAYER_HEAD){
										sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
										sender.sendMessage(ChatColor.WHITE + " ");
										if(itemstack.getType() != Material.PLAYER_HEAD){
											sender.sendMessage(ChatColor.RED + " MUST BE PLAYERHEAD");
											sender.sendMessage(ChatColor.WHITE + " ");
										}
										sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline1") + "player_heads.yml");
										sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline2"));
										sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline3"));
										sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline4") + "/mmh ph add");
										sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline5") + "player head.");
										sender.sendMessage(ChatColor.WHITE + " ");
										sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
										return false;
									}
									int tradeNumber = Integer.parseInt(args[2]);
									playerHeads.set("players.player_" + (tradeNumber) + ".price_1", price1);
									playerHeads.set("players.player_" + (tradeNumber) + ".price_2", price2);
									playerHeads.set("players.player_" + (tradeNumber) + ".itemstack", itemstack);
									playerHeads.set("players.player_" + (tradeNumber) + ".quantity", itemstack.getAmount());
									//playerHeads.set("players.player_" + (tradeNumber + 1) + ".chance", 0.002);
									//log("customFile=" + customFile);
									try {
										playerHeads.save(playerFile);
									} catch (IOException e) {
										sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("cterror") + "player_heads.yml!");
										return false;
										//e.printStackTrace();
									}
									sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.WHITE + " player_" + args[2] + " " + lang.get("ctsuccessrep"));
									return true;
								}else{
									sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("ctnumreq"));
									return false;
								}
							}
						}
					}else if(!(sender instanceof Player)){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("noconsole"));
						return false;
					}else if(!sender.hasPermission("moremobheads.playerheads")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("nopermordisabled"));
					return false;
					}
				}
				if(args[0].equalsIgnoreCase("blockheads")||args[0].equalsIgnoreCase("bh")){
					if(sender.hasPermission("moremobheads.blockheads")&&sender instanceof Player
							&&getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)){
						Player player = (Player) sender;
						if(!(args.length >= 2)){
							sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
							sender.sendMessage(ChatColor.WHITE + " ");
							sender.sendMessage(ChatColor.WHITE + " /mmh bh - " + lang.getString("cthelp"));
							sender.sendMessage(ChatColor.WHITE + " /mmh bh add - " + lang.getString("ctadd") + "block_heads.yml");
							sender.sendMessage(ChatColor.WHITE + " /mmh bh remove # - " + lang.getString("ctremove"));
							sender.sendMessage(ChatColor.WHITE + " /mmh bh replace # - " + lang.getString("ctreplace").replace("<num>", "#"));
							//sender.sendMessage(ChatColor.WHITE + " ");
							sender.sendMessage(ChatColor.WHITE + " ");
							sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
							return true;
						}else if(args[1].equalsIgnoreCase("add")){
							ItemStack itemstack = player.getInventory().getItemInOffHand();
							ItemStack price1 = player.getInventory().getItem(0);
							ItemStack price2 = player.getInventory().getItem(1);
							if(price1 == null){price1 = new ItemStack(Material.AIR);}
							if(price2 == null){price2 = new ItemStack(Material.AIR);}
							//Material price1 = item1.getType();
							//Material price2 = item2.getType();
							
							if(itemstack.getType() == Material.AIR||price1 == null||price1.getType() == Material.AIR||itemstack.getType() != Material.PLAYER_HEAD){
								sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
								sender.sendMessage(ChatColor.WHITE + " ");
								if(itemstack.getType() != Material.PLAYER_HEAD){
									sender.sendMessage(ChatColor.RED + " MUST BE PLAYERHEAD");
									sender.sendMessage(ChatColor.WHITE + " ");
								}
								sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline1") + "block_heads.yml");
								sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline2"));
								sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline3"));
								sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline4") + "/mmh bh add");
								sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline5") + "block head.");
								sender.sendMessage(ChatColor.WHITE + " ");
								sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
								return false;
							}
							int tradeNumber = (int) blockHeads.get("blocks.number", 1);
							blockHeads.set("blocks.block_" + (tradeNumber + 1) + ".price_1", price1);
							blockHeads.set("blocks.block_" + (tradeNumber + 1) + ".price_2", price2);
							blockHeads.set("blocks.block_" + (tradeNumber + 1) + ".itemstack", itemstack);
							blockHeads.set("blocks.block_" + (tradeNumber + 1) + ".quantity", itemstack.getAmount());
							//blockHeads.set("blocks.block_" + (tradeNumber + 1) + ".chance", 0.002);
							blockHeads.set("blocks.number", (tradeNumber + 1));
							//log("customFile=" + customFile);
							try {
								blockHeads.save(blockFile);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.WHITE + " block_" + (tradeNumber + 1) + " " + lang.get("ctsuccessadd"));
							return true;
						}else if(args[1].equalsIgnoreCase("remove")){
							if(!(args.length >= 3)){
								sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("ctargument"));
								return false;
							}else{
								if(isInteger(args[2])){
									blockHeads.set("blocks.block_" + args[2] + ".price_1", "");
									blockHeads.set("blocks.block_" + args[2] + ".price_2", "");
									blockHeads.set("blocks.block_" + args[2] + ".itemstack", "");
									blockHeads.set("blocks.block_" + args[2] + ".quantity", "");
									//blockHeads.set("blocks.block_" + args[2] + ".chance", "");
									if(debug){logDebug("blockFile=" + blockFile);}
									try {
										blockHeads.save(blockFile);
									} catch (IOException e) {
										sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("cterror") + "block_heads.yml!");
										return false;
										//e.printStackTrace();
									}
									sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.WHITE + " block_" + args[2] + " " + lang.get("ctsuccessrem"));
									return true;
								}else{
									sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("ctnumreq"));
								}
							}
						}else if(args[1].equalsIgnoreCase("replace")){
							if(args.length != 3){
								sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("ctargument"));
								return false;
							}else{
								if(isInteger(args[2])){
									ItemStack itemstack = player.getInventory().getItemInOffHand();
									ItemStack price1 = player.getInventory().getItem(0);
									ItemStack price2 = player.getInventory().getItem(1);
									if(price1 == null){price1 = new ItemStack(Material.AIR);}
									if(price2 == null){price2 = new ItemStack(Material.AIR);}
									if(itemstack.getType() == Material.AIR||price1 == null||price1.getType() == Material.AIR||itemstack.getType() != Material.PLAYER_HEAD){
										sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
										sender.sendMessage(ChatColor.WHITE + " ");
										if(itemstack.getType() != Material.PLAYER_HEAD){
											sender.sendMessage(ChatColor.RED + " MUST BE PLAYERHEAD");
											sender.sendMessage(ChatColor.WHITE + " ");
										}
										sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline1") + "block_heads.yml");
										sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline2"));
										sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline3"));
										sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline4") + "/mmh bh add");
										sender.sendMessage(ChatColor.WHITE + " " + lang.getString("ctline5") + "block head.");
										sender.sendMessage(ChatColor.WHITE + " ");
										sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
										return false;
									}
									int tradeNumber = Integer.parseInt(args[2]);
									blockHeads.set("blocks.block_" + (tradeNumber) + ".price_1", price1);
									blockHeads.set("blocks.block_" + (tradeNumber) + ".price_2", price2);
									blockHeads.set("blocks.block_" + (tradeNumber) + ".itemstack", itemstack);
									blockHeads.set("blocks.block_" + (tradeNumber) + ".quantity", itemstack.getAmount());
									//blockHeads.set("blocks.block_" + (tradeNumber + 1) + ".chance", 0.002);
									//log("customFile=" + customFile);
									try {
										blockHeads.save(blockFile);
									} catch (IOException e) {
										sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("cterror") + "block_heads.yml");
										return false;
										//e.printStackTrace();
									}
									sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.WHITE + " block_" + args[2] + " " + lang.get("ctsuccessrep"));
									return true;
								}else{
									sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("ctnumreq"));
									return false;
								}
							}
						}
					}else if(!(sender instanceof Player)){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("noconsole"));
						return false;
					}else if(!sender.hasPermission("moremobheads.blockheads")){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("nopermordisabled"));
					return false;
					}
				}
			}
			return false;
	}
	
	boolean isInteger(String s){
	    try{
	        Integer.parseInt(s);
	        return true;
	    }catch (NumberFormatException ex){
	        return false;
	    }
	}
	
	@EventHandler
	public void OnTake(EntityPickupItemEvent e){
	//ItemStack item = new ItemStack(7);
	LivingEntity entity = e.getEntity();
	Item item = e.getItem();
		if(entity instanceof Player){
			//log("" + e.getItem().getItemStack().toString());//e.getItem().getType() instanceof Material.PLAYER_HEAD
			Material headtype = item.getItemStack().getType();
			if (headtype == Material.PLAYER_HEAD&&headtype != Material.CREEPER_HEAD&&headtype != Material.ZOMBIE_HEAD&&headtype != Material.SKELETON_SKULL
					&&headtype != Material.WITHER_SKELETON_SKULL&&headtype != Material.DRAGON_HEAD){//e.getItem().getItemStack().equals(new ItemStack(Material.PLAYER_HEAD))
					if(debug){logDebug("EPIE isPlayerEPIE=true");}
					if(debug){logDebug("EPIE item.length=" +	item.getName().length());}
				SkullMeta skullname = (SkullMeta) item.getItemStack().getItemMeta();
				if(skullname.getOwner() != null){
					String name = skullname.getOwner().toString();
					if(debug){logDebug("EPIE name=" + name);}
					if(debug){logDebug("EPIE lore=" + skullname.getLore());}
					if(skullname.getOwner().toString().length() >= 40){
						if(debug){logDebug("EPIE ownerName.lngth >= 40");}
							ItemStack itmStack = e.getItem().getItemStack();
							SkullMeta meta = (SkullMeta) e.getItem().getItemStack().getItemMeta();
							String daMobName = "null";
							if(meta != null){
								String isCat = CatHeads.getNameFromTexture(meta.getOwner().toString());
								String isHorse = HorseHeads.getNameFromTexture(meta.getOwner().toString());
								String isLlama = LlamaHeads.getNameFromTexture(meta.getOwner().toString());
								String isMobHead = MobHeads.getNameFromTexture(meta.getOwner().toString());
								String isRabbit = RabbitHeads.getNameFromTexture(meta.getOwner().toString());
								String isSheep = SheepHeads.getNameFromTexture(meta.getOwner().toString());
								String isVillager = VillagerHeads.getNameFromTexture(meta.getOwner().toString());
								String isZombieVillager = ZombieVillagerHeads.getNameFromTexture(meta.getOwner().toString());
								String isplayerhead = isPlayerHead(meta.getOwner().toString());
								String isblockhead = isBlockHead(meta.getOwner().toString());
								if(isCat != null){		daMobName = isCat;	}
								if(isHorse != null){	daMobName = isHorse;	}
								if(isLlama != null){	daMobName = isLlama;	}
								if(isMobHead != null){	daMobName = isMobHead;	}
								if(isRabbit != null){	daMobName = isRabbit;	}
								if(isSheep != null){	daMobName = isSheep;	}
								if(isVillager != null){	daMobName = isVillager;	}
								if(isZombieVillager != null){	daMobName = isZombieVillager;	}
								if(isblockhead != null){	daMobName = isblockhead;	}
								if(isplayerhead != null){	daMobName = isplayerhead;	}
								
								ArrayList<String> lore = new ArrayList();
								//log("" + meta.getOwner().toString());
								//String name = LlamaHeads.getNameFromTexture(meta.getOwner().toString());
								if(debug){logDebug("EPIE mobname from texture=" + daMobName);}
								List<String> skullLore = skullname.getLore();
								if(skullLore != null){
									if(skullLore.toString().contains("Killed by")){
										lore.addAll(skullname.getLore());
									}
								}
								if(skullLore == null||!skullname.getLore().toString().contains(this.getName())){
									if(getConfig().getBoolean("lore.show_plugin_name", true)){
										lore.add(ChatColor.AQUA + "" + this.getName());
									}
								}
								if(daMobName != "null"){
									daMobName = langName.getString(daMobName.toLowerCase().replace(" ", "."), daMobName);
								}else{
									daMobName = langName.getString(daMobName.toLowerCase().replace(" ", "."), "404 Name Not Found");
								}
								meta.setLore(lore);
								meta.setDisplayName(daMobName);
								itmStack.setItemMeta(meta);
								//if(debug){logDebug("test3a");}
							}else{
								if(debug){logDebug("EPIE test3b meta == null");}
						}
					}
				}
			}else{
				if(debug){logDebug("not player head");}
			}
		}
			//log("test4");
	}
	
	public String isPlayerHead(String string){
		int numOfCustomTrades = playerHeads.getInt("players.number") + 1;
		if(debug){logDebug("iPH string=" + string);}
		for(int randomPlayerHead=1; randomPlayerHead<numOfCustomTrades; randomPlayerHead++){
			ItemStack itemstack = playerHeads.getItemStack("players.player_" + randomPlayerHead + ".itemstack", new ItemStack(Material.AIR));
			SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
			if(skullmeta != null){
				//if(debug&&skullmeta != null){logDebug("iPH getOwner_" + randomPlayerHead + "=" + skullmeta.getOwner().toString());}
				if(skullmeta.getOwner().toString().contains(string)){
					return itemstack.getItemMeta().getDisplayName();
				}
			}
		}
		//playerHeads
		return null;
	}
	
	public String isBlockHead(String string){
		int numOfCustomTrades = blockHeads.getInt("blocks.number") + 1;
		if(debug){logDebug("iBH string=" + string);}
		for(int randomBlockHead=1; randomBlockHead<numOfCustomTrades; randomBlockHead++){
			ItemStack itemstack = blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
			SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
			if(skullmeta != null){
				//if(debug&&skullmeta != null){logDebug("iBH getOwner_" + randomBlockHead + "=" + skullmeta.getOwner().toString());}
				if(skullmeta.getOwner().toString().contains(string)){
					return itemstack.getItemMeta().getDisplayName();
				}
			}
		}
		//blockHeads
		return null;
	}

	public static void copyFile_Java7(String origin, String destination) throws IOException {
		Path FROM = Paths.get(origin);
		Path TO = Paths.get(destination);
		//overwrite the destination file if it exists, and copy
		// the file attributes, including the rwx permissions
		CopyOption[] options = new CopyOption[]{
			StandardCopyOption.REPLACE_EXISTING,
			StandardCopyOption.COPY_ATTRIBUTES
		}; 
		Files.copy(FROM, TO, options);
	}
	
	public void copyChance(YmlConfiguration oldconfig, File file){
		YmlConfiguration config = new YmlConfiguration();
		try {
			config.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		config.set("player_chance_percent", oldconfig.get("player_chance_percent", "0.25"));
		config.set("named_mob_chance_percent", oldconfig.get("named_mob_chance_percent", "0.25"));
		config.set("bat_chance_percent", oldconfig.get("bat_chance_percent", "0.25"));
		config.set("bee.angry_pollinated_chance_percent", oldconfig.get("bee.angry_pollinated_chance_percent", "0.25"));
		config.set("bee.angry_chance_percent", oldconfig.get("bee.angry_chance_percent", "0.25"));
		config.set("bee.pollinated_chance_percent", oldconfig.get("bee.pollinated_chance_percent", "0.25"));
		config.set("bee.chance_percent", oldconfig.get("bee.chance_percent", "0.25"));
		config.set("blaze_chance_percent", oldconfig.get("blaze_chance_percent", "0.25"));
		config.set("cat.all_black_chance_percent", oldconfig.get("cat.all_black_chance_percent", "0.25"));
		config.set("cat.black_chance_percent", oldconfig.get("cat.black_chance_percent", "0.25"));
		config.set("cat.british_shorthair_chance_percent", oldconfig.get("cat.british_shorthair_chance_percent", "0.25"));
		config.set("cat.calico_chance_percent", oldconfig.get("cat.calico_chance_percent", "0.25"));
		config.set("cat.jellie_chance_percent", oldconfig.get("cat.jellie_chance_percent", "0.25"));
		config.set("cat.persian_chance_percent", oldconfig.get("cat.persian_chance_percent", "0.25"));
		config.set("cat.ragdoll_chance_percent", oldconfig.get("cat.ragdoll_chance_percent", "0.25"));
		config.set("cat.red_chance_percent", oldconfig.get("cat.red_chance_percent", "0.25"));
		config.set("cat.siamese_chance_percent", oldconfig.get("cat.siamese_chance_percent", "0.25"));
		config.set("cat.tabby_chance_percent", oldconfig.get("cat.tabby_chance_percent", "0.25"));
		config.set("cat.white_chance_percent", oldconfig.get("cat.white_chance_percent", "0.25"));
		config.set("cat.wild_ocelot_chance_percent", oldconfig.get("cat.wild_ocelot_chance_percent", "0.25"));
		config.set("cave_spider_chance_percent", oldconfig.get("cave_spider_chance_percent", "0.25"));
		config.set("chicken_chance_percent", oldconfig.get("chicken_chance_percent", "0.25"));
		config.set("cod_chance_percent", oldconfig.get("cod_chance_percent", "0.25"));
		config.set("cow_chance_percent", oldconfig.get("cow_chance_percent", "0.25"));
		config.set("creeper_chance_percent", oldconfig.get("creeper_chance_percent", "0.25"));
		config.set("dolphin_chance_percent", oldconfig.get("dolphin_chance_percent", "0.25"));
		config.set("donkey_chance_percent", oldconfig.get("donkey_chance_percent", "0.25"));
		config.set("drowned_chance_percent", oldconfig.get("drowned_chance_percent", "0.25"));
		config.set("elder_guardian_chance_percent", oldconfig.get("elder_guardian_chance_percent", "0.25"));
		config.set("ender_dragon_chance_percent", oldconfig.get("ender_dragon_chance_percent", "0.25"));
		config.set("enderman_chance_percent", oldconfig.get("enderman_chance_percent", "0.25"));
		config.set("endermite_chance_percent", oldconfig.get("endermite_chance_percent", "0.25"));
		config.set("evoker_chance_percent", oldconfig.get("evoker_chance_percent", "0.25"));
		config.set("fox.red_chance_percent", oldconfig.get("fox.red_chance_percent", "0.25"));
		config.set("fox.snow_chance_percent", oldconfig.get("fox.snow_chance_percent", "0.25"));
		config.set("ghast_chance_percent", oldconfig.get("ghast_chance_percent", "0.25"));
		config.set("giant_chance_percent", oldconfig.get("giant_chance_percent", "0.25"));
		config.set("guardian_chance_percent", oldconfig.get("guardian_chance_percent", "0.25"));
		config.set("hoglin_chance_percent", oldconfig.get("hoglin_chance_percent", "0.25"));
		config.set("horse.black_chance_percent", oldconfig.get("horse.black_chance_percent", "0.25"));
		config.set("horse.brown_chance_percent", oldconfig.get("horse.brown_chance_percent", "0.25"));
		config.set("horse.chestnut_chance_percent", oldconfig.get("horse.chestnut_chance_percent", "0.25"));
		config.set("horse.creamy_chance_percent", oldconfig.get("horse.creamy_chance_percent", "0.25"));
		config.set("horse.dark_brown_chance_percent", oldconfig.get("horse.dark_brown_chance_percent", "0.25"));
		config.set("horse.gray_chance_percent", oldconfig.get("horse.gray_chance_percent", "0.25"));
		config.set("horse.white_chance_percent", oldconfig.get("horse.white_chance_percent", "0.25"));
		config.set("husk_chance_percent", oldconfig.get("husk_chance_percent", "0.25"));
		config.set("illusioner_chance_percent", oldconfig.get("illusioner_chance_percent", "0.25"));
		config.set("iron_golem_chance_percent", oldconfig.get("iron_golem_chance_percent", "0.25"));
		config.set("llama.brown_chance_percent", oldconfig.get("llama.brown_chance_percent", "0.25"));
		config.set("llama.creamy_chance_percent", oldconfig.get("llama.creamy_chance_percent", "0.25"));
		config.set("llama.gray_chance_percent", oldconfig.get("llama.gray_chance_percent", "0.25"));
		config.set("llama.white_chance_percent", oldconfig.get("llama.white_chance_percent", "0.25"));
		config.set("magma_cube_chance_percent", oldconfig.get("magma_cube_chance_percent", "0.25"));
		config.set("mule_chance_percent", oldconfig.get("mule_chance_percent", "0.25"));
		config.set("mushroom_cow.red_chance_percent", oldconfig.get("mushroom_cow.red_chance_percent", "0.25"));
		config.set("mushroom_cow.brown_chance_percent", oldconfig.get("mushroom_cow.brown_chance_percent", "0.25"));
		config.set("panda.aggressive_chance_percent", oldconfig.get("panda.aggressive_chance_percent", "0.25"));
		config.set("panda.brown_chance_percent", oldconfig.get("panda.brown_chance_percent", "0.25"));
		config.set("panda.lazy_chance_percent", oldconfig.get("panda.lazy_chance_percent", "0.25"));
		config.set("panda.normal_chance_percent", oldconfig.get("panda.normal_chance_percent", "0.25"));
		config.set("panda.playful_chance_percent", oldconfig.get("panda.playful_chance_percent", "0.25"));
		config.set("panda.weak_chance_percent", oldconfig.get("panda.weak_chance_percent", "0.25"));
		config.set("panda.worried_chance_percent", oldconfig.get("panda.worried_chance_percent", "0.25"));
		config.set("parrot.blue_chance_percent", oldconfig.get("parrot.blue_chance_percent", "0.25"));
		config.set("parrot.cyan_chance_percent", oldconfig.get("parrot.cyan_chance_percent", "0.25"));
		config.set("parrot.gray_chance_percent", oldconfig.get("parrot.gray_chance_percent", "0.25"));
		config.set("parrot.green_chance_percent", oldconfig.get("parrot.green_chance_percent", "0.25"));
		config.set("parrot.red_chance_percent", oldconfig.get("parrot.red_chance_percent", "0.25"));
		config.set("phantom_chance_percent", oldconfig.get("phantom_chance_percent", "0.25"));
		config.set("pig_chance_percent", oldconfig.get("pig_chance_percent", "0.25"));
		config.set("piglin_chance_percent", oldconfig.get("piglin_chance_percent", "0.25"));
		config.set("pig_zombie_chance_percent", oldconfig.get("pig_zombie_chance_percent", "0.25"));
		config.set("pillager_chance_percent", oldconfig.get("pillager_chance_percent", "0.25"));
		config.set("polar_bear_chance_percent", oldconfig.get("polar_bear_chance_percent", "0.25"));
		config.set("pufferfish_chance_percent", oldconfig.get("pufferfish_chance_percent", "0.25"));
		config.set("rabbit.black_chance_percent", oldconfig.get("rabbit.black_chance_percent", "0.25"));
		config.set("rabbit.black_and_white_chance_percent", oldconfig.get("rabbit.black_and_white_chance_percent", "0.25"));
		config.set("rabbit.brown_chance_percent", oldconfig.get("rabbit.brown_chance_percent", "0.25"));
		config.set("rabbit.gold_chance_percent", oldconfig.get("rabbit.gold_chance_percent", "0.25"));
		config.set("rabbit.salt_and_pepper_chance_percent", oldconfig.get("rabbit.salt_and_pepper_chance_percent", "0.25"));
		config.set("rabbit.the_killer_bunny_chance_percent", oldconfig.get("rabbit.the_killer_bunny_chance_percent", "0.25"));
		config.set("rabbit.toast_chance_percent", oldconfig.get("rabbit.toast_chance_percent", "0.25"));
		config.set("rabbit.white_chance_percent", oldconfig.get("rabbit.white_chance_percent", "0.25"));
		config.set("ravager_chance_percent", oldconfig.get("ravager_chance_percent", "0.25"));
		config.set("salmon_chance_percent", oldconfig.get("salmon_chance_percent", "0.25"));
		config.set("sheep.black_chance_percent", oldconfig.get("sheep.black_chance_percent", "0.25"));
		config.set("sheep.blue_chance_percent", oldconfig.get("sheep.blue_chance_percent", "0.25"));
		config.set("sheep.brown_chance_percent", oldconfig.get("sheep.brown_chance_percent", "0.25"));
		config.set("sheep.cyan_chance_percent", oldconfig.get("sheep.cyan_chance_percent", "0.25"));
		config.set("sheep.gray_chance_percent", oldconfig.get("sheep.gray_chance_percent", "0.25"));
		config.set("sheep.green_chance_percent", oldconfig.get("sheep.green_chance_percent", "0.25"));
		config.set("sheep.jeb__chance_percent", oldconfig.get("sheep.jeb__chance_percent", "0.25"));
		config.set("sheep.light_blue_chance_percent", oldconfig.get("sheep.light_blue_chance_percent", "0.25"));
		config.set("sheep.light_gray_chance_percent", oldconfig.get("sheep.light_gray_chance_percent", "0.25"));
		config.set("sheep.lime_chance_percent", oldconfig.get("sheep.lime_chance_percent", "0.25"));
		config.set("sheep.magenta_chance_percent", oldconfig.get("sheep.magenta_chance_percent", "0.25"));
		config.set("sheep.orange_chance_percent", oldconfig.get("sheep.orange_chance_percent", "0.25"));
		config.set("sheep.pink_chance_percent", oldconfig.get("sheep.pink_chance_percent", "0.25"));
		config.set("sheep.purple_chance_percent", oldconfig.get("sheep.purple_chance_percent", "0.25"));
		config.set("sheep.red_chance_percent", oldconfig.get("sheep.red_chance_percent", "0.25"));
		config.set("sheep.white_chance_percent", oldconfig.get("sheep.white_chance_percent", "0.25"));
		config.set("sheep.yellow_chance_percent", oldconfig.get("sheep.yellow_chance_percent", "0.25"));
		config.set("shulker_chance_percent", oldconfig.get("shulker_chance_percent", "0.25"));
		config.set("silverfish_chance_percent", oldconfig.get("silverfish_chance_percent", "0.25"));
		config.set("skeleton_chance_percent", oldconfig.get("skeleton_chance_percent", "0.25"));
		config.set("skeleton_horse_chance_percent", oldconfig.get("skeleton_horse_chance_percent", "0.25"));
		config.set("slime_chance_percent", oldconfig.get("slime_chance_percent", "0.25"));
		config.set("snowman_chance_percent", oldconfig.get("snowman_chance_percent", "0.25"));
		config.set("spider_chance_percent", oldconfig.get("spider_chance_percent", "0.25"));
		config.set("squid_chance_percent", oldconfig.get("squid_chance_percent", "0.25"));
		config.set("stray_chance_percent", oldconfig.get("stray_chance_percent", "0.25"));
		config.set("strider_chance_percent", oldconfig.get("strider_chance_percent", "0.25"));
		config.set("trader_llama.brown_chance_percent", oldconfig.get("trader_llama.brown_chance_percent", "0.25"));
		config.set("trader_llama.creamy_chance_percent", oldconfig.get("trader_llama.creamy_chance_percent", "0.25"));
		config.set("trader_llama.gray_chance_percent", oldconfig.get("trader_llama.gray_chance_percent", "0.25"));
		config.set("trader_llama.white_chance_percent", oldconfig.get("trader_llama.white_chance_percent", "0.25"));
		config.set("tropical_fish_chance_percent", oldconfig.get("tropical_fish_chance_percent", "0.25"));
		config.set("turtle_chance_percent", oldconfig.get("turtle_chance_percent", "0.25"));
		config.set("vex_chance_percent", oldconfig.get("vex_chance_percent", "0.25"));
		config.set("villager.desert.armorer_chance_percent", oldconfig.get("villager.desert.armorer_chance_percent", "0.25"));
		config.set("villager.desert.butcher_chance_percent", oldconfig.get("villager.desert.butcher_chance_percent", "0.25"));
		config.set("villager.desert.cartographer_chance_percent", oldconfig.get("villager.desert.cartographer_chance_percent", "0.25"));
		config.set("villager.desert.cleric_chance_percent", oldconfig.get("villager.desert.cleric_chance_percent", "0.25"));
		config.set("villager.desert.farmer_chance_percent", oldconfig.get("villager.desert.farmer_chance_percent", "0.25"));
		config.set("villager.desert.fisherman_chance_percent", oldconfig.get("villager.desert.fisherman_chance_percent", "0.25"));
		config.set("villager.desert.fletcher_chance_percent", oldconfig.get("villager.desert.fletcher_chance_percent", "0.25"));
		config.set("villager.desert.leatherworker_chance_percent", oldconfig.get("villager.desert.leatherworker_chance_percent", "0.25"));
		config.set("villager.desert.librarian_chance_percent", oldconfig.get("villager.desert.librarian_chance_percent", "0.25"));
		config.set("villager.desert.mason_chance_percent", oldconfig.get("villager.desert.mason_chance_percent", "0.25"));
		config.set("villager.desert.nitwit_chance_percent", oldconfig.get("villager.desert.nitwit_chance_percent", "0.25"));
		config.set("villager.desert.none_chance_percent", oldconfig.get("villager.desert.none_chance_percent", "0.25"));
		config.set("villager.desert.shepherd_chance_percent", oldconfig.get("villager.desert.shepherd_chance_percent", "0.25"));
		config.set("villager.desert.toolsmith_chance_percent", oldconfig.get("villager.desert.toolsmith_chance_percent", "0.25"));
		config.set("villager.desert.weaponsmith_chance_percent", oldconfig.get("villager.desert.weaponsmith_chance_percent", "0.25"));
		config.set("villager.jungle.armorer_chance_percent", oldconfig.get("villager.jungle.armorer_chance_percent", "0.25"));
		config.set("villager.jungle.butcher_chance_percent", oldconfig.get("villager.jungle.butcher_chance_percent", "0.25"));
		config.set("villager.jungle.cartographer_chance_percent", oldconfig.get("villager.jungle.cartographer_chance_percent", "0.25"));
		config.set("villager.jungle.cleric_chance_percent", oldconfig.get("villager.jungle.cleric_chance_percent", "0.25"));
		config.set("villager.jungle.farmer_chance_percent", oldconfig.get("villager.jungle.farmer_chance_percent", "0.25"));
		config.set("villager.jungle.fisherman_chance_percent", oldconfig.get("villager.jungle.fisherman_chance_percent", "0.25"));
		config.set("villager.jungle.fletcher_chance_percent", oldconfig.get("villager.jungle.fletcher_chance_percent", "0.25"));
		config.set("villager.jungle.leatherworker_chance_percent", oldconfig.get("villager.jungle.leatherworker_chance_percent", "0.25"));
		config.set("villager.jungle.librarian_chance_percent", oldconfig.get("villager.jungle.librarian_chance_percent", "0.25"));
		config.set("villager.jungle.mason_chance_percent", oldconfig.get("villager.jungle.mason_chance_percent", "0.25"));
		config.set("villager.jungle.nitwit_chance_percent", oldconfig.get("villager.jungle.nitwit_chance_percent", "0.25"));
		config.set("villager.jungle.none_chance_percent", oldconfig.get("villager.jungle.none_chance_percent", "0.25"));
		config.set("villager.jungle.shepherd_chance_percent", oldconfig.get("villager.jungle.shepherd_chance_percent", "0.25"));
		config.set("villager.jungle.toolsmith_chance_percent", oldconfig.get("villager.jungle.toolsmith_chance_percent", "0.25"));
		config.set("villager.jungle.weaponsmith_chance_percent", oldconfig.get("villager.jungle.weaponsmith_chance_percent", "0.25"));
		config.set("villager.plains.armorer_chance_percent", oldconfig.get("villager.plains.armorer_chance_percent", "0.25"));
		config.set("villager.plains.butcher_chance_percent", oldconfig.get("villager.plains.butcher_chance_percent", "0.25"));
		config.set("villager.plains.cartographer_chance_percent", oldconfig.get("villager.plains.cartographer_chance_percent", "0.25"));
		config.set("villager.plains.cleric_chance_percent", oldconfig.get("villager.plains.cleric_chance_percent", "0.25"));
		config.set("villager.plains.farmer_chance_percent", oldconfig.get("villager.plains.farmer_chance_percent", "0.25"));
		config.set("villager.plains.fisherman_chance_percent", oldconfig.get("villager.plains.fisherman_chance_percent", "0.25"));
		config.set("villager.plains.fletcher_chance_percent", oldconfig.get("villager.plains.fletcher_chance_percent", "0.25"));
		config.set("villager.plains.leatherworker_chance_percent", oldconfig.get("villager.plains.leatherworker_chance_percent", "0.25"));
		config.set("villager.plains.librarian_chance_percent", oldconfig.get("villager.plains.librarian_chance_percent", "0.25"));
		config.set("villager.plains.mason_chance_percent", oldconfig.get("villager.plains.mason_chance_percent", "0.25"));
		config.set("villager.plains.nitwit_chance_percent", oldconfig.get("villager.plains.nitwit_chance_percent", "0.25"));
		config.set("villager.plains.none_chance_percent", oldconfig.get("villager.plains.none_chance_percent", "0.25"));
		config.set("villager.plains.shepherd_chance_percent", oldconfig.get("villager.plains.shepherd_chance_percent", "0.25"));
		config.set("villager.plains.toolsmith_chance_percent", oldconfig.get("villager.plains.toolsmith_chance_percent", "0.25"));
		config.set("villager.plains.weaponsmith_chance_percent", oldconfig.get("villager.plains.weaponsmith_chance_percent", "0.25"));
		config.set("villager.savanna.armorer_chance_percent", oldconfig.get("villager.savanna.armorer_chance_percent", "0.25"));
		config.set("villager.savanna.butcher_chance_percent", oldconfig.get("villager.savanna.butcher_chance_percent", "0.25"));
		config.set("villager.savanna.cartographer_chance_percent", oldconfig.get("villager.savanna.cartographer_chance_percent", "0.25"));
		config.set("villager.savanna.cleric_chance_percent", oldconfig.get("villager.savanna.cleric_chance_percent", "0.25"));
		config.set("villager.savanna.farmer_chance_percent", oldconfig.get("villager.savanna.farmer_chance_percent", "0.25"));
		config.set("villager.savanna.fisherman_chance_percent", oldconfig.get("villager.savanna.fisherman_chance_percent", "0.25"));
		config.set("villager.savanna.fletcher_chance_percent", oldconfig.get("villager.savanna.fletcher_chance_percent", "0.25"));
		config.set("villager.savanna.leatherworker_chance_percent", oldconfig.get("villager.savanna.leatherworker_chance_percent", "0.25"));
		config.set("villager.savanna.librarian_chance_percent", oldconfig.get("villager.savanna.librarian_chance_percent", "0.25"));
		config.set("villager.savanna.mason_chance_percent", oldconfig.get("villager.savanna.mason_chance_percent", "0.25"));
		config.set("villager.savanna.nitwit_chance_percent", oldconfig.get("villager.savanna.nitwit_chance_percent", "0.25"));
		config.set("villager.savanna.none_chance_percent", oldconfig.get("villager.savanna.none_chance_percent", "0.25"));
		config.set("villager.savanna.shepherd_chance_percent", oldconfig.get("villager.savanna.shepherd_chance_percent", "0.25"));
		config.set("villager.savanna.toolsmith_chance_percent", oldconfig.get("villager.savanna.toolsmith_chance_percent", "0.25"));
		config.set("villager.savanna.weaponsmith_chance_percent", oldconfig.get("villager.savanna.weaponsmith_chance_percent", "0.25"));
		config.set("villager.snow.armorer_chance_percent", oldconfig.get("villager.snow.armorer_chance_percent", "0.25"));
		config.set("villager.snow.butcher_chance_percent", oldconfig.get("villager.snow.butcher_chance_percent", "0.25"));
		config.set("villager.snow.cartographer_chance_percent", oldconfig.get("villager.snow.cartographer_chance_percent", "0.25"));
		config.set("villager.snow.cleric_chance_percent", oldconfig.get("villager.snow.cleric_chance_percent", "0.25"));
		config.set("villager.snow.farmer_chance_percent", oldconfig.get("villager.snow.farmer_chance_percent", "0.25"));
		config.set("villager.snow.fisherman_chance_percent", oldconfig.get("villager.snow.fisherman_chance_percent", "0.25"));
		config.set("villager.snow.fletcher_chance_percent", oldconfig.get("villager.snow.fletcher_chance_percent", "0.25"));
		config.set("villager.snow.leatherworker_chance_percent", oldconfig.get("villager.snow.leatherworker_chance_percent", "0.25"));
		config.set("villager.snow.librarian_chance_percent", oldconfig.get("villager.snow.librarian_chance_percent", "0.25"));
		config.set("villager.snow.mason_chance_percent", oldconfig.get("villager.snow.mason_chance_percent", "0.25"));
		config.set("villager.snow.nitwit_chance_percent", oldconfig.get("villager.snow.nitwit_chance_percent", "0.25"));
		config.set("villager.snow.none_chance_percent", oldconfig.get("villager.snow.none_chance_percent", "0.25"));
		config.set("villager.snow.shepherd_chance_percent", oldconfig.get("villager.snow.shepherd_chance_percent", "0.25"));
		config.set("villager.snow.toolsmith_chance_percent", oldconfig.get("villager.snow.toolsmith_chance_percent", "0.25"));
		config.set("villager.snow.weaponsmith_chance_percent", oldconfig.get("villager.snow.weaponsmith_chance_percent", "0.25"));
		config.set("villager.swamp.armorer_chance_percent", oldconfig.get("villager.swamp.armorer_chance_percent", "0.25"));
		config.set("villager.swamp.butcher_chance_percent", oldconfig.get("villager.swamp.butcher_chance_percent", "0.25"));
		config.set("villager.swamp.cartographer_chance_percent", oldconfig.get("villager.swamp.cartographer_chance_percent", "0.25"));
		config.set("villager.swamp.cleric_chance_percent", oldconfig.get("villager.swamp.cleric_chance_percent", "0.25"));
		config.set("villager.swamp.farmer_chance_percent", oldconfig.get("villager.swamp.farmer_chance_percent", "0.25"));
		config.set("villager.swamp.fisherman_chance_percent", oldconfig.get("villager.swamp.fisherman_chance_percent", "0.25"));
		config.set("villager.swamp.fletcher_chance_percent", oldconfig.get("villager.swamp.fletcher_chance_percent", "0.25"));
		config.set("villager.swamp.leatherworker_chance_percent", oldconfig.get("villager.swamp.leatherworker_chance_percent", "0.25"));
		config.set("villager.swamp.librarian_chance_percent", oldconfig.get("villager.swamp.librarian_chance_percent", "0.25"));
		config.set("villager.swamp.mason_chance_percent", oldconfig.get("villager.swamp.mason_chance_percent", "0.25"));
		config.set("villager.swamp.nitwit_chance_percent", oldconfig.get("villager.swamp.nitwit_chance_percent", "0.25"));
		config.set("villager.swamp.none_chance_percent", oldconfig.get("villager.swamp.none_chance_percent", "0.25"));
		config.set("villager.swamp.shepherd_chance_percent", oldconfig.get("villager.swamp.shepherd_chance_percent", "0.25"));
		config.set("villager.swamp.toolsmith_chance_percent", oldconfig.get("villager.swamp.toolsmith_chance_percent", "0.25"));
		config.set("villager.swamp.weaponsmith_chance_percent", oldconfig.get("villager.swamp.weaponsmith_chance_percent", "0.25"));
		config.set("villager.taiga.armorer_chance_percent", oldconfig.get("villager.taiga.armorer_chance_percent", "0.25"));
		config.set("villager.taiga.butcher_chance_percent", oldconfig.get("villager.taiga.butcher_chance_percent", "0.25"));
		config.set("villager.taiga.cartographer_chance_percent", oldconfig.get("villager.taiga.cartographer_chance_percent", "0.25"));
		config.set("villager.taiga.cleric_chance_percent", oldconfig.get("villager.taiga.cleric_chance_percent", "0.25"));
		config.set("villager.taiga.farmer_chance_percent", oldconfig.get("villager.taiga.farmer_chance_percent", "0.25"));
		config.set("villager.taiga.fisherman_chance_percent", oldconfig.get("villager.taiga.fisherman_chance_percent", "0.25"));
		config.set("villager.taiga.fletcher_chance_percent", oldconfig.get("villager.taiga.fletcher_chance_percent", "0.25"));
		config.set("villager.taiga.leatherworker_chance_percent", oldconfig.get("villager.taiga.leatherworker_chance_percent", "0.25"));
		config.set("villager.taiga.librarian_chance_percent", oldconfig.get("villager.taiga.librarian_chance_percent", "0.25"));
		config.set("villager.taiga.mason_chance_percent", oldconfig.get("villager.taiga.mason_chance_percent", "0.25"));
		config.set("villager.taiga.nitwit_chance_percent", oldconfig.get("villager.taiga.nitwit_chance_percent", "0.25"));
		config.set("villager.taiga.none_chance_percent", oldconfig.get("villager.taiga.none_chance_percent", "0.25"));
		config.set("villager.taiga.shepherd_chance_percent", oldconfig.get("villager.taiga.shepherd_chance_percent", "0.25"));
		config.set("villager.taiga.toolsmith_chance_percent", oldconfig.get("villager.taiga.toolsmith_chance_percent", "0.25"));
		config.set("villager.taiga.weaponsmith_chance_percent", oldconfig.get("villager.taiga.weaponsmith_chance_percent", "0.25"));
		config.set("vindicator_chance_percent", oldconfig.get("vindicator_chance_percent", "0.25"));
		config.set("wandering_trader_chance_percent", oldconfig.get("wandering_trader_chance_percent", "0.25"));
		config.set("witch_chance_percent", oldconfig.get("witch_chance_percent", "0.25"));
		config.set("wither_chance_percent", oldconfig.get("wither_chance_percent", "0.25"));
		config.set("wither_skeleton_chance_percent", oldconfig.get("wither_skeleton_chance_percent", "0.25"));
		config.set("wolf_chance_percent", oldconfig.get("wolf_chance_percent", "0.25"));
		config.set("zoglin_chance_percent", oldconfig.get("zoglin_chance_percent", "0.25"));
		config.set("zombie_chance_percent", oldconfig.get("zombie_chance_percent", "0.25"));
		config.set("zombie_horse_chance_percent", oldconfig.get("zombie_horse_chance_percent", "0.25"));
		config.set("zombie_pigman_chance_percent", oldconfig.get("zombie_pigman_chance_percent", "0.25"));
		config.set("zombified_piglin_chance_percent", oldconfig.get("zombified_piglin_chance_percent", "0.25"));
		config.set("zombie_villager_chance_percent", oldconfig.get("zombie_villager_chance_percent", "0.25"));
		try {
			config.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
