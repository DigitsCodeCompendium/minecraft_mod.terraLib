package com.digitscodecompendium.terralib.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TerraColorsTest {
    @Test
    void parsesAndFormatsRgb() {
        assertEquals(0x35D4FF, TerraColors.parseRgb("#35D4FF"));
        assertEquals(0x35D4FF, TerraColors.parseRgb("35d4ff"));
        assertEquals("#35D4FF", TerraColors.formatRgb(0xAA35D4FF));
    }

    @Test
    void rejectsMalformedColors() {
        assertThrows(IllegalArgumentException.class, () -> TerraColors.parseRgb("#123"));
        assertThrows(IllegalArgumentException.class, () -> TerraColors.parseRgb("#GGGGGG"));
        assertThrows(IllegalArgumentException.class, () -> TerraColors.parseRgb(null));
    }

    @Test
    void appliesAlpha() {
        assertEquals(0x8035D4FF, TerraColors.withAlpha(0x35D4FF, 0x80));
        assertThrows(IllegalArgumentException.class, () -> TerraColors.withAlpha(0, 256));
    }
}
