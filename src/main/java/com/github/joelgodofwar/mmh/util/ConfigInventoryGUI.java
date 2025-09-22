package com.github.joelgodofwar.mmh.util;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ConfigInventoryGUI implements Listener {
	private final String title;
	private final List<ItemStack> items;
	private final ConfigMenuListener listener;
	private boolean isOpen = false;

	public ConfigInventoryGUI(String title, List<ItemStack> items, ConfigMenuListener listener) {
		this.title = title;
		this.items = items != null ? List.copyOf(items) : List.of(); // Immutable copy
		this.listener = listener;
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
		ItemStack clickedItem = event.getCurrentItem();
		if ((clickedItem == null) || !clickedItem.hasItemMeta()) {
			return;
		}

		String displayName = clickedItem.getItemMeta().getDisplayName();
		if ((displayName != null) && displayName.equals("§cClose")) {
			player.closeInventory();
		} else if (listener != null) {
			listener.onClick(player, clickedItem);
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}
		if (!event.getView().getTitle().equals(title)) {
			return;
		}
		isOpen = false;
		HandlerList.unregisterAll(this);
	}
}