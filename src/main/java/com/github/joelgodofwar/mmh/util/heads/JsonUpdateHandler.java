package com.github.joelgodofwar.mmh.util.heads;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.joelgodofwar.mmh.MoreMobHeads;
import com.github.joelgodofwar.mmh.util.tools.JarUtil;

public class JsonUpdateHandler {
	private final MoreMobHeads mmh;
	private static final String STATUS_KEY = "status";
	private static final String STATUS_COMPLETED = "completed";
	private static final String PROCESSED_VERSION_KEY = "processedVersion";

	public JsonUpdateHandler(MoreMobHeads plugin) {
		this.mmh = plugin;
	}

	public void processUpdates(String headsDirPath) {
		File headsDir = new File(headsDirPath);
		if (!headsDir.exists() || !headsDir.isDirectory()) {
			mmh.LOGGER.log("Heads directory does not exist: " + headsDirPath);
			return;
		}

		String currentVersion = mmh.getDescription().getVersion();
		File tempUpdateFile = new File(mmh.getDataFolder(), "temp_update.json");
		String jarUpdatePath = "heads/update.json";

		// Step 0: Extract JAR's update.json to temp
		try {
			JarUtil.copyFileFromJar(jarUpdatePath, tempUpdateFile.getAbsolutePath());
			if (!tempUpdateFile.exists()) {
				mmh.LOGGER.log("No update.json found in JAR at " + jarUpdatePath + ", skipping update process");
				return;
			}
		} catch (Exception e) {
			mmh.LOGGER.warn("Failed to extract update.json from JAR: " + e.getMessage());
			return;
		}

		File serverUpdateFile = new File(headsDir, "update.json");
		boolean shouldProcess = true;
		String serverProcessedVersion = null;

		// Check server's update.json version
		if (serverUpdateFile.exists()) {
			try {
				String serverContent = new String(Files.readAllBytes(serverUpdateFile.toPath()));
				JSONObject serverJson = new JSONObject(serverContent);
				if (serverJson.has(PROCESSED_VERSION_KEY)) {
					serverProcessedVersion = serverJson.getString(PROCESSED_VERSION_KEY);
					int compareResult = compareVersions(serverProcessedVersion, currentVersion);
					if (compareResult >= 0) {
						mmh.LOGGER.log("Server update.json is up-to-date (processed for " + serverProcessedVersion + "), skipping for version " + currentVersion);
						shouldProcess = false;
					} else {
						mmh.LOGGER.log("Server update.json is outdated (processed for " + serverProcessedVersion + " < " + currentVersion + "), overwriting and re-processing");
					}
				} else {
					mmh.LOGGER.log("Server update.json lacks processedVersion (legacy), overwriting and processing for " + currentVersion);
				}
			} catch (Exception e) {
				mmh.LOGGER.warn("Error reading server update.json for version check: " + e.getMessage());
				// Fall back to processing
			}
		} else {
			mmh.LOGGER.log("No server update.json found, copying from JAR for version " + currentVersion);
		}

		if (!shouldProcess) {
			if (tempUpdateFile.exists()) {
				tempUpdateFile.delete(); // Cleanup
			}
			return;
		}

		// Backup old server file if exists
		if (serverUpdateFile.exists()) {
			File backupBaseDir = new File(mmh.getDataFolder(), "backup");
			if (!backupBaseDir.exists()) {
				backupBaseDir.mkdirs();
			}
			String relativePath = "heads/update.json";
			File backupFile = new File(backupBaseDir, relativePath);
			File backupParentDir = backupFile.getParentFile();
			if (!backupParentDir.exists()) {
				backupParentDir.mkdirs();
			}
			try {
				Files.copy(serverUpdateFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				mmh.LOGGER.log("Backed up old update.json to: " + backupFile.getAbsolutePath());
			} catch (IOException e) {
				mmh.LOGGER.warn("Failed to backup old update.json: " + e.getMessage());
			}
		}

		// Overwrite server with JAR's temp file
		try {
			Files.copy(tempUpdateFile.toPath(), serverUpdateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			mmh.LOGGER.log("Overwritten server update.json with JAR version for " + currentVersion);
		} catch (IOException e) {
			mmh.LOGGER.warn("Failed to copy JAR update.json to server: " + e.getMessage());
			if (tempUpdateFile.exists()) {
				tempUpdateFile.delete();
			}
			return;
		}

		// Now process
		try {
			String content = new String(Files.readAllBytes(serverUpdateFile.toPath()));
			JSONObject json = new JSONObject(content);

			// Step 1: Read chance_config.yml (but don't migrate chances yet)
			Map<String, Double> chanceMap = new HashMap<>();
			File chanceConfigFile = new File(mmh.getDataFolder(), "chance_config.yml");
			if (chanceConfigFile.exists()) {
				mmh.LOGGER.log("Reading chance_config.yml to migrate chances to JSON files...");
				YamlConfiguration chanceConfig = new YamlConfiguration();
				try {
					chanceConfig.load(chanceConfigFile);
					int entryCount = 0;
					for (String key : chanceConfig.getKeys(true)) {
						if (key.startsWith("chance_percent.")) {
							String entityKey = key.replace("chance_percent.", "");
							double chance = chanceConfig.getDouble(key, -1.0); // Use -1.0 to indicate no value
							if ((chance >= 0.0) && (chance <= 100.0)) { // Ensure chance is valid
								chanceMap.put(entityKey, chance);
								mmh.LOGGER.log("Found chance for " + entityKey + ": " + chance);
								entryCount++;
							} else {
								mmh.LOGGER.log("Invalid chance value for " + entityKey + ": " + chance + ", must be between 0.0 and 100.0");
							}
						}
					}
					mmh.LOGGER.log("Total chances found in chance_config.yml: " + entryCount);
				} catch (Exception e) {
					mmh.LOGGER.warn("Error reading chance_config.yml: " + e.getMessage());
					e.printStackTrace();
				}
			} else {
				mmh.LOGGER.log("chance_config.yml not found, skipping chance migration.");
			}

			// Step 2: Process file operations (backup and/or delete)
			JSONArray files = json.optJSONArray("files");
			if (files != null) {
				File backupBaseDir = new File(mmh.getDataFolder(), "backup");
				if (!backupBaseDir.exists()) {
					backupBaseDir.mkdirs(); // Create backup directory if it doesn't exist
					mmh.LOGGER.log("Created backup directory: " + backupBaseDir.getAbsolutePath());
				}

				for (int i = 0; i < files.length(); i++) {
					JSONObject fileEntry = files.getJSONObject(i);
					String filePath = fileEntry.optString("file", "");
					boolean backup = fileEntry.optBoolean("backup", false);
					boolean delete = fileEntry.optBoolean("delete", false);
					boolean backupAndDelete = fileEntry.optBoolean("backup/delete", false);

					if (filePath.isEmpty()) {
						continue;
					}

					// If "backup/delete" is true, set both backup and delete to true
					if (backupAndDelete) {
						backup = true;
						delete = true;
					}

					// Determine the source file path
					File sourceFile;
					if (filePath.startsWith("entity/") || filePath.startsWith("player/") || filePath.startsWith("block/")) {
						sourceFile = new File(headsDir, filePath);
					} else {
						sourceFile = new File(mmh.getDataFolder(), filePath);
					}

					// Determine the backup file path relative to getDataFolder()
					String relativePath = filePath;
					if (filePath.startsWith("entity/")) {
						relativePath = "heads/" + filePath;
					} else if (filePath.startsWith("player/")) {
						relativePath = "heads/" + filePath;
					} else if (filePath.startsWith("block/")) {
						relativePath = "heads/" + filePath;
					}
					File backupFile = new File(backupBaseDir, relativePath);

					// Perform backup if requested
					if (backup) {
						if (sourceFile.exists()) {
							try {
								// Ensure the backup directory structure exists
								File backupParentDir = backupFile.getParentFile();
								if (!backupParentDir.exists()) {
									backupParentDir.mkdirs();
								}

								Files.copy(sourceFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
								mmh.LOGGER.log("Backed up file to: " + backupFile.getAbsolutePath());
							} catch (IOException e) {
								mmh.LOGGER.warn("Failed to back up file " + filePath + ": " + e.getMessage());
								e.printStackTrace();
							}
						} else {
							mmh.LOGGER.log("File not found, no backup needed: " + filePath);
						}
					}

					// Perform deletion if requested
					if (delete) {
						if (sourceFile.exists()) {
							if (sourceFile.delete()) {
								mmh.LOGGER.log("Deleted file as specified in update.json: " + filePath);
							} else {
								mmh.LOGGER.warn("Failed to delete file: " + filePath);
							}
						} else {
							mmh.LOGGER.log("File not found, no deletion needed: " + filePath);
						}
					}
				}
			}

			// Step 3: Process "compare" and "forced" updates
			if (files != null) {
				for (int i = 0; i < files.length(); i++) {
					JSONObject fileEntry = files.getJSONObject(i);
					String filePath = fileEntry.optString("file", "");
					boolean compare = fileEntry.optBoolean("compare", false);
					JSONArray forcedFieldsArray = fileEntry.optJSONArray("forced");
					List<String> forcedFields = new ArrayList<>();
					if (forcedFieldsArray != null) {
						for (int j = 0; j < forcedFieldsArray.length(); j++) {
							forcedFields.add(forcedFieldsArray.getString(j));
						}
					}

					if (filePath.isEmpty() || !compare) {
						continue;
					}

					// Determine the server file path
					File serverFile;
					if (filePath.startsWith("entity/") || filePath.startsWith("player/") || filePath.startsWith("block/")) {
						serverFile = new File(headsDir, filePath);
					} else {
						serverFile = new File(mmh.getDataFolder(), filePath);
					}

					// Determine the JAR file path
					String jarFilePath = "heads/" + filePath;
					File tempJarFile = new File(mmh.getDataFolder(), "temp_" + filePath.replace("/", "_"));
					try {
						// Extract the JAR file to a temporary location
						JarUtil.copyFileFromJar(jarFilePath, tempJarFile.getAbsolutePath());
						if (!tempJarFile.exists()) {
							mmh.LOGGER.log("JAR file not found: " + jarFilePath + ", skipping compare for " + filePath);
							continue;
						}

						// Read both JSON files
						String serverContent = serverFile.exists() ? new String(Files.readAllBytes(serverFile.toPath())) : "{}";
						String jarContent = new String(Files.readAllBytes(tempJarFile.toPath()));
						JSONObject serverJson = new JSONObject(serverContent);
						JSONObject jarJson = new JSONObject(jarContent);

						boolean modified = false;

						// Compare top-level fields (e.g., "defaultChance", "structure")
						for (String key : jarJson.keySet()) {
							if (!serverJson.has(key) && !key.equals("heads")) {
								// Ensure defaultChance is written as a double
								if (key.equals("defaultChance")) {
									serverJson.put(key, 1.0); // Force double format
								} else {
									serverJson.put(key, jarJson.get(key));
								}
								modified = true;
								mmh.LOGGER.log("Added missing top-level field '" + key + "' to " + filePath);
							}
						}

						// Compare heads
						String structure = jarJson.optString("structure", "flat");
						if (structure.equals("nested")) {
							JSONObject serverHeads = serverJson.optJSONObject("heads");
							JSONObject jarHeads = jarJson.optJSONObject("heads");
							if (jarHeads != null) {
								if (serverHeads == null) {
									serverHeads = new JSONObject();
									serverJson.put("heads", serverHeads);
								}

								for (String category : jarHeads.keySet()) {
									JSONObject jarCategoryHeads = jarHeads.getJSONObject(category);
									JSONObject serverCategoryHeads = serverHeads.optJSONObject(category);
									if (serverCategoryHeads == null) {
										serverHeads.put(category, jarCategoryHeads);
										modified = true;
										mmh.LOGGER.log("Added missing category '" + category + "' to " + filePath);
										continue;
									}

									for (String variant : jarCategoryHeads.keySet()) {
										JSONObject jarHeadData = jarCategoryHeads.getJSONObject(variant);
										JSONObject serverHeadData = serverCategoryHeads.optJSONObject(variant);
										if (serverHeadData == null) {
											serverCategoryHeads.put(variant, jarHeadData);
											modified = true;
											mmh.LOGGER.log("Added missing head '" + category + "." + variant + "' to " + filePath);
											continue;
										}

										// Compare fields within the head
										for (String field : jarHeadData.keySet()) {
											if (!serverHeadData.has(field)) {
												serverHeadData.put(field, jarHeadData.get(field));
												modified = true;
												mmh.LOGGER.log("Added missing field '" + field + "' to head '" + category + "." + variant + "' in " + filePath);
											} else if (forcedFields.contains(field)) {
												serverHeadData.put(field, jarHeadData.get(field));
												modified = true;
												mmh.LOGGER.log("Forced update of field '" + field + "' in head '" + category + "." + variant + "' in " + filePath + " to JAR value");
											}
										}
									}
								}
							}
						} else {
							JSONArray serverHeads = serverJson.optJSONArray("heads");
							JSONArray jarHeads = jarJson.optJSONArray("heads");
							if (jarHeads != null) {
								if (serverHeads == null) {
									serverHeads = new JSONArray();
									serverJson.put("heads", serverHeads);
								}

								for (int j = 0; j < jarHeads.length(); j++) {
									JSONObject jarHeadData = jarHeads.getJSONObject(j);
									boolean found = false;
									for (int k = 0; k < serverHeads.length(); k++) {
										JSONObject serverHeadData = serverHeads.getJSONObject(k);
										if (serverHeadData.optString("langName").equals(jarHeadData.optString("langName"))) {
											found = true;
											for (String field : jarHeadData.keySet()) {
												if (!serverHeadData.has(field)) {
													serverHeadData.put(field, jarHeadData.get(field));
													modified = true;
													mmh.LOGGER.log("Added missing field '" + field + "' to head '" + jarHeadData.optString("langName") + "' in " + filePath);
												} else if (forcedFields.contains(field)) {
													serverHeadData.put(field, jarHeadData.get(field));
													modified = true;
													mmh.LOGGER.log("Forced update of field '" + field + "' in head '" + jarHeadData.optString("langName") + "' in " + filePath + " to JAR value");
												}
											}
											break;
										}
									}
									if (!found) {
										serverHeads.put(jarHeadData);
										modified = true;
										mmh.LOGGER.log("Added missing head '" + jarHeadData.optString("langName") + "' to " + filePath);
									}
								}
							}
						}

						if (modified) {
							try (FileWriter writer = new FileWriter(serverFile)) {
								writer.write(serverJson.toString(4));
								mmh.LOGGER.log("Updated " + filePath + " after compare/forced operations");
							}
						}

					} catch (Exception e) {
						mmh.LOGGER.warn("Error comparing/updating " + filePath + ": " + e.getMessage());
						e.printStackTrace();
					} finally {
						if (tempJarFile.exists()) {
							tempJarFile.delete();
						}
					}
				}
			}

			// Step 4: Update chances in JSON files (moved from Step 3)
			if (!chanceMap.isEmpty()) {
				mmh.LOGGER.log("Migrating chances from chance_config.yml to JSON files...");
				File entityDir = new File(headsDir, "entity");
				if (entityDir.exists() && entityDir.isDirectory()) {
					File[] jsonFiles = entityDir.listFiles((dir, name) -> name.endsWith(".json") && !name.equals("update.json"));
					if (jsonFiles != null) {
						mmh.LOGGER.log("Found " + jsonFiles.length + " JSON files in " + entityDir.getAbsolutePath() + " to process for chance migration.");
						for (File jsonFile : jsonFiles) {
							try {
								mmh.LOGGER.log("Processing JSON file: " + jsonFile.getName());
								String jsonContent = new String(Files.readAllBytes(jsonFile.toPath()));
								JSONObject jsonData = new JSONObject(jsonContent);

								String structure = jsonData.optString("structure", "flat");
								boolean modified = false;

								if (structure.equals("nested")) {
									JSONObject heads = jsonData.optJSONObject("heads");
									if (heads != null) {
										for (String category : heads.keySet()) {
											JSONObject categoryHeads = heads.getJSONObject(category);
											for (String variant : categoryHeads.keySet()) {
												JSONObject headData = categoryHeads.getJSONObject(variant);
												String langName = headData.optString("langName", "");

												if (category.equals("zombie_villager")) {
													Double chance = chanceMap.get("zombie_villager");
													if (chance != null) {
														double oldChance = headData.optDouble("chance", -1.0);
														if (oldChance != chance) {
															headData.put("chance", chance); // Already a double
															modified = true;
															mmh.LOGGER.log("Updated chance for zombie_villager." + variant + " (" + langName + ") to " + chance);
														} else {
															mmh.LOGGER.log("Chance for zombie_villager." + variant + " (" + langName + ") is already " + chance + ", no update needed.");
														}
													} else {
														mmh.LOGGER.log("No chance found for zombie_villager in chance_config.yml, skipping update for " + langName);
													}
												} else {
													Double chance = chanceMap.get(langName);
													if (chance != null) {
														double oldChance = headData.optDouble("chance", -1.0);
														if (oldChance != chance) {
															headData.put("chance", chance); // Already a double
															modified = true;
															mmh.LOGGER.log("Updated chance for " + langName + " to " + chance);
														} else {
															mmh.LOGGER.log("Chance for " + langName + " is already " + chance + ", no update needed.");
														}
													} else {
														String baseEntity = langName.split("\\.")[0];
														chance = chanceMap.get(baseEntity);
														if (chance != null) {
															double oldChance = headData.optDouble("chance", -1.0);
															if (oldChance != chance) {
																headData.put("chance", chance); // Already a double
																modified = true;
																mmh.LOGGER.log("Updated chance for " + langName + " to " + chance + " (using base entity " + baseEntity + ")");
															} else {
																mmh.LOGGER.log("Chance for " + langName + " (using base entity " + baseEntity + ") is already " + chance + ", no update needed.");
															}
														} else {
															mmh.LOGGER.log("No chance found for " + langName + " or base entity " + baseEntity + " in chance_config.yml, skipping update");
														}
													}
												}
											}
										}
									}
								} else {
									JSONArray heads = jsonData.optJSONArray("heads");
									if (heads != null) {
										for (int i = 0; i < heads.length(); i++) {
											JSONObject headData = heads.getJSONObject(i);
											String langName = headData.optString("langName", "");

											Double chance = chanceMap.get(langName);
											if (chance != null) {
												double oldChance = headData.optDouble("chance", -1.0);
												if (oldChance != chance) {
													headData.put("chance", chance); // Already a double
													modified = true;
													mmh.LOGGER.log("Updated chance for " + langName + " to " + chance);
												} else {
													mmh.LOGGER.log("Chance for " + langName + " is already " + chance + ", no update needed.");
												}
											} else {
												mmh.LOGGER.log("No chance found for " + langName + " in chance_config.yml, skipping update");
											}
										}
									}
								}

								if (modified) {
									try (FileWriter writer = new FileWriter(jsonFile)) {
										writer.write(jsonData.toString(4));
										mmh.LOGGER.log("Updated chances in " + jsonFile.getName());
									}
								} else {
									mmh.LOGGER.log("No chance updates needed for " + jsonFile.getName());
								}
							} catch (Exception e) {
								mmh.LOGGER.warn("Error updating chances in " + jsonFile.getName() + ": " + e.getMessage());
								e.printStackTrace();
							}
						}
					} else {
						mmh.LOGGER.log("No JSON files found in " + entityDir.getAbsolutePath() + " to process for chance migration.");
					}
				} else {
					mmh.LOGGER.log("Entity directory does not exist: " + entityDir.getAbsolutePath());
				}
			}

			// Step 5: Mark as completed and add processed version
			json.put(STATUS_KEY, STATUS_COMPLETED);
			json.put(PROCESSED_VERSION_KEY, currentVersion);
			try (FileWriter writer = new FileWriter(serverUpdateFile)) {
				writer.write(json.toString(4));
				mmh.LOGGER.log("Marked update.json as completed for plugin version " + currentVersion);
			}

		} catch (Exception e) {  // Broader catch for processing errors
			mmh.LOGGER.warn("Error processing update.json: " + e.getMessage());
			e.printStackTrace();
			// Don't set completed on error, so it can retry
		} finally {
			if (tempUpdateFile.exists()) {
				tempUpdateFile.delete();
			}
		}
	}

	/**
	 * Compares two version strings (e.g., "1.20_1.0.43.D9" vs "1.20_1.0.43.D10").
	 * Returns -1 if v1 < v2, 0 if equal, 1 if v1 > v2.
	 */
	private int compareVersions(String v1, String v2) {
		if (v1 == null || v2 == null) {
			return -1; // Treat null as older
		}
		String[] parts1 = v1.split("[._]");
		String[] parts2 = v2.split("[._]");
		int minLength = Math.min(parts1.length, parts2.length);
		for (int i = 0; i < minLength; i++) {
			String p1 = parts1[i];
			String p2 = parts2[i];
			// Handle dev suffix like "D9"
			if (p1.startsWith("D") && p2.startsWith("D")) {
				try {
					int n1 = Integer.parseInt(p1.substring(1));
					int n2 = Integer.parseInt(p2.substring(1));
					if (n1 != n2) {
						return Integer.compare(n1, n2);
					}
				} catch (NumberFormatException e) {
					// Fallback to string compare if invalid
					if (!p1.equals(p2)) {
						return p1.compareTo(p2) < 0 ? -1 : 1;
					}
				}
			} else if (isNumeric(p1) && isNumeric(p2)) {
				int n1 = Integer.parseInt(p1);
				int n2 = Integer.parseInt(p2);
				if (n1 != n2) {
					return Integer.compare(n1, n2);
				}
			} else {
				// String compare for non-numeric
				if (!p1.equals(p2)) {
					return p1.compareTo(p2) < 0 ? -1 : 1;
				}
			}
		}
		// If prefixes match, shorter is older
		return Integer.compare(parts1.length, parts2.length);
	}

	private boolean isNumeric(String s) {
		return s.matches("\\d+");
	}
}