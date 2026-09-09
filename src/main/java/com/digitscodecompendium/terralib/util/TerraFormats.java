package com.digitscodecompendium.terralib.util;

import java.util.Locale;

/** Locale-stable formatting shared by Terra commands, screens, and HUDs. */
public final class TerraFormats {
    private TerraFormats() {
    }

    public static String scaledNumber(long value, long scale, int decimalPlaces) {
        if (scale == 0) throw new IllegalArgumentException("Scale cannot be zero");
        if (decimalPlaces < 0) throw new IllegalArgumentException("Decimal places cannot be negative");
        return String.format(Locale.ROOT, "%." + decimalPlaces + "f", (double) value / scale);
    }

    /** Formats Minecraft ticks without discarding the remaining ticks. */
    public static String ticks(long totalTicks) {
        long remaining = Math.max(0L, totalTicks);
        long days = remaining / 1_728_000L;
        remaining %= 1_728_000L;
        long hours = remaining / 72_000L;
        remaining %= 72_000L;
        long minutes = remaining / 1_200L;
        remaining %= 1_200L;
        long seconds = remaining / 20L;
        long ticks = remaining % 20L;
        if (days > 0) return String.format(Locale.ROOT, "%dd %dh %dm", days, hours, minutes);
        if (hours > 0) return String.format(Locale.ROOT, "%dh %dm %ds", hours, minutes, seconds);
        if (minutes > 0) return String.format(Locale.ROOT, "%dm %ds %dt", minutes, seconds, ticks);
        if (seconds > 0) return String.format(Locale.ROOT, "%ds %dt", seconds, ticks);
        return ticks + "t";
    }

    /** Turns paths such as {@code terraskills:master_miner} into {@code Master Miner}. */
    public static String humanizeIdentifier(String identifier) {
        if (identifier == null || identifier.isBlank()) return "";
        int separator = identifier.indexOf(':');
        String path = separator >= 0 ? identifier.substring(separator + 1) : identifier;
        String[] words = path.replace('/', '_').split("_+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            if (!result.isEmpty()) result.append(' ');
            result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return result.toString();
    }
}
