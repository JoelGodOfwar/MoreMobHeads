package com.github.joelgodofwar.mmh.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.github.joelgodofwar.mmh.enums.Perms;
import lib.github.joelgodofwar.coreutils.util.YmlConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.joelgodofwar.mmh.MoreMobHeads;

import com.github.joelgodofwar.mmh.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.mmh.common.error.Report;
import com.github.joelgodofwar.mmh.common.error.ReportType;
import org.bukkit.entity.Player;

public class ConfigCommand {
    private final YmlConfiguration config;
	private final File configFile;
    private final DetailedErrorReporter reporter;
	public static final ReportType COMMAND_CONFIG_EXECUTE = new ReportType("Error executing ConfigCommand.");
	public static final ReportType COMMAND_CONFIG_HANDLEWORLDCOMMAND = new ReportType("Error processing handleWorldCommand.");
	public static final ReportType COMMAND_CONFIG_HANDLEPLAYERHEADSCOMMAND = new ReportType("Error processing handlePlayerHeadsCommand.");
	public static final ReportType COMMAND_CONFIG_HANDLEMOBHEADSCOMMAND = new ReportType("Error processing handleMobHeadsCommand.");
	public static final ReportType COMMAND_CONFIG_PROCESSLIST = new ReportType("Error processing processList.");
	MoreMobHeads mmh;

	public ConfigCommand(MoreMobHeads plugin) {
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
		this.config = plugin.config;

		this.reporter = new DetailedErrorReporter(plugin);
		this.mmh = plugin;
	}

	public boolean execute(CommandSender sender, String[] args) {
		if (!Perms.CONFIG.hasPermission(sender)) {
			sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
			return true;
		}

		try {

			// Case 1: No arguments at all
			if (args.length == 0) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.AQUA + "The configuration GUI is only available to players.");
				}
				sender.sendMessage(ChatColor.AQUA + "Usage:");
				sender.sendMessage(ChatColor.AQUA + " • /mmh config <config.path> <value>  - Set any config option");
				sender.sendMessage(ChatColor.AQUA + "   Example: /mmh config global_settings.debug true");
				sender.sendMessage(ChatColor.AQUA + "   Example: /mmh config head_settings.mob_heads.whitelist add zombie,villager");
				sender.sendMessage(ChatColor.AQUA + "   Example: /mmh config head_settings.player_heads.blacklist add Notch,Herobrine");
				if (sender instanceof Player) {
					sender.sendMessage(ChatColor.YELLOW + "Click here or type /mmh config gui to open the menu.");
				}
				return true;
			}

			String path = args[0].toLowerCase();

			// 1. Legacy list commands — only ones that contain "whitelist" or "blacklist"
			if (path.contains("whitelist") || path.contains("blacklist")) {
				return handleLegacyListCommand(sender, args);
			}

			// 2. Universal setter — everything else with a dot + value
			if (path.contains(".") && args.length >= 2) {
				return handleUniversalSet(sender, args);
			}

			// 3. Fallback
			sender.sendMessage(ChatColor.DARK_RED + "Invalid config path or command.");
			sender.sendMessage(ChatColor.AQUA + "Use /mmh config for help.");
			return true;
		} catch (Exception e) {
			reporter.reportDetailed(this, Report.newBuilder(COMMAND_CONFIG_EXECUTE).error(e));
			sender.sendMessage(ChatColor.DARK_RED + "An error occurred while processing the command.");
			return true;
		}
	}

	private boolean handleLegacyListCommand(CommandSender sender, String[] args) {
		// This preserves ALL your existing functionality perfectly
		String sub = args[0].toLowerCase();
		if (sub.startsWith("world.")) {
			return handleWorldCommand(sender, args);
		} else if (sub.startsWith("head_settings.player_heads.")) {
			return handlePlayerHeadsCommand(sender, args);
		} else if (sub.startsWith("head_settings.mob_heads.")) {
			return handleMobHeadsCommand(sender, args);
		}
		return false;
	}

	private boolean handleUniversalSet(CommandSender sender, String[] args) {
		try {
			String path = args[0];
			String rawValue = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).trim();

			Object value;
			if (rawValue.equalsIgnoreCase("true") || rawValue.equalsIgnoreCase("false")) {
				value = Boolean.parseBoolean(rawValue);
			} else if (rawValue.matches("-?\\d+")) {
				value = Integer.parseInt(rawValue);
			} else if (rawValue.matches("-?\\d+(\\.\\d+)?")) {
				value = Double.parseDouble(rawValue);
			} else if (rawValue.startsWith("[") && rawValue.endsWith("]")) {
				value = config.getList(path, new ArrayList<>()); // fallback
				// parse [a,b,c]
				String inner = rawValue.substring(1, rawValue.length() - 1);
				value = Arrays.stream(inner.split(","))
						.<String>map(String::trim)
						.filter(s -> !s.isEmpty())
						.collect(Collectors.toList());
			} else if (rawValue.contains(",")) {
				value = Arrays.stream(rawValue.split(","))
						.map(String::trim)
						.filter(s -> !s.isEmpty())
						.collect(Collectors.toList());
			} else {
				value = rawValue;
			}

			// THIS IS THE KEY FIX:
			config.set(path, null); // clears old value + forces path creation
			config.set(path, value);

			YmlConfiguration.saveConfig(configFile, config);

			sender.sendMessage(ChatColor.GREEN + "Set " + ChatColor.GOLD + path + ChatColor.GREEN + " → " + ChatColor.YELLOW + value);
			return true;

		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Failed to set config value: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleWorldCommand(CommandSender sender, String[] args) {
		try {
			if (args.length < 3) {
				sender.sendMessage(ChatColor.AQUA + "Usage: /mmh config world.<whitelist/blacklist> <add/remove> [world(s)]");
				return true;
			}

			String[] subCommandParts = args[0].toLowerCase().split("\\.");
			if (subCommandParts.length != 2 || !subCommandParts[0].equals("world")) {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid subcommand. Use: world.whitelist or world.blacklist");
				return true;
			}

			String listType = subCommandParts[1];
			if (!listType.equals("whitelist") && !listType.equals("blacklist")) {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid list type. Use: whitelist or blacklist");
				return true;
			}

			String action = args[1].toLowerCase();
			if (!action.equals("add") && !action.equals("remove")) {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid action. Use: add or remove");
				return true;
			}

			String itemsInput = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
			List<String> items = Arrays.stream(itemsInput.split(","))
					.map(String::trim)
					.filter(item -> !item.isEmpty())
					.collect(Collectors.toList());

			if (items.isEmpty()) {
				sender.sendMessage(ChatColor.DARK_RED + "No valid worlds provided.");
				sender.sendMessage(ChatColor.AQUA + "Usage: /mmh config world." + listType + " " + action + " [world(s)]");
				return true;
			}

			return processList(sender, "global_settings.world." + listType, items, action, "worlds");
		} catch (Exception e) {
			reporter.reportDetailed(this, Report.newBuilder(COMMAND_CONFIG_HANDLEWORLDCOMMAND).error(e));
			sender.sendMessage(ChatColor.DARK_RED + "An error occurred while processing the world command.");
			return true;
		}
	}

	private boolean handlePlayerHeadsCommand(CommandSender sender, String[] args) {
		try {
			if (args.length < 3) {
				sender.sendMessage(ChatColor.AQUA + "Usage: /mmh config head_settings.player_heads.<whitelist/blacklist> <add/remove> [name(s)]");
				return true;
			}

			String[] subCommandParts = args[0].toLowerCase().split("\\.");
			if (subCommandParts.length != 3 || !subCommandParts[0].equals("head_settings") || !subCommandParts[1].equals("player_heads")) {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid subcommand. Use: head_settings.player_heads.whitelist or head_settings.player_heads.blacklist");
				return true;
			}

			String listType = subCommandParts[2];
			if (!listType.equals("whitelist") && !listType.equals("blacklist")) {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid list type. Use: whitelist or blacklist");
				return true;
			}

			String action = args[1].toLowerCase();
			if (!action.equals("add") && !action.equals("remove")) {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid action. Use: add or remove");
				return true;
			}

			String itemsInput = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
			List<String> items = Arrays.stream(itemsInput.split(","))
					.map(String::trim)
					.filter(item -> !item.isEmpty())
					.collect(Collectors.toList());

			if (items.isEmpty()) {
				sender.sendMessage(ChatColor.DARK_RED + "No valid player names provided.");
				sender.sendMessage(ChatColor.AQUA + "Usage: /mmh config head_settings.player_heads." + listType + " " + action + " [name(s)]");
				return true;
			}

			return processList(sender, "head_settings.player_heads." + listType + ".player_head_" + listType, items, action, "player names");
		} catch (Exception e) {
			reporter.reportDetailed(this, Report.newBuilder(COMMAND_CONFIG_HANDLEPLAYERHEADSCOMMAND).error(e));
			sender.sendMessage(ChatColor.DARK_RED + "An error occurred while processing the player heads command.");
			return true;
		}
	}

	private boolean handleMobHeadsCommand(CommandSender sender, String[] args) {
		try {
			if (args.length < 3) {
				sender.sendMessage(ChatColor.AQUA + "Usage: /mmh config head_settings.mob_heads.<whitelist/blacklist> <add/remove> [mob(s)]");
				return true;
			}

			String[] subCommandParts = args[0].toLowerCase().split("\\.");
			if (subCommandParts.length != 3 || !subCommandParts[0].equals("head_settings") || !subCommandParts[1].equals("mob_heads")) {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid subcommand. Use: head_settings.mob_heads.whitelist or head_settings.mob_heads.blacklist");
				return true;
			}

			String listType = subCommandParts[2];
			if (!listType.equals("whitelist") && !listType.equals("blacklist")) {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid list type. Use: whitelist or blacklist");
				return true;
			}

			String action = args[1].toLowerCase();
			if (!action.equals("add") && !action.equals("remove")) {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid action. Use: add or remove");
				return true;
			}

			String itemsInput = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
			List<String> items = Arrays.stream(itemsInput.split(","))
					.map(String::trim)
					.filter(item -> !item.isEmpty())
					.collect(Collectors.toList());

			if (items.isEmpty()) {
				sender.sendMessage(ChatColor.DARK_RED + "No valid mob names provided.");
				sender.sendMessage(ChatColor.AQUA + "Usage: /mmh config head_settings.mob_heads." + listType + " " + action + " [mob(s)]");
				return true;
			}

			return processList(sender, "head_settings.mob_heads." + listType, items, action, "mob names");
		} catch (Exception e) {
			reporter.reportDetailed(this, Report.newBuilder(COMMAND_CONFIG_HANDLEMOBHEADSCOMMAND).error(e));
			sender.sendMessage(ChatColor.DARK_RED + "An error occurred while processing the mob heads command.");
			return true;
		}
	}

	private boolean processList(CommandSender sender, String configPath, List<String> items, String action, String itemType) {
		try {
			List<String> itemList = config.getStringList(configPath);
			// Replace "names_go_here" with empty list if present
			if ((itemList.size() == 1) && itemList.get(0).equals("names_go_here")) {
				itemList = new ArrayList<>();
			}

			List<String> modifiedItems = new ArrayList<>();
			List<String> skippedItems = new ArrayList<>();

			if (action.equals("add")) {
				for (String item : items) {
					if (!itemList.contains(item)) {
						itemList.add(item);
						modifiedItems.add(item);
					} else {
						skippedItems.add(item);
					}
				}
			} else { // remove
				for (String item : items) {
					if (itemList.contains(item)) {
						itemList.remove(item);
						modifiedItems.add(item);
					} else {
						skippedItems.add(item);
					}
				}
			}

			// Set to "names_go_here" if the list is empty after removal
			if (itemList.isEmpty() && action.equals("remove")) {
				itemList = Collections.singletonList("names_go_here");
			}

			// Save the updated list
			config.set(configPath, itemList);
			YmlConfiguration.saveConfig(configFile, config);
			if (!modifiedItems.isEmpty()) {
				sender.sendMessage(ChatColor.GREEN + (action.equals("add") ? "Added to " : "Removed from ") + itemType + ": " + String.join(", ", modifiedItems));
			}
			if (!skippedItems.isEmpty()) {
				sender.sendMessage(ChatColor.GOLD + "Skipped (" + (action.equals("add") ? "already in " : "not in ") + itemType + "): " + String.join(", ", skippedItems));
			}
			if (modifiedItems.isEmpty() && skippedItems.isEmpty()) {
				sender.sendMessage(ChatColor.AQUA + "No changes made to " + itemType + ".");
			}

			return true;
		} catch (Exception e) {
			reporter.reportDetailed(this, Report.newBuilder(COMMAND_CONFIG_PROCESSLIST).error(e));
			sender.sendMessage(ChatColor.DARK_RED + "Failed to process the " + itemType + " list.");
			mmh.logDebug("Failed to process " + configPath + ": " + e.getMessage());
			return true;
		}
    }
}