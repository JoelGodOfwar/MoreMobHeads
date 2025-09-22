package com.github.joelgodofwar.mmh.util.heads;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.github.joelgodofwar.mmh.MoreMobHeads;

public class PlayerHeadLoader {
	private final MoreMobHeads plugin;
	private List<PlayerHeadData> playerHeadDataList = new ArrayList<>();

	public PlayerHeadLoader(MoreMobHeads plugin) {
		this.plugin = plugin;
	}

	public void loadAllPlayerHeads(String directoryPath) {
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			plugin.getLogger().warning("Directory does not exist: " + directoryPath);
			return;
		}
		for (File file : directory.listFiles((dir, name) -> name.endsWith(".json"))) {
			//plugin.logDebug("Loading player head from file: " + file.getName());
			try {
				PlayerHeadData data = loadFromJsonFile(file);
				playerHeadDataList.add(data);
			} catch (Exception e) {
				plugin.getLogger().warning("Error loading player head from " + file.getName() + ": " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public PlayerHeadData loadFromJsonFile(File file) throws Exception {
		try (FileReader reader = new FileReader(file)) {
			JSONTokener tokener = new JSONTokener(reader);
			JSONObject json = new JSONObject(tokener);

			String langName = json.optString("lang_name", file.getName().replace(".json", ""));
			JSONObject headJson = json.getJSONObject("head");
			String displayName = headJson.getString("displayName");
			String noteblockSound = headJson.optString("noteblockSound", null);
			List<String> lore = new ArrayList<>();
			if (headJson.has("lore")) {
				for (Object line : headJson.getJSONArray("lore")) {
					lore.add(line.toString());
				}
			}
			String texture = headJson.getString("texture");
			String uuid = headJson.getString("uuid");
			int quantity = headJson.optInt("quantity", 1);
			ItemStack price1 = null;
			ItemStack price2 = null;
			if (json.has("prices") && json.getJSONObject("prices").has("items")) {
				JSONObject prices = json.getJSONObject("prices");
				if (prices.getJSONArray("items").length() > 0) {
					price1 = itemStackFromJson(prices.getJSONArray("items").getJSONObject(0));
					//plugin.logDebug("Loaded price1 for " + langName + ": " + price1);
				}
				if (prices.getJSONArray("items").length() > 1) {
					price2 = itemStackFromJson(prices.getJSONArray("items").getJSONObject(1));
					//plugin.logDebug("Loaded price2 for " + langName + ": " + price2);
				}
			} else {
				plugin.logDebug("No prices found for " + langName);
			}
			int maxUses = headJson.optInt("maxUses", 1);

			return new PlayerHeadData(langName, displayName, noteblockSound, lore, texture, uuid, quantity, price1, price2, maxUses);
		}
	}

	public PlayerHead createPlayerHead(PlayerHeadData data) {
		ItemStack item = HeadUtils.makeHead(
				data.getDisplayName(),
				data.getTexture(),
				data.getUuid(),
				new ArrayList<>(data.getLore()),
				data.getNoteblockSound()
				);
		item.setAmount(data.getQuantity());
		// Apply the "MoreMobHeads" lore
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
		if (plugin.getConfig().getBoolean("head_settings.lore.show_plugin_name", true)) {
			lore.removeIf(line -> line.equals(ChatColor.AQUA + "MoreMobHeads"));
			lore.add(ChatColor.AQUA + "MoreMobHeads");
		}
		meta.setLore(lore.isEmpty() ? null : lore);
		item.setItemMeta(meta);
		return new PlayerHead(data, item, data.getPrice1(), data.getPrice2(), data.getMaxUses());
	}

	private ItemStack itemStackFromJson(JSONObject json) {
		try {
			Material material = Material.valueOf(json.getString("type"));
			int amount = json.optInt("amount", 1);
			return new ItemStack(material, amount);
		} catch (IllegalArgumentException e) {
			plugin.getLogger().warning("Invalid material in price: " + json.toString());
			return null;
		}
	}

	public List<PlayerHeadData> getPlayerHeadDataList() {
		return playerHeadDataList;
	}
}