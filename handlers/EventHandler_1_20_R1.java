package com.github.joelgodofwar.mmh.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.common.PluginLibrary;
import com.github.joelgodofwar.mmh.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.mmh.common.error.Report;
import com.github.joelgodofwar.mmh.enums.CatHeads;
import com.github.joelgodofwar.mmh.enums.HorseHeads;
import com.github.joelgodofwar.mmh.enums.LlamaHeads;
import com.github.joelgodofwar.mmh.enums.MobHeads;
import com.github.joelgodofwar.mmh.enums.MobHeads117;
import com.github.joelgodofwar.mmh.enums.MobHeads119;
import com.github.joelgodofwar.mmh.enums.MobHeads120;
import com.github.joelgodofwar.mmh.enums.RabbitHeads;
import com.github.joelgodofwar.mmh.enums.SheepHeads;
import com.github.joelgodofwar.mmh.enums.TropicalFishHeads;
import com.github.joelgodofwar.mmh.enums.VillagerHeads;
import com.github.joelgodofwar.mmh.enums.ZombieVillagerHeads;
import com.github.joelgodofwar.mmh.util.ChatColorUtils;
import com.github.joelgodofwar.mmh.util.ConfigHelper;
import com.github.joelgodofwar.mmh.util.StrUtils;
import com.github.joelgodofwar.mmh.util.Utils;
import com.github.joelgodofwar.mmh.util.VerifyConfig;
import com.github.joelgodofwar.mmh.util.YmlConfiguration;

import de.tr7zw.changeme.nbtapi.NBTItem;

/**
 * 1.8 1_8_R1 1.8.3 1_8_R2 1.8.8 1_8_R3 1.9 1_9_R1 1.9.4 1_9_R2 1.10 1_10_R1
 * 1.11 1_11_R1 1.12 1_12_R1 1.13 1_13_R1 1.13.1 1_13_R2 1.14 1_14_R1 1.15
 * 1_15_R1 1.16.1 1_16_R1 1.16.2 1_16_R2 1.17 1_17_R1
 */

@SuppressWarnings("deprecation")
public class EventHandler_1_20_R1 implements CommandExecutor, TabCompleter, Listener {
	/** Variables */
	MoreMobHeads mmh;
	double defpercent = 13.0;
	String world_whitelist;
	String world_blacklist;
	String mob_whitelist;
	String mob_blacklist;
	boolean debug;
	YmlConfiguration chanceConfig;
	File blockFile117;
	File blockFile1172;
	File blockFile1173;
	//File blockFile119;
	File blockFile120;
	public FileConfiguration blockHeads = new YamlConfiguration();
	public FileConfiguration blockHeads2 = new YamlConfiguration();
	public FileConfiguration blockHeads3 = new YamlConfiguration();
	public FileConfiguration blockHeads4 = new YamlConfiguration();

	List<MerchantRecipe> playerhead_recipes = new ArrayList<MerchantRecipe>();
	List<MerchantRecipe> blockhead_recipes = new ArrayList<MerchantRecipe>();
	List<MerchantRecipe> custometrade_recipes = new ArrayList<MerchantRecipe>();
	int BHNum, BHNum2, BHNum3, BHNum4;
	VerifyConfig verify;// = new VerifyConfig(mmh.config);
	DetailedErrorReporter reporter;



	@SuppressWarnings({ "static-access", "unchecked", "rawtypes" })
	public EventHandler_1_20_R1(final MoreMobHeads plugin) { // TODO: Top of code
		try { // REPORT_EVENT_HANDLER_LOAD "Error while loading EventHandler."
			/** Set variables */
			mmh = plugin;
			reporter = new DetailedErrorReporter(mmh);
			verify = new VerifyConfig(mmh);
			mmh.LOGGER.log("Loading 1.20 EventHandler...");
			long startTime = System.currentTimeMillis();
			mmh.getCommand("mmh").setExecutor(this);
			world_whitelist = mmh.config.getString("world.whitelist", "");
			world_blacklist = mmh.config.getString("world.blacklist", "");
			mob_whitelist = mmh.config.getString("mob.whitelist", "");
			mob_blacklist = mmh.config.getString("mob.blacklist", "");
			debug = plugin.debug;
			chanceConfig = plugin.chanceConfig;
			blockFile117 = new File(mmh.getDataFolder() + "" + File.separatorChar + "block_heads_1_17.yml");
			blockFile1172 = new File(mmh.getDataFolder() + "" + File.separatorChar + "block_heads_1_17_2.yml");
			blockFile120 = new File(mmh.getDataFolder() + "" + File.separatorChar + "block_heads_1_20.yml");
			if (mmh.config.getBoolean("wandering_trades.custom_wandering_trader", true)) {
				if (!blockFile117.exists()) {
					mmh.saveResource("block_heads_1_17.yml", true);
					mmh.LOGGER.log("block_heads_1_17.yml not found! Creating in " + mmh.getDataFolder() + "");
				}
				if (!blockFile1172.exists()) {
					mmh.saveResource("block_heads_1_17_2.yml", true);
					mmh.LOGGER.log("block_heads_1_17_2.yml not found! Creating in " + mmh.getDataFolder() + "");
				}
				if (!blockFile120.exists()) {
					mmh.saveResource("block_heads_1_20.yml", true);
					mmh.LOGGER.log("block_heads_1_20.yml not found! Creating in " + mmh.getDataFolder() + "");
				}
				blockHeads = new YamlConfiguration();
				try {
					mmh.LOGGER.log("Loading " + blockFile117 + "...");
					blockHeads.load(blockFile117);
				} catch (Exception exception) {
					mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_BLOCKHEAD_LOAD_ERROR).error(exception));
				}
				blockHeads2 = new YamlConfiguration();
				try {
					mmh.LOGGER.log("Loading " + blockFile1172 + "...");
					blockHeads2.load(blockFile1172);
				} catch (Exception exception) {
					mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_BLOCKHEAD_LOAD_ERROR).error(exception));
				}
				if (Double.parseDouble(mmh.getMCVersion().substring(0, 4)) >= 1.20) {
					blockFile1173 = blockFile120;
				}
				blockHeads3 = new YamlConfiguration();
				try {
					mmh.LOGGER.log("Loading " + blockFile1173 + "...");
					blockHeads3.load(blockFile1173);
				} catch (Exception exception) {
					mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_BLOCKHEAD_LOAD_ERROR).error(exception));
				}

				boolean showlore = mmh.config.getBoolean("head_settings.lore.show_plugin_name", true);
				ArrayList<String> headlore = new ArrayList();
				headlore.add(ChatColor.AQUA + "" + mmh.getName());

				mmh.LOGGER.log("Loading PlayerHead Recipes...");
				for (int i = 1; i < (mmh.playerHeads.getInt("players.number") + 1); i++) {
					ItemStack price1 = mmh.playerHeads.getItemStack("players.player_" + i + ".price_1",
							new ItemStack(Material.AIR));
					ItemStack price2 = mmh.playerHeads.getItemStack("players.player_" + i + ".price_2",
							new ItemStack(Material.AIR));
					ItemStack itemstack = mmh.playerHeads.getItemStack("players.player_" + i + ".itemstack",
							new ItemStack(Material.AIR));
					if (showlore) {
						SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
						meta.setLore(headlore);
						itemstack.setItemMeta(meta);
						itemstack.setItemMeta(meta);
					}
					MerchantRecipe recipe = new MerchantRecipe(itemstack,
							mmh.playerHeads.getInt("players.player_" + i + ".quantity", 3));
					recipe.addIngredient(price1);
					recipe.addIngredient(price2);
					playerhead_recipes.add(recipe);
				}
				mmh.LOGGER.log(playerhead_recipes.size() + " PlayerHead Recipes ADDED...");
				mmh.LOGGER.log("Loading BlockHead Recipes...");
				BHNum = blockHeads.getInt("blocks.number");
				// BlockHeads
				mmh.LOGGER.log("BlockHeads=" + BHNum);
				for (int i = 1; i < (BHNum + 1); i++) {
					ItemStack price1 = blockHeads.getItemStack("blocks.block_" + i + ".price_1",
							new ItemStack(Material.AIR));
					ItemStack price2 = blockHeads.getItemStack("blocks.block_" + i + ".price_2",
							new ItemStack(Material.AIR));
					ItemStack itemstack = blockHeads.getItemStack("blocks.block_" + i + ".itemstack",
							new ItemStack(Material.AIR));
					if (showlore) {
						SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
						meta.setLore(headlore);
						itemstack.setItemMeta(meta);
						itemstack.setItemMeta(meta);
					}
					MerchantRecipe recipe = new MerchantRecipe(itemstack,
							blockHeads.getInt("blocks.block_" + i + ".quantity", 8));
					recipe.setExperienceReward(true);
					recipe.addIngredient(price1);
					recipe.addIngredient(price2);
					blockhead_recipes.add(recipe);
				}
				BHNum2 = blockHeads2.getInt("blocks.number");
				// blockHeads 2
				mmh.LOGGER.log("BlockHeads2=" + BHNum2);
				for (int i = 1; i < (BHNum2 + 1); i++) {
					ItemStack price1 = blockHeads2.getItemStack("blocks.block_" + i + ".price_1",
							new ItemStack(Material.AIR));
					ItemStack price2 = blockHeads2.getItemStack("blocks.block_" + i + ".price_2",
							new ItemStack(Material.AIR));
					ItemStack itemstack = blockHeads2.getItemStack("blocks.block_" + i + ".itemstack",
							new ItemStack(Material.AIR));
					if (showlore) {
						SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
						meta.setLore(headlore);
						itemstack.setItemMeta(meta);
						itemstack.setItemMeta(meta);
					}
					MerchantRecipe recipe = new MerchantRecipe(itemstack,
							blockHeads2.getInt("blocks.block_" + i + ".quantity", 8));
					recipe.setExperienceReward(true);
					recipe.addIngredient(price1);
					recipe.addIngredient(price2);
					blockhead_recipes.add(recipe);
				}
				BHNum3 = blockHeads3.getInt("blocks.number");
				// blockHeads 3
				mmh.LOGGER.log("BlockHeads3=" + BHNum3);
				for (int i = 1; i < (BHNum3 + 1); i++) {
					ItemStack price1 = blockHeads3.getItemStack("blocks.block_" + i + ".price_1",
							new ItemStack(Material.AIR));
					ItemStack price2 = blockHeads3.getItemStack("blocks.block_" + i + ".price_2",
							new ItemStack(Material.AIR));
					ItemStack itemstack = blockHeads3.getItemStack("blocks.block_" + i + ".itemstack",
							new ItemStack(Material.AIR));
					if (showlore) {
						SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
						meta.setLore(headlore);
						itemstack.setItemMeta(meta);
						itemstack.setItemMeta(meta);
					}
					MerchantRecipe recipe = new MerchantRecipe(itemstack,
							blockHeads3.getInt("blocks.block_" + i + ".quantity", 8));
					recipe.setExperienceReward(true);
					recipe.addIngredient(price1);
					recipe.addIngredient(price2);
					blockhead_recipes.add(recipe);
				}

				mmh.LOGGER.log(blockhead_recipes.size() + " BlockHead Recipes ADDED...");
				mmh.LOGGER.log("Loading CustomTrades Recipes...");
				for (int i = 1; i < (mmh.traderCustom.getInt("custom_trades.number") + 1); i++) {
					ItemStack price1 = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".price_1",
							new ItemStack(Material.AIR));
					ItemStack price2 = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".price_2",
							new ItemStack(Material.AIR));
					ItemStack itemstack = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".itemstack",
							new ItemStack(Material.AIR));
					/** Code to fix missing noteblock SkullMeta */
					boolean doIt = Utils.isSupportedVersion("1.20.2.3936");
					if(doIt) {
						mmh.LOGGER.log("doIt=" + doIt);
						if(itemstack.getType().equals(Material.PLAYER_HEAD)) {
							SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
							String sound = mmh.traderCustom.getString("custom_trades.trade_" + i + ".note_block_sound", "entity.player.hurt");
							meta.setNoteBlockSound(NamespacedKey.minecraft(sound));
							itemstack.setItemMeta(meta);
						}//*/
					}
					/** Code to fix missing noteblock SkullMeta */
					MerchantRecipe recipe = new MerchantRecipe(itemstack,
							mmh.traderCustom.getInt("custom_trades.trade_" + i + ".quantity", 1));
					recipe.setExperienceReward(true);
					recipe.addIngredient(price1);
					recipe.addIngredient(price2);
					custometrade_recipes.add(recipe);
				}
				mmh.LOGGER.log(custometrade_recipes.size() + " CustomTrades Recipes ADDED...");
				mmh.LOGGER.log("EventHandler_1_20 took " + mmh.LoadTime(startTime) + "ms to load");
			}
		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_EVENT_HANDLER_LOAD).error(exception));
		}
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
			mmh.LOGGER.debug("EDEE - START");
			mmh.LOGGER.debug("EDEE - getEntity=" + event.getEntity().getName().toString());
			mmh.LOGGER.debug("EDEE - getDamager=" + event.getDamager().getName().toString());

			Player player = null;
			if (event.getDamager() instanceof Player) {
				player = (Player) event.getDamager();
				if ((event.getEntity() instanceof EnderCrystal)) {
					/** Is Player and Is End Crystal */
					EnderCrystal ec = (EnderCrystal) event.getEntity();
					mmh.endCrystals.put(ec.getUniqueId(), player.getUniqueId());
				}
			} else if (event.getDamager() instanceof Arrow) {
				Arrow arrow = (Arrow) event.getDamager();
				if ((arrow.getShooter() instanceof Player) && !(event.getEntity() instanceof EnderCrystal)) {
					/** Is Player but Not End Crystal */
					player = (Player) arrow.getShooter();
				} else if ((arrow.getShooter() instanceof Player) && (event.getEntity() instanceof EnderCrystal)) {
					/** Is Player and Is End Crystal */
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
					/** Is Player but Not End Crystal */
					player = (Player) snowball.getShooter();
				} else if ((snowball.getShooter() instanceof Player) && (event.getEntity() instanceof EnderCrystal)) {
					/** Is Player and Is End Crystal */
					player = (Player) snowball.getShooter();
					EnderCrystal ec = (EnderCrystal) event.getEntity();
					mmh.endCrystals.put(ec.getUniqueId(), player.getUniqueId());
				} else {
					return; // Not Player or Not End Crystal
				}
			} else if (event.getDamager() instanceof Egg) {
				Egg egg = (Egg) event.getDamager();
				if ((egg.getShooter() instanceof Player) && !(event.getEntity() instanceof EnderCrystal)) {
					/** Is Player but Not End Crystal */
					player = (Player) egg.getShooter();
				} else if ((egg.getShooter() instanceof Player) && (event.getEntity() instanceof EnderCrystal)) {
					/** Is Player and Is End Crystal */
					player = (Player) egg.getShooter();
					EnderCrystal ec = (EnderCrystal) event.getEntity();
					mmh.endCrystals.put(ec.getUniqueId(), player.getUniqueId());
				} else {
					return; // Not Player or Not End Crystal
				}
			} else if (event.getDamager() instanceof Trident) {
				Trident trident = (Trident) event.getDamager();
				if ((trident.getShooter() instanceof Player) && !(event.getEntity() instanceof EnderCrystal)) {
					/** Is Player but Not End Crystal */
					player = (Player) trident.getShooter();
				} else if ((trident.getShooter() instanceof Player) && (event.getEntity() instanceof EnderCrystal)) {
					/** Is Player and Is End Crystal */
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
				player = Bukkit.getPlayer(pUUID);
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
				mmh.LOGGER.debug("EDEE - dc=" + dc);
			}
			// Store the damaging player's UUID and the damaging weapon in the map

			if(player == null) {
				return;
			}
			mmh.LOGGER.debug("EDEE - UUID=" + player.getUniqueId());
			//mmh.LOGGER.debug("EDEE - Weapon=" + resolveDamagingWeapon(player.getInventory(), event.getCause()).orElse(null));
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

			mmh.LOGGER.debug("EDE - world_whitelist=" + world_whitelist);
			mmh.LOGGER.debug("EDE - world_blacklist=" + world_blacklist);
			mmh.LOGGER.debug("EDE - mob_whitelist=" + mob_whitelist);
			mmh.LOGGER.debug("EDE - mob_blacklist=" + mob_blacklist);

			try { // REPORT_WHITE_BLACK_LIST "Unable to parse global whitelist/blacklist"
				if ((world_whitelist != null) && !world_whitelist.isEmpty() && (world_blacklist != null)
						&& !world_blacklist.isEmpty()) {
					if (!StrUtils.stringContains(world_whitelist, world.getName().toString())
							&& StrUtils.stringContains(world_blacklist, world.getName().toString())) {

						mmh.LOGGER.debug("EDE - World - On blacklist and Not on whitelist.");

						return;
					} else if (!StrUtils.stringContains(world_whitelist, world.getName().toString())
							&& !StrUtils.stringContains(world_blacklist, world.getName().toString())) {

						mmh.LOGGER.debug("EDE - World - Not on whitelist.");

						return;
					} else if (!StrUtils.stringContains(world_whitelist, world.getName().toString())) {

					}
				} else if ((world_whitelist != null) && !world_whitelist.isEmpty()) {
					if (!StrUtils.stringContains(world_whitelist, world.getName().toString())) {

						mmh.LOGGER.debug("EDE - World - Not on whitelist.");

						return;
					}
				} else if ((world_blacklist != null) && !world_blacklist.isEmpty()) {
					if (StrUtils.stringContains(world_blacklist, world.getName().toString())) {

						mmh.LOGGER.debug("EDE - World - On blacklist.");

						return;
					}
				}
			} catch(Exception exception){
				reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_WHITE_BLACK_LIST).error(exception));
			}

			if ((entity instanceof Player)
					&& ((entity.getKiller() instanceof Player) || (entity.getKiller() instanceof Creeper))) {	// TODO: Player Kill Player
				try{ // REPORT_PLAYER_KILL_PLAYER "Unable to parse Player Death."
					mmh.LOGGER.debug("EDE Entity is Player");
					if ((entity.getKiller().hasPermission("moremobheads.players") || mmh.isDev) || (entity.getKiller() instanceof Creeper)) {

						mmh.LOGGER.debug("EDE DropIt=" + mmh.DropIt(event, chanceConfig.getDouble("chance_percent.player", 50.0)));
						mmh.LOGGER.debug("EDE chance_percent.player=" + chanceConfig.getDouble("chance_percent.player", 50.0));

						if (mmh.DropIt(event, chanceConfig.getDouble("chance_percent.player", 50.0))) {
							// Player daKiller = entity.getKiller();

							mmh.LOGGER.debug("EDE Killer is Player line:1073");

							Player victim = (Player) entity;
							Player killer = entity.getKiller();
							String killerName;
							String entityName;
							PlayerProfile profile =  victim.getPlayerProfile();
							ItemStack head = new ItemStack(Material.PLAYER_HEAD);
							SkullMeta meta = (SkullMeta) head.getItemMeta();
							PlayerTextures textures = profile.getTextures();

							mmh.LOGGER.debug(" EDE PlayerProfile getName: " + profile.getName() );
							mmh.LOGGER.debug(" EDE PlayerProfile UUID: " + profile.getUniqueId() );
							mmh.LOGGER.debug(" EDE PlayerProfile Skin: " + profile.getTextures().getSkin() );

							killerName = mmh.getNickname(killer);
							entityName = mmh.getNickname(victim);

							head = mmh.makeHead(entityName, profile.getTextures().getSkin().toString(), profile.getUniqueId().toString(), entity.getType(), entity.getKiller());
							mmh.playerGiveOrDropHead(entity.getKiller(), head);
							mmh.LOGGER.debug("EDE " + ((Player) entity).getDisplayName().toString() + " Player Head Dropped");
							if (mmh.config.getBoolean("head_settings.player_heads.announce_kill.enabled", true)) {
								announceBeheading(entity, entityName, killer,
										mmh.config.getBoolean("head_settings.player_heads.announce_kill.displayname", true));
							}
						}
						return;
					} else {
						mmh.LOGGER.debug("EDE Killer does not have permission \"moremobheads.players\"");
					}
				} catch(Exception exception){
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLAYER_KILL_PLAYER).error(exception));
				}

			} else if (event.getEntity() instanceof LivingEntity) {
				if ((entity.getKiller() instanceof Player) || (entity.getKiller() instanceof Creeper)) {
					String name = event.getEntityType().toString().replace(" ", "_");
					mmh.LOGGER.debug("EDE name=" + name);
					String isNametag = null;
					@Nonnull
					PersistentDataContainer pdc = entity.getPersistentDataContainer();
					isNametag = entity.getPersistentDataContainer().get(mmh.NAMETAG_KEY, PersistentDataType.STRING);// .getScoreboardTags();//
					if ((isNametag != null)) {
						mmh.LOGGER.debug("EDE isNametag=" + isNametag.toString());
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
									mmh.LOGGER.debug("EDE customname=" + entity.getCustomName().toString());
									if ((entity instanceof Skeleton) || (entity instanceof Zombie) || (entity instanceof PigZombie)) {
										if (mmh.getServer().getPluginManager().getPlugin("SilenceMobs") != null) {
											if (entity.getCustomName().toLowerCase().contains("silenceme")
													|| entity.getCustomName().toLowerCase().contains("silence me")) {
												return;
											}
										}
										boolean enforcewhitelist = mmh.config.getBoolean("head_settings.player_heads.whitelist.enforce", false);
										boolean enforceblacklist = mmh.config.getBoolean("head_settings.player_heads.blacklist.enforce", false);
										boolean onwhitelist = mmh.config.getString("head_settings.player_heads.whitelist.player_head_whitelist", "")
												.toLowerCase().contains(entity.getCustomName().toLowerCase());
										boolean onblacklist = mmh.config.getString("head_settings.player_heads.blacklist.player_head_blacklist", "")
												.toLowerCase().contains(entity.getCustomName().toLowerCase());
										/**  */
										if (mmh.DropIt(event, chanceConfig.getDouble("named_mob", 10.0))) {
											boolean isTrue = false;
											if (enforcewhitelist && enforceblacklist) {
												if (onwhitelist && !(onblacklist)) {
													isTrue = true;
												}else {
													isTrue = false;
												}
											} else if (enforcewhitelist && !enforceblacklist) {
												if (onwhitelist) {
													isTrue = true;
												}else {
													isTrue = false;
												}
											} else if (!enforcewhitelist && enforceblacklist) {
												if (!onblacklist) {
													isTrue = true;
												}else {
													isTrue = false;
												}
											} else if (!enforcewhitelist && !enforceblacklist) {
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
												if(entity.getEquipment().getHelmet().isSimilar(head)) {
													Drops.add(head);
													mmh.LOGGER.debug(" EDE " + entity.getCustomName().toString() + " Head Dropped");
													if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
														announceBeheading(entity, entity.getCustomName(),
																entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
													}
												} else if(!entity.getEquipment().getHelmet().isSimilar(head)) {
													mmh.LOGGER.debug(" EDE NamedMob is not waering PlayerHead drop canceled.");
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

						try { // REPORT_PLAYER_KILL_MOB "Unable to parse Mob Kill."
							switch (name) {
							case "CREEPER":
								// ConfigHelper.Double(chanceConfig,
								// "chance_percent.creeper", defpercent)
								Creeper creeper = (Creeper) event.getEntity();
								double cchance = ConfigHelper.Double(chanceConfig, "chance_percent.creeper", defpercent);
								if (creeper.isPowered()) {
									name = "CREEPER_CHARGED";
									cchance = ConfigHelper.Double(chanceConfig, "chance_percent.creeper_charged", defpercent);
								}
								if (mmh.DropIt(event, cchance)) {
									mmh.LOGGER.debug("EDE Creeper vanilla="
											+ mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.creeper", false));
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.creeper", false)
											&& (name != "CREEPER_CHARGED")) {
										Drops.add(new ItemStack(Material.CREEPER_HEAD));
									} else { // mmh.langName
										Drops.add(
												mmh.makeHead( mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
														, MobHeads.valueOf(name).getTexture().toString(), MobHeads.valueOf(name).getOwner(), entity.getType(), entity.getKiller() )
												);
										//Drops.add(MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name).getTexture().toString(), mmh.langName.getString(name.toLowerCase(),MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()), entity));
									} // MobHeads.valueOf(name).getName() + " Head"
									mmh.LOGGER.debug("EDE Creeper Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "").replace(" Head", ""),
												entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "ENDER_DRAGON":
								// ConfigHelper.Double(chanceConfig,
								// "chance_percent.ender_dragon", defpercent)
								if (mmh.DropIt(event,
										ConfigHelper.Double(chanceConfig, "chance_percent.ender_dragon", defpercent))) {
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.ender_dragon", false)) {
										Drops.add(new ItemStack(Material.DRAGON_HEAD));
									} else {
										Drops.add(
												mmh.makeHead( mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
														, MobHeads.valueOf(name).getTexture().toString(), MobHeads.valueOf(name).getOwner(), entity.getType(), entity.getKiller() )
												);
									}
									mmh.LOGGER.debug("EDE Ender Dragon Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,mmh.langName.getString(name.toLowerCase(),MobHeads.valueOf(name).getName() + "").replace(" Head", "")
												,entity.getKiller(),mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname",true));
									}
								}
								break;
							case "PIGLIN":
								// ConfigHelper.Double(chanceConfig, "chance_percent.ender_dragon", defpercent)
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig, "chance_percent.piglin", defpercent))) {
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.piglin", false)) {
										Drops.add(new ItemStack(Material.PIGLIN_HEAD));
									} else {
										Drops.add(
												mmh.makeHead( mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
														, MobHeads.valueOf(name).getTexture().toString(), MobHeads.valueOf(name).getOwner(), entity.getType(), entity.getKiller() )
												);
									}
									mmh.LOGGER.debug("EDE Ender Dragon Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
												.replace(" Head", ""),entity.getKiller(),mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "SKELETON":
								// ConfigHelper.Double(chanceConfig, "chance_percent.skeleton", defpercent)
								if (mmh.DropIt(event,
										ConfigHelper.Double(chanceConfig, "chance_percent.skeleton", defpercent))) {
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.skeleton", false)) {
										Drops.add(new ItemStack(Material.SKELETON_SKULL));
									} else {
										Drops.add(
												mmh.makeHead( mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
														, MobHeads.valueOf(name).getTexture().toString(), MobHeads.valueOf(name).getOwner(), entity.getType(), entity.getKiller() )
												);
									}
									mmh.LOGGER.debug("EDE Skeleton vanilla="
											+ mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.skeleton", false));
									mmh.LOGGER.debug("EDE Skeleton Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
												.replace(" Head", ""),entity.getKiller(),mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "WITHER_SKELETON":
								// ConfigHelper.Double(chanceConfig, "chance_percent.wither_skeleton",
								// defpercent)
								if (mmh.DropIt(event,
										ConfigHelper.Double(chanceConfig, "chance_percent.wither_skeleton", defpercent))) {
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.wither_skeleton", false)) {
										Drops.add(new ItemStack(Material.WITHER_SKELETON_SKULL));
									} else {
										Drops.add(
												mmh.makeHead( mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
														, MobHeads.valueOf(name).getTexture().toString(), MobHeads.valueOf(name).getOwner(), entity.getType(), entity.getKiller() )
												);
									}
									mmh.LOGGER.debug("EDE Wither Skeleton Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "ZOMBIE":
								// ConfigHelper.Double(chanceConfig, "chance_percent.zombie", defpercent)
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig, "chance_percent.zombie", defpercent))) {
									if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.zombie", false)) {
										Drops.add(new ItemStack(Material.ZOMBIE_HEAD));
									} else {

										Drops.add(
												mmh.makeHead( mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
														, MobHeads.valueOf(name).getTexture().toString(), MobHeads.valueOf(name).getOwner(), entity.getType(), entity.getKiller() )
												);
									}
									mmh.LOGGER.debug("EDE Zombie vanilla="+ mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.zombie", false));
									mmh.LOGGER.debug("EDE Zombie Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
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
								mmh.LOGGER.log("daFishName: " + daFishName);
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig, "chance_percent.tropical_fish." + daFishName.toLowerCase(), defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase() + "." + daFishName.toLowerCase(), TropicalFishHeads.valueOf(name + "_" + daFishName).getName() + "")
													, TropicalFishHeads.valueOf(name + "_" + daFishName).getTexture().toString(), TropicalFishHeads.valueOf(name + "_" + daFishName).getOwner(), entity.getType(), entity.getKiller() )
											//MoreMobHeadsLib.addSound(mmh.makeSkull(TropicalFishHeads.valueOf(name + "_" + daFishName).getTexture().toString()
											//		, mmh.langName.getString(name.toLowerCase() + "." + daFishName.toLowerCase(), TropicalFishHeads.valueOf(name + "_" + daFishName).getName() + " Head")
											//		, entity.getKiller()), entity)
											);
									mmh.LOGGER.debug("EDE TROPICAL_FISH:" + daFishName + " head dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString(name.toLowerCase() + "." + daFishName.toLowerCase(), TropicalFishHeads.valueOf(name + "_" + daFishName).getName() + "")
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "WITHER":
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(), defpercent))) {
									String name2 = name + "_NORMAL";
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name2.toLowerCase(), MobHeads.valueOf(name2).getName() + "")
													, MobHeads.valueOf(name2).getTexture().toString(), MobHeads.valueOf(name2).getOwner(), entity.getType(), entity.getKiller() )
											/**MoreMobHeadsLib.addSound(mmh.makeSkull(MobHeads.valueOf(name2).getTexture().toString(), mmh.langName.getString(name2.toLowerCase().replace("_", ".")
											, MobHeads.valueOf(name2).getName() + " Head"), entity.getKiller()), entity)//*/
											);
									mmh.LOGGER.debug("EDE " + name2 + " Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString(name2.toLowerCase().replace("_", ".")
												, MobHeads.valueOf(name2).getName() + "").replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
									if (coinFlip()) {
										name2 = name + "_PROJECTILE";
										Drops.add(
												mmh.makeHead( mmh.langName.getString(name2.toLowerCase(), MobHeads.valueOf(name2).getName() + "")
														, MobHeads.valueOf(name2).getTexture().toString(), MobHeads.valueOf(name2).getOwner(), entity.getType(), entity.getKiller() )
												);
										mmh.LOGGER.debug("EDE " + name2 + " Head Dropped");
									}
									if (coinFlip()) {
										name2 = name + "_BLUE_PROJECTILE";
										Drops.add(
												mmh.makeHead( mmh.langName.getString(name2.toLowerCase(), MobHeads.valueOf(name2).getName() + "")
														, MobHeads.valueOf(name2).getTexture().toString(), MobHeads.valueOf(name2).getOwner(), entity.getType(), entity.getKiller() )
												);
										mmh.LOGGER.debug("EDE " + name2 + " Head Dropped");
									}
								}
								break;
							case "WOLF":
								Wolf wolf = (Wolf) event.getEntity();
								// ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(),
								// defpercent)
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(),
										defpercent))) {
									if (wolf.isAngry()) {
										String name2 = name + "_ANGRY";
										Drops.add(
												mmh.makeHead( mmh.langName.getString(name2.toLowerCase(), MobHeads.valueOf(name2).getName() + "")
														, MobHeads.valueOf(name2).getTexture().toString(), MobHeads.valueOf(name2).getOwner(), entity.getType(), entity.getKiller() )
												);
										mmh.LOGGER.debug("EDE Angry Wolf Head Dropped");
										if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
											announceBeheading(entity, mmh.langName.getString(name.toLowerCase() + "_angry", MobHeads.valueOf(name + "_ANGRY").getName() + "")
													.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
										}
									} else {
										Drops.add(
												mmh.makeHead( mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
														, MobHeads.valueOf(name).getTexture().toString(), MobHeads.valueOf(name).getOwner(), entity.getType(), entity.getKiller() )
												);
										mmh.LOGGER.debug("EDE Wolf Head Dropped");
										if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
											announceBeheading(entity, mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
													.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
										}
									}
								}
								break;
							case "FOX":
								Fox dafox = (Fox) entity;
								String dafoxtype = dafox.getFoxType().toString();
								mmh.LOGGER.debug("EDE dafoxtype=" + dafoxtype);
								// ConfigHelper.Double(chanceConfig, "chance_percent.fox." +
								// dafoxtype.toLowerCase(), defpercent)
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig,
										"chance_percent.fox." + dafoxtype.toLowerCase(), defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase() + "." + dafoxtype.toLowerCase(), MobHeads.valueOf(name + "_" + dafoxtype).getName() + "")
													, MobHeads.valueOf(name + "_" + dafoxtype).getTexture().toString(), MobHeads.valueOf(name + "_" + dafoxtype).getOwner(), entity.getType(), entity.getKiller() )
											); //
									mmh.LOGGER.debug("EDE Fox Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString(name.toLowerCase() + "." + dafoxtype.toLowerCase(), MobHeads.valueOf(name + "_" + dafoxtype).getName() + "")
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}

								break;
							case "CAT":
								Cat dacat = (Cat) entity;
								String dacattype = dacat.getCatType().toString();
								mmh.LOGGER.debug("entity cat=" + dacat.getCatType());
								// ConfigHelper.Double(chanceConfig, "chance_percent.cat." +
								// dacattype.toLowerCase(), defpercent)
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig,
										"chance_percent.cat." + dacattype.toLowerCase(), defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase() + "." + dacattype.toLowerCase(), CatHeads.valueOf(dacattype).getName() + "")
													, CatHeads.valueOf(dacattype).getTexture().toString(), CatHeads.valueOf(dacattype).getOwner(), entity.getType(), entity.getKiller() )
											);
									mmh.LOGGER.debug("EDE Cat Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString(name.toLowerCase() + "." + dacattype.toLowerCase(), CatHeads.valueOf(dacattype).getName() + "")
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "OCELOT":
								// ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(),
								// defpercent)
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(),
										defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
													, MobHeads.valueOf(name).getTexture().toString(), MobHeads.valueOf(name).getOwner(), entity.getType(), entity.getKiller() )
											);
									mmh.LOGGER.debug("EDE " + name + " Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString(MobHeads.valueOf(name).getNameString(),
														MobHeads.valueOf(name).getName() + "").replace(" Head", ""),
												entity.getKiller(),
												mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								mmh.LOGGER.debug("EDE " + MobHeads.valueOf(name) + " killed");
								break;
							case "BEE":
								Bee daBee = (Bee) entity;
								int daAnger = daBee.getAnger();
								mmh.LOGGER.debug("EDE daAnger=" + daAnger);
								boolean daNectar = daBee.hasNectar();
								mmh.LOGGER.debug("EDE daNectar=" + daNectar);
								if ((daAnger >= 1) && (daNectar == true)) {
									// ConfigHelper.Double(chanceConfig, "chance_percent.bee.angry_pollinated",
									// defpercent)
									if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig,
											"chance_percent.bee.angry_pollinated", defpercent))) {
										Drops.add(
												mmh.makeHead( mmh.langName.getString("bee.angry_pollinated", MobHeads.valueOf("BEE_ANGRY_POLLINATED").getName() + "")
														, MobHeads.valueOf("BEE_ANGRY_POLLINATED").getTexture().toString(), MobHeads.valueOf("BEE_ANGRY_POLLINATED").getOwner(), entity.getType(), entity.getKiller() )
												);
										mmh.LOGGER.debug("EDE Angry Pollinated Bee Head Dropped");
										if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
											announceBeheading(entity,
													mmh.langName.getString(name.toLowerCase() + ".angry_pollinated", MobHeads.valueOf("BEE_ANGRY_POLLINATED").getName() + "")
													.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
										}
									}
								} else if ((daAnger >= 1) && (daNectar == false)) {
									// ConfigHelper.Double(chanceConfig, "chance_percent.bee.angry", defpercent)
									if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig, "chance_percent.bee.angry", defpercent))) {
										Drops.add(
												mmh.makeHead( mmh.langName.getString("bee.angry", MobHeads.valueOf("BEE_ANGRY").getName() + "")
														, MobHeads.valueOf("BEE_ANGRY").getTexture().toString(), MobHeads.valueOf("BEE_ANGRY").getOwner(), entity.getType(), entity.getKiller() )
												);
										mmh.LOGGER.debug("EDE Angry Bee Head Dropped");
										if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
											announceBeheading(entity,
													mmh.langName.getString(name.toLowerCase() + ".angry", MobHeads.valueOf("BEE_ANGRY").getName() + "")
													.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
										}
									}
								} else if ((daAnger == 0) && (daNectar == true)) {
									// ConfigHelper.Double(chanceConfig, "chance_percent.bee.pollinated",
									// defpercent)
									if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig, "chance_percent.bee.pollinated", defpercent))) {
										Drops.add(
												mmh.makeHead( mmh.langName.getString("bee.pollinated", MobHeads.valueOf("BEE_POLLINATED").getName() + "")
														, MobHeads.valueOf("BEE_POLLINATED").getTexture().toString(), MobHeads.valueOf("BEE_POLLINATED").getOwner(), entity.getType(), entity.getKiller() )
												);
										mmh.LOGGER.debug("EDE Pollinated Bee Head Dropped");
										if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
											announceBeheading(entity,
													mmh.langName.getString(name.toLowerCase() + ".pollinated", MobHeads.valueOf("BEE_POLLINATED").getName() + "")
													.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
										}
									}
								} else if ((daAnger == 0) && (daNectar == false)) {
									// ConfigHelper.Double(chanceConfig, "chance_percent.bee.chance_percent",
									// defpercent)
									if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig, "chance_percent.bee.normal", defpercent))) {
										Drops.add(
												mmh.makeHead( mmh.langName.getString("bee.none", MobHeads.valueOf("BEE").getName() + "")
														, MobHeads.valueOf("BEE").getTexture().toString(), MobHeads.valueOf("BEE").getOwner(), entity.getType(), entity.getKiller() )
												);
										mmh.LOGGER.debug("EDE Bee Head Dropped");
										if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
											announceBeheading(entity,
													mmh.langName.getString(name.toLowerCase() + ".none", MobHeads.valueOf("BEE").getName() + "")
													.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
										}
									}
								}
								break;
							case "LLAMA":
								Llama daLlama = (Llama) entity;
								String daLlamaColor = daLlama.getColor().toString();
								//String daLlamaName = LlamaHeads.valueOf(name + "_" + daLlamaColor).getName() + " Head";// daLlamaColor.toLowerCase().replace("b",

								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig,
										"chance_percent.llama." + daLlamaColor.toLowerCase(), defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase() + "." + daLlamaColor.toLowerCase(), LlamaHeads.valueOf(name + "_" + daLlamaColor).getName() + "")
													, LlamaHeads.valueOf(name + "_" + daLlamaColor).getTexture().toString(), LlamaHeads.valueOf(name + "_" + daLlamaColor).getOwner(), entity.getType(), entity.getKiller() )
											);
									mmh.LOGGER.debug("EDE Llama Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString(name.toLowerCase() + "." + daLlamaColor.toLowerCase(), LlamaHeads.valueOf(name + "_" + daLlamaColor).getName() + "")
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "HORSE":
								Horse daHorse = (Horse) entity;
								String daHorseColor = daHorse.getColor().toString();
								//String daHorseName = HorseHeads.valueOf(name + "_" + daHorseColor).getName() + " Head";// daHorseColor.toLowerCase().replace("b",

								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig,
										"chance_percent.horse." + daHorseColor.toLowerCase(), defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase() + "." + daHorseColor.toLowerCase(), HorseHeads.valueOf(name + "_" + daHorseColor).getName() + "")
													, HorseHeads.valueOf(name + "_" + daHorseColor).getTexture().toString(), HorseHeads.valueOf(name + "_" + daHorseColor).getOwner(), entity.getType(), entity.getKiller() )
											);
									mmh.LOGGER.debug("EDE Horse Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString(name.toLowerCase() + "." + daHorseColor.toLowerCase(), HorseHeads.valueOf(name + "_" + daHorseColor).getName() + "")
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "MOOSHROOM":
								name = "MUSHROOM_COW";
							case "MUSHROOM_COW":
								MushroomCow daMushroom = (MushroomCow) entity;
								String daCowVariant = daMushroom.getVariant().toString();
								//String daCowName = daCowVariant.toLowerCase().replace("br", "Br").replace("re", "Re")									+ " Mooshroom Head";
								mmh.LOGGER.debug("EDE " + name + "_" + daCowVariant);
								// ConfigHelper.Double(chanceConfig, "chance_percent.mushroom_cow." +
								// daCowVariant.toLowerCase(), defpercent)
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig,
										"chance_percent.mushroom_cow." + daCowVariant.toLowerCase(), defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase() + "." + daCowVariant.toLowerCase(), MobHeads.valueOf(name + "_" + daCowVariant).getName() + "")
													, MobHeads.valueOf(name + "_" + daCowVariant).getTexture().toString(), MobHeads.valueOf(name + "_" + daCowVariant).getOwner(), entity.getType(), entity.getKiller() )
											);
									mmh.LOGGER.debug("EDE Mooshroom Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString(name.toLowerCase() + "." + daCowVariant.toLowerCase(), MobHeads.valueOf(name + "_" + daCowVariant).getName() + "")
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
								mmh.LOGGER.debug("EDE " + name + "_" + daPandaGene);
								// ConfigHelper.Double(chanceConfig, "chance_percent.panda." +
								// daPandaGene.toLowerCase(), defpercent)
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig,
										"chance_percent.panda." + daPandaGene.toLowerCase(), defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase() + "." + daPandaGene.toLowerCase(), MobHeads.valueOf(name + "_" + daPandaGene).getName() + "")
													, MobHeads.valueOf(name + "_" + daPandaGene).getTexture().toString(), MobHeads.valueOf(name + "_" + daPandaGene).getOwner(), entity.getType(), entity.getKiller() )
											);
									mmh.LOGGER.debug("EDE Panda Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString(name.toLowerCase() + "." + daPandaGene.toLowerCase(), MobHeads.valueOf(name + "_" + daPandaGene).getName() + "")
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "PARROT":
								Parrot daParrot = (Parrot) entity;
								String daParrotVariant = daParrot.getVariant().toString();
								//String daParrotName = daParrotVariant.toLowerCase().replace("b", "B").replace("c", "C")
								//		.replace("g", "G").replace("red", "Red") + " Parrot Head";
								mmh.LOGGER.debug("EDE " + name + "_" + daParrotVariant);
								// ConfigHelper.Double(chanceConfig, "chance_percent.parrot." +
								// daParrotVariant.toLowerCase(), defpercent)
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig,
										"chance_percent.parrot." + daParrotVariant.toLowerCase(), defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase() + "." + daParrotVariant.toLowerCase(), MobHeads.valueOf(name + "_" + daParrotVariant).getName() + "")
													, MobHeads.valueOf(name + "_" + daParrotVariant).getTexture().toString(), MobHeads.valueOf(name + "_" + daParrotVariant).getOwner(), entity.getType(), entity.getKiller() )
											);
									mmh.LOGGER.debug("EDE Parrot Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString(name.toLowerCase() + "." + daParrotVariant.toLowerCase(),MobHeads.valueOf(name + "_" + daParrotVariant).getName() + "")
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
								mmh.LOGGER.debug("EDE " + name + "_" + daRabbitType);
								// ConfigHelper.Double(chanceConfig, "chance_percent.rabbit." +
								// daRabbitType.toLowerCase(), defpercent)
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig,
										"chance_percent.rabbit." + daRabbitType.toLowerCase(), defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase() + "." + daRabbitType.toLowerCase(), RabbitHeads.valueOf(name + "_" + daRabbitType).getName() + "")
													, RabbitHeads.valueOf(name + "_" + daRabbitType).getTexture().toString(), RabbitHeads.valueOf(name + "_" + daRabbitType).getOwner(), entity.getType(), entity.getKiller() )
											);
									mmh.LOGGER.debug("EDE Rabbit Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString(name.toLowerCase() + "." + daRabbitType.toLowerCase(), RabbitHeads.valueOf(name + "_" + daRabbitType).getName() + "")
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "VILLAGER":
								Villager daVillager = (Villager) entity; // Location jobsite =
								// daVillager.getMemory(MemoryKey.JOB_SITE);
								String daVillagerType = daVillager.getVillagerType().toString();
								String daVillagerProfession = daVillager.getProfession().toString();
								mmh.LOGGER.debug("EDE name=" + name);
								mmh.LOGGER.debug("EDE profession=" + daVillagerProfession);
								mmh.LOGGER.debug("EDE type=" + daVillagerType);
								String daName = name + "_" + daVillagerProfession + "_" + daVillagerType;
								mmh.LOGGER.debug("EDE " + daName + "		 " + name + "_" + daVillagerProfession + "_"
										+ daVillagerType);
								//String daVillagerName = VillagerHeads.valueOf(daName).getName() + " Head";
								// ConfigHelper.Double(chanceConfig, "chance_percent.villager." +
								// daVillagerType.toLowerCase() + "." + daVillagerProfession.toLowerCase(),
								// defpercent)
								if (mmh.DropIt(event,
										ConfigHelper.Double(chanceConfig, "chance_percent.villager."
												+ daVillagerType.toLowerCase() + "." + daVillagerProfession.toLowerCase(),
												defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(daName.toLowerCase(), VillagerHeads.valueOf(daName).getName() + "")
													, VillagerHeads.valueOf(daName).getTexture().toString(), VillagerHeads.valueOf(daName).getOwner(), entity.getType(), entity.getKiller() )
											);
									mmh.LOGGER.debug("EDE Villager Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString(name.toLowerCase() + "." + daVillagerType.toLowerCase() + "." + daVillagerProfession.toLowerCase(),
														VillagerHeads.valueOf(name + "_" + daVillagerProfession + "_" + daVillagerType) .getName() + "")
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "ZOMBIE_VILLAGER":
								ZombieVillager daZombieVillager = (ZombieVillager) entity;
								String daZombieVillagerProfession = daZombieVillager.getVillagerProfession().toString();
								//String daZombieVillagerName = ZombieVillagerHeads.valueOf(name + "_" + daZombieVillagerProfession).getName() + " Head";
								mmh.LOGGER.debug("EDE " + name + "_" + daZombieVillagerProfession);
								// ConfigHelper.Double(chanceConfig, "chance_percent.zombie_villager",
								// defpercent)
								if (mmh.DropIt(event,
										ConfigHelper.Double(chanceConfig, "chance_percent.zombie_villager", defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase() + "." + daZombieVillagerProfession.toLowerCase()
											, ZombieVillagerHeads.valueOf(name + "_" + daZombieVillagerProfession).getName() + "")
													, ZombieVillagerHeads.valueOf(name + "_" + daZombieVillagerProfession).getTexture().toString()
													, ZombieVillagerHeads.valueOf(name + "_" + daZombieVillagerProfession).getOwner(), entity.getType(), entity.getKiller() )

											);
									mmh.LOGGER.debug("EDE Zombie Villager Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString(name.toLowerCase() + "." + daZombieVillagerProfession.toLowerCase(),
														ZombieVillagerHeads.valueOf(name + "_" + daZombieVillagerProfession).getName() + "")
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
								mmh.LOGGER.debug("EDE " + daSheepColor + "_" + name);
								// ConfigHelper.Double(chanceConfig, "chance_percent.sheep." +
								// daSheepColor.toLowerCase(), defpercent)
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig,
										"chance_percent.sheep." + daSheepColor.toLowerCase(), defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase() + "." + daSheepColor.toLowerCase(), SheepHeads.valueOf(name + "_" + daSheepColor).getName() + "")
													, SheepHeads.valueOf(name + "_" + daSheepColor).getTexture().toString(), SheepHeads.valueOf(name + "_" + daSheepColor).getOwner(), entity.getType(), entity.getKiller() )
											);
									mmh.LOGGER.debug("EDE Sheep Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString(name.toLowerCase() + "." + daSheepColor.toLowerCase(), SheepHeads.valueOf(name + "_" + daSheepColor).getName() + "")
												.replace(" Head", ""), entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "TRADER_LLAMA":
								TraderLlama daTraderLlama = (TraderLlama) entity;
								String daTraderLlamaColor = daTraderLlama.getColor().toString();
								//String daTraderLlamaName = LlamaHeads.valueOf(name + "_" + daTraderLlamaColor).getName() + " Head";
								mmh.LOGGER.debug("EDE " + daTraderLlamaColor + "_" + name);
								// ConfigHelper.Double(chanceConfig, "chance_percent.trader_llama." +
								// daTraderLlamaColor.toLowerCase(), defpercent)
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig,
										"chance_percent.trader_llama." + daTraderLlamaColor.toLowerCase(), defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase() + "." + daTraderLlamaColor.toLowerCase(), LlamaHeads.valueOf(name + "_" + daTraderLlamaColor).getName() + "")
													, LlamaHeads.valueOf(name + "_" + daTraderLlamaColor).getTexture().toString(), LlamaHeads.valueOf(name + "_" + daTraderLlamaColor).getOwner(), entity.getType(), entity.getKiller() )
											);
									mmh.LOGGER.debug("EDE Trader Llama Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName
												.getString(name.toLowerCase() + "." + daTraderLlamaColor.toLowerCase(),
														LlamaHeads.valueOf(name + "_" + daTraderLlamaColor).getName() + "")
												.replace(" Head", ""), entity.getKiller(),
												mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								break;
							case "AXOLOTL":
								Axolotl daAxolotl = (Axolotl) entity;
								String daAxolotlVariant = daAxolotl.getVariant().toString();
								//String daAxolotlName = MobHeads117.valueOf(name + "_" + daAxolotlVariant).getName() + " Head";
								mmh.LOGGER.debug("EDE " + daAxolotlVariant + "_" + name);
								// ConfigHelper.Double(chanceConfig, "chance_percent.axolotl." +
								// daAxolotlVariant.toLowerCase(), defpercent)
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig,
										"chance_percent.axolotl." + daAxolotlVariant.toLowerCase(), defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase() + "." + daAxolotlVariant.toLowerCase(), MobHeads117.valueOf(name + "_" + daAxolotlVariant).getName() + "")
													, MobHeads117.valueOf(name + "_" + daAxolotlVariant).getTexture().toString(), MobHeads117.valueOf(name + "_" + daAxolotlVariant).getOwner(), entity.getType(), entity.getKiller() )
											);
									mmh.LOGGER.debug("EDE Axolotl Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName
												.getString(name.toLowerCase() + "." + daAxolotlVariant.toLowerCase(),
														MobHeads117.valueOf(name + "_" + daAxolotlVariant).getName() + "")
												.replace(" Head", ""), entity.getKiller(),
												mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
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
								mmh.LOGGER.debug("EDE " + daGoatVariant + "_" + name);
								// ConfigHelper.Double(chanceConfig, "chance_percent.goat." +
								// daGoatVariant.toLowerCase(), defpercent)
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig,
										"chance_percent.goat." + daGoatVariant.toLowerCase(), defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase() + "." + daGoatVariant.toLowerCase(), MobHeads117.valueOf(name + "_" + daGoatVariant).getName() + "")
													, MobHeads117.valueOf(name + "_" + daGoatVariant).getTexture().toString(), MobHeads117.valueOf(name + "_" + daGoatVariant).getOwner(), entity.getType(), entity.getKiller()  )
											);
									mmh.LOGGER.debug("EDE Goat Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName
												.getString(name.toLowerCase() + "." + daGoatVariant.toLowerCase(),
														MobHeads117.valueOf(name + "_" + daGoatVariant).getName() + "")
												.replace(" Head", ""),
												entity.getKiller(),
												mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
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
									if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(), defpercent))) {
										Drops.add(
												mmh.makeHead( mmh.langName.getString(name.toLowerCase().replace("_", "."), MobHeads.valueOf(name).getName() + "")
														, MobHeads.valueOf(name).getTexture().toString(), MobHeads.valueOf(name).getOwner(), entity.getType(), entity.getKiller()  )
												);
										mmh.LOGGER.debug("EDE " + name + " Head Dropped");
										if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
											announceBeheading(entity, mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "").replace(" Head", ""), entity.getKiller(),
													mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
										}
									}
								} else // ConfigHelper.Double(chanceConfig,
									// "chance_percent." + name.toLowerCase(),
									// defpercent)
									if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(), defpercent))) {
										Drops.add(
												mmh.makeHead( mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
														, MobHeads.valueOf(name).getTexture().toString(), MobHeads.valueOf(name).getOwner(), entity.getType(), entity.getKiller()  )
												);
										mmh.LOGGER.debug("EDE " + name + " Head Dropped");
										if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
											announceBeheading(entity, mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "").replace(" Head", ""),
													entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
										}
									}

								mmh.LOGGER.debug("EDE " + MobHeads.valueOf(name) + " killed");
								break;
							case "FROG":
								Frog daFrog = (Frog) entity;
								String daFrogVariant = daFrog.getVariant().toString();
								name = name.concat("_" + daFrogVariant);
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig, "chance_percent." + name.replace("_", ".").toLowerCase(), defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.replace("_", ".").toLowerCase(), MobHeads119.valueOf(name).getName() + "")
													, MobHeads119.valueOf(name).getTexture().toString(), MobHeads119.valueOf(name).getOwner(), entity.getType(), entity.getKiller()  )
											);
									mmh.LOGGER.debug("EDE Frog Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString(name.replace("_", ".").toLowerCase(), MobHeads119.valueOf(name).getName() + "").replace(" Head", ""),
												entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}

								break;
							case "TADPOLE":
							case "ALLAY":
							case "WARDEN":
								mmh.LOGGER.debug("EDE TAW name=" + name);
								mmh.LOGGER.debug("EDE TAW texture=" + MobHeads119.valueOf(name).getTexture().toString());
								mmh.LOGGER.debug("EDE TAW location=" + entity.getLocation().toString());
								mmh.LOGGER.debug("EDE TAW getName=" + event.getEntity().getName());
								mmh.LOGGER.debug("EDE TAW killer=" + entity.getKiller().toString());
								// ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(),
								// defpercent)
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(), defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase(), MobHeads119.valueOf(name).getName() + "")
													, MobHeads119.valueOf(name).getTexture().toString(), MobHeads119.valueOf(name).getOwner(), entity.getType(), entity.getKiller()  )
											);
									mmh.LOGGER.debug("EDE " + name + " Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString(name.toLowerCase(), MobHeads119.valueOf(name).getName() + "").replace(" Head", ""),
												entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								mmh.LOGGER.debug("EDE " + MobHeads119.valueOf(name) + " killed");
								break;
							case "CAMEL":
							case "SNIFFER":
								mmh.LOGGER.debug("EDE CS name=" + name);
								mmh.LOGGER.debug("EDE CS texture=" + MobHeads120.valueOf(name).getTexture().toString());
								mmh.LOGGER.debug("EDE CS location=" + entity.getLocation().toString());
								mmh.LOGGER.debug("EDE CS getName=" + event.getEntity().getName());
								mmh.LOGGER.debug("EDE CS killer=" + entity.getKiller().toString());
								// ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(),
								// defpercent)
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(),
										defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase(), MobHeads120.valueOf(name).getName() + "")
													, MobHeads120.valueOf(name).getTexture().toString(), MobHeads120.valueOf(name).getOwner(), entity.getType(), entity.getKiller()  )
											);
									mmh.LOGGER.debug("EDE " + name + " Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString(name.toLowerCase(), MobHeads120.valueOf(name).getName() + "").replace(" Head", "")
												, entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								mmh.LOGGER.debug("EDE " + MobHeads120.valueOf(name) + " killed");
								break;
							case "VEX":
								mmh.LOGGER.debug("EDE name=" + name);
								mmh.LOGGER.debug("EDE texture=" + MobHeads.valueOf(name).getTexture().toString());
								mmh.LOGGER.debug("EDE location=" + entity.getLocation().toString());
								mmh.LOGGER.debug("EDE getName=" + event.getEntity().getName());
								mmh.LOGGER.debug("EDE killer=" + entity.getKiller().toString());
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(),
										defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
													, MobHeads.valueOf(name).getTexture().toString(), MobHeads.valueOf(name).getOwner(), entity.getType(), entity.getKiller()  )
											);
									mmh.LOGGER.debug("EDE " + name + " Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity, mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "").replace(" Head", ""),
												entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
									if (coinFlip()) {
										name = name.concat("_ANGRY");
										Drops.add(
												mmh.makeHead( mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
														, MobHeads.valueOf(name).getTexture().toString(), MobHeads.valueOf(name).getOwner(), entity.getType(), entity.getKiller()  )
												);
										mmh.LOGGER.debug("EDE " + name + " Head Dropped");
									}
									mmh.LOGGER.debug("EDE " + MobHeads.valueOf(name) + " killed");
								}
								break;
							default:
								// mmh.makeSkull(MobHeads.valueOf(name).getTexture(), name);
								mmh.LOGGER.debug("EDE name=" + name + " line:1122");
								mmh.LOGGER.debug("EDE texture=" + MobHeads.valueOf(name).getTexture().toString() + " line:1123");
								mmh.LOGGER.debug("EDE location=" + entity.getLocation().toString() + " line:1124");
								mmh.LOGGER.debug("EDE getName=" + event.getEntity().getName() + " line:1125");
								mmh.LOGGER.debug("EDE killer=" + entity.getKiller().toString() + " line:1126");
								// ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(),
								// defpercent)
								if (mmh.DropIt(event, ConfigHelper.Double(chanceConfig, "chance_percent." + name.toLowerCase(),
										defpercent))) {
									Drops.add(
											mmh.makeHead( mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "")
													, MobHeads.valueOf(name).getTexture().toString(), MobHeads.valueOf(name).getOwner(), entity.getType(), entity.getKiller()  )
											);
									mmh.LOGGER.debug("EDE " + name + " Head Dropped");
									if (mmh.config.getBoolean("head_settings.mob_heads.announce_kill.enabled", true)) {
										announceBeheading(entity,
												mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + "").replace(" Head", "")
												, entity.getKiller(), mmh.config.getBoolean("head_settings.mob_heads.announce_kill.displayname", true));
									}
								}
								mmh.LOGGER.debug("EDE " + MobHeads.valueOf(name) + " killed");
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

	@SuppressWarnings({ "static-access", "unused" })
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) { // onEntitySpawn(EntitySpawnEvent e) { // TODO: onCreatureSpawn
		try { // REPORT_CS_EVENT_ERROR "Error while entity spawning."
			if (mmh.config.getBoolean("wandering_trades.custom_wandering_trader", true)) {
				Entity entity = event.getEntity();
				if (entity instanceof WanderingTrader) {
					// traderHeads2 = YamlConfiguration.loadConfiguration(traderFile2);
					mmh.LOGGER.debug("CSE WanderingTrader spawned");
					WanderingTrader trader = (WanderingTrader) entity;
					List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();
					final List<MerchantRecipe> oldRecipes = trader.getRecipes();

					/**
					 * Player Heads
					 */
					if (mmh.config.getBoolean("wandering_trades.player_heads.enabled", true)) {
						// Check if default max heads is larger than number of playerheads
						int numOfplayerheads = (playerhead_recipes.size() - 1) >= 0 ? playerhead_recipes.size() - 1 : 0;
						int phMaxDef = (3 >= numOfplayerheads) ? 3 : numOfplayerheads;

						int playerRandom = mmh.randomBetween(mmh.config.getInt("wandering_trades.player_heads.min", 0),
								mmh.config.getInt("wandering_trades.player_heads.max", phMaxDef));
						mmh.LOGGER.debug("CSE.PH playerRandom=" + playerRandom);
						if ((playerRandom > 0) && (numOfplayerheads > 0)) {
							mmh.LOGGER.debug("CSE.PH playerRandom > 0");
							mmh.LOGGER.debug("CSE.PH numOfplayerheads=" + numOfplayerheads);
							HashSet<Integer> used = new HashSet<Integer>();
							outerLoop: for (int i = 0; i < playerRandom; i++) {
								int randomPlayerHead = mmh.randomBetween(0, numOfplayerheads);
								while (used.contains(randomPlayerHead) || (randomPlayerHead > numOfplayerheads)) { // while we
									// have
									// already
									// used
									// the
									// number
									randomPlayerHead = mmh.randomBetween(0, numOfplayerheads); // generate a new one because
									// it's already used
									// infinite loop catch
									if (i >= 500) {
										mmh.LOGGER.debug("CSE.PH timed out");
										break outerLoop;
									}
								}
								// by this time, add will be unique
								used.add(randomPlayerHead);
								recipes.add(playerhead_recipes.get(randomPlayerHead));
							}
							used.clear();
						}
					}
					/**
					 * Block Heads
					 */
					if (mmh.config.getBoolean("wandering_trades.block_heads.enabled", true)) {
						// check if default max block heads is larger than number of blockheads
						int numOfblockheads = BHNum >= 0 ? BHNum : 0;
						int bhMaxDef = (5 >= numOfblockheads) ? 5 : numOfblockheads;

						int min = mmh.config.getInt("wandering_trades.block_heads.pre_116.min", 0);
						int max;
						if (Double.parseDouble(mmh.getMCVersion().substring(0, 4)) >= 1.16) {
							max = mmh.config.getInt("wandering_trades.block_heads.pre_116.max", bhMaxDef);
						} else {
							max = mmh.config.getInt("wandering_trades.block_heads.pre_116.max", bhMaxDef);
						}
						mmh.LOGGER.debug("CSE BH1 min=" + min + " max=" + max);
						int blockRandom = mmh.randomBetween(min, max);
						mmh.LOGGER.debug("CSE blockRandom=" + blockRandom);
						if (blockRandom > 0) {
							mmh.LOGGER.debug("CSE blockRandom > 0");
							mmh.LOGGER.debug("CSE numOfblockheads=" + numOfblockheads);
							HashSet<Integer> used = new HashSet<Integer>();
							outerLoop: for (int i = 0; i < blockRandom; i++) {
								mmh.LOGGER.debug("CSE i=" + i);
								int randomBlockHead = mmh.randomBetween(0, numOfblockheads);
								while (used.contains(randomBlockHead)) {
									// while we have already used the number
									randomBlockHead = mmh.randomBetween(0, numOfblockheads);
									// generate a new one because it's already used
									if (i >= 500) {
										mmh.LOGGER.debug("CSE.BH1 timed out");
										break outerLoop;
									}
								}
								// by this time, add will be unique
								used.add(randomBlockHead);
								recipes.add(blockhead_recipes.get(randomBlockHead));
							}
							used.clear();
						}
					}

					/**
					 * Block Heads 2
					 */
					if (mmh.config.getBoolean("wandering_trades.block_heads.enabled", true)) {
						if (Double.parseDouble(mmh.getMCVersion().substring(0, 4)) >= 1.16) {
							// check if default max BH is larger than number of block heads
							int numOfblockheads = ((BHNum + BHNum2) - 1) >= 0 ? (BHNum + BHNum2) - 1 : 0;
							int bhMaxDef = (5 >= numOfblockheads) ? 5 : numOfblockheads;

							int min = mmh.config.getInt("wandering_trades.block_heads.is_116.min", 0);
							int max = mmh.config.getInt("wandering_trades.block_heads.is_116.max", bhMaxDef);
							mmh.LOGGER.debug("CSE BH2 min=" + min + " max=" + max);
							int blockRandom = mmh.randomBetween(min, max);
							mmh.LOGGER.debug("CSE blockRandom=" + blockRandom);
							if (blockRandom > 0) {
								mmh.LOGGER.debug("CSE blockRandom > 0");
								mmh.LOGGER.debug("CSE numOfblockheads=" + numOfblockheads);
								HashSet<Integer> used = new HashSet<Integer>();
								outerLoop: for (int i = 0; i < blockRandom; i++) {
									mmh.LOGGER.debug("CSE i=" + i);
									int randomBlockHead = mmh.randomBetween(BHNum - 1, numOfblockheads);
									while (used.contains(randomBlockHead)) {
										// while we have already used the number
										randomBlockHead = mmh.randomBetween(BHNum - 1, numOfblockheads);
										// generate a new one because it's already used
										if (i >= 500) {
											mmh.LOGGER.debug("CSE.BH2 timed out");
											break outerLoop;
										}
									}
									// by this time, add will be unique
									used.add(randomBlockHead);
									recipes.add(blockhead_recipes.get(randomBlockHead));
								}
								used.clear();
							}
						}
						if (Double.parseDouble(mmh.getMCVersion().substring(0, 4)) >= 1.17) {
							// check if default max BH is larger than number of blockheads
							int numOfblockheads = ((BHNum + BHNum2 + BHNum3) - 1) >= 0 ? (BHNum + BHNum2 + BHNum3) - 1 : 0;
							int bhMaxDef = (5 >= numOfblockheads) ? 5 : numOfblockheads;

							int min1 = mmh.config.getInt("wandering_trades.block_heads.is_117.min", 0);
							// int max1 = mmh.config.getInt("wandering_trades.block_heads.is_117.max",
							// 5) / 2;
							int max1 = mmh.config.getInt("wandering_trades.block_heads.is_117.max", 5);
							mmh.LOGGER.debug("CSE BH2 min=" + min1 + " max=" + max1);
							int blockRandom1 = mmh.randomBetween(min1, max1);
							mmh.LOGGER.debug("CSE blockRandom=" + blockRandom1);
							if (blockRandom1 > 0) {
								mmh.LOGGER.debug("CSE blockRandom > 0");
								mmh.LOGGER.debug("CSE numOfblockheads=" + numOfblockheads);
								HashSet<Integer> used = new HashSet<Integer>();
								outerLoop: for (int i = 0; i < blockRandom1; i++) {
									mmh.LOGGER.debug("CSE i=" + i);
									int randomBlockHead = mmh.randomBetween((BHNum + BHNum2) - 1, numOfblockheads);
									while (used.contains(randomBlockHead) || (randomBlockHead > numOfblockheads)) { // while
										// we have already used the number
										randomBlockHead = mmh.randomBetween((BHNum + BHNum2) - 1, numOfblockheads);
										// generate a new one because it's already used
										if (i >= 500) {
											mmh.LOGGER.debug("CSE.BH3 timed out");
											break outerLoop;
										}
									}
									// by this time, add will be unique
									used.add(randomBlockHead);
									recipes.add(blockhead_recipes.get(randomBlockHead));
								}
								used.clear();
							}
						}

					}

					/**
					 * Custom Trades
					 */
					if (mmh.config.getBoolean("wandering_trades.custom_trades.enabled", false)) {
						int numOfCustomTrades = (custometrade_recipes.size() - 1) >= 0 ? custometrade_recipes.size() - 1
								: 0;
						numOfCustomTrades = numOfCustomTrades - 1;
						int ctMaxDef = (5 >= numOfCustomTrades) ? 5 : numOfCustomTrades;

						int customRandom = mmh.randomBetween(
								mmh.config.getInt("wandering_trades.custom_trades.min", 0),
								mmh.config.getInt("wandering_trades.custom_trades.max", ctMaxDef));

						// if(debug){mmh.TEST.debug("CSE numOfCustomTrades=" + numOfCustomTrades);}
						// int customRandom =
						// randomBetween(getConfig().getInt("wandering_trades.min_custom_trades", 0),
						// mmh.config.getInt("wandering_trades.max_custom_trades", 3));
						mmh.LOGGER.debug("CSE customRandom=" + customRandom);
						if (customRandom > 0) {
							mmh.LOGGER.debug("CSE customRandom > 0");
							// for(int randomCustomTrade=1; randomCustomTrade<numOfCustomTrades;
							// randomCustomTrade++){
							HashSet<Integer> used = new HashSet<Integer>();
							for (int i = 0; i < customRandom; i++) {

								double chance = Math.random();
								mmh.LOGGER.debug("CSE chance=" + chance + " line:1540");
								if (mmh.traderCustom.getDouble("custom_trades.trade_" + i + ".chance", 0.002) > chance) {
									recipes.add(custometrade_recipes.get(i));
								}
								if (i >= 500) {
									mmh.LOGGER.debug("CSE.CT timed out");
									break;
								}
							}
							used.clear();
						}
					}

					if (mmh.config.getBoolean("wandering_trades.keep_default_trades", true)) {
						recipes.addAll(oldRecipes);
					}
					trader.setRecipes(recipes);
					/** }});// */
				}

			}
		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CS_EVENT_ERROR).error(exception));
		}

	}

	@Override
	@SuppressWarnings({ "unused", "static-access", "rawtypes", "unchecked" })
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { // TODO: Commands
		try {
			// log("command=" + cmd.getName() + " args=" + args[0] + args[1]);
			if (cmd.getName().equalsIgnoreCase("MMH")) {
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
						sender.sendMessage(ChatColor.WHITE + " /mmh toggledebug - "
								+ mmh.get("mmh.command.debuguse", "Temporarily toggles debug."));// Cancels SinglePlayerSleep");
						if (mmh.config.getBoolean("wandering_trades.custom_wandering_trader", true)) {
							sender.sendMessage(ChatColor.WHITE + " /mmh playerheads - "
									+ mmh.get("mmh.command.playerheads", "Shows how to use the playerheads commands"));
							// sender.sendMessage(ChatColor.WHITE + " /mmh blockheads - " +
							// mmh.lang.get("blockheads", "Shows how to use the blockheads commands"));
							sender.sendMessage(ChatColor.WHITE + " /mmh customtrader - "
									+ mmh.get("mmh.command.customtrader", "Shows how to use the customtrader commands"));
						}
						sender.sendMessage(ChatColor.WHITE + " /mmh fixhead - " + mmh.get("mmh.command.headfix"));
						sender.sendMessage(ChatColor.WHITE + " /mmh givemh - " + mmh.get("mmh.command.give.mobhead"));
						sender.sendMessage(ChatColor.WHITE + " /mmh giveph - " + mmh.get("mmh.command.give.playerhead"));
						sender.sendMessage(ChatColor.WHITE + " /mmh givebh - " + mmh.get("mmh.command.give.blockhead"));
						sender.sendMessage(
								ChatColor.WHITE + " /mmh display perms/vars - " + mmh.get("mmh.command.display.help"));
						sender.sendMessage(ChatColor.WHITE + " ");
						sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
						+ ChatColor.GREEN + "]===============[]");
						return true;
					}
				}catch (Exception exception) {
					reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_MENU_ERROR).error(exception));
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
						reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_HEADNBT_ERROR).error(exception));
					}
				}
				// /mmh display permvar playername
				// / 0 1 2
				if (args[0].equalsIgnoreCase("display")) {
					if (args[1].equalsIgnoreCase("perms") || args[1].equalsIgnoreCase("permissions")) {
						try { // REPORT_COMMAND_DISPLAY_PERMS "Error executing Display Perms command."
							if (sender instanceof Player) {
								Player player = (Player) sender;
								sender.sendMessage(
										"" + mmh.get("mmh.command.display.you").replace("<player>", player.getDisplayName()));
								sender.sendMessage("moremobheads.players=" + player.hasPermission("moremobheads.players"));
								sender.sendMessage("moremobheads.mobs=" + player.hasPermission("moremobheads.mobs"));
								sender.sendMessage("moremobheads.nametag=" + player.hasPermission("moremobheads.nametag"));
								sender.sendMessage("moremobheads.reload=" + player.hasPermission("moremobheads.reload"));
								sender.sendMessage(
										"moremobheads.toggledebug=" + player.hasPermission("moremobheads.toggledebug"));
								sender.sendMessage("moremobheads.showUpdateAvailable="
										+ player.hasPermission("moremobheads.showUpdateAvailable"));
								sender.sendMessage(
										"moremobheads.customtrader=" + player.hasPermission("moremobheads.customtrader"));
								sender.sendMessage(
										"moremobheads.playerheads=" + player.hasPermission("moremobheads.playerheads"));
								sender.sendMessage(
										"moremobheads.blockheads=" + player.hasPermission("moremobheads.blockheads"));
								sender.sendMessage("moremobheads.fixhead=" + player.hasPermission("moremobheads.fixhead"));
								sender.sendMessage("moremobheads.give=" + player.hasPermission("moremobheads.give"));
								sender.sendMessage(
										"" + mmh.getName() + " " + mmh.getDescription().getVersion() + " display perms end");
							} else if (args.length >= 2) {
								Player player = sender.getServer().getPlayer(args[2]);
								sender.sendMessage(""
										+ mmh.get("mmh.command.display.them").replace("<player>", player.getDisplayName()));
								sender.sendMessage("moremobheads.players=" + player.hasPermission("moremobheads.players"));
								sender.sendMessage("moremobheads.mobs=" + player.hasPermission("moremobheads.mobs"));
								sender.sendMessage("moremobheads.nametag=" + player.hasPermission("moremobheads.nametag"));
								sender.sendMessage("moremobheads.reload=" + player.hasPermission("moremobheads.reload"));
								sender.sendMessage(
										"moremobheads.toggledebug=" + player.hasPermission("moremobheads.toggledebug"));
								sender.sendMessage("moremobheads.showUpdateAvailable="
										+ player.hasPermission("moremobheads.showUpdateAvailable"));
								sender.sendMessage(
										"moremobheads.customtrader=" + player.hasPermission("moremobheads.customtrader"));
								sender.sendMessage(
										"moremobheads.playerheads=" + player.hasPermission("moremobheads.playerheads"));
								sender.sendMessage(
										"moremobheads.blockheads=" + player.hasPermission("moremobheads.blockheads"));
								sender.sendMessage("moremobheads.fixhead=" + player.hasPermission("moremobheads.fixhead"));
								sender.sendMessage("moremobheads.give=" + player.hasPermission("moremobheads.give"));
								sender.sendMessage("" + mmh.getName() + " " + mmh.getDescription().getVersion()
										+ " display perms end");
							} else {
								sender.sendMessage("Console can only check permissions of Players.");
							}
						}catch (Exception exception) {
							reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_DISPLAY_PERMS).error(exception));
						}
					} else if (args[1].equalsIgnoreCase("vars") || args[1].equalsIgnoreCase("variables")) {
						try { // REPORT_COMMAND_DISPLAY_VARS "Error executing Display Vars command."
							sender.sendMessage(
									"" + mmh.getName() + " " + mmh.getDescription().getVersion() + " display varss start");
							sender.sendMessage("debug=" + debug);
							sender.sendMessage("daLang=" + mmh.daLang);

							world_whitelist = mmh.config.getString("global_settings.world.whitelist", "");
							world_blacklist = mmh.config.getString("global_settings.world.blacklist", "");
							mob_whitelist = mmh.config.getString("head_settings.mob_heads.whitelist", "");
							mob_blacklist = mmh.config.getString("head_settings.mob_heads.blacklist", "");

							sender.sendMessage("world_whitelist=" + world_whitelist);
							sender.sendMessage("world_blacklist=" + world_blacklist);
							sender.sendMessage("mob_whitelist=" + mob_whitelist);
							sender.sendMessage("mob_blacklist=" + mob_blacklist);
							sender.sendMessage("" + mmh.getName() + " " + mmh.getDescription().getVersion() + " display varss end");
						}catch (Exception exception) {
							reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_DISPLAY_VARS).error(exception));
						}
					} else if (args[1].equalsIgnoreCase("chance") || args[1].equalsIgnoreCase("chance_percent")) {
						try { // REPORT_COMMAND_DISPLAY_CHANCE "Error executing Display Chance command."
							ConfigurationSection cs = chanceConfig.getConfigurationSection("chance_percent");
							List<String> daSet = new ArrayList<String>();
							// log(Level.INFO, "args.lngth=" + args.length);
							if (args.length == 3) {
								for (String key : cs.getKeys(true)) {
									if (key.contains(args[2])) {
										sender.sendMessage("" + key + "=" + cs.get(key));
										daSet.add("" + key + "=" + cs.get(key));
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
										mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLUGIN_UNKNOWN_ERROR).error(exception));
									} finally {
										pw.close();
									}
									sender.sendMessage("chance_config.yml has been dumped into " + chanceFile.toString());
									mmh.LOGGER.log("chance_config.yml has been dumped into " + chanceFile.toString());
								} else {
									sender.sendMessage("" + args[2] + " was not found in chance_percent.yml");
								}
							} else {
								for (String key : cs.getKeys(true)) {
									if (!cs.get(key).toString().contains("MemorySection")) {
										sender.sendMessage("" + key + "=" + cs.get(key).toString());
										daSet.add("" + key + "=" + cs.get(key).toString());
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
										mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLUGIN_UNKNOWN_ERROR).error(exception));
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
							reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_DISPLAY_CHANCE).error(exception));
						}
					} else if ( args[1].equalsIgnoreCase("config") ) {
						mmh.LOGGER.debug("Config.yml DUMP - INCLUDE THIS WITH ANY ISSUE REPORT VVV");
						mmh.dumpConfig(mmh.getConfig());
						mmh.LOGGER.debug("Config.yml DUMP - INCLUDE THIS WITH ANY ISSUE REPORT ^^^");
					}
				}
				if (args[0].equalsIgnoreCase("reload")) { //TODO: Reload command
					try { // REPORT_COMMAND_RELOAD_ERROR "Error executing Reload command."
						String perm = "moremobheads.reload";
						boolean hasPerm = sender.hasPermission(perm) || !(sender instanceof Player) ? true : false;
						mmh.LOGGER.debug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
						if ((hasPerm || mmh.isDev) || !(sender instanceof Player) ) {
							//mmh.configReload();
							mmh.LOGGER.log("Reloading 1.20 EventHandler...");
							long startTime = System.currentTimeMillis();
							try {
								mmh.config.load(new File(mmh.getDataFolder(), "config.yml"));
							} catch (Exception exception) {
								mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_CANNOT_LOAD_CONFIG).error(exception));
							}
							world_whitelist = mmh.config.getString("global_settings.world.whitelist", "");
							world_blacklist = mmh.config.getString("global_settings.world.blacklist", "");
							mob_whitelist = mmh.config.getString("head_settings.mob_heads.whitelist", "");
							mob_blacklist = mmh.config.getString("head_settings.mob_heads.blacklist", "");
							debug = mmh.debug;
							chanceConfig = mmh.chanceConfig;
							blockFile117 = new File(mmh.getDataFolder() + "" + File.separatorChar + "block_heads_1_17.yml");
							blockFile1172 = new File(mmh.getDataFolder() + "" + File.separatorChar + "block_heads_1_17_2.yml");
							blockFile120 = new File(mmh.getDataFolder() + "" + File.separatorChar + "block_heads_1_20.yml");

							if (mmh.config.getBoolean("wandering_trades.custom_wandering_trader", true)) {
								if (!blockFile117.exists()) {
									mmh.saveResource("block_heads_1_17.yml", true);
									mmh.LOGGER.log("block_heads_1_17.yml not found! Creating in " + mmh.getDataFolder() + "");
								}
								if (!blockFile1172.exists()) {
									mmh.saveResource("block_heads_1_17_2.yml", true);
									mmh.LOGGER.log("block_heads_1_17_2.yml not found! Creating in " + mmh.getDataFolder() + "");
								}
								if (!blockFile120.exists()) {
									mmh.saveResource("block_heads_1_20.yml", true);
									mmh.LOGGER.log("block_heads_1_20.yml not found! Creating in " + mmh.getDataFolder() + "");
								}
								blockHeads = new YamlConfiguration();
								try {
									mmh.LOGGER.log("Loading " + blockFile117 + "...");
									blockHeads.load(blockFile117);
								} catch (Exception exception) {
									mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLUGIN_UNKNOWN_ERROR).error(exception));
								}
								blockHeads2 = new YamlConfiguration();
								try {
									mmh.LOGGER.log("Loading " + blockFile1172 + "...");
									blockHeads2.load(blockFile1172);
								} catch (Exception exception) {
									mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLUGIN_UNKNOWN_ERROR).error(exception));
								}
								blockHeads3 = new YamlConfiguration();
								try {
									mmh.LOGGER.log("Loading " + blockFile120 + "...");
									blockHeads3.load(blockFile120);
								} catch (Exception exception) {
									mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLUGIN_UNKNOWN_ERROR).error(exception));
								}

								boolean showlore = mmh.config.getBoolean("head_settings.lore.show_plugin_name", true);
								ArrayList<String> headlore = new ArrayList();
								headlore.add(ChatColor.AQUA + "" + mmh.getName());

								if(!mmh.playerFile.exists()){
									mmh.saveResource("player_heads.yml", true);
									mmh.LOGGER.log("player_heads.yml not found! copied player_heads.yml to " + mmh.getDataFolder() + "");
								}
								mmh.LOGGER.log("Loading player_heads file...");
								mmh.playerHeads = new YamlConfiguration();
								try {
									mmh.playerHeads.load(mmh.playerFile);
								} catch (Exception exception) {
									reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLAYERHEAD_LOAD_ERROR).error(exception));
								}

								mmh.LOGGER.log("Loading PlayerHead Recipes...");
								for (int i = 1; i < (mmh.playerHeads.getInt("players.number") + 1); i++) {
									ItemStack price1 = mmh.playerHeads.getItemStack("players.player_" + i + ".price_1",
											new ItemStack(Material.AIR));
									ItemStack price2 = mmh.playerHeads.getItemStack("players.player_" + i + ".price_2",
											new ItemStack(Material.AIR));
									ItemStack itemstack = mmh.playerHeads.getItemStack("players.player_" + i + ".itemstack",
											new ItemStack(Material.AIR));
									if (showlore) {
										SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
										meta.setLore(headlore);
										itemstack.setItemMeta(meta);
										itemstack.setItemMeta(meta);
									}
									MerchantRecipe recipe = new MerchantRecipe(itemstack,
											mmh.playerHeads.getInt("players.player_" + i + ".quantity", 3));
									recipe.addIngredient(price1);
									recipe.addIngredient(price2);
									playerhead_recipes.add(recipe);
								}
								mmh.LOGGER.log(playerhead_recipes.size() + " PlayerHead Recipes ADDED...");
								mmh.LOGGER.log("Loading BlockHead Recipes...");
								BHNum = blockHeads.getInt("blocks.number");
								// BlockHeads
								mmh.LOGGER.log("BlockHeads=" + BHNum);
								for (int i = 1; i < (BHNum + 1); i++) {
									ItemStack price1 = blockHeads.getItemStack("blocks.block_" + i + ".price_1",
											new ItemStack(Material.AIR));
									ItemStack price2 = blockHeads.getItemStack("blocks.block_" + i + ".price_2",
											new ItemStack(Material.AIR));
									ItemStack itemstack = blockHeads.getItemStack("blocks.block_" + i + ".itemstack",
											new ItemStack(Material.AIR));
									if (showlore) {
										SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
										meta.setLore(headlore);
										itemstack.setItemMeta(meta);
										itemstack.setItemMeta(meta);
									}
									MerchantRecipe recipe = new MerchantRecipe(itemstack,
											blockHeads.getInt("blocks.block_" + i + ".quantity", 8));
									recipe.setExperienceReward(true);
									recipe.addIngredient(price1);
									recipe.addIngredient(price2);
									blockhead_recipes.add(recipe);
								}
								BHNum2 = blockHeads2.getInt("blocks.number");
								// blockHeads 2
								mmh.LOGGER.log("BlockHeads2=" + BHNum2);
								for (int i = 1; i < (BHNum2 + 1); i++) {
									ItemStack price1 = blockHeads2.getItemStack("blocks.block_" + i + ".price_1",
											new ItemStack(Material.AIR));
									ItemStack price2 = blockHeads2.getItemStack("blocks.block_" + i + ".price_2",
											new ItemStack(Material.AIR));
									ItemStack itemstack = blockHeads2.getItemStack("blocks.block_" + i + ".itemstack",
											new ItemStack(Material.AIR));
									if (showlore) {
										SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
										meta.setLore(headlore);
										itemstack.setItemMeta(meta);
										itemstack.setItemMeta(meta);
									}
									MerchantRecipe recipe = new MerchantRecipe(itemstack,
											blockHeads2.getInt("blocks.block_" + i + ".quantity", 8));
									recipe.setExperienceReward(true);
									recipe.addIngredient(price1);
									recipe.addIngredient(price2);
									blockhead_recipes.add(recipe);
								}
								BHNum3 = blockHeads3.getInt("blocks.number");
								// blockHeads 3
								mmh.LOGGER.log("BlockHeads3=" + BHNum3);
								for (int i = 1; i < (BHNum3 + 1); i++) {
									ItemStack price1 = blockHeads3.getItemStack("blocks.block_" + i + ".price_1",
											new ItemStack(Material.AIR));
									ItemStack price2 = blockHeads3.getItemStack("blocks.block_" + i + ".price_2",
											new ItemStack(Material.AIR));
									ItemStack itemstack = blockHeads3.getItemStack("blocks.block_" + i + ".itemstack",
											new ItemStack(Material.AIR));
									if (showlore) {
										SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
										meta.setLore(headlore);
										itemstack.setItemMeta(meta);
										itemstack.setItemMeta(meta);
									}
									MerchantRecipe recipe = new MerchantRecipe(itemstack,
											blockHeads3.getInt("blocks.block_" + i + ".quantity", 8));
									recipe.setExperienceReward(true);
									recipe.addIngredient(price1);
									recipe.addIngredient(price2);
									blockhead_recipes.add(recipe);
								}

								mmh.LOGGER.log(blockhead_recipes.size() + " BlockHead Recipes ADDED...");
								mmh.LOGGER.log("Loading CustomTrades Recipes...");
								for (int i = 1; i < (mmh.traderCustom.getInt("custom_trades.number") + 1); i++) {
									ItemStack price1 = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".price_1",
											new ItemStack(Material.AIR));
									ItemStack price2 = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".price_2",
											new ItemStack(Material.AIR));
									ItemStack itemstack = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".itemstack",
											new ItemStack(Material.AIR));
									/** Code to fix missing noteblock SkullMeta */
									boolean doIt = Utils.isSupportedVersion("1.20.2.3936");
									if(doIt) {
										mmh.LOGGER.log("doIt=" + doIt);
										if(itemstack.getType().equals(Material.PLAYER_HEAD)) {
											SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
											String sound = mmh.traderCustom.getString("custom_trades.trade_" + i + ".note_block_sound", "entity.player.hurt");
											meta.setNoteBlockSound(NamespacedKey.minecraft(sound));
											itemstack.setItemMeta(meta);
										}//*/
									}
									/** Code to fix missing noteblock SkullMeta */
									MerchantRecipe recipe = new MerchantRecipe(itemstack,
											mmh.traderCustom.getInt("custom_trades.trade_" + i + ".quantity", 1));
									recipe.setExperienceReward(true);
									recipe.addIngredient(price1);
									recipe.addIngredient(price2);
									custometrade_recipes.add(recipe);
								}
								mmh.LOGGER.log(custometrade_recipes.size() + " CustomTrades Recipes ADDED...");
								mmh.LOGGER.log("EventHandler_1_20 took " + mmh.LoadTime(startTime) + "ms to load");
							}
							sender.sendMessage(
									ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.get("mmh.message.reloaded"));

							return true;
						} else if (!hasPerm) {
							sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
									+ mmh.get("mmh.message.noperm").toString().replace("<perm>", perm));
							return false;
						}
					}catch (Exception exception) {
						reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_RELOAD_ERROR).error(exception));
					}
				}
				if (args[0].equalsIgnoreCase("toggledebug") || args[0].equalsIgnoreCase("td")) {
					try { // REPORT_COMMAND_TOGGLE_DEBUG "Error executing ToggleDebug Command."
						String perm = "moremobheads.toggledebug";
						boolean hasPerm = sender.hasPermission(perm) || !(sender instanceof Player) ? true : false;
						mmh.LOGGER.debug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
						if (sender.isOp() || (hasPerm || mmh.isDev) || !(sender instanceof Player) ) {
							debug = !debug;
							mmh.debug = debug;
							sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
									+ mmh.get("mmh.message.debugtrue").toString().replace("boolean", "" + debug));
							return true;
						} else if (!hasPerm) {
							sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
									+ mmh.get("mmh.message.noperm").toString().replace("<perm>", perm));
							return false;
						}
					}catch (Exception exception) {
						reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_TOGGLE_DEBUG).error(exception));

					}
				}
				if (args[0].equalsIgnoreCase("customtrader") || args[0].equalsIgnoreCase("ct")) {
					try { // REPORT_COMMAND_CUSTOM_TRADER "Error executing CustomTrader Command."
						String perm = "moremobheads.customtrader";
						boolean hasPerm = sender.hasPermission(perm);
						mmh.LOGGER.debug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
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
								mmh.LOGGER.debug("CMD CT ADD Start -----");
								ItemStack itemstack = player.getInventory().getItemInOffHand();
								ItemStack price1 = player.getInventory().getItem(0);
								ItemStack price2 = player.getInventory().getItem(1);
								if (price1 == null) {
									price1 = new ItemStack(Material.AIR);
								}
								if (price2 == null) {
									price2 = new ItemStack(Material.AIR);
								}
								// Material price1 = item1.getType();
								// Material price2 = item2.getType();

								if ((itemstack.getType() == Material.AIR) || (price1 == null) || (price1.getType() == Material.AIR)) {
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
									mmh.LOGGER.debug("CMD CT ADD End Error -----");
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
										NamespacedKey sound = meta.getNoteBlockSound();
										mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".note_block_sound", sound.getKey());
										mmh.LOGGER.debug("sound.getKey()=" + sound.getNamespace());
										mmh.LOGGER.debug("sound.getKey()=" + sound.getKey());

									}
								}
								/** Code to fix missing noteblock SkullMeta */
								mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".quantity",
										itemstack.getAmount());
								mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".chance", 0.002);
								mmh.traderCustom.set("custom_trades.number", (tradeNumber + 1));
								mmh.LOGGER.debug("CMD CT ADD price1=" + price1.getType());
								mmh.LOGGER.debug("CMD CT ADD price2=" + price2.getType());
								mmh.LOGGER.debug("CMD CT ADD itemstack=" + itemstack.getType());
								if (itemstack.getType() == Material.PLAYER_HEAD) {
									ItemMeta skullMeta = itemstack.getItemMeta();
									mmh.LOGGER.debug("CMD CT ADD IS DisplayName=" + skullMeta.getDisplayName());
									if (skullMeta.hasLore()) {
										mmh.LOGGER.debug("CMD CT ADD IS lore=" + String.join(",", skullMeta.getLore()));

									}
								}
								mmh.LOGGER.debug("CMD CT ADD quantity=" + itemstack.getAmount());
								mmh.LOGGER.debug("CMD CT ADD chance=0.002");
								// log("customFile=" + customFile);
								try {
									mmh.traderCustom.save(mmh.customFile);
									mmh.traderCustom.load(mmh.customFile);
								} catch (Exception exception) {
									mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLUGIN_UNKNOWN_ERROR).error(exception));
								}
								mmh.LOGGER.debug("CMD CT ADD End -----");
								sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " trade_"
										+ (tradeNumber + 1) + " " + mmh.get("mmh.message.ct.successadd"));
								return true;
							} else if (args[1].equalsIgnoreCase("remove")) {
								mmh.LOGGER.debug("CMD CT Remove Start -----");
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
									mmh.LOGGER.debug("customFile=" + mmh.customFile);
									try {
										mmh.traderCustom.save(mmh.customFile);
										mmh.traderCustom.load(mmh.customFile);
									} catch (Exception exception) {
										mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLUGIN_UNKNOWN_ERROR).error(exception));

										mmh.LOGGER.debug("CMD CT Remove End Exception -----");

										sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
												+ mmh.get("mmh.command.ct.error"));
										return false;
										// e.printStackTrace();
									}
									mmh.LOGGER.debug("CMD CT Remove End -----");
									sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " trade_"
											+ args[2] + " " + mmh.get("mmh.message.ct.successrem"));
									return true;
								} else {
									mmh.LOGGER.debug("CMD CT Remove End 2 -----");
									sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
											+ mmh.get("mmh.command.ct.numberreq"));
									return false;
								}
							} else if (args[1].equalsIgnoreCase("replace")) {
								mmh.LOGGER.debug("CMD CT Replace Start -----");
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
									if ((itemstack.getType() == Material.AIR) || (price1 == null)
											|| (price1.getType() == Material.AIR)) {
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
										mmh.LOGGER.debug("CMD CT Replace End Error -----");
										return false;
									}
									int tradeNumber = Integer.parseInt(args[2]);
									mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".price_1", price1);
									mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".price_2", price2);
									mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".itemstack", itemstack);
									/** Code to fix missing noteblock SkullMeta */
									boolean doIt = Utils.isSupportedVersion("1.20.2.3936");
									if(doIt) {
										mmh.LOGGER.log("CT A doIt=" + doIt);
										if(itemstack.getType().equals(Material.PLAYER_HEAD)) {
											SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
											NamespacedKey sound = meta.getNoteBlockSound();
											mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".note_block_sound", sound.getKey());
											mmh.LOGGER.debug("sound.getKey()=" + sound.getNamespace());
											mmh.LOGGER.debug("sound.getKey()=" + sound.getKey());
										}
									}
									/** Code to fix missing noteblock SkullMeta */
									mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".quantity",
											itemstack.getAmount());
									mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".chance", 0.002);
									mmh.LOGGER.debug("CMD CT Replace price1=" + price1.getType());
									mmh.LOGGER.debug("CMD CT Replace price2=" + price2.getType());
									mmh.LOGGER.debug("CMD CT Replace itemstack=" + itemstack.getType());
									if (itemstack.getType() == Material.PLAYER_HEAD) {
										ItemMeta skullMeta = itemstack.getItemMeta();
										mmh.LOGGER.debug("CMD CT Replace IS DisplayName=" + skullMeta.getDisplayName());
										if (skullMeta.hasLore()) {
											mmh.LOGGER.debug("CMD CT Replace IS lore=" + String.join(",", skullMeta.getLore()));
										}
										mmh.LOGGER.debug("CMD CT Replace quantity=" + itemstack.getAmount());
										mmh.LOGGER.debug("CMD CT Replace chance=0.002");
										// log("customFile=" + customFile);
										try {
											mmh.traderCustom.save(mmh.customFile);
											mmh.traderCustom.load(mmh.customFile);
										} catch (Exception exception) {
											mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLUGIN_UNKNOWN_ERROR).error(exception));
											mmh.LOGGER.debug("CMD CT Replace End Exception -----");
											sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
													+ mmh.get("mmh.command.ct.error"));
											return false;
											// e.printStackTrace();
										}
										mmh.LOGGER.debug("CMD CT Replace End -----");
										sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " trade_"
												+ args[2] + " " + mmh.get("mmh.message.ct.successrep"));
										return true;
									} else {
										mmh.LOGGER.debug("CMD CT Replace End 2 -----");
										sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
												+ mmh.get("mmh.command.ct.numberreq"));
										return false;
									}
								}
							}
						} else if (!(sender instanceof Player)) {
							mmh.LOGGER.debug("CMD CT Replace End Console -----");
							sender.sendMessage(
									ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.get("mmh.message.noconsole"));
							return false;
						} else if (!hasPerm) {
							mmh.LOGGER.debug("CMD CT Replace End !Perm -----");
							sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
									+ mmh.get("mmh.message.nopermordisabled").toString().replace("<perm>", perm));
							return false;
						}

					}catch (Exception exception) {
						reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_CUSTOM_TRADER).error(exception));
					}
				}
				if (args[0].equalsIgnoreCase("playerheads") || args[0].equalsIgnoreCase("ph")) {
					try { // REPORT_COMMAND_PLAYER_HEADS "Error executing PlayerHeads Command."
						String perm = "moremobheads.playerheads";
						boolean hasPerm = sender.hasPermission(perm);
						mmh.LOGGER.debug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
						if ( (hasPerm || mmh.isDev) && (sender instanceof Player)
								&& mmh.config.getBoolean("wandering_trades.custom_wandering_trader", true)) {
							Player player = (Player) sender;
							if (!(args.length >= 2)) {
								sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
								+ ChatColor.GREEN + "]===============[]");
								sender.sendMessage(ChatColor.WHITE + " ");
								sender.sendMessage(ChatColor.WHITE + " /mmh ph - " + mmh.get("mmh.command.ct.help"));
								sender.sendMessage(ChatColor.WHITE + " /mmh ph add - " + mmh.get("mmh.command.ct.add")
								+ "player_heads.yml");
								sender.sendMessage(ChatColor.WHITE + " /mmh ph remove # - "
										+ mmh.get("mmh.command.ct.remove").replace("custom_trades", "playerheads"));
								sender.sendMessage(ChatColor.WHITE + " /mmh ph replace # - " + mmh.get("mmh.command.ct.replace")
								.replace("<num>", "#").replace("custom trade", "pleayerhead"));
								// sender.sendMessage(ChatColor.WHITE + " ");
								sender.sendMessage(ChatColor.WHITE + " ");
								sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
								+ ChatColor.GREEN + "]===============[]");
								return true;
							} else if (args[1].equalsIgnoreCase("add")) {
								mmh.LOGGER.debug("CMD PH ADD Start -----");
								ItemStack itemstack = player.getInventory().getItemInOffHand();
								ItemStack price1 = player.getInventory().getItem(0);
								ItemStack price2 = player.getInventory().getItem(1);
								if (price1 == null) {
									price1 = new ItemStack(Material.AIR);
								}
								if (price2 == null) {
									price2 = new ItemStack(Material.AIR);
								}
								// Material price1 = item1.getType();
								// Material price2 = item2.getType();

								if ((itemstack.getType() == Material.AIR) || (price1 == null) || (price1.getType() == Material.AIR)
										|| (itemstack.getType() != Material.PLAYER_HEAD)) {
									sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
									+ ChatColor.GREEN + "]===============[]");
									sender.sendMessage(ChatColor.WHITE + " ");
									if (itemstack.getType() != Material.PLAYER_HEAD) {
										sender.sendMessage(ChatColor.RED + " MUST BE PLAYERHEAD");
										sender.sendMessage(ChatColor.WHITE + " ");
									}
									sender.sendMessage(
											ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line1") + "player_heads.yml");
									sender.sendMessage(ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line2"));
									sender.sendMessage(ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line3"));
									sender.sendMessage(ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line4") + "/mmh ph add");
									sender.sendMessage(
											ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line5") + "player head.");
									sender.sendMessage(ChatColor.WHITE + " ");
									sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName()
									+ ChatColor.GREEN + "]===============[]");
									mmh.LOGGER.debug("CMD PH ADD End Error -----");
									return false;
								}
								int tradeNumber = (int) mmh.playerHeads.get("players.number", 1);
								mmh.playerHeads.set("players.player_" + (tradeNumber + 1) + ".price_1", price1);
								mmh.playerHeads.set("players.player_" + (tradeNumber + 1) + ".price_2", price2);
								mmh.playerHeads.set("players.player_" + (tradeNumber + 1) + ".itemstack", itemstack);
								mmh.playerHeads.set("players.player_" + (tradeNumber + 1) + ".quantity", itemstack.getAmount());
								mmh.LOGGER.debug("CMD PH ADD price1=" + price1.getType());
								mmh.LOGGER.debug("CMD PH ADD price2=" + price2.getType());
								mmh.LOGGER.debug("CMD PH ADD itemstack=" + itemstack.getType());
								if (itemstack.getType() == Material.PLAYER_HEAD) {
									ItemMeta skullMeta = itemstack.getItemMeta();
									mmh.LOGGER.debug("CMD PH ADD IS DisplayName=" + skullMeta.getDisplayName());
									if (skullMeta.hasLore()) {
										mmh.LOGGER.debug("CMD PH ADD IS lore=" + String.join(",", skullMeta.getLore()));

									}
								}
								mmh.LOGGER.debug("CMD PH ADD quantity=" + itemstack.getAmount());
								// playerHeads.set("players.player_" + (tradeNumber + 1) + ".chance", 0.002);
								mmh.playerHeads.set("players.number", (tradeNumber + 1));
								// log("customFile=" + customFile);
								try {
									mmh.playerHeads.save(mmh.playerFile);
									mmh.playerHeads.load(mmh.playerFile);
								} catch (Exception exception) {
									mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLUGIN_UNKNOWN_ERROR).error(exception));
									mmh.LOGGER.debug("CMD PH ADD End Exception -----");
								}
								mmh.LOGGER.debug("CMD PH ADD End -----");
								sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " player_"
										+ (tradeNumber + 1) + " " + mmh.get("mmh.message.ct.successadd"));
								return true;
							} else if (args[1].equalsIgnoreCase("remove")) {
								mmh.LOGGER.debug("CMD PH Remove Start -----");
								if (!(args.length >= 3)) {
									sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
											+ mmh.get("mmh.command.ct.argument"));
									return false;
								} else if (mmh.isInteger(args[2])) {
									mmh.playerHeads.set("players.player_" + args[2] + ".price_1", "");
									mmh.playerHeads.set("players.player_" + args[2] + ".price_2", "");
									mmh.playerHeads.set("players.player_" + args[2] + ".itemstack", "");
									mmh.playerHeads.set("players.player_" + args[2] + ".quantity", "");
									// playerHeads.set("custom_trades.trade_" + args[2] + ".chance", "");
									mmh.LOGGER.debug("playerFile=" + mmh.playerFile);
									try {
										mmh.playerHeads.save(mmh.playerFile);
										mmh.playerHeads.load(mmh.playerFile);
									} catch (Exception exception) {
										mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLUGIN_UNKNOWN_ERROR).error(exception));

										mmh.LOGGER.debug("CMD PH Remove End Exception -----");

										sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
												+ mmh.get("mmh.command.ct.error") + "custom_trades.yml!");
										return false;
										// e.printStackTrace();
									}
									mmh.LOGGER.debug("CMD PH Remove End -----");
									sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " player_"
											+ args[2] + " " + mmh.get("mmh.message.ct.successrem"));
									return true;
								} else {
									mmh.LOGGER.debug("CMD PH Remove End 2 -----");
									sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
											+ mmh.get("mmh.command.ct.numberreq"));
									return false;
								}
							} else if (args[1].equalsIgnoreCase("replace")) {
								mmh.LOGGER.debug("CMD PH Replace Start -----");
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
									if ((itemstack.getType() == Material.AIR) || (price1 == null)
											|| (price1.getType() == Material.AIR)
											|| (itemstack.getType() != Material.PLAYER_HEAD)) {
										sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW
												+ mmh.getName() + ChatColor.GREEN + "]===============[]");
										sender.sendMessage(ChatColor.WHITE + " ");
										if (itemstack.getType() != Material.PLAYER_HEAD) {
											sender.sendMessage(ChatColor.RED + " " + mmh.get("mmh.command.playerhead.msg"));
											sender.sendMessage(ChatColor.WHITE + " ");
										}
										sender.sendMessage(ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line1")
										+ "player_heads.yml");
										sender.sendMessage(ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line2"));
										sender.sendMessage(ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line3"));
										sender.sendMessage(
												ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line4") + "/mmh ph add");
										sender.sendMessage(
												ChatColor.WHITE + " " + mmh.get("mmh.command.ct.line5") + "player head.");
										sender.sendMessage(ChatColor.WHITE + " ");
										sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW
												+ mmh.getName() + ChatColor.GREEN + "]===============[]");
										mmh.LOGGER.debug("CMD PH Replace End Error -----");
										return false;
									}
									int tradeNumber = Integer.parseInt(args[2]);
									mmh.playerHeads.set("players.player_" + (tradeNumber) + ".price_1", price1);
									mmh.playerHeads.set("players.player_" + (tradeNumber) + ".price_2", price2);
									mmh.playerHeads.set("players.player_" + (tradeNumber) + ".itemstack", itemstack);
									mmh.playerHeads.set("players.player_" + (tradeNumber) + ".quantity",
											itemstack.getAmount());
									mmh.LOGGER.debug("CMD PH Replace price1=" + price1.getType());
									mmh.LOGGER.debug("CMD PH Replace price2=" + price2.getType());
									mmh.LOGGER.debug("CMD PH Replace itemstack=" + itemstack.getType());
									if (itemstack.getType() == Material.PLAYER_HEAD) {
										ItemMeta skullMeta = itemstack.getItemMeta();
										mmh.LOGGER.debug("CMD PH Replace IS DisplayName=" + skullMeta.getDisplayName());
										if (skullMeta.hasLore()) {
											mmh.LOGGER.debug("CMD PH Replace IS lore=" + String.join(",", skullMeta.getLore()));
										}
										mmh.LOGGER.debug("CMD PH Replace quantity=" + itemstack.getAmount());
										// playerHeads.set("players.player_" + (tradeNumber + 1) + ".chance", 0.002);
										// log("customFile=" + customFile);
										try {
											mmh.playerHeads.save(mmh.playerFile);
											mmh.playerHeads.load(mmh.playerFile);
										} catch (Exception exception) {
											mmh.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLUGIN_UNKNOWN_ERROR).error(exception));
											mmh.LOGGER.debug("CMD PH Replace End Exception -----");
											sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
													+ mmh.get("mmh.command.ct.error") + "player_heads.yml!");
											return false;
											// e.printStackTrace();
										}
										mmh.LOGGER.debug("CMD PH Replace End -----");
										sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " player_"
												+ args[2] + " " + mmh.get("mmh.message.ct.successrep"));
										return true;
									} else {
										mmh.LOGGER.debug("CMD PH Replace End 2 -----");
										sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
												+ mmh.get("mmh.command.ct.numberreq"));
										return false;
									}
								}
							}
						} else if (!(sender instanceof Player)) {
							mmh.LOGGER.debug("CMD PH Replace End Console -----");
							sender.sendMessage(
									ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.get("mmh.message.noconsole"));
							return false;
						} else if (!hasPerm) {
							mmh.LOGGER.debug("CMD PH Replace End !Perm -----");
							sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
									+ mmh.get("mmh.message.nopermordisabled").toString().replace("<perm>", perm));
							return false;
						}

					}catch (Exception exception) {
						reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_PLAYER_HEADS).error(exception));
					}
				}
				if (args[0].equalsIgnoreCase("fixhead") || args[0].equalsIgnoreCase("fh")) {
					try { // REPORT_COMMAND_FIX_HEAD "Error executing Fixhead Command."
						String perm = "moremobheads.fixhead";
						boolean hasPerm = sender.hasPermission(perm);
						mmh.LOGGER.debug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
						if (sender instanceof Player) {
							Player player = (Player) sender;
							if (hasPerm || mmh.isDev) {
								if (!args[1].isEmpty()) {
									if (args[1].equalsIgnoreCase("name")) {
										mmh.LOGGER.debug("CMD FH Name Start -----");
										// FixHead NBT
										ItemStack mainHand = player.getInventory().getItemInMainHand();
										if (mainHand != null) {
											if (mainHand.getType().equals(Material.PLAYER_HEAD)) {
												String texture = mainHand.getItemMeta().getDisplayName();

												SkullMeta skullname = (SkullMeta) mainHand.getItemMeta();
												if (skullname.getOwner() != null) {
													String name = skullname.getOwner().toString();
													mmh.LOGGER.debug("EPIE name=" + name);
													mmh.LOGGER.debug("EPIE lore=" + skullname.getLore());
													if (skullname.getOwner().toString().length() >= 40) {
														mmh.LOGGER.debug("EPIE ownerName.lngth >= 40");
														ItemStack itmStack = mainHand;
														// SkullMeta skullname = (SkullMeta)
														// e.getItem().getItemStack().getItemMeta();
														String daMobName = "null";
														if (skullname != null) {
															String isCat = CatHeads
																	.getNameFromTexture(skullname.getOwner().toString());
															String isHorse = HorseHeads
																	.getNameFromTexture(skullname.getOwner().toString());
															String isLlama = LlamaHeads
																	.getNameFromTexture(skullname.getOwner().toString());
															String isMobHead = MobHeads
																	.getNameFromTexture(skullname.getOwner().toString());
															String isRabbit = RabbitHeads
																	.getNameFromTexture(skullname.getOwner().toString());
															String isSheep = SheepHeads
																	.getNameFromTexture(skullname.getOwner().toString());
															String isVillager = VillagerHeads
																	.getNameFromTexture(skullname.getOwner().toString());
															String isZombieVillager = ZombieVillagerHeads
																	.getNameFromTexture(skullname.getOwner().toString());
															String isplayerhead = mmh
																	.isPlayerHead(skullname.getOwner().toString());
															String isblockhead = mmh
																	.isBlockHead(skullname.getOwner().toString());
															String isblockhead2 = mmh
																	.isBlockHead2(skullname.getOwner().toString());
															String isblockhead3 = mmh
																	.isBlockHead3(skullname.getOwner().toString());
															if (isCat != null) {
																daMobName = isCat;
															}
															if (isHorse != null) {
																daMobName = isHorse;
															}
															if (isLlama != null) {
																daMobName = isLlama;
															}
															if (isMobHead != null) {
																daMobName = isMobHead;
															}
															if (isRabbit != null) {
																daMobName = isRabbit;
															}
															if (isSheep != null) {
																daMobName = isSheep;
															}
															if (isVillager != null) {
																daMobName = isVillager;
															}
															if (isZombieVillager != null) {
																daMobName = isZombieVillager;
															}
															if (daMobName == null) {
																if (blockHeads != null) {
																	if (isblockhead != null) {
																		daMobName = isblockhead;
																	}
																}
																if (blockHeads2 != null) {
																	if (isblockhead2 != null) {
																		daMobName = isblockhead2;
																	}
																}
																if (blockHeads3 != null) {
																	if (isblockhead3 != null) {
																		daMobName = isblockhead3;
																	}
																}
																if (mmh.playerHeads != null) {
																	if (isplayerhead != null) {
																		daMobName = isplayerhead;
																	}
																}
															}
															ArrayList<String> lore = new ArrayList();
															// log("" + meta.getOwner().toString());
															// String name =
															// LlamaHeads.getNameFromTexture(meta.getOwner().toString());
															mmh.LOGGER.debug("EPIE mobname from texture=" + daMobName);
															List<String> skullLore = skullname.getLore();
															if (skullLore != null) {
																if (skullLore.toString().contains( ChatColorUtils.setColors( mmh.langName.getString("killedby", "<RED>Killed <RESET>By <YELLOW><player>") ) ) ) {
																	lore.addAll(skullname.getLore());
																}
															}
															if ((skullLore == null) || !skullname.getLore().toString()
																	.contains(mmh.getName())) {
																if (mmh.config.getBoolean("head_settings.lore.show_plugin_name", true)) {
																	lore.add(ChatColor.AQUA + "" + mmh.getName());
																}
															}
															if (daMobName != "null") {
																daMobName = mmh.langName.getString(
																		daMobName.toLowerCase().replace(" ", "."), daMobName);
															} else {
																daMobName = mmh.langName.getString(
																		daMobName.toLowerCase().replace(" ", "."),
																		"404 Name Not Found");
															}
															skullname.setLore(lore);
															skullname.setDisplayName(daMobName);
															itmStack.setItemMeta(skullname);
															// fixHeadNBT(skullname.getOwner(), daMobName, lore);
															mmh.LOGGER.debug("CMD FH Name End -----");
															sender.sendMessage("" + mmh.get("mmh.command.fixhead.name"));
															// if(debug){mmh.TEST.debug("test3a");}
															return true;
														} else {
															mmh.LOGGER.debug("CMD FH Name End Meta Null -----");
															return false;
														}
													}
												}
											} else {
												mmh.LOGGER.debug("CMD FH Name End Error -----");
												sender.sendMessage("An Error occured.");
												return false;
											}
										}
									}

									if (args[1].equalsIgnoreCase("stack")) {
										mmh.LOGGER.debug("CMD FH Stack Start -----");
										// FixHead Stack
										ItemStack mainHand = player.getInventory().getItemInMainHand();
										ItemStack offHand = player.getInventory().getItemInOffHand();
										if ((mainHand != null) && (offHand != null)) {
											if (mainHand.getType().equals(Material.PLAYER_HEAD)
													&& offHand.getType().equals(Material.PLAYER_HEAD)) {
												ItemStack is = mmh.fixHeadStack(offHand, mainHand);
												// is.setAmount(mainHand.getAmount());
												if (is != mainHand) {
													player.getInventory().setItemInMainHand(is);
													mmh.LOGGER.debug("is=" + is.getType());
													mmh.LOGGER.debug("CMD FH Stack End -----");
													sender.sendMessage("" + mmh.get("mmh.command.fixhead.stack.success"));
													return true;
												} else {
													mmh.LOGGER.debug("CMD FH Stack End Error -----");
													sender.sendMessage("" + mmh.get("mmh.command.fixhead.stack.error"));
													return false;
												}
											} else if (!mainHand.getType().equals(Material.PLAYER_HEAD)
													&& !offHand.getType().equals(Material.PLAYER_HEAD)) {
												mmh.LOGGER.debug("CMD FH Stack End Error Main Off -----");
												sender.sendMessage("" + mmh.get("mmh.command.fixhead.stack.notph"));
												return false;
											} else if (!mainHand.getType().equals(Material.PLAYER_HEAD)
													&& offHand.getType().equals(Material.PLAYER_HEAD)) {
												mmh.LOGGER.debug("CMD FH Stack End Error Main -----");
												sender.sendMessage("" + mmh.get("mmh.command.fixhead.stack.main"));
												return false;
											} else if (mainHand.getType().equals(Material.PLAYER_HEAD)
													&& !offHand.getType().equals(Material.PLAYER_HEAD)) {
												mmh.LOGGER.debug("CMD FH Stack End Error Off -----");
												sender.sendMessage("" + mmh.get("mmh.command.fixhead.stack.off"));
												return false;
											}
										}
									}
								}

							} else if (!hasPerm) {
								mmh.LOGGER.debug("CMD FH Stack End !Perm -----");
								sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
										+ mmh.get("mmh.message.noperm").toString().replace("<perm>", perm));
								return false;
							}
						}
					}catch (Exception exception) {
						reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_FIX_HEAD).error(exception));
					}
				}
				if (args[0].equalsIgnoreCase("giveMH")) {
					try { // REPORT_COMMAND_GIVE_MH "Error executing GiveMH Command."
						// /mmh giveMH player mob qty
						// cmd 0 1 2 3
						if (args.length == 4) {
							String perm = "moremobheads.give";
							boolean hasPerm = sender.hasPermission(perm) || !(sender instanceof Player) ? true : false;
							mmh.LOGGER.debug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
							if (hasPerm || mmh.isDev) {
								Player player = Bukkit.getPlayer(args[1]);
								if (!args[2].isEmpty()) {
									String mob = args[2].toLowerCase();
									mmh.LOGGER.log("mob=" + mob);
									if (!args[3].isEmpty()) {
										int number = Integer.parseInt(args[3]);
										String[] splitmob = mob.split("\\.");
										mmh.LOGGER.debug("CMD GMH splitmob[0]=" + splitmob[0]);
										switch (splitmob[0]) {
										case "creeper_charged":
											mmh.playerGiveOrDropHead( player,
													mmh.makeHeads(mmh.langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName()),
															MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(), MobHeads.valueOf(splitmob[0].toUpperCase()).getOwner(), EntityType.CREEPER, number)
													);
											break;
										case "creeper":
											if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.creeper", false)) {
												mmh.playerGiveOrDropHead(player, new ItemStack(Material.CREEPER_HEAD));
											} else {
												mmh.playerGiveOrDropHead(player,
														mmh.makeHeads(mmh.langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName()),
																MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(), MobHeads.valueOf(splitmob[0].toUpperCase()).getOwner(), EntityType.CREEPER, number)
														);
											}
											break;
										case "mushroom_cow":
											mmh.playerGiveOrDropHead(player,
													mmh.makeHeads(
															mmh.langName.getString((splitmob[0] + "." + splitmob[1]).toLowerCase(), MobHeads.valueOf((splitmob[0] + "." + splitmob[1]).toUpperCase()).getName()),
															MobHeads.valueOf((splitmob[0] + "." + splitmob[1]).toUpperCase()).getTexture(), MobHeads.valueOf((splitmob[0] + "." + splitmob[1]).toUpperCase()).getOwner(), EntityType.COW, number)
													);
											break;
										case "strider_shivering":
											mmh.playerGiveOrDropHead(player,
													mmh.makeHeads(mmh.langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName()),
															MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(), MobHeads.valueOf(splitmob[0].toUpperCase()).getOwner(), EntityType.STRIDER, number)
													);
											break;
										case "zombie":
											if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.zombie", false)) {
												mmh.playerGiveOrDropHead(player, new ItemStack(Material.ZOMBIE_HEAD));
											} else {
												mmh.playerGiveOrDropHead(player,
														mmh.makeHeads(mmh.langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName()),
																MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(), MobHeads.valueOf(splitmob[0].toUpperCase()).getOwner(), EntityType.ZOMBIE, number)
														);
											}
											break;

										case "piglin":
											if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.piglin", false)) {
												mmh.playerGiveOrDropHead(player, new ItemStack(Material.PIGLIN_HEAD));
											} else {
												mmh.playerGiveOrDropHead(player,
														mmh.makeHeads(mmh.langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName()),
																MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(), MobHeads.valueOf(splitmob[0].toUpperCase()).getOwner(), EntityType.PIGLIN, number)
														);
											}
											break;
										case "skeleton":
											if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.skeleton", false)) {
												mmh.playerGiveOrDropHead(player, new ItemStack(Material.SKELETON_SKULL));
											} else {
												mmh.playerGiveOrDropHead(player,
														mmh.makeHeads(mmh.langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName()),
																MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(), MobHeads.valueOf(splitmob[0].toUpperCase()).getOwner(), EntityType.SKELETON, number)
														);
											}
											break;
										case "wither_skeleton":
											if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.wither_skeleton", false)) {
												mmh.playerGiveOrDropHead(player, new ItemStack(Material.WITHER_SKELETON_SKULL));
											} else {
												mmh.playerGiveOrDropHead(player,
														mmh.makeHeads(mmh.langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName()),
																MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(), MobHeads.valueOf(splitmob[0].toUpperCase()).getOwner(), EntityType.WITHER_SKELETON, number)
														);
											}
											break;
										case "ender_dragon":
											if (mmh.config.getBoolean("head_settings.mob_heads.vanilla_heads.ender_dragon", false)) {
												mmh.playerGiveOrDropHead(player, new ItemStack(Material.DRAGON_HEAD));
											} else {
												mmh.playerGiveOrDropHead(player,
														mmh.makeHeads(mmh.langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName()),
																MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(), MobHeads.valueOf(splitmob[0].toUpperCase()).getOwner(), EntityType.ENDER_DRAGON, number)
														);
											}
											break;
										case "cat":
											mmh.playerGiveOrDropHead(player,
													mmh.makeHeads(
															mmh.langName.getString((splitmob[0] + "." + splitmob[1]).toLowerCase(), CatHeads.valueOf(splitmob[1].toUpperCase()).getName()),
															CatHeads.valueOf(splitmob[1].toUpperCase()).getTexture(), CatHeads.valueOf(splitmob[1].toUpperCase()).getOwner(), EntityType.CAT, number)
													);
											break;
										case "bee":
											mmh.LOGGER.log("splitmob.length=" + splitmob.length);
											if (splitmob.length == 1) {
												mmh.playerGiveOrDropHead(player,
														mmh.makeHeads(mmh.langName.getString(splitmob[0].toLowerCase() + ".none", MobHeads.valueOf(splitmob[0].toUpperCase()).getName()),
																MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture(), MobHeads.valueOf(splitmob[0].toUpperCase()).getOwner(), EntityType.BEE, number)
														);
											} else {
												mmh.playerGiveOrDropHead(player,
														mmh.makeHeads(mmh.langName.getString(mob.replace(".", "_").toLowerCase(), MobHeads.valueOf(mob.replace(".", "_").toUpperCase()).getName()),
																MobHeads.valueOf(mob.replace(".", "_").toUpperCase()).getTexture(), MobHeads.valueOf(mob.replace(".", "_").toUpperCase()).getOwner(), EntityType.BEE, number)
														);
											}
											break;
										case "villager": // villager type profession,
											// villager profession type
											// name = splitmob[0], type = splitmob[1],
											// profession = splitmob[2]
											mmh.playerGiveOrDropHead(player,
													mmh.makeHeads(mmh.langName.getString((splitmob[0] + "." + splitmob[1] + "." + splitmob[2]).toLowerCase(),
															VillagerHeads.valueOf((splitmob[0] + "_" + splitmob[2] + "_" + splitmob[1]).toUpperCase()).getName()),
															VillagerHeads.valueOf((splitmob[0] + "_" + splitmob[2] + "_" + splitmob[1]).toUpperCase()).getTexture(),
															VillagerHeads.valueOf((splitmob[0] + "_" + splitmob[2] + "_" + splitmob[1]).toUpperCase()).getOwner(), EntityType.VILLAGER, number)
													);
											break;
										case "zombie_villager":
											mmh.playerGiveOrDropHead(player,
													mmh.makeHeads(mmh.langName.getString((splitmob[0] + "." + splitmob[1]).toLowerCase(),
															ZombieVillagerHeads.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getName()),
															ZombieVillagerHeads.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getTexture(),
															ZombieVillagerHeads.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getOwner(), EntityType.ZOMBIE_VILLAGER, number)
													);
											break;
										case "llama":
										case "trader_llama":
											mmh.playerGiveOrDropHead(player,
													mmh.makeHeads(mmh.langName.getString((splitmob[0] + "." + splitmob[1]).toLowerCase(),
															LlamaHeads.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getName()),
															LlamaHeads.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getTexture(),
															LlamaHeads.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getOwner(), EntityType.LLAMA, number)
													);
											break;
										case "horse":
											mmh.playerGiveOrDropHead(player,
													mmh.makeHeads(mmh.langName.getString((splitmob[0] + "." + splitmob[1]).toLowerCase(),
															HorseHeads.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getName()),
															HorseHeads.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getTexture(),
															HorseHeads.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getOwner(), EntityType.HORSE, number)
													);
											break;
										case "rabbit":
											if( splitmob[1].equalsIgnoreCase("Toast") ) {
												mmh.playerGiveOrDropHead(player,
														mmh.makeHeads(mmh.langName.getString((splitmob[0] + "." + splitmob[1]).toLowerCase(),
																RabbitHeads.valueOf((splitmob[0] + "_").toUpperCase().concat( StrUtils.toTitleCase(splitmob[1]) )).getName()),
																RabbitHeads.valueOf((splitmob[0] + "_").toUpperCase().concat( StrUtils.toTitleCase(splitmob[1]) )).getTexture(),
																RabbitHeads.valueOf((splitmob[0] + "_").toUpperCase().concat( StrUtils.toTitleCase(splitmob[1]) )).getOwner(), EntityType.RABBIT, number)
														);
												break;
											}
											mmh.playerGiveOrDropHead(player,
													mmh.makeHeads(mmh.langName.getString((splitmob[0] + "." + splitmob[1]).toLowerCase(),
															RabbitHeads.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getName()),
															RabbitHeads.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getTexture(),
															RabbitHeads.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getOwner(), EntityType.RABBIT, number)
													);
											break;
										case "sheep":
											String sheeptype;
											if (splitmob[1].equalsIgnoreCase("jeb_")) {
												sheeptype = "jeb_";
											} else {
												sheeptype = splitmob[1].toUpperCase();
											}
											mmh.playerGiveOrDropHead(player,
													mmh.makeHeads(mmh.langName.getString((splitmob[0] + "." + sheeptype).toLowerCase(),
															SheepHeads.valueOf((splitmob[0] + "_").toUpperCase().concat(sheeptype)).getName()),
															SheepHeads.valueOf((splitmob[0] + "_").toUpperCase().concat(sheeptype)).getTexture(),
															SheepHeads.valueOf((splitmob[0] + "_").toUpperCase().concat(sheeptype)).getOwner(), EntityType.SHEEP, number)
													);
											break;
										case "goat":
											mmh.playerGiveOrDropHead(player,
													mmh.makeHeads(mmh.langName.getString((splitmob[0] + "." + splitmob[1]).toLowerCase(),
															MobHeads117.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getName()),
															MobHeads117.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getTexture(),
															MobHeads117.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getOwner(), EntityType.GOAT, number)
													);
											break;
										case "axolotl":
											mmh.playerGiveOrDropHead(player,
													mmh.makeHeads(mmh.langName.getString((splitmob[0] + "." + splitmob[1]).toLowerCase(),
															MobHeads117.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getName()),
															MobHeads117.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getTexture(),
															MobHeads117.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getOwner(), EntityType.AXOLOTL, number)
													);
											break;
										case "frog":
											mmh.playerGiveOrDropHead(player,
													mmh.makeHeads(mmh.langName.getString((splitmob[0] + "." + splitmob[1]).toLowerCase(),
															MobHeads119.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getName()),
															MobHeads119.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getTexture(),
															MobHeads119.valueOf((splitmob[0] + "_" + splitmob[1]).toUpperCase()).getOwner(), EntityType.FROG, number)
													);
											break;
										case "camel":
											mmh.playerGiveOrDropHead(player,
													mmh.makeHeads(mmh.langName.getString((splitmob[0]).toLowerCase(),
															MobHeads120.valueOf((splitmob[0]).toUpperCase()).getName()),
															MobHeads120.valueOf((splitmob[0]).toUpperCase()).getTexture(),
															MobHeads120.valueOf((splitmob[0]).toUpperCase()).getOwner(), EntityType.CAMEL, number)
													);
											break;
										case "sniffer":
											mmh.playerGiveOrDropHead(player,
													mmh.makeHeads(mmh.langName.getString((splitmob[0]).toLowerCase(),
															MobHeads120.valueOf((splitmob[0]).toUpperCase()).getName()),
															MobHeads120.valueOf((splitmob[0]).toUpperCase()).getTexture(),
															MobHeads120.valueOf((splitmob[0]).toUpperCase()).getOwner(), EntityType.SNIFFER, number)
													);
											break;
										case "allay":
										case "tadpole":
										case "warden":
											mmh.playerGiveOrDropHead(player,
													mmh.makeHeads(mmh.langName.getString(mob.toLowerCase(),
															MobHeads119.valueOf(mob.replace(".", "_").toUpperCase()).getName()),
															MobHeads119.valueOf(mob.replace(".", "_").toUpperCase()).getTexture(),
															MobHeads119.valueOf(mob.replace(".", "_").toUpperCase()).getOwner(), EntityType.fromName(splitmob[0].toString()), number)
													);
											break;
										case "tropical_fish":// TropicalFishHeads
											String fishType = splitmob[1].toUpperCase();
											mmh.LOGGER.log("splitmob[0]=" + splitmob[0]);
											mmh.LOGGER.log("splitmob[1]=" + splitmob[1]);
											mmh.LOGGER.log("fishType=" + fishType);
											mmh.playerGiveOrDropHead(player,
													mmh.makeHeads(mmh.langName.getString((splitmob[0] + "." + splitmob[1]).toLowerCase(),
															TropicalFishHeads.valueOf((splitmob[0] + "." + splitmob[1]).toUpperCase()).getName()),
															TropicalFishHeads.valueOf((splitmob[0] + "." + splitmob[1]).toUpperCase()).getTexture(),
															TropicalFishHeads.valueOf((splitmob[0] + "." + splitmob[1]).toUpperCase()).getOwner(), EntityType.TROPICAL_FISH, number)
													);
											break;
										default:
											mmh.playerGiveOrDropHead(player,
													mmh.makeHeads(mmh.langName.getString(mob.toLowerCase(),
															MobHeads.valueOf(mob.replace(".", "_").toUpperCase()).getName()),
															MobHeads.valueOf(mob.replace(".", "_").toUpperCase()).getTexture(),
															MobHeads.valueOf(mob.replace(".", "_").toUpperCase()).getOwner(), EntityType.fromName(splitmob[0].toString()), number)
													);
											break;
										}
									}
								}
							} else if (!hasPerm) {
								sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
										+ mmh.get("mmh.message.noperm").toString().replace("<perm>", perm));
								return false;
							}
						} else {
							sender.sendMessage("" + mmh.get("mmh.command.usage") + ", /mmh givemh playername mobname 1");
							return false;
						}
					}catch (Exception exception) {
						reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_GIVE_MH).error(exception));
					}
				}
				// /mmh giveph player
				// /mmh giveph player player
				// 0 1 2 3
				if (args[0].equalsIgnoreCase("givePH")) {
					try { // REPORT_COMMAND_GIVE_PH "Error executing GivePH Command."
						if (args.length >= 2) {
							String perm = "moremobheads.give";
							boolean hasPerm = sender.hasPermission(perm) || !(sender instanceof Player) ? true : false;
							mmh.LOGGER.debug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
							if (hasPerm || mmh.isDev) {
								mmh.LOGGER.debug("CMD GPH args.length=" + args.length);
								if ((args.length == 2) && (sender instanceof Player)) {
									mmh.givePlayerHead((Player) sender, args[1]);
									mmh.LOGGER.debug("CMD GPH args1=" + args[1]);
									return true;
								} else if (args.length == 3) {
									Player player = Bukkit.getPlayer(args[1]);
									mmh.givePlayerHead(player, args[2]);
									mmh.LOGGER.debug("CMD GPH args1=" + args[1] + ", args2=" + args[2]);
									return true;
								} else if ((args.length == 2) && !(sender instanceof Player)) {
									sender.sendMessage(
											"" + mmh.get("mmh.command.give.console") + "" + mmh.get("mmh.command.usage") + ":");
									sender.sendMessage(
											"\"/mmh giveph playername 1\" - " + mmh.get("mmh.command.give.playerhead.you"));
									sender.sendMessage("\"/mmh giveph playername playername 1\" - "
											+ mmh.get("mmh.command.give.playerhead.them"));
									return false;
								}
							} else if (!hasPerm) {
								sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
										+ mmh.get("mmh.message.noperm").toString().replace("<perm>", perm));
								return false;
							}
						} else {
							sender.sendMessage("" + mmh.get("mmh.command.usage") + ":");
							sender.sendMessage("\"/mmh giveph playername 1\" - " + mmh.get("mmh.command.give.playerhead.you"));
							sender.sendMessage(
									"\"/mmh giveph playername playername 1\" - " + mmh.get("mmh.command.give.playerhead.them"));
							return false;
						}
						return false;
					}catch (Exception exception) {
						reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_GIVE_PH).error(exception));
					}
				}
				// /mmh givebh block
				// /mmh givebh player block
				// 0 1 2 3
				if (args[0].equalsIgnoreCase("giveBH")) {
					try { // REPORT_COMMAND_GIVE_BH "Error executing GiveBH Command."
						mmh.LOGGER.debug("Start GiveBH");
						mmh.LOGGER.debug("Command=" + cmd.getName() + ", arguments=" + Arrays.toString(args));
						if (args.length >= 2) {
							String perm = "moremobheads.give";
							boolean hasPerm = sender.hasPermission(perm) || !(sender instanceof Player) ? true : false;
							mmh.LOGGER.debug(sender.getName() + " has the permission " + perm + "=" + hasPerm);
							if (hasPerm || mmh.isDev) {
								mmh.LOGGER.debug("CMD GBH args.length=" + args.length);
								if ((args.length == 2) && (sender instanceof Player)) {
									mmh.giveBlockHead((Player) sender, args[1].replace("_", " "));
									mmh.LOGGER.debug("CMD GBH args1=" + args[1]);
									mmh.LOGGER.debug("End GiveBH True 1");
									return true;
								} else if (args.length == 3) {
									Player player = Bukkit.getPlayer(args[1]);
									mmh.giveBlockHead(player, args[2].replace("_", " "));
									mmh.LOGGER.debug("CMD GBH args1=" + args[1] + ", args2=" + args[2]);
									mmh.LOGGER.debug("End GiveBH True 2");
									return true;
								} else if ((args.length == 2) && !(sender instanceof Player)) {
									sender.sendMessage(
											"" + mmh.get("mmh.command.give.console") + " mmh giveBh <player> <block>");
									mmh.LOGGER.debug("End GiveBH False 1");
									return false;
								}
							} else if (!hasPerm) {
								sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " "
										+ mmh.get("mmh.message.noperm").toString().replace("<perm>", perm));
								mmh.LOGGER.debug("End GiveBH False 2");
								return false;
							}
						} else {
							sender.sendMessage("" + mmh.get("mmh.command.usage") + ":");
							sender.sendMessage("\"/mmh givebh <block>\" - " + mmh.get("mmh.command.give.blockhead.you"));
							sender.sendMessage(
									"\"/mmh giveph playername <block>\" - " + mmh.get("mmh.command.give.blockhead.them"));
							mmh.LOGGER.debug("End GiveBH False 3");
							return false;
						}
						mmh.LOGGER.debug("End GiveBH False 4");
						return false;
					}catch (Exception exception) {
						reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_GIVE_BH).error(exception));
					}
				}
				if (args[0].equalsIgnoreCase("dev")) {
					try { // REPORT_COMMAND_DEV_ERROR "Error exuting dev Command."
						if (sender instanceof Player) {
							Player player = (Player) sender;
							if (player.getName().equalsIgnoreCase("JoelGodOfWar")
									|| player.getName().equalsIgnoreCase("JoelYahwehOfWar")) {
								mmh.isDev = !mmh.isDev;
								player.sendMessage("You have toggled isDev to " + mmh.isDev);
								return true;
							} else {
								player.sendMessage("You are not the developer.");
								return false;
							}
						}
					}catch (Exception exception) {
						reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_COMMAND_DEV_ERROR).error(exception));
					}
				}
			}
		}catch(Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.UNHANDLED_COMMAND_ERROR).error(exception));
			// ERROR_RUNNING_DRAGON_DEATH_COMMAND "Error running command after dragon death."
		}
		return false;
	}

	@Override
	@SuppressWarnings("static-access")
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { // TODO: Tab Complete
		try { // REPORT_TAB_COMPLETE_ERROR "Error parsing Tab Complete."
			if (command.getName().equalsIgnoreCase("mmh")) {
				List<String> autoCompletes = new ArrayList<>(); // create a new string list for tab completion
				if (args.length == 1) { // reload, toggledebug, playerheads, customtrader, headfix
					autoCompletes.add("reload");
					autoCompletes.add("toggledebug");
					autoCompletes.add("playerheads");
					autoCompletes.add("customtrader");
					autoCompletes.add("fixhead");
					autoCompletes.add("givemh");
					autoCompletes.add("giveph");
					autoCompletes.add("givebh");
					autoCompletes.add("display");
					return autoCompletes; // then return the list
				}
				if (args.length > 1) {
					if (args[0].equalsIgnoreCase("display") && args[1].isEmpty()) {
						autoCompletes.add("permissions");
						autoCompletes.add("variables");
						return autoCompletes; // then return the list
					} else if (args[0].equalsIgnoreCase("display") && args[1].equalsIgnoreCase("permissions")) {
						if (args[1].equalsIgnoreCase("permissions")) {
							return null;
						}
					}
					if (args[0].equalsIgnoreCase("fixhead") || (args[0].equalsIgnoreCase("fh") && args[1].isEmpty())) {
						autoCompletes.add("name");
						autoCompletes.add("stack");
						return autoCompletes; // then return the list
					}
					if (args[0].equalsIgnoreCase("playerheads") || (args[0].equalsIgnoreCase("ph") && args[1].isEmpty())) {
						autoCompletes.add("add");
						autoCompletes.add("remove");
						autoCompletes.add("replace");
						return autoCompletes; // then return the list
					} else if ((args[0].equalsIgnoreCase("playerheads") || args[0].equalsIgnoreCase("ph"))
							&& (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("replace"))) {
						if (args[1].equalsIgnoreCase("remove")) {
							autoCompletes.add("0");
							return autoCompletes; // then return the list
						}
						if (args[1].equalsIgnoreCase("replace")) {
							autoCompletes.add("0");
							return autoCompletes; // then return the list
						}
					}
					if (args[0].equalsIgnoreCase("customtrader") || (args[0].equalsIgnoreCase("ct") && args[1].isEmpty())) {
						autoCompletes.add("add");
						autoCompletes.add("remove");
						autoCompletes.add("replace");
						return autoCompletes; // then return the list
					} else if ((args[0].equalsIgnoreCase("customtrader") || args[0].equalsIgnoreCase("ct"))
							&& (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("replace"))) {
						if (args[1].equalsIgnoreCase("remove")) {
							autoCompletes.add("0");
							return autoCompletes; // then return the list
						}
						if (args[1].equalsIgnoreCase("replace")) {
							autoCompletes.add("0");
							return autoCompletes; // then return the list
						}
					}
					if (args[0].equalsIgnoreCase("givebh")) {
						if (args.length < 2) {
							// /mmh giveph block
							// /mmh giveph @p block
							// /cmd 0 1 2
							// return null to list all players.
							return null;
						}
						if (args.length > 2) {
							for (int i = 1; i < blockHeads.getInt("blocks.number"); ++i) {
								ItemStack stack = blockHeads.getItemStack("blocks.block_" + i + ".itemstack");
								String name = stack.getItemMeta().getDisplayName().replace(" ", "_");
								autoCompletes.add(ChatColor.stripColor(name));
							}
							if (Double.parseDouble(StrUtils.Left(mmh.getMCVersion(), 4)) >= 1.16) {
								for (int i = 1; i < blockHeads2.getInt("blocks.number"); ++i) {
									ItemStack stack = blockHeads2.getItemStack("blocks.block_" + i + ".itemstack");
									String name = stack.getItemMeta().getDisplayName().replace(" ", "_");
									autoCompletes.add(ChatColor.stripColor(name));
								}
							}
							if (Double.parseDouble(StrUtils.Left(mmh.getMCVersion(), 4)) >= 1.17) {
								for (int i = 1; i < blockHeads3.getInt("blocks.number"); ++i) {
									ItemStack stack = blockHeads3.getItemStack("blocks.block_" + i + ".itemstack");
									String name = stack.getItemMeta().getDisplayName().replace(" ", "_");
									autoCompletes.add(ChatColor.stripColor(name));
								}
							}

							return autoCompletes;
						}
					}
					if (args[0].equalsIgnoreCase("giveph")) {
						// return null;
						if (args.length < 2) {
							// /mmh giveph @p @P
							// /cmd 0 1 2
							// return null to list all players.
							return null;
						}
						if (args.length == 2) {
							// /mmh giveph @p @P
							// /cmd 0 1 2
							// return null to list all players.
							return null;
						}

					}
					if (args[0].equalsIgnoreCase("givemh")) {
						if (args.length < 2) {
							// /mmh give @p
							// /cmd 0 1
							// return null to list all players.
							return null;
						} else if (args.length > 2) {
							mmh.LOGGER.debug("TC arg1!null args.length=" + args.length);
						}
						if (args.length == 3) {

							// /mmh give @p moblist #
							// /cmd 0 1 2 3
							for (String key : chanceConfig.getConfigurationSection("chance_percent").getKeys(true)) {
								// System.out.println(key);
								autoCompletes.add(key);
								// System.out.println(key);
								if (key.equalsIgnoreCase("wolf")) {
									autoCompletes.add("wolf.angry");
								} else if (key.equalsIgnoreCase("wither")) {
									autoCompletes.add("wither.normal");
									autoCompletes.add("wither.projectile");
									autoCompletes.add("wither.blue_projectile");
									autoCompletes.remove(autoCompletes.indexOf("wither"));
								} else if (key.equalsIgnoreCase("zombie_villager")) {
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
								} else if (key.equalsIgnoreCase("vex")) {
									autoCompletes.add("vex.angry");
								}
							}
							autoCompletes.remove(autoCompletes.indexOf("axolotl"));
							autoCompletes.remove(autoCompletes.indexOf("bee"));
							autoCompletes.remove(autoCompletes.indexOf("cat"));
							autoCompletes.remove(autoCompletes.indexOf("fox"));
							autoCompletes.remove(autoCompletes.indexOf("goat"));
							autoCompletes.remove(autoCompletes.indexOf("horse"));
							autoCompletes.remove(autoCompletes.indexOf("llama"));
							autoCompletes.remove(autoCompletes.indexOf("panda"));
							autoCompletes.remove(autoCompletes.indexOf("parrot"));
							autoCompletes.remove(autoCompletes.indexOf("rabbit"));
							autoCompletes.remove(autoCompletes.indexOf("sheep"));
							autoCompletes.remove(autoCompletes.indexOf("trader_llama"));
							autoCompletes.remove(autoCompletes.indexOf("mushroom_cow"));
							autoCompletes.remove(autoCompletes.indexOf("tropical_fish"));
							autoCompletes.remove(autoCompletes.indexOf("villager"));
							autoCompletes.remove(autoCompletes.indexOf("villager.desert"));
							autoCompletes.remove(autoCompletes.indexOf("villager.jungle"));
							autoCompletes.remove(autoCompletes.indexOf("villager.plains"));
							autoCompletes.remove(autoCompletes.indexOf("villager.savanna"));
							autoCompletes.remove(autoCompletes.indexOf("villager.snow"));
							autoCompletes.remove(autoCompletes.indexOf("villager.swamp"));
							autoCompletes.remove(autoCompletes.indexOf("villager.taiga"));
							autoCompletes.remove(autoCompletes.indexOf("frog"));

							return autoCompletes;
						} else if (args.length == 4) {
							autoCompletes.add("1");
							return autoCompletes;
						}
					}
				}
			}
		}catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_TAB_COMPLETE_ERROR).error(exception));
		}
		return null;

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
			mmh.LOGGER.debug("RDW - DamageCause=" + damageCause.toString());
			switch (damageCause) {
			case ENTITY_ATTACK:
				// Check if the player is holding any item in the main hand
				ItemStack mainHandItem = playerInventory.getItemInMainHand();
				mmh.LOGGER.debug("RDW - mainHandItem=" + mainHandItem.getType().toString());
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





}