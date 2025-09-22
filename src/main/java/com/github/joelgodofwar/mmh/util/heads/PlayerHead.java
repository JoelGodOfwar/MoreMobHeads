package com.github.joelgodofwar.mmh.util.heads;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public class PlayerHead {
	private final PlayerHeadData data;
	private final ItemStack head;
	private final ItemStack price1;
	private final ItemStack price2;
	private final int maxUses; // Added

	public PlayerHead(PlayerHeadData data, ItemStack head, ItemStack price1, ItemStack price2, int maxUses) {
		this.data = data;
		this.head = head;
		this.price1 = price1 != null ? price1.clone() : null;
		this.price2 = price2 != null ? price2.clone() : null;
		this.maxUses = maxUses;
	}

	public String getDisplayName() {
		return data.getDisplayName();
	}

	public String getNoteblockSound() {
		return data.getNoteblockSound();
	}

	public List<String> getLore() {
		return data.getLore();
	}

	public ItemStack getHead() {
		return head.clone();
	}

	public int getQuantity() {
		return data.getQuantity();
	}

	public ItemStack getPrice1() {
		return price1 != null ? price1.clone() : null;
	}

	public ItemStack getPrice2() {
		return price2 != null ? price2.clone() : null;
	}

	public int getMaxUses() {
		return maxUses;
	}

	public String getLangName() {
		return data.getLangName();
	}

}