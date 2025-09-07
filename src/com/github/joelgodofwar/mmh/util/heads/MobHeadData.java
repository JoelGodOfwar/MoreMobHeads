package com.github.joelgodofwar.mmh.util.heads;

import java.util.ArrayList;
import java.util.List;

public class MobHeadData {
	private final String displayName;
	private final String langName;
	private final String texture;
	private final String uuid;
	private final List<String> lore;
	private final String noteblockSound;
	private final int quantity;
	private double chance;
	private final String minMCVersion;
	private final String maxMCVersion;
	private final String filePath;

	public MobHeadData(String displayName, String langName, String texture, String uuid, List<String> lore,
			String noteblockSound, int quantity, double chance, String minMCVersion, String maxMCVersion, String filePath) {
		this.displayName = displayName;
		this.langName = langName;
		this.texture = texture;
		this.uuid = uuid;
		this.lore = lore != null ? new ArrayList<>(lore) : new ArrayList<>();
		this.noteblockSound = noteblockSound;
		this.quantity = quantity;
		this.chance = chance;
		this.minMCVersion = minMCVersion;
		this.maxMCVersion = maxMCVersion;
		this.filePath = filePath;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getLangName() {
		return langName;
	}

	public String getTexture() {
		return texture;
	}

	public String getUuid() {
		return uuid;
	}

	public ArrayList<String> getLore() {
		return new ArrayList<>(lore);
	}

	public String getNoteblockSound() {
		return noteblockSound;
	}

	public int getQuantity() {
		return quantity;
	}

	public double getChance() {
		return chance;
	}

	public String getMinMCVersion() {
		return minMCVersion;
	}

	public String getMaxMCVersion() { // Added
		return maxMCVersion;
	}

	public String getFilePath() { return filePath; }

	// Setter for chance (needed for apply command)
	public void setChance(double chance) { this.chance = chance; }
}