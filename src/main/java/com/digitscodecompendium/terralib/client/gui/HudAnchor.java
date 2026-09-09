package com.digitscodecompendium.terralib.client.gui;

/** A point on a HUD element that is attached to a configured screen position. */
public enum HudAnchor {
    TOP_LEFT(0.0D, 0.0D), TOP(0.5D, 0.0D), TOP_RIGHT(1.0D, 0.0D),
    LEFT(0.0D, 0.5D), CENTER(0.5D, 0.5D), RIGHT(1.0D, 0.5D),
    BOTTOM_LEFT(0.0D, 1.0D), BOTTOM(0.5D, 1.0D), BOTTOM_RIGHT(1.0D, 1.0D);

    private final double horizontal;
    private final double vertical;

    HudAnchor(double horizontal, double vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public double horizontal() {
        return horizontal;
    }

    public double vertical() {
        return vertical;
    }
}
