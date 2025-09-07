package com.github.joelgodofwar.mmh.handlers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import javax.annotation.Nonnull;

import com.github.joelgodofwar.mmh.util.heads.*;
import lib.github.joelgodofwar.coreutils.util.StrUtils;
import lib.github.joelgodofwar.coreutils.util.YmlConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Goat;
import org.bukkit.entity.Horse;
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
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Strider;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.Trident;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.TropicalFish.Pattern;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.StriderTemperatureChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.json.JSONObject;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.MoreMobHeadsLib;
import com.github.joelgodofwar.mmh.command.Command_1_20_R2;
import com.github.joelgodofwar.mmh.command.ConfigGuiCommand;
import com.github.joelgodofwar.mmh.command.GiveHeadCommand;
import com.github.joelgodofwar.mmh.command.ViewHeadsCommand;
import com.github.joelgodofwar.mmh.common.PluginLibrary;
import com.github.joelgodofwar.mmh.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.mmh.common.error.Report;
import com.github.joelgodofwar.mmh.util.ChatColorUtils;
import com.github.joelgodofwar.mmh.util.DLCInstaller;

import com.github.joelgodofwar.mmh.util.VerifyConfig;
import com.github.joelgodofwar.mmh.util.Version;

import com.github.joelgodofwar.mmh.util.tools.JarUtil;

public class EventHandler_1_20_R1_2 implements Listener, MMHEventHandler {
	/** Variables */
	MoreMobHeads mmh;
	double defpercent = 13.0;
	String world_whitelist;
	String world_blacklist;
	String mob_whitelist;
	String mob_blacklist;
	boolean debug;
	YmlConfiguration chanceConfig;

	public YamlConfiguration beheadingMessages = new YamlConfiguration();

	// Head default values.
	String nameDEF = "Name Not Found";
	String uuidDEF = "40404040-4040-4040-4040-404040404040";
	String textureDEF = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY1NDljZjhiNWE1MWUwZmFkM2MyMmY5YTY3ZDg3Mjc2ZDdhMzdiZjY0Zjk1ODgwMDI2ZDlkMzE5ZTMyMjhiNSJ9fX0=";
	ArrayList<String> loreDEF = new ArrayList<>(Arrays.asList("§cNotify an Admin§e!"));

	List<MerchantRecipe> playerhead_recipes = new ArrayList<MerchantRecipe>();
	List<MerchantRecipe> blockhead_recipes = new ArrayList<MerchantRecipe>();
	List<MerchantRecipe> custometrade_recipes = new ArrayList<MerchantRecipe>();
	int BHNum, BHNum2, BHNum3, BHNum4;
	VerifyConfig verify;// = new VerifyConfig(mmh.config);
	DetailedErrorReporter reporter;

	private GiveHeadCommand giveHeadCommand;
	private ViewHeadsCommand viewHeadsCommand;
	@SuppressWarnings("unused") ConfigGuiCommand configCommand;
	PlayerHeadLoader playerHeadLoader;
	BlockHeadLoader blockHeadLoader;
	MobHeadLoader mobHeadLoader;
	MiniBlockLoader miniBlockLoader;
	private HeadManager headManager;
	private boolean dlcAdvertisingEnabled;

	// Default player names from player_heads.yml
	private static final List<String> DEFAULT_PLAYER_NAMES = Arrays.asList(
			// Hermits
			"Etho", "falsesymmetry", "BdoubleO100", "Renthedog", "Grian", "Welsknight",
			"GoodTimesWithScar", "hypnotizd", "VintageBeef", "Xisuma", "joehillssays",
			"xBCrafted", "impulseSV", "Zedaph", "Docm77", "ZombieCleo", "Keralis",
			"Mumbo", "cubfan135", "iJevin", "Tango", "Skizzleman", "GeminiTay",
			"PearlescentMoon", "Smallishbeans",
			// Former Hermits
			"Stressmonster101", "Tinfoilchef", "Jessassin", "Biffa2001", "PythonGB",
			"iskall85", "generikB",
			// Wish They Were Hermits
			"Dataless822"
			);

	@SuppressWarnings({ })
	public EventHandler_1_20_R1_2(MoreMobHeads plugin, HeadManager headManager) {
		// TODO: Top of code
		try { // REPORT_EVENT_HANDLER_LOAD "Error while loading EventHandler."
			/** Set variables */
			mmh = plugin;
			this.headManager = headManager;
			reporter = new DetailedErrorReporter(mmh);
			verify = new VerifyConfig(mmh);
			mmh.LOGGER.log("Loading 1.20 EventHandler...");
			long startTime = System.currentTimeMillis();
			//mmh.getCommand("mmh").setExecutor(this);
			world_whitelist = mmh.config.getString("world.whitelist", "");
			world_blacklist = mmh.config.getString("world.blacklist", "");
			mob_whitelist = mmh.config.getString("mob.whitelist", "");
			mob_blacklist = mmh.config.getString("mob.blacklist", "");
			debug = plugin.debug;
			dlcAdvertisingEnabled = mmh.getConfig().getBoolean("global_settings.disable_dlc_display", false);

			try {
				beheadingMessages.load(new File(mmh.getDataFolder() + "" + File.separatorChar + "messages.yml"));
			} catch (Exception exception) {
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_MESSAGES_LOAD_ERROR).error(exception));
			}

			// Load all heads and recipes
			loadHeadsAndRecipes();

			// Initialize GiveHeadCommand
			giveHeadCommand = new GiveHeadCommand(mmh, headManager, headManager.getJsonPlayerHeads(),
					headManager.getOnlinePlayerHeads(), headManager.getPendingTargets(),
					headManager.getPendingHeadTypes());
			// Initialize ViewHeadsCommand with HeadManager
			viewHeadsCommand = new ViewHeadsCommand(mmh, headManager.getJsonPlayerHeads(),
					headManager.getOnlinePlayerHeads(), headManager.getMobHeadsList(), headManager.getBlockHeadsList());
			configCommand = new ConfigGuiCommand(plugin);

			// Load heads for currently online players
			for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				giveHeadCommand.loadPlayerHead(onlinePlayer);
			}

			mmh.LOGGER.log("EventHandler_1_20_R1 took " + mmh.LoadTime(startTime) + " to load");

		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_EVENT_HANDLER_LOAD).error(exception));
		}
		// Register the command with ViewHeadsCommand
		Objects.requireNonNull(mmh.getCommand("mmh")).setExecutor(new Command_1_20_R2(mmh, headManager, giveHeadCommand, viewHeadsCommand, mmh.eventHandler));
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		try {
			Entity entity = event.getEntity();
			DamageCause cause = event.getCause();

			if(cause.equals(DamageCause.FIRE_TICK)) {
				//mmh.entityDamageCause.put(entity.getUniqueId(), cause);
				UUID eUUID = mmh.entityPlayers.get(entity.getUniqueId());
				mmh.playerWeapons.put(eUUID, mmh.playerWeapons.get(eUUID));
			}
		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.UNHANDLED_ENTITYDAMAGE_EVENT_ERROR).error(exception));
		}
	}

	@SuppressWarnings({})
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		try { // REPORT_EDE_EVENT_ERROR "Unable to get damager weapon."
			mmh.logDebug("EDEE - START");
			mmh.logDebug("EDEE - getEntity=" + event.getEntity().getName());
			mmh.logDebug("EDEE - getDamager=" + event.getDamager().getName());

			Player player = null;
			if (event.getDamager() instanceof Player) {
				player = (Player) event.getDamager();
				if ((event.getEntity() instanceof EnderCrystal)) {
					//** Is Player and Is End Crystal */
					EnderCrystal ec = (EnderCrystal) event.getEntity();
					mmh.endCrystals.put(ec.getUniqueId(), player.getUniqueId());
				}
			} else if (event.getDamager() instanceof Arrow) {
				Arrow arrow = (Arrow) event.getDamager();
				if ((arrow.getShooter() instanceof Player) && !(event.getEntity() instanceof EnderCrystal)) {
					//** Is Player but Not End Crystal */
					player = (Player) arrow.getShooter();
				} else if ((arrow.getShooter() instanceof Player) && (event.getEntity() instanceof EnderCrystal)) {
					//** Is Player and Is End Crystal */
					player = (Player) arrow.getShooter();
					EnderCrystal ec = (EnderCrystal) event.getEntity();
					mmh.endCrystals.put(ec.getUniqueId(), player.getUniqueId());
				} else {
					return; // Not Player or Not End Crystal
				}
			} else if (event.getDamager() instanceof ThrownPotion) {
				ThrownPotion potion = (ThrownPotion) event.getDamager();
				if (!(potion.getShooter() instanceof Player)) {
					return;
				}
				player = (Player) potion.getShooter();
			} else if (event.getDamager() instanceof Snowball) {
				Snowball snowball = (Snowball) event.getDamager();
				if ((snowball.getShooter() instanceof Player) && !(event.getEntity() instanceof EnderCrystal)) {
					//** Is Player but Not End Crystal */
					player = (Player) snowball.getShooter();
				} else if ((snowball.getShooter() instanceof Player) && (event.getEntity() instanceof EnderCrystal)) {
					//** Is Player and Is End Crystal */
					player = (Player) snowball.getShooter();
					EnderCrystal ec = (EnderCrystal) event.getEntity();
					mmh.endCrystals.put(ec.getUniqueId(), player.getUniqueId());
				} else {
					return; // Not Player or Not End Crystal
				}
			} else if (event.getDamager() instanceof Egg) {
				Egg egg = (Egg) event.getDamager();
				if ((egg.getShooter() instanceof Player) && !(event.getEntity() instanceof EnderCrystal)) {
					//** Is Player but Not End Crystal */
					player = (Player) egg.getShooter();
				} else if ((egg.getShooter() instanceof Player) && (event.getEntity() instanceof EnderCrystal)) {
					//** Is Player and Is End Crystal */
					player = (Player) egg.getShooter();
					EnderCrystal ec = (EnderCrystal) event.getEntity();
					mmh.endCrystals.put(ec.getUniqueId(), player.getUniqueId());
				} else {
					return; // Not Player or Not End Crystal
				}
			} else if (event.getDamager() instanceof Trident) {
				Trident trident = (Trident) event.getDamager();
				if ((trident.getShooter() instanceof Player) && !(event.getEntity() instanceof EnderCrystal)) {
					//** Is Player but Not End Crystal */
					player = (Player) trident.getShooter();
				} else if ((trident.getShooter() instanceof Player) && (event.getEntity() instanceof EnderCrystal)) {
					//** Is Player and Is End Crystal */
					player = (Player) trident.getShooter();
					EnderCrystal ec = (EnderCrystal) event.getEntity();
					mmh.endCrystals.put(ec.getUniqueId(), player.getUniqueId());
				} else {
					return; // Not Player or Not End Crystal
				}
			} else if (event.getDamager() instanceof TNTPrimed) {
				TNTPrimed tnt = (TNTPrimed) event.getDamager();
				if (!(tnt.getSource() instanceof Player)) {
					return;
				}
				player = (Player) tnt.getSource();
			} else if (event.getDamager() instanceof EnderCrystal) {
				EnderCrystal ec = (EnderCrystal) event.getDamager();
				UUID pUUID = mmh.endCrystals.get(ec.getUniqueId());
				if(pUUID == null) {return;}
				player = Bukkit.getPlayer(pUUID);
                assert player != null;
                mmh.playerWeapons.put(player.getUniqueId(), new ItemStack(Material.END_CRYSTAL));
				return;
			} else if (event.getDamager() instanceof Creeper) {
				Creeper creeper = (Creeper) event.getDamager();
				creeper.isPowered();
				return;
			} else {

				return;
			}

			Entity entity = event.getEntity();
			EntityDamageEvent ede = entity.getLastDamageCause();
			if(ede != null) {
				DamageCause dc = ede.getCause();
				mmh.logDebug("EDEE - dc=" + dc);
			}
			// Store the damaging player's UUID and the damaging weapon in the map

			if(player == null) {
				return;
			}
			mmh.logDebug("EDEE - UUID=" + player.getUniqueId());
			//mmh.logDebug("EDEE - Weapon=" + resolveDamagingWeapon(player.getInventory(), event.getCause()).orElse(null));
			mmh.entityPlayers.put(event.getEntity().getUniqueId(), player.getUniqueId());
			mmh.playerWeapons.put(player.getUniqueId(),	resolveDamagingWeapon(player.getInventory(), event.getCause()).orElse(null));
			// Log Entity and Player UUIDs
			// Log Player UUID and Weapon

			// if Fire_tick get Player UUID

			// Log Player UUID and Weapon stored.

		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.UNHANDLED_ENTITYDAMAGEBYENTITY_EVENT_ERROR).error(exception));
		}
	}

	/** Events go here */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDeathEvent(EntityDeathEvent event) {// TODO: EnityDeathEvent
		try {
			LivingEntity entity = event.getEntity();
			// Verify that the killer is a player
			if (!(entity.getKiller() instanceof Player) && !(entity.getKiller() instanceof Creeper)) {
				return;
			}

			World world = event.getEntity().getWorld();
			List<ItemStack> Drops = event.getDrops();
			world_whitelist = mmh.config.getString("global_settings.world.whitelist", "");
			world_blacklist = mmh.config.getString("global_settings.world.blacklist", "");
			mob_whitelist = mmh.config.getString("head_settings.mob_heads.whitelist", "");
			mob_blacklist = mmh.config.getString("head_settings.mob_heads.blacklist", "");

			mmh.logDebug("EDE - world_whitelist=" + world_whitelist);
			mmh.logDebug("EDE - world_blacklist=" + world_blacklist);
			mmh.logDebug("EDE - mob_whitelist=" + mob_whitelist);
			mmh.logDebug("EDE - mob_blacklist=" + mob_blacklist);

			try { // REPORT_WHITE_BLACK_LIST "Unable to parse global whitelist/blacklist"
				if ((world_whitelist != null) && !world_whitelist.isEmpty() && (world_blacklist != null)
						&& !world_blacklist.isEmpty()) {
					if (!StrUtils.stringContains(world_whitelist, world.getName())
							&& StrUtils.stringContains(world_blacklist, world.getName())) {

						mmh.logDebug("EDE - World - On blacklist and Not on whitelist.");

						return;
					} else if (!StrUtils.stringContains(world_whitelist, world.getName())
							&& !StrUtils.stringContains(world_blacklist, world.getName())) {

						mmh.logDebug("EDE - World - Not on whitelist.");

						return;
					} else if (!StrUtils.stringContains(world_whitelist, world.getName())) {

					}
				} else if ((world_whitelist != null) && !world_whitelist.isEmpty()) {
					if (!StrUtils.stringContains(world_whitelist, world.getName())) {

						mmh.logDebug("EDE - World - Not on whitelist.");

						return;
					}
				} else if ((world_blacklist != null) && !world_blacklist.isEmpty()) {
					if (StrUtils.stringContains(world_blacklist, world.getName())) {

						mmh.logDebug("EDE - World - On blacklist.");

						return;
					}
				}
			} catch(Exception exception){
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_WHITE_BLACK_LIST).error(exception));
			}

			if ((entity instanceof Player) && ((entity.getKiller() instanceof Player) || (entity.getKiller() instanceof Creeper))) {
				// TODO: Player Kill Player
				try{ // REPORT_PLAYER_KILL_PLAYER "Unable to parse Player Death."
					mmh.logDebug("EDE Entity is Player");
					if ((entity.getKiller().hasPermission("moremobheads.players") || mmh.isDev) || (entity.getKiller() instanceof Creeper)) {

						mmh.logDebug("EDE DropIt=" + mmh.DropIt(event, mmh.playerChance, entity.getKiller() ));
						mmh.logDebug("EDE chance_percent.player=" + mmh.playerChance );

						if (mmh.DropIt(event, mmh.playerChance , entity.getKiller())) {
							// Player daKiller = entity.getKiller();

							mmh.logDebug("EDE Killer is Player line:1073");

							Player victim = (Player) entity;
							Player killer = entity.getKiller();
							String killerName;
							String entityName;
							PlayerProfile profile =  victim.getPlayerProfile();
							ItemStack head = new ItemStack(Material.PLAYER_HEAD);
							SkullMeta meta = (SkullMeta) head.getItemMeta();
							PlayerTextures textures = profile.getTextures();

							mmh.logDebug(" EDE PlayerProfile getName: " + profile.getName() );
							mmh.logDebug(" EDE PlayerProfile UUID: " + profile.getUniqueId() );
							mmh.logDebug(" EDE PlayerProfile Skin: " + profile.getTextures().getSkin() );

							killerName = mmh.getNickname(killer);
							entityName = mmh.getNickname(victim);

							head = mmh.makeHead(entityName, profile.getTextures().getSkin().toString(), profile.getUniqueId().toString(), entity.getType(), entity.getKiller());
							boolean isCanceled = mmh.callDropEvent(entity, entity.getKiller(),head, head.getItemMeta().getDisplayName(), HeadUtils.convertURLToBase64(profile.getTextures().getSkin().toString()), profile.getUniqueId().toString(), head.getItemMeta().getLore(), "entity.player.hurt");
							if (!isCanceled) {
								mmh.playerGiveOrDropHead(entity.getKiller(), head);
								mmh.logDebug("EDE " + ((Player) entity).getDisplayName() + " Player Head Dropped");
							}
							if (mmh.config.getBoolean("head_settings.player_heads.announce_kill.enabled", true)) {
								announceBeheading(entity, entityName, killer,
										mmh.config.getBoolean("head_settings.player_heads.announce_kill.displayname", true));
							}
						}
						return;
					} else {
						mmh.logDebug("EDE Killer does not have permission \"moremobheads.players\"");
					}
				} catch(Exception exception){
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLAYER_KILL_PLAYER).error(exception));
				}

			} else if (event.getEntity() instanceof LivingEntity) {
				if ((entity.getKiller() instanceof Player) || (entity.getKiller() instanceof Creeper)) {
					String name = event.getEntityType().toString().replace(" ", "_");
					mmh.logDebug("EDE name=" + name);
					String isNametag = null;
					@Nonnull
					PersistentDataContainer pdc = entity.getPersistentDataContainer();
					isNametag = entity.getPersistentDataContainer().get(mmh.NAMETAG_KEY, PersistentDataType.STRING);// .getScoreboardTags();//
					if ((isNametag != null)) {
						mmh.logDebug("EDE isNametag=" + isNametag);
					}

					if ( (entity.getKiller() instanceof Creeper) || (entity.getKiller().hasPermission("moremobheads.mobs")  || mmh.isDev) ) {
						if ( ((entity.getKiller() instanceof Creeper) || (entity.getKiller().hasPermission("moremobheads.nametag")  || mmh.isDev) ) && (isNametag != null)) {

							try{ // REPORT_PLAYER_NAMED_MOB "Unable to parse named mob kill."
								EntityDamageEvent ede = entity.getLastDamageCause();
								if(ede != null) {
									DamageCause dc = ede.getCause();
									mmh.LOGGER.log("EDEE - dc=" + dc);
									if(dc.equals(DamageCause.FIRE_TICK)) {

									}
								}
								if ((entity.getCustomName() != null) && !(entity.getCustomName().contains("jeb_")) && !(entity.getCustomName().contains("Toast"))) {
									mmh.logDebug("EDE customname=" + entity.getCustomName());
									if ((entity instanceof Skeleton) || (entity instanceof Zombie) || (entity instanceof PigZombie)) {
										if (mmh.getServer().getPluginManager().getPlugin("SilenceMobs") != null) {
											if (entity.getCustomName().toLowerCase().contains("silenceme")
													|| entity.getCustomName().toLowerCase().contains("silence me")) {
												return;
											}
										}
										boolean enforce_whitelist = mmh.config.getBoolean("head_settings.player_heads.whitelist.enforce", false);
										boolean enforce_blacklist = mmh.config.getBoolean("head_settings.player_heads.blacklist.enforce", false);
										boolean on_whitelist = mmh.config.getString("head_settings.player_heads.whitelist.player_head_whitelist", "")
												.toLowerCase().contains(entity.getCustomName().toLowerCase());
										boolean on_blacklist = mmh.config.getString("head_settings.player_heads.blacklist.player_head_blacklist", "")
												.toLowerCase().contains(entity.getCustomName().toLowerCase());
										//**  */
										if (mmh.DropIt(event, chanceConfig.getDouble("named_mob", 10.0), entity.getKiller())) {
											boolean isTrue = false;
											if (enforce_whitelist && enforce_blacklist) {
												if (on_whitelist && !(on_blacklist)) {
													isTrue = true;
												}else {
													isTrue = false;
												}
											} else if (enforce_whitelist && !enforce_blacklist) {
												if (on_whitelist) {
													isTrue = true;
												}else {
													isTrue = false;
												}
											} else if (!enforce_whitelist && enforce_blacklist) {
												if (!on_blacklist) {
													isTrue = true;
												}else {
													isTrue = false;
												}
											} else if (!enforce_whitelist && !enforce_blacklist) {
												isTrue = true;
											}
											if(isTrue) {
												PlayerProfile profile = Bukkit.createPlayerProfile(entity.getCustomName());
												ItemStack head = new ItemStack(Material.PLAYER_HEAD);
												SkullMeta meta = (SkullMeta) head.getItemMeta();
												PlayerTextures textures = profile.getTextures();

												profile.setTextures(textures);
												meta.setOwnerProfile(profile);
												meta.setNoteBlockSound(NamespacedKey.minecraft( "entity.player.hurt" ));
												ArrayList<String> lore = new ArrayList();
												if(mmh.config.getBoolean("head_settings.lore.show_killer", true)){
													lore.add(ChatColor.RESET + ChatColorUtils.setColors( mmh.langName.getString("killedby", "<RED>Killed <RESET>By <YELLOW><player>").replace("<player>"
															, mmh.getName(entity.getType(), entity.getKiller()) ) ) );
												}
												if(mmh.config.getBoolean("head_settings.lore.show_plugin_name", true)){
													lore.add(ChatColor.AQUA + "MoreMobHeads");
												}
												meta.setLore(lore);
												meta.setLore(lore);
												head.setItemMeta(meta);
												if(entity.getEquipment().getHelmet().getType() == Material.PLAYER_HEAD) {
													head = entity.getEquipment().getHelmet();
													boolean isCanceled = mmh.callDropEvent(entity, entity.getKiller(),head, head.getItemMeta().getDisplayName(), null, null, head.getItemMeta().getLore(), null);
													if (!isCanceled){
														Drops.add(head);
														mmh.logDebug(" EDE " + entity.getCustomName() + " Head Dropped");
													}
													if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
														announceBeheading(entity, entity.getCustomName(),
																entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
													}
												} else if(!entity.getEquipment().getHelmet().isSimilar(head)) {
													mmh.logDebug(" EDE NamedMob is not waering PlayerHead drop canceled.");
												}
												return;
											}
										}
										if ((mob_whitelist != null) && !mob_whitelist.isEmpty() && (mob_blacklist != null)
												&& !mob_blacklist.isEmpty()) {
											if (!StrUtils.stringContains(mob_whitelist, name)) {// mob_whitelist.contains(name)
												mmh.LOGGER.log("EDE - Mob - Not on whitelist. Mob=" + name);
												return;
											}
										} else if ((mob_whitelist != null) && !mob_whitelist.isEmpty()) {
											if (!StrUtils.stringContains(mob_whitelist, name)
													&& StrUtils.stringContains(mob_blacklist, name)) {// mob_whitelist.contains(name)
												mmh.LOGGER.log("EDE - Mob - Not on whitelist - Is on blacklist. Mob=" + name);
												return;
											}
										} else if ((mob_blacklist != null) && !mob_blacklist.isEmpty()) {
											if (StrUtils.stringContains(mob_blacklist, name)) {
												mmh.LOGGER.log("EDE - Mob - On blacklist. Mob=" + name);
												return;
											}
										}
									}
								}
							} catch(Exception exception){
								reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLAYER_NAMED_MOB).error(exception));
							}
						}
						MobHeadData mhd;
						try { // REPORT_PLAYER_KILL_MOB "Unable to parse Mob Kill."
							switch (name) {
							case "CREEPER":
								// ConfigHelper.Double(chanceConfig,
								// "chance_percent.creeper", defpercent)
								Creeper creeper = (Creeper) event.getEntity();
								//double cchance = ConfigHelper.Double(chanceConfig, "chance_percent.creeper", defpercent);
								if (creeper.isPowered()) {
									name = "CREEPER.CHARGED";
									//cchance = ConfigHelper.Double(chanceConfig, "chance_percent.creeper_charged", defpercent);
								}else {
									name = "CREEPER.NORMAL";
								}
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() ).getData();
								double cchance = mhd.getChance();
								if (mmh.DropIt(event, cchance, entity.getKiller())) {
									mmh.logDebug("EDE Creeper vanilla="
											+ mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.creeper", false));
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.creeper", false) && (name != "CREEPER_CHARGED")) {
										Drops = mmh.addHeadToDrops(event.getEntity(), event.getEntity().getKiller(), new ItemStack(Material.CREEPER_HEAD), null, null, null, null, null, Drops);
									} else {
										ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, entity.getKiller() );
										Drops = mmh.addHeadToDrops(entity, entity.getKiller(),
												HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
												, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
										//Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name).getTexture().toString(), mmh.langName.getString(name.toLowerCase(),MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()), entity));
									} // MobHeads.valueOf(name).getName() + " Head"
									mmh.logDebug("EDE Creeper Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ).replace(" Head", ""),
												entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "ENDER_DRAGON":
								// ConfigHelper.Double(chanceConfig,
								// "chance_percent.ender_dragon", defpercent)
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.ender_dragon", false)) {
										Drops.add(new ItemStack(Material.DRAGON_HEAD));
									} else {
										Drops.add(
												mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
														mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
												);
									}
									mmh.logDebug("EDE Ender Dragon Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ).replace(" Head", "")
												,entity.getKiller(),mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname",true));
									}
								}
								break;
							case "PIGLIN":
								// ConfigHelper.Double(chanceConfig, "chance_percent.ender_dragon", defpercent)
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.piglin", false)) {
										Drops.add(new ItemStack(Material.PIGLIN_HEAD));
									} else {
										Drops.add(
												mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
														mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
												);
									}
									mmh.logDebug("EDE Ender Dragon Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""),entity.getKiller(),mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "SKELETON":
								// ConfigHelper.Double(chanceConfig, "chance_percent.skeleton", defpercent)
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.skeleton", false)) {
										Drops.add(new ItemStack(Material.SKELETON_SKULL));
									} else {
										Drops.add(
												mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
														mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
												);
									}
									mmh.logDebug("EDE Skeleton vanilla="
											+ mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.skeleton", false));
									mmh.logDebug("EDE Skeleton Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""),entity.getKiller(),mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "WITHER_SKELETON":
								// ConfigHelper.Double(chanceConfig, "chance_percent.wither_skeleton",
								// defpercent)
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.wither_skeleton", false)) {
										Drops.add(new ItemStack(Material.WITHER_SKELETON_SKULL));
									} else {
										Drops.add(
												mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
														mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
												);
									}
									mmh.logDebug("EDE Wither Skeleton Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "ZOMBIE":
								// ConfigHelper.Double(chanceConfig, "chance_percent.zombie", defpercent)
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.zombie", false)) {
										Drops.add(new ItemStack(Material.ZOMBIE_HEAD));
									} else {

										Drops.add(
												mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
														mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
												);
									}
									mmh.logDebug("EDE Zombie vanilla="+ mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.zombie", false));
									mmh.logDebug("EDE Zombie Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""),entity.getKiller(),mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "TROPICAL_FISH":
								TropicalFish daFish = (TropicalFish) entity;
								DyeColor daFishBody = daFish.getBodyColor();
								DyeColor daFishPatternColor = daFish.getPatternColor();
								Pattern daFishType = daFish.getPattern();
								mmh.LOGGER.log("bodycolor=" + daFishBody.toString() + "\nPatternColor=" + daFishPatternColor.toString()
								+ "\nPattern=" + daFishType.toString());
								// TropicalFishHeads daFishEnum = TropicalFishHeads.getIfPresent(name);
								String daFishName = mmh.getNamedTropicalFishName(daFishType, daFishBody, daFishPatternColor);
								mmh.logDebug("daFishName: " + daFishName);
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() + "." + daFishName.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											//MoreMobHeadsLib.addSound(mmh.makeSkull(TropicalFishHeads.valueOf(name + "_" + daFishName).getTexture().toString()
											//		, mmh.langName.getString(name.toLowerCase() + "." + daFishName.toLowerCase(), TropicalFishHeads.valueOf(name + "_" + daFishName).getName() + " Head")
											//		, entity.getKiller()), entity)
											);
									mmh.logDebug("EDE TROPICAL_FISH:" + daFishName + " head dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "WITHER":
								String name2 = name + "_NORMAL";
								mhd = headManager.loadedMobHeads.get( name2.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {

									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											/**MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name2).getTexture().toString(), mmh.langName.getString(name2.toLowerCase().replace("_", ".")
											, MobHeads.valueOf(name2).getName() + " Head"), entity.getKiller()), entity)//*/
											);
									mmh.logDebug("EDE " + name2 + " Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
									if (coinFlip()) {
										name2 = name + "_PROJECTILE";
										mhd = headManager.loadedMobHeads.get( name2.toLowerCase() ).getData();
										Drops.add(
												mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
														mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
												);
										mmh.logDebug("EDE " + name2 + " Head Dropped");
									}
									if (coinFlip()) {
										name2 = name + "_BLUE_PROJECTILE";
										mhd = headManager.loadedMobHeads.get( name2.toLowerCase() ).getData();
										Drops.add(
												mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
														mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
												);
										mmh.logDebug("EDE " + name2 + " Head Dropped");
									}
								}
								break;

							case "FOX":
								Fox dafox = (Fox) entity;
								String dafoxtype = dafox.getFoxType().toString();
								mmh.logDebug("EDE dafoxtype=" + dafoxtype);
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() + "." + dafoxtype.toLowerCase() ).getData();
								// ConfigHelper.Double(chanceConfig, "chance_percent.fox." +
								// dafoxtype.toLowerCase(), defpercent)
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											); //
									mmh.logDebug("EDE Fox Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}

								break;
							case "CAT":
								Cat dacat = (Cat) entity;
								String dacattype = dacat.getCatType().toString();
								mmh.logDebug("entity cat=" + dacat.getCatType());
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() + "." + dacattype.toLowerCase() ).getData();
								// ConfigHelper.Double(chanceConfig, "chance_percent.cat." +
								// dacattype.toLowerCase(), defpercent)
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											);
									mmh.logDebug("EDE Cat Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "OCELOT":
								// ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(),
								// defpercent)
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											);
									mmh.logDebug("EDE " + name + " Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(),
												mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								mmh.logDebug("EDE " + mhd.getDisplayName() + " killed");
								break;
							case "BEE":
								Bee daBee = (Bee) entity;
								int daAnger = daBee.getAnger();
								mmh.logDebug("EDE daAnger=" + daAnger);
								boolean daNectar = daBee.hasNectar();
								mmh.logDebug("EDE daNectar=" + daNectar);
								if ((daAnger >= 1) && (daNectar == true)) {
									// ConfigHelper.Double(chanceConfig, "chance_percent.bee.angry_pollinated",
									// defpercent)
									mhd = headManager.loadedMobHeads.get( "bee.angry_pollinated" ).getData();
									if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
										Drops.add(
												mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
														mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
												);
										mmh.logDebug("EDE Angry Pollinated Bee Head Dropped");
										if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
											announceBeheading(entity,
													mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
													.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
										}
									}
								} else if ((daAnger >= 1) && (daNectar == false)) {
									// ConfigHelper.Double(chanceConfig, "chance_percent.bee.angry", defpercent)
									mhd = headManager.loadedMobHeads.get( "bee.angry" ).getData();
									if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
										Drops.add(
												mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
														mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
												);
										mmh.logDebug("EDE Angry Bee Head Dropped");
										if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
											announceBeheading(entity,
													mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
													.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
										}
									}
								} else if ((daAnger == 0) && (daNectar == true)) {
									// ConfigHelper.Double(chanceConfig, "chance_percent.bee.pollinated",
									// defpercent)
									mhd = headManager.loadedMobHeads.get( "bee.pollinated" ).getData();
									if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
										Drops.add(
												mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
														mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
												);
										mmh.logDebug("EDE Pollinated Bee Head Dropped");
										if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
											announceBeheading(entity,
													mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
													.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
										}
									}
								} else if ((daAnger == 0) && (daNectar == false)) {
									// ConfigHelper.Double(chanceConfig, "chance_percent.bee.chance_percent",
									// defpercent)
									mhd = headManager.loadedMobHeads.get( "bee.none" ).getData();
									if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
										Drops.add(
												mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
														mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
												);
										mmh.logDebug("EDE Bee Head Dropped");
										if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
											announceBeheading(entity,
													mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
													.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
										}
									}
								}
								break;
							case "LLAMA":
								Llama daLlama = (Llama) entity;
								String daLlamaColor = daLlama.getColor().toString();
								//String daLlamaName = LlamaHeads.valueOf(name + "_" + daLlamaColor).getName() + " Head";// daLlamaColor.toLowerCase().replace("b",
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() + "." + daLlamaColor.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											);
									mmh.logDebug("EDE Llama Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "HORSE":
								Horse daHorse = (Horse) entity;
								String daHorseColor = daHorse.getColor().toString();
								//String daHorseName = HorseHeads.valueOf(name + "_" + daHorseColor).getName() + " Head";// daHorseColor.toLowerCase().replace("b",
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() + "." + daHorseColor.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											);
									mmh.logDebug("EDE Horse Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;

							case "MUSHROOM_COW":
								name = "MOOSHROOM";
							case "MOOSHROOM":
								MushroomCow daMushroom = (MushroomCow) entity;
								String daCowVariant = daMushroom.getVariant().toString();
								//String daCowName = daCowVariant.toLowerCase().replace("br", "Br").replace("re", "Re")									+ " Mooshroom Head";
								mmh.logDebug("EDE " + name + "_" + daCowVariant);
								// ConfigHelper.Double(chanceConfig, "chance_percent.mushroom_cow." +
								// daCowVariant.toLowerCase(), defpercent)
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() + "." + daCowVariant.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											);
									mmh.logDebug("EDE Mooshroom Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "PANDA":
								Panda daPanda = (Panda) entity;
								String daPandaGene = daPanda.getMainGene().toString();
								/**String daPandaName = daPandaGene.toLowerCase().replace("br", "Br").replace("ag", "Ag")
									.replace("la", "La").replace("no", "No").replace("p", "P").replace("we", "We")
									.replace("wo", "Wo") + " Panda Head";//
							if (daPandaGene.equalsIgnoreCase("normal")) {
								daPandaName.replace("normal ", "");
							}//*/
								mmh.logDebug("EDE " + name + "_" + daPandaGene);
								// ConfigHelper.Double(chanceConfig, "chance_percent.panda." +
								// daPandaGene.toLowerCase(), defpercent)
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() + "." + daPandaGene.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											);
									mmh.logDebug("EDE Panda Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "PARROT":
								Parrot daParrot = (Parrot) entity;
								String daParrotVariant = daParrot.getVariant().toString();
								//String daParrotName = daParrotVariant.toLowerCase().replace("b", "B").replace("c", "C")
								//		.replace("g", "G").replace("red", "Red") + " Parrot Head";
								mmh.logDebug("EDE " + name + "_" + daParrotVariant);
								// ConfigHelper.Double(chanceConfig, "chance_percent.parrot." +
								// daParrotVariant.toLowerCase(), defpercent)
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() + "." + daParrotVariant.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											);
									mmh.logDebug("EDE Parrot Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "RABBIT":
								String daRabbitType;
								Rabbit daRabbit = (Rabbit) entity;
								daRabbitType = daRabbit.getRabbitType().toString();
								if (daRabbit.getCustomName() != null) {
									if (daRabbit.getCustomName().contains("Toast")) {
										daRabbitType = "Toast";
									}
								}
								//String daRabbitName = RabbitHeads.valueOf(name + "_" + daRabbitType).getName() + " Head";
								mmh.logDebug("EDE " + name + "_" + daRabbitType);
								// ConfigHelper.Double(chanceConfig, "chance_percent.rabbit." +
								// daRabbitType.toLowerCase(), defpercent)
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() + "." + daRabbitType.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											);
									mmh.logDebug("EDE Rabbit Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "VILLAGER":
								Villager daVillager = (Villager) entity; // Location jobsite =
								// daVillager.getMemory(MemoryKey.JOB_SITE);
								String daVillagerType = daVillager.getVillagerType().toString();
								String daVillagerProfession = daVillager.getProfession().toString();

								mmh.logDebug("EDE name=" + name);
								mmh.logDebug("EDE profession=" + daVillagerProfession);
								mmh.logDebug("EDE type=" + daVillagerType);
								//String daName = name + "_" + daVillagerProfession + "_" + daVillagerType;
								String daName = name + "." + daVillagerType + "." + daVillagerProfession;
								mmh.logDebug("EDE " + daName + "		 " + name + "_" + daVillagerProfession + "_"
										+ daVillagerType);
								//String daVillagerName = VillagerHeads.valueOf(daName).getName() + " Head";
								// ConfigHelper.Double(chanceConfig, "chance_percent.villager." +
								// daVillagerType.toLowerCase() + "." + daVillagerProfession.toLowerCase(),
								// defpercent)
								mhd = headManager.loadedMobHeads.get( daName.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											);
									mmh.logDebug("EDE Villager Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "ZOMBIE_VILLAGER":
								ZombieVillager daZombieVillager = (ZombieVillager) entity;
								String daZombieVillagerType = daZombieVillager.getVillagerType().toString();
								String daZombieVillagerProfession = daZombieVillager.getVillagerProfession().toString();
								//String daZombieVillagerName = ZombieVillagerHeads.valueOf(name + "_" + daZombieVillagerProfession).getName() + " Head";
								mmh.logDebug("EDE " + name + "_" + daZombieVillagerProfession);
								// ConfigHelper.Double(chanceConfig, "chance_percent.zombie_villager",
								// defpercent)
								mhd = headManager.loadedMobHeads.get( (name + "." + daZombieVillagerType + "." + daZombieVillagerProfession).toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )

											);
									mmh.logDebug("EDE Zombie Villager Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "SHEEP":
								Sheep daSheep = (Sheep) entity;
								String daSheepColor = daSheep.getColor().toString();
								String daSheepName;

								if (daSheep.getCustomName() != null) {
									if (daSheep.getCustomName().contains("jeb_")) {
										daSheepColor = "jeb_";
									} else {
										daSheepColor = daSheep.getColor().toString();
									}
								}
								//daSheepName = SheepHeads.valueOf(name + "_" + daSheepColor).getName() + " Head";
								mmh.logDebug("EDE " + daSheepColor + "_" + name);
								// ConfigHelper.Double(chanceConfig, "chance_percent.sheep." +
								// daSheepColor.toLowerCase(), defpercent)
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() + "." + daSheepColor.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											);
									mmh.logDebug("EDE Sheep Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "TRADER_LLAMA":
								TraderLlama daTraderLlama = (TraderLlama) entity;
								String daTraderLlamaColor = daTraderLlama.getColor().toString();
								//String daTraderLlamaName = LlamaHeads.valueOf(name + "_" + daTraderLlamaColor).getName() + " Head";
								mmh.logDebug("EDE " + daTraderLlamaColor + "_" + name);
								// ConfigHelper.Double(chanceConfig, "chance_percent.trader_llama." +
								// daTraderLlamaColor.toLowerCase(), defpercent)
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() + "." + daTraderLlamaColor.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											);
									mmh.logDebug("EDE Trader Llama Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "AXOLOTL":
								Axolotl daAxolotl = (Axolotl) entity;
								String daAxolotlVariant = daAxolotl.getVariant().toString();
								//String daAxolotlName = MobHeads117.valueOf(name + "_" + daAxolotlVariant).getName() + " Head";
								mmh.logDebug("EDE " + daAxolotlVariant + "_" + name);
								// ConfigHelper.Double(chanceConfig, "chance_percent.axolotl." +
								// daAxolotlVariant.toLowerCase(), defpercent)
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() + "." + daAxolotlVariant.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											);
									mmh.logDebug("EDE Axolotl Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "GOAT":
								Goat daGoat = (Goat) entity;
								String daGoatVariant;
								//String daGoatName;// = MobHeads117.valueOf(name + "_" + daAxolotlVariant).getName() + " Head";
								if (daGoat.isScreaming()) {
									// Giving screaming goat head
									daGoatVariant = "SCREAMING";
								} else {
									// give goat head
									daGoatVariant = "NORMAL";
								}
								mmh.logDebug("EDE " + daGoatVariant + "_" + name);
								// ConfigHelper.Double(chanceConfig, "chance_percent.goat." +
								// daGoatVariant.toLowerCase(), defpercent)
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() + "." + daGoatVariant.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											);
									mmh.logDebug("EDE Goat Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "STRIDER":
								Strider daStrider = (Strider) entity;
								PersistentDataContainer pdc2 = daStrider.getPersistentDataContainer();
								boolean isShivering = Boolean.parseBoolean(daStrider.getPersistentDataContainer().get(mmh.SHIVERING_KEY, PersistentDataType.STRING));
								if (mmh.chance25oftrue()) { // chance50oftrue()
									// isShivering
									name = name.concat("_SHIVERING");
									// ConfigHelper.Double(chanceConfig,
									// "chance_percent." + name.toLowerCase(),
									// defpercent)
									mhd = headManager.loadedMobHeads.get( name.toLowerCase() ).getData();
									if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
										Drops.add(
												mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
														mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
												);
										mmh.logDebug("EDE " + name + " Head Dropped");
										if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
											announceBeheading(entity, mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ).replace(" Head", ""), entity.getKiller(),
													mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
										}
									}
								}else {
									mhd = headManager.loadedMobHeads.get( name.toLowerCase() ).getData();
									if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
										Drops.add(
												mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
														mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
												);
										mmh.logDebug("EDE " + name + " Head Dropped");
										if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
											announceBeheading(entity, mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ).replace(" Head", ""),
													entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
										}
									}
								}

								mmh.logDebug("EDE " + mhd.getDisplayName() + " killed");
								break;
							case "FROG":
								Frog daFrog = (Frog) entity;
								String daFrogVariant = daFrog.getVariant().toString();
								name = name.concat("." + daFrogVariant);
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											);
									mmh.logDebug("EDE Frog Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ).replace(" Head", ""),
												entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}

								break;
							case "VEX":
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() ).getData();
								mmh.logDebug("EDE name=" + name);
								mmh.logDebug("EDE texture=" + mhd.getTexture());
								mmh.logDebug("EDE location=" + entity.getLocation().toString());
								mmh.logDebug("EDE getName=" + event.getEntity().getName());
								mmh.logDebug("EDE killer=" + entity.getKiller().toString());

								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											);
									mmh.logDebug("EDE " + name + " Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ).replace(" Head", ""),
												entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
									if (coinFlip()) {
										name = name.concat("_ANGRY");
										mhd = headManager.loadedMobHeads.get( name.toLowerCase() ).getData();
										Drops.add(
												mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
														mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
												);
										mmh.logDebug("EDE " + name + " Head Dropped");
									}
									mmh.logDebug("EDE " + mhd.getDisplayName() + " killed");
								}
								break;
							case "WOLF":
								Wolf wolf = (Wolf) event.getEntity();
								name2 = MoreMobHeadsLib.getName(name, wolf);
								mmh.logDebug("EDE name2 = " + name2);
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() + "." + name2.toLowerCase() ).getData();
								// ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(),
								// defpercent)
								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											);
									//name2 = (name2 + name).replace("_", " ");
									mmh.logDebug("EDE " + name2 + " Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() )
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							default:
								if(name == "CHICKEN") { name = "CHICKEN.NORMAL"; }
								if(name == "COW") { name = "COW.NORMAL"; }
								if(name == "PIG") { name = "PIG.NORMAL"; }
								if(name == "SNOW_GOLEM") {
									name = "SNOWMAN";
								}
								// mmh.makeSkull(MobHeads.valueOf(name).getTexture(), name);
								mhd = headManager.loadedMobHeads.get( name.toLowerCase() ).getData();
								mmh.logDebug("EDE name=" + name + " default");
								mmh.logDebug("EDE texture=" + mhd.getTexture() + " default");
								mmh.logDebug("EDE location=" + entity.getLocation().toString() + " default");
								mmh.logDebug("EDE getName=" + event.getEntity().getName() + " default");
								mmh.logDebug("EDE killer=" + entity.getKiller().toString() + " default");
								// ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(),
								// defpercent)

								if ( mmh.DropIt( event, mhd.getChance(), entity.getKiller() ) ) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ),
													mhd.getTexture(), mhd.getUuid(), mhd.getNoteblockSound(), false, entity.getKiller() )
											);
									mmh.logDebug("EDE " + name + " Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ).replace(" Head", "")
												, entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								mmh.logDebug("EDE " + mhd.getDisplayName() + " killed");
								break;
							}// End switch
						}catch(Exception exception){
							reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLAYER_KILL_MOB).error(exception));
						}
						return;
					}
				}
			}
		}catch(Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.UNHANDLED_ENTITYDEATHEVENT_ERROR).error(exception));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) { // onEntitySpawn(EntitySpawnEvent e) { // TODO: onCreatureSpawn
		try {
			if (mmh.config.getBoolean("wandering_trades.custom_wandering_trader", true)) {
				Entity entity = event.getEntity();
				if (entity instanceof WanderingTrader) {
					mmh.logDebug("CSE WanderingTrader spawned");
					WanderingTrader trader = (WanderingTrader) entity;
					List<MerchantRecipe> recipes = new ArrayList<>();
					final List<MerchantRecipe> oldRecipes = trader.getRecipes();

					//** Player Heads */
					if (mmh.config.getBoolean("wandering_trades.player_heads.enabled", true)) {
						// Check if default max heads is larger than number of playerheads
						if ((playerhead_recipes == null) || playerhead_recipes.isEmpty()) {
							mmh.logDebug("CSE.PH playerhead_recipes is null or empty - detailed check: null=" + (playerhead_recipes == null) + ", size=" + (playerhead_recipes != null ? playerhead_recipes.size() : 0));
							mmh.LOGGER.warn("CSE.PH playerhead_recipes is null or empty - skipping player head trades");
						} else {
							int numOfplayerheads = playerhead_recipes.size() > 0 ? playerhead_recipes.size() - 1 : 0;
							int phMaxDef = Math.max(3, numOfplayerheads); // Simplified from ternary

							int playerRandom = mmh.randomBetween(
									mmh.config.getInt("wandering_trades.player_heads.min", 0),
									mmh.config.getInt("wandering_trades.player_heads.max", phMaxDef));
							mmh.logDebug("CSE.PH playerRandom=" + playerRandom);
							if (playerRandom > 0 && numOfplayerheads >= 0) {
								mmh.logDebug("CSE.PH playerRandom > 0");
								mmh.logDebug("CSE.PH numOfplayerheads=" + numOfplayerheads);
								// Create and shuffle indices
								List<Integer> indices = new ArrayList<>(numOfplayerheads + 1);
								for (int i = 0; i <= numOfplayerheads; i++) {
									indices.add(i);
								}
								Collections.shuffle(indices, mmh.getRandom());
								// Add up to playerRandom recipes
								for (int i = 0; i < Math.min(playerRandom, indices.size()); i++) {
									recipes.add(playerhead_recipes.get(indices.get(i)));
								}
							}
						}
					}

					//** Block Heads */
					if (mmh.config.getBoolean("wandering_trades.block_heads.enabled", true)) {
						// Get the current server version
						Version currentVersion = new Version(mmh.getServer());
						mmh.logDebug("CSE BH Server Version=" + currentVersion.getVersion());

						if ((blockhead_recipes == null) || blockhead_recipes.isEmpty()) {
							mmh.logDebug("CSE.BH blockhead_recipes is null or empty - detailed check: null=" + (blockhead_recipes == null) + ", size=" + (blockhead_recipes != null ? blockhead_recipes.size() : 0));
							mmh.LOGGER.warn("CSE.BH blockhead_recipes is null or empty - skipping block head trades");
						} else {
							// Filter block heads by min_mc_version
							List<MerchantRecipe> compatibleBlockRecipes = new ArrayList<>();
							List<Integer> compatibleIndices = new ArrayList<>();
							for (int i = 0; i < blockhead_recipes.size(); i++) {
								BlockHead blockHead = headManager.loadedBlockHeads.values().toArray(new BlockHead[0])[i];
								String minMCVersion = blockHead.getMinMCVersion();
								if ((minMCVersion == null) || currentVersion.isAtLeast(minMCVersion)) {
									compatibleBlockRecipes.add(blockhead_recipes.get(i));
									compatibleIndices.add(i);
								} else {
									mmh.logDebug("CSE BH Skipping " + blockHead.getDisplayName() + " (min_mc_version " + minMCVersion + " > server version " + currentVersion.getVersion() + ")");
								}
							}

							// Use the filtered list of compatible block heads
							int numOfblockheads = compatibleBlockRecipes.size() > 0 ? compatibleBlockRecipes.size() - 1 : 0;
							int bhMaxDef = Math.max(5, numOfblockheads); // Simplified from ternary

							int min = mmh.config.getInt("wandering_trades.block_heads.min", 0);
							int max = mmh.config.getInt("wandering_trades.block_heads.max", bhMaxDef);
							mmh.logDebug("CSE BH min=" + min + " max=" + max);
							int blockRandom = mmh.randomBetween(min, max);
							mmh.logDebug("CSE blockRandom=" + blockRandom);
							if (blockRandom > 0 && numOfblockheads >= 0) {
								mmh.logDebug("CSE blockRandom > 0");
								mmh.logDebug("CSE numOfblockheads=" + numOfblockheads);
								// Create and shuffle indices
								List<Integer> indices = new ArrayList<>(numOfblockheads + 1);
								for (int i = 0; i <= numOfblockheads; i++) {
									indices.add(i);
								}
								Collections.shuffle(indices, mmh.getRandom());
								// Add up to blockRandom recipes
								for (int i = 0; i < Math.min(blockRandom, indices.size()); i++) {
									recipes.add(compatibleBlockRecipes.get(indices.get(i)));
								}
							}
						}
					}

					//** Custom Trades */
					if (mmh.config.getBoolean("wandering_trades.custom_trades.enabled", false)) {
						if ((custometrade_recipes == null) || custometrade_recipes.isEmpty()) {
							mmh.LOGGER.warn("CSE.CT custometrade_recipes is null or empty - skipping custom trades");
						} else {
							int numOfCustomTrades = custometrade_recipes.size(); // Simplified, assuming size >= 0
							int ctMaxDef = Math.max(5, numOfCustomTrades); // Simplified from ternary
							mmh.logDebug("CSE CT numOfCustomTrades=" + numOfCustomTrades);
							int customRandom = mmh.randomBetween(
									mmh.config.getInt("wandering_trades.custom_trades.min", 0),
									mmh.config.getInt("wandering_trades.custom_trades.max", ctMaxDef));
							mmh.logDebug("CSE CT customRandom=" + customRandom);
							if (customRandom > 0 && numOfCustomTrades > 0) {
								mmh.logDebug("CSE CT customRandom > 0");
								// Create and shuffle indices
								List<Integer> indices = new ArrayList<>(numOfCustomTrades);
								for (int i = 0; i < numOfCustomTrades; i++) {
									indices.add(i); // Use 0-based indices
								}
								Collections.shuffle(indices, mmh.getRandom());
								// Process up to customRandom trades
								for (int i = 0; i < Math.min(customRandom, indices.size()); i++) {
									int index = indices.get(i); // 0-based index
									int tradeNumber = index + 1; // 1-based trade number for config
									mmh.logDebug("CSE CT tradeNumber=" + tradeNumber);
									double chance = mmh.getRandom().nextDouble() * 100; // [0.0, 100.0)
									mmh.logDebug("CSE CT chance=" + chance);
									double rawChance = mmh.traderCustom.getDouble("custom_trades.trade_" + tradeNumber + ".chance", 33);
									double tradeChance = (rawChance < 1 ? rawChance * 100 : rawChance);
									mmh.logDebug("CSE CT tradeChance=" + tradeChance);
									if (tradeChance > chance) {
										recipes.add(custometrade_recipes.get(index));
									}
								}
							}
						}
					}

					//** DLC Advertisement Trades */
					if (!dlcAdvertisingEnabled) {
						List<MerchantRecipe> dlcTrades = DLCHeads.getDLCAdvertisementTrades(mmh);
						if (!dlcTrades.isEmpty()) {
							mmh.logDebug("CSE DLC Adding " + dlcTrades.size() + " DLC advertisement trades");
							recipes.addAll(dlcTrades);
						} else {
							mmh.logDebug("CSE DLC No DLC advertisement trades available (all DLCs installed or none available)");
						}
					}

					if (mmh.config.getBoolean("wandering_trades.keep_default_trades", true) || (recipes == null) || recipes.isEmpty()) {
						if ((oldRecipes != null) && !oldRecipes.isEmpty()) {
							recipes.addAll(oldRecipes);
						}
					}

					if ((recipes != null) && !recipes.isEmpty()) {
						trader.setRecipes(recipes);
					}
				}
			}
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CS_EVENT_ERROR).error(exception));
		}

	}



	/**
	 * Simulates a coin flip, returning `true` for heads and `false` for tails.
	 *
	 * @return `true` if the coin flip results in heads; `false` for tails.
	 */
	public boolean coinFlip() {
		Random random = new Random();
		if(mmh.isDev) {return true;}
		return random.nextDouble() < 0.5;
	}

	@EventHandler
	public void onStriderShiver(StriderTemperatureChangeEvent event) {
		Strider strider = event.getEntity();
		PersistentDataContainer pdc = strider.getPersistentDataContainer();
		if (event.isShivering()) {
			pdc.set(mmh.SHIVERING_KEY, PersistentDataType.STRING, "true");
		} else {
			pdc.set(mmh.SHIVERING_KEY, PersistentDataType.STRING, "false");
		}
	}

	public void announceBeheading(Entity entity, String entityName2, Player player, boolean display) {
		try { // REPORT_ANNOUNCE_ERROR "Error announcing beheading."
			UUID damagingPlayerUUID = player != null ? player.getUniqueId() : null;
			ItemStack damagingWeapon = mmh.playerWeapons.get(damagingPlayerUUID);
			String killerName = player.getDisplayName();
			String entityName = entityName2;
			if (display) {
				killerName = player.getDisplayName();
				entityName = entityName2;
			} else {
				killerName = player.getName();
				entityName = entity.getName();
			}

			if ((damagingPlayerUUID != null) && (damagingWeapon != null)) {
				Player damagingPlayer = Bukkit.getPlayer(damagingPlayerUUID);
				if (damagingPlayer != null) {
					String weaponName = damagingWeapon.getItemMeta().getDisplayName();
					if (!damagingWeapon.getItemMeta().hasDisplayName()) {
						weaponName = damagingWeapon.getType().name();
					}
					int randomIndex = (int) (Math.random() * mmh.beheadingMessages.getConfigurationSection("messages").getKeys(false).size()) + 1;
					String announcement = mmh.beheadingMessages.getString("messages.message_" + randomIndex, "%killerName% beheaded %entityName% with %weaponName%.")
							.replace("%killerName%", killerName).replace("%entityName%", entityName).replace("%weaponName%", weaponName);

					Bukkit.broadcastMessage(ChatColorUtils.setColors(announcement));
				}
			} else if ((damagingPlayerUUID != null) && (damagingWeapon == null)) { // Bare Hands?
				String weaponName = "Bare Hands";
				int randomIndex = (int) (Math.random() * mmh.beheadingMessages.getConfigurationSection("messages").getKeys(false).size()) + 1;
				String announcement = mmh.beheadingMessages.getString("messages.message_" + randomIndex, "%killerName% beheaded %entityName% with %weaponName%.")
						.replace("%killerName%", killerName).replace("%entityName%", entityName).replace("%weaponName%", weaponName);

				Bukkit.broadcastMessage(ChatColorUtils.setColors(announcement));

			}

			// Remove entity from map since it's dead.
			UUID entityUUID = entity.getUniqueId();
			if (mmh.entityPlayers.containsKey(entityUUID)) {
				mmh.entityPlayers.remove(entityUUID);
			}
			mmh.playerWeapons.remove(damagingPlayerUUID);
		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_ANNOUNCE_ERROR).error(exception));
		}
	}

	// public class EquipmentSlotResolver {

	/**
	 * Resolves the damaging weapon used based on the specified DamageCause in a player's inventory.
	 *
	 * @param playerInventory The player's inventory to search for the damaging weapon.
	 * @param damageCause The DamageCause that triggered the damage event.
	 * @return An Optional containing the damaging weapon item if found, or an empty Optional if not found.
	 */
	public Optional<ItemStack> resolveDamagingWeapon(PlayerInventory playerInventory, DamageCause damageCause) {
		try { // REPORT_RESOLVE_DAMAGE_WEAPON "Error resolving the Damaging Weapon."
			mmh.logDebug("RDW - DamageCause=" + damageCause.toString());
			switch (damageCause) {
			case ENTITY_ATTACK:
				// Check if the player is holding any item in the main hand
				ItemStack mainHandItem = playerInventory.getItemInMainHand();
				mmh.logDebug("RDW - mainHandItem=" + mainHandItem.getType().toString());
				if (!mainHandItem.getType().equals(Material.AIR)) {
					return Optional.of(mainHandItem);
				}
				break;
			case PROJECTILE:
				// Ranged damage
				Optional<ItemStack> bowItem = getWeaponItem(playerInventory, Material.BOW);
				if (bowItem.isPresent()) {
					return bowItem;
				}
				Optional<ItemStack> crossbowItem = getWeaponItem(playerInventory, Material.CROSSBOW);
				if (crossbowItem.isPresent()) {
					return crossbowItem;
				}
				Optional<ItemStack> tridentItem = getWeaponItem(playerInventory, Material.TRIDENT);
				if (tridentItem.isPresent()) {
					return tridentItem;
				}
				Optional<ItemStack> snowItem = getWeaponItem(playerInventory, Material.SNOWBALL);
				if (snowItem.isPresent()) {
					return snowItem;
				}
				Optional<ItemStack> eggItem = getWeaponItem(playerInventory, Material.EGG);
				if (eggItem.isPresent()) {
					return eggItem;
				}
				break;
			case MAGIC:
				// Potion
				return getWeaponItem(playerInventory, Material.SPLASH_POTION);
			case ENTITY_EXPLOSION:
				return Optional.of(new ItemStack(Material.TNT));
			default:
				break;
			}
		}catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_RESOLVE_DAMAGE_WEAPON).error(exception));
		}
		return Optional.empty();
	}

	/**
	 * Retrieves the player's weapon item of the specified material, if it exists in either the main hand or off hand.
	 *
	 * @param playerInventory The player's inventory to search.
	 * @param material The material type of the weapon to look for.
	 * @return An Optional containing the weapon item if found, or an empty Optional if not found.
	 */
	private Optional<ItemStack> getWeaponItem(PlayerInventory playerInventory, Material material) {
		try { // REPORT_GET_WEAPON_ERROR "Error getting weapon."
			ItemStack mainHandItem = playerInventory.getItemInMainHand();
			ItemStack offHandItem = playerInventory.getItemInOffHand();

			if (mainHandItem.getType() == material) {
				return Optional.of(mainHandItem);
			} else if (offHandItem.getType() == material) {
				return Optional.of(offHandItem);
			}
		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_GET_WEAPON_ERROR).error(exception));
		}
		return Optional.empty();
	}

	public void checkMiniBlocks() {
		if ( mmh.config.getBoolean("wandering_trades.custom_wandering_trader", true) || mmh.config.getBoolean("head_settings.mini_blocks.stonecutter", true) ) {
			/**if (!blockFile117.exists()) {
				mmh.saveResource("block_heads_1_17.yml", true);
				mmh.LOGGER.log("block_heads_1_17.yml not found! Creating in " + mmh.getDataFolder() + "");
			}
			Version curBlock1Version = new Version(mmh.fileVersions.getString("block_heads_1_17", "0.0.1"));
			if(curBlock1Version.compareTo(mmh.minBlock117Version) < 0) {
				mmh.LOGGER.log("block_heads_1_17.yml is outdated backing up...");
				try {
					MoreMobHeads.copyFile(mmh.getDataFolder() + "" + File.separatorChar + "config.yml",mmh.getDataFolder() + "" + File.separatorChar + "backup" + File.separatorChar + "block_heads_1_17.yml");
				} catch (Exception exception) {
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_COPY_FILE).error(exception));
				}
				mmh.LOGGER.log("Saving new block_heads_1_17.yml...");
				mmh.saveResource("block_heads_1_17.yml", true);
			}
			if (!blockFile1172.exists()) {
				mmh.saveResource("block_heads_1_17_2.yml", true);
				mmh.LOGGER.log("block_heads_1_17_2.yml not found! Creating in " + mmh.getDataFolder() + "");
			}
			Version curBlock2Version = new Version(mmh.fileVersions.getString("block_heads_1_17_2", "0.0.1"));
			if(curBlock2Version.compareTo(mmh.minBlock1172Version) < 0) {
				mmh.LOGGER.log("block_heads_1_17_2.yml is outdated backing up...");
				try {
					MoreMobHeads.copyFile(mmh.getDataFolder() + "" + File.separatorChar + "config.yml",mmh.getDataFolder() + "" + File.separatorChar + "backup" + File.separatorChar + "block_heads_1_17_2.yml");
				} catch (Exception exception) {
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_COPY_FILE).error(exception));
				}
				mmh.LOGGER.log("Saving new block_heads_1_17_2.yml...");
				mmh.saveResource("block_heads_1_17_2.yml", true);
			}
			if (!blockFile120.exists()) {
				mmh.saveResource("block_heads_1_20.yml", true);
				mmh.LOGGER.log("block_heads_1_20.yml not found! Creating in " + mmh.getDataFolder() + "");
			}
			Version curBlock3Version = new Version(mmh.fileVersions.getString("block_heads_1_20", "0.0.1"));
			if(curBlock3Version.compareTo(mmh.minBlock120Version) < 0) {
				mmh.LOGGER.log("block_heads_1_20.yml is outdated backing up...");
				try {
					MoreMobHeads.copyFile(mmh.getDataFolder() + "" + File.separatorChar + "config.yml",mmh.getDataFolder() + "" + File.separatorChar + "backup" + File.separatorChar + "block_heads_1_20.yml");
				} catch (Exception exception) {
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_COPY_FILE).error(exception));
				}
				mmh.LOGGER.log("Saving new block_heads_1_20.yml...");
				mmh.saveResource("block_heads_1_20.yml", true);
			}
			blockHeads = new YamlConfiguration();
			try {
				mmh.LOGGER.log("Loading block_heads_1_17.yml...");
				blockHeads.load(blockFile117);
			} catch (Exception exception) {
				MoreMobHeads.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_BLOCKHEAD_LOAD_ERROR).error(exception));
			}

			blockHeads2 = new YamlConfiguration();
			try {
				mmh.LOGGER.log("Loading block_heads_1_17_2.yml...");
				blockHeads2.load(blockFile1172);
			} catch (Exception exception) {
				MoreMobHeads.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_BLOCKHEAD_LOAD_ERROR).error(exception));
			}

			blockHeads3 = new YamlConfiguration();
			try {
				mmh.LOGGER.log("Loading block_heads_1_20.yml...");
				blockHeads3.load(blockFile120);
			} catch (Exception exception) {
				MoreMobHeads.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_BLOCKHEAD_LOAD_ERROR).error(exception));
			}//*/


			mmh.LOGGER.log("Loading PlayerHead Recipes...");
			playerhead_recipes.clear(); // Clear existing recipes
			for (PlayerHead playerHead : headManager.loadedPlayerHeads.values()) {
				ItemStack head = playerHead.getHead().clone();
				head = addPluginLore(head); // Add "MoreMobHeads" lore
				head.setAmount(playerHead.getQuantity());
				int maxUses = playerHead.getMaxUses();
				MerchantRecipe recipe = new MerchantRecipe(head, maxUses);
				ItemStack price1 = playerHead.getPrice1();
				if ((price1 != null) && !price1.getType().equals(Material.AIR)) {
					recipe.addIngredient(price1);
				}
				ItemStack price2 = playerHead.getPrice2();
				if ((price2 != null) && !price2.getType().equals(Material.AIR)) {
					recipe.addIngredient(price2);
				}
				playerhead_recipes.add(recipe);
			}
			mmh.LOGGER.log(playerhead_recipes.size() + " PlayerHead Recipes ADDED...");

			// Load BlockHead Recipes using loadedBlockHeads
			mmh.LOGGER.log("Loading BlockHead Recipes...");
			blockhead_recipes.clear(); // Clear existing recipes
			mmh.blockhead_list.clear(); // Clear existing blockhead list for /mmh give
			for (BlockHead blockHead : headManager.loadedBlockHeads.values()) {
				ItemStack head = blockHead.getHead().clone();
				head = addPluginLore(head); // Add "MoreMobHeads" lore
				head.setAmount(blockHead.getQuantity());
				// Add to list for give command
				mmh.blockhead_list.add(head);
				int maxUses = blockHead.getMaxUses();
				MerchantRecipe recipe = new MerchantRecipe(head, maxUses);
				ItemStack price1 = blockHead.getPrice1();
				if ((price1 != null) && !price1.getType().equals(Material.AIR)) {
					recipe.addIngredient(price1);
				}
				ItemStack price2 = blockHead.getPrice2();
				if ((price2 != null) && !price2.getType().equals(Material.AIR)) {
					recipe.addIngredient(price2);
				}
				blockhead_recipes.add(recipe);
			}
			mmh.LOGGER.log(blockhead_recipes.size() + " BlockHead Recipes ADDED...");

			mmh.LOGGER.log("Loading CustomTrades Recipes...");
			for (int i = 1; i < (mmh.traderCustom.getInt("custom_trades.number") + 1); i++) {
				ItemStack price1 = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".price_1", new ItemStack(Material.AIR));
				ItemStack price2 = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".price_2", new ItemStack(Material.AIR));
				ItemStack itemstack = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".itemstack", new ItemStack(Material.AIR));

				mmh.LOGGER.log("i=" + i);

				if(itemstack.getType().equals(Material.AIR)) {continue;}
				MerchantRecipe recipe = new MerchantRecipe(itemstack,
						mmh.traderCustom.getInt("custom_trades.trade_" + i + ".quantity", 1));
				if(!price1.getType().equals(Material.AIR)) {recipe.addIngredient(price1);}
				if(!price2.getType().equals(Material.AIR)) {recipe.addIngredient(price2);}
				custometrade_recipes.add(recipe);
			}
			mmh.LOGGER.log(custometrade_recipes.size() + " CustomTrades Recipes ADDED...");//*/

		}
	}


	private void convertCustomPlayerHeads(File playerHeadsDir, boolean useDefaultPlayerHeads) {
		File playerHeadsYml = new File(mmh.getDataFolder(), "player_heads.yml");
		if (!playerHeadsDir.exists()) {
			playerHeadsDir.mkdirs();
		}
		if (playerHeadsYml.exists()) {
			mmh.LOGGER.log("Found player_heads.yml, processing custom player heads...");
			File backupDir = new File(mmh.getDataFolder(), "backup");
			if (!backupDir.exists()) {
				backupDir.mkdirs();
			}
			File backupFile = new File(backupDir, "player_heads.yml");
			try {
				Files.copy(playerHeadsYml.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				mmh.LOGGER.log("Backed up player_heads.yml to " + backupFile.getAbsolutePath());
			} catch (IOException e) {
				mmh.LOGGER.warn("Failed to back up player_heads.yml: " + e.getMessage());
				e.printStackTrace();
				return; // Skip conversion if backup fails
			}
			try {
				YamlConfiguration yaml = YamlConfiguration.loadConfiguration(playerHeadsYml);
				if (yaml.contains("players")) {
					int playerCount = yaml.getInt("players.number", 0);
					int customCount = 0;
					for (int i = 1; i <= playerCount; i++) {
						String path = "players.player_" + i + ".";
						if (!yaml.contains(path)) {
							continue;
						}

						// Extract head data
						String name = yaml.getString(path + "head.name", "Unknown");
						// Skip default heads
						if (DEFAULT_PLAYER_NAMES.contains(name)) {
							mmh.logDebug("Skipping default player head: " + name);
							continue;
						}

						String uuid = yaml.getString(path + "head.uuid", uuidDEF);
						String texture = yaml.getString(path + "head.texture", textureDEF);
						List<String> lore = yaml.getStringList(path + "head.lore");
						if (lore == null) {
							lore = loreDEF;
						}
						int quantity = yaml.getInt(path + "head.amount", 1);
						int maxUses = yaml.getInt(path + "maxuses", 1);
						String noteblockSound = yaml.getString(path + "head.noteblockSound", "entity.player.hurt");

						// Extract prices
						ItemStack price1 = yaml.getItemStack(path + "price_1", new ItemStack(Material.AIR));
						ItemStack price2 = yaml.getItemStack(path + "price_2", new ItemStack(Material.AIR));

						// Validate required fields
						if (texture.equals(textureDEF) || name.equals("Unknown")) {
							mmh.LOGGER.warn("Skipping invalid player head player_" + i + ": missing name or texture");
							continue;
						}

						// Create JSON
						JSONObject json = new JSONObject();
						json.put("structure", "trade");
						json.put("lang_name", name);

						JSONObject headJson = new JSONObject();
						headJson.put("displayName", name);
						headJson.put("texture", texture);
						headJson.put("uuid", uuid);
						headJson.put("lore", lore);
						headJson.put("quantity", quantity);
						headJson.put("maxUses", maxUses);
						headJson.put("noteblockSound", noteblockSound);

						json.put("head", headJson);

						JSONObject pricesJson = new JSONObject();
						pricesJson.put("comment", "At least one price must be a valid non-AIR item");
						List<JSONObject> items = new ArrayList<>();
						if ((price1 != null) && !price1.getType().equals(Material.AIR)) {
							JSONObject price1Json = new JSONObject();
							price1Json.put("type", price1.getType().name());
							price1Json.put("amount", price1.getAmount());
							items.add(price1Json);
						}
						if ((price2 != null) && !price2.getType().equals(Material.AIR)) {
							JSONObject price2Json = new JSONObject();
							price2Json.put("type", price2.getType().name());
							price2Json.put("amount", price2.getAmount());
							items.add(price2Json);
						}
						// Ensure at least one price
						if (items.isEmpty()) {
							JSONObject airJson = new JSONObject();
							airJson.put("type", "AIR");
							airJson.put("amount", 1);
							items.add(airJson);
						}
						pricesJson.put("items", items);
						json.put("prices", pricesJson);

						// Save JSON file
						File jsonFile = new File(playerHeadsDir, name + ".json");
						if (jsonFile.exists()) {
							mmh.LOGGER.log("Overwriting existing custom player head JSON: " + jsonFile.getName());
						}
						try (FileWriter writer = new FileWriter(jsonFile)) {
							writer.write(json.toString(2));
							mmh.LOGGER.log("Created custom player head JSON: " + jsonFile.getName());
							customCount++;
						}
					}
					mmh.LOGGER.log("Processed " + customCount + " custom player heads from player_heads.yml.");

				} else {
					mmh.LOGGER.log("No players section found in player_heads.yml.");
				}
				// Delete player_heads.yml after successful processing
				if (playerHeadsYml.delete()) {
					mmh.LOGGER.log("Deleted player_heads.yml after successful conversion.");
				} else {
					mmh.LOGGER.warn("Failed to delete player_heads.yml.");
				}
			} catch (Exception exception) {
				mmh.LOGGER.log("Failed to process player_heads.yml: " + exception.getMessage());
				exception.printStackTrace();
				return;
			}
		} else {
			mmh.LOGGER.log("No player_heads.yml found, skipping custom player head conversion.");
		}
		File[] jsonFiles = playerHeadsDir.listFiles((dir, name) -> name.endsWith(".json"));
		if (!useDefaultPlayerHeads && ((jsonFiles == null) || (jsonFiles.length == 0))) {
			mmh.LOGGER.warn("No player heads available: default heads disabled and no custom heads defined in player_heads.yml.");
		}
	}

	private ItemStack addPluginLore(ItemStack item) {
		if ((item == null) || !item.hasItemMeta()) {
			return item;
		}
		ItemStack result = item.clone();
		ItemMeta meta = result.getItemMeta();
		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
		if (mmh.config.getBoolean("head_settings.lore.show_plugin_name", true)) {
			// Remove any existing "MoreMobHeads" lore to avoid duplicates
			lore.removeIf(line -> line.equals(ChatColor.AQUA + "MoreMobHeads"));
			lore.add(ChatColor.AQUA + "MoreMobHeads");
		}
		meta.setLore(lore.isEmpty() ? null : lore);
		result.setItemMeta(meta);
		return result;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		giveHeadCommand.loadPlayerHead(event.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		headManager.getOnlinePlayerHeads().remove(event.getPlayer().getUniqueId());
		// Remove any pending targets involving this player
		headManager.getPendingTargets().entrySet().removeIf(entry -> entry.getValue().equals(event.getPlayer()));
		headManager.getPendingHeadTypes().remove(event.getPlayer());
	}

	@Override public GiveHeadCommand getGiveHeadCommand() {
		return giveHeadCommand;
	}

	@Override public ViewHeadsCommand getViewHeadsCommand() {
		return viewHeadsCommand;
	}

	public Version getServerVersion() {
		return new Version(Bukkit.getServer());
	}

	@Override
	public void loadHeadsAndRecipes() {
		try {
			// Process DLCs
			DLCInstaller installer = new DLCInstaller(mmh);
			installer.processDLCs();

			// Copy heads from JAR
			copyHeadsFromJar();

			// Process update.json
			JsonUpdateHandler updateHandler = new JsonUpdateHandler(mmh);
			updateHandler.processUpdates(new File(mmh.getDataFolder(), "heads").getAbsolutePath());

			// Convert player heads
			File playerHeadsDir = new File(mmh.getDataFolder(), "heads/player");
			boolean useDefaultPlayerHeads = mmh.config.getBoolean("head_settings.player_heads.use_default_player_heads", true);
			convertCustomPlayerHeads(playerHeadsDir, useDefaultPlayerHeads);

			// Load heads
			loadMobHeads();
			loadPlayerHeads();
			loadBlockHeads();
			loadMiniBlocks();

			// Populate head lists
			populateHeadLists();

			// Load recipes
			loadRecipes();
		} catch (Exception e) {
			mmh.LOGGER.warn("Error loading heads and recipes: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void copyHeadsFromJar() throws IOException {
		File headsDir = new File(mmh.getDataFolder(), "heads");
		boolean useDefaultPlayerHeads = mmh.config.getBoolean("head_settings.player_heads.use_default_player_heads", true);

		// First copy
		if (useDefaultPlayerHeads) {
			JarUtil.copyFolderFromJar("heads", mmh.getDataFolder(), JarUtil.CopyOption.COPY_IF_NOT_EXIST);
			mmh.LOGGER.log("Successfully copied heads directory (new files only, first copy) to " + headsDir.getAbsolutePath());
		} else {
			JarUtil.copyFolderFromJar("heads/entity", new File(mmh.getDataFolder(), "heads/entity"), JarUtil.CopyOption.COPY_IF_NOT_EXIST);
			JarUtil.copyFolderFromJar("heads/block", new File(mmh.getDataFolder(), "heads/block"), JarUtil.CopyOption.COPY_IF_NOT_EXIST);
			mmh.LOGGER.log("Skipped copying default player heads (first copy); copied entity and block heads to " + headsDir.getAbsolutePath());
		}

		// Second copy
		if (useDefaultPlayerHeads) {
			JarUtil.copyFolderFromJar("heads", mmh.getDataFolder(), JarUtil.CopyOption.COPY_IF_NOT_EXIST);
			mmh.LOGGER.log("Successfully copied heads directory (new files only, second copy) to " + headsDir.getAbsolutePath());
		} else {
			JarUtil.copyFolderFromJar("heads/entity", new File(mmh.getDataFolder(), "heads/entity"), JarUtil.CopyOption.COPY_IF_NOT_EXIST);
			JarUtil.copyFolderFromJar("heads/block", new File(mmh.getDataFolder(), "heads/block"), JarUtil.CopyOption.COPY_IF_NOT_EXIST);
			mmh.LOGGER.log("Skipped copying default player heads (second copy); copied entity and block heads to " + headsDir.getAbsolutePath());
		}
	}

	public void loadMobHeads() {
		String directoryPath = mmh.getDataFolder() + "/heads/entity";
		mmh.LOGGER.log("Loading mob heads from: " + directoryPath);
		mobHeadLoader = new MobHeadLoader(mmh);
		mobHeadLoader.loadAllMobHeads(directoryPath);
		headManager.loadedMobHeads().clear();
		for (MobHeadData data : mobHeadLoader.getMobHeadDataList()) {
			MobHead mobHead = mobHeadLoader.createMobHead(data);
			String key = mobHead.getLangName();
			if (headManager.loadedMobHeads().containsKey(key)) {
				mmh.LOGGER.warn("Duplicate mob head key found: " + key + " (display name: " + mobHead.getDisplayName() + ")");
				continue;
			}
			headManager.loadedMobHeads().put(key, mobHead);
			headManager.mobHeadsNameList().add(key);
		}
		mmh.LOGGER.log(headManager.loadedMobHeads().isEmpty() ? "No mob heads were loaded." : "Successfully loaded " + headManager.loadedMobHeads().size() + " mob heads:");
	}

	private void loadPlayerHeads() {
		String directoryPath = mmh.getDataFolder() + "/heads/player";
		mmh.LOGGER.log("Loading player heads from: " + directoryPath);
		playerHeadLoader = new PlayerHeadLoader(mmh);
		playerHeadLoader.loadAllPlayerHeads(directoryPath);
		headManager.getJsonPlayerHeads().clear();
		headManager.loadedPlayerHeads().clear();
		for (PlayerHeadData data : playerHeadLoader.getPlayerHeadDataList()) {
			PlayerHead playerHead = playerHeadLoader.createPlayerHead(data);
			String key = playerHead.getDisplayName().toLowerCase().replaceAll("§[0-9a-fk-or]", "");
			if (headManager.loadedPlayerHeads().containsKey(key)) {
				continue;
			}
			headManager.loadedPlayerHeads().put(key, playerHead);
			headManager.getJsonPlayerHeads().add(playerHead.getHead());
		}
		mmh.LOGGER.log(headManager.loadedPlayerHeads().isEmpty() ? "No player heads were loaded." : "Successfully loaded " + headManager.loadedPlayerHeads().size() + " player heads:");
	}

	private void loadBlockHeads() {
		String directoryPath = mmh.getDataFolder() + "/heads/block"; // Corrected path
		mmh.LOGGER.log("Loading block heads from: " + directoryPath);
		blockHeadLoader = new BlockHeadLoader(mmh);
		blockHeadLoader.loadAllBlockHeads(directoryPath);
		headManager.loadedBlockHeads().clear();
		for (BlockHeadData data : blockHeadLoader.getBlockHeadDataList()) {
			BlockHead blockHead = blockHeadLoader.createBlockHead(data);
			String key = blockHead.getDisplayName().toLowerCase().replaceAll("§[0-9a-fk-or]", "");
			headManager.loadedBlockHeads().put(key, blockHead);
			headManager.blockHeadsNameList().add(key);
		}
		mmh.LOGGER.log(headManager.loadedBlockHeads().isEmpty() ? "No block heads were loaded." : "Successfully loaded " + headManager.loadedBlockHeads().size() + " block heads:");
	}

	private void loadMiniBlocks() {
		if (!mmh.config.getBoolean("head_settings.mini_blocks.stonecutter", false)) {
			mmh.LOGGER.log("MiniBlock Stonecutter recipes are disabled - skipping MiniBlock loading.");
			return;
		}

		String directoryPath = mmh.getDataFolder() + "/heads/block";
		mmh.LOGGER.log("Loading mini blocks from: " + directoryPath);
		miniBlockLoader = new MiniBlockLoader(mmh);
		miniBlockLoader.loadAllMiniBlocks(new File(directoryPath).getAbsolutePath());
		mmh.logDebug("Loaded " + miniBlockLoader.getMiniBlocks().size() + " MiniBlocks");

		headManager.loadedMiniBlocks().clear();
		headManager.miniBlocksList().clear();
		if (miniBlockLoader.getMiniBlocks().isEmpty()) {
			mmh.LOGGER.warn("No mini blocks loaded - skipping Stonecutter recipe registration");
			return;
		}

		for (MiniBlock miniBlock : miniBlockLoader.getMiniBlocks()) {
			String key = miniBlock.getDisplayName().toLowerCase().replaceAll("§[0-9a-fk-or]", "");
			headManager.loadedMiniBlocks().put(key, miniBlock);
			ItemStack head = miniBlock.getHead();
			head = addPluginLore(head);
			headManager.miniBlocksList().add(head);
		}
		headManager.miniBlocksList().sort(Comparator.comparing(item -> item.getItemMeta().getDisplayName().replaceAll("§[0-9a-fk-or]", "")));
		mmh.LOGGER.log("Successfully loaded " + headManager.loadedMiniBlocks().size() + " MiniBlocks:");
		mmh.LOGGER.log("Total MiniBlocks (not shown in GUI): " + headManager.miniBlocksList().size());

		int quantity = mmh.config.getInt("head_settings.mini_blocks.perblock", 1);
		MiniBlockRecipes miniblockrecipes = new MiniBlockRecipes(mmh, miniBlockLoader.getMiniBlocks(), quantity);
		miniblockrecipes.register();
	}

	private void populateHeadLists() {
		// Mob heads
		Map<ItemStack, MobHead> mobHeadMap = new HashMap<>();
		headManager.mobHeadsList().clear();
		for (MobHead mobHead : headManager.loadedMobHeads().values()) {
			ItemStack head = mobHead.getHead();
			head = addPluginLore(head);
			headManager.mobHeadsList().add(head);
			mobHeadMap.put(head, mobHead);
		}
		headManager.mobHeadsList().sort((head1, head2) -> {
			MobHead mobHead1 = mobHeadMap.get(head1);
			MobHead mobHead2 = mobHeadMap.get(head2);
			String langName1 = mobHead1.getLangName();
			String langName2 = mobHead2.getLangName();
			return langName1.compareTo(langName2);
		});

		// Player heads
		Map<ItemStack, PlayerHead> playerHeadMap = new HashMap<>();
		headManager.playerHeadsList().clear();
		for (PlayerHead playerHead : headManager.loadedPlayerHeads().values()) {
			ItemStack head = playerHead.getHead();
			headManager.playerHeadsList().add(head);
			playerHeadMap.put(head, playerHead);
		}
		headManager.playerHeadsList().sort((head1, head2) -> {
			PlayerHead playerHead1 = playerHeadMap.get(head1);
			PlayerHead playerHead2 = playerHeadMap.get(head2);
			String langName1 = playerHead1.getLangName();
			String langName2 = playerHead2.getLangName();
			return langName1.compareTo(langName2);
		});

		// Block heads
		Map<ItemStack, BlockHead> blockHeadMap = new HashMap<>();
		headManager.blockHeadsList().clear();
		for (BlockHead blockHead : headManager.loadedBlockHeads().values()) {
			ItemStack head = blockHead.getHead();
			head = addPluginLore(head);
			ItemStack headForGui = head.clone();
			headForGui.setAmount(1);
			headManager.blockHeadsList().add(headForGui);
			blockHeadMap.put(headForGui, blockHead);
		}
		headManager.blockHeadsList().sort((head1, head2) -> {
			BlockHead blockHead1 = blockHeadMap.get(head1);
			BlockHead blockHead2 = blockHeadMap.get(head2);
			String langName1 = blockHead1.getLangName();
			String langName2 = blockHead2.getLangName();
			return langName1.compareTo(langName2);
		});
	}

	private void loadRecipes() {
		// Player head recipes
		playerhead_recipes.clear();
		for (PlayerHead playerHead : headManager.loadedPlayerHeads().values()) {
			ItemStack head = playerHead.getHead().clone();
			head = addPluginLore(head);
			head.setAmount(playerHead.getQuantity());
			int maxUses = playerHead.getMaxUses();
			MerchantRecipe recipe = new MerchantRecipe(head, maxUses);
			ItemStack price1 = playerHead.getPrice1();
			if ((price1 != null) && !price1.getType().equals(Material.AIR)) {
				recipe.addIngredient(price1);
			}
			ItemStack price2 = playerHead.getPrice2();
			if ((price2 != null) && !price2.getType().equals(Material.AIR)) {
				recipe.addIngredient(price2);
			}
			playerhead_recipes.add(recipe);

		}
		mmh.LOGGER.log(playerhead_recipes.size() + " PlayerHead Recipes ADDED...");

		// Block head recipes
		blockhead_recipes.clear();
		for (BlockHead blockHead : headManager.loadedBlockHeads().values()) {
			ItemStack head = blockHead.getHead().clone();
			head.setAmount(blockHead.getQuantity());
			int maxUses = blockHead.getMaxUses();
			MerchantRecipe recipe = new MerchantRecipe(head, maxUses);
			ItemStack price1 = blockHead.getPrice1();
			if ((price1 != null) && !price1.getType().equals(Material.AIR)) {
				recipe.addIngredient(price1);
			}
			ItemStack price2 = blockHead.getPrice2();
			if ((price2 != null) && !price2.getType().equals(Material.AIR)) {
				recipe.addIngredient(price2);
			}
			blockhead_recipes.add(recipe);
		}
		mmh.LOGGER.log(blockhead_recipes.size() + " BlockHead Recipes ADDED...");

		// Custom trades recipes
		custometrade_recipes.clear();
		for (int i = 1; i <= mmh.traderCustom.getInt("custom_trades.number"); i++) {
			ItemStack price1 = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".price_1", new ItemStack(Material.AIR));
			ItemStack price2 = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".price_2", new ItemStack(Material.AIR));
			ItemStack itemstack = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".itemstack", new ItemStack(Material.AIR));
			if (itemstack.getType().equals(Material.AIR)) {
				continue;
			}
			MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.traderCustom.getInt("custom_trades.trade_" + i + ".quantity", 1));
			if (!price1.getType().equals(Material.AIR)) {
				recipe.addIngredient(price1);
			}
			if (!price2.getType().equals(Material.AIR)) {
				recipe.addIngredient(price2);
			}
			custometrade_recipes.add(recipe);
		}
		mmh.LOGGER.log(custometrade_recipes.size() + " CustomTrades Recipes ADDED...");
	}

}