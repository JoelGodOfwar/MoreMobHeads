package com.github.joelgodofwar.mmh.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lib.github.joelgodofwar.coreutils.util.YmlConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.util.gui.Language;
import com.github.joelgodofwar.mmh.util.heads.HeadUtils;
import com.github.joelgodofwar.mmh.util.heads.InventoryGUI;

public class ConfigGUI {
	final MoreMobHeads mmh;
	final YmlConfiguration config;
	final List<ConfigSetting> settings;
	final InventoryGUI gui;
	final File configFile;
	@SuppressWarnings("deprecation") static final NamespacedKey CONFIG_KEY = new NamespacedKey("moremobheads", "config_setting");

	public interface ConfigSetting {
		String getPath();
		String getDisplayName();
		ItemStack createIcon(YmlConfiguration config);
		void updateValue(Player player, YmlConfiguration config, ConfigGUI parent);
	}

	public interface IconProvider {
		ItemStack createIcon(YmlConfiguration config);
	}

	public ConfigGUI(MoreMobHeads plugin, List<ConfigSetting> settings, YmlConfiguration config) {
		this.mmh = plugin;
		this.configFile = new File(plugin.getDataFolder(), "config.yml");
		this.config = config;
		this.settings = settings;
		System.out.println(String.format("ConfigGUI initializing with %d settings, config: %s, configFile: %s", settings.size(), config != null ? "not null" : "null", configFile.getPath()));
		List<ItemStack> items = new ArrayList<>();
		for (ConfigSetting setting : settings) {
			ItemStack icon = setting.createIcon(config);
			ItemMeta meta = icon != null ? icon.getItemMeta() : null;
			if ((meta != null) && (icon != null)) {
				meta.getPersistentDataContainer().set(CONFIG_KEY, PersistentDataType.STRING, setting.getPath());
				icon.setItemMeta(meta);
				System.out.println(String.format("Added item for setting: %s, path: %s, class: %s", setting.getDisplayName(), setting.getPath(), setting.getClass().getSimpleName()));
			} else {
				System.out.println(String.format("Failed to create icon for setting: %s, path: %s", setting.getDisplayName(), setting.getPath()));
			}
			items.add(icon);
		}
		this.gui = new InventoryGUI("Plugin Settings", items, this::handleClick);
		System.out.println(String.format("ConfigGUI initialized with %d items", items.size()));
	}

	public void open(Player player) {
		System.out.println(String.format("Opening ConfigGUI for player: %s", player.getName()));
		gui.open(player, null);
	}

	private void handleClick(Player player, ItemStack item) {
		if ((item == null) || !item.hasItemMeta()) {
			System.out.println(String.format("Invalid item clicked by player: %s", player.getName()));
			player.sendMessage(ChatColor.RED + "Invalid item clicked!");
			return;
		}
		ItemMeta meta = item.getItemMeta();
		String settingPath = meta.getPersistentDataContainer().get(CONFIG_KEY, PersistentDataType.STRING);
		if (settingPath == null) {
			System.out.println(String.format("No setting path found for item clicked by player: %s", player.getName()));
			player.sendMessage(ChatColor.RED + "No setting associated with this item!");
			return;
		}
		System.out.println(String.format("Handling click for player: %s, settingPath: %s", player.getName(), settingPath));
		for (ConfigSetting setting : settings) {
			if (setting.getPath().equals(settingPath)) {
				System.out.println(String.format("Found setting: %s for player: %s, class: %s", setting.getDisplayName(), player.getName(), setting.getClass().getSimpleName()));
				try {
					System.out.println(String.format("Attempting to update setting: %s for player: %s", setting.getDisplayName(), player.getName()));
					setting.updateValue(player, config, this);
				} catch (Throwable e) {
					System.out.println(String.format("Critical error invoking updateValue for setting %s, player %s: %s", setting.getDisplayName(), player.getName(), e.getMessage()));
					player.sendMessage(ChatColor.RED + "Error updating setting: " + setting.getDisplayName() + " - " + e.getMessage());
					// Fallback to main menu
					new BukkitRunnable() {
						@Override
						public void run() {
							open(player);
							System.out.println(String.format("Reopened main ConfigGUI for player: %s due to error", player.getName()));
							player.sendMessage(ChatColor.GREEN + "Reopened main settings menu due to error");
						}
					}.runTaskLater(mmh, 1L);
					return;
				}
				if (!(setting instanceof CategorySetting)) {
					try {
						YmlConfiguration.saveConfig(configFile, config);
					} catch (Exception e) {
						System.out.println(String.format("Failed to save config for setting %s: %s", setting.getDisplayName(), e.getMessage()));
						player.sendMessage(ChatColor.RED + "Failed to save configuration!");
					}
					List<ItemStack> updatedItems = new ArrayList<>();
					for (ConfigSetting setting2 : settings) {
						ItemStack updatedIcon = setting2.createIcon(config);
						ItemMeta updatedMeta = updatedIcon != null ? updatedIcon.getItemMeta() : null;
						if ((updatedMeta != null) && (updatedIcon != null)) {
							updatedMeta.getPersistentDataContainer().set(CONFIG_KEY, PersistentDataType.STRING, setting2.getPath());
							updatedIcon.setItemMeta(updatedMeta);
							System.out.println(String.format("Updated item for setting: %s, path: %s", setting2.getDisplayName(), setting2.getPath()));
						} else {
							System.out.println(String.format("Failed to update icon for setting: %s, path: %s", setting2.getDisplayName(), setting2.getPath()));
						}
						updatedItems.add(updatedIcon);
					}
					player.closeInventory();
					System.out.println(String.format("Closing inventory for player: %s, preparing update", player.getName()));
					new BukkitRunnable() {
						@Override
						public void run() {
							InventoryGUI updatedGui = new InventoryGUI("Plugin Settings", updatedItems, ConfigGUI.this::handleClick);
							updatedGui.open(player, null);
							System.out.println(String.format("Opened updated GUI for player: %s, items: %d", player.getName(), updatedItems.size()));
							player.sendMessage(ChatColor.GREEN + "Updated settings GUI. Path: " + settingPath);
						}
					}.runTaskLater(mmh, 1L);
				}
				break;
			}
		}
		System.out.println(String.format("No setting matched for path: %s, player: %s", settingPath, player.getName()));
	}

	public static class BooleanSetting implements ConfigSetting {
		private final String path;
		private final String displayName;

		public BooleanSetting(String path, String displayName) {
			this.path = path;
			this.displayName = displayName;
		}

		@Override
		public String getPath() {
			return path;
		}

		@Override
		public String getDisplayName() {
			return displayName;
		}

		@Override
		public ItemStack createIcon(YmlConfiguration config) {
			System.out.println(String.format("Creating icon for BooleanSetting: %s, path: %s", displayName, path));
			Material material;
			try {
				if ((config != null) && config.isSet(path)) {
					material = config.getBoolean(path) ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
				} else {
					material = Material.GRAY_STAINED_GLASS_PANE;
					System.out.println(String.format("Config null or path %s not set for BooleanSetting: %s", path, displayName));
				}
			} catch (Exception e) {
				System.out.println(String.format("Error accessing config for path %s: %s", path, e.getMessage()));
				material = Material.GRAY_STAINED_GLASS_PANE;
			}
			ItemStack item = new ItemStack(material);
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				meta.setDisplayName(ChatColor.YELLOW + displayName);
				List<String> lore = new ArrayList<>();
				lore.add(ChatColor.GRAY + "Current: " + ((config != null) && config.isSet(path) ? (config.getBoolean(path) ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled") : ChatColor.GRAY + "Not Set"));
				lore.add(ChatColor.GRAY + "Click to toggle");
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			return item;
		}

		@Override
		public void updateValue(Player player, YmlConfiguration config, ConfigGUI parent) {
			boolean current = (config != null) && config.isSet(path) && config.getBoolean(path);
			if (config != null) {
				config.set(path, !current);
				System.out.println(String.format("Toggled %s to %s for player: %s", this.getDisplayName(), !current ? "enabled" : "disabled", player.getName()));
			} else {
				System.out.println(String.format("Config is null, cannot toggle %s for player: %s", displayName, player.getName()));
			}
			player.sendMessage(ChatColor.GREEN + displayName + " set to " + (!current ? "enabled" : "disabled"));
		}
	}

	public static class IntegerSetting implements ConfigSetting {
		private final String path;
		private final String displayName;
		private final IconProvider icon;
		private final int minValue;
		private final int maxValue;
		private final int increment;

		public IntegerSetting(String path, String displayName, IconProvider icon, int minValue, int maxValue, int increment) {
			this.path = path;
			this.displayName = displayName;
			this.icon = icon;
			this.minValue = minValue;
			this.maxValue = maxValue;
			this.increment = increment;
		}

		@Override
		public String getPath() {
			return path;
		}

		@Override
		public String getDisplayName() {
			return displayName;
		}

		@Override
		public ItemStack createIcon(YmlConfiguration config) {
			System.out.println(String.format("Creating icon for IntegerSetting: %s, path: %s", displayName, path));
			int value = config != null ? config.getInt(path) : 0;
			ItemStack item = icon.createIcon(config);
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				meta.setDisplayName(ChatColor.YELLOW + displayName);
				List<String> lore = new ArrayList<>();
				lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + value);
				lore.add(ChatColor.GRAY + "Click to increase by " + increment);
				lore.add(ChatColor.GRAY + "Shift-click to decrease by " + increment);
				meta.setLore(lore);
				item.setItemMeta(meta);
			} else {
				System.out.println(String.format("No meta for IntegerSetting: %s", displayName));
			}
			return item;
		}

		@Override
		public void updateValue(Player player, YmlConfiguration config, ConfigGUI parent) {
			int current = config != null ? config.getInt(path) : 0;
			int newValue = player.isSneaking() ? current - increment : current + increment;
			newValue = Math.max(minValue, Math.min(maxValue, newValue));
			if (config != null) {
				config.set(path, newValue);
				System.out.println(String.format("Set %s to %d for player: %s", displayName, newValue, player.getName()));
			} else {
				System.out.println(String.format("Config is null, cannot set %s for player: %s", displayName, player.getName()));
			}
			player.sendMessage(ChatColor.GREEN + displayName + " set to " + newValue);
		}
	}

	public static class ListSetting implements ConfigSetting {
		private final String path;
		private final String displayName;
		private final IconProvider icon;

		public ListSetting(String path, String displayName, IconProvider icon) {
			this.path = path;
			this.displayName = displayName;
			this.icon = icon;
		}

		@Override
		public String getPath() {
			return path;
		}

		@Override
		public String getDisplayName() {
			return displayName;
		}

		@Override
		public ItemStack createIcon(YmlConfiguration config) {
			System.out.println(String.format("Creating icon for ListSetting: %s, path: %s", displayName, path));
			List<String> value = config != null ? config.getStringList(path) : new ArrayList<>();
			ItemStack item = icon.createIcon(config);
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				meta.setDisplayName(ChatColor.YELLOW + displayName);
				List<String> lore = new ArrayList<>();
				lore.add(ChatColor.GRAY + "Current: " + (value.isEmpty() ? "None" : String.join(", ", value)));
				lore.add(ChatColor.GRAY + "Display-only (edit in config.yml)");
				meta.setLore(lore);
				item.setItemMeta(meta);
			} else {
				System.out.println(String.format("No meta for ListSetting: %s", displayName));
			}
			return item;
		}

		@Override
		public void updateValue(Player player, YmlConfiguration config, ConfigGUI parent) {
			System.out.println(String.format("ListSetting %s is display-only for player: %s", displayName, player.getName()));
			player.sendMessage(ChatColor.YELLOW + displayName + " is display-only. Edit in config.yml.");
		}
	}

	public static class LanguageSetting implements ConfigSetting {
		private final String path;
		private final String displayName;
		private final IconProvider icon;

		public LanguageSetting(String path, String displayName, IconProvider icon) {
			this.path = path;
			this.displayName = displayName;
			this.icon = icon;
		}

		@Override
		public String getPath() {
			return path;
		}

		@Override
		public String getDisplayName() {
			return displayName;
		}

		@Override
		public ItemStack createIcon(YmlConfiguration config) {
			System.out.println(String.format("Creating icon for LanguageSetting: %s, path: %s", displayName, path));
			String value = config != null ? config.getString(path, "en_US") : "en_US";
			ItemStack item = icon.createIcon(config);
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				meta.setDisplayName(ChatColor.YELLOW + displayName);
				List<String> lore = new ArrayList<>();
				Language lang = Language.getByLangCode(value);
				String langName = lang != null ? lang.getLangNameInEnglish() : value;
				lore.add(ChatColor.GRAY + "Current: " + ChatColor.WHITE + langName);
				lore.add(ChatColor.GRAY + "Click to select language");
				meta.setLore(lore);
				item.setItemMeta(meta);
			} else {
				System.out.println(String.format("No meta for LanguageSetting: %s", displayName));
			}
			return item;
		}

		@SuppressWarnings("unused") @Override
		public void updateValue(Player player, YmlConfiguration config, ConfigGUI parent) {
			System.out.println(String.format("Starting LanguageSetting update for player: %s", player.getName()));
			try {
				System.out.println(String.format("Checking Language class availability for player: %s", player.getName()));
				Class<?> languageClass = Class.forName("com.github.joelgodofwar.mmh.util.gui.Language");
				System.out.println(String.format("Accessing Language enum for player: %s", player.getName()));
				Language[] languages = Language.values();
				System.out.println(String.format("Language enum count: %d", languages.length));

				List<ItemStack> languageItems = new ArrayList<>();
				for (Language lang : languages) {
					try {
						ItemStack item = new ItemStack(Material.BOOK); // Use BOOK for testing
						ItemMeta meta = item.getItemMeta();
						if (meta != null) {
							meta.setDisplayName(ChatColor.YELLOW + lang.getLangNameInEnglish());
							meta.setLore(Arrays.asList(ChatColor.GRAY + lang.getLangNameInLang()));
							meta.getPersistentDataContainer().set(CONFIG_KEY, PersistentDataType.STRING, lang.getLangCode());
							item.setItemMeta(meta);
							System.out.println(String.format("Created BOOK item for %s: %s", lang.getLangNameInEnglish(), item.getType()));
						}
						languageItems.add(item);
					} catch (Exception e) {
						System.out.println(String.format("Error creating BOOK item for %s: %s", lang.getLangNameInEnglish(), e.getMessage()));
					}
				}
				if (languageItems.isEmpty()) {
					System.out.println(String.format("No language items created for player: %s", player.getName()));
					player.sendMessage(ChatColor.RED + "No languages available!");
					return;
				}
				player.closeInventory();
				System.out.println(String.format("Opening language selection for player: %s, items: %d", player.getName(), languageItems.size()));
				InventoryGUI languageGui = new InventoryGUI("Select Language", languageItems, (p, i) -> {
					ItemMeta itemMeta = i != null ? i.getItemMeta() : null;
					if ((itemMeta == null) || !itemMeta.hasDisplayName()) {
						System.out.println(String.format("Invalid language item clicked by player: %s", p.getName()));
						p.sendMessage(ChatColor.RED + "Invalid language item!");
						return;
					}
					String langCode = itemMeta.getPersistentDataContainer().get(CONFIG_KEY, PersistentDataType.STRING);
					Language selectedLang = Language.getByLangCode(langCode);
					if (selectedLang != null) {
						if (config != null) {
							config.set(path, selectedLang.getLangCode());
							try {
								YmlConfiguration.saveConfig(parent.configFile, config);
								System.out.println(String.format("Language set to %s for player: %s", selectedLang.getLangCode(), p.getName()));
							} catch (Exception e) {
								System.out.println(String.format("Failed to save config for language %s: %s", selectedLang.getLangCode(), e.getMessage()));
								p.sendMessage(ChatColor.RED + "Failed to save language setting!");
							}
						} else {
							System.out.println(String.format("Config is null, cannot set language %s for player: %s", selectedLang.getLangCode(), p.getName()));
							p.sendMessage(ChatColor.RED + "Configuration error, cannot set language!");
						}
						p.sendMessage(ChatColor.GREEN + selectedLang.getLangNameInEnglish() + " set to " + selectedLang.getLangCode());
					} else {
						System.out.println(String.format("No language found for langCode: %s, player: %s", langCode, p.getName()));
						p.sendMessage(ChatColor.RED + "Language not found!");
					}
					new BukkitRunnable() {
						@Override
						public void run() {
							List<ItemStack> subItems = new ArrayList<>();
							for (ConfigSetting setting : parent.settings) {
								if (setting.getPath().equals("plugin_settings")) {
									CategorySetting pluginSettings = (CategorySetting) setting;
									for (ConfigSetting subSetting : pluginSettings.subSettings) {
										ItemStack icon = subSetting.createIcon(config);
										ItemMeta meta = icon != null ? icon.getItemMeta() : null;
										if ((meta != null) && (icon != null)) {
											meta.getPersistentDataContainer().set(CONFIG_KEY, PersistentDataType.STRING, subSetting.getPath());
											icon.setItemMeta(meta);
											System.out.println(String.format("Set CONFIG_KEY for sub-item: %s, path: %s", subSetting.getDisplayName(), subSetting.getPath()));
										}
										subItems.add(icon);
									}
									InventoryGUI subGui = new InventoryGUI("Plugin Settings Menu", subItems, parent::handleClick);
									subGui.open(p, null);
									System.out.println(String.format("Reopened sub-menu Plugin Settings for player: %s, items: %d", p.getName(), subItems.size()));
									p.sendMessage(ChatColor.GREEN + "Reopened Plugin Settings menu");
									break;
								}
							}
						}
					}.runTaskLater(parent.mmh, 1L);
				});
				languageGui.open(player, null);
				player.sendMessage(ChatColor.GREEN + "Attempting to open language selection menu");
			} catch (Throwable e) {
				System.out.println(String.format("Critical error in LanguageSetting.updateValue for player %s: %s", player.getName(), e.getMessage()));
				player.sendMessage(ChatColor.RED + "Failed to open language menu: " + e.getMessage());
				// Fallback to main menu
				new BukkitRunnable() {
					@Override
					public void run() {
						parent.open(player);
						System.out.println(String.format("Reopened main ConfigGUI for player: %s due to error", player.getName()));
						player.sendMessage(ChatColor.GREEN + "Reopened main settings menu due to error");
					}
				}.runTaskLater(MoreMobHeads.getInstance(), 1L);
			}
		}
	}

	public static class CategorySetting implements ConfigSetting {
		private final String path;
		private final String displayName;
		private final IconProvider icon;
		private final List<ConfigSetting> subSettings;

		public CategorySetting(String path, String displayName, IconProvider icon, List<ConfigSetting> subSettings) {
			this.path = path;
			this.displayName = displayName;
			this.icon = icon;
			this.subSettings = subSettings;
		}

		@Override
		public String getPath() {
			return path;
		}

		@Override
		public String getDisplayName() {
			return displayName;
		}

		@Override
		public ItemStack createIcon(YmlConfiguration config) {
			System.out.println(String.format("Creating icon for CategorySetting: %s, path: %s", displayName, path));
			ItemStack item = icon.createIcon(config);
			ItemMeta meta = item.getItemMeta();
			if (meta != null) {
				meta.setDisplayName(ChatColor.YELLOW + displayName);
				List<String> lore = new ArrayList<>();
				lore.add(ChatColor.GRAY + "Click to view settings");
				meta.setLore(lore);
				meta.getPersistentDataContainer().set(CONFIG_KEY, PersistentDataType.STRING, path);
				item.setItemMeta(meta);
			} else {
				System.out.println(String.format("No meta for CategorySetting: %s", displayName));
			}
			return item;
		}

		@Override
		public void updateValue(Player player, YmlConfiguration config, ConfigGUI parent) {
			List<ItemStack> subItems = new ArrayList<>();
			for (ConfigSetting setting : subSettings) {
				ItemStack icon = setting.createIcon(config);
				ItemMeta meta = icon != null ? icon.getItemMeta() : null;
				if ((meta != null) && (icon != null)) {
					meta.getPersistentDataContainer().set(CONFIG_KEY, PersistentDataType.STRING, setting.getPath());
					icon.setItemMeta(meta);
					System.out.println(String.format("Set CONFIG_KEY for sub-item: %s, path: %s", setting.getDisplayName(), setting.getPath()));
				} else {
					System.out.println(String.format("No meta or null icon for sub-item: %s", setting.getDisplayName()));
				}
				subItems.add(icon);
			}
			player.closeInventory();
			System.out.println(String.format("Opening sub-menu %s for player: %s, items: %d", displayName, player.getName(), subItems.size()));
			InventoryGUI subGui = new InventoryGUI(displayName + " Menu", subItems, parent::handleClick);
			subGui.open(player, null);
			player.sendMessage(ChatColor.GREEN + "Attempting to open sub-menu for " + displayName);
		}
	}

	public static class MaterialIcon implements IconProvider {
		private final Material material;

		public MaterialIcon(Material material) {
			this.material = material;
		}

		@Override
		public ItemStack createIcon(YmlConfiguration config) {
			return new ItemStack(material);
		}
	}

	public static class SkinnedHeadIcon implements IconProvider {
		private final String displayName;
		private final String texture;
		private final String uuid;
		private final ArrayList<String> lore;
		private final String noteblockSound;

		public SkinnedHeadIcon(String displayName, String texture, String uuid, ArrayList<String> lore, String noteblockSound) {
			this.displayName = displayName;
			this.texture = texture;
			this.uuid = uuid;
			this.lore = lore != null ? new ArrayList<String>(lore) : null;
			this.noteblockSound = noteblockSound;
		}

		@Override
		public ItemStack createIcon(YmlConfiguration config) {
			return HeadUtils.makeHead(displayName, texture, uuid, lore, noteblockSound);
		}
	}
}