package com.github.joelgodofwar.mmh.util.heads;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class BlockHeadData {
	private final String langName;
	private final String displayName;
	private final String noteblockSound;
	private final List<String> lore;
	private final double chance;
	private final double defaultChance;
	private final String texture;
	private final String uuid; // Added
	private final int quantity;
	private final ItemStack price1;
	private final ItemStack price2;
	private final int maxUses;
	private final String minMCVersion;

	public BlockHeadData(String langName, String displayName, String noteblockSound, List<String> lore,
			double chance, double defaultChance, String texture, String uuid, int quantity,
			ItemStack price1, ItemStack price2, int maxUses, String minMCVersion) {
		this.langName = langName;
		this.displayName = displayName;
		this.noteblockSound = noteblockSound;
		this.lore = lore != null ? new ArrayList<>(lore) : new ArrayList<>();
		this.chance = chance;
		this.defaultChance = defaultChance;
		this.texture = texture;
		this.uuid = uuid;
		this.quantity = quantity;
		this.price1 = price1;
		this.price2 = price2;
		this.maxUses = maxUses;
		this.minMCVersion = minMCVersion;
	}

	public String getLangName() {
		return langName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getNoteblockSound() {
		return noteblockSound;
	}

	public ArrayList<String> getLore() {
		return new ArrayList<>(lore);
	}

	public double getChance() {
		return chance;
	}

	public double getDefaultChance() {
		return defaultChance;
	}

	public String getTexture() {
		return texture;
	}

	public String getUuid() {
		return uuid;
	}

	public int getQuantity() {
		return quantity;
	}

	public ItemStack getPrice1() {
		return price1;
	}

	public ItemStack getPrice2() {
		return price2;
	}

	public int getMaxUses() {
		return maxUses;
	}

	public String getMinMCVersion() {
		return minMCVersion;
	}
}