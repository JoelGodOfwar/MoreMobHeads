package com.github.joelgodofwar.mmh.util;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Loads MobHead data from JSON files into a list, supporting both flat and nested structures.
 * Designed to handle various mob types and future block heads with a flexible loading mechanism.
 */
public class MobHeadLoader {
	private final List<MobHead> mobHeads;

	/**
	 * Constructs a new MobHeadLoader with an empty list of MobHeads.
	 */
	public MobHeadLoader() {
		this.mobHeads = new ArrayList<>();
	}

	/**
	 * Loads MobHead data from a single JSON file.
	 * Supports "flat" (array-based) or "nested" (object-based) head structures based on the "structure" field.
	 *
	 * @param filePath The path to the JSON file to load.
	 */
	public void loadFromJsonFile(String filePath) {
		try {
			String content = new String(Files.readAllBytes(new File(filePath).toPath()));
			JSONObject jsonObj = new JSONObject(content);
			double defaultChance = jsonObj.optDouble("defaultChance", 100.0); // Default to 100.0 if missing
			String structure = jsonObj.optString("structure", "flat"); // Default to flat if missing
			Object headsData = jsonObj.get("heads");

			if ("flat".equalsIgnoreCase(structure)) {
				loadFlatHeads((JSONArray) headsData, defaultChance);
			} else if ("nested".equalsIgnoreCase(structure)) {
				loadNestedHeads((JSONObject) headsData, defaultChance);
			} else {
				System.err.println("Unknown structure type '" + structure + "' in " + filePath);
			}

			System.out.println("Loaded " + mobHeads.size() + " mob heads from " + filePath);
		} catch (Exception e) {
			System.err.println("Error loading mob heads from " + filePath + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Loads MobHeads from a flat JSON array structure.
	 *
	 * @param headsArray The JSONArray containing head data.
	 * @param defaultChance The default chance value to use if a head doesn't specify its own chance.
	 */
	private void loadFlatHeads(JSONArray headsArray, double defaultChance) {
		for (int i = 0; i < headsArray.length(); i++) {
			JSONObject headJson = headsArray.getJSONObject(i);
			mobHeads.add(createMobHead(headJson, defaultChance));
		}
	}

	/**
	 * Loads MobHeads from a nested JSON object structure (e.g., biome -> profession).
	 *
	 * @param headsObj The JSONObject containing nested head data.
	 * @param defaultChance The default chance value to use if a head doesn't specify its own chance.
	 */
	private void loadNestedHeads(JSONObject headsObj, double defaultChance) {
		for (String level1Key : headsObj.keySet()) { // e.g., biome
			JSONObject level1Obj = headsObj.getJSONObject(level1Key);
			for (String level2Key : level1Obj.keySet()) { // e.g., profession
				JSONObject headJson = level1Obj.getJSONObject(level2Key);
				mobHeads.add(createMobHead(headJson, defaultChance));
			}
		}
	}

	/**
	 * Creates a MobHead instance from a JSON object, applying default values where fields are missing.
	 *
	 * @param headJson The JSONObject containing head data.
	 * @param defaultChance The default chance value to use if "chance" is not specified.
	 * @return A new MobHead instance populated with the JSON data.
	 */
	private MobHead createMobHead(JSONObject headJson, double defaultChance) {
		ArrayList<String> lore = new ArrayList<>();
		if (headJson.has("lore")) {
			JSONArray loreArray = headJson.getJSONArray("lore");
			for (int i = 0; i < loreArray.length(); i++) {
				lore.add(loreArray.getString(i));
			}
		}

		return new MobHead(
				headJson.getString("displayName"),
				headJson.getString("langName"),
				headJson.getString("texture"),
				headJson.getString("uuid"),
				lore,
				headJson.getString("noteblockSound"),
				headJson.getInt("quantity"),
				headJson.optDouble("chance", defaultChance) // Use defaultChance if chance is missing
				);
	}

	/**
	 * Returns a copy of the loaded MobHeads list.
	 *
	 * @return A new ArrayList containing all loaded MobHeads, protecting the internal list.
	 */
	public List<MobHead> getMobHeads() {
		return new ArrayList<>(mobHeads);
	}

	/**
	 * Clears all loaded MobHeads from the internal list.
	 */
	public void clear() {
		mobHeads.clear();
	}

	/**
	 * Loads all MobHead JSON files from a specified directory.
	 *
	 * @param directoryPath The path to the directory containing JSON files to load.
	 */
	public void loadAllMobHeads(String directoryPath) {
		File directory = new File(directoryPath);
		File[] jsonFiles = directory.listFiles((dir, name) -> name.endsWith(".json"));
		if (jsonFiles != null) {
			for (File file : jsonFiles) {
				loadFromJsonFile(file.getAbsolutePath());
			}
		}
	}
}