package com.github.joelgodofwar.mmh.util.heads;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.json.JSONObject;

import com.github.joelgodofwar.mmh.util.datatypes.JsonDataType;

import javax.annotation.Nonnull;

@SuppressWarnings("deprecation")
public class HeadUtils {
	// Persistent data keys
	public static final NamespacedKey NAME_KEY = new NamespacedKey("moremobheads", "head_name");
	public static final NamespacedKey LORE_KEY = new NamespacedKey("moremobheads", "head_lore");
	public static final NamespacedKey UUID_KEY = new NamespacedKey("moremobheads", "head_uuid");
	public static final NamespacedKey TEXTURE_KEY = new NamespacedKey("moremobheads", "head_texture");
	public static final NamespacedKey SOUND_KEY = new NamespacedKey("moremobheads", "head_sound");
	public final static PersistentDataType<String,String[]> LORE_PDT = new JsonDataType<>(String[].class);

	/**
	 * Creates a custom player head ItemStack with specified properties.
	 *
	 * @param displayName the display name to set for the ItemStack
	 * @param texture     the Base64-encoded texture string for the player head
	 * @param uuid        the UUID to associate with the player head
	 * @param lore        the list of lore lines to add to the ItemStack
	 * @param noteblockSound the sound to play when the head is used with a note block
	 * @return the configured ItemStack representing the custom player head
	 */
	public static ItemStack makeHead(String displayName, String texture, String uuid, ArrayList<String> lore, String noteblockSound) {
		PlayerProfile profile = org.bukkit.Bukkit.createPlayerProfile(UUID.fromString(uuid), "");
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		PlayerTextures textures = profile.getTextures();
		URL url = null;
		try {
			url = convertBase64ToURL(texture);
		} catch (Exception exception) {
			System.err.println("Error converting Base64 to URL: " + exception.getMessage());
			exception.printStackTrace();
		}
		textures.setSkin(url);
		profile.setTextures(textures);
        assert meta != null;
        meta.setOwnerProfile(profile);
		if (noteblockSound != null) {
			meta.setNoteBlockSound(NamespacedKey.minecraft(noteblockSound));
		}

		meta.setLore(lore);
		meta.setDisplayName(displayName);

		PersistentDataContainer skullPDC = meta.getPersistentDataContainer();
		if (displayName != null) { skullPDC.set(NAME_KEY, PersistentDataType.STRING, displayName); }
		if (texture != null) { skullPDC.set(TEXTURE_KEY, PersistentDataType.STRING, texture); }
        skullPDC.set(UUID_KEY, PersistentDataType.STRING, uuid);
        if (lore != null) { skullPDC.set(LORE_KEY, LORE_PDT, lore.toArray(new String[0])); }
		if (noteblockSound != null) { skullPDC.set(SOUND_KEY, PersistentDataType.STRING, noteblockSound); }

		head.setItemMeta(meta);
		return head;
	}

	/**
	 * Converts a Base64-encoded string to a URL.
	 *
	 * @param base64 the Base64-encoded string to convert
	 * @return the URL generated from the Base64 string
	 * @throws Exception if the Base64 string is invalid or cannot be converted to a URL
	 */
	public static URL convertBase64ToURL(String base64) throws Exception {
		try {
			return new URL(base64);
		} catch (Exception ignored) {}

		String jsonString = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
		JSONObject jsonObject = new JSONObject(jsonString);
		JSONObject jstextures = jsonObject.getJSONObject("textures");
		JSONObject skin = jstextures.getJSONObject("SKIN");
		String jsurl = skin.getString("url");
		return new URL(jsurl);
	}

	/**
     * Converts a URL to a Base64-encoded texture string for a Minecraft skin.
     * <p>
     * Creates a JSON object with the structure {"textures":{"SKIN":{"url":"&lt;URL&gt;"}}}
     * and encodes it to Base64.
     *
     * @param url The URL of the skin texture (e.g., "<a href="http://textures.minecraft.net/">...</a>...").
     * @return The Base64-encoded texture string.
     * @throws IllegalArgumentException if the URL is null, empty, or invalid.
     */
	public static String convertURLToBase64(String url) throws IllegalArgumentException {
		if (url == null || url.trim().isEmpty()) {
			throw new IllegalArgumentException("URL cannot be null or empty");
		}
		try {
			// Validate URL format
			new URL(url).toURI(); // Throws if URL is invalid
			// Create JSON object
			JSONObject skin = new JSONObject();
			skin.put("url", url);
			JSONObject textures = new JSONObject();
			textures.put("SKIN", skin);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("textures", textures);
			// Convert JSON to string
			String jsonString = jsonObject.toString();
			// Encode to Base64
			return Base64.getEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to convert URL to Base64 texture: " + e.getMessage(), e);
		}
	}

	/**
	 * Checks if the drops list contains any ItemStack with a material name ending with "_HEAD" or "_SKULL" after the last underscore (case-insensitive).
	 *
	 * @param drops The list of ItemStacks to check.
	 * @return true if at least one ItemStack has a material name ending with "_HEAD" or "_SKULL", false otherwise.
	 */
	public static boolean containsHead(@Nonnull List<ItemStack> drops) {
		for (ItemStack item : drops) {
			if (item != null && item.getType() != null && item.getType() != Material.AIR) {
				String materialName = item.getType().name().toLowerCase();
				int lastUnderscore = materialName.lastIndexOf('_');
				if (lastUnderscore != -1) {
					String suffix = materialName.substring(lastUnderscore);
					if (suffix.equals("_head") || suffix.equals("_skull")) {
						return true;
					}
				}
			}
		}
		return false;
	}
}