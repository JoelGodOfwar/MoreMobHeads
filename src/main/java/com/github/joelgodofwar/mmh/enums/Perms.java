package com.github.joelgodofwar.mmh.enums;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Perms {
    BLOCK_HEADS("blockheads"),
    CONFIG("config"),
    CUSTOM_TRADER("customtrader"),
    FIX_HEAD("fixhead"),
    GIVE("give"),
    MOBS("mobs"),
    NAMETAG("nametag"),
    PLAYER_HEADS("playerheads"),
    PLAYERS("players"),
    RELOAD("reload"),
    SHOW_UPDATE_AVAILABLE("showUpdateAvailable"),
    TOGGLE_DEBUG("toggledebug"),
    VIEW_HEADS("viewheads");

    private static final String PREFIX = "moremobheads.";
    private final String permission;

    Perms(String permission) {
        this.permission = PREFIX + permission;
    }

    public String getPermission() {
        return permission;
    }

    /**
     * Checks if a CommandSender has this specific permission.
     * Always returns true for the console.
     * @param sender The CommandSender to check.
     * @return true if the sender is the console or has the permission, false otherwise.
     */
    public boolean hasPermission(CommandSender sender) {
        return !(sender instanceof Player) || sender.hasPermission(permission);
    }

    /**
     * Checks if a CommandSender has this permission or is an operator.
     * Always returns true for the console.
     * @param sender The CommandSender to check.
     * @return true if the sender is the console, has the permission, or is an op, false otherwise.
     */
    public boolean hasPermissionOrOp(CommandSender sender) {
        return !(sender instanceof Player) || (sender.isOp() || sender.hasPermission(permission));
    }

    /**
     * Checks if a Player has this specific permission.
     * @param player The Player to check.
     * @return true if the player has the permission, false otherwise.
     */
    public boolean hasPermission(Player player) {
        return player.hasPermission(permission);
    }

    /**
     * Checks if a Player has this permission or is an operator.
     * @param player The Player to check.
     * @return true if the player has the permission or is an op, false otherwise.
     */
    public boolean hasPermissionOrOp(Player player) {
        return player.isOp() || player.hasPermission(permission);
    }
}