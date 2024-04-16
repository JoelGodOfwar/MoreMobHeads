package com.github.joelgodofwar.mmh.util;

import com.github.joelgodofwar.mmh.MoreMobHeads;

/**
 * Utility class for verifying and retrieving values from a FileConfiguration.
 */
public class VerifyConfig {
	MoreMobHeads mmh;

	/**
	 * Constructs a VerifyConfig object with the given FileConfiguration.
	 *
	 * @param config The FileConfiguration to be verified.
	 */
	public VerifyConfig(final MoreMobHeads plugin) {
		this.mmh = plugin;
	}

	/**
	 * Retrieves a string value from the configuration for the specified path.
	 * If the value is null, the specified defaultValue is returned.
	 *
	 * @param path         The path to the configuration value.
	 * @param defaultValue The default value to be returned if the specified path is null.
	 * @return The retrieved string value or the defaultValue if null.
	 */
	public String getString(String path, String defaultValue ) {
		try {
			String stringValue = mmh.getConfig().getString(path, null);
			if (stringValue == null) {
				throw new NullPointerException("Could not read value of: " + path);
			}
			return stringValue;
		}catch (NullPointerException e) {
			e.printStackTrace();
		}
		return defaultValue;
	}

	/**
	 * Retrieves a boolean value from the configuration for the specified path.
	 * If the value is null, the specified defaultValue is returned.
	 *
	 * @param path         The path to the configuration value.
	 * @param defaultValue The default value to be returned if the specified path is null.
	 * @return The retrieved boolean value or the defaultValue if null.
	 */
	public boolean getBoolean(String path, boolean defaultValue ) {
		try {
			String stringValue = mmh.getConfig().getString(path, null);
			if (stringValue == null) {
				throw new NullPointerException("Could not read value of: " + path);
			}
			return Boolean.parseBoolean(stringValue);
		}catch (NullPointerException e) {
			e.printStackTrace();
		}
		return defaultValue;
	}

	/**
	 * Retrieves an integer value from the configuration for the specified path.
	 * If the value is null, the specified defaultValue is returned.
	 *
	 * @param path         The path to the configuration value.
	 * @param defaultValue The default value to be returned if the specified path is null.
	 * @return The retrieved integer value or the defaultValue if null.
	 */
	public int getInt(String path, int defaultValue ) {
		try {
			String stringValue = mmh.getConfig().getString(path, null);
			if (stringValue == null) {
				throw new NullPointerException("Could not read value of: " + path);
			}
			return Integer.parseInt(stringValue);
		}catch (NullPointerException | NumberFormatException  e) {
			e.printStackTrace();
		}
		return defaultValue;
	}

	public boolean checkString(String path){
		try {
			String stringValue = mmh.getConfig().getString(path, null);
			if(stringValue != null) {
				return true;
			}else {
				return false;
			}
		}catch(Exception e) {
			return false;
		}
	}

	public void verifyValues() {
		mmh.LOGGER.log("plugin_settings.auto_update_check " + getBoolean("plugin_settings.auto_update_check", true));
		mmh.LOGGER.log("plugin_settings.debug " + getBoolean("plugin_settings.debug", false));
		mmh.LOGGER.log("plugin_settings.lang " + getString("plugin_settings.lang", "en_US"));
		mmh.LOGGER.log("global_settings.console.colorful_console " + getBoolean("global_settings.console.colorful_console", true));
		mmh.LOGGER.log("global_settings.console.silent_console " + getBoolean("global_settings.console.silent_console", true));
		mmh.LOGGER.log("global_settings.console.longpluginname " + getBoolean("global_settings.console.longpluginname", true));
		mmh.LOGGER.log("global_settings.world.whitelist " + getString("global_settings.world.whitelist", ""));
		mmh.LOGGER.log("global_settings.world.blacklist " + getString("global_settings.world.blacklist", ""));
		mmh.LOGGER.log("global_settings.event.piston_extend " + getBoolean("global_settings.event.piston_extend", true));
		mmh.LOGGER.log("head_settings.apply_looting " + getBoolean("head_settings.apply_looting", true));
		mmh.LOGGER.log("head_settings.lore.show_killer " + getBoolean("head_settings.lore.show_killer", true));
		mmh.LOGGER.log("head_settings.lore.show_plugin_name " + getBoolean("head_settings.lore.show_plugin_name", true));
		mmh.LOGGER.log("head_settings.player_heads.announce_kill.enabled" + getBoolean("head_settings.player_heads.announce_kill.enabled", false));
		mmh.LOGGER.log("head_settings.player_heads.announce_kill.displayname " + getBoolean("head_settings.player_heads.announce_kill.displayname", true));
		mmh.LOGGER.log("head_settings.player_heads.whitelist.enforce " + getBoolean("head_settings.player_heads.whitelist.enforce", true));
		mmh.LOGGER.log("head_settings.player_heads.whitelist.player_head_whitelist " + getString("head_settings.player_heads.whitelist.player_head_whitelist", ""));
		mmh.LOGGER.log("head_settings.player_heads.blacklist.enforce " + getBoolean("head_settings.player_heads.blacklist.enforce", true));
		mmh.LOGGER.log("head_settings.player_heads.blacklist.player_head_blacklist " + getString("head_settings.player_heads.blacklist.player_head_blacklist", ""));
		mmh.LOGGER.log("head_settings.mob_heads.announce_kill.enabled " + getBoolean("head_settings.mob_heads.announce_kill.enabled", false));
		mmh.LOGGER.log("head_settings.mob_heads.announce_kill.displayname " + getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
		mmh.LOGGER.log("head_settings.mob_heads.whitelist " + getString("head_settings.mob_heads.whitelist", ""));
		mmh.LOGGER.log("head_settings.mob_heads.blacklist " + getString("head_settings.mob_heads.blacklist", ""));
		mmh.LOGGER.log("head_settings.mob_heads.nametag " + getBoolean("head_settings.mob_heads.nametag", false));
		mmh.LOGGER.log("head_settings.mob_heads.vanilla_heads.creepers " + getBoolean("head_settings.mob_heads.vanilla_heads.creepers", true));
		mmh.LOGGER.log("head_settings.mob_heads.vanilla_heads.ender_dragon " + getBoolean("head_settings.mob_heads.vanilla_heads.ender_dragon", true));
		mmh.LOGGER.log("head_settings.mob_heads.vanilla_heads.piglin " + getBoolean("head_settings.mob_heads.vanilla_heads.piglin", true));
		mmh.LOGGER.log("head_settings.mob_heads.vanilla_heads.skeleton " + getBoolean("head_settings.mob_heads.vanilla_heads.skeleton", true));
		mmh.LOGGER.log("head_settings.mob_heads.vanilla_heads.wither_skeleto " + getBoolean("head_settings.mob_heads.vanilla_heads.wither_skeleton", true));
		mmh.LOGGER.log("head_settings.mob_heads.vanilla_heads.zombie " + getBoolean("head_settings.mob_heads.vanilla_heads.zombie", true));
		mmh.LOGGER.log("wandering_trades.custom_wandering_trader " + getBoolean("wandering_trades.custom_wandering_trader", true));
		mmh.LOGGER.log("wandering_trades.keep_default_trades " + getBoolean("wandering_trades.keep_default_trades", true));
		mmh.LOGGER.log("wandering_trades.player_heads.enabled " + getBoolean("wandering_trades.player_heads.enabled", true));
		mmh.LOGGER.log("wandering_trades.player_heads.min " + getInt("wandering_trades.player_heads.min", 0));
		mmh.LOGGER.log("wandering_trades.player_heads.max " + getInt("wandering_trades.player_heads.max", 5));
		mmh.LOGGER.log("wandering_trades.block_heads.enabled " + getBoolean("wandering_trades.block_heads.enabled", true));
		mmh.LOGGER.log("wandering_trades.block_heads.pre_116.min" + getInt("wandering_trades.block_heads.pre_116.min", 0));
		mmh.LOGGER.log("wandering_trades.block_heads.pre_116.max " + getInt("wandering_trades.block_heads.pre_116.max", 5));
		mmh.LOGGER.log("wandering_trades.block_heads.is_116.min" + getInt("wandering_trades.block_heads.is_116.min", 0));
		mmh.LOGGER.log("wandering_trades.block_heads.is_116.max " + getInt("wandering_trades.block_heads.is_116.max", 5));
		mmh.LOGGER.log("wandering_trades.block_heads.is_117.min " + getInt("wandering_trades.block_heads.is_117.min", 0));
		mmh.LOGGER.log("wandering_trades.block_heads.is_117.max " + getInt("wandering_trades.block_heads.is_117.max", 5));
		mmh.LOGGER.log("wandering_trades.custom_trades.enabled " + getBoolean("wandering_trades.custom_trades.enabled", true));
		mmh.LOGGER.log("wandering_trades.custom_trades.min " + getInt("wandering_trades.custom_trades.min", 0));
		mmh.LOGGER.log("wandering_trades.custom_trades.max " + getInt("wandering_trades.custom_trades.max", 5));
	}

}
