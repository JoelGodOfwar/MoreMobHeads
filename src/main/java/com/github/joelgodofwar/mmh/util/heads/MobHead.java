package com.github.joelgodofwar.mmh.util.heads;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

public class MobHead {
	private final MobHeadData data;
	private final ItemStack head;

	public MobHead(MobHeadData data, ItemStack head) {
		this.data = data;
		this.head = head;
	}

	public String getDisplayName() {
		return data.getDisplayName();
	}

	public String getNoteblockSound() {
		return data.getNoteblockSound();
	}

	public ArrayList<String> getLore() {
		return data.getLore();
	}

	public ItemStack getHead() {
		return head.clone();
	}

	public int getQuantity() {
		return data.getQuantity();
	}

	public String getMinMCVersion() {
		return data.getMinMCVersion();
	}

	public String getMaxMCVersion() { // Added
		return data.getMaxMCVersion();
	}

	public MobHeadData getData() {
		return data;
	}

	public String getLangName() {
		return data.getLangName();
	}
}