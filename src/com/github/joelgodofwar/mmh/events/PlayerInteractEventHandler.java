package com.github.joelgodofwar.mmh.events;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.common.PluginLibrary;

import com.github.joelgodofwar.mmh.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.mmh.common.error.Report;
import com.github.joelgodofwar.mmh.util.mob.NameTag;
import lib.github.joelgodofwar.coreutils.util.common.PluginLogger;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PlayerInteractEventHandler implements Listener {
    private final MoreMobHeads plugin;
    private final PluginLogger LOGGER;
    private final DetailedErrorReporter reporter;
    private final NamespacedKey NAMETAG_KEY;

    public PlayerInteractEventHandler(MoreMobHeads plugin) {
        this.plugin = plugin;
        this.LOGGER = plugin.LOGGER;
        this.reporter = MoreMobHeads.reporter;
        this.NAMETAG_KEY = plugin.NAMETAG_KEY;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEntityEvent event) {
        try {
            Player player = event.getPlayer();
            if (player.hasPermission("moremobheads.nametag")) {
                plugin.logDebug(" PIEE moremobheads.nametag=true");
                if (plugin.config.getBoolean("head_settings.mob_heads.nametag", false)) {
                    plugin.logDebug(" PIEE mob.nametag=true");
                    Material material = player.getInventory().getItemInMainHand().getType();
                    Material material2 = player.getInventory().getItemInOffHand().getType();
                    String name = "";
                    if (material.equals(Material.NAME_TAG)) {
                        name = Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName();
                        plugin.logDebug(" PIEE" + player.getDisplayName() + " Main hand name=" + name);
                    }
                    if (material2.equals(Material.NAME_TAG)) {
                        name = Objects.requireNonNull(player.getInventory().getItemInOffHand().getItemMeta()).getDisplayName();
                        plugin.logDebug("PIEE " + player.getDisplayName() + " Off hand name=" + name);
                    }

                    if (material.equals(Material.NAME_TAG) || material2.equals(Material.NAME_TAG)) {
                        if (plugin.getServer().getPluginManager().getPlugin("SilenceMobs") != null) {
                            if (name.toLowerCase().contains("silenceme") || name.toLowerCase().contains("silence me")) {
                                return;
                            }
                        }
                        LivingEntity mob = (LivingEntity) event.getRightClicked();
                        plugin.logDebug("canwearhead=" + NameTag.canWearHead(mob));
                        if (NameTag.canWearHead(mob)) {
                            boolean enforce_whitelist = plugin.config.getBoolean("head_settings.player_heads.whitelist.enforce", false);
                            boolean enforce_blacklist = plugin.config.getBoolean("head_settings.player_heads.blacklist.enforce", false);
                            boolean on_whitelist = plugin.config.getString("head_settings.player_heads.whitelist.player_head_whitelist", "").toLowerCase().contains(name.toLowerCase());
                            boolean on_blacklist = plugin.config.getString("head_settings.player_heads.blacklist.player_head_blacklist", "").toLowerCase().contains(name.toLowerCase());
                            if (enforce_whitelist && enforce_blacklist) {
                                if (on_whitelist && !on_blacklist) {
                                    plugin.giveMobHead(mob, name);
                                } else {
                                    event.setCancelled(true);
                                    plugin.logDebug(" PIEE - Name Error 1");
                                }
                            } else if (enforce_whitelist && !enforce_blacklist) {
                                if (on_whitelist) {
                                    plugin.giveMobHead(mob, name);
                                } else {
                                    event.setCancelled(true);
                                    plugin.logDebug(" PIEE - \"" + name + "\" not on whitelist.");
                                }
                            } else if (!enforce_whitelist && enforce_blacklist) {
                                if (!on_blacklist) {
                                    plugin.giveMobHead(mob, name);
                                } else {
                                    event.setCancelled(true);
                                    plugin.logDebug(" PIEE - \"" + name + "\" is on blacklist.");
                                }
                            } else {
                                plugin.giveMobHead(mob, name);
                            }
                            PersistentDataContainer pdc = mob.getPersistentDataContainer();
                            pdc.set(NAMETAG_KEY, PersistentDataType.STRING, "nametag");
                        }
                    }
                } else {
                    plugin.logDebug("mob.nametag=false");
                }
            } else {
                plugin.logDebug("moremobheads.nametag=false");
            }
        } catch (Exception exception) {
            reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PIE_LOAD_ERROR).error(exception));
        }
    }
}