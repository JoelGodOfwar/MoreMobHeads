package com.github.joelgodofwar.mmh.util.heads;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import lib.github.joelgodofwar.coreutils.util.Version;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.joelgodofwar.mmh.MoreMobHeads;


/**
 * Loads and processes mob head data from JSON files for the MoreMobHeads plugin.
 * <p>
 * This class reads mob head configurations from JSON files in the plugin's data folder,
 * handling both flat and nested JSON structures, and creates {@link MobHeadData} objects
 * for compatible mob heads based on the server version.
 */
public class MobHeadLoader {
	private final MoreMobHeads mmh; // Added for logging and version access
	private final List<MobHeadData> mobHeadDataList;
	private final Version serverVersion; // Added for version comparison
	public double playerChance; // Added to store player.json chance

	/**
	 * Constructs a new MobHeadLoader instance.
	 *
	 * @param plugin The main {@link MoreMobHeads} plugin instance.
	 */
	public MobHeadLoader(MoreMobHeads plugin) { // Updated constructor
		this.mmh = plugin;
		this.mobHeadDataList = new ArrayList<>();
		// Get the server version (e.g., "1.21.4")
		this.serverVersion = new Version(Bukkit.getServer());
		this.playerChance = 50.0; // Default value
	}

	/**
	 * Loads mob head data from a specified JSON file.
	 * <p>
	 * Processes the JSON file, handling player heads (player.json) separately and
	 * delegating to {@link #loadFlatHeads(JSONArray, double, String)} or
	 * {@link #loadNestedHeads(JSONObject, double, String, String)} based on the
	 * file's structure (flat or nested).
	 *
	 * @param filePath The absolute path to the JSON file.
	 */
	public void loadFromJsonFile(String filePath) {
		try {
			String content = new String(Files.readAllBytes(new File(filePath).toPath()));
			JSONObject jsonObj = new JSONObject(content);
			// Check if the file is player.json
			if (filePath.endsWith("player.json")) {
				mmh.playerChance = jsonObj.optDouble("chance", 50.0);
				mmh.LOGGER.log("Loaded player chance: " + playerChance + " from " + filePath);
				return; // Skip further processing for player.json
			}
			if (filePath.endsWith("named.json")) {
				mmh.namedChance = jsonObj.optDouble("chance", 10.0);
				mmh.LOGGER.log("Loaded named chance: " + playerChance + " from " + filePath);
				return; // Skip further processing for player.json
			}
			double defaultChance = jsonObj.optDouble("defaultChance", 100.0);
			String structure = jsonObj.optString("structure", "flat");
			Object headsData = jsonObj.get("heads");

			if (headsData instanceof JSONArray) {
				mmh.logDebug("Processing flat structure for " + filePath);
				loadFlatHeads((JSONArray) headsData, defaultChance, filePath);
			} else if (headsData instanceof JSONObject) {
				mmh.logDebug("Processing nested structure for " + filePath + " (ignoring structure value: " + structure + ")");
				loadNestedHeads((JSONObject) headsData, defaultChance, "", filePath);
			} else {
				mmh.LOGGER.warn("Invalid heads data type in " + filePath + ": " + headsData.getClass().getSimpleName());
			}

			//mmh.LOGGER.log("Loaded " + mobHeadDataList.size() + " mob head data from " + filePath);
		} catch (Exception e) {
			mmh.LOGGER.warn("Error loading mob heads from " + filePath + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Loads mob head data from a flat JSON structure.
	 * <p>
	 * Iterates through the heads array, creates {@link MobHeadData} objects, and adds
	 * them to the internal list if they are compatible with the server version.
	 *
	 * @param headsArray    The JSON array containing mob head data.
	 * @param defaultChance The default drop chance for heads without a specified chance.
	 * @param filePath      The path to the JSON file being processed.
	 */
	private void loadFlatHeads(JSONArray headsArray, double defaultChance, String filePath) {
		for (int i = 0; i < headsArray.length(); i++) {
			JSONObject headJson = headsArray.getJSONObject(i);
			MobHeadData headData = createMobHeadData(headJson, defaultChance, filePath);
			if ((headData != null) && isVersionCompatible(headData.getMinMCVersion(), headData.getMaxMCVersion())) {
				mobHeadDataList.add(headData);
			} else {
				mmh.LOGGER.log("Skipping head '" + headJson.optString("displayName", "unknown") + "' from "
						+ filePath + " due to version incompatibility (server version: " + serverVersion + ")");
			}
		}
	}

	/**
	 * Loads mob head data from a nested JSON structure.
	 * <p>
	 * Recursively processes the JSON object, creating {@link MobHeadData} objects for
	 * entries with a displayName and adding them to the internal list if compatible.
	 *
	 * @param headsObj      The JSON object containing nested mob head data.
	 * @param defaultChance The default drop chance for heads without a specified chance.
	 * @param parentKey     The parent key for constructing full langName keys.
	 * @param filePath      The path to the JSON file being processed.
	 */
	private void loadNestedHeads(JSONObject headsObj, double defaultChance, String parentKey, String filePath) {
		for (String key : headsObj.keySet()) {
			String fullKey = parentKey.isEmpty() ? key : parentKey + "." + key;
			Object value = headsObj.get(key);

			if (value instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject) value;
				if (jsonObject.has("displayName")) {
					MobHeadData headData = createMobHeadData(jsonObject, defaultChance, filePath);
					if ((headData != null) && isVersionCompatible(headData.getMinMCVersion(), headData.getMaxMCVersion())) {
						mobHeadDataList.add(headData);
					} else {
						mmh.LOGGER.log("Skipping head '" + jsonObject.optString("displayName", "unknown")
								+ "' from " + filePath + " due to version incompatibility (server version: " + serverVersion + ")");
					}
				} else {
					loadNestedHeads(jsonObject, defaultChance, fullKey, filePath);
				}
			} else {
				mmh.logDebug("Unexpected value type for key " + fullKey + ": " + value);
			}
		}
	}

	/**
	 * Creates a {@link MobHeadData} object from a JSON object.
	 * <p>
	 * Extracts mob head properties (displayName, langName, texture, etc.) and
	 * constructs a {@link MobHeadData} instance, including the file path.
	 *
	 * @param headJson      The JSON object containing mob head data.
	 * @param defaultChance The default drop chance to use if not specified.
	 * @param filePath      The path to the JSON file being processed.
	 * @return A {@link MobHeadData} object, or null if creation fails.
	 */
	private MobHeadData createMobHeadData(JSONObject headJson, double defaultChance, String filePath) {
		ArrayList<String> lore = new ArrayList<>();
		if (headJson.has("lore")) {
			JSONArray loreArray = headJson.getJSONArray("lore");
			for (int i = 0; i < loreArray.length(); i++) {
				lore.add(loreArray.getString(i));
			}
		}

		String minMCVersion = headJson.optString("min_mc_version", null);
		String maxMCVersion = headJson.optString("max_mc_version", null); // Added

		return new MobHeadData(
				headJson.getString("displayName"),
				headJson.optString("langName", ""),
				headJson.getString("texture"),
				headJson.getString("uuid"),
				lore,
				headJson.getString("noteblockSound"),
				headJson.optInt("quantity", 1),
				headJson.optDouble("chance", defaultChance),
				minMCVersion,
				maxMCVersion,
				filePath
				);
	}

	/**
	 * Checks if a mob head is compatible with the current server version.
	 * <p>
	 * Compares the server version against the minimum and maximum Minecraft versions
	 * specified in the mob head data.
	 *
	 * @param minMcVersion The minimum Minecraft version required, or null if none.
	 * @param maxMcVersion The maximum Minecraft version supported, or null if none.
	 * @return true if the mob head is compatible, false otherwise.
	 */
	private boolean isVersionCompatible(String minMcVersion, String maxMcVersion) {
		// If neither max_mc_version nor min_mc_version is specified, load the head
		if ((maxMcVersion == null) && (minMcVersion == null)) {
			return true;
		}

		// Check max_mc_version
		//mmh.LOGGER.log("Checking max_mc_version: " + maxMcVersion);
		if (maxMcVersion != null) {
			try {
				Version maxVersion = new Version(maxMcVersion);
				//mmh.LOGGER.log("Parsed maxVersion: " + maxVersion);
				boolean isAtMost = serverVersion.isAtMost(maxVersion);
				//mmh.LOGGER.log("serverVersion.isAtMost(maxVersion): " + isAtMost + " (compareTo result: " + serverVersion.compareTo(maxVersion) + ")");
				if (!isAtMost) {
					mmh.LOGGER.log("Server version " + serverVersion + " is greater than max_mc_version " + maxMcVersion + ", skipping head");
					return false;
				}
			} catch (Exception e) {
				mmh.LOGGER.warn("Failed to parse max_mc_version '" + maxMcVersion + "': " + e.getMessage() + ", assuming compatibility");
				return true;
			}
		}

		// Check min_mc_version
		//mmh.LOGGER.log("Checking min_mc_version: " + minMcVersion);
		if (minMcVersion != null) {
			try {
				Version minVersion = new Version(minMcVersion);
				//mmh.LOGGER.log("Parsed minVersion: " + minVersion);
				boolean isAtLeast = serverVersion.isAtLeast(minVersion);
				//mmh.LOGGER.log("serverVersion.isAtLeast(minVersion): " + isAtLeast + " (compareTo result: " + serverVersion.compareTo(minVersion) + ")");
				if (!isAtLeast) {
					mmh.LOGGER.log("Server version " + serverVersion + " is less than min_mc_version " + minMcVersion + ", skipping head");
					return false;
				}
			} catch (Exception e) {
				mmh.LOGGER.warn("Failed to parse min_mc_version '" + minMcVersion + "': " + e.getMessage() + ", assuming compatibility");
				return true;
			}
		}

		return true;
	}

	/**
	 * Returns a copy of the loaded mob head data list.
	 *
	 * @return A new {@link List} containing all loaded {@link MobHeadData} objects.
	 */
	public List<MobHeadData> getMobHeadDataList() {
		return new ArrayList<>(mobHeadDataList);
	}

	/**
	 * Clears the internal list of loaded mob head data.
	 */
	public void clear() {
		mobHeadDataList.clear();
	}

	/**
	 * Loads all mob head JSON files from the specified directory.
	 * <p>
	 * Processes all .json files in the directory, calling {@link #loadFromJsonFile(String)}
	 * for each file. Logs errors for individual files and continues loading others.
	 *
	 * @param directoryPath The path to the directory containing mob head JSON files.
	 */
	public void loadAllMobHeads(String directoryPath) {
		File directory = new File(directoryPath);
		File[] jsonFiles = directory.listFiles((dir, name) -> name.endsWith(".json"));
		if (jsonFiles != null) {
			for (File file : jsonFiles) {
				try {
					loadFromJsonFile(file.getAbsolutePath());
					mmh.logDebug("Successfully loaded heads from " + file.getAbsolutePath());
				} catch (Exception e) {
					mmh.LOGGER.warn("Failed to load heads from " + file.getAbsolutePath() + ": " + e.getMessage());
					e.printStackTrace();
				}
			}
			mmh.LOGGER.log("Loaded " + mobHeadDataList.size() + " mob heads from " + directoryPath);
		} else {
			mmh.LOGGER.warn("No JSON files found in directory: " + directoryPath);
		}
	}

	/**
	 * Creates a {@link MobHead} instance from a {@link MobHeadData} object.
	 * <p>
	 * Uses {@link HeadUtils#makeHead(String, String, String, java.util.ArrayList, String)} to create
	 * an ItemStack and pairs it with the provided data.
	 *
	 * @param data The {@link MobHeadData} containing mob head properties.
	 * @return A new {@link MobHead} instance.
	 */
	public MobHead createMobHead(MobHeadData data) {
		ItemStack head = HeadUtils.makeHead(
				data.getDisplayName(),
				data.getTexture(),
				data.getUuid(),
				data.getLore(),
				data.getNoteblockSound()
				);
		return new MobHead(data, head);
	}
}