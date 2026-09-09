package com.digitscodecompendium.terralib.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TerraFormatsTest {
    @Test
    void formatsScaledValuesUsingRootLocale() {
        assertEquals("12.30", TerraFormats.scaledNumber(1230, 100, 2));
        assertThrows(IllegalArgumentException.class, () -> TerraFormats.scaledNumber(1, 0, 1));
    }

    @Test
    void formatsTickDurations() {
        assertEquals("7t", TerraFormats.ticks(7));
        assertEquals("2s 3t", TerraFormats.ticks(43));
        assertEquals("1m 2s 3t", TerraFormats.ticks(1243));
        assertEquals("0t", TerraFormats.ticks(-20));
    }

    @Test
    void humanizesNamespacedPaths() {
        assertEquals("Master Miner", TerraFormats.humanizeIdentifier("terraskills:master_miner"));
        assertEquals("Machines Iron Refinery", TerraFormats.humanizeIdentifier("terraindustry:machines/iron_refinery"));
        assertEquals("", TerraFormats.humanizeIdentifier(""));
    }
}
