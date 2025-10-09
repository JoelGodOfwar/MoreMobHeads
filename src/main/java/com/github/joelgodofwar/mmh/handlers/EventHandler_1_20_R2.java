package com.github.joelgodofwar.mmh.handlers;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.MoreMobHeadsLib;
import com.github.joelgodofwar.mmh.command.Command_1_20_R2;
import com.github.joelgodofwar.mmh.command.GiveHeadCommand;
import com.github.joelgodofwar.mmh.command.ViewHeadsCommand;
import com.github.joelgodofwar.mmh.common.PluginLibrary;
import com.github.joelgodofwar.mmh.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.mmh.common.error.Report;
import com.github.joelgodofwar.mmh.enums.Perms;
import com.github.joelgodofwar.mmh.events.BeheadingHandler;
import com.github.joelgodofwar.mmh.util.DLCInstaller;
import com.github.joelgodofwar.mmh.util.VerifyConfig;
import lib.github.joelgodofwar.coreutils.util.Version;
import com.github.joelgodofwar.mmh.util.heads.*;
import com.github.joelgodofwar.mmh.util.tools.JarUtil;
import lib.github.joelgodofwar.coreutils.util.StrUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.entity.TropicalFish.Pattern;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class EventHandler_1_20_R2 implements Listener, MMHEventHandler {
	/** Variables */
	MoreMobHeads mmh;
	double defpercent = 13.0;
	String world_whitelist;
	String world_blacklist;
	String mob_whitelist;
	String mob_blacklist;
	boolean debug;
	public YamlConfiguration beheadingMessages = new YamlConfiguration();

	// Head default values.
	String nameDEF = "Name Not Found";
	String uuidDEF = "40404040-4040-4040-4040-404040404040";
	String textureDEF = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY1NDljZjhiNWE1MWUwZmFkM2MyMmY5YTY3ZDg3Mjc2ZDdhMzdiZjY0Zjk1ODgwMDI2ZDlkMzE5ZTMyMjhiNSJ9fX0=";
	ArrayList<String> loreDEF = new ArrayList<>(List.of("§cNotify an Admin§e!"));

	List<MerchantRecipe> playerhead_recipes = new ArrayList<MerchantRecipe>();
	List<MerchantRecipe> blockhead_recipes = new ArrayList<MerchantRecipe>();
	List<MerchantRecipe> custometrade_recipes = new ArrayList<MerchantRecipe>();
	//int BHNum, BHNum2, BHNum3, BHNum4;
	VerifyConfig verify;// = new VerifyConfig(mmh.config);
	DetailedErrorReporter reporter;
	private GiveHeadCommand giveHeadCommand;
	private ViewHeadsCommand viewHeadsCommand;
	PlayerHeadLoader playerHeadLoader;
	BlockHeadLoader blockHeadLoader;
	MobHeadLoader mobHeadLoader;
	MiniBlockLoader miniBlockLoader;
	private HeadManager headManager;
	Double player_Chance;
	Double named_Chance;
	private boolean dlcAdvertisingEnabled;
	boolean player_announce_enabled;
	boolean player_announce_display;
	boolean mob_announce_enabled;
	boolean mob_announce_display;
	Player playerBedKiller;
	public BeheadingHandler beheading;

	// Default player names from player_heads.yml
	private static final List<String> DEFAULT_PLAYER_NAMES = Arrays.asList(
			// Hermits
			"Etho", "falsesymmetry", "BdoubleO100", "Renthedog", "Grian", "Welsknight", "GoodTimesWithScar", "hypnotizd", "VintageBeef", "Xisuma", "joehillssays", "xBCrafted",
			"impulseSV", "Zedaph", "Docm77", "ZombieCleo", "Keralis", "Mumbo", "cubfan135", "iJevin", "Tango", "Skizzleman", "GeminiTay", "PearlescentMoon", "Smallishbeans",
			// Former Hermits
			"Stressmonster101", "Tinfoilchef", "Jessassin", "Biffa2001", "PythonGB", "iskall85", "generikB",
			// Wish They Were Hermits
			"Dataless822"
			);

	public EventHandler_1_20_R2(MoreMobHeads plugin, HeadManager headManager) {
		// TODO: Top of code
		try { // REPORT_EVENT_HANDLER_LOAD "Error while loading EventHandler."
			//** Set variables */
			mmh = plugin;
			this.player_Chance = mmh.playerChance;
			this.named_Chance = mmh.namedChance;
			this.headManager = headManager;
			reporter = new DetailedErrorReporter(mmh);
			verify = new VerifyConfig(mmh);
			mmh.LOGGER.log("Loading 1.20.6/1.21 EventHandler...");
			long startTime = System.currentTimeMillis();
			//mmh.getCommand("mmh").setExecutor(this);
			world_whitelist = mmh.config.getString("world.whitelist", "");
			world_blacklist = mmh.config.getString("world.blacklist", "");
			mob_whitelist = mmh.config.getString("mob.whitelist", "");
			mob_blacklist = mmh.config.getString("mob.blacklist", "");
			debug = plugin.debug;
			dlcAdvertisingEnabled = mmh.getConfig().getBoolean("global_settings.disable_dlc_display", false);
			player_announce_enabled = mmh.config.getBoolean("head_settings.player_heads.announce_kill.enabled", true);
			player_announce_display = mmh.config.getBoolean("head_settings.player_heads.announce_kill.displayname", true);
			mob_announce_enabled = mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true);
			mob_announce_display = mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true);
			beheading = new BeheadingHandler(mmh);

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


			// Load heads for currently online players
			for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				giveHeadCommand.loadPlayerHead(onlinePlayer);
			}


			mmh.LOGGER.log("EventHandler_1_20_R2 took " + mmh.LoadTime(startTime) + " to load");

		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_EVENT_HANDLER_LOAD).error(exception));
		}
		// Register the command with ViewHeadsCommand
		Objects.requireNonNull(mmh.getCommand("mmh")).setExecutor(new Command_1_20_R2(mmh, headManager, giveHeadCommand, viewHeadsCommand, mmh.eventHandler));
	}



	/** Events go here */
	@SuppressWarnings({"unused"})
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDeathEvent(EntityDeathEvent event) {// TODO: EntityDeathEvent
		try {
			LivingEntity entity = event.getEntity();
			// Verify that the killer is a player
			if (!(entity.getKiller() instanceof Player) && !(entity.getKiller() instanceof Creeper)
					&& !(entity.getLastDamageCause().getCause() == DamageCause.BLOCK_EXPLOSION)) {
				return;
			}
			DamageCause theCause = event.getEntity().getLastDamageCause().getCause();
			if(theCause == DamageCause.BLOCK_EXPLOSION){
				// do nothing for now
				mmh.logDebug("EDE - entity.getKiller()=" + entity.getKiller());
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

			String worldName = world.getName(); // Cache for readability/perf
			try { // REPORT_WHITE_BLACK_LIST "Unable to parse global whitelist/blacklist"
				boolean hasWhitelist = (world_whitelist != null) && !world_whitelist.isEmpty();
				boolean hasBlacklist = (world_blacklist != null) && !world_blacklist.isEmpty();
				boolean onWhitelist = StrUtils.stringContains(world_whitelist, worldName);
				boolean onBlacklist = StrUtils.stringContains(world_blacklist, worldName);

				if (hasWhitelist) {
					// Whitelist present: must be on it (overrides blacklist if present)
					if (!onWhitelist) {
						mmh.logDebug("EDE - World - Not on whitelist.");
						return;
					}
					// If on whitelist, proceed regardless of blacklist
				} else if (hasBlacklist) {
					// No whitelist, but blacklist present: block if on it
					if (onBlacklist) {
						mmh.logDebug("EDE - World - On blacklist.");
						return;
					}
					// If not on blacklist, proceed
				}
				// Neither list: or on whitelist: proceed (no log needed)
			} catch (Exception exception) {
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_WHITE_BLACK_LIST).error(exception));
			}

			Player theKiller = entity.getKiller() != null ? entity.getKiller() : beheading.getPlayerBedKiller();
			if (theKiller == null) {
				DamageCause lastCause = entity.getLastDamageCause() != null ? entity.getLastDamageCause().getCause() : null;
				if (lastCause == DamageCause.ENTITY_EXPLOSION) {
					theKiller = beheading.getPlayerCreeperKiller();
				} else if (lastCause == DamageCause.BLOCK_EXPLOSION) {
					theKiller = beheading.getPlayerBedKiller();
				}
			}
			if (theKiller == null) {
				mmh.logDebug("EDE - No attributable player killer found (e.g., pure block explosion without player). Skipping head drop.");
				return; // Early exit if no player to credit/notify
			}
			if ((entity instanceof Player) && ((entity.getKiller() instanceof Player) || (entity.getKiller() instanceof Creeper))) {
				// TODO: Player Kill Player
				try{ // REPORT_PLAYER_KILL_PLAYER "Unable to parse Player Death."
					mmh.logDebug("EDE Entity is Player");
					if ((Perms.PLAYERS.hasPermission(theKiller) || mmh.isDev) || (entity.getKiller() instanceof Creeper)) {

						boolean drop_It = mmh.DropIt(event, player_Chance, entity.getKiller() );
						mmh.logDebug("EDE DropIt=" + drop_It);
						mmh.logDebug("EDE chance_percent.player=" + player_Chance );
						if (drop_It) {
							// Player daKiller = entity.getKiller();

							mmh.logDebug("EDE Killer is Player line:1073");

							Player victim = (Player) entity;
                            String killerName;
							String entityName;
							PlayerProfile profile = victim.getPlayerProfile();
							ItemStack head = new ItemStack(Material.PLAYER_HEAD);
							SkullMeta meta = (SkullMeta) head.getItemMeta();
							PlayerTextures textures = profile.getTextures();

							mmh.logDebug(" EDE PlayerProfile getName: " + profile.getName());
							mmh.logDebug(" EDE PlayerProfile UUID: " + profile.getUniqueId());
							mmh.logDebug(" EDE PlayerProfile Skin: " + profile.getTextures().getSkin());

							killerName = mmh.getNickname(theKiller);
							entityName = mmh.getNickname(victim);

							head = mmh.makeHead(entityName, profile.getTextures().getSkin().toString(), profile.getUniqueId().toString(), entity.getType(), entity.getKiller());
							boolean isCanceled = mmh.callDropEvent(entity, entity.getKiller(), head, head.getItemMeta().getDisplayName(), HeadUtils.convertURLToBase64(profile.getTextures().getSkin().toString()), profile.getUniqueId().toString(), head.getItemMeta().getLore(), "entity.player.hurt");
							if (!isCanceled) {
								mmh.playerGiveOrDropHead(theKiller, head);
								mmh.logDebug("EDE " + ((Player) entity).getDisplayName() + " Player Head Dropped");
								if (player_announce_enabled) {
									beheading.announceBeheading(entity, entityName, theKiller, player_announce_display);
								}
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
				if ((entity.getKiller() instanceof Player) || (entity.getKiller() instanceof Creeper)
				|| (entity.getLastDamageCause().getCause().equals(DamageCause.BLOCK_EXPLOSION))) {

					String name = event.getEntityType().toString().replace(" ", "_");
					mmh.logDebug("EDE NM name=" + name);
					String isNametag = null;
					@Nonnull
					PersistentDataContainer pdc = entity.getPersistentDataContainer();
					isNametag = entity.getPersistentDataContainer().get(mmh.NAMETAG_KEY, PersistentDataType.STRING);// .getScoreboardTags();//
					if ((isNametag != null)) {
						mmh.logDebug("EDE NM isNametag=" + isNametag);
					}

					if ( ( (entity.getLastDamageCause().getCause().equals(DamageCause.BLOCK_EXPLOSION)) || (entity.getKiller() instanceof Creeper) )
							|| (Perms.MOBS.hasPermission(theKiller)  || mmh.isDev) ) {

						if ( ((entity.getKiller() instanceof Creeper) || (Perms.NAMETAG.hasPermission(theKiller)  || mmh.isDev) ) && (isNametag != null)) {

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
										String mobNameLower = entity.getCustomName().toLowerCase();
										if (mmh.getServer().getPluginManager().getPlugin("SilenceMobs") != null) {
											if (mobNameLower.contains("silenceme") || mobNameLower.contains("silence me") || mobNameLower.contains("silenced")) {
												return;
											}
										}
										boolean enforce_whitelist = mmh.config.getBoolean("head_settings.player_heads.whitelist.enforce", false);
										boolean enforce_blacklist = mmh.config.getBoolean("head_settings.player_heads.blacklist.enforce", false);
										String whitelistStr = mmh.config.getString("head_settings.player_heads.whitelist.player_head_whitelist", "").toLowerCase();
										String blacklistStr = mmh.config.getString("head_settings.player_heads.blacklist.player_head_blacklist", "").toLowerCase();

										boolean hasWhitelist = enforce_whitelist && !whitelistStr.isEmpty();
										boolean hasBlacklist = enforce_blacklist && !blacklistStr.isEmpty();
										boolean onWhitelist = hasWhitelist && whitelistStr.contains(mobNameLower);
										boolean onBlacklist = hasBlacklist && blacklistStr.contains(mobNameLower);
										if (mmh.DropIt(event, named_Chance, theKiller)) {
											boolean allowed = true;
											if (hasWhitelist) {
												// Whitelist present: must be on it (overrides blacklist)
												if (!onWhitelist) {
													allowed = false;
													mmh.logDebug("EDE - Named Mob - Not on whitelist: " + entity.getCustomName());
												}
											} else if (hasBlacklist) {
												// No whitelist, but blacklist: block if on it
												if (onBlacklist) {
													allowed = false;
													mmh.logDebug("EDE - Named Mob - On blacklist: " + entity.getCustomName());
												}
											}
											if(allowed) {
												PlayerProfile profile = Bukkit.createPlayerProfile(entity.getCustomName());
												ItemStack head = new ItemStack(Material.PLAYER_HEAD);
												SkullMeta meta = (SkullMeta) head.getItemMeta();
												PlayerTextures textures = profile.getTextures();

												profile.setTextures(textures);
												meta.setOwnerProfile(profile);
												meta.setNoteBlockSound(NamespacedKey.minecraft( "entity.player.hurt" ));
												ArrayList<String> lore = mmh.modifyLore((ArrayList<String>) head.getItemMeta().getLore(), false, theKiller );
												meta.setLore(lore);
												meta.setLore(lore);
												head.setItemMeta(meta);
												if(entity.getEquipment().getHelmet().getType() == Material.PLAYER_HEAD) {
													head = entity.getEquipment().getHelmet();
													boolean isCanceled = mmh.callDropEvent(entity, theKiller, head, head.getItemMeta().getDisplayName(), null, null, head.getItemMeta().getLore(), null);
													if (!isCanceled) {
														Drops.add(head);
														mmh.logDebug("EDE " + entity.getCustomName() + " Head Dropped");
														if (mob_announce_enabled) {
															beheading.announceBeheading(entity, entity.getCustomName(),
																	theKiller, mob_announce_display);
														}
													}
												} else if(!entity.getEquipment().getHelmet().isSimilar(head)) {
													mmh.logDebug(" EDE NamedMob is not wearing PlayerHead drop canceled.");
												}
												return;
											}
										}
										// General mob whitelist/blacklist check (post-named logic, if no drop)
										if ((mob_whitelist != null) && !mob_whitelist.isEmpty()) {
											boolean hasMobBlacklist = (mob_blacklist != null) && !mob_blacklist.isEmpty();
											boolean onMobWhitelist = StrUtils.stringContains(mob_whitelist, name);
											boolean onMobBlacklist = hasMobBlacklist && StrUtils.stringContains(mob_blacklist, name);

											if (hasMobBlacklist) {
												// Both present: whitelist overrides (proceed only if on WL)
												if (!onMobWhitelist) {
													mmh.LOGGER.log("EDE - Mob - Not on whitelist (with blacklist). Mob=" + name);
													return;
												}
											} else {
												// Only whitelist: block if not on it
												if (!onMobWhitelist) {
													mmh.LOGGER.log("EDE - Mob - Not on whitelist. Mob=" + name);
													return;
												}
											}
										} else if ((mob_blacklist != null) && !mob_blacklist.isEmpty()) {
											// Only blacklist: block if on it
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

						mmh.logDebug("EDE NM LivingEntity = " + (event.getEntity() instanceof LivingEntity));
						MobHeadData mhd;

						try { // REPORT_PLAYER_KILL_MOB "Unable to parse Mob Kill."
							switch (name) {
							case "CREEPER":
								// ConfigHelper.Double(chanceConfig,
								Creeper creeper = (Creeper) event.getEntity();
								if (creeper.isPowered()) {
									name = "CREEPER.CHARGED";
								}else {
									name = "CREEPER.NORMAL";
								}
								mhd = headManager.getMobOrDefault( name.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									mmh.logDebug("EDE Creeper vanilla=" + mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.creeper", false));
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.creeper", false) && (!name.equals("CREEPER.CHARGED"))) {
										Drops = mmh.addHeadToDrops(entity, theKiller, new ItemStack(Material.CREEPER_HEAD), null, null, null, null, null, Drops);
									} else {
										ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
										Drops = mmh.addHeadToDrops(entity, theKiller,
												HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
												, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									}
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Creeper Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName()).replace(" Head", ""),
													theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "ENDER_DRAGON":
								// ConfigHelper.Double(chanceConfig,
								mhd = headManager.getMobOrDefault( name.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									mmh.logDebug("EDE Ender Dragon vanilla=" + mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.ender_dragon", false));
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.ender_dragon", false)) {
										Drops = mmh.addHeadToDrops(event.getEntity(), theKiller, new ItemStack(Material.DRAGON_HEAD), null, null, null, null, null, Drops);
									} else {
										ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
										Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
												HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
												, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									}
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Ender Dragon Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName()).replace(" Head", "")
													, theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "PIGLIN":
								mhd = headManager.getMobOrDefault( name.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.piglin", false)) {
										Drops = mmh.addHeadToDrops(event.getEntity(), theKiller, new ItemStack(Material.PIGLIN_HEAD), null, null, null, null, null, Drops);
									} else {
										ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
										Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
												HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
												, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									}
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Ender Dragon Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
													.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "SKELETON":
								mhd = headManager.getMobOrDefault( name.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.skeleton", false)) {
										Drops = mmh.addHeadToDrops(event.getEntity(), theKiller, new ItemStack(Material.SKELETON_SKULL), null, null, null, null, null, Drops);
									} else {
										ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
										Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
												HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
												, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									}
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Skeleton Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
													.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "WITHER_SKELETON":
								// ConfigHelper.Double(chanceConfig, "chance_percent.wither_skeleton",
								// defpercent)
								mhd = headManager.getMobOrDefault( name.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.wither_skeleton", false)) {
										Drops = mmh.addHeadToDrops(event.getEntity(), theKiller, new ItemStack(Material.WITHER_SKELETON_SKULL), null, null, null, null, null, Drops);
									} else {
										ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
										Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
												HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
												, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									}
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Wither Skeleton Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
													.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "ZOMBIE":
								// ConfigHelper.Double(chanceConfig, "chance_percent.zombie", defpercent)
								mhd = headManager.getMobOrDefault( name.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.zombie", false)) {
										Drops = mmh.addHeadToDrops(event.getEntity(), theKiller, new ItemStack(Material.ZOMBIE_HEAD), null, null, null, null, null, Drops);
									} else {
										ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
										Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
												HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
												, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									}
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Zombie Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
													.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "TROPICAL_FISH":
								TropicalFish daFish = (TropicalFish) entity;
								DyeColor daFishBody = daFish.getBodyColor();
								DyeColor daFishPatternColor = daFish.getPatternColor();
								Pattern daFishType = daFish.getPattern();
								mmh.logDebug("bodycolor=" + daFishBody.toString() + "\nPatternColor=" + daFishPatternColor.toString()
								+ "\nPattern=" + daFishType.toString());
								// TropicalFishHeads daFishEnum = TropicalFishHeads.getIfPresent(name);
								String daFishName = mmh.getNamedTropicalFishName(daFishType, daFishBody, daFishPatternColor);
								mmh.logDebug("daFishName: " + daFishName);
								mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + daFishName.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE TROPICAL_FISH:" + daFishName + " head dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
													.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "WITHER":
								String name2 = name + "_NORMAL";
								mhd = headManager.getMobOrDefault( name2.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE " + name2 + " Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
													.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
									if (coinFlip()) {
										name2 = name + "_PROJECTILE";
										mhd = headManager.getMobOrDefault( name2.toLowerCase() ).getData();
										lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
										Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
												HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
												, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
										if(HeadUtils.containsHead(Drops)) {
											mmh.logDebug("EDE " + name2 + " Head Dropped");
										}
									}
									if (coinFlip()) {
										name2 = name + "_BLUE_PROJECTILE";
										mhd = headManager.getMobOrDefault( name2.toLowerCase() ).getData();
										lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
										Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
												HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
												, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
										if(HeadUtils.containsHead(Drops)) {
											mmh.logDebug("EDE " + name2 + " Head Dropped");
										}
									}
								}
								break;
							case "FOX":
								Fox dafox = (Fox) entity;
								String dafoxtype = dafox.getFoxType().toString();
								mmh.logDebug("EDE dafoxtype=" + dafoxtype);
								mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + dafoxtype.toLowerCase() ).getData();
								// ConfigHelper.Double(chanceConfig, "chance_percent.fox." +
								// dafoxtype.toLowerCase(), defpercent)
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Fox Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity,
													mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
															.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "CAT":
								Cat dacat = (Cat) entity;
								String dacattype = dacat.getCatType().toString();
								mmh.logDebug("entity cat=" + dacat.getCatType());
								mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + dacattype.toLowerCase() ).getData();
								// ConfigHelper.Double(chanceConfig, "chance_percent.cat." +
								// dacattype.toLowerCase(), defpercent)
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Cat Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
													.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "OCELOT":
								// ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(),
								// defpercent)
								mhd = headManager.getMobOrDefault( name.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE " + name + " Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
															.replace(" Head", ""), theKiller,
													mob_announce_display);
										}
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
								if ((daAnger >= 1) && (daNectar)) {
									// ConfigHelper.Double(chanceConfig, "chance_percent.bee.angry_pollinated",
									// defpercent)
									mhd = headManager.getMobOrDefault( "bee.angry_pollinated" ).getData();
									if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
										ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
										Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
												HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
												, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
										if(HeadUtils.containsHead(Drops)) {
											mmh.logDebug("EDE Angry Pollinated Bee Head Dropped");
											if (mob_announce_enabled) {
												beheading.announceBeheading(entity,
														mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
																.replace(" Head", ""), theKiller, mob_announce_display);
											}
										}
									}
								} else if ((daAnger >= 1) && (!daNectar)) {
									// ConfigHelper.Double(chanceConfig, "chance_percent.bee.angry", defpercent)
									mhd = headManager.getMobOrDefault( "bee.angry" ).getData();
									if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
										ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
										Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
												HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
												, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
										if(HeadUtils.containsHead(Drops)) {
											mmh.logDebug("EDE Angry Bee Head Dropped");
											if (mob_announce_enabled) {
												beheading.announceBeheading(entity,
														mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
																.replace(" Head", ""), theKiller, mob_announce_display);
											}
										}
									}
								} else if ((daAnger == 0) && (daNectar)) {
									// ConfigHelper.Double(chanceConfig, "chance_percent.bee.pollinated",
									// defpercent)
									mhd = headManager.getMobOrDefault( "bee.pollinated" ).getData();
									if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
										ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
										Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
												HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
												, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
										if(HeadUtils.containsHead(Drops)) {
											mmh.logDebug("EDE Pollinated Bee Head Dropped");
											if (mob_announce_enabled) {
												beheading.announceBeheading(entity,
														mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
																.replace(" Head", ""), theKiller, mob_announce_display);
											}
										}
									}
								} else if ((daAnger == 0) && (!daNectar)) {
									// ConfigHelper.Double(chanceConfig, "chance_percent.bee.chance_percent",
									// defpercent)
									mhd = headManager.getMobOrDefault( "bee.none" ).getData();
									if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
										ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
										Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
												HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
												, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
										if(HeadUtils.containsHead(Drops)) {
											mmh.logDebug("EDE Bee Head Dropped");
											if (mob_announce_enabled) {
												beheading.announceBeheading(entity,
														mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
																.replace(" Head", ""), theKiller, mob_announce_display);
											}
										}
									}
								}
								break;
							case "LLAMA":
								Llama daLlama = (Llama) entity;
								String daLlamaColor = daLlama.getColor().toString();
								//String daLlamaName = LlamaHeads.valueOf(name + "_" + daLlamaColor).getName() + " Head";// daLlamaColor.toLowerCase().replace("b",
								mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + daLlamaColor.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Llama Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity,
													mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
															.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "HORSE":
								Horse daHorse = (Horse) entity;
								String daHorseColor = daHorse.getColor().toString();
								//String daHorseName = HorseHeads.valueOf(name + "_" + daHorseColor).getName() + " Head";// daHorseColor.toLowerCase().replace("b",
								mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + daHorseColor.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Horse Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity,
													mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
															.replace(" Head", ""), theKiller, mob_announce_display);
										}
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
								mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + daCowVariant.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE " + name + " Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity,
													mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
															.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "PANDA":
								Panda daPanda = (Panda) entity;
								String daPandaGene = daPanda.getMainGene().toString();
								mmh.logDebug("EDE " + name + "_" + daPandaGene);
								// ConfigHelper.Double(chanceConfig, "chance_percent.panda." +
								// daPandaGene.toLowerCase(), defpercent)
								mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + daPandaGene.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Panda Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity,
													mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
															.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "PARROT":
								Parrot daParrot = (Parrot) entity;
								String daParrotVariant = daParrot.getVariant().toString();
								mmh.logDebug("EDE " + name + "_" + daParrotVariant);
								mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + daParrotVariant.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Parrot Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity,
													mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
															.replace(" Head", ""), theKiller, mob_announce_display);
										}
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
								mmh.logDebug("EDE " + name + "_" + daRabbitType);
								mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + daRabbitType.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Rabbit Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity,
													mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
															.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "VILLAGER":
								Villager daVillager = (Villager) entity; // Location jobsite =
								String daVillagerType = daVillager.getVillagerType().toString();
								String daVillagerProfession = daVillager.getProfession().toString();

								mmh.logDebug("EDE name=" + name);
								mmh.logDebug("EDE profession=" + daVillagerProfession);
								mmh.logDebug("EDE type=" + daVillagerType);
								//String daName = name + "_" + daVillagerProfession + "_" + daVillagerType;
								String daName = name + "." + daVillagerType + "." + daVillagerProfession;
								mmh.logDebug("EDE " + daName + "		 " + name + "_" + daVillagerProfession + "_"
										+ daVillagerType);

								mhd = headManager.getMobOrDefault( daName.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Villager Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity,
													mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
															.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "ZOMBIE_VILLAGER":
								ZombieVillager daZombieVillager = (ZombieVillager) entity;
								String daZombieVillagerType = daZombieVillager.getVillagerType().toString();
								String daZombieVillagerProfession = daZombieVillager.getVillagerProfession().toString();
								mmh.logDebug("EDE " + name + "_" + daZombieVillagerType + "_" + daZombieVillagerProfession);

								mhd = headManager.getMobOrDefault( (name + "." + daZombieVillagerType + "." + daZombieVillagerProfession).toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Zombie Villager Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
													.replace(" Head", ""), theKiller, mob_announce_display);
										}
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
								mmh.logDebug("EDE " + daSheepColor + "_" + name);
								mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + daSheepColor.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Sheep Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
													.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "TRADER_LLAMA":
								TraderLlama daTraderLlama = (TraderLlama) entity;
								String daTraderLlamaColor = daTraderLlama.getColor().toString();
								mmh.logDebug("EDE " + daTraderLlamaColor + "_" + name);

								mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + daTraderLlamaColor.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Trader Llama Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
													.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "AXOLOTL":
								Axolotl daAxolotl = (Axolotl) entity;
								String daAxolotlVariant = daAxolotl.getVariant().toString();
								mmh.logDebug("EDE " + daAxolotlVariant + "_" + name);
								mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + daAxolotlVariant.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Axolotl Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
													.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "GOAT":
								Goat daGoat = (Goat) entity;
								String daGoatVariant;
								if (daGoat.isScreaming()) {
									// Giving screaming goat head
									daGoatVariant = "SCREAMING";
								} else {
									// give goat head
									daGoatVariant = "NORMAL";
								}
								mmh.logDebug("EDE " + daGoatVariant + "_" + name);
								mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + daGoatVariant.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Goat Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
													.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "STRIDER":
								Strider daStrider = (Strider) entity;
								PersistentDataContainer pdc2 = daStrider.getPersistentDataContainer();
								boolean isShivering = Boolean.parseBoolean(daStrider.getPersistentDataContainer().get(mmh.SHIVERING_KEY, PersistentDataType.STRING));
								if (isShivering) {
									name = name.concat(".SHIVERING");
								}else {
									name = name.concat(".NORMAL");
								}
								mhd = headManager.getMobOrDefault( name.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE " + name + " Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName()).replace(" Head", ""),
													theKiller, mob_announce_display);
										}
									}
								}
								mmh.logDebug("EDE " + mhd.getDisplayName() + " killed");
								break;
							case "FROG":
								Frog daFrog = (Frog) entity;
								String daFrogVariant = daFrog.getVariant().toString();
								name = name.concat("." + daFrogVariant);
								mhd = headManager.getMobOrDefault( name.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE Frog Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName()).replace(" Head", ""),
													theKiller, mob_announce_display);
										}
									}
								}

								break;
							case "VEX":
								mhd = headManager.getMobOrDefault( name.toLowerCase() ).getData();
								mmh.logDebug("EDE name=" + name);
								mmh.logDebug("EDE texture=" + mhd.getTexture());
								mmh.logDebug("EDE location=" + entity.getLocation().toString());
								mmh.logDebug("EDE getName=" + event.getEntity().getName());
								mmh.logDebug("EDE killer=" + theKiller.toString());

								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE " + name + " Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName()).replace(" Head", ""),
													theKiller, mob_announce_display);
										}
									}
									if (coinFlip()) {
										name = name.concat("_ANGRY");
										mhd = headManager.getMobOrDefault( name.toLowerCase() ).getData();
										lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
										Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
												HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
												, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
										if(HeadUtils.containsHead(Drops)) {
											mmh.logDebug("EDE " + name + " Head Dropped");
										}
									}
									mmh.logDebug("EDE " + mhd.getDisplayName() + " killed");
								}
								break;
							case "WOLF":
								Wolf wolf = (Wolf) event.getEntity();
								name2 = MoreMobHeadsLib.getName(name, wolf);
								mmh.logDebug("EDE name2 = " + name2);
								mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + name2.toLowerCase() ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE " + name2 + " Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
													.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "COW":
								Cow cow = (Cow) event.getEntity();
								if(getServerVersion().isAtLeast("1.21.5")) {
									name2 = MoreMobHeadsLib.getName(name, cow);
									mmh.logDebug("EDE name2 = " + name2);
									mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + name2.toLowerCase() ).getData();
								}else {
									mhd = headManager.getMobOrDefault( name.toLowerCase() + ".normal" ).getData();
								}
								// defpercent)
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE " + mhd.getDisplayName() + " Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
													.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "CHICKEN":
								Chicken chicken = (Chicken) event.getEntity();
								if(getServerVersion().isAtLeast("1.21.5")) {
									name2 = MoreMobHeadsLib.getName(name, chicken);
									mmh.logDebug("EDE name2 = " + name2);
									mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + name2.toLowerCase() ).getData();
								}else {
									mhd = headManager.getMobOrDefault( name.toLowerCase() + ".normal" ).getData();
								}

								// defpercent)
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE " + mhd.getDisplayName() + " Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
													.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "PIG":
								Pig pig = (Pig) event.getEntity();
								if(getServerVersion().isAtLeast("1.21.5")) {
									name2 = MoreMobHeadsLib.getName(name, pig);
									mmh.logDebug("EDE name2 = " + name2);
									mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + name2.toLowerCase() ).getData();
								}else {
									mhd = headManager.getMobOrDefault( name.toLowerCase() + ".normal" ).getData();
								}

								// defpercent)
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE " + mhd.getDisplayName() + " Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
													.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							case "COPPER_GOLEM":
								mhd = headManager.getMobOrDefault( name.toLowerCase() + "." + MoreMobHeadsLib.getName(name, entity) ).getData();
								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE " + mhd.getDisplayName() + " Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity, mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName())
													.replace(" Head", ""), theKiller, mob_announce_display);
										}
									}
								}
								break;
							default:
								if(Objects.equals(name, "HAPPY_GHAST")) {
									if( mmh.isBabyEntity( event.getEntity() ) ) {
										name = "GHASTLING";
									}
								}
								// mmh.makeSkull(MobHeads.valueOf(name).getTexture(), name);
								mhd = headManager.getMobOrDefault( name.toLowerCase() ).getData();
								mmh.logDebug("EDE name=" + name + " default");
								mmh.logDebug("EDE texture=" + mhd.getTexture() + " default");
								mmh.logDebug("EDE location=" + entity.getLocation().toString() + " default");
								mmh.logDebug("EDE getName=" + event.getEntity().getName() + " default");
								mmh.logDebug("EDE killer=" + theKiller.toString() + " default");
								// ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(),
								// defpercent)

								if ( mmh.DropIt( event, mhd.getChance(), theKiller ) ) {
									ArrayList<String> lore = mmh.modifyLore( mhd.getLore(), false, theKiller );
									Drops = mmh.addHeadToDrops(event.getEntity(), theKiller,
											HeadUtils.makeHead( mmh.langName.getString( mhd.getLangName(), mhd.getDisplayName() ), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound() )
											, mhd.getDisplayName(), mhd.getTexture(), mhd.getUuid(), lore, mhd.getNoteblockSound(), Drops);
									if(HeadUtils.containsHead(Drops)) {
										mmh.logDebug("EDE " + name + " Head Dropped");
										if (mob_announce_enabled) {
											beheading.announceBeheading(entity,
													mmh.langName.getString(mhd.getLangName(), mhd.getDisplayName()).replace(" Head", "")
													, theKiller, mob_announce_display);
										}
									}
								}
								mmh.logDebug("EDE " + mhd.getDisplayName() + " killed");
								break;
							}// End switch
						}catch(Exception exception){
							reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLAYER_KILL_MOB).error(exception));
						}
                    }
				}
			}
		}catch(Exception exception){
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.UNHANDLED_ENTITYDEATHEVENT_ERROR).error(exception));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {  // TODO: CreatureSpawnEvent
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



	public void checkMiniBlocks() {
		if ( mmh.config.getBoolean("wandering_trades.custom_wandering_trader", true) || mmh.config.getBoolean("head_settings.mini_blocks.stonecutter", true) ) {

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

	@Override public void loadHeadsAndRecipes() {
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
		headManager.miniBlocksList().sort(Comparator.comparing(item -> Objects.requireNonNull(item.getItemMeta()).getDisplayName().replaceAll("§[0-9a-fk-or]", "")));
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
}