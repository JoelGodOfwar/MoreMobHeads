package com.github.joelgodofwar.mmh.util;

import java.util.ArrayList;
import java.util.List;

import lib.github.joelgodofwar.coreutils.util.YmlConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.github.joelgodofwar.mmh.util.ConfigGUI.ConfigSetting;

public abstract class ConfigMenuListener {
	protected final ConfigGUI parent;

	public ConfigMenuListener(ConfigGUI parent) {
		this.parent = parent;
	}

	public abstract void onClick(Player player, ItemStack item);

	// Default implementation for handling updates
	public void handleUpdate(Player player, String settingPath, ItemStack item) {
		if ((item == null) || !item.hasItemMeta()) {
			player.sendMessage(ChatColor.RED + "Invalid item clicked!");
			return;
		}
		ItemMeta meta = item.getItemMeta();
		String path = meta.getPersistentDataContainer().get(ConfigGUI.CONFIG_KEY, PersistentDataType.STRING);
		if (path == null) {
			player.sendMessage(ChatColor.RED + "No setting associated with this item!");
			return;
		}
		for (ConfigSetting setting : parent.settings) {
			if (setting.getPath().equals(path)) {
				setting.updateValue(player, parent.config, parent);
				YmlConfiguration.saveConfig(parent.configFile, parent.config);
				List<ItemStack> updatedItems = new ArrayList<>();
				for (ConfigSetting setting2 : parent.settings) {
					ItemStack updatedIcon = setting2.createIcon(parent.config);
					ItemMeta updatedMeta = updatedIcon.getItemMeta();
					if (updatedMeta != null) {
						updatedMeta.getPersistentDataContainer().set(ConfigGUI.CONFIG_KEY, PersistentDataType.STRING, setting2.getPath());
						updatedIcon.setItemMeta(updatedMeta);
					}
					updatedItems.add(updatedIcon);
				}
				// Close the current inventory
				player.closeInventory();
				// Create and open the updated GUI
				ConfigInventoryGUI updatedGui = new ConfigInventoryGUI("Plugin Settings", updatedItems, new ConfigMenuListenerImpl(parent));
				updatedGui.open(player);
				player.sendMessage(ChatColor.GREEN + "Updated settings GUI. Path: " + path); // Debug
				break;
			}
		}
	}
}

// Inner implementation class (optional, can be merged further if desired)
class ConfigMenuListenerImpl extends ConfigMenuListener {
	public ConfigMenuListenerImpl(ConfigGUI parent) {
		super(parent);
	}

	@Override
	public void onClick(Player player, ItemStack item) {
		handleUpdate(player, null, item); // Delegate to default implementation
	}
}