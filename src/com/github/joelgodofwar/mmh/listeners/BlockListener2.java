package com.github.joelgodofwar.mmh.listeners;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import com.github.joelgodofwar.mmh.util.datatypes.JsonDataType;

import io.papermc.lib.PaperLib;
import io.papermc.lib.features.blockstatesnapshot.BlockStateSnapshotResult;

class BlockListener2 implements Listener  {
	public final static Logger logger = Logger.getLogger("Minecraft");
	private Plugin plugin;
	
	BlockListener2(Plugin plugin) {
	this.plugin = plugin;
	}

	// Persistent Heads
	private final NamespacedKey NAME_KEY = new NamespacedKey(plugin, "head_name");
	private final NamespacedKey LORE_KEY = new NamespacedKey(plugin, "head_lore");
	private final PersistentDataType<String,String[]> LORE_PDT = new JsonDataType<>(String[].class);
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		@Nonnull ItemStack headItem = event.getItemInHand();
		if (headItem.getType() != Material.PLAYER_HEAD) return;
		ItemMeta meta = headItem.getItemMeta();
		if (meta == null) return;
		@Nonnull String name = meta.getDisplayName();
		@Nullable List<String> lore = meta.getLore();
		@Nonnull Block block = event.getBlockPlaced();
		// NOTE: Not using snapshots is broken: https://github.com/PaperMC/Paper/issues/3913
		BlockStateSnapshotResult blockStateSnapshotResult = PaperLib.getBlockState(block, true);
		TileState skullState = (TileState) blockStateSnapshotResult.getState();
		@Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
		skullPDC.set(NAME_KEY, PersistentDataType.STRING, name);
		if (lore != null) skullPDC.set(LORE_KEY, LORE_PDT, lore.toArray(new String[0]));
		if (blockStateSnapshotResult.isSnapshot()) skullState.update();
		String strLore = "null";
		if(lore != null){ strLore = lore.toString(); }
		log(Level.INFO, "Player " + event.getPlayer().getName() + " placed a head named \"" + name + "\" with lore=\'" + strLore + "\' at " + event.getBlockPlaced().getLocation());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockDropItemEvent(BlockDropItemEvent event) {
		@Nonnull BlockState blockState = event.getBlockState();
		Material blockType = blockState.getType();
		if (blockType != Material.PLAYER_HEAD && blockType != Material.PLAYER_WALL_HEAD) return;
		TileState skullState = (TileState) blockState;
		@Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
		@Nullable String name = skullPDC.get(NAME_KEY, PersistentDataType.STRING);
		@Nullable String[] lore = skullPDC.get(LORE_KEY, LORE_PDT);
		if (name == null) return;
		for (Item item: event.getItems()) { // Ideally should only be one...
			@Nonnull ItemStack itemstack = item.getItemStack();
			if (itemstack.getType() == Material.PLAYER_HEAD) {
			@Nullable ItemMeta meta = itemstack.getItemMeta();
			if (meta == null) continue; // This shouldn't happen
			meta.setDisplayName(name);
			if (lore != null) meta.setLore(Arrays.asList(lore));
			itemstack.setItemMeta(meta);
			}
		}
		log(Level.INFO, "BDIE - Persistent head completed.");
	}
	
	/**
	 * Prevents player from removing player-head NBT by water logging them
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		handleBlock(event.getBlock(), event, false);
	}
	
	/**
	 * Prevents player from removing player-head NBT using running water
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onLiquidFlow(BlockFromToEvent event) {
		handleBlock(event.getToBlock(), event, true);
	}
	
	/*
	 * Prevents explosion from removing player-head NBT using an explosion
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockExplosion(BlockExplodeEvent event) {
		handleExplosionEvent(event.blockList(), event.getYield());
	}
	
	/*
	 * Prevents entity from removing player-head NBT using an explosion
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityExplosion(EntityExplodeEvent event) {
		handleExplosionEvent(event.blockList(), event.getYield());
	}
	
	/*
	 * Prevents piston extending from removing NBT data.
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPistonExtendEvent(BlockPistonExtendEvent event) {
		List<Block> blocks = event.getBlocks();
		Iterator<Block> iter = blocks.iterator();
		while (iter.hasNext()) {
			Block block = iter.next();
			if (block.getState() instanceof Skull) { //if (block.getState() instanceof Skull && random.nextFloat() <= explosionYield)
				handleBlock(block, null, false);
				iter.remove();
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void handleExplosionEvent(@Nonnull final List<Block> blocksExploded, final float explosionYield) {
		final Random random = ThreadLocalRandom.current();
		Iterator<Block> iter = blocksExploded.iterator();
		while (iter.hasNext()) {
			Block block = iter.next();
			if (block.getState() instanceof Skull) { //if (block.getState() instanceof Skull && random.nextFloat() <= explosionYield)
				handleBlock(block, null, false);
				iter.remove();
			}
		}
	}
	
	private void handleBlock(Block block, Cancellable event, boolean cancelEvent) {
		@Nonnull BlockState blockState = block.getState();
		if (blockState.getType() != Material.PLAYER_HEAD && blockState.getType() != Material.PLAYER_WALL_HEAD) return;
		Skull skullState = (Skull) blockState;
		@Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
		@Nullable String name = skullPDC.get(NAME_KEY, PersistentDataType.STRING);
		@Nullable String[] lore = skullPDC.get(LORE_KEY, LORE_PDT);
		if (name == null) return;
		@Nonnull Optional<ItemStack> skullStack = block.getDrops().stream().filter(is -> is.getType() == Material.PLAYER_HEAD).findAny();
		if (skullStack.isPresent()) {
			if (updateDrop(block, name, lore, skullStack.get())) return; // This shouldn't happen
			if (cancelEvent) event.setCancelled(true);
		}

		BlockState blockState1 = block.getWorld().getBlockAt(block.getLocation()).getState();
		blockState1.update(true, true);
		log(Level.INFO, "HB - Persistent head completed.");
	}
	
	@SuppressWarnings("unused")
	private void handleEvent(Supplier<Block> blockSupplier, Cancellable event, boolean cancelEvent) {
		Block block = blockSupplier.get();
		@Nonnull BlockState blockState = block.getState();
		if (blockState.getType() != Material.PLAYER_HEAD && blockState.getType() != Material.PLAYER_WALL_HEAD) return;
		Skull skullState = (Skull) blockState;
		@Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
		@Nullable String name = skullPDC.get(NAME_KEY, PersistentDataType.STRING);
		@Nullable String[] lore = skullPDC.get(LORE_KEY, LORE_PDT);
		if (name == null) return;
		@Nonnull Optional<ItemStack> skullStack = block.getDrops().stream().filter(is -> is.getType() == Material.PLAYER_HEAD).findAny();
		if (skullStack.isPresent()) {
			if (updateDrop(block, name, lore, skullStack.get())) return; // This shouldn't happen
			if (cancelEvent) event.setCancelled(true);
		}

		BlockState blockState1 = block.getWorld().getBlockAt(block.getLocation()).getState();
		blockState1.update(true, true);
		log(Level.INFO, "HE - Persistent head completed.");
	}

	private boolean updateDrop(Block block, @Nullable String name, @Nullable String[] lore, @Nonnull ItemStack itemstack) {
		@Nullable ItemMeta meta = itemstack.getItemMeta();
		if (meta == null) return true;
		meta.setDisplayName(name);
		if (lore != null) meta.setLore(Arrays.asList(lore));
		itemstack.setItemMeta(meta);

		block.getWorld().dropItemNaturally(block.getLocation(), itemstack);
		block.getDrops().clear();
		block.setType(Material.AIR);
		log(Level.INFO, "UD - Persistent head completed.");
		return false;
	}
	// Persistent Heads
	
	public void log(String string) {
		log(Level.INFO, string);
	}
	
	public	void log(Level level, String dalog){ //TODO: Log
		PluginDescriptionFile pdfFile = plugin.getDescription();
		logger.log(level, ChatColor.YELLOW + pdfFile.getName() + " v" + pdfFile.getVersion() + ChatColor.RESET + " " + dalog );
	}
}
