package com.github.joelgodofwar.mmh.util;

import java.util.List;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class ConfigInventoryGUI implements Listener {
	private final String title;
	private final List<ItemStack> items;
	private final ConfigMenuListener listener;
	private boolean isOpen = false;
	private MoreMobHeads mmh;

	public ConfigInventoryGUI(String title, List<ItemStack> items, ConfigMenuListener listener) {
		this.title = title;
		this.items = items != null ? List.copyOf(items) : List.of(); // Immutable copy
		this.listener = listener;
		this.mmh = MoreMobHeads.getInstance();
		Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("MoreMobHeads"));
	}

	public void open(Player player) {
		if (isOpen) {
			player.closeInventory(); // Ensure previous inventory is closed
		}
		Inventory inventory = Bukkit.createInventory(player, 54, title);
		for (int i = 0; i < Math.min(items.size(), 54); i++) {
			inventory.setItem(i, items.get(i));
		}
		// Add Close button
		ItemStack closeButton = new ItemStack(org.bukkit.Material.BARRIER);
		var closeMeta = closeButton.getItemMeta();
		closeMeta.setDisplayName("§cClose");
		closeButton.setItemMeta(closeMeta);
		inventory.setItem(49, closeButton);
		player.openInventory(inventory);
		isOpen = true;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getWhoClicked();
		if (!event.getView().getTitle().equals(title)) {
			return;
		}

		event.setCancelled(true);
		long now = System.currentTimeMillis();
		Long last = mmh.lastGuiClickTime.getOrDefault(player.getUniqueId(), 0L);
		if (now - last < mmh.GUI_CLICK_COOLDOWN_MS) {
			mmh.logDebug("Ignoring duplicate/fast click for " + player.getName() + " (cooldown active)");
			return;
		}
		mmh.lastGuiClickTime.put(player.getUniqueId(), now);
		ItemStack clickedItem = event.getCurrentItem();
		if ((clickedItem == null) || !clickedItem.hasItemMeta()) {
			return;
		}

		String displayName = clickedItem.getItemMeta().getDisplayName();
		if ((displayName != null) && displayName.equals("§cClose")) {
			NamespacedKey closeFlag = new NamespacedKey(mmh, "config_gui_real_close");
			player.getPersistentDataContainer().set(closeFlag, PersistentDataType.BYTE, (byte) 1);
			player.closeInventory();
			return;
		} else if (listener != null) {
			listener.onClick(player, clickedItem);
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player)) return;

		if (event.getView().getTitle().equals(title)) {
			isOpen = false;
			mmh.logDebug("ConfigInventoryGUI marked as closed for " + event.getPlayer().getName());

			// Optional unregister
			HandlerList.unregisterAll(this);
		}
	}
}