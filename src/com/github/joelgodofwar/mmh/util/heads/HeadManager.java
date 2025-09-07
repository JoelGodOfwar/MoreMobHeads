package com.github.joelgodofwar.mmh.util.heads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public class HeadManager {
	// Head maps
	public Map<String, MobHead> loadedMobHeads = new HashMap<>();
	public Map<String, PlayerHead> loadedPlayerHeads = new HashMap<>();
	public Map<String, BlockHead> loadedBlockHeads = new HashMap<>();
	public Map<String, MiniBlock> loadedMiniBlocks = new HashMap<>();
	// GUI lists
	public List<ItemStack> mobHeadsList = new ArrayList<>();
	public List<ItemStack> playerHeadsList = new ArrayList<>();
	public List<ItemStack> blockHeadsList = new ArrayList<>();
	public List<ItemStack> miniBlocksList = new ArrayList<>();
	// Name lists
	public List<String> mobHeadsNameList = new ArrayList<>();
	public List<String> blockHeadsNameList = new ArrayList<>();
	// Shared variables for GiveHeadCommand
	public List<ItemStack> jsonPlayerHeads = new ArrayList<>();
	public Map<UUID, ItemStack> onlinePlayerHeads = new HashMap<>();
	public Map<Player, Player> pendingTargets = new HashMap<>();
	public Map<Player, String> pendingHeadTypes = new HashMap<>();
	// Shared variables for Wandering Trader trades
	public List<MerchantRecipe> playerhead_recipes = new ArrayList<>();
	public List<MerchantRecipe> blockhead_recipes = new ArrayList<>();

	private static final MobHead NOT_FOUND_HEAD = createNotFoundHead();

	private static MobHead createNotFoundHead() {
		String nameDEF = "Name Not Found";
		String uuidDEF = "40404040-4040-4040-4040-404040404040";
		String textureDEF = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY1NDljZjhiNWE1MWUwZmFkM2MyMmY5YTY3ZDg3Mjc2ZDdhMzdiZjY0Zjk1ODgwMDI2ZDlkMzE5ZTMyMjhiNSJ9fX0=";
		ArrayList<String> loreDEF = new ArrayList<String>(Arrays.asList("§cNotify an Admin§e!"));
		MobHeadData data = new MobHeadData(nameDEF, "not_found", textureDEF, uuidDEF, loreDEF, "entity.player.hurt", 1, 100.0, null, null, null);
		ItemStack head = HeadUtils.makeHead(nameDEF, textureDEF, uuidDEF, loreDEF, "entity.player.hurt");
		return new MobHead(data, head);
	}

	public MobHead getMobOrDefault(String key) {
		if ((key == null) || key.isEmpty()) {
			MoreMobHeads.getInstance().LOGGER.warn("Invalid MobHead key: " + key);
			return NOT_FOUND_HEAD;
		}
		MobHead mobHead = loadedMobHeads.get(key);
		if (mobHead == null) {
			MoreMobHeads.getInstance().LOGGER.warn("No MobHead found for key: " + key);
			return NOT_FOUND_HEAD;
		}
		return mobHead;
	}

	// Getter methods
	public Map<String, MobHead> getLoadedMobHeads() {
		return new HashMap<>(loadedMobHeads);
	}

	public Map<String, PlayerHead> getLoadedPlayerHeads() {
		return new HashMap<>(loadedPlayerHeads);
	}

	public Map<String, BlockHead> getLoadedBlockHeads() {
		return new HashMap<>(loadedBlockHeads);
	}

	public Map<String, MiniBlock> getLoadedMiniBlocks() {
		return new HashMap<>(loadedMiniBlocks);
	}

	public List<ItemStack> getMobHeadsList() {
		return new ArrayList<>(mobHeadsList);
	}

	public List<ItemStack> getPlayerHeadsList() {
		return new ArrayList<>(playerHeadsList);
	}

	public List<ItemStack> getBlockHeadsList() {
		return new ArrayList<>(blockHeadsList);
	}

	public List<ItemStack> getMiniBlocksList() {
		return new ArrayList<>(miniBlocksList);
	}

	public List<String> getMobHeadsNameList() {
		return new ArrayList<>(mobHeadsNameList);
	}

	public List<String> getBlockHeadsNameList() {
		return new ArrayList<>(blockHeadsNameList);
	}

	public List<ItemStack> getJsonPlayerHeads() {
		return jsonPlayerHeads;
	}

	public Map<UUID, ItemStack> getOnlinePlayerHeads() {
		return onlinePlayerHeads;
	}

	public Map<Player, Player> getPendingTargets() {
		return pendingTargets;
	}

	public Map<Player, String> getPendingHeadTypes() {
		return pendingHeadTypes;
	}

	public List<MerchantRecipe> getPlayerheadRecipes() {
		return new ArrayList<>(playerhead_recipes);
	}

	public List<MerchantRecipe> getBlockheadRecipes() {
		return new ArrayList<>(blockhead_recipes);
	}

	// Direct access for NewEventHandler_1_20_R2 to populate the state
	public Map<String, MobHead> loadedMobHeads() {
		return loadedMobHeads;
	}

	public Map<String, PlayerHead> loadedPlayerHeads() {
		return loadedPlayerHeads;
	}

	public Map<String, BlockHead> loadedBlockHeads() {
		return loadedBlockHeads;
	}

	public Map<String, MiniBlock> loadedMiniBlocks() {
		return loadedMiniBlocks;
	}

	public List<ItemStack> mobHeadsList() {
		return mobHeadsList;
	}

	public List<ItemStack> playerHeadsList() {
		return playerHeadsList;
	}

	public List<ItemStack> blockHeadsList() {
		return blockHeadsList;
	}

	public List<ItemStack> miniBlocksList() {
		return miniBlocksList;
	}

	public List<String> mobHeadsNameList() {
		return mobHeadsNameList;
	}

	public List<String> blockHeadsNameList() {
		return blockHeadsNameList;
	}

	public List<MerchantRecipe> playerheadRecipes() {
		return playerhead_recipes;
	}

	public List<MerchantRecipe> blockheadRecipes() {
		return blockhead_recipes;
	}
}