package com.github.joelgodofwar.mmh.util;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftUUIDConverter {

    private static final Pattern INT_ARRAY_PATTERN = Pattern.compile(
            "\\[I;(-?\\d+),(-?\\d+),(-?\\d+),(-?\\d+)]");

    /**
     * Universal parser - accepts BOTH formats:
     *   - "36319a34-ffa8-4a59-b676-309311c15f86"
     *   - "[I;909220404,-5748135,-1233768301,297885574]"
     * <p>
     * Always returns a clean java.util.UUID (or null if input is invalid/empty)
     */
    public static UUID parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        String trimmed = input.trim();

        // Detect Minecraft 1.16+ int array format
        if (trimmed.startsWith("[I;") && trimmed.endsWith("]")) {
            return parseIntArray(trimmed);
        }

        // Assume standard UUID string format
        try {
            return UUID.fromString(trimmed);
        } catch (IllegalArgumentException e) {
            return null; // invalid UUID
        }
    }

    /**
     * Always returns standard UUID string: "36319a34-ffa8-4a59-b676-309311c15f86"
     */
    public static String parseToString(String input) {
        UUID uuid = parse(input);
        return uuid != null ? uuid.toString() : null;
    }

    /**
     * Always returns Minecraft 1.16+ int array format:
     * "[I;909220404,-5748135,-1233768301,297885574]"
     */
    public static String parseToIntArrayString(String input) {
        UUID uuid = parse(input);
        return uuid != null ? toIntArrayString(uuid) : null;
    }

    /**
     * Convert UUID → Minecraft int array string
     */
    public static String toIntArrayString(UUID uuid) {
        if (uuid == null) return null;
        int[] arr = toIntArray(uuid);
        return "[I;" + arr[0] + "," + arr[1] + "," + arr[2] + "," + arr[3] + "]";
    }

    /**
     * Convert UUID → int array (for manual NBT use)
     */
    public static int[] toIntArray(UUID uuid) {
        if (uuid == null) return null;
        long most = uuid.getMostSignificantBits();
        long least = uuid.getLeastSignificantBits();

        return new int[]{
                (int) (most >>> 32),
                (int) most,
                (int) (least >>> 32),
                (int) least
        };
    }

    // ====================== INTERNAL HELPERS ======================

    private static UUID parseIntArray(String str) {
        Matcher matcher = INT_ARRAY_PATTERN.matcher(str);
        if (!matcher.matches()) {
            return null;
        }

        try {
            int[] arr = new int[4];
            for (int i = 0; i < 4; i++) {
                arr[i] = Integer.parseInt(matcher.group(i + 1));
            }

            long most = ((long) arr[0] << 32) | (arr[1] & 0xFFFFFFFFL);
            long least = ((long) arr[2] << 32) | (arr[3] & 0xFFFFFFFFL);

            return new UUID(most, least);
        } catch (Exception e) {
            return null;
        }
    }
}
