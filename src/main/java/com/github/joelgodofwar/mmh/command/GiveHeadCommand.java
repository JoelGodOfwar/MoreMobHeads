package com.github.joelgodofwar.mmh.command;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.util.heads.HeadManager;
import com.github.joelgodofwar.mmh.util.heads.InventoryGUI;

public class GiveHeadCommand {
	private final MoreMobHeads mmh;
	private final HeadManager headManager;
	private final List<ItemStack> jsonPlayerHeads;
	private final Map<UUID, ItemStack> onlinePlayerHeads;
	private final Map<Player, Player> pendingTargets;
	private final Map<Player, String> pendingHeadTypes;

	public GiveHeadCommand(MoreMobHeads plugin, HeadManager headManager, List<ItemStack> jsonPlayerHeads,
			Map<UUID, ItemStack> onlinePlayerHeads, Map<Player, Player> pendingTargets,
			Map<Player, String> pendingHeadTypes) {
		this.mmh = plugin;
		this.headManager = headManager;
		this.jsonPlayerHeads = jsonPlayerHeads;
		this.onlinePlayerHeads = onlinePlayerHeads;
		this.pendingTargets = pendingTargets;
		this.pendingHeadTypes = pendingHeadTypes;
	}

	public void execute(Player player) {
		List<ItemStack> headTypes = new ArrayList<>();

		ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
		ItemMeta playerMeta = playerHead.getItemMeta();
		playerMeta.setDisplayName("§ePlayer Head");
		playerHead.setItemMeta(playerMeta);
		headTypes.add(playerHead);

		ItemStack mobHead = new ItemStack(Material.CREEPER_HEAD);
		ItemMeta mobMeta = mobHead.getItemMeta();
		mobMeta.setDisplayName("§eMob Head");
		mobHead.setItemMeta(mobMeta);
		headTypes.add(mobHead);

		ItemStack blockHead = new ItemStack(Material.CARVED_PUMPKIN);
		ItemMeta blockMeta = blockHead.getItemMeta();
		blockMeta.setDisplayName("§eBlock Head");
		blockHead.setItemMeta(blockMeta);
		headTypes.add(blockHead);

		InventoryGUI gui = new InventoryGUI("Select Head Type", headTypes, (selectedPlayer, selectedItem) -> {
			if ((selectedItem == null) || !selectedItem.hasItemMeta()) {
				mmh.getLogger().warning("Selected item is null or has no meta in Select Head Type GUI");
				return;
			}

			String displayName = Objects.requireNonNull(selectedItem.getItemMeta()).getDisplayName();
			String headType = null;
			if (displayName.equals("§ePlayer Head")) {
				headType = "player";
			} else if (displayName.equals("§eMob Head")) {
				headType = "mob";
			} else if (displayName.equals("§eBlock Head")) {
				headType = "block";
			}

			if (headType != null) {
				mmh.getLogger().info("Player " + selectedPlayer.getName() + " selected head type: " + headType);
				pendingHeadTypes.put(selectedPlayer, headType);
				openTargetSelectionGui(selectedPlayer);
			} else {
				mmh.getLogger().warning("Unknown head type selected: " + displayName);
			}
		});
		gui.open(player, null);
	}

	private void openTargetSelectionGui(Player player) {
		// Step 2: Open GUI to select target player
		List<ItemStack> onlineHeads = new ArrayList<>(onlinePlayerHeads.values());
		if (onlineHeads.isEmpty()) {
			player.sendMessage(ChatColor.RED + "No online players available to select.");
			mmh.getLogger().warning("No targets available for " + player.getName() + " in givehead command");
			return;
		}

		ItemStack senderHead = onlinePlayerHeads.get(player.getUniqueId());
		if (senderHead == null) {
			player.sendMessage(ChatColor.RED + "Error: Your head could not be loaded.");
			mmh.getLogger().warning("Sender head not found for " + player.getName() + " in givehead command");
			return;
		}

		InventoryGUI targetGui = new InventoryGUI("Select Target Player", onlineHeads, senderHead);
		targetGui.open(player, (selectedPlayer, selectedHead) -> {
			if ((selectedHead != null) && selectedHead.hasItemMeta()) {
				// Find the target player based on the display name of the selected head
				Player target = null;
				String selectedDisplayName = Objects.requireNonNull(selectedHead.getItemMeta()).getDisplayName();
				for (Map.Entry<UUID, ItemStack> entry : onlinePlayerHeads.entrySet()) {
					ItemStack head = entry.getValue();
					if (head.hasItemMeta() && Objects.requireNonNull(head.getItemMeta()).getDisplayName().equals(selectedDisplayName)) {
						target = Bukkit.getPlayer(entry.getKey());
						break;
					}
				}
				if (target == null) {
					selectedPlayer.sendMessage(ChatColor.RED + "Target player is no longer online.");
					mmh.getLogger().warning("Target not found for " + selectedPlayer.getName() + " in givehead command");
					return;
				}

				// Store the target player for the next step
				pendingTargets.put(selectedPlayer, target);
				mmh.getLogger().info("Player " + selectedPlayer.getName() + " selected target: " + target.getName());

				// Step 3: Open GUI to select head (or source for player heads)
				String headType = pendingHeadTypes.get(selectedPlayer);
				if (headType == null) {
					selectedPlayer.sendMessage(ChatColor.RED + "Error: Head type not selected.");
					mmh.getLogger().warning("Head type not found for " + selectedPlayer.getName() + " in givehead command");
					return;
				}

				if (headType.equals("player")) {
					// For player heads, show a choice between Plugin Heads and Online Players
					ItemStack pluginHeadsButton = new ItemStack(Material.BOOK);
					ItemMeta pluginMeta = pluginHeadsButton.getItemMeta();
					pluginMeta.setDisplayName(ChatColor.GREEN + "Plugin Heads");
					pluginHeadsButton.setItemMeta(pluginMeta);

					ItemStack onlineHeadsButton = new ItemStack(Material.PLAYER_HEAD);
					ItemMeta onlineMeta = onlineHeadsButton.getItemMeta();
					onlineMeta.setDisplayName(ChatColor.AQUA + "Online Players");
					onlineHeadsButton.setItemMeta(onlineMeta);

					Map<ItemStack, Runnable> choices = new HashMap<>();
					choices.put(pluginHeadsButton, () -> {
						// Step 4a: Open GUI for JSON-loaded player heads
						InventoryGUI jsonGui = new InventoryGUI("Select a Plugin Head", jsonPlayerHeads, (jsonPlayer, selectedJsonHead) -> {
							if (selectedJsonHead != null) {
								giveHead(jsonPlayer, selectedJsonHead);
							}
							cleanup(jsonPlayer);
						});
						jsonGui.open(selectedPlayer, null);
					});
					choices.put(onlineHeadsButton, () -> {
						// Step 4b: Open GUI for online player heads
						List<ItemStack> onlineHeadsAgain = new ArrayList<>(onlinePlayerHeads.values());
						if (onlineHeadsAgain.isEmpty()) {
							selectedPlayer.sendMessage(ChatColor.RED + "No online players available to select.");
							cleanup(selectedPlayer);
							return;
						}
						InventoryGUI onlineGui = new InventoryGUI("Select an Online Player Head", onlineHeadsAgain, (onlinePlayer, selectedOnlineHead) -> {
							if (selectedOnlineHead != null) {
								giveHead(onlinePlayer, selectedOnlineHead);
							}
							cleanup(onlinePlayer);
						});
						onlineGui.open(selectedPlayer, null);
					});

					InventoryGUI choiceGui = new InventoryGUI("Select Head Source", choices);
					choiceGui.open(selectedPlayer, null);
				} else if (headType.equals("mob")) {
					// Step 4: Open GUI for mob heads
					InventoryGUI mobGui = new InventoryGUI("Select a Mob Head", headManager.getMobHeadsList(), (mobPlayer, selectedMobHead) -> {
						if (selectedMobHead != null) {
							giveHead(mobPlayer, selectedMobHead);
						}
						cleanup(mobPlayer);
					});
					mobGui.open(selectedPlayer, null);
				} else if (headType.equals("block")) {
					// Step 4: Open GUI for block heads
					InventoryGUI blockGui = new InventoryGUI("Select a Block Head", headManager.getBlockHeadsList(), (blockPlayer, selectedBlockHead) -> {
						if (selectedBlockHead != null) {
							ItemStack headToGive = selectedBlockHead.clone();
							headToGive.setAmount(1); // Ensure block heads are given as single items
							giveHead(blockPlayer, headToGive);
						}
						cleanup(blockPlayer);
					});
					blockGui.open(selectedPlayer, null);
				}
			}
		});
	}

	private void giveHead(Player player, ItemStack head) {
		Player target = pendingTargets.get(player);
		if (target != null) {
			target.getWorld().dropItemNaturally(target.getLocation(), head);
			player.sendMessage("§aGave head to " + target.getName() + "!");
			mmh.getLogger().info("Player " + player.getName() + " gave head to " + target.getName());
		} else {
			player.sendMessage("§cTarget player is no longer online.");
			mmh.getLogger().warning("Target not found for " + player.getName() + " in givehead command");
		}
	}

	private void cleanup(Player player) {
		pendingTargets.remove(player);
		pendingHeadTypes.remove(player);
	}

	public void loadPlayerHead(Player player) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		ItemMeta meta = head.getItemMeta();
		meta.setDisplayName(player.getName());
		if (meta instanceof org.bukkit.inventory.meta.SkullMeta) {
			org.bukkit.inventory.meta.SkullMeta skullMeta = (org.bukkit.inventory.meta.SkullMeta) meta;
			skullMeta.setOwningPlayer(player);
		}
		// Add "MoreMobHeads" lore if enabled in config
		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
		if (mmh.getConfig().getBoolean("head_settings.lore.show_plugin_name", true)) {
			// Remove any existing "MoreMobHeads" lore to avoid duplicates
			lore.removeIf(line -> line.equals(ChatColor.AQUA + "MoreMobHeads"));
			lore.add(ChatColor.AQUA + "MoreMobHeads");
		}
		meta.setLore(lore.isEmpty() ? null : lore);

		head.setItemMeta(meta);
		onlinePlayerHeads.put(player.getUniqueId(), head);
		mmh.getLogger().info("Loaded head for player: " + player.getName());
	}

	@SuppressWarnings("unused")
	private ItemStack addPluginLore(ItemStack item) {
		if ((item == null) || !item.hasItemMeta()) {
			return item;
		}
		ItemStack result = item.clone();
		ItemMeta meta = result.getItemMeta();
		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
		if (mmh.config.getBoolean("head_settings.lore.show_plugin_name", true)) {
			// Remove any existing "MoreMobHeads" lore to avoid duplicates
			lore.removeIf(line -> line.equals(ChatColor.AQUA + "MoreMobHeads"));
			lore.add(ChatColor.AQUA + "MoreMobHeads");
		}
		meta.setLore(lore.isEmpty() ? null : lore);
		result.setItemMeta(meta);
		return result;
	}

}