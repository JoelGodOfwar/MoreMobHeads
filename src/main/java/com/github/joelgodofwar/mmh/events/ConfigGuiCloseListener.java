package com.github.joelgodofwar.mmh.events;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigGuiCloseListener implements Listener {
    private final MoreMobHeads mmh;
    // Map to track the currently scheduled reload-check task per player
    final ConcurrentHashMap<UUID, BukkitTask> pendingReloadTasks = new ConcurrentHashMap<>();

    public ConfigGuiCloseListener(MoreMobHeads plugin) {
        this.mmh = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        String title = getTitleSafely(event.getView());
        if (!isConfigGuiTitle(title)) return;

        UUID uuid = player.getUniqueId();
        if (!mmh.inConfigGuiSession.contains(uuid)) return;

        // ──────────────────────────────────────────────────────────────
        // IMMEDIATE SYNC CHECK: see what inventory is open RIGHT NOW
        // This runs in the same tick as the close event
        String currentAfterClose = getTitleSafely(player.getOpenInventory());
        mmh.logDebug("Immediate post-close title check: " + (currentAfterClose == null ? "null" : currentAfterClose));
        // ──────────────────────────────────────────────────────────────

        // If another config GUI is already open, this was navigation → SKIP scheduling
        if (isConfigGuiTitle(currentAfterClose)) {
            mmh.logDebug("Skipping schedule - close appears to be navigation (still open: " + currentAfterClose + ")");
            return;
        }

        // If we reach here → no config GUI is open anymore → likely real exit
        mmh.logDebug("Looks like potential final close (no config GUI open) → proceeding with schedule");

        // Cancel any previous pending task
        BukkitTask old = pendingReloadTasks.remove(uuid);
        if (old != null && !old.isCancelled()) {
            old.cancel();
            mmh.logDebug("Cancelled old pending task before scheduling new one");
        }

        // Schedule confirmation delay (in case of lag or very fast manual reopen)
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                pendingReloadTasks.remove(uuid);

                if (!player.isOnline()) return;

                String current = getTitleSafely(player.getOpenInventory());
                if (!isConfigGuiTitle(current)) {
                    mmh.inConfigGuiSession.remove(uuid);
                    mmh.reloadPlugin();
                    player.sendMessage(ChatColor.GREEN + "Configuration changes applied!");
                    mmh.logDebug("FINAL RELOAD - confirmed no GUI open after delay");
                } else {
                    mmh.logDebug("Delayed check aborted - GUI reopened meanwhile (" + current + ")");
                }
            }
        }.runTaskLater(mmh, 10L);  // increased to 10 ticks for safety

        pendingReloadTasks.put(uuid, task);
        mmh.logDebug("SCHEDULED potential final-exit check after close of: " + title);
    }

    private String getTitleSafely(Object viewObj) {
        if (viewObj == null) return null;
        try {
            Method m = viewObj.getClass().getMethod("getTitle");
            return (String) m.invoke(viewObj);
        } catch (Exception e) {
            mmh.logDebug("Failed to get inventory title via reflection: " + e.getMessage());
            return null;
        }
    }

    private boolean isConfigGuiTitle(String title) {
        if (title == null) return false;
        return title.startsWith("Config Settings") ||
                title.startsWith("Plugin Settings") ||
                title.startsWith("Global Settings") ||
                title.startsWith("Head Settings") ||
                title.startsWith("Wandering Trades") ||
                title.equals("Select Language") ||
                title.contains(" - Page ");
    }

    // Call this when the plugin disables or when a player quits mid-session (optional but good practice)
    public void cleanup(UUID uuid) {
        BukkitTask task = pendingReloadTasks.remove(uuid);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
}