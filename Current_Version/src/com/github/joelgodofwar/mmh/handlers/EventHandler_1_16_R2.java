package com.github.joelgodofwar.mmh.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fox;
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
import org.bukkit.entity.Strider;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.StriderTemperatureChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.util.CatHeads;
import com.github.joelgodofwar.mmh.util.HorseHeads;
import com.github.joelgodofwar.mmh.util.LlamaHeads;
import com.github.joelgodofwar.mmh.util.MobHeads;
import com.github.joelgodofwar.mmh.util.RabbitHeads;
import com.github.joelgodofwar.mmh.util.SheepHeads;
import com.github.joelgodofwar.mmh.util.StrUtils;
import com.github.joelgodofwar.mmh.util.VillagerHeads;
import com.github.joelgodofwar.mmh.util.YmlConfiguration;
import com.github.joelgodofwar.mmh.util.ZombieVillagerHeads;

import de.tr7zw.changeme.nbtapi.NBTItem;
/**
1.8		1_8_R1		1.8.3	1_8_R2
1.8.8 	1_8_R3
1.9		1_9_R1		1.9.4	1_9_R2	
1.10	1_10_R1
1.11	1_11_R1
1.12	1_12_R1
1.13	1_13_R1		1.13.1	1_13_R2
1.14	1_14_R1
1.15	1_15_R1
1.16.1	1_16_R1		1.16.2	1_16_R2
1.17	1_17_R1
*/

public class EventHandler_1_16_R2 implements CommandExecutor, TabCompleter , Listener { 
	/** Variables */
	MoreMobHeads mmh;
	double defpercent = 0.013;
	String world_whitelist;
	String world_blacklist;
	String mob_whitelist;
	String mob_blacklist;
	boolean debug;
	YmlConfiguration chanceConfig;
	File blockFile;
	File blockFile116;
	File blockFile1162;
	public FileConfiguration blockHeads  = new YamlConfiguration();
	public FileConfiguration blockHeads2  = new YamlConfiguration();
	public FileConfiguration blockHeads3  = new YamlConfiguration();
	
	//List<MerchantRecipe> cached_recipes = new ArrayList<MerchantRecipe>();
	List<MerchantRecipe> playerhead_recipes = new ArrayList<MerchantRecipe>();
	List<MerchantRecipe> blockhead_recipes = new ArrayList<MerchantRecipe>();
	List<MerchantRecipe> custometrade_recipes = new ArrayList<MerchantRecipe>();
	int BHNum, BHNum2;
	
	@SuppressWarnings({ "static-access", "unchecked", "rawtypes" })
	public EventHandler_1_16_R2(final MoreMobHeads plugin){
		/** Set variables */
		mmh = plugin;
		mmh.log(Level.INFO, "Loading 1.15-16 EventHandler...");
		world_whitelist = plugin.world_whitelist;
		world_blacklist = plugin.mob_blacklist;
		mob_whitelist = plugin.mob_whitelist;
		mob_blacklist = plugin.mob_blacklist;
		debug = plugin.debug;
		chanceConfig = plugin.chanceConfig;
		blockFile116 = new File(mmh.getDataFolder() + "" + File.separatorChar + "block_heads_1_16.yml");
		blockFile1162 = new File(mmh.getDataFolder() + "" + File.separatorChar + "block_heads_1_16_2.yml");
		if(mmh.getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)){
			if(!mmh.getMCVersion().startsWith("1.16")&&!mmh.getMCVersion().startsWith("1.17")){
				blockFile = new File(mmh.getDataFolder() + "" + File.separatorChar + "block_heads.yml");//\
				if(debug){logDebug("block_heads=" + blockFile.getPath());}
				if(!blockFile.exists()){																	// checks if the yaml does not exist
					mmh.saveResource("block_heads.yml", true);
					log(Level.INFO, "block_heads.yml not found! Creating in " + mmh.getDataFolder() + "");
					//ConfigAPI.copy(getResource("lang.yml"), langFile); // copies the yaml from your jar to the folder /plugin/<pluginName>
				}
			}
			if(Double.parseDouble(StrUtils.Left(mmh.getMCVersion(), 4)) == 1.16){
				if(debug){logDebug("block_heads_1_16=" + blockFile116.getPath());}
				if(debug){logDebug("block_heads_1_16_2=" + blockFile1162.getPath());}
				if(!blockFile116.exists()){
					mmh.saveResource("block_heads_1_16.yml", true);
					log(Level.INFO, "block_heads_1_16.yml not found! Creating in " + mmh.getDataFolder() + "");
				}
				if(!blockFile1162.exists()){
					mmh.saveResource("block_heads_1_16_2.yml", true);
					log(Level.INFO, "block_heads_1_16_2.yml not found! Creating in " + mmh.getDataFolder() + "");
				}
				blockFile = new File(mmh.getDataFolder() + "" + File.separatorChar + "block_heads_1_16.yml");
				log(Level.INFO, "Loading block_heads_1_16 files...");
			
			}else{
				log(Level.INFO, "Loading block_heads file...");
			}
	
			blockHeads = new YamlConfiguration();
			try {
				blockHeads.load(blockFile);
				log(Level.INFO, "Loading " + blockFile + "...");
			} catch (IOException | InvalidConfigurationException e1) {
				mmh.stacktraceInfo();
				e1.printStackTrace();
			}
			if(blockHeads.get("blocks.block_79.price_2.amount", null) != null){
				log(Level.INFO, "block_heads files outdated, updating...");
				blockHeads.set("blocks.block_79.price_2.amount", "");
				try {
					blockHeads.save(blockFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			blockHeads2 = new YamlConfiguration();
			try {
				blockHeads2.load(blockFile1162);
				log(Level.INFO, "Loading " + blockFile1162 + "...");
			} catch (IOException | InvalidConfigurationException e1) {
				mmh.stacktraceInfo();
				e1.printStackTrace();
			}
			
			boolean showlore = mmh.getConfig().getBoolean("lore.show_plugin_name", true);
			ArrayList<String> headlore = new ArrayList();
			headlore.add(ChatColor.AQUA + "" + mmh.getName());
			
			mmh.log(Level.INFO, "Loading PlayerHead Recipes...");
			for(int i=1; i < mmh.playerHeads.getInt("players.number") + 1; i++){
				ItemStack price1 = mmh.playerHeads.getItemStack("players.player_" + i + ".price_1", new ItemStack(Material.AIR));
				ItemStack price2 = mmh.playerHeads.getItemStack("players.player_" + i + ".price_2", new ItemStack(Material.AIR));
				ItemStack itemstack = mmh.playerHeads.getItemStack("players.player_" + i + ".itemstack", new ItemStack(Material.AIR));
				if(showlore) {
					SkullMeta meta = (SkullMeta)itemstack.getItemMeta();
					meta.setLore(headlore);
					itemstack.setItemMeta(meta);
					itemstack.setItemMeta(meta);
				}
				MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.playerHeads.getInt("players.player_" + i + ".quantity", (int) 3));
				recipe.addIngredient(price1);
				recipe.addIngredient(price2);
				playerhead_recipes.add(recipe);
			}
			mmh.log(Level.INFO, playerhead_recipes.size() + " PlayerHead Recipes ADDED...");
			mmh.log(Level.INFO, "Loading BlockHead Recipes...");
			BHNum = blockHeads.getInt("blocks.number");
			// BlockHeads
			mmh.log(Level.INFO, "BlockHeads=" + BHNum);
			for(int i=1; i < BHNum + 1; i++){
				ItemStack price1 = blockHeads.getItemStack("blocks.block_" + i + ".price_1", new ItemStack(Material.AIR));
				ItemStack price2 = blockHeads.getItemStack("blocks.block_" + i + ".price_2", new ItemStack(Material.AIR));
				ItemStack itemstack = blockHeads.getItemStack("blocks.block_" + i + ".itemstack", new ItemStack(Material.AIR));
				if(showlore) {
					SkullMeta meta = (SkullMeta)itemstack.getItemMeta();
					meta.setLore(headlore);
					itemstack.setItemMeta(meta);
					itemstack.setItemMeta(meta);
				}
				MerchantRecipe recipe = new MerchantRecipe(itemstack, blockHeads.getInt("blocks.block_" + i + ".quantity", (int) 1));
				recipe.setExperienceReward(true);
				recipe.addIngredient(price1);
				recipe.addIngredient(price2);
				blockhead_recipes.add(recipe);
			}
			BHNum2 = blockHeads2.getInt("blocks.number");
			// blockHeads 2
			mmh.log(Level.INFO, "BlockHeads2=" + BHNum2);
			for(int i=1; i < BHNum2 + 1; i++){
				ItemStack price1 = blockHeads2.getItemStack("blocks.block_" + i + ".price_1", new ItemStack(Material.AIR));
				ItemStack price2 = blockHeads2.getItemStack("blocks.block_" + i + ".price_2", new ItemStack(Material.AIR));
				ItemStack itemstack = blockHeads2.getItemStack("blocks.block_" + i + ".itemstack", new ItemStack(Material.AIR));
				if(showlore) {
					SkullMeta meta = (SkullMeta)itemstack.getItemMeta();
					meta.setLore(headlore);
					itemstack.setItemMeta(meta);
					itemstack.setItemMeta(meta);
				}
				MerchantRecipe recipe = new MerchantRecipe(itemstack, blockHeads2.getInt("blocks.block_" + i + ".quantity", (int) 1));
				recipe.setExperienceReward(true);
				recipe.addIngredient(price1);
				recipe.addIngredient(price2);
				blockhead_recipes.add(recipe);
			}
	
			mmh.log(Level.INFO, blockhead_recipes.size() + " BlockHead Recipes ADDED...");
			mmh.log(Level.INFO, "Loading CustomTrades Recipes...");
			for(int i=1; i < mmh.traderCustom.getInt("custom_trades.number") + 1; i++){
				ItemStack price1 = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".price_1", new ItemStack(Material.AIR));
				ItemStack price2 = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".price_2", new ItemStack(Material.AIR));
				ItemStack itemstack = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".itemstack", new ItemStack(Material.AIR));
				MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.traderCustom.getInt("custom_trades.trade_" + i + ".quantity", (int) 1));
				recipe.setExperienceReward(true);
				recipe.addIngredient(price1);
				recipe.addIngredient(price2);
				custometrade_recipes.add(recipe);
			}
			mmh.log(Level.INFO, custometrade_recipes.size() + " CustomTrades Recipes ADDED...");
		}
	}
	
	/** Events go here */
	@SuppressWarnings({ "deprecation", "unchecked", "unused", "rawtypes" })
	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event){// TODO: EnityDeathEvent
		LivingEntity entity = event.getEntity();
		World world = event.getEntity().getWorld();
		List<ItemStack> Drops = event.getDrops();
		world_whitelist = mmh.getConfig().getString("world.whitelist", "");
		world_blacklist = mmh.getConfig().getString("world.blacklist", "");
		mob_whitelist = mmh.getConfig().getString("mob.whitelist", "");
		mob_blacklist = mmh.getConfig().getString("mob.blacklist", "");
		if(debug){mmh.logDebug( "EDE - world_whitelist=" + world_whitelist );}
		if(debug){mmh.logDebug( "EDE - world_blacklist=" + world_blacklist );}
		if(debug){mmh.logDebug( "EDE - mob_whitelist=" + mob_whitelist );}
		if(debug){mmh.logDebug( "EDE - mob_blacklist=" + mob_blacklist );}
			
		if(world_whitelist != null&&!world_whitelist.isEmpty()&&world_blacklist != null&&!world_blacklist.isEmpty()){
			if(!StrUtils.stringContains(world_whitelist, world.getName().toString())&&StrUtils.stringContains(world_blacklist, world.getName().toString())){
				if(debug){mmh.logDebug("EDE - World - On blacklist and Not on whitelist.");}
				return;
			}else if(!StrUtils.stringContains(world_whitelist, world.getName().toString())&&!StrUtils.stringContains(world_blacklist, world.getName().toString())){
				if(debug){mmh.logDebug("EDE - World - Not on whitelist.");}
				return;
			}else if(!StrUtils.stringContains(world_whitelist, world.getName().toString())){
				
			}
		}else if(world_whitelist != null&&!world_whitelist.isEmpty()){
			if(!StrUtils.stringContains(world_whitelist, world.getName().toString())){
				if(debug){mmh.logDebug("EDE - World - Not on whitelist.");}
				return;
			}
		}else if(world_blacklist != null&&!world_blacklist.isEmpty()){
			if(StrUtils.stringContains(world_blacklist, world.getName().toString())){
				if(debug){mmh.logDebug("EDE - World - On blacklist.");}
				return;
			}
		}
			if(entity instanceof Player){
				if(debug){mmh.logDebug("EDE Entity is Player line:877");}

				if(entity.getKiller() instanceof Player){
					if(entity.getKiller().hasPermission("moremobheads.players")){
						if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.player", 0.50))){
							//Player daKiller = entity.getKiller();
							if(debug){mmh.logDebug("EDE Killer is Player line:1073");}
							ItemStack helmet = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
							SkullMeta meta = (SkullMeta)helmet.getItemMeta();
							meta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(((Player) entity).getUniqueId()));
							meta.setDisplayName(((Player) entity).getDisplayName() + "'s Head");
							ArrayList<String> lore = new ArrayList();
							if(mmh.getConfig().getBoolean("lore.show_killer", true)){
								lore.add(ChatColor.RESET + "Killed by " + ChatColor.RESET + ChatColor.YELLOW + entity.getKiller().getDisplayName());
							}
							if(mmh.getConfig().getBoolean("lore.show_plugin_name", true)){
								lore.add(ChatColor.AQUA + "" + mmh.getName());
							}
							meta.setLore(lore);
								helmet.setItemMeta(meta);//																	 e2d4c388-42d5-4a96-b4c9-623df7f5e026
							helmet.setItemMeta(meta);
						
							//entity.getWorld().dropItemNaturally(entity.getLocation(), helmet);
							Drops.add(helmet);
							if(debug){mmh.logDebug("EDE " + ((Player) entity).getDisplayName().toString() + " Player Head Dropped");}
						}
						return;
					}
				}
			}else if(event.getEntity() instanceof LivingEntity){
				/** Move this higher 
				double chancepercent = 0.50; //** Set to check config.yml later/
				String s = Double.toString(chancepercent);
				log("chancepercent=" + s.length());
				/** Move this higher */
				if(entity.getKiller() instanceof Player){
					String name = event.getEntityType().toString().replace(" ", "_");
					if(debug){mmh.logDebug("EDE name=" + name);}
					//ItemStack itemstack = event.getEntity().getKiller().getInventory().getItemInMainHand();
					//if(itemstack != null){
						/**if(debug){mmh.logDebug("itemstack=" + itemstack.getType().toString() + " line:159");}
						int enchantmentlevel = itemstack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);//.containsEnchantment(Enchantment.LOOT_BONUS_MOBS);
						if(debug){mmh.logDebug("enchantmentlevel=" + enchantmentlevel + " line:161");}
						double enchantmentlevelpercent = ((double)enchantmentlevel / 100);
						if(debug){mmh.logDebug("enchantmentlevelpercent=" + enchantmentlevelpercent + " line:163");}
						double chance = Math.random();
						if(debug){mmh.logDebug("chance=" + chance + " line:165");}
						
						if(debug){mmh.logDebug("chancepercent=" + chancepercent + " line:167");}
						chancepercent = chancepercent + enchantmentlevelpercent;
						if(debug){mmh.logDebug("chancepercent2=" + chancepercent + " line:169");}*/
						//if(chancepercent > 0.00 && chancepercent < 0.99){
								//if (chancepercent > chance){
									//event.getDrops().add(new ItemStack(Material.CREEPER_HEAD, 1));
							//@Nonnull Set<String> isSpawner;
							String isNametag = null;
							@Nonnull
							PersistentDataContainer pdc = entity.getPersistentDataContainer();
							isNametag = entity.getPersistentDataContainer().get(mmh.NAMETAG_KEY, PersistentDataType.STRING);//.getScoreboardTags();//
							if(debug&&isNametag != null){mmh.logDebug("EDE isNametag=" + isNametag.toString());}

							if(entity.getKiller().hasPermission("moremobheads.mobs")){
								if(entity.getKiller().hasPermission("moremobheads.nametag")&&isNametag != null){
									if(entity.getCustomName() != null&&!(entity.getCustomName().contains("jeb_"))
												&&!(entity.getCustomName().contains("Toast"))){
											if(debug){mmh.logDebug("EDE customname=" + entity.getCustomName().toString());}
											if(entity instanceof Skeleton||entity instanceof Zombie||entity instanceof PigZombie){
												if(mmh.getServer().getPluginManager().getPlugin("SilenceMobs") != null){
													if(entity.getCustomName().toLowerCase().contains("silenceme")||entity.getCustomName().toLowerCase().contains("silence me")){
													return;
												}
												}
												boolean enforcewhitelist = mmh.getConfig().getBoolean("whitelist.enforce", false);
												boolean enforceblacklist = mmh.getConfig().getBoolean("blacklist.enforce", false);
												boolean onwhitelist = mmh.getConfig().getString("whitelist.player_head_whitelist", "").toLowerCase().contains(entity.getCustomName().toLowerCase());
												boolean onblacklist = mmh.getConfig().getString("blacklist.player_head_blacklist", "").toLowerCase().contains(entity.getCustomName().toLowerCase());
												if(mmh.DropIt(event, chanceConfig.getDouble("named_mob", 0.10))){
													if(enforcewhitelist&&enforceblacklist){
														if(onwhitelist&&!(onblacklist)){
															Drops.add(mmh.dropMobHead(entity, entity.getCustomName(), entity.getKiller()));
														if(debug){mmh.logDebug("EDE " + entity.getCustomName().toString() + " Head Dropped");}
														}
													}else if(enforcewhitelist&&!enforceblacklist){
														if(onwhitelist){
															Drops.add(mmh.dropMobHead(entity, entity.getCustomName(), entity.getKiller()));
														if(debug){mmh.logDebug("EDE " +entity.getCustomName().toString() + " Head Dropped");}
														}
													}else if(!enforcewhitelist&&enforceblacklist){
														if(!onblacklist){
															Drops.add(mmh.dropMobHead(entity, entity.getCustomName(), entity.getKiller()));
														if(debug){mmh.logDebug("EDE " +entity.getCustomName().toString() + " Head Dropped");}
														}
													}else{
														Drops.add(mmh.dropMobHead(entity, entity.getCustomName(), entity.getKiller()));
													if(debug){mmh.logDebug("EDE " +entity.getCustomName().toString() + " Head Dropped");}
													}
												}
											}
										return;
									}
								}
					 			//String name = event.getEntity().getName().toUpperCase().replace(" ", "_");

					 			if(mob_whitelist != null&&!mob_whitelist.isEmpty()&&mob_blacklist != null&&!mob_blacklist.isEmpty()){
					 				if(!StrUtils.stringContains(mob_whitelist, name)){//mob_whitelist.contains(name)
					 					mmh.log(Level.INFO, "EDE - Mob - Not on whitelist. Mob=" + name);
					 					return;
					 				}
					 			}else if(mob_whitelist != null&&!mob_whitelist.isEmpty()){
					 				if(!StrUtils.stringContains(mob_whitelist, name)&&StrUtils.stringContains(mob_blacklist, name)){//mob_whitelist.contains(name)
					 					mmh.log(Level.INFO, "EDE - Mob - Not on whitelist - Is on blacklist. Mob=" + name);
					 					return;
					 				}
					 			}else if(mob_blacklist != null&&!mob_blacklist.isEmpty()){
					 				if(StrUtils.stringContains(mob_blacklist, name)){
					 					mmh.log(Level.INFO, "EDE - Mob - On blacklist. Mob=" + name);
					 					return;
					 				}
					 			}
					 			
								switch (name) {
					 			case "CREEPER":
					 				Creeper creeper = (Creeper) event.getEntity();
					 				double cchance = chanceConfig.getDouble("chance_percent.creeper", defpercent);
				 					if(creeper.isPowered()) {
				 						name = "CREEPER_CHARGED";
				 						cchance = 1.00;
				 					}
					 				if(mmh.DropIt(event, cchance)){
						 				if(mmh.getConfig().getBoolean("vanilla_heads.creeper", false)&&name != "CREEPER_CHARGED"){
						 					Drops.add( new ItemStack(Material.CREEPER_HEAD));
						 				}else{ // mmh.langName
						 					Drops.add( mmh.makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
						 							mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
						 				} // MobHeads.valueOf(name).getName() + " Head"
						 				if(debug){mmh.logDebug("EDE Creeper vanilla=" + mmh.getConfig().getBoolean("vanilla_heads.creeper", false));}
						 				if(debug){mmh.logDebug("EDE Creeper Head Dropped");}
					 				}
					 				break;
					 			case "ZOMBIE":
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.zombie", defpercent))){
					 					if(mmh.getConfig().getBoolean("vanilla_heads.zombie", false)){
						 					Drops.add( new ItemStack(Material.ZOMBIE_HEAD));
						 				}else{
						 					Drops.add( mmh.makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
						 							mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
						 				}
					 					if(debug){mmh.logDebug("EDE Zombie vanilla=" + mmh.getConfig().getBoolean("vanilla_heads.zombie", false));}
					 					if(debug){mmh.logDebug("EDE Zombie Head Dropped");}
					 				}
					 				break;
					 			case "SKELETON":
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.skeleton", defpercent))){
					 					if(mmh.getConfig().getBoolean("vanilla_heads.skeleton", false)){
						 					Drops.add( new ItemStack(Material.SKELETON_SKULL));
						 				}else{
						 					Drops.add( mmh.makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
						 							mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
						 				}
					 					if(debug){mmh.logDebug("EDE Skeleton vanilla=" + mmh.getConfig().getBoolean("vanilla_heads.skeleton", false));}
					 					if(debug){mmh.logDebug("EDE Skeleton Head Dropped");}
					 				}
					 				break;
					 			case "WITHER_SKELETON":
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.wither_skeleton", defpercent))){
					 					if(mmh.getConfig().getBoolean("vanilla_heads.wither_skeleton", false)){
						 					Drops.add( new ItemStack(Material.WITHER_SKELETON_SKULL));
						 				}else{
						 					Drops.add( mmh.makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
						 							mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
						 				}
					 					if(debug){mmh.logDebug("EDE Wither Skeleton Head Dropped");}
					 				}
					 				break;
					 			case "ENDER_DRAGON":
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.ender_dragon", defpercent))){
					 					if(mmh.getConfig().getBoolean("vanilla_heads.ender_dragon", false)){
						 					Drops.add( new ItemStack(Material.DRAGON_HEAD));
						 				}else{
						 					Drops.add( mmh.makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
						 							mmh.langName.getString(name.toLowerCase(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
						 				}
					 					if(debug){mmh.logDebug("EDE Ender Dragon Head Dropped");}
					 				}
					 				break;
					 			/**case "TROPICAL_FISH":
					 				TropicalFish daFish = (TropicalFish) entity;
					 				DyeColor daFishBody = daFish.getBodyColor();
					 				DyeColor daFishPatternColor = daFish.getPatternColor();
					 				Pattern	daFishType = daFish.getPattern();
					 				log("bodycolor=" + daFishBody.toString() + "\nPatternColor=" + daFishPatternColor.toString() + "\nPattern=" + daFishType.toString());
					 				//TropicalFishHeads daFishEnum = TropicalFishHeads.getIfPresent(name);
					 				
					 				if(mmh.DropIt(event, mmh.getConfig().getDouble(name + "_" +	daFishType, defpercent))){
					 					Drops.add( mmh.makeSkull(MobHeads.valueOf(name + "_" +	daFishType).getTexture(), MobHeads.valueOf(name + "_" +	daFishType).getName(), entity.getKiller()));
					 				}
					 				if(debug){mmh.logDebug("Skeleton Head Dropped");}
					 				break;*/
					 			case "WITHER":
									//Wither wither = (Wither) event.getEntity();
									int random = mmh.randomBetween(1, 4);
									if(debug){mmh.logDebug("EDE Wither random=" + random + "");}
									if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.wither", defpercent))){
										Drops.add( mmh.makeSkull(MobHeads.valueOf(name + "_" + random).getTexture().toString(), 
					 							mmh.langName.getString(name.toLowerCase() + "." + random, MobHeads.valueOf(name + "_" + random).getName() + " Head"), entity.getKiller()));
										if(debug){mmh.logDebug("EDE Wither_" + random + " Head Dropped");}
									}
									break;
					 			case "WOLF":
					 				Wolf wolf = (Wolf) event.getEntity();
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))){
						 				if(wolf.isAngry()){
						 					Drops.add( mmh.makeSkull(MobHeads.valueOf(name + "_ANGRY").getTexture().toString(), 
						 							mmh.langName.getString(name.toLowerCase() + "_angry", MobHeads.valueOf(name + "_ANGRY").getName() + " Head"), entity.getKiller()));
						 					if(debug){mmh.logDebug("EDE Angry Wolf Head Dropped");}
						 				}else{
						 					Drops.add( mmh.makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
						 							mmh.langName.getString(name.toLowerCase(), event.getEntity().getName() + " Head"), entity.getKiller()));
						 					if(debug){mmh.logDebug("EDE Wolf Head Dropped");}
						 				}
					 				}
					 				break;
					 			case "FOX":
					 				Fox dafox = (Fox) entity;
					 				String dafoxtype = dafox.getFoxType().toString();
					 				if(debug){mmh.logDebug("EDE dafoxtype=" + dafoxtype);}
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.fox." + dafoxtype.toString().toLowerCase(), defpercent))){
					 					Drops.add( mmh.makeSkull(MobHeads.valueOf(name + "_" + dafoxtype).getTexture().toString(), 
					 							mmh.langName.getString(name.toLowerCase() + "." + dafoxtype.toLowerCase(), MobHeads.valueOf(name + "_" + dafoxtype).getName() + " Head"), entity.getKiller()));
					 					if(debug){mmh.logDebug("EDE Fox Head Dropped");}
					 				}
					 				
					 				break;
					 			case "CAT":
					 				Cat dacat = (Cat) entity;
					 				String dacattype = dacat.getCatType().toString();
					 				if(debug){mmh.logDebug("entity cat=" + dacat.getCatType());}
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.cat." + dacattype.toLowerCase(), defpercent))){
					 					Drops.add(
				 							mmh.makeSkull(CatHeads.valueOf(dacattype).getTexture().toString(), 
				 									mmh.langName.getString(name.toLowerCase() + "." + dacattype.toLowerCase(), CatHeads.valueOf(dacattype).getName() + " Head"), entity.getKiller()));
					 					if(debug){mmh.logDebug("EDE Cat Head Dropped");}
					 				}
					 				break;
					 			case "OCELOT":
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))){
					 					Drops.add( mmh.makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
					 							mmh.langName.getString(MobHeads.valueOf(name).getNameString(), MobHeads.valueOf(name).getName() + " Head"), entity.getKiller()));
					 					if(debug){mmh.logDebug("EDE " + name + " Head Dropped");}
					 				}
					 				if(debug){mmh.logDebug("EDE " + MobHeads.valueOf(name) + " killed");}
					 				
					 				break;
					 			case "BEE":
					 				Bee daBee = (Bee) entity;
					 				int daAnger = daBee.getAnger();
					 				if(debug){mmh.logDebug("EDE daAnger=" + daAnger);}
					 				boolean daNectar = daBee.hasNectar();
					 				if(debug){mmh.logDebug("EDE daNectar=" + daNectar);}
						 				if(daAnger >= 1&&daNectar == true){
						 					if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.bee.angry_pollinated", defpercent))){
						 						Drops.add( mmh.makeSkull(MobHeads.valueOf("BEE_ANGRY_POLLINATED").getTexture().toString(), 
						 								mmh.langName.getString(name.toLowerCase() + ".angry_pollinated", "Angry Pollinated Bee Head"), entity.getKiller()));
						 						if(debug){mmh.logDebug("EDE Angry Pollinated Bee Head Dropped");}
						 					}
						 				}else if(daAnger >= 1&&daNectar == false){
						 					if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.bee.angry", defpercent))){
						 						Drops.add( mmh.makeSkull(MobHeads.valueOf("BEE_ANGRY").getTexture().toString(), 
						 								mmh.langName.getString(name.toLowerCase() + ".angry", "Angry Bee Head"), entity.getKiller()));
						 						if(debug){mmh.logDebug("EDE Angry Bee Head Dropped");}
						 					}
						 				}else if(daAnger == 0&&daNectar == true){
						 					if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.bee.pollinated", defpercent))){
						 						Drops.add( mmh.makeSkull(MobHeads.valueOf("BEE_POLLINATED").getTexture().toString(), 
						 								mmh.langName.getString(name.toLowerCase() + ".pollinated", "Pollinated Bee Head"), entity.getKiller()));
						 						if(debug){mmh.logDebug("EDE Pollinated Bee Head Dropped");}
						 					}
						 				}else if(daAnger == 0&&daNectar == false){
						 					if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.bee.chance_percent", defpercent))){
						 						Drops.add( mmh.makeSkull(MobHeads.valueOf("BEE").getTexture().toString(), 
						 								mmh.langName.getString(name.toLowerCase() + ".none", "Bee Head"), entity.getKiller()));
						 						if(debug){mmh.logDebug("EDE Bee Head Dropped");}
						 					}
						 				}
					 				break;
					 			case "LLAMA":
					 				Llama daLlama = (Llama) entity;
					 				String daLlamaColor = daLlama.getColor().toString();
					 				String daLlamaName = LlamaHeads.valueOf(name + "_" + daLlamaColor).getName() + " Head";//daLlamaColor.toLowerCase().replace("b", "B").replace("c", "C").replace("g", "G").replace("wh", "Wh") + " Llama Head";
					 				//log(name + "_" + daLlamaColor);
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.llama." + daLlamaColor.toLowerCase(), defpercent))){		
					 					Drops.add( mmh.makeSkull(LlamaHeads.valueOf(name + "_" + daLlamaColor).getTexture().toString(), 
					 							mmh.langName.getString(name.toLowerCase() + "." + daLlamaColor.toLowerCase(), daLlamaName), entity.getKiller()));
					 					if(debug){mmh.logDebug("EDE Llama Head Dropped");}
					 				}
					 				break;
					 			case "HORSE":
					 				Horse daHorse = (Horse) entity;
					 				String daHorseColor = daHorse.getColor().toString();
					 				String daHorseName = HorseHeads.valueOf(name + "_" + daHorseColor).getName() + " Head";//daHorseColor.toLowerCase().replace("b", "B").replace("ch", "Ch").replace("cr", "Cr").replace("d", "D")
					 						//.replace("g", "G").replace("wh", "Wh").replace("_", " ") + " Horse Head";
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.horse." + daHorseColor.toLowerCase(), defpercent))){
					 					Drops.add( mmh.makeSkull(HorseHeads.valueOf(name + "_" + daHorseColor).getTexture().toString(), 
					 							mmh.langName.getString(name.toLowerCase() + "." + daHorseColor.toLowerCase(), daHorseName), entity.getKiller()));
					 					if(debug){mmh.logDebug("EDE Horse Head Dropped");}
					 				}
					 				break;
					 			case "MUSHROOM_COW":
					 				MushroomCow daMushroom = (MushroomCow) entity;
					 				String daCowVariant = daMushroom.getVariant().toString();
					 				String daCowName = daCowVariant.toLowerCase().replace("br", "Br").replace("re", "Re") + " Mooshroom Head";
					 				if(debug){mmh.logDebug("EDE " + name + "_" + daCowVariant);}
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.mushroom_cow." + daCowVariant.toLowerCase(), defpercent))){
					 					Drops.add( mmh.makeSkull(MobHeads.valueOf(name + "_" + daCowVariant).getTexture().toString(), 
					 							mmh.langName.getString(name.toLowerCase() + "." + daCowVariant.toLowerCase(), daCowName), entity.getKiller()));
					 					if(debug){mmh.logDebug("EDE Mooshroom Head Dropped");}
					 				}
					 				break;
					 			case "PANDA":
					 				Panda daPanda = (Panda) entity;
					 				String daPandaGene = daPanda.getMainGene().toString();
					 				String daPandaName = daPandaGene.toLowerCase().replace("br", "Br").replace("ag", "Ag").replace("la", "La")
					 						.replace("no", "No").replace("p", "P").replace("we", "We").replace("wo", "Wo") + " Panda Head";
					 				if(daPandaGene.equalsIgnoreCase("normal")){daPandaName.replace("normal ", "");}
					 				if(debug){mmh.logDebug("EDE " + name + "_" + daPandaGene);}
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.panda." + daPandaGene.toLowerCase(), defpercent))){
					 					Drops.add( mmh.makeSkull(MobHeads.valueOf(name + "_" + daPandaGene).getTexture().toString(), 
					 							mmh.langName.getString(name.toLowerCase() + "." + daPandaGene.toLowerCase(), daPandaName), entity.getKiller()));
					 					if(debug){mmh.logDebug("EDE Panda Head Dropped");}
					 				}
					 				break;
					 			case "PARROT":
					 				Parrot daParrot = (Parrot) entity;
					 				String daParrotVariant = daParrot.getVariant().toString();
					 				String daParrotName = daParrotVariant.toLowerCase().replace("b", "B").replace("c", "C").replace("g", "G")
					 						.replace("red", "Red") + " Parrot Head";
					 				if(debug){mmh.logDebug("EDE " + name + "_" + daParrotVariant);}
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.parrot." + daParrotVariant.toLowerCase(), defpercent))){
					 					Drops.add( mmh.makeSkull(MobHeads.valueOf(name + "_" + daParrotVariant).getTexture().toString(), 
					 							mmh.langName.getString(name.toLowerCase() + "." + daParrotVariant.toLowerCase(), daParrotName), entity.getKiller()));
					 					if(debug){mmh.logDebug("EDE Parrot Head Dropped");}
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
					 				String daRabbitName = RabbitHeads.valueOf(name + "_" + daRabbitType).getName() + " Head";
					 				if(debug){mmh.logDebug("EDE " + name + "_" + daRabbitType);}
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.rabbit." + daRabbitType.toLowerCase(), defpercent))){
					 					Drops.add( mmh.makeSkull(RabbitHeads.valueOf(name + "_" + daRabbitType).getTexture().toString(), 
					 							mmh.langName.getString(name.toLowerCase() + "." + daRabbitType.toLowerCase(), daRabbitName), entity.getKiller()));
					 					if(debug){mmh.logDebug("EDE Rabbit Head Dropped");}
					 				}
					 				break;
					 			case "VILLAGER":
					 				Villager daVillager = (Villager) entity; // Location jobsite = daVillager.getMemory(MemoryKey.JOB_SITE);
					 				String daVillagerType = daVillager.getVillagerType().toString();
					 				String daVillagerProfession = daVillager.getProfession().toString();
					 				if(debug){mmh.logDebug("EDE name=" + name);}
					 				if(debug){mmh.logDebug("EDE profession=" + daVillagerProfession);}
					 				if(debug){mmh.logDebug("EDE type=" + daVillagerType);}
					 				String daName = name + "_" + daVillagerProfession + "_" + daVillagerType;
					 				if(debug){mmh.logDebug("EDE " + daName + "		 " + name + "_" + daVillagerProfession + "_" + daVillagerType);}
					 				String daVillagerName = VillagerHeads.valueOf(daName).getName() + " Head";
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.villager." + daVillagerType.toLowerCase() + "." + daVillagerProfession.toLowerCase(), defpercent))){
					 					Drops.add( mmh.makeSkull(VillagerHeads.valueOf(name + "_" + daVillagerProfession + "_" + daVillagerType).getTexture().toString(), 
					 							mmh.langName.getString(name.toLowerCase() + "." + daVillagerType.toLowerCase() + "." + daVillagerProfession.toLowerCase()
					 									, daVillagerName), entity.getKiller()));
					 					if(debug){mmh.logDebug("EDE Villager Head Dropped");}
					 				}
					 				break;
					 			case "ZOMBIE_VILLAGER":
					 				ZombieVillager daZombieVillager = (ZombieVillager) entity;
					 				String daZombieVillagerProfession = daZombieVillager.getVillagerProfession().toString();
					 				String daZombieVillagerName = ZombieVillagerHeads.valueOf(name + "_" + daZombieVillagerProfession).getName() + " Head";
					 				if(debug){mmh.logDebug("EDE " + name + "_" + daZombieVillagerProfession);}
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.zombie_villager", defpercent))){
					 					Drops.add( mmh.makeSkull(ZombieVillagerHeads.valueOf(name + "_" + daZombieVillagerProfession).getTexture().toString(), 
					 							mmh.langName.getString(name.toLowerCase() + "." + daZombieVillagerProfession.toLowerCase(), daZombieVillagerName), entity.getKiller()));
					 					if(debug){mmh.logDebug("EDE Zombie Villager Head Dropped");}
					 				}
					 				break;
					 			case "SHEEP":
					 				Sheep daSheep = (Sheep) entity;
					 				String daSheepColor = daSheep.getColor().toString();
					 				String daSheepName;
					 				
					 				if(daSheep.getCustomName() != null){
						 				if(daSheep.getCustomName().contains("jeb_")){
						 					daSheepColor = "jeb_";
						 				}else{
						 					daSheepColor = daSheep.getColor().toString();
						 				}
					 				}
					 				daSheepName = SheepHeads.valueOf(name + "_" + daSheepColor).getName() + " Head";
					 				if(debug){mmh.logDebug("EDE " + daSheepColor + "_" + name);}
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.sheep." + daSheepColor.toLowerCase(), defpercent))){
					 					Drops.add( mmh.makeSkull(SheepHeads.valueOf(name + "_" + daSheepColor).getTexture().toString(), 
					 							mmh.langName.getString(name.toLowerCase() + "." + daSheepColor.toLowerCase(), daSheepName), entity.getKiller()));
					 					if(debug){mmh.logDebug("EDE Sheep Head Dropped");}
					 				}
					 				break;
					 			/**case "STRIDER":
					 				Strider strider = (Strider) entity;
					 				
					 				break;*/
					 			case "TRADER_LLAMA":
					 				TraderLlama daTraderLlama = (TraderLlama) entity;
					 				String daTraderLlamaColor = daTraderLlama.getColor().toString();
					 				String daTraderLlamaName = LlamaHeads.valueOf(name + "_" + daTraderLlamaColor).getName() + " Head";
					 				if(debug){mmh.logDebug("EDE " + daTraderLlamaColor + "_" + name);}
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent.trader_llama." + daTraderLlamaColor.toLowerCase(), defpercent))){
					 					Drops.add( mmh.makeSkull(LlamaHeads.valueOf(name + "_" + daTraderLlamaColor).getTexture().toString(), 
					 							mmh.langName.getString(name.toLowerCase() + "." + daTraderLlamaColor.toLowerCase(), daTraderLlamaName), entity.getKiller()));
					 					if(debug){mmh.logDebug("EDE Trader Llama Head Dropped");}
					 				}
					 				break;
					 			case "STRIDER":
					 				Strider daStrider = (Strider) entity;
					 				PersistentDataContainer pdc2 = daStrider.getPersistentDataContainer();
					 				boolean isShivering = Boolean.parseBoolean( daStrider.getPersistentDataContainer().get(mmh.SHIVERING_KEY, PersistentDataType.STRING) );
					 				if(mmh.chance25oftrue()) { // isShivering
					 					name = name.concat("_SHIVERING");
					 					if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))){
						 					Drops.add( mmh.makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
						 							mmh.langName.getString(name.toLowerCase(), "Shivering " + event.getEntity().getName() + " Head"), entity.getKiller()));
						 					if(debug){mmh.logDebug("EDE " + name + " Head Dropped");}
						 				}
					 				}else {
					 					if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))){
						 					Drops.add( mmh.makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
						 							mmh.langName.getString(name.toLowerCase(), event.getEntity().getName() + " Head"), entity.getKiller()));
						 					if(debug){mmh.logDebug("EDE " + name + " Head Dropped");}
						 				}
					 				}
					 				
					 				if(debug){mmh.logDebug("EDE " + MobHeads.valueOf(name) + " killed");}
					 				break;
					 			default:
					 				//mmh.makeSkull(MobHeads.valueOf(name).getTexture(), name);
					 				if(debug){mmh.logDebug("EDE name=" + name + " line:1005");}
					 				if(debug){mmh.logDebug("EDE texture=" + MobHeads.valueOf(name).getTexture().toString() + " line:1006");}
					 				if(debug){mmh.logDebug("EDE location=" + entity.getLocation().toString() + " line:1007");}
					 				if(debug){mmh.logDebug("EDE getName=" + event.getEntity().getName() + " line:1008");}
					 				if(debug){mmh.logDebug("EDE killer=" + entity.getKiller().toString() + " line:1009");}
					 				if(mmh.DropIt(event, chanceConfig.getDouble("chance_percent." + name.toLowerCase(), defpercent))){
					 					Drops.add( mmh.makeSkull(MobHeads.valueOf(name).getTexture().toString(), 
					 							mmh.langName.getString(name.toLowerCase(), event.getEntity().getName() + " Head"), entity.getKiller()));
					 					if(debug){mmh.logDebug("EDE " + name + " Head Dropped");}
					 				}
					 				if(debug){mmh.logDebug("EDE " + MobHeads.valueOf(name) + " killed");}
					 				break;
					 			}
							}
						//}
					//}
					return;
				}
			}
	}
	
	@SuppressWarnings({ "static-access", "unused" })
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event){ //onEntitySpawn(EntitySpawnEvent e) { // TODO: onCreatureSpawn
		if(mmh.getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)){
			Entity entity = event.getEntity();
					if(entity instanceof WanderingTrader){
						//traderHeads2 = YamlConfiguration.loadConfiguration(traderFile2);
						if(debug){mmh.logDebug("CSE WanderingTrader spawned");}
						WanderingTrader trader = (WanderingTrader) entity;
						List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();
						List<MerchantRecipe> oldRecipes = trader.getRecipes();
						
						/** if(!cached_recipes.isEmpty()) {
							recipes.addAll(cached_recipes);
							if(mmh.getConfig().getBoolean("wandering_trades.keep_default_trades", true)){
								recipes.addAll(oldRecipes);
							}
							trader.setRecipes(recipes);
							cached_recipes.clear();
							cached_recipes.clear();
							if(debug){mmh.logDebug("CSE WT loaded, list cleared.");}
						}else {
							if(debug){mmh.logDebug("CSE WT Error.");}
						}*/
							
						
						// BukkitTask updateTask = mmh.getServer().getScheduler().runTaskAsynchronously(mmh, new Runnable() {
							
							//public void run() {*/
						
						/**
						 *  Player Heads
						 */ 
						if(mmh.getConfig().getBoolean("wandering_trades.player_heads.enabled", true)){
							int playerRandom = mmh.randomBetween(mmh.getConfig().getInt("wandering_trades.player_heads.min", 0), mmh.getConfig().getInt("wandering_trades.player_heads.max", 3));
							if(debug){mmh.logDebug("CSE playerRandom=" + playerRandom);}
							if(playerRandom > 0){
								if(debug){mmh.logDebug("CSE playerRandom > 0");}
								int numOfplayerheads = mmh.playerHeads.getInt("players.number", 0) - 1;
								if(debug){mmh.logDebug("CSE numOfplayerheads=" + numOfplayerheads);}
								HashSet<Integer> used = new HashSet<Integer>();
								for(int i=0; i<playerRandom; i++){
									int randomPlayerHead = mmh.randomBetween(0, numOfplayerheads);
									while (used.contains(randomPlayerHead)) { //while we have already used the number
										randomPlayerHead = mmh.randomBetween(0, numOfplayerheads); //generate a new one because it's already used
								    }
								    //by this time, add will be unique
								    used.add(randomPlayerHead);
									/**	if(debug){mmh.logDebug("CSE randomPlayerHead=" + randomPlayerHead);}
									ItemStack price1 = mmh.playerHeads.getItemStack("players.player_" + randomPlayerHead + ".price_1", new ItemStack(Material.AIR));
										if(debug){mmh.logDebug("CSE price1=" + price1);}
									ItemStack price2 = mmh.playerHeads.getItemStack("players.player_" + randomPlayerHead + ".price_2", new ItemStack(Material.AIR));
										if(debug){mmh.logDebug("CSE price2=" + price2);}
									ItemStack itemstack = mmh.playerHeads.getItemStack("players.player_" + randomPlayerHead + ".itemstack", new ItemStack(Material.AIR));
										if(debug){mmh.logDebug("CSE itemstack=" + itemstack);}
									MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.playerHeads.getInt("players.player_" + randomPlayerHead + ".quantity", (int) 3));
									recipe.setExperienceReward(true);
									recipe.addIngredient(price1);
									recipe.addIngredient(price2);//*/
									recipes.add(playerhead_recipes.get(randomPlayerHead));
								}
								used.clear();
							}
						}
						/**
						 *  Block Heads
						 */
						if(mmh.getConfig().getBoolean("wandering_trades.block_heads.enabled", true)){
							int min = mmh.getConfig().getInt("wandering_trades.block_heads.min", 0);
							int max;
							if(mmh.getMCVersion().startsWith("1.16")||mmh.getMCVersion().startsWith("1.17")){
								max = mmh.getConfig().getInt("wandering_trades.block_heads.pre_116.max", 5);
							}else{
								max = mmh.getConfig().getInt("wandering_trades.block_heads.pre_116.max", 5);
							}
							if(debug){mmh.logDebug("CSE BH1 min=" + min + " max=" + max);}
							int blockRandom = mmh.randomBetween(min, max);
								if(debug){mmh.logDebug("CSE blockRandom=" + blockRandom);}
							if(blockRandom > 0){
									if(debug){mmh.logDebug("CSE blockRandom > 0");}
								int numOfblockheads = BHNum >= 0 ? BHNum : 0;
									if(debug){mmh.logDebug("CSE numOfblockheads=" + numOfblockheads);}
								HashSet<Integer> used = new HashSet<Integer>();
								for(int i=0; i<blockRandom; i++){
										if(debug){mmh.logDebug("CSE i=" + i);}
									int randomBlockHead = mmh.randomBetween(0, numOfblockheads);
									while (used.contains(randomBlockHead)) { //while we have already used the number
										randomBlockHead = mmh.randomBetween(0, numOfblockheads); //generate a new one because it's already used
								    }
								    //by this time, add will be unique
								    used.add(randomBlockHead);
									/**	if(debug){mmh.logDebug("CSE randomBlockHead=" + randomBlockHead);}
									ItemStack price1 = mmh.blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".price_1", new ItemStack(Material.AIR));
										if(debug){mmh.logDebug("CSE price1=" + price1);}
									ItemStack price2 = mmh.blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".price_2", new ItemStack(Material.AIR));
										if(debug){mmh.logDebug("CSE price2=" + price2);}
									ItemStack itemstack = mmh.blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
										if(debug){mmh.logDebug("CSE itemstack=" + itemstack);}
									MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.blockHeads.getInt("blocks.block_" + randomBlockHead + ".quantity", (int) 1));
										recipe.setExperienceReward(true);
										recipe.addIngredient(price1);
										recipe.addIngredient(price2);//*/
										recipes.add(blockhead_recipes.get(randomBlockHead));
								}
								used.clear();
							}
						}
						
						if(mmh.getMCVersion().startsWith("1.16")||mmh.getMCVersion().startsWith("1.17")){
							/**
							 *  Block Heads 2
							 */
							if(mmh.getConfig().getBoolean("wandering_trades.block_heads.enabled", true)){
								int min = mmh.getConfig().getInt("wandering_trades.block_heads.is_116.min", 0);
								int max = mmh.getConfig().getInt("wandering_trades.block_heads.is_116.max", 5) / 2;
									if(debug){mmh.logDebug("CSE BH2 min=" + min + " max=" + max);}
								int blockRandom = mmh.randomBetween(min, max);
									if(debug){mmh.logDebug("CSE blockRandom=" + blockRandom);}
								if(blockRandom > 0){
										if(debug){mmh.logDebug("CSE blockRandom > 0");}
									int numOfblockheads = (BHNum + BHNum2 - 1) >= 0 ? BHNum + BHNum2 - 1 : 0;
										if(debug){mmh.logDebug("CSE numOfblockheads=" + numOfblockheads);}
									HashSet<Integer> used = new HashSet<Integer>();
									for(int i=0; i<blockRandom; i++){
											if(debug){mmh.logDebug("CSE i=" + i);}
										int randomBlockHead = mmh.randomBetween(BHNum, numOfblockheads);
										while (used.contains(randomBlockHead)) { //while we have already used the number
											randomBlockHead = mmh.randomBetween(BHNum, numOfblockheads); //generate a new one because it's already used
									    }
									    //by this time, add will be unique
									    used.add(randomBlockHead);
										/**	if(debug){mmh.logDebug("CSE randomBlockHead=" + randomBlockHead);}
										ItemStack price1 = mmh.blockHeads2.getItemStack("blocks.block_" + randomBlockHead + ".price_1", new ItemStack(Material.AIR));
											if(debug){mmh.logDebug("CSE price1=" + price1);}
										ItemStack price2 = mmh.blockHeads2.getItemStack("blocks.block_" + randomBlockHead + ".price_2", new ItemStack(Material.AIR));
											if(debug){mmh.logDebug("CSE price2=" + price2);}
										ItemStack itemstack = mmh.blockHeads2.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
											if(debug){mmh.logDebug("CSE itemstack=" + itemstack);}
										MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.blockHeads2.getInt("blocks.block_" + randomBlockHead + ".quantity", (int) 1));
											recipe.setExperienceReward(true);
											recipe.addIngredient(price1);
											recipe.addIngredient(price2);//*/
											recipes.add(blockhead_recipes.get(randomBlockHead));
									}
									used.clear();
								}
							}
						}
						
						/**
						 *  Custom Trades
						 */
						if(mmh.getConfig().getBoolean("wandering_trades.custom_trades.enabled", false)){
							int customRandom = mmh.randomBetween(mmh.getConfig().getInt("wandering_trades.custom_trades.min", 0), mmh.getConfig().getInt("wandering_trades.custom_trades.max", 5));
							int numOfCustomTrades = mmh.traderCustom.getInt("custom_trades.number", -1) - 1;
							//if(debug){logDebug("CSE numOfCustomTrades=" + numOfCustomTrades);}
							//int customRandom = randomBetween(getConfig().getInt("wandering_trades.min_custom_trades", 0), getConfig().getInt("wandering_trades.max_custom_trades", 3));
								if(debug){mmh.logDebug("CSE customRandom=" + customRandom);}
							if(customRandom > 0){
								if(debug){mmh.logDebug("CSE customRandom > 0");}
								//for(int randomCustomTrade=1; randomCustomTrade<numOfCustomTrades; randomCustomTrade++){
								HashSet<Integer> used = new HashSet<Integer>();
								for(int i=0; i<customRandom; i++){
									int randomCustomTrade = mmh.randomBetween(0, numOfCustomTrades);
									while (used.contains(randomCustomTrade)) { //while we have already used the number
										randomCustomTrade = mmh.randomBetween(0, numOfCustomTrades); //generate a new one because it's already used
								    }
								    //by this time, add will be unique
								    used.add(randomCustomTrade);
									if(debug){mmh.logDebug("CSE randomCustomTrade=" + randomCustomTrade);}
									/** Fix chance later */
									double chance = Math.random();
										if(debug){mmh.logDebug("CSE chance=" + chance + " line:1540");}
									if(mmh.traderCustom.getDouble("custom_trades.trade_" + randomCustomTrade + ".chance", 0.002) > chance){
										/**	if(debug){mmh.logDebug("CSE randomCustomTrade=" + randomCustomTrade);}
										ItemStack price1 = mmh.traderCustom.getItemStack("custom_trades.trade_" + randomCustomTrade + ".price_1", new ItemStack(Material.AIR));
											if(debug){mmh.logDebug("CSE price1=" + price1.toString());}
										ItemStack price2 = mmh.traderCustom.getItemStack("custom_trades.trade_" + randomCustomTrade + ".price_2", new ItemStack(Material.AIR));
											if(debug){mmh.logDebug("CSE price2=" + price2.toString());}
										ItemStack itemstack = mmh.traderCustom.getItemStack("custom_trades.trade_" + randomCustomTrade + ".itemstack", new ItemStack(Material.AIR));
											if(debug){mmh.logDebug("CSE itemstack=" + itemstack.toString());}
										MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.traderCustom.getInt("custom_trades.trade_" + randomCustomTrade + ".quantity", (int) 1));
												recipe.setExperienceReward(true);
												recipe.addIngredient(price1);
												recipe.addIngredient(price2);//*/
												recipes.add(custometrade_recipes.get(randomCustomTrade));
									}
								}
								used.clear();
							}
						}
						
						if(mmh.getConfig().getBoolean("wandering_trades.keep_default_trades", true)){
							recipes.addAll(oldRecipes);
						}
						trader.setRecipes(recipes);
					}
				}
				
	}
	
	/**@SuppressWarnings("unused")
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) { // TODO: OnPlayerJoin
		/**if(cached_recipes.isEmpty()) {
			List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();
			BukkitTask updateTask = mmh.getServer().getScheduler().runTaskAsynchronously(mmh, new Runnable() {
				
				@SuppressWarnings("static-access")
				public void run() {//*/
			/**
			 *  Player Heads
			 */ 
			/**if(mmh.getConfig().getBoolean("wandering_trades.player_heads.enabled", true)){
				int playerRandom = mmh.randomBetween(mmh.getConfig().getInt("wandering_trades.player_heads.min", 0), mmh.getConfig().getInt("wandering_trades.player_heads.max", 3));
				if(debug){mmh.logDebug("CSE playerRandom=" + playerRandom);}
				if(playerRandom > 0){
					if(debug){mmh.logDebug("CSE playerRandom > 0");}
					int numOfplayerheads = mmh.playerHeads.getInt("players.number");
					if(debug){mmh.logDebug("CSE numOfplayerheads=" + numOfplayerheads);}
					HashSet<Integer> used = new HashSet<Integer>();
					for(int i=0; i<playerRandom; i++){
						int randomPlayerHead = mmh.randomBetween(1, numOfplayerheads);
						while (used.contains(randomPlayerHead)) { //while we have already used the number
							randomPlayerHead = mmh.randomBetween(1, numOfplayerheads); //generate a new one because it's already used
					    }
					    //by this time, add will be unique
					    used.add(randomPlayerHead);
							if(debug){mmh.logDebug("CSE randomPlayerHead=" + randomPlayerHead);}
						ItemStack price1 = mmh.playerHeads.getItemStack("players.player_" + randomPlayerHead + ".price_1", new ItemStack(Material.AIR));
							if(debug){mmh.logDebug("CSE price1=" + price1);}
						ItemStack price2 = mmh.playerHeads.getItemStack("players.player_" + randomPlayerHead + ".price_2", new ItemStack(Material.AIR));
							if(debug){mmh.logDebug("CSE price2=" + price2);}
						ItemStack itemstack = mmh.playerHeads.getItemStack("players.player_" + randomPlayerHead + ".itemstack", new ItemStack(Material.AIR));
							if(debug){mmh.logDebug("CSE itemstack=" + itemstack);}
						MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.playerHeads.getInt("players.player_" + randomPlayerHead + ".quantity", (int) 3));
						recipe.setExperienceReward(true);
						recipe.addIngredient(price1);
						recipe.addIngredient(price2);
						recipes.add(recipe);
					}
					used.clear();
				}
			}//*/
			/**
			 *  Block Heads
			 */
			/**if(mmh.getConfig().getBoolean("wandering_trades.block_heads.enabled", true)){
				int min = mmh.getConfig().getInt("wandering_trades.block_heads.min", 0);
				int max;
				if(mmh.getMCVersion().startsWith("1.16")||mmh.getMCVersion().startsWith("1.17")){
					max = mmh.getConfig().getInt("wandering_trades.block_heads.pre_116.max", 5);
				}else{
					max = mmh.getConfig().getInt("wandering_trades.block_heads.pre_116.max", 5);
				}
				if(debug){mmh.logDebug("CSE BH1 min=" + min + " max=" + max);}
				int blockRandom = mmh.randomBetween(min, max);
					if(debug){mmh.logDebug("CSE blockRandom=" + blockRandom);}
				if(blockRandom > 0){
						if(debug){mmh.logDebug("CSE blockRandom > 0");}
					int numOfblockheads = mmh.blockHeads.getInt("blocks.number");
						if(debug){mmh.logDebug("CSE numOfblockheads=" + numOfblockheads);}
					HashSet<Integer> used = new HashSet<Integer>();
					for(int i=0; i<blockRandom; i++){
							if(debug){mmh.logDebug("CSE i=" + i);}
						int randomBlockHead = mmh.randomBetween(1, numOfblockheads);
						while (used.contains(randomBlockHead)) { //while we have already used the number
							randomBlockHead = mmh.randomBetween(1, numOfblockheads); //generate a new one because it's already used
					    }
					    //by this time, add will be unique
					    used.add(randomBlockHead);
							if(debug){mmh.logDebug("CSE randomBlockHead=" + randomBlockHead);}
						ItemStack price1 = mmh.blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".price_1", new ItemStack(Material.AIR));
							if(debug){mmh.logDebug("CSE price1=" + price1);}
						ItemStack price2 = mmh.blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".price_2", new ItemStack(Material.AIR));
							if(debug){mmh.logDebug("CSE price2=" + price2);}
						ItemStack itemstack = mmh.blockHeads.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
							if(debug){mmh.logDebug("CSE itemstack=" + itemstack);}
						MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.blockHeads.getInt("blocks.block_" + randomBlockHead + ".quantity", (int) 1));
							recipe.setExperienceReward(true);
							recipe.addIngredient(price1);
							recipe.addIngredient(price2);
							recipes.add(recipe);
					}
					used.clear();
				}
			}//*/
			
			/**if(mmh.getMCVersion().startsWith("1.16")||mmh.getMCVersion().startsWith("1.17")){
				/**
				 *  Block Heads 2
				 */
				/**if(mmh.getConfig().getBoolean("wandering_trades.block_heads.enabled", true)){
					int min = mmh.getConfig().getInt("wandering_trades.block_heads.is_116.min", 0);
					int max = mmh.getConfig().getInt("wandering_trades.block_heads.is_116.max", 5) / 2;
						if(debug){mmh.logDebug("CSE BH2 min=" + min + " max=" + max);}
					int blockRandom = mmh.randomBetween(min, max);
						if(debug){mmh.logDebug("CSE blockRandom=" + blockRandom);}
					if(blockRandom > 0){
							if(debug){mmh.logDebug("CSE blockRandom > 0");}
						int numOfblockheads = mmh.blockHeads2.getInt("blocks.number");
							if(debug){mmh.logDebug("CSE numOfblockheads=" + numOfblockheads);}
						HashSet<Integer> used = new HashSet<Integer>();
						for(int i=0; i<blockRandom; i++){
								if(debug){mmh.logDebug("CSE i=" + i);}
							int randomBlockHead = mmh.randomBetween(1, numOfblockheads);
							while (used.contains(randomBlockHead)) { //while we have already used the number
								randomBlockHead = mmh.randomBetween(1, numOfblockheads); //generate a new one because it's already used
						    }
						    //by this time, add will be unique
						    used.add(randomBlockHead);
								if(debug){mmh.logDebug("CSE randomBlockHead=" + randomBlockHead);}
							ItemStack price1 = mmh.blockHeads2.getItemStack("blocks.block_" + randomBlockHead + ".price_1", new ItemStack(Material.AIR));
								if(debug){mmh.logDebug("CSE price1=" + price1);}
							ItemStack price2 = mmh.blockHeads2.getItemStack("blocks.block_" + randomBlockHead + ".price_2", new ItemStack(Material.AIR));
								if(debug){mmh.logDebug("CSE price2=" + price2);}
							ItemStack itemstack = mmh.blockHeads2.getItemStack("blocks.block_" + randomBlockHead + ".itemstack", new ItemStack(Material.AIR));
								if(debug){mmh.logDebug("CSE itemstack=" + itemstack);}
							MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.blockHeads2.getInt("blocks.block_" + randomBlockHead + ".quantity", (int) 1));
								recipe.setExperienceReward(true);
								recipe.addIngredient(price1);
								recipe.addIngredient(price2);
								recipes.add(recipe);
						}
						used.clear();
					}
				}
			}//*/
			
			/**
			 *  Custom Trades
			 */
			/**if(mmh.getConfig().getBoolean("wandering_trades.custom_trades.enabled", false)){
				int customRandom = mmh.randomBetween(mmh.getConfig().getInt("wandering_trades.custom_trades.min", 0), mmh.getConfig().getInt("wandering_trades.custom_trades.max", 5));
				int numOfCustomTrades = mmh.traderCustom.getInt("custom_trades.number") + 1;
				//if(debug){logDebug("CSE numOfCustomTrades=" + numOfCustomTrades);}
				//int customRandom = randomBetween(getConfig().getInt("wandering_trades.min_custom_trades", 0), getConfig().getInt("wandering_trades.max_custom_trades", 3));
					if(debug){mmh.logDebug("CSE customRandom=" + customRandom);}
				if(customRandom > 0){
					if(debug){mmh.logDebug("CSE customRandom > 0");}
					//for(int randomCustomTrade=1; randomCustomTrade<numOfCustomTrades; randomCustomTrade++){
					HashSet<Integer> used = new HashSet<Integer>();
					for(int i=0; i<customRandom; i++){
						int randomCustomTrade = mmh.randomBetween(1, numOfCustomTrades);
						while (used.contains(randomCustomTrade)) { //while we have already used the number
							randomCustomTrade = mmh.randomBetween(1, numOfCustomTrades); //generate a new one because it's already used
					    }
					    //by this time, add will be unique
					    used.add(randomCustomTrade);
						if(debug){mmh.logDebug("CSE randomCustomTrade=" + randomCustomTrade);}
						/** Fix chance later */
						/**double chance = Math.random();
							if(debug){mmh.logDebug("CSE chance=" + chance + " line:1540");}
						if(mmh.traderCustom.getDouble("custom_trades.trade_" + randomCustomTrade + ".chance", 0.002) > chance){
								if(debug){mmh.logDebug("CSE randomCustomTrade=" + randomCustomTrade);}
							ItemStack price1 = mmh.traderCustom.getItemStack("custom_trades.trade_" + randomCustomTrade + ".price_1", new ItemStack(Material.AIR));
								if(debug){mmh.logDebug("CSE price1=" + price1.toString());}
							ItemStack price2 = mmh.traderCustom.getItemStack("custom_trades.trade_" + randomCustomTrade + ".price_2", new ItemStack(Material.AIR));
								if(debug){mmh.logDebug("CSE price2=" + price2.toString());}
							ItemStack itemstack = mmh.traderCustom.getItemStack("custom_trades.trade_" + randomCustomTrade + ".itemstack", new ItemStack(Material.AIR));
								if(debug){mmh.logDebug("CSE itemstack=" + itemstack.toString());}
							MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.traderCustom.getInt("custom_trades.trade_" + randomCustomTrade + ".quantity", (int) 1));
									recipe.setExperienceReward(true);
									recipe.addIngredient(price1);
									recipe.addIngredient(price2);
									recipes.add(recipe);
						}
					}
					used.clear();
				}
			}
			cached_recipes.addAll(recipes);
			}});//*/
	/**}//*/
	
	@SuppressWarnings({ "unused", "static-access", "deprecation", "rawtypes", "unchecked" })
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){ // TODO: Commands
			//log("command=" + cmd.getName() + " args=" + args[0] + args[1]);
			if (cmd.getName().equalsIgnoreCase("MMH")){
				if (args.length == 0){
					sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName() + ChatColor.GREEN + "]===============[]");
					sender.sendMessage(ChatColor.WHITE + " ");// https://ko-fi.com/joelgodofwar
					sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.get("newvers.donate") + ": https://ko-fi.com/joelgodofwar");// https://ko-fi.com/joelgodofwar
					sender.sendMessage(ChatColor.WHITE + " ");// https://ko-fi.com/joelgodofwar
					sender.sendMessage(ChatColor.WHITE + " /mmh reload - " + mmh.lang.get("reload", "Reloads this plugin."));//subject to server admin approval");
					sender.sendMessage(ChatColor.WHITE + " /mmh toggledebug - " + mmh.lang.get("srdebuguse", "Temporarily toggles debug."));//Cancels SinglePlayerSleep");
					if(mmh.getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)){
						sender.sendMessage(ChatColor.WHITE + " /mmh playerheads - " + mmh.lang.get("playerheads", "Shows how to use the playerheads commands"));
						//sender.sendMessage(ChatColor.WHITE + " /mmh blockheads - " + mmh.lang.get("blockheads", "Shows how to use the blockheads commands"));
						sender.sendMessage(ChatColor.WHITE + " /mmh customtrader - " + mmh.lang.get("customtrader", "Shows how to use the customtrader commands"));
					}
					sender.sendMessage(ChatColor.WHITE + " /mmh fixhead - " + mmh.lang.getString("headfix"));
					sender.sendMessage(ChatColor.WHITE + " /mmh givemh - gives player chosen the chosen mobhead");
					sender.sendMessage(ChatColor.WHITE + " /mmh giveph - gives player chosen player's head");
					sender.sendMessage(ChatColor.WHITE + " /mmh givebh - gives player chosen block's head");
					sender.sendMessage(ChatColor.WHITE + " /mmh display perms/vars - Shows permissions you have, or variables");
					sender.sendMessage(ChatColor.WHITE + " ");
					sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName() + ChatColor.GREEN + "]===============[]");
					return true;
				}
				if(args[0].equalsIgnoreCase("headNBT")){
					if(!(sender instanceof Player)) {
						return true;
					}
					Player player = (Player) sender;
					ItemStack mainHand = player.getInventory().getItemInMainHand();
					ItemStack offHand = player.getInventory().getItemInOffHand();
					if( mainHand != null && mainHand.getType().equals(Material.PLAYER_HEAD) ) {
						NBTItem item = new NBTItem(mainHand);
						log(Level.INFO,"" + item);
						player.sendMessage("" + item);
					}else if( offHand != null && offHand.getType().equals(Material.PLAYER_HEAD) ) {
						NBTItem item = new NBTItem(offHand);
						player.sendMessage("" + item);
						log(Level.INFO,"" + item);
					}else {
						//log(Level.INFO,"You do not have a head in either hand.");
						player.sendMessage("You do not have a head in either hand.");
					}
				}
				// /mmh display permvar playername
				// /     0        1        2
				if(args[0].equalsIgnoreCase("display")){
					if(args[1].equalsIgnoreCase("perms")||args[1].equalsIgnoreCase("permissions")){
						if(sender instanceof Player) {
							Player player = (Player) sender;
							sender.sendMessage("You " + player.getDisplayName() + " have the following permissions.");
							sender.sendMessage("moremobheads.players=" + player.hasPermission("moremobheads.players"));
							sender.sendMessage("moremobheads.mobs=" + player.hasPermission("moremobheads.mobs"));
							sender.sendMessage("moremobheads.nametag=" + player.hasPermission("moremobheads.nametag"));
							sender.sendMessage("moremobheads.reload=" + player.hasPermission("moremobheads.reload"));
							sender.sendMessage("moremobheads.toggledebug=" + player.hasPermission("moremobheads.toggledebug"));
							sender.sendMessage("moremobheads.showUpdateAvailable=" + player.hasPermission("moremobheads.showUpdateAvailable"));
							sender.sendMessage("moremobheads.customtrader=" + player.hasPermission("moremobheads.customtrader"));
							sender.sendMessage("moremobheads.playerheads=" + player.hasPermission("moremobheads.playerheads"));
							sender.sendMessage("moremobheads.blockheads=" + player.hasPermission("moremobheads.blockheads"));
							sender.sendMessage("moremobheads.fixhead=" + player.hasPermission("moremobheads.fixhead"));
							sender.sendMessage("moremobheads.give=" + player.hasPermission("moremobheads.give"));
							sender.sendMessage("" + mmh.getName() + " " + mmh.getDescription().getVersion() + " display perms end");
						}else {
							if(args.length >= 2) {
								Player player = sender.getServer().getPlayer(args[2]);
								sender.sendMessage("" + player.getDisplayName() + " has the following permissions.");
								sender.sendMessage("moremobheads.players=" + player.hasPermission("moremobheads.players"));
								sender.sendMessage("moremobheads.mobs=" + player.hasPermission("moremobheads.mobs"));
								sender.sendMessage("moremobheads.nametag=" + player.hasPermission("moremobheads.nametag"));
								sender.sendMessage("moremobheads.reload=" + player.hasPermission("moremobheads.reload"));
								sender.sendMessage("moremobheads.toggledebug=" + player.hasPermission("moremobheads.toggledebug"));
								sender.sendMessage("moremobheads.showUpdateAvailable=" + player.hasPermission("moremobheads.showUpdateAvailable"));
								sender.sendMessage("moremobheads.customtrader=" + player.hasPermission("moremobheads.customtrader"));
								sender.sendMessage("moremobheads.playerheads=" + player.hasPermission("moremobheads.playerheads"));
								sender.sendMessage("moremobheads.blockheads=" + player.hasPermission("moremobheads.blockheads"));
								sender.sendMessage("moremobheads.fixhead=" + player.hasPermission("moremobheads.fixhead"));
								sender.sendMessage("moremobheads.give=" + player.hasPermission("moremobheads.give"));
								sender.sendMessage("" + mmh.getName() + " " + mmh.getDescription().getVersion() + " display perms end");
							}else {
								sender.sendMessage("Console can only check permissions of Players.");
							}
						}
					}else if(args[1].equalsIgnoreCase("vars")||args[1].equalsIgnoreCase("variables")){
						sender.sendMessage("" + mmh.getName() + " " + mmh.getDescription().getVersion() + " display perms start");
						sender.sendMessage("debug=" + debug);
						sender.sendMessage("daLang=" + mmh.daLang);
						
						world_whitelist = mmh.getConfig().getString("world.whitelist", "");
						world_blacklist = mmh.getConfig().getString("world.blacklist", "");
						mob_whitelist = mmh.getConfig().getString("mob.whitelist", "");
						mob_blacklist = mmh.getConfig().getString("mob.blacklist", "");
						
						sender.sendMessage("world_whitelist=" + world_whitelist);
						sender.sendMessage("world_blacklist=" + world_blacklist);
						sender.sendMessage("mob_whitelist=" + mob_whitelist);
						sender.sendMessage("mob_blacklist=" + mob_blacklist);
						sender.sendMessage("" + mmh.getName() + " " + mmh.getDescription().getVersion() + " display perms end");
					}
				}
				if(args[0].equalsIgnoreCase("reload")){
					String perm = "moremobheads.reload";
					boolean hasPerm = sender.hasPermission(perm);
					if(debug){mmh.logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);}
					if(hasPerm||!(sender instanceof Player)){
						mmh.configReload();
						blockFile116 = new File(mmh.getDataFolder() + "" + File.separatorChar + "block_heads_1_16.yml");
						blockFile1162 = new File(mmh.getDataFolder() + "" + File.separatorChar + "block_heads_1_16_2.yml");
						if(mmh.getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)){
							if(!mmh.getMCVersion().startsWith("1.16")&&!mmh.getMCVersion().startsWith("1.17")){
								blockFile = new File(mmh.getDataFolder() + "" + File.separatorChar + "block_heads.yml");//\
								if(debug){logDebug("block_heads=" + blockFile.getPath());}
								if(!blockFile.exists()){																	// checks if the yaml does not exist
									mmh.saveResource("block_heads.yml", true);
									log(Level.INFO, "block_heads.yml not found! Creating in " + mmh.getDataFolder() + "");
									//ConfigAPI.copy(getResource("lang.yml"), langFile); // copies the yaml from your jar to the folder /plugin/<pluginName>
								}
							}
							if(Double.parseDouble(StrUtils.Left(mmh.getMCVersion(), 4)) == 1.16){
								if(debug){logDebug("block_heads_1_16=" + blockFile116.getPath());}
								if(debug){logDebug("block_heads_1_16_2=" + blockFile1162.getPath());}
								if(!blockFile116.exists()){
									mmh.saveResource("block_heads_1_16.yml", true);
									log(Level.INFO, "block_heads_1_16.yml not found! Creating in " + mmh.getDataFolder() + "");
								}
								if(!blockFile1162.exists()){
									mmh.saveResource("block_heads_1_16_2.yml", true);
									log(Level.INFO, "block_heads_1_16_2.yml not found! Creating in " + mmh.getDataFolder() + "");
								}
								blockFile = new File(mmh.getDataFolder() + "" + File.separatorChar + "block_heads_1_16.yml");
								log(Level.INFO, "Loading block_heads_1_16 files...");
							
							}else{
								log(Level.INFO, "Loading block_heads file...");
							}
					
							blockHeads = new YamlConfiguration();
							try {
								blockHeads.load(blockFile);
								log(Level.INFO, "Loading " + blockFile + "...");
							} catch (IOException | InvalidConfigurationException e1) {
								mmh.stacktraceInfo();
								e1.printStackTrace();
							}
							if(blockHeads.get("blocks.block_79.price_2.amount", null) != null){
								log(Level.INFO, "block_heads files outdated, updating...");
								blockHeads.set("blocks.block_79.price_2.amount", "");
								try {
									blockHeads.save(blockFile);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							
							blockHeads2 = new YamlConfiguration();
							try {
								blockHeads2.load(blockFile1162);
								log(Level.INFO, "Loading " + blockFile1162 + "...");
							} catch (IOException | InvalidConfigurationException e1) {
								mmh.stacktraceInfo();
								e1.printStackTrace();
							}
							
							boolean showlore = mmh.getConfig().getBoolean("lore.show_plugin_name", true);
							ArrayList<String> headlore = new ArrayList();
							headlore.add(ChatColor.AQUA + "" + mmh.getName());
							
							mmh.log(Level.INFO, "Loading PlayerHead Recipes...");
							for(int i=1; i < mmh.playerHeads.getInt("players.number") + 1; i++){
								ItemStack price1 = mmh.playerHeads.getItemStack("players.player_" + i + ".price_1", new ItemStack(Material.AIR));
								ItemStack price2 = mmh.playerHeads.getItemStack("players.player_" + i + ".price_2", new ItemStack(Material.AIR));
								ItemStack itemstack = mmh.playerHeads.getItemStack("players.player_" + i + ".itemstack", new ItemStack(Material.AIR));
								if(showlore) {
									SkullMeta meta = (SkullMeta)itemstack.getItemMeta();
									meta.setLore(headlore);
									itemstack.setItemMeta(meta);
									itemstack.setItemMeta(meta);
								}
								MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.playerHeads.getInt("players.player_" + i + ".quantity", (int) 3));
								recipe.addIngredient(price1);
								recipe.addIngredient(price2);
								playerhead_recipes.add(recipe);
							}
							mmh.log(Level.INFO, playerhead_recipes.size() + " PlayerHead Recipes ADDED...");
							mmh.log(Level.INFO, "Loading BlockHead Recipes...");
							BHNum = blockHeads.getInt("blocks.number");
							// BlockHeads
							mmh.log(Level.INFO, "BlockHeads=" + BHNum);
							for(int i=1; i < BHNum + 1; i++){
								ItemStack price1 = blockHeads.getItemStack("blocks.block_" + i + ".price_1", new ItemStack(Material.AIR));
								ItemStack price2 = blockHeads.getItemStack("blocks.block_" + i + ".price_2", new ItemStack(Material.AIR));
								ItemStack itemstack = blockHeads.getItemStack("blocks.block_" + i + ".itemstack", new ItemStack(Material.AIR));
								if(showlore) {
									SkullMeta meta = (SkullMeta)itemstack.getItemMeta();
									meta.setLore(headlore);
									itemstack.setItemMeta(meta);
									itemstack.setItemMeta(meta);
								}
								MerchantRecipe recipe = new MerchantRecipe(itemstack, blockHeads.getInt("blocks.block_" + i + ".quantity", (int) 1));
								recipe.setExperienceReward(true);
								recipe.addIngredient(price1);
								recipe.addIngredient(price2);
								blockhead_recipes.add(recipe);
							}
							BHNum2 = blockHeads2.getInt("blocks.number");
							// blockHeads 2
							mmh.log(Level.INFO, "BlockHeads2=" + BHNum2);
							for(int i=1; i < BHNum2 + 1; i++){
								ItemStack price1 = blockHeads2.getItemStack("blocks.block_" + i + ".price_1", new ItemStack(Material.AIR));
								ItemStack price2 = blockHeads2.getItemStack("blocks.block_" + i + ".price_2", new ItemStack(Material.AIR));
								ItemStack itemstack = blockHeads2.getItemStack("blocks.block_" + i + ".itemstack", new ItemStack(Material.AIR));
								if(showlore) {
									SkullMeta meta = (SkullMeta)itemstack.getItemMeta();
									meta.setLore(headlore);
									itemstack.setItemMeta(meta);
									itemstack.setItemMeta(meta);
								}
								MerchantRecipe recipe = new MerchantRecipe(itemstack, blockHeads2.getInt("blocks.block_" + i + ".quantity", (int) 1));
								recipe.setExperienceReward(true);
								recipe.addIngredient(price1);
								recipe.addIngredient(price2);
								blockhead_recipes.add(recipe);
							}
					
							mmh.log(Level.INFO, blockhead_recipes.size() + " BlockHead Recipes ADDED...");
							mmh.log(Level.INFO, "Loading CustomTrades Recipes...");
							for(int i=1; i < mmh.traderCustom.getInt("custom_trades.number") + 1; i++){
								ItemStack price1 = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".price_1", new ItemStack(Material.AIR));
								ItemStack price2 = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".price_2", new ItemStack(Material.AIR));
								ItemStack itemstack = mmh.traderCustom.getItemStack("custom_trades.trade_" + i + ".itemstack", new ItemStack(Material.AIR));
								MerchantRecipe recipe = new MerchantRecipe(itemstack, mmh.traderCustom.getInt("custom_trades.trade_" + i + ".quantity", (int) 1));
								recipe.setExperienceReward(true);
								recipe.addIngredient(price1);
								recipe.addIngredient(price2);
								custometrade_recipes.add(recipe);
							}
							mmh.log(Level.INFO, custometrade_recipes.size() + " CustomTrades Recipes ADDED...");
						}
						sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("reloaded"));
						return true;
					}else if(!hasPerm){
						sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("noperm").toString().replace("<perm>", perm) );
						return false;
					}
				}
				if(args[0].equalsIgnoreCase("toggledebug")||args[0].equalsIgnoreCase("td")){
					String perm = "moremobheads.toggledebug";
					boolean hasPerm = sender.hasPermission(perm);
					if(debug){logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);}
					if(sender.isOp()||hasPerm||!(sender instanceof Player)){
						debug = !debug;
						sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("debugtrue").toString().replace("boolean", "" + debug));
						return true;
					}else if(!hasPerm){
						sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("noperm").toString().replace("<perm>", perm) );
						return false;
					}
				}
				if(args[0].equalsIgnoreCase("customtrader")||args[0].equalsIgnoreCase("ct")){
					String perm = "moremobheads.customtrader";
					boolean hasPerm = sender.hasPermission(perm);
					if(debug){logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);}
					if(hasPerm&&sender instanceof Player
							&& mmh.getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)){
						log(Level.INFO, "has permission");
						Player player = (Player) sender;
						if(!(args.length >= 2)){
							sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName() + ChatColor.GREEN + "]===============[]");
							sender.sendMessage(ChatColor.WHITE + " ");
							sender.sendMessage(ChatColor.WHITE + " /mmh ct - " + mmh.lang.getString("cthelp"));
							sender.sendMessage(ChatColor.WHITE + " /mmh ct add - " + mmh.lang.getString("ctadd") + "custom_trades.yml");
							sender.sendMessage(ChatColor.WHITE + " /mmh ct remove # - " + mmh.lang.getString("ctremove"));
							sender.sendMessage(ChatColor.WHITE + " /mmh ct replace # - " + mmh.lang.getString("ctreplace").replace("<num>", "#"));
							sender.sendMessage(ChatColor.WHITE + " ");
							sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName() + ChatColor.GREEN + "]===============[]");
							return true;
						}else if(args[1].equalsIgnoreCase("add")){
							if(debug) {logDebug("CMD CT ADD Start -----");}
							ItemStack itemstack = player.getInventory().getItemInOffHand();
							ItemStack price1 = player.getInventory().getItem(0);
							ItemStack price2 = player.getInventory().getItem(1);
							if(price1 == null){price1 = new ItemStack(Material.AIR);}
							if(price2 == null){price2 = new ItemStack(Material.AIR);}
							//Material price1 = item1.getType();
							//Material price2 = item2.getType();
							
							if(itemstack.getType() == Material.AIR||price1 == null||price1.getType() == Material.AIR){
								log(Level.INFO, "error air");
								sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName() + ChatColor.GREEN + "]===============[]");
								sender.sendMessage(ChatColor.WHITE + " ");
								sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline1") + "custom_trades.yml");
								sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline2"));
								sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline3"));
								sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline4") + "/mmh ct add");
								sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline5") + "custom trade.");
								sender.sendMessage(ChatColor.WHITE + " ");
								sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName() + ChatColor.GREEN + "]===============[]");
								if(debug) {logDebug("CMD CT ADD End Error -----");}
								return false;
							}
							int tradeNumber = (int)mmh.traderCustom.get("custom_trades.number", 1);
							mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".price_1", price1);
							mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".price_2", price2);
							mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".itemstack", itemstack);
							mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".quantity", itemstack.getAmount());
							mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber + 1) + ".chance", 0.002);
							mmh.traderCustom.set("custom_trades.number", (tradeNumber + 1));
							if(debug) {logDebug("CMD CT ADD price1=" + price1.getType());}
							if(debug) {logDebug("CMD CT ADD price2=" + price2.getType());}
							if(debug) {logDebug("CMD CT ADD itemstack=" + itemstack.getType());}
							if(debug) {if(itemstack.getType() == Material.PLAYER_HEAD) {
									ItemMeta skullMeta = itemstack.getItemMeta(); 
									logDebug("CMD CT ADD IS DisplayName=" + skullMeta.getDisplayName());
									if(skullMeta.hasLore()) {
										logDebug("CMD CT ADD IS lore=" + String.join(",",skullMeta.getLore()));
									}
							}}
							if(debug) {logDebug("CMD CT ADD quantity=" + itemstack.getAmount());}
							if(debug) {logDebug("CMD CT ADD chance=0.002");}
							//log("customFile=" + customFile);
							try {
								mmh.traderCustom.save(mmh.customFile);
								mmh.traderCustom.load(mmh.customFile);
							} catch (IOException | InvalidConfigurationException e) {
								mmh.stacktraceInfo();
								e.printStackTrace();
							}
							if(debug) {logDebug("CMD CT ADD End -----");}
							sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " trade_" + (tradeNumber + 1) + " " + mmh.lang.get("ctsuccessadd"));
							return true;
						}else if(args[1].equalsIgnoreCase("remove")){
							if(debug) {logDebug("CMD CT Remove Start -----");}
							if(!(args.length >= 3)){
								sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("ctargument"));
								return false;
							}else{
								if(mmh.isInteger(args[2])){
									mmh.traderCustom.set("custom_trades.trade_" + args[2] + ".price_1", "");
									mmh.traderCustom.set("custom_trades.trade_" + args[2] + ".price_2", "");
									mmh.traderCustom.set("custom_trades.trade_" + args[2] + ".itemstack", "");
									mmh.traderCustom.set("custom_trades.trade_" + args[2] + ".quantity", "");
									mmh.traderCustom.set("custom_trades.trade_" + args[2] + ".chance", "");
									if(debug){logDebug("customFile=" + mmh.customFile);}
									try {
										mmh.traderCustom.save(mmh.customFile);
										mmh.traderCustom.load(mmh.customFile);
									} catch (IOException | InvalidConfigurationException e) {
										if(debug) {logDebug("CMD CT Remove End Exception -----");}
										sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("cterror"));
										return false;
										//e.printStackTrace();
									}
									if(debug) {logDebug("CMD CT Remove End -----");}
									sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " trade_" + args[2] + " " + mmh.lang.get("ctsuccessrem"));
									return true;
								}else{
									if(debug) {logDebug("CMD CT Remove End 2 -----");}
									sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("ctnumreq"));
									return false;
								}
							}
						}else if(args[1].equalsIgnoreCase("replace")){
							if(debug) {logDebug("CMD CT Replace Start -----");}
							if(args.length != 3){
								sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("ctargument"));
								return false;
							}else{
								if(mmh.isInteger(args[2])){
									ItemStack itemstack = player.getInventory().getItemInOffHand();
									ItemStack price1 = player.getInventory().getItem(0);
									ItemStack price2 = player.getInventory().getItem(1);
									if(price1 == null){price1 = new ItemStack(Material.AIR);}
									if(price2 == null){price2 = new ItemStack(Material.AIR);}
									if(itemstack.getType() == Material.AIR||price1 == null||price1.getType() == Material.AIR){
										sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName() + ChatColor.GREEN + "]===============[]");
										sender.sendMessage(ChatColor.WHITE + " ");
										sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline1") + "custom_trades.yml");
										sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline2"));
										sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline3"));
										sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline4") + "/mmh ct add");
										sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline5") + "custom trade.");
										sender.sendMessage(ChatColor.WHITE + " ");
										sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName() + ChatColor.GREEN + "]===============[]");
										if(debug) {logDebug("CMD CT Replace End Error -----");}
										return false;
									}
									int tradeNumber = Integer.parseInt(args[2]);
									mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".price_1", price1);
									mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".price_2", price2);
									mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".itemstack", itemstack);
									mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".quantity", itemstack.getAmount());
									mmh.traderCustom.set("custom_trades.trade_" + (tradeNumber) + ".chance", 0.002);
									if(debug) {logDebug("CMD CT Replace price1=" + price1.getType());}
									if(debug) {logDebug("CMD CT Replace price2=" + price2.getType());}
									if(debug) {logDebug("CMD CT Replace itemstack=" + itemstack.getType());}
									if(debug) {if(itemstack.getType() == Material.PLAYER_HEAD) {
										ItemMeta skullMeta = itemstack.getItemMeta(); 
										logDebug("CMD CT Replace IS DisplayName=" + skullMeta.getDisplayName());
										if(skullMeta.hasLore()) {
											logDebug("CMD CT Replace IS lore=" + String.join(",",skullMeta.getLore()));
										}
									}}
									if(debug) {logDebug("CMD CT Replace quantity=" + itemstack.getAmount());}
									if(debug) {logDebug("CMD CT Replace chance=0.002");}
									
									//log("customFile=" + customFile);
									try {
										mmh.traderCustom.save(mmh.customFile);
										mmh.traderCustom.load(mmh.customFile);
									} catch (IOException | InvalidConfigurationException e) {
										if(debug) {logDebug("CMD CT Replace End Exception -----");}
										sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("cterror"));
										return false;
										//e.printStackTrace();
									}
									if(debug) {logDebug("CMD CT Replace End -----");}
									sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " trade_" + args[2] + " " + mmh.lang.get("ctsuccessrep"));
									return true;
								}else{
									if(debug) {logDebug("CMD CT Replace End 2 -----");}
									sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("ctnumreq"));
									return false;
								}
							}
						}
					}else if(!(sender instanceof Player)){
						if(debug) {logDebug("CMD CT Replace End Console -----");}
						sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("noconsole"));
						return false;
					}else if(!hasPerm){
						if(debug) {logDebug("CMD CT Replace End !Perm -----");}
						sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("nopermordisabled").toString().replace("<perm>", perm) );
					return false;
					}
				}
				if(args[0].equalsIgnoreCase("playerheads")||args[0].equalsIgnoreCase("ph")){
					String perm = "moremobheads.playerheads";
					boolean hasPerm = sender.hasPermission(perm);
					if(debug){logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);}
					if(hasPerm&&sender instanceof Player
							&& mmh.getConfig().getBoolean("wandering_trades.custom_wandering_trader", true)){
						Player player = (Player) sender;
						if(!(args.length >= 2)){
							sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName() + ChatColor.GREEN + "]===============[]");
							sender.sendMessage(ChatColor.WHITE + " ");
							sender.sendMessage(ChatColor.WHITE + " /mmh ph - " + mmh.lang.getString("cthelp"));
							sender.sendMessage(ChatColor.WHITE + " /mmh ph add - " + mmh.lang.getString("ctadd") + "player_heads.yml");
							sender.sendMessage(ChatColor.WHITE + " /mmh ph remove # - " + mmh.lang.getString("ctremove"));
							sender.sendMessage(ChatColor.WHITE + " /mmh ph replace # - " + mmh.lang.getString("ctreplace").replace("<num>", "#"));
							//sender.sendMessage(ChatColor.WHITE + " ");
							sender.sendMessage(ChatColor.WHITE + " ");
							sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName() + ChatColor.GREEN + "]===============[]");
							return true;
						}else if(args[1].equalsIgnoreCase("add")){
							if(debug) {logDebug("CMD PH ADD Start -----");}
							ItemStack itemstack = player.getInventory().getItemInOffHand();
							ItemStack price1 = player.getInventory().getItem(0);
							ItemStack price2 = player.getInventory().getItem(1);
							if(price1 == null){price1 = new ItemStack(Material.AIR);}
							if(price2 == null){price2 = new ItemStack(Material.AIR);}
							//Material price1 = item1.getType();
							//Material price2 = item2.getType();
							
							if(itemstack.getType() == Material.AIR||price1 == null||price1.getType() == Material.AIR||itemstack.getType() != Material.PLAYER_HEAD){
								sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName() + ChatColor.GREEN + "]===============[]");
								sender.sendMessage(ChatColor.WHITE + " ");
								if(itemstack.getType() != Material.PLAYER_HEAD){
									sender.sendMessage(ChatColor.RED + " MUST BE PLAYERHEAD");
									sender.sendMessage(ChatColor.WHITE + " ");
								}
								sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline1") + "player_heads.yml");
								sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline2"));
								sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline3"));
								sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline4") + "/mmh ph add");
								sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline5") + "player head.");
								sender.sendMessage(ChatColor.WHITE + " ");
								sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName() + ChatColor.GREEN + "]===============[]");
								if(debug) {logDebug("CMD PH ADD End Error -----");}
								return false;
							}
							int tradeNumber = (int) mmh.playerHeads.get("players.number", 1);
							mmh.playerHeads.set("players.player_" + (tradeNumber + 1) + ".price_1", price1);
							mmh.playerHeads.set("players.player_" + (tradeNumber + 1) + ".price_2", price2);
							mmh.playerHeads.set("players.player_" + (tradeNumber + 1) + ".itemstack", itemstack);
							mmh.playerHeads.set("players.player_" + (tradeNumber + 1) + ".quantity", itemstack.getAmount());
							if(debug) {logDebug("CMD PH ADD price1=" + price1.getType());}
							if(debug) {logDebug("CMD PH ADD price2=" + price2.getType());}
							if(debug) {logDebug("CMD PH ADD itemstack=" + itemstack.getType());}
							if(debug) {if(itemstack.getType() == Material.PLAYER_HEAD) {
								ItemMeta skullMeta = itemstack.getItemMeta(); 
								logDebug("CMD PH ADD IS DisplayName=" + skullMeta.getDisplayName());
								if(skullMeta.hasLore()) {
									logDebug("CMD PH ADD IS lore=" + String.join(",",skullMeta.getLore()));
								}
							}}
							if(debug) {logDebug("CMD PH ADD quantity=" + itemstack.getAmount());}
							//playerHeads.set("players.player_" + (tradeNumber + 1) + ".chance", 0.002);
							mmh.playerHeads.set("players.number", (tradeNumber + 1));
							//log("customFile=" + customFile);
							try {
								mmh.playerHeads.save(mmh.playerFile);
								mmh.playerHeads.load(mmh.playerFile);
							} catch (IOException | InvalidConfigurationException e) {
								if(debug) {logDebug("CMD PH ADD End Exception -----");}
								mmh.stacktraceInfo();
								e.printStackTrace();
							}
							if(debug) {logDebug("CMD PH ADD End -----");}
							sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " player_" + (tradeNumber + 1) + " " + mmh.lang.get("ctsuccessadd"));
							return true;
						}else if(args[1].equalsIgnoreCase("remove")){
							if(debug) {logDebug("CMD PH Remove Start -----");}
							if(!(args.length >= 3)){
								sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("ctargument"));
								return false;
							}else{
								if(mmh.isInteger(args[2])){
									mmh.playerHeads.set("players.player_" + args[2] + ".price_1", "");
									mmh.playerHeads.set("players.player_" + args[2] + ".price_2", "");
									mmh.playerHeads.set("players.player_" + args[2] + ".itemstack", "");
									mmh.playerHeads.set("players.player_" + args[2] + ".quantity", "");
									//playerHeads.set("custom_trades.trade_" + args[2] + ".chance", "");
									if(debug){logDebug("playerFile=" + mmh.playerFile);}
									try {
										mmh.playerHeads.save(mmh.playerFile);
										mmh.playerHeads.load(mmh.playerFile);
									} catch (IOException | InvalidConfigurationException e) {
										if(debug) {logDebug("CMD PH Remove End Exception -----");}
										sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("cterror") + "custom_trades.yml!");
										return false;
										//e.printStackTrace();
									}
									if(debug) {logDebug("CMD PH Remove End -----");}
									sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " player_" + args[2] + " " + mmh.lang.get("ctsuccessrem"));
									return true;
								}else{
									if(debug) {logDebug("CMD PH Remove End 2 -----");}
									sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("ctnumreq"));
									return false;
								}
							}
						}else if(args[1].equalsIgnoreCase("replace")){
							if(debug) {logDebug("CMD PH Replace Start -----");}
							if(args.length != 3){
								sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("ctargument"));
								return false;
							}else{
								if(mmh.isInteger(args[2])){
									ItemStack itemstack = player.getInventory().getItemInOffHand();
									ItemStack price1 = player.getInventory().getItem(0);
									ItemStack price2 = player.getInventory().getItem(1);
									if(price1 == null){price1 = new ItemStack(Material.AIR);}
									if(price2 == null){price2 = new ItemStack(Material.AIR);}
									if(itemstack.getType() == Material.AIR||price1 == null||price1.getType() == Material.AIR||itemstack.getType() != Material.PLAYER_HEAD){
										sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName() + ChatColor.GREEN + "]===============[]");
										sender.sendMessage(ChatColor.WHITE + " ");
										if(itemstack.getType() != Material.PLAYER_HEAD){
											sender.sendMessage(ChatColor.RED + " MUST BE PLAYERHEAD");
											sender.sendMessage(ChatColor.WHITE + " ");
										}
										sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline1") + "player_heads.yml");
										sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline2"));
										sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline3"));
										sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline4") + "/mmh ph add");
										sender.sendMessage(ChatColor.WHITE + " " + mmh.lang.getString("ctline5") + "player head.");
										sender.sendMessage(ChatColor.WHITE + " ");
										sender.sendMessage(ChatColor.GREEN + "[]===============[" + ChatColor.YELLOW + mmh.getName() + ChatColor.GREEN + "]===============[]");
										if(debug) {logDebug("CMD PH Replace End Error -----");}
										return false;
									}
									int tradeNumber = Integer.parseInt(args[2]);
									mmh.playerHeads.set("players.player_" + (tradeNumber) + ".price_1", price1);
									mmh.playerHeads.set("players.player_" + (tradeNumber) + ".price_2", price2);
									mmh.playerHeads.set("players.player_" + (tradeNumber) + ".itemstack", itemstack);
									mmh.playerHeads.set("players.player_" + (tradeNumber) + ".quantity", itemstack.getAmount());
									if(debug) {logDebug("CMD PH Replace price1=" + price1.getType());}
									if(debug) {logDebug("CMD PH Replace price2=" + price2.getType());}
									if(debug) {logDebug("CMD PH Replace itemstack=" + itemstack.getType());}
									if(debug) {if(itemstack.getType() == Material.PLAYER_HEAD) {
										ItemMeta skullMeta = itemstack.getItemMeta(); 
										logDebug("CMD PH Replace IS DisplayName=" + skullMeta.getDisplayName());
										if(skullMeta.hasLore()) {
											logDebug("CMD PH Replace IS lore=" + String.join(",",skullMeta.getLore()));
										}
									}}
									if(debug) {logDebug("CMD PH Replace quantity=" + itemstack.getAmount());}
									//playerHeads.set("players.player_" + (tradeNumber + 1) + ".chance", 0.002);
									//log("customFile=" + customFile);
									try {
										mmh.playerHeads.save(mmh.playerFile);
										mmh.playerHeads.load(mmh.playerFile);
									} catch (IOException | InvalidConfigurationException e) {
										if(debug) {logDebug("CMD PH Replace End Exception -----");}
										sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("cterror") + "player_heads.yml!");
										return false;
										//e.printStackTrace();
									}
									if(debug) {logDebug("CMD PH Replace End -----");}
									sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.WHITE + " player_" + args[2] + " " + mmh.lang.get("ctsuccessrep"));
									return true;
								}else{
									if(debug) {logDebug("CMD PH Replace End 2 -----");}
									sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("ctnumreq"));
									return false;
								}
							}
						}
					}else if(!(sender instanceof Player)){
						if(debug) {logDebug("CMD PH Replace End Console -----");}
						sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("noconsole"));
						return false;
					}else if(!hasPerm){
						if(debug) {logDebug("CMD PH Replace End !Perm -----");}
						sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("nopermordisabled").toString().replace("<perm>", perm) );
						return false;
					}
				}
				if(args[0].equalsIgnoreCase("fixhead")||args[0].equalsIgnoreCase("fh")){
					String perm = "moremobheads.fixhead";
					boolean hasPerm = sender.hasPermission(perm);
					if(debug){logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);}
					if(sender instanceof Player) {
						Player player = (Player) sender;
						if(hasPerm) {
							if(!args[1].isEmpty()) {
								if(args[1].equalsIgnoreCase("name")) {
									if(debug) {logDebug("CMD FH Name Start -----");}
									// FixHead NBT
									ItemStack mainHand = player.getInventory().getItemInMainHand();
									if( mainHand != null ){
										if( mainHand.getType().equals(Material.PLAYER_HEAD) ) {
											String texture = mainHand.getItemMeta().getDisplayName();
											
											SkullMeta skullname = (SkullMeta) mainHand.getItemMeta();
											if(skullname.getOwner() != null){
												String name = skullname.getOwner().toString();
												if(debug){logDebug("EPIE name=" + name);}
												if(debug){logDebug("EPIE lore=" + skullname.getLore());}
												if(skullname.getOwner().toString().length() >= 40){
													if(debug){logDebug("EPIE ownerName.lngth >= 40");}
														ItemStack itmStack = mainHand;
														//SkullMeta skullname = (SkullMeta) e.getItem().getItemStack().getItemMeta();
														String daMobName = "null";
														if(skullname != null){
															String isCat = CatHeads.getNameFromTexture(skullname.getOwner().toString());
															String isHorse = HorseHeads.getNameFromTexture(skullname.getOwner().toString());
															String isLlama = LlamaHeads.getNameFromTexture(skullname.getOwner().toString());
															String isMobHead = MobHeads.getNameFromTexture(skullname.getOwner().toString());
															String isRabbit = RabbitHeads.getNameFromTexture(skullname.getOwner().toString());
															String isSheep = SheepHeads.getNameFromTexture(skullname.getOwner().toString());
															String isVillager = VillagerHeads.getNameFromTexture(skullname.getOwner().toString());
															String isZombieVillager = ZombieVillagerHeads.getNameFromTexture(skullname.getOwner().toString());
															String isplayerhead = mmh.isPlayerHead(skullname.getOwner().toString());
															String isblockhead = mmh.isBlockHead(skullname.getOwner().toString());
															String isblockhead2 = mmh.isBlockHead2(skullname.getOwner().toString());
															String isblockhead3 = mmh.isBlockHead3(skullname.getOwner().toString());
															if(isCat != null){				daMobName = isCat;	}
															if(isHorse != null){			daMobName = isHorse;	}
															if(isLlama != null){			daMobName = isLlama;	}
															if(isMobHead != null){			daMobName = isMobHead;	}
															if(isRabbit != null){			daMobName = isRabbit;	}
															if(isSheep != null){			daMobName = isSheep;	}
															if(isVillager != null){			daMobName = isVillager;	}
															if(isZombieVillager != null){	daMobName = isZombieVillager;	}
															if(daMobName == null){
																if(blockHeads != null){
																	if(isblockhead != null){	daMobName = isblockhead;	}
																}
																if(blockHeads2 != null){
																	if(isblockhead2 != null){	daMobName = isblockhead2;	}
																}
																if(blockHeads3 != null){
																	if(isblockhead3 != null){	daMobName = isblockhead3;	}
																}
																if(mmh.playerHeads != null){
																	if(isplayerhead != null){	daMobName = isplayerhead;	}
																}
															}
															ArrayList<String> lore = new ArrayList();
															//log("" + meta.getOwner().toString());
															//String name = LlamaHeads.getNameFromTexture(meta.getOwner().toString());
															if(debug){logDebug("EPIE mobname from texture=" + daMobName);}
															List<String> skullLore = skullname.getLore();
															if(skullLore != null){
																if(skullLore.toString().contains("Killed by")){
																	lore.addAll(skullname.getLore());
																}
															}
															if(skullLore == null||!skullname.getLore().toString().contains(mmh.getName())){
																if(mmh.getConfig().getBoolean("lore.show_plugin_name", true)){
																	lore.add(ChatColor.AQUA + "" + mmh.getName());
																}
															}
															if(daMobName != "null"){
																daMobName = mmh.langName.getString(daMobName.toLowerCase().replace(" ", "."), daMobName);
															}else{
																daMobName = mmh.langName.getString(daMobName.toLowerCase().replace(" ", "."), "404 Name Not Found");
															}
															skullname.setLore(lore);
															skullname.setDisplayName(daMobName);
															itmStack.setItemMeta(skullname);
															//fixHeadNBT(skullname.getOwner(), daMobName, lore);
															if(debug) {logDebug("CMD FH Name End -----");}
															sender.sendMessage("DisplayName of head in your main hand has been fixed.");
															//if(debug){logDebug("test3a");}
															return true;
														}else{
															if(debug) {logDebug("CMD FH Name End Meta Null -----");}
															return false;
													}
												}
											}
										}else {
											if(debug) {logDebug("CMD FH Name End Error -----");}
											sender.sendMessage("An Error occured.");
											return false;
										}
									}
								}
								
								if(args[1].equalsIgnoreCase("stack")) {
									if(debug) {logDebug("CMD FH Stack Start -----");}
									// FixHead Stack
									ItemStack mainHand = player.getInventory().getItemInMainHand();
									ItemStack offHand = player.getInventory().getItemInOffHand();
									if( mainHand != null && offHand != null ){
										if( mainHand.getType().equals(Material.PLAYER_HEAD) && offHand.getType().equals(Material.PLAYER_HEAD) ) {
											ItemStack is = mmh.fixHeadStack(offHand, mainHand);
											//is.setAmount(mainHand.getAmount());
											if(is != mainHand) {
												player.getInventory().setItemInMainHand(is);
												if(debug) {logDebug("CMD FH Stack End -----");}
												sender.sendMessage("NBT data of off hand head has been copied to the head in your main hand");
												return true;
											}else {
												if(debug) {logDebug("CMD FH Stack End Error -----");}
												sender.sendMessage("An Error occured. See plugins/MoreMobHeads/logs/mmh_debug.log for details");
												return false;
											}
										}else if( !mainHand.getType().equals(Material.PLAYER_HEAD) && !offHand.getType().equals(Material.PLAYER_HEAD) ){
											if(debug) {logDebug("CMD FH Stack End Error Main Off -----");}
											sender.sendMessage("Items in Main hand, and Off hand are not Player_Head.");
											return false;
										}else if( !mainHand.getType().equals(Material.PLAYER_HEAD) && offHand.getType().equals(Material.PLAYER_HEAD) ){
											if(debug) {logDebug("CMD FH Stack End Error Main -----");}
											sender.sendMessage("Item in Main hand is not a Player_Head.");
											return false;
										}else if( mainHand.getType().equals(Material.PLAYER_HEAD) && !offHand.getType().equals(Material.PLAYER_HEAD) ){
											if(debug) {logDebug("CMD FH Stack End Error Off -----");}
											sender.sendMessage("Item in Off hand is not a Player_Head.");
											return false;
										}
									}
								}
							}
							
						}else if(!hasPerm){
							if(debug) {logDebug("CMD FH Stack End !Perm -----");}
							sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("noperm").toString().replace("<perm>", perm) );
							return false;
						}
					}
				}
				if(args[0].equalsIgnoreCase("giveMH")){
					// /mmh giveMH player mob qty
					// cmd  0      1      2   3
					if( args.length==4 ){
						String perm = "moremobheads.give";
						boolean hasPerm = sender.hasPermission(perm);
						if(debug){logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);}
						if(hasPerm) {
							Player player = Bukkit.getPlayer(args[1]);
							if(!args[2].isEmpty()) {
								String mob = args[2].toLowerCase();
								log(Level.INFO, "mob=" + mob);
								if(!args[3].isEmpty()) {
									int number = Integer.parseInt(args[3]);
									String[] splitmob = mob.split("\\.");
									switch (splitmob[0]) {
									case "creeper":
										if(mmh.getConfig().getBoolean("vanilla_heads.creeper", false)){
						 					player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.CREEPER_HEAD));
						 				}else{ // mmh.langName
						 					player.getWorld().dropItemNaturally(player.getLocation(), mmh.makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture().toString(), 
						 							mmh.langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number ));
						 				} // MobHeads.valueOf(name).getName() + " Head"
										break;
									case "zombie":
										if(mmh.getConfig().getBoolean("vanilla_heads.zombie", false)){
						 					player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.ZOMBIE_HEAD));
						 				}else{ // mmh.langName
						 					player.getWorld().dropItemNaturally(player.getLocation(), mmh.makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture().toString(), 
						 							mmh.langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number ));
						 				} // MobHeads.valueOf(name).getName() + " Head"
										break;
									case "skeleton":
										if(mmh.getConfig().getBoolean("vanilla_heads.skeleton", false)){
						 					player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.SKELETON_SKULL));
						 				}else{ // mmh.langName
						 					player.getWorld().dropItemNaturally(player.getLocation(), mmh.makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture().toString(), 
						 							mmh.langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number ));
						 				} // MobHeads.valueOf(name).getName() + " Head"
										break;
									case "wither_skeleton":
										if(mmh.getConfig().getBoolean("vanilla_heads.wither_skeleton", false)){
						 					player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.WITHER_SKELETON_SKULL));
						 				}else{ // mmh.langName
						 					player.getWorld().dropItemNaturally(player.getLocation(), mmh.makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture().toString(), 
						 							mmh.langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number ));
						 				} // MobHeads.valueOf(name).getName() + " Head"
										break;
									case "ender_dragon":
										if(mmh.getConfig().getBoolean("vanilla_heads.ender_dragon", false)){
						 					player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.DRAGON_HEAD));
						 				}else{ // mmh.langName
						 					player.getWorld().dropItemNaturally(player.getLocation(), mmh.makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture().toString(), 
						 							mmh.langName.getString(splitmob[0].toLowerCase(), MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number ));
						 				} // MobHeads.valueOf(name).getName() + " Head"
										break;
									case "cat":
										player.getWorld().dropItemNaturally(player.getLocation(),
					 							mmh.makeSkulls(CatHeads.valueOf(splitmob[1].toUpperCase()).getTexture().toString(), 
					 									mmh.langName.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), CatHeads.valueOf(splitmob[1].toUpperCase()).getName() + " Head"), number ));
										break;
									case "bee":
										log(Level.INFO, "splitmob.length=" + splitmob.length);
										if(splitmob.length == 1) {
											player.getWorld().dropItemNaturally(player.getLocation(), mmh.makeSkulls(MobHeads.valueOf(splitmob[0].toUpperCase()).getTexture().toString(), 
						 							mmh.langName.getString(splitmob[0].toLowerCase() + ".none", MobHeads.valueOf(splitmob[0].toUpperCase()).getName() + " Head"), number ));
										}else {
											player.getWorld().dropItemNaturally(player.getLocation(), mmh.makeSkulls( MobHeads.valueOf( mob.toUpperCase().replace(".", "_") ).getTexture().toString(), 
						 							mmh.langName.getString(mob.toLowerCase().replace(".", "_"), MobHeads.valueOf(mob.toUpperCase().replace(".", "_")).getName() + " Head"), number ));
										}
										break;
									case "villager": // villager type profession, villager profession type
										// name = splitmob[0], type =  splitmob[1], profession = splitmob[2]
										player.getWorld().dropItemNaturally(player.getLocation(), mmh.makeSkulls(VillagerHeads.valueOf( splitmob[0].toUpperCase() + "_" + splitmob[2].toUpperCase() + "_" + splitmob[1].toUpperCase() ).getTexture().toString(), 
					 							mmh.langName.getString( splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase() + "." + splitmob[2].toLowerCase()
					 									, VillagerHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[2].toUpperCase() + "_" + splitmob[1].toUpperCase()).getName() + " Head"), number ));
										break;
									case "zombie_villager":
										player.getWorld().dropItemNaturally(player.getLocation(), mmh.makeSkulls(ZombieVillagerHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[2].toUpperCase() ).getTexture().toString(), 
					 							mmh.langName.getString(splitmob[0].toLowerCase() + "." + splitmob[2].toLowerCase(), ZombieVillagerHeads.valueOf(splitmob[0].toUpperCase() + "_" + splitmob[2].toUpperCase() ).getName() ), number ));
										break;
									case "llama":
									case "trader_llama":
										player.getWorld().dropItemNaturally(player.getLocation(), mmh.makeSkulls(LlamaHeads.valueOf( splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase() ).getTexture().toString(), 
					 							mmh.langName.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), LlamaHeads.valueOf( splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase() ).getName()), number ));
										break;
									case "horse":
										player.getWorld().dropItemNaturally(player.getLocation(), mmh.makeSkulls(HorseHeads.valueOf( splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase() ).getTexture().toString(), 
					 							mmh.langName.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), HorseHeads.valueOf( splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase() ).getName()), number ));
										break;
									case "rabbit":
										player.getWorld().dropItemNaturally(player.getLocation(), mmh.makeSkulls(RabbitHeads.valueOf( splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase() ).getTexture().toString(), 
					 							mmh.langName.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), RabbitHeads.valueOf( splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase() ).getName()), number ));
										break;
									case "sheep":
										String sheeptype;
										if(splitmob[1].equalsIgnoreCase("jeb_")) {
											sheeptype = "jeb_";
										}else {
											sheeptype = splitmob[1].toUpperCase();
										}
										player.getWorld().dropItemNaturally(player.getLocation(), mmh.makeSkulls(SheepHeads.valueOf( splitmob[0].toUpperCase() + "_" + sheeptype ).getTexture().toString(), 
					 							mmh.langName.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), SheepHeads.valueOf( splitmob[0].toUpperCase() + "_" + sheeptype ).getName()), number ));
										break;
									case "goat":
									case "axolotl":
									case "glow_squid":
										//player.getWorld().dropItemNaturally(player.getLocation(), mmh.makeSkulls(MobHeads117.valueOf( splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase() ).getTexture().toString(), 
					 					//		mmh.langName.getString(splitmob[0].toLowerCase() + "." + splitmob[1].toLowerCase(), MobHeads117.valueOf( splitmob[0].toUpperCase() + "_" + splitmob[1].toUpperCase() ).getName()), number ));
										break;
									default:
										player.getWorld().dropItemNaturally(player.getLocation(), mmh.makeSkulls(MobHeads.valueOf(mob.toUpperCase().replace(".", "_")).getTexture().toString(), 
					 							mmh.langName.getString(mob.toLowerCase(), MobHeads.valueOf(mob.toUpperCase().replace(".", "_")).getName() + " Head"), number ));
										break;
									}
								}
							}
						}else if(!hasPerm){
							sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("noperm").toString().replace("<perm>", perm) );
							return false;
						}
					}else {
						sender.sendMessage("Command usage, /mmh givemh playername mobname 1");
						return false;
					}
					
				}
				// /mmh giveph player
				// /mmh giveph player player
				//  0   1      2      3
				if(args[0].equalsIgnoreCase("givePH")){
					if( args.length >= 2 ){
						String perm = "moremobheads.give";
						boolean hasPerm = sender.hasPermission(perm);
						if(debug){logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);}
						if( hasPerm ){
							if(debug) {logDebug("CMD GPH args.length=" + args.length);}
							if( args.length==2 && sender instanceof Player ){
								mmh.givePlayerHead((Player) sender,args[1]);
								if(debug) {logDebug("CMD GPH args1=" + args[1]);}
								return true;
							}else if( args.length==3){
								Player player = Bukkit.getPlayer(args[1]);
								mmh.givePlayerHead(player,args[2]);
								if(debug) {logDebug("CMD GPH args1=" + args[1] + ", args2=" +args[2]);}
								return true;
							}else if( args.length==2 && !(sender instanceof Player) ){
								sender.sendMessage("Console cannot give itself Heads. Command usage, \"/mmh giveph playername 1\" or \"/mmh giveph playername playername 1\"");
								return false;
							}
						}else if( !hasPerm ){
							sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("noperm").toString().replace("<perm>", perm) );
							return false;
						}
					}else {
						sender.sendMessage("Command usage, \"/mmh giveph playername 1\" or \"/mmh giveph playername playername 1\"");
						return false;
					}
					return false;
				}
				// /mmh givebh block
				// /mmh givebh player block
				//  0   1      2      3
				if(args[0].equalsIgnoreCase("giveBH")){
					if(debug){logDebug("Start GiveBH");}
					if(debug){logDebug("Command=" + cmd.getName() + ", arguments=" + Arrays.toString(args));}
					if( args.length >= 2 ){
						String perm = "moremobheads.give";
						boolean hasPerm = sender.hasPermission(perm);
						if(debug){logDebug(sender.getName() + " has the permission " + perm + "=" + hasPerm);}
						if( hasPerm ){
							if(debug) {logDebug("CMD GBH args.length=" + args.length);}
							if( args.length==2 && sender instanceof Player ){
								mmh.giveBlockHead((Player) sender,args[1].replace("_", " "));
								if(debug) {logDebug("CMD GBH args1=" + args[1]);}
								if(debug){logDebug("End GiveBH True 1");}
								return true;
							}else if( args.length==3){
								Player player = Bukkit.getPlayer(args[1]);
								mmh.giveBlockHead(player,args[2].replace("_", " "));
								if(debug) {logDebug("CMD GBH args1=" + args[1] + ", args2=" +args[2]);}
								if(debug){logDebug("End GiveBH True 2");}
								return true;
							}else if( args.length==2 && !(sender instanceof Player) ){
								sender.sendMessage("Console cannot give itself Heads. /mmh giveBh <player> <block>");
								if(debug){logDebug("End GiveBH False 1");}
								return false;
							}
						}else if( !hasPerm ){
							sender.sendMessage(ChatColor.YELLOW + mmh.getName() + ChatColor.RED + " " + mmh.lang.get("noperm").toString().replace("<perm>", perm) );
							if(debug){logDebug("End GiveBH False 2");}
							return false;
						}
					}else {
						sender.sendMessage("Command usage, \"/mmh givebh block\" or \"/mmh giveph playername block\"");
						if(debug){logDebug("End GiveBH False 3");}
						return false;
					}
					if(debug){logDebug("End GiveBH False 4");}
					return false;
				}
			}
			return false;
	}
	
    @SuppressWarnings("static-access")
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { // TODO: Tab Complete
		if (command.getName().equalsIgnoreCase("mmh")) {
			List<String> autoCompletes = new ArrayList<>(); //create a new string list for tab completion
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
			if(args.length > 1) {
				if( args[0].equalsIgnoreCase("display") && args[1].isEmpty() ) {
					autoCompletes.add("permissions");
					autoCompletes.add("variables");
					return autoCompletes; // then return the list
				}else if( args[0].equalsIgnoreCase("display") && args[1].equalsIgnoreCase("permissions") ) {
					if( args[1].equalsIgnoreCase("permissions") ) {
						return null;
					}
				}
				if( args[0].equalsIgnoreCase("fixhead") || args[0].equalsIgnoreCase("fh") && args[1].isEmpty() ) {
					autoCompletes.add("name");
					autoCompletes.add("stack");
					return autoCompletes; // then return the list
				}
				if( args[0].equalsIgnoreCase("playerheads") || args[0].equalsIgnoreCase("ph") && args[1].isEmpty() ) {
					autoCompletes.add("add");
					autoCompletes.add("remove");
					autoCompletes.add("replace");
					return autoCompletes; // then return the list
				}else if( (args[0].equalsIgnoreCase("playerheads") || args[0].equalsIgnoreCase("ph")) && (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("replace")) ) {
					if( args[1].equalsIgnoreCase("remove") ) {
						autoCompletes.add("0");
						return autoCompletes; // then return the list
					}
					if( args[1].equalsIgnoreCase("replace") ) {
						autoCompletes.add("0");
						return autoCompletes; // then return the list
					}
				}
				if( args[0].equalsIgnoreCase("customtrader") || args[0].equalsIgnoreCase("ct") && args[1].isEmpty() ) {
					autoCompletes.add("add");
					autoCompletes.add("remove");
					autoCompletes.add("replace");
					return autoCompletes; // then return the list
				}else if( (args[0].equalsIgnoreCase("customtrader") || args[0].equalsIgnoreCase("ct")) && (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("replace")) ) {
					if( args[1].equalsIgnoreCase("remove") ) {
						autoCompletes.add("0");
						return autoCompletes; // then return the list
					}
					if( args[1].equalsIgnoreCase("replace") ) {
						autoCompletes.add("0");
						return autoCompletes; // then return the list
					}
				}
				if( args[0].equalsIgnoreCase("givebh") ) {
					if(  args.length < 2 ) {
						// /mmh giveph block
						// /mmh giveph @p block
						// /cmd 0      1  2
						// return null to list all players.
						return null;
					}
					if(  args.length > 2 ) {
						for(int i = 1; i < blockHeads.getInt("blocks.number"); ++i) {
							ItemStack stack = blockHeads.getItemStack("blocks.block_" + i + ".itemstack");
							String name = stack.getItemMeta().getDisplayName().replace(" ", "_");
							autoCompletes.add( ChatColor.stripColor( name ) );
						}
						if(Double.parseDouble(StrUtils.Left(mmh.getMCVersion(), 4)) >= 1.16) {
							for(int i = 1; i < blockHeads2.getInt("blocks.number"); ++i) {
								ItemStack stack = blockHeads2.getItemStack("blocks.block_" + i + ".itemstack");
								String name = stack.getItemMeta().getDisplayName().replace(" ", "_");
								autoCompletes.add( ChatColor.stripColor( name ) );
							}
						}
						return autoCompletes;
					}
				}
				if( args[0].equalsIgnoreCase("giveph") ) {
					//return null;
					if(  args.length < 2 ) { 
						// /mmh giveph @p @P
						// /cmd 0      1  2
						// return null to list all players.
						return null;
					}
					if(  args.length == 2 ) { 
						// /mmh giveph @p @P
						// /cmd 0      1  2
						// return null to list all players.
						return null;
					}
					
				}
				if( args[0].equalsIgnoreCase("givemh") ) {
					if(  args.length < 2 ) { 
						// /mmh give @p
						// /cmd 0    1
						// return null to list all players.
						return null;
					}else if(  args.length > 2 ) {
						if(debug) {mmh.logDebug("TC arg1!null args.length=" + args.length);}
						if( args.length == 3 ) {
						
							// /mmh give @p moblist #
							// /cmd 0    1  2       3
						    for(String key : chanceConfig.getConfigurationSection("chance_percent").getKeys(true)) {
						        //System.out.println(key);
						    	if(key.contains("axolotl")||key.contains("goat")||key.contains("glow")) {
						    		continue;
						    	}
						        autoCompletes.add(key);
						        //System.out.println(key);
						        if(key.equalsIgnoreCase("wolf")) {
						    		autoCompletes.add("wolf.angry");
						    	}else if(key.equalsIgnoreCase("wither")) {
						    		autoCompletes.add("wither.1");
						    		autoCompletes.add("wither.2");
						    		autoCompletes.add("wither.3");
						    		autoCompletes.add("wither.4");
						    		autoCompletes.remove(autoCompletes.indexOf("wither"));
						    	}else if(key.equalsIgnoreCase("zombie_villager")) {
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
						    	}
						    }
						    //autoCompletes.remove(autoCompletes.indexOf("axolotl"));
						    autoCompletes.remove(autoCompletes.indexOf("bee.chance_percent"));
						    autoCompletes.remove(autoCompletes.indexOf("cat"));
						    autoCompletes.remove(autoCompletes.indexOf("fox"));
						    //autoCompletes.remove(autoCompletes.indexOf("goat"));
						    autoCompletes.remove(autoCompletes.indexOf("horse"));
						    autoCompletes.remove(autoCompletes.indexOf("llama"));
						    autoCompletes.remove(autoCompletes.indexOf("panda"));
						    autoCompletes.remove(autoCompletes.indexOf("parrot"));
						    autoCompletes.remove(autoCompletes.indexOf("rabbit"));
						    autoCompletes.remove(autoCompletes.indexOf("sheep"));
						    autoCompletes.remove(autoCompletes.indexOf("trader_llama"));
						    autoCompletes.remove(autoCompletes.indexOf("mushroom_cow"));
						    autoCompletes.remove(autoCompletes.indexOf("villager"));
						    autoCompletes.remove(autoCompletes.indexOf("villager.desert"));
						    autoCompletes.remove(autoCompletes.indexOf("villager.jungle"));
						    autoCompletes.remove(autoCompletes.indexOf("villager.plains"));
						    autoCompletes.remove(autoCompletes.indexOf("villager.savanna"));
						    autoCompletes.remove(autoCompletes.indexOf("villager.snow"));
						    autoCompletes.remove(autoCompletes.indexOf("villager.swamp"));
						    autoCompletes.remove(autoCompletes.indexOf("villager.taiga"));
						    
						    return autoCompletes;
						}else if(  args.length == 4 ) {
							autoCompletes.add("1");
							return autoCompletes;
						}
					}
				}
			}
		}
		return null;
	}
	
    private void log(Level lvl, String msg) {
    	mmh.log(lvl, msg, null);
    }
    private void logDebug(String msg) {
    	mmh.logDebug(msg);
    }
    
    @EventHandler
	public void onStriderShiver(StriderTemperatureChangeEvent event){
    	Strider strider = event.getEntity();
    	PersistentDataContainer pdc = strider.getPersistentDataContainer();
    	if (event.isShivering()) {
    		pdc.set(mmh.SHIVERING_KEY, PersistentDataType.STRING, "true");
    	}else {
    		pdc.set(mmh.SHIVERING_KEY, PersistentDataType.STRING, "false");
    	}
	}
    
}