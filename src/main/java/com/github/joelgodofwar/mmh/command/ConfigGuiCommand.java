package com.github.joelgodofwar.mmh.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.github.joelgodofwar.coreutils.util.YmlConfiguration;
import lib.github.joelgodofwar.coreutils.util.common.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.joelgodofwar.mmh.MoreMobHeads;

import com.github.joelgodofwar.mmh.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.mmh.common.error.Report;
import com.github.joelgodofwar.mmh.common.error.ReportType;
import com.github.joelgodofwar.mmh.util.gui.Language;
import com.github.joelgodofwar.mmh.util.heads.HeadUtils;
import com.github.joelgodofwar.mmh.util.heads.InventoryGUI;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ConfigGuiCommand {
	private final MoreMobHeads mmh;
	private final YmlConfiguration config;
	private final File configFile;
	private static final NamespacedKey MENU_KEY = new NamespacedKey(MoreMobHeads.getInstance(), "menu_action");
	@SuppressWarnings("unused") private static final String DLC_CUTOFF_VERSION = "1.1.0"; // Version when DLC becomes required
	private PluginLogger LOGGER;
	private static DetailedErrorReporter reporter;
	public static final ReportType COMMAND_CONFIG_EXECUTE = new ReportType("Error executing Config Command.");
	public static final ReportType COMMAND_CONFIG_CONFIGMAIN = new ReportType("Error processing configMain.");
	public static final ReportType COMMAND_CONFIG_CONFIGPSETTINGS = new ReportType("Error processing configPSettings.");
	public static final ReportType COMMAND_CONFIG_CONFIGGLOBALSETTINGS = new ReportType("Error processing configGlobalSettings.");
	public static final ReportType COMMAND_CONFIG_CONFIGHEADSETTINGS = new ReportType("Error processing configHeadSettings.");
	public static final ReportType COMMAND_CONFIG_CONFIGWANDERINGTRADES = new ReportType("Error processing configWanderingTrades.");
	public static final ReportType COMMAND_CONFIG_CONFIGLABGUAGE = new ReportType("Error processing configLanguage.");


	@SuppressWarnings("static-access")
	public ConfigGuiCommand(MoreMobHeads plugin) {
		this.mmh = plugin;
		this.configFile = new File(plugin.getDataFolder(), "config.yml");
		this.config = mmh.config;
		this.LOGGER = mmh.LOGGER;
		this.reporter = new DetailedErrorReporter(mmh);
	}

	public void execute(Player player) {
		// Check kill switch conditions
		if (!isGuiAccessible()) {
			player.sendMessage(ChatColor.RED + "This feature requires the MoreMobHeads DLC. Dev-build access has expired.");
			return;
		}
		try {
			configMain(player);
		}catch(Exception exception) { // COMMAND_CONFIG_CONFIGWANDERINGTRADES	COMMAND_CONFIG_CONFIGLABGUAGE
			reporter.reportDetailed(this, Report.newBuilder(COMMAND_CONFIG_EXECUTE).error(exception));
		}
	}

	private boolean isGuiAccessible() {
		// Check if DLC is installed
		/** if (mmh.isDlcInstalled()) {
			return true;
		}
		// Check dev_mode and version
		String currentVersion = mmh.getVersion();
		boolean isDevMode = config.getBoolean("dev_mode", false); // Default to false in release
		return isDevMode && (compareVersions(currentVersion, DLC_CUTOFF_VERSION) < 0);//*/
		return true;
	}

	@SuppressWarnings("unused") private int compareVersions(String v1, String v2) {
		String[] parts1 = v1.split("\\.");
		String[] parts2 = v2.split("\\.");
		for (int i = 0; i < Math.min(parts1.length, parts2.length); i++) {
			int n1 = Integer.parseInt(parts1[i].replaceAll("[^0-9]", ""));
			int n2 = Integer.parseInt(parts2[i].replaceAll("[^0-9]", ""));
			if (n1 != n2) {
				return n1 - n2;
			}
		}
		return parts1.length - parts2.length;
	}

	public void configMain(Player player) {
		try {
			mmh.logDebug(String.format("Opening main config menu for player: %s", player.getName()));
			Map<ItemStack, Runnable> choices = new HashMap<>();
			Map<ItemStack, Integer> slotAssignments = new HashMap<>();

			// Plugin Settings
			ItemStack pluginSettings = new ItemStack(Material.BOOK);
			ItemMeta psMeta = pluginSettings.getItemMeta();
			if (psMeta != null) {
				psMeta.setDisplayName(ChatColor.YELLOW + "Plugin Settings");
				psMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to view plugin settings"));
				psMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "plugin_settings");
				pluginSettings.setItemMeta(psMeta);
			}
			choices.put(pluginSettings, () -> {
				mmh.logDebug(String.format("Plugin Settings clicked by player: %s", player.getName()));
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configPSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(pluginSettings, 0);

			// Global Settings
			ItemStack globalSettings = new ItemStack(Material.GLOBE_BANNER_PATTERN);
			ItemMeta gsMeta = globalSettings.getItemMeta();
			if (gsMeta != null) {
				gsMeta.setDisplayName(ChatColor.YELLOW + "Global Settings");
				gsMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to view global settings"));
				gsMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "global_settings");
				globalSettings.setItemMeta(gsMeta);
			}
			choices.put(globalSettings, () -> {
				mmh.logDebug(String.format("Global Settings clicked by player: %s", player.getName()));
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configGlobalSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(globalSettings, 1);

			// Head Settings
			ItemStack headSettings = new ItemStack(Material.PLAYER_HEAD);
			ItemMeta hsMeta = headSettings.getItemMeta();
			if (hsMeta != null) {
				hsMeta.setDisplayName(ChatColor.YELLOW + "Head Settings");
				hsMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to view head settings"));
				hsMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "head_settings");
				headSettings.setItemMeta(hsMeta);
			}
			choices.put(headSettings, () -> {
				mmh.logDebug(String.format("Head Settings clicked by player: %s", player.getName()));
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(headSettings, 2);

			// Wandering Trades Settings
			ItemStack wanderingTrades = new ItemStack(Material.EMERALD);
			ItemMeta wtMeta = wanderingTrades.getItemMeta();
			if (wtMeta != null) {
				wtMeta.setDisplayName(ChatColor.YELLOW + "Wandering Trades Settings");
				wtMeta.setLore(Arrays.asList(ChatColor.GRAY + "Click to configure wandering trader trades"));
				wtMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "wandering_trades");
				wanderingTrades.setItemMeta(wtMeta);
			}
			choices.put(wanderingTrades, () -> {
				mmh.logDebug(String.format("Wandering Trades clicked by player: %s", player.getName()));
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(wanderingTrades, 3);

			InventoryGUI gui = new InventoryGUI("Config Settings", choices);
			gui.setForcePreviousButton(false);
			gui.openWithSlots(player, slotAssignments, null);
			mmh.logDebug(String.format("Main config menu opened for player: %s, items: %d", player.getName(), choices.size()));
		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(COMMAND_CONFIG_CONFIGMAIN).error(exception));
		}
	}

	private void configPSettings(Player player) {
		try {
			mmh.logDebug(String.format("Opening plugin settings menu for player: %s", player.getName()));
			if (!isGuiAccessible()) {
				player.sendMessage(ChatColor.RED + "This feature requires the MoreMobHeads DLC. Dev-build access has expired.");
				player.closeInventory();
				return;
			}
			Map<ItemStack, Runnable> choices = new HashMap<>();
			Map<ItemStack, Integer> slotAssignments = new HashMap<>();

			// Auto Update Check
			ItemStack autoUpdate = new ItemStack(config.getBoolean("plugin_settings.auto_update_check") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta auMeta = autoUpdate.getItemMeta();
			if (auMeta != null) {
				auMeta.setDisplayName(ChatColor.YELLOW + "Auto Update Check");
				auMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("plugin_settings.auto_update_check") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Change to false to stop auto-update-check"));
				auMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "auto_update_check");
				autoUpdate.setItemMeta(auMeta);
			}
			choices.put(autoUpdate, () -> {
				boolean current = config.getBoolean("plugin_settings.auto_update_check");
				config.set("plugin_settings.auto_update_check", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled auto_update_check to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Auto Update Check set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save config for auto_update_check: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configPSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(autoUpdate, 0);

			// Debug Mode
			ItemStack debugMode = new ItemStack(config.getBoolean("plugin_settings.debug") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta dmMeta = debugMode.getItemMeta();
			if (dmMeta != null) {
				dmMeta.setDisplayName(ChatColor.YELLOW + "Debug Mode");
				dmMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("plugin_settings.debug") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Set to true before sending a log about an issue.",
						ChatColor.GRAY + "logs trace data required to pinpoint where errors are."));
				dmMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "debug");
				debugMode.setItemMeta(dmMeta);
			}
			choices.put(debugMode, () -> {
				boolean current = config.getBoolean("plugin_settings.debug");
				config.set("plugin_settings.debug", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled debug to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Debug Mode set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save config for debug: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configPSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(debugMode, 1);

			// Language
			ItemStack language = new ItemStack(Material.PLAYER_HEAD); // Using skull for skinned heads
			SkullMeta langMeta = (SkullMeta) language.getItemMeta();
			if (langMeta != null) {
				String langCode = config.getString("plugin_settings.lang", "en_US");
				Language lang = Language.getByLangCode(langCode);
				String langName = lang != null ? lang.getLangNameInEnglish() : langCode;
				langMeta.setDisplayName(ChatColor.YELLOW + langName);
				langMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + ChatColor.WHITE + langName,
						ChatColor.WHITE + "Click to select language",
						ChatColor.GRAY + "čeština (cs_CZ), Deutsch (de_DE), English (en_US),",
						ChatColor.GRAY + "Español (es_ES), Español (es_MX), Français (fr_FR),",
						ChatColor.GRAY + "Italiano (it_IT), Magyar (hu_HU), 日本語 (ja_JP), 한국어 (ko_KR),",
						ChatColor.GRAY + "Lolcat (lol_US), Melayu (my_MY), Nederlands (nl_NL),",
						ChatColor.GRAY + "Polski (pl_PL), Português (pt_BR), Русский (ru_RU),",
						ChatColor.GRAY + "Svenska (sv_SV), Türkçe (tr_TR), 中文(简体) (zh_CN),",
						ChatColor.GRAY + "中文(繁體) (zh_TW)"));
				// Set a representative skin for the current language
				if (lang != null) {
					String texture = lang.getTexture();
					if (texture != null) {
						language = HeadUtils.makeHead(ChatColor.YELLOW + langName, texture, lang.getUuid(), new ArrayList<>(Arrays.asList(
								ChatColor.WHITE + "Current: " + ChatColor.WHITE + langName,
								ChatColor.WHITE + "Click to select language")), null);
					}
				}
				langMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "language");
				language.setItemMeta(langMeta);
			}
			choices.put(language, () -> {
				mmh.logDebug(String.format("Language clicked by player: %s", player.getName()));
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configLanguage(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(language, 2);

			// Previous Menu
			ItemStack prevButton = new ItemStack(Material.ARROW);
			ItemMeta prevMeta = prevButton.getItemMeta();
			if (prevMeta != null) {
				prevMeta.setDisplayName(ChatColor.YELLOW + "Previous Menu");
				prevMeta.setLore(Arrays.asList(ChatColor.GRAY + "Return to Config Settings"));
				prevMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "previous_menu");
				prevButton.setItemMeta(prevMeta);
			}
			choices.put(prevButton, () -> {
				mmh.logDebug(String.format("Previous Menu clicked by player: %s", player.getName()));
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configMain(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(prevButton, 45);

			InventoryGUI gui = new InventoryGUI("Plugin Settings", choices);
			gui.setForcePreviousButton(false);
			gui.openWithSlots(player, slotAssignments, null);
			mmh.logDebug(String.format("Plugin settings menu opened for player: %s, items: %d", player.getName(), choices.size()));
		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(COMMAND_CONFIG_CONFIGPSETTINGS).error(exception));
		}
	}

	@SuppressWarnings("deprecation")
	private void configGlobalSettings(Player player) {
		try {
			mmh.logDebug(String.format("Opening global settings menu for player: %s", player.getName()));
			if (!isGuiAccessible()) {
				player.sendMessage(ChatColor.RED + "This feature requires the MoreMobHeads DLC. Dev-build access has expired.");
				player.closeInventory();
				return;
			}
			Map<ItemStack, Runnable> choices = new HashMap<>();
			Map<ItemStack, Integer> slotAssignments = new HashMap<>();

			// Colorful Console
			ItemStack colorfulConsole = new ItemStack(config.getBoolean("global_settings.console.colorful_console") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta ccMeta = colorfulConsole.getItemMeta();
			if (ccMeta != null) {
				ccMeta.setDisplayName(ChatColor.YELLOW + "Colorful Console");
				ccMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("global_settings.console.colorful_console") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Enables fancy ANSI colors in console.",
						ChatColor.GRAY + "(Disable if you're getting weird characters in the console)"));
				ccMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "colorful_console");
				colorfulConsole.setItemMeta(ccMeta);
			}
			choices.put(colorfulConsole, () -> {
				boolean current = config.getBoolean("global_settings.console.colorful_console");
				config.set("global_settings.console.colorful_console", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled colorful_console to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Colorful Console set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save colorful_console: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configGlobalSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(colorfulConsole, 0);

			// Silent Console
			ItemStack silentConsole = new ItemStack(config.getBoolean("global_settings.console.silent_console") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta scMeta = silentConsole.getItemMeta();
			if (scMeta != null) {
				scMeta.setDisplayName(ChatColor.YELLOW + "Silent Console");
				scMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("global_settings.console.silent_console") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Enables silent start mode."));
				scMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "silent_console");
				silentConsole.setItemMeta(scMeta);
			}
			choices.put(silentConsole, () -> {
				boolean current = config.getBoolean("global_settings.console.silent_console");
				config.set("global_settings.console.silent_console", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled silent_console to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Silent Console set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save silent_console: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configGlobalSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(silentConsole, 1);

			// Long Plugin Name
			ItemStack longPluginName = new ItemStack(config.getBoolean("global_settings.console.longpluginname") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta lpnMeta = longPluginName.getItemMeta();
			if (lpnMeta != null) {
				lpnMeta.setDisplayName(ChatColor.YELLOW + "Long Plugin Name");
				lpnMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("global_settings.console.longpluginname") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Should MoreMobHeads use it's",
						ChatColor.GRAY + "full name or MMH in console messages?"));
				lpnMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "longpluginname");
				longPluginName.setItemMeta(lpnMeta);
			}
			choices.put(longPluginName, () -> {
				boolean current = config.getBoolean("global_settings.console.longpluginname");
				config.set("global_settings.console.longpluginname", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled longpluginname to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Long Plugin Name set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save longpluginname: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configGlobalSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(longPluginName, 2);

			// Whitelist
			ItemStack whitelist = new ItemStack(Material.MAP);
			ItemMeta wlMeta = whitelist.getItemMeta();
			if (wlMeta != null) {
				List<String> wlList = config.getStringList("global_settings.world.whitelist");
				String wlDisplay = wlList.isEmpty() || wlList.get(0).equals("names_go_here") ? "names_go_here" : String.join(", ", wlList);
				wlMeta.setDisplayName(ChatColor.YELLOW + "Whitelist");
				wlMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + wlDisplay,
						ChatColor.WHITE + "Click for: /mmh config world.whitelist add [world]",
						ChatColor.GRAY + "# list of worlds MMH will work in.",
						ChatColor.GRAY + "Blanks means all except for blacklisted worlds Example",
						ChatColor.GRAY + "(world, world_nether, world_the_end) will drop heads"));
				wlMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "whitelist");
				whitelist.setItemMeta(wlMeta);
			}
			choices.put(whitelist, () -> {
				mmh.logDebug(String.format("Whitelist clicked by player: %s", player.getName()));
				TextComponent message = new TextComponent("Click for 'world.whitelist add/remove' command suggestion.");
				message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mmh config world.whitelist add/remove [world(s)]"));
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to have the command placed in the chat window.").create()));
				player.spigot().sendMessage(message);
				//player.sendMessage(ChatColor.YELLOW + "Use: /mmh config world.whitelist add [world]");
				//player.closeInventory();
			});
			slotAssignments.put(whitelist, 3);

			// Blacklist
			ItemStack blacklist = new ItemStack(Material.FILLED_MAP);
			ItemMeta blMeta = blacklist.getItemMeta();
			if (blMeta != null) {
				List<String> blList = config.getStringList("global_settings.world.blacklist");
				String blDisplay = blList.isEmpty() || blList.get(0).equals("names_go_here") ? "names_go_here" : String.join(", ", blList);
				blMeta.setDisplayName(ChatColor.YELLOW + "Blacklist");
				blMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + blDisplay,
						ChatColor.WHITE + "Click for: /mmh config world.blacklist add [world]",
						ChatColor.GRAY + "List of worlds MMH will NOT work in.",
						ChatColor.GRAY + "Example (world_dungeon) will NOT drop heads"));
				blMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "blacklist");
				blacklist.setItemMeta(blMeta);
			}
			choices.put(blacklist, () -> {
				mmh.logDebug(String.format("Blacklist clicked by player: %s", player.getName()));
				TextComponent message = new TextComponent("Click for 'world.blacklist add/remove' command suggestion.");
				message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mmh config world.blacklist add/remove [world(s)]"));
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to have the command placed in the chat window.").create()));
				player.spigot().sendMessage(message);
				//player.sendMessage(ChatColor.YELLOW + "Use: /mmh config world.blacklist add [world]");
				//player.closeInventory();
			});
			slotAssignments.put(blacklist, 4);

			// Piston Extend
			ItemStack pistonExtend = new ItemStack(config.getBoolean("global_settings.event.piston_extend") ? Material.PISTON : Material.STICKY_PISTON);
			ItemMeta peMeta = pistonExtend.getItemMeta();
			if (peMeta != null) {
				peMeta.setDisplayName(ChatColor.YELLOW + "Piston Extend");
				peMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("global_settings.event.piston_extend") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Event settings. " + ChatColor.DARK_RED + "You should not set this to false."));
				peMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "piston_extend");
				pistonExtend.setItemMeta(peMeta);
			}
			choices.put(pistonExtend, () -> {
				boolean current = config.getBoolean("global_settings.event.piston_extend");
				config.set("global_settings.event.piston_extend", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled piston_extend to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Piston Extend set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save piston_extend: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configGlobalSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(pistonExtend, 5);

			// Disable DLC Nag for Players
			ItemStack disableDlcNagPlayers = new ItemStack(config.getBoolean("global_settings.disable_dlc_nag_for_players") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta dnpMeta = disableDlcNagPlayers.getItemMeta();
			if (dnpMeta != null) {
				dnpMeta.setDisplayName(ChatColor.YELLOW + "Disable DLC Nag for Players");
				dnpMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("global_settings.disable_dlc_nag_for_players") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Disable DLC nag messages for non-OP players."));
				dnpMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "disable_dlc_nag_for_players");
				disableDlcNagPlayers.setItemMeta(dnpMeta);
			}
			choices.put(disableDlcNagPlayers, () -> {
				boolean current = config.getBoolean("global_settings.disable_dlc_nag_for_players");
				config.set("global_settings.disable_dlc_nag_for_players", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled disable_dlc_nag_for_players to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Disable DLC Nag for Players set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save disable_dlc_nag_for_players: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configGlobalSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(disableDlcNagPlayers, 6);

			// Disable DLC Nag for OPs
			ItemStack disableDlcNagOps = new ItemStack(config.getBoolean("global_settings.disable_dlc_nag_for_ops") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta dnoMeta = disableDlcNagOps.getItemMeta();
			if (dnoMeta != null) {
				dnoMeta.setDisplayName(ChatColor.YELLOW + "Disable DLC Nag for OPs");
				dnoMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("global_settings.disable_dlc_nag_for_ops") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Disable DLC nag messages for OPs."));
				dnoMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "disable_dlc_nag_for_ops");
				disableDlcNagOps.setItemMeta(dnoMeta);
			}
			choices.put(disableDlcNagOps, () -> {
				boolean current = config.getBoolean("global_settings.disable_dlc_nag_for_ops");
				config.set("global_settings.disable_dlc_nag_for_ops", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled disable_dlc_nag_for_ops to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Disable DLC Nag for OPs set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save disable_dlc_nag_for_ops: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configGlobalSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(disableDlcNagOps, 7);

			// Disable DLC Display
			ItemStack disableDlcDisplay = new ItemStack(config.getBoolean("global_settings.disable_dlc_display") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta dddMeta = disableDlcDisplay.getItemMeta();
			if (dddMeta != null) {
				dddMeta.setDisplayName(ChatColor.YELLOW + "Disable DLC Display");
				dddMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("global_settings.disable_dlc_display") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Disable DLC display."));
				dddMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "disable_dlc_display");
				disableDlcDisplay.setItemMeta(dddMeta);
			}
			choices.put(disableDlcDisplay, () -> {
				boolean current = config.getBoolean("global_settings.disable_dlc_display");
				config.set("global_settings.disable_dlc_display", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled disable_dlc_display to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Disable DLC Display set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save disable_dlc_display: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configGlobalSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(disableDlcDisplay, 8);

			// Previous Menu
			ItemStack prevButton = new ItemStack(Material.ARROW);
			ItemMeta prevMeta = prevButton.getItemMeta();
			if (prevMeta != null) {
				prevMeta.setDisplayName(ChatColor.YELLOW + "Previous Menu");
				prevMeta.setLore(Arrays.asList(ChatColor.GRAY + "Return to Config Settings"));
				prevMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "previous_menu");
				prevButton.setItemMeta(prevMeta);
			}
			choices.put(prevButton, () -> {
				mmh.logDebug(String.format("Previous Menu clicked by player: %s", player.getName()));
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configMain(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(prevButton, 45);

			InventoryGUI gui = new InventoryGUI("Global Settings", choices);
			gui.setForcePreviousButton(false);
			gui.openWithSlots(player, slotAssignments, null);
			mmh.logDebug(String.format("Global settings menu opened for player: %s, items: %d", player.getName(), choices.size()));
		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(COMMAND_CONFIG_CONFIGGLOBALSETTINGS).error(exception));
		}

	}

	@SuppressWarnings("deprecation") private void configHeadSettings(Player player) {
		try {
			mmh.logDebug(String.format("Opening head settings menu for player: %s", player.getName()));
			if (!isGuiAccessible()) {
				player.sendMessage(ChatColor.RED + "This feature requires the MoreMobHeads DLC. Dev-build access has expired.");
				player.closeInventory();
				return;
			}
			Map<ItemStack, Runnable> choices = new HashMap<>();
			Map<ItemStack, Integer> slotAssignments = new HashMap<>();

			// Apply Looting
			ItemStack applyLooting = new ItemStack(config.getBoolean("head_settings.apply_looting") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta alMeta = applyLooting.getItemMeta();
			if (alMeta != null) {
				alMeta.setDisplayName(ChatColor.YELLOW + "Apply Looting");
				alMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.apply_looting") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Set if looting is applied or not."));
				alMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "apply_looting");
				applyLooting.setItemMeta(alMeta);
			}
			choices.put(applyLooting, () -> {
				boolean current = config.getBoolean("head_settings.apply_looting");
				config.set("head_settings.apply_looting", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled apply_looting to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Apply Looting set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save apply_looting: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(applyLooting, 0);

			// Show Killer in Lore
			ItemStack showKiller = new ItemStack(config.getBoolean("head_settings.lore.show_killer") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta skMeta = showKiller.getItemMeta();
			if (skMeta != null) {
				skMeta.setDisplayName(ChatColor.YELLOW + "Show Killer in Lore");
				skMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.lore.show_killer") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Set whether Killed by is added to lore."));
				skMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "show_killer");
				showKiller.setItemMeta(skMeta);
			}
			choices.put(showKiller, () -> {
				boolean current = config.getBoolean("head_settings.lore.show_killer");
				config.set("head_settings.lore.show_killer", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled show_killer to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Show Killer in Lore set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save show_killer: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(showKiller, 1);

			// Show Plugin Name in Lore
			ItemStack showPluginName = new ItemStack(config.getBoolean("head_settings.lore.show_plugin_name") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta spnMeta = showPluginName.getItemMeta();
			if (spnMeta != null) {
				spnMeta.setDisplayName(ChatColor.YELLOW + "Show Plugin Name in Lore");
				spnMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.lore.show_plugin_name") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Set whether MoreMobHeads is added to lore."));
				spnMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "show_plugin_name");
				showPluginName.setItemMeta(spnMeta);
			}
			choices.put(showPluginName, () -> {
				boolean current = config.getBoolean("head_settings.lore.show_plugin_name");
				config.set("head_settings.lore.show_plugin_name", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled show_plugin_name to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Show Plugin Name in Lore set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save show_plugin_name: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(showPluginName, 2);

			// Stonecutter Mini Blocks
			ItemStack stonecutter = new ItemStack(config.getBoolean("head_settings.mini_blocks.stonecutter") ? Material.STONECUTTER : Material.STONE);
			ItemMeta sMeta = stonecutter.getItemMeta();
			if (sMeta != null) {
				sMeta.setDisplayName(ChatColor.YELLOW + "Stonecutter Mini Blocks");
				sMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.mini_blocks.stonecutter") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Should MMH allow stonecutting Mini_Blocks(Block Heads)?"));
				sMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "stonecutter");
				stonecutter.setItemMeta(sMeta);
			}
			choices.put(stonecutter, () -> {
				boolean current = config.getBoolean("head_settings.mini_blocks.stonecutter");
				config.set("head_settings.mini_blocks.stonecutter", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled stonecutter to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Stonecutter Mini Blocks set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save stonecutter: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(stonecutter, 3);

			// Increase Mini Blocks Per Block
			ItemStack increasePerBlock = new ItemStack(Material.STONE);
			ItemMeta ipbMeta = increasePerBlock.getItemMeta();
			if (ipbMeta != null) {
				int current = config.getInt("head_settings.mini_blocks.perblock", 1);
				ipbMeta.setDisplayName(ChatColor.YELLOW + "Increase Mini Blocks Per Block");
				ipbMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + current,
						ChatColor.WHITE + "Click to increase (max 10)",
						ChatColor.GRAY + "Increases the number of mini blocks per full block."));
				ipbMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "increase_perblock");
				increasePerBlock.setItemMeta(ipbMeta);
			}
			choices.put(increasePerBlock, () -> {
				int current = config.getInt("head_settings.mini_blocks.perblock", 1);
				if (current < 10) {
					config.set("head_settings.mini_blocks.perblock", current + 1);
					try {
						YmlConfiguration.saveConfig(configFile, config);
						mmh.logDebug(String.format("Increased mini_blocks.perblock to %d for player: %s", current + 1, player.getName()));
						player.sendMessage(ChatColor.GREEN + "Mini Blocks Per Block set to " + (current + 1));
					} catch (Exception e) {
						mmh.logDebug(String.format("Failed to save mini_blocks.perblock: %s", e.getMessage()));
						player.sendMessage(ChatColor.RED + "Failed to save configuration!");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "Mini Blocks Per Block is already at maximum (10)!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(increasePerBlock, 4);

			// Decrease Mini Blocks Per Block
			ItemStack decreasePerBlock = new ItemStack(Material.STONE_SLAB);
			ItemMeta dpbMeta = decreasePerBlock.getItemMeta();
			if (dpbMeta != null) {
				int current = config.getInt("head_settings.mini_blocks.perblock", 1);
				dpbMeta.setDisplayName(ChatColor.YELLOW + "Decrease Mini Blocks Per Block");
				dpbMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + current,
						ChatColor.WHITE + "Click to decrease (min 1)",
						ChatColor.GRAY + "Decreases the number of mini blocks per full block."));
				dpbMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "decrease_perblock");
				decreasePerBlock.setItemMeta(dpbMeta);
			}
			choices.put(decreasePerBlock, () -> {
				int current = config.getInt("head_settings.mini_blocks.perblock", 1);
				if (current > 1) {
					config.set("head_settings.mini_blocks.perblock", current - 1);
					try {
						YmlConfiguration.saveConfig(configFile, config);
						mmh.logDebug(String.format("Decreased mini_blocks.perblock to %d for player: %s", current - 1, player.getName()));
						player.sendMessage(ChatColor.GREEN + "Mini Blocks Per Block set to " + (current - 1));
					} catch (Exception e) {
						mmh.logDebug(String.format("Failed to save mini_blocks.perblock: %s", e.getMessage()));
						player.sendMessage(ChatColor.RED + "Failed to save configuration!");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "Mini Blocks Per Block is already at minimum (1)!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(decreasePerBlock, 5);

			// Announce Player Kill Enabled
			ItemStack announcePlayerKill = new ItemStack(config.getBoolean("head_settings.player_heads.announce_kill.enabled") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta apkMeta = announcePlayerKill.getItemMeta();
			if (apkMeta != null) {
				apkMeta.setDisplayName(ChatColor.YELLOW + "Announce Player Kill");
				apkMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.player_heads.announce_kill.enabled") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Should MMH announce player beheadings?"));
				apkMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "announce_player_kill");
				announcePlayerKill.setItemMeta(apkMeta);
			}
			choices.put(announcePlayerKill, () -> {
				boolean current = config.getBoolean("head_settings.player_heads.announce_kill.enabled");
				config.set("head_settings.player_heads.announce_kill.enabled", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled announce_player_kill to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Announce Player Kill set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save announce_player_kill: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(announcePlayerKill, 6);

			// Announce Player Kill Display Name
			ItemStack announcePlayerKillDisplay = new ItemStack(config.getBoolean("head_settings.player_heads.announce_kill.displayname") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta apkdMeta = announcePlayerKillDisplay.getItemMeta();
			if (apkdMeta != null) {
				apkdMeta.setDisplayName(ChatColor.YELLOW + "Announce Player Kill Display Name");
				apkdMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.player_heads.announce_kill.displayname") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Should MMH use Display Names? Used by Killed by message too."));
				apkdMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "announce_player_kill_displayname");
				announcePlayerKillDisplay.setItemMeta(apkdMeta);
			}
			choices.put(announcePlayerKillDisplay, () -> {
				boolean current = config.getBoolean("head_settings.player_heads.announce_kill.displayname");
				config.set("head_settings.player_heads.announce_kill.displayname", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled announce_player_kill_displayname to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Announce Player Kill Display Name set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save announce_player_kill_displayname: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(announcePlayerKillDisplay, 7);

			// Player Head Whitelist Enforce
			ItemStack whitelistEnforce = new ItemStack(config.getBoolean("head_settings.player_heads.whitelist.enforce") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta weMeta = whitelistEnforce.getItemMeta();
			if (weMeta != null) {
				weMeta.setDisplayName(ChatColor.YELLOW + "Player Head Whitelist Enforce");
				weMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.player_heads.whitelist.enforce") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "False will not use whitelist, true will use whitelist"));
				weMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "whitelist_enforce");
				whitelistEnforce.setItemMeta(weMeta);
			}
			choices.put(whitelistEnforce, () -> {
				boolean current = config.getBoolean("head_settings.player_heads.whitelist.enforce");
				config.set("head_settings.player_heads.whitelist.enforce", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled whitelist_enforce to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Player Head Whitelist Enforce set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save whitelist_enforce: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(whitelistEnforce, 8);

			// Player Head Whitelist
			ItemStack playerHeadWhitelist = new ItemStack(Material.MAP);
			ItemMeta pwlMeta = playerHeadWhitelist.getItemMeta();
			if (pwlMeta != null) {
				List<String> wlList = config.getStringList("head_settings.player_heads.whitelist.player_head_whitelist");
				String wlDisplay = wlList.isEmpty() || wlList.get(0).equals("names_go_here") ? "names_go_here" : String.join(", ", wlList);
				pwlMeta.setDisplayName(ChatColor.YELLOW + "Player Head Whitelist");
				pwlMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + wlDisplay,
						ChatColor.WHITE + "Click for: /mmh config head_settings.player_heads.whitelist add [name]",
						ChatColor.GRAY + "Seperate player names with commas",
						ChatColor.GRAY + "\"Xisumavoid, falsesymmetry, etc.\""));
				pwlMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "player_head_whitelist");
				playerHeadWhitelist.setItemMeta(pwlMeta);
			}
			choices.put(playerHeadWhitelist, () -> {
				mmh.logDebug(String.format("Player Head Whitelist clicked by player: %s", player.getName()));
				TextComponent message = new TextComponent("Click for 'head_settings.player_heads.whitelist add/remove' command suggestion.");
				message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mmh config head_settings.player_heads.whitelist add/remove [name(s)]"));
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to have the command placed in the chat window.").create()));
				player.spigot().sendMessage(message);
				//player.sendMessage(ChatColor.YELLOW + "Use: /mmh config head_settings.player_heads.whitelist add [name]");
			});
			slotAssignments.put(playerHeadWhitelist, 9);

			// Player Head Blacklist Enforce
			ItemStack blacklistEnforce = new ItemStack(config.getBoolean("head_settings.player_heads.blacklist.enforce") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta beMeta = blacklistEnforce.getItemMeta();
			if (beMeta != null) {
				beMeta.setDisplayName(ChatColor.YELLOW + "Player Head Blacklist Enforce");
				beMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.player_heads.blacklist.enforce") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "False will not use blacklist, true will use blacklist"));
				beMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "blacklist_enforce");
				blacklistEnforce.setItemMeta(beMeta);
			}
			choices.put(blacklistEnforce, () -> {
				boolean current = config.getBoolean("head_settings.player_heads.blacklist.enforce");
				config.set("head_settings.player_heads.blacklist.enforce", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled blacklist_enforce to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Player Head Blacklist Enforce set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save blacklist_enforce: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(blacklistEnforce, 10);

			// Player Head Blacklist
			ItemStack playerHeadBlacklist = new ItemStack(Material.FILLED_MAP);
			ItemMeta pblMeta = playerHeadBlacklist.getItemMeta();
			if (pblMeta != null) {
				List<String> blList = config.getStringList("head_settings.player_heads.blacklist.player_head_blacklist");
				String blDisplay = blList.isEmpty() || blList.get(0).equals("names_go_here") ? "names_go_here" : String.join(", ", blList);
				pblMeta.setDisplayName(ChatColor.YELLOW + "Player Head Blacklist");
				pblMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + blDisplay,
						ChatColor.WHITE + "Click for: /mmh config head_settings.player_heads.blacklist add [name]",
						ChatColor.GRAY + "Seperate player names with commas",
						ChatColor.GRAY + "\"JoeHillsSays, TinFoilChef, etc.\""));
				pblMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "player_head_blacklist");
				playerHeadBlacklist.setItemMeta(pblMeta);
			}
			choices.put(playerHeadBlacklist, () -> {
				mmh.logDebug(String.format("Player Head Blacklist clicked by player: %s", player.getName()));
				TextComponent message = new TextComponent("Click for 'head_settings.player_heads.blacklist add/remove' command suggestion.");
				message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mmh config head_settings.player_heads.blacklist add/remove [name(s)]"));
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to have the command placed in the chat window.").create()));
				player.spigot().sendMessage(message);
				//player.sendMessage(ChatColor.YELLOW + "Use: /mmh config head_settings.player_heads.blacklist add [name]");
			});
			slotAssignments.put(playerHeadBlacklist, 11);

			// Use Default Player Heads
			ItemStack useDefaultPlayerHeads = new ItemStack(config.getBoolean("head_settings.player_heads.use_default_player_heads") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta udphMeta = useDefaultPlayerHeads.getItemMeta();
			if (udphMeta != null) {
				udphMeta.setDisplayName(ChatColor.YELLOW + "Use Default Player Heads");
				udphMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.player_heads.use_default_player_heads") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Whether to copy default player head JSON files from the plugin JAR"));
				udphMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "use_default_player_heads");
				useDefaultPlayerHeads.setItemMeta(udphMeta);
			}
			choices.put(useDefaultPlayerHeads, () -> {
				boolean current = config.getBoolean("head_settings.player_heads.use_default_player_heads");
				config.set("head_settings.player_heads.use_default_player_heads", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled use_default_player_heads to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Use Default Player Heads set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save use_default_player_heads: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(useDefaultPlayerHeads, 12);

			// Announce Mob Kill Enabled
			ItemStack announceMobKill = new ItemStack(config.getBoolean("head_settings.mob_heads.announce_kill.enabled") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta amkMeta = announceMobKill.getItemMeta();
			if (amkMeta != null) {
				amkMeta.setDisplayName(ChatColor.YELLOW + "Announce Mob Kill");
				amkMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.mob_heads.announce_kill.enabled") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Should MMH announce mob beheadings?"));
				amkMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "announce_mob_kill");
				announceMobKill.setItemMeta(amkMeta);
			}
			choices.put(announceMobKill, () -> {
				boolean current = config.getBoolean("head_settings.mob_heads.announce_kill.enabled");
				config.set("head_settings.mob_heads.announce_kill.enabled", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled announce_mob_kill to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Announce Mob Kill set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save announce_mob_kill: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(announceMobKill, 13);

			// Announce Mob Kill Display Name
			ItemStack announceMobKillDisplay = new ItemStack(config.getBoolean("head_settings.mob_heads.announce_kill.displayname") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta amkdMeta = announceMobKillDisplay.getItemMeta();
			if (amkdMeta != null) {
				amkdMeta.setDisplayName(ChatColor.YELLOW + "Announce Mob Kill Display Name");
				amkdMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.mob_heads.announce_kill.displayname") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Should MMH use Display Names? Used by Killed by message too."));
				amkdMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "announce_mob_kill_displayname");
				announceMobKillDisplay.setItemMeta(amkdMeta);
			}
			choices.put(announceMobKillDisplay, () -> {
				boolean current = config.getBoolean("head_settings.mob_heads.announce_kill.displayname");
				config.set("head_settings.mob_heads.announce_kill.displayname", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled announce_mob_kill_displayname to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Announce Mob Kill Display Name set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save announce_mob_kill_displayname: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(announceMobKillDisplay, 14);

			// Mob Head Whitelist
			ItemStack mobHeadWhitelist = new ItemStack(Material.MAP);
			ItemMeta mwlMeta = mobHeadWhitelist.getItemMeta();
			if (mwlMeta != null) {
				List<String> wlList = config.getStringList("head_settings.mob_heads.whitelist");
				String wlDisplay = wlList.isEmpty() ? "None" : String.join(", ", wlList);
				mwlMeta.setDisplayName(ChatColor.YELLOW + "Mob Head Whitelist");
				mwlMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + wlDisplay,
						ChatColor.WHITE + "Click for: /mmh config head_settings.mob_heads.whitelist add [mob]",
						ChatColor.GRAY + "List of Mobs that MMH will make heads for.",
						ChatColor.GRAY + "Example (Cat, Fox, Enderman, Piglin) will drop heads"));
				mwlMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "mob_head_whitelist");
				mobHeadWhitelist.setItemMeta(mwlMeta);
			}
			choices.put(mobHeadWhitelist, () -> {
				mmh.logDebug(String.format("Mob Head Whitelist clicked by player: %s", player.getName()));
				TextComponent message = new TextComponent("Click for 'head_settings.mob_heads.whitelist add/remove' command suggestion.");
				message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mmh config head_settings.mob_heads.whitelist add/remove [mob(s)]"));
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to have the command placed in the chat window.").create()));
				player.spigot().sendMessage(message);
				//player.sendMessage(ChatColor.YELLOW + "Use: /mmh config head_settings.mob_heads.whitelist add [mob]");
			});
			slotAssignments.put(mobHeadWhitelist, 15);

			// Mob Head Blacklist
			ItemStack mobHeadBlacklist = new ItemStack(Material.FILLED_MAP);
			ItemMeta mblMeta = mobHeadBlacklist.getItemMeta();
			if (mblMeta != null) {
				List<String> blList = config.getStringList("head_settings.mob_heads.blacklist");
				String blDisplay = blList.isEmpty() ? "None" : String.join(", ", blList);
				mblMeta.setDisplayName(ChatColor.YELLOW + "Mob Head Blacklist");
				mblMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + blDisplay,
						ChatColor.WHITE + "Click for: /mmh config head_settings.mob_heads.blacklist add [mob]",
						ChatColor.GRAY + "List of Mobs that MMH will NOT make heads for.",
						ChatColor.GRAY + "Example (Wither_Skeleton, Creeper, Ender_Dragon) will NOT drop heads"));
				mblMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "mob_head_blacklist");
				mobHeadBlacklist.setItemMeta(mblMeta);
			}
			choices.put(mobHeadBlacklist, () -> {
				mmh.logDebug(String.format("Mob Head Blacklist clicked by player: %s", player.getName()));
				TextComponent message = new TextComponent("Click for 'head_settings.mob_heads.blacklist add/remove' command suggestion.");
				message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/mmh config head_settings.mob_heads.blacklist add/remove [mob(s)]"));
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to have the command placed in the chat window.").create()));
				player.spigot().sendMessage(message);
				//player.sendMessage(ChatColor.YELLOW + "Use: /mmh config head_settings.mob_heads.blacklist add [mob]");
			});
			slotAssignments.put(mobHeadBlacklist, 16);

			// Nametag Permission
			ItemStack nametag = new ItemStack(config.getBoolean("head_settings.mob_heads.nametag") ? Material.NAME_TAG : Material.PAPER);
			ItemMeta nMeta = nametag.getItemMeta();
			if (nMeta != null) {
				nMeta.setDisplayName(ChatColor.YELLOW + "Nametag Permission");
				nMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.mob_heads.nametag") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Should players with permission moremobheads.nametag be able to give",
						ChatColor.GRAY + "certain mobs player heads using nametags?"));
				nMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "nametag");
				nametag.setItemMeta(nMeta);
			}
			choices.put(nametag, () -> {
				boolean current = config.getBoolean("head_settings.mob_heads.nametag");
				config.set("head_settings.mob_heads.nametag", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled nametag to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Nametag Permission set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save nametag: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(nametag, 17);

			// Vanilla Heads - Creeper
			ItemStack vanillaCreeper = new ItemStack(config.getBoolean("head_settings.mob_heads.vanilla_heads.creeper") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta vcMeta = vanillaCreeper.getItemMeta();
			if (vcMeta != null) {
				vcMeta.setDisplayName(ChatColor.YELLOW + "Vanilla Creeper Head");
				vcMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.mob_heads.vanilla_heads.creeper") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Set whether plugin gives vanilla heads or MoreMobHeads."));
				vcMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "vanilla_creeper");
				vanillaCreeper.setItemMeta(vcMeta);
			}
			choices.put(vanillaCreeper, () -> {
				boolean current = config.getBoolean("head_settings.mob_heads.vanilla_heads.creeper");
				config.set("head_settings.mob_heads.vanilla_heads.creeper", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled vanilla_creeper to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Vanilla Creeper Head set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save vanilla_creeper: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(vanillaCreeper, 18);

			// Vanilla Heads - Ender Dragon
			ItemStack vanillaEnderDragon = new ItemStack(config.getBoolean("head_settings.mob_heads.vanilla_heads.ender_dragon") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta vedMeta = vanillaEnderDragon.getItemMeta();
			if (vedMeta != null) {
				vedMeta.setDisplayName(ChatColor.YELLOW + "Vanilla Ender Dragon Head");
				vedMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.mob_heads.vanilla_heads.ender_dragon") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Set whether plugin gives vanilla heads or MoreMobHeads."));
				vedMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "vanilla_ender_dragon");
				vanillaEnderDragon.setItemMeta(vedMeta);
			}
			choices.put(vanillaEnderDragon, () -> {
				boolean current = config.getBoolean("head_settings.mob_heads.vanilla_heads.ender_dragon");
				config.set("head_settings.mob_heads.vanilla_heads.ender_dragon", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled vanilla_ender_dragon to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Vanilla Ender Dragon Head set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save vanilla_ender_dragon: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(vanillaEnderDragon, 19);

			// Vanilla Heads - Piglin
			ItemStack vanillaPiglin = new ItemStack(config.getBoolean("head_settings.mob_heads.vanilla_heads.piglin") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta vpMeta = vanillaPiglin.getItemMeta();
			if (vpMeta != null) {
				vpMeta.setDisplayName(ChatColor.YELLOW + "Vanilla Piglin Head");
				vpMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.mob_heads.vanilla_heads.piglin") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Set whether plugin gives vanilla heads or MoreMobHeads."));
				vpMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "vanilla_piglin");
				vanillaPiglin.setItemMeta(vpMeta);
			}
			choices.put(vanillaPiglin, () -> {
				boolean current = config.getBoolean("head_settings.mob_heads.vanilla_heads.piglin");
				config.set("head_settings.mob_heads.vanilla_heads.piglin", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled vanilla_piglin to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Vanilla Piglin Head set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save vanilla_piglin: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(vanillaPiglin, 20);

			// Vanilla Heads - Skeleton
			ItemStack vanillaSkeleton = new ItemStack(config.getBoolean("head_settings.mob_heads.vanilla_heads.skeleton") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta vsMeta = vanillaSkeleton.getItemMeta();
			if (vsMeta != null) {
				vsMeta.setDisplayName(ChatColor.YELLOW + "Vanilla Skeleton Head");
				vsMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.mob_heads.vanilla_heads.skeleton") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Set whether plugin gives vanilla heads or MoreMobHeads."));
				vsMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "vanilla_skeleton");
				vanillaSkeleton.setItemMeta(vsMeta);
			}
			choices.put(vanillaSkeleton, () -> {
				boolean current = config.getBoolean("head_settings.mob_heads.vanilla_heads.skeleton");
				config.set("head_settings.mob_heads.vanilla_heads.skeleton", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled vanilla_skeleton to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Vanilla Skeleton Head set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save vanilla_skeleton: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(vanillaSkeleton, 21);

			// Vanilla Heads - Wither Skeleton
			ItemStack vanillaWitherSkeleton = new ItemStack(config.getBoolean("head_settings.mob_heads.vanilla_heads.wither_skeleton") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta vwsMeta = vanillaWitherSkeleton.getItemMeta();
			if (vwsMeta != null) {
				vwsMeta.setDisplayName(ChatColor.YELLOW + "Vanilla Wither Skeleton Head");
				vwsMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.mob_heads.vanilla_heads.wither_skeleton") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Set whether plugin gives vanilla heads or MoreMobHeads."));
				vwsMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "vanilla_wither_skeleton");
				vanillaWitherSkeleton.setItemMeta(vwsMeta);
			}
			choices.put(vanillaWitherSkeleton, () -> {
				boolean current = config.getBoolean("head_settings.mob_heads.vanilla_heads.wither_skeleton");
				config.set("head_settings.mob_heads.vanilla_heads.wither_skeleton", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled vanilla_wither_skeleton to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Vanilla Wither Skeleton Head set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save vanilla_wither_skeleton: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(vanillaWitherSkeleton, 22);

			// Vanilla Heads - Zombie
			ItemStack vanillaZombie = new ItemStack(config.getBoolean("head_settings.mob_heads.vanilla_heads.zombie") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta vzMeta = vanillaZombie.getItemMeta();
			if (vzMeta != null) {
				vzMeta.setDisplayName(ChatColor.YELLOW + "Vanilla Zombie Head");
				vzMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("head_settings.mob_heads.vanilla_heads.zombie") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Set whether plugin gives vanilla heads or MoreMobHeads."));
				vzMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "vanilla_zombie");
				vanillaZombie.setItemMeta(vzMeta);
			}
			choices.put(vanillaZombie, () -> {
				boolean current = config.getBoolean("head_settings.mob_heads.vanilla_heads.zombie");
				config.set("head_settings.mob_heads.vanilla_heads.zombie", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled vanilla_zombie to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Vanilla Zombie Head set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save vanilla_zombie: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configHeadSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(vanillaZombie, 23);

			// Previous Menu
			ItemStack prevButton = new ItemStack(Material.ARROW);
			ItemMeta prevMeta = prevButton.getItemMeta();
			if (prevMeta != null) {
				prevMeta.setDisplayName(ChatColor.YELLOW + "Previous Menu");
				prevMeta.setLore(Arrays.asList(ChatColor.GRAY + "Return to Config Settings"));
				prevMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "previous_menu");
				prevButton.setItemMeta(prevMeta);
			}
			choices.put(prevButton, () -> {
				mmh.logDebug(String.format("Previous Menu clicked by player: %s", player.getName()));
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configMain(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(prevButton, 45);

			InventoryGUI gui = new InventoryGUI("Head Settings", choices);
			gui.setForcePreviousButton(false);
			gui.openWithSlots(player, slotAssignments, null);
			mmh.logDebug(String.format("Head settings menu opened for player: %s, items: %d", player.getName(), choices.size()));
		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(COMMAND_CONFIG_CONFIGHEADSETTINGS).error(exception));
		}
		//COMMAND_CONFIG_CONFIGHEADSETTINGS
	}

	@SuppressWarnings("unused")
	private void configWanderingTrades(Player player) {
		try {
			mmh.logDebug(String.format("Opening wandering trades menu for player: %s", player.getName()));
			if (!isGuiAccessible()) {
				player.sendMessage(ChatColor.RED + "This feature requires the MoreMobHeads DLC. Dev-build access has expired.");
				player.closeInventory();
				return;
			}
			Map<ItemStack, Runnable> choices = new HashMap<>();
			Map<ItemStack, Integer> slotAssignments = new HashMap<>();

			// Custom Wandering Trader
			ItemStack customTrader = new ItemStack(config.getBoolean("wandering_trades.custom_wandering_trader") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta ctMeta = customTrader.getItemMeta();
			if (ctMeta != null) {
				ctMeta.setDisplayName(ChatColor.YELLOW + "Custom Wandering Trader");
				ctMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("wandering_trades.custom_wandering_trader") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Enable custom wandering trader head trades."));
				ctMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "custom_trader");
				customTrader.setItemMeta(ctMeta);
			}
			choices.put(customTrader, () -> {
				boolean current = config.getBoolean("wandering_trades.custom_wandering_trader");
				config.set("wandering_trades.custom_wandering_trader", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled custom_wandering_trader to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Custom Wandering Trader set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save custom_wandering_trader: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(customTrader, 0);

			// Keep Default Trades
			ItemStack keepDefault = new ItemStack(config.getBoolean("wandering_trades.keep_default_trades") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta kdMeta = keepDefault.getItemMeta();
			if (kdMeta != null) {
				kdMeta.setDisplayName(ChatColor.YELLOW + "Keep Default Trades");
				kdMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("wandering_trades.keep_default_trades") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Preserve default wandering trader trades."));
				kdMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "keep_default");
				keepDefault.setItemMeta(kdMeta);
			}
			choices.put(keepDefault, () -> {
				boolean current = config.getBoolean("wandering_trades.keep_default_trades");
				config.set("wandering_trades.keep_default_trades", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled keep_default_trades to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Keep Default Trades set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save keep_default_trades: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(keepDefault, 1);

			// Player Heads Enabled
			ItemStack playerHeadsEnabled = new ItemStack(config.getBoolean("wandering_trades.player_heads.enabled") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta pheMeta = playerHeadsEnabled.getItemMeta();
			if (pheMeta != null) {
				pheMeta.setDisplayName(ChatColor.YELLOW + "Player Heads Enabled");
				pheMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("wandering_trades.player_heads.enabled") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Enable player head trades."));
				pheMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "player_heads_enabled");
				playerHeadsEnabled.setItemMeta(pheMeta);
			}
			choices.put(playerHeadsEnabled, () -> {
				boolean current = config.getBoolean("wandering_trades.player_heads.enabled");
				config.set("wandering_trades.player_heads.enabled", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled player_heads.enabled to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Player Heads Enabled set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save player_heads.enabled: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(playerHeadsEnabled, 2);

			// Increase Player Heads Max
			ItemStack increasePlayerHeadsMax = new ItemStack(Material.STONE);
			ItemMeta iphmMeta = increasePlayerHeadsMax.getItemMeta();
			if (iphmMeta != null) {
				int current = config.getInt("wandering_trades.player_heads.max", 3);
				iphmMeta.setDisplayName(ChatColor.YELLOW + "Increase Player Heads Max");
				iphmMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current Max: " + current,
						ChatColor.WHITE + "Click to increase (max 10)",
						ChatColor.GRAY + "Increases the maximum number of player head trades."));
				iphmMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "increase_player_heads_max");
				increasePlayerHeadsMax.setItemMeta(iphmMeta);
			}
			choices.put(increasePlayerHeadsMax, () -> {
				int current = config.getInt("wandering_trades.player_heads.max", 3);
				if (current < 10) {
					config.set("wandering_trades.player_heads.max", current + 1);
					try {
						YmlConfiguration.saveConfig(configFile, config);
						mmh.logDebug(String.format("Increased player_heads.max to %d for player: %s", current + 1, player.getName()));
						player.sendMessage(ChatColor.GREEN + "Player Heads Max set to " + (current + 1));
					} catch (Exception e) {
						mmh.logDebug(String.format("Failed to save player_heads.max: %s", e.getMessage()));
						player.sendMessage(ChatColor.RED + "Failed to save configuration!");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "Player Heads Max is already at maximum (10)!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(increasePlayerHeadsMax, 3);

			// Decrease Player Heads Max
			ItemStack decreasePlayerHeadsMax = new ItemStack(Material.STONE_SLAB);
			ItemMeta dphmMeta = decreasePlayerHeadsMax.getItemMeta();
			if (dphmMeta != null) {
				int current = config.getInt("wandering_trades.player_heads.max", 3);
				dphmMeta.setDisplayName(ChatColor.YELLOW + "Decrease Player Heads Max");
				dphmMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current Max: " + current,
						ChatColor.WHITE + "Click to decrease (min 0)",
						ChatColor.GRAY + "Decreases the maximum number of player head trades."));
				dphmMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "decrease_player_heads_max");
				decreasePlayerHeadsMax.setItemMeta(dphmMeta);
			}
			choices.put(decreasePlayerHeadsMax, () -> {
				int current = config.getInt("wandering_trades.player_heads.max", 3);
				if (current > 0) {
					config.set("wandering_trades.player_heads.max", current - 1);
					try {
						YmlConfiguration.saveConfig(configFile, config);
						mmh.logDebug(String.format("Decreased player_heads.max to %d for player: %s", current - 1, player.getName()));
						player.sendMessage(ChatColor.GREEN + "Player Heads Max set to " + (current - 1));
					} catch (Exception e) {
						mmh.logDebug(String.format("Failed to save player_heads.max: %s", e.getMessage()));
						player.sendMessage(ChatColor.RED + "Failed to save configuration!");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "Player Heads Max is already at minimum (0)!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(decreasePlayerHeadsMax, 4);

			// Increase Player Heads Min
			ItemStack increasePlayerHeadsMin = new ItemStack(Material.STONE);
			ItemMeta iphmnMeta = increasePlayerHeadsMin.getItemMeta();
			if (iphmnMeta != null) {
				int current = config.getInt("wandering_trades.player_heads.min", 0);
				int max = config.getInt("wandering_trades.player_heads.max", 3);
				iphmnMeta.setDisplayName(ChatColor.YELLOW + "Increase Player Heads Min");
				iphmnMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current Min: " + current,
						ChatColor.WHITE + "Click to increase (max 9, up to Max)",
						ChatColor.GRAY + "Increases the minimum number of player head trades."));
				iphmnMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "increase_player_heads_min");
				increasePlayerHeadsMin.setItemMeta(iphmnMeta);
			}
			choices.put(increasePlayerHeadsMin, () -> {
				int current = config.getInt("wandering_trades.player_heads.min", 0);
				int max = config.getInt("wandering_trades.player_heads.max", 3);
				if ((current < 9) && (current < max)) {
					config.set("wandering_trades.player_heads.min", current + 1);
					try {
						YmlConfiguration.saveConfig(configFile, config);
						mmh.logDebug(String.format("Increased player_heads.min to %d for player: %s", current + 1, player.getName()));
						player.sendMessage(ChatColor.GREEN + "Player Heads Min set to " + (current + 1));
					} catch (Exception e) {
						mmh.logDebug(String.format("Failed to save player_heads.min: %s", e.getMessage()));
						player.sendMessage(ChatColor.RED + "Failed to save configuration!");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "Player Heads Min cannot exceed 9 or Max (" + max + ")!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(increasePlayerHeadsMin, 5);

			// Decrease Player Heads Min
			ItemStack decreasePlayerHeadsMin = new ItemStack(Material.STONE_SLAB);
			ItemMeta dphmnMeta = decreasePlayerHeadsMin.getItemMeta();
			if (dphmnMeta != null) {
				int current = config.getInt("wandering_trades.player_heads.min", 0);
				dphmnMeta.setDisplayName(ChatColor.YELLOW + "Decrease Player Heads Min");
				dphmnMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current Min: " + current,
						ChatColor.WHITE + "Click to decrease (min 0)",
						ChatColor.GRAY + "Decreases the minimum number of player head trades."));
				dphmnMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "decrease_player_heads_min");
				decreasePlayerHeadsMin.setItemMeta(dphmnMeta);
			}
			choices.put(decreasePlayerHeadsMin, () -> {
				int current = config.getInt("wandering_trades.player_heads.min", 0);
				if (current > 0) {
					config.set("wandering_trades.player_heads.min", current - 1);
					try {
						YmlConfiguration.saveConfig(configFile, config);
						mmh.logDebug(String.format("Decreased player_heads.min to %d for player: %s", current - 1, player.getName()));
						player.sendMessage(ChatColor.GREEN + "Player Heads Min set to " + (current - 1));
					} catch (Exception e) {
						mmh.logDebug(String.format("Failed to save player_heads.min: %s", e.getMessage()));
						player.sendMessage(ChatColor.RED + "Failed to save configuration!");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "Player Heads Min is already at minimum (0)!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(decreasePlayerHeadsMin, 6);

			// Block Heads Enabled
			ItemStack blockHeadsEnabled = new ItemStack(config.getBoolean("wandering_trades.block_heads.enabled") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta bheMeta = blockHeadsEnabled.getItemMeta();
			if (bheMeta != null) {
				bheMeta.setDisplayName(ChatColor.YELLOW + "Block Heads Enabled");
				bheMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("wandering_trades.block_heads.enabled") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Enable block head trades."));
				bheMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "block_heads_enabled");
				blockHeadsEnabled.setItemMeta(bheMeta);
			}
			choices.put(blockHeadsEnabled, () -> {
				boolean current = config.getBoolean("wandering_trades.block_heads.enabled");
				config.set("wandering_trades.block_heads.enabled", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled block_heads.enabled to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Block Heads Enabled set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save block_heads.enabled: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(blockHeadsEnabled, 7);

			// Increase Block Heads Max
			ItemStack increaseBlockHeadsMax = new ItemStack(Material.STONE);
			ItemMeta ibhmMeta = increaseBlockHeadsMax.getItemMeta();
			if (ibhmMeta != null) {
				int current = config.getInt("wandering_trades.block_heads.max", 5);
				ibhmMeta.setDisplayName(ChatColor.YELLOW + "Increase Block Heads Max");
				ibhmMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current Max: " + current,
						ChatColor.WHITE + "Click to increase (max 10)",
						ChatColor.GRAY + "Increases the maximum number of block head trades."));
				ibhmMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "increase_block_heads_max");
				increaseBlockHeadsMax.setItemMeta(ibhmMeta);
			}
			choices.put(increaseBlockHeadsMax, () -> {
				int current = config.getInt("wandering_trades.block_heads.max", 5);
				if (current < 10) {
					config.set("wandering_trades.block_heads.max", current + 1);
					try {
						YmlConfiguration.saveConfig(configFile, config);
						mmh.logDebug(String.format("Increased block_heads.max to %d for player: %s", current + 1, player.getName()));
						player.sendMessage(ChatColor.GREEN + "Block Heads Max set to " + (current + 1));
					} catch (Exception e) {
						mmh.logDebug(String.format("Failed to save block_heads.max: %s", e.getMessage()));
						player.sendMessage(ChatColor.RED + "Failed to save configuration!");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "Block Heads Max is already at maximum (10)!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(increaseBlockHeadsMax, 8);

			// Decrease Block Heads Max
			ItemStack decreaseBlockHeadsMax = new ItemStack(Material.STONE_SLAB);
			ItemMeta dbhmMeta = decreaseBlockHeadsMax.getItemMeta();
			if (dbhmMeta != null) {
				int current = config.getInt("wandering_trades.block_heads.max", 5);
				dbhmMeta.setDisplayName(ChatColor.YELLOW + "Decrease Block Heads Max");
				dbhmMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current Max: " + current,
						ChatColor.WHITE + "Click to decrease (min 1)",
						ChatColor.GRAY + "Decreases the maximum number of block head trades."));
				dbhmMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "decrease_block_heads_max");
				decreaseBlockHeadsMax.setItemMeta(dbhmMeta);
			}
			choices.put(decreaseBlockHeadsMax, () -> {
				int current = config.getInt("wandering_trades.block_heads.max", 5);
				if (current > 1) {
					config.set("wandering_trades.block_heads.max", current - 1);
					try {
						YmlConfiguration.saveConfig(configFile, config);
						mmh.logDebug(String.format("Decreased block_heads.max to %d for player: %s", current - 1, player.getName()));
						player.sendMessage(ChatColor.GREEN + "Block Heads Max set to " + (current - 1));
					} catch (Exception e) {
						mmh.logDebug(String.format("Failed to save block_heads.max: %s", e.getMessage()));
						player.sendMessage(ChatColor.RED + "Failed to save configuration!");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "Block Heads Max is already at minimum (1)!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(decreaseBlockHeadsMax, 9);

			// Increase Block Heads Min
			ItemStack increaseBlockHeadsMin = new ItemStack(Material.STONE);
			ItemMeta ibhmnMeta = increaseBlockHeadsMin.getItemMeta();
			if (ibhmnMeta != null) {
				int current = config.getInt("wandering_trades.block_heads.min", 1);
				ibhmnMeta.setDisplayName(ChatColor.YELLOW + "Increase Block Heads Min");
				ibhmnMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current Min: " + current,
						ChatColor.WHITE + "Click to increase (max 9)",
						ChatColor.GRAY + "Increases the minimum number of block head trades."));
				ibhmnMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "increase_block_heads_min");
				increaseBlockHeadsMin.setItemMeta(ibhmnMeta);
			}
			choices.put(increaseBlockHeadsMin, () -> {
				int current = config.getInt("wandering_trades.block_heads.min", 1);
				int max = config.getInt("wandering_trades.block_heads.max", 5);
				if ((current < 9) && (current < max)) {
					config.set("wandering_trades.block_heads.min", current + 1);
					try {
						YmlConfiguration.saveConfig(configFile, config);
						mmh.logDebug(String.format("Increased block_heads.min to %d for player: %s", current + 1, player.getName()));
						player.sendMessage(ChatColor.GREEN + "Block Heads Min set to " + (current + 1));
					} catch (Exception e) {
						mmh.logDebug(String.format("Failed to save block_heads.min: %s", e.getMessage()));
						player.sendMessage(ChatColor.RED + "Failed to save configuration!");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "Block Heads Min cannot exceed 9 or Max!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(increaseBlockHeadsMin, 10);

			// Decrease Block Heads Min
			ItemStack decreaseBlockHeadsMin = new ItemStack(Material.STONE_SLAB);
			ItemMeta dbhmnMeta = decreaseBlockHeadsMin.getItemMeta();
			if (dbhmnMeta != null) {
				int current = config.getInt("wandering_trades.block_heads.min", 1);
				dbhmnMeta.setDisplayName(ChatColor.YELLOW + "Decrease Block Heads Min");
				dbhmnMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current Min: " + current,
						ChatColor.WHITE + "Click to decrease (min 1)",
						ChatColor.GRAY + "Decreases the minimum number of block head trades."));
				dbhmnMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "decrease_block_heads_min");
				decreaseBlockHeadsMin.setItemMeta(dbhmnMeta);
			}
			choices.put(decreaseBlockHeadsMin, () -> {
				int current = config.getInt("wandering_trades.block_heads.min", 1);
				if (current > 1) {
					config.set("wandering_trades.block_heads.min", current - 1);
					try {
						YmlConfiguration.saveConfig(configFile, config);
						mmh.logDebug(String.format("Decreased block_heads.min to %d for player: %s", current - 1, player.getName()));
						player.sendMessage(ChatColor.GREEN + "Block Heads Min set to " + (current - 1));
					} catch (Exception e) {
						mmh.logDebug(String.format("Failed to save block_heads.min: %s", e.getMessage()));
						player.sendMessage(ChatColor.RED + "Failed to save configuration!");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "Block Heads Min is already at minimum (1)!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(decreaseBlockHeadsMin, 11);

			// Custom Trades Enabled
			ItemStack customTradesEnabled = new ItemStack(config.getBoolean("wandering_trades.custom_trades.enabled") ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
			ItemMeta cteMeta = customTradesEnabled.getItemMeta();
			if (cteMeta != null) {
				cteMeta.setDisplayName(ChatColor.YELLOW + "Custom Trades Enabled");
				cteMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current: " + (config.getBoolean("wandering_trades.custom_trades.enabled") ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
						ChatColor.WHITE + "Click to toggle",
						ChatColor.GRAY + "Enable custom trade options."));
				cteMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "custom_trades_enabled");
				customTradesEnabled.setItemMeta(cteMeta);
			}
			choices.put(customTradesEnabled, () -> {
				boolean current = config.getBoolean("wandering_trades.custom_trades.enabled");
				config.set("wandering_trades.custom_trades.enabled", !current);
				try {
					YmlConfiguration.saveConfig(configFile, config);
					mmh.logDebug(String.format("Toggled custom_trades.enabled to %s for player: %s", !current, player.getName()));
					player.sendMessage(ChatColor.GREEN + "Custom Trades Enabled set to " + (!current ? "enabled" : "disabled"));
				} catch (Exception e) {
					mmh.logDebug(String.format("Failed to save custom_trades.enabled: %s", e.getMessage()));
					player.sendMessage(ChatColor.RED + "Failed to save configuration!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(customTradesEnabled, 12);

			// Increase Custom Trades Max
			ItemStack increaseCustomTradesMax = new ItemStack(Material.STONE);
			ItemMeta ictmMeta = increaseCustomTradesMax.getItemMeta();
			if (ictmMeta != null) {
				int current = config.getInt("wandering_trades.custom_trades.max", 5);
				ictmMeta.setDisplayName(ChatColor.YELLOW + "Increase Custom Trades Max");
				ictmMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current Max: " + current,
						ChatColor.WHITE + "Click to increase (max 10)",
						ChatColor.GRAY + "Increases the maximum number of custom trades."));
				ictmMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "increase_custom_trades_max");
				increaseCustomTradesMax.setItemMeta(ictmMeta);
			}
			choices.put(increaseCustomTradesMax, () -> {
				int current = config.getInt("wandering_trades.custom_trades.max", 5);
				if (current < 10) {
					config.set("wandering_trades.custom_trades.max", current + 1);
					try {
						YmlConfiguration.saveConfig(configFile, config);
						mmh.logDebug(String.format("Increased custom_trades.max to %d for player: %s", current + 1, player.getName()));
						player.sendMessage(ChatColor.GREEN + "Custom Trades Max set to " + (current + 1));
					} catch (Exception e) {
						mmh.logDebug(String.format("Failed to save custom_trades.max: %s", e.getMessage()));
						player.sendMessage(ChatColor.RED + "Failed to save configuration!");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "Custom Trades Max is already at maximum (10)!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(increaseCustomTradesMax, 13);

			// Decrease Custom Trades Max
			ItemStack decreaseCustomTradesMax = new ItemStack(Material.STONE_SLAB);
			ItemMeta dctmMeta = decreaseCustomTradesMax.getItemMeta();
			if (dctmMeta != null) {
				int current = config.getInt("wandering_trades.custom_trades.max", 5);
				dctmMeta.setDisplayName(ChatColor.YELLOW + "Decrease Custom Trades Max");
				dctmMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current Max: " + current,
						ChatColor.WHITE + "Click to decrease (min 0)",
						ChatColor.GRAY + "Decreases the maximum number of custom trades."));
				dctmMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "decrease_custom_trades_max");
				decreaseCustomTradesMax.setItemMeta(dctmMeta);
			}
			choices.put(decreaseCustomTradesMax, () -> {
				int current = config.getInt("wandering_trades.custom_trades.max", 5);
				if (current > 0) {
					config.set("wandering_trades.custom_trades.max", current - 1);
					try {
						YmlConfiguration.saveConfig(configFile, config);
						mmh.logDebug(String.format("Decreased custom_trades.max to %d for player: %s", current - 1, player.getName()));
						player.sendMessage(ChatColor.GREEN + "Custom Trades Max set to " + (current - 1));
					} catch (Exception e) {
						mmh.logDebug(String.format("Failed to save custom_trades.max: %s", e.getMessage()));
						player.sendMessage(ChatColor.RED + "Failed to save configuration!");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "Custom Trades Max is already at minimum (0)!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(decreaseCustomTradesMax, 14);

			// Increase Custom Trades Min
			ItemStack increaseCustomTradesMin = new ItemStack(Material.STONE);
			ItemMeta ictmnMeta = increaseCustomTradesMin.getItemMeta();
			if (ictmnMeta != null) {
				int current = config.getInt("wandering_trades.custom_trades.min", 0);
				ictmnMeta.setDisplayName(ChatColor.YELLOW + "Increase Custom Trades Min");
				ictmnMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current Min: " + current,
						ChatColor.WHITE + "Click to increase (max 9)",
						ChatColor.GRAY + "Increases the minimum number of custom trades."));
				ictmnMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "increase_custom_trades_min");
				increaseCustomTradesMin.setItemMeta(ictmnMeta);
			}
			choices.put(increaseCustomTradesMin, () -> {
				int current = config.getInt("wandering_trades.custom_trades.min", 0);
				int max = config.getInt("wandering_trades.custom_trades.max", 5);
				if ((current < 9) && (current < max)) {
					config.set("wandering_trades.custom_trades.min", current + 1);
					try {
						YmlConfiguration.saveConfig(configFile, config);
						mmh.logDebug(String.format("Increased custom_trades.min to %d for player: %s", current + 1, player.getName()));
						player.sendMessage(ChatColor.GREEN + "Custom Trades Min set to " + (current + 1));
					} catch (Exception e) {
						mmh.logDebug(String.format("Failed to save custom_trades.min: %s", e.getMessage()));
						player.sendMessage(ChatColor.RED + "Failed to save configuration!");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "Custom Trades Min cannot exceed 9 or Max!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(increaseCustomTradesMin, 15);

			// Decrease Custom Trades Min
			ItemStack decreaseCustomTradesMin = new ItemStack(Material.STONE_SLAB);
			ItemMeta dctmnMeta = decreaseCustomTradesMin.getItemMeta();
			if (dctmnMeta != null) {
				int current = config.getInt("wandering_trades.custom_trades.min", 0);
				dctmnMeta.setDisplayName(ChatColor.YELLOW + "Decrease Custom Trades Min");
				dctmnMeta.setLore(Arrays.asList(
						ChatColor.WHITE + "Current Min: " + current,
						ChatColor.WHITE + "Click to decrease (min 0)",
						ChatColor.GRAY + "Decreases the minimum number of custom trades."));
				dctmnMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "decrease_custom_trades_min");
				decreaseCustomTradesMin.setItemMeta(dctmnMeta);
			}
			choices.put(decreaseCustomTradesMin, () -> {
				int current = config.getInt("wandering_trades.custom_trades.min", 0);
				if (current > 0) {
					config.set("wandering_trades.custom_trades.min", current - 1);
					try {
						YmlConfiguration.saveConfig(configFile, config);
						mmh.logDebug(String.format("Decreased custom_trades.min to %d for player: %s", current - 1, player.getName()));
						player.sendMessage(ChatColor.GREEN + "Custom Trades Min set to " + (current - 1));
					} catch (Exception e) {
						mmh.logDebug(String.format("Failed to save custom_trades.min: %s", e.getMessage()));
						player.sendMessage(ChatColor.RED + "Failed to save configuration!");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "Custom Trades Min is already at minimum (0)!");
				}
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configWanderingTrades(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(decreaseCustomTradesMin, 16);

			// Previous Menu
			ItemStack prevButton = new ItemStack(Material.ARROW);
			ItemMeta prevMeta = prevButton.getItemMeta();
			if (prevMeta != null) {
				prevMeta.setDisplayName(ChatColor.YELLOW + "Previous Menu");
				prevMeta.setLore(Arrays.asList(ChatColor.GRAY + "Return to Config Settings"));
				prevMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "previous_menu");
				prevButton.setItemMeta(prevMeta);
			}
			choices.put(prevButton, () -> {
				mmh.logDebug(String.format("Previous Menu clicked by player: %s", player.getName()));
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configMain(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(prevButton, 45);

			InventoryGUI gui = new InventoryGUI("Wandering Trades", choices);
			gui.setForcePreviousButton(false);
			gui.openWithSlots(player, slotAssignments, null);
			mmh.logDebug(String.format("Wandering trades menu opened for player: %s, items: %d", player.getName(), choices.size()));
		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(COMMAND_CONFIG_CONFIGWANDERINGTRADES).error(exception));
		}
	}

	private void configLanguage(Player player) {
		try{
			mmh.logDebug(String.format("Opening language menu for player: %s", player.getName()));
			if (!isGuiAccessible()) {
				player.sendMessage(ChatColor.RED + "This feature requires the MoreMobHeads DLC. Dev-build access has expired.");
				player.closeInventory();
				return;
			}
			Map<ItemStack, Runnable> choices = new HashMap<>();
			Map<ItemStack, Integer> slotAssignments = new HashMap<>();

			// Language Options with Skinned Heads
			int slot = 0;
			for (Language lang : Language.values()) {
				ItemStack langItem = HeadUtils.makeHead(ChatColor.YELLOW + lang.getLangNameInEnglish(), lang.getTexture(), lang.getUuid(), new ArrayList<>(Arrays.asList(
						ChatColor.WHITE + "Code: " + lang.getLangCode(),
						ChatColor.WHITE + "Click to select")), null);
				SkullMeta lmMeta = (SkullMeta) langItem.getItemMeta();
				if (lmMeta != null) {
					lmMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "language_" + lang.getLangCode());
					langItem.setItemMeta(lmMeta);
				}
				final String langCode = lang.getLangCode();
				choices.put(langItem, () -> {
					config.set("plugin_settings.lang", langCode);
					try {
						YmlConfiguration.saveConfig(configFile, config);
						mmh.logDebug(String.format("Language set to %s for player: %s", langCode, player.getName()));
						player.sendMessage(ChatColor.GREEN + "Language set to " + lang.getLangNameInEnglish());
					} catch (Exception e) {
						mmh.logDebug(String.format("Failed to save language: %s", e.getMessage()));
						player.sendMessage(ChatColor.RED + "Failed to save configuration!");
					}
					player.closeInventory();
					new BukkitRunnable() {
						@Override
						public void run() {
							configPSettings(player);
						}
					}.runTaskLater(mmh, 1L);
				});
				slotAssignments.put(langItem, slot++);
				if (slot >= 45) {
					break; // Limit to 45 slots for a single inventory page
				}
			}

			// Previous Menu
			ItemStack prevButton = new ItemStack(Material.ARROW);
			ItemMeta prevMeta = prevButton.getItemMeta();
			if (prevMeta != null) {
				prevMeta.setDisplayName(ChatColor.YELLOW + "Previous Menu");
				prevMeta.setLore(Arrays.asList(ChatColor.GRAY + "Return to Plugin Settings"));
				prevMeta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, "previous_menu");
				prevButton.setItemMeta(prevMeta);
			}
			choices.put(prevButton, () -> {
				mmh.logDebug(String.format("Previous Menu clicked by player: %s", player.getName()));
				player.closeInventory();
				new BukkitRunnable() {
					@Override
					public void run() {
						configPSettings(player);
					}
				}.runTaskLater(mmh, 1L);
			});
			slotAssignments.put(prevButton, 45);

			InventoryGUI gui = new InventoryGUI("Select Language", choices);
			gui.setForcePreviousButton(false);
			gui.openWithSlots(player, slotAssignments, null);
			mmh.logDebug(String.format("Language menu opened for player: %s, items: %d", player.getName(), choices.size()));
		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(COMMAND_CONFIG_CONFIGLABGUAGE).error(exception));
		}
	}
}