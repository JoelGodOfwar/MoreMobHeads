package com.github.joelgodofwar.mmh.command;

import com.github.joelgodofwar.mmh.MoreMobHeads;

import com.github.joelgodofwar.mmh.common.error.DetailedErrorReporter;
import com.github.joelgodofwar.mmh.common.error.Report;
import com.github.joelgodofwar.mmh.common.error.ReportType;
import com.github.joelgodofwar.mmh.enums.Perms;
import com.github.joelgodofwar.mmh.handlers.MMHEventHandler;
import com.github.joelgodofwar.mmh.util.heads.HeadManager;
import com.github.joelgodofwar.mmh.util.heads.MobHead;
import com.github.joelgodofwar.mmh.util.heads.MobHeadData;
import lib.github.joelgodofwar.coreutils.util.common.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the /mmh chance command, allowing administrators to manage
 * drop chance configurations for entity mob heads.
 */
public class ChanceCommand {
    private final MoreMobHeads mmh;
    private final PluginLogger LOGGER;
    private final DetailedErrorReporter reporter;
    private final HeadManager headManager;
    private final MMHEventHandler eventHandler;
    public static final ReportType COMMAND_CHANCE_EXECUTE = new ReportType("Error executing ChanceCommand.");

    public ChanceCommand(MoreMobHeads mmh, HeadManager headManager, MMHEventHandler eventHandler) {
        this.mmh = mmh;
        this.LOGGER = mmh.LOGGER;
        this.reporter = new DetailedErrorReporter(mmh);
        this.headManager = headManager;
        this.eventHandler = eventHandler;
    }

    public boolean execute(CommandSender sender, String[] args) {
        try {
            if (!(sender instanceof ConsoleCommandSender) && !Perms.CONFIG.hasPermission(sender)) {
                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command.");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.AQUA + "Usage: /mmh chance <export/import/set>");
                return true;
            }

            String subCommand = args[1].toLowerCase();
            File updateFolder = new File(mmh.getDataFolder(), "update");
            File chanceFile = new File(updateFolder, "entity_chances.json");

            if (subCommand.equals("export")) {
                if (!updateFolder.exists() && !updateFolder.mkdirs()) {
                    sender.sendMessage(ChatColor.DARK_RED + "Failed to create update folder.");
                    return true;
                }

                Map<String, JSONObject> fileData = new HashMap<>();
                Map<String, Double> fileDefaultChances = new HashMap<>();
                for (Map.Entry<String, MobHead> entry : headManager.loadedMobHeads().entrySet()) {
                    MobHead mobHead = entry.getValue();
                    MobHeadData data = mobHead.getData();
                    String filePath = data.getFilePath();
                    if (filePath == null) {
                        continue;
                    }
                    String fileName = new File(filePath).getName();
                    if (!fileName.equals("player.json") && !fileName.equals("named.json")) {
                        fileData.computeIfAbsent(fileName, k -> new JSONObject().put("chances", new JSONObject()))
                                .getJSONObject("chances").put(entry.getKey(), data.getChance());
                        if (!fileDefaultChances.containsKey(fileName)) {
                            try {
                                String content = new String(Files.readAllBytes(new File(filePath).toPath()));
                                JSONObject jsonObj = new JSONObject(content);
                                fileDefaultChances.put(fileName, jsonObj.optDouble("defaultChance", 100.0));
                            } catch (Exception e) {
                                LOGGER.warn("Failed to read defaultChance from " + fileName + ": " + e.getMessage());
                                fileDefaultChances.put(fileName, 100.0);
                            }
                        }
                    }
                }

                File playerFile = new File(mmh.getDataFolder() + "/heads/entity", "player.json");
                if (playerFile.exists()) {
                    JSONObject playerData = new JSONObject().put("chance", mmh.playerChance);
                    fileData.put("player.json", playerData);
                }
                File namedFile = new File(mmh.getDataFolder() + "/heads/entity", "named.json");
                if (namedFile.exists()) {
                    JSONObject namedData = new JSONObject().put("chance", mmh.namedChance);
                    fileData.put("named.json", namedData);
                }

                JSONObject chancesJson = new JSONObject();
                JSONObject files = new JSONObject();
                for (Map.Entry<String, JSONObject> entry : fileData.entrySet()) {
                    String fileName = entry.getKey();
                    JSONObject fileObj = entry.getValue();
                    if (!fileName.equals("player.json") && !fileName.equals("named.json")) {
                        fileObj.put("defaultChance", fileDefaultChances.getOrDefault(fileName, 100.0));
                    }
                    files.put(fileName, fileObj);
                }
                chancesJson.put("files", files);

                try (FileWriter writer = new FileWriter(chanceFile)) {
                    writer.write(chancesJson.toString(4));
                    sender.sendMessage(ChatColor.GREEN + "Exported entity head chances to " + chanceFile.getName() + ".");
                } catch (Exception e) {
                    reporter.reportDetailed(this, Report.newBuilder(COMMAND_CHANCE_EXECUTE).error(e));
                    sender.sendMessage(ChatColor.DARK_RED + "Failed to export to " + chanceFile.getName() + ".");
                    return true;
                }
            } else if (subCommand.equals("import")) {
                if (!chanceFile.exists()) {
                    sender.sendMessage(ChatColor.DARK_RED + chanceFile.getName() + " does not exist.");
                    return true;
                }

                JSONObject files;
                try {
                    String content = new String(Files.readAllBytes(chanceFile.toPath()));
                    files = new JSONObject(content).getJSONObject("files");
                } catch (Exception e) {
                    reporter.reportDetailed(this, Report.newBuilder(COMMAND_CHANCE_EXECUTE).error(e));
                    sender.sendMessage(ChatColor.DARK_RED + "Failed to read " + chanceFile.getName() + ".");
                    return true;
                }

                String headsFolder = mmh.getDataFolder() + "/heads/entity";
                for (String fileName : files.keySet()) {
                    JSONObject fileObj = files.getJSONObject(fileName);
                    File jsonFile = new File(headsFolder, fileName);
                    if (!jsonFile.exists()) {
                        LOGGER.warn("File not found: " + fileName);
                        continue;
                    }

                    if (fileName.equals("player.json")) {
                        double playerChance = fileObj.optDouble("chance", mmh.playerChance);
                        if (playerChance < 0.0 || playerChance > 100.0) {
                            LOGGER.warn("Invalid chance value " + playerChance + " for " + fileName + "; skipping");
                            continue;
                        }
                        try {
                            JSONObject playerJson = new JSONObject().put("chance", playerChance);
                            try (FileWriter writer = new FileWriter(jsonFile)) {
                                writer.write(playerJson.toString(4));
                            }
                            mmh.playerChance = playerChance;
                        } catch (Exception e) {
                            LOGGER.warn("Failed to update " + fileName + ": " + e.getMessage());
                            continue;
                        }
                    } else if (fileName.equals("named.json")) {
                        double namedChance = fileObj.optDouble("chance", mmh.namedChance);
                        if (namedChance < 0.0 || namedChance > 100.0) {
                            LOGGER.warn("Invalid chance value " + namedChance + " for " + fileName + "; skipping");
                            continue;
                        }
                        try {
                            JSONObject namedJson = new JSONObject().put("chance", namedChance);
                            try (FileWriter writer = new FileWriter(jsonFile)) {
                                writer.write(namedJson.toString(4));
                            }
                            mmh.namedChance = namedChance;
                        } catch (Exception e) {
                            LOGGER.warn("Failed to update " + fileName + ": " + e.getMessage());
                            continue;
                        }
                    } else {
                        double defaultChance = fileObj.optDouble("defaultChance", 100.0);
                        if (defaultChance < 0.0 || defaultChance > 100.0) {
                            LOGGER.warn("Invalid defaultChance value " + defaultChance + " for " + fileName + "; using 100.0");
                            defaultChance = 100.0;
                        }
                        JSONObject chances = fileObj.getJSONObject("chances");
                        try {
                            String content = new String(Files.readAllBytes(jsonFile.toPath()));
                            JSONObject jsonObj = new JSONObject(content);
                            jsonObj.put("defaultChance", defaultChance);
                            updateChancesInJson(jsonObj, chances, fileName);
                            try (FileWriter writer = new FileWriter(jsonFile)) {
                                writer.write(jsonObj.toString(4));
                            }
                        } catch (Exception e) {
                            LOGGER.warn("Failed to update " + fileName + ": " + e.getMessage());
                            continue;
                        }
                    }
                }

                eventHandler.loadMobHeads();

                File backupFolder = new File(mmh.getDataFolder(), "backup");
                if (!backupFolder.exists() && !backupFolder.mkdirs()) {
                    sender.sendMessage(ChatColor.DARK_RED + "Failed to create backup folder.");
                    return true;
                }
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
                File backupFile = new File(backupFolder, "entity_chances_" + timestamp + ".json");
                try {
                    Files.move(chanceFile.toPath(), backupFile.toPath());
                    sender.sendMessage(ChatColor.GREEN + "Moved " + chanceFile.getName() + " to " + backupFile.getPath());
                } catch (Exception e) {
                    LOGGER.warn("Failed to move " + chanceFile.getName() + " to backup: " + e.getMessage());
                    sender.sendMessage(ChatColor.DARK_RED + "Failed to move " + chanceFile.getName() + " to backup.");
                }

                sender.sendMessage(ChatColor.GREEN + "Imported entity head chances, updated files, and reloaded heads.");
            } else if (subCommand.equals("set")) {
                if (args.length != 4) {
                    sender.sendMessage(ChatColor.AQUA + "Usage: /mmh chance set <langName> <percent>");
                    return true;
                }

                String langName = args[2].toLowerCase();
                double chance;
                try {
                    chance = Double.parseDouble(args[3]);
                    if (chance < 0.0 || chance > 100.0) {
                        sender.sendMessage(ChatColor.DARK_RED + "Percent must be between 0.0 and 100.0.");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.DARK_RED + "Invalid percent value: " + args[3]);
                    return true;
                }

                if (langName.equals("player")) {
                    File playerFile = new File(mmh.getDataFolder() + "/heads/entity", "player.json");
                    if (!playerFile.exists()) {
                        sender.sendMessage(ChatColor.DARK_RED + "player.json not found.");
                        return true;
                    }
                    try {
                        JSONObject playerJson = new JSONObject().put("chance", chance);
                        try (FileWriter writer = new FileWriter(playerFile)) {
                            writer.write(playerJson.toString(4));
                        }
                        mmh.playerChance = chance;
                        sender.sendMessage(ChatColor.GREEN + "Set player head drop chance to " + chance + "%.");
                    } catch (Exception e) {
                        LOGGER.warn("Failed to update player.json for chance " + chance + ": " + e.getMessage());
                        sender.sendMessage(ChatColor.DARK_RED + "Failed to update player head drop chance.");
                        return true;
                    }
                } else if (langName.equals("named")) {
                    File namedFile = new File(mmh.getDataFolder() + "/heads/entity", "named.json");
                    if (!namedFile.exists()) {
                        sender.sendMessage(ChatColor.DARK_RED + "named.json not found.");
                        return true;
                    }
                    try {
                        JSONObject namedJson = new JSONObject().put("chance", chance);
                        try (FileWriter writer = new FileWriter(namedFile)) {
                            writer.write(namedJson.toString(4));
                        }
                        mmh.namedChance = chance;
                        sender.sendMessage(ChatColor.GREEN + "Set named mob head drop chance to " + chance + "%.");
                    } catch (Exception e) {
                        LOGGER.warn("Failed to update named.json for chance " + chance + ": " + e.getMessage());
                        sender.sendMessage(ChatColor.DARK_RED + "Failed to update named mob head drop chance.");
                        return true;
                    }
                } else if (langName.endsWith(".default")) {
                    String fileName = langName.substring(0, langName.length() - ".default".length()) + ".json";
                    File jsonFile = new File(mmh.getDataFolder() + "/heads/entity", fileName);
                    if (!jsonFile.exists()) {
                        sender.sendMessage(ChatColor.DARK_RED + "File not found: " + fileName);
                        return true;
                    }
                    try {
                        String content = new String(Files.readAllBytes(jsonFile.toPath()));
                        JSONObject jsonObj = new JSONObject(content);
                        jsonObj.put("defaultChance", chance);
                        try (FileWriter writer = new FileWriter(jsonFile)) {
                            writer.write(jsonObj.toString(4));
                        }
                        sender.sendMessage(ChatColor.GREEN + "Set default drop chance for " + fileName + " to " + chance + "%.");
                    } catch (Exception e) {
                        LOGGER.warn("Failed to update defaultChance in " + fileName + ": " + e.getMessage());
                        sender.sendMessage(ChatColor.DARK_RED + "Failed to update default chance for " + fileName + ".");
                        return true;
                    }
                } else {
                    MobHead mobHead = headManager.loadedMobHeads().get(langName);
                    if (mobHead == null) {
                        sender.sendMessage(ChatColor.DARK_RED + "Invalid langName: " + langName);
                        return true;
                    }
                    MobHeadData data = mobHead.getData();
                    String filePath = data.getFilePath();
                    if (filePath == null) {
                        sender.sendMessage(ChatColor.DARK_RED + "No file associated with " + langName);
                        return true;
                    }
                    File jsonFile = new File(filePath);
                    if (!jsonFile.exists()) {
                        sender.sendMessage(ChatColor.DARK_RED + "File not found: " + jsonFile.getName());
                        return true;
                    }

                    try {
                        String content = new String(Files.readAllBytes(jsonFile.toPath()));
                        JSONObject jsonObj = new JSONObject(content);
                        String structure = jsonObj.optString("structure", "flat");
                        boolean updated = false;
                        if ("flat".equalsIgnoreCase(structure)) {
                            JSONArray heads = jsonObj.getJSONArray("heads");
                            for (int i = 0; i < heads.length(); i++) {
                                JSONObject head = heads.getJSONObject(i);
                                if (head.optString("langName").equals(langName)) {
                                    head.put("chance", chance);
                                    updated = true;
                                    break;
                                }
                            }
                        } else if ("nested".equalsIgnoreCase(structure)) {
                            JSONObject heads = jsonObj.getJSONObject("heads");
                            mmh.logDebug("Attempting to update " + langName + " in " + jsonFile.getName());
                            updated = updateNestedChance(heads, langName, chance);
                        }
                        if (!updated) {
                            sender.sendMessage(ChatColor.DARK_RED + "Could not find " + langName + " in " + jsonFile.getName());
                            return true;
                        }
                        try (FileWriter writer = new FileWriter(jsonFile)) {
                            writer.write(jsonObj.toString(4));
                        }
                        data.setChance(chance);
                        sender.sendMessage(ChatColor.GREEN + "Set drop chance for " + langName + " to " + chance + "%.");
                    } catch (Exception e) {
                        LOGGER.warn("Failed to update " + jsonFile.getName() + " for " + langName + ": " + e.getMessage());
                        sender.sendMessage(ChatColor.DARK_RED + "Failed to update chance for " + langName + ".");
                        return true;
                    }
                }

                eventHandler.loadMobHeads();
            } else {
                sender.sendMessage(ChatColor.AQUA + "Usage: /mmh chance <export/import/set>");
            }
            return true;
        } catch (Exception e) {
            reporter.reportDetailed(this, Report.newBuilder(COMMAND_CHANCE_EXECUTE).error(e));
            sender.sendMessage(ChatColor.DARK_RED + "An error occurred while processing the command.");
            return true;
        }
    }

    private void updateChancesInJson(JSONObject jsonObj, JSONObject chances, String fileName) {
        String structure = jsonObj.optString("structure", "flat");
        if ("flat".equalsIgnoreCase(structure)) {
            JSONArray heads = jsonObj.getJSONArray("heads");
            for (int i = 0; i < heads.length(); i++) {
                JSONObject head = heads.getJSONObject(i);
                String langName = head.optString("langName");
                if (chances.has(langName)) {
                    double chance = chances.getDouble(langName);
                    if (chance < 0.0 || chance > 100.0) {
                        LOGGER.warn("Invalid chance value " + chance + " for " + langName + " in " + fileName + "; skipping");
                        continue;
                    }
                    head.put("chance", chance);
                }
            }
        } else if ("nested".equalsIgnoreCase(structure)) {
            JSONObject heads = jsonObj.getJSONObject("heads");
            for (String key : heads.keySet()) {
                updateNestedChances(heads.getJSONObject(key), chances, key, fileName);
            }
        }
    }

    private void updateNestedChances(JSONObject node, JSONObject chances, String parentKey, String fileName) {
        for (String key : node.keySet()) {
            String fullKey = parentKey + "." + key;
            Object value = node.get(key);
            if (value instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) value;
                if (jsonObject.has("langName") && chances.has(fullKey)) {
                    double chance = chances.getDouble(fullKey);
                    if (chance < 0.0 || chance > 100.0) {
                        LOGGER.warn("Invalid chance value " + chance + " for " + fullKey + " in " + fileName + "; skipping");
                        continue;
                    }
                    jsonObject.put("chance", chance);
                } else {
                    updateNestedChances(jsonObject, chances, fullKey, fileName);
                }
            }
        }
    }

    private boolean updateNestedChance(JSONObject node, String langName, double chance) {
        String[] parts = langName.split("\\.");
        JSONObject current = node;
        mmh.logDebug("Processing langName: " + langName + ", parts: " + String.join(",", parts));

        // Traverse to the parent of the final key (e.g., heads.axolotl)
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            if (current.has(part)) {
                Object next = current.get(part);
                if (next instanceof JSONObject) {
                    current = (JSONObject) next;
                    mmh.logDebug("Traversed to: " + part);
                } else {
                    LOGGER.warn("Invalid structure at " + part + " in " + langName);
                    return false;
                }
            } else {
                LOGGER.warn("Key not found: " + part + " in " + langName);
                return false;
            }
        }

        // Update the final key (e.g., blue)
        String finalKey = parts[parts.length - 1];
        if (current.has(finalKey)) {
            Object finalNode = current.get(finalKey);
            if (finalNode instanceof JSONObject) {
                JSONObject head = (JSONObject) finalNode;
                head.put("chance", chance);
                mmh.logDebug("Updated chance for " + langName + " to " + chance);
                return true;
            }
        }
        LOGGER.warn("Could not find " + finalKey + " in " + langName);
        return false;
    }
}