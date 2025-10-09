package com.github.joelgodofwar.mmh.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

public class DLCInstaller {
	private final JavaPlugin plugin;
	private final File updateDir;
	private final File backupDir;

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
		if (!plugin.getConfig().getBoolean("enable_dlc", true)) {
			plugin.getLogger().info("DLC processing disabled in config.");
			return;
		}

		File[] zipFiles = updateDir.listFiles((dir, name) -> name.endsWith(".zip"));
		if ((zipFiles == null) || (zipFiles.length == 0)) {
			plugin.getLogger().info("No DLC zip files found in update directory.");
			return;
		}

		for (File zipFile : zipFiles) {
			try {
				if (zipFile.getName().endsWith("bundle.zip")) {
					unpackAndProcessBundle(zipFile);
				} else {
					installDLC(zipFile);
				}
			} catch (IOException e) {
				plugin.getLogger().warning("Failed to process DLC: " + zipFile.getName() + " - " + e.getMessage());
			}
		}
	}

	/**
	 * Unpacks a bundle ZIP file and processes the contained DLC ZIPs.
	 *
	 * @param bundleZip The bundle ZIP file to unpack
	 * @throws IOException if an error occurs during file unpacking or processing
	 */
	private void unpackAndProcessBundle(File bundleZip) throws IOException {
		plugin.getLogger().info("Processing bundle ZIP: " + bundleZip.getName());
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(bundleZip))) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.getName().endsWith(".zip")) {
					File outputZip = new File(plugin.getDataFolder(), entry.getName());
					try (FileOutputStream fos = new FileOutputStream(outputZip)) {
						byte[] buffer = new byte[1024];
						int bytesRead;
						while ((bytesRead = zis.read(buffer)) != -1) {
							fos.write(buffer, 0, bytesRead);
						}
					}
					plugin.getLogger().info("Extracted " + entry.getName() + " from bundle to " + outputZip.getAbsolutePath());
					installDLC(outputZip); // Process the inner ZIP as a DLC
				}
			}
		}
		// Move bundle to backup after processing
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
		File backupZip = new File(backupDir, bundleZip.getName().replace(".zip", "_" + timestamp + ".zip"));
		Files.move(bundleZip.toPath(), backupZip.toPath(), StandardCopyOption.REPLACE_EXISTING);
		plugin.getLogger().info("Moved bundle " + bundleZip.getName() + " to backup: " + backupZip.getName());
	}

	/**
	 * Installs a DLC from the specified ZIP file by extracting and copying its contents.
	 *
	 * @param zipFile The ZIP file containing the DLC to install
	 * @throws IOException if an error occurs during file extraction or copying
	 */
	private void installDLC(File zipFile) throws IOException {
		JSONObject updateJson = extractUpdateJson(zipFile);
		if (updateJson == null) {
			plugin.getLogger().warning("No update.json found in " + zipFile.getName());
			return;
		}

		if (!updateJson.has("files") || !(updateJson.get("files") instanceof JSONArray)) {
			plugin.getLogger().warning("Invalid update.json in " + zipFile.getName() + ": missing or invalid 'files' array");
			return;
		}

		String dlcVersion = updateJson.optString("version", "unknown");
		String pluginVersion = plugin.getDescription().getVersion();
		if (!isDlcCompatible(dlcVersion)) {
			plugin.getLogger().warning("DLC " + zipFile.getName() + " version " + dlcVersion + " may not be compatible with plugin version " + pluginVersion);
		}

		String packName = updateJson.optString("packName", "Unknown");
		plugin.getLogger().info("Installing DLC pack: " + packName);

		Set<String> copiedFiles = new HashSet<>();
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.isDirectory() || entry.getName().equals("update.json")) {
					continue;
				}

				for (Object fileObj : updateJson.getJSONArray("files")) {
					JSONObject fileJson = (JSONObject) fileObj;
					String source = fileJson.optString("source", "");
					String destination = fileJson.optString("destination", "");
					if (source.isEmpty() || destination.isEmpty()) {
						plugin.getLogger().warning("Invalid file entry in update.json: " + fileJson.toString());
						continue;
					}

					if (entry.getName().equals(source)) {
						File destFile = new File(plugin.getDataFolder(), destination);
						if (copiedFiles.contains(destination)) {
							plugin.getLogger().warning("Duplicate destination " + destination + " in " + zipFile.getName() + "; skipping");
							break;
						}

						// Backup existing file
						if (destFile.exists()) {
							String timestamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
							File backupFile = new File(backupDir, destFile.getName() + "_" + timestamp + ".bak");
							try {
								Files.copy(destFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
								plugin.getLogger().info("Backed up " + destination + " to backup/" + backupFile.getName());
							} catch (IOException e) {
								plugin.getLogger().warning("Failed to back up " + destination + ": " + e.getMessage());
							}
						}
						// Copy new file
						destFile.getParentFile().mkdirs();
						try (FileOutputStream fos = new FileOutputStream(destFile)) {
							byte[] buffer = new byte[1024];
							int len;
							while ((len = zis.read(buffer)) > 0) {
								fos.write(buffer, 0, len);
							}
						}
						plugin.getLogger().info("Copied " + source + " to " + destination);
						copiedFiles.add(destination);
						break;
					}
				}
				zis.closeEntry();
			}
		}

		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
		File backupZip = new File(backupDir, zipFile.getName().replace(".zip", "_" + timestamp + ".zip"));
		Files.move(zipFile.toPath(), backupZip.toPath(), StandardCopyOption.REPLACE_EXISTING);
		plugin.getLogger().info("Moved " + zipFile.getName() + " to backup: " + backupZip.getName());
	}

	/**
	 * Extracts and parses the update.json file from the specified ZIP file.
	 *
	 * @param zipFile The ZIP file to extract the update.json from
	 * @return A JSONObject representing the update.json content, or null if not found
	 * @throws IOException if an error occurs during file reading
	 */
	private JSONObject extractUpdateJson(File zipFile) throws IOException {
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.getName().equals("update.json")) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int len;
					while ((len = zis.read(buffer)) > 0) {
						baos.write(buffer, 0, len);
					}
					return new JSONObject(baos.toString());
				}
				zis.closeEntry();
			}
		}
		return null;
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