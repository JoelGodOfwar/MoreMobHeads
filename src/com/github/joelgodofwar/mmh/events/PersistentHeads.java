package com.github.joelgodofwar.mmh.events;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.common.PluginLibrary;

import com.github.joelgodofwar.mmh.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.mmh.common.error.Report;
import com.github.joelgodofwar.mmh.util.SkinUtils;
import lib.github.joelgodofwar.coreutils.util.common.PluginLogger;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Handles events related to persistent player head data, ensuring that custom head metadata
 * (name, lore, UUID, texture, and sound) is preserved across various block-related actions
 * such as placement, breaking, waterlogging, explosions, and piston movements.
 * This class manages the persistence of head data in the block's PersistentDataContainer
 * and restores it to dropped items when necessary.
 */
public class PersistentHeads implements Listener {
    private final MoreMobHeads plugin;
    private final PluginLogger LOGGER;
    private final DetailedErrorReporter reporter;

    /**
     * Constructs a new PersistentHeads event handler.
     *
     * @param plugin The MoreMobHeads plugin instance, providing access to configuration,
     *               logger, and persistent data keys.
     */
    public PersistentHeads(MoreMobHeads plugin) {
        this.plugin = plugin;
        this.LOGGER = plugin.LOGGER;
        this.reporter = MoreMobHeads.reporter;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Handles the placement of a player head block, storing its metadata (name, lore, UUID,
     * texture, and sound) in the block's PersistentDataContainer.
     *
     * @param event The BlockPlaceEvent triggered when a player places a block.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        SkinUtils skinUtils = new SkinUtils();
        try {
            @Nonnull ItemStack headItem = event.getItemInHand();
            if (headItem.getType() != Material.PLAYER_HEAD) {
                return;
            }
            ItemMeta meta = headItem.getItemMeta();
            if (meta == null) {
                return;
            }
            @Nonnull String name = meta.getDisplayName();
            plugin.logDebug("BPE name = " + name);
            @Nullable List<String> lore = meta.getLore();
            plugin.logDebug("BPE lore = " + lore);
            String uuid = skinUtils.getHeadUUID(headItem);
            String texture = skinUtils.getHeadTexture(headItem);
            NamespacedKey nskSound = skinUtils.getHeadNoteblockSound(headItem);
            String sound = null;
            if (nskSound != null) {
                sound = nskSound.toString();
            }
            @Nonnull Block block = event.getBlockPlaced();
            TileState skullState = (TileState) block.getState();
            @Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
            skullPDC.set(plugin.NAME_KEY, PersistentDataType.STRING, name);
            if (lore != null) {
                skullPDC.set(plugin.LORE_KEY, plugin.LORE_PDT, lore.toArray(new String[0]));
            }
            if (uuid != null) {
                skullPDC.set(plugin.UUID_KEY, PersistentDataType.STRING, uuid);
            }
            if (texture != null) {
                skullPDC.set(plugin.TEXTURE_KEY, PersistentDataType.STRING, texture);
            }
            if (sound != null) {
                skullPDC.set(plugin.SOUND_KEY, PersistentDataType.STRING, sound);
            }
            skullState.update();
            String strLore = "no lore";
            if (lore != null) {
                strLore = lore.toString();
            }
            plugin.logDebug("Player " + event.getPlayer().getName() + " placed a head named \"" + name + "\" with lore='" + strLore + "' at " + event.getBlockPlaced().getLocation());
        } catch (Exception exception) {
            reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.BLOCK_PLACE_EVENT_ERROR).error(exception));
        }
    }

    /**
     * Handles the breaking of a player head block, restoring its metadata (name, lore, UUID,
     * texture, and sound) to the dropped ItemStack.
     *
     * @param event The BlockDropItemEvent triggered when a block is broken and drops items.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockDropItemEvent(BlockDropItemEvent event) {
        @Nonnull BlockState blockState = event.getBlockState();
        Material blockType = blockState.getType();
        if ((blockType != Material.PLAYER_HEAD) && (blockType != Material.PLAYER_WALL_HEAD)) {
            return;
        }
        TileState skullState = (TileState) blockState;
        @Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
        @Nullable String name = skullPDC.get(plugin.NAME_KEY, PersistentDataType.STRING);
        if (name != null) {
            plugin.logDebug("BDIE name = " + name);
        }
        @Nullable String[] lore = skullPDC.get(plugin.LORE_KEY, plugin.LORE_PDT);
        if (lore != null) {
            plugin.logDebug("BDIE lore = " + Arrays.toString(lore));
        }
        String uuid = skullPDC.get(plugin.UUID_KEY, PersistentDataType.STRING);
        if (uuid != null) {
            plugin.logDebug("BDIE uuid = " + uuid);
        }
        String texture = skullPDC.get(plugin.TEXTURE_KEY, PersistentDataType.STRING);
        if (texture != null) {
            plugin.logDebug("BDIE texture = " + texture);
        }
        String sound = skullPDC.get(plugin.SOUND_KEY, PersistentDataType.STRING);
        if (sound != null) {
            plugin.logDebug("BDIE sound = " + sound);
        }
        if (name == null) {
            return;
        }
        for (Item item : event.getItems()) {
            @Nonnull ItemStack itemstack = item.getItemStack();
            if (itemstack.getType() == Material.PLAYER_HEAD) {
                @Nullable ItemMeta meta = itemstack.getItemMeta();
                if (meta == null) {
                    continue;
                }
                meta.setDisplayName(name);
                if (lore != null) {
                    meta.setLore(Arrays.asList(lore));
                }
                skullPDC.set(plugin.NAME_KEY, PersistentDataType.STRING, name);
                if (lore != null) {
                    skullPDC.set(plugin.LORE_KEY, plugin.LORE_PDT, lore);
                }
                if (uuid != null) {
                    skullPDC.set(plugin.UUID_KEY, PersistentDataType.STRING, uuid);
                }
                if (texture != null) {
                    skullPDC.set(plugin.TEXTURE_KEY, PersistentDataType.STRING, texture);
                }
                if (sound != null) {
                    skullPDC.set(plugin.SOUND_KEY, PersistentDataType.STRING, sound);
                }
                itemstack.setItemMeta(meta);
                itemstack.setItemMeta(meta);
            }
        }
        plugin.logDebug("BDIE - Persistent head completed.");
    }

    /**
     * Prevents removal of player head NBT data when a player waterlogs a head using a bucket.
     *
     * @param event The PlayerBucketEmptyEvent triggered when a player empties a bucket.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        handleBlock(event.getBlock(), event, false);
    }

    /**
     * Prevents removal of player head NBT data when flowing water affects a head.
     *
     * @param event The BlockFromToEvent triggered when liquid flows to a new block.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onLiquidFlow(BlockFromToEvent event) {
        handleBlock(event.getToBlock(), event, true);
    }

    /**
     * Prevents removal of player head NBT data during a block explosion.
     *
     * @param event The BlockExplodeEvent triggered when blocks are destroyed by an explosion.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplosion(BlockExplodeEvent event) {
        handleExplosionEvent(event.blockList(), event.getYield());
    }

    /**
     * Prevents removal of player head NBT data during an entity explosion.
     *
     * @param event The EntityExplodeEvent triggered when an entity causes an explosion.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplosion(EntityExplodeEvent event) {
        handleExplosionEvent(event.blockList(), event.getYield());
    }

    /**
     * Prevents removal of player head NBT data when a piston extends and moves a head.
     * Only processes if the 'global_settings.event.piston_extend' config is enabled.
     *
     * @param event The BlockPistonExtendEvent triggered when a piston extends.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPistonExtendEvent(BlockPistonExtendEvent event) {
        if (!plugin.config.getBoolean("global_settings.event.piston_extend", true)) {
            return;
        }
        List<Block> blocks = event.getBlocks();
        Iterator<Block> iter = blocks.iterator();
        try {
            while (iter.hasNext()) {
                Block block = iter.next();
                if (block.getState() instanceof Skull) {
                    handleBlock(block, null, false);
                    iter.remove();
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Processes blocks affected by an explosion, preserving player head NBT data and removing
     * heads from the explosion's block list to prevent destruction.
     *
     * @param blocksExploded The list of blocks affected by the explosion.
     * @param explosionYield The yield of the explosion, currently unused in logic.
     */
    private void handleExplosionEvent(@Nonnull final List<Block> blocksExploded, final float explosionYield) {
        final Random random = ThreadLocalRandom.current();
        Iterator<Block> iter = blocksExploded.iterator();
        try {
            while (iter.hasNext()) {
                Block block = iter.next();
                if (block.getState() instanceof Skull) {
                    handleBlock(block, null, false);
                    iter.remove();
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Handles a block event to preserve player head NBT data, updating dropped items with
     * metadata and optionally canceling the event.
     *
     * @param block The block to process, expected to be a player head or wall head.
     * @param event The cancellable event, if applicable (e.g., water flow, bucket empty).
     * @param cancelEvent Whether to cancel the event if a head is processed.
     */
    private void handleBlock(Block block, Cancellable event, boolean cancelEvent) {
        @Nonnull BlockState blockState = block.getState();
        if ((blockState.getType() != Material.PLAYER_HEAD) && (blockState.getType() != Material.PLAYER_WALL_HEAD)) {
            return;
        }
        Skull skullState = (Skull) blockState;
        @Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
        @Nullable String name = skullPDC.get(plugin.NAME_KEY, PersistentDataType.STRING);
        @Nullable String[] lore = skullPDC.get(plugin.LORE_KEY, plugin.LORE_PDT);
        String uuid = skullPDC.get(plugin.UUID_KEY, PersistentDataType.STRING);
        String texture = skullPDC.get(plugin.TEXTURE_KEY, PersistentDataType.STRING);
        String sound = skullPDC.get(plugin.SOUND_KEY, PersistentDataType.STRING);
        if (name == null) {
            return;
        }
        @Nonnull Optional<ItemStack> skullStack = block.getDrops().stream().filter(is -> is.getType() == Material.PLAYER_HEAD).findAny();
        if (skullStack.isPresent()) {
            if (updateDrop(block, name, lore, uuid, texture, sound, skullStack.get())) {
                return;
            }
            if (cancelEvent) {
                event.setCancelled(true);
            }
        }
        BlockState blockState1 = block.getWorld().getBlockAt(block.getLocation()).getState();
        blockState1.update(true, true);
        plugin.logDebug("HB - Persistent head completed.");
    }

    /**
     * Handles a block event using a block supplier, preserving player head NBT data and
     * optionally canceling the event. Note: This method is currently unused.
     *
     * @param blockSupplier A supplier providing the block to process.
     * @param event The cancellable event, if applicable.
     * @param cancelEvent Whether to cancel the event if a head is processed.
     */
    private void handleEvent(Supplier<Block> blockSupplier, Cancellable event, boolean cancelEvent) {
        Block block = blockSupplier.get();
        @Nonnull BlockState blockState = block.getState();
        if ((blockState.getType() != Material.PLAYER_HEAD) && (blockState.getType() != Material.PLAYER_WALL_HEAD)) {
            return;
        }
        Skull skullState = (Skull) blockState;
        @Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
        @Nullable String name = skullPDC.get(plugin.NAME_KEY, PersistentDataType.STRING);
        @Nullable String[] lore = skullPDC.get(plugin.LORE_KEY, plugin.LORE_PDT);
        String uuid = skullPDC.get(plugin.UUID_KEY, PersistentDataType.STRING);
        String texture = skullPDC.get(plugin.TEXTURE_KEY, PersistentDataType.STRING);
        String sound = skullPDC.get(plugin.SOUND_KEY, PersistentDataType.STRING);
        if (name == null) {
            return;
        }
        @Nonnull Optional<ItemStack> skullStack = block.getDrops().stream().filter(is -> is.getType() == Material.PLAYER_HEAD).findAny();
        if (skullStack.isPresent()) {
            if (updateDrop(block, name, lore, uuid, texture, sound, skullStack.get())) {
                return;
            }
            if (cancelEvent) {
                event.setCancelled(true);
            }
        }
        BlockState blockState1 = block.getWorld().getBlockAt(block.getLocation()).getState();
        blockState1.update(true, true);
        plugin.logDebug("HE - Persistent head completed.");
    }

    /**
     * Updates a dropped player head ItemStack with its metadata (name, lore, UUID, texture,
     * and sound), drops it naturally, and clears the block to air.
     *
     * @param block The block being processed (player head or wall head).
     * @param name The display name of the head.
     * @param lore The lore of the head, may be null.
     * @param uuid The UUID of the head, may be null.
     * @param texture The texture of the head, may be null.
     * @param sound The sound associated with the head, may be null.
     * @param itemstack The ItemStack to update with head metadata.
     * @return true if the ItemMeta is null (indicating failure), false otherwise.
     */
    private boolean updateDrop(Block block, String name, @Nullable String[] lore, String uuid, String texture, String sound, @Nonnull ItemStack itemstack) {
        @Nullable ItemMeta meta = itemstack.getItemMeta();
        if (meta == null) {
            return true;
        }
        meta.setDisplayName(name);
        if (lore != null) {
            meta.setLore(Arrays.asList(lore));
        }
        TileState skullState = (TileState) block.getState();
        @Nonnull PersistentDataContainer skullPDC = skullState.getPersistentDataContainer();
        skullPDC.set(plugin.NAME_KEY, PersistentDataType.STRING, name);
        if (lore != null) {
            skullPDC.set(plugin.LORE_KEY, plugin.LORE_PDT, lore);
        }
        if (uuid != null) {
            skullPDC.set(plugin.UUID_KEY, PersistentDataType.STRING, uuid);
        }
        if (texture != null) {
            skullPDC.set(plugin.TEXTURE_KEY, PersistentDataType.STRING, texture);
        }
        if (sound != null) {
            skullPDC.set(plugin.SOUND_KEY, PersistentDataType.STRING, sound);
        }
        itemstack.setItemMeta(meta);
        block.getWorld().dropItemNaturally(block.getLocation(), itemstack);
        block.getDrops().clear();
        block.setType(Material.AIR);
        plugin.logDebug("UD - Persistent head completed.");
        return false;
    }
}