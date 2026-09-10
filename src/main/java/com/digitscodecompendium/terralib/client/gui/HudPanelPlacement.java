package com.digitscodecompendium.terralib.client.gui;

import java.util.Objects;

/** A scale, screen position, and anchor shared by positionable TerraLib HUD panels. */
public record HudPanelPlacement(double scale, double horizontalPercent, double verticalPercent, HudAnchor anchor) {
    public HudPanelPlacement {
        if (!Double.isFinite(scale) || scale <= 0.0D) {
            throw new IllegalArgumentException("Scale must be finite and positive");
        }
        if (!Double.isFinite(horizontalPercent) || !Double.isFinite(verticalPercent)) {
            throw new IllegalArgumentException("HUD position must be finite");
        }
        Objects.requireNonNull(anchor, "anchor");
    }
}
