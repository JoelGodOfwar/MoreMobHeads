package com.github.joelgodofwar.mmh.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

// Kept for potential future optional content / emergency patches
// Not currently used — DLC model has been discontinued
public class DLCInstaller {
	private final JavaPlugin plugin;
	private final File updateDir;
	private final File backupDir;
	private record JsonPair(JSONObject json, String rawContent) {}

	/**
	 * Constructs a new DLCInstaller instance for the specified plugin.
	 *
	 * @param plugin The JavaPlugin instance to manage DLC installation for
	 */
	public DLCInstaller(JavaPlugin plugin) {
		this.plugin = plugin;
		this.updateDir = new File(plugin.getDataFolder(), "update");
		this.backupDir = new File(plugin.getDataFolder(), "backup");
		createDirectories();
	}

	/**
	 * Creates the update and backup directories if they do not exist.
	 */
	private void createDirectories() {
		if (!updateDir.exists()) {
			updateDir.mkdirs();
		}
		if (!backupDir.exists()) {
			backupDir.mkdirs();
		}
	}

	/**
	 * Processes all DLC ZIP files in the update directory.
	 *
     */
	public void processDLCs() {
		try {
			if (!plugin.getConfig().getBoolean("enable_dlc", true)) {
				plugin.getLogger().info("DLC processing disabled in config.");
				return;
			}

			File[] potentialBundles = updateDir.listFiles((dir, name) ->
					name.toLowerCase().endsWith(".zip") && name.toLowerCase().contains("bundle")
			);

			if (potentialBundles != null && potentialBundles.length > 0) {
				for (File bundleZip : potentialBundles) {
					plugin.getLogger().info("Detected bundle ZIP: " + bundleZip.getName() + " — unpacking inner DLCs...");
					unpackBundle(bundleZip);
				}
				plugin.getLogger().info("Bundle unpacking complete. Continuing with regular DLC processing.");
			}

			File[] zipFiles = updateDir.listFiles((dir, name) -> name.endsWith(".zip"));
			if (zipFiles == null || zipFiles.length == 0) {
				plugin.getLogger().info("No DLC ZIP files found in update directory.");
				return;
			}

			for (File zipFile : zipFiles) {
				plugin.getLogger().info("Processing DLC ZIP: " + zipFile.getName());

				JsonPair pair = extractUpdateJsonWithContent(zipFile);
				if (pair == null) {
					plugin.getLogger().warning("No update.json found in " + zipFile.getName() + " — skipping");
					continue;
				}

				JSONObject dlcJson = pair.json();
				String jsonContent = pair.rawContent();

				String dlcVersion = dlcJson.optString("version", "unknown");
				if (!isDlcCompatible(dlcVersion)) {
					plugin.getLogger().warning("DLC " + zipFile.getName() + " version " + dlcVersion + " is not compatible with plugin — skipping");
					continue;
				}

				// Copy the ACTUAL extracted update.json content to heads/update/
				File headsUpdateDir = new File(plugin.getDataFolder(), "heads/update");
				if (!headsUpdateDir.exists()) {
					headsUpdateDir.mkdirs();
					plugin.getLogger().info("Created heads/update directory for DLC");
				}

				File dlcUpdateDest = new File(headsUpdateDir,
						"update_dlc_" + dlcJson.optString("packName", "unknown").replace(" ", "_") + ".json");

				try {
					Files.write(dlcUpdateDest.toPath(), jsonContent.getBytes(StandardCharsets.UTF_8));
					plugin.getLogger().info("Copied extracted DLC update.json to heads/update/" + dlcUpdateDest.getName());
				} catch (IOException e) {
					plugin.getLogger().warning("Failed to write DLC update.json: " + e.getMessage());
					continue;
				}

				// Process all files in DLC "files" array
				JSONArray dlcFiles = dlcJson.optJSONArray("files");
				if (dlcFiles != null) {
					int successCount = 0;
					for (int i = 0; i < dlcFiles.length(); i++) {
						JSONObject dlcEntry = dlcFiles.getJSONObject(i);
						String source = dlcEntry.optString("source", "");
						String destination = dlcEntry.optString("destination", "");
						if (source.isEmpty() || destination.isEmpty()) {
							plugin.getLogger().warning("Invalid DLC entry in " + zipFile.getName() + " — skipping");
							continue;
						}

						File destFile = new File(plugin.getDataFolder(), destination);
						if (!destFile.getParentFile().exists()) {
							destFile.getParentFile().mkdirs();
						}

						boolean copied = copyFromZip(zipFile, source, destFile);
						if (copied) {
							plugin.getLogger().info("Installed DLC file: " + destination);
							successCount++;
						} else {
							plugin.getLogger().warning("Failed to install DLC file: " + source);
						}
					}
					plugin.getLogger().info("Successfully installed " + successCount + " of " + dlcFiles.length() + " files from " + zipFile.getName());
				} else {
					plugin.getLogger().warning("No 'files' array in DLC update.json — skipping");
				}

				// Backup the ZIP after processing
				backupZipFile(zipFile);
			}
		} catch (Exception e) {
			plugin.getLogger().warning("Unexpected error in processDLCs: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Unpacks a bundle ZIP containing inner DLC ZIPs into the update/ directory.
	 * After unpacking, the inner ZIPs will be processed in the main loop.
	 */
	private void unpackBundle(File bundleZip) {
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(bundleZip))) {
			ZipEntry entry;
			int extractedCount = 0;

			while ((entry = zis.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					zis.closeEntry();
					continue;
				}

				String entryName = entry.getName();
				if (!entryName.toLowerCase().endsWith(".zip")) {
					plugin.getLogger().warning("Non-ZIP file found in bundle " + bundleZip.getName() + ": " + entryName + " — skipping");
					zis.closeEntry();
					continue;
				}

				// Use only the filename part (in case bundle has subfolders)
				String fileName = new File(entryName).getName();
				File outputFile = new File(updateDir, fileName);

				// Avoid overwriting if same name already exists
				if (outputFile.exists()) {
					plugin.getLogger().warning("Inner ZIP already exists: " + fileName + " — skipping extraction");
					zis.closeEntry();
					continue;
				}

				try (FileOutputStream fos = new FileOutputStream(outputFile)) {
					byte[] buffer = new byte[8192];
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					extractedCount++;
					plugin.getLogger().info("Extracted inner DLC: " + fileName);
				}
				zis.closeEntry();
			}

			plugin.getLogger().info("Successfully extracted " + extractedCount + " DLC ZIP(s) from bundle " + bundleZip.getName());
		} catch (IOException e) {
			plugin.getLogger().warning("Failed to unpack bundle " + bundleZip.getName() + ": " + e.getMessage());
			e.printStackTrace();
			return;
		}

		// After successful unpack → move bundle to backup
		backupZipFile(bundleZip);  // reusing your existing method
	}

	private boolean copyFromZip(File zipFile, String sourcePath, File destFile) {
		plugin.getLogger().info("=== DEBUG: Searching for '" + sourcePath + "' in ZIP: " + zipFile.getName() + " ===");

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
			ZipEntry entry;
			java.util.List<String> allEntryNames = new java.util.ArrayList<>(); // Collect for summary

			while ((entry = zis.getNextEntry()) != null) {
				String entryName = entry.getName();
				allEntryNames.add(entryName);

				// Optional: show if it's a directory or file
				String type = entry.isDirectory() ? "DIR " : "FILE";
				plugin.getLogger().info("  ZIP entry: " + type + " → " + entryName);

				if (entryName.equals(sourcePath)) {
					plugin.getLogger().info("  → MATCH FOUND! Copying '" + sourcePath + "' → " + destFile.getPath());

					try (FileOutputStream fos = new FileOutputStream(destFile)) {
						byte[] buffer = new byte[1024];
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
					}
					zis.closeEntry();
					return true;
				}
				zis.closeEntry();
			}

			// If we get here → no match
			plugin.getLogger().warning("  → NO MATCH for '" + sourcePath + "'");
			plugin.getLogger().info("All entries in ZIP were (" + allEntryNames.size() + " total):");
			for (String name : allEntryNames) {
				plugin.getLogger().info("   - " + name);
			}

		} catch (IOException e) {
			plugin.getLogger().warning("Failed to read ZIP: " + e.getMessage());
			e.printStackTrace();
		}

		return false;
	}

	private void backupZipFile(File zipFile) {
		String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
		File backupZip = new File(backupDir, zipFile.getName().replace(".zip", "_" + timestamp + ".zip"));
		try {
			Files.move(zipFile.toPath(), backupZip.toPath(), StandardCopyOption.REPLACE_EXISTING);
			plugin.getLogger().info("Moved " + zipFile.getName() + " to backup: " + backupZip.getName());
		} catch (IOException e) {
			plugin.getLogger().warning("Failed to backup DLC ZIP " + zipFile.getName() + ": " + e.getMessage());
		}
	}

	/**
	 * Extracts and parses the update.json file from the specified ZIP file.
	 *
	 * @param zipFile The ZIP file to extract the update.json from
	 * @return A JsonPair containing the JSONObject and the raw JSON string, or null if not found/error
	 */
	private JsonPair extractUpdateJsonWithContent(File zipFile) {
		plugin.getLogger().info("Attempting to extract update.json from DLC ZIP: " + zipFile.getName());
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if ("update.json".equals(entry.getName())) {
					plugin.getLogger().info("Found update.json in ZIP — reading...");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int len;
					while ((len = zis.read(buffer)) > 0) {
						baos.write(buffer, 0, len);
					}
					String jsonString = baos.toString(StandardCharsets.UTF_8);
					plugin.getLogger().info("update.json extracted successfully (length: " + jsonString.length() + " chars)");
					JSONObject jsonObject = new JSONObject(jsonString);
					return new JsonPair(jsonObject, jsonString);
				}
				zis.closeEntry();
			}
			plugin.getLogger().warning("No update.json found in DLC ZIP: " + zipFile.getName());
			return null;
		} catch (Exception e) {
			plugin.getLogger().warning("Error extracting update.json from " + zipFile.getName() + ": " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Checks if the DLC version is compatible with the plugin version.
	 *
	 * @param dlcVersion The version of the DLC to check
	 * @return true if the DLC is compatible, false otherwise
	 */
	private boolean isDlcCompatible(String dlcVersion) {
		String pluginVersion = plugin.getDescription().getVersion();
		return dlcVersion.startsWith("1.0") && pluginVersion.startsWith("1.20_1.0");
	}
}