package com.github.joelgodofwar.mmh.events;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.common.PluginLibrary;
import com.github.joelgodofwar.mmh.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.mmh.common.error.Report;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BeheadingHandler implements Listener {
    private final MoreMobHeads mmh;
    private final DetailedErrorReporter reporter;
    private Player playerBedKiller;
    private final boolean mob_announce_enabled;
    private final boolean mob_announce_display;
    private final Map<UUID, List<Pair<UUID, Long>>> killedEntities = new ConcurrentHashMap<>();
    private final Map<UUID, Pair<UUID, Long>> ignitedCreepers = new ConcurrentHashMap<>();
    public YamlConfiguration beheadingMessages = new YamlConfiguration();

    public BeheadingHandler(MoreMobHeads plugin) {
        this.mmh = plugin;
        this.reporter = new DetailedErrorReporter(plugin);
        this.mob_announce_enabled = mmh.getConfig().getBoolean("head_settings.mob_heads.announce_kill.enabled", true);
        this.mob_announce_display = mmh.getConfig().getBoolean("head_settings.mob_heads.announce_kill.displayname", true);
        try {
            beheadingMessages.load(new File(mmh.getDataFolder() + "" + File.separatorChar + "messages.yml"));
        } catch (Exception exception) {
            reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_MESSAGES_LOAD_ERROR).error(exception));
        }
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        try {
            if (!mob_announce_enabled) return;
            Player player = event.getPlayer();
            Entity rightClicked = event.getRightClicked();
            if (!(rightClicked instanceof Creeper)) return; // Only creepers

            ItemStack handItem = player.getInventory().getItemInMainHand();
            Material itemType = handItem.getType();
            if (itemType != Material.FLINT_AND_STEEL && itemType != Material.FIRE_CHARGE) return; // Only these igniters

            UUID creeperUUID = rightClicked.getUniqueId();
            UUID playerUUID = player.getUniqueId();
            long timestamp = System.currentTimeMillis();
            ignitedCreepers.put(creeperUUID, new Pair<>(playerUUID, timestamp));
            mmh.logDebug("PIAE - Player " + player.getName() + " ignited creeper " + creeperUUID + " with " + itemType);

            // Auto-cleanup after 5s if no explosion
            new BukkitRunnable() {
                @Override
                public void run() {
                    ignitedCreepers.remove(creeperUUID);
                }
            }.runTaskLater(mmh, 100L); // 5 ticks? Wait, 100L = 5s
        } catch (Exception exception) {
            reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.UNHANDLED_PLAYERINTERACTATENTITY_EVENT_ERROR).error(exception)); // Add to PluginLibrary
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        try {
            if (mob_announce_enabled) {
                Player player = event.getPlayer();
                UUID playerUUID = player.getUniqueId();

                if (event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                        event.getClickedBlock() != null &&
                        event.getClickedBlock().getType().toString().endsWith("_BED")) {
                    World.Environment env = player.getWorld().getEnvironment();
                    if (env == World.Environment.NETHER || env == World.Environment.THE_END) {
                        if (!mmh.bedInteractions.containsKey(playerUUID)) { // Prevent multiple entries
                            mmh.bedInteractions.put(playerUUID, System.currentTimeMillis());
                            ItemStack bedItem = new ItemStack(event.getClickedBlock().getType());
                            mmh.playerWeapons.put(playerUUID, bedItem);
                            mmh.logDebug("PI - Player " + player.getName() + " interacted with bed in " + env.name());
                            // Clear previous kills
                            killedEntities.remove(playerUUID);
                            mmh.logDebug("Cleared killedEntities for player " + player.getName() + " on bed interaction");
                        }
                    }
                } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
                    long currentTime = System.currentTimeMillis();
                    if (mmh.bedInteractions.containsKey(playerUUID) &&
                            currentTime - mmh.bedInteractions.get(playerUUID) > 1000) {
                        mmh.bedInteractions.remove(playerUUID);
                        mmh.playerWeapons.remove(playerUUID);
                        mmh.logDebug("PI - Removed " + player.getName() + " from bedInteractions (non-bed click)");
                    }
                }
            }
        } catch (Exception exception) {
            reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.UNHANDLED_PLAYERINTERACT_EVENT_ERROR).error(exception));
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        try {
            if (mob_announce_enabled) {
                Entity entity = event.getEntity();
                EntityDamageEvent.DamageCause cause = event.getCause();

                if (!(entity instanceof LivingEntity)) {
                    return;
                }

                if (cause.equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
                    UUID eUUID = mmh.entityPlayers.get(entity.getUniqueId());
                    if (eUUID != null) {
                        ItemStack weapon = mmh.playerWeapons.get(eUUID);
                        mmh.playerWeapons.put(eUUID, weapon);
                        mmh.logDebug("EDE - FIRE_TICK: Maintained playerWeapons for UUID " + eUUID + ", weapon: " + (weapon != null ? weapon.getType() : "null"));
                    } else {
                        mmh.logDebug("EDE - FIRE_TICK: No player UUID found for entity " + entity.getName());
                    }
                }

                World.Environment env = entity.getWorld().getEnvironment();
                if (cause.equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) &&
                        (env == World.Environment.NETHER || env == World.Environment.THE_END)) {
                    mmh.logDebug("EDE - Bed explosion damaged " + entity.getName() + " in " + env.name());
                    LivingEntity theMob = (LivingEntity) entity;
                    UUID triggerUUID = null;
                    long currentTime = System.currentTimeMillis();
                    for (UUID uuid : mmh.bedInteractions.keySet()) {
                        if (currentTime - mmh.bedInteractions.get(uuid) <= 1000) {
                            triggerUUID = uuid;
                            break;
                        }
                    }

                    if (triggerUUID != null) {
                        Player triggerPlayer = mmh.getServer().getPlayer(triggerUUID);
                        if (triggerPlayer != null) {
                            double theDamage = event.getDamage();
                            double theHealth = theMob.getHealth();
                            if (theHealth <= theDamage) {
                                playerBedKiller = triggerPlayer;
                                mmh.entityPlayers.put(theMob.getUniqueId(), triggerUUID);
                                mmh.logDebug("Set playerBedKiller to " + triggerPlayer.getName() + " for entity " + theMob.getName());
                            }
                            ItemStack weapon = mmh.playerWeapons.get(triggerUUID);
                            if (weapon == null || weapon.getType() == Material.AIR) {
                                weapon = resolveDamagingWeapon(triggerPlayer.getInventory(), cause).orElse(null);
                            }
                            mmh.playerWeapons.put(triggerUUID, weapon);
                            mmh.logDebug("EDE - Bed explosion by " + triggerPlayer.getName() + ", weapon: " + (weapon != null ? weapon.getType() : "null"));
                            if (entity.getType() == EntityType.ENDER_DRAGON) {
                                mmh.logDebug("EDE - " + triggerPlayer.getName() + " damaged the Ender Dragon with a bed explosion!");
                            }
                        }
                    } else {
                        mmh.logDebug("EDE - No recent bed interaction found for explosion damaging " + entity.getName());
                    }
                }
                if (cause.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
                    mmh.logDebug("EDE - Potential creeper explosion damaged " + entity.getName());
                    LivingEntity theMob = (LivingEntity) entity;
                    EntityDamageByEntityEvent edbee = (event instanceof EntityDamageByEntityEvent) ? (EntityDamageByEntityEvent) event : null;
                    UUID triggerUUID = null;
                    long currentTime = System.currentTimeMillis();
                    if (edbee != null && edbee.getDamager() instanceof Creeper) {
                        UUID creeperUUID = edbee.getDamager().getUniqueId();
                        Pair<UUID, Long> ignition = ignitedCreepers.get(creeperUUID);
                        if (ignition != null && (currentTime - ignition.getValue() <= 2000)) { // Within 2s of interact
                            triggerUUID = ignition.getKey();
                            ignitedCreepers.remove(creeperUUID); // Consume tracking
                            mmh.logDebug("EDE - Attributed creeper explosion to player UUID " + triggerUUID);
                        }
                    }

                    if (triggerUUID != null) {
                        Player triggerPlayer = mmh.getServer().getPlayer(triggerUUID);
                        if (triggerPlayer != null) {
                            double theDamage = event.getDamage();
                            double theHealth = theMob.getHealth();
                            if (theHealth <= theDamage) { // Likely fatal blow
                                mmh.entityPlayers.put(theMob.getUniqueId(), triggerUUID);
                                mmh.logDebug("Set creeper killer to " + triggerPlayer.getName() + " for entity " + theMob.getName());
                            }
                            // Set "Creeper" as weapon (head for theme; display name for announce)
                            ItemStack creeperWeapon = new ItemStack(Material.CREEPER_HEAD);
                            ItemMeta meta = creeperWeapon.getItemMeta();
                            if (meta != null) {
                                meta.setDisplayName("§aCreeper"); // Custom name for flavor
                                creeperWeapon.setItemMeta(meta);
                            }
                            mmh.playerWeapons.put(triggerUUID, creeperWeapon);
                            mmh.logDebug("EDE - Creeper explosion by " + triggerPlayer.getName() + " (weapon: Creeper)");
                        }
                    } else {
                        mmh.logDebug("EDE - No recent creeper ignition for explosion on " + entity.getName());
                    }
                }
            }
        } catch (Exception exception) {
            reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.UNHANDLED_ENTITYDAMAGE_EVENT_ERROR).error(exception));
        }
    }

    @SuppressWarnings({})
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        try {
            if (mob_announce_enabled) {
                mmh.logDebug("EDEE - START");
                mmh.logDebug("EDEE - getEntity=" + event.getEntity().getName());
                mmh.logDebug("EDEE - getDamager=" + event.getDamager().getName());

                Player player = null;
                if (event.getDamager() instanceof Player) {
                    player = (Player) event.getDamager();
                    if (event.getEntity() instanceof EnderCrystal) {
                        mmh.endCrystals.put(((EnderCrystal) event.getEntity()).getUniqueId(), player.getUniqueId());
                    }
                } else if (event.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) event.getDamager();
                    if (arrow.getShooter() instanceof Player) {
                        player = (Player) arrow.getShooter();
                        if (event.getEntity() instanceof EnderCrystal) {
                            mmh.endCrystals.put(((EnderCrystal) event.getEntity()).getUniqueId(), player.getUniqueId());
                        }
                    } else {
                        return;
                    }
                } else if (event.getDamager() instanceof ThrownPotion) {
                    ThrownPotion potion = (ThrownPotion) event.getDamager();
                    if (potion.getShooter() instanceof Player) {
                        player = (Player) potion.getShooter();
                    } else {
                        return;
                    }
                } else if (event.getDamager() instanceof Snowball) {
                    Snowball snowball = (Snowball) event.getDamager();
                    if (snowball.getShooter() instanceof Player) {
                        player = (Player) snowball.getShooter();
                        if (event.getEntity() instanceof EnderCrystal) {
                            mmh.endCrystals.put(((EnderCrystal) event.getEntity()).getUniqueId(), player.getUniqueId());
                        }
                    } else {
                        return;
                    }
                } else if (event.getDamager() instanceof Egg) {
                    Egg egg = (Egg) event.getDamager();
                    if (egg.getShooter() instanceof Player) {
                        player = (Player) egg.getShooter();
                        if (event.getEntity() instanceof EnderCrystal) {
                            mmh.endCrystals.put(((EnderCrystal) event.getEntity()).getUniqueId(), player.getUniqueId());
                        }
                    } else {
                        return;
                    }
                } else if (event.getDamager() instanceof Trident) {
                    Trident trident = (Trident) event.getDamager();
                    if (trident.getShooter() instanceof Player) {
                        player = (Player) trident.getShooter();
                        if (event.getEntity() instanceof EnderCrystal) {
                            mmh.endCrystals.put(((EnderCrystal) event.getEntity()).getUniqueId(), player.getUniqueId());
                        }
                    } else {
                        return;
                    }
                } else if (event.getDamager() instanceof TNTPrimed) {
                    TNTPrimed tnt = (TNTPrimed) event.getDamager();
                    if (tnt.getSource() instanceof Player) {
                        player = (Player) tnt.getSource();
                    } else {
                        return;
                    }
                } else if (event.getDamager() instanceof EnderCrystal) {
                    EnderCrystal ec = (EnderCrystal) event.getDamager();
                    UUID pUUID = mmh.endCrystals.get(ec.getUniqueId());
                    if (pUUID == null) {
                        return;
                    }
                    player = mmh.getServer().getPlayer(pUUID);
                    if (player != null) {
                        mmh.playerWeapons.put(player.getUniqueId(), new ItemStack(Material.END_CRYSTAL));
                    }
                    return;
                } else if (event.getDamager() instanceof Creeper) {
                    mmh.logDebug("EDEE - Creeper damage ignored");
                    return;
                } else {
                    mmh.logDebug("EDEE - Unknown damager type: " + event.getDamager().getType());
                    return;
                }

                if (player == null) {
                    return;
                }

                Entity entity = event.getEntity();
                EntityDamageEvent ede = entity.getLastDamageCause();
                if (ede != null) {
                    mmh.logDebug("EDEE - dc=" + ede.getCause());
                }
                mmh.logDebug("EDEE - UUID=" + player.getUniqueId());
                mmh.entityPlayers.put(entity.getUniqueId(), player.getUniqueId());
                mmh.playerWeapons.put(player.getUniqueId(), resolveDamagingWeapon(player.getInventory(), ede != null ? ede.getCause() : EntityDamageEvent.DamageCause.ENTITY_ATTACK).orElse(null));
            }
        } catch (Exception exception) {
            reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.UNHANDLED_ENTITYDAMAGEBYENTITY_EVENT_ERROR).error(exception));
        }
    }

    public void announceBeheading(Entity entity, String entityName2, Player player, boolean display) {
        try {
            if (!mob_announce_enabled) {
                mmh.logDebug("Announce skipped: mob_announce_enabled is false");
                return;
            }
            display = mob_announce_display;
            UUID damagingPlayerUUID = player != null ? player.getUniqueId() : null;
            ItemStack damagingWeapon = mmh.playerWeapons.get(damagingPlayerUUID);
            String killerName = display ? player.getDisplayName() : mmh.getNickname(player);

            // Track bed explosion kills
            if (player == playerBedKiller && damagingPlayerUUID != null) {
                long currentTime = System.currentTimeMillis();
                UUID entityUUID = entity.getUniqueId();
                List<Pair<UUID, Long>> playerKills = killedEntities.computeIfAbsent(damagingPlayerUUID, k -> new ArrayList<>());
                synchronized (playerKills) {
                    playerKills.add(new Pair<>(entityUUID, currentTime));
                    mmh.logDebug("Added kill for player " + player.getName() + ", entity " + entityName2 + ", total kills: " + playerKills.size());
                }
            }
            // Creeper cleanup (remove any remaining player ignitions post-announce)
            if (damagingPlayerUUID != null) {
                ignitedCreepers.entrySet().removeIf(entry -> entry.getValue().getKey().equals(damagingPlayerUUID));
            }
            // Announce for all kills
            if (damagingPlayerUUID != null) {
                String weaponName = damagingWeapon != null && damagingWeapon.getItemMeta() != null
                        ? (damagingWeapon.getItemMeta().hasDisplayName() ? damagingWeapon.getItemMeta().getDisplayName() : damagingWeapon.getType().name())
                        : (player == playerBedKiller ? "ORANGE_BED" : "Bare Hands");
                int randomIndex = (int) (Math.random() * Objects.requireNonNull(beheadingMessages.getConfigurationSection("messages")).getKeys(false).size()) + 1;
                String announcement = beheadingMessages.getString("messages.message_" + randomIndex, "%killerName% beheaded %entityName% with %weaponName%.")
                        .replace("%killerName%", killerName).replace("%entityName%", entityName2).replace("%weaponName%", weaponName);
                mmh.coreUtils.broadcast(announcement); // Use direct broadcast
                mmh.logDebug("Announced beheading: " + announcement);

                // Schedule cleanup only once per explosion
                if (player == playerBedKiller && damagingPlayerUUID != null) {
                    List<Pair<UUID, Long>> playerKills = killedEntities.getOrDefault(damagingPlayerUUID, new ArrayList<>());
                    if (!playerKills.isEmpty()) {
                        mmh.logDebug("Scheduling cleanup for player " + player.getName() + ", kills: " + playerKills.size());
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                synchronized (playerKills) {
                                    mmh.playerWeapons.remove(damagingPlayerUUID);
                                    mmh.bedInteractions.remove(damagingPlayerUUID);
                                    for (Pair<UUID, Long> kill : playerKills) {
                                        mmh.entityPlayers.remove(kill.getKey());
                                    }
                                    mmh.logDebug("Cleanup - Removed player " + player.getName() + " and " + playerKills.size() + " entities from maps for bed explosion");
                                    killedEntities.remove(damagingPlayerUUID);
                                }
                            }
                        }.runTaskLater(mmh, 5L);
                    }
                } else {
                    // Unified non-bed cleanup (covers creeper/TNT/direct kills)
                    mmh.playerWeapons.remove(damagingPlayerUUID);
                    mmh.entityPlayers.remove(entity.getUniqueId());
                    mmh.logDebug("Cleanup - Removed player " + player.getName() + " and entity " + entityName2 + " for non-bed kill");
                }
            }
        } catch (Exception exception) {
            reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_ANNOUNCE_ERROR).error(exception));
        }
    }

    public void scheduleBedExplosionCleanup(Player player, long currentTime) {
        if (!mob_announce_enabled) {
            return;
        }
        UUID playerUUID = player.getUniqueId();
        List<Pair<UUID, Long>> playerKills = killedEntities.getOrDefault(playerUUID, new ArrayList<>());
        List<Pair<UUID, Long>> killsToClean = new ArrayList<>();
        synchronized (playerKills) {
            // Collect kills older than 250ms
            for (Pair<UUID, Long> kill : playerKills) {
                if (currentTime - kill.getValue() > 250) {
                    killsToClean.add(kill);
                }
            }
            // Remove collected kills from playerKills
            playerKills.removeAll(killsToClean);
        }
        if (!killsToClean.isEmpty()) {
            mmh.logDebug("Scheduling cleanup for player " + player.getName() + ", kills: " + killsToClean.size());
            new BukkitRunnable() {
                @Override
                public void run() {
                    mmh.playerWeapons.remove(playerUUID);
                    mmh.bedInteractions.remove(playerUUID);
                    synchronized (killsToClean) {
                        for (Pair<UUID, Long> kill : killsToClean) {
                            mmh.entityPlayers.remove(kill.getKey());
                        }
                    }
                    if (playerKills.isEmpty()) {
                        killedEntities.remove(playerUUID);
                    }
                    mmh.logDebug("Cleanup - Removed player " + player.getName() + " and " + killsToClean.size() + " entities from maps for bed explosion");
                }
            }.runTaskLater(mmh, 5L);
        }
    }

    public Optional<ItemStack> resolveDamagingWeapon(PlayerInventory playerInventory, EntityDamageEvent.DamageCause damageCause) {
        try {
            mmh.logDebug("RDW - DamageCause=" + damageCause.toString());
            UUID playerUUID = playerInventory.getHolder() instanceof Player ? ((Player) playerInventory.getHolder()).getUniqueId() : null;
            if (damageCause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION && playerUUID != null && mmh.playerWeapons.containsKey(playerUUID)) {
                return Optional.of(mmh.playerWeapons.get(playerUUID));
            }
            switch (damageCause) {
                case ENTITY_ATTACK:
                    ItemStack mainHandItem = playerInventory.getItemInMainHand();
                    mmh.logDebug("RDW - mainHandItem=" + mainHandItem.getType().toString());
                    if (!mainHandItem.getType().equals(Material.AIR)) {
                        return Optional.of(mainHandItem);
                    }
                    break;
                case PROJECTILE:
                    Optional<ItemStack> bowItem = getWeaponItem(playerInventory, Material.BOW);
                    if (bowItem.isPresent()) return bowItem;
                    Optional<ItemStack> crossbowItem = getWeaponItem(playerInventory, Material.CROSSBOW);
                    if (crossbowItem.isPresent()) return crossbowItem;
                    Optional<ItemStack> tridentItem = getWeaponItem(playerInventory, Material.TRIDENT);
                    if (tridentItem.isPresent()) return tridentItem;
                    Optional<ItemStack> snowItem = getWeaponItem(playerInventory, Material.SNOWBALL);
                    if (snowItem.isPresent()) return snowItem;
                    Optional<ItemStack> eggItem = getWeaponItem(playerInventory, Material.EGG);
                    if (eggItem.isPresent()) return eggItem;
                    break;
                case MAGIC:
                    return getWeaponItem(playerInventory, Material.SPLASH_POTION);
                case ENTITY_EXPLOSION:
                    return Optional.of(new ItemStack(Material.TNT));
                case BLOCK_EXPLOSION:
                    Optional<ItemStack> bedItem = getWeaponItem(playerInventory, Material.RED_BED);
                    if (bedItem.isPresent()) return bedItem;
                    bedItem = getWeaponItem(playerInventory, Material.ORANGE_BED);
                    if (bedItem.isPresent()) return bedItem;
                    bedItem = getWeaponItem(playerInventory, Material.YELLOW_BED);
                    if (bedItem.isPresent()) return bedItem;
                    bedItem = getWeaponItem(playerInventory, Material.WHITE_BED);
                    if (bedItem.isPresent()) return bedItem;
                    return Optional.of(new ItemStack(Material.RED_BED));
                default:
                    break;
            }
        } catch (Exception exception) {
            reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_RESOLVE_DAMAGE_WEAPON).error(exception));
        }
        return Optional.empty();
    }

    private Optional<ItemStack> getWeaponItem(PlayerInventory playerInventory, Material material) {
        try {
            ItemStack mainHandItem = playerInventory.getItemInMainHand();
            ItemStack offHandItem = playerInventory.getItemInOffHand();
            if (mainHandItem.getType() == material) {
                return Optional.of(mainHandItem);
            } else if (offHandItem.getType() == material) {
                return Optional.of(offHandItem);
            }
        } catch (Exception exception) {
            reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_GET_WEAPON_ERROR).error(exception));
        }
        return Optional.empty();
    }

    public Player getPlayerBedKiller() {
        return playerBedKiller;
    }

    // Fallback for EntityDeathEvent creeper kills (simple recent scan; refine with timestamps if needed)
    public Player getPlayerCreeperKiller() {
        long cutoff = System.currentTimeMillis() - 2000; // 2s window
        // Scan entityPlayers for recent attributions (assumes creeper sets recent entries)
        for (Map.Entry<UUID, UUID> entry : mmh.entityPlayers.entrySet()) {
            Player p = mmh.getServer().getPlayer(entry.getValue());
            if (p != null) { // Could add timestamp check if you extend entityPlayers to store time
                return p;
            }
        }
        return null;
    }

    public static class Pair<K, V> {
        private final K key;
        private final V value;
        public Pair(K key, V value) { this.key = key; this.value = value; }
        public K getKey() { return key; }
        public V getValue() { return value; }
    }
}