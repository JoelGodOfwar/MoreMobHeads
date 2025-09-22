package com.github.joelgodofwar.mmh.util.heads;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.StonecuttingRecipe;

import com.github.joelgodofwar.mmh.MoreMobHeads;

public class MiniBlockRecipes {
	private final MoreMobHeads plugin;
	private final List<MiniBlock> miniBlocks;
	private final int quantity;

	public MiniBlockRecipes(MoreMobHeads plugin, List<MiniBlock> miniBlocks, int quantity) {
		this.plugin = plugin;
		this.miniBlocks = miniBlocks;
		this.quantity = quantity;
	}

	public void register() {
		for (MiniBlock miniBlock : miniBlocks) {
			// Get the head and input material
			ItemStack head = miniBlock.getHead();
			head.setAmount(quantity); // Set the quantity for the output
			Material material = miniBlock.getPrice2().getType();

			// Register the Stonecutter recipe
			NamespacedKey recipeKey = new NamespacedKey(plugin, "mini_" + miniBlock.getData().getLangName().toLowerCase());
			StonecuttingRecipe recipe = new StonecuttingRecipe(recipeKey, head, material);
			recipe.setGroup("mini_blocks");

			Recipe existingRecipe = Bukkit.getRecipe(recipeKey);
			if (existingRecipe == null) {
				try {
					Bukkit.addRecipe(recipe);
					//plugin.LOGGER.debug("Registered Stonecutter recipe for " + miniBlock.getDisplayName());
				} catch (IllegalStateException e) {
					plugin.LOGGER.warn("Failed to register Stonecutter recipe for " + miniBlock.getDisplayName() + ": " + e.getMessage());
				}
			}
		}
	}
}