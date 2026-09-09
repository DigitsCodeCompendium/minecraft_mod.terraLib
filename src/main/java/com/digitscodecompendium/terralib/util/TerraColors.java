package com.digitscodecompendium.terralib.util;

import java.util.Locale;

/** Parsing and formatting helpers for RGB values exposed by Terra configuration UIs. */
public final class TerraColors {
    private TerraColors() {
    }

    public static int parseRgb(String value) {
        if (value == null) throw new IllegalArgumentException("Color cannot be null");
        String normalized = value.startsWith("#") ? value.substring(1) : value;
        if (normalized.length() != 6) {
            throw new IllegalArgumentException("Expected a six-digit hexadecimal color");
        }
        try {
            return Integer.parseInt(normalized, 16);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Expected a six-digit hexadecimal color", exception);
        }
    }

    public static String formatRgb(int rgb) {
        return String.format(Locale.ROOT, "#%06X", rgb & 0xFFFFFF);
    }

    public static int withAlpha(int rgb, int alpha) {
        if (alpha < 0 || alpha > 255) throw new IllegalArgumentException("Alpha must be between 0 and 255");
        return alpha << 24 | rgb & 0xFFFFFF;
    }
}
