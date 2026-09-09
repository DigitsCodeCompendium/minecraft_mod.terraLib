package com.digitscodecompendium.terralib.client.gui;

/**
 * Colors used by the procedural Terra UI primitives. All values are ARGB.
 * Mods can define their own theme or start with one of the built-in themes.
 */
public record TerraUiTheme(
        int outline,
        int frameDark,
        int frameHighlight,
        int surfaceDark,
        int surface,
        int surfaceHighlight,
        int text,
        int mutedText,
        int positive,
        int negative,
        int progressBright,
        int progress,
        int progressDark
) {
    public static final TerraUiTheme MACHINE = new TerraUiTheme(
            0xFF17130F, 0xFF4A3420, 0xFF9B7542,
            0xFF1A1C1D, 0xFF464A4C, 0xFF73787B,
            0xFFF1E0BE, 0xFFC9AA77, 0xFF63C944, 0xFF803232,
            0xFFD39A4B, 0xFFB87931, 0xFF4A3420
    );

    public static final TerraUiTheme VANILLA = new TerraUiTheme(
            0xFF000000, 0xFF373737, 0xFFFFFFFF,
            0xFF202020, 0xFF555555, 0xFF8B8B8B,
            0xFFFFFFFF, 0xFFA0A0A0, 0xFF55AA00, 0xFFAA3333,
            0xFF80FF20, 0xFF55AA00, 0xFF285500
    );
}
