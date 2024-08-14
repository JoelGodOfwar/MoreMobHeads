package com.github.joelgodofwar.mmh;
//1.14

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.TileState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Goat;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.TropicalFish.Pattern;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.json.JSONObject;

import com.earth2me.essentials.Essentials;
import com.github.joelgodofwar.mmh.common.PluginLibrary;
import com.github.joelgodofwar.mmh.common.PluginLogger;
import com.github.joelgodofwar.mmh.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.mmh.common.error.Report;
import com.github.joelgodofwar.mmh.enums.CatHeads;
import com.github.joelgodofwar.mmh.enums.HorseHeads;
import com.github.joelgodofwar.mmh.enums.LlamaHeads;
import com.github.joelgodofwar.mmh.enums.MobHeads;
import com.github.joelgodofwar.mmh.enums.MobHeads117;
import com.github.joelgodofwar.mmh.enums.RabbitHeads;
import com.github.joelgodofwar.mmh.enums.SheepHeads;
import com.github.joelgodofwar.mmh.enums.TropicalFishHeads;
import com.github.joelgodofwar.mmh.enums.VillagerHeads;
import com.github.joelgodofwar.mmh.enums.ZombieVillagerHeads;
import com.github.joelgodofwar.mmh.handlers.EventHandler_1_20_R1;
import com.github.joelgodofwar.mmh.handlers.EventHandler_1_20_R2;
import com.github.joelgodofwar.mmh.i18n.Translator;
import com.github.joelgodofwar.mmh.util.Ansi;
import com.github.joelgodofwar.mmh.util.ChatColorUtils;
import com.github.joelgodofwar.mmh.util.Format;
import com.github.joelgodofwar.mmh.util.SkinUtils;
import com.github.joelgodofwar.mmh.util.StrUtils;
import com.github.joelgodofwar.mmh.util.Utils;
import com.github.joelgodofwar.mmh.util.Version;
import com.github.joelgodofwar.mmh.util.VersionChecker;
import com.github.joelgodofwar.mmh.util.YmlConfiguration;
import com.github.joelgodofwar.mmh.util.datatypes.JsonDataType;
import com.github.joelgodofwar.mmh.util.mob.NameTag;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTList;
import de.tr7zw.changeme.nbtapi.NBTListCompound;
import dev.majek.hexnicks.HexNicks;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
public class MoreMobHeads extends JavaPlugin implements Listener{
	/** Languages: čeština (cs_CZ), Deutsch (de_DE), English (en_US), Español (es_ES), Español (es_MX), Français (fr_FR), Italiano (it_IT), Magyar (hu_HU), 日本語 (ja_JP), 한국어 (ko_KR), Lolcat (lol_US), Melayu (my_MY), Nederlands (nl_NL), Polski (pl_PL), Português (pt_BR), Русский (ru_RU), Svenska (sv_SV), Türkçe (tr_TR), 中文(简体) (zh_CN), 中文(繁體) (zh_TW) */
	//public final static Logger logger = Logger.getLogger("Minecraft");
	static String THIS_NAME;
	static String THIS_VERSION;
	/** update checker variables */
	public int projectID = 73997; // https://spigotmc.org/resources/71236
	public String githubURL = "https://github.com/JoelGodOfwar/MoreMobHeads/raw/master/versioncheck/1.20/versions.xml";
	boolean UpdateAvailable =  false;
	public String UColdVers;
	public String UCnewVers;
	public boolean UpdateCheck;
	public String DownloadLink = "https://dev.bukkit.org/projects/moremobheads2";
	/** end update checker variables */
	public boolean isDev = false;
	public boolean debug = false;
	public static String daLang;
	//String updateURL = "https://github.com/JoelGodOfwar/MoreMobHeads/raw/master/versioncheck/1.14/version.txt";
	//File langFile;
	//public FileConfiguration lang;
	File langNameFile;
	public FileConfiguration langName;
	public File playerFile;
	public FileConfiguration playerHeads;
	File blockFile;
	File blockFile116;
	File blockFile1162;
	File blockFile117;
	File blockFile119;
	File blockFile120;
	public FileConfiguration blockHeads  = new YamlConfiguration();
	public FileConfiguration blockHeads2  = new YamlConfiguration();
	public FileConfiguration blockHeads3  = new YamlConfiguration();
	public FileConfiguration blockHeads4  = new YamlConfiguration();
	public FileConfiguration fileVersions  = new YamlConfiguration();
	public File fileVersionsFile;
	public File customFile;
	public FileConfiguration traderCustom;
	File chanceFile;
	public YmlConfiguration chanceConfig;
	public YmlConfiguration oldchanceConfig;
	public YmlConfiguration beheadingMessages = new YmlConfiguration();
	public YamlConfiguration oldMessages;
	File mobnameFile;
	FileConfiguration mobname;
	double defpercent = 0.013;
	//static boolean showkiller;
	//static boolean showpluginname;
	public YmlConfiguration config = new YmlConfiguration();
	YamlConfiguration oldconfig = new YamlConfiguration();
	static String datafolder;
	public String world_whitelist;
	public String world_blacklist;
	public String mob_whitelist;
	public String mob_blacklist;
	boolean colorful_console;
	boolean silent_console;
	public final NamespacedKey NAMETAG_KEY = new NamespacedKey(this, "name_tag");
	public final NamespacedKey SHIVERING_KEY = new NamespacedKey(this, "shivering_tag");
	public Map<UUID, UUID> entityPlayers = new HashMap<>();      // Entity UUID, Player UUID
	public Map<UUID, ItemStack> playerWeapons = new HashMap<>(); // Player UUID, Main hand item
	public Map<UUID, UUID> endCrystals = new HashMap<>();
	File debugFile;
	Random random = new Random();
	String pluginName = THIS_NAME;
	Translator lang2;
	HashMap<String, String> namedTropicalFish = new HashMap<>();
	private Map<Player, Random> chanceRandoms = new HashMap<>();
	public List<ItemStack> blockhead_list = new ArrayList<ItemStack>();
	public static DetailedErrorReporter reporter;
	public String jarfilename = this.getFile().getAbsoluteFile().toString();
	public PluginLogger LOGGER;

	// Persistent Heads
	public final NamespacedKey NAME_KEY = new NamespacedKey(this, "head_name");
	public final NamespacedKey LORE_KEY = new NamespacedKey(this, "head_lore");
	public final NamespacedKey UUID_KEY = new NamespacedKey(this, "head_uuid");
	public final NamespacedKey TEXTURE_KEY = new NamespacedKey(this, "head_texture");
	public final NamespacedKey SOUND_KEY = new NamespacedKey(this, "head_sound");
	public final PersistentDataType<String,String[]> LORE_PDT = new JsonDataType<>(String[].class);

	public Version minConfigVersion = new Version("1.0.24");
	public Version minMessagesVersion = new Version("1.0.2");
	public Version minChanceVersion = new Version("1.0.28");
	public Version minLangVersion = new Version("1.0.4");
	public Version minBlock117Version = new Version("1.0.1");
	public Version minBlock1172Version = new Version("1.0.1");
	public Version minBlock120Version = new Version("1.0.2");
	public Version minPlayerVersion = new Version("1.0.1");
	public Version minCustomVersion = new Version("1.0.0");

	@SuppressWarnings("unused") @Override // TODO: onEnable
	public void onEnable(){

		long startTime = System.currentTimeMillis();
		LOGGER = new PluginLogger(this);
		reporter = new DetailedErrorReporter(this);
		UpdateCheck = getConfig().getBoolean("plugin_settings.auto_update_check", true);
		debug = getConfig().getBoolean("plugin_settings.debug", false);
		daLang = getConfig().getString("plugin_settings.lang", "en_US");
		oldconfig = new YamlConfiguration();
		oldMessages = new YamlConfiguration();
		lang2 = new Translator(daLang, getDataFolder().toString());
		THIS_NAME = this.getDescription().getName();
		THIS_VERSION = this.getDescription().getVersion();
		if(!getConfig().getBoolean("global_settings.console.longpluginname", true)) {
			pluginName = "MMH";
		}else {
			pluginName = THIS_NAME;
		}

		datafolder = this.getDataFolder().toString();
		colorful_console = getConfig().getBoolean("global_settings.console.colorful_console", true);
		silent_console = getConfig().getBoolean("global_settings.console.silent_console", false);

		try{
			LOGGER.log(ChatColor.YELLOW + "**************************************" + ChatColor.RESET);
			LOGGER.log(ChatColor.GREEN + " v" + THIS_VERSION + ChatColor.RESET + " Loading...");
			LOGGER.log("Server Version: " + getServer().getVersion().toString());

			// Handle unexpected Minecraft versions
			Version checkVersion = this.verifyMinecraftVersion();

			/** DEV check **/
			File jarfile = this.getFile().getAbsoluteFile();
			if(jarfile.toString().contains("-DEV")){
				debug = true;
				LOGGER.warn(ChatColor.RED + "Jar file contains -DEV, debug set to true" + ChatColor.RESET);
				LOGGER.warn(ChatColor.RED + "jarfilename = " + StrUtils.Right(jarfilename, jarfilename.length() - jarfilename.lastIndexOf(File.separatorChar)) + ChatColor.RESET);
				//log("jarfile contains dev, debug set to true.");
			}

			/** Version Check */
			if( !(Double.parseDouble( getMCVersion().substring(0, 4) ) >= 1.14) ){
				// !getMCVersion().startsWith("1.14")&&!getMCVersion().startsWith("1.15")&&!getMCVersion().startsWith("1.16")&&!getMCVersion().startsWith("1.17")
				LOGGER.warn(ChatColor.RED + " *!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!" + ChatColor.RESET);
				LOGGER.warn(ChatColor.RED + " " + get("mmh.message.server_not_version") + ChatColor.RESET);
				LOGGER.warn(ChatColor.RED + "  v" + THIS_VERSION + " disabling." + ChatColor.RESET);
				LOGGER.warn(ChatColor.RED + " *!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!" + ChatColor.RESET);
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}

			// Make sure directory exists and files exist.
			checkDirectories();
			LOGGER.log("Loading file version checker...");
			fileVersionsFile = new File(getDataFolder() + "" + File.separatorChar + "fileVersions.yml");
			try {
				fileVersions.load(fileVersionsFile);
			} catch (Exception exception) {
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_FILEVERSION).error(exception));
			}
			// Check if Config needs update.
			checkConfig();
			// Check if MEssages needs update.
			checkMessages();
			// Check if MiniBlocks needs update,
			checkMiniBlocks();
			// Check if Chance needs update.
			checkChance();
			// Check if Lang needs update.
			checkLang();







			world_whitelist = config.getString("global_settings.world.whitelist", "");
			world_blacklist = config.getString("global_settings.world.blacklist", "");
			mob_whitelist = config.getString("head_settings.mob_heads.whitelist", "");
			mob_blacklist = config.getString("head_settings.mob_heads.blacklist", "");

			getServer().getPluginManager().registerEvents(this, this);

			//String jarfilename = this.getFile().getAbsoluteFile().toString();
			LOGGER.debug("-<[ PLEASE INCLUDE THIS WITH ANY ISSUE REPORTS ]>-");
			LOGGER.debug("This server is running " + Bukkit.getName() + " version " + Bukkit.getVersion() + " (API version " + Bukkit.getBukkitVersion() + ")");
			LOGGER.debug("vardebug= " + debug + " debug=" + config.getString("plugin_settings.debug","error").toUpperCase() + " in " + this.getDataFolder() + File.separatorChar + "config.yml");
			LOGGER.debug("jarfilename= " + StrUtils.Right(jarfilename, jarfilename.length() - jarfilename.lastIndexOf(File.separatorChar)));
			LOGGER.debug("-<[ PLEASE INCLUDE THIS WITH ANY ISSUE REPORTS ]>-");

			/** Register EventHandler */
			String packageName = this.getServer().getClass().getPackage().getName();
			Version version = new Version(Bukkit.getServer());
			LOGGER.debug("version=" + version);
			Version min = new Version("1.21");
			LOGGER.debug("version.atOrAbove(min) = " + version.atOrAbove(min));
			if( version.isBetween("1.20", "1.20.4") ){
				getServer().getPluginManager().registerEvents( new EventHandler_1_20_R1(this), this);
				//getCommand("mmh").setExecutor(new EventHandler_1_17_R1(this));
			}else if( version.isBetween("1.20.5","1.20.6") ){
				getServer().getPluginManager().registerEvents( new EventHandler_1_20_R2(this), this);
				//getCommand("mmh").setExecutor(new EventHandler_1_17_R1(this));
			}else if( version.isAtLeast(min) ){
				getServer().getPluginManager().registerEvents( new EventHandler_1_20_R2(this), this);
				//getCommand("mmh").setExecutor(new EventHandler_1_17_R1(this));
			}else{
				LOGGER.warn("Not compatible with this version of Minecraft:" + version);
				getServer().getPluginManager().disablePlugin(this);
			}

			namedTropicalFish.put("STRIPEY-ORANGE-GRAY", "ANEMONE");
			namedTropicalFish.put("FLOPPER-GRAY-GRAY", "BLACK_TANG");
			namedTropicalFish.put("FLOPPER-GRAY-BLUE", "BLUE_TANG");
			namedTropicalFish.put("CLAYFISH-WHITE-GRAY", "BUTTERFLYFISH");
			namedTropicalFish.put("SUNSTREAK-BLUE-GRAY", "CICHLID");
			namedTropicalFish.put("KOB-ORANGE-WHITE", "CLOWNFISH");
			namedTropicalFish.put("SPOTTY-PINK-LIGHT_BLUE", "COTTON_CANDY_BETTA");
			namedTropicalFish.put("BLOCKFISH-PURPLE-YELLOW", "DOTTYBACK");
			namedTropicalFish.put("CLAYFISH-WHITE-RED", "EMPEROR_RED_SNAPPER");
			namedTropicalFish.put("SPOTTY-WHITE-YELLOW", "GOATFISH");
			namedTropicalFish.put("GLITTER-WHITE-GRAY", "MOORISH_IDOL");
			namedTropicalFish.put("CLAYFISH-WHITE-ORANGE", "ORNATE_BUTTERFLYFISH");
			namedTropicalFish.put("DASHER-CYAN-PINK", "PARROTFISH");
			namedTropicalFish.put("BRINELY-LIME-LIGHT_BLUE", "QUEEN_ANGELFISH");
			namedTropicalFish.put("BETTY-RED-WHITE", "RED_CICHLID");
			namedTropicalFish.put("SNOOPER-GRAY-RED", "RED_LIPPED_BLENNY");
			namedTropicalFish.put("BLOCKFISH-RED-WHITE", "RED_SNAPPER");
			namedTropicalFish.put("KOB-RED-WHITE", "TOMATO_CLOWNFISH");
			namedTropicalFish.put("FLOPPER-WHITE-YELLOW", "THREADFIN");
			namedTropicalFish.put("SUNSTREAK-GRAY-WHITE", "TRIGGERFISH");
			namedTropicalFish.put("DASHER-CYAN-YELLOW", "YELLOWTAIL_PARROTFISH");
			namedTropicalFish.put("FLOPPER-YELLOW-YELLOW", "YELLOW_TANG");

			/** Update Checker */
			if(UpdateCheck){
				LOGGER.log("Checking for updates...");
				try {
					VersionChecker updater = new VersionChecker(this, projectID, githubURL);
					if(updater.checkForUpdates()) {
						/** Update available */
						UpdateAvailable = true; // TODO: Update Checker
						UColdVers = updater.oldVersion();
						UCnewVers = updater.newVersion();

						LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
						LOGGER.log("* " + get("mmh.version.message").toString().replace("<MyPlugin>", THIS_NAME) );
						LOGGER.log("* " + get("mmh.version.old_vers") + ChatColor.RED + UColdVers );
						LOGGER.log("* " + get("mmh.version.new_vers") + ChatColor.GREEN + UCnewVers );
						LOGGER.log("*");
						LOGGER.log("* " + get("mmh.version.please_update") );
						LOGGER.log("*");
						LOGGER.log("* " + get("mmh.version.download") + ": " + DownloadLink + "/files");
						LOGGER.log("* " + get("mmh.version.donate") + ": https://ko-fi.com/joelgodofwar");
						LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
					}else{
						/** Up to date */
						LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
						LOGGER.log("* " + get("mmh.version.curvers"));
						LOGGER.log("* " + get("mmh.version.donate") + ": https://ko-fi.com/joelgodofwar");
						LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
						UpdateAvailable = false;
					}
				}catch(Exception exception) {
					//exception.printStackTrace();
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_UPDATE_PLUGIN).error(exception));
				}
			}else {
				/** auto_update_check is false so nag. */
				LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
				LOGGER.log("* " + get("mmh.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
				LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
			}
			/** end update checker */



			consoleInfo("Enabled - Loading took " + LoadTime(startTime));

			try {
				Metrics metrics	= new Metrics(this, 6128);
				// New chart here
				// myPlugins()
				metrics.addCustomChart(new AdvancedPie("my_other_plugins", new Callable<Map<String, Integer>>() {
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
				metrics.addCustomChart(new AdvancedPie("vanilla_heads", new Callable<Map<String, Integer>>() {
					@Override
					public Map<String, Integer> call() throws Exception {
						Map<String, Integer> valueMap = new HashMap<>();
						//int varTotal = myPlugins();
						valueMap.put("CREEPER " + config.getString("head_settings.mob_heads.vanilla_heads.creeper").toUpperCase(), 1);
						valueMap.put("ENDER_DRAGON " + config.getString("head_settings.mob_heads.vanilla_heads.ender_dragon").toUpperCase(), 1);
						valueMap.put("SKELETON " + config.getString("head_settings.mob_heads.vanilla_heads.skeleton").toUpperCase(), 1);
						valueMap.put("WITHER_SKELETON " + config.getString("head_settings.mob_heads.vanilla_heads.wither_skeleton").toUpperCase(), 1);
						valueMap.put("ZOMBIE " + config.getString("head_settings.mob_heads.vanilla_heads.zombie").toUpperCase(), 1);
						valueMap.put("PIGLIN " + config.getString("head_settings.mob_heads.vanilla_heads.piglin").toUpperCase(), 1);
						return valueMap;
					}
				}));
				metrics.addCustomChart(new SimplePie("auto_update_check", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return "" + config.getString("plugin_settings.auto_update_check").toUpperCase();
					}
				}));
				// add to site
				metrics.addCustomChart(new SimplePie("var_debug", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return "" + config.getString("plugin_settings.debug").toUpperCase();
					}
				}));
				metrics.addCustomChart(new SimplePie("var_lang", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return "" + config.getString("plugin_settings.lang").toUpperCase();
					}
				}));
				metrics.addCustomChart(new SimplePie("whitelist.enforce", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return "" + config.getString("head_settings.player_heads.whitelist.enforce").toUpperCase();
					}
				}));
				metrics.addCustomChart(new SimplePie("blacklist.enforce", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return "" + config.getString("head_settings.player_heads.blacklist.enforce").toUpperCase();
					}
				}));
				metrics.addCustomChart(new SimplePie("custom_wandering_trader", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return "" + config.getString("wandering_trades.custom_wandering_trader").toUpperCase();
					}
				}));
				metrics.addCustomChart(new SimplePie("player_heads", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return "" + config.getString("wandering_trades.player_heads.enabled").toUpperCase();
					}
				}));
				metrics.addCustomChart(new SimplePie("block_heads", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return "" + config.getString("wandering_trades.block_heads.enabled").toUpperCase();
					}
				}));
				metrics.addCustomChart(new SimplePie("custom_trades", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return "" + config.getString("wandering_trades.custom_trades.enabled").toUpperCase();
					}
				}));
				metrics.addCustomChart(new SimplePie("apply_looting", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return "" + config.getString("head_settings.apply_looting").toUpperCase();
					}
				}));
				metrics.addCustomChart(new SimplePie("show_killer", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return "" + config.getString("head_settings.lore.show_killer").toUpperCase();
					}
				}));
				metrics.addCustomChart(new SimplePie("show_plugin_name", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return "" + config.getString("head_settings.lore.show_plugin_name").toUpperCase();
					}
				}));
			} catch (Exception exception) {
				// Handle the exception or log it
				//exception.printStackTrace();
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_METRICS_LOAD_ERROR).error(exception));
			}
			try {
				// Your code here that might cause an exception

				// For testing purposes, you can throw an exception like this:
				//throw new RuntimeException("This is a test exception.");
			} catch (Exception exception) {
				// Handle the exception or log it
				//exception.printStackTrace();
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_UPDATE_PLUGIN).error(exception));
			}
		} catch (Exception exception) {
			//exception.printStackTrace();
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.ERROR_ENABLING_PLUGIN).error(exception));
		}
	}

	@Override // TODO: onDisable
	public void onDisable(){
		String defVer = "0.1.0";
		fileVersions.set("config", config.getString("version", defVer));
		fileVersions.set("messages", beheadingMessages.getString("version", defVer));
		fileVersions.set("player_heads", playerHeads.getString("version", defVer));
		fileVersions.set("custom_trades", traderCustom.getString("custom_trades.version", defVer));
		fileVersions.set("chance_config", chanceConfig.getString("version", defVer));
		fileVersions.set("lang", langName.getString("version", defVer));
		fileVersions.set("block_heads_1_17", blockHeads.getString("version", defVer));
		fileVersions.set("block_heads_1_17_2", blockHeads2.getString("version", defVer));
		fileVersions.set("block_heads_1_20", blockHeads3.getString("version", defVer));
		fileVersionsFile = new File(getDataFolder() + "" + File.separatorChar + "fileVersions.yml");
		try {
			fileVersions.save(fileVersionsFile);
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_SAVE_FILEVERSION).error(exception));
		}
		consoleInfo("Disabled");
	}

	public void consoleInfo(String state) {
		//loading(Ansi.GREEN + "**************************************" + Ansi.RESET);
		loading(ChatColor.YELLOW + " v" + THIS_VERSION + ChatColor.RESET + " is " + state  + ChatColor.RESET);
		//loading(Ansi.GREEN + "**************************************" + Ansi.RESET);
	}

	public void loading(String string) {
		if(!colorful_console) {
			string = Ansi.stripAnsi(string);
			string = ChatColor.stripColor(string);
		}
		LOGGER.log(string);
	}

	public static String getMCVersion() {
		String strVersion = Bukkit.getVersion();
		strVersion = strVersion.substring(strVersion.indexOf("MC: "), strVersion.length());
		strVersion = strVersion.replace("MC: ", "").replace(")", "");
		return strVersion;
	}


	public void giveMobHead(LivingEntity mob, String name){
		UUID uuid = null;
		OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(name);
		if(oPlayer.hasPlayedBefore()) {
			LOGGER.debug("Player hasPlayedBefore, getting local UUID.");
			uuid = oPlayer.getUniqueId();
		}else {
			LOGGER.debug("Player !hasPlayedBefore, getting UUID from Mojang.");
			uuid = UUID.fromString(getPlayerUUID(name));
		}
		if(uuid == null) {
			LOGGER.warn("gPH User doesn't exist or invalid UUID");
			return;
		}
		PlayerProfile profile = Bukkit.createPlayerProfile(uuid, name);
		String texture = APIRequest(uuid.toString(), "sessionProfile", "value");
		LOGGER.debug("gMH UUID = " + uuid.toString());
		LOGGER.debug("gMH textureCoded = \'" + texture + "\'");

		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		PlayerTextures textures = profile.getTextures();
		URL url = null;
		try {
			url = convertBase64ToURL(texture);
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_HEAD_URL_ERROR).error(exception));
		}
		textures.setSkin(url);
		profile.setTextures(textures);
		meta.setOwnerProfile(profile);
		meta.setNoteBlockSound(NamespacedKey.minecraft( "entity.player.hurt" ));
		ArrayList<String> lore = new ArrayList();
		if(config.getBoolean("head_settings.lore.show_plugin_name", true)){
			lore.add(ChatColor.AQUA + THIS_NAME);
		}
		meta.setLore(lore);
		meta.setLore(lore);
		head.setItemMeta(meta);
		if(mob.getEquipment().getHelmet() == null) {
			mob.getEquipment().setHelmet(head);
		}
		head.setItemMeta(meta);
		LOGGER.debug("helmet = " + mob.getEquipment().getHelmet().toString());
		if(mob.getEquipment().getHelmet().getType() == Material.PLAYER_HEAD) {
			// Prevent head duping.
			return;
		}
		if((mob.getEquipment().getHelmet() == null) || (mob.getEquipment().getHelmet().getType() == Material.AIR)) {
			mob.getEquipment().setHelmet(head);
		}else {
			mob.getWorld().dropItemNaturally(mob.getLocation(), head);
		}

		if(getServer().getPluginManager().getPlugin("WildStacker") != null){
			@Nonnull
			PersistentDataContainer pdc = mob.getPersistentDataContainer();
			pdc.set(NAMETAG_KEY, PersistentDataType.STRING, "nametag");
		}
	}

	private String getPlayerUUID(String username) {
		String uuid = APIRequest(username, "userProfile", "id");
		if ((uuid == null) || uuid.equals("error")) {
			return null;
		}
		LOGGER.debug("getPlayerUUID uuid = " + uuid);
		// Format UUID with dashes
		String formattedUUID = String.format(
				"%s-%s-%s-%s-%s",
				uuid.substring(0, 8),
				uuid.substring(8, 12),
				uuid.substring(12, 16),
				uuid.substring(16, 20),
				uuid.substring(20)
				);
		try {
			UUID.fromString(formattedUUID);
		} catch (IllegalArgumentException e) {
			LOGGER.debug("Error with UUID.");
			return null;
		}
		return formattedUUID;
	}

	private String APIRequest(String value, String urlType, String toSearch) {
		String url = "";
		if (urlType.equals("userProfile")) {
			url = "https://api.mojang.com/users/profiles/minecraft/";
		} else if (urlType.equals("sessionProfile")) {
			url = "https://sessionserver.mojang.com/session/minecraft/profile/";
		}

		HttpURLConnection connection = null;
		try {
			URL api = new URL(url + value);
			connection = (HttpURLConnection) api.openConnection();
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuilder response = new StringBuilder();
				for (String responseChar; (responseChar = reader.readLine()) != null; ) {
					response.append(responseChar);
				}
				reader.close();
				JSONObject responseObject = new JSONObject(response.toString());
				// Check if the response contains an error message
				if (responseObject.has("errorMessage")) {
					return "error";
				}
				// Proceed with the existing logic to get texture or ID
				if (!toSearch.equals("id")) {
					return responseObject
							.getJSONArray("properties")
							.getJSONObject(0)
							.getString("value");
				} else {
					return responseObject.getString("id");
				}
			} else {
				LOGGER.debug(String.format(
						"Could not get %s. Response code: %s",
						((toSearch.equals("id")) ? "UUID" : "texture"),
						responseCode
						));
			}
		} catch (MalformedURLException error) {
			LOGGER.debug("An error occurred while trying to access the URL.");
		} catch (IOException error) {
			LOGGER.debug("An error occurred while attempting to connect to the URL.");
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return null;
	}


	/**private String APIRequest(String value, String url, String toSearch) {
		HttpURLConnection connection = null;
		try {
			URL api = new URL(url + value);
			connection = (HttpURLConnection) api.openConnection();

			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				StringBuilder response = new StringBuilder();

				for (String responseChar; (responseChar = reader.readLine()) != null; ) {
					response.append(responseChar);
				}

				reader.close();

				JSONObject responseObject = new JSONObject(response.toString());

				if (!toSearch.equals("id")) {
					return responseObject
							.getJSONArray("properties")
							.getJSONObject(0)
							.getString("value");
				} else {
					return responseObject.getString("id");
				}
			}
			else {
				LOGGER.log(String.format(
						"Could not get %s. Response code: %s",
						((toSearch.equals("id")) ? "UUID" : "texture"),
						responseCode
						));
			}
		} catch (MalformedURLException error) {
			LOGGER.log("An error occurred while trying to access the URL.");
		} catch (IOException error) {
			LOGGER.log("An error occurred while attempting to connect to the URL.");
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return "";
	}//*/



	public void givePlayerHead(Player player, String name){
		UUID uuid = null;
		OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(name);
		if(oPlayer.hasPlayedBefore()) {
			LOGGER.debug("Player hasPlayedBefore, getting local UUID.");
			uuid = oPlayer.getUniqueId();
		}else {
			LOGGER.debug("Player !hasPlayedBefore, getting UUID from Mojang.");
			uuid = UUID.fromString(getPlayerUUID(name));
		}
		if(uuid == null) {
			LOGGER.warn("gPH User doesn't exist or invalid UUID");
			return;
		}
		PlayerProfile profile = Bukkit.createPlayerProfile(uuid, name);
		String texture = APIRequest(uuid.toString(), "sessionProfile", "value");
		LOGGER.debug("gPH UUID = " + uuid.toString());
		LOGGER.debug("gPH textureCoded = \'" + texture + "\'");

		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		PlayerTextures textures = profile.getTextures();
		URL url = null;
		try {
			url = convertBase64ToURL(texture);
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_HEAD_URL_ERROR).error(exception));
		}
		textures.setSkin(url);
		profile.setTextures(textures);
		meta.setOwnerProfile(profile);
		meta.setNoteBlockSound(NamespacedKey.minecraft( "entity.player.hurt" ));
		ArrayList<String> lore = new ArrayList();
		if(config.getBoolean("head_settings.lore.show_plugin_name", true)){
			lore.add(ChatColor.AQUA + THIS_NAME);
		}
		meta.setLore(lore);
		meta.setLore(lore);
		head.setItemMeta(meta);
		playerGiveOrDropHead(player, head);
	}

	/**
	 * Gives the specified player a head item if their inventory has space; otherwise, drops it naturally.
	 * 
	 * @param player The player to give the head to.
	 * @param itemstack The head ItemStack to give or drop.
	 */
	public void playerGiveOrDropHead(Player player, ItemStack itemstack) {
		// Check if the player or the item is null
		if( (player == null) || (itemstack == null) ) {
			return;
		}
		// Check if the player's inventory has an empty slot
		if(player.getInventory().firstEmpty() != -1) {
			// Add the head to the player's inventory
			player.getInventory().addItem(itemstack);
		}else {
			// Drop the head naturally if the inventory is full
			player.getWorld().dropItemNaturally(player.getLocation(), itemstack);
		}
	}

	public void giveBlockHead(Player player, String blockName) {
		LOGGER.debug("giveBlockHead START");
		ItemStack blockStack = null;
		int isBlock = isBlockHeadName(blockName);

		if(isBlock != -1){
			LOGGER.debug("GBH isBlock=" + isBlock);
			blockStack = blockhead_list.get(isBlock);
			blockStack.setAmount(1);
		}/**else if(isBlock2 != -1){
			LOGGER.debug("GBH isBlock2=" + isBlock2);
			blockStack = blockHeads2.getItemStack("blocks.block_" + isBlock2 + ".itemstack", new ItemStack(Material.AIR));
		}else if(isBlock3 != -1){
			LOGGER.debug("GBH isBlock3=" + isBlock3);
			blockStack = blockHeads3.getItemStack("blocks.block_" + isBlock3 + ".itemstack", new ItemStack(Material.AIR));
		}else if(isBlock4 != -1){
			LOGGER.debug("GBH isBlock4=" + isBlock4);
			blockStack = blockHeads4.getItemStack("blocks.block_" + isBlock4 + ".itemstack", new ItemStack(Material.AIR));
		}else if(isBlock5 != -1){
			LOGGER.debug("GBH isBlock5=" + isBlock5);
			blockStack = blockHeads5.getItemStack("blocks.block_" + isBlock5 + ".itemstack", new ItemStack(Material.AIR));
		}//*/
		else {
			/**            Add translation for this line.    *****************************************************************************************************  */
			player.sendMessage(THIS_NAME + " v" + THIS_VERSION + " Sorry could not find \"" + blockName + "\""); // TODO: Add translation for this line.
		}
		if( (blockStack != null) && (blockStack.getType() != Material.AIR) ) {
			playerGiveOrDropHead(player, blockStack);
			//player.getWorld().dropItemNaturally(player.getLocation(), blockStack);
			LOGGER.debug("GBH BlockHead given to " + player.getName());
		}
		LOGGER.debug("giveBlockHead END");
	}

	/**
	 * Checks if the inventory of the specified player is full.
	 *
	 * @param player The player whose inventory is to be checked.
	 * @return `true` if the player's inventory is full; `false` otherwise.
	 */
	public boolean isInventoryFull(Player player)	{
		return !(player.getInventory().firstEmpty() == -1);
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEntityEvent event){// TODO: PlayerInteractEntityEvent
		if(!(event.getPlayer() instanceof Player)) {
			return;
		}
		try{
			Player player = event.getPlayer();
			if(player.hasPermission("moremobheads.nametag")){
				LOGGER.debug(" PIEE moremobheads.nametag=true");
				if(config.getBoolean("head_settings.mob_heads.nametag", false)) {
					LOGGER.debug(" PIEE mob.nametag=true");
					Material material = player.getInventory().getItemInMainHand().getType();
					Material material2 = player.getInventory().getItemInOffHand().getType();
					String name = "";
					if(material.equals(Material.NAME_TAG)){
						name = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
						LOGGER.debug(" PIEE" + player.getDisplayName() + " Main hand name=" + name);
					}
					if(material2.equals(Material.NAME_TAG)){
						name = player.getInventory().getItemInOffHand().getItemMeta().getDisplayName();
						LOGGER.debug("PIEE " + player.getDisplayName() + " Off hand name=" + name);
					}

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
						LOGGER.debug("canwearhead=" + NameTag.canWearHead(mob));
						if(NameTag.canWearHead(mob)){
							// Piglin, drowned, husk, pillager, stray

							boolean enforcewhitelist = config.getBoolean("head_settings.player_heads.whitelist.enforce", false);
							boolean enforceblacklist = config.getBoolean("head_settings.player_heads.blacklist.enforce", false);
							boolean onwhitelist = config.getString("head_settings.player_heads.whitelist.player_head_whitelist", "").toLowerCase().contains(name.toLowerCase());
							boolean onblacklist = config.getString("head_settings.player_heads.blacklist.player_head_blacklist", "").toLowerCase().contains(name.toLowerCase());
							if(enforcewhitelist&&enforceblacklist){
								if(onwhitelist&&!(onblacklist)){
									giveMobHead(mob, name);
								}else{
									event.setCancelled(true); // return;
									LOGGER.debug(" PIEE - Name Error 1");
								}
							}else if(enforcewhitelist&&!enforceblacklist){
								if(onwhitelist){
									giveMobHead(mob, name);
								}else{
									event.setCancelled(true); // return;
									LOGGER.debug(" PIEE - Name not on whitelist.");
								}
							}else if(!enforcewhitelist&&enforceblacklist){
								if(!onblacklist){
									giveMobHead(mob, name);
								}else{
									event.setCancelled(true); // return;
									LOGGER.debug(" PIEE - Name is on blacklist.");
								}
							}else{
								giveMobHead(mob, name);
							}
							PersistentDataContainer pdc = mob.getPersistentDataContainer();
							pdc.set(NAMETAG_KEY, PersistentDataType.STRING, "nametag");
						}
					}
				} else {
					LOGGER.debug("mob.nametag=false");
				}
			} else {
				LOGGER.debug("moremobheads.nametag=false");
				//player.sendMessage(mob.getName() + " profession= " + mob.getVillagerProfession());
			}

			//player.sendMessage(mob.getName() + " profession= " + mob.getVillagerProfession());
		} catch (Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PIE_LOAD_ERROR).error(exception));
		}

	}

	/**
	 * Determines whether an entity should drop an item based on a chance percentage.
	 *
	 * @param event         The EntityDeathEvent representing the death of the entity.
	 * @param chancePercent The chance percentage for the item drop.
	 *                      The drop will occur if a random value is less than or equal to this percentage.
	 * @return {@code true} if the item should be dropped, {@code false} otherwise.
	 */
	public boolean DropIt(EntityDeathEvent event, double chancePercent){// TODO: DropIt
		Player player = event.getEntity().getKiller();
		ItemStack itemstack = event.getEntity().getKiller().getInventory().getItemInMainHand();
		if(isDev) {return true;}
		if(itemstack != null){
			LOGGER.debug(" DI itemstack=" + itemstack.getType().toString());
			int enchantmentlevel = 0;
			if(config.getBoolean("head_settings.apply_looting", true)){
				enchantmentlevel = itemstack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
			}
			if(chancePercent == 0){
				LOGGER.debug(" DI chancePercent == 0");
				LOGGER.debug(" DI returning=false");
				return false;
			}
			LOGGER.debug(" DI enchantmentlevel=" + enchantmentlevel);
			DecimalFormat df = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.US));
			// Handle lower than 1 chances
			if((chancePercent < 1) && (enchantmentlevel == 0)){
				Random chanceRandom = chanceRandoms.computeIfAbsent(player, p -> new Random(p.getUniqueId().hashCode()));
				double inverseChance = 1 / (chancePercent / 100); // Adjust to percentage before calculating inverse
				LOGGER.debug(" DI chance=" + inverseChance);
				LOGGER.debug(" DI chancePercent=" + chancePercent);
				inverseChance = inverseChance - enchantmentlevel;
				LOGGER.debug(" DI inverseChance - enchantmentlevel = " + inverseChance);
				int randomValue = chanceRandom.nextInt((int) inverseChance);
				LOGGER.debug(" DI randomValue == 0 (" + (randomValue == 0) + ")");
				if (randomValue == 0) {
					LOGGER.debug(" DI returning=true");
					return true;
				}
			}else { // Normal 1-100 chance
				Random chanceRandom = chanceRandoms.computeIfAbsent(player, p -> new Random(p.getUniqueId().hashCode()));
				//double chance = Double.parseDouble(String.format(Locale.US, "%.2f", chanceRandom.nextDouble() * 100));
				double chance = Double.parseDouble(df.format(chanceRandom.nextDouble() * 100));
				LOGGER.debug(" DI chance=" + chance);
				LOGGER.debug(" DI chancePercent=" + chancePercent);
				chancePercent = chancePercent + enchantmentlevel;
				LOGGER.debug(" DI chancePercent + enchantmentlevel=" + chancePercent);
				LOGGER.debug(" DI " + chancePercent +" >= " + chance + " (" + (chancePercent >= chance) + ")");
				if ((chancePercent >= chance) || isDev){
					LOGGER.debug(" DI returning=true");
					return true;
				}
			}
		}
		LOGGER.debug(" DI returning=false");
		return false;
	}

	public boolean DropIt2( double chancepercent){
		double chance = Math.random() * 100;
		if(isDev) {return true;}
		LOGGER.debug(" DI2 chance=" + chance);
		LOGGER.debug(" DI2 chancepercent=" + chancepercent);
		return chancepercent >= chance;
	}

	/**
	 * Generates a random integer between the specified minimum and maximum values (inclusive).
	 *
	 * @param min The minimum value for the random integer (inclusive).
	 * @param max The maximum value for the random integer (inclusive).
	 * @return A random integer within the specified range [min, max].
	 */
	public int randomBetween(int min, int max) {
		Random r = new Random();
		int random = r.nextInt((max - min) + 1) + min;
		return random;
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) // TODO: OnPlayerJoin
	{
		Player player = event.getPlayer();
		//if(p.isOp() && UpdateCheck||p.hasPermission("moremobheads.showUpdateAvailable")){
		/** Notify Ops */
		if(UpdateAvailable&&(player.isOp()||player.hasPermission("moremobheads.showUpdateAvailable"))){
			String links = "[\"\",{\"text\":\"<Download>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/history\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\" \",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\"| \"},{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}},{\"text\":\" | \"},{\"text\":\"<Notes>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Notes_msg>\"}}]";
			links = links.replace("<DownloadLink>", DownloadLink).replace("<Download>", get("mmh.version.download"))
					.replace("<Donate>", get("mmh.version.donate")).replace("<please_update>", get("mmh.version.please_update"))
					.replace("<Donate_msg>", get("mmh.version.donate.message")).replace("<Notes>", get("mmh.version.notes"))
					.replace("<Notes_msg>", get("mmh.version.notes.message"));
			String versions = "" + ChatColor.GRAY + get("mmh.version.new_vers") + ": " + ChatColor.GREEN + "{nVers} | " + get("mmh.version.old_vers") + ": " + ChatColor.RED + "{oVers}";
			player.sendMessage("" + ChatColor.GRAY + get("mmh.version.message").toString().replace("<MyPlugin>", ChatColor.GOLD + THIS_NAME + ChatColor.GRAY) );
			Utils.sendJson(player, links);
			player.sendMessage(versions.replace("{nVers}", UCnewVers).replace("{oVers}", UColdVers));
		}

		if(player.getDisplayName().equals("JoelYahwehOfWar")||player.getDisplayName().equals("JoelGodOfWar")){
			player.sendMessage(THIS_NAME + " " + THIS_VERSION + " Hello father!");
		}
	}

	/**
	 * Removes player from map when they leave the server
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		chanceRandoms.remove(event.getPlayer());
	}

	/**
	 * Sends a JSON-formatted message to the specified player using the `tellraw` command.
	 *
	 * @param player The player to receive the JSON message.
	 * @param string The JSON string representing the message to send.
	 */
	public void sendJson(Player player, String string) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw \"" + player.getName() +
				"\" " + string);
	}

	public void makeHead(EntityDeathEvent event, Material material){// TODO: makeHead
		ItemStack itemstack = event.getEntity().getKiller().getInventory().getItemInMainHand();
		if(itemstack != null){
			LOGGER.debug("itemstack=" + itemstack.getType().toString() + " line:954");
			int enchantmentlevel = itemstack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);//.containsEnchantment(Enchantment.LOOT_BONUS_MOBS);
			LOGGER.debug("enchantmentlevel=" + enchantmentlevel + " line:956");
			double enchantmentlevelpercent = ((double)enchantmentlevel / 100);
			LOGGER.debug("enchantmentlevelpercent=" + enchantmentlevelpercent + " line:958");
			double chance = Math.random();
			LOGGER.debug("chance=" + chance + " line:960");
			double chancepercent = 0.25; /** Set to check config.yml later*/
			LOGGER.debug("chancepercent=" + chancepercent + " line:962");
			chancepercent = chancepercent + enchantmentlevelpercent;
			LOGGER.debug("chancepercent2=" + chancepercent + " line:964");
			if((chancepercent > 0.00) && (chancepercent < 0.99)){
				if (chancepercent > chance){
					event.getDrops().add(new ItemStack(material, 1));
				}
			}
		}
	}

	public String getName(EntityType eType, Player player) {
		String daType = "";
		switch(eType) {
		case PLAYER:
			daType = "player_heads";
			break;
		default:
			daType = "mob_heads";
		}
		if(config.getBoolean("head_settings." + daType + ".announce_kill.displayname", true)){
			return getNickname(player);
		}
		return player.getName();
	}

	/**
	 * Retrieves the nickname of the specified player.
	 * 
	 * @param player The player whose nickname is to be retrieved.
	 * @return The nickname of the player, or the player's name if no nickname is found.
	 */
	public  String getNickname(Player player){
		String playerName = null;
		LOGGER.debug("GN - player.getDisplayName()=" + player.getDisplayName());
		LOGGER.debug("GN - player.getName()=" + player.getName());
		LOGGER.debug("GN - head_settings.player_heads.announce_kill.displayname=" + getConfig().getBoolean("head_settings.player_heads.announce_kill.displayname"));
		if(config.getBoolean("head_settings.player_heads.announce_kill.displayname", false)) {
			playerName = ChatColorUtils.setColorsByCode(player.getDisplayName());
			if(getServer().getPluginManager().getPlugin("VentureChat") != null){
				MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(player);
				String nick = mcp.getNickname();
				if(nick != null){
					LOGGER.debug("GN - mcp.getNickname()=" + mcp.getNickname());
					LOGGER.debug("GN - ChatColor.translateAlternateColorCodes('&', nick)=" + ChatColor.translateAlternateColorCodes('&', nick));
					//ChatColor.translateAlternateColorCodes('&', nick);
					//nick = nick.replaceAll("§", "&");
					nick = ChatColorUtils.setColorsByCode(nick);
					LOGGER.debug("VentureChat ChatColorUtils.setColorsByCode(nick)=" + nick);
					return nick;
				}
				LOGGER.debug("GN - VentureChat Nick=null using " + playerName);
				return Format.color(playerName);
			}else if(getServer().getPluginManager().getPlugin("Essentials") != null){
				Essentials ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
				//User user = ess.getUserMap().getUser(player.getName());
				//if(debug){logDebug("Essnetials Nick=" + ess.getUserMap().getUser(player.getName()).getNickname());}
				String nick = ess.getUserMap().getUser(player.getName()).getNickname();
				if(nick != null){
					LOGGER.debug("GN - Essentials Nick=" + nick);
					return ChatColor.translateAlternateColorCodes('&', nick);
				}
				LOGGER.debug("GN - Essentials Nick=null using: " + playerName );
				return ChatColorUtils.setColorsByCode(playerName);
			}else if(getServer().getPluginManager().getPlugin("HexNicks") != null){
				CompletableFuture<Component> nickFuture = HexNicks.api().getStoredNick(player);
				try {
					String nick = GsonComponentSerializer.gson().serialize(nickFuture.get());
					if(nick != null){
						LOGGER.debug("GN - HexNick Nick=" + nick);
						if(nick.contains("[")) {
							nick = nick.substring(nick.indexOf("[") + 1);
						}
						if(nick.contains("]")) {
							nick = nick.substring(0, nick.indexOf("]"));
						}
						return "\"}," + ChatColor.translateAlternateColorCodes('&', nick) + ",{\"text\": \"";
					}
				} catch (Exception exception) {
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_GET_HEXNICK).error(exception));
				}
				LOGGER.debug("GN - HexNick Nick=null using " + playerName);
				return ChatColorUtils.setColorsByCode(playerName);
			}else{
				LOGGER.debug("GN - No nickname found using=" + playerName);
				return playerName;
			}
		}else {
			playerName = player.getName();
		}

		return playerName;
	}

	/**
	 * Creates a custom head ItemStack with the specified name, texture URL (as a String),
	 * associated entity type, and player who delivered the killing blow.
	 *
	 * @param name The name of the custom head.
	 * @param texture String of the Base64-encoded string or direct URL of the texture for the custom head.
	 * @param uuid String UUID of Mob
	 * @param eType The EntityType associated with the custom head.
	 * @param killer The player who delivered the killing blow to the entity.
	 * @return An ItemStack representing the custom head with the provided name, texture, sound, and lore.
	 */
	public ItemStack makeHead(String name, String texture, String uuid, EntityType eType, Player killer) {
		// Create the PlayerProfile using UUID and name
		PlayerProfile profile =  Bukkit.createPlayerProfile(UUID.fromString(uuid), "");
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		PlayerTextures textures = profile.getTextures();
		URL url = null;
		try {
			url = convertBase64ToURL(texture);
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_HEAD_URL_ERROR).error(exception));
		}
		textures.setSkin(url);
		profile.setTextures(textures);
		meta.setOwnerProfile(profile);
		meta.setNoteBlockSound(NamespacedKey.minecraft( getSoundString(ChatColor.stripColor(name), eType) ));
		ArrayList<String> lore = new ArrayList();
		if(config.getBoolean("head_settings.lore.show_killer", true)){
			lore.add(ChatColor.RESET + ChatColorUtils.setColors( langName.getString("killedby", "<RED>Killed <RESET>By <YELLOW><player>").replace("<player>", getName(eType, killer)) ) );
		}
		if(config.getBoolean("head_settings.lore.show_plugin_name", true)){
			lore.add(ChatColor.AQUA + "MoreMobHeads");
		}
		meta.setLore(lore);
		meta.setLore(lore);
		meta.setDisplayName(name);
		PersistentDataContainer skullPDC = meta.getPersistentDataContainer();
		skullPDC.set(NAME_KEY, PersistentDataType.STRING, name);
		if (lore != null) {
			skullPDC.set(LORE_KEY, LORE_PDT, lore.toArray(new String[0]));
		}
		/**skullPDC.set(UUID_KEY, PersistentDataType.STRING, uuid);
		skullPDC.set(TEXTURE_KEY, PersistentDataType.STRING, url.toString());
		skullPDC.set(SOUND_KEY, PersistentDataType.STRING, meta.getNoteBlockSound().toString());//*/

		head.setItemMeta(meta);

		return head;
	}

	/**
	 * Creates a custom head ItemStack with the specified name, texture URL (as a String),
	 * associated entity type, and player who delivered the killing blow.
	 *
	 * @param name The name of the custom head.
	 * @param type The type of the custom head.
	 * @param texture String of the Base64-encoded string or direct URL of the texture for the custom head.
	 * @param uuid String UUID of Mob
	 * @param eType The EntityType associated with the custom head.
	 * @param killer The player who delivered the killing blow to the entity.
	 * @return An ItemStack representing the custom head with the provided name, texture, sound, and lore.
	 */
	public ItemStack makeHead(String name, String type, String texture, String uuid, EntityType eType, Player killer) {
		// Create the PlayerProfile using UUID and name
		PlayerProfile profile =  Bukkit.createPlayerProfile(UUID.fromString(uuid), "");
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		PlayerTextures textures = profile.getTextures();
		URL url = null;
		try {
			url = convertBase64ToURL(texture);
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_HEAD_URL_ERROR).error(exception));
		}
		textures.setSkin(url);
		profile.setTextures(textures);
		meta.setOwnerProfile(profile);
		meta.setNoteBlockSound(NamespacedKey.minecraft( getSoundString(meta.getDisplayName(), eType) ));
		ArrayList<String> lore = new ArrayList();
		lore.add(ChatColor.WHITE + "Type: " + type);
		if(config.getBoolean("head_settings.lore.show_killer", true)){
			lore.add(ChatColor.RESET + ChatColorUtils.setColors( langName.getString("killedby", "<RED>Killed <RESET>By <YELLOW><player>").replace("<player>", getName(eType, killer)) ) );
		}
		if(config.getBoolean("head_settings.lore.show_plugin_name", true)){
			lore.add(ChatColor.AQUA + "MoreMobHeads");
		}
		meta.setLore(lore);
		meta.setLore(lore);
		meta.setDisplayName(name);
		head.setItemMeta(meta);
		PersistentDataContainer skullPDC = head.getItemMeta().getPersistentDataContainer();
		skullPDC.set(NAME_KEY, PersistentDataType.STRING, name);
		if (lore != null) {
			skullPDC.set(LORE_KEY, LORE_PDT, lore.toArray(new String[0]));
		}
		skullPDC.set(UUID_KEY, PersistentDataType.STRING, uuid);
		skullPDC.set(TEXTURE_KEY, PersistentDataType.STRING, url.toString());
		skullPDC.set(SOUND_KEY, PersistentDataType.STRING, meta.getNoteBlockSound().toString());
		return head;
	}

	/**
	 * Creates a custom head ItemStack with the specified name, texture URL (as a Base64-encoded string or a direct URL), associated entity type,
	 * and player UUID who delivered the killing blow.
	 *
	 * @param name The name of the custom head.
	 * @param texture String of the Base64-encoded string or direct URL of the texture for the custom head.
	 * @param uuid String UUID of the player.
	 * @param eType The EntityType associated with the custom head.
	 * @param amount The number of heads to make.
	 * @return An ItemStack representing the custom head with the provided name, texture, sound, and lore.
	 */
	public ItemStack makeHeads(String name, String texture, String uuid, EntityType eType, int amount) {
		// Create the PlayerProfile using UUID and name
		PlayerProfile profile =  Bukkit.createPlayerProfile(UUID.fromString(uuid), "");
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, amount);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		PlayerTextures textures = profile.getTextures();
		URL url = null;
		try {
			url = convertBase64ToURL(texture);
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_HEAD_URL_ERROR).error(exception));
		}
		textures.setSkin(url);
		profile.setTextures(textures);
		meta.setOwnerProfile(profile);
		meta.setNoteBlockSound(NamespacedKey.minecraft( getSoundString(meta.getDisplayName(), eType) ));
		ArrayList<String> lore = new ArrayList();
		if(config.getBoolean("head_settings.lore.show_plugin_name", true)){
			lore.add(ChatColor.AQUA + "MoreMobHeads");
		}
		meta.setLore(lore);
		meta.setLore(lore);
		meta.setDisplayName(name);
		head.setItemMeta(meta);
		PersistentDataContainer skullPDC = head.getItemMeta().getPersistentDataContainer();
		skullPDC.set(NAME_KEY, PersistentDataType.STRING, name);
		if (lore != null) {
			skullPDC.set(LORE_KEY, LORE_PDT, lore.toArray(new String[0]));
		}
		skullPDC.set(UUID_KEY, PersistentDataType.STRING, uuid);
		skullPDC.set(TEXTURE_KEY, PersistentDataType.STRING, url.toString());
		skullPDC.set(SOUND_KEY, PersistentDataType.STRING, meta.getNoteBlockSound().toString());
		return head;
	}

	/**
	 * Creates a custom head ItemStack with the specified name, texture URL (as a Base64-encoded string or a direct URL), associated entity type,
	 * and player UUID who delivered the killing blow.
	 *
	 * @param name The name of the custom head.
	 * @param texture String of the Base64-encoded string or direct URL of the texture for the custom head.
	 * @param uuid String UUID of the player.
	 * @param eType The EntityType associated with the custom head.
	 * @param amount The number of heads to make.
	 * @return An ItemStack representing the custom head with the provided name, texture, sound, and lore.
	 */
	public ItemStack makeHeads(String name, String texture, String uuid,List<String> lore , EntityType eType, int amount) {
		// Create the PlayerProfile using UUID and name
		PlayerProfile profile =  Bukkit.createPlayerProfile(UUID.fromString(uuid), "");
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, amount);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		PlayerTextures textures = profile.getTextures();
		URL url = null;
		try {
			url = convertBase64ToURL(texture);
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_HEAD_URL_ERROR).error(exception));
		}
		textures.setSkin(url);
		profile.setTextures(textures);
		meta.setOwnerProfile(profile);
		meta.setNoteBlockSound(NamespacedKey.minecraft( getSoundString(meta.getDisplayName(), eType) ));
		List<String> headLore = new ArrayList<>();
		if( (lore == null) || lore.isEmpty() ) {
			lore = new ArrayList();
		}
		//LOGGER.debug("lore = " + lore);
		lore = StrUtils.removeBlanks(lore);
		if(config.getBoolean("head_settings.lore.show_plugin_name", true)){
			headLore.addAll(lore);
			headLore.add(ChatColor.AQUA + "MoreMobHeads");
		}
		meta.setLore(headLore);
		meta.setLore(headLore);
		meta.setDisplayName(name);
		head.setItemMeta(meta);
		PersistentDataContainer skullPDC = head.getItemMeta().getPersistentDataContainer();
		skullPDC.set(NAME_KEY, PersistentDataType.STRING, name);
		if (lore != null) {
			skullPDC.set(LORE_KEY, LORE_PDT, headLore.toArray(new String[0]));
		}
		skullPDC.set(UUID_KEY, PersistentDataType.STRING, uuid);
		skullPDC.set(TEXTURE_KEY, PersistentDataType.STRING, url.toString());
		skullPDC.set(SOUND_KEY, PersistentDataType.STRING, meta.getNoteBlockSound().toString());
		return head;
	}

	/**
	 * Converts a Base64-encoded string containing a JSON structure or a URL string to a URL object
	 * representing the texture's URL.
	 *
	 * @param input The Base64-encoded string containing the JSON structure with a URL or a URL string itself.
	 * @return A URL object representing the texture's URL. If the input is a direct URL string,
	 *         it is returned directly. If the input is a Base64-encoded string, the method
	 *         decodes it, extracts the URL from the JSON structure, and returns the corresponding URL object.
	 * @throws MalformedURLException If the URL extraction or URL creation encounters a malformed URL.
	 */
	private static URL convertBase64ToURL(String base64) throws MalformedURLException {
		try {
			return new URL(base64);
		} catch (MalformedURLException ignored) {}

		String jsonString = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);

		JSONObject jsonObject = new JSONObject(jsonString);
		JSONObject jstextures = jsonObject.getJSONObject("textures");
		JSONObject skin = jstextures.getJSONObject("SKIN");
		String jsurl = skin.getString("url");
		return new URL(jsurl);
	}

	/**
	 * Converts a URL string to a Base64-encoded string containing a JSON structure.
	 * 
	 * @param urlString The URL string to be converted.
	 * @return A Base64-encoded string representing the JSON structure with the URL.
	 */
	public static String convertURLToBase64(String urlString) {
		// Create the JSON structure
		JSONObject jsonObject = new JSONObject();
		JSONObject textures = new JSONObject();
		JSONObject skin = new JSONObject();
		skin.put("url", urlString);
		textures.put("SKIN", skin);
		jsonObject.put("textures", textures);

		// Convert JSON object to string
		String jsonString = jsonObject.toString();

		// Encode JSON string to Base64
		String base64String = Base64.getEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));

		return base64String;
	}

	/**
	 * Converts a Base64-encoded string containing a JSON structure or a URL string to a URL object
	 * representing the texture's URL.
	 *
	 * @param input The Base64-encoded string containing the JSON structure with a URL
	 *              or a URL string itself.
	 * @return A URL object representing the texture's URL extracted from the input string.
	 * @throws MalformedURLException If the URL extraction or URL creation encounters a malformed URL.
	 */
	public URL getUrlFromBase64(String base64) throws MalformedURLException {
		// Check if the input is already a valid URL
		try {
			return new URL(base64);
		} catch (MalformedURLException ignored) {
			// Proceed with the decoding logic if it's not a valid URL
		}
		String decoded = new String(Base64.getDecoder().decode(base64));
		// We simply remove the "beginning" and "ending" part of the JSON, so we're left with only the URL. You could use a proper
		// JSON parser for this, but that's not worth it. The String will always start exactly with this stuff anyway
		return new URL(decoded.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(), decoded.length() - "\"}}}".length()));
	}

	/**
	 * Retrieves the sound string associated with the display name and entity type.
	 *
	 * @param displayname The display name of the entity.
	 * @param entity The entity for which to determine the sound string.
	 * @return The sound string for the given entity based on its display name and type.
	 */
	public String getSoundString(String displayname, EntityType eType) {
		//EntityType eType = entity.getType();
		String name = eType.name();
		String soundType = "ambient";
		switch (eType) {
		case ALLAY:
			soundType = "ambient_without_item";
			break;
		case AXOLOTL:
			soundType = "idle_air";
			break;
		case BEE:
			String bee = (displayname.contains(" ")) ? StrUtils.Left(displayname, displayname.indexOf(" ") - 1).toUpperCase() : "BEE";
			switch(bee) {
			case "ANGRY": // Angry Pollinated Bee
				soundType = "loop_aggressive";
				break;
			case "POLLINATED": // Pollinated Bee
				soundType = "pollinate";
				break;
			default: // Bee
				soundType = "loop";
				break;
			}
			break;
		case CAVE_SPIDER:
		case SPIDER:
			name = "SPIDER";
			soundType = "step";
			break;
		case CREEPER:
			soundType = "primed";
			break;
		case COD:
		case SALMON:
		case TROPICAL_FISH:
		case TADPOLE:
			soundType = "flop";
			break;
		case IRON_GOLEM:
		case PLAYER:
		case SNOWMAN:
			soundType = "hurt";
			break;
		case GOAT:
			String goat = (displayname.contains(" ")) ? StrUtils.Left(displayname, displayname.indexOf(" ") - 1).toUpperCase() : "GOAT";
			switch (goat) {
			case "SCREAMING":
				soundType = "screaming.ambient";
				break;
			default:
				soundType = "ambient";
				break;
			}
			break;
		case MUSHROOM_COW:
			name = "COW";
			soundType = "ambient";
			break;
		case PANDA:
			//				Check						If true get string left of space										Default
			String panda = (displayname.contains(" ")) ? StrUtils.Left(displayname, displayname.indexOf(" ") - 1).toUpperCase() : "PANDA";
			switch (panda) {
			case "WORRIED":
				soundType = "worried_ambient";
				break;
			case "AGGRESSIVE":
				soundType = "aggressive_ambient";
				break;
			case "WEAK":
				soundType = "sneeze";
				break;
			default:
				soundType = "ambient";
				break;
			}
			break;
		case PUFFERFISH:
			soundType = "blow_up";
			break;
		case SLIME:
		case MAGMA_CUBE:
			soundType = "squish_small";
			break;
		case RABBIT:
			String rabbit = (displayname.contains(" ")) ? StrUtils.Left(displayname, displayname.indexOf(" ") - 1).toUpperCase() : "RABBIT";
			switch (rabbit) {
			case "THE":
				soundType = "attack";
				break;
			default:
				soundType = "ambient";
				break;
			}
			break;
		case SNIFFER:
			soundType = "scenting";
			break;
		case TRADER_LLAMA:
			name = "LLAMA";
			soundType = "ambient";
			break;
		case TURTLE:
			soundType = "ambient_land";
			break;
		case VEX:
			String vex = (displayname.contains(" ")) ? StrUtils.Left(displayname, displayname.indexOf(" ") - 1).toUpperCase() : "VEX";
			switch (vex) {
			case "ANGRY":
				soundType = "charge";
				break;
			default:
				soundType = "ambient";
				break;
			}
			break;
		case WOLF:
			//String wolf = (displayname.contains(" ")) ? StrUtils.Left(displayname, displayname.indexOf(" ") - 1).toUpperCase() : "WOLF";

			if(displayname.contains("Angry")) {
				soundType = "growl";
			}else {
				soundType = "ambient";
			}
			break;
		case WITHER:
			if (displayname.contains("projectile")) {
				soundType = "shoot";
			} else {
				soundType = "ambient";
			}
			break;
		default:
			soundType = "ambient";
			if (name.equalsIgnoreCase("BREEZE")) {
				soundType = "idle_ground";
			}
			break;
		}
		return "entity." + name.toLowerCase() + "." + soundType;
	}

	/**
	 * Creates and returns an ItemStack with the specified texture, name, and UUID, producing multiple copies.
	 *
	 * @param texture The base64 texture string for the trader's skull appearance.
	 * @param name The name of the custom trader's skulls.
	 * @param uuid The string UUID associated with the trader's GameProfile.
	 * @param amount The number of identical trader's skull ItemStacks to create.
	 * @return An ItemStack representing the custom trader's skull with the provided texture, name, UUID, and in the specified quantity.
	 */
	/**public ItemStack makeTraderSkull(String texture, String name, String uuid, int amount){
		ItemStack item = new ItemStack(Material.PLAYER_HEAD, amount);
		if(texture == null) {
			return item;
		}
		SkullMeta meta = (SkullMeta) item.getItemMeta();

		GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), texture);
		profile.getProperties().put("textures", new Property("textures", texture));
		profile.getProperties().put("SkullOwner", new Property("id", uuid));
		profile.getProperties().put("display", new Property("Name", name));
		setGameProfile(meta, profile);
		ArrayList<String> lore = new ArrayList();
		if(config.getBoolean("head_settings.lore.show_plugin_name", true)){
			lore.add(ChatColor.AQUA + "MoreMobHeads");
		}
		meta.setLore(lore);
		meta.setLore(lore);

		//meta.setOwningPlayer(Bukkit.getOfflinePlayer(ownerUUID));
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}//*/

	/**
	 * Creates and returns a custom skull ItemStack with the specified texture and name.
	 *
	 * @param texture The base64 texture string for the skull's appearance.
	 * @param name The name of the custom skull.
	 * @param killer The Player object representing the killer of the mob or player that was killed. Pass null if not applicable.
	 * @return An ItemStack representing the custom skull with the provided texture and name.
	 */
	/**public ItemStack makeSkull(String texture, String name, Player killer){
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		if(texture == null) {
			return item;
		}
		SkullMeta meta = (SkullMeta) item.getItemMeta();

		GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), texture);
		profile.getProperties().put("textures", new Property("textures", texture));
		profile.getProperties().put("display", new Property("Name", name));
		//profile.getProperties().put("BlockEntityTag", new Property("note_block_sound", "minecraft:entity.camel.ambient"));

		setGameProfile(meta, profile);
		ArrayList<String> lore = new ArrayList();

		if(config.getBoolean("head_settings.lore.show_killer", true)){
			lore.add(ChatColor.RESET + ChatColorUtils.setColors( langName.getString("killedby", "<RED>Killed <RESET>By <YELLOW><player>").replace("<player>", killer.getName()) ) );
		}
		if(config.getBoolean("head_settings.lore.show_plugin_name", true)){
			lore.add(ChatColor.AQUA + "MoreMobHeads");
		}
		meta.setLore(lore);
		meta.setLore(lore);

		//meta.setOwningPlayer(Bukkit.getOfflinePlayer(ownerUUID));
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}//*/

	/**
	 * Creates and returns a custom skull ItemStack with the specified texture and name, producing multiple copies.
	 *
	 * @param texture The base64 texture string for the skulls' appearance.
	 * @param name The name of the custom skulls.
	 * @param amount The number of identical skull ItemStacks to create.
	 * @return An ItemStack of the custom skull with the provided texture and name, in the specified quantity.
	 */
	public ItemStack makeSkulls(String texture, String name, int amount){
		ItemStack item = new ItemStack(Material.PLAYER_HEAD, amount);
		if(texture == null) {
			return item;
		}
		SkullMeta meta = (SkullMeta) item.getItemMeta();

		GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), texture);
		profile.getProperties().put("textures", new Property("textures", texture));
		profile.getProperties().put("display", new Property("Name", name));
		setGameProfile(meta, profile);
		ArrayList<String> lore = new ArrayList();

		if(config.getBoolean("head_settings.lore.show_plugin_name", true)){
			lore.add(ChatColor.AQUA + "MoreMobHeads");
		}
		meta.setLore(lore);
		meta.setLore(lore);

		//meta.setOwningPlayer(Bukkit.getOfflinePlayer(ownerUUID));
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}

	static JavaPlugin plugin = getInstance();
	private static Field fieldProfileItem;
	public static void setGameProfile(SkullMeta meta, GameProfile profile) {
		try{
			if(fieldProfileItem == null) {
				fieldProfileItem = meta.getClass().getDeclaredField("profile");
			}
			fieldProfileItem.setAccessible(true);
			fieldProfileItem.set(meta, profile);
		}
		catch(Exception exception){
			reporter.reportDetailed(plugin, Report.newBuilder(PluginLibrary.REPORT_HEAD_URL_ERROR).error(exception));
		}
	}

	public static MoreMobHeads getInstance() {
		return (MoreMobHeads) Bukkit.getPluginManager().getPlugin("MoreMobHeads");
	}

	public boolean isInteger(String string){
		try{
			Integer.parseInt(string);
			return true;
		}catch (NumberFormatException e){
			return false;
		}
	}

	public String isPlayerHead(String string){
		try{
			playerFile = new File(getDataFolder() + "" + File.separatorChar + "player_heads.yml");//\
			if(!playerFile.exists()){																	// checks if the yaml does not exist
				return null;
			}
			int numOfCustomTrades = playerHeads.getInt("players.number", 0) + 1;
			LOGGER.debug("iPH string=" + string);
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
		}catch(Exception exception){
			//stacktraceInfo();
			//e.printStackTrace();
			return null;
		}
		//playerHeads
		return null;
	}

	public String isBlockHead(String string){ // TODO: isBlockHead
		try{
			if(!(Double.parseDouble(StrUtils.Left(getMCVersion(), 4)) >= 1.16)){
				blockFile = new File(getDataFolder() + "" + File.separatorChar + "block_heads.yml");//\
				if(!blockFile.exists()){																	// checks if the yaml does not exist
					return null;
				}
			}
			blockFile116 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16.yml");
			//blockFile1162 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16_2.yml");
			if(Double.parseDouble(StrUtils.Left(getMCVersion(), 4)) >= 1.16){
				if(!blockFile116.exists()){
					return null;
				}
			}
			int numOfCustomTrades = blockHeads.getInt("blocks.number", 0) + 1;
			LOGGER.debug("iBH string=" + string);
			for(int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++){
				ItemStack itemstack = blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
				if(itemstack != null){
					SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
					if(skullmeta != null){
						//if(debug&&skullmeta != null){logDebug("iBH getOwner_" + randomBlockHead + "=" + skullmeta.getOwner().toString());}
						if(skullmeta.getDisplayName() != null){
							if(ChatColor.stripColor(skullmeta.getDisplayName()).equals(string)){
								return itemstack.getItemMeta().getDisplayName();
							}
						}
					}
				}
			}
		}catch(Exception exception){
			//stacktraceInfo();
			//e.printStackTrace();
			return null;
		}
		//blockHeads
		return null;
	}

	public String isBlockHead2(String string){
		try{
			if(!(Double.parseDouble(StrUtils.Left(getMCVersion(), 4)) >= 1.16)){																// checks if the yaml does not exist
				return null;
			}
			blockFile1162 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_16_2.yml");
			if(getMCVersion().startsWith("1.16")||getMCVersion().startsWith("1.17")){
				if(!blockFile1162.exists()){
					return null;
				}

			}
			int numOfCustomTrades = blockHeads2.getInt("blocks.number", 0) + 1;
			LOGGER.debug("iBH2 string=" + string);
			for(int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++){
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
		}catch(Exception exception){
			//stacktraceInfo();
			//e.printStackTrace();
			return null;
		}
		//blockHeads
		return null;
	}

	public String isBlockHead3(String string){
		try{
			if(!(Double.parseDouble(StrUtils.Left(getMCVersion(), 4)) >= 1.16)){																// checks if the yaml does not exist
				return null;
			}
			blockFile117 = new File(getDataFolder() + "" + File.separatorChar + "block_heads_1_17.yml");
			if(getMCVersion().startsWith("1.16")||getMCVersion().startsWith("1.17")){
				if(!blockFile117.exists()){
					return null;
				}

			}
			int numOfCustomTrades = blockHeads3.getInt("blocks.number", 0) + 1;
			LOGGER.debug("iBH3 string=" + string);
			for(int randomBlockHead = 1; randomBlockHead < numOfCustomTrades; randomBlockHead++){
				ItemStack itemstack = blockHeads3.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
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
		}catch(Exception exception){
			//stacktraceInfo();
			//e.printStackTrace();
			return null;
		}
		//blockHeads
		return null;
	}


	public int isBlockHeadName(String string){ // TODO: isBlockHeadName
		LOGGER.debug("iBHN START");
		try{
			LOGGER.debug("iBH string=" + string);
			for(int randomBlockHead = 1; randomBlockHead < (blockhead_list.size() + 1); randomBlockHead++){
				ItemStack itemstack = blockhead_list.get(randomBlockHead);
				//ItemStack itemstack = blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
				if(itemstack != null){
					SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
					if(skullmeta != null){
						//if(debug&&skullmeta != null){logDebug("iBH getOwner_" + randomBlockHead + "=" + skullmeta.getOwner().toString());}
						if(skullmeta.getDisplayName() != null){
							if(ChatColor.stripColor(skullmeta.getDisplayName()).toLowerCase().equals(string.toLowerCase())){
								LOGGER.debug("iBHN END Sucess!");
								return randomBlockHead; //itemstack.getItemMeta().getDisplayName();
							}
						}
					}
				}
			}
		}catch(Exception exception){
			LOGGER.debug("iBHN END Failure=Exception");
			return -1;
		}
		//blockHeads
		LOGGER.debug("iBHN END Failure!");
		return -1;
	}

	public static void copyFile(String origin, String destination) throws IOException {
		Path FROM = Paths.get(origin);
		Path TO = Paths.get(destination);
		// Ensure the destination directory exists
		if (TO.getParent() != null) {
			Files.createDirectories(TO.getParent());
		}
		//overwrite the destination file if it exists, and copy
		// the file attributes, including the rwx permissions
		CopyOption[] options = new CopyOption[]{
				StandardCopyOption.REPLACE_EXISTING,
				StandardCopyOption.COPY_ATTRIBUTES
		};
		Files.copy(FROM, TO, options);
	}

	public double normalize(double value) {
		// Check if the value is in the old format
		if ((value >= 0.0001) && (value <= 1.00)) {
			// Convert to the new format (multiply by 100)
			return Math.max(value * 100.0, 0.5);
		} else if ((value >= 0.5) && (value <= 100.0)) {
			// Value is already in the new format
			return value;
		} else {
			return 13.13;
		}
	}

	public void copyConfig(String from, String to){
		LOGGER.log("Loading new config.yml...");
		try {
			config.load(new File(to));
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
		}
		LOGGER.log("Loading old config.yml...");
		try {
			oldconfig.load(new File(from));
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
		}
		LOGGER.log("Copying values from backup" + File.separatorChar + "config.yml...");
		config.set("plugin_settings.auto_update_check"							, oldconfig.get("plugin_settings.auto_update_check", true));
		config.set("plugin_settings.debug"										, oldconfig.get("plugin_settings.debug", false));
		config.set("plugin_settings.lang"										, oldconfig.get("plugin_settings.lang", "en_US"));

		config.set("global_settings.console.colorful_console"					, oldconfig.get("global_settings.console.colorful_console", true));
		config.set("global_settings.console.silent_console"						, oldconfig.get("global_settings.console.silent_console", false));
		config.set("global_settings.console.longpluginname"						, oldconfig.get("global_settings.console.longpluginname", true));
		config.set("global_settings.world.whitelist"							, oldconfig.get("global_settings.world.whitelist", ""));
		config.set("global_settings.world.blacklist"							, oldconfig.get("global_settings.world.blacklist", ""));
		config.set("global_settings.event.piston_extend"						, oldconfig.get("global_settings.event.piston_extend", true));

		config.set("head_settings.apply_looting"								, oldconfig.get("head_settings.apply_looting", true));
		config.set("head_settings.lore.show_killer"								, oldconfig.get("head_settings.lore.show_killer", true));
		config.set("head_settings.lore.show_plugin_name"						, oldconfig.get("head_settings.lore.show_plugin_name", true));
		config.set("head_settings.mini_blocks.stonecutter"						, oldconfig.get("head_settings.mini_blocks.stonecutter", false));
		config.set("head_settings.mini_blocks.perblock"							, oldconfig.get("head_settings.mini_blocks.perblock", 1));
		config.set("head_settings.player_heads.announce_kill.enabled"			, oldconfig.get("head_settings.player_heads.announce_kill.enabled", true));
		config.set("head_settings.player_heads.announce_kill.displayname"		, oldconfig.get("head_settings.player_heads.announce_kill.displayname", true));
		config.set("head_settings.player_heads.whitelist.enforce"				, oldconfig.get("head_settings.player_heads.whitelist.enforce", true));
		config.set("head_settings.player_heads.whitelist.player_head_whitelist"	, oldconfig.get("head_settings.player_heads.whitelist.player_head_whitelist", "names_go_here"));
		config.set("head_settings.player_heads.blacklist.enforce"				, oldconfig.get("head_settings.player_heads.blacklist.enforce", true));
		config.set("head_settings.player_heads.blacklist.player_head_blacklist"	, oldconfig.get("head_settings.player_heads.blacklist.player_head_blacklist", "names_go_here"));
		config.set("head_settings.mob_heads.announce_kill.enabled"				, oldconfig.get("head_settings.mob_heads.announce_kill.enabled", true));
		config.set("head_settings.mob_heads.announce_kill.displayname"			, oldconfig.get("head_settings.mob_heads.announce_kill.displayname", true));
		config.set("head_settings.mob_heads.whitelist"							, oldconfig.get("head_settings.mob_heads.whitelist", ""));
		config.set("head_settings.mob_heads.blacklist"							, oldconfig.get("head_settings.mob_heads.blacklist", ""));
		config.set("head_settings.mob_heads.nametag"							, oldconfig.get("head_settings.mob_heads.nametag", false));
		config.set("head_settings.mob_heads.vanilla_heads.creeper"				, oldconfig.get("head_settings.mob_heads.vanilla_heads.creeper", false));
		config.set("head_settings.mob_heads.vanilla_heads.ender_dragon"			, oldconfig.get("head_settings.mob_heads.vanilla_heads.ender_dragon", false));
		config.set("head_settings.mob_heads.vanilla_heads.piglin"				, oldconfig.get("head_settings.mob_heads.vanilla_heads.piglin", false));
		config.set("head_settings.mob_heads.vanilla_heads.skeleton"				, oldconfig.get("head_settings.mob_heads.vanilla_heads.skeleton", false));
		config.set("head_settings.mob_heads.vanilla_heads.wither_skeleton"		, oldconfig.get("head_settings.mob_heads.vanilla_heads.wither_skeleton", false));
		config.set("head_settings.mob_heads.vanilla_heads.zombie"				, oldconfig.get("head_settings.mob_heads.vanilla_heads.zombie", false));

		config.set("wandering_trades.custom_wandering_trader"					, oldconfig.get("wandering_trades.custom_wandering_trader", true));
		config.set("wandering_trades.keep_default_trades"						, oldconfig.get("wandering_trades.keep_default_trades", true));
		config.set("wandering_trades.player_heads.enabled"						, oldconfig.get("wandering_trades.player_heads.enabled", true));
		config.set("wandering_trades.player_heads.min"							, oldconfig.get("wandering_trades.player_heads.min", 0));
		config.set("wandering_trades.player_heads.max"							, oldconfig.get("wandering_trades.player_heads.max", 5));
		config.set("wandering_trades.block_heads.enabled"						, oldconfig.get("wandering_trades.block_heads.enabled", true));
		config.set("wandering_trades.block_heads.pre_116.min"					, oldconfig.get("wandering_trades.block_heads.pre_116.min", 0));
		config.set("wandering_trades.block_heads.pre_116.max"					, oldconfig.get("wandering_trades.block_heads.pre_116.max", 5));
		config.set("wandering_trades.block_heads.is_116.min"					, oldconfig.get("wandering_trades.block_heads.is_116.min", 0));
		config.set("wandering_trades.block_heads.is_116.max"					, oldconfig.get("wandering_trades.block_heads.is_116.max", 5));
		config.set("wandering_trades.block_heads.is_117.min"					, oldconfig.get("wandering_trades.block_heads.is_117.min", 0));
		config.set("wandering_trades.block_heads.is_117.max"					, oldconfig.get("wandering_trades.block_heads.is_117.max", 5));
		config.set("wandering_trades.custom_trades.enabled"						, oldconfig.get("wandering_trades.custom_trades.enabled", false));
		config.set("wandering_trades.custom_trades.min"							, oldconfig.get("wandering_trades.custom_trades.min", 0));
		config.set("wandering_trades.custom_trades.max"							, oldconfig.get("wandering_trades.custom_trades.max", 5));
		LOGGER.log("Saving config.yml...");
		try {
			config.save(new File(getDataFolder(), "config.yml"));
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_SAVE_CONFIG).error(exception));
		}
		config = new YmlConfiguration();
		oldconfig = null;
		LOGGER.log("Update complete config.yml...");
	}

	public void copyChance(String from, String to){
		chanceConfig = new YmlConfiguration();
		oldchanceConfig = new YmlConfiguration();
		try {
			chanceConfig.load(new File(to));
			oldchanceConfig.load(new File(from));
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CHANCE_LOAD_ERROR).error(exception));
			/**stacktraceInfo();
			e.printStackTrace();//*/
		}
		LOGGER.log("Copying values frome backup/chance_config.yml to chance_config.yml");
		chanceConfig.set("chance_percent.player"								, normalize(oldchanceConfig.getDouble("chance_percent.player", 50.0) ) );
		chanceConfig.set("chance_percent.named_mob"								, normalize(oldchanceConfig.getDouble("chance_percent.named_mob", 10.0) ) );
		chanceConfig.set("chance_percent.allay"									, normalize(oldchanceConfig.getDouble("chance_percent.allay", 20.0) ) );
		chanceConfig.set("chance_percent.armadillo"								, normalize(oldchanceConfig.getDouble("chance_percent.armadillo", 10.0) ) );
		chanceConfig.set("chance_percent.axolotl.blue"							, normalize(oldchanceConfig.getDouble("chance_percent.axolotl.blue", 100.0) ) );
		chanceConfig.set("chance_percent.axolotl.cyan"							, normalize(oldchanceConfig.getDouble("chance_percent.axolotl.cyan", 20.0) ) );
		chanceConfig.set("chance_percent.axolotl.gold"							, normalize(oldchanceConfig.getDouble("chance_percent.axolotl.gold", 20.0) ) );
		chanceConfig.set("chance_percent.axolotl.lucy"							, normalize(oldchanceConfig.getDouble("chance_percent.axolotl.lucy", 20.0) ) );
		chanceConfig.set("chance_percent.axolotl.wild"							, normalize(oldchanceConfig.getDouble("chance_percent.axolotl.wild", 20.0) ) );
		chanceConfig.set("chance_percent.bat"									, normalize(oldchanceConfig.getDouble("chance_percent.bat", 10.0) ) );
		chanceConfig.set("chance_percent.bee.angry_pollinated"					, normalize(oldchanceConfig.getDouble("chance_percent.bee.angry_pollinated", 20.0) ) );
		chanceConfig.set("chance_percent.bee.angry"								, normalize(oldchanceConfig.getDouble("chance_percent.bee.angry", 20.0) ) );
		chanceConfig.set("chance_percent.bee.pollinated"						, normalize(oldchanceConfig.getDouble("chance_percent.bee.pollinated", 20.0) ) );
		chanceConfig.set("chance_percent.bee.chance_percent"					, normalize(oldchanceConfig.getDouble("chance_percent.bee.normal", 20.0) ) );
		chanceConfig.set("chance_percent.blaze"									, normalize(oldchanceConfig.getDouble("chance_percent.blaze", 0.5) ) );
		chanceConfig.set("chance_percent.camel"									, normalize(oldchanceConfig.getDouble("chance_percent.camel", 27.0) ) );
		chanceConfig.set("chance_percent.cat.all_black"							, normalize(oldchanceConfig.getDouble("chance_percent.cat.all_black", 33.0) ) );
		chanceConfig.set("chance_percent.cat.black"								, normalize(oldchanceConfig.getDouble("chance_percent.cat.black", 33.0) ) );
		chanceConfig.set("chance_percent.cat.british_shorthair"					, normalize(oldchanceConfig.getDouble("chance_percent.cat.british_shorthair", 33.0) ) );
		chanceConfig.set("chance_percent.cat.calico"							, normalize(oldchanceConfig.getDouble("chance_percent.cat.calico", 33.0) ) );
		chanceConfig.set("chance_percent.cat.jellie"							, normalize(oldchanceConfig.getDouble("chance_percent.cat.jellie", 33.0) ) );
		chanceConfig.set("chance_percent.cat.persian"							, normalize(oldchanceConfig.getDouble("chance_percent.cat.persian", 33.0) ) );
		chanceConfig.set("chance_percent.cat.ragdoll"							, normalize(oldchanceConfig.getDouble("chance_percent.cat.ragdoll", 33.0) ) );
		chanceConfig.set("chance_percent.cat.red"								, normalize(oldchanceConfig.getDouble("chance_percent.cat.red", 33.0) ) );
		chanceConfig.set("chance_percent.cat.siamese"							, normalize(oldchanceConfig.getDouble("chance_percent.cat.siamese", 33.0) ) );
		chanceConfig.set("chance_percent.cat.tabby"								, normalize(oldchanceConfig.getDouble("chance_percent.cat.tabby", 33.0) ) );
		chanceConfig.set("chance_percent.cat.white"								, normalize(oldchanceConfig.getDouble("chance_percent.cat.white", 33.0) ) );

		chanceConfig.set("chance_percent.cave_spider"							, normalize(oldchanceConfig.getDouble("chance_percent.cave_spider", 0.5) ) );
		chanceConfig.set("chance_percent.chicken"								, normalize(oldchanceConfig.getDouble("chance_percent.chicken", 1.0) ) );
		chanceConfig.set("chance_percent.cod"									, normalize(oldchanceConfig.getDouble("chance_percent.cod", 10.0) ) );
		chanceConfig.set("chance_percent.cow"									, normalize(oldchanceConfig.getDouble("chance_percent.cow", 1.0) ) );
		chanceConfig.set("chance_percent.creeper"								, normalize(oldchanceConfig.getDouble("chance_percent.creeper", 50.0) ) );
		chanceConfig.set("chance_percent.creeper_charged"						, normalize(oldchanceConfig.getDouble("chance_percent.creeper_charged", 100.0) ) );
		chanceConfig.set("chance_percent.dolphin"								, normalize(oldchanceConfig.getDouble("chance_percent.dolphin", 33.0) ) );
		chanceConfig.set("chance_percent.donkey"								, normalize(oldchanceConfig.getDouble("chance_percent.donkey", 20.0) ) );
		chanceConfig.set("chance_percent.drowned"								, normalize(oldchanceConfig.getDouble("chance_percent.drowned", 5.0) ) );
		chanceConfig.set("chance_percent.elder_guardian"						, normalize(oldchanceConfig.getDouble("chance_percent.elder_guardian", 100.0) ) );
		chanceConfig.set("chance_percent.ender_dragon"							, normalize(oldchanceConfig.getDouble("chance_percent.ender_dragon", 100.0) ) );
		chanceConfig.set("chance_percent.enderman"								, normalize(oldchanceConfig.getDouble("chance_percent.enderman", 0.5) ) );
		chanceConfig.set("chance_percent.endermite"								, normalize(oldchanceConfig.getDouble("chance_percent.endermite", 10.0) ) );
		chanceConfig.set("chance_percent.evoker"								, normalize(oldchanceConfig.getDouble("chance_percent.evoker", 25.0) ) );
		chanceConfig.set("chance_percent.fox.red"								, normalize(oldchanceConfig.getDouble("chance_percent.fox.red", 10.0) ) );
		chanceConfig.set("chance_percent.fox.snow"								, normalize(oldchanceConfig.getDouble("chance_percent.fox.snow", 20.0) ) );
		chanceConfig.set("chance_percent.frog.cold"								, normalize(oldchanceConfig.getDouble("chance_percent.frog.cold", 20.0) ) );
		chanceConfig.set("chance_percent.frog.temperate"						, normalize(oldchanceConfig.getDouble("chance_percent.frog.temperate", 20.0) ) );
		chanceConfig.set("chance_percent.frog.warm"								, normalize(oldchanceConfig.getDouble("chance_percent.frog.warm", 20.0) ) );
		chanceConfig.set("chance_percent.ghast"									, normalize(oldchanceConfig.getDouble("chance_percent.ghast", 6.25) ) );
		chanceConfig.set("chance_percent.giant"									, normalize(oldchanceConfig.getDouble("chance_percent.giant", 2.5) ) );
		chanceConfig.set("chance_percent.glow_squid"							, normalize(oldchanceConfig.getDouble("chance_percent.glow_squid", 5.0) ) );
		chanceConfig.set("chance_percent.goat.mormal"							, normalize(oldchanceConfig.getDouble("chance_percent.goat.normal", 1.0) ) );
		chanceConfig.set("chance_percent.goat.screaming"						, normalize(oldchanceConfig.getDouble("chance_percent.goat.screaming", 100.0) ) );
		chanceConfig.set("chance_percent.guardian"								, normalize(oldchanceConfig.getDouble("chance_percent.guardian", 0.5) ) );
		chanceConfig.set("chance_percent.hoglin"								, normalize(oldchanceConfig.getDouble("chance_percent.hoglin", 3.0) ) );
		chanceConfig.set("chance_percent.horse.black"							, normalize(oldchanceConfig.getDouble("chance_percent.horse.black", 27.0) ) );
		chanceConfig.set("chance_percent.horse.brown"							, normalize(oldchanceConfig.getDouble("chance_percent.horse.brown", 27.0) ) );
		chanceConfig.set("chance_percent.horse.chestnut"						, normalize(oldchanceConfig.getDouble("chance_percent.horse.chestnut", 27.0) ) );
		chanceConfig.set("chance_percent.horse.creamy"							, normalize(oldchanceConfig.getDouble("chance_percent.horse.creamy", 27.0) ) );
		chanceConfig.set("chance_percent.horse.dark_brown"						, normalize(oldchanceConfig.getDouble("chance_percent.horse.dark_brown", 27.0) ) );
		chanceConfig.set("chance_percent.horse.gray"							, normalize(oldchanceConfig.getDouble("chance_percent.horse.gray", 27.0) ) );
		chanceConfig.set("chance_percent.horse.white"							, normalize(oldchanceConfig.getDouble("chance_percent.horse.white", 27.0) ) );
		chanceConfig.set("chance_percent.husk"									, normalize(oldchanceConfig.getDouble("chance_percent.husk", 6.0) ) );
		chanceConfig.set("chance_percent.illusioner"							, normalize(oldchanceConfig.getDouble("chance_percent.illusioner", 25.0) ) );
		chanceConfig.set("chance_percent.iron_golem"							, normalize(oldchanceConfig.getDouble("chance_percent.iron_golem", 5.0) ) );
		chanceConfig.set("chance_percent.llama.brown"							, normalize(oldchanceConfig.getDouble("chance_percent.llama.brown", 24.0) ) );
		chanceConfig.set("chance_percent.llama.creamy"							, normalize(oldchanceConfig.getDouble("chance_percent.llama.creamy", 24.0) ) );
		chanceConfig.set("chance_percent.llama.gray"							, normalize(oldchanceConfig.getDouble("chance_percent.llama.gray", 24.0) ) );
		chanceConfig.set("chance_percent.llama.white"							, normalize(oldchanceConfig.getDouble("chance_percent.llama.white", 24.0) ) );
		chanceConfig.set("chance_percent.magma_cube"							, normalize(oldchanceConfig.getDouble("chance_percent.magma_cube", 0.5) ) );
		chanceConfig.set("chance_percent.mule"									, normalize(oldchanceConfig.getDouble("chance_percent.mule", 20.0) ) );
		chanceConfig.set("chance_percent.mushroom_cow.red"						, normalize(oldchanceConfig.getDouble("chance_percent.mushroom_cow.red", 1.0) ) );
		chanceConfig.set("chance_percent.mushroom_cow.brown"					, normalize(oldchanceConfig.getDouble("chance_percent.mushroom_cow.brown", 10.0) ) );
		chanceConfig.set("chance_percent.ocelot"								, normalize(oldchanceConfig.getDouble("chance_percent.ocelot", 20.0) ) );
		chanceConfig.set("chance_percent.panda.aggressive"						, normalize(oldchanceConfig.getDouble("chance_percent.panda.aggressive", 27.0) ) );
		chanceConfig.set("chance_percent.panda.brown"							, normalize(oldchanceConfig.getDouble("chance_percent.panda.brown", 27.0) ) );
		chanceConfig.set("chance_percent.panda.lazy"							, normalize(oldchanceConfig.getDouble("chance_percent.panda.lazy", 27.0) ) );
		chanceConfig.set("chance_percent.panda.normal"							, normalize(oldchanceConfig.getDouble("chance_percent.panda.normal", 27.0) ) );
		chanceConfig.set("chance_percent.panda.playful"							, normalize(oldchanceConfig.getDouble("chance_percent.panda.playful", 27.0) ) );
		chanceConfig.set("chance_percent.panda.weak"							, normalize(oldchanceConfig.getDouble("chance_percent.panda.weak", 27.0) ) );
		chanceConfig.set("chance_percent.panda.worried"							, normalize(oldchanceConfig.getDouble("chance_percent.panda.worried", 27.0) ) );
		chanceConfig.set("chance_percent.parrot.blue"							, normalize(oldchanceConfig.getDouble("chance_percent.parrot.blue", 25.0) ) );
		chanceConfig.set("chance_percent.parrot.cyan"							, normalize(oldchanceConfig.getDouble("chance_percent.parrot.cyan", 25.0) ) );
		chanceConfig.set("chance_percent.parrot.gray"							, normalize(oldchanceConfig.getDouble("chance_percent.parrot.gray", 25.0) ) );
		chanceConfig.set("chance_percent.parrot.green"							, normalize(oldchanceConfig.getDouble("chance_percent.parrot.green", 25.0) ) );
		chanceConfig.set("chance_percent.parrot.red"							, normalize(oldchanceConfig.getDouble("chance_percent.parrot.red", 25.0) ) );
		chanceConfig.set("chance_percent.phantom"								, normalize(oldchanceConfig.getDouble("chance_percent.phantom", 10.0) ) );
		chanceConfig.set("chance_percent.pig"									, normalize(oldchanceConfig.getDouble("chance_percent.pig", 1.0) ) );
		chanceConfig.set("chance_percent.piglin"								, normalize(oldchanceConfig.getDouble("chance_percent.piglin", 4.0) ) );
		chanceConfig.set("chance_percent.pig_zombie"							, normalize(oldchanceConfig.getDouble("chance_percent.pig_zombie", 0.5) ) );
		chanceConfig.set("chance_percent.pillager"								, normalize(oldchanceConfig.getDouble("chance_percent.pillager", 2.5) ) );
		chanceConfig.set("chance_percent.polar_bear"							, normalize(oldchanceConfig.getDouble("chance_percent.polar_bear", 20.0) ) );
		chanceConfig.set("chance_percent.pufferfish"							, normalize(oldchanceConfig.getDouble("chance_percent.pufferfish", 15.0) ) );
		chanceConfig.set("chance_percent.rabbit.black"							, normalize(oldchanceConfig.getDouble("chance_percent.rabbit.black", 26.0) ) );
		chanceConfig.set("chance_percent.rabbit.black_and_white"				, normalize(oldchanceConfig.getDouble("chance_percent.rabbit.black_and_white", 26.0) ) );
		chanceConfig.set("chance_percent.rabbit.brown"							, normalize(oldchanceConfig.getDouble("chance_percent.rabbit.brown", 26.0) ) );
		chanceConfig.set("chance_percent.rabbit.gold"							, normalize(oldchanceConfig.getDouble("chance_percent.rabbit.gold", 26.0) ) );
		chanceConfig.set("chance_percent.rabbit.salt_and_pepper"				, normalize(oldchanceConfig.getDouble("chance_percent.rabbit.salt_and_pepper", 26.0) ) );
		chanceConfig.set("chance_percent.rabbit.the_killer_bunny"				, normalize(oldchanceConfig.getDouble("chance_percent.rabbit.the_killer_bunny", 100.0) ) );
		chanceConfig.set("chance_percent.rabbit.toast"							, normalize(oldchanceConfig.getDouble("chance_percent.rabbit.toast", 26.0) ) );
		chanceConfig.set("chance_percent.rabbit.white"							, normalize(oldchanceConfig.getDouble("chance_percent.rabbit.white", 26.0) ) );
		chanceConfig.set("chance_percent.ravager"								, normalize(oldchanceConfig.getDouble("chance_percent.ravager", 25.0) ) );
		chanceConfig.set("chance_percent.salmon"								, normalize(oldchanceConfig.getDouble("chance_percent.salmon", 10.0) ) );
		chanceConfig.set("chance_percent.sheep.black"							, normalize(oldchanceConfig.getDouble("chance_percent.sheep.black", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.blue"							, normalize(oldchanceConfig.getDouble("chance_percent.sheep.blue", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.brown"							, normalize(oldchanceConfig.getDouble("chance_percent.sheep.brown", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.cyan"							, normalize(oldchanceConfig.getDouble("chance_percent.sheep.cyan", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.gray"							, normalize(oldchanceConfig.getDouble("chance_percent.sheep.gray", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.green"							, normalize(oldchanceConfig.getDouble("chance_percent.sheep.green", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.jeb_"							, normalize(oldchanceConfig.getDouble("chance_percent.sheep.jeb_", 10.0) ) );
		chanceConfig.set("chance_percent.sheep.light_blue"						, normalize(oldchanceConfig.getDouble("chance_percent.sheep.light_blue", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.light_gray"						, normalize(oldchanceConfig.getDouble("chance_percent.sheep.light_gray", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.lime"							, normalize(oldchanceConfig.getDouble("chance_percent.sheep.lime", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.magenta"							, normalize(oldchanceConfig.getDouble("chance_percent.sheep.magenta", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.orange"							, normalize(oldchanceConfig.getDouble("chance_percent.sheep.orange", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.pink"							, normalize(oldchanceConfig.getDouble("chance_percent.sheep.pink", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.purple"							, normalize(oldchanceConfig.getDouble("chance_percent.sheep.purple", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.red"								, normalize(oldchanceConfig.getDouble("chance_percent.sheep.red", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.white"							, normalize(oldchanceConfig.getDouble("chance_percent.sheep.white", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.yellow"							, normalize(oldchanceConfig.getDouble("chance_percent.sheep.yellow", 1.75) ) );
		chanceConfig.set("chance_percent.shulker"								, normalize(oldchanceConfig.getDouble("chance_percent.shulker", 5.0) ) );
		chanceConfig.set("chance_percent.silverfish"							, normalize(oldchanceConfig.getDouble("chance_percent.silverfish", 5.0) ) );
		chanceConfig.set("chance_percent.skeleton"								, normalize(oldchanceConfig.getDouble("chance_percent.skeleton", 2.5) ) );
		chanceConfig.set("chance_percent.skeleton_horse"						, normalize(oldchanceConfig.getDouble("chance_percent.skeleton_horse", 20.0) ) );
		chanceConfig.set("chance_percent.slime"									, normalize(oldchanceConfig.getDouble("chance_percent.slime", 0.5) ) );
		chanceConfig.set("chance_percent.sniffer"								, normalize(oldchanceConfig.getDouble("chance_percent.sniffer", 50.0) ) );
		chanceConfig.set("chance_percent.snowman"								, normalize(oldchanceConfig.getDouble("chance_percent.snowman", 5.0) ) );
		chanceConfig.set("chance_percent.spider"								, normalize(oldchanceConfig.getDouble("chance_percent.spider", 0.5) ) );
		chanceConfig.set("chance_percent.squid"									, normalize(oldchanceConfig.getDouble("chance_percent.squid", 5.0) ) );
		chanceConfig.set("chance_percent.stray"									, normalize(oldchanceConfig.getDouble("chance_percent.stray", 6.0) ) );
		chanceConfig.set("chance_percent.strider"								, normalize(oldchanceConfig.getDouble("chance_percent.strider", 10.0) ) );
		chanceConfig.set("chance_percent.tadpole"								, normalize(oldchanceConfig.getDouble("chance_percent.tadpole", 10.0) ) );
		chanceConfig.set("chance_percent.trader_llama.brown"					, normalize(oldchanceConfig.getDouble("chance_percent.trader_llama.brown", 24.0) ) );
		chanceConfig.set("chance_percent.trader_llama.creamy"					, normalize(oldchanceConfig.getDouble("chance_percent.trader_llama.creamy", 24.0) ) );
		chanceConfig.set("chance_percent.trader_llama.gray"						, normalize(oldchanceConfig.getDouble("chance_percent.trader_llama.gray", 24.0) ) );
		chanceConfig.set("chance_percent.trader_llama.white"					, normalize(oldchanceConfig.getDouble("chance_percent.trader_llama.white", 24.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.tropical_fish"			, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.tropical_fish", 10.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.anemone"					, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.anemone", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.black_tang"				, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.black_tang", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.blue_tang"				, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.blue_tang", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.butterflyfish"			, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.butterflyfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.cichlid"					, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.cichlid", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.clownfish"				, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.clownfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.cotton_candy_betta"		, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.cotton_candy_betta", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.dottyback"				, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.dottyback", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.emperor_red_snapper"		, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.emperor_red_snapper", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.goatfish"				, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.goatfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.moorish_idol"			, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.moorish_idol", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.ornate_butterflyfish"	, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.ornate_butterflyfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.parrotfish"				, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.parrotfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.queen_angelfish"			, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.queen_angelfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.red_cichlid"				, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.red_cichlid", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.red_lipped_blenny"		, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.red_lipped_blenny", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.red_snapper"				, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.red_snapper", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.threadfin"				, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.threadfin", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.tomato_clownfish"		, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.tomato_clownfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.triggerfish"				, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.triggerfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.yellowtail_parrotfish"	, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.yellow_parrotfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.yellow_tang"				, normalize(oldchanceConfig.getDouble("chance_percent.tropical_fish.yellow_tang", 50.0) ) );

		chanceConfig.set("chance_percent.turtle"							, normalize(oldchanceConfig.getDouble("chance_percent.turtle", 10.0) ) );
		chanceConfig.set("chance_percent.vex"								, normalize(oldchanceConfig.getDouble("chance_percent.vex", 10.0) ) );
		chanceConfig.set("chance_percent.villager.desert.armorer"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.desert.armorer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.butcher"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.desert.butcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.cartographer"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.desert.cartographer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.cleric"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.desert.cleric", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.farmer"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.desert.farmer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.fisherman"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.desert.fisherman", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.fletcher"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.desert.fletcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.leatherworker"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.desert.leatherworker", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.librarian"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.desert.librarian", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.mason"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.desert.mason", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.nitwit"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.desert.nitwit", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.none"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.desert.none", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.shepherd"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.desert.shepherd", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.toolsmith"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.desert.toolsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.weaponsmith"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.desert.weaponsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.armorer"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.jungle.armorer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.butcher"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.jungle.butcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.cartographer"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.jungle.cartographer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.cleric"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.jungle.cleric", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.farmer"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.jungle.farmer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.fisherman"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.jungle.fisherman", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.fletcher"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.jungle.fletcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.leatherworker"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.jungle.leatherworker", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.librarian"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.jungle.librarian", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.mason"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.jungle.mason", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.nitwit"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.jungle.nitwit", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.none"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.jungle.none", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.shepherd"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.jungle.shepherd", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.toolsmith"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.jungle.toolsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.weaponsmith"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.jungle.weaponsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.armorer"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.plains.armorer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.butcher"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.plains.butcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.cartographer"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.plains.cartographer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.cleric"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.plains.cleric", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.farmer"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.plains.farmer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.fisherman"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.plains.fisherman", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.fletcher"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.plains.fletcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.leatherworker"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.plains.leatherworker", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.librarian"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.plains.librarian", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.mason"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.plains.mason", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.nitwit"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.plains.nitwit", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.none"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.plains.none", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.shepherd"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.plains.shepherd", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.toolsmith"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.plains.toolsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.weaponsmith"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.plains.weaponsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.armorer"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.savanna.armorer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.butcher"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.savanna.butcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.cartographer"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.savanna.cartographer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.cleric"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.savanna.cleric", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.farmer"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.savanna.farmer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.fisherman"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.savanna.fisherman", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.fletcher"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.savanna.fletcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.leatherworker"	, normalize(oldchanceConfig.getDouble("chance_percent.villager.savanna.leatherworker", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.librarian"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.savanna.librarian", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.mason"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.savanna.mason", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.nitwit"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.savanna.nitwit", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.none"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.savanna.none", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.shepherd"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.savanna.shepherd", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.toolsmith"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.savanna.toolsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.weaponsmith"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.savanna.weaponsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.armorer"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.snow.armorer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.butcher"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.snow.butcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.cartographer"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.snow.cartographer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.cleric"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.snow.cleric", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.farmer"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.snow.farmer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.fisherman"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.snow.fisherman", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.fletcher"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.snow.fletcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.leatherworker"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.snow.leatherworker", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.librarian"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.snow.librarian", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.mason"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.snow.mason", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.nitwit"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.snow.nitwit", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.none"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.snow.none", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.shepherd"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.snow.shepherd", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.toolsmith"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.snow.toolsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.weaponsmith"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.snow.weaponsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.armorer"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.swamp.armorer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.butcher"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.swamp.butcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.cartographer"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.swamp.cartographer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.cleric"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.swamp.cleric", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.farmer"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.swamp.farmer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.fisherman"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.swamp.fisherman", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.fletcher"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.swamp.fletcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.leatherworker"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.swamp.leatherworker", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.librarian"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.swamp.librarian", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.mason"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.swamp.mason", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.nitwit"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.swamp.nitwit", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.none"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.swamp.none", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.shepherd"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.swamp.shepherd", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.toolsmith"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.swamp.toolsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.weaponsmith"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.swamp.weaponsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.armorer"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.taiga.armorer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.butcher"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.taiga.butcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.cartographer"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.taiga.cartographer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.cleric"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.taiga.cleric", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.farmer"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.taiga.farmer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.fisherman"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.taiga.fisherman", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.fletcher"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.taiga.fletcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.leatherworker"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.taiga.leatherworker", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.librarian"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.taiga.librarian", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.mason"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.taiga.mason", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.nitwit"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.taiga.nitwit", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.none"				, normalize(oldchanceConfig.getDouble("chance_percent.villager.taiga.none", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.shepherd"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.taiga.shepherd", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.toolsmith"			, normalize(oldchanceConfig.getDouble("chance_percent.villager.taiga.toolsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.weaponsmith"		, normalize(oldchanceConfig.getDouble("chance_percent.villager.taiga.weaponsmith", 100.0) ) );
		chanceConfig.set("chance_percent.vindicator"						, normalize(oldchanceConfig.getDouble("chance_percent.vindicator", 5.0) ) );
		chanceConfig.set("chance_percent.wandering_trader"					, normalize(oldchanceConfig.getDouble("chance_percent.wandering_trader", 100.0) ) );
		chanceConfig.set("chance_percent.warden"							, normalize(oldchanceConfig.getDouble("chance_percent.warden", 100.0) ) );
		chanceConfig.set("chance_percent.witch"								, normalize(oldchanceConfig.getDouble("chance_percent.witch", 0.5) ) );
		chanceConfig.set("chance_percent.wither"							, normalize(oldchanceConfig.getDouble("chance_percent.wither", 100.0) ) );
		chanceConfig.set("chance_percent.wither_skeleton"					, normalize(oldchanceConfig.getDouble("chance_percent.wither_skeleton", 2.5) ) );
		chanceConfig.set("chance_percent.wolf.ashen"						, normalize(oldchanceConfig.getDouble("chance_percent.wolf.ashen", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.black"						, normalize(oldchanceConfig.getDouble("chance_percent.wolf.black", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.chestnut"						, normalize(oldchanceConfig.getDouble("chance_percent.wolf.chestnut", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.pale"							, normalize(oldchanceConfig.getDouble("chance_percent.wolf.pale", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.rusty"						, normalize(oldchanceConfig.getDouble("chance_percent.wolf.rusty", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.snowy"						, normalize(oldchanceConfig.getDouble("chance_percent.wolf.snowy", 50.0) ) );
		chanceConfig.set("chance_percent.wolf.spotted"						, normalize(oldchanceConfig.getDouble("chance_percent.wolf.spotted", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.striped"						, normalize(oldchanceConfig.getDouble("chance_percent.wolf.striped", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.woods"						, normalize(oldchanceConfig.getDouble("chance_percent.wolf.woods", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_ashen"					, normalize(oldchanceConfig.getDouble("chance_percent.wolf.angry_ashen", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_black"					, normalize(oldchanceConfig.getDouble("chance_percent.wolf.angry_black", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_chestnut"				, normalize(oldchanceConfig.getDouble("chance_percent.wolf.angry_chestnut", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_pale"					, normalize(oldchanceConfig.getDouble("chance_percent.wolf.angry_pale", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_rusty"					, normalize(oldchanceConfig.getDouble("chance_percent.wolf.angry_rusty", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_snowy"					, normalize(oldchanceConfig.getDouble("chance_percent.wolf.angry_snowy", 50.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_spotted"				, normalize(oldchanceConfig.getDouble("chance_percent.wolf.angry_spotted", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_striped"				, normalize(oldchanceConfig.getDouble("chance_percent.wolf.angry_striped", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_woods"					, normalize(oldchanceConfig.getDouble("chance_percent.wolf.angry_woods", 20.0) ) );
		chanceConfig.set("chance_percent.zoglin"							, normalize(oldchanceConfig.getDouble("chance_percent.zoglin", 20.0) ) );
		chanceConfig.set("chance_percent.zombie"							, normalize(oldchanceConfig.getDouble("chance_percent.zombie", 2.5) ) );
		chanceConfig.set("chance_percent.zombie_horse"						, normalize(oldchanceConfig.getDouble("chance_percent.zombie_horse", 100.0) ) );
		chanceConfig.set("chance_percent.zombie_pigman"						, normalize(oldchanceConfig.getDouble("chance_percent.zombie_pigman", 0.5) ) );
		chanceConfig.set("chance_percent.zombified_piglin"					, normalize(oldchanceConfig.getDouble("chance_percent.zombified_piglin", 0.5) ) );
		chanceConfig.set("chance_percent.zombie_villager"					, normalize(oldchanceConfig.getDouble("chance_percent.zombie_villager", 50.0) ) );
		try {
			chanceConfig.save(to);
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CHANCE_SAVE_ERROR).error(exception));
		}
		LOGGER.log("chance_config.yml has been updated!");
		oldchanceConfig = null;
	}

	public void copyConfigValues(String currentFilePath, String oldFilePath) {
		// Load configurations
		YmlConfiguration currentConfig = new YmlConfiguration();
		YmlConfiguration oldConfig = new YmlConfiguration();

		try {
			currentConfig.load(new File(currentFilePath));
			oldConfig.load(new File(oldFilePath));
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
			return;
		}

		// Copy values from oldConfig to currentConfig
		for (String key : currentConfig.getKeys(true)) {
			if (!key.equals("version") && oldConfig.isSet(key)) {
				currentConfig.set(key, oldConfig.get(key));
			}
		}

		// Save the updated currentConfig
		try {
			currentConfig.save(new File(currentFilePath));
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_SAVE_CONFIG).error(exception));
		} finally {
			// Release resources
			currentConfig = null;
			oldConfig = null;
		}
	}


	//@SuppressWarnings("unused")
	//private final PersistentDataType LORE_PDT2 = new JsonDataType<>(String.class);
	//private final NamespacedKey DISPLAY_KEY = new NamespacedKey(this, "head_display");
	//private final NamespacedKey SKULLOWNER_KEY = new NamespacedKey(this, "head_skullowner");

	// TODO: Persistent Heads
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		SkinUtils skinUtils = new SkinUtils();
		try {
			@Nonnull ItemStack headItem = event.getItemInHand();
			if (headItem.getType() != Material.PLAYER_HEAD) {
				return;
			}
			ItemMeta meta = headItem.getItemMeta();
			if (meta == null) {
				return;
			}
			@Nonnull String name = meta.getDisplayName();
			LOGGER.debug("BPE name = " + name);
			@Nullable List<String> lore = meta.getLore();
			LOGGER.debug("BPE lore = " + lore);
			//PersistentDataContainer pdc = headItem.getItemMeta().getPersistentDataContainer();
			String uuid = skinUtils.getHeadUUID(headItem);
			String texture = skinUtils.getHeadTexture(headItem); // pdc.get(TEXTURE_KEY, PersistentDataType.STRING);
			NamespacedKey nskSound = skinUtils.getHeadNoteblockSound(headItem);
			String sound = null;
			if(nskSound != null) { sound = nskSound.toString(); }
			@Nonnull Block block = event.getBlockPlaced();
			// NOTE: Not using snapshots is broken: https://github.com/PaperMC/Paper/issues/3913
			//BlockStateSnapshotResult blockStateSnapshotResult = PaperLib.getBlockState(block, true);
			TileState skullState = (TileState) block.getState();
			@Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
			skullPDC.set(NAME_KEY, PersistentDataType.STRING, name);
			if (lore != null) { skullPDC.set(LORE_KEY, LORE_PDT, lore.toArray(new String[0])); }
			if (uuid != null) { skullPDC.set(UUID_KEY, PersistentDataType.STRING, uuid); }
			if (texture != null) { skullPDC.set(TEXTURE_KEY, PersistentDataType.STRING, texture); }
			if (sound != null) { skullPDC.set(SOUND_KEY, PersistentDataType.STRING, sound); }

			skullState.update();
			String strLore = "no lore";
			if(lore != null){ strLore = lore.toString(); }
			LOGGER.debug("Player " + event.getPlayer().getName() + " placed a head named \"" + name + "\" with lore=\'" + strLore + "\' at " + event.getBlockPlaced().getLocation());
		}catch(Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.BLOCK_PLACE_EVENT_ERROR).error(exception));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockDropItemEvent(BlockDropItemEvent event) {
		@Nonnull BlockState blockState = event.getBlockState();
		Material blockType = blockState.getType();
		if ((blockType != Material.PLAYER_HEAD) && (blockType != Material.PLAYER_WALL_HEAD)) {
			return;
		}
		TileState skullState = (TileState) blockState;
		@Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
		@Nullable String name = skullPDC.get(NAME_KEY, PersistentDataType.STRING);
		if(name != null){ LOGGER.debug("BDIE name = " + name); }
		@Nullable String[] lore = skullPDC.get(LORE_KEY, LORE_PDT);
		if(lore != null){ LOGGER.debug("BDIE lore = " + lore.toString()); }
		String uuid = skullPDC.get(UUID_KEY, PersistentDataType.STRING);
		if(uuid != null){ LOGGER.debug("BDIE uuid = " + uuid); }
		String texture = skullPDC.get(TEXTURE_KEY, PersistentDataType.STRING);
		if(texture != null){ LOGGER.debug("BDIE texture = " + texture); }
		String sound = skullPDC.get(SOUND_KEY, PersistentDataType.STRING);
		if(sound != null){ LOGGER.debug("BDIE sound = " + sound); }
		if (name == null) {
			return;
		}
		for (Item item: event.getItems()) { // Ideally should only be one...
			@Nonnull ItemStack itemstack = item.getItemStack();
			if (itemstack.getType() == Material.PLAYER_HEAD) {
				@Nullable ItemMeta meta = itemstack.getItemMeta();
				if (meta == null)
				{
					continue; // This shouldn't happen
				}
				meta.setDisplayName(name);
				if (lore != null) {
					meta.setLore(Arrays.asList(lore));
				}
				skullPDC.set(NAME_KEY, PersistentDataType.STRING, name);
				if (lore != null) { skullPDC.set(LORE_KEY, LORE_PDT, lore); }
				if (uuid != null) { skullPDC.set(UUID_KEY, PersistentDataType.STRING, uuid); }
				if (texture != null) { skullPDC.set(TEXTURE_KEY, PersistentDataType.STRING, texture); }
				if (sound != null) { skullPDC.set(SOUND_KEY, PersistentDataType.STRING, sound); }
				itemstack.setItemMeta(meta);
				itemstack.setItemMeta(meta);
			}
		}
		LOGGER.debug("BDIE - Persistent head completed.");
	}

	/**
	 * Prevents player from removing player-head NBT by water logging them
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		handleBlock(event.getBlock(), event, false);
	}

	/**
	 * Prevents player from removing player-head NBT using running water
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onLiquidFlow(BlockFromToEvent event) {
		handleBlock(event.getToBlock(), event, true);
	}

	/*
	 * Prevents explosion from removing player-head NBT using an explosion
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockExplosion(BlockExplodeEvent event) {
		handleExplosionEvent(event.blockList(), event.getYield());
	}

	/*
	 * Prevents entity from removing player-head NBT using an explosion
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityExplosion(EntityExplodeEvent event) {
		handleExplosionEvent(event.blockList(), event.getYield());
	}

	/*
	 * Prevents piston extending from removing NBT data.
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPistonExtendEvent(BlockPistonExtendEvent event) {
		if(!config.getBoolean("global_settings.event.piston_extend", true)) {
			return;
		}
		List<Block> blocks = event.getBlocks();
		Iterator<Block> iter = blocks.iterator();
		try {
			while (iter.hasNext()) {
				Block block = iter.next();
				if (block.getState() instanceof Skull) { //if (block.getState() instanceof Skull && random.nextFloat() <= explosionYield)
					handleBlock(block, null, false);
					iter.remove();
				}
			}
		}catch(Exception ignored) {

		}
	}

	@SuppressWarnings("unused")
	private void handleExplosionEvent(@Nonnull final List<Block> blocksExploded, final float explosionYield) {
		final Random random = ThreadLocalRandom.current();
		Iterator<Block> iter = blocksExploded.iterator();
		try {
			while (iter.hasNext()) {
				Block block = iter.next();
				if (block.getState() instanceof Skull) { //if (block.getState() instanceof Skull && random.nextFloat() <= explosionYield)
					handleBlock(block, null, false);
					iter.remove();
				}
			}
		}catch(Exception ignored) {

		}
	}

	private void handleBlock(Block block, Cancellable event, boolean cancelEvent) {
		@Nonnull BlockState blockState = block.getState();
		if ((blockState.getType() != Material.PLAYER_HEAD) && (blockState.getType() != Material.PLAYER_WALL_HEAD)) {
			return;
		}
		Skull skullState = (Skull) blockState;
		@Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
		@Nullable String name = skullPDC.get(NAME_KEY, PersistentDataType.STRING);
		@Nullable String[] lore = skullPDC.get(LORE_KEY, LORE_PDT);
		String uuid = skullPDC.get(UUID_KEY, PersistentDataType.STRING);
		String texture = skullPDC.get(TEXTURE_KEY, PersistentDataType.STRING);
		String sound = skullPDC.get(SOUND_KEY, PersistentDataType.STRING);
		if (name == null) {
			return;
		}
		@Nonnull Optional<ItemStack> skullStack = block.getDrops().stream().filter(is -> is.getType() == Material.PLAYER_HEAD).findAny();
		if (skullStack.isPresent()) {
			if (updateDrop(block, name, lore, uuid, texture, sound, skullStack.get()))
			{
				return; // This shouldn't happen
			}
			if (cancelEvent) {
				event.setCancelled(true);
			}
		}

		BlockState blockState1 = block.getWorld().getBlockAt(block.getLocation()).getState();
		blockState1.update(true, true);
		LOGGER.debug("HB - Persistent head completed.");
	}

	/**@EventHandler(priority = EventPriority.LOWEST)
    public void onBlockExplodeEvent(BlockExplodeEvent event) {
		List<Block> a = event.blockList();
		for (Block block: event.blockList()) { // Ideally should only be one...
			@Nonnull BlockState blockState = block.getState();
	        Material blockType = blockState.getType();
	        if (blockType != Material.PLAYER_HEAD && blockType != Material.PLAYER_WALL_HEAD) return;
	        TileState skullState = (TileState) blockState;
	        @Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
	        @Nullable String name = skullPDC.get(NAME_KEY, PersistentDataType.STRING);
	        @Nullable String[] lore = skullPDC.get(LORE_KEY, LORE_PDT);
	        if (name == null) return;
	        block.breakNaturally();
		}

    }//*/

	@SuppressWarnings("unused")
	private void handleEvent(Supplier<Block> blockSupplier, Cancellable event, boolean cancelEvent) {
		Block block = blockSupplier.get();
		@Nonnull BlockState blockState = block.getState();
		if ((blockState.getType() != Material.PLAYER_HEAD) && (blockState.getType() != Material.PLAYER_WALL_HEAD)) {
			return;
		}
		Skull skullState = (Skull) blockState;
		@Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
		@Nullable String name = skullPDC.get(NAME_KEY, PersistentDataType.STRING);
		@Nullable String[] lore = skullPDC.get(LORE_KEY, LORE_PDT);

		String uuid = skullPDC.get(UUID_KEY, PersistentDataType.STRING);
		String texture = skullPDC.get(TEXTURE_KEY, PersistentDataType.STRING);
		String sound = skullPDC.get(SOUND_KEY, PersistentDataType.STRING);
		if (name == null) {
			return;
		}
		@Nonnull Optional<ItemStack> skullStack = block.getDrops().stream().filter(is -> is.getType() == Material.PLAYER_HEAD).findAny();
		if (skullStack.isPresent()) {
			if (updateDrop(block, name, lore, uuid, texture, sound, skullStack.get()))
			{
				return; // This shouldn't happen
			}
			if (cancelEvent) {
				event.setCancelled(true);
			}
		}

		BlockState blockState1 = block.getWorld().getBlockAt(block.getLocation()).getState();
		blockState1.update(true, true);
		LOGGER.debug("HE - Persistent head completed.");
	}

	private boolean updateDrop(Block block, @Nullable String name, @Nullable String[] lore, String uuid, String texture, String sound, @Nonnull ItemStack itemstack) {
		@Nullable ItemMeta meta = itemstack.getItemMeta();
		if (meta == null) {
			return true;
		}
		meta.setDisplayName(name);
		if (lore != null) {
			meta.setLore(Arrays.asList(lore));
		}

		TileState skullState = (TileState) block.getState();
		@Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
		skullPDC.set(NAME_KEY, PersistentDataType.STRING, name);
		if (lore != null) {
			skullPDC.set(LORE_KEY, LORE_PDT, lore);
		}
		skullPDC.set(UUID_KEY, PersistentDataType.STRING, uuid);
		skullPDC.set(TEXTURE_KEY, PersistentDataType.STRING, texture);
		skullPDC.set(SOUND_KEY, PersistentDataType.STRING, sound);
		itemstack.setItemMeta(meta);

		block.getWorld().dropItemNaturally(block.getLocation(), itemstack);
		block.getDrops().clear();
		block.setType(Material.AIR);
		LOGGER.debug("UD - Persistent head completed.");
		return false;
	}
	// Persistent Heads

	/**@EventHandler()
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
    }// */

	@SuppressWarnings("unused")
	public ItemStack fixHeadStack(ItemStack offHand, ItemStack mainHand){
		NBTItem nbti = new NBTItem(offHand);
		Set<String> SkullKeys = nbti.getKeys();
		int damage = nbti.getInteger("Damage");
		NBTCompound display = nbti.getCompound("display");
		NBTCompound SkullOwner = nbti.getCompound("SkullOwner");
		LOGGER.debug("FHS Offhand damage=" + damage);
		LOGGER.debug("FHS Offhand display=" + display.toString());
		LOGGER.debug("FHS Offhand SkullOwner=" + SkullOwner.toString());

		NBTItem nbti2 = new NBTItem(mainHand);
		Set<String> SkullKeys2 = nbti2.getKeys();
		int damage2 = nbti2.getInteger("Damage");
		NBTCompound display2 = nbti2.getCompound("display");
		NBTCompound SkullOwner2 = nbti2.getCompound("SkullOwner");
		LOGGER.debug("FHS Mainhand damage=" + damage2);
		LOGGER.debug("FHS Mainhand display=" + display2.toString());
		LOGGER.debug("FHS Mainhand SkullOwner=" + SkullOwner2.toString());

		if( display.equals(display2) && SkullOwner.equals(SkullOwner2) && (damage != damage2)){
			ItemStack is = new ItemStack(offHand);
			is.setAmount(mainHand.getAmount());
			LOGGER.debug("FHS d=d2, so=so2, d!=D2 - return offhand");
			return is;
		}else if( !display.equals(display2) && SkullOwner.equals(SkullOwner2) && ((damage == damage2)||(damage != damage2))){
			ItemStack is = new ItemStack(offHand);
			is.setAmount(mainHand.getAmount());
			LOGGER.debug("FHS d!=d2, so=so2, d ignored - return offhand");
			return is;
		}else if( display.equals(display2) && SkullOwner.equals(SkullOwner2) && (damage == damage2)){
			LOGGER.debug("FHS d=d2, so=so2, d=d2 - return mainhand");
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
			if(config.getBoolean("head_settings.lore.show_plugin_name", true)){
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

	public ItemStack makeHead(String textureValue, String displayName, String uuid, @Nullable ArrayList<String> lore) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1); // Creating the ItemStack, your input may vary.
		NBTItem nbti = new NBTItem(head); // Creating the wrapper.

		NBTCompound disp = nbti.addCompound("display");
		disp.setString("Name", displayName); // Setting the name of the Item

		if(lore.isEmpty()) {
			if(config.getBoolean("head_settings.lore.show_plugin_name", true)){
				lore.add(ChatColor.AQUA + "MoreMobHeads");
			}
		}
		if(!lore.isEmpty()) {
			NBTList l = disp.getStringList("Lore");
			l.add(lore); // Adding a bit of lore.
		}

		NBTCompound skull = nbti.addCompound("SkullOwner"); // Getting the compound, that way we can set the skin information
		skull.setString("Name", displayName); // Owner's name
		skull.setString("Id", uuid);

		NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
		texture.setString("Value",  textureValue);

		head = nbti.getItem(); // Refresh the ItemStack
		return head;
	}

	public String getTextureFromEntity(LivingEntity entity) {
		String name = entity.getName().toUpperCase();
		switch (name) {
		case "CREEPER":
			Creeper creeper = (Creeper) entity;
			double cchance = chanceConfig.getDouble("chance_percent.creeper", defpercent);
			if(creeper.isPowered()) {
				name = "CREEPER_CHARGED";
				cchance = 1.00;
			}
			if(DropIt2(cchance)){
				return MobHeads.valueOf(name).getTexture().toString();
			}
			break;
		case "ZOMBIE":
			if(DropIt2(chanceConfig.getDouble("chance_percent.zombie", defpercent))){
				return MobHeads.valueOf(name).getTexture().toString();
			}
			break;
		case "SKELETON":
			if(DropIt2(chanceConfig.getDouble("chance_percent.skeleton", defpercent))){
				return MobHeads.valueOf(name).getTexture().toString();
			}
			break;
		case "WITHER_SKELETON":
			if(DropIt2(chanceConfig.getDouble("chance_percent.wither_skeleton", defpercent))){
				return MobHeads.valueOf(name).getTexture().toString();
			}
			break;
		case "ENDER_DRAGON":
			if(DropIt2(chanceConfig.getDouble("chance_percent.ender_dragon", defpercent))){
				return MobHeads.valueOf(name).getTexture().toString();
			}
			break;
		case "TROPICAL_FISH":
			TropicalFish daFish = (TropicalFish) entity;
			DyeColor daFishBody = daFish.getBodyColor();
			DyeColor daFishPatternColor = daFish.getPatternColor();
			Pattern	daFishPattern = daFish.getPattern();
			String daFishType = getNamedTropicalFishName(daFishPattern, daFishBody, daFishPatternColor );
			if(DropIt2(chanceConfig.getDouble("TROPICAL_FISH." + daFishType, defpercent))){
				return TropicalFishHeads.valueOf(daFishType).getTexture().toString();
			}
			LOGGER.debug("Skeleton Head Dropped");
			break;//*/
		case "WITHER":
			//Wither wither = (Wither) event.getEntity();
			int random = randomBetween(1, 4);
			if(DropIt2(chanceConfig.getDouble("chance_percent.wither", defpercent))){
				return MobHeads.valueOf(name + "_" + random).getTexture().toString();
			}
			break;
		case "WOLF":
			Wolf wolf = (Wolf) entity;
			if(DropIt2(chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))){
				if(wolf.isAngry()){
					return MobHeads.valueOf(name + "_ANGRY").getTexture().toString();
				}else{
					return MobHeads.valueOf(name).getTexture().toString();
				}
			}
			break;
		case "FOX":
			Fox dafox = (Fox) entity;
			String dafoxtype = dafox.getFoxType().toString();
			if(DropIt2(chanceConfig.getDouble("chance_percent.fox." + dafoxtype.toString().toLowerCase(), defpercent))){
				return MobHeads.valueOf(name + "_" + dafoxtype).getTexture().toString();
			}

			break;
		case "CAT":
			Cat dacat = (Cat) entity;
			String dacattype = dacat.getCatType().toString();
			if(DropIt2(chanceConfig.getDouble("chance_percent.cat." + dacattype.toLowerCase(), defpercent))){
				return CatHeads.valueOf(dacattype).getTexture().toString();
			}
			break;
		case "OCELOT":
			if(DropIt2(chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))){
				return MobHeads.valueOf(name).getTexture().toString();
			}

			break;
		case "BEE":
			Bee daBee = (Bee) entity;
			int daAnger = daBee.getAnger();
			boolean daNectar = daBee.hasNectar();
			if((daAnger >= 1)&&(daNectar == true)){
				if(DropIt2(chanceConfig.getDouble("chance_percent.bee.angry_pollinated", defpercent))){
					return MobHeads.valueOf("BEE_ANGRY_POLLINATED").getTexture().toString();
				}
			}else if((daAnger >= 1)&&(daNectar == false)){
				if(DropIt2(chanceConfig.getDouble("chance_percent.bee.angry", defpercent))){
					return MobHeads.valueOf("BEE_ANGRY").getTexture().toString();
				}
			}else if((daAnger == 0)&&(daNectar == true)){
				if(DropIt2(chanceConfig.getDouble("chance_percent.bee.pollinated", defpercent))){
					return MobHeads.valueOf("BEE_POLLINATED").getTexture().toString();
				}
			}else if((daAnger == 0)&&(daNectar == false)){
				if(DropIt2(chanceConfig.getDouble("chance_percent.bee.chance_percent", defpercent))){
					return MobHeads.valueOf("BEE").getTexture().toString();
				}
			}
			break;
		case "LLAMA":
			Llama daLlama = (Llama) entity;
			String daLlamaColor = daLlama.getColor().toString();
			//log(name + "_" + daLlamaColor);
			if(DropIt2(chanceConfig.getDouble("chance_percent.llama." + daLlamaColor.toLowerCase(), defpercent))){
				return LlamaHeads.valueOf(name + "_" + daLlamaColor).getTexture().toString();
			}
			break;
		case "HORSE":
			Horse daHorse = (Horse) entity;
			String daHorseColor = daHorse.getColor().toString();
			//.replace("g", "G").replace("wh", "Wh").replace("_", " ") + " Horse Head";
			if(DropIt2(chanceConfig.getDouble("chance_percent.horse." + daHorseColor.toLowerCase(), defpercent))){
				return HorseHeads.valueOf(name + "_" + daHorseColor).getTexture().toString();
			}
			break;
		case "MOOSHROOM":
			name = "MUSHROOM_COW";
		case "MUSHROOM_COW":
			MushroomCow daMushroom = (MushroomCow) entity;
			String daCowVariant = daMushroom.getVariant().toString();
			if(DropIt2(chanceConfig.getDouble("chance_percent.mushroom_cow." + daCowVariant.toLowerCase(), defpercent))){
				return MobHeads.valueOf(name + "_" + daCowVariant).getTexture().toString();
			}
			break;
		case "PANDA":
			Panda daPanda = (Panda) entity;
			String daPandaGene = daPanda.getMainGene().toString();
			String daPandaName = daPandaGene.toLowerCase().replace("br", "Br").replace("ag", "Ag").replace("la", "La")
					.replace("no", "No").replace("p", "P").replace("we", "We").replace("wo", "Wo") + " Panda Head";
			if(daPandaGene.equalsIgnoreCase("normal")){daPandaName.replace("normal ", "");}
			if(DropIt2(chanceConfig.getDouble("chance_percent.panda." + daPandaGene.toLowerCase(), defpercent))){
				return MobHeads.valueOf(name + "_" + daPandaGene).getTexture().toString();
			}
			break;
		case "PARROT":
			Parrot daParrot = (Parrot) entity;
			String daParrotVariant = daParrot.getVariant().toString();
			if(DropIt2(chanceConfig.getDouble("chance_percent.parrot." + daParrotVariant.toLowerCase(), defpercent))){
				return MobHeads.valueOf(name + "_" + daParrotVariant).getTexture().toString();
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
			if(DropIt2(chanceConfig.getDouble("chance_percent.rabbit." + daRabbitType.toLowerCase(), defpercent))){
				return RabbitHeads.valueOf(name + "_" + daRabbitType).getTexture().toString();
			}
			break;
		case "VILLAGER":
			Villager daVillager = (Villager) entity; // Location jobsite = daVillager.getMemory(MemoryKey.JOB_SITE);
			String daVillagerType = daVillager.getVillagerType().toString();
			String daVillagerProfession = daVillager.getProfession().toString();
			if(DropIt2(chanceConfig.getDouble("chance_percent.villager." + daVillagerType.toLowerCase() + "." + daVillagerProfession.toLowerCase(), defpercent))){
				return VillagerHeads.valueOf(name + "_" + daVillagerProfession + "_" + daVillagerType).getTexture().toString();
			}
			break;
		case "ZOMBIE_VILLAGER":
			ZombieVillager daZombieVillager = (ZombieVillager) entity;
			String daZombieVillagerProfession = daZombieVillager.getVillagerProfession().toString();
			if(DropIt2(chanceConfig.getDouble("chance_percent.zombie_villager", defpercent))){
				return ZombieVillagerHeads.valueOf(name + "_" + daZombieVillagerProfession).getTexture().toString();
			}
			break;
		case "SHEEP":
			Sheep daSheep = (Sheep) entity;
			String daSheepColor = daSheep.getColor().toString();

			if(daSheep.getCustomName() != null){
				if(daSheep.getCustomName().contains("jeb_")){
					daSheepColor = "jeb_";
				}else{
					daSheepColor = daSheep.getColor().toString();
				}
			}
			if(DropIt2(chanceConfig.getDouble("chance_percent.sheep." + daSheepColor.toLowerCase(), defpercent))){
				return SheepHeads.valueOf(name + "_" + daSheepColor).getTexture().toString();
			}
			break;
			/**case "STRIDER":
				Strider strider = (Strider) entity;

				break;*/
		case "TRADER_LLAMA":
			TraderLlama daTraderLlama = (TraderLlama) entity;
			String daTraderLlamaColor = daTraderLlama.getColor().toString();
			if(DropIt2(chanceConfig.getDouble("chance_percent.trader_llama." + daTraderLlamaColor.toLowerCase(), defpercent))){
				return LlamaHeads.valueOf(name + "_" + daTraderLlamaColor).getTexture().toString();
			}
			break;
		case "AXOLOTL":
			Axolotl daAxolotl = (Axolotl) entity;
			String daAxolotlVariant = daAxolotl.getVariant().toString();
			if(DropIt2(chanceConfig.getDouble("chance_percent.axolotl." + daAxolotlVariant.toLowerCase(), defpercent))){
				return MobHeads117.valueOf(name + "_" + daAxolotlVariant).getTexture().toString();
			}
			break;
		case "GOAT":
			Goat daGoat = (Goat) entity;
			String daGoatVariant;
			if(daGoat.isScreaming()) {
				// Giving screaming goat head
				daGoatVariant = "SCREAMING";
			}else {
				// give goat head
				daGoatVariant = "NORMAL";
			}
			if(DropIt2(chanceConfig.getDouble("chance_percent.goat." + daGoatVariant.toLowerCase(), defpercent))){
				return MobHeads117.valueOf(name + "_" + daGoatVariant).getTexture().toString();
			}
			break;
		default:
			//makeSkull(MobHeads.valueOf(name).getTexture(), name);
			if(DropIt2(chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))){
				return MobHeads.valueOf(name).getTexture().toString();
			}
			break;
		}
		return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWI3YWY5ZTQ0MTEyMTdjN2RlOWM2MGFjYmQzYzNmZDY1MTk3ODMzMzJhMWIzYmM1NmZiZmNlOTA3MjFlZjM1In19fQ==";
	}

	public String getTexturefromEntityType(EntityType entityType, boolean randef) {
		String name = entityType.toString();
		switch (name) {
		case "CREEPER":
			double cchance = chanceConfig.getDouble("chance_percent.creeper", defpercent);
			if(randef) {
				name = "CREEPER_CHARGED";
				cchance = 1.00;
			}
			if(DropIt2(cchance)){
				return MobHeads.valueOf(name).getTexture().toString();
			}
			break;
		case "ZOMBIE":
			if(DropIt2(chanceConfig.getDouble("chance_percent.zombie", defpercent))){
				return MobHeads.valueOf(name).getTexture().toString();
			}
			break;
		case "SKELETON":
			if(DropIt2(chanceConfig.getDouble("chance_percent.skeleton", defpercent))){
				return MobHeads.valueOf(name).getTexture().toString();
			}
			break;
		case "WITHER_SKELETON":
			if(DropIt2(chanceConfig.getDouble("chance_percent.wither_skeleton", defpercent))){
				return MobHeads.valueOf(name).getTexture().toString();
			}
			break;
		case "ENDER_DRAGON":
			if(DropIt2(chanceConfig.getDouble("chance_percent.ender_dragon", defpercent))){
				return MobHeads.valueOf(name).getTexture().toString();
			}
			break;//*/
		case "TROPICAL_FISH":
			String[] daFishType = {"TROPICAL_FISH", "ANEMONE", "BLACK_TANG", "BLUE_TANG", "BUTTERFLYFISH", "CICHLID", "CLOWNFISH", "COTTON_CANDY_BETTA", "DOTTYBACK", "EMPEROR_RED_SNAPPER", "GOATFISH", "MOORISH_IDOL", "ORNATE_BUTTERFLYFISH", "PARROTFISH", "QUEEN_ANGELFISH", "RED_CICHLID", "RED_LIPPED_BLENNY", "RED_SNAPPER", "THREADFIN", "TRIGGERFISH", "YELLOWTAIL_PARROTFISH", "YELLOW_TANG"};
			int randomfish;
			if(randef) {
				randomfish = randomBetween(0, 22);
			}else {
				randomfish = 0;
			}
			if(DropIt2(chanceConfig.getDouble("TROPICAL_FISH." + daFishType, defpercent))){
				return TropicalFishHeads.valueOf(daFishType[randomfish]).getTexture().toString();
			}
			break;//*/
		case "WITHER":
			//Wither wither = (Wither) event.getEntity();
			int random = randomBetween(1, 4);
			if(randef) {
				name = name.concat("_" + random);
			}else {
				name = name.concat("_1");
			}
			if(DropIt2(chanceConfig.getDouble("chance_percent.wither", defpercent))){
				return MobHeads.valueOf(name).getTexture().toString();
			}
			break;
		case "WOLF":
			String[] dawolftype = {"","_ANGRY"};
			int randomwolf;
			if(randef) {
				randomwolf = randomBetween(0, 1);
			}else {
				randomwolf = 0;
			}
			if(DropIt2(chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))){
				return MobHeads.valueOf(name + dawolftype[randomwolf]).getTexture().toString();
			}
			break;
		case "FOX":
			String[] dafoxtype = {"RED","SNOW"};
			int randomfox;
			if(randef) {
				randomfox = randomBetween(0, 1);
			}else {
				randomfox = 0;
			}
			if(DropIt2(chanceConfig.getDouble("chance_percent.fox." + dafoxtype[randomfox].toString().toLowerCase(), defpercent))){
				return MobHeads.valueOf(name + "_" + dafoxtype[randomfox]).getTexture().toString();
			}

			break;
		case "CAT":
			int randomcat;
			//String dacattype = dacat.getCatType().toString();
			String[] dacattype = {"ALL_BLACK","BLACK","BRITISH_SHORTHAIR","CALICO","JELLIE","PERSIAN","RAGDOLL","RED","SIAMESE","TABBY","WHITE"};
			if(randef) {
				randomcat = randomBetween(0, 10);
			}else {
				randomcat = 10;
			}
			if(DropIt2(chanceConfig.getDouble("chance_percent.cat." + dacattype[randomcat].toLowerCase(), defpercent))){
				return CatHeads.valueOf(dacattype[randomcat]).getTexture().toString();
			}
			break;
		case "OCELOT":
			if(DropIt2(chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))){
				return MobHeads.valueOf(name).getTexture().toString();
			}

			break;
		case "BEE":
			int randombee;
			if(randef) {
				randombee = randomBetween(1, 4);
			}else {
				randombee = 1;
			}
			if(randombee == 4){
				if(DropIt2(chanceConfig.getDouble("chance_percent.bee.angry_pollinated", defpercent))){
					return MobHeads.valueOf("BEE_ANGRY_POLLINATED").getTexture().toString();
				}
			}else if(randombee == 3){
				if(DropIt2(chanceConfig.getDouble("chance_percent.bee.angry", defpercent))){
					return MobHeads.valueOf("BEE_ANGRY").getTexture().toString();
				}
			}else if(randombee == 2){
				if(DropIt2(chanceConfig.getDouble("chance_percent.bee.pollinated", defpercent))){
					return MobHeads.valueOf("BEE_POLLINATED").getTexture().toString();
				}
			}else if(randombee == 1){
				if(DropIt2(chanceConfig.getDouble("chance_percent.bee.chance_percent", defpercent))){
					return MobHeads.valueOf("BEE").getTexture().toString();
				}
			}
			break;
		case "LLAMA":
			String[] daLlamaColor = {"BROWN","CREAMY","GRAY","WHITE"};
			int randomllama;
			if(randef) {
				randomllama = randomBetween(0, 3);
			}else {
				randomllama = 4;
			}
			//log(name + "_" + daLlamaColor);
			if(DropIt2(chanceConfig.getDouble("chance_percent.llama." + daLlamaColor[randomllama].toLowerCase(), defpercent))){
				return LlamaHeads.valueOf(name + "_" + daLlamaColor[randomllama]).getTexture().toString();
			}
			break;
		case "TRADER_LLAMA":
			String[] daTraderLlamaColor = {"BROWN","CREAMY","GRAY","WHITE"};
			int randomtllama;
			if(randef) {
				randomtllama = randomBetween(0, 3);
			}else {
				randomtllama = 4;
			}
			if(DropIt2(chanceConfig.getDouble("chance_percent.trader_llama." + daTraderLlamaColor[randomtllama].toLowerCase(), defpercent))){
				return LlamaHeads.valueOf(name + "_" + daTraderLlamaColor[randomtllama]).getTexture().toString();
			}
			break;
		case "HORSE":
			String[] daHorseColor = {"BLACK","BROWN","CHESTNUT","CREAMY","DARK_BROWN","GRAY","WHITE"};
			int randomhorse;
			if(randef) {
				randomhorse = randomBetween(0, 6);
			}else {
				randomhorse = 1;
			}
			//.replace("g", "G").replace("wh", "Wh").replace("_", " ") + " Horse Head";
			if(DropIt2(chanceConfig.getDouble("chance_percent.horse." + daHorseColor[randomhorse].toLowerCase(), defpercent))){
				return HorseHeads.valueOf(name + "_" + daHorseColor).getTexture().toString();
			}
			break;
		case "MOOSHROOM":
			name = "MUSHROOM_COW";
		case "MUSHROOM_COW":
			String[] daCowVariant = {"BROWN","RED"};
			int randomcow;
			if(randef) {
				randomcow = randomBetween(0, 6);
			}else {
				randomcow = 1;
			}
			if(DropIt2(chanceConfig.getDouble("chance_percent.mushroom_cow." + daCowVariant[randomcow].toLowerCase(), defpercent))){
				return MobHeads.valueOf(name + "_" + daCowVariant[randomcow]).getTexture().toString();
			}
			break;
		case "PANDA":
			String[] daPandaGene = {"AGRESSIVE","BROWN","LAZY","NORMAL","PLAYFUL","WEAK","WORRIED"};
			int randompan;
			if(randef) {
				randompan = randomBetween(0, 6);
			}else {
				randompan = 3;
			}
			if(DropIt2(chanceConfig.getDouble("chance_percent.panda." + daPandaGene[randompan].toLowerCase(), defpercent))){
				return MobHeads.valueOf(name + "_" + daPandaGene[randompan]).getTexture().toString();
			}
			break;
		case "PARROT":
			String[] daParrotVariant = {"BLUE","CYAN","GRAY","GREEN","RED"};
			int randompar;
			if(randef) {
				randompar = randomBetween(0, 4);
			}else {
				randompar = 3;
			}
			if(DropIt2(chanceConfig.getDouble("chance_percent.parrot." + daParrotVariant[randompar].toLowerCase(), defpercent))){
				return MobHeads.valueOf(name + "_" + daParrotVariant[randompar]).getTexture().toString();
			}
			break;
		case "RABBIT":
			String[] daRabbitType = {"BLACK","BLACK_AND_WHITE","BROWN","GOLD","SALT_AND_PEPPER","THE_KILLER_BUNNY","WHITE","Toast"};
			int randomrab;
			if(randef) {
				randomrab = randomBetween(0, 7);
			}else {
				randomrab = 3;
			}
			if(DropIt2(chanceConfig.getDouble("chance_percent.rabbit." + daRabbitType[randomrab].toLowerCase(), defpercent))){
				return RabbitHeads.valueOf(name + "_" + daRabbitType[randomrab]).getTexture().toString();
			}
			break;
		case "VILLAGER":
			String[] daVillagerType = {"DESERT","JUNGLE","PLAINS","SAVANNA","SNOW","SWAMP","TAIGA"};
			String[] daVillagerProfession = {"ARMORER","BUTCHER","CARTOGRAPHER","CLERIC","FARMER","FISHERMAN","FLETCHER"
					,"LEATHERWORKER","LIBRARIAN","MASON","NITWIT","NONE","SHEPHERD","TOOLSMITH","WEAPONSMITH"};
			int randomviltyp;
			int randomvilpro;
			if(randef) {
				randomviltyp = randomBetween(0, 6);
				randomvilpro = randomBetween(0, 14);
			}else {
				randomviltyp = 2;
				randomvilpro = 11;
			}
			if(DropIt2(chanceConfig.getDouble("chance_percent.villager." + daVillagerType[randomviltyp].toLowerCase() + "." +
					daVillagerProfession[randomvilpro].toLowerCase(), defpercent))){
				return VillagerHeads.valueOf(name + "_" + daVillagerProfession[randomvilpro] + "_" + daVillagerType[randomviltyp]).getTexture().toString();
			}
			break;
		case "ZOMBIE_VILLAGER":
			String[] daZombieVillagerProfession = {"ARMORER","BUTCHER","CARTOGRAPHER","CLERIC","FARMER","FISHERMAN","FLETCHER"
					,"LEATHERWORKER","LIBRARIAN","MASON","NITWIT","NONE","SHEPHERD","TOOLSMITH","WEAPONSMITH"};
			int randomzvpro;
			if(randef) {
				randomzvpro = randomBetween(0, 14);
			}else {
				randomzvpro = 11;
			}
			if(DropIt2(chanceConfig.getDouble("chance_percent.zombie_villager", defpercent))){
				return ZombieVillagerHeads.valueOf(name + "_" + daZombieVillagerProfession[randomzvpro]).getTexture().toString();
			}
			break;
		case "SHEEP":
			String[] daSheepColor = {"BLACK","BLUE","BROWN","CYAN","GRAY","GREEN","jeb_","LIGHT_BLUE","LIGHT_GRAY","LIME","MAGENTA","ORANGE"
					,"PINK","PURPLE","RED","WHITE","YELLOW"};
			int randomshe;
			if(randef) {
				randomshe = randomBetween(0, 16);
			}else {
				randomshe = 6;
			}
			if(DropIt2(chanceConfig.getDouble("chance_percent.sheep." + daSheepColor[randomshe].toLowerCase(), defpercent))){
				return SheepHeads.valueOf(name + "_" + daSheepColor[randomshe]).getTexture().toString();
			}
			break;//*/
			/**case "STRIDER":
			Strider strider = (Strider) entity;

			break;*/
		case "AXOLOTL":
			String[] daAxolotlVariant = {"BLUE","CYAN","GOLD","LUCY","WILD"};
			int randomaxl;
			if(randef) {
				randomaxl = randomBetween(0, 4);
			}else {
				randomaxl = 3;
			}
			if(DropIt2(chanceConfig.getDouble("chance_percent.axolotl." + daAxolotlVariant[randomaxl].toLowerCase(), defpercent))){
				return MobHeads117.valueOf(name + "_" + daAxolotlVariant[randomaxl]).getTexture().toString();
			}
			break;
		case "GOAT":
			String[] daGoatVariant = {"NORMAL","SCREAMING"};
			int randomgoat;
			if(randef) {
				randomgoat = randomBetween(0, 1);
			}else {
				randomgoat = 0;
			}
			if(DropIt2(chanceConfig.getDouble("chance_percent.goat." + daGoatVariant[randomgoat].toLowerCase(), defpercent))){
				return MobHeads117.valueOf(name + "_" + daGoatVariant[randomgoat]).getTexture().toString();
			}
			break;
		default:
			//makeSkull(MobHeads.valueOf(name).getTexture(), name);
			if(DropIt2(chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))){
				return MobHeads.valueOf(name).getTexture().toString();
			}
			break;

		}
		return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWI3YWY5ZTQ0MTEyMTdjN2RlOWM2MGFjYmQzYzNmZDY1MTk3ODMzMzJhMWIzYmM1NmZiZmNlOTA3MjFlZjM1In19fQ==";
	}//*/

	public boolean chance25oftrue() {
		//For 25% chance of true
		return (random.nextInt(4) == 0) ? true : false;
	}
	public void consoleLog(String string) {
		if(!silent_console) {
			loading(string);
		}
	}

	/**
	 * Calculates and formats the elapsed time from a specified start time.
	 *
	 * @param startTime The start time in milliseconds.
	 * @return A formatted string representing the elapsed time.
	 */
	public String LoadTime(long startTime) {
		long elapsedTime = System.currentTimeMillis() - startTime;
		long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60;
		long milliseconds = elapsedTime % 1000;

		if (minutes > 0) {
			return String.format("%d min %d s %d ms.", minutes, seconds, milliseconds);
		} else if (seconds > 0) {
			return String.format("%d s %d ms.", seconds, milliseconds);
		} else {
			return String.format("%d ms.", elapsedTime);
		}
	}


	@SuppressWarnings("static-access")
	public String get(String key, String... defaultValue) {
		return lang2.get(key, defaultValue);
	}

	public boolean isPluginRequired(String pluginName) {
		String[] requiredPlugins = {"SinglePlayerSleep", "MoreMobHeads", "NoEndermanGrief", "ShulkerRespawner", "MoreMobHeads", "RotationalWrench", "SilenceMobs", "VillagerWorkstationHighlights"};
		for (String requiredPlugin : requiredPlugins) {
			if ((getServer().getPluginManager().getPlugin(requiredPlugin) != null) && getServer().getPluginManager().isPluginEnabled(requiredPlugin)) {
				if (requiredPlugin.equals(pluginName)) {
					return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	public void dumpConfig(FileConfiguration config) {
		for (String key : config.getKeys(true)) {
			Object value = config.get(key);
			if ((value != null) && value.getClass().isArray()) {
				value = Arrays.asList((Object[]) value);
			}
			LOGGER.log(key + " = '" + value + "'");
		}
	}

	public String getNamedTropicalFishName(Pattern pattern, DyeColor color1, DyeColor color2) {
		String key = pattern.name() + "-" + color1.name() + "-" + color2.name();
		LOGGER.log("key=" + key);
		LOGGER.log("namedTropicalFish=" +  namedTropicalFish.isEmpty());
		return namedTropicalFish.getOrDefault(key, "TROPICAL_FISH");
	}

	public void checkDirectories() {
		/**	Check for config */
		try{
			if(!getDataFolder().exists()){
				LOGGER.log("Data Folder doesn't exist");
				LOGGER.log("Creating Data Folder");
				getDataFolder().mkdirs();
				LOGGER.log("Data Folder Created at " + getDataFolder());
			}
			File file = new File(getDataFolder(), "config.yml");
			if(!file.exists()){
				LOGGER.log("config.yml not found, creating!");
				saveResource("config.yml", true);
			}
			file = new File(getDataFolder(), "chance_config.yml");
			if(!file.exists()){
				LOGGER.log("chance_config.yml not found, creating!");
				saveResource("chance_config.yml", true);
			}
			file = new File(getDataFolder(), "messages.yml");
			if(!file.exists()){
				LOGGER.log("messages.yml not found, creating!");
				saveResource("messages.yml", true);
			}
			file = new File(getDataFolder(), "fileVersions.yml");
			if(!file.exists()){
				LOGGER.log("fileVersions.yml not found, creating!");
				saveResource("fileVersions.yml", true);
			}
			file = new File(getDataFolder() + "" + File.separatorChar + "lang" + File.separatorChar, daLang + "_mobnames.yml");
			if(!file.exists()){
				LOGGER.log("lang" + File.separatorChar, daLang + "_mobnames.yml not found, creating!");
				saveResource("lang" + File.separatorChar + daLang + "_mobnames.yml", true);
			}
		}catch(Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
		}
	}

	public void checkConfig() {
		// Config file check
		Version curConfigVersion = new Version(fileVersions.getString("config", "0.0.1"));
		if(curConfigVersion.compareTo(minConfigVersion) < 0) {
			LOGGER.log("config.yml is outdated backing up...");
			try {
				copyFile(getDataFolder() + "" + File.separatorChar + "config.yml",getDataFolder() + "" + File.separatorChar + "backup" + File.separatorChar + "config.yml");
			} catch (Exception exception) {
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_COPY_FILE).error(exception));
			}
			LOGGER.log("Saving new config.yml...");
			saveResource("config.yml", true);
			// from new File(getDataFolder() + "" + File.separatorChar + "backup", "config.yml")
			copyConfig("" + getDataFolder() + File.separatorChar + "backup" + File.separatorChar + "config.yml", "" + getDataFolder() + File.separatorChar + "config.yml");
		}
		LOGGER.log("Loading config file...");
		try {
			config.load(new File(getDataFolder() + "" + File.separatorChar + "config.yml"));
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
		}
	}

	public void checkMessages() {
		// Message file check
		Version curMessagesVersion = new Version(fileVersions.getString("messages", "0.0.1"));
		if(curMessagesVersion.compareTo(minMessagesVersion) < 0) {
			LOGGER.log("messages.yml is outdated backing up...");
			try {
				copyFile(getDataFolder() + "" + File.separatorChar + "messages.yml", getDataFolder() + "" + File.separatorChar + "backup" + File.separatorChar + "messages.yml");
			} catch (Exception exception) {
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_MESSAGES_COPY_ERROR).error(exception));
			}
			LOGGER.log("Saving new messages.yml...");
			saveResource("messages.yml", true);
			LOGGER.log("Loading new messages.yml...");
			try {
				beheadingMessages.load(new File(getDataFolder() + "" + File.separatorChar + "messages.yml"));
			} catch (Exception exception) {
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_MESSAGES_LOAD_ERROR).error(exception));
			}
			LOGGER.log("Loading old messages.yml...");
			try {
				oldMessages.load(new File(getDataFolder() + "" + File.separatorChar + "messages.yml"));
			} catch (Exception exception) {
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_OLDMESSAGES_LOAD_ERROR).error(exception));
			}
			LOGGER.log("Copying values from backup" + File.separatorChar + "messages.yml...");
			ConfigurationSection oldMessagesSection = oldMessages.getConfigurationSection("messages");

			for (String messageKey : oldMessagesSection.getKeys(false)) {
				String messageValue = oldMessagesSection.getString(messageKey);
				beheadingMessages.set("messages." + messageKey, messageValue.replace("<killerName>", "%killerName%")
						.replace("<entityName>", "%entityName%")
						.replace("<weaponName>", "%weaponName%"));
			}
			LOGGER.log("Saving messages.yml...");
			try {
				beheadingMessages.save(new File(getDataFolder(), "messages.yml"));
			} catch (Exception exception) {
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_OLDMESSAGES_SAVE_ERROR).error(exception));
			}
			beheadingMessages = new YmlConfiguration();
			oldMessages = null;
			LOGGER.log("Update complete config.yml...");
		}
		LOGGER.log("Loading messages file...");
		try {
			beheadingMessages.load(new File(getDataFolder() + "" + File.separatorChar + "messages.yml"));
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_MESSAGES_LOAD_ERROR).error(exception));
		}
	}

	public void checkMiniBlocks() {
		// check player heads
		if( config.getBoolean("wandering_trades.custom_wandering_trader", true) || config.getBoolean("head_settings.mini_blocks.stonecutter", true) ){
			File file = new File(getDataFolder(), "custom_trades.yml");
			if(!file.exists()){
				LOGGER.log("custom_trades.yml not found, creating!");
				saveResource("custom_trades.yml", true);
			}
			file = new File(getDataFolder(), "block_heads_1_17_2.yml");
			if(!file.exists()){
				LOGGER.log("block_heads_1_17_2.yml not found, creating!");
				saveResource("block_heads_1_17_2.yml", true);
			}
			file = new File(getDataFolder(), "block_heads_1_17.yml");
			if(!file.exists()){
				LOGGER.log("block_heads_1_17.yml not found, creating!");
				saveResource("block_heads_1_17.yml", true);
			}
			file = new File(getDataFolder(), "block_heads_1_20.yml");
			if(!file.exists()){
				LOGGER.log("block_heads_1_20.yml not found, creating!");
				saveResource("block_heads_1_20.yml", true);
			}
			file = new File(getDataFolder(), "player_heads.yml");
			if(!file.exists()){
				LOGGER.log("player_heads.yml not found, creating!");
				saveResource("player_heads.yml", true);
			}
			/** Trader heads load */
			Version curPlayerHeadsVersion = new Version(fileVersions.getString("player_heads", "0.0.1"));
			playerFile = new File(getDataFolder() + "" + File.separatorChar + "player_heads.yml");
			if(curPlayerHeadsVersion.compareTo(minPlayerVersion) < 0) {
				LOGGER.log("player_heads.yml is outdated backing up...");
				try {
					copyFile(playerFile.getAbsolutePath(),getDataFolder() + "" + File.separatorChar + "backup" + File.separatorChar + "player_heads.yml");
				} catch (Exception exception) {
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_COPY_FILE).error(exception));
				}
				LOGGER.log("Saving new player_heads.yml...");
				saveResource("player_heads.yml", true);
			}
			LOGGER.log("Loading player_heads.yml...");
			playerHeads = new YamlConfiguration();
			try {
				playerHeads.load(playerFile);
			} catch (Exception exception) {
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLAYERHEAD_LOAD_ERROR).error(exception));
			}

			Version curCustomVersion = new Version(fileVersions.getString("custom_trades", "0.0.1"));
			customFile = new File(getDataFolder() + "" + File.separatorChar + "custom_trades.yml");
			if(curCustomVersion.compareTo(minCustomVersion) < 0) {
				LOGGER.log("custom_trades.yml is outdated backing up...");
				try {
					copyFile(customFile.getAbsolutePath(),getDataFolder() + "" + File.separatorChar + "backup" + File.separatorChar + "custom_trades.yml");
				} catch (Exception exception) {
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_COPY_FILE).error(exception));
				}
				LOGGER.log("Saving new custom_trades.yml...");
				saveResource("custom_trades.yml", true);
			}
			LOGGER.log("Loading custom_trades file...");
			traderCustom = new YamlConfiguration();
			try {
				traderCustom.load(customFile);
			} catch (Exception exception) {
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CUSTOM_LOAD_ERROR).error(exception));
			}

		}
	}

	public void checkChance() {
		Version curChanceVersion = new Version(fileVersions.getString("chance_config", "0.0.1"));
		chanceFile = new File(getDataFolder() + "" + File.separatorChar + "chance_config.yml");
		if(curChanceVersion.compareTo(minChanceVersion) < 0) {
			LOGGER.log("chance_config.yml is outdated backing up...");
			try {
				copyFile(chanceFile.getAbsolutePath(),getDataFolder() + "" + File.separatorChar + "backup" + File.separatorChar + "chance_config.yml");
			} catch (Exception exception) {
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_COPY_FILE).error(exception));
			}
			LOGGER.log("Saving new chance_config.yml...");
			saveResource("chance_config.yml", true);

			LOGGER.log("Copying values from backup" + File.separatorChar + "chance_config.yml...");
			copyChance(getDataFolder() + "" + File.separatorChar + "backup" + File.separatorChar + "chance_config.yml", chanceFile.getPath());
		}
		LOGGER.log("Loading chance_config file...");
		chanceConfig = new YmlConfiguration();
		try {
			chanceConfig.load(chanceFile);
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CHANCE_LOAD_ERROR).error(exception));
		}
	}

	public void checkLang() {
		Version curLangVersion = new Version(fileVersions.getString("lang", "0.0.1"));
		langNameFile = new File(getDataFolder() + "" + File.separatorChar + "lang" + File.separatorChar, daLang + "_mobnames.yml");
		if(curLangVersion.compareTo(minLangVersion) < 0) {
			LOGGER.log(daLang + "_mobnames.yml is outdated backing up...");
			try {
				copyFile(chanceFile.getAbsolutePath(),getDataFolder() + "" + File.separatorChar + "backup" + File.separatorChar + daLang + "_mobnames.yml");
			} catch (Exception exception) {
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_COPY_FILE).error(exception));
			}
			LOGGER.log("Saving new " + daLang + "_mobnames.yml...");
			saveResource("lang" + File.separatorChar + daLang + "_mobnames.yml", true);
			LOGGER.log("Copying values from backup" + File.separatorChar + daLang + "_mobnames.yml...");
			copyConfigValues(getDataFolder() + "" + File.separatorChar + "lang" + File.separatorChar + daLang + "_mobnames.yml",
					getDataFolder() + "" + File.separatorChar + "backup" + File.separatorChar + "" + daLang + "_mobnames.yml");
		}
		LOGGER.log("Loading language based mobname file...");
		langName = new YamlConfiguration();
		try {
			langName.load(langNameFile);
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_MOBNAMES_LOAD_ERROR).error(exception));
		}
	}

	// Used to check Minecraft version
	private Version verifyMinecraftVersion() {
		Version minimum = new Version(PluginLibrary.MINIMUM_MINECRAFT_VERSION);
		Version maximum = new Version(PluginLibrary.MAXIMUM_MINECRAFT_VERSION);

		try {
			Version current = new Version(this.getServer());

			// We'll just warn the user for now
			if (current.compareTo(minimum) < 0) {
				LOGGER.warn("Version " + current + " is lower than the minimum " + minimum);
			}
			if (current.compareTo(maximum) > 0) {
				LOGGER.warn("Version " + current + " has not yet been tested! Proceed with caution.");
			}

			return current;
		} catch (Exception exception) {
			reporter.reportWarning(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_PARSE_MINECRAFT_VERSION).error(exception).messageParam(maximum));

			// Unknown version - just assume it is the latest
			return maximum;
		}
	}

	public String getjarfilename() {
		return jarfilename;
	}

	public boolean getDebug() {
		return debug;
	}


}
