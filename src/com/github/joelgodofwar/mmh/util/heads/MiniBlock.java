package com.github.joelgodofwar.mmh.util.heads;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

/**
 * Represents a MiniBlock for Stonecutter recipes, containing the head ItemStack (quantity 1)
 * and the price2 ItemStack as the input material.
 */
public class MiniBlock {
	private final BlockHeadData data;
	private final ItemStack head;
	private final ItemStack price2;

	public MiniBlock(BlockHeadData data, ItemStack head, ItemStack price2) {
		this.data = data;
		this.head = head.clone(); // Clone to prevent external modification
		this.price2 = price2 != null ? price2.clone() : null;
	}

	/**
	 * Gets the data associated with this MiniBlock.
	 *
	 * @return The BlockHeadData object.
	 */
	public BlockHeadData getData() {
		return data;
	}

	/**
	 * Gets the skinned player head ItemStack (quantity 1).
	 *
	 * @return A clone of the ItemStack to prevent modification.
	 */
	public ItemStack getHead() {
		return head.clone();
	}

	/**
	 * Gets the input material ItemStack for the Stonecutter recipe (price2).
	 *
	 * @return A clone of the ItemStack to prevent modification.
	 */
	public ItemStack getPrice2() {
		return price2.clone();
	}

	// Convenience methods to access BlockHeadData fields directly
	public String getDisplayName() { return data.getDisplayName(); }
	public String getTexture() { return data.getTexture(); }
	public String getUuid() { return data.getUuid(); }
	public ArrayList<String> getLore() { return data.getLore(); }
	public String getNoteblockSound() { return data.getNoteblockSound(); }
}