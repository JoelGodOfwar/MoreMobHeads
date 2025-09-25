package com.github.joelgodofwar.mmh;

import com.earth2me.essentials.Essentials;
import com.github.joelgodofwar.mmh.command.Command_1_20_R2;
import com.github.joelgodofwar.mmh.common.PluginLibrary;
import com.github.joelgodofwar.mmh.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.mmh.common.error.Report;
import com.github.joelgodofwar.mmh.events.*;
import com.github.joelgodofwar.mmh.handlers.EventHandler_1_20_R1;
import com.github.joelgodofwar.mmh.handlers.EventHandler_1_20_R2;
import com.github.joelgodofwar.mmh.handlers.MMHEventHandler;
import com.github.joelgodofwar.mmh.i18n.Translator;
import com.github.joelgodofwar.mmh.util.*;
import com.github.joelgodofwar.mmh.util.ChatColorUtils;
import com.github.joelgodofwar.mmh.util.datatypes.JsonDataType;
import com.github.joelgodofwar.mmh.util.heads.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTList;
import de.tr7zw.changeme.nbtapi.NBTListCompound;
import dev.majek.hexnicks.HexNicks;
import lib.github.joelgodofwar.coreutils.CoreUtils;
import lib.github.joelgodofwar.coreutils.util.*;
import lib.github.joelgodofwar.coreutils.util.MMHDLC;
import lib.github.joelgodofwar.coreutils.util.Version;
import lib.github.joelgodofwar.coreutils.util.VersionChecker;
import lib.github.joelgodofwar.coreutils.util.common.PluginLogger;
import mineverse.Aust1n46.chat.api.MineverseChatAPI;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.apache.commons.lang.StringUtils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SimplePie;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.TropicalFish.Pattern;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings({ "unchecked", "rawtypes", "deprecation", "unused" })
public class MoreMobHeads extends JavaPlugin implements Listener{
	//** Languages: čeština (cs_CZ), Deutsch (de_DE), English (en_US), Español (es_ES), Español (es_MX), Français (fr_FR), Italiano (it_IT), Magyar (hu_HU), 日本語 (ja_JP), 한국어 (ko_KR), Lolcat (lol_US), Melayu (my_MY), Nederlands (nl_NL), Polski (pl_PL), Português (pt_BR), Русский (ru_RU), Svenska (sv_SV), Türkçe (tr_TR), 中文(简体) (zh_CN), 中文(繁體) (zh_TW) */
	public static final long DEV_BUILD_START_TIME = 1758762004L; // Placeholder for Maven replacement
	public final com.github.joelgodofwar.mmh.util.BuildValidator buildValidator = new BuildValidator();
	public static String THIS_NAME;
	public static String THIS_VERSION;
	//** update checker variables */
	public int projectID = 73997; // https://spigotmc.org/resources/71236
	public String githubURL = "https://github.com/JoelGodOfwar/MoreMobHeads/raw/master/versioncheck/1.20/versions.xml";
	public boolean UpdateAvailable =  false;
	public String UC_oldVersion;
	public String UC_newVersion;
	public boolean UpdateCheck;
	public String DownloadLink = "https://dev.bukkit.org/projects/moremobheads2";
	//** end update checker variables */
	public boolean isDev = false;
	public boolean debug = false;
	public static String daLang;
	File langNameFile;
	public FileConfiguration langName;
	public File playerFile;
	public FileConfiguration playerHeads;
	public List<MMHDLC> mmhDLC;
	public FileConfiguration fileVersions  = new YamlConfiguration();
	public File fileVersionsFile;
	public File customFile;
	public FileConfiguration traderCustom;
	File chanceFile;
	public YmlConfiguration chanceConfig;
	public YmlConfiguration old_chanceConfig;
	public YamlConfiguration beheadingMessages = new YamlConfiguration();
	public YamlConfiguration oldMessages;
	File mobnameFile;
	FileConfiguration mob_name;
	double def_percent = 0.013;
	public YmlConfiguration config = new YmlConfiguration(this);
	YamlConfiguration old_config = new YamlConfiguration();
	static String data_folder;
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
	public ConcurrentHashMap<UUID, Long> bedInteractions = new ConcurrentHashMap<>();
	File debugFile;
	String pluginName = THIS_NAME;
	public Translator lang2;
	HashMap<String, String> namedTropicalFish = new HashMap<>();
	public Map<Player, Random> chanceRandoms = new HashMap<>();
	public List<ItemStack> blockhead_list = new ArrayList<ItemStack>();
	public static DetailedErrorReporter reporter;
	public String jar_file_name = this.getFile().getAbsoluteFile().toString();
	public PluginLogger LOGGER;

	// Persistent Heads
	public final NamespacedKey NAME_KEY = new NamespacedKey(this, "head_name");
	public final NamespacedKey LORE_KEY = new NamespacedKey(this, "head_lore");
	public final NamespacedKey UUID_KEY = new NamespacedKey(this, "head_uuid");
	public final NamespacedKey TEXTURE_KEY = new NamespacedKey(this, "head_texture");
	public final NamespacedKey SOUND_KEY = new NamespacedKey(this, "head_sound");
	public final NamespacedKey CONFIG_KEY = new NamespacedKey(this, "config_setting");
	public final PersistentDataType<String,String[]> LORE_PDT = new JsonDataType<>(String[].class);

	public Version minConfigVersion = new Version("1.0.29");
	public Version minMessagesVersion = new Version("1.0.2");
	public Version minLangVersion = new Version("1.0.5");
	public Version minCustomVersion = new Version("1.0.0");
	// Create a HashMap to store langName -> MobHead mappings
	private PlayerHeadLoader playerHeadLoader;
	private BlockHeadLoader blockHeadLoader;
	private MobHeadLoader mobHeadLoader;
	private MiniBlockLoader miniBlockLoader;

	public HeadManager headManager;
	public MMHEventHandler eventHandler;
	public double playerChance = 50.0;
	public double namedChance = 10.0;
	public final Set<UUID> warnedPlayers = new HashSet<>();
	private final Random random = new Random();
	public CoreUtils coreUtils;


	@Override // TODO: onEnable
	public void onEnable(){

		long startTime = System.currentTimeMillis();
		LOGGER = new PluginLogger(this);
		reporter = new DetailedErrorReporter(this);
		UpdateCheck = getConfig().getBoolean("plugin_settings.auto_update_check", true);
		CoreUtils.debug = getConfig().getBoolean("plugin_settings.debug", false);
		daLang = getConfig().getString("plugin_settings.lang", "en_US");
		old_config = new YamlConfiguration();
		oldMessages = new YamlConfiguration();
		lang2 = new Translator(daLang, getDataFolder().toString());
		THIS_NAME = this.getDescription().getName();
		THIS_VERSION = this.getDescription().getVersion();
		if(!getConfig().getBoolean("global_settings.console.longpluginname", true)) {
			pluginName = "MMH";
		}else {
			pluginName = THIS_NAME;
		}

		data_folder = this.getDataFolder().toString();
		colorful_console = getConfig().getBoolean("global_settings.console.colorful_console", true);
		silent_console = getConfig().getBoolean("global_settings.console.silent_console", false);

		try{
			LOGGER.log(ChatColor.YELLOW + "**************************************" + ChatColor.RESET);
			LOGGER.log(ChatColor.GREEN + " v" + THIS_VERSION + ChatColor.RESET + " Loading...");
			LOGGER.log("Server Version: " + getServer().getVersion());

			// Handle unexpected Minecraft versions
			Version checkVersion = this.verifyMinecraftVersion();

			//** DEV check **/
			File jarfile = this.getFile().getAbsoluteFile();
			if(jarfile.toString().contains("-DEV")){
				CoreUtils.debug = true;
				LOGGER.warn(ChatColor.RED + "Jar file contains -DEV, debug set to true" + ChatColor.RESET);
				LOGGER.warn(ChatColor.RED + "jarfilename = " + StrUtils.Right(jar_file_name, jar_file_name.length() - jar_file_name.lastIndexOf(File.separatorChar)) + ChatColor.RESET);
				//log("jarfile contains dev, debug set to true.");
			}

			//** Version Check */
			if( !(checkVersion.isAtLeast(PluginLibrary.MINIMUM_MINECRAFT_VERSION)) ){
				// !getMCVersion().startsWith("1.14")&&!getMCVersion().startsWith("1.15")&&!getMCVersion().startsWith("1.16")&&!getMCVersion().startsWith("1.17")
				LOGGER.warn(ChatColor.RED + " *!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!" + ChatColor.RESET);
				LOGGER.warn(ChatColor.RED + " " + get("mmh.message.server_not_version") + ChatColor.RESET);
				LOGGER.warn(ChatColor.RED + "  v" + THIS_VERSION + " disabling." + ChatColor.RESET);
				LOGGER.warn(ChatColor.RED + " *!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!" + ChatColor.RESET);
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}
			if (!buildValidator.isBuildValid()) {
				getLogger().severe(ChatColor.DARK_RED + "This dev-build has expired or is incompatible with your Minecraft version.");
				getLogger().info(ChatColor.AQUA + "Update to the latest dev-build or full release at https://dev.bukkit.org/projects/moremobheads2/files");
				getServer().getPluginManager().disablePlugin(this);
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
			// Check if Messages needs update.
			checkMessages();
			// Check if MiniBlocks needs update,
			checkMiniBlocks();
			// Check if Chance needs update.
			//checkChance();
			// Check if Lang needs update.
			checkLang();

			world_whitelist = config.getString("global_settings.world.whitelist", "");
			world_blacklist = config.getString("global_settings.world.blacklist", "");
			mob_whitelist = config.getString("head_settings.mob_heads.whitelist", "");
			mob_blacklist = config.getString("head_settings.mob_heads.blacklist", "");

			getServer().getPluginManager().registerEvents(this, this);

			//String jarfilename = this.getFile().getAbsoluteFile().toString();
			logDebug("-<[ PLEASE INCLUDE THIS WITH ANY ISSUE REPORTS ]>-");
			logDebug("This server is running " + Bukkit.getName() + " version " + Bukkit.getVersion() + " (API version " + Bukkit.getBukkitVersion() + ")");
			logDebug("vardebug= " + CoreUtils.debug + " debug=" + config.getString("plugin_settings.debug","error").toUpperCase(Locale.ROOT) + " in " + this.getDataFolder() + File.separatorChar + "config.yml");
			logDebug("jarfilename= " + StrUtils.Right(jar_file_name, jar_file_name.length() - jar_file_name.lastIndexOf(File.separatorChar)));
			logDebug("-<[ PLEASE INCLUDE THIS WITH ANY ISSUE REPORTS ]>-");



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

			//** Update Checker */
			if(UpdateCheck){
				LOGGER.log("Checking for updates...");
				try {
					VersionChecker updater = new VersionChecker(this, projectID, githubURL);
					boolean isCurrent = updater.checkForUpdates();
					mmhDLC = updater.getDLCList();
					String availableDLCs = ((mmhDLC != null) && !mmhDLC.isEmpty()) ?
							mmhDLC.stream()
							.filter(dlc -> !new File(getDataFolder(), dlc.getMarkerFile()).exists())
							.map(dlc -> String.format("%s (%d heads, $%.2f)",
									StrUtils.toProperTitleCase(dlc.getFilename().replace("mmh_", "").replace("_", " ").replaceAll("\\.[^\\.]+$", "")),
									dlc.getNumberOfFiles(), dlc.getPrice()))
							.collect(Collectors.joining(", ")) : "";
					availableDLCs = ChatColor.YELLOW + (availableDLCs.isEmpty() ?
							"All available DLCs installed." : "Available DLCs: " + availableDLCs);

					if(isCurrent) {
						//** Update available */
						UpdateAvailable = true; // TODO: Update Checker
						UC_oldVersion = updater.oldVersion();
						UC_newVersion = updater.newVersion();

						LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
						LOGGER.log("* " + get("mmh.version.message").replace("<MyPlugin>", THIS_NAME) );
						LOGGER.log("* " + get("mmh.version.old_vers") + ChatColor.RED + UC_oldVersion);
						LOGGER.log("* " + get("mmh.version.new_vers") + ChatColor.GREEN + UC_newVersion);
						LOGGER.log("*");
						LOGGER.log("* " + get("mmh.version.please_update") );
						LOGGER.log("*");
						LOGGER.log("* " + get("mmh.version.download") + ": " + DownloadLink + "/files");
						if(!StringUtils.contains(availableDLCs, "installed")) {
							LOGGER.log("* " + ChatColor.YELLOW + get("mmh.version.dlc.message"));
							LOGGER.log("* " + availableDLCs);
						}
						LOGGER.log("* " + get("mmh.version.donate") + ": https://ko-fi.com/joelgodofwar");
						LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
					}else{
						//** Up to date */
						LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
						LOGGER.log("* " + get("mmh.version.curvers"));
						if(!StringUtils.contains(availableDLCs, "installed")) {
							LOGGER.log("* " + ChatColor.YELLOW + get("mmh.version.dlc.message"));
							LOGGER.log("* " + availableDLCs);
						}
						LOGGER.log("* " + get("mmh.version.donate") + ": https://ko-fi.com/joelgodofwar");
						LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
						UpdateAvailable = false;
					}
				}catch(Exception exception) {
					//exception.printStackTrace();
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_UPDATE_PLUGIN).error(exception));
				}
			}else {
				//** auto_update_check is false so nag. */
				LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
				LOGGER.log("* " + ChatColor.YELLOW + get("mmh.version.dlc.message"));
				LOGGER.log("* " + get("mmh.version.donate.message") + ": https://ko-fi.com/joelgodofwar");
				LOGGER.log("*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*!*");
			}
			//** end update checker */

			if(getServer().getPluginManager().getPlugin("HeadBlocks") != null){
				getServer().getPluginManager().registerEvents( new OnPlayerBreakBlockEvent(), this);
				getServer().getPluginManager().registerEvents( new OnPlayerPlaceBlockEvent(), this);
			}

			// Load events handlers.
			getServer().getPluginManager().registerEvents( new PlayerInteractEventHandler(this), this);
			getServer().getPluginManager().registerEvents( new PlayerJoinEventHandler(this), this);
			getServer().getPluginManager().registerEvents( new PlayerQuitEventHandler(this), this);
			getServer().getPluginManager().registerEvents( new PersistentHeads(this), this);
			getServer().getPluginManager().registerEvents(new BeheadingHandler(this), this);
			coreUtils = new CoreUtils(this);


			// Other initialization code...
			headManager = new HeadManager();

			//** Register EventHandler */
			String packageName = this.getServer().getClass().getPackage().getName();
			Version version = new Version(Bukkit.getServer());
			logDebug("version=" + version);
			Version min = new Version("1.21");
			logDebug("version.atOrAbove(min) = " + version.atOrAbove(min));
			if( version.isBetween("1.20", "1.20.4") ){
				eventHandler = new EventHandler_1_20_R1(this, headManager);
				getServer().getPluginManager().registerEvents((EventHandler_1_20_R1) eventHandler, this);
				Command_1_20_R2 commandHandler = new Command_1_20_R2(this, headManager, eventHandler.getGiveHeadCommand(), eventHandler.getViewHeadsCommand(), eventHandler);
				Objects.requireNonNull(getCommand("mmh")).setExecutor(commandHandler);
				Objects.requireNonNull(getCommand("mmh")).setTabCompleter(commandHandler);
			}else if( version.isBetween("1.20.5","1.21.9") ){
				eventHandler = new EventHandler_1_20_R2(this, headManager);
				getServer().getPluginManager().registerEvents((EventHandler_1_20_R2) eventHandler, this);
				Command_1_20_R2 commandHandler = new Command_1_20_R2(this, headManager, eventHandler.getGiveHeadCommand(), eventHandler.getViewHeadsCommand(), eventHandler);
				Objects.requireNonNull(getCommand("mmh")).setExecutor(commandHandler);
				Objects.requireNonNull(getCommand("mmh")).setTabCompleter(commandHandler);
			}else{
				LOGGER.warn("Not compatible with this version of Minecraft:" + version);
				getServer().getPluginManager().disablePlugin(this);
			}

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
						valueMap.put("CREEPER " + config.getString("head_settings.mob_heads.vanilla_heads.creeper", "false").toUpperCase(Locale.ROOT), 1);
						valueMap.put("ENDER_DRAGON " + config.getString("head_settings.mob_heads.vanilla_heads.ender_dragon", "false").toUpperCase(Locale.ROOT), 1);
						valueMap.put("SKELETON " + config.getString("head_settings.mob_heads.vanilla_heads.skeleton", "false").toUpperCase(Locale.ROOT), 1);
						valueMap.put("WITHER_SKELETON " + config.getString("head_settings.mob_heads.vanilla_heads.wither_skeleton", "false").toUpperCase(Locale.ROOT), 1);
						valueMap.put("ZOMBIE " + config.getString("head_settings.mob_heads.vanilla_heads.zombie", "false").toUpperCase(Locale.ROOT), 1);
						valueMap.put("PIGLIN " + config.getString("head_settings.mob_heads.vanilla_heads.piglin", "false").toUpperCase(Locale.ROOT), 1);
						return valueMap;
					}
				}));
				metrics.addCustomChart(new SimplePie("auto_update_check", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return config.getString("plugin_settings.auto_update_check", "false").toUpperCase(Locale.ROOT);
					}
				}));
				// add to site
				metrics.addCustomChart(new SimplePie("var_debug", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return config.getString("plugin_settings.debug", "false").toUpperCase(Locale.ROOT);
					}
				}));
				metrics.addCustomChart(new SimplePie("var_lang", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return config.getString("plugin_settings.lang", "false").toUpperCase(Locale.ROOT);
					}
				}));
				metrics.addCustomChart(new SimplePie("whitelist.enforce", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return config.getString("head_settings.player_heads.whitelist.enforce", "false").toUpperCase(Locale.ROOT);
					}
				}));
				metrics.addCustomChart(new SimplePie("blacklist.enforce", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return config.getString("head_settings.player_heads.blacklist.enforce", "false").toUpperCase(Locale.ROOT);
					}
				}));
				metrics.addCustomChart(new SimplePie("custom_wandering_trader", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return config.getString("wandering_trades.custom_wandering_trader", "false").toUpperCase(Locale.ROOT);
					}
				}));
				metrics.addCustomChart(new SimplePie("player_heads", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return config.getString("wandering_trades.player_heads.enabled", "false").toUpperCase(Locale.ROOT);
					}
				}));
				metrics.addCustomChart(new SimplePie("block_heads", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return config.getString("wandering_trades.block_heads.enabled", "false").toUpperCase(Locale.ROOT);
					}
				}));
				metrics.addCustomChart(new SimplePie("custom_trades", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return config.getString("wandering_trades.custom_trades.enabled", "false").toUpperCase(Locale.ROOT);
					}
				}));
				metrics.addCustomChart(new SimplePie("apply_looting", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return config.getString("head_settings.apply_looting", "false").toUpperCase(Locale.ROOT);
					}
				}));
				metrics.addCustomChart(new SimplePie("show_killer", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return config.getString("head_settings.lore.show_killer", "false").toUpperCase(Locale.ROOT);
					}
				}));
				metrics.addCustomChart(new SimplePie("show_plugin_name", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return config.getString("head_settings.lore.show_plugin_name", "false").toUpperCase(Locale.ROOT);
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


	public boolean isDLCInstalled(String dlcName){
		if ((mmhDLC == null) || mmhDLC.isEmpty()) {
			return false;
		}
		return mmhDLC.stream()
				.filter(dlc -> dlc.getFilename().replaceAll("\\.[^\\.]+$", "").equalsIgnoreCase(dlcName))
				.anyMatch(dlc -> new File(getDataFolder(), dlc.getMarkerFile()).exists());
	}

	@Override // TODO: onDisable
	public void onDisable(){
		String defVer = "0.1.0";
		fileVersions.set("config", config.getString("version", defVer));
		fileVersions.set("messages", beheadingMessages.getString("version", defVer));
		fileVersions.set("custom_trades", traderCustom.getString("custom_trades.version", defVer));
		fileVersions.set("lang", langName.getString("version", defVer));
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
			logDebug("Player hasPlayedBefore, getting local UUID.");
			uuid = oPlayer.getUniqueId();
		}else {
			logDebug("Player !hasPlayedBefore, getting UUID from Mojang.");
			uuid = UUID.fromString(getPlayerUUID(name));
		}
		if(uuid == null) {
			LOGGER.warn("gPH User doesn't exist or invalid UUID");
			return;
		}
		PlayerProfile profile = Bukkit.createPlayerProfile(uuid, name);
		String texture = APIRequest(uuid.toString(), "sessionProfile", "value");
		logDebug("gMH UUID = " + uuid.toString());
		logDebug("gMH textureCoded = '" + texture + "'");

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
		if(Objects.requireNonNull(mob.getEquipment()).getHelmet() == null) {
			mob.getEquipment().setHelmet(head);
		}
		head.setItemMeta(meta);
		logDebug("helmet = " + mob.getEquipment().getHelmet().toString());
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
		logDebug("getPlayerUUID uuid = " + uuid);
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
			logDebug("Error with UUID.");
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
				logDebug(String.format(
						"Could not get %s. Response code: %s",
						((toSearch.equals("id")) ? "UUID" : "texture"),
						responseCode
						));
			}
		} catch (MalformedURLException error) {
			logDebug("An error occurred while trying to access the URL.");
		} catch (IOException error) {
			logDebug("An error occurred while attempting to connect to the URL.");
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return null;
	}

	public void givePlayerHead(Player player, String name){
		UUID uuid = null;
		OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(name);
		if(oPlayer.hasPlayedBefore()) {
			logDebug("Player hasPlayedBefore, getting local UUID.");
			uuid = oPlayer.getUniqueId();
		}else {
			logDebug("Player !hasPlayedBefore, getting UUID from Mojang.");
			uuid = UUID.fromString(getPlayerUUID(name));
		}
		if(uuid == null) {
			LOGGER.warn("gPH User doesn't exist or invalid UUID");
			return;
		}
		PlayerProfile profile = Bukkit.createPlayerProfile(uuid, name);
		String texture = APIRequest(uuid.toString(), "sessionProfile", "value");
		logDebug("gPH UUID = " + uuid.toString());
		logDebug("gPH textureCoded = \'" + texture + "\'");

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
		logDebug("giveBlockHead START");
		ItemStack blockStack = null;
		int isBlock = isBlockHeadName(blockName);

		if(isBlock != -1){
			logDebug("GBH isBlock=" + isBlock);
			blockStack = blockhead_list.get(isBlock);
			blockStack.setAmount(1);
		} else {
			/**            Add translation for this line.    *****************************************************************************************************  */
			player.sendMessage(THIS_NAME + " v" + THIS_VERSION + " Sorry could not find \"" + blockName + "\""); // TODO: Add translation for this line.
		}
		if( (blockStack != null) && (blockStack.getType() != Material.AIR) ) {
			playerGiveOrDropHead(player, blockStack);
			//player.getWorld().dropItemNaturally(player.getLocation(), blockStack);
			logDebug("GBH BlockHead given to " + player.getName());
		}
		logDebug("giveBlockHead END");
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


	/**
	 * Determines whether an entity should drop an item based on a chance percentage.
	 *
	 * @param event         The EntityDeathEvent representing the death of the entity.
	 * @param chancePercent The chance percentage for the item drop.
	 *                      The drop will occur if a random value is less than or equal to this percentage.
	 * @return {@code true} if the item should be dropped, {@code false} otherwise.
	 */
	public boolean DropIt(EntityDeathEvent event, double chancePercent, Player player){// TODO: DropIt
		//Player player = event.getEntity().getKiller();
		ItemStack itemstack = player.getInventory().getItemInMainHand();
		if(isDev) {return true;}
		if(itemstack != null){
			logDebug(" DI itemstack=" + itemstack.getType().toString());
			int enchantment_level = 0;
			if(config.getBoolean("head_settings.apply_looting", true)){
				enchantment_level = itemstack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
			}
			if(chancePercent == 0){
				logDebug(" DI chancePercent == 0");
				logDebug(" DI returning=false");
				return false;
			}
			logDebug(" DI enchantment_level=" + enchantment_level);
			DecimalFormat df = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.US));
			// Handle lower than 1 chances
			if((chancePercent < 1) && (enchantment_level == 0)){
				Random chanceRandom = chanceRandoms.computeIfAbsent(player, p -> new Random(p.getUniqueId().hashCode()));
				double inverseChance = 1 / (chancePercent / 100); // Adjust to percentage before calculating inverse
				logDebug(" DI chance=" + inverseChance);
				logDebug(" DI chancePercent=" + chancePercent);
				inverseChance = inverseChance - enchantment_level;
				logDebug(" DI inverseChance - enchantment_level = " + inverseChance);
				int randomValue = chanceRandom.nextInt((int) inverseChance);
				logDebug(" DI randomValue == 0 (" + (randomValue == 0) + ")");
				if (randomValue == 0) {
					logDebug(" DI returning=true");
					return true;
				}
			}else { // Normal 1-100 chance
				Random chanceRandom = chanceRandoms.computeIfAbsent(player, p -> new Random(p.getUniqueId().hashCode()));
				//double chance = Double.parseDouble(String.format(Locale.US, "%.2f", chanceRandom.nextDouble() * 100));
				double chance = Double.parseDouble(df.format(chanceRandom.nextDouble() * 100));
				logDebug(" DI chance=" + chance);
				logDebug(" DI chancePercent=" + chancePercent);
				chancePercent = chancePercent + enchantment_level;
				logDebug(" DI chancePercent + enchantment_level=" + chancePercent);
				logDebug(" DI " + chancePercent +" >= " + chance + " (" + (chancePercent >= chance) + ")");
				if ((chancePercent >= chance) || isDev){
					logDebug(" DI returning=true");
					return true;
				}
			}
		}
		logDebug(" DI returning=false");
		return false;
	}

	public boolean DropIt2( double chancepercent){
		double chance = Math.random() * 100;
		if(isDev) {return true;}
		logDebug(" DI2 chance=" + chance);
		logDebug(" DI2 chancepercent=" + chancepercent);
		return chancepercent >= chance;
	}

	public Random getRandom() {
		return random;
	}

	/**
	 * Generates a random integer between the specified minimum and maximum values (inclusive).
	 *
	 * @param min The minimum value for the random integer (inclusive).
	 * @param max The maximum value for the random integer (inclusive).
	 * @return A random integer within the specified range [min, max].
	 */
	public int randomBetween(int min, int max) {
		return min + random.nextInt((max - min) + 1);
	}


	private void dropHead(Player player, String key) {
		PlayerHead head = headManager.loadedPlayerHeads.get(key);
		if (head != null) {
			player.getWorld().dropItemNaturally(player.getLocation(), head.getHead());
			player.sendMessage("Dropped head: " + key);
		} else {
			player.sendMessage("Head not found: " + key);
		}
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
			logDebug("itemstack=" + itemstack.getType().toString() + " line:954");
			int enchantmentlevel = itemstack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);//.containsEnchantment(Enchantment.LOOT_BONUS_MOBS);
			logDebug("enchantmentlevel=" + enchantmentlevel + " line:956");
			double enchantmentlevelpercent = ((double)enchantmentlevel / 100);
			logDebug("enchantmentlevelpercent=" + enchantmentlevelpercent + " line:958");
			double chance = Math.random();
			logDebug("chance=" + chance + " line:960");
			double chancepercent = 0.25; /** Set to check config.yml later*/
			logDebug("chancepercent=" + chancepercent + " line:962");
			chancepercent = chancepercent + enchantmentlevelpercent;
			logDebug("chancepercent2=" + chancepercent + " line:964");
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

	public String getName(boolean isPlayer, Player player) {
		String daType = "";
		if(isPlayer) {
			daType = "player_heads";
		}else{
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
		logDebug("GN - player.getDisplayName()=" + player.getDisplayName());
		logDebug("GN - player.getName()=" + player.getName());
		logDebug("GN - head_settings.player_heads.announce_kill.displayname=" + getConfig().getBoolean("head_settings.player_heads.announce_kill.displayname"));
		if(config.getBoolean("head_settings.player_heads.announce_kill.displayname", false)) {
			playerName = ChatColorUtils.setColorsByCode(player.getDisplayName());
			if(getServer().getPluginManager().getPlugin("VentureChat") != null){
				MineverseChatPlayer mcp = MineverseChatAPI.getMineverseChatPlayer(player);
				String nick = mcp.getNickname();
				if(nick != null){
					logDebug("GN - mcp.getNickname()=" + mcp.getNickname());
					logDebug("GN - ChatColor.translateAlternateColorCodes('&', nick)=" + ChatColor.translateAlternateColorCodes('&', nick));
					nick = ChatColorUtils.setColorsByCode(nick);
					logDebug("VentureChat ChatColorUtils.setColorsByCode(nick)=" + nick);
					return nick;
				}
				logDebug("GN - VentureChat Nick=null using " + playerName);
				return Format.color(playerName);
			}else if(getServer().getPluginManager().getPlugin("Essentials") != null){
				Essentials ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
                assert ess != null;
                String nick = ess.getUserMap().getUser(player.getName()).getNickname();
				if(nick != null){
					logDebug("GN - Essentials Nick=" + nick);
					return ChatColor.translateAlternateColorCodes('&', nick);
				}
				logDebug("GN - Essentials Nick=null using: " + playerName );
				return ChatColorUtils.setColorsByCode(playerName);
			}else if(getServer().getPluginManager().getPlugin("HexNicks") != null){
				CompletableFuture<Component> nickFuture = HexNicks.api().getStoredNick(player);
				try {
					String nick = GsonComponentSerializer.gson().serialize(nickFuture.get());
					if(nick != null){
						logDebug("GN - HexNick Nick=" + nick);
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
				logDebug("GN - HexNick Nick=null using " + playerName);
				return ChatColorUtils.setColorsByCode(playerName);
			}else{
				logDebug("GN - No nickname found using=" + playerName);
				return playerName;
			}
		}else {
			playerName = player.getName();
		}

		return playerName;
	}

	public ItemStack makeHead(String displayName, String texture, String uuid, String noteBlockSound, boolean isPlayer, Player killer) {
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
		meta.setNoteBlockSound(NamespacedKey.minecraft( noteBlockSound ));
		ArrayList<String> lore = new ArrayList();
		if(config.getBoolean("head_settings.lore.show_killer", true)){
			lore.add(ChatColor.RESET + ChatColorUtils.setColors( langName.getString("killedby", "<RED>Killed <RESET>By <YELLOW><player>").replace("<player>", getName(isPlayer, killer)) ) );
		}
		if(config.getBoolean("head_settings.lore.show_plugin_name", true)){
			lore.add(ChatColor.AQUA + "MoreMobHeads");
		}
		meta.setLore(lore);
		meta.setLore(lore);
		meta.setDisplayName(displayName);
		PersistentDataContainer skullPDC = meta.getPersistentDataContainer();
		skullPDC.set(NAME_KEY, PersistentDataType.STRING, displayName);
		if (lore != null) {
			skullPDC.set(LORE_KEY, LORE_PDT, lore.toArray(new String[0]));
		}

		head.setItemMeta(meta);

		return head;
	}

	public ItemStack makeHead( String displayName, String texture, String uuid, ArrayList<String> lore, String noteblockSound ) {
		// Create the PlayerProfile using UUID with no name
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
		meta.setNoteBlockSound(NamespacedKey.minecraft( noteblockSound ));

		meta.setLore(lore);
		meta.setLore(lore);
		meta.setDisplayName(displayName);
		PersistentDataContainer skullPDC = meta.getPersistentDataContainer();
		skullPDC.set(NAME_KEY, PersistentDataType.STRING, displayName);
		if (lore != null) {
			skullPDC.set(LORE_KEY, LORE_PDT, lore.toArray(new String[0]));
		}

		head.setItemMeta(meta);

		return head;
	}

	/**
	 * Creates a custom head ItemStack with the specified name, texture URL (as a String),
	 * associated entity type, and player who delivered the killing blow.
	 *
	 * @param displayName The name of the custom head.
	 * @param texture String of the Base64-encoded string or direct URL of the texture for the custom head.
	 * @param uuid String UUID of Mob
	 * @param eType The EntityType associated with the custom head.
	 * @param killer The player who delivered the killing blow to the entity.
	 * @return An ItemStack representing the custom head with the provided name, texture, sound, and lore.
	 */
	public ItemStack makeHead(String displayName, String texture, String uuid, EntityType eType, Player killer) {
		// ( String langName, String texture, String uuid, ArrayList<String> lore, String noteblockSound )
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
		meta.setNoteBlockSound(NamespacedKey.minecraft( getSoundString(ChatColor.stripColor(displayName), eType) ));
		ArrayList<String> lore = new ArrayList();
		if(config.getBoolean("head_settings.lore.show_killer", true)){
			lore.add(ChatColor.RESET + ChatColorUtils.setColors( langName.getString("killedby", "<RED>Killed <RESET>By <YELLOW><player>").replace("<player>", getName(eType, killer)) ) );
		}
		if(config.getBoolean("head_settings.lore.show_plugin_name", true)){
			lore.add(ChatColor.AQUA + "MoreMobHeads");
		}
		meta.setLore(lore);
		meta.setLore(lore);
		meta.setDisplayName(displayName);
		PersistentDataContainer skullPDC = meta.getPersistentDataContainer();
		skullPDC.set(NAME_KEY, PersistentDataType.STRING, displayName);
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
	 * @param displayName The name of the custom head.
	 * @param type The type of the custom head.
	 * @param texture String of the Base64-encoded string or direct URL of the texture for the custom head.
	 * @param uuid String UUID of Mob
	 * @param eType The EntityType associated with the custom head.
	 * @param killer The player who delivered the killing blow to the entity.
	 * @return An ItemStack representing the custom head with the provided name, texture, sound, and lore.
	 */
	public ItemStack makeHead(String displayName, String type, String texture, String uuid, EntityType eType, Player killer) {
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
        assert meta != null;
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
		meta.setDisplayName(displayName);
		head.setItemMeta(meta);
		PersistentDataContainer skullPDC = head.getItemMeta().getPersistentDataContainer();
		skullPDC.set(NAME_KEY, PersistentDataType.STRING, displayName);
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
	 * @param base64 The Base64-encoded string containing the JSON structure with a URL or a URL string itself.
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
	 * @param base64 The Base64-encoded string containing the JSON structure with a URL
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
	 * @param eType The entity for which to determine the sound string.
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
			String bee = (displayname.contains(" ")) ? StrUtils.Left(displayname, displayname.indexOf(" ") - 1).toUpperCase(Locale.ROOT) : "BEE";
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
			String goat = (displayname.contains(" ")) ? StrUtils.Left(displayname, displayname.indexOf(" ") - 1).toUpperCase(Locale.ROOT) : "GOAT";
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
			String panda = (displayname.contains(" ")) ? StrUtils.Left(displayname, displayname.indexOf(" ") - 1).toUpperCase(Locale.ROOT) : "PANDA";
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
			String rabbit = (displayname.contains(" ")) ? StrUtils.Left(displayname, displayname.indexOf(" ") - 1).toUpperCase(Locale.ROOT) : "RABBIT";
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
			String vex = (displayname.contains(" ")) ? StrUtils.Left(displayname, displayname.indexOf(" ") - 1).toUpperCase(Locale.ROOT) : "VEX";
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
			//String wolf = (displayname.contains(" ")) ? StrUtils.Left(displayname, displayname.indexOf(" ") - 1).toUpperCase(Locale.ROOT) : "WOLF";

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
	 * Modifies the lore for a head drop, preserving base lore and adding conditional lines.
	 * If the base lore contains "Notify an Admin!" (ignoring color codes), returns only that line.
	 * Otherwise, adds "Killed by:" and "MoreMobHeads" based on config settings.
	 *
	 * @param baseLore The initial lore from the head's data (e.g., JSON or 404 default).
	 * @param victimIsPlayer True if the victim is a player, affecting killer name formatting.
	 * @param killer The player who killed the entity, or null if no killer.
	 * @return The modified ArrayList of lore lines.
	 */
	public ArrayList<String> modifyLore(ArrayList<String> baseLore, boolean victimIsPlayer, Player killer) {
		ArrayList<String> lore = baseLore != null ? new ArrayList<>(baseLore) : new ArrayList<>();

		// Check for "Notify an Admin!" after stripping color codes
		boolean hasNotifyAdmin = false;
		if (lore != null) {
			for (String line : lore) {
				if ((line != null) && ChatColor.stripColor(line).equals("Notify an Admin!")) {
					hasNotifyAdmin = true;
					break;
				}
			}
		}
		if (hasNotifyAdmin) {
			return new ArrayList<>(Arrays.asList("§cNotify an Admin§e!")); // Return only this line with colors
		}

		// Proceed with normal lore modification
		if (config.getBoolean("head_settings.lore.show_killer", true) && (killer != null)) {
			String killerName = getName(victimIsPlayer, killer); // Use mob head context
			String killedByFormat = langName.getString("killedby", "<RED>Killed <RESET>By <YELLOW><player>")
					.replace("<player>", killerName);
			lore.add(ChatColor.RESET + ChatColorUtils.setColors(killedByFormat));
		}
		if (config.getBoolean("head_settings.lore.show_plugin_name", true)) {
			lore.add(ChatColor.AQUA + "MoreMobHeads");
		}
		return lore;
	}

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


	public int isBlockHeadName(String string){ // TODO: isBlockHeadName
		logDebug("iBHN START");
		try{
			logDebug("iBH string=" + string);
			for(int randomBlockHead = 1; randomBlockHead < (blockhead_list.size() + 1); randomBlockHead++){
				ItemStack itemstack = blockhead_list.get(randomBlockHead);
				//ItemStack itemstack = blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
				if(itemstack != null){
					SkullMeta skullmeta = (SkullMeta) itemstack.getItemMeta();
					if(skullmeta != null){
						//if(debug&&skullmeta != null){logDebug("iBH getOwner_" + randomBlockHead + "=" + skullmeta.getOwner().toString());}
						if(skullmeta.getDisplayName() != null){
							if(ChatColor.stripColor(skullmeta.getDisplayName()).toLowerCase().equals(string.toLowerCase())){
								logDebug("iBHN END Sucess!");
								return randomBlockHead; //itemstack.getItemMeta().getDisplayName();
							}
						}
					}
				}
			}
		}catch(Exception exception){
			logDebug("iBHN END Failure=Exception");
			return -1;
		}
		//blockHeads
		logDebug("iBHN END Failure!");
		return -1;
	}

	public static void copyFile(String origin, String destination) throws IOException {
		FileUtils.copyFile(origin, destination);
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
			old_config.load(new File(from));
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_CHECK_CONFIG).error(exception));
		}
		LOGGER.log("Copying values from backup" + File.separatorChar + "config.yml...");
		config.set("plugin_settings.auto_update_check"							, old_config.get("plugin_settings.auto_update_check", true));
		config.set("plugin_settings.debug"										, old_config.get("plugin_settings.debug", false));
		config.set("plugin_settings.lang"										, old_config.get("plugin_settings.lang", "en_US"));

		config.set("global_settings.console.colorful_console"					, old_config.get("global_settings.console.colorful_console", true));
		config.set("global_settings.console.silent_console"						, old_config.get("global_settings.console.silent_console", false));
		config.set("global_settings.console.longpluginname"						, old_config.get("global_settings.console.longpluginname", true));
		config.set("global_settings.world.whitelist"							, old_config.get("global_settings.world.whitelist", ""));
		config.set("global_settings.world.blacklist"							, old_config.get("global_settings.world.blacklist", ""));
		config.set("global_settings.event.piston_extend"						, old_config.get("global_settings.event.piston_extend", true));
		config.set("global_settings.disable_dlc_nag_for_players"				, old_config.get("global_settings.disable_dlc_nag_for_players", false));
		config.set("global_settings.disable_dlc_nag_for_ops"					, old_config.get("global_settings.disable_dlc_nag_for_ops", false));
		config.set("global_settings.disable_dlc_display"						, old_config.get("global_settings.disable_dlc_display", false));

		config.set("head_settings.apply_looting"								, old_config.get("head_settings.apply_looting", true));
		config.set("head_settings.lore.show_killer"								, old_config.get("head_settings.lore.show_killer", true));
		config.set("head_settings.lore.show_plugin_name"						, old_config.get("head_settings.lore.show_plugin_name", true));
		config.set("head_settings.mini_blocks.stonecutter"						, old_config.get("head_settings.mini_blocks.stonecutter", false));
		config.set("head_settings.mini_blocks.perblock"							, old_config.get("head_settings.mini_blocks.perblock", 1));
		config.set("head_settings.player_heads.announce_kill.enabled"			, old_config.get("head_settings.player_heads.announce_kill.enabled", true));
		config.set("head_settings.player_heads.announce_kill.displayname"		, old_config.get("head_settings.player_heads.announce_kill.displayname", true));
		config.set("head_settings.player_heads.whitelist.enforce"				, old_config.get("head_settings.player_heads.whitelist.enforce", true));
		config.set("head_settings.player_heads.whitelist.player_head_whitelist"	, old_config.get("head_settings.player_heads.whitelist.player_head_whitelist", "names_go_here"));
		config.set("head_settings.player_heads.blacklist.enforce"				, old_config.get("head_settings.player_heads.blacklist.enforce", true));
		config.set("head_settings.player_heads.blacklist.player_head_blacklist"	, old_config.get("head_settings.player_heads.blacklist.player_head_blacklist", "names_go_here"));
		config.set("head_settings.player_heads.use_default_player_heads"		, old_config.get("head_settings.player_heads.use_default_player_heads", true));
		config.set("head_settings.mob_heads.announce_kill.enabled"				, old_config.get("head_settings.mob_heads.announce_kill.enabled", true));
		config.set("head_settings.mob_heads.announce_kill.displayname"			, old_config.get("head_settings.mob_heads.announce_kill.displayname", true));
		config.set("head_settings.mob_heads.whitelist"							, old_config.get("head_settings.mob_heads.whitelist", ""));
		config.set("head_settings.mob_heads.blacklist"							, old_config.get("head_settings.mob_heads.blacklist", ""));
		config.set("head_settings.mob_heads.nametag"							, old_config.get("head_settings.mob_heads.nametag", false));
		config.set("head_settings.mob_heads.vanilla_heads.creeper"				, old_config.get("head_settings.mob_heads.vanilla_heads.creeper", false));
		config.set("head_settings.mob_heads.vanilla_heads.ender_dragon"			, old_config.get("head_settings.mob_heads.vanilla_heads.ender_dragon", false));
		config.set("head_settings.mob_heads.vanilla_heads.piglin"				, old_config.get("head_settings.mob_heads.vanilla_heads.piglin", false));
		config.set("head_settings.mob_heads.vanilla_heads.skeleton"				, old_config.get("head_settings.mob_heads.vanilla_heads.skeleton", false));
		config.set("head_settings.mob_heads.vanilla_heads.wither_skeleton"		, old_config.get("head_settings.mob_heads.vanilla_heads.wither_skeleton", false));
		config.set("head_settings.mob_heads.vanilla_heads.zombie"				, old_config.get("head_settings.mob_heads.vanilla_heads.zombie", false));

		config.set("wandering_trades.custom_wandering_trader"					, old_config.get("wandering_trades.custom_wandering_trader", true));
		config.set("wandering_trades.keep_default_trades"						, old_config.get("wandering_trades.keep_default_trades", true));
		config.set("wandering_trades.player_heads.enabled"						, old_config.get("wandering_trades.player_heads.enabled", true));
		config.set("wandering_trades.player_heads.min"							, old_config.get("wandering_trades.player_heads.min", 0));
		config.set("wandering_trades.player_heads.max"							, old_config.get("wandering_trades.player_heads.max", 5));
		config.set("wandering_trades.block_heads.enabled"						, old_config.get("wandering_trades.block_heads.enabled", true));

		if( old_config.get("wandering_trades.block_heads.pre_116.min") != null ) {
			int pre_116_min = old_config.getInt("wandering_trades.block_heads.pre_116.min", 0);
			int is_116_min = old_config.getInt("wandering_trades.block_heads.is_116.min", 0);
			int is_117_min = old_config.getInt("wandering_trades.block_heads.is_117.min", 0);
			int pre_116_max = old_config.getInt("wandering_trades.block_heads.pre_116.max", 5);
			int is_116_max = old_config.getInt("wandering_trades.block_heads.is_116.max", 5);
			int is_117_max = old_config.getInt("wandering_trades.block_heads.is_117.max", 5);
			int newMin = pre_116_min + is_116_min + is_117_min;
			int newMax = pre_116_max + is_116_max + is_117_max;
			config.set("wandering_trades.block_heads.min"					, newMin );
			config.set("wandering_trades.block_heads.max"					, newMax );
		}else {
			config.set("wandering_trades.block_heads.min"					, old_config.get("wandering_trades.block_heads.min") );
			config.set("wandering_trades.block_heads.max"					, old_config.get("wandering_trades.block_heads.max") );
		}

		config.set("wandering_trades.custom_trades.enabled"						, old_config.get("wandering_trades.custom_trades.enabled", false));
		config.set("wandering_trades.custom_trades.min"							, old_config.get("wandering_trades.custom_trades.min", 0));
		config.set("wandering_trades.custom_trades.max"							, old_config.get("wandering_trades.custom_trades.max", 5));
		LOGGER.log("Saving config.yml...");
		try {
			config.save(new File(getDataFolder(), "config.yml"));
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_SAVE_CONFIG).error(exception));
		}
		config = new YmlConfiguration(this);
		old_config = null;
		LOGGER.log("Update complete config.yml...");
	}

	public void copyChance(String from, String to){
		chanceConfig = new YmlConfiguration(this);
		old_chanceConfig = new YmlConfiguration(this);
		try {
			chanceConfig.load(new File(to));
			old_chanceConfig.load(new File(from));
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CHANCE_LOAD_ERROR).error(exception));
        }
		LOGGER.log("Copying values frome backup/chance_config.yml to chance_config.yml");
		chanceConfig.set("chance_percent.player"								, normalize(old_chanceConfig.getDouble("chance_percent.player", 50.0) ) );
		chanceConfig.set("chance_percent.named_mob"								, normalize(old_chanceConfig.getDouble("chance_percent.named_mob", 10.0) ) );
		chanceConfig.set("chance_percent.allay"									, normalize(old_chanceConfig.getDouble("chance_percent.allay", 20.0) ) );
		chanceConfig.set("chance_percent.armadillo"								, normalize(old_chanceConfig.getDouble("chance_percent.armadillo", 10.0) ) );
		chanceConfig.set("chance_percent.axolotl.blue"							, normalize(old_chanceConfig.getDouble("chance_percent.axolotl.blue", 100.0) ) );
		chanceConfig.set("chance_percent.axolotl.cyan"							, normalize(old_chanceConfig.getDouble("chance_percent.axolotl.cyan", 20.0) ) );
		chanceConfig.set("chance_percent.axolotl.gold"							, normalize(old_chanceConfig.getDouble("chance_percent.axolotl.gold", 20.0) ) );
		chanceConfig.set("chance_percent.axolotl.lucy"							, normalize(old_chanceConfig.getDouble("chance_percent.axolotl.lucy", 20.0) ) );
		chanceConfig.set("chance_percent.axolotl.wild"							, normalize(old_chanceConfig.getDouble("chance_percent.axolotl.wild", 20.0) ) );
		chanceConfig.set("chance_percent.bat"									, normalize(old_chanceConfig.getDouble("chance_percent.bat", 10.0) ) );
		chanceConfig.set("chance_percent.bee.angry_pollinated"					, normalize(old_chanceConfig.getDouble("chance_percent.bee.angry_pollinated", 20.0) ) );
		chanceConfig.set("chance_percent.bee.angry"								, normalize(old_chanceConfig.getDouble("chance_percent.bee.angry", 20.0) ) );
		chanceConfig.set("chance_percent.bee.pollinated"						, normalize(old_chanceConfig.getDouble("chance_percent.bee.pollinated", 20.0) ) );
		chanceConfig.set("chance_percent.bee.chance_percent"					, normalize(old_chanceConfig.getDouble("chance_percent.bee.normal", 20.0) ) );
		chanceConfig.set("chance_percent.blaze"									, normalize(old_chanceConfig.getDouble("chance_percent.blaze", 0.5) ) );
		chanceConfig.set("chance_percent.camel"									, normalize(old_chanceConfig.getDouble("chance_percent.camel", 27.0) ) );
		chanceConfig.set("chance_percent.cat.all_black"							, normalize(old_chanceConfig.getDouble("chance_percent.cat.all_black", 33.0) ) );
		chanceConfig.set("chance_percent.cat.black"								, normalize(old_chanceConfig.getDouble("chance_percent.cat.black", 33.0) ) );
		chanceConfig.set("chance_percent.cat.british_shorthair"					, normalize(old_chanceConfig.getDouble("chance_percent.cat.british_shorthair", 33.0) ) );
		chanceConfig.set("chance_percent.cat.calico"							, normalize(old_chanceConfig.getDouble("chance_percent.cat.calico", 33.0) ) );
		chanceConfig.set("chance_percent.cat.jellie"							, normalize(old_chanceConfig.getDouble("chance_percent.cat.jellie", 33.0) ) );
		chanceConfig.set("chance_percent.cat.persian"							, normalize(old_chanceConfig.getDouble("chance_percent.cat.persian", 33.0) ) );
		chanceConfig.set("chance_percent.cat.ragdoll"							, normalize(old_chanceConfig.getDouble("chance_percent.cat.ragdoll", 33.0) ) );
		chanceConfig.set("chance_percent.cat.red"								, normalize(old_chanceConfig.getDouble("chance_percent.cat.red", 33.0) ) );
		chanceConfig.set("chance_percent.cat.siamese"							, normalize(old_chanceConfig.getDouble("chance_percent.cat.siamese", 33.0) ) );
		chanceConfig.set("chance_percent.cat.tabby"								, normalize(old_chanceConfig.getDouble("chance_percent.cat.tabby", 33.0) ) );
		chanceConfig.set("chance_percent.cat.white"								, normalize(old_chanceConfig.getDouble("chance_percent.cat.white", 33.0) ) );

		chanceConfig.set("chance_percent.cave_spider"							, normalize(old_chanceConfig.getDouble("chance_percent.cave_spider", 0.5) ) );
		chanceConfig.set("chance_percent.chicken"								, normalize(old_chanceConfig.getDouble("chance_percent.chicken", 1.0) ) );
		chanceConfig.set("chance_percent.cod"									, normalize(old_chanceConfig.getDouble("chance_percent.cod", 10.0) ) );
		chanceConfig.set("chance_percent.cow"									, normalize(old_chanceConfig.getDouble("chance_percent.cow", 1.0) ) );
		chanceConfig.set("chance_percent.creeper"								, normalize(old_chanceConfig.getDouble("chance_percent.creeper", 50.0) ) );
		chanceConfig.set("chance_percent.creeper_charged"						, normalize(old_chanceConfig.getDouble("chance_percent.creeper_charged", 100.0) ) );
		chanceConfig.set("chance_percent.dolphin"								, normalize(old_chanceConfig.getDouble("chance_percent.dolphin", 33.0) ) );
		chanceConfig.set("chance_percent.donkey"								, normalize(old_chanceConfig.getDouble("chance_percent.donkey", 20.0) ) );
		chanceConfig.set("chance_percent.drowned"								, normalize(old_chanceConfig.getDouble("chance_percent.drowned", 5.0) ) );
		chanceConfig.set("chance_percent.elder_guardian"						, normalize(old_chanceConfig.getDouble("chance_percent.elder_guardian", 100.0) ) );
		chanceConfig.set("chance_percent.ender_dragon"							, normalize(old_chanceConfig.getDouble("chance_percent.ender_dragon", 100.0) ) );
		chanceConfig.set("chance_percent.enderman"								, normalize(old_chanceConfig.getDouble("chance_percent.enderman", 0.5) ) );
		chanceConfig.set("chance_percent.endermite"								, normalize(old_chanceConfig.getDouble("chance_percent.endermite", 10.0) ) );
		chanceConfig.set("chance_percent.evoker"								, normalize(old_chanceConfig.getDouble("chance_percent.evoker", 25.0) ) );
		chanceConfig.set("chance_percent.fox.red"								, normalize(old_chanceConfig.getDouble("chance_percent.fox.red", 10.0) ) );
		chanceConfig.set("chance_percent.fox.snow"								, normalize(old_chanceConfig.getDouble("chance_percent.fox.snow", 20.0) ) );
		chanceConfig.set("chance_percent.frog.cold"								, normalize(old_chanceConfig.getDouble("chance_percent.frog.cold", 20.0) ) );
		chanceConfig.set("chance_percent.frog.temperate"						, normalize(old_chanceConfig.getDouble("chance_percent.frog.temperate", 20.0) ) );
		chanceConfig.set("chance_percent.frog.warm"								, normalize(old_chanceConfig.getDouble("chance_percent.frog.warm", 20.0) ) );
		chanceConfig.set("chance_percent.ghast"									, normalize(old_chanceConfig.getDouble("chance_percent.ghast", 6.25) ) );
		chanceConfig.set("chance_percent.giant"									, normalize(old_chanceConfig.getDouble("chance_percent.giant", 2.5) ) );
		chanceConfig.set("chance_percent.glow_squid"							, normalize(old_chanceConfig.getDouble("chance_percent.glow_squid", 5.0) ) );
		chanceConfig.set("chance_percent.goat.mormal"							, normalize(old_chanceConfig.getDouble("chance_percent.goat.normal", 1.0) ) );
		chanceConfig.set("chance_percent.goat.screaming"						, normalize(old_chanceConfig.getDouble("chance_percent.goat.screaming", 100.0) ) );
		chanceConfig.set("chance_percent.guardian"								, normalize(old_chanceConfig.getDouble("chance_percent.guardian", 0.5) ) );
		chanceConfig.set("chance_percent.hoglin"								, normalize(old_chanceConfig.getDouble("chance_percent.hoglin", 3.0) ) );
		chanceConfig.set("chance_percent.horse.black"							, normalize(old_chanceConfig.getDouble("chance_percent.horse.black", 27.0) ) );
		chanceConfig.set("chance_percent.horse.brown"							, normalize(old_chanceConfig.getDouble("chance_percent.horse.brown", 27.0) ) );
		chanceConfig.set("chance_percent.horse.chestnut"						, normalize(old_chanceConfig.getDouble("chance_percent.horse.chestnut", 27.0) ) );
		chanceConfig.set("chance_percent.horse.creamy"							, normalize(old_chanceConfig.getDouble("chance_percent.horse.creamy", 27.0) ) );
		chanceConfig.set("chance_percent.horse.dark_brown"						, normalize(old_chanceConfig.getDouble("chance_percent.horse.dark_brown", 27.0) ) );
		chanceConfig.set("chance_percent.horse.gray"							, normalize(old_chanceConfig.getDouble("chance_percent.horse.gray", 27.0) ) );
		chanceConfig.set("chance_percent.horse.white"							, normalize(old_chanceConfig.getDouble("chance_percent.horse.white", 27.0) ) );
		chanceConfig.set("chance_percent.husk"									, normalize(old_chanceConfig.getDouble("chance_percent.husk", 6.0) ) );
		chanceConfig.set("chance_percent.illusioner"							, normalize(old_chanceConfig.getDouble("chance_percent.illusioner", 25.0) ) );
		chanceConfig.set("chance_percent.iron_golem"							, normalize(old_chanceConfig.getDouble("chance_percent.iron_golem", 5.0) ) );
		chanceConfig.set("chance_percent.llama.brown"							, normalize(old_chanceConfig.getDouble("chance_percent.llama.brown", 24.0) ) );
		chanceConfig.set("chance_percent.llama.creamy"							, normalize(old_chanceConfig.getDouble("chance_percent.llama.creamy", 24.0) ) );
		chanceConfig.set("chance_percent.llama.gray"							, normalize(old_chanceConfig.getDouble("chance_percent.llama.gray", 24.0) ) );
		chanceConfig.set("chance_percent.llama.white"							, normalize(old_chanceConfig.getDouble("chance_percent.llama.white", 24.0) ) );
		chanceConfig.set("chance_percent.magma_cube"							, normalize(old_chanceConfig.getDouble("chance_percent.magma_cube", 0.5) ) );
		chanceConfig.set("chance_percent.mule"									, normalize(old_chanceConfig.getDouble("chance_percent.mule", 20.0) ) );
		chanceConfig.set("chance_percent.mushroom_cow.red"						, normalize(old_chanceConfig.getDouble("chance_percent.mushroom_cow.red", 1.0) ) );
		chanceConfig.set("chance_percent.mushroom_cow.brown"					, normalize(old_chanceConfig.getDouble("chance_percent.mushroom_cow.brown", 10.0) ) );
		chanceConfig.set("chance_percent.ocelot"								, normalize(old_chanceConfig.getDouble("chance_percent.ocelot", 20.0) ) );
		chanceConfig.set("chance_percent.panda.aggressive"						, normalize(old_chanceConfig.getDouble("chance_percent.panda.aggressive", 27.0) ) );
		chanceConfig.set("chance_percent.panda.brown"							, normalize(old_chanceConfig.getDouble("chance_percent.panda.brown", 27.0) ) );
		chanceConfig.set("chance_percent.panda.lazy"							, normalize(old_chanceConfig.getDouble("chance_percent.panda.lazy", 27.0) ) );
		chanceConfig.set("chance_percent.panda.normal"							, normalize(old_chanceConfig.getDouble("chance_percent.panda.normal", 27.0) ) );
		chanceConfig.set("chance_percent.panda.playful"							, normalize(old_chanceConfig.getDouble("chance_percent.panda.playful", 27.0) ) );
		chanceConfig.set("chance_percent.panda.weak"							, normalize(old_chanceConfig.getDouble("chance_percent.panda.weak", 27.0) ) );
		chanceConfig.set("chance_percent.panda.worried"							, normalize(old_chanceConfig.getDouble("chance_percent.panda.worried", 27.0) ) );
		chanceConfig.set("chance_percent.parrot.blue"							, normalize(old_chanceConfig.getDouble("chance_percent.parrot.blue", 25.0) ) );
		chanceConfig.set("chance_percent.parrot.cyan"							, normalize(old_chanceConfig.getDouble("chance_percent.parrot.cyan", 25.0) ) );
		chanceConfig.set("chance_percent.parrot.gray"							, normalize(old_chanceConfig.getDouble("chance_percent.parrot.gray", 25.0) ) );
		chanceConfig.set("chance_percent.parrot.green"							, normalize(old_chanceConfig.getDouble("chance_percent.parrot.green", 25.0) ) );
		chanceConfig.set("chance_percent.parrot.red"							, normalize(old_chanceConfig.getDouble("chance_percent.parrot.red", 25.0) ) );
		chanceConfig.set("chance_percent.phantom"								, normalize(old_chanceConfig.getDouble("chance_percent.phantom", 10.0) ) );
		chanceConfig.set("chance_percent.pig"									, normalize(old_chanceConfig.getDouble("chance_percent.pig", 1.0) ) );
		chanceConfig.set("chance_percent.piglin"								, normalize(old_chanceConfig.getDouble("chance_percent.piglin", 4.0) ) );
		chanceConfig.set("chance_percent.pig_zombie"							, normalize(old_chanceConfig.getDouble("chance_percent.pig_zombie", 0.5) ) );
		chanceConfig.set("chance_percent.pillager"								, normalize(old_chanceConfig.getDouble("chance_percent.pillager", 2.5) ) );
		chanceConfig.set("chance_percent.polar_bear"							, normalize(old_chanceConfig.getDouble("chance_percent.polar_bear", 20.0) ) );
		chanceConfig.set("chance_percent.pufferfish"							, normalize(old_chanceConfig.getDouble("chance_percent.pufferfish", 15.0) ) );
		chanceConfig.set("chance_percent.rabbit.black"							, normalize(old_chanceConfig.getDouble("chance_percent.rabbit.black", 26.0) ) );
		chanceConfig.set("chance_percent.rabbit.black_and_white"				, normalize(old_chanceConfig.getDouble("chance_percent.rabbit.black_and_white", 26.0) ) );
		chanceConfig.set("chance_percent.rabbit.brown"							, normalize(old_chanceConfig.getDouble("chance_percent.rabbit.brown", 26.0) ) );
		chanceConfig.set("chance_percent.rabbit.gold"							, normalize(old_chanceConfig.getDouble("chance_percent.rabbit.gold", 26.0) ) );
		chanceConfig.set("chance_percent.rabbit.salt_and_pepper"				, normalize(old_chanceConfig.getDouble("chance_percent.rabbit.salt_and_pepper", 26.0) ) );
		chanceConfig.set("chance_percent.rabbit.the_killer_bunny"				, normalize(old_chanceConfig.getDouble("chance_percent.rabbit.the_killer_bunny", 100.0) ) );
		chanceConfig.set("chance_percent.rabbit.toast"							, normalize(old_chanceConfig.getDouble("chance_percent.rabbit.toast", 26.0) ) );
		chanceConfig.set("chance_percent.rabbit.white"							, normalize(old_chanceConfig.getDouble("chance_percent.rabbit.white", 26.0) ) );
		chanceConfig.set("chance_percent.ravager"								, normalize(old_chanceConfig.getDouble("chance_percent.ravager", 25.0) ) );
		chanceConfig.set("chance_percent.salmon"								, normalize(old_chanceConfig.getDouble("chance_percent.salmon", 10.0) ) );
		chanceConfig.set("chance_percent.sheep.black"							, normalize(old_chanceConfig.getDouble("chance_percent.sheep.black", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.blue"							, normalize(old_chanceConfig.getDouble("chance_percent.sheep.blue", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.brown"							, normalize(old_chanceConfig.getDouble("chance_percent.sheep.brown", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.cyan"							, normalize(old_chanceConfig.getDouble("chance_percent.sheep.cyan", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.gray"							, normalize(old_chanceConfig.getDouble("chance_percent.sheep.gray", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.green"							, normalize(old_chanceConfig.getDouble("chance_percent.sheep.green", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.jeb_"							, normalize(old_chanceConfig.getDouble("chance_percent.sheep.jeb_", 10.0) ) );
		chanceConfig.set("chance_percent.sheep.light_blue"						, normalize(old_chanceConfig.getDouble("chance_percent.sheep.light_blue", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.light_gray"						, normalize(old_chanceConfig.getDouble("chance_percent.sheep.light_gray", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.lime"							, normalize(old_chanceConfig.getDouble("chance_percent.sheep.lime", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.magenta"							, normalize(old_chanceConfig.getDouble("chance_percent.sheep.magenta", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.orange"							, normalize(old_chanceConfig.getDouble("chance_percent.sheep.orange", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.pink"							, normalize(old_chanceConfig.getDouble("chance_percent.sheep.pink", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.purple"							, normalize(old_chanceConfig.getDouble("chance_percent.sheep.purple", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.red"								, normalize(old_chanceConfig.getDouble("chance_percent.sheep.red", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.white"							, normalize(old_chanceConfig.getDouble("chance_percent.sheep.white", 1.75) ) );
		chanceConfig.set("chance_percent.sheep.yellow"							, normalize(old_chanceConfig.getDouble("chance_percent.sheep.yellow", 1.75) ) );
		chanceConfig.set("chance_percent.shulker"								, normalize(old_chanceConfig.getDouble("chance_percent.shulker", 5.0) ) );
		chanceConfig.set("chance_percent.silverfish"							, normalize(old_chanceConfig.getDouble("chance_percent.silverfish", 5.0) ) );
		chanceConfig.set("chance_percent.skeleton"								, normalize(old_chanceConfig.getDouble("chance_percent.skeleton", 2.5) ) );
		chanceConfig.set("chance_percent.skeleton_horse"						, normalize(old_chanceConfig.getDouble("chance_percent.skeleton_horse", 20.0) ) );
		chanceConfig.set("chance_percent.slime"									, normalize(old_chanceConfig.getDouble("chance_percent.slime", 0.5) ) );
		chanceConfig.set("chance_percent.sniffer"								, normalize(old_chanceConfig.getDouble("chance_percent.sniffer", 50.0) ) );
		chanceConfig.set("chance_percent.snowman"								, normalize(old_chanceConfig.getDouble("chance_percent.snowman", 5.0) ) );
		chanceConfig.set("chance_percent.spider"								, normalize(old_chanceConfig.getDouble("chance_percent.spider", 0.5) ) );
		chanceConfig.set("chance_percent.squid"									, normalize(old_chanceConfig.getDouble("chance_percent.squid", 5.0) ) );
		chanceConfig.set("chance_percent.stray"									, normalize(old_chanceConfig.getDouble("chance_percent.stray", 6.0) ) );
		chanceConfig.set("chance_percent.strider"								, normalize(old_chanceConfig.getDouble("chance_percent.strider", 10.0) ) );
		chanceConfig.set("chance_percent.tadpole"								, normalize(old_chanceConfig.getDouble("chance_percent.tadpole", 10.0) ) );
		chanceConfig.set("chance_percent.trader_llama.brown"					, normalize(old_chanceConfig.getDouble("chance_percent.trader_llama.brown", 24.0) ) );
		chanceConfig.set("chance_percent.trader_llama.creamy"					, normalize(old_chanceConfig.getDouble("chance_percent.trader_llama.creamy", 24.0) ) );
		chanceConfig.set("chance_percent.trader_llama.gray"						, normalize(old_chanceConfig.getDouble("chance_percent.trader_llama.gray", 24.0) ) );
		chanceConfig.set("chance_percent.trader_llama.white"					, normalize(old_chanceConfig.getDouble("chance_percent.trader_llama.white", 24.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.tropical_fish"			, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.tropical_fish", 10.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.anemone"					, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.anemone", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.black_tang"				, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.black_tang", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.blue_tang"				, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.blue_tang", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.butterflyfish"			, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.butterflyfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.cichlid"					, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.cichlid", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.clownfish"				, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.clownfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.cotton_candy_betta"		, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.cotton_candy_betta", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.dottyback"				, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.dottyback", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.emperor_red_snapper"		, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.emperor_red_snapper", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.goatfish"				, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.goatfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.moorish_idol"			, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.moorish_idol", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.ornate_butterflyfish"	, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.ornate_butterflyfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.parrotfish"				, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.parrotfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.queen_angelfish"			, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.queen_angelfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.red_cichlid"				, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.red_cichlid", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.red_lipped_blenny"		, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.red_lipped_blenny", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.red_snapper"				, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.red_snapper", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.threadfin"				, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.threadfin", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.tomato_clownfish"		, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.tomato_clownfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.triggerfish"				, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.triggerfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.yellowtail_parrotfish"	, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.yellow_parrotfish", 50.0) ) );
		chanceConfig.set("chance_percent.tropical_fish.yellow_tang"				, normalize(old_chanceConfig.getDouble("chance_percent.tropical_fish.yellow_tang", 50.0) ) );

		chanceConfig.set("chance_percent.turtle"							, normalize(old_chanceConfig.getDouble("chance_percent.turtle", 10.0) ) );
		chanceConfig.set("chance_percent.vex"								, normalize(old_chanceConfig.getDouble("chance_percent.vex", 10.0) ) );
		chanceConfig.set("chance_percent.villager.desert.armorer"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.desert.armorer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.butcher"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.desert.butcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.cartographer"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.desert.cartographer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.cleric"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.desert.cleric", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.farmer"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.desert.farmer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.fisherman"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.desert.fisherman", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.fletcher"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.desert.fletcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.leatherworker"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.desert.leatherworker", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.librarian"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.desert.librarian", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.mason"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.desert.mason", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.nitwit"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.desert.nitwit", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.none"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.desert.none", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.shepherd"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.desert.shepherd", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.toolsmith"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.desert.toolsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.desert.weaponsmith"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.desert.weaponsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.armorer"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.jungle.armorer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.butcher"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.jungle.butcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.cartographer"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.jungle.cartographer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.cleric"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.jungle.cleric", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.farmer"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.jungle.farmer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.fisherman"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.jungle.fisherman", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.fletcher"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.jungle.fletcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.leatherworker"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.jungle.leatherworker", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.librarian"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.jungle.librarian", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.mason"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.jungle.mason", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.nitwit"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.jungle.nitwit", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.none"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.jungle.none", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.shepherd"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.jungle.shepherd", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.toolsmith"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.jungle.toolsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.jungle.weaponsmith"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.jungle.weaponsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.armorer"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.plains.armorer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.butcher"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.plains.butcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.cartographer"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.plains.cartographer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.cleric"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.plains.cleric", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.farmer"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.plains.farmer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.fisherman"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.plains.fisherman", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.fletcher"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.plains.fletcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.leatherworker"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.plains.leatherworker", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.librarian"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.plains.librarian", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.mason"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.plains.mason", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.nitwit"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.plains.nitwit", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.none"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.plains.none", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.shepherd"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.plains.shepherd", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.toolsmith"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.plains.toolsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.plains.weaponsmith"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.plains.weaponsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.armorer"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.savanna.armorer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.butcher"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.savanna.butcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.cartographer"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.savanna.cartographer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.cleric"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.savanna.cleric", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.farmer"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.savanna.farmer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.fisherman"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.savanna.fisherman", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.fletcher"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.savanna.fletcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.leatherworker"	, normalize(old_chanceConfig.getDouble("chance_percent.villager.savanna.leatherworker", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.librarian"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.savanna.librarian", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.mason"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.savanna.mason", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.nitwit"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.savanna.nitwit", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.none"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.savanna.none", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.shepherd"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.savanna.shepherd", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.toolsmith"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.savanna.toolsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.savanna.weaponsmith"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.savanna.weaponsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.armorer"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.snow.armorer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.butcher"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.snow.butcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.cartographer"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.snow.cartographer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.cleric"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.snow.cleric", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.farmer"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.snow.farmer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.fisherman"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.snow.fisherman", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.fletcher"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.snow.fletcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.leatherworker"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.snow.leatherworker", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.librarian"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.snow.librarian", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.mason"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.snow.mason", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.nitwit"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.snow.nitwit", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.none"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.snow.none", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.shepherd"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.snow.shepherd", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.toolsmith"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.snow.toolsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.snow.weaponsmith"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.snow.weaponsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.armorer"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.swamp.armorer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.butcher"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.swamp.butcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.cartographer"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.swamp.cartographer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.cleric"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.swamp.cleric", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.farmer"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.swamp.farmer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.fisherman"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.swamp.fisherman", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.fletcher"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.swamp.fletcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.leatherworker"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.swamp.leatherworker", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.librarian"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.swamp.librarian", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.mason"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.swamp.mason", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.nitwit"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.swamp.nitwit", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.none"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.swamp.none", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.shepherd"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.swamp.shepherd", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.toolsmith"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.swamp.toolsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.swamp.weaponsmith"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.swamp.weaponsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.armorer"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.taiga.armorer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.butcher"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.taiga.butcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.cartographer"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.taiga.cartographer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.cleric"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.taiga.cleric", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.farmer"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.taiga.farmer", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.fisherman"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.taiga.fisherman", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.fletcher"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.taiga.fletcher", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.leatherworker"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.taiga.leatherworker", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.librarian"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.taiga.librarian", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.mason"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.taiga.mason", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.nitwit"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.taiga.nitwit", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.none"				, normalize(old_chanceConfig.getDouble("chance_percent.villager.taiga.none", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.shepherd"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.taiga.shepherd", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.toolsmith"			, normalize(old_chanceConfig.getDouble("chance_percent.villager.taiga.toolsmith", 100.0) ) );
		chanceConfig.set("chance_percent.villager.taiga.weaponsmith"		, normalize(old_chanceConfig.getDouble("chance_percent.villager.taiga.weaponsmith", 100.0) ) );
		chanceConfig.set("chance_percent.vindicator"						, normalize(old_chanceConfig.getDouble("chance_percent.vindicator", 5.0) ) );
		chanceConfig.set("chance_percent.wandering_trader"					, normalize(old_chanceConfig.getDouble("chance_percent.wandering_trader", 100.0) ) );
		chanceConfig.set("chance_percent.warden"							, normalize(old_chanceConfig.getDouble("chance_percent.warden", 100.0) ) );
		chanceConfig.set("chance_percent.witch"								, normalize(old_chanceConfig.getDouble("chance_percent.witch", 0.5) ) );
		chanceConfig.set("chance_percent.wither"							, normalize(old_chanceConfig.getDouble("chance_percent.wither", 100.0) ) );
		chanceConfig.set("chance_percent.wither_skeleton"					, normalize(old_chanceConfig.getDouble("chance_percent.wither_skeleton", 2.5) ) );
		chanceConfig.set("chance_percent.wolf.ashen"						, normalize(old_chanceConfig.getDouble("chance_percent.wolf.ashen", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.black"						, normalize(old_chanceConfig.getDouble("chance_percent.wolf.black", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.chestnut"						, normalize(old_chanceConfig.getDouble("chance_percent.wolf.chestnut", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.pale"							, normalize(old_chanceConfig.getDouble("chance_percent.wolf.pale", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.rusty"						, normalize(old_chanceConfig.getDouble("chance_percent.wolf.rusty", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.snowy"						, normalize(old_chanceConfig.getDouble("chance_percent.wolf.snowy", 50.0) ) );
		chanceConfig.set("chance_percent.wolf.spotted"						, normalize(old_chanceConfig.getDouble("chance_percent.wolf.spotted", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.striped"						, normalize(old_chanceConfig.getDouble("chance_percent.wolf.striped", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.woods"						, normalize(old_chanceConfig.getDouble("chance_percent.wolf.woods", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_ashen"					, normalize(old_chanceConfig.getDouble("chance_percent.wolf.angry_ashen", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_black"					, normalize(old_chanceConfig.getDouble("chance_percent.wolf.angry_black", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_chestnut"				, normalize(old_chanceConfig.getDouble("chance_percent.wolf.angry_chestnut", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_pale"					, normalize(old_chanceConfig.getDouble("chance_percent.wolf.angry_pale", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_rusty"					, normalize(old_chanceConfig.getDouble("chance_percent.wolf.angry_rusty", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_snowy"					, normalize(old_chanceConfig.getDouble("chance_percent.wolf.angry_snowy", 50.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_spotted"				, normalize(old_chanceConfig.getDouble("chance_percent.wolf.angry_spotted", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_striped"				, normalize(old_chanceConfig.getDouble("chance_percent.wolf.angry_striped", 20.0) ) );
		chanceConfig.set("chance_percent.wolf.angry_woods"					, normalize(old_chanceConfig.getDouble("chance_percent.wolf.angry_woods", 20.0) ) );
		chanceConfig.set("chance_percent.zoglin"							, normalize(old_chanceConfig.getDouble("chance_percent.zoglin", 20.0) ) );
		chanceConfig.set("chance_percent.zombie"							, normalize(old_chanceConfig.getDouble("chance_percent.zombie", 2.5) ) );
		chanceConfig.set("chance_percent.zombie_horse"						, normalize(old_chanceConfig.getDouble("chance_percent.zombie_horse", 100.0) ) );
		chanceConfig.set("chance_percent.zombie_pigman"						, normalize(old_chanceConfig.getDouble("chance_percent.zombie_pigman", 0.5) ) );
		chanceConfig.set("chance_percent.zombified_piglin"					, normalize(old_chanceConfig.getDouble("chance_percent.zombified_piglin", 0.5) ) );
		chanceConfig.set("chance_percent.zombie_villager"					, normalize(old_chanceConfig.getDouble("chance_percent.zombie_villager", 50.0) ) );
		try {
			chanceConfig.save(to);
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CHANCE_SAVE_ERROR).error(exception));
		}
		LOGGER.log("chance_config.yml has been updated!");
		old_chanceConfig = null;
	}

	public void copyConfigValues(String currentFilePath, String oldFilePath) {
		// Load configurations
		YmlConfiguration currentConfig = new YmlConfiguration(this);
		YmlConfiguration oldConfig = new YmlConfiguration(this);

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




	public ItemStack fixHeadStack(ItemStack offHand, ItemStack mainHand){
		NBTItem nbti = new NBTItem(offHand);
		Set<String> SkullKeys = nbti.getKeys();
		int damage = nbti.getInteger("Damage");
		NBTCompound display = nbti.getCompound("display");
		NBTCompound SkullOwner = nbti.getCompound("SkullOwner");
		logDebug("FHS Offhand damage=" + damage);
        assert display != null;
		logDebug("FHS Offhand display=" + display.toString());
        assert SkullOwner != null;
		logDebug("FHS Offhand SkullOwner=" + SkullOwner.toString());

		NBTItem nbti2 = new NBTItem(mainHand);
		Set<String> SkullKeys2 = nbti2.getKeys();
		int damage2 = nbti2.getInteger("Damage");
		NBTCompound display2 = nbti2.getCompound("display");
		NBTCompound SkullOwner2 = nbti2.getCompound("SkullOwner");
		logDebug("FHS Mainhand damage=" + damage2);
        assert display2 != null;
		logDebug("FHS Mainhand display=" + display2.toString());
        assert SkullOwner2 != null;
		logDebug("FHS Mainhand SkullOwner=" + SkullOwner2.toString());

		if( display.equals(display2) && SkullOwner.equals(SkullOwner2) && (damage != damage2)){
			ItemStack is = new ItemStack(offHand);
			is.setAmount(mainHand.getAmount());
			logDebug("FHS d=d2, so=so2, d!=D2 - return offhand");
			return is;
		}else if( !display.equals(display2) && SkullOwner.equals(SkullOwner2) && ((damage == damage2)||(damage != damage2))){
			ItemStack is = new ItemStack(offHand);
			is.setAmount(mainHand.getAmount());
			logDebug("FHS d!=d2, so=so2, d ignored - return offhand");
			return is;
		}else if( display.equals(display2) && SkullOwner.equals(SkullOwner2) && (damage == damage2)){
			logDebug("FHS d=d2, so=so2, d=d2 - return mainhand");
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

        assert lore != null;
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
		//**	Check for config */
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

            assert oldMessagesSection != null;
            for (String messageKey : oldMessagesSection.getKeys(false)) {
				String messageValue = oldMessagesSection.getString(messageKey);
                assert messageValue != null;
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
			beheadingMessages = new YmlConfiguration(this);
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

			//** Trader heads load */

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

	public boolean isBabyEntity(LivingEntity entity) {
		return (entity instanceof Ageable) && !((Ageable) entity).isAdult();
	}

	public void checkLang() {
		Version curLangVersion = new Version(fileVersions.getString("lang", "0.0.1"));
		langNameFile = new File(getDataFolder() + "" + File.separatorChar + "lang" + File.separatorChar, daLang + "_mobnames.yml");
		if(curLangVersion.compareTo(minLangVersion) < 0) {
			LOGGER.log(daLang + "_mobnames.yml is outdated backing up...");
			try {
				copyFile(langNameFile.getAbsolutePath(),getDataFolder() + "" + File.separatorChar + "backup" + File.separatorChar + daLang + "_mobnames.yml");
			} catch (Exception exception) {
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_COPY_FILE).error(exception));
			}
			LOGGER.log("Saving new " + daLang + "_mobnames.yml...");
			saveResource("lang" + File.separatorChar + daLang + "_mobnames.yml", true);
			LOGGER.log("Copying values from backup" + File.separatorChar + daLang + "_mobnames.yml...");
			copyConfigValues(getDataFolder() + "" + File.separatorChar + "lang" + File.separatorChar + daLang + "_mobnames.yml",
					getDataFolder() + "" + File.separatorChar + "backup" + File.separatorChar + daLang + "_mobnames.yml");
		}
		LOGGER.log("Loading language based mobname file...");
		langName = new YamlConfiguration();
		try {
			langName.load(langNameFile);
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_MOBNAMES_LOAD_ERROR).error(exception));
		}
	}

	// Used to check the Minecraft version
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
		return jar_file_name;
	}

	public boolean getDebug() {
		return CoreUtils.debug;
	}


	public HeadManager getHeadManager() {
		return headManager;
	}

	public Map<String, MobHead> getLoadedMobHeads() {
		return new HashMap<>(headManager.loadedMobHeads);
	}

	public Map<String, PlayerHead> getLoadedPlayerHeads() {
		return new HashMap<>(headManager.loadedPlayerHeads);
	}

	public Map<String, BlockHead> getLoadedBlockHeads() {
		return new HashMap<>(headManager.loadedBlockHeads);
	}

	public Map<String, MiniBlock> getLoadedMiniBlocks() {
		return new HashMap<>(headManager.loadedMiniBlocks);
	}

	/**
	 * Fires a MobHeadDropEvent and adds the head to the drops if not cancelled.
	 *
	 * @param entity        The entity that dropped the head (e.g., creeper).
	 * @param player        The player who caused the drop, or null if none.
	 * @param head          The ItemStack representing the mob head.
	 * @param displayName   The display name of the head.
	 * @param texture       The texture of the head.
	 * @param uuid          The UUID associated with the head, or null.
	 * @param lore          The lore of the head, or null.
	 * @param noteblockSound The note block sound of the head, or null.
	 * @return true if the event was cancelled, false otherwise.
	 */
	public boolean callDropEvent(@Nonnull Entity entity, @Nullable Player player, @Nonnull ItemStack head,
								 @Nullable String displayName, @Nullable String texture, @Nullable String uuid,
								 @Nullable List<String> lore, @Nullable String noteblockSound) {
		try {
			MobHeadDropEvent dropEvent = new MobHeadDropEvent(entity, player, head, displayName, texture, uuid, lore, noteblockSound);
			getServer().getPluginManager().callEvent(dropEvent);
			return dropEvent.isCancelled();
		} catch (Exception exception) {
			MoreMobHeads.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.HEAD_DROP_ERROR).error(exception));
			return true; // Consider cancelled on error to prevent invalid drops
		}
	}

	/**
	 * Fires a MobHeadDropEvent and adds the head to the drops if not cancelled, then returns the drops.
	 *
	 * @param entity        The entity that dropped the head (e.g., creeper).
	 * @param player        The player who caused the drop, or null if none.
	 * @param head          The ItemStack representing the mob head.
	 * @param displayName   The display name of the head, or null for vanilla heads.
	 * @param texture       The texture of the head, or null for vanilla heads.
	 * @param uuid          The UUID associated with the head, or null.
	 * @param lore          The lore of the head, or null.
	 * @param noteblockSound The note block sound of the head, or null.
	 * @param drops         The list of ItemStacks to add the head to if not cancelled.
	 * @return The updated drops list, with the head added if not cancelled, or unchanged if cancelled.
	 */
	public List<ItemStack> addHeadToDrops(@Nonnull Entity entity, @Nullable Player player, @Nonnull ItemStack head,
										  @Nullable String displayName, @Nullable String texture, @Nullable String uuid,
										  @Nullable List<String> lore, @Nullable String noteblockSound,
										  @Nonnull List<ItemStack> drops) {
		try {
			MobHeadDropEvent dropEvent = new MobHeadDropEvent(entity, player, head, displayName, texture, uuid, lore, noteblockSound);
			getServer().getPluginManager().callEvent(dropEvent);
			if (!dropEvent.isCancelled()) {
				drops.add(dropEvent.getHead());
			}
			return drops;
		} catch (Exception exception) {
			MoreMobHeads.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.HEAD_DROP_ERROR).error(exception));
			return drops; // Return unchanged drops on error
		}
	}

	/**
	 * Logs all details when a MobHeadDropEvent is fired.
	 *
	 * @param event The MobHeadDropEvent.
	 */
	@EventHandler
	public void onMobHeadDrop(MobHeadDropEvent event) {
		String texture = event.getTexture() != null ? event.getTexture().substring(0, Math.min(event.getTexture().length(), 20)) + "..." : "None";
		String playerName = event.getPlayer() != null ? event.getPlayer().getName() : "None";
		String displayName = event.getDisplayName() != null ? event.getDisplayName() : "None";
		String uuid = event.getUuid() != null ? event.getUuid() : "None";
		String lore = event.getLore() != null ? event.getLore().toString() : "None";
		String noteblockSound = event.getNoteblockSound() != null ? event.getNoteblockSound() : "None";
		String url = event.getSkinURL();
		LOGGER.log("MobHeadDropEvent fired: " +
				"Entity=" + event.getEntity().getType() +
				", Player=" + playerName +
				", Head=" + event.getHead().getType() +
				", DisplayName=" + displayName +
				", Texture=" + texture +
				", UUID=" + uuid +
				", Lore=" + lore +
				"§r, NoteblockSound=" + noteblockSound +
				", Cancelled=" + event.isCancelled() +
				", URL=" + url
		);
		//event.setCancelled(true);
	}


	public void logDebug(String message){
		LOGGER.debug(message, CoreUtils.debug);
	}
	public void logDebug(String message, Object... args) {
		LOGGER.debug(message, args);
	}
}