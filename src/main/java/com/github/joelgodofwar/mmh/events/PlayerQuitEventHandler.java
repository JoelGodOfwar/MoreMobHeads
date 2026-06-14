package com.github.joelgodofwar.mmh.events;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.common.error.Report;
import com.github.joelgodofwar.mmh.common.PluginLibrary;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class PlayerQuitEventHandler implements Listener {
    private final MoreMobHeads mmh;

    public PlayerQuitEventHandler(MoreMobHeads plugin) {
        this.mmh = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Removes player from map when they leave the server
     */
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        try {
            mmh.closeListener.cleanup(event.getPlayer().getUniqueId());
            BukkitTask pendingTask = mmh.closeListener.pendingReloadTasks.remove(event.getPlayer().getUniqueId());
            if (pendingTask != null && !pendingTask.isCancelled()) {
                pendingTask.cancel();
                mmh.logDebug("Cancelled pending config reload task on quit for " + event.getPlayer().getName());
            }
            mmh.chanceRandoms.remove(event.getPlayer());

            UUID playerUUID = event.getPlayer().getUniqueId();
            // Remove player from bedInteractions on logout
            if (mmh.bedInteractions.containsKey(playerUUID)) {
                mmh.bedInteractions.remove(playerUUID);
                mmh.logDebug("PQ - Removed " + event.getPlayer().getName() + " from bedInteractions (logout)");
            }
        } catch (Exception exception) {
            MoreMobHeads.reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.UNHANDLED_PLAYERQUIT_EVENT_ERROR).error(exception));
        }
    }
}