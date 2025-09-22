package com.github.joelgodofwar.mmh.events;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URL;
import java.util.List;
import com.github.joelgodofwar.mmh.util.heads.HeadUtils;

/**
 * Fired when the MoreMobHeads plugin drops a mob head for a player.
 * This event allows other plugins to modify the head's properties or cancel the drop.
 */
public class MobHeadDropEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Entity entity;
    private final Player player;
    private ItemStack head;
    private String displayName;
    private String texture;
    private String uuid;
    private List<String> lore;
    private String noteblockSound;
    private boolean cancelled;

    /**
     * Constructs a new MobHeadDropEvent.
     *
     * @param entity        The entity that dropped the head (e.g., zombie, creeper).
     * @param player        The player who caused the head drop, or null if no player is involved.
     * @param head          The ItemStack representing the mob head being dropped.
     * @param displayName   The display name of the head, or null for vanilla heads.
     * @param texture       The texture of the head, or null for vanilla heads.
     * @param uuid          The UUID associated with the head, or null if not applicable.
     * @param lore          The lore of the head, or null if none.
     * @param noteblockSound The note block sound associated with the head, or null if none.
     */
    public MobHeadDropEvent(@Nonnull Entity entity, @Nullable Player player, @Nonnull ItemStack head,
                            @Nullable String displayName, @Nullable String texture, @Nullable String uuid,
                            @Nullable List<String> lore, @Nullable String noteblockSound) {
        this.entity = entity;
        this.player = player;
        this.head = head;
        this.displayName = displayName;
        this.texture = texture;
        this.uuid = uuid;
        this.lore = lore;
        this.noteblockSound = noteblockSound;
        this.cancelled = false;
    }

    /**
     * Gets the entity that dropped the head.
     *
     * @return The entity (e.g., zombie, creeper).
     */
    @Nonnull
    public Entity getEntity() {
        return entity;
    }

    /**
     * Gets the player who caused the head drop, if applicable.
     *
     * @return The player, or null if no player is involved.
     */
    @Nullable
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the ItemStack representing the mob head being dropped.
     *
     * @return The mob head ItemStack.
     */
    @Nonnull
    public ItemStack getHead() {
        return head;
    }

    /**
     * Sets the ItemStack representing the mob head.
     *
     * @param head The new ItemStack for the head.
     */
    public void setHead(@Nonnull ItemStack head) {
        this.head = head;
    }

    /**
     * Gets the display name of the head.
     *
     * @return The display name, or null for vanilla heads.
     */
    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name of the head.
     *
     * @param displayName The new display name, or null for vanilla heads.
     */
    public void setDisplayName(@Nullable String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the texture of the head.
     *
     * @return The texture, or null for vanilla heads.
     */
    @Nullable
    public String getTexture() {
        return texture;
    }

    /**
     * Sets the texture of the head.
     *
     * @param texture The new texture, or null for vanilla heads.
     */
    public void setTexture(@Nullable String texture) {
        this.texture = texture;
    }

    /**
     * Gets the UUID associated with the head.
     *
     * @return The UUID, or null if not applicable.
     */
    @Nullable
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the UUID associated with the head.
     *
     * @param uuid The new UUID, or null if not applicable.
     */
    public void setUuid(@Nullable String uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the lore of the head.
     *
     * @return The lore, or null if none.
     */
    @Nullable
    public List<String> getLore() {
        return lore;
    }

    /**
     * Sets the lore of the head.
     *
     * @param lore The new lore, or null if none.
     */
    public void setLore(@Nullable List<String> lore) {
        this.lore = lore;
    }

    /**
     * Gets the note block sound associated with the head.
     *
     * @return The note block sound, or null if none.
     */
    @Nullable
    public String getNoteblockSound() {
        return noteblockSound;
    }

    /**
     * Sets the note block sound associated with the head.
     *
     * @param noteblockSound The new note block sound, or null if none.
     */
    public void setNoteblockSound(@Nullable String noteblockSound) {
        this.noteblockSound = noteblockSound;
    }

    /**
     * Checks if the head is a vanilla Minecraft head (e.g., CREEPER_HEAD, ZOMBIE_HEAD).
     *
     * @return true if the head's Material is not PLAYER_HEAD, false otherwise.
     */
    public boolean isVanilla() {
        return head.getType() != Material.PLAYER_HEAD;
    }

    /**
     * Gets the skin URL from the Base64-encoded texture of the head.
     * <p>
     * Converts the Base64 texture string to its original URL using
     * {@link HeadUtils#convertBase64ToURL(String)}.
     *
     * @return The skin URL as a String, or null if the texture is null or invalid.
     */
    public String getSkinURL() {
        if (this.texture == null) {
            return null;
        }
        try {
            URL url = HeadUtils.convertBase64ToURL(this.texture);
            return url.toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    @Nonnull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Gets the HandlerList for this event.
     *
     * @return The HandlerList.
     */
    @Nonnull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}