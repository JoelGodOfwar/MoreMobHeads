package com.github.joelgodofwar.mmh.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import lib.github.joelgodofwar.coreutils.CoreUtils;
import lib.github.joelgodofwar.coreutils.util.YmlConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.common.PluginLibrary;
import com.github.joelgodofwar.mmh.common.error.Report;
import com.github.joelgodofwar.mmh.handlers.MMHEventHandler;
import com.github.joelgodofwar.mmh.util.Utils;
import com.github.joelgodofwar.mmh.util.heads.HeadManager;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.jetbrains.annotations.NotNull;

/**
 * Handles the /mmh command and its subcommands for the MoreMobHeads plugin.
 */
@SuppressWarnings("unused")
public class Command_1_20_R2 implements CommandExecutor, TabCompleter {
	private final MoreMobHeads mmh;
	private final HeadManager headManager;
	private final GiveHeadCommand giveHeadCommand;
	ViewHeadsCommand viewHeadsCommand;
	ConfigGuiCommand configGuiCommand;
	ConfigCommand configCommand;
	ChanceCommand chanceCommand;
	private final MMHEventHandler eventHandler;

	public Command_1_20_R2(MoreMobHeads plugin, HeadManager headManager, GiveHeadCommand giveHeadCommand, ViewHeadsCommand viewHeadsCommand, MMHEventHandler eventHandler) {
		this.mmh = plugin;
		this.headManager = headManager;
		this.giveHeadCommand = giveHeadCommand;
		this.viewHeadsCommand = viewHeadsCommand;
		this.eventHandler = eventHandler;
		this.configGuiCommand = new ConfigGuiCommand(mmh);
		this.configCommand = new ConfigCommand(mmh);
		this.chanceCommand = new ChanceCommand(plugin, headManager, eventHandler);
	}

	/**
	 * Executes the /mmh command, delegating to subcommand handlers based on arguments.
	 * <p>
	 * Supports subcommands such as "chance", "give", "view", "config", and others.
	 * Checks permissions and provides usage messages for invalid inputs.
	 *
	 * @param sender  The sender of the command.
	 * @param command The command being executed.
	 * @param label   The command label used (e.g., "mmh").
	 * @param args    The command arguments.
	 * @return true if the command was processed successfully, false otherwise.
	 */
	@Override
	@SuppressWarnings({ "static-access", "deprecation" })
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) { // TODO: Commands
		try {
			// log("command=" + command.getName() + " args=" + args[0] + args[1]);
			if (command.getName().equalsIgnoreCase("MMH")) {
				try { // REPORT_COMMAND_MENU_ERROR "Error displaying Command Menu."
					if (args.length == 0) {
						sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
						+ ChatColor.GREEN + "]===============[]");
						sender.sendMessage(ChatColor.WHITE + " ");// https://ko-fi.com/joelgodofwar
						sender.sendMessage(
								ChatColor.WHITE + " " + mmh.get("mmh.version.donate") + ": https://ko-fi.com/joelgodofwar");// https://ko-fi.com/joelgodofwar
						sender.sendMessage(ChatColor.WHITE + " ");// https://ko-fi.com/joelgodofwar
						sender.sendMessage(
								ChatColor.WHITE + " /mmh reload - " + mmh.get("mmh.command.reload", "Reloads this plugin."));// subject
						// to
						// server
						// admin
						// approval");
						sender.sendMessage(ChatColor.WHITE + " /mmh toggledebug - " + mmh.get("mmh.command.debuguse", "Temporarily toggles debug."));
						if (mmh.config.getBoolean("wandering_trades.custom_wandering_trader", true)) {
							sender.sendMessage(ChatColor.WHITE + " /mmh playerheads - " + mmh.get("mmh.command.playerheads", "Shows how to use the playerheads commands"));
							sender.sendMessage(ChatColor.WHITE + " /mmh customtrader - " + mmh.get("mmh.command.customtrader", "Shows how to use the customtrader commands"));
						}
						sender.sendMessage(ChatColor.WHITE + " /mmh givehead - " + mmh.get("mmh.command.givehead", "Opens a GUI to give a head to a player."));
						sender.sendMessage(ChatColor.WHITE + " /mmh viewheads - " + mmh.get("mmh.command.viewheads", "Opens a GUI to view available heads."));
						sender.sendMessage(ChatColor.WHITE + " /mmh display perms/vars - " + mmh.get("mmh.command.display.help"));
						sender.sendMessage(ChatColor.WHITE + " ");
						sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
						+ ChatColor.GREEN + "]===============[]");
						return true;
					}
				}catch (Exception exception) {
					mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_MENU_ERROR).error(exception));
				}
				if ( args[0].equalsIgnoreCase("givehead") || args[0].equalsIgnoreCase("GH") ) {
					if (!(sender instanceof Player)) {
						sender.sendMessage("This command can only be used by players.");
						return true;
					}
					Player player = (Player) sender;
					if (!player.hasPermission("moremobheads.give")) {
						player.sendMessage("§cYou do not have permission to use this command.");
						return true;
					}
					giveHeadCommand.execute(player);
				}
				if ( args[0].equalsIgnoreCase("viewHeads") || args[0].equalsIgnoreCase("VH") ) {
					if (!(sender instanceof Player)) {
						sender.sendMessage("This command can only be used by players.");
						return true;
					}
					Player player = (Player) sender;
					if (!player.hasPermission("moremobheads.viewheads")) { // Will likely set this as True, since it's only viewing not giving heads
						player.sendMessage("§cYou do not have permission to use this command.");
						return true;
					}
					viewHeadsCommand.execute(player);
					return true;
				}
				if (args[0].equalsIgnoreCase("config")) {
					if (args.length == 1) {
						if (!(sender instanceof Player)) {
							sender.sendMessage(ChatColor.DARK_RED + "The configuration GUI is only for players.");
							sender.sendMessage(ChatColor.AQUA + "Use /mmh config <subcommand> [args], e.g., /mmh config world.whitelist add world_nether");
							return true;
						}
						Player player = (Player) sender;
						if (!player.hasPermission("moremobheads.config")) {
							player.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
							return true;
						}
						configGuiCommand.execute(player);
						return true;
					} else {
						return configCommand.execute(sender, args);
					}
				}
				if (args[0].equalsIgnoreCase("chance")) {
					if (args.length == 1) {
						if (!(sender instanceof ConsoleCommandSender) && !sender.hasPermission("moremobheads.config")) {
							sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
							return true;
						}
						sender.sendMessage(ChatColor.AQUA + "Use /mmh chance <export/import/set>, e.g., /mmh chance export, /mmh chance set <langName> <percent>");
						return true;
					} else {
						return chanceCommand.execute(sender, args);
					}
				}
				if (args[0].equalsIgnoreCase("headNBT")) {
					try { // REPORT_COMMAND_HEADNBT_ERROR "Error executing HeadNBT command."
						if (!(sender instanceof Player)) {
							return true;
						}
						Player player = (Player) sender;
						ItemStack mainHand = player.getInventory().getItemInMainHand();
						ItemStack offHand = player.getInventory().getItemInOffHand();
						if ((mainHand != null) && mainHand.getType().equals(Material.PLAYER_HEAD)) {
							NBTItem item = new NBTItem(mainHand);
							mmh.LOGGER.log("" + item);
							player.sendMessage("" + item);
						} else if ((offHand != null) && offHand.getType().equals(Material.PLAYER_HEAD)) {
							NBTItem item = new NBTItem(offHand);
							player.sendMessage("" + item);
							mmh.LOGGER.log("" + item);
						} else {
							// log(Level.INFO,"You do not have a head in either hand.");
							player.sendMessage("" + mmh.get("mmh.command.headnbt"));
						}
					}catch(Exception exception) {
						mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_HEADNBT_ERROR).error(exception));
					}
				}
				// /mmh display permvar playername
				// / 0 1 2
				if (args[0].equalsIgnoreCase("display")) {
					if (args[1].equalsIgnoreCase("perms") || args[1].equalsIgnoreCase("permissions")) {
						try { // REPORT_COMMAND_DISPLAY_PERMS "Error executing Display Perms command."
							if (sender instanceof Player) {
								Player player = (Player) sender;
								sender.sendMessage(mmh.get("mmh.command.display.you").replace("<player>", player.getDisplayName()));
								sender.sendMessage("moremobheads.players=" + player.hasPermission("moremobheads.players"));
								sender.sendMessage("moremobheads.mobs=" + player.hasPermission("moremobheads.mobs"));
								sender.sendMessage("moremobheads.nametag=" + player.hasPermission("moremobheads.nametag"));
								sender.sendMessage("moremobheads.reload=" + player.hasPermission("moremobheads.reload"));
								sender.sendMessage("moremobheads.toggledebug=" + player.hasPermission("moremobheads.toggledebug"));
								sender.sendMessage("moremobheads.showUpdateAvailable=" + player.hasPermission("moremobheads.showUpdateAvailable"));
								sender.sendMessage("moremobheads.customtrader=" + player.hasPermission("moremobheads.customtrader"));
								sender.sendMessage("moremobheads.playerheads=" + player.hasPermission("moremobheads.playerheads"));
								sender.sendMessage("moremobheads.blockheads=" + player.hasPermission("moremobheads.blockheads"));
								sender.sendMessage("moremobheads.give=" + player.hasPermission("moremobheads.give"));
								sender.sendMessage("moremobheads.viewheads=" + player.hasPermission("moremobheads.viewheads"));
								sender.sendMessage("moremobheads.config=" + player.hasPermission("moremobheads.config"));
								sender.sendMessage(mmh.getName() + " " + mmh.getDescription().getVersion() + " display perms end");
							} else if (args.length >= 2) {
								Player player = sender.getServer().getPlayer(args[2]);
								sender.sendMessage(mmh.get("mmh.command.display.them").replace("<player>", player.getDisplayName()));
								sender.sendMessage("moremobheads.players=" + player.hasPermission("moremobheads.players"));
								sender.sendMessage("moremobheads.mobs=" + player.hasPermission("moremobheads.mobs"));
								sender.sendMessage("moremobheads.nametag=" + player.hasPermission("moremobheads.nametag"));
								sender.sendMessage("moremobheads.reload=" + player.hasPermission("moremobheads.reload"));
								sender.sendMessage("moremobheads.toggledebug=" + player.hasPermission("moremobheads.toggledebug"));
								sender.sendMessage("moremobheads.showUpdateAvailable=" + player.hasPermission("moremobheads.showUpdateAvailable"));
								sender.sendMessage("moremobheads.customtrader=" + player.hasPermission("moremobheads.customtrader"));
								sender.sendMessage("moremobheads.playerheads=" + player.hasPermission("moremobheads.playerheads"));
								sender.sendMessage("moremobheads.blockheads=" + player.hasPermission("moremobheads.blockheads"));
								sender.sendMessage("moremobheads.give=" + player.hasPermission("moremobheads.give"));
								sender.sendMessage("moremobheads.viewheads=" + player.hasPermission("moremobheads.viewheads"));
								sender.sendMessage("moremobheads.config=" + player.hasPermission("moremobheads.config"));
								sender.sendMessage(mmh.getName() + " " + mmh.getDescription().getVersion() + " display perms end");
							} else {
								sender.sendMessage("Console can only check permissions of Players.");
							}
						}catch (Exception exception) {
							mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_DISPLAY_PERMS).error(exception));
						}
					} else if (args[1].equalsIgnoreCase("vars") || args[1].equalsIgnoreCase("variables")) {
						try { // REPORT_COMMAND_DISPLAY_VARS "Error executing Display Vars command."
							sender.sendMessage(mmh.getName() + " " + mmh.getDescription().getVersion() + " display varss start");
							sender.sendMessage("debug=" + CoreUtils.debug);
							sender.sendMessage("daLang=" + MoreMobHeads.daLang);

							mmh.world_whitelist = mmh.config.getString("global_settings.world.whitelist", "");
							mmh.world_blacklist = mmh.config.getString("global_settings.world.blacklist", "");
							mmh.mob_whitelist = mmh.config.getString("head_settings.mob_heads.whitelist", "");
							mmh.mob_blacklist = mmh.config.getString("head_settings.mob_heads.blacklist", "");

							sender.sendMessage("world_whitelist=" + mmh.world_whitelist);
							sender.sendMessage("world_blacklist=" + mmh.world_blacklist);
							sender.sendMessage("mob_whitelist=" + mmh.mob_whitelist);
							sender.sendMessage("mob_blacklist=" + mmh.mob_blacklist);
							sender.sendMessage(mmh.getName() + " " + mmh.getDescription().getVersion() + " display varss end");
						}catch (Exception exception) {
							mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_DISPLAY_VARS).error(exception));
						}
					} else if (args[1].equalsIgnoreCase("chance") || args[1].equalsIgnoreCase("chance_percent")) {
						try { // REPORT_COMMAND_DISPLAY_CHANCE "Error executing Display Chance command."
							ConfigurationSection cs = mmh.chanceConfig.getConfigurationSection("chance_percent");
							List<String> daSet = new ArrayList<String>();
							// log(Level.INFO, "args.lngth=" + args.length);
							if (args.length == 3) {
                                assert cs != null;
                                for (String key : cs.getKeys(true)) {
									if (key.contains(args[2])) {
										sender.sendMessage(key + "=" + cs.get(key));
										daSet.add(key + "=" + cs.get(key));
									}
								}

								if (!daSet.isEmpty()) {
									File chanceFile = new File(
											mmh.getDataFolder() + "" + File.separatorChar + "logs" + File.separatorChar,
											"chance_dump" + ".log");
									PrintWriter pw = null;
									try {
										pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(chanceFile), StandardCharsets.UTF_8));
										for (String s : daSet) {
											pw.println(s);
										}
										pw.flush();
									} catch (Exception exception) {
										MoreMobHeads.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLUGIN_UNKNOWN_ERROR).error(exception));
									} finally {
                                        assert pw != null;
                                        pw.close();
									}
									sender.sendMessage("chance_config.yml has been dumped into " + chanceFile.toString());
									mmh.LOGGER.log("chance_config.yml has been dumped into " + chanceFile.toString());
								} else {
									sender.sendMessage(args[2] + " was not found in chance_percent.yml");
								}
							} else {
                                assert cs != null;
                                for (String key : cs.getKeys(true)) {
									if (!Objects.requireNonNull(cs.get(key)).toString().contains("MemorySection")) {
										sender.sendMessage(key + "=" + Objects.requireNonNull(cs.get(key)).toString());
										daSet.add(key + "=" + Objects.requireNonNull(cs.get(key)).toString());
									}
								}
								if (!daSet.isEmpty()) {
									File chanceFile = new File(
											mmh.getDataFolder() + "" + File.separatorChar + "logs" + File.separatorChar,
											"chance_dump" + ".log");
									PrintWriter pw = null;
									try {
										pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(chanceFile), "UTF-8"));
										for (String s : daSet) {
											pw.println(s);
										}
										pw.flush();
									} catch (Exception exception) {
										MoreMobHeads.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLUGIN_UNKNOWN_ERROR).error(exception));
									} finally {
										pw.close();
									}
									sender.sendMessage("chance_config.yml has been dumped into " + chanceFile.toString());
									mmh.LOGGER.log("chance_config.yml has been dumped into " + chanceFile.toString());
								} else {
									sender.sendMessage("Error dumping chance_percent.yml");
								}
							}
							// chanceConfig
						}catch (Exception exception) {
							mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_DISPLAY_CHANCE).error(exception));
						}
					} else if ( args[1].equalsIgnoreCase("config") ) {
						mmh.logDebug("Config.yml DUMP - INCLUDE THIS WITH ANY ISSUE REPORT VVV");
						mmh.dumpConfig(mmh.getConfig());
						mmh.logDebug("Config.yml DUMP - INCLUDE THIS WITH ANY ISSUE REPORT ^^^");
					}
				}
				// Reload subcommand
				if (args[0].equalsIgnoreCase("reload")) {
					try { // REPORT_COMMAND_RELOAD_ERROR "Error executing Reload command."
						String perm = "moremobheads.reload";
						boolean hasPerm = sender.hasPermission(perm) || !(sender instanceof Player);
						mmh.logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
						if (hasPerm || mmh.isDev || !(sender instanceof Player)) {
							mmh.LOGGER.log("Reloading MoreMobHeads...");
							long startTime = System.currentTimeMillis();

							// Save file versions
							saveFileVersions();

							// Reload file versions
							mmh.LOGGER.log("Loading file version checker...");
							mmh.fileVersionsFile = new File(mmh.getDataFolder() + "" + File.separatorChar + "fileVersions.yml");
							try {
								mmh.fileVersions.load(mmh.fileVersionsFile);
							} catch (Exception exception) {
								mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_FILEVERSION).error(exception));
							}

							// Reload configs
							mmh.config = new YmlConfiguration();
							mmh.beheadingMessages = new YamlConfiguration();
							try {
								mmh.beheadingMessages.load(new File(mmh.getDataFolder() + "" + File.separatorChar + "messages.yml"));
							} catch (Exception exception) {
								mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_MESSAGES_LOAD_ERROR).error(exception));
							}

							// Check directories and update configs
							mmh.checkDirectories();
							mmh.checkConfig();
							mmh.checkMessages();
							mmh.checkLang();

							// Update settings
							mmh.world_whitelist = mmh.config.getString("global_settings.world.whitelist", "");
							mmh.world_blacklist = mmh.config.getString("global_settings.world.blacklist", "");
							mmh.mob_whitelist = mmh.config.getString("head_settings.mob_heads.whitelist", "");
							mmh.mob_blacklist = mmh.config.getString("head_settings.mob_heads.blacklist", "");
							CoreUtils.debug = mmh.config.getBoolean("debug", false); // Update debug from config

							// Reload heads and recipes (including DLCs and stonecutter recipes)
							eventHandler.loadHeadsAndRecipes();

							mmh.LOGGER.log("MoreMobHeads reload took " + mmh.LoadTime(startTime) + " to complete");
							sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.GREEN + " " + mmh.get("mmh.message.reloaded"));
							return true;
						} else {
							sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
									+ mmh.get("mmh.message.noperm").replace("<perm>", perm));
							return false;
						}
					} catch (Exception exception) {
						mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_RELOAD_ERROR).error(exception));
						return false;
					}
				}
				if (args[0].equalsIgnoreCase("toggledebug") || args[0].equalsIgnoreCase("td")) {
					try { // REPORT_COMMAND_TOGGLE_DEBUG "Error executing ToggleDebug Command."
						if (mmh.buildValidator.isFinalWeek()) {
							sender.sendMessage("§cDebug mode is locked on during the final week!");
							return true;
						}
						String perm = "moremobheads.toggledebug";
						if(!sender.hasPermission(perm)){
							sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.get("mmh.message.noperm").replace("<perm>", perm));
							return false;
						}
						mmh.logDebug(sender.getName() + " has the permission " + perm + "=" + perm);
						CoreUtils.debug = !CoreUtils.debug;
                        sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.get("mmh.message.debugtrue").replace("boolean", "" + CoreUtils.debug));
						return true;
					}catch (Exception exception) {
						mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_TOGGLE_DEBUG).error(exception));
					}
				}
				if (args[0].equalsIgnoreCase("customtrader") || args[0].equalsIgnoreCase("ct")) {
					try { // REPORT_COMMAND_CUSTOM_TRADER "Error executing CustomTrader Command."
						String perm = "moremobheads.customtrader";
						boolean hasPerm = sender.hasPermission(perm);
						mmh.logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
						if ( (hasPerm || mmh.isDev) && (sender instanceof Player)
								&& mmh.config.getBoolean("wandering_trades.custom_wandering_trader", true)) {
							mmh.LOGGER.log("has permission");
							Player player = (Player) sender;
							if (!(args.length >= 2)) {
								sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
								+ ChatColor.GREEN + "]===============[]");
								sender.sendMessage(ChatColor.WHITE + " ");
								sender.sendMessage(ChatColor.WHITE + " /mmh ct - " + mmh.get("mmh.command.ct.help"));
								sender.sendMessage(ChatColor.WHITE + " /mmh ct add - " + mmh.get("mmh.command.ct.add")
								+ "custom_trades.yml");
								sender.sendMessage(ChatColor.WHITE + " /mmh ct remove # - " + mmh.get("mmh.command.ct.remove"));
								sender.sendMessage(ChatColor.WHITE + " /mmh ct replace # - "
										+ mmh.get("mmh.command.ct.replace").replace("<num>", "#"));
								sender.sendMessage(ChatColor.WHITE + " ");
								sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
								+ ChatColor.GREEN + "]===============[]");
								return true;
							} else if (args[1].equalsIgnoreCase("add")) {
								mmh.logDebug("CMD CT ADD Start -----");
								ItemStack itemstack = player.getInventory().getItemInOffHand();
								ItemStack price1 = player.getInventory().getItem(0);
								ItemStack price2 = player.getInventory().getItem(1);
								if (price1 == null) {
									price1 = new ItemStack(Material.AIR);
								}
								if (price2 == null) {
									price2 = new ItemStack(Material.AIR);
								}
								if(itemstack.getType() == Material.PLAYER_HEAD) {
									sender.sendMessage("Player Heads must be added manually.");
									return false;
								}
								// Material price1 = item1.getType();
								// Material price2 = item2.getType();

								if (itemstack.getType() == Material.AIR || price1.getType() == Material.AIR) {
									mmh.LOGGER.log("error air");
									sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
									+ ChatColor.GREEN + "]===============[]");
									sender.sendMessage(ChatColor.WHITE + " ");
									sender.sendMessage(
											ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line1") + "custom_trades.yml");
									sender.sendMessage(ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line2"));
									sender.sendMessage(ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line3"));
									sender.sendMessage(ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line4") + "/mmh ct add");
									sender.sendMessage(
											ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line5") + "custom trade.");
									sender.sendMessage(ChatColor.WHITE + " ");
									sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
									+ ChatColor.GREEN + "]===============[]");
									mmh.logDebug("CMD CT ADD End Error -----");
									return false;
								}
								int tradeNumber = (int) mmh.traderCustom.get("custom_trades.number", 1);
								mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".price_1", price1);
								mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".price_2", price2);
								mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".itemstack", itemstack);
								/** Code to fix missing noteblock SkullMeta */
								boolean doIt = Utils.isSupportedVersion("1.20.2.3936");
								if(doIt) {
									mmh.LOGGER.log("CT A doIt=" + doIt);
									if(itemstack.getType().equals(Material.PLAYER_HEAD)) {
										SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
                                        assert meta != null;
                                        NamespacedKey sound = meta.getNoteBlockSound();
										if(sound != null) {
											mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".note_block_sound", sound.getKey());
											mmh.logDebug("sound.getKey()=" + sound.getNamespace());
											mmh.logDebug("sound.getKey()=" + sound.getKey());
										}
									}
								}
								//** Code to fix missing noteblock SkullMeta */
								mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".quantity",
										itemstack.getAmount());
								mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".chance", 0.002);
								mmh.traderCustom.set("custom_trades.number", (tradeNumber + 1));
								mmh.logDebug("CMD CT ADD price1=" + price1.getType());
								mmh.logDebug("CMD CT ADD price2=" + price2.getType());
								mmh.logDebug("CMD CT ADD itemstack=" + itemstack.getType());
								if (itemstack.getType() == Material.PLAYER_HEAD) {
									ItemMeta skullMeta = itemstack.getItemMeta();
                                    assert skullMeta != null;
                                    mmh.logDebug("CMD CT ADD IS DisplayName=" + skullMeta.getDisplayName());
									if (skullMeta.hasLore()) {
										mmh.logDebug("CMD CT ADD IS lore=" + String.join(",", Objects.requireNonNull(skullMeta.getLore())));

									}
								}
								mmh.logDebug("CMD CT ADD quantity=" + itemstack.getAmount());
								mmh.logDebug("CMD CT ADD chance=0.002");
								// log("customFile=" + customFile);
								try {
									mmh.traderCustom.save(mmh.customFile);
									mmh.traderCustom.load(mmh.customFile);
								} catch (Exception exception) {
									MoreMobHeads.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLUGIN_UNKNOWN_ERROR).error(exception));
								}
								mmh.logDebug("CMD CT ADD End -----");
								sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " trade_"
										+ (tradeNumber + 1) + " " + mmh.get("mmh.message.ct.successadd"));
								return true;
							} else if (args[1].equalsIgnoreCase("remove")) {
								mmh.logDebug("CMD CT Remove Start -----");
								if (!(args.length >= 3)) {
									sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
											+ mmh.get("mmh.command.ct.argument"));
									return false;
								} else if (mmh.isInteger(args[2])) {
									mmh.traderCustom.set("custom_trades.trade_" + args[2] + ".price_1", "");
									mmh.traderCustom.set("custom_trades.trade_" + args[2] + ".price_2", "");
									mmh.traderCustom.set("custom_trades.trade_" + args[2] + ".itemstack", "");
									mmh.traderCustom.set("custom_trades.trade_" + args[2] + ".quantity", "");
									mmh.traderCustom.set("custom_trades.trade_" + args[2] + ".chance", "");
									mmh.logDebug("customFile=" + mmh.customFile);
									try {
										mmh.traderCustom.save(mmh.customFile);
										mmh.traderCustom.load(mmh.customFile);
									} catch (Exception exception) {
										MoreMobHeads.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLUGIN_UNKNOWN_ERROR).error(exception));

										mmh.logDebug("CMD CT Remove End Exception -----");

										sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
												+ mmh.get("mmh.command.ct.error"));
										return false;
										// e.printStackTrace();
									}
									mmh.logDebug("CMD CT Remove End -----");
									sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " trade_"
											+ args[2] + " " + mmh.get("mmh.message.ct.successrem"));
									return true;
								} else {
									mmh.logDebug("CMD CT Remove End 2 -----");
									sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
											+ mmh.get("mmh.command.ct.numberreq"));
									return false;
								}
							} else if (args[1].equalsIgnoreCase("replace")) {
								mmh.logDebug("CMD CT Replace Start -----");
								if (args.length != 3) {
									sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
											+ mmh.get("mmh.command.ct.argument"));
									return false;
								} else if (mmh.isInteger(args[2])) {
									ItemStack itemstack = player.getInventory().getItemInOffHand();
									ItemStack price1 = player.getInventory().getItem(0);
									ItemStack price2 = player.getInventory().getItem(1);
									if (price1 == null) {
										price1 = new ItemStack(Material.AIR);
									}
									if (price2 == null) {
										price2 = new ItemStack(Material.AIR);
									}
									if (itemstack.getType() == Material.AIR || price1.getType() == Material.AIR) {
										sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW
												+ mmh.getName() + ChatColor.GREEN + "]===============[]");
										sender.sendMessage(ChatColor.WHITE + " ");
										sender.sendMessage(ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line1")
										+ "custom_trades.yml");
										sender.sendMessage(ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line2"));
										sender.sendMessage(ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line3"));
										sender.sendMessage(
												ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line4") + "/mmh ct add");
										sender.sendMessage(
												ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line5") + "custom trade.");
										sender.sendMessage(ChatColor.WHITE + " ");
										sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW
												+ mmh.getName() + ChatColor.GREEN + "]===============[]");
										mmh.logDebug("CMD CT Replace End Error -----");
										return false;
									}
									int tradeNumber = Integer.parseInt(args[2]);
									mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".price_1", price1);
									mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".price_2", price2);
									mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".itemstack", itemstack);
									//** Code to fix missing noteblock SkullMeta */
									boolean doIt = Utils.isSupportedVersion("1.20.2.3936");
									if(doIt) {
										mmh.LOGGER.log("CT A doIt=" + doIt);
										if(itemstack.getType().equals(Material.PLAYER_HEAD)) {
											SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
                                            assert meta != null;
                                            NamespacedKey sound = meta.getNoteBlockSound();
                                            assert sound != null;
                                            mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".note_block_sound", sound.getKey());
											mmh.logDebug("sound.getKey()=" + sound.getNamespace());
											mmh.logDebug("sound.getKey()=" + sound.getKey());
										}
									}
									//** Code to fix missing noteblock SkullMeta */
									mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".quantity",
											itemstack.getAmount());
									mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".chance", 0.002);
									mmh.logDebug("CMD CT Replace price1=" + price1.getType());
									mmh.logDebug("CMD CT Replace price2=" + price2.getType());
									mmh.logDebug("CMD CT Replace itemstack=" + itemstack.getType());
									if (itemstack.getType() == Material.PLAYER_HEAD) {
										ItemMeta skullMeta = itemstack.getItemMeta();
                                        assert skullMeta != null;
                                        mmh.logDebug("CMD CT Replace IS DisplayName=" + skullMeta.getDisplayName());
										if (skullMeta.hasLore()) {
											mmh.logDebug("CMD CT Replace IS lore=" + String.join(",", Objects.requireNonNull(skullMeta.getLore())));
										}
										mmh.logDebug("CMD CT Replace quantity=" + itemstack.getAmount());
										mmh.logDebug("CMD CT Replace chance=0.002");
										// log("customFile=" + customFile);
										try {
											mmh.traderCustom.save(mmh.customFile);
											mmh.traderCustom.load(mmh.customFile);
										} catch (Exception exception) {
											MoreMobHeads.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLUGIN_UNKNOWN_ERROR).error(exception));
											mmh.logDebug("CMD CT Replace End Exception -----");
											sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
													+ mmh.get("mmh.command.ct.error"));
											return false;
											// e.printStackTrace();
										}
										mmh.logDebug("CMD CT Replace End -----");
										sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " trade_"
												+ args[2] + " " + mmh.get("mmh.message.ct.successrep"));
										return true;
									} else {
										mmh.logDebug("CMD CT Replace End 2 -----");
										sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
												+ mmh.get("mmh.command.ct.numberreq"));
										return false;
									}
								}
							}
						} else if (!(sender instanceof Player)) {
							mmh.logDebug("CMD CT Replace End Console -----");
							sender.sendMessage(
									ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.get("mmh.message.noconsole"));
							return false;
						} else if (!hasPerm) {
							mmh.logDebug("CMD CT Replace End !Perm -----");
							sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
									+ mmh.get("mmh.message.nopermordisabled").replace("<perm>", perm));
							return false;
						}

					}catch (Exception exception) {
						mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_CUSTOM_TRADER).error(exception));
					}
				}


				if (args[0].equalsIgnoreCase("dev")) {
					try { // REPORT_COMMAND_DEV_ERROR "Error exuting dev Command."
						if (sender instanceof Player) {
							Player player = (Player) sender;
							if (player.getName().equalsIgnoreCase("JoelGodOfWar") || player.getName().equalsIgnoreCase("JoelYahwehOfWar")) {
								mmh.isDev = !mmh.isDev;
								player.sendMessage("You have toggled isDev to " + mmh.isDev);

								// test code for CoreUtils
								mmh.coreUtils.sendJsonMessage(player, mmh.coreUtils.fixColors("&6Server Type: &e" + mmh.coreUtils.getServerTypeName()));
								//sender.sendMessage(mmh.coreUtils.fixColors("&6Server Type: &e" + mmh.coreUtils.getServerTypeName()));
								mmh.coreUtils.getNicknameAsync(mmh, player, true, nickname -> {
									mmh.coreUtils.sendJsonMessage(player, mmh.coreUtils.fixColors("&6Nickname: &e" + nickname));
								});
								// End test code
								return true;
							} else {
								player.sendMessage("You are not the developer.");
								return false;
							}
						}
					}catch (Exception exception) {
						mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_DEV_ERROR).error(exception));
					}
				}
			}
		}catch(Exception exception) {
			mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.UNHANDLED_COMMAND_ERROR).error(exception));
			// ERROR_RUNNING_DRAGON_DEATH_COMMAND "Error running command after dragon death."
		}
		return false;
	}


	/**
	 * Provides tab completion suggestions for the /mmh command.
	 * <p>
	 * Suggests subcommands like "chance", "give", "view", "config", etc., for the first argument,
	 * and further completions for subcommands (e.g., "create" and "apply" for /mmh chance)
	 * based on permissions and input.
	 *
	 * @param sender  The sender requesting tab completion.
	 * @param command The command being completed.
	 * @param alias   The command alias used (e.g., "mmh").
	 * @param args    The current arguments entered.
	 * @return A list of possible completions, or null if none apply.
	 */
	@Override
	@SuppressWarnings("static-access")
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { // TODO: Tab Complete
		try { // REPORT_TAB_COMPLETE_ERROR "Error parsing Tab Complete."
			if (command.getName().equalsIgnoreCase("mmh")) {
				List<String> autoCompletes = new ArrayList<>(); // create a new string list for tab completion
				if (args.length == 1) { // reload, toggledebug, playerheads, customtrader, headfix
					autoCompletes.add("reload");
					autoCompletes.add("toggledebug");
					autoCompletes.add("customtrader");
					autoCompletes.add("givehead");
					autoCompletes.add("viewheads");
					autoCompletes.add("display");
					if(sender.hasPermission("moremobheads.config")) {
						autoCompletes.add("chance");
						autoCompletes.add("config");
					}
					return filterCompletions(autoCompletes, args[0]);
				}
				if (args.length > 1) {
					if (args[0].equalsIgnoreCase("display")) {
						if (args.length == 2) {
							autoCompletes.add("permissions");
							autoCompletes.add("variables");
							return filterCompletions(autoCompletes, args[1]);
						} else if (args[1].equalsIgnoreCase("permissions")) {
							return null;
						}
					}

					if (args[0].equalsIgnoreCase("customtrader") || args[0].equalsIgnoreCase("ct")) {
						if (args.length == 2) {
							autoCompletes.add("add");
							autoCompletes.add("remove");
							autoCompletes.add("replace");
							return filterCompletions(autoCompletes, args[1]);
						} else if (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("replace")) {
							autoCompletes.add("0");
							return filterCompletions(autoCompletes, args[2]);
						}
					}

					if (args[0].equalsIgnoreCase("chance") && (sender instanceof ConsoleCommandSender || sender.hasPermission("moremobheads.config"))) {
						if (args.length == 2) {
							autoCompletes.add("export");
							autoCompletes.add("import");
							autoCompletes.add("set");
							return filterCompletions(autoCompletes, args[1]);
						} else if (args[1].equalsIgnoreCase("set")) {
							if (args.length == 3) {
								autoCompletes.addAll(headManager.loadedMobHeads().keySet());
								autoCompletes.add("player");
								// Add <fileName>.default suggestions
								File headsFolder = new File(mmh.getDataFolder(), "heads/entity");
								if (headsFolder.exists()) {
									File[] files = headsFolder.listFiles((dir, name) -> name.endsWith(".json") && !name.equals("player.json"));
									if (files != null) {
										for (File file : files) {
											String fileName = file.getName().replace(".json", "");
											autoCompletes.add(fileName + ".default");
										}
									}
								}
								return filterCompletions(autoCompletes, args[2]);
							}
						}
					}

					if (args[0].equalsIgnoreCase("config") && sender.hasPermission("moremobheads.config")) {
						if (args.length == 2) {
							autoCompletes.add("world");
							autoCompletes.add("head_settings");
							return filterCompletions(autoCompletes, args[1]);
						} else if (args[1].toLowerCase().startsWith("world")) {
							if (args.length == 3) {
								autoCompletes.add("whitelist");
								autoCompletes.add("blacklist");
								return filterCompletions(autoCompletes, args[2]);
							} else if (args[2].equalsIgnoreCase("whitelist") || args[2].equalsIgnoreCase("blacklist")) {
								if (args.length == 4) {
									autoCompletes.add("add");
									autoCompletes.add("remove");
									return filterCompletions(autoCompletes, args[3]);
								} else if (args[3].equalsIgnoreCase("add") || args[3].equalsIgnoreCase("remove")) {
									// Suggest world names
									List<String> worlds = Bukkit.getWorlds().stream()
											.map(World::getName)
											.collect(Collectors.toList());
									return filterCompletions(worlds, args[args.length - 1]);
								}
							}
						} else if (args[1].toLowerCase().startsWith("head_settings")) {
							if (args.length == 3) {
								autoCompletes.add("player_heads");
								autoCompletes.add("mob_heads");
								return filterCompletions(autoCompletes, args[2]);
							} else if (args[2].equalsIgnoreCase("player_heads")) {
								if (args.length == 4) {
									autoCompletes.add("whitelist");
									autoCompletes.add("blacklist");
									return filterCompletions(autoCompletes, args[3]);
								} else if (args[3].equalsIgnoreCase("whitelist") || args[3].equalsIgnoreCase("blacklist")) {
									if (args.length == 5) {
										autoCompletes.add("add");
										autoCompletes.add("remove");
										return filterCompletions(autoCompletes, args[4]);
									} else if (args[4].equalsIgnoreCase("add") || args[4].equalsIgnoreCase("remove")) {
										// Suggest online player names
										List<String> players = Bukkit.getOnlinePlayers().stream()
												.map(Player::getName)
												.collect(Collectors.toList());
										return filterCompletions(players, args[args.length - 1]);
									}
								}
							} else if (args[2].equalsIgnoreCase("mob_heads")) {
								if (args.length == 4) {
									autoCompletes.add("whitelist");
									autoCompletes.add("blacklist");
									return filterCompletions(autoCompletes, args[3]);
								} else if (args[3].equalsIgnoreCase("whitelist") || args[3].equalsIgnoreCase("blacklist")) {
									if (args.length == 5) {
										autoCompletes.add("add");
										autoCompletes.add("remove");
										return filterCompletions(autoCompletes, args[4]);
									} else if (args[4].equalsIgnoreCase("add") || args[4].equalsIgnoreCase("remove")) {
										// Suggest mob names (filtered to relevant mobs)
										List<String> mobs = Arrays.stream(EntityType.values())
												.filter(EntityType::isAlive)
												.filter(e -> !e.equals(EntityType.PLAYER)) // Exclude player
												.map(e -> e.name().toLowerCase())
												.collect(Collectors.toList());
										return filterCompletions(mobs, args[args.length - 1]);
									}
								}
							}
						}
					}
				}
			}
		}catch (Exception exception) {
			mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_TAB_COMPLETE_ERROR).error(exception));
		}
		return null;
	}

	/**
	 * Filters tab completion suggestions based on the user's input.
	 * <p>
	 * Returns a list of completions that start with the provided argument (case-insensitive).
	 * If the input is null or empty, returns all completions.
	 *
	 * @param completions The list of possible completions.
	 * @param arg         The user's current input to filter against.
	 * @return A filtered list of completions matching the input.
	 */
	private List<String> filterCompletions(List<String> completions, String arg) {
		if ((arg == null) || arg.isEmpty()) {
			return completions;
		}
		return completions.stream()
				.filter(c -> c.toLowerCase().startsWith(arg.toLowerCase()))
				.collect(Collectors.toList());
	}

	@SuppressWarnings("static-access")
	public void saveFileVersions() {
		String defVer = "0.1.0";
		mmh.fileVersions.set("config", mmh.config.getString("version", defVer));
		if(mmh.beheadingMessages != null) {
			mmh.fileVersions.set("messages", mmh.beheadingMessages.getString("version", defVer));
		}
		if(mmh.playerHeads != null) {
			mmh.fileVersions.set("player_heads", mmh.playerHeads.getString("version", defVer));
		}
		if(mmh.traderCustom != null) {
			mmh.fileVersions.set("custom_trades", mmh.traderCustom.getString("custom_trades.version", defVer));
		}
		mmh.fileVersions.set("lang", mmh.langName.getString("version", defVer));
		mmh.fileVersionsFile = new File(mmh.getDataFolder() + "" + File.separatorChar + "fileVersions.yml");
		try {
			mmh.fileVersions.save(mmh.fileVersionsFile);
		} catch (Exception exception) {
			mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_SAVE_FILEVERSION).error(exception));
		}
		mmh.fileVersions  = new YamlConfiguration();
	}

}