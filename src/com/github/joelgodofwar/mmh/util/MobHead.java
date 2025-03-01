package com.github.joelgodofwar.mmh.util;

import java.util.ArrayList;

/**
 * Represents a mob head with its display properties, drop chance, and associated data.
 * All fields are immutable and set during construction.
 */
public class MobHead {
	private final String displayName;
	private final String langName;
	private final String texture;
	private final String uuid;
	private final ArrayList<String> lore;
	private final String noteblockSound;
	private final int quantity;
	private final double chance;

	/**
	 * Constructs a new MobHead with the specified properties.
	 * Lore is defensively copied to prevent external modification, and chance is clamped to 0.0–100.0.
	 *
	 * @param displayName The display name of the mob head.
	 * @param langName The language key or identifier for the mob head.
	 * @param texture The Base64-encoded texture string for the mob head.
	 * @param uuid The UUID associated with the mob head.
	 * @param lore A list of lore strings to display on the mob head item; null becomes an empty list.
	 * @param noteblockSound The sound played when the head is used as a note block.
	 * @param quantity The number of heads dropped when the drop occurs.
	 * @param chance The percentage chance (0.0–100.0) of the head dropping.
	 */
	public MobHead(String displayName, String langName, String texture, String uuid, ArrayList<String> lore,
			String noteblockSound, int quantity, double chance) {
		this.displayName = displayName;
		this.langName = langName;
		this.texture = texture;
		this.uuid = uuid;
		this.lore = lore != null ? new ArrayList<>(lore) : new ArrayList<>(); // Defensive copy, never null
		this.noteblockSound = noteblockSound;
		this.quantity = quantity;
		this.chance = Math.max(0.0, Math.min(100.0, chance)); // Clamp to 0.0–100.0
	}

	/**
	 * Gets the display name of the mob head.
	 *
	 * @return The display name.
	 */
	public String getDisplayName() { return displayName; }

	/**
	 * Gets the language key or identifier for the mob head.
	 *
	 * @return The language name.
	 */
	public String getLangName() { return langName; }

	/**
	 * Gets the Base64-encoded texture string for the mob head.
	 *
	 * @return The texture string.
	 */
	public String getTexture() { return texture; }

	/**
	 * Gets the UUID associated with the mob head.
	 *
	 * @return The UUID string.
	 */
	public String getUuid() { return uuid; }

	/**
	 * Gets the lore lines for the mob head.
	 *
	 * @return A new ArrayList containing the lore, protecting the internal list.
	 */
	public ArrayList<String> getLore() { return new ArrayList<>(lore); }

	/**
	 * Gets the sound played when the head is used as a note block.
	 *
	 * @return The note block sound identifier.
	 */
	public String getNoteblockSound() { return noteblockSound; }

	/**
	 * Gets the quantity of heads dropped when the drop occurs.
	 *
	 * @return The quantity.
	 */
	public int getQuantity() { return quantity; }

	/**
	 * Gets the percentage chance of the head dropping.
	 *
	 * @return The chance value (0.0–100.0).
	 */
	public double getChance() { return chance; }
}