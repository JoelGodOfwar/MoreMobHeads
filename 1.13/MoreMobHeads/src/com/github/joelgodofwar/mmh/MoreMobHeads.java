package com.github.joelgodofwar.mmh;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.joelgodofwar.mmh.api.Ansi;
import com.github.joelgodofwar.mmh.api.CatHeads;
import com.github.joelgodofwar.mmh.api.ConfigAPI;
import com.github.joelgodofwar.mmh.api.Metrics;
import com.github.joelgodofwar.mmh.api.MobHeads;
import com.github.joelgodofwar.mmh.api.VillagerHeads;
import com.github.joelgodofwar.mmh.api.ZombieVillagerHeads;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;


public class MoreMobHeads  extends JavaPlugin implements Listener{
	
	public final static Logger logger = Logger.getLogger("Minecraft");
	public static boolean UpdateCheck;
	public static boolean debug;
	public static String daLang;
	String updateURL = "https://github.com/JoelGodOfwar/MoreMobHeads/raw/master/versioncheck/1.13/version.txt";
	File langFile;
    FileConfiguration lang;
	
	@Override // TODO: onEnable
	public void onEnable(){
		ConfigAPI.CheckForConfig(this);
		//PluginDescriptionFile pdfFile = this.getDescription();
		
		getServer().getPluginManager().registerEvents(this, this);
		
		UpdateCheck = getConfig().getBoolean("auto_update_check");
		debug = getConfig().getBoolean("debug");
		daLang = getConfig().getString("lang", "en_US");
		//langFolder = getDataFolder() + "\\lang\\";
		langFile = new File(getDataFolder() + "\\lang\\", daLang + ".yml");//\
		if(!langFile.exists()){                                  // checks if the yaml does not exist
			langFile.getParentFile().mkdirs();                  // creates the /plugins/<pluginName>/ directory if not found
			saveResource("lang\\cs_CZ.yml", true);
			saveResource("lang\\de_DE.yml", true);
			saveResource("lang\\en_US.yml", true);
			saveResource("lang\\fr_FR.yml", true);
			saveResource("lang\\nl_NL.yml", true);
			saveResource("lang\\pt_BR.yml", true);
			saveResource("lang\\zh_CN.yml", true);
			log("lang file not found! copied cs_CZ.yml, de_DE.yml, en_US.yml, fr_FR.yml, nl_NL.yml, pt_BR.yml, and zh_CN.yml to lang folder location");
			//ConfigAPI.copy(getResource("lang.yml"), langFile); // copies the yaml from your jar to the folder /plugin/<pluginName>
        }
		lang = new YamlConfiguration();
		try {
			lang.load(langFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		consoleInfo("Enabled");
		/** DEV check **/
		File jarfile = this.getFile().getAbsoluteFile();
		if(jarfile.toString().contains("-DEV")){
			debug = true;
			logDebug("Jar file contains -DEV, debug set to true");
			//log("jarfile contains dev, debug set to true.");
		}
		Metrics metrics  = new Metrics(this);
		// New chart here
		// myPlugins()
		metrics.addCustomChart(new Metrics.AdvancedPie("my_other_plugins", new Callable<Map<String, Integer>>() {
	        @Override
	        public Map<String, Integer> call() throws Exception {
	            Map<String, Integer> valueMap = new HashMap<>();
	            //int varTotal = myPlugins();
	            if(getServer().getPluginManager().getPlugin("DragonDropElytra") != null){valueMap.put("DragonDropElytra", 1);}
	    		if(getServer().getPluginManager().getPlugin("NoEndermanGrief") != null){valueMap.put("NoEndermanGrief", 1);}
	    		if(getServer().getPluginManager().getPlugin("PortalHelper") != null){valueMap.put("PortalHelper", 1);}
	    		if(getServer().getPluginManager().getPlugin("ShulkerRespawner") != null){valueMap.put("ShulkerRespawner", 1);}
	    		if(getServer().getPluginManager().getPlugin("SinglePlayerSleep") != null){valueMap.put("SinglePlayerSleep", 1);}
	            return valueMap;
	        }
	    }));
		metrics.addCustomChart(new Metrics.SimplePie("auto_update_check", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	            return "" + getConfig().getString("auto_update_check").toUpperCase();
	        }
	    }));
		// add to site
		metrics.addCustomChart(new Metrics.SimplePie("var_debug", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	            return "" + getConfig().getString("debug").toUpperCase();
	        }
	    }));
		metrics.addCustomChart(new Metrics.SimplePie("var_lang", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	            return "" + getConfig().getString("lang").toUpperCase();
	        }
	    }));
	}
	
	@Override // TODO: onDisable
	public void onDisable(){
		consoleInfo("Disabled");
	}
	
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEntityEvent event){// TODO:
		if(!(event.getPlayer() instanceof Player))
			return;
		try{
			Player player = event.getPlayer();
			if(player.hasPermission("moremobheads.nametag")){
				Material material = player.getInventory().getItemInMainHand().getType();
				Material material2 = player.getInventory().getItemInOffHand().getType();
				String name = null;
				if(material.equals(Material.NAME_TAG)){
					name = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
					if(debug){logDebug(player.getDisplayName() + " Main hand name=" + name);};
				}
				if(material2.equals(Material.NAME_TAG)){
					name = player.getInventory().getItemInOffHand().getItemMeta().getDisplayName();
					if(debug){logDebug(player.getDisplayName() + " Off hand name=" + name);};
				}
				
				LivingEntity mob = (LivingEntity) event.getRightClicked();
				//player.sendMessage("Testing");
				if(material.equals(Material.NAME_TAG)||material2.equals(Material.NAME_TAG)){
					//player.sendMessage("Testing 2");
					if(mob instanceof Skeleton||mob instanceof Zombie||mob instanceof PigZombie){
						//player.sendMessage("Testing 3 " + name);
						ItemStack helmet = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
						SkullMeta meta = (SkullMeta)helmet.getItemMeta();
					    
						meta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(name)); //.setOwner(name);
						//meta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(UUID.fromString(name)));
						//meta.setOwner("e2d4c388-42d5-4a96-b4c9-623df7f5e026");
					    helmet.setItemMeta(meta);//                                   e2d4c388-42d5-4a96-b4c9-623df7f5e026
						mob.getEquipment().setHelmet(helmet);
						helmet.setItemMeta(meta);
						//player.getWorld().dropItemNaturally(player.getLocation(), helmet);
						mob.getEquipment().setHelmet(helmet);
						//player.sendMessage(mob.getName() + " named " + mob.getCustomName() + " given player head");
						
					}
			}
			}else{
				//player.sendMessage(mob.getName() + " named " + mob.getCustomName() + " NOT given player head");
			}
			
		}catch (Exception e){
			//e.printStackTrace();
		}

	}
	 
	@SuppressWarnings("deprecation")
	public void dropMobHead(Entity entity, String name){// TODO:
		 ItemStack helmet = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
		 SkullMeta meta = (SkullMeta)helmet.getItemMeta();
		 meta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(name)); //.setOwner(name);
		 helmet.setItemMeta(meta);//                                   e2d4c388-42d5-4a96-b4c9-623df7f5e026
		 helmet.setItemMeta(meta);
		 entity.getWorld().dropItemNaturally(entity.getLocation(), helmet);
	}
	 
	public boolean DropIt(EntityDeathEvent event, double chancepercent){// TODO:
		ItemStack itemstack = event.getEntity().getKiller().getInventory().getItemInMainHand();
		if(itemstack != null){
				if(debug){logDebug("itemstack=" + itemstack.getType().toString() + " line:192");}
			int enchantmentlevel = itemstack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);//.containsEnchantment(Enchantment.LOOT_BONUS_MOBS);
				if(debug){logDebug("enchantmentlevel=" + enchantmentlevel + " line:194");}
			double enchantmentlevelpercent = ((double)enchantmentlevel / 100);
				if(debug){logDebug("enchantmentlevelpercent=" + enchantmentlevelpercent + " line:196");}
			double chance = Math.random();
				if(debug){logDebug("chance=" + chance + " line:198");}
			
				if(debug){logDebug("chancepercent=" + chancepercent + " line:200");}
			chancepercent = chancepercent + enchantmentlevelpercent;
				if(debug){logDebug("chancepercent2=" + chancepercent + " line:202");}
			if (chancepercent > chance){
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings({ "deprecation" })
	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event){// TODO:
		LivingEntity entity = event.getEntity();
		
			if(entity instanceof Player){
				if(debug){logDebug("Entity is Player line:205");}
				if(entity.getKiller() instanceof Player){
					if(entity.getKiller().hasPermission("moremobheads.players")){
						//Player daKiller = entity.getKiller();
						if(debug){logDebug("Killer is Player line:208");}
						ItemStack helmet = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
						SkullMeta meta = (SkullMeta)helmet.getItemMeta();
						meta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(((Player) entity).getDisplayName())); //.setOwner(name);
					    helmet.setItemMeta(meta);//                                   e2d4c388-42d5-4a96-b4c9-623df7f5e026
						helmet.setItemMeta(meta);
						if(DropIt(event, 0.50)){
							entity.getWorld().dropItemNaturally(entity.getLocation(), helmet);
							if(debug){logDebug(entity.getCustomName().toString() + " Player Head Dropped");}
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
		 			//ItemStack itemstack = event.getEntity().getKiller().getInventory().getItemInMainHand();
					//if(itemstack != null){
						/**if(debug){logDebug("itemstack=" + itemstack.getType().toString() + " line:159");}
						int enchantmentlevel = itemstack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);//.containsEnchantment(Enchantment.LOOT_BONUS_MOBS);
						if(debug){logDebug("enchantmentlevel=" + enchantmentlevel + " line:161");}
						double enchantmentlevelpercent = ((double)enchantmentlevel / 100);
						if(debug){logDebug("enchantmentlevelpercent=" + enchantmentlevelpercent + " line:163");}
						double chance = Math.random();
						if(debug){logDebug("chance=" + chance + " line:165");}
						
						if(debug){logDebug("chancepercent=" + chancepercent + " line:167");}
						chancepercent = chancepercent + enchantmentlevelpercent;
						if(debug){logDebug("chancepercent2=" + chancepercent + " line:169");}*/
						//if(chancepercent > 0.00 && chancepercent < 0.99){
						    //if (chancepercent > chance){
						    	//event.getDrops().add(new ItemStack(Material.CREEPER_HEAD, 1));
							if(entity.getKiller().hasPermission("moremobheads.mobs")){
						    	if(entity.getCustomName() != null&&!(entity.getCustomName().contains("jeb_"))
						    			&&!(entity.getCustomName().contains("Toast"))){
						    		if(debug){logDebug("customname=" + entity.getCustomName().toString());}
						    		ItemStack helmet = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
									SkullMeta meta = (SkullMeta)helmet.getItemMeta();
									meta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(entity.getCustomName())); //.setOwner(name);
								    helmet.setItemMeta(meta);//                                   e2d4c388-42d5-4a96-b4c9-623df7f5e026
									helmet.setItemMeta(meta);
									if(DropIt(event, getConfig().getDouble("named_mob_chance_percent", 0.10))){
										entity.getWorld().dropItemNaturally(entity.getLocation(), helmet);
										if(debug){logDebug(entity.getCustomName().toString() + " Head Dropped");}
									}
									return;
						    	}
					 			//String name = event.getEntity().getName().toUpperCase().replace(" ", "_");
					 			String name = event.getEntityType().toString().replace(" ", "_");
					 			log("name=" + name);
					 			switch (name) {
					 			case "CREEPER":
					 				if(DropIt(event, getConfig().getDouble("creeper_chance_percent", 0.025))){
						 				entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf("CREEPER").getTexture(), "Creeper Head"));
						 				if(debug){logDebug("Creeper Head Dropped");}
					 				}
					 				break;
					 			case "ZOMBIE":
					 				if(DropIt(event, getConfig().getDouble("zombie_chance_percent", 0.025))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf("ZOMBIE").getTexture(), "Zombie Head"));
					 					if(debug){logDebug("Zombie Head Dropped");}
					 				}
					 				break;
					 			case "SKELETON":
					 				if(DropIt(event, getConfig().getDouble("skeleton_chance_percent", 0.025))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf("SKELETON").getTexture(), "Skeleton Head"));
					 				}
					 				if(debug){logDebug("Skeleton Head Dropped");}
					 				break;
					 			case "WITHER_SKELETON":
					 				if(DropIt(event, getConfig().getDouble("wither_skeleton_chance_percent", 0.025))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf("WITHER_SKELETON").getTexture(), "Wither Skeleton Head"));
					 				}
					 				if(debug){logDebug("Wither Skeleton Head Dropped");}
					 			case "OCELOT":
					 				Ocelot dacat = (Ocelot) entity;
					 				String dacattype = dacat.getCatType().toString();
					 				log("entity cat=" + dacat.getCatType());
					 				if(DropIt(event, getConfig().getDouble("cat." + dacattype.toLowerCase() + "_chance_percent", 0.025))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(),
				 							makeSkull(CatHeads.valueOf(dacattype).getTexture(),
				 									CatHeads.valueOf(dacattype).getName() + " Head"));
					 				}
					 				if(debug){logDebug("Cat Head Dropped");}
					 				break;
					 			case "LLAMA"://1.11
					 				Llama daLlama = (Llama) entity;
					 				String daLlamaColor = daLlama.getColor().toString();
					 				String daLlamaName = daLlamaColor.toLowerCase().replace("b", "B").replace("c", "C").replace("g", "G").replace("wh", "Wh") + " Llama Head";
					 				//log(name + "_" + daLlamaColor);
					 				if(DropIt(event, getConfig().getDouble("bee_chance_percent", 0.025))){	
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name + "_" + daLlamaColor).getTexture(), daLlamaName));
					 				}
					 				if(debug){logDebug("Llama Head Dropped");}
					 				break;
					 			case "HORSE":
					 				Horse daHorse = (Horse) entity;
					 				String daHorseColor = daHorse.getColor().toString();
					 				String daHorseName = daHorseColor.toLowerCase().replace("b", "B").replace("ch", "Ch").replace("cr", "Cr").replace("d", "D")
					 						.replace("g", "G").replace("wh", "Wh").replace("_", " ") + " Horse Head";
					 				if(DropIt(event, getConfig().getDouble("horse_chance_percent", 0.025))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name + "_" + daHorseColor).getTexture(), daHorseName));
					 				}
					 				if(debug){logDebug("Horse Head Dropped");}
					 				break;
					 			case "PARROT"://1.12
					 				Parrot daParrot = (Parrot) entity;
					 				String daParrotVariant = daParrot.getVariant().toString();
					 				String daParrotName = daParrotVariant.toLowerCase().replace("b", "B").replace("c", "C").replace("g", "G")
					 						.replace("red", "Red") + " Parrot Head";
					 				log(name + "_" + daParrotVariant);
					 				if(DropIt(event, getConfig().getDouble("parrot_chance_percent", 0.025))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name + "_" + daParrotVariant).getTexture(), daParrotName));
					 				}
					 				if(debug){logDebug("Parrot Head Dropped");}
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
					 				String daRabbitName = MobHeads.valueOf(name + "_" + daRabbitType).getName() + " Head";
					 				log(name + "_" + daRabbitType);
					 				if(DropIt(event, getConfig().getDouble("rabbit." + daRabbitType.toLowerCase() + "_chance_percent", 0.025))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name + "_" + daRabbitType).getTexture(), daRabbitName));
					 				}
					 				if(debug){logDebug("Rabbit Head Dropped");}
					 				break;
					 			case "VILLAGER":
					 				Villager daVillager = (Villager) entity;
					 				//String daVillagerType = daVillager.getCareer().toString();
					 				String daVillagerProfession = daVillager.getCareer().toString();
					 				log("name=" + name);
					 				log("profession=" + daVillagerProfession);
					 				//log("type=" + daVillagerType);
					 				String daName = name + "_" + daVillagerProfession;
					 				log(daName + "     " + name + "_" + daVillagerProfession);
					 				String daVillagerName = VillagerHeads.valueOf(daName).getName() + " Head";
					 				if(DropIt(event, getConfig().getDouble("villager." + daVillagerProfession.toLowerCase() + "_chance_percent", 0.025))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(VillagerHeads.valueOf(name + "_" + daVillagerProfession).getTexture(), daVillagerName));
					 				}
					 				if(debug){logDebug("Villager Head Dropped");}
					 				break;
					 			case "ZOMBIE_VILLAGER":
					 				ZombieVillager daZombieVillager = (ZombieVillager) entity;
					 				String daZombieVillagerProfession = daZombieVillager.getVillagerProfession().toString();
					 				String daZombieVillagerName = ZombieVillagerHeads.valueOf(name + "_" + daZombieVillagerProfession).getName() + " Head";
					 				log(name + "_" + daZombieVillagerProfession);
					 				if(DropIt(event, getConfig().getDouble("zombie_villager_chance_percent", 0.025))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(ZombieVillagerHeads.valueOf(name + "_" + daZombieVillagerProfession).getTexture(), daZombieVillagerName));
					 				}
					 				if(debug){logDebug("Zombie Villager Head Dropped");}
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
					 				daSheepName = MobHeads.valueOf(name + "_" + daSheepColor).getName() + " Head";
					 				log(daSheepColor + "_" + name);
					 				if(DropIt(event, getConfig().getDouble("sheep." + daSheepColor.toLowerCase() + "_chance_percent", 0.025))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name + "_" + daSheepColor).getTexture(), daSheepName));
					 				}
					 				if(debug){logDebug("Sheep Head Dropped");}
					 				break;
					 			default:
					 				//makeSkull(MobHeads.valueOf(name).getTexture(), name);
					 				log("name=" + name + " line:242");
					 				log("texture=" + MobHeads.valueOf(name).getTexture() + " line:243");
					 				if(DropIt(event, getConfig().getDouble(name.toLowerCase() + "_chance_percent", 0.025))){
					 					entity.getWorld().dropItemNaturally(entity.getLocation(), makeSkull(MobHeads.valueOf(name).getTexture(), event.getEntity().getName() + " Head"));
					 				}
					 				log(MobHeads.valueOf(name) + " killed");
					 				if(debug){logDebug(name + " Head Dropped");}
					 				break;
					 			}
						    }
						//}
					//}
					return;
				}
			}
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event)
	  {
	    Player p = event.getPlayer();
	    //if(p.isOp() && UpdateCheck||p.hasPermission("sps.showUpdateAvailable")){	
			try {
			
				URL url = new URL(updateURL);
				//TODO: change MC version for correct version checking.
				final URLConnection conn = url.openConnection();
	            conn.setConnectTimeout(5000);
	            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            final String response = reader.readLine();
	            final String localVersion = this.getDescription().getVersion();
	            if(debug){this.logDebug("response= ." + response + ".");} //TODO: Logger
	            if(debug){this.logDebug("localVersion= ." + localVersion + ".");} //TODO: Logger
	            if (!response.equalsIgnoreCase(localVersion)) {
	            	logWarn(Ansi.YELLOW + this.getName() + Ansi.RED + " " + lang.get("newvers") + Ansi.WHITE + " v" + response + Ansi.SANE);
	            	if(p.isOp() && UpdateCheck||p.hasPermission("sps.showUpdateAvailable")){
	            		p.sendMessage(ChatColor.YELLOW + this.getName() + ChatColor.RED + " " + lang.get("newvers") + ChatColor.WHITE + " v" + response);
	            	}
				}
			} catch (MalformedURLException e) {
				this.logDebug(this.getName() + " caught a MalformedURLException, and is still working");
				e.printStackTrace();
			} catch (IOException e) {
				this.logDebug(this.getName() + " caught an IOException, and is still working");
				e.printStackTrace();
			}catch (Exception e) {
				this.logDebug(this.getName() + " caught an Exception, and is still working");
				e.printStackTrace();
			}
			
		//}
	    if(p.getDisplayName().equals("JoelYahwehOfWar")||p.getDisplayName().equals("JoelGodOfWar")){
	    	p.sendMessage(this.getName() + " " + this.getDescription().getVersion() + " Hello father!");
	    	//p.sendMessage("seed=" + p.getWorld().getSeed());
	    }
	}
	
	public void makeHead(EntityDeathEvent event, Material material){// TODO:
		 ItemStack itemstack = event.getEntity().getKiller().getInventory().getItemInMainHand();
			if(itemstack != null){
					if(debug){logDebug("itemstack=" + itemstack.getType().toString() + " line:289");}
				int enchantmentlevel = itemstack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);//.containsEnchantment(Enchantment.LOOT_BONUS_MOBS);
					if(debug){logDebug("enchantmentlevel=" + enchantmentlevel + " line:291");}
				double enchantmentlevelpercent = ((double)enchantmentlevel / 100);
					if(debug){logDebug("enchantmentlevelpercent=" + enchantmentlevelpercent + " line:293");}
				double chance = Math.random();
					if(debug){logDebug("chance=" + chance + " line:295");}
				double chancepercent = 0.25; /** Set to check config.yml later*/
					if(debug){logDebug("chancepercent=" + chancepercent + " line:297");}
				chancepercent = chancepercent + enchantmentlevelpercent;
					if(debug){logDebug("chancepercent2=" + chancepercent + " line:299");}
				if(chancepercent > 0.00 && chancepercent < 0.99){
				    if (chancepercent > chance){
				    	event.getDrops().add(new ItemStack(material, 1));
				    }
				}
			}
	}
	 
	public static ItemStack makeSkull(String textureCode, String headName){// TODO:
			ItemStack item = new ItemStack(Material.PLAYER_HEAD);
			if(textureCode == null) return item;
			SkullMeta meta = (SkullMeta) item.getItemMeta();

			GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(textureCode.getBytes()), textureCode);
			profile.getProperties().put("textures", new Property("textures", textureCode));
			setGameProfile(meta, profile);
			//meta.setOwningPlayer(Bukkit.getOfflinePlayer(ownerUUID));

			meta.setDisplayName(headName);
			item.setItemMeta(meta);
			return item;
	}
	private static Field fieldProfileItem;
	public static void setGameProfile(SkullMeta meta, GameProfile profile){// TODO:
			try{
				if(fieldProfileItem == null) fieldProfileItem = meta.getClass().getDeclaredField("profile");
				fieldProfileItem.setAccessible(true);
				fieldProfileItem.set(meta, profile);
			}
			catch(NoSuchFieldException e){e.printStackTrace();}
			catch(SecurityException e){e.printStackTrace();}
			catch(IllegalArgumentException e){e.printStackTrace();}
			catch(IllegalAccessException e){e.printStackTrace();}
	}
	public void consoleInfo(String state) {// TODO:
			PluginDescriptionFile pdfFile = this.getDescription();
			logger.info(Ansi.YELLOW + "**************************************" + Ansi.SANE);
			logger.info(Ansi.GREEN + pdfFile.getName() + " v" + pdfFile.getVersion() + Ansi.SANE + " is " + state);
			logger.info(Ansi.YELLOW + "**************************************" + Ansi.SANE);
	}
		
		public  void log(String dalog){// TODO:
			logger.info(Ansi.YELLOW + "" + this.getName() + Ansi.SANE + " " + dalog + Ansi.SANE);
		}
		public  void logDebug(String dalog){
			log(" " + this.getDescription().getVersion() + Ansi.RED + Ansi.Bold + " [DEBUG] " + Ansi.SANE + dalog);
		}
		public void logWarn(String dalog){
			log(" " + this.getDescription().getVersion() + Ansi.RED + Ansi.Bold + " [WARN] " + Ansi.SANE + dalog  + Ansi.SANE);
		}

}
