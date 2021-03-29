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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
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
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.joelgodofwar.mmh.util.Ansi;
import com.github.joelgodofwar.mmh.util.CatHeads;
import com.github.joelgodofwar.mmh.util.HorseHeads;
import com.github.joelgodofwar.mmh.util.LlamaHeads;
import com.github.joelgodofwar.mmh.util.Metrics;
import com.github.joelgodofwar.mmh.util.MobHeads;
import com.github.joelgodofwar.mmh.util.RabbitHeads;
import com.github.joelgodofwar.mmh.util.SheepHeads;
import com.github.joelgodofwar.mmh.util.StrUtils;
import com.github.joelgodofwar.mmh.util.UpdateChecker;
import com.github.joelgodofwar.mmh.util.VillagerHeads;
import com.github.joelgodofwar.mmh.util.YmlConfiguration;
import com.github.joelgodofwar.mmh.util.ZombieVillagerHeads;
import com.github.joelgodofwar.mmh.util.datatypes.JsonDataType;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTList;
import de.tr7zw.changeme.nbtapi.NBTListCompound;
import io.papermc.lib.PaperLib;
import io.papermc.lib.features.blockstatesnapshot.BlockStateSnapshotResult;


@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
public class MoreMobHeads extends JavaPlugin implements Listener{
	
	public final static Logger logger = Logger.getLogger("Minecraft");
	/** update checker variables */
	public String updateurl = "https://github.com/JoelGodOfwar/MoreMobHeads/raw/master/versioncheck/{vers}/version.txt";
	public String newVerMsg;
	public int updateVersion = 73997; // https://spigotmc.org/resources/73997
	boolean UpdateAvailable =  false;
	public String UColdVers;
	public String UCnewVers;
	public static boolean UpdateCheck;
	public String thisName = this.getName();
	public String thisVersion = this.getDescription().getVersion();
	/** end update checker variables */
	public static boolean debug = false;
	public static String daLang;
	//String updateURL = "https://github.com/JoelGodOfwar/MoreMobHeads/raw/master/versioncheck/1.14/version.txt";
	File langFile;
	FileConfiguration lang;
	File langNameFile;
	FileConfiguration langName;
	File playerFile;
	FileConfiguration playerHeads;
	File blockFile;
	File blockFile116;
	File blockFile1162;
	FileConfiguration blockHeads  = new YamlConfiguration();
	FileConfiguration blockHeads2  = new YamlConfiguration();
	File customFile;
	FileConfiguration traderCustom;
	File chanceFile;
	YmlConfiguration chanceConfig;
	File mobnameFile;
	FileConfiguration mobname;
	double defpercent = 0.013;
	//static boolean showkiller;
	//static boolean showpluginname;
	YmlConfiguration config = new YmlConfiguration();
	YamlConfiguration oldconfig = new YamlConfiguration();
	static PluginDescriptionFile pdfFile;
	static String datafolder;
	String world_whitelist;
	String world_blacklist;
	String mob_whitelist;
	String mob_blacklist;
	boolean colorful_console;
	private final NamespacedKey NAMETAG_KEY = new NamespacedKey(this, "name_tag");
	
	@Override // TODO: onEnable
	public void onEnable(){
		UpdateCheck = getConfig().getBoolean("auto_update_check");
		//showkiller = getConfig().getBoolean("lore.show_killer", true);
		//showpluginname = getConfig().getBoolean("lore.show_plugin_name", true);
		debug = getConfig().getBoolean("debug", false);
		daLang = getConfig().getString("lang", "en_US");
		oldconfig = new YamlConfiguration();
		pdfFile = this.getDescription();
		datafolder = this.getDataFolder().toString();
		colorful_console = getConfig().getBoolean("colorful_console", true);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(Ansi.GREEN + "**************************************" + Ansi.RESET);
		logger.info(Ansi.YELLOW + pdfFile.getName() + " v" + pdfFile.getVersion() + Ansi.RESET + " Loading...");
				
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
			stacktraceInfo();
			e.printStackTrace();
		}
		String checklangversion = lang.getString("langversion", "1.0.0");
		if(checklangversion != null){
			if(!checklangversion.equalsIgnoreCase("1.0.14")){
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
		
		/** update checker variable */
		newVerMsg = Ansi.YELLOW + this.getName() + Ansi.MAGENTA + " v{oVer}" + Ansi.RESET + " " + lang.get("newvers") + Ansi.GREEN + " v{nVer}" + Ansi.RESET;
		/** end update checker variable */
		
		/** Version Check */
		if(!getMCVersion().startsWith("1.14")&&!getMCVersion().startsWith("1.15")&&!getMCVersion().startsWith("1.16")
				&&!getMCVersion().startsWith("1.17")){
			logger.info(Ansi.RED + "WARNING! *!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!" + Ansi.RESET);
			logger.info(Ansi.RED + "WARNING! " + lang.get("server_not_version") + Ansi.RESET);
			logger.info(Ansi.RED + "WARNING! " + this.getName() + " v" + this.getDescription().getVersion() + " disabling." + Ansi.RESET);
			logger.info(Ansi.RED + "WARNING! *!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!" + Ansi.RESET);
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
			stacktraceInfo();
			e.printStackTrace();
		}
	log("Loading config file...");
		try {
			oldconfig.load(new File(getDataFolder() + "" + File.separatorChar + "config.yml"));
		} catch (Exception e2) {
			logWarn("Could not load config.yml");
			stacktraceInfo();
			e2.printStackTrace();
		}
		String checkconfigversion = oldconfig.getString("version", "1.0.0");
		if(checkconfigversion != null){
			if(!checkconfigversion.equalsIgnoreCase("1.0.17")){
				boolean isOldFormat;
				try {
					copyFile_Java7(getDataFolder() + "" + File.separatorChar + "config.yml",getDataFolder() + "" + File.separatorChar + "old_config.yml");
				} catch (IOException e) {
					stacktraceInfo();
					e.printStackTrace();
				}
				if(config.get("custom_wandering_trader", null) != null){
					isOldFormat = true;
				}else{
					isOldFormat = false;
				}
				saveResource("config.yml", true);
				
				try {
					config.load(new File(getDataFolder(), "config.yml"));
				} catch (IOException | InvalidConfigurationException e1) {
					logWarn("Could not load config.yml");
					stacktraceInfo();
					e1.printStackTrace();
				}
				try {
					oldconfig.load(new File(getDataFolder(), "old_config.yml"));
				} catch (IOException | InvalidConfigurationException e1) {
					stacktraceInfo();
					e1.printStackTrace();
				}
				if(isOldFormat){
					config.set("auto_update_check", oldconfig.get("auto_update_check", true));
					config.set("debug", oldconfig.get("debug", false));
					config.set("lang", oldconfig.get("lang", "en_US"));
					config.set("colorful_console", oldconfig.get("colorful_console", true));//
					config.set("vanilla_heads.creepers", oldconfig.get("creeper_vanilla_heads", false));
					config.set("vanilla_heads.ender_dragon", oldconfig.get("ender_dragon_vanilla_heads", false));
					config.set("vanilla_heads.skeleton", oldconfig.get("skeleton_vanilla_heads", false));
					config.set("vanilla_heads.wither_skeleton", oldconfig.get("wither_skeleton_vanilla_heads", false));
					config.set("vanilla_heads.zombie", oldconfig.get("zombie_vanilla_heads", false));
					config.set("world.whitelist", oldconfig.get("world.whitelist", ""));
					config.set("world.blacklist", oldconfig.get("world.blacklist", ""));
					config.set("mob.whitelist", oldconfig.get("mob.whitelist", ""));
					config.set("mob.blacklist", oldconfig.get("mob.blacklist", ""));
					config.set("mob.nametag", oldconfig.get("mob.nametag", false));
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
				}else{
					config.set("auto_update_check", oldconfig.get("auto_update_check", true));
					config.set("debug", oldconfig.get("debug", false));
					config.set("lang", oldconfig.get("lang", "en_US"));
					config.set("colorful_console", oldconfig.get("colorful_console", true));
					config.set("vanilla_heads.creepers", oldconfig.get("vanilla_heads.creepers", false));
					config.set("vanilla_heads.ender_dragon", oldconfig.get("vanilla_heads.ender_dragon", false));
					config.set("vanilla_heads.skeleton", oldconfig.get("vanilla_heads.skeleton", false));
					config.set("vanilla_heads.wither_skeleton", oldconfig.get("vanilla_heads.wither_skeleton", false));
					config.set("vanilla_heads.zombie", oldconfig.get("vanilla_heads.zombie", false));
					config.set("world.whitelist", oldconfig.get("world.whitelist", ""));
					config.set("world.blacklist", oldconfig.get("world.blacklist", ""));
					config.set("mob.whitelist", oldconfig.get("mob.whitelist", ""));
					config.set("mob.blacklist", oldconfig.get("mob.blacklist", ""));
					config.set("mob.nametag", oldconfig.get("mob.nametag", false));
					config.set("lore.show_killer", oldconfig.get("lore.show_killer", true));
					config.set("lore.show_plugin_name", oldconfig.get("lore.show_plugin_name", true));
					config.set("wandering_trades.custom_wandering_trader", oldconfig.get("wandering_trades.custom_wandering_trader", true));
					config.set("wandering_trades.player_heads.enabled", oldconfig.get("wandering_trades.player_heads.enabled", true));
					config.set("wandering_trades.player_heads.min", oldconfig.get("wandering_trades.player_heads.min", 0));
					config.set("wandering_trades.player_heads.max", oldconfig.get("wandering_trades.player_heads.max", 3));
					config.set("wandering_trades.block_heads.enabled", oldconfig.get("wandering_trades.block_heads.enabled", true));
					config.set("wandering_trades.block_heads.min", oldconfig.get("wandering_trades.block_heads.min", 0));
					config.set("wandering_trades.block_heads.max", oldconfig.get("wandering_trades.block_heads.max", 3));
					config.set("wandering_trades.custom_trades.enabled", oldconfig.get("wandering_trades.custom_trades.enabled", false));
					config.set("wandering_trades.custom_trades.min", oldconfig.get("wandering_trades.custom_trades.min", 0));
					config.set("wandering_trades.custom_trades.max", oldconfig.get("wandering_trades.custom_trades.max", 3));
					config.set("apply_looting", oldconfig.get("apply_looting", true));
					config.set("whitelist.enforce", oldconfig.get("whitelist.enforce", true));
					config.set("whitelist.player_head_whitelist", oldconfig.get("whitelist.player_head_whitelist", "names_go_here"));
					config.set("blacklist.enforce", oldconfig.get("enforce_blacklist", true));
					config.set("blacklist.player_head_blacklist", oldconfig.get("blacklist.player_head_blacklist", "names_go_here"));
				}
				//config.set("", oldconfig.get("", true));
				
				try {
					config.save(new File(getDataFolder(), "config.yml"));
				} catch (IOException e) {
					logWarn("Could not save old settings to config.yml");
					stacktraceInfo();
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
					stacktraceInfo();
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
				stacktraceInfo();
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
			blockFile116 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16.yml");
			blockFile1162 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16_2.yml");
			if(getMCVersion().startsWith("1.16")||getMCVersion().startsWith("1.17")){
				if(debug){logDebug("block_heads_1_16=" + blockFile116.getPath());}
				if(debug){logDebug("block_heads_1_16_2=" + blockFile1162.getPath());}
				if(!blockFile116.exists()){
					saveResource("block_heads_1_16.yml", true);
					log("block_heads_1_16.yml not found! copied block_heads_1_16.yml to " + getDataFolder() + "");
				}
				if(!blockFile1162.exists()){
					saveResource("block_heads_1_16_2.yml", true);
					log("block_heads_1_16_2.yml not found! copied block_heads_1_16_2.yml to " + getDataFolder() + "");
				}
				blockFile = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16.yml");
				log("Loading block_heads_1_16 files...");
			
			}else{
				log("Loading block_heads file...");
			}
		
			blockHeads = new YamlConfiguration();
			try {
				blockHeads.load(blockFile);
			} catch (IOException | InvalidConfigurationException e1) {
				stacktraceInfo();
				e1.printStackTrace();
			}
			if(blockHeads.get("blocks.block_79.price_2.amount", null) != null){
				log("block_heads files outdated, updating...");
				blockHeads.set("blocks.block_79.price_2.amount", "");
				try {
					blockHeads.save(blockFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			blockHeads2 = new YamlConfiguration();
			try {
				blockHeads2.load(blockFile1162);
			} catch (IOException | InvalidConfigurationException e1) {
				stacktraceInfo();
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
				stacktraceInfo();
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
			stacktraceInfo();
			e.printStackTrace();
		}
		/** chanceConfig update check */
		String checkchanceConfigversion = chanceConfig.getString("version", "1.0.0");
		if(checkchanceConfigversion != null){
			if(!checkchanceConfigversion.equalsIgnoreCase("1.0.18")){
				try {
					copyFile_Java7(getDataFolder() + "" + File.separatorChar + "chance_config.yml",getDataFolder() + "" + File.separatorChar + "old_chance_config.yml");
				} catch (IOException e) {
					stacktraceInfo();
					e.printStackTrace();
				}
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
			stacktraceInfo();
			e.printStackTrace();
		}
		/** Mob Names update check */
		String checklangnameConfigversion = langName.getString("creepeer_charged", "outdated");
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
					stacktraceInfo();
					e.printStackTrace();
				}
			}
		}
		/** end Mob names translation */
		
		world_whitelist = config.getString("world.whitelist", "");
		world_blacklist = config.getString("world.blacklist", "");
		mob_whitelist = config.getString("mob.whitelist", "");
		mob_blacklist = config.getString("mob.blacklist", "");
		
		/** Update Checker */
		if(UpdateCheck){
			try {
				Bukkit.getConsoleSender().sendMessage("Checking for updates...");
				UpdateChecker updater = new UpdateChecker(this, updateVersion, updateurl);
				if(updater.checkForUpdates()) {
					UpdateAvailable = true; // TODO: Update Checker
					UColdVers = updater.oldVersion();
					UCnewVers = updater.newVersion();
					Bukkit.getConsoleSender().sendMessage(newVerMsg.replace("{oVer}", UColdVers).replace("{nVer}", UCnewVers));
					Bukkit.getConsoleSender().sendMessage(Ansi.GREEN + UpdateChecker.getResourceUrl() + Ansi.RESET);
				}else{
					UpdateAvailable = false;
				}
			}catch(Exception e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not process update check");
				e.printStackTrace();
			}
		}
		/** end update checker */
		
		getServer().getPluginManager().registerEvents(this, this);
		
		String jarfilename = this.getFile().getAbsoluteFile().toString();
		log(Ansi.RED + " (  " + ChatColor.YELLOW + "-<[ PLEASE INCLUDE THIS WITH ANY ISSUE REPORTS ]>-" + Ansi.RESET);
		log(Ansi.RED + "  ) " + ChatColor.WHITE + "This server is running " + Bukkit.getName() + " version " + Bukkit.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ")" + Ansi.RESET);
		log(Ansi.RED + " (  " + ChatColor.WHITE + "vardebug=" + debug + " debug=" + getConfig().get("debug","error") + " in " + this.getDataFolder() + File.separatorChar + "config.yml" + Ansi.RESET);
		log(Ansi.RED + "  ) " + ChatColor.WHITE + "jarfilename=" + StrUtils.Right(jarfilename, jarfilename.length() - jarfilename.lastIndexOf(File.separatorChar)) + Ansi.RESET);
		log(Ansi.RED + " (  " + ChatColor.YELLOW + "-<[ PLEASE INCLUDE THIS WITH ANY ISSUE REPORTS ]>-" + Ansi.RESET);
		
		//Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "version");
		if(getConfig().getBoolean("debug")==true&&!(jarfile.toString().contains("-DEV"))){
			logDebug("Config.yml DUMP - INCLUDE THIS WITH ANY ISSUE REPORT VVV");
			logDebug("auto_update_check=" + getConfig().getBoolean("auto_update_check"));
			logDebug("debug=" + getConfig().getBoolean("debug"));
			logDebug("lang=" + getConfig().getString("lang"));
			logDebug("vanilla_heads.creeper=" + getConfig().getBoolean("vanilla_heads.creeper"));
			logDebug("vanilla_heads.ender_dragon=" + getConfig().getBoolean("vanilla_heads.ender_dragon"));
			logDebug("vanilla_heads.skeleton=" + getConfig().getBoolean("vanilla_heads.skeleton"));
			logDebug("vanilla_heads.wither_skeleton=" + getConfig().getBoolean("vanilla_heads.wither_skeleton"));
			logDebug("vanilla_heads.zombie=" + getConfig().getBoolean("vanilla_heads.zombie"));
			logDebug("world.whitelist=" + getConfig().getString("world.whitelist"));
			logDebug("world.blacklist=" + getConfig().getString("world.blacklist"));
			logDebug("mob.whitelist=" + getConfig().getString("mob.whitelist"));
			logDebug("mob.blacklist=" + getConfig().getString("mob.blacklist"));
			logDebug("lore.show_killer=" + getConfig().getBoolean("lore.show_killer"));
			logDebug("lore.show_plugin_name=" + getConfig().getBoolean("lore.show_plugin_name"));
			logDebug("wandering_trades.custom_wandering_trader=" + getConfig().getBoolean("wandering_trades.custom_wandering_trader"));
			logDebug("wandering_trades.keep_default_trades=" + getConfig().getBoolean("wandering_trades.keep_default_trades"));
			logDebug("wandering_trades.player_heads.enabled=" + getConfig().getBoolean("wandering_trades.player_heads.enabled"));
			logDebug("wandering_trades.player_heads.min=" + getConfig().getString("wandering_trades.player_heads.min"));
			logDebug("wandering_trades.player_heads.max=" + getConfig().getString("wandering_trades.player_heads.max"));
			logDebug("wandering_trades.block_heads.enabled=" + getConfig().getBoolean("wandering_trades.block_heads.enabled"));
			logDebug("wandering_trades.block_heads.min=" + getConfig().getString("wandering_trades.block_heads.min"));
			logDebug("wandering_trades.block_heads.max=" + getConfig().getString("wandering_trades.block_heads.max"));
			logDebug("wandering_trades.custom_trades.enabled=" + getConfig().getBoolean("wandering_trades.custom_trades.enabled"));
			logDebug("wandering_trades.custom_trades.min=" + getConfig().getString("wandering_trades.custom_trades.min"));
			logDebug("wandering_trades.custom_trades.max=" + getConfig().getString("wandering_trades.custom_trades.max"));
			logDebug("apply_looting=" + getConfig().getBoolean("apply_looting"));
			logDebug("whitelist.enforce=" + getConfig().getBoolean("whitelist.enforce"));
			logDebug("whitelist.player_head_whitelist=" + getConfig().getString("whitelist.player_head_whitelist"));
			logDebug("blacklist.enforce=" + getConfig().getBoolean("blacklist.enforce"));
			logDebug("blacklist.player_head_blacklist=" + getConfig().getString("blacklist.player_head_blacklist"));
			logDebug("Config.yml DUMP - INCLUDE THIS WITH ANY ISSUE REPORT ^^^");
		}
		
		consoleInfo("Enabled");
		
		Metrics metrics	= new Metrics(this);
		// New chart here
		// myPlugins()
		metrics.addCustomChart(new Metrics.AdvancedPie("my_other_plugins", new Callable<Map<String, Integer>>() {
					@Override
					public Map<String, Integer> call() throws Exception {
							Map<String, Integer> valueMap = new HashMap<>();
							
							if(getServer().getPluginManager().getPlugin("DragonDropElytra") != null){valueMap.put("DragonDropElytra", 1);}
				    		if(getServer().getPluginManager().getPlugin("NoEndermanGrief") != null){valueMap.put("NoEndermanGrief", 1);}
				    		if(getServer().getPluginManager().getPlugin("PortalHelper") != null){valueMap.put("PortalHelper", 1);}
				    		if(getServer().getPluginManager().getPlugin("ShulkerRespawner") != null){valueMap.put("ShulkerRespawner", 1);}
				    		//if(getServer().getPluginManager().getPlugin("MoreMobHeads") != null){valueMap.put("MoreMobHeads", 1);}
				    		if(getServer().getPluginManager().getPlugin("SilenceMobs") != null){valueMap.put("SilenceMobs", 1);}
				    		if(getServer().getPluginManager().getPlugin("SinglePlayerSleep") != null){valueMap.put("SinglePlayerSleep", 1);}
							if(getServer().getPluginManager().getPlugin("VillagerWorkstationHighlights") != null){valueMap.put("VillagerWorkstationHighlights", 1);}
							if(getServer().getPluginManager().getPlugin("RotationalWrench") != null){valueMap.put("RotationalWrench", 1);}
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
	
	public void consoleInfo(String state) {
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(Ansi.GREEN + "**************************************" + Ansi.RESET);
		logger.info(Ansi.YELLOW + pdfFile.getName() + " v" + pdfFile.getVersion() + Ansi.RESET + " is " + state);
		logger.info(Ansi.GREEN + "**************************************" + Ansi.RESET);
	}
	
	public	void log(String dalog){// TODO: log
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info(Ansi.YELLOW + pdfFile.getName() + " v" + pdfFile.getVersion() + Ansi.RESET + " " + dalog );
	}
	public	void logDebug(String dalog){
		log(Ansi.RED + "[DEBUG] " + Ansi.RESET + dalog);
	}
	public void logWarn(String dalog){
		log(Ansi.RED + "[WARN] " + Ansi.RESET  + dalog);
	}
	
	public static String getMCVersion() {
		String strVersion = Bukkit.getVersion();
		strVersion = strVersion.substring(strVersion.indexOf("MC: "), strVersion.length());
		strVersion = strVersion.replace("MC: ", "").replace(")", "");
		return strVersion;
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
		if(getServer().getPluginManager().getPlugin("WildStacker") != null){
			@Nonnull
			PersistentDataContainer pdc = mob.getPersistentDataContainer();
			pdc.set(NAMETAG_KEY, PersistentDataType.STRING, "nametag");
		}
	}
	
	public void givePlayerHead(Player player, String playerName){
		ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
		SkullMeta meta = (SkullMeta)playerHead.getItemMeta();
			meta.setDisplayName(playerName + "'s Head");
		meta.setOwner(playerName); //.setOwner(name);
			playerHead.setItemMeta(meta);//																	 e2d4c388-42d5-4a96-b4c9-623df7f5e026
		//player.getEquipment().setHelmet(playerHead);
		playerHead.setItemMeta(meta);
		player.getWorld().dropItemNaturally(player.getLocation(), playerHead);
	}
	
	public boolean isInventoryFull(Player p)	{
	    return !(p.getInventory().firstEmpty() == -1);
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEntityEvent event){// TODO: PlayerInteractEntityEvent
		if(!(event.getPlayer() instanceof Player))
			return;
		try{
			Player player = event.getPlayer();
			if(player.hasPermission("moremobheads.nametag")){
				if(getConfig().getBoolean("mob.nametag", false)) {
					Material material = player.getInventory().getItemInMainHand().getType();
					Material material2 = player.getInventory().getItemInOffHand().getType();
					String name = "";
					if(material.equals(Material.NAME_TAG)){
						name = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
						if(debug){logDebug("PIEE" + player.getDisplayName() + " Main hand name=" + name);};
					}
					if(material2.equals(Material.NAME_TAG)){
						name = player.getInventory().getItemInOffHand().getItemMeta().getDisplayName();
						if(debug){logDebug("PIEE " + player.getDisplayName() + " Off hand name=" + name);};
					}
					
		/** experimental code */
					/*ItemStack itemstack = player.getInventory().getItemInOffHand();
					getConfig().set("itemstack", itemstack);
					saveConfig();*/
			//		log("itemstack set");
	
					//player.getInventory().addItem(getConfig().getItemStack("itemstack"));
					
					/*Villager villager = (Villager) mob;
					List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();
					MerchantRecipe recipe = new MerchantRecipe(getConfig().getItemStack("itemstack"), 1);
					recipe.addIngredient(new ItemStack(Material.EMERALD));
									recipes.add(recipe);
									villager.setRecipes(recipes);*/
			/** experimental code */
									
					//player.sendMessage("Testing");
					if(material.equals(Material.NAME_TAG)||material2.equals(Material.NAME_TAG)){
						if(getServer().getPluginManager().getPlugin("SilenceMobs") != null){
							if(name.toLowerCase().contains("silenceme")||name.toLowerCase().contains("silence me")){
								return;
							}
						}
						//player.sendMessage("Testing 2");
						LivingEntity mob = (LivingEntity) event.getRightClicked();
						//log("mob=" + mob.getType().toString());
						if(mob instanceof Skeleton||mob instanceof Zombie||mob instanceof PigZombie||mob instanceof Villager){
							//log("mob=" + mob.getType().toString());
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
									event.setCancelled(true); // return;
									if(debug){log("PIE - Name Error 1");}
								}
							}else if(enforcewhitelist&&!enforceblacklist){
								if(onwhitelist){
									giveMobHead(mob, name);
								}else{
									event.setCancelled(true); // return;
									if(debug){log("PIE - Name not on whitelist.");}
								}
							}else if(!enforcewhitelist&&enforceblacklist){
								if(!onblacklist){
									giveMobHead(mob, name);
								}else{
									event.setCancelled(true); // return;
									if(debug){log("PIE - Name is on blacklist.");}
								}
							}else{
								giveMobHead(mob, name);
							}
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
			stacktraceInfo();
			e.printStackTrace();
		}

	}
	 
	public void dropMobHead(Entity entity, String name, Player killer){// TODO: dropMobHead
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
	 
	public boolean DropIt(EntityDeathEvent event, double chancepercent){// TODO: DropIt
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
	
	
	@SuppressWarnings("unused")
	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event){// TODO: EnityDeathEvent
		LivingEntity entity = event.getEntity();
		World world = event.getEntity().getWorld();
		
			
		if(world_whitelist != null&&!world_whitelist.isEmpty()&&world_blacklist != null&&!world_blacklist.isEmpty()){
			if(!StrUtils.stringContains(world_whitelist, world.getName().toString())&&StrUtils.stringContains(world_blacklist, world.getName().toString())){
				if(debug){logDebug("EDE - World - On blacklist and Not on whitelist.");}
				return;
			}else if(!StrUtils.stringContains(world_whitelist, world.getName().toString())&&!StrUtils.stringContains(world_blacklist, world.getName().toString())){
				if(debug){logDebug("EDE - World - Not on whitelist.");}
				return;
			}else if(!StrUtils.stringContains(world_whitelist, world.getName().toString())){
				
			}
		}else if(world_whitelist != null&&!world_whitelist.isEmpty()){
			if(!StrUtils.stringContains(world_whitelist, world.getName().toString())){
				if(debug){logDebug("EDE - World - Not on whitelist.");}
				return;
			}
		}else if(world_blacklist != null&&!world_blacklist.isEmpty()){
			if(StrUtils.stringContains(world_blacklist, world.getName().toString())){
				if(debug){logDebug("EDE - World - On blacklist.");}
				return;
			}
		}
			if(entity instanceof Player){
				if(debug){logDebug("EDE Entity is Player line:877");}

				if(entity.getKiller() instanceof Player){
					if(entity.getKiller().hasPermission("moremobheads.players")){
						if(DropIt(event, chanceConfig.getDouble("chance_percent.player", 0.50))){
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
					String name = event.getEntityType().toString().replace(" ", "_");
					if(debug){logDebug("EDE name=" + name);}
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
							//@Nonnull Set<String> isSpawner;
							String isNametag = null;
							@Nonnull
							PersistentDataContainer pdc = entity.getPersistentDataContainer();
							isNametag = entity.getPersistentDataContainer().get(NAMETAG_KEY, PersistentDataType.STRING);//.getScoreboardTags();//
							if(debug&&isNametag != null){logDebug("EDE isNametag=" + isNametag.toString());}

							if(entity.getKiller().hasPermission("moremobheads.mobs")){
								if(entity.getKiller().hasPermission("moremobheads.nametag")&&isNametag != null){
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
												if(DropIt(event, chanceConfig.getDouble("named_mob", 0.10))){
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
					 			
					 			if(mob_whitelist != null&&!mob_whitelist.isEmpty()&&mob_blacklist != null&&!mob_blacklist.isEmpty()){
					 				if(!StrUtils.stringContains(mob_whitelist, name)){//mob_whitelist.contains(name)
					 					log("EDE - Mob - Not on whitelist.");
					 					return;
					 				}
					 			}else if(mob_whitelist != null&&!mob_whitelist.isEmpty()){
					 				if(!StrUtils.stringContains(mob_whitelist, name)&&StrUtils.stringContains(mob_blacklist, name)){//mob_whitelist.contains(name)
					 					log("EDE - Mob - Not on whitelist.");
					 					return;
					 				}
					 			}else if(mob_blacklist != null&&!mob_blacklist.isEmpty()){
					 				if(StrUtils.stringContains(mob_blacklist, name)){
					 					log("EDE - Mob - On blacklist.");
					 					return;
					 				}
					 			}
								switch (name) {
					 			case "CREEPER":
					 				Creeper creeper = (Creeper) event.getEntity();
					 				double cchance = chanceConfig.getDouble("chance_percent.creeper", defpercent);
				 					if(creeper.isPowered()) {
				 						name = "CREEPER_CHARGED";
				 						cchance = 1.00;
				 					}
					 				if(DropIt(event, cchance)){
						 				if(getConfig().getBoolean("vanilla_heads.creeper", false)&&name != "CREEPER_CHARGED"){
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
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent.zombie", defpercent))){
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
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent.skeleton", defpercent))){
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
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent.wither_skeleton", defpercent))){
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
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent.ender_dragon", defpercent))){
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
					 				
					 				if(DropIt(event, getConfig().getDouble(name + "_" +	daFishType, defpercent))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name + "_" +	daFishType).getTexture(), MobHeads.valueOf(name + "_" +	daFishType).getName(), entity.getKiller()));
					 				}
					 				if(debug){logDebug("Skeleton Head Dropped");}
					 				break;*/
					 			case "WITHER":
									//Wither wither = (Wither) event.getEntity();
									int random = randomBetween(1, 4);
									if(debug){logDebug("EDE Wither random=" + random + "");}
									if(DropIt(event, chanceConfig.getDouble("chance_percent.wither", defpercent))){
										entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name + "_" + random).getTexture().toString(), 
					 							langName.getString(name.toLowerCase() + "." + random, MobHeads.valueOf(name + "_" + random).getName() + " Head"), entity.getKiller()));
										if(debug){logDebug("EDE Wither_" + random + " Head Dropped");}
									}
									break;
					 			case "WOLF":
					 				Wolf wolf = (Wolf) event.getEntity();
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))){
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
					 				if(debug){logDebug("EDE dafoxtype=" + dafoxtype);}
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent.fox." + dafoxtype.toString().toLowerCase(), defpercent))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name + "_" + dafoxtype).getTexture().toString(), 
					 							langName.getString(name.toLowerCase() + "." + dafoxtype.toLowerCase(), MobHeads.valueOf(name + "_" + dafoxtype).getName() + " Head"), entity.getKiller()));
					 					if(debug){logDebug("EDE Fox Head Dropped");}
					 				}
					 				
					 				break;
					 			case "CAT":
					 				Cat dacat = (Cat) entity;
					 				String dacattype = dacat.getCatType().toString();
					 				if(debug){logDebug("entity cat=" + dacat.getCatType());}
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent.cat." + dacattype.toLowerCase(), defpercent))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(),
				 							makeSkull(CatHeads.valueOf(dacattype).getTexture().toString(), 
				 									langName.getString(name.toLowerCase() + "." + dacattype.toLowerCase(), CatHeads.valueOf(dacattype).getName() + " Head"), entity.getKiller()));
					 					if(debug){logDebug("EDE Cat Head Dropped");}
					 				}
					 				break;
					 			case "OCELOT":
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
					 							langName.getString(MobHeads.valueOf(name).getNameString(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
					 					if(debug){logDebug("EDE " + name + " Head Dropped");}
					 				}
					 				if(debug){logDebug("EDE " + MobHeads.valueOf(name) + " killed");}
					 				
					 				break;
					 			case "BEE":
					 				Bee daBee = (Bee) entity;
					 				int daAnger = daBee.getAnger();
					 				if(debug){logDebug("EDE daAnger=" + daAnger);}
					 				boolean daNectar = daBee.hasNectar();
					 				if(debug){logDebug("EDE daNectar=" + daNectar);}
						 				if(daAnger >= 1&&daNectar == true){
						 					if(DropIt(event, chanceConfig.getDouble("chance_percent.bee.angry_pollinated", defpercent))){
						 						entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf("BEE_ANGRY_POLLINATED").getTexture().toString(), 
						 								langName.getString(name.toLowerCase() + ".angry_pollinated", "Angry Pollinated Bee Head"), entity.getKiller()));
						 						if(debug){logDebug("EDE Angry Pollinated Bee Head Dropped");}
						 					}
						 				}else if(daAnger >= 1&&daNectar == false){
						 					if(DropIt(event, chanceConfig.getDouble("chance_percent.bee.angry", defpercent))){
						 						entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf("BEE_ANGRY").getTexture().toString(), 
						 								langName.getString(name.toLowerCase() + ".angry", "Angry Bee Head"), entity.getKiller()));
						 						if(debug){logDebug("EDE Angry Bee Head Dropped");}
						 					}
						 				}else if(daAnger == 0&&daNectar == true){
						 					if(DropIt(event, chanceConfig.getDouble("chance_percent.bee.pollinated", defpercent))){
						 						entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf("BEE_POLLINATED").getTexture().toString(), 
						 								langName.getString(name.toLowerCase() + ".pollinated", "Pollinated Bee Head"), entity.getKiller()));
						 						if(debug){logDebug("EDE Pollinated Bee Head Dropped");}
						 					}
						 				}else if(daAnger == 0&&daNectar == false){
						 					if(DropIt(event, chanceConfig.getDouble("chance_percent.bee.chance_percent", defpercent))){
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
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent.llama." + daLlamaColor.toLowerCase(), defpercent))){		
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
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent.horse." + daHorseColor.toLowerCase(), defpercent))){
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
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent.mushroom_cow." + daCowVariant.toLowerCase(), defpercent))){
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
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent.panda." + daPandaGene.toLowerCase(), defpercent))){
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
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent.parrot." + daParrotVariant.toLowerCase(), defpercent))){
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
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent.rabbit." + daRabbitType.toLowerCase(), defpercent))){
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
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent.villager." + daVillagerType.toLowerCase() + "." + daVillagerProfession.toLowerCase(), defpercent))){
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
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent.zombie_villager", defpercent))){
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
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent.sheep." + daSheepColor.toLowerCase(), defpercent))){
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
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent.trader_llama." + daTraderLlamaColor.toLowerCase(), defpercent))){
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
					 				if(DropIt(event, chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))){
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
								HashSet<Integer> used = new HashSet<Integer>();
								for(int i=0; i<playerRandom; i++){
									int randomPlayerHead = randomBetween(1, numOfplayerheads);
									while (used.contains(randomPlayerHead)) { //while we have already used the number
										randomPlayerHead = randomBetween(1, numOfplayerheads); //generate a new one because it's already used
								    }
								    //by this time, add will be unique
								    used.add(randomPlayerHead);
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
								used.clear();
							}
						}
						/**
						 *  Block Heads
						 */
						if(getConfig().getBoolean("wandering_trades.block_heads.enabled", true)){
							int min = getConfig().getInt("wandering_trades.block_heads.min", 0);
							int max;
							if(getMCVersion().startsWith("1.16")||getMCVersion().startsWith("1.17")){
								max = getConfig().getInt("wandering_trades.block_heads.max", 5) / 2;
							}else{
								max = getConfig().getInt("wandering_trades.block_heads.max", 5);
							}
							if(debug){logDebug("CSE BH1 min=" + min + " max=" + max);}
							int blockRandom = randomBetween(min, max);
								if(debug){logDebug("CSE blockRandom=" + blockRandom);}
							if(blockRandom > 0){
									if(debug){logDebug("CSE blockRandom > 0");}
								int numOfblockheads = blockHeads.getInt("blocks.number");
									if(debug){logDebug("CSE numOfblockheads=" + numOfblockheads);}
								HashSet<Integer> used = new HashSet<Integer>();
								for(int i=0; i<blockRandom; i++){
										if(debug){logDebug("CSE i=" + i);}
									int randomBlockHead = randomBetween(1, numOfblockheads);
									while (used.contains(randomBlockHead)) { //while we have already used the number
										randomBlockHead = randomBetween(1, numOfblockheads); //generate a new one because it's already used
								    }
								    //by this time, add will be unique
								    used.add(randomBlockHead);
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
								used.clear();
							}
						}
						
						if(getMCVersion().startsWith("1.16")||getMCVersion().startsWith("1.17")){
							/**
							 *  Block Heads 2
							 */
							if(getConfig().getBoolean("wandering_trades.block_heads.enabled", true)){
								int min = getConfig().getInt("wandering_trades.block_heads.min", 0);
								int max = getConfig().getInt("wandering_trades.block_heads.max", 5) / 2;
									if(debug){logDebug("CSE BH2 min=" + min + " max=" + max);}
								int blockRandom = randomBetween(min, max);
									if(debug){logDebug("CSE blockRandom=" + blockRandom);}
								if(blockRandom > 0){
										if(debug){logDebug("CSE blockRandom > 0");}
									int numOfblockheads = blockHeads2.getInt("blocks.number");
										if(debug){logDebug("CSE numOfblockheads=" + numOfblockheads);}
									HashSet<Integer> used = new HashSet<Integer>();
									for(int i=0; i<blockRandom; i++){
											if(debug){logDebug("CSE i=" + i);}
										int randomBlockHead = randomBetween(1, numOfblockheads);
										while (used.contains(randomBlockHead)) { //while we have already used the number
											randomBlockHead = randomBetween(1, numOfblockheads); //generate a new one because it's already used
									    }
									    //by this time, add will be unique
									    used.add(randomBlockHead);
											if(debug){logDebug("CSE randomBlockHead=" + randomBlockHead);}
										ItemStack price1 = blockHeads2.getItemStack("blocks.block_" + randomBlockHead + ".price_1", new ItemStack(Material.AIR));
											if(debug){logDebug("CSE price1=" + price1);}
										ItemStack price2 = blockHeads2.getItemStack("blocks.block_" + randomBlockHead + ".price_2", new ItemStack(Material.AIR));
											if(debug){logDebug("CSE price2=" + price2);}
										ItemStack itemstack = blockHeads2.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
											if(debug){logDebug("CSE itemstack=" + itemstack);}
										MerchantRecipe recipe = new MerchantRecipe(itemstack, blockHeads2.getInt("blocks.block_" + randomBlockHead + ".quantity", (int) 1));
											recipe.setExperienceReward(true);
											recipe.addIngredient(price1);
											recipe.addIngredient(price2);
											recipes.add(recipe);
									}
									used.clear();
								}
							}
						}
						
						/**
						 *  Custom Trades
						 */
						if(getConfig().getBoolean("wandering_trades.custom_trades.enabled", false)){
							int customRandom = randomBetween(getConfig().getInt("wandering_trades.custom_trades.min", 0), getConfig().getInt("wandering_trades.custom_trades.max", 5));
							int numOfCustomTrades = traderCustom.getInt("custom_trades.number") + 1;
							//if(debug){logDebug("CSE numOfCustomTrades=" + numOfCustomTrades);}
							//int customRandom = randomBetween(getConfig().getInt("wandering_trades.min_custom_trades", 0), getConfig().getInt("wandering_trades.max_custom_trades", 3));
								if(debug){logDebug("CSE customRandom=" + customRandom);}
							if(customRandom > 0){
								if(debug){logDebug("CSE customRandom > 0");}
								//for(int randomCustomTrade=1; randomCustomTrade<numOfCustomTrades; randomCustomTrade++){
								HashSet<Integer> used = new HashSet<Integer>();
								for(int i=0; i<customRandom; i++){
									int randomCustomTrade = randomBetween(1, numOfCustomTrades);
									while (used.contains(randomCustomTrade)) { //while we have already used the number
										randomCustomTrade = randomBetween(1, numOfCustomTrades); //generate a new one because it's already used
								    }
								    //by this time, add will be unique
								    used.add(randomCustomTrade);
									if(debug){logDebug("CSE randomCustomTrade=" + randomCustomTrade);}
									/** Fix chance later */
									double chance = Math.random();
										if(debug){logDebug("CSE chance=" + chance + " line:1540");}
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
												recipe.addIngredient(price2);
												recipes.add(recipe);
									}
								}
								used.clear();
							}
						}
						
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
	public void onPlayerJoinEvent(PlayerJoinEvent event) // TODO: OnPlayerJoin
		{
			Player p = event.getPlayer();
			//if(p.isOp() && UpdateCheck||p.hasPermission("moremobheads.showUpdateAvailable")){	
			/** Notify Ops */
			if(UpdateAvailable&&(p.isOp()||p.hasPermission("moremobheads.showUpdateAvailable"))){
				p.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " v" + UColdVers + ChatColor.RESET + " " + lang.get("newvers") + ChatColor.GREEN + " v" + UCnewVers + ChatColor.RESET + "\n" + ChatColor.GREEN + UpdateChecker.getResourceUrl() + ChatColor.RESET);
				//Bukkit.getConsoleSender().sendMessage(this.getName() + Ansi.RED + " v" + UColdVers + Ansi.RESET + " " + lang.get("newvers") + Ansi.GREEN + " v" + UCnewVers + Ansi.RESET);
			}

			if(p.getDisplayName().equals("JoelYahwehOfWar")||p.getDisplayName().equals("JoelGodOfWar")){
				p.sendMessage(this.getName() + " " + this.getDescription().getVersion() + " Hello father!");
				//p.sendMessage("seed=" + p.getWorld().getSeed());
			}
	}
	
	public void makeHead(EntityDeathEvent event, Material material){// TODO: makeHead
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
	
	public ItemStack makeTraderSkull(String textureCode, String headName, String uuid, int amount){// TODO: maketraderSkull
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
	
	public ItemStack makeSkull(String textureCode, String headName, Player killer){// TODO: makeSkull
			ItemStack item = new ItemStack(Material.PLAYER_HEAD);
			if(textureCode == null) return item;
			SkullMeta meta = (SkullMeta) item.getItemMeta();

			GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(textureCode.getBytes()), textureCode);
			profile.getProperties().put("textures", new Property("textures", textureCode));
			profile.getProperties().put("display", new Property("Name", headName));
			setGameProfile(meta, profile);
			ArrayList<String> lore = new ArrayList();
			
			if(getConfig().getBoolean("lore.show_killer", true)){
				lore.add(ChatColor.RESET + "Killed by " + ChatColor.RESET + ChatColor.YELLOW + killer.getName());
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
	
	public ItemStack makeSkulls(String textureCode, String headName, int amount){
			ItemStack item = new ItemStack(Material.PLAYER_HEAD, amount);
			if(textureCode == null) return item;
			SkullMeta meta = (SkullMeta) item.getItemMeta();

			GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(textureCode.getBytes()), textureCode);
			profile.getProperties().put("textures", new Property("textures", textureCode));
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
	private static Field fieldProfileItem;
	public static void setGameProfile(SkullMeta meta, GameProfile profile){
			try{
				if(fieldProfileItem == null) fieldProfileItem = meta.getClass().getDeclaredField("profile");
				fieldProfileItem.setAccessible(true);
				fieldProfileItem.set(meta, profile);
			}
			catch(NoSuchFieldException e){
				stacktraceInfoStatic();
				e.printStackTrace();}
			catch(SecurityException e){
				stacktraceInfoStatic();
				e.printStackTrace();}
			catch(IllegalArgumentException e){
				stacktraceInfoStatic();
				e.printStackTrace();}
			catch(IllegalAccessException e){
				stacktraceInfoStatic();
				e.printStackTrace();}
	}

	
	@SuppressWarnings("unused")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){ // TODO: Commands
			//log("command=" + cmd.getName() + " args=" + args[0] + args[1]);
			if (cmd.getName().equalsIgnoreCase("MMH")){
				if (args.length == 0){
					sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
					sender.sendMessage(ChatColor.WHITE + " ");
					sender.sendMessage(ChatColor.WHITE + " /mmh reload - " + lang.get("reload", "Reloads this plugin."));//subject to server admin approval");
					sender.sendMessage(ChatColor.WHITE + " /mmh toggledebug - " + lang.get("srdebuguse", "Temporarily toggles debug."));//Cancels SinglePlayerSleep");
					if(getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)){
						sender.sendMessage(ChatColor.WHITE + " /mmh playerheads - " + lang.get("playerheads", "Shows how to use the playerheads commands"));
						//sender.sendMessage(ChatColor.WHITE + " /mmh blockheads - " + lang.get("blockheads", "Shows how to use the blockheads commands"));
						sender.sendMessage(ChatColor.WHITE + " /mmh customtrader - " + lang.get("customtrader", "Shows how to use the customtrader commands"));
					}
					sender.sendMessage(ChatColor.WHITE + " /mmh fixhead - " + lang.getString("headfix"));
					sender.sendMessage(ChatColor.WHITE + " /mmh givemh - gives player chosen the chosen mobhead");
					sender.sendMessage(ChatColor.WHITE + " /mmh giveph - gives player chosen player's head");
					sender.sendMessage(ChatColor.WHITE + " ");
					sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + this.getName() + ChatColor.GREEN + "]===============[]");
					return true;
				} 
				if(args[0].equalsIgnoreCase("reload")){
					String perm = "moremobheads.reload";
					boolean hasPerm = sender.hasPermission(perm);
					if(debug){logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);}
					if(hasPerm||!(sender instanceof Player)){
						oldconfig = new YamlConfiguration();
						log("Checking config file version...");
						try {
							oldconfig.load(new File(getDataFolder() + "" + File.separatorChar + "config.yml"));
						} catch (Exception e2) {
							logWarn("Could not load config.yml");
							stacktraceInfo();
							e2.printStackTrace();
						}
						String checkconfigversion = oldconfig.getString("version", "1.0.0");
						if(checkconfigversion != null){
							if(!checkconfigversion.equalsIgnoreCase("1.0.17")){
								try {
									copyFile_Java7(getDataFolder() + "" + File.separatorChar + "config.yml",getDataFolder() + "" + File.separatorChar + "old_config.yml");
								} catch (IOException e) {
									stacktraceInfo();
									e.printStackTrace();
								}
								saveResource("config.yml", true);
								
								try {
									config.load(new File(getDataFolder(), "config.yml"));
								} catch (IOException | InvalidConfigurationException e1) {
									logWarn("Could not load config.yml");
									stacktraceInfo();
									e1.printStackTrace();
								}
								try {
									oldconfig.load(new File(getDataFolder(), "old_config.yml"));
								} catch (IOException | InvalidConfigurationException e1) {
									logWarn("Could not load old_config.yml");
									stacktraceInfo();
									e1.printStackTrace();
								}
								config.set("auto_update_check", oldconfig.get("auto_update_check", true));
								config.set("debug", oldconfig.get("debug", false));
								config.set("lang", oldconfig.get("lang", "en_US"));
								config.set("colorful_console", oldconfig.get("colorful_console", true));
								config.set("vanilla_heads.creepers", oldconfig.get("vanilla_heads.creepers", false));
								config.set("vanilla_heads.ender_dragon", oldconfig.get("vanilla_heads.ender_dragon", false));
								config.set("vanilla_heads.skeleton", oldconfig.get("vanilla_heads.skeleton", false));
								config.set("vanilla_heads.wither_skeleton", oldconfig.get("vanilla_heads.wither_skeleton", false));
								config.set("vanilla_heads.zombie", oldconfig.get("vanilla_heads.zombie", false));
								config.set("world.whitelist", oldconfig.get("world.whitelist", ""));
								config.set("world.blacklist", oldconfig.get("world.blacklist", ""));
								config.set("mob.whitelist", oldconfig.get("mob.whitelist", ""));
								config.set("mob.blacklist", oldconfig.get("mob.blacklist", ""));
								config.set("mob.nametag", oldconfig.get("mob.nametag", false));
								config.set("lore.show_killer", oldconfig.get("lore.show_killer", true));
								config.set("lore.show_plugin_name", oldconfig.get("lore.show_plugin_name", true));
								config.set("wandering_trades.custom_wandering_trader", oldconfig.get("wandering_trades.custom_wandering_trader", true));
								config.set("wandering_trades.player_heads.enabled", oldconfig.get("wandering_trades.player_heads.enabled", true));
								config.set("wandering_trades.player_heads.min", oldconfig.get("wandering_trades.player_heads.min", 0));
								config.set("wandering_trades.player_heads.max", oldconfig.get("wandering_trades.player_heads.max", 3));
								config.set("wandering_trades.block_heads.enabled", oldconfig.get("wandering_trades.block_heads.enabled", true));
								config.set("wandering_trades.block_heads.min", oldconfig.get("wandering_trades.block_heads.min", 0));
								config.set("wandering_trades.block_heads.max", oldconfig.get("wandering_trades.block_heads.max", 3));
								config.set("wandering_trades.custom_trades.enabled", oldconfig.get("wandering_trades.custom_trades.enabled", false));
								config.set("wandering_trades.custom_trades.min", oldconfig.get("wandering_trades.custom_trades.min", 0));
								config.set("wandering_trades.custom_trades.max", oldconfig.get("wandering_trades.custom_trades.max", 3));
								config.set("apply_looting", oldconfig.get("apply_looting", true));
								config.set("whitelist.enforce", oldconfig.get("whitelist.enforce", true));
								config.set("whitelist.player_head_whitelist", oldconfig.get("whitelist.player_head_whitelist", "names_go_here"));
								config.set("blacklist.enforce", oldconfig.get("enforce_blacklist", true));
								config.set("blacklist.player_head_blacklist", oldconfig.get("blacklist.player_head_blacklist", "names_go_here"));
								//config.set("", oldconfig.get("", true));
								
								try {
									config.save(new File(getDataFolder(), "config.yml"));
								} catch (IOException e) {
									logWarn("Could not save old settings to config.yml");
									stacktraceInfo();
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
									stacktraceInfo();
									e1.printStackTrace();
								}
							}
							oldconfig = null;
						}
						log("Loading config file...");
						try {
							getConfig().load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e) {
							stacktraceInfo();
							e.printStackTrace();
						}
						try {
							config.load(new File(getDataFolder(), "config.yml"));
						} catch (IOException | InvalidConfigurationException e1) {
							logWarn("Could not load config.yml");
							stacktraceInfo();
							e1.printStackTrace();
						}
						
						world_whitelist = config.getString("world.whitelist", "");
						world_blacklist = config.getString("world.blacklist", "");
						mob_whitelist = config.getString("mob.whitelist", "");
						mob_blacklist = config.getString("mob.blacklist", "");
						colorful_console = getConfig().getBoolean("colorful_console", true);
						
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
								stacktraceInfo();
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
							blockFile116 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16.yml");
							blockFile1162 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16_2.yml");
							if(getMCVersion().startsWith("1.16")||getMCVersion().startsWith("1.17")){
								if(debug){logDebug("block_heads_1_16=" + blockFile116.getPath());}
								if(debug){logDebug("block_heads_1_16_2=" + blockFile1162.getPath());}
								if(!blockFile116.exists()){
									saveResource("block_heads_1_16.yml", true);
									log("block_heads_1_16.yml not found! copied block_heads_1_16.yml to " + getDataFolder() + "");
								}
								if(!blockFile1162.exists()){
									saveResource("block_heads_1_16_2.yml", true);
									log("block_heads_1_16_2.yml not found! copied block_heads_1_16_2.yml to " + getDataFolder() + "");
								}
								blockFile = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16.yml");
							log("Loading block_heads_1_16 files...");
							
							}else{
							log("Loading block_heads file...");
							}
						
							blockHeads = new YamlConfiguration();
							try {
								blockHeads.load(blockFile);
							} catch (IOException | InvalidConfigurationException e1) {
								stacktraceInfo();
								e1.printStackTrace();
							}
							blockHeads2 = new YamlConfiguration();
							try {
								blockHeads2.load(blockFile1162);
							} catch (IOException | InvalidConfigurationException e1) {
								stacktraceInfo();
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
								stacktraceInfo();
								e.printStackTrace();
							}
						}
						log("Loading chance_config file...");
						chanceFile = new File(getDataFolder() + "" + File.separatorChar + "chance_config.yml");
						try {
							chanceConfig.load(chanceFile);
						} catch (IOException | InvalidConfigurationException e) {
							stacktraceInfo();
							e.printStackTrace();
						}
						//showkiller = getConfig().getBoolean("lore.show_killer", true);
						//showpluginname = getConfig().getBoolean("lore.show_plugin_name", true);
						debug = getConfig().getBoolean("debug", false);
						daLang = getConfig().getString("lang", "en_US");
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("reloaded"));
						return true;
					}else if(!hasPerm){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("noperm").toString().replace("<perm>", perm) );
						return false;
					}
				}
				if(args[0].equalsIgnoreCase("toggledebug")||args[0].equalsIgnoreCase("td")){
					String perm = "moremobheads.toggledebug";
					boolean hasPerm = sender.hasPermission(perm);
					if(debug){logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);}
					if(sender.isOp()||hasPerm||!(sender instanceof Player)){
						debug = !debug;
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("debugtrue").toString().replace("boolean", "" + debug));
						return true;
					}else if(!hasPerm){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("noperm").toString().replace("<perm>", perm) );
						return false;
					}
				}
				if(args[0].equalsIgnoreCase("customtrader")||args[0].equalsIgnoreCase("ct")){
					String perm = "moremobheads.customtrader";
					boolean hasPerm = sender.hasPermission(perm);
					if(debug){logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);}
					if(hasPerm&&sender instanceof Player
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
								stacktraceInfo();
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
					}else if(!hasPerm){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("nopermordisabled").toString().replace("<perm>", perm) );
					return false;
					}
				}
				if(args[0].equalsIgnoreCase("playerheads")||args[0].equalsIgnoreCase("ph")){
					String perm = "moremobheads.playerheads";
					boolean hasPerm = sender.hasPermission(perm);
					if(debug){logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);}
					if(hasPerm&&sender instanceof Player
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
								stacktraceInfo();
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
					}else if(!hasPerm){
						sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("nopermordisabled").toString().replace("<perm>", perm) );
						return false;
					}
				}
				if(args[0].equalsIgnoreCase("fixhead")||args[0].equalsIgnoreCase("fh")){
					String perm = "moremobheads.fixhead";
					boolean hasPerm = sender.hasPermission(perm);
					if(debug){logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);}
					if(sender instanceof Player) {
						Player player = (Player) sender;
						if(hasPerm) {
							if(!args[1].isEmpty()) {
								if(args[1].equalsIgnoreCase("name")) {
									// FixHead NBT
									ItemStack mainHand = player.getInventory().getItemInMainHand();
									if( mainHand != null ){
										if( mainHand.getType().equals(Material.PLAYER_HEAD) ) {
											String texture = mainHand.getItemMeta().getDisplayName();
											
											SkullMeta skullname = (SkullMeta) mainHand.getItemMeta();
											if(skullname.getOwner() != null){
												String name = skullname.getOwner().toString();
												if(debug){logDebug("EPIE name=" + name);}
												if(debug){logDebug("EPIE lore=" + skullname.getLore());}
												if(skullname.getOwner().toString().length() >= 40){
													if(debug){logDebug("EPIE ownerName.lngth >= 40");}
														ItemStack itmStack = mainHand;
														//SkullMeta skullname = (SkullMeta) e.getItem().getItemStack().getItemMeta();
														String daMobName = "null";
														if(skullname != null){
															String isCat = CatHeads.getNameFromTexture(skullname.getOwner().toString());
															String isHorse = HorseHeads.getNameFromTexture(skullname.getOwner().toString());
															String isLlama = LlamaHeads.getNameFromTexture(skullname.getOwner().toString());
															String isMobHead = MobHeads.getNameFromTexture(skullname.getOwner().toString());
															String isRabbit = RabbitHeads.getNameFromTexture(skullname.getOwner().toString());
															String isSheep = SheepHeads.getNameFromTexture(skullname.getOwner().toString());
															String isVillager = VillagerHeads.getNameFromTexture(skullname.getOwner().toString());
															String isZombieVillager = ZombieVillagerHeads.getNameFromTexture(skullname.getOwner().toString());
															String isplayerhead = isPlayerHead(skullname.getOwner().toString());
															String isblockhead = isBlockHead(skullname.getOwner().toString());
															String isblockhead2 = isBlockHead2(skullname.getOwner().toString());
															if(isCat != null){				daMobName = isCat;	}
															if(isHorse != null){			daMobName = isHorse;	}
															if(isLlama != null){			daMobName = isLlama;	}
															if(isMobHead != null){			daMobName = isMobHead;	}
															if(isRabbit != null){			daMobName = isRabbit;	}
															if(isSheep != null){			daMobName = isSheep;	}
															if(isVillager != null){			daMobName = isVillager;	}
															if(isZombieVillager != null){	daMobName = isZombieVillager;	}
															if(daMobName == null){
																if(blockHeads != null){
																	if(isblockhead != null){	daMobName = isblockhead;	}
																}
																if(blockHeads2 != null){
																	if(isblockhead2 != null){	daMobName = isblockhead2;	}
																}
																if(playerHeads != null){
																	if(isplayerhead != null){	daMobName = isplayerhead;	}
																}
															}
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
															skullname.setLore(lore);
															skullname.setDisplayName(daMobName);
															itmStack.setItemMeta(skullname);
															//fixHeadNBT(skullname.getOwner(), daMobName, lore);
															sender.sendMessage("DisplayName of head in your main hand has been fixed.");
															//if(debug){logDebug("test3a");}
														}else{
															if(debug){logDebug("EPIE test3b meta == null");}
													}
												}
											}
										}else {
											sender.sendMessage("An Error occured.");
										}
									}
								}
								
								if(args[1].equalsIgnoreCase("stack")) {
									// FixHead Stack
									ItemStack mainHand = player.getInventory().getItemInMainHand();
									ItemStack offHand = player.getInventory().getItemInOffHand();
									if( mainHand != null && offHand != null ){
										if( mainHand.getType().equals(Material.PLAYER_HEAD) && offHand.getType().equals(Material.PLAYER_HEAD) ) {
											ItemStack is = fixHeadStack(offHand, mainHand);
											//is.setAmount(mainHand.getAmount());
											if(is != mainHand) {
												player.getInventory().setItemInMainHand(is);
												sender.sendMessage("NBT data of off hand head has been copied to the head in your main hand");
											}else {
												sender.sendMessage("An Error occured.");
											}
										}else if( !mainHand.getType().equals(Material.PLAYER_HEAD) && !offHand.getType().equals(Material.PLAYER_HEAD) ){
											sender.sendMessage("Items in Main hand, and Off hand are not Player_Head.");
										}else if( !mainHand.getType().equals(Material.PLAYER_HEAD) && offHand.getType().equals(Material.PLAYER_HEAD) ){
											sender.sendMessage("Item in Main hand is not a Player_Head.");
										}else if( mainHand.getType().equals(Material.PLAYER_HEAD) && !offHand.getType().equals(Material.PLAYER_HEAD) ){
											sender.sendMessage("Item in Off hand is not a Player_Head.");
										}
									}
								}
							}
							
						}else if(!hasPerm){
							sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("noperm").toString().replace("<perm>", perm) );
							return false;
						}
					}
				}
				if(args[0].equalsIgnoreCase("giveMH")){
					// /mmh giveMH player mob qty
					// cmd  0      1      2   3
					if( args.length==4 ){
						String perm = "moremobheads.give";
						boolean hasPerm = sender.hasPermission(perm);
						if(debug){logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);}
						if(hasPerm) {
							Player player = Bukkit.getPlayer(args[1]);
							if(!args[2].isEmpty()) {
								String mob = args[2].toLowerCase();
								log("mob=" + mob);
								if(!args[3].isEmpty()) {
									int number = Integer.parseInt(args[3]);
									String[] splitmob = mob.split("\\.");
									switch (splitmob[0]) {
									case "creeper":
										if(getConfig().getBoolean("vanilla_heads.creeper", false)){
						 					player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.CREEPER_HEAD));
						 				}else{ // langName
						 					player.getWorld().dropItemNaturally(player.getLocation(), makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture().toString(), 
						 							langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number ));
						 				} // MobHeads.valueOf(name).getName() + " Head"
										break;
									case "zombie":
										if(getConfig().getBoolean("vanilla_heads.zombie", false)){
						 					player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.ZOMBIE_HEAD));
						 				}else{ // langName
						 					player.getWorld().dropItemNaturally(player.getLocation(), makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture().toString(), 
						 							langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number ));
						 				} // MobHeads.valueOf(name).getName() + " Head"
										break;
									case "skeleton":
										if(getConfig().getBoolean("vanilla_heads.skeleton", false)){
						 					player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.SKELETON_SKULL));
						 				}else{ // langName
						 					player.getWorld().dropItemNaturally(player.getLocation(), makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture().toString(), 
						 							langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number ));
						 				} // MobHeads.valueOf(name).getName() + " Head"
										break;
									case "wither_skeleton":
										if(getConfig().getBoolean("vanilla_heads.wither_skeleton", false)){
						 					player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.WITHER_SKELETON_SKULL));
						 				}else{ // langName
						 					player.getWorld().dropItemNaturally(player.getLocation(), makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture().toString(), 
						 							langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number ));
						 				} // MobHeads.valueOf(name).getName() + " Head"
										break;
									case "ender_dragon":
										if(getConfig().getBoolean("vanilla_heads.ender_dragon", false)){
						 					player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.DRAGON_HEAD));
						 				}else{ // langName
						 					player.getWorld().dropItemNaturally(player.getLocation(), makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture().toString(), 
						 							langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number ));
						 				} // MobHeads.valueOf(name).getName() + " Head"
										break;
									case "cat":
										player.getWorld().dropItemNaturally(player.getLocation(),
					 							makeSkulls(CatHeads.valueOf(splitmob[1].toUpperCase()).getTexture().toString(), 
					 									langName.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), CatHeads.valueOf(splitmob[1].toUpperCase()).getName() + " Head"), number ));
										break;
									case "bee":
										log("splitmob.length=" + splitmob.length);
										if(splitmob.length == 1) {
											player.getWorld().dropItemNaturally(player.getLocation(), makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture().toString(), 
						 							langName.getString(splitmob[0].toLowerCase() + ".none", MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number ));
										}else {
											player.getWorld().dropItemNaturally(player.getLocation(), makeSkulls( MobHeads.valueOf( mob.toUpperCase().replace(".", "_") ).getTexture().toString(), 
						 							langName.getString(mob.toLowerCase().replace(".", "_"), MobHeads.valueOf(mob.toUpperCase().replace(".", "_")).getName() + " Head"), number ));
										}
										break;
									case "villager": // villager type profession, villager profession type
										// name = splitmob[0], type =  splitmob[1], profession = splitmob[2]
										player.getWorld().dropItemNaturally(player.getLocation(), makeSkulls(VillagerHeads.valueOf( splitmob[0].toUpperCase() + "_" + splitmob[2].toUpperCase() + "_" + splitmob[1].toUpperCase() ).getTexture().toString(), 
					 							langName.getString( splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase() + "." + splitmob[2].toLowerCase()
					 									, VillagerHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[2].toUpperCase() + "_" + splitmob[1].toUpperCase()).getName() + " Head"), number ));
										break;
									case "zombie_villager":
										player.getWorld().dropItemNaturally(player.getLocation(), makeSkulls(ZombieVillagerHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[2].toUpperCase() ).getTexture().toString(), 
					 							langName.getString(splitmob[0].toLowerCase() + "." + splitmob[2].toLowerCase(), ZombieVillagerHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[2].toUpperCase() ).getName() ), number ));
										break;
									case "llama":
									case "trader_llama":
										player.getWorld().dropItemNaturally(player.getLocation(), makeSkulls(LlamaHeads.valueOf( splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase() ).getTexture().toString(), 
					 							langName.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), LlamaHeads.valueOf( splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase() ).getName()), number ));
										break;
									case "horse":
										player.getWorld().dropItemNaturally(player.getLocation(), makeSkulls(HorseHeads.valueOf( splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase() ).getTexture().toString(), 
					 							langName.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), HorseHeads.valueOf( splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase() ).getName()), number ));
										break;
									case "rabbit":
										player.getWorld().dropItemNaturally(player.getLocation(), makeSkulls(RabbitHeads.valueOf( splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase() ).getTexture().toString(), 
					 							langName.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), RabbitHeads.valueOf( splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase() ).getName()), number ));
										break;
									case "sheep":
										String sheeptype;
										if(splitmob[1].equalsIgnoreCase("jeb_")) {
											sheeptype = "jeb_";
										}else {
											sheeptype = splitmob[1].toUpperCase();
										}
										player.getWorld().dropItemNaturally(player.getLocation(), makeSkulls(SheepHeads.valueOf( splitmob[0].toUpperCase() + "_" + sheeptype ).getTexture().toString(), 
					 							langName.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), SheepHeads.valueOf( splitmob[0].toUpperCase() + "_" + sheeptype ).getName()), number ));
										break;
									default:
										player.getWorld().dropItemNaturally(player.getLocation(), makeSkulls(MobHeads.valueOf(mob.toUpperCase().replace(".", "_")).getTexture().toString(), 
					 							langName.getString(mob.toLowerCase(), MobHeads.valueOf(mob.toUpperCase().replace(".", "_")).getName() + " Head"), number ));
										break;
									}
								}
							}
						}else if(!hasPerm){
							sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("noperm").toString().replace("<perm>", perm) );
							return false;
						}
					}else {
						sender.sendMessage("Command usage, /mmh givemh playername mobname 1");
						return false;
					}
					
				}
				// /mmh giveph player
				// /mmh giveph player player
				//  0   1      2      3
				if(args[0].equalsIgnoreCase("givePH")){ 
					if( args.length >= 2 ){
						String perm = "moremobheads.give";
						boolean hasPerm = sender.hasPermission(perm);
						if(debug){logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);}
						if( hasPerm ){
							if(debug) {logDebug("CMD GPH args.length=" + args.length);}
							if( args.length==2 && sender instanceof Player ){
								givePlayerHead((Player) sender,args[1]);
								if(debug) {logDebug("CMD GPH args1=" + args[1]);}
								return true;
							}else if( args.length==3){
								Player player = Bukkit.getPlayer(args[1]);
								givePlayerHead(player,args[2]);
								if(debug) {logDebug("CMD GPH args1=" + args[1] + ", args2=" +args[2]);}
								return true;
							}else if( args.length==2 && !(sender instanceof Player) ){
								sender.sendMessage("Console cannot give itself Heads. /mmh giveph <player> <player>");
								return false;
							}
						}else if( !hasPerm ){
							sender.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("noperm").toString().replace("<perm>", perm) );
							return false;
						}
					}else {
						sender.sendMessage("Command usage, /mmh giveph playername playername 1");
						return false;
					}
					return false;
				}
				/**if(args[0].equalsIgnoreCase("blockheads")||args[0].equalsIgnoreCase("bh")){
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
				}*/
			}
			return false;
	}
	
	/**
	for (Material material : Material.values()) {
		autoCompletes.add(material.name());
	}
	autoCompletes.remove(autoCompletes.indexOf("bedrock"));*/
	
	@Override 
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { // TODO: Tab Complete
		if (command.getName().equalsIgnoreCase("mmh")) {
			List<String> autoCompletes = new ArrayList<>(); //create a new string list for tab completion
			if (args.length == 1) { // reload, toggledebug, playerheads, customtrader, headfix
				autoCompletes.add("reload");
				autoCompletes.add("toggledebug");
				autoCompletes.add("playerheads");
				autoCompletes.add("customtrader");
				autoCompletes.add("fixhead");
				autoCompletes.add("givemh");
				autoCompletes.add("giveph");
				return autoCompletes; // then return the list
			}
			if(args.length > 1) {
				if( args[0].equalsIgnoreCase("fixhead") || args[0].equalsIgnoreCase("fh") && args[1].isEmpty() ) {
					autoCompletes.add("name");
					autoCompletes.add("stack");
					return autoCompletes; // then return the list
				}
				if( args[0].equalsIgnoreCase("playerheads") || args[0].equalsIgnoreCase("ph") && args[1].isEmpty() ) {
					autoCompletes.add("add");
					autoCompletes.add("remove");
					autoCompletes.add("replace");
					return autoCompletes; // then return the list
				}else if( (args[0].equalsIgnoreCase("playerheads") || args[0].equalsIgnoreCase("ph")) && (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("replace")) ) {
					if( args[1].equalsIgnoreCase("remove") ) {
						autoCompletes.add("0");
						return autoCompletes; // then return the list
					}
					if( args[1].equalsIgnoreCase("replace") ) {
						autoCompletes.add("0");
						return autoCompletes; // then return the list
					}
				}
				if( args[0].equalsIgnoreCase("customtrader") || args[0].equalsIgnoreCase("ct") && args[1].isEmpty() ) {
					autoCompletes.add("add");
					autoCompletes.add("remove");
					autoCompletes.add("replace");
					return autoCompletes; // then return the list
				}else if( (args[0].equalsIgnoreCase("customtrader") || args[0].equalsIgnoreCase("ct")) && (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("replace")) ) {
					if( args[1].equalsIgnoreCase("remove") ) {
						autoCompletes.add("0");
						return autoCompletes; // then return the list
					}
					if( args[1].equalsIgnoreCase("replace") ) {
						autoCompletes.add("0");
						return autoCompletes; // then return the list
					}
				}
				if( args[0].equalsIgnoreCase("giveph") ) {
					//return null;
					if(  args.length < 2 ) { 
						// /mmh giveph @p @P
						// /cmd 0      1  2
						// return null to list all players.
						return null;
					}
					if(  args.length > 2 ) { 
						// /mmh giveph @p @P
						// /cmd 0      1  2
						// return null to list all players.
						return null;
					}
					
				}
				if( args[0].equalsIgnoreCase("givemh") ) {
					if(  args.length < 2 ) { 
						// /mmh give @p
						// /cmd 0    1
						// return null to list all players.
						return null;
					}else if(  args.length > 2 ) {
						if(debug) {logDebug("TC arg1!null args.length=" + args.length);}
						if( args.length == 3 ) {
						
							// /mmh give @p moblist #
							// /cmd 0    1  2       3
						    for(String key : chanceConfig.getConfigurationSection("chance_percent").getKeys(true)) {
						        //System.out.println(key);
						        autoCompletes.add(key);
						        //System.out.println(key);
						        if(key.equalsIgnoreCase("wolf")) {
						    		autoCompletes.add("wolf.angry");
						    	}else if(key.equalsIgnoreCase("wither")) {
						    		autoCompletes.add("wither.1");
						    		autoCompletes.add("wither.2");
						    		autoCompletes.add("wither.3");
						    		autoCompletes.add("wither.4");
						    		autoCompletes.remove(autoCompletes.indexOf("wither"));
						    	}else if(key.equalsIgnoreCase("zombie_villager")) {
						    		autoCompletes.add("zombie_villager.armorer");
						    		autoCompletes.add("zombie_villager.butcher");
						    		autoCompletes.add("zombie_villager.cartographer");
						    		autoCompletes.add("zombie_villager.cleric");
						    		autoCompletes.add("zombie_villager.farmer");
						    		autoCompletes.add("zombie_villager.fisherman");
						    		autoCompletes.add("zombie_villager.fletcher");
						    		autoCompletes.add("zombie_villager.leatherworker");
						    		autoCompletes.add("zombie_villager.librarian");
						    		autoCompletes.add("zombie_villager.mason");
						    		autoCompletes.add("zombie_villager.nitwit");
						    		autoCompletes.add("zombie_villager.none");
						    		autoCompletes.add("zombie_villager.shepherd");
						    		autoCompletes.add("zombie_villager.toolsmith");
						    		autoCompletes.add("zombie_villager.weaponsmith");
						    		autoCompletes.remove(autoCompletes.indexOf("zombie_villager"));
						    	}
						    }
						    autoCompletes.remove(autoCompletes.indexOf("bee.chance_percent"));
						    autoCompletes.remove(autoCompletes.indexOf("cat"));
						    autoCompletes.remove(autoCompletes.indexOf("fox"));
						    autoCompletes.remove(autoCompletes.indexOf("horse"));
						    autoCompletes.remove(autoCompletes.indexOf("llama"));
						    autoCompletes.remove(autoCompletes.indexOf("panda"));
						    autoCompletes.remove(autoCompletes.indexOf("parrot"));
						    autoCompletes.remove(autoCompletes.indexOf("rabbit"));
						    autoCompletes.remove(autoCompletes.indexOf("sheep"));
						    autoCompletes.remove(autoCompletes.indexOf("trader_llama"));
						    autoCompletes.remove(autoCompletes.indexOf("mushroom_cow"));
						    autoCompletes.remove(autoCompletes.indexOf("villager"));
						    autoCompletes.remove(autoCompletes.indexOf("villager.desert"));
						    autoCompletes.remove(autoCompletes.indexOf("villager.jungle"));
						    autoCompletes.remove(autoCompletes.indexOf("villager.plains"));
						    autoCompletes.remove(autoCompletes.indexOf("villager.savanna"));
						    autoCompletes.remove(autoCompletes.indexOf("villager.snow"));
						    autoCompletes.remove(autoCompletes.indexOf("villager.swamp"));
						    autoCompletes.remove(autoCompletes.indexOf("villager.taiga"));
						    
						    return autoCompletes;
						}else if(  args.length == 4 ) {
							autoCompletes.add("1");
							return autoCompletes;
						}
					}
				}
			}
		}
		return null;
	}
	
	boolean isInteger(String s){
	    try{
	        Integer.parseInt(s);
	        return true;
	    }catch (NumberFormatException ex){
	        return false;
	    }
	}
	
	//@SuppressWarnings("unused")
	@EventHandler
	public void OnTake(EntityPickupItemEvent e){
	/** //ItemStack item = new ItemStack(7);
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
								String isblockhead2 = isBlockHead2(meta.getOwner().toString());
								if(isCat != null){				daMobName = isCat;	}
								if(isHorse != null){			daMobName = isHorse;	}
								if(isLlama != null){			daMobName = isLlama;	}
								if(isMobHead != null){			daMobName = isMobHead;	}
								if(isRabbit != null){			daMobName = isRabbit;	}
								if(isSheep != null){			daMobName = isSheep;	}
								if(isVillager != null){			daMobName = isVillager;	}
								if(isZombieVillager != null){	daMobName = isZombieVillager;	}
								if(daMobName == null){
									if(blockHeads != null){
										if(isblockhead != null){	daMobName = isblockhead;	}
									}
								}
								if(daMobName == null){
									if(blockHeads2 != null){
										if(isblockhead2 != null){	daMobName = isblockhead2;	}
									}
								}
								if(daMobName == null){
									if(playerHeads != null){
										if(isplayerhead != null){	daMobName = isplayerhead;	}
									}
								}
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
			//log("test4");*/
	}
	
	public String isPlayerHead(String string){
		try{
			playerFile = new File(getDataFolder() + "" + File.separatorChar + "player_heads.yml");//\
			if(!playerFile.exists()){																	// checks if the yaml does not exist
				return null;
			}
			int numOfCustomTrades = playerHeads.getInt("players.number", 0) + 1;
			if(debug){logDebug("iPH string=" + string);}
			for(int randomPlayerHead=1; randomPlayerHead<numOfCustomTrades; randomPlayerHead++){
				ItemStack itemstack = playerHeads.getItemStack("players.player_" + randomPlayerHead + ".itemstack", new ItemStack(Material.AIR));
				if(itemstack != null){
					SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
					if(skullmeta != null){
						//if(debug&&skullmeta != null){logDebug("iPH getOwner_" + randomPlayerHead + "=" + skullmeta.getOwner().toString());}
						if(skullmeta.getOwner() != null){
							if(skullmeta.getOwner().toString().contains(string)){
								return itemstack.getItemMeta().getDisplayName();
							}
						}
					}
				}
			}
		}catch(Exception e){
			//stacktraceInfo();
			//e.printStackTrace();
			return null;
		}
		//playerHeads
		return null;
	}
	
	public String isBlockHead(String string){
		try{
			if(!getMCVersion().startsWith("1.16")&&!getMCVersion().startsWith("1.17")){
				blockFile = new File(getDataFolder() + "" + File.separatorChar + "block_heads.yml");//\
				if(!blockFile.exists()){																	// checks if the yaml does not exist
					return null;
				}
			}
			blockFile116 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16.yml");
			blockFile1162 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16_2.yml");
			if(getMCVersion().startsWith("1.16")||getMCVersion().startsWith("1.17")){
				if(!blockFile116.exists()){
					return null;
				}
			}
			int numOfCustomTrades = blockHeads.getInt("blocks.number", 0) + 1;
			if(debug){logDebug("iBH string=" + string);}
			for(int randomBlockHead=1; randomBlockHead<numOfCustomTrades; randomBlockHead++){
				ItemStack itemstack = blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
				if(itemstack != null){
					SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
					if(skullmeta != null){
						//if(debug&&skullmeta != null){logDebug("iBH getOwner_" + randomBlockHead + "=" + skullmeta.getOwner().toString());}
						if(skullmeta.getOwner() != null){
							if(skullmeta.getOwner().toString().contains(string)){
								return itemstack.getItemMeta().getDisplayName();
							}
						}
					}
				}
			}
		}catch(Exception e){
			//stacktraceInfo();
			//e.printStackTrace();
			return null;
		}
		//blockHeads
		return null;
	}
	public String isBlockHead2(String string){
		try{
			if(!getMCVersion().startsWith("1.16")&&!getMCVersion().startsWith("1.17")){																// checks if the yaml does not exist
					return null;
			}
			blockFile1162 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16_2.yml");
			if(getMCVersion().startsWith("1.16")||getMCVersion().startsWith("1.17")){
				if(!blockFile1162.exists()){
					return null;
				}
			
			}
			int numOfCustomTrades = blockHeads2.getInt("blocks.number", 0) + 1;
			if(debug){logDebug("iBH string=" + string);}
			for(int randomBlockHead=1; randomBlockHead<numOfCustomTrades; randomBlockHead++){
				ItemStack itemstack = blockHeads2.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
				if(itemstack != null){
					SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
					if(skullmeta != null){
						//if(debug&&skullmeta != null){logDebug("iBH getOwner_" + randomBlockHead + "=" + skullmeta.getOwner().toString());}
						if(skullmeta.getOwner() != null){
							if(skullmeta.getOwner().toString().contains(string)){
								return itemstack.getItemMeta().getDisplayName();
							}
						}
					}
				}
			}
		}catch(Exception e){
			//stacktraceInfo();
			//e.printStackTrace();
			return null;
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
			stacktraceInfo();
			e.printStackTrace();
		}
		config.set("player", oldconfig.get("player", "0.50"));
		config.set("named_mob", oldconfig.get("named_mob", "0.10"));
		config.set("axolotl", oldconfig.get("axolotl", "0.10"));
		config.set("bat", oldconfig.get("bat", "0.10"));
		config.set("bee.angry_pollinated", oldconfig.get("bee.angry_pollinated", "0.20"));
		config.set("bee.angry", oldconfig.get("bee.angry", "0.20"));
		config.set("bee.pollinated", oldconfig.get("bee.pollinated", "0.20"));
		config.set("bee.chance_percent", oldconfig.get("bee.chance_percent", "0.20"));
		config.set("blaze", oldconfig.get("blaze", "0.005"));
		config.set("cat.all_black", oldconfig.get("cat.all_black", "0.33"));
		config.set("cat.black", oldconfig.get("cat.black", "0.33"));
		config.set("cat.british_shorthair", oldconfig.get("cat.british_shorthair", "0.33"));
		config.set("cat.calico", oldconfig.get("cat.calico", "0.33"));
		config.set("cat.jellie", oldconfig.get("cat.jellie", "0.33"));
		config.set("cat.persian", oldconfig.get("cat.persian", "0.33"));
		config.set("cat.ragdoll", oldconfig.get("cat.ragdoll", "0.33"));
		config.set("cat.red", oldconfig.get("cat.red", "0.33"));
		config.set("cat.siamese", oldconfig.get("cat.siamese", "0.33"));
		config.set("cat.tabby", oldconfig.get("cat.tabby", "0.33"));
		config.set("cat.white", oldconfig.get("cat.white", "0.33"));
		
		config.set("cave_spider", oldconfig.get("cave_spider", "0.005"));
		config.set("chicken", oldconfig.get("chicken", "0.01"));
		config.set("cod", oldconfig.get("cod", "0.10"));
		config.set("cow", oldconfig.get("cow", "0.01"));
		config.set("creeper", oldconfig.get("creeper", "0.50"));
		config.set("creeper_charged", oldconfig.get("creeper_charged", "1.00"));
		config.set("dolphin", oldconfig.get("dolphin", "0.33"));
		config.set("donkey", oldconfig.get("donkey", "0.20"));
		config.set("drowned", oldconfig.get("drowned", "0.05"));
		config.set("elder_guardian", oldconfig.get("elder_guardian", "1.00"));
		config.set("ender_dragon", oldconfig.get("ender_dragon", "1.00"));
		config.set("enderman", oldconfig.get("enderman", "0.005"));
		config.set("endermite", oldconfig.get("endermite", "0.10"));
		config.set("evoker", oldconfig.get("evoker", "0.25"));
		config.set("fox.red", oldconfig.get("fox.red", "0.10"));
		config.set("fox.snow", oldconfig.get("fox.snow", "0.10"));
		config.set("ghast", oldconfig.get("ghast", "0.0625"));
		config.set("giant", oldconfig.get("giant", "0.025"));
		config.set("glow_squid", oldconfig.get("glow_squid", "0.10"));
		config.set("goat", oldconfig.get("goat", "0.10"));
		config.set("guardian", oldconfig.get("guardian", "0.005"));
		config.set("hoglin", oldconfig.get("hoglin", "0.03"));
		config.set("horse.black", oldconfig.get("horse.black", "0.27"));
		config.set("horse.brown", oldconfig.get("horse.brown", "0.27"));
		config.set("horse.chestnut", oldconfig.get("horse.chestnut", "0.27"));
		config.set("horse.creamy", oldconfig.get("horse.creamy", "0.27"));
		config.set("horse.dark_brown", oldconfig.get("horse.dark_brown", "0.27"));
		config.set("horse.gray", oldconfig.get("horse.gray", "0.27"));
		config.set("horse.white", oldconfig.get("horse.white", "0.27"));
		config.set("husk", oldconfig.get("husk", "0.06"));
		config.set("illusioner", oldconfig.get("illusioner", "0.25"));
		config.set("iron_golem", oldconfig.get("iron_golem", "0.05"));
		config.set("llama.brown", oldconfig.get("llama.brown", "0.24"));
		config.set("llama.creamy", oldconfig.get("llama.creamy", "0.24"));
		config.set("llama.gray", oldconfig.get("llama.gray", "0.24"));
		config.set("llama.white", oldconfig.get("llama.white", "0.24"));
		config.set("magma_cube", oldconfig.get("magma_cube", "0.005"));
		config.set("mule", oldconfig.get("mule", "0.20"));
		config.set("mushroom_cow.red", oldconfig.get("mushroom_cow.red", "0.10"));
		config.set("mushroom_cow.brown", oldconfig.get("mushroom_cow.brown", "0.10"));
		config.set("ocelot", oldconfig.get("cat.wild_ocelot", "0.20"));
		config.set("panda.aggressive", oldconfig.get("panda.aggressive", "0.27"));
		config.set("panda.brown", oldconfig.get("panda.brown", "0.27"));
		config.set("panda.lazy", oldconfig.get("panda.lazy", "0.27"));
		config.set("panda.normal", oldconfig.get("panda.normal", "0.27"));
		config.set("panda.playful", oldconfig.get("panda.playful", "0.27"));
		config.set("panda.weak", oldconfig.get("panda.weak", "0.27"));
		config.set("panda.worried", oldconfig.get("panda.worried", "0.27"));
		config.set("parrot.blue", oldconfig.get("parrot.blue", "0.25"));
		config.set("parrot.cyan", oldconfig.get("parrot.cyan", "0.25"));
		config.set("parrot.gray", oldconfig.get("parrot.gray", "0.25"));
		config.set("parrot.green", oldconfig.get("parrot.green", "0.25"));
		config.set("parrot.red", oldconfig.get("parrot.red", "0.25"));
		config.set("phantom", oldconfig.get("phantom", "0.10"));
		config.set("pig", oldconfig.get("pig", "0.01"));
		config.set("piglin", oldconfig.get("piglin", "0.04"));
		config.set("pig_zombie", oldconfig.get("pig_zombie", "0.005"));
		config.set("pillager", oldconfig.get("pillager", "0.025"));
		config.set("polar_bear", oldconfig.get("polar_bear", "0.20"));
		config.set("pufferfish", oldconfig.get("pufferfish", "0.15"));
		config.set("rabbit.black", oldconfig.get("rabbit.black", "0.26"));
		config.set("rabbit.black_and_white", oldconfig.get("rabbit.black_and_white", "0.26"));
		config.set("rabbit.brown", oldconfig.get("rabbit.brown", "0.26"));
		config.set("rabbit.gold", oldconfig.get("rabbit.gold", "0.26"));
		config.set("rabbit.salt_and_pepper", oldconfig.get("rabbit.salt_and_pepper", "0.26"));
		config.set("rabbit.the_killer_bunny", oldconfig.get("rabbit.the_killer_bunny", "1.00"));
		config.set("rabbit.toast", oldconfig.get("rabbit.toast", "0.26"));
		config.set("rabbit.white", oldconfig.get("rabbit.white", "0.26"));
		config.set("ravager", oldconfig.get("ravager", "0.25"));
		config.set("salmon", oldconfig.get("salmon", "0.10"));
		config.set("sheep.black", oldconfig.get("sheep.black", "0.0175"));
		config.set("sheep.blue", oldconfig.get("sheep.blue", "0.0175"));
		config.set("sheep.brown", oldconfig.get("sheep.brown", "0.0175"));
		config.set("sheep.cyan", oldconfig.get("sheep.cyan", "0.0175"));
		config.set("sheep.gray", oldconfig.get("sheep.gray", "0.0175"));
		config.set("sheep.green", oldconfig.get("sheep.green", "0.0175"));
		config.set("sheep.jeb_", oldconfig.get("sheep.jeb_", "0.0175"));
		config.set("sheep.light_blue", oldconfig.get("sheep.light_blue", "0.0175"));
		config.set("sheep.light_gray", oldconfig.get("sheep.light_gray", "0.0175"));
		config.set("sheep.lime", oldconfig.get("sheep.lime", "0.0175"));
		config.set("sheep.magenta", oldconfig.get("sheep.magenta", "0.0175"));
		config.set("sheep.orange", oldconfig.get("sheep.orange", "0.0175"));
		config.set("sheep.pink", oldconfig.get("sheep.pink", "0.0175"));
		config.set("sheep.purple", oldconfig.get("sheep.purple", "0.0175"));
		config.set("sheep.red", oldconfig.get("sheep.red", "0.0175"));
		config.set("sheep.white", oldconfig.get("sheep.white", "0.0175"));
		config.set("sheep.yellow", oldconfig.get("sheep.yellow", "0.0175"));
		config.set("shulker", oldconfig.get("shulker", "0.05"));
		config.set("silverfish", oldconfig.get("silverfish", "0.05"));
		config.set("skeleton", oldconfig.get("skeleton", "0.025"));
		config.set("skeleton_horse", oldconfig.get("skeleton_horse", "0.20"));
		config.set("slime", oldconfig.get("slime", "0.005"));
		config.set("snowman", oldconfig.get("snowman", "0.05"));
		config.set("spider", oldconfig.get("spider", "0.005"));
		config.set("squid", oldconfig.get("squid", "0.05"));
		config.set("stray", oldconfig.get("stray", "0.06"));
		config.set("strider", oldconfig.get("strider", "0.10"));
		config.set("trader_llama.brown", oldconfig.get("trader_llama.brown", "0.24"));
		config.set("trader_llama.creamy", oldconfig.get("trader_llama.creamy", "0.24"));
		config.set("trader_llama.gray", oldconfig.get("trader_llama.gray", "0.24"));
		config.set("trader_llama.white", oldconfig.get("trader_llama.white", "0.24"));
		config.set("tropical_fish", oldconfig.get("tropical_fish", "0.10"));
		config.set("turtle", oldconfig.get("turtle", "0.10"));
		config.set("vex", oldconfig.get("vex", "0.10"));
		config.set("villager.desert.armorer", oldconfig.get("villager.desert.armorer", "1.00"));
		config.set("villager.desert.butcher", oldconfig.get("villager.desert.butcher", "1.00"));
		config.set("villager.desert.cartographer", oldconfig.get("villager.desert.cartographer", "1.00"));
		config.set("villager.desert.cleric", oldconfig.get("villager.desert.cleric", "1.00"));
		config.set("villager.desert.farmer", oldconfig.get("villager.desert.farmer", "1.00"));
		config.set("villager.desert.fisherman", oldconfig.get("villager.desert.fisherman", "1.00"));
		config.set("villager.desert.fletcher", oldconfig.get("villager.desert.fletcher", "1.00"));
		config.set("villager.desert.leatherworker", oldconfig.get("villager.desert.leatherworker", "1.00"));
		config.set("villager.desert.librarian", oldconfig.get("villager.desert.librarian", "1.00"));
		config.set("villager.desert.mason", oldconfig.get("villager.desert.mason", "1.00"));
		config.set("villager.desert.nitwit", oldconfig.get("villager.desert.nitwit", "1.00"));
		config.set("villager.desert.none", oldconfig.get("villager.desert.none", "1.00"));
		config.set("villager.desert.shepherd", oldconfig.get("villager.desert.shepherd", "1.00"));
		config.set("villager.desert.toolsmith", oldconfig.get("villager.desert.toolsmith", "1.00"));
		config.set("villager.desert.weaponsmith", oldconfig.get("villager.desert.weaponsmith", "1.00"));
		config.set("villager.jungle.armorer", oldconfig.get("villager.jungle.armorer", "1.00"));
		config.set("villager.jungle.butcher", oldconfig.get("villager.jungle.butcher", "1.00"));
		config.set("villager.jungle.cartographer", oldconfig.get("villager.jungle.cartographer", "1.00"));
		config.set("villager.jungle.cleric", oldconfig.get("villager.jungle.cleric", "1.00"));
		config.set("villager.jungle.farmer", oldconfig.get("villager.jungle.farmer", "1.00"));
		config.set("villager.jungle.fisherman", oldconfig.get("villager.jungle.fisherman", "1.00"));
		config.set("villager.jungle.fletcher", oldconfig.get("villager.jungle.fletcher", "1.00"));
		config.set("villager.jungle.leatherworker", oldconfig.get("villager.jungle.leatherworker", "1.00"));
		config.set("villager.jungle.librarian", oldconfig.get("villager.jungle.librarian", "1.00"));
		config.set("villager.jungle.mason", oldconfig.get("villager.jungle.mason", "1.00"));
		config.set("villager.jungle.nitwit", oldconfig.get("villager.jungle.nitwit", "1.00"));
		config.set("villager.jungle.none", oldconfig.get("villager.jungle.none", "1.00"));
		config.set("villager.jungle.shepherd", oldconfig.get("villager.jungle.shepherd", "1.00"));
		config.set("villager.jungle.toolsmith", oldconfig.get("villager.jungle.toolsmith", "1.00"));
		config.set("villager.jungle.weaponsmith", oldconfig.get("villager.jungle.weaponsmith", "1.00"));
		config.set("villager.plains.armorer", oldconfig.get("villager.plains.armorer", "1.00"));
		config.set("villager.plains.butcher", oldconfig.get("villager.plains.butcher", "1.00"));
		config.set("villager.plains.cartographer", oldconfig.get("villager.plains.cartographer", "1.00"));
		config.set("villager.plains.cleric", oldconfig.get("villager.plains.cleric", "1.00"));
		config.set("villager.plains.farmer", oldconfig.get("villager.plains.farmer", "1.00"));
		config.set("villager.plains.fisherman", oldconfig.get("villager.plains.fisherman", "1.00"));
		config.set("villager.plains.fletcher", oldconfig.get("villager.plains.fletcher", "1.00"));
		config.set("villager.plains.leatherworker", oldconfig.get("villager.plains.leatherworker", "1.00"));
		config.set("villager.plains.librarian", oldconfig.get("villager.plains.librarian", "1.00"));
		config.set("villager.plains.mason", oldconfig.get("villager.plains.mason", "1.00"));
		config.set("villager.plains.nitwit", oldconfig.get("villager.plains.nitwit", "1.00"));
		config.set("villager.plains.none", oldconfig.get("villager.plains.none", "1.00"));
		config.set("villager.plains.shepherd", oldconfig.get("villager.plains.shepherd", "1.00"));
		config.set("villager.plains.toolsmith", oldconfig.get("villager.plains.toolsmith", "1.00"));
		config.set("villager.plains.weaponsmith", oldconfig.get("villager.plains.weaponsmith", "1.00"));
		config.set("villager.savanna.armorer", oldconfig.get("villager.savanna.armorer", "1.00"));
		config.set("villager.savanna.butcher", oldconfig.get("villager.savanna.butcher", "1.00"));
		config.set("villager.savanna.cartographer", oldconfig.get("villager.savanna.cartographer", "1.00"));
		config.set("villager.savanna.cleric", oldconfig.get("villager.savanna.cleric", "1.00"));
		config.set("villager.savanna.farmer", oldconfig.get("villager.savanna.farmer", "1.00"));
		config.set("villager.savanna.fisherman", oldconfig.get("villager.savanna.fisherman", "1.00"));
		config.set("villager.savanna.fletcher", oldconfig.get("villager.savanna.fletcher", "1.00"));
		config.set("villager.savanna.leatherworker", oldconfig.get("villager.savanna.leatherworker", "1.00"));
		config.set("villager.savanna.librarian", oldconfig.get("villager.savanna.librarian", "1.00"));
		config.set("villager.savanna.mason", oldconfig.get("villager.savanna.mason", "1.00"));
		config.set("villager.savanna.nitwit", oldconfig.get("villager.savanna.nitwit", "1.00"));
		config.set("villager.savanna.none", oldconfig.get("villager.savanna.none", "1.00"));
		config.set("villager.savanna.shepherd", oldconfig.get("villager.savanna.shepherd", "1.00"));
		config.set("villager.savanna.toolsmith", oldconfig.get("villager.savanna.toolsmith", "1.00"));
		config.set("villager.savanna.weaponsmith", oldconfig.get("villager.savanna.weaponsmith", "1.00"));
		config.set("villager.snow.armorer", oldconfig.get("villager.snow.armorer", "1.00"));
		config.set("villager.snow.butcher", oldconfig.get("villager.snow.butcher", "1.00"));
		config.set("villager.snow.cartographer", oldconfig.get("villager.snow.cartographer", "1.00"));
		config.set("villager.snow.cleric", oldconfig.get("villager.snow.cleric", "1.00"));
		config.set("villager.snow.farmer", oldconfig.get("villager.snow.farmer", "1.00"));
		config.set("villager.snow.fisherman", oldconfig.get("villager.snow.fisherman", "1.00"));
		config.set("villager.snow.fletcher", oldconfig.get("villager.snow.fletcher", "1.00"));
		config.set("villager.snow.leatherworker", oldconfig.get("villager.snow.leatherworker", "1.00"));
		config.set("villager.snow.librarian", oldconfig.get("villager.snow.librarian", "1.00"));
		config.set("villager.snow.mason", oldconfig.get("villager.snow.mason", "1.00"));
		config.set("villager.snow.nitwit", oldconfig.get("villager.snow.nitwit", "1.00"));
		config.set("villager.snow.none", oldconfig.get("villager.snow.none", "1.00"));
		config.set("villager.snow.shepherd", oldconfig.get("villager.snow.shepherd", "1.00"));
		config.set("villager.snow.toolsmith", oldconfig.get("villager.snow.toolsmith", "1.00"));
		config.set("villager.snow.weaponsmith", oldconfig.get("villager.snow.weaponsmith", "1.00"));
		config.set("villager.swamp.armorer", oldconfig.get("villager.swamp.armorer", "1.00"));
		config.set("villager.swamp.butcher", oldconfig.get("villager.swamp.butcher", "1.00"));
		config.set("villager.swamp.cartographer", oldconfig.get("villager.swamp.cartographer", "1.00"));
		config.set("villager.swamp.cleric", oldconfig.get("villager.swamp.cleric", "1.00"));
		config.set("villager.swamp.farmer", oldconfig.get("villager.swamp.farmer", "1.00"));
		config.set("villager.swamp.fisherman", oldconfig.get("villager.swamp.fisherman", "1.00"));
		config.set("villager.swamp.fletcher", oldconfig.get("villager.swamp.fletcher", "1.00"));
		config.set("villager.swamp.leatherworker", oldconfig.get("villager.swamp.leatherworker", "1.00"));
		config.set("villager.swamp.librarian", oldconfig.get("villager.swamp.librarian", "1.00"));
		config.set("villager.swamp.mason", oldconfig.get("villager.swamp.mason", "1.00"));
		config.set("villager.swamp.nitwit", oldconfig.get("villager.swamp.nitwit", "1.00"));
		config.set("villager.swamp.none", oldconfig.get("villager.swamp.none", "1.00"));
		config.set("villager.swamp.shepherd", oldconfig.get("villager.swamp.shepherd", "1.00"));
		config.set("villager.swamp.toolsmith", oldconfig.get("villager.swamp.toolsmith", "1.00"));
		config.set("villager.swamp.weaponsmith", oldconfig.get("villager.swamp.weaponsmith", "1.00"));
		config.set("villager.taiga.armorer", oldconfig.get("villager.taiga.armorer", "1.00"));
		config.set("villager.taiga.butcher", oldconfig.get("villager.taiga.butcher", "1.00"));
		config.set("villager.taiga.cartographer", oldconfig.get("villager.taiga.cartographer", "1.00"));
		config.set("villager.taiga.cleric", oldconfig.get("villager.taiga.cleric", "1.00"));
		config.set("villager.taiga.farmer", oldconfig.get("villager.taiga.farmer", "1.00"));
		config.set("villager.taiga.fisherman", oldconfig.get("villager.taiga.fisherman", "1.00"));
		config.set("villager.taiga.fletcher", oldconfig.get("villager.taiga.fletcher", "1.00"));
		config.set("villager.taiga.leatherworker", oldconfig.get("villager.taiga.leatherworker", "1.00"));
		config.set("villager.taiga.librarian", oldconfig.get("villager.taiga.librarian", "1.00"));
		config.set("villager.taiga.mason", oldconfig.get("villager.taiga.mason", "1.00"));
		config.set("villager.taiga.nitwit", oldconfig.get("villager.taiga.nitwit", "1.00"));
		config.set("villager.taiga.none", oldconfig.get("villager.taiga.none", "1.00"));
		config.set("villager.taiga.shepherd", oldconfig.get("villager.taiga.shepherd", "1.00"));
		config.set("villager.taiga.toolsmith", oldconfig.get("villager.taiga.toolsmith", "1.00"));
		config.set("villager.taiga.weaponsmith", oldconfig.get("villager.taiga.weaponsmith", "1.00"));
		config.set("vindicator", oldconfig.get("vindicator", "0.05"));
		config.set("wandering_trader", oldconfig.get("wandering_trader", "1.00"));
		config.set("warden", oldconfig.get("warden", "1.00"));
		config.set("witch", oldconfig.get("witch", "0.005"));
		config.set("wither", oldconfig.get("wither", "1.00"));
		config.set("wither_skeleton", oldconfig.get("wither_skeleton", "0.025"));
		config.set("wolf", oldconfig.get("wolf", "0.02"));
		config.set("zoglin", oldconfig.get("zoglin", "0.20"));
		config.set("zombie", oldconfig.get("zombie", "0.025"));
		config.set("zombie_horse", oldconfig.get("zombie_horse", "1.00"));
		config.set("zombie_pigman", oldconfig.get("zombie_pigman", "0.005"));
		config.set("zombified_piglin", oldconfig.get("zombified_piglin", "0.005"));
		config.set("zombie_villager", oldconfig.get("zombie_villager", "0.50"));
		try {
			config.save(file);
		} catch (IOException e) {
			stacktraceInfo();
			e.printStackTrace();
		}
		
	}
	
	public void stacktraceInfo(){
		logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " Include this with the stacktrace when reporting issues.");
		logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " This server is running " + Bukkit.getName() + " version " + Bukkit.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ")");
		logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " vardebug=" + debug + " debug=" + getConfig().get("debug","error") + " in " + this.getDataFolder() + "/config.yml");
		logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " jarfile name=" + this.getFile().getAbsoluteFile());
		debug = true;
		logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " DEBUG has been set as true until plugin reload or /mmh td, or /mmh reload.");
	}
	public static void stacktraceInfoStatic(){
		logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " Include this with the stacktrace when reporting issues.");
		logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " This server is running " + Bukkit.getName() + " version " + Bukkit.getVersion() + " (Implementing API version " + Bukkit.getBukkitVersion() + ")");
		logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " vardebug=" + debug);
		debug = true;
		logger.info(pdfFile.getName() + " v" + pdfFile.getVersion() + " DEBUG has been set as true until plugin reload or /mmh td, or /mmh reload.");
	}
	
	private final NamespacedKey NAME_KEY = new NamespacedKey(this, "head_name");
    private final NamespacedKey LORE_KEY = new NamespacedKey(this, "head_lore");
    private final PersistentDataType<String,String[]> LORE_PDT = new JsonDataType<>(String[].class);
    //@SuppressWarnings("unused")
	//private final PersistentDataType LORE_PDT2 = new JsonDataType<>(String.class);
    //private final NamespacedKey DISPLAY_KEY = new NamespacedKey(this, "head_display");
    //private final NamespacedKey SKULLOWNER_KEY = new NamespacedKey(this, "head_skullowner");
    
    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        @Nonnull ItemStack headItem = event.getItemInHand();
        if (headItem.getType() != Material.PLAYER_HEAD) return;
        ItemMeta meta = headItem.getItemMeta();
        if (meta == null) return;
        @Nonnull String name = meta.getDisplayName();
        @Nullable List<String> lore = meta.getLore();
        @Nonnull Block block = event.getBlockPlaced();
        // NOTE: Not using snapshots is broken: https://github.com/PaperMC/Paper/issues/3913
        BlockStateSnapshotResult blockStateSnapshotResult = PaperLib.getBlockState(block, true);
        TileState skullState = (TileState) blockStateSnapshotResult.getState();
        @Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
        skullPDC.set(NAME_KEY, PersistentDataType.STRING, name);
        if (lore != null) skullPDC.set(LORE_KEY, LORE_PDT, lore.toArray(new String[0]));
        if (blockStateSnapshotResult.isSnapshot()) skullState.update();
    }

    @EventHandler
    public void onBlockDropItemEvent(BlockDropItemEvent event) {
        @Nonnull BlockState blockState = event.getBlockState();
        if (blockState.getType() != Material.PLAYER_HEAD) return;
        TileState skullState = (TileState) blockState;
        @Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
        @Nullable String name = skullPDC.get(NAME_KEY, PersistentDataType.STRING);
        @Nullable String[] lore = skullPDC.get(LORE_KEY, LORE_PDT);
        if (name == null) return;
        for (Item item: event.getItems()) { // Ideally should only be one...
            @Nonnull ItemStack itemstack = item.getItemStack();
            if (itemstack.getType() == Material.PLAYER_HEAD) {
                @Nullable ItemMeta meta = itemstack.getItemMeta();
                if (meta == null) continue; // This shouldn't happen
                meta.setDisplayName(name);
                if (lore != null) meta.setLore(Arrays.asList(lore));
                itemstack.setItemMeta(meta);
            }
        }

    }
    
	@EventHandler()
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
    	Block block = event.getBlock();
    	Location loc = block.getLocation();
    	@Nonnull BlockState blockState = block.getState();
        Material blockType = blockState.getType();
        if (blockType != Material.PLAYER_HEAD && blockType != Material.PLAYER_WALL_HEAD) return;
        TileState skullState = (TileState) blockState;
        @Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
        @Nullable String name = skullPDC.get(NAME_KEY, PersistentDataType.STRING);
        @Nullable String[] lore = skullPDC.get(LORE_KEY, LORE_PDT);
        if (name == null) return;
    	Collection<ItemStack> drops = block.getDrops();
		ItemStack[] stackArray = drops.toArray(new ItemStack[0]);
    	@Nonnull ItemStack itemstack = stackArray[0];
        if (itemstack.getType() == Material.PLAYER_HEAD) {
            @Nullable ItemMeta meta = itemstack.getItemMeta();
            if (meta == null) {
            	logWarn("meta=null");
            	return; // This shouldn't happen
            }
            meta.setDisplayName(name);
            if (lore != null) meta.setLore(Arrays.asList(lore));
            itemstack.setItemMeta(meta);
            
        	block.getWorld().dropItemNaturally(block.getLocation(), itemstack);
        	block.getDrops().clear();
        	block.setType(Material.AIR);
        }

        BlockState blockS = block.getWorld().getBlockAt(loc).getState();
        blockS.update(true, true);
    }
	
    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event) {
        Block block = event.getToBlock();
        Location loc = block.getLocation();
        
        @Nonnull BlockState blockState = block.getState();
        Material blockType = blockState.getType();
        if (blockType != Material.PLAYER_HEAD && blockType != Material.PLAYER_WALL_HEAD) return;
        TileState skullState = (TileState) blockState;
        @Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
        @Nullable String name = skullPDC.get(NAME_KEY, PersistentDataType.STRING);
        @Nullable String[] lore = skullPDC.get(LORE_KEY, LORE_PDT);
        if (name == null) return;
        Collection<ItemStack> drops = block.getDrops();
        ItemStack[] stackArray = drops.toArray(new ItemStack[0]);
        @Nonnull ItemStack itemstack = stackArray[0];
        if (itemstack.getType() == Material.PLAYER_HEAD) {
        	@Nullable ItemMeta meta = itemstack.getItemMeta();
        	if (meta == null) return; // This shouldn't happen
        	meta.setDisplayName(name);
        	if (lore != null) meta.setLore(Arrays.asList(lore));
        	itemstack.setItemMeta(meta);
        	
        	block.getWorld().dropItemNaturally(block.getLocation(), itemstack);
        	block.getDrops().clear();
        	block.setType(Material.AIR);
        	event.setCancelled(true);
        }

        BlockState blockS = block.getWorld().getBlockAt(loc).getState();
        blockS.update(true, true);
    }
    
    @SuppressWarnings("unused")
	public ItemStack fixHeadStack(ItemStack offHand, ItemStack mainHand){
    	NBTItem nbti = new NBTItem(offHand);
    	Set<String> SkullKeys = nbti.getKeys();
    	int damage = nbti.getInteger("Damage");
    	NBTCompound display = nbti.getCompound("display");
    	NBTCompound SkullOwner = nbti.getCompound("SkullOwner");
    	
    	NBTItem nbti2 = new NBTItem(mainHand);
    	Set<String> SkullKeys2 = nbti2.getKeys();
    	int damage2 = nbti2.getInteger("Damage");
    	NBTCompound display2 = nbti2.getCompound("display");
    	NBTCompound SkullOwner2 = nbti2.getCompound("SkullOwner");
        if( display.equals(display2) && SkullOwner.equals(SkullOwner2) && damage != damage2){
        	ItemStack is = new ItemStack(offHand);
        	is.setAmount(mainHand.getAmount());
        	return is;
        }else if( display.equals(display2) && SkullOwner.equals(SkullOwner2) && damage == damage2){
        	return mainHand;
        }
        return null;
	}
    public ItemStack fixHeadNBT(String textureValue, String displayName, ArrayList<String> lore) {
    	//String textureValue = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWY1MjQxNjZmN2NlODhhNTM3MTU4NzY2YTFjNTExZTMyMmE5M2E1ZTExZGJmMzBmYTZlODVlNzhkYTg2MWQ4In19fQ=="; // Pulled from the head link, scroll to the bottom and the "Other Value" field has this texture id.

    	ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1); // Creating the ItemStack, your input may vary.
    	NBTItem nbti = new NBTItem(head); // Creating the wrapper.

    	NBTCompound disp = nbti.addCompound("display");
    	disp.setString("Name", displayName); // Setting the name of the Item
    	if(lore.isEmpty()) {
    		if(getConfig().getBoolean("lore.show_plugin_name", true)){
				lore.add(ChatColor.AQUA + "MoreMobHeads");
			}
    	}
    	if(!lore.isEmpty()) {
	    	NBTList l = disp.getStringList("Lore");
	    	l.add(lore); // Adding a bit of lore.
    	}
    	
    	NBTCompound skull = nbti.addCompound("SkullOwner"); // Getting the compound, that way we can set the skin information
    	skull.setString("Name", displayName); // Owner's name
    	//skull.setString("Id", uuid);
    	// The UUID, note that skulls with the same UUID but different textures will misbehave and only one texture will load
    	// (They'll share it), if skulls have different UUIDs and same textures they won't stack. See UUID.randomUUID();

    	NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
    	texture.setString("Value",  textureValue);

    	head = nbti.getItem(); // Refresh the ItemStack
    	return head;
    }
}
