package com.github.joelgodofwar.mmh.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.joelgodofwar.mmh.MoreMobHeads;

public class MiniBlockRecipes {
	public static List<MerchantRecipe> blockhead_recipes = new ArrayList<MerchantRecipe>();
	public static int quanity;

	public MiniBlockRecipes(List<MerchantRecipe> recipes, int quanity) {
		MiniBlockRecipes.blockhead_recipes = recipes;
		MiniBlockRecipes.quanity = quanity;
	}

	public static void addRecipes(String name, ItemStack itemstack, String meterial) {
		NamespacedKey recipeKey = new NamespacedKey(MoreMobHeads.getInstance(), "mini_" + name.toLowerCase());
		StonecuttingRecipe srecipe = new StonecuttingRecipe(recipeKey, itemstack, Material.getMaterial(meterial));
		srecipe.setGroup("mini_blocks");
		Recipe existingRecipe = Bukkit.getRecipe(recipeKey);
		if (existingRecipe == null) {
			Bukkit.addRecipe(srecipe);
		}
	}

	public static void register() {

		/**int recipesToCheck = 5; // Number of recipes to check
		for (int i = 0; i < blockhead_recipes.size(); i++) {
			if (i >= recipesToCheck) {
				break; // Exit the loop early after checking the specified number of recipes
			}

			MerchantRecipe recipe = blockhead_recipes.get(i);
			List<ItemStack> ingredients = recipe.getIngredients();
			ItemStack result = recipe.getResult();

			System.out.println("Recipe " + (i + 1) + ":");
			for (int j = 0; j < ingredients.size(); j++) {
				ItemStack ingredient = ingredients.get(j);
				System.out.println("  Ingredient " + (j + 1) + ": " + ingredient.getType().name());
			}
			System.out.println("  Result: " + result.getType().name());
		}//*/

		for (MerchantRecipe recipe : blockhead_recipes) {
			List<ItemStack> ingredients = recipe.getIngredients();
			if (ingredients.size() >= 2) { // Ensure there are at least 2 ingredients
				ItemStack materialStack = ingredients.get(1); // The Material the Head looks like
				ItemStack headStack = recipe.getResult(); // The player_head skinned with a Block's pattern

				Material material = materialStack.getType();
				String materialName = material.name();

				// Check if the display name of the head contains "(Lit)"
				ItemMeta meta = headStack.getItemMeta();
				headStack.setAmount(quanity);
				if ((meta != null) && meta.hasDisplayName() && meta.getDisplayName().toLowerCase().contains("(lit)")) {
					materialName += "_lit";
				}

				addRecipes(materialName, headStack, material.name());
			}
		}
	}

}
