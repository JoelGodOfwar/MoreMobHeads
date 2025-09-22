package com.github.joelgodofwar.mmh.util.heads;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import lib.github.joelgodofwar.coreutils.util.Version;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.github.joelgodofwar.mmh.MoreMobHeads;


public class MiniBlockLoader {
	private final MoreMobHeads plugin;
	private final List<MiniBlock> miniBlocks = new ArrayList<>();

	public MiniBlockLoader(MoreMobHeads plugin) {
		this.plugin = plugin;
	}

	public void loadAllMiniBlocks(String directoryPath) {
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			plugin.getLogger().warning("MiniBlock directory does not exist: " + directoryPath);
			return;
		}
		Version currentVersion = new Version(plugin.getServer());
		for (File file : directory.listFiles((dir, name) -> name.endsWith(".json"))) {
			//plugin.logDebug("Loading mini block from file: " + file.getName());
			try {
				MiniBlock miniBlock = loadFromJsonFile(file, currentVersion);
				if (miniBlock != null) {
					miniBlocks.add(miniBlock);
				}
			} catch (Exception e) {
				plugin.getLogger().warning("Error loading mini block from " + file.getName() + ": " + e.getMessage());
				e.printStackTrace();
			}
		}
		//plugin.logDebug("Loaded " + miniBlocks.size() + " Mini Blocks");
	}

	public MiniBlock loadFromJsonFile(File file, Version currentVersion) throws Exception {
		try (FileReader reader = new FileReader(file)) {
			JSONTokener tokener = new JSONTokener(reader);
			JSONObject json = new JSONObject(tokener);

			// Check min_mc_version before proceeding
			JSONObject headJson = json.getJSONObject("head");
			String minMCVersion = headJson.optString("min_mc_version", null);
			if ((minMCVersion != null) && !currentVersion.isAtLeast(new Version(minMCVersion))) {
				plugin.logDebug("Skipping mini block " + file.getName() + " (min_mc_version " + minMCVersion + " > server version " + currentVersion.getVersion() + ")");
				return null;
			}

			String langName = json.optString("lang_name", file.getName().replace(".json", ""));
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
					// plugin.logDebug("Loaded price1 for " + langName + ": " + price1);
				}
				if (prices.getJSONArray("items").length() > 1) {
					price2 = itemStackFromJson(prices.getJSONArray("items").getJSONObject(1));
					// plugin.logDebug("Loaded price2 for " + langName + ": " + price2);
				}
			} else {
				plugin.logDebug("No prices found for " + langName);
			}
			int maxUses = headJson.optInt("maxUses", 1);

			BlockHeadData data = new BlockHeadData(
					langName, displayName, noteblockSound, lore, 0.0, 0.0, texture, uuid, quantity,
					price1, price2, maxUses, minMCVersion
					);

			ItemStack head = HeadUtils.makeHead(
					data.getDisplayName(),
					data.getTexture(),
					data.getUuid(),
					new ArrayList<>(data.getLore()),
					data.getNoteblockSound()
					);
			head.setAmount(1);

			if (price2 == null) {
				plugin.LOGGER.warn("No price2 found for mini block " + langName + " - skipping");
				return null;
			}
			return new MiniBlock(data, head, price2);
		}
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

	public List<MiniBlock> getMiniBlocks() {
		return new ArrayList<>(miniBlocks);
	}
}