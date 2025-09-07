package com.github.joelgodofwar.mmh.util.heads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryGUI2 implements Listener {
	private final String title;
	private final List<ItemStack> items;
	private final ItemStack senderHead;
	private final Map<ItemStack, Runnable> choices;
	private final BiConsumer<Player, ItemStack> callback;
	private final Map<Player, Integer> playerPages = new HashMap<>();
	private final Map<Player, BiConsumer<Player, ItemStack>> playerCallbacks = new HashMap<>();
	private final Map<Player, Boolean> isNavigating = new HashMap<>(); // Track if the player is navigating pages
	private static final int ITEMS_PER_PAGE = 45;

	public InventoryGUI2(String title, List<ItemStack> items, BiConsumer<Player, ItemStack> callback) {
		this.title = title;
		this.items = new ArrayList<>();
		for (ItemStack item : items) {
			ItemStack clonedItem = item.clone();
			ItemMeta meta = clonedItem.getItemMeta();
			/**List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
			lore.add("§7Quantity: §e" + clonedItem.getAmount());
			meta.setLore(lore);//*/
			clonedItem.setItemMeta(meta);
			this.items.add(clonedItem);
		}
		this.senderHead = null;
		this.choices = null;
		this.callback = callback;
		Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("MoreMobHeads"));
	}

	public InventoryGUI2(String title, List<ItemStack> items, ItemStack senderHead) {
		this.title = title;
		this.items = new ArrayList<>();
		for (ItemStack item : items) {
			ItemStack clonedItem = item.clone();
			ItemMeta meta = clonedItem.getItemMeta();
			/**List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
			lore.add("§7Quantity: §e" + clonedItem.getAmount());
			meta.setLore(lore);//*/
			clonedItem.setItemMeta(meta);
			this.items.add(clonedItem);
		}
		this.senderHead = senderHead != null ? senderHead.clone() : null;
		this.choices = null;
		this.callback = null;
		Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("MoreMobHeads"));
	}

	public InventoryGUI2(String title, Map<ItemStack, Runnable> choices) {
		this.title = title;
		this.items = new ArrayList<>(choices.keySet());
		this.senderHead = null;
		this.choices = choices;
		this.callback = null;
		Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("MoreMobHeads"));
	}

	public void open(Player player, BiConsumer<Player, ItemStack> callbackOverride) {
		playerPages.put(player, 0);
		playerCallbacks.put(player, callbackOverride != null ? callbackOverride : callback);
		updateInventory(player);
	}

	private void updateInventory(Player player) {
		int page = playerPages.getOrDefault(player, 0);
		int totalPages = (int) Math.ceil((double) items.size() / ITEMS_PER_PAGE);
		if ((page < 0) || (page >= totalPages)) {
			page = 0;
			playerPages.put(player, page);
		}

		// Set navigating flag to true before opening the new inventory
		isNavigating.put(player, true);

		Inventory inventory = Bukkit.createInventory(player, 54, title + " - Page " + (page + 1) + "/" + totalPages);
		int startIndex = page * ITEMS_PER_PAGE;
		int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, items.size());
		for (int i = startIndex; i < endIndex; i++) {
			inventory.setItem(i - startIndex, items.get(i));
		}

		if (page > 0) {
			ItemStack prevButton = new ItemStack(Material.ARROW);
			ItemMeta prevMeta = prevButton.getItemMeta();
			prevMeta.setDisplayName("§ePrevious Page");
			prevButton.setItemMeta(prevMeta);
			inventory.setItem(45, prevButton);
		}

		if (page < (totalPages - 1)) {
			ItemStack nextButton = new ItemStack(Material.ARROW);
			ItemMeta nextMeta = nextButton.getItemMeta();
			nextMeta.setDisplayName("§eNext Page");
			nextButton.setItemMeta(nextMeta);
			inventory.setItem(53, nextButton);
		}

		ItemStack closeButton = new ItemStack(Material.BARRIER);
		ItemMeta closeMeta = closeButton.getItemMeta();
		closeMeta.setDisplayName("§cClose");
		closeButton.setItemMeta(closeMeta);
		inventory.setItem(49, closeButton);

		if (senderHead != null) {
			inventory.setItem(46, senderHead);
		}

		player.openInventory(inventory);

		// Reset navigating flag after opening the new inventory
		isNavigating.put(player, false);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getWhoClicked();
		String viewTitle = event.getView().getTitle();
		if (!viewTitle.startsWith(title + " - Page ")) {
			return;
		}

		event.setCancelled(true);
		ItemStack clickedItem = event.getCurrentItem();
		if ((clickedItem == null) || !clickedItem.hasItemMeta()) {
			return;
		}

		String displayName = clickedItem.getItemMeta().getDisplayName();
		if (displayName.equals("§ePrevious Page")) {
			playerPages.put(player, playerPages.getOrDefault(player, 0) - 1);
			updateInventory(player);
		} else if (displayName.equals("§eNext Page")) {
			playerPages.put(player, playerPages.getOrDefault(player, 0) + 1);
			updateInventory(player);
		} else if (displayName.equals("§cClose")) {
			player.closeInventory();
		} else if (choices != null) {
			Runnable action = choices.get(clickedItem);
			if (action != null) {
				player.closeInventory();
				action.run();
			}
		} else {
			BiConsumer<Player, ItemStack> callback = playerCallbacks.get(player);
			if (callback != null) {
				player.closeInventory();
				callback.accept(player, clickedItem);
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getPlayer();
		String viewTitle = event.getView().getTitle();
		if (!viewTitle.startsWith(title + " - Page ")) {
			return;
		}

		// Only unregister the listener if this is a full close, not a page transition
		if (!isNavigating.getOrDefault(player, false)) {
			playerPages.remove(player);
			playerCallbacks.remove(player);
			isNavigating.remove(player);
			HandlerList.unregisterAll(this);
		}
	}
}