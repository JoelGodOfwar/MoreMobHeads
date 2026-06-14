package com.github.joelgodofwar.mmh.util.heads;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import lib.github.joelgodofwar.coreutils.CoreUtils;
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
			CoreUtils.log("Heads directory does not exist: " + headsDirPath);
			return;
		}

		String currentVersion = mmh.getDescription().getVersion();

		File updateDir = headsDir; // Files are directly in heads/

		File backupDir = new File(mmh.getDataFolder(), "backup/updates"); // Your preferred path
		if (!backupDir.exists()) {
			backupDir.mkdirs();
		}

		// Step 0: Pre-extract ALL possible update files from JAR (main + numbered)
		List<String> possibleUpdatePaths = new ArrayList<>();
		possibleUpdatePaths.add("heads/update.json");

		// Adjust max number if you have more than 10
		for (int i = 1; i <= 10; i++) {
			possibleUpdatePaths.add("heads/update" + i + ".json");
		}

		for (String jarPath : possibleUpdatePaths) {
			String fileName = new File(jarPath).getName();
			File tempFile = new File(mmh.getDataFolder(), "temp_" + fileName);
			try {
				JarUtil.copyFileFromJar(jarPath, tempFile.getAbsolutePath());
				if (tempFile.exists()) {
					File serverFile = new File(updateDir, fileName);
					if (!serverFile.exists()) {
						Files.copy(tempFile.toPath(), serverFile.toPath());
						CoreUtils.log("Extracted missing " + fileName + " from JAR to heads/");
					} else {
						CoreUtils.debug(fileName + " already exists in heads/ — skipping extract");
					}
				}
				tempFile.delete();
			} catch (Exception e) {
				CoreUtils.debug("No " + fileName + " in JAR or failed to extract: " + e.getMessage());
			}
		}

		// Now build the list (files should now exist)
		List<File> updateFiles = new ArrayList<>();

		File mainUpdate = new File(updateDir, "update.json");
		if (mainUpdate.exists()) {
			updateFiles.add(mainUpdate);
		}

		File[] numbered = updateDir.listFiles(f -> f.getName().matches("update\\d+\\.json"));
		if (numbered != null) {
			Arrays.sort(numbered, Comparator.comparingInt(f -> Integer.parseInt(f.getName().replaceAll("\\D", ""))));
			updateFiles.addAll(Arrays.asList(numbered));
		}

		if (updateFiles.isEmpty()) {
			CoreUtils.log("No update files found in heads folder after extraction, skipping");
			return;
		}

		CoreUtils.log("Found " + updateFiles.size() + " update files to process");

		// Process all
		for (File updateFile : updateFiles) {
			String fileName = updateFile.getName();
			CoreUtils.log("Processing update file: " + fileName);

			File tempUpdateFile = new File(mmh.getDataFolder(), "temp_" + fileName);
			String jarUpdatePath = "heads/" + fileName;

			try {
				JarUtil.copyFileFromJar(jarUpdatePath, tempUpdateFile.getAbsolutePath());
				if (!tempUpdateFile.exists()) {
					CoreUtils.log("No " + fileName + " found in JAR, skipping");
					continue;
				}
				CoreUtils.debug("Extracted " + fileName + " from JAR to temp");
			} catch (Exception e) {
				CoreUtils.warn("Failed to extract " + fileName + " from JAR: " + e.getMessage());
				continue;
			}

			processSingleUpdate(tempUpdateFile, updateFile, headsDirPath, currentVersion);

			tempUpdateFile.delete();

			/* File backupFile = new File(backupDir, fileName);
			try {
				Files.move(updateFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				CoreUtils.log("Moved processed " + fileName + " to backup/updates");
			} catch (IOException e) {
				CoreUtils.warn("Failed to move " + fileName + " to backups: " + e.getMessage());
			}//*/
		}
	}

	private void processUpdateFile(File updateFile, String headsDirPath, String currentVersion) {
		CoreUtils.log("Processing update file: " + updateFile.getName());

		File tempUpdateFile = new File(mmh.getDataFolder(), "temp_" + updateFile.getName());
		String jarUpdatePath = "heads/" + updateFile.getName();

		try {
			JarUtil.copyFileFromJar(jarUpdatePath, tempUpdateFile.getAbsolutePath());
			if (!tempUpdateFile.exists()) {
				CoreUtils.log("No " + updateFile.getName() + " found in JAR, skipping");
				return;
			}
		} catch (Exception e) {
			CoreUtils.warn("Failed to extract " + updateFile.getName() + ": " + e.getMessage());
			return;
		}

		// Call your preserved long logic
		processSingleUpdate(tempUpdateFile, updateFile, headsDirPath, currentVersion);

		tempUpdateFile.delete();
	}

	private void processSingleUpdate(File tempUpdateFile, File serverUpdateFile, String headsDirPath, String currentVersion) {
		File headsDir = new File(headsDirPath);
		if (!headsDir.exists() || !headsDir.isDirectory()) {
			CoreUtils.log("Heads directory does not exist: " + headsDirPath);
			return;
		}

		String fileName = serverUpdateFile.getName();
		boolean isMainUpdate = fileName.equals("update.json");

		boolean shouldProcess = true;
		boolean forceRefreshFromJar = false;

		// === SMART VERSION + STATUS CHECK FOR ALL UPDATE FILES ===
		if (serverUpdateFile.exists()) {
			try {
				String serverContent = new String(Files.readAllBytes(serverUpdateFile.toPath()));
				JSONObject serverJson = new JSONObject(serverContent);

				boolean isCompleted = serverJson.has(STATUS_KEY)
						&& serverJson.getString(STATUS_KEY).equals(STATUS_COMPLETED);

				String serverProcessed = serverJson.optString(PROCESSED_VERSION_KEY, "");

				if (isCompleted && serverProcessed.equals(currentVersion)) {
					CoreUtils.log(fileName + " is up-to-date and completed for " + currentVersion + " — skipping");
					shouldProcess = false;
				}
				else if (isCompleted) {
					CoreUtils.log(fileName + " completed for older version (" + serverProcessed
							+ " ≠ " + currentVersion + ") → will delete + refresh from JAR");
					forceRefreshFromJar = true;
				}
				else {
					CoreUtils.log(fileName + " not completed or missing version → will process");
				}
			} catch (Exception e) {
				CoreUtils.warn("Error reading " + fileName + ": " + e.getMessage());
				// We still try to process on error (fail-safe)
			}
		}

		if (!shouldProcess) {
			return;                    // Early exit - nothing else runs
		}

		// === FORCE REFRESH FROM JAR WHEN NEEDED ===
		if (forceRefreshFromJar || !serverUpdateFile.exists()) {
			if (serverUpdateFile.exists()) {
				try {
					serverUpdateFile.delete();
					CoreUtils.log("Deleted outdated " + fileName);
				} catch (Exception ignored) {}
			}

			try {
				Files.copy(tempUpdateFile.toPath(), serverUpdateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				CoreUtils.log("Copied fresh " + fileName + " from JAR");
			} catch (IOException e) {
				CoreUtils.warn("Failed to copy " + fileName + " from JAR: " + e.getMessage());
				return;
			}
		}

		// Decide which file to actually read/process (fresh JAR version for numbered, updated server for main)
		File fileToRead = isMainUpdate ? serverUpdateFile : tempUpdateFile;
		CoreUtils.log("[DEBUG] Starting process for file: " + fileName);
		CoreUtils.log("[DEBUG] Using fileToRead: " + fileToRead.getAbsolutePath() + " | exists=" + fileToRead.exists() + " | size=" + fileToRead.length());
		/* boolean shouldProcess = true;

		// General check for ALL files: skip if "status" is "completed"
		try {
			String serverContent = new String(Files.readAllBytes(serverUpdateFile.toPath()));
			JSONObject serverJson = new JSONObject(serverContent);
			if (serverJson.has(STATUS_KEY) && serverJson.getString(STATUS_KEY).equals(STATUS_COMPLETED)) {
				CoreUtils.log(fileName + " status is already 'completed', skipping processing");
				shouldProcess = false;
			}
		} catch (Exception e) {
			CoreUtils.warn("Error checking status in " + fileName + ": " + e.getMessage());
			// Proceed on error (assume needs processing)
		}

		// Additional version check ONLY for main update.json (keep your original logic here)
		if (isMainUpdate && shouldProcess && serverUpdateFile.exists()) {
			try {
				String serverContent = new String(Files.readAllBytes(serverUpdateFile.toPath()));
				JSONObject serverJson = new JSONObject(serverContent);
				if (serverJson.has(PROCESSED_VERSION_KEY)) {
					String serverProcessedVersion = serverJson.getString(PROCESSED_VERSION_KEY);
					if (compareVersions(serverProcessedVersion, currentVersion) >= 0) {
						CoreUtils.log("Main update.json already processed for " + serverProcessedVersion + " (>= " + currentVersion + "), skipping");
						shouldProcess = false;
					} else {
						CoreUtils.log("Main update.json outdated (processed for " + serverProcessedVersion + " < " + currentVersion + "), will update and process");
					}
				} else {
					CoreUtils.log("Main update.json lacks processedVersion, will process for " + currentVersion);
				}
			} catch (Exception e) {
				CoreUtils.warn("Error checking version in main update.json: " + e.getMessage());
				// Proceed on error
			}
		}

		if (!shouldProcess) {
			return;
		}

		// For main update.json: refresh it from JAR
		if (isMainUpdate) {
			try {
				Files.copy(tempUpdateFile.toPath(), serverUpdateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				CoreUtils.log("Copied fresh " + fileName + " from JAR to server for processing");
			} catch (IOException e) {
				CoreUtils.warn("Failed to copy fresh " + fileName + " to server: " + e.getMessage());
				return;
			}
		}

		// Decide which file to actually read/process (fresh JAR version for numbered, updated server for main)
		File fileToRead = isMainUpdate ? serverUpdateFile : tempUpdateFile;
		CoreUtils.log("[DEBUG] Starting process for file: " + fileName);
		CoreUtils.log("[DEBUG] Using fileToRead: " + fileToRead.getAbsolutePath() + " | exists=" + fileToRead.exists() + " | size=" + fileToRead.length());
		CoreUtils.log("[DEBUG] shouldProcess = " + shouldProcess + " | isMainUpdate = " + isMainUpdate);//*/

		try {
			String content = new String(Files.readAllBytes(fileToRead.toPath()));
			JSONObject json = new JSONObject(content);
			boolean fileModified = false;

			// Step 1: Load chance map (used later for migration)
			Map<String, Double> chanceMap = loadChanceMapFromConfig();

			// Step 2: Process file operations (backup and/or delete)
			JSONArray files = json.optJSONArray("files");
			if (files != null) {
				File backupBaseDir = new File(mmh.getDataFolder(), "backup");
				if (!backupBaseDir.exists()) {
					backupBaseDir.mkdirs(); // Create backup directory if it doesn't exist
					CoreUtils.log("Created backup directory: " + backupBaseDir.getAbsolutePath());
				}

				for (int i = 0; i < files.length(); i++) {
					JSONObject fileEntry = files.getJSONObject(i);
					String filePath = fileEntry.optString("file", "");
					boolean backup = fileEntry.optBoolean("backup", false);
					boolean delete = fileEntry.optBoolean("delete", false);
					boolean replace = fileEntry.optBoolean("replace", false);

					boolean backupAndDelete = fileEntry.optBoolean("backup/delete", false);
					boolean fullReplace = fileEntry.optBoolean("backup/delete/replace", false);
					boolean backupAndPatch = fileEntry.optBoolean("backup/patch", false);

					if (filePath.isEmpty()) {
						continue;
					}

					if (fullReplace) {
						backup = delete = replace = true;
					}
					// If "backup/delete" is true, set both backup and delete to true
					if (backupAndDelete) {
						backup = true;
						delete = true;
					} else if (backupAndPatch) {
						backup = true;
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
					backupFileIfRequested(sourceFile, backupFile, backup);

					// === NEW: Root-level "if" conditional logic ===
					JSONObject ifBranch = fileEntry.optJSONObject("if");
					if (ifBranch != null && !ifBranch.isEmpty()) {
						CoreUtils.debug("[IF-START] Found root-level 'if' conditional for: " + filePath);

						String serverContent = new String(Files.readAllBytes(sourceFile.toPath()));
						JSONObject serverJson = new JSONObject(serverContent);
						String currentStructure = serverJson.optString("structure", "flat");
						CoreUtils.debug("[IF-STRUCT] Server structure: " + currentStructure);

						JSONObject branch = ifBranch.optJSONObject(currentStructure);
						if (branch == null || branch.isEmpty()) {
							CoreUtils.debug("[IF-SKIP] No branch for structure '" + currentStructure + "' — using normal actions");
							// Continue to normal processing below
						} else {
							CoreUtils.debug("[IF-APPLY] Applying branch for '" + currentStructure + "'");

							// Override main actions from the branch
							backup = branch.optBoolean("backup", backup);
							delete = branch.optBoolean("delete", delete);
							replace = branch.optBoolean("replace", replace);

							// Patch from branch if present (overrides main patch)
							if (branch.has("patch") && !branch.isNull("patch")) {
								JSONObject branchPatch = branch.getJSONObject("patch");
								CoreUtils.debug("[IF-PATCH] Using branch patch");
								fileModified |= applyConditionalPatchIfRequested(sourceFile, branch, filePath, headsDirPath);
							}

							// Compare from branch if present (overrides main compare)
							if (branch.optBoolean("compare", false)) {
								CoreUtils.debug("[IF-COMPARE] Using branch compare");
								fileModified |= compareAndForceFieldsFromJarIfRequested(sourceFile, branch, filePath, headsDir, headsDirPath);
							}

							// ... add other branch overrides here if needed (e.g., "delete": true)
							// Do NOT continue — let normal actions run with updated values
						}
					}

					// Perform deletion if requested
					fileModified |= deleteFileIfRequested(sourceFile, delete, filePath);

					// Perform full replacement from JAR if requested
					fileModified |= replaceFileFromJarIfRequested(filePath, sourceFile, replace);

					// Perform conditional patch if requested
					fileModified |= applyConditionalPatchIfRequested(sourceFile, fileEntry, filePath, headsDirPath);

					// Perform compare and force fields from JAR if requested
					fileModified |= compareAndForceFieldsFromJarIfRequested(sourceFile, fileEntry, filePath, headsDir, headsDirPath);

					if (fileModified) {
						CoreUtils.log("File " + filePath + " was modified during processing");
					}
				}
			}

			// Step 4: Migrate chances to entity JSON files (global, after per-file operations)
			migrateChancesToEntityFiles(chanceMap, headsDir);

			// Step 5: Mark as completed and add processed version
			// Re-read the file (in case processing changed it)
			String finalContent = new String(Files.readAllBytes(serverUpdateFile.toPath()));
			JSONObject finalJson = new JSONObject(finalContent);
			finalJson.put(STATUS_KEY, STATUS_COMPLETED);
			finalJson.put(PROCESSED_VERSION_KEY, currentVersion);

			try (FileWriter writer = new FileWriter(serverUpdateFile)) {
				writer.write(finalJson.toString(4));
				CoreUtils.log("Marked " + fileName + " as completed for plugin version " + currentVersion);
			} catch (IOException e) {
				CoreUtils.warn("Failed to write completed status to " + fileName + ": " + e.getMessage());
			}

		} catch (Exception e) {  // Broader catch for processing errors
			CoreUtils.warn("Error processing " + fileName + ": " + e.getMessage());
			e.printStackTrace();
			// Don't set completed on error, so it can retry
		} finally {
			if (tempUpdateFile.exists()) {
				tempUpdateFile.delete();
			}
		}
	}

	/**
	 * Loads the chance percentages from chance_config.yml into a Map.
	 * Logs progress, invalid values, and errors.
	 *
	 * @return A map of entity/base keys → chance (0.0–100.0), empty map if file missing or error
	 */
	private Map<String, Double> loadChanceMapFromConfig() {
		Map<String, Double> chanceMap = new HashMap<>();

		File chanceConfigFile = new File(mmh.getDataFolder(), "chance_config.yml");
		if (!chanceConfigFile.exists()) {
			CoreUtils.log("chance_config.yml not found, skipping chance migration.");
			return chanceMap;  // empty map
		}

		CoreUtils.log("Reading chance_config.yml to migrate chances to JSON files...");

		YamlConfiguration chanceConfig = new YamlConfiguration();
		try {
			chanceConfig.load(chanceConfigFile);
			int entryCount = 0;

			for (String key : chanceConfig.getKeys(true)) {
				if (key.startsWith("chance_percent.")) {
					String entityKey = key.replace("chance_percent.", "");
					double chance = chanceConfig.getDouble(key, -1.0); // -1.0 = no value

					if (chance >= 0.0 && chance <= 100.0) {
						chanceMap.put(entityKey, chance);
						CoreUtils.log("Found chance for " + entityKey + ": " + chance);
						entryCount++;
					} else {
						CoreUtils.log("Invalid chance value for " + entityKey + ": " + chance
								+ ", must be between 0.0 and 100.0");
					}
				}
			}

			CoreUtils.log("Total chances found in chance_config.yml: " + entryCount);

		} catch (Exception e) {
			CoreUtils.warn("Error reading chance_config.yml: " + e.getMessage());
			e.printStackTrace();
			// Return partial map if error (fail-soft)
		}

		return chanceMap;
	}

	/**
	 * Backs up the source file to the specified backup location if backup is requested.
	 * Creates parent directories if needed. Logs success or failure.
	 *
	 * @param sourceFile     The file to back up (e.g., the server JSON being updated)
	 * @param backupFile     The target backup path
	 * @param backupRequested Whether backup should be performed (from the update entry)
	 */
	private void backupFileIfRequested(File sourceFile, File backupFile, boolean backupRequested) {
		if (!backupRequested) {
			CoreUtils.debug("Backup skipped (not requested) for: " + sourceFile.getName());
			return;
		}

		if (!sourceFile.exists()) {
			CoreUtils.log("File not found, no backup needed: " + sourceFile.getAbsolutePath());
			return;
		}

		try {
			// Ensure the backup directory structure exists
			File backupParentDir = backupFile.getParentFile();
			if (!backupParentDir.exists()) {
				backupParentDir.mkdirs();
				CoreUtils.debug("Created backup parent directory: " + backupParentDir.getAbsolutePath());
			}

			Files.copy(sourceFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			CoreUtils.log("Backed up file to: " + backupFile.getAbsolutePath());
		} catch (IOException e) {
			CoreUtils.warn("Failed to back up file " + sourceFile.getAbsolutePath() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Deletes the source file if deletion is requested in the update entry.
	 * Logs success, failure, or if the file didn't exist.
	 *
	 * @param sourceFile       The file to delete (e.g., the server head JSON)
	 * @param deleteRequested  Whether deletion was requested (from the update entry's "delete" flag)
	 * @param filePath         The relative path for logging (e.g., "entity/allay.json")
	 */
	private boolean deleteFileIfRequested(File sourceFile, boolean deleteRequested, String filePath) {
		if (!deleteRequested) {
			CoreUtils.debug("Deletion skipped (not requested) for: " + filePath);
			return false;
		}

		if (!sourceFile.exists()) {
			CoreUtils.log("File not found, no deletion needed: " + filePath);
			return false;
		}

		if (sourceFile.delete()) {
			CoreUtils.log("Deleted file as specified in update: " + filePath);
			return true;
		} else {
			CoreUtils.warn("Failed to delete file: " + filePath);
			return false;
		}
	}

	/**
	 * Replaces the target server file with a fresh copy from the JAR if replacement is requested.
	 * Uses JarUtil to copy directly. Logs success or failure.
	 *
	 * @param filePath          The relative path (e.g., "entity/tropical_fish.json") for logging and JAR path construction
	 * @param targetFile        The server-side file to replace (sourceFile from the loop)
	 * @param replaceRequested  Whether replacement was requested (from the update entry's "replace" flag)
	 */
	private boolean replaceFileFromJarIfRequested(String filePath, File targetFile, boolean replaceRequested) {
		if (!replaceRequested) {
			CoreUtils.debug("Replacement skipped (not requested) for: " + filePath);
			return false;
		}

		String jarSourcePath = "heads/" + filePath;  // Consistent with your convention

		try {
			JarUtil.copyFileFromJar(jarSourcePath, targetFile.getAbsolutePath());
			if (targetFile.exists()) {
				CoreUtils.log("Replaced " + filePath + " with fresh version from JAR");
				return true;
			} else {
				CoreUtils.warn("Failed to copy new " + filePath + " from JAR (file not found in JAR?)");
				return false;
			}
		} catch (Exception e) {
			CoreUtils.warn("Error replacing " + filePath + " from JAR: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Applies a conditional patch from the update entry to the target file if patching is requested.
	 * Handles flat/nested structures, optional full replace from JAR, and field patches.
	 * Logs all steps, successes, and failures.
	 *
	 * @param targetFile     The server-side head JSON file to patch
	 * @param fileEntry    The 'patch' JSONObject from the file entry (may be null/empty)
	 * @param filePath       Relative path for logging and JAR source (e.g., "entity/copper_golem.json")
	 * @param headsDirPath   Base heads directory (used if needed for context)
	 * @return true if the file was modified (and should be saved), false otherwise
	 */
	private boolean applyConditionalPatchIfRequested(File targetFile, JSONObject fileEntry, String filePath, String headsDirPath) {
		CoreUtils.debug("[PATCH-START] Entering patch handler for: " + filePath);

		// Correct detection: check if "patch" key exists and is not null
		boolean hasPatch = fileEntry.has("patch") && !fileEntry.isNull("patch");
		CoreUtils.debug("[PATCH-FLAG] Has patch object: " + hasPatch);

		if (!hasPatch) {
			CoreUtils.debug("[PATCH-SKIP] No patch object present → early return");
			return false;
		}

		// Fetch the actual patch object
		JSONObject patchConfig = fileEntry.optJSONObject("patch"); // safe, returns null if not object
		if (patchConfig == null || patchConfig.isEmpty()) {
			CoreUtils.debug("[PATCH-SKIP] Patch object is empty or not an object");
			return false;
		}

		CoreUtils.debug("[PATCH-RAW] Raw patch object: " + patchConfig.toString(2));

		if (!targetFile.exists()) {
			CoreUtils.debug("[PATCH-SKIP] Target file does not exist: " + filePath);
			return false;
		}

		boolean modified = false;

		try {
			// 1. Load SERVER JSON first — THIS determines the current structure
			String serverContent = new String(Files.readAllBytes(targetFile.toPath()));
			JSONObject serverJson = new JSONObject(serverContent);
			String currentStructure = serverJson.optString("structure", "flat"); // default to flat if missing
			CoreUtils.debug("[PATCH-STRUCT] Server current structure: " + currentStructure);

			// 2. Choose which patch block to use based on SERVER structure
			JSONObject applicablePatch = null;
			String patchType = "none";

			// NEW: Check for conditional "if" branch first (priority)
			JSONObject ifBranch = patchConfig.optJSONObject("if");
			if (ifBranch != null && !ifBranch.isEmpty()) {
				CoreUtils.debug("[PATCH-IF] Found conditional 'if' branch");

				// Use the branch that matches the server's current structure
				applicablePatch = ifBranch.optJSONObject(currentStructure);
				patchType = currentStructure;

				if (applicablePatch == null || applicablePatch.isEmpty()) {
					CoreUtils.debug("[PATCH-IF-SKIP] No patch defined for structure '" + currentStructure + "' in 'if' — ignoring");
					return false;
				}
			} else {
				// Fallback to legacy direct flat/nested selection (old behavior)
				CoreUtils.debug("[PATCH-LEGACY] No 'if' conditional — using direct flat/nested");
				if (currentStructure.equals("nested") && patchConfig.has("nested")) {
					applicablePatch = patchConfig.getJSONObject("nested");
					patchType = "nested";
				} else if (patchConfig.has("flat")) {
					applicablePatch = patchConfig.getJSONObject("flat");
					patchType = "flat";
				}
			}

			if (applicablePatch == null) {
				CoreUtils.debug("[PATCH-SKIP] No applicable patch block for structure '" + currentStructure + "'");
				return false;
			}

			CoreUtils.debug("[PATCH-TYPE] Applying " + patchType + " patch to " + filePath);

			// 3. Special: full file replace for nested if requested
			if ("nested".equals(patchType) && applicablePatch.has("replace") && applicablePatch.getBoolean("replace")) {
				String jarSourcePath = "heads/" + filePath;
				CoreUtils.debug("[PATCH-REPLACE] Full nested replace requested → copying from JAR: " + jarSourcePath);
				try {
					JarUtil.copyFileFromJar(jarSourcePath, targetFile.getAbsolutePath());
					CoreUtils.log("Replaced nested file " + filePath + " with JAR version");
					modified = true;
				} catch (Exception e) {
					CoreUtils.warn("Failed to perform nested replace for " + filePath + ": " + e.getMessage());
				}
			}

			// 4. Remove replace flag before applying remaining field patches
			applicablePatch.remove("replace");

			// 5. Apply field patches if anything left
			if (!applicablePatch.isEmpty()) {
				CoreUtils.debug("[PATCH-FIELDS] Applying " + applicablePatch.keySet().size() + " field patches");

				if ("flat".equals(patchType) || !currentStructure.equals("nested")) {
					// Flat / trade / other: apply to single/multiple heads array
					JSONArray heads = serverJson.optJSONArray("heads");
					if (heads != null) {
						for (int j = 0; j < heads.length(); j++) {
							JSONObject head = heads.getJSONObject(j);
							modified |= applyPatchToHead(head, applicablePatch);
						}
					} else {
						// Fallback: if no "heads" array, try single "head" object (trade case)
						JSONObject singleHead = serverJson.optJSONObject("head");
						if (singleHead != null) {
							modified |= applyPatchToHead(singleHead, applicablePatch);
						}
					}
				} else { // nested
					JSONObject heads = serverJson.optJSONObject("heads");
					if (heads != null) {
						JSONObject specificPatches = applicablePatch;
						JSONObject defaultPatch = applicablePatch;

						if (applicablePatch.has("byLangName")) {
							specificPatches = applicablePatch.getJSONObject("byLangName");
						}
						if (applicablePatch.has("default")) {
							defaultPatch = applicablePatch.getJSONObject("default");
						}

						modified |= applyPatchToNestedHeadsWithLangName(heads, specificPatches, defaultPatch);
					}
				}
			}

			// 6. Save if anything changed
			if (modified) {
				try (FileWriter writer = new FileWriter(targetFile)) {
					writer.write(serverJson.toString(4));
				}
				CoreUtils.log("Successfully patched " + filePath + " (" + patchType + ")");
			} else {
				CoreUtils.debug("[PATCH-NOCHANGE] No modifications needed for " + filePath);
			}

		} catch (Exception e) {
			CoreUtils.warn("[PATCH-ERROR] Failed patching " + filePath + ": " + e.getMessage());
			e.printStackTrace();
		}

		return modified;
	}

	/**
	 * If "compare" is true in the file entry, compares the server JSON with the JAR version,
	 * adds missing top-level fields/heads, and forces specified fields (e.g., langKey/langFormat).
	 * Handles flat and nested structures. Cleans up temp JAR file.
	 *
	 * @param serverFile    The target server-side head JSON file
	 * @param fileEntry     The JSONObject from "files" array (contains "compare", "forced")
	 * @param filePath      Relative path for logging and JAR extraction (e.g., "entity/allay.json")
	 * @param headsDir      Base heads directory File (for constructing paths)
	 * @param headsDirPath  String path to heads dir (passed for consistency)
	 * @return true if the file was modified and saved, false otherwise
	 */
	private boolean compareAndForceFieldsFromJarIfRequested(File serverFile, JSONObject fileEntry, String filePath, File headsDir, String headsDirPath) {
		CoreUtils.debug("[COMPARE-START] Entering method for file: " + filePath);

		boolean compare = fileEntry.optBoolean("compare", false);
		CoreUtils.debug("[COMPARE-FLAG] Raw compare value from JSON: " + fileEntry.opt("compare") + " → resolved to: " + compare);

		if (!compare) {
			CoreUtils.debug("[COMPARE-SKIP] compare=false → early return for " + filePath);
			return false;
		}
		CoreUtils.debug("[COMPARE-PROCEED] compare=true → continuing for " + filePath);

		if (filePath.isEmpty()) {
			CoreUtils.debug("[PATH-EMPTY] filePath is empty → skipping " + filePath);
			return false;
		}
		CoreUtils.debug("[PATH-OK] filePath is valid: " + filePath);

		// Get forced fields list
		JSONArray forcedArray = fileEntry.optJSONArray("forced");
		List<String> forcedFields = new ArrayList<>();
		if (forcedArray != null) {
			CoreUtils.debug("[FORCED-ARRAY] Found forced array with " + forcedArray.length() + " items");
			for (int j = 0; j < forcedArray.length(); j++) {
				String field = forcedArray.getString(j);
				forcedFields.add(field);
				CoreUtils.debug("[FORCED-ADD] Added forced field: " + field);
			}
		} else {
			CoreUtils.debug("[FORCED-NONE] No 'forced' array found in entry");
		}

		// JAR path and temp file
		String jarFilePath = "heads/" + filePath;
		File tempJarFile = new File(mmh.getDataFolder(), "temp_" + filePath.replace("/", "_"));
		CoreUtils.debug("[JAR-PATH] Attempting to extract: " + jarFilePath);
		CoreUtils.debug("[TEMP-PATH] Temp file target: " + tempJarFile.getAbsolutePath());

		boolean modified = false;

		try {
			CoreUtils.debug("[JAR-COPY-START] Calling JarUtil.copyFileFromJar...");
			JarUtil.copyFileFromJar(jarFilePath, tempJarFile.getAbsolutePath());
			CoreUtils.debug("[JAR-COPY-END] Copy completed");

			if (!tempJarFile.exists()) {
				CoreUtils.warn("[JAR-MISSING] Temp JAR file does NOT exist after copy: " + jarFilePath);
				return false;
			}
			CoreUtils.debug("[TEMP-EXISTS] Temp file exists, size = " + tempJarFile.length() + " bytes");

			if (tempJarFile.length() == 0) {
				CoreUtils.warn("[TEMP-EMPTY] Temp JAR file is EMPTY (0 bytes): " + jarFilePath);
			}

			// Read server content
			String serverContent;
			if (serverFile.exists()) {
				serverContent = new String(Files.readAllBytes(serverFile.toPath()));
				CoreUtils.debug("[SERVER-READ-OK] Server file read, length: " + serverContent.length() + " chars");
			} else {
				serverContent = "{}";
				CoreUtils.debug("[SERVER-MISSING] Server file does not exist → using empty {}");
			}

			// Read JAR content
			String jarContent = new String(Files.readAllBytes(tempJarFile.toPath()));
			CoreUtils.debug("[JAR-READ-OK] JAR content read, length: " + jarContent.length() + " chars");

			JSONObject serverJson = new JSONObject(serverContent);
			JSONObject jarJson = new JSONObject(jarContent);

			CoreUtils.debug("[JSON-STATUS] Server JSON keys count: " + serverJson.keySet().size());
			CoreUtils.debug("[JSON-STATUS] JAR JSON keys count: " + jarJson.keySet().size());
			CoreUtils.debug("[JSON-STATUS] JAR has 'structure': " + jarJson.has("structure") +
					", has 'heads': " + jarJson.has("heads"));

			// === NEW: Flat → Nested Migration (26.1) ===
			// Only triggers when server file is still flat but JAR is now nested
			String serverStructure = serverJson.optString("structure", "flat");
			String jarStructure = jarJson.optString("structure", "flat");

			if (serverStructure.equals("flat") && jarStructure.equals("nested")) {
				CoreUtils.log("=== Flat → Nested migration triggered for " + filePath + " ===");

				// 1. Extract custom chances from old flat array (protect admin changes)
				Map<String, Double> customChances = new HashMap<>();
				JSONArray serverHeadsArray = serverJson.optJSONArray("heads");
				if (serverHeadsArray != null) {
					for (int i = 0; i < serverHeadsArray.length(); i++) {
						JSONObject head = serverHeadsArray.getJSONObject(i);
						String langName = head.optString("langName", "");
						double chance = head.optDouble("chance", -1.0);
						if (!langName.isEmpty() && chance >= 0.0) {
							customChances.put(langName, chance);
							CoreUtils.debug("Saved custom chance → " + langName + " = " + chance);
						}
					}
				}

				// 2. Backup old flat file
				File backupDir = new File(mmh.getDataFolder(), "backup/flat_to_nested");
				if (!backupDir.exists()) backupDir.mkdirs();
				File backupFile = new File(backupDir, new File(filePath).getName());
				try {
					Files.copy(serverFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					CoreUtils.log("Backed up old flat version → " + backupFile.getAbsolutePath());
				} catch (Exception e) {
					CoreUtils.warn("Backup failed: " + e.getMessage());
				}

				// 3. Replace with fresh nested file from JAR
				try {
					JarUtil.copyFileFromJar("heads/" + filePath, serverFile.getAbsolutePath());
					CoreUtils.log("Replaced with new nested version from JAR");
				} catch (Exception e) {
					CoreUtils.warn("Failed to copy nested file from JAR: " + e.getMessage());
					return false;
				}

				// 4. Reload the new nested JSON
				String newContent = new String(Files.readAllBytes(serverFile.toPath()));
				serverJson = new JSONObject(newContent);

				// 5. Re-apply custom chances
				boolean chancesApplied = applyCustomChancesToNested(serverJson.optJSONObject("heads"), customChances);
				if (chancesApplied) {
					try (FileWriter writer = new FileWriter(serverFile)) {
						writer.write(serverJson.toString(4));
					}
					CoreUtils.log("Re-applied custom chances to new nested structure");
				}

				modified = true;
				CoreUtils.log("Flat → Nested migration completed for " + filePath);
				return true; // Exit early - skip rest of original logic
			}
			// === End of new migration block ===

			// Top-level fields comparison
			CoreUtils.debug("[TOP-START] Starting top-level field comparison");
			for (String key : jarJson.keySet()) {
				if (!serverJson.has(key) && !key.equals("heads")) {
					CoreUtils.debug("[TOP-ADD] Adding missing top-level key: " + key);
					if (key.equals("defaultChance")) {
						serverJson.put(key, 1.0);
					} else {
						serverJson.put(key, jarJson.get(key));
					}
					modified = true;
					CoreUtils.log("Added missing top-level field '" + key + "' to " + filePath);
				}
			}

			// Structure handling
			String structure = jarJson.optString("structure", "flat");
			CoreUtils.debug("[STRUCTURE] Detected: " + structure);

			if (structure.equals("nested")) {
				CoreUtils.debug("[NESTED-START] Processing nested structure");
				JSONObject serverHeads = serverJson.optJSONObject("heads");
				JSONObject jarHeads = jarJson.optJSONObject("heads");

				if (jarHeads == null) {
					CoreUtils.debug("[NESTED-SKIP] No 'heads' in JAR JSON");
				} else {
					CoreUtils.debug("[NESTED-HEADS] JAR has " + jarHeads.keySet().size() + " categories");
					if (serverHeads == null) {
						serverHeads = new JSONObject();
						serverJson.put("heads", serverHeads);
						CoreUtils.debug("[NESTED-CREATE] Created new heads object on server");
						modified = true;
					}

					for (String category : jarHeads.keySet()) {
						JSONObject jarCategory = jarHeads.getJSONObject(category);
						JSONObject serverCategory = serverHeads.optJSONObject(category);

						if (serverCategory == null) {
							serverHeads.put(category, jarCategory);
							modified = true;
							CoreUtils.log("Added missing category '" + category + "' to " + filePath);
							continue;
						}

						// Flexible handling for both old (2-level) and new (3-level) nesting
						for (String key1 : jarCategory.keySet()) {
							Object obj1 = jarCategory.get(key1);
							if (!(obj1 instanceof JSONObject)) continue;   // Skip primitives like "chance"

							JSONObject jarLevel1 = (JSONObject) obj1;
							JSONObject serverLevel1 = serverCategory.optJSONObject(key1);

							if (serverLevel1 == null) {
								serverCategory.put(key1, jarLevel1);
								modified = true;
								continue;
							}

							for (String key2 : jarLevel1.keySet()) {
								Object obj2 = jarLevel1.get(key2);
								if (!(obj2 instanceof JSONObject)) continue;   // Skip primitives

								JSONObject jarHead = (JSONObject) obj2;
								JSONObject serverHead = serverLevel1.optJSONObject(key2);

								if (serverHead == null) {
									serverLevel1.put(key2, jarHead);
									modified = true;
									CoreUtils.log("Added missing head '" + key2 + "' in " + category + "." + key1 + " to " + filePath);
									continue;
								}

								CoreUtils.debug("[HEAD-FIELDS] Comparing fields in " + category + "." + key1 + "." + key2);
								modified |= mergeHeadFields(serverHead, jarHead, fileEntry, filePath,
										category + "." + key1 + "." + key2);
							}
						}
					}
				}
			} else if (structure.equals("trade")) {
				CoreUtils.debug("[TRADE-START] Processing trade structure");

				JSONObject serverHead = serverJson.optJSONObject("head");
				JSONObject jarHead = jarJson.optJSONObject("head");

				if (jarHead == null) {
					CoreUtils.debug("[TRADE-SKIP] No 'head' object in JAR JSON");
				} else {
					CoreUtils.debug("[TRADE-HEAD] JAR has 'head' object");

					if (serverHead == null) {
						serverHead = new JSONObject();
						serverJson.put("head", serverHead);
						CoreUtils.debug("[TRADE-CREATE] Created new 'head' object on server");
					}

					// Step 1: Handle forced fields first (always overwrite if present in JAR)
					JSONArray forced = fileEntry.optJSONArray("forced");
					if (forced != null) {
						for (int f = 0; f < forced.length(); f++) {
							String forcedField = forced.getString(f);
							if (jarHead.has(forcedField)) {
								Object jarValue = jarHead.get(forcedField);
								serverHead.put(forcedField, jarValue);
								modified = true;
								CoreUtils.debug("Forced merged '" + forcedField + "' in trade head = " + jarValue);
							}
						}
					}

					// Step 2: Normal merge (only add missing, skip forced fields)
					for (String field : jarHead.keySet()) {
						boolean isForced = false;
						if (forced != null) {
							for (int f = 0; f < forced.length(); f++) {
								if (forced.getString(f).equals(field)) {
									isForced = true;
									break;
								}
							}
						}

						if (isForced) {
							CoreUtils.debug("Skipping field '" + field + "' — already handled as forced");
							continue;
						}

						CoreUtils.debug("[TRADE-FIELD-CHECK] Field: " + field + " | server has: " + serverHead.has(field));
						if (!serverHead.has(field)) {
							serverHead.put(field, jarHead.get(field));
							modified = true;
							CoreUtils.log("Added missing field '" + field + "' to head in " + filePath);
						}
					}
				}
			} else { // flat
				CoreUtils.debug("[FLAT-START] Processing flat structure");
				JSONArray serverHeads = serverJson.optJSONArray("heads");
				JSONArray jarHeads = jarJson.optJSONArray("heads");

				if (jarHeads == null) {
					CoreUtils.debug("[FLAT-SKIP] No 'heads' array in JAR JSON");
				} else {
					CoreUtils.debug("[FLAT-HEADS] JAR has " + jarHeads.length() + " heads");
					if (serverHeads == null) {
						serverHeads = new JSONArray();
						serverJson.put("heads", serverHeads);
						CoreUtils.debug("[FLAT-CREATE] Created new 'heads' array on server");
					}

					// Step 1: Handle forced fields first (always overwrite if present in JAR)
					JSONArray forced = fileEntry.optJSONArray("forced");
					if (forced != null) {
						for (int h = 0; h < serverHeads.length(); h++) {
							JSONObject serverHead = serverHeads.getJSONObject(h);
							JSONObject jarHead = findMatchingJarHead(jarHeads, serverHead.optString("uuid", ""));
							if (jarHead != null) {
								for (int f = 0; f < forced.length(); f++) {
									String forcedField = forced.getString(f);
									if (jarHead.has(forcedField)) {
										Object jarValue = jarHead.get(forcedField);
										serverHead.put(forcedField, jarValue);
										modified = true;
										CoreUtils.debug("Forced merged '" + forcedField + "' in flat head '" + serverHead.optString("langName", "unknown") + "' = " + jarValue);
									}
								}
							}
						}
					}

					// Step 2: Normal merge (add missing heads/fields, skip forced)
					// ... (your existing flat merge logic)
				}
			}

			// Final save
			if (modified) {
				CoreUtils.debug("[SAVE-START] Modified=true → saving file");
				try (FileWriter writer = new FileWriter(serverFile)) {
					writer.write(serverJson.toString(4));
					CoreUtils.log("Updated " + filePath + " after compare/forced operations");
				}
				CoreUtils.debug("[SAVE-DONE] Save completed");
				return true;
			} else {
				CoreUtils.debug("[NO-MODIFY] No changes detected for " + filePath);
			}

		} catch (Exception e) {
			CoreUtils.warn("[COMPARE-ERROR] Exception in compare/force for " + filePath + ": " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (tempJarFile.exists()) {
				CoreUtils.debug("[CLEANUP] Deleting temp file: " + tempJarFile.getName());
				tempJarFile.delete();
			} else {
				CoreUtils.debug("[CLEANUP] No temp file to delete");
			}
		}

		CoreUtils.debug("[COMPARE-END] Method returning false (no modification)");
		return false;
	}

	private boolean mergeHeadFields(JSONObject serverHead, JSONObject jarHead, JSONObject fileEntry, String filePath, String headPath) {
		boolean modified = false;

		// Step 1: Handle forced fields first (always overwrite if present in JAR)
		JSONArray forced = fileEntry.optJSONArray("forced");
		if (forced != null) {
			for (int f = 0; f < forced.length(); f++) {
				String forcedField = forced.getString(f);
				if (jarHead.has(forcedField)) {
					Object jarValue = jarHead.get(forcedField);
					serverHead.put(forcedField, jarValue); // Always overwrite for forced
					modified = true;
					CoreUtils.debug("Forced merged '" + forcedField + "' in head '" + headPath + "' = " + jarValue);
				}
			}
		}

		// Step 2: Normal merge (only add missing, skip forced fields)
		for (String field : jarHead.keySet()) {
			boolean isForced = false;
			if (forced != null) {
				for (int f = 0; f < forced.length(); f++) {
					if (forced.getString(f).equals(field)) {
						isForced = true;
						break;
					}
				}
			}

			if (isForced) {
				CoreUtils.debug("Skipping field '" + field + "' in head '" + headPath + "' — already handled as forced");
				continue;
			}

			CoreUtils.debug("[FIELD-CHECK] Field: " + field + " | server has: " + serverHead.has(field));
			if (!serverHead.has(field)) {
				serverHead.put(field, jarHead.get(field));
				modified = true;
				CoreUtils.log("Added missing field '" + field + "' to head '" + headPath + "' in " + filePath);
			}
		}

		return modified;
	}

	/**
	 * Finds the matching JAR head object in a flat array by uuid.
	 * Returns null if no match found.
	 */
	private JSONObject findMatchingJarHead(JSONArray jarHeads, String uuid) {
		if (jarHeads == null || uuid == null || uuid.isEmpty()) {
			return null;
		}

		for (int i = 0; i < jarHeads.length(); i++) {
			JSONObject jarHead = jarHeads.optJSONObject(i);
			if (jarHead != null && uuid.equals(jarHead.optString("uuid", ""))) {
				CoreUtils.debug("Found matching JAR head for uuid: " + uuid);
				return jarHead;
			}
		}

		CoreUtils.debug("No matching JAR head found for uuid: " + uuid);
		return null;
	}

	/**
	 * Migrates chance values from the loaded chanceMap to all entity JSON files in heads/entity/.
	 * Handles both flat and nested structures, updates "chance" field where langName matches,
	 * logs updates/skips, and saves modified files.
	 *
	 * @param chanceMap Map of entity/langName keys to chance values (from chance_config.yml)
	 * @param headsDir  The base heads directory File (e.g., plugins/MoreMobHeads/heads)
	 */
	private void migrateChancesToEntityFiles(Map<String, Double> chanceMap, File headsDir) {
		if (chanceMap.isEmpty()) {
			CoreUtils.debug("No chances to migrate (chanceMap empty)");
			return;
		}

		CoreUtils.log("Migrating chances from chance_config.yml to JSON files...");

		File entityDir = new File(headsDir, "entity");
		if (!entityDir.exists() || !entityDir.isDirectory()) {
			CoreUtils.log("Entity directory does not exist: " + entityDir.getAbsolutePath());
			return;
		}

		File[] jsonFiles = entityDir.listFiles((dir, name) -> name.endsWith(".json") && !name.equals("update.json"));
		if (jsonFiles == null || jsonFiles.length == 0) {
			CoreUtils.log("No JSON files found in " + entityDir.getAbsolutePath() + " to process for chance migration.");
			return;
		}

		CoreUtils.log("Found " + jsonFiles.length + " JSON files in " + entityDir.getAbsolutePath() + " to process for chance migration.");

		for (File jsonFile : jsonFiles) {
			try {
				CoreUtils.log("Processing JSON file: " + jsonFile.getName());
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

								Double chance = null;
								if (category.equals("zombie_villager")) {
									chance = chanceMap.get("zombie_villager");
								} else {
									chance = chanceMap.get(langName);
									if (chance == null) {
										String baseEntity = langName.split("\\.")[0];
										chance = chanceMap.get(baseEntity);
									}
								}

								if (chance != null) {
									double oldChance = headData.optDouble("chance", -1.0);
									if (oldChance != chance) {
										headData.put("chance", chance); // double
										modified = true;
										CoreUtils.log("Updated chance for " + (category.equals("zombie_villager") ? "zombie_villager." + variant : langName) +
												" (" + langName + ") to " + chance);
									} else {
										CoreUtils.log("Chance for " + langName + " is already " + chance + ", no update needed.");
									}
								} else {
									CoreUtils.log("No chance found for " + langName + " in chance_config.yml, skipping update");
								}
							}
						}
					}
				} else { // flat
					JSONArray heads = jsonData.optJSONArray("heads");
					if (heads != null) {
						for (int i = 0; i < heads.length(); i++) {
							JSONObject headData = heads.getJSONObject(i);
							String langName = headData.optString("langName", "");

							Double chance = chanceMap.get(langName);
							if (chance != null) {
								double oldChance = headData.optDouble("chance", -1.0);
								if (oldChance != chance) {
									headData.put("chance", chance);
									modified = true;
									CoreUtils.log("Updated chance for " + langName + " to " + chance);
								} else {
									CoreUtils.log("Chance for " + langName + " is already " + chance + ", no update needed.");
								}
							} else {
								CoreUtils.log("No chance found for " + langName + " in chance_config.yml, skipping update");
							}
						}
					}
				}

				if (modified) {
					try (FileWriter writer = new FileWriter(jsonFile)) {
						writer.write(jsonData.toString(4));
						CoreUtils.log("Updated chances in " + jsonFile.getName());
					}
				} else {
					CoreUtils.log("No chance updates needed for " + jsonFile.getName());
				}

			} catch (Exception e) {
				CoreUtils.warn("Error updating chances in " + jsonFile.getName() + ": " + e.getMessage());
				e.printStackTrace();
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

	private boolean applyPatchToHead(JSONObject head, JSONObject patchFields) {
		boolean modified = false;
		for (String field : patchFields.keySet()) {
			Object value = patchFields.get(field);
			// Only update if missing or different
			if (!head.has(field) || !head.get(field).equals(value)) {
				head.put(field, value);
				modified = true;
				CoreUtils.log("Patched field '" + field + "' in head '" + head.optString("langName", "unknown") + "'");
			}
		}
		return modified;
	}

	private boolean applyPatchToNestedHeads(JSONObject categoryHeads, JSONObject patchFields) {
		boolean modified = false;
		for (String categoryKey : categoryHeads.keySet()) {
			Object categoryObj = categoryHeads.get(categoryKey);
			if (categoryObj instanceof JSONObject) {
				JSONObject category = (JSONObject) categoryObj;
				for (String variantKey : category.keySet()) {
					Object headObj = category.get(variantKey);
					if (headObj instanceof JSONObject) {
						JSONObject head = (JSONObject) headObj;
						modified |= applyPatchToHead(head, patchFields);
					}
				}
			}
		}
		return modified;
	}

	private boolean applyPatchToNestedHeadsWithLangName(JSONObject categoryHeads, JSONObject specificPatches, JSONObject defaultPatch) {
		boolean modified = false;
		for (String categoryKey : categoryHeads.keySet()) {
			Object categoryObj = categoryHeads.get(categoryKey);
			if (categoryObj instanceof JSONObject) {
				JSONObject category = (JSONObject) categoryObj;
				for (String variantKey : category.keySet()) {
					Object headObj = category.get(variantKey);
					if (headObj instanceof JSONObject) {
						JSONObject head = (JSONObject) headObj;
						String langName = head.optString("langName", "");

						JSONObject headPatch = specificPatches.has(langName)
								? specificPatches.getJSONObject(langName)
								: defaultPatch;

						if (headPatch != null && !headPatch.isEmpty()) {
							modified |= applyPatchToHead(head, headPatch);
						}
					}
				}
			}
		}
		return modified;
	}

	/**
	 * Recursively applies saved custom chances into a nested heads structure.
	 * Used during flat → nested migration.
	 */
	private boolean applyCustomChancesToNested(JSONObject headsObj, Map<String, Double> customChances) {
		if (headsObj == null || customChances.isEmpty()) return false;

		boolean modified = false;

		for (String key : headsObj.keySet()) {
			Object value = headsObj.get(key);
			if (value instanceof JSONObject obj) {
				if (obj.has("langName")) {
					// This is a leaf head
					String langName = obj.optString("langName", "");
					if (customChances.containsKey(langName)) {
						double oldChance = obj.optDouble("chance", -1.0);
						double newChance = customChances.get(langName);
						if (Math.abs(oldChance - newChance) > 0.001) {  // floating point tolerance
							obj.put("chance", newChance);
							modified = true;
							CoreUtils.log("Migrated custom chance for '" + langName + "' → " + newChance);
						}
					}
				} else {
					// Deeper nesting (e.g. category → baby → color)
					modified |= applyCustomChancesToNested(obj, customChances);
				}
			}
		}
		return modified;
	}
}