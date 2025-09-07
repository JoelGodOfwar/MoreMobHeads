package com.github.joelgodofwar.mmh.events;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.common.error.Report;
import com.github.joelgodofwar.mmh.common.PluginLibrary;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitEventHandler implements Listener {
    private final MoreMobHeads plugin;

    public PlayerQuitEventHandler(MoreMobHeads plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Removes player from map when they leave the server
     */
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        try {
            plugin.chanceRandoms.remove(event.getPlayer());

            UUID playerUUID = event.getPlayer().getUniqueId();
            // Remove player from bedInteractions on logout
            if (plugin.bedInteractions.containsKey(playerUUID)) {
                plugin.bedInteractions.remove(playerUUID);
                plugin.logDebug("PQ - Removed " + event.getPlayer().getName() + " from bedInteractions (logout)");
            }
        } catch (Exception exception) {
            MoreMobHeads.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.UNHANDLED_PLAYERQUIT_EVENT_ERROR).error(exception));
        }
    }
}