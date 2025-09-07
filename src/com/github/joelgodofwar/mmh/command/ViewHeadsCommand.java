package com.github.joelgodofwar.mmh.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.util.heads.DLCHeads;
import com.github.joelgodofwar.mmh.util.heads.InventoryGUI;

@SuppressWarnings("unused")
public class ViewHeadsCommand {
	private final MoreMobHeads mmh;
	private final List<ItemStack> jsonPlayerHeads;
	private final Map<UUID, ItemStack> onlinePlayerHeads;
	private final List<ItemStack> mobHeadsList;
	private final List<ItemStack> blockHeadsList;
	private final boolean dlcAdvertisingEnabled;

	public ViewHeadsCommand(MoreMobHeads plugin, List<ItemStack> jsonPlayerHeads, Map<UUID, ItemStack> onlinePlayerHeads,
			List<ItemStack> mobHeadsList, List<ItemStack> blockHeadsList) {
		this.mmh = plugin;
		this.jsonPlayerHeads = jsonPlayerHeads;
		this.onlinePlayerHeads = onlinePlayerHeads;
		this.mobHeadsList = mobHeadsList;
		this.blockHeadsList = blockHeadsList;
		this.dlcAdvertisingEnabled = mmh.getConfig().getBoolean("dlc_advertising.enabled", false);
	}

	public void execute(Player player) {
		// Create a GUI to select the head type (Mob Heads, Player Heads, Block Heads)
		List<ItemStack> headTypes = new ArrayList<>();

		// Player Heads option
		ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
		ItemMeta playerMeta = playerHead.getItemMeta();
		playerMeta.setDisplayName("§ePlayer Heads");
		playerHead.setItemMeta(playerMeta);
		headTypes.add(playerHead);

		// Mob Heads option
		ItemStack mobHead = new ItemStack(Material.CREEPER_HEAD);
		ItemMeta mobMeta = mobHead.getItemMeta();
		mobMeta.setDisplayName("§eMob Heads");
		mobHead.setItemMeta(mobMeta);
		headTypes.add(mobHead);

		// Block Heads option
		ItemStack blockHead = new ItemStack(Material.CARVED_PUMPKIN);
		ItemMeta blockMeta = blockHead.getItemMeta();
		blockMeta.setDisplayName("§eBlock Heads");
		blockHead.setItemMeta(blockMeta);
		headTypes.add(blockHead);

		// DLC Heads option
		ItemStack dlcHead = new ItemStack(Material.SKELETON_SKULL);
		ItemMeta dlcMeta = dlcHead.getItemMeta();
		dlcMeta.setDisplayName("§eDLC Heads");
		dlcHead.setItemMeta(dlcMeta);
		headTypes.add(dlcHead);

		// Create a map of actions for each head type
		Map<ItemStack, Runnable> choices = new HashMap<>();

		// Player Heads: Show a choice between Plugin Heads and Online Players
		choices.put(playerHead, () -> {
			ItemStack pluginHeadsButton = new ItemStack(Material.BOOK);
			ItemMeta pluginMeta = pluginHeadsButton.getItemMeta();
			pluginMeta.setDisplayName(ChatColor.GREEN + "Plugin Heads");
			pluginHeadsButton.setItemMeta(pluginMeta);

			ItemStack onlineHeadsButton = new ItemStack(Material.PLAYER_HEAD);
			ItemMeta onlineMeta = onlineHeadsButton.getItemMeta();
			onlineMeta.setDisplayName(ChatColor.AQUA + "Online Players");
			onlineHeadsButton.setItemMeta(onlineMeta);

			Map<ItemStack, Runnable> playerChoices = new HashMap<>();
			playerChoices.put(pluginHeadsButton, () -> {
				InventoryGUI jsonGui = new InventoryGUI("Select a Plugin Head", jsonPlayerHeads, (BiConsumer<Player, ItemStack>) null);
				jsonGui.open(player, null);
			});
			playerChoices.put(onlineHeadsButton, () -> {
				List<ItemStack> onlineHeads = new ArrayList<>(onlinePlayerHeads.values());
				if (onlineHeads.isEmpty()) {
					player.sendMessage(ChatColor.RED + "No online players available to view.");
					return;
				}
				InventoryGUI onlineGui = new InventoryGUI("Select an Online Player Head", onlineHeads, (BiConsumer<Player, ItemStack>) null);
				onlineGui.open(player, null);
			});

			InventoryGUI choiceGui = new InventoryGUI("Select Head Source", playerChoices);
			choiceGui.open(player, null);
		});

		// Mob Heads: Show the mob heads list
		choices.put(mobHead, () -> {
			InventoryGUI mobGui = new InventoryGUI("Mob Heads", mobHeadsList, (BiConsumer<Player, ItemStack>) null);
			mobGui.open(player, null);
		});

		// Block Heads: Show the block heads list
		choices.put(blockHead, () -> {
			InventoryGUI blockGui = new InventoryGUI("Block Heads", blockHeadsList, (BiConsumer<Player, ItemStack>) null);
			blockGui.open(player, null);
		});

		// DLC Heads: Show DLC heads list (if enabled and DLCs are available)
		if (!dlcAdvertisingEnabled) {
			List<ItemStack> dlcHeads = DLCHeads.getDLCHeads(mmh);
			if (!dlcHeads.isEmpty()) {
				choices.put(dlcHead, () -> {
					InventoryGUI dlcGui = new InventoryGUI("DLC Heads", dlcHeads, (BiConsumer<Player, ItemStack>) null);
					dlcGui.open(player, null);
				});
			}
		}

		// Open the head type selection GUI
		InventoryGUI headTypeGui = new InventoryGUI("Select Head Type", choices);
		headTypeGui.open(player, null);
	}
}