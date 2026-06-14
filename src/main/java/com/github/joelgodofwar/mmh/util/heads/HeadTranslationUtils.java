package com.github.joelgodofwar.mmh.util.heads;

import com.github.joelgodofwar.mmh.MoreMobHeads;

public class HeadTranslationUtils {

    private static final MoreMobHeads mmh = MoreMobHeads.getInstance();

    // Constructor not needed since everything is static, but kept for potential future use
    public HeadTranslationUtils() {
    }

    /**
     * Builds the final translated display name for a head using the provided langFormat template
     * and langKey.
     * <p>
     * Supported placeholders:
     *   %name% / %base%      - Primary base name (aliases — both do the same thing)
     *   %Name% / %Base%      - Capitalized version
     *   %block1%             - First block (in compound langKey)
     *   %block2%             - Second block
     *   %block3%             - Third block (rare)
     *   %variant%            - All variant parts joined with space
     *   %Variant%            - Same as %variant% but with trailing space if non-empty
     *   %var1%, %var2%, %var3% - Individual variant parts
     * <p>
     * langKey formats:
     *   - "block.acacia_leaves"
     *   - "block.blackstone.chiseled.polished"
     *   - "block.deepslate,block.coal_ore"
     */
    public static String buildTranslatedDisplayName(String langFormat, String langKey) {
        //mmh.LOGGER.debug("buildTranslatedDisplayName called with langKey: '" + langKey + "' | langFormat: '" + langFormat + "'");

        if (langKey == null || langKey.isEmpty()) {
            //mmh.LOGGER.debug("langKey is null or empty — returning empty string");
            return "";
        }

        // === Hotfix: Auto-prepend "entity." for legacy/incomplete mob keys ===
        String originalLangKey = langKey; // Save for final log
        if (!langKey.contains(".") || (!langKey.startsWith("block.") && !langKey.startsWith("entity."))) {
            langKey = "entity." + langKey;
            //mmh.LOGGER.debug("Legacy/incomplete key detected — prepended 'entity.' → new langKey: '" + langKey + "'");
        } else {
            //mmh.LOGGER.debug("langKey already properly prefixed — no change");
        }

        // Auto-detect head type from langKey prefix
        String defaultFormatKey = langKey.startsWith("block.") ? "mmh.format.block.default" : "mmh.format.entity.default";
        //mmh.LOGGER.debug("Detected head type → using defaultFormatKey: " + defaultFormatKey);

        // === Resolve langFormat: key or literal? ===
        String actualFormat;
        if (langFormat == null || langFormat.isEmpty()) {
            actualFormat = getLang(defaultFormatKey, "%Variant%%name%");
            //mmh.LOGGER.debug("No langFormat in JSON — resolved default: '" + actualFormat + "'");
            if (actualFormat.isEmpty()) {
                actualFormat = "%Variant%%name%";
                //mmh.LOGGER.debug("Default format missing in lang — using hard fallback: '%Variant%%name%'");
            }
        } else if (langFormat.contains(".") || langFormat.startsWith("mmh.")) {
            actualFormat = getLang(langFormat, "");
            //mmh.LOGGER.debug("langFormat is a key ('" + langFormat + "') — resolved to: '" + actualFormat + "'");
            if (actualFormat.isEmpty()) {
                actualFormat = "%Variant%%name%";
                //mmh.LOGGER.debug("Custom format key not found — falling back to '%Variant%%name%'");
            }
        } else {
            actualFormat = langFormat;
            //mmh.LOGGER.debug("langFormat is literal string from JSON: '" + actualFormat + "'");
        }

        String result = actualFormat;
        //mmh.LOGGER.debug("Starting replacements with format template: '" + result + "'");

        // === Compound base: comma-separated (e.g., "block.deepslate,block.coal_ore") ===
        if (langKey.contains(",")) {
            //mmh.LOGGER.debug("Compound langKey detected: " + langKey);
            String[] baseKeys = langKey.split(",");
            String primaryName = "";

            for (int i = 0; i < baseKeys.length; i++) {
                String key = baseKeys[i].trim();
                if (key.isEmpty()) continue;

                String name = getLang(key, "");
                //mmh.LOGGER.debug("Compound part " + (i+1) + " key: '" + key + "' → translated: '" + name + "'");

                if (name.isEmpty() && key.startsWith("block.")) {
                    String fallback = key.substring(6).replace("_", " ");
                    name = capitalizeWords(fallback);
                    //mmh.LOGGER.debug("  → Block fallback used: '" + name + "'");
                }

                result = result.replace("%block" + (i + 1) + "%", name);

                if (i == 0) {
                    primaryName = name;
                }
            }

            String capitalizedPrimary = capitalize(primaryName);
            result = result.replace("%name%", primaryName)
                    .replace("%Name%", capitalizedPrimary)
                    .replace("%base%", primaryName)
                    .replace("%Base%", capitalizedPrimary);

            // Clear variant placeholders
            result = result.replace("%variant%", "")
                    .replace("%Variant%", "")
                    .replace("%var1%", "")
                    .replace("%var2%", "")
                    .replace("%var3%", "");

            //mmh.LOGGER.debug("Compound final result: '" + result + "'");
            return cleanWhitespace(result);
        }

        // === Single base with dotted variants (e.g., "block.blackstone.chiseled.polished") ===
        String[] parts = langKey.split("\\.");
        if (parts.length >= 2 && "block".equals(parts[0])) {
            //mmh.LOGGER.debug("Block key with variants detected: " + langKey);

            String baseKey = "block." + parts[1];
            String baseName = getLang(baseKey, capitalizeWords(parts[1].replace("_", " ")));
            //mmh.LOGGER.debug("Base block key: '" + baseKey + "' → '" + baseName + "'");

            String capitalizedBase = capitalize(baseName);

            result = result.replace("%name%", baseName)
                    .replace("%Name%", capitalizedBase)
                    .replace("%base%", baseName)
                    .replace("%Base%", capitalizedBase)
                    .replace("%block1%", baseName);

            StringBuilder fullVariant = new StringBuilder();
            String var1 = "", var2 = "", var3 = "";

            for (int i = 2; i < parts.length; i++) {
                String vKey = "variant." + parts[i];
                String v = getLang(vKey, capitalizeWords(parts[i].replace("_", " ")));
                //mmh.LOGGER.debug("Variant part " + (i-1) + ": '" + vKey + "' → '" + v + "'");

                if (i == 2) var1 = v;
                if (i == 3) var2 = v;
                if (i == 4) var3 = v;

                if (fullVariant.length() > 0) fullVariant.append(" ");
                fullVariant.append(v);
            }

            String variantStr = fullVariant.toString();
            String variantCapital = variantStr.isEmpty() ? "" : variantStr + " ";

            result = result.replace("%variant%", variantStr)
                    .replace("%Variant%", variantCapital)
                    .replace("%var1%", var1)
                    .replace("%var2%", var2)
                    .replace("%var3%", var3);

            //mmh.LOGGER.debug("Block final result: '" + result + "'");
            return cleanWhitespace(result);
        }
        // === Single base with dotted variants for entities (e.g., "entity.wolf.angry.spotted") ===
        if (parts.length >= 2 && "entity".equals(parts[0])) {
            //mmh.LOGGER.debug("Entity key with variants detected: " + langKey);

            String baseKey = "entity." + parts[1];
            String baseName = getLang(baseKey, capitalizeWords(parts[1].replace("_", " ")));
            //mmh.LOGGER.debug("Base entity key: '" + baseKey + "' → '" + baseName + "'");

            String capitalizedBase = capitalize(baseName);

            result = result.replace("%name%", baseName)
                    .replace("%Name%", capitalizedBase)
                    .replace("%base%", baseName)
                    .replace("%Base%", capitalizedBase)
                    .replace("%block1%", baseName); // Optional — keep if used in any format

            StringBuilder fullVariant = new StringBuilder();
            String var1 = "", var2 = "", var3 = "";

            for (int i = 2; i < parts.length; i++) {
                String vKey = "variant." + parts[i];
                String v = getLang(vKey, capitalizeWords(parts[i].replace("_", " ")));
                //mmh.LOGGER.debug("Variant part " + (i-1) + ": '" + vKey + "' → '" + v + "'");

                if (i == 2) var1 = v;
                if (i == 3) var2 = v;
                if (i == 4) var3 = v;

                if (fullVariant.length() > 0) fullVariant.append(" ");
                fullVariant.append(v);
            }

            String variantStr = fullVariant.toString();
            String variantCapital = variantStr.isEmpty() ? "" : variantStr + " "; // No trailing space if empty

            result = result.replace("%variant%", variantStr)
                    .replace("%Variant%", variantCapital)
                    .replace("%var1%", var1)
                    .replace("%var2%", var2)
                    .replace("%var3%", var3);

            //mmh.LOGGER.debug("Entity final result: '" + result + "'");
            return cleanWhitespace(result);
        }

        // === Final fallback: treat whole key as base ===
        String fallbackName = getLang(langKey, capitalizeWords(langKey.replace(".", " ").replace("_", " ")));
        //mmh.LOGGER.debug("Final fallback: getLang('" + langKey + "') → '" + fallbackName + "'");

        String capitalized = capitalize(fallbackName);

        result = result.replace("%name%", fallbackName)
                .replace("%Name%", capitalized)
                .replace("%base%", fallbackName)
                .replace("%Base%", capitalized)
                .replace("%block1%", fallbackName);

        //mmh.LOGGER.debug("FINAL display name: '" + result + "' (original langKey: '" + originalLangKey + "')");

        return cleanWhitespace(result);
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) return "";
        String[] words = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }

    private static String getLang(String key, String... defaultValue) {
        return mmh.get(key, defaultValue.length > 0 ? defaultValue[0] : "");
    }

    // Consistent defaults
    public static String getDefaultMobFormat() {
        return "%Variant%%name%";
    }
    public static String getDefaultBlockFormat() {
        return "§eMini %name% %Variant%";
    }

    public static String cleanWhitespace(String input) {
        if (input == null) {
            return null;
        }
        return input.trim().replaceAll("\\s+", " ");
    }
}