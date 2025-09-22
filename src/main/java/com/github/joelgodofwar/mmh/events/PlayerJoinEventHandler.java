package com.github.joelgodofwar.mmh.events;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.common.PluginLibrary;
import com.github.joelgodofwar.mmh.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.mmh.common.error.Report;
import com.github.joelgodofwar.mmh.enums.Perms;
import lib.github.joelgodofwar.coreutils.util.StrUtils;
import lib.github.joelgodofwar.coreutils.util.common.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerJoinEventHandler implements Listener {
    private final MoreMobHeads plugin;
    private final DetailedErrorReporter reporter;
    private final Set<UUID> warnedPlayers;
    private final String THIS_NAME;
    private final String THIS_VERSION;
    private final boolean UpdateAvailable;
    private final String UC_newVersion;
    private final String UC_oldVersion;
    private final String DownloadLink;

    public PlayerJoinEventHandler(MoreMobHeads plugin) {
        this.plugin = plugin;
        PluginLogger LOGGER = plugin.LOGGER;
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
        if (UpdateAvailable && (player.isOp() || Perms.SHOW_UPDATE_AVAILABLE.hasPermission(player))) {
            String links = "[\"\",{\"text\":\"<Download>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/history\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\" \",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<please_update>\"}},{\"text\":\"| \"},{\"text\":\"<Donate>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://ko-fi.com/joelgodofwar\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Donate_msg>\"}},{\"text\":\" | \"},{\"text\":\"<Notes>\",\"bold\":true,\"color\":\"gold\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"<DownloadLink>/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"<Notes_msg>\"}}]";
            links = links.replace("<DownloadLink>", DownloadLink)
                    .replace("<Download>", plugin.get("mmh.version.download"))
                    .replace("<Donate>", plugin.get("mmh.version.donate"))
                    .replace("<please_update>", plugin.get("mmh.version.please_update"))
                    .replace("<Donate_msg>", plugin.get("mmh.version.donate.message"))
                    .replace("<Notes>", plugin.get("mmh.version.notes"))
                    .replace("<Notes_msg>", plugin.get("mmh.version.notes.message"));
            String versions = ChatColor.GRAY + plugin.get("mmh.version.new_vers") + ": " + ChatColor.GREEN + "{nVers} | " + plugin.get("mmh.version.old_vers") + ": " + ChatColor.RED + "{oVers}";
            player.sendMessage(ChatColor.GRAY + plugin.get("mmh.version.message").replace("<MyPlugin>", ChatColor.GOLD + THIS_NAME + ChatColor.GRAY));
            plugin.coreUtils.sendJsonMessage(player, links);
            player.sendMessage(versions.replace("{nVers}", UC_newVersion).replace("{oVers}", UC_oldVersion));
        }

        boolean disableDLCNagPlayers = plugin.config.getBoolean("disable-dlc-nag-for-players", false);
        boolean disableDLCNagOPs = plugin.config.getBoolean("disable-dlc-nag-for-ops", false);
        if ((plugin.mmhDLC != null) && !plugin.mmhDLC.isEmpty()) {
            String availableDLCs = plugin.mmhDLC.stream()
                    .filter(dlc -> !new java.io.File(plugin.getDataFolder(), dlc.getMarkerFile()).exists())
                    .map(dlc -> String.format("%s (%d heads, $%.2f)",
                            StrUtils.toProperTitleCase(dlc.getFilename().replace("mmh_", "").replace("_", " ").replaceAll("\\.[^.]+$", "")),
                            dlc.getNumberOfFiles(), dlc.getPrice()))
                    .collect(Collectors.joining(", "));
            if (!availableDLCs.isEmpty()) {
                double chance = player.isOp() ? (disableDLCNagOPs ? 0.0 : 0.13) : (disableDLCNagPlayers ? 0.0 : 0.05);
                if (new Random().nextDouble() < chance) {
                    player.sendMessage(ChatColor.YELLOW + "Available DLCs: " + availableDLCs);
                }
            }
        }

        long daysRemaining = plugin.buildValidator.getDaysRemaining();
        try {
            if (player.isOp()) {
                UUID playerUUID = player.getUniqueId();
                if (!warnedPlayers.contains(playerUUID)) {
                    if (!plugin.buildValidator.isBuildValid()) {
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
}