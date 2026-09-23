package com.github.joelgodofwar.mmh.events;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.common.PluginLibrary;
import com.github.joelgodofwar.mmh.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.mmh.common.error.Report;
import com.github.joelgodofwar.mmh.enums.Perms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitTask;

import java.net.URL;
import java.util.Set;
import java.util.UUID;

public class PlayerJoinEventHandler implements Listener {
    private final MoreMobHeads mmh;
    private final DetailedErrorReporter reporter;
    private final Set<UUID> warnedPlayers;
    private final String THIS_NAME;
    private final String THIS_VERSION;
    private final boolean UpdateAvailable;
    private final String UC_newVersion;
    private final String UC_oldVersion;
    private final String DownloadLink;

    public PlayerJoinEventHandler(MoreMobHeads plugin) {
        this.mmh = plugin;
        this.reporter = MoreMobHeads.reporter;
        this.warnedPlayers = plugin.warnedPlayers;
        this.THIS_NAME = MoreMobHeads.THIS_NAME;
        this.THIS_VERSION = MoreMobHeads.THIS_VERSION;
        this.UpdateAvailable = plugin.UpdateAvailable;
        this.UC_newVersion = plugin.UC_newVersion;
        this.UC_oldVersion = plugin.UC_oldVersion;
        this.DownloadLink = plugin.DownloadLink;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Cancel any pending cleanup
        BukkitTask skinTask = mmh.cleanupTasks.remove(player.getUniqueId());
        if (skinTask != null) skinTask.cancel();
        cachePlayerSkin(player);
        if (UpdateAvailable && (player.isOp() || Perms.SHOW_UPDATE_AVAILABLE.hasPermission(player))) {
            String links = "[\"\",{\"text\":\"<Download>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/history\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\" \",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\"| \"},{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}},{\"text\":\" | \"},{\"text\":\"<Notes>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Notes_msg>\"}}]";
            links = links.replace("<DownloadLink>", DownloadLink)
                    .replace("<Download>", mmh.get("mmh.version.download"))
                    .replace("<Donate>", mmh.get("mmh.version.donate"))
                    .replace("<please_update>", mmh.get("mmh.version.please_update"))
                    .replace("<Donate_msg>", mmh.get("mmh.version.donate.message"))
                    .replace("<Notes>", mmh.get("mmh.version.notes"))
                    .replace("<Notes_msg>", mmh.get("mmh.version.notes.message"));
            String versions = ChatColor.GRAY + mmh.get("mmh.version.new_vers") + ": " + ChatColor.GREEN + "{nVers} | " + mmh.get("mmh.version.old_vers") + ": " + ChatColor.RED + "{oVers}";
            player.sendMessage(ChatColor.GRAY + mmh.get("mmh.version.message").replace("<MyPlugin>", ChatColor.GOLD + THIS_NAME + ChatColor.GRAY));
            mmh.coreUtils.sendJsonMessage(player, links);
            player.sendMessage(versions.replace("{nVers}", UC_newVersion).replace("{oVers}", UC_oldVersion));
        }

        long daysRemaining = mmh.buildValidator.getDaysRemaining();
        try {
            if (player.isOp()) {
                UUID playerUUID = player.getUniqueId();
                if (!warnedPlayers.contains(playerUUID)) {
                    if (!mmh.buildValidator.isBuildValid()) {
                        player.sendMessage("§c[MoreMobHeads] Dev-build has expired!");
                    } else if (daysRemaining <= 7) {
                        player.sendMessage("§e[MoreMobHeads] Dev-build expires in " + daysRemaining + " day(s)!");
                    } else if (daysRemaining == 30 || daysRemaining == 14) {
                        player.sendMessage("§a[MoreMobHeads] Dev-build valid, expires in " + daysRemaining + " days");
                    }
                    warnedPlayers.add(playerUUID);
                }
            }
        } catch (Exception e) {
            reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_PLAYER_JOIN_ERROR).error(e));
        }

        if (player.getDisplayName().equals("JoelYahwehOfWar") || player.getDisplayName().equals("JoelGodOfWar")) {
            player.sendMessage(THIS_NAME + " " + THIS_VERSION + " Hello father!");
            player.sendMessage("Dev-build valid, expires in " + daysRemaining + " days");
        }
    }

    public void cachePlayerSkin(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerProfile profile = player.getPlayerProfile();

        // Force complete if needed (still async-friendly)
        if (!profile.isComplete()) {
            profile.update().thenAcceptAsync(updated -> {
                storeSkinInCache(uuid, updated);
            }, r -> Bukkit.getScheduler().runTask(mmh, r));
            return;
        }
        storeSkinInCache(uuid, profile);
    }

    private void storeSkinInCache(UUID uuid, PlayerProfile profile) {
        PlayerTextures textures = profile.getTextures();
        URL skinUrl = textures != null ? textures.getSkin() : null;
        if (skinUrl != null) {
            mmh.playerSkinCache.put(uuid, skinUrl.toString());
            mmh.playerProfileIdCache.put(uuid, profile.getUniqueId()); // in case it differs
            mmh.logDebug("Cached skin for " + uuid + ": " + skinUrl);
        } else {
            mmh.logDebug("No skin available for " + uuid + " (will use fallback)");
        }
    }
}