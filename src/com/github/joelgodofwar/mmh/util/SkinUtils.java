package com.github.joelgodofwar.mmh.util;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.json.JSONObject;

import com.github.joelgodofwar.mmh.common.PluginLibrary;
import com.github.joelgodofwar.mmh.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.mmh.common.error.Report;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

public class SkinUtils {

	/**
	 * Creates a custom head ItemStack with the specified name, texture URL (as a String),
	 * associated entity type, and player who delivered the killing blow.
	 *
	 * @param name The name of the custom head.
	 * @param texture String of the Base64-encoded string or direct URL of the texture for the custom head.
	 * @param uuid String UUID of Mob
	 * @param lore List<String> of Lore
	 * @param noteBlockSound of the head
	 * @param reporter DetailedErrorReporter
	 * @return An ItemStack representing the custom head with the provided name, texture, sound, and lore.
	 */
	public ItemStack makeHead(String name, String texture, String uuid, List<String> lore, NamespacedKey noteBlockSound, DetailedErrorReporter reporter) {
		// Create the PlayerProfile using UUID and name
		PlayerProfile profile =  Bukkit.createPlayerProfile(UUID.fromString(uuid), "");
		// get Player head item stack
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		// get SkullMeta from new player head
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		// get textures from the profile
		PlayerTextures textures = profile.getTextures();
		URL url = null;
		// convert base64 string to URL if not URL already
		try {
			url = convertBase64ToURL(texture);
		} catch (Exception exception) {
			reporter.reportDetailed(this, Report.newBuilder(PluginLibrary.REPORT_HEAD_URL_ERROR).error(exception));
		}
		// set the skin URL
		textures.setSkin(url);
		// save skin texture to head
		profile.setTextures(textures);
		// now set the profile, noteblock sound, lore, and displayname
		meta.setOwnerProfile(profile);
		if(noteBlockSound != null) { meta.setNoteBlockSound(noteBlockSound); }
		if(lore != null) {
			// we do this twice because sometimes it doesn't take
			meta.setLore(lore);
			meta.setLore(lore);
		}
		meta.setDisplayName(name);
		// return the complete head.
		return head;
	}

	/**
	 * Converts a Base64-encoded string containing a JSON structure or a URL string to a URL object
	 * representing the texture's URL.
	 *
	 * @param input The Base64-encoded string containing the JSON structure with a URL or a URL string itself.
	 * @return A URL object representing the texture's URL. If the input is a direct URL string,
	 *         it is returned directly. If the input is a Base64-encoded string, the method
	 *         decodes it, extracts the URL from the JSON structure, and returns the corresponding URL object.
	 * @throws MalformedURLException If the URL extraction or URL creation encounters a malformed URL.
	 */
	public URL convertBase64ToURL(String base64) throws MalformedURLException {
		try {
			return new URL(base64);
		} catch (MalformedURLException ignored) {}

		String jsonString = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);

		JSONObject jsonObject = new JSONObject(jsonString);
		JSONObject jstextures = jsonObject.getJSONObject("textures");
		JSONObject skin = jstextures.getJSONObject("SKIN");
		String jsurl = skin.getString("url");
		return new URL(jsurl);
	}

	/**
	 * Extracts the Base64 texture string from an ItemStack's ItemMeta.
	 *
	 * @param itemStack the ItemStack from which to extract the texture
	 * @return the DisplayName string, or null if not found
	 */
	public NamespacedKey getHeadNoteblockSound(ItemStack itemStack) {
		if (itemStack == null) {
			return null;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null) {
			return null;
		}
		SkullMeta skullmeta = (SkullMeta) itemMeta;
		if(skullmeta != null) {
			NamespacedKey noteblocksound = skullmeta.getNoteBlockSound();
			if(noteblocksound != null) {
				return noteblocksound;
			}
		}
		return null;
	}

	/**
	 * Extracts the Base64 texture string from an ItemStack's ItemMeta.
	 *
	 * @param itemStack the ItemStack from which to extract the texture
	 * @return the List<String>, or null if not found
	 */
	public List<String> getHeadLore(ItemStack itemStack){
		if (itemStack == null) {
			return null;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null) {
			return null;
		}
		List<String> lore = itemMeta.getLore();
		if(lore != null) {
			return lore;
		}
		return null;
	}

	/**
	 * Extracts the Base64 texture string from an ItemStack's ItemMeta.
	 *
	 * @param itemStack the ItemStack from which to extract the texture
	 * @return the DisplayName string, or null if not found
	 */
	public String getHeadDisplayName(ItemStack itemStack) {
		if (itemStack == null) {
			return null;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null) {
			return null;
		}
		String displayName = itemMeta.getDisplayName();
		if(displayName != null) {
			return displayName;
		}
		return null;
	}

	/**
	 * Extracts the Base64 texture string from an ItemStack's ItemMeta.
	 *
	 * @param itemStack the ItemStack from which to extract the texture
	 * @return the Base64 texture string, or null if not found
	 */
	public String getHeadTexture(ItemStack itemStack) {
		if (itemStack == null) {
			return null;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null) {
			return null;
		}
		Object profile = getPrivate(null, itemMeta, itemMeta.getClass(), "profile");
		if (!(profile instanceof GameProfile)) {
			return null;
		}
		GameProfile gameProfile = (GameProfile) profile;
		PropertyMap properties = gameProfile.getProperties();
		if (properties == null) {
			return null;
		}
		Collection<Property> textures = properties.get("textures");
		if ((textures != null) && !textures.isEmpty()) {
			Property textureProperty = textures.iterator().next();
			String input = textureProperty.toString();
			//System.out.println("input = " + input);
			String texture = input.substring(input.indexOf("value=") + 6, input.lastIndexOf(','));
			//System.out.println("texture = " + texture);
			return texture;
		}
		return null;
	}

	/**
	 * Extracts the Base64 texture string from an ItemStack's ItemMeta.
	 *
	 * @param itemStack the ItemStack from which to extract the texture
	 * @return the UUID string, or null if not found
	 */
	public String getHeadUUID(ItemStack itemStack) {
		if (itemStack == null) {
			return null;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null) {
			return null;
		}
		Object profile = getPrivate(null, itemMeta, itemMeta.getClass(), "profile");
		if (!(profile instanceof GameProfile)) {
			return null;
		}
		GameProfile gameProfile = (GameProfile) profile;
		UUID uuid = gameProfile.getId();

		if ((uuid != null)) {
			return uuid.toString();
		}
		return null;
	}

	/**
	 * Reflection to get Private field of ItemStack
	 * */
	public static Object getPrivate(Logger logger, Object o, Class<?> c, String field) {
		try {
			Field access = c.getDeclaredField(field);
			access.setAccessible(true);
			return access.get(o);
		} catch (Exception ex) {
			ex.printStackTrace();
			//logger.log(Level.SEVERE, "Error getting private member of " + o.getClass().getName() + "." + field, ex);
		}
		return null;
	}

	/**public static String getProfileURL(Object profile) {
		String url = null;
		if ((profile == null) || !(profile instanceof GameProfile)) {
			return null;
		}
		GameProfile gameProfile = (GameProfile)profile;
		PropertyMap properties = gameProfile.getProperties();
		if (properties == null) {
			return null;
		}
		Collection<Property> textures = properties.get("textures");
		if ((textures != null) && (textures.size() > 0)) {
			Property textureProperty = textures.iterator().next();

			try {
				//String decoded = Base64Coder.decodeString(texture);
				String texture = getValue(textureProperty);
				url = texture;//getTextureURL(decoded);
			} catch (Exception ex) {
				//platform.getLogger().log(Level.WARNING, "Could not parse textures in profile", ex);
			}
		}
		return url;
	}
	protected static String getValue(Property property) {
		return property.getValue();
	}
	public static Object getSkullProfile(ItemMeta itemMeta) {
		if ((itemMeta == null) || !(itemMeta instanceof SkullMeta)) {
			return null;
		}
		return ReflectionUtils.getPrivate(null, itemMeta, itemMeta.getClass(), "profile");
	}//*/
}
