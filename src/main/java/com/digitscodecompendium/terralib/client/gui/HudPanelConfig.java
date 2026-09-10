package com.digitscodecompendium.terralib.client.gui;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Objects;

/**
 * The standard client configuration controls for a positionable TerraLib HUD panel.
 *
 * <p>The values are added to a consuming mod's config builder; the consuming mod remains responsible for building
 * and registering that client config spec.</p>
 */
public final class HudPanelConfig {
    public static final Defaults DEFAULTS = new Defaults(true, 1.0D, 98.0D, 98.0D, HudAnchor.BOTTOM_RIGHT);

    private final ModConfigSpec.BooleanValue enabled;
    private final ModConfigSpec.DoubleValue scale;
    private final ModConfigSpec.DoubleValue horizontalPercent;
    private final ModConfigSpec.DoubleValue verticalPercent;
    private final ModConfigSpec.EnumValue<HudAnchor> anchor;

    private HudPanelConfig(ModConfigSpec.BooleanValue enabled, ModConfigSpec.DoubleValue scale,
                           ModConfigSpec.DoubleValue horizontalPercent, ModConfigSpec.DoubleValue verticalPercent,
                           ModConfigSpec.EnumValue<HudAnchor> anchor) {
        this.enabled = enabled;
        this.scale = scale;
        this.horizontalPercent = horizontalPercent;
        this.verticalPercent = verticalPercent;
        this.anchor = anchor;
    }

    /** Defines a config category using the defaults established by TerraSkills' skill-point HUD. */
    public static HudPanelConfig define(ModConfigSpec.Builder builder, String category,
                                        String translationPrefix) {
        return define(builder, category, translationPrefix, DEFAULTS);
    }

    /** Defines a config category with custom default values and the standard ranges. */
    public static HudPanelConfig define(ModConfigSpec.Builder builder, String category, String translationPrefix,
                                        Defaults defaults) {
        Objects.requireNonNull(builder, "builder");
        requireText(category, "category");
        requireText(translationPrefix, "translationPrefix");
        Objects.requireNonNull(defaults, "defaults");

        builder.push(category);
        ModConfigSpec.BooleanValue enabled;
        ModConfigSpec.DoubleValue scale;
        ModConfigSpec.DoubleValue horizontalPercent;
        ModConfigSpec.DoubleValue verticalPercent;
        ModConfigSpec.EnumValue<HudAnchor> anchor;
        try {
            enabled = builder.comment("Show this panel on the HUD.")
                    .translation(translationPrefix + ".hudEnabled")
                    .define("enabled", defaults.enabled());
            scale = builder.comment("HUD scale. 1 is normal size.")
                    .translation(translationPrefix + ".scale")
                    .defineInRange("scale", defaults.scale(), 0.25D, 4.0D);
            horizontalPercent = builder.comment("Horizontal position as a percentage of screen width.")
                    .translation(translationPrefix + ".horizontalPercent")
                    .defineInRange("horizontalPercent", defaults.horizontalPercent(), 0.0D, 100.0D);
            verticalPercent = builder.comment("Vertical position as a percentage of screen height.")
                    .translation(translationPrefix + ".verticalPercent")
                    .defineInRange("verticalPercent", defaults.verticalPercent(), 0.0D, 100.0D);
            anchor = builder.comment("The point on the HUD attached to the configured screen position.")
                    .translation(translationPrefix + ".anchor")
                    .defineEnum("anchor", defaults.anchor());
        } finally {
            builder.pop();
        }
        return new HudPanelConfig(enabled, scale, horizontalPercent, verticalPercent, anchor);
    }

    public boolean enabled() {
        return enabled.get();
    }

    public double scale() {
        return scale.get();
    }

    public double horizontalPercent() {
        return horizontalPercent.get();
    }

    public double verticalPercent() {
        return verticalPercent.get();
    }

    public HudAnchor anchor() {
        return anchor.get();
    }

    /** Takes a current immutable snapshot suitable for a positionable HUD renderer. */
    public HudPanelPlacement placement() {
        return new HudPanelPlacement(scale(), horizontalPercent(), verticalPercent(), anchor());
    }

    private static void requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }

    /** Default values for a panel; the supported ranges remain scale 0.25-4 and position 0-100 percent. */
    public record Defaults(boolean enabled, double scale, double horizontalPercent, double verticalPercent,
                           HudAnchor anchor) {
        public Defaults {
            if (!Double.isFinite(scale) || scale < 0.25D || scale > 4.0D) {
                throw new IllegalArgumentException("Default HUD scale must be between 0.25 and 4");
            }
            if (!Double.isFinite(horizontalPercent) || horizontalPercent < 0.0D || horizontalPercent > 100.0D
                    || !Double.isFinite(verticalPercent) || verticalPercent < 0.0D || verticalPercent > 100.0D) {
                throw new IllegalArgumentException("Default HUD positions must be between 0 and 100 percent");
            }
            Objects.requireNonNull(anchor, "anchor");
        }
    }
}
