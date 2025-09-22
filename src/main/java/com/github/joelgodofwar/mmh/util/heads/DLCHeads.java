package com.github.joelgodofwar.mmh.util.heads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import com.github.joelgodofwar.mmh.MoreMobHeads;

public class DLCHeads {
	// Data class to store DLC head info
	private static class DLCHeadInfo {
		final String displayName;
		final String texture;
		final String uuid;
		final String dlcName;
		final List<String> lore;

		DLCHeadInfo(String displayName, String texture, String uuid, String dlcName, List<String> lore) {
			this.displayName = displayName;
			this.texture = texture;
			this.uuid = uuid;
			this.dlcName = dlcName;
			this.lore = lore;
		}
	}

	// List of DLC heads (easy to add new entries)
	private static final List<DLCHeadInfo> DLC_HEADS = Arrays.asList(
			new DLCHeadInfo(
					"§7Mini Pale Oak Log (Horizontal)",
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDQ5YWY1NDRkMDcwMTZiMGQ5ZWNhZjJhODkwYzIxNTBjNDg2ZjM0NWQyZTExNmUzNTg3OWYzYmU2NzIyMDhjZCJ9fX0=",
					"51384948-e6ed-49de-bdc9-073edfafdba1",
					"mmh_all_logs",
					Arrays.asList(
							"§eWant more log heads?",
							"§eAsk your Admin to get DLC:",
							"§9mmh_all_logs (§f48 Heads§9)",
							"§eat §fhttps://ko-fi.com/joelgodofwar"
							)
					),
			new DLCHeadInfo(
					"§7Mini Cyan Shulker Box",
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzcxMTNkMWNmN2FjNjIyNDNkOGJiNDdjZTY2MjU0NTYwMTVmN2U4MmE5MmIwNjU2Y2M2YWQ2MmY2ZTk3MjI1MiJ9fX0=",
					"2efdf31a-3cc3-4455-8caf-944f1073087e",
					"mmh_all_shulker_boxes",
					Arrays.asList(
							"§eWant more shulker heads?",
							"§eAsk your Admin to get DLC:",
							"§9mmh_all_shulker_boxes (§f17 Heads§9)",
							"§eat §fhttps://ko-fi.com/joelgodofwar"
							)
					),
			new DLCHeadInfo(
					"§7Mini Pale Oak Wood",
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWJiMTYwMjNjNmYyZGI3YWViNWU4NTViY2RmOTA1ZjU1Mzg1ZDM0MzkwY2U3YzgxNWM1NjFiZTI4MmQ4NTFjZCJ9fX0=",
					"0453928f-b9e8-4c5f-8247-2f7532e77d5d",
					"mmh_all_woods",
					Arrays.asList(
							"§eWant more wood heads?",
							"§eAsk your Admin to get DLC:",
							"§9mmh_all_woods (§f22 Heads§9)",
							"§eat §fhttps://ko-fi.com/joelgodofwar"
							)
					)
			);

	public static List<ItemStack> getDLCHeads(MoreMobHeads mmh) {
		List<ItemStack> heads = new ArrayList<>();
		for (DLCHeadInfo info : DLC_HEADS) {
			if (!mmh.isDLCInstalled(info.dlcName)) {
				ItemStack head = HeadUtils.makeHead(
						info.displayName,
						info.texture,
						info.uuid,
						new ArrayList<>(info.lore),
						null
						);
				heads.add(head);
			}
		}
		return heads;
	}

	public static List<MerchantRecipe> getDLCAdvertisementTrades(MoreMobHeads mmh) {
		List<MerchantRecipe> trades = new ArrayList<>();
		for (DLCHeadInfo info : DLC_HEADS) {
			if (!mmh.isDLCInstalled(info.dlcName)) {
				ItemStack head = HeadUtils.makeHead(
						info.displayName,
						info.texture,
						info.uuid,
						new ArrayList<>(info.lore),
						null
						);
				ItemStack barrier = new ItemStack(Material.BARRIER, 1);
				MerchantRecipe trade = new MerchantRecipe(head, 0, 0, false);
				trade.addIngredient(barrier);
				trades.add(trade);
			}
		}
		return trades;
	}

	// Getter for DLC names (for later DLC installation check)
	public static List<String> getDLCNames() {
		List<String> dlcNames = new ArrayList<>();
		for (DLCHeadInfo info : DLC_HEADS) {
			dlcNames.addAll(Arrays.asList(info.dlcName.split(",")));
		}
		return dlcNames;
	}
}