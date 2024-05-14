package com.github.joelgodofwar.mmh.common;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;

import com.github.joelgodofwar.mmh.common.error.BasicErrorReporter;
import com.github.joelgodofwar.mmh.common.error.ErrorReporter;
import com.github.joelgodofwar.mmh.common.error.ReportType;
import com.google.common.collect.ImmutableList;

public class PluginLibrary {

	/**
	 * The minimum version MoreMobHeads has been tested with.
	 */
	public static final String MINIMUM_MINECRAFT_VERSION = "1.20";

	/**
	 * The maximum version MoreMobHeads has been tested with.
	 */
	public static final String MAXIMUM_MINECRAFT_VERSION = "1.20.4";

	/**
	 * The date (with ISO 8601 or YYYY-MM-DD) when the most recent version (1.20.4) was released.
	 */
	public static final String MINECRAFT_LAST_RELEASE_DATE = "2023-12-07";

	/**
	 * Plugins that are currently incompatible with MoreMobHeads.
	 */
	public static final List<String> INCOMPATIBLE = ImmutableList.of("");

	private static Plugin plugin;

	private static boolean updatesDisabled;
	private static boolean initialized;
	private static ErrorReporter reporter = new BasicErrorReporter();

	protected static void init(Plugin plugin, ErrorReporter reporter) {
		Validate.isTrue(!initialized, "MoreMobHeads has already been initialized.");
		PluginLibrary.plugin = plugin;
		PluginLibrary.reporter = reporter;

		initialized = true;
	}

	// Every possible error or warning report type
	public static final ReportType REPORT_CANNOT_DELETE_CONFIG = new ReportType("Cannot delete old MoreMobHeads configuration.");
	public static final ReportType REPORT_CANNOT_COPY_FILE = new ReportType("Cannot copy file.");
	public static final ReportType REPORT_METRICS_LOAD_ERROR = new ReportType("Cannot load bStats Metrics.");

	public static final ReportType REPORT_PLUGIN_LOAD_ERROR = new ReportType("Cannot load MoreMobHeads.");
	public static final ReportType REPORT_CANNOT_LOAD_CONFIG = new ReportType("Cannot load configuration");
	public static final ReportType REPORT_CANNOT_CHECK_CONFIG = new ReportType("Cannot check configuration");
	public static final ReportType REPORT_CANNOT_SAVE_CONFIG = new ReportType("Cannot save configuration");
	public static final ReportType REPORT_PLUGIN_ENABLE_ERROR = new ReportType("Cannot enable MoreMobHeads.");
	public static final ReportType REPORT_PLUGIN_UNKNOWN_ERROR = new ReportType("Unknown Error");

	public static final ReportType REPORT_MESSAGES_LOAD_ERROR = new ReportType("Could not load messages.yml");
	public static final ReportType REPORT_MESSAGES_COPY_ERROR = new ReportType("Could not copy messages.yml to old_messages.yml");
	public static final ReportType REPORT_OLDMESSAGES_LOAD_ERROR = new ReportType("Could not load old_messages.yml");
	public static final ReportType REPORT_OLDMESSAGES_SAVE_ERROR = new ReportType("Could not save old messages to messages.yml");
	public static final ReportType REPORT_PLAYERHEAD_LOAD_ERROR = new ReportType("Could not load player_heads.yml");
	public static final ReportType REPORT_CUSTOM_LOAD_ERROR = new ReportType("Could not load custom_trades.yml");
	public static final ReportType REPORT_CHANCE_LOAD_ERROR = new ReportType("Could not load chance_config.yml");
	public static final ReportType REPORT_CHANCE_SAVE_ERROR = new ReportType("Could not save chance_config.yml");
	public static final ReportType REPORT_CHANCE_COPY_ERROR = new ReportType("Could not copy chance_config.yml to old_chance_config.yml");
	public static final ReportType REPORT_MOBNAMES_LOAD_ERROR = new ReportType("Could not load lang_mobnames file");

	public static final ReportType REPORT_PIE_LOAD_ERROR = new ReportType("Error with naming mob.");
	public static final ReportType REPORT_HEAD_URL_ERROR = new ReportType("Malformed URL Error");
	public static final ReportType REPORT_BLOCKHEAD_LOAD_ERROR = new ReportType("Cannot load BlockHeads file");

	public static final ReportType REPORT_METRICS_IO_ERROR = new ReportType("Unable to enable metrics due to network problems.");
	public static final ReportType REPORT_METRICS_GENERIC_ERROR = new ReportType("Unable to enable metrics due to network problems.");

	public static final ReportType REPORT_CANNOT_PARSE_MINECRAFT_VERSION = new ReportType("Unable to retrieve current Minecraft version. Assuming %s");
	public static final ReportType REPORT_CANNOT_DETECT_CONFLICTING_PLUGINS = new ReportType("Unable to detect conflicting plugin versions.");
	public static final ReportType REPORT_CANNOT_REGISTER_COMMAND = new ReportType("Cannot register command %s: %s");

	public static final ReportType REPORT_CANNOT_CREATE_TIMEOUT_TASK = new ReportType("Unable to create packet timeout task.");
	public static final ReportType REPORT_CANNOT_UPDATE_PLUGIN = new ReportType("Cannot perform automatic updates.");

	public static final ReportType REPORT_CANNOT_GET_HEXNICK = new ReportType("Cannot get Nick from HexNicks.");

	public static final ReportType REPORT_PLAYER_NAMED_MOB = new ReportType("Unable to parse named mob kill.");
	public static final ReportType REPORT_PLAYER_KILL_PLAYER = new ReportType("Unable to parse Player Death.");
	public static final ReportType REPORT_WHITE_BLACK_LIST = new ReportType("Unable to parse global whitelist/blacklist.");
	public static final ReportType REPORT_PLAYER_KILL_MOB = new ReportType("Unable to parse Mob Kill.");

	public static final ReportType REPORT_EVENT_HANDLER_LOAD = new ReportType("Error while loading EventHandler.");
	public static final ReportType REPORT_CS_EVENT_ERROR = new ReportType("Error while entity spawning.");
	public static final ReportType REPORT_ANNOUNCE_ERROR = new ReportType("Error announcing beheading.");
	public static final ReportType REPORT_RESOLVE_DAMAGE_WEAPON = new ReportType("Error resolving the Damaging Weapon.");
	public static final ReportType REPORT_GET_WEAPON_ERROR = new ReportType("Error getting weapon.");


	public static final ReportType REPORT_COMMAND_MENU_ERROR = new ReportType("Error displaying Command Menu.");
	public static final ReportType REPORT_COMMAND_HEADNBT_ERROR = new ReportType("Error executing HeadNBT command.");
	public static final ReportType REPORT_COMMAND_DISPLAY_PERMS = new ReportType("Error executing Display Perms command.");
	public static final ReportType REPORT_COMMAND_DISPLAY_VARS = new ReportType("Error executing Display Vars command.");
	public static final ReportType REPORT_COMMAND_DISPLAY_CHANCE = new ReportType("Error executing Display Chance command.");
	public static final ReportType REPORT_COMMAND_RELOAD_ERROR = new ReportType("Error executing Reload command.");
	public static final ReportType REPORT_COMMAND_TOGGLE_DEBUG = new ReportType("Error executing ToggleDebug Command.");
	public static final ReportType REPORT_COMMAND_CUSTOM_TRADER = new ReportType("Error executing CustomTrader Command.");
	public static final ReportType REPORT_COMMAND_PLAYER_HEADS = new ReportType("Error executing PlayerHeads Command.");
	public static final ReportType REPORT_COMMAND_FIX_HEAD = new ReportType("Error executing Fixhead Command.");
	public static final ReportType REPORT_COMMAND_GIVE_MH = new ReportType("Error executing GiveMH Command.");
	public static final ReportType REPORT_COMMAND_GIVE_PH = new ReportType("Error executing GivePH Command.");
	public static final ReportType REPORT_COMMAND_GIVE_BH = new ReportType("Error executing GiveBH Command.");
	public static final ReportType REPORT_COMMAND_DEV_ERROR = new ReportType("Error exuting dev Command.");

	public static final ReportType UNHANDLED_COMMAND_ERROR = new ReportType("Command had an Unhandled exception.");
	public static final ReportType REPORT_TAB_COMPLETE_ERROR = new ReportType("Error parsing Tab Complete.");

	public static final ReportType UNHANDLED_ENTITYDEATHEVENT_ERROR = new ReportType("Unhandled EntityDeathEvent Exception.");
	public static final ReportType UNHANDLED_ENTITYDAMAGE_EVENT_ERROR = new ReportType("Unable to parse fire damage.");
	public static final ReportType UNHANDLED_ENTITYDAMAGEBYENTITY_EVENT_ERROR = new ReportType("Unable to get damager weapon.");




	/**
	 * Gets the MoreMobHeads plugin instance.
	 * @return The plugin instance
	 */
	public static Plugin getPlugin() {
		return plugin;
	}

	/**
	 * Disables the MoreMobHeads update checker.
	 */
	public static void disableUpdates() {
		updatesDisabled = true;
	}

	/**
	 * Retrieve the current error reporter.
	 * @return Current error reporter.
	 */
	public static ErrorReporter getErrorReporter() {
		return reporter;
	}

	/**
	 * Whether updates are currently disabled.
	 * @return True if it is, false if not
	 */
	public static boolean updatesDisabled() {
		return updatesDisabled;
	}
}