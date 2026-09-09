package com.digitscodecompendium.terralib.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.Objects;

/** Stateless, texture-free rendering primitives shared by Terra screens and HUDs. */
public final class TerraGui {
    public static final int SLOT_SIZE = 18;

    private TerraGui() {
    }

    /** Draws the bronze, riveted machine panel used by Terra Industry. */
    public static void machinePanel(GuiGraphics graphics, int x, int y, int width, int height) {
        machinePanel(graphics, x, y, width, height, TerraUiTheme.MACHINE);
    }

    public static void machinePanel(GuiGraphics graphics, int x, int y, int width, int height,
                                    TerraUiTheme theme) {
        requireSize(width, height);
        Objects.requireNonNull(theme, "theme");
        graphics.fill(x, y, x + width, y + height, theme.outline());
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, theme.frameDark());
        graphics.fill(x + 3, y + 3, x + width - 3, y + height - 3, theme.surfaceDark());
        if (height >= 24) {
            graphics.fill(x + 5, y + 5, x + width - 5, y + 22, theme.frameDark());
            graphics.fill(x + 6, y + 6, x + width - 6, y + 7, theme.frameHighlight());
        }
        if (width >= 14 && height >= 14) {
            rivet(graphics, x + 8, y + 8, theme.frameHighlight());
            rivet(graphics, x + width - 11, y + 8, theme.frameHighlight());
            rivet(graphics, x + 8, y + height - 11, theme.frameHighlight());
            rivet(graphics, x + width - 11, y + height - 11, theme.frameHighlight());
        }
    }

    /** Draws a vanilla-widget-style raised panel suitable for compact HUDs. */
    public static void raisedPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        raisedPanel(graphics, x, y, width, height, TerraUiTheme.VANILLA);
    }

    public static void raisedPanel(GuiGraphics graphics, int x, int y, int width, int height,
                                   TerraUiTheme theme) {
        requireSize(width, height);
        Objects.requireNonNull(theme, "theme");
        graphics.fill(x, y, x + width, y + height, theme.outline());
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, theme.frameDark());
        graphics.fill(x + 1, y + 1, x + width - 2, y + 2, theme.frameHighlight());
        graphics.fill(x + 1, y + 1, x + 2, y + height - 2, theme.frameHighlight());
        graphics.fill(x + 2, y + height - 2, x + width - 1, y + height - 1, theme.surface());
        graphics.fill(x + width - 2, y + 2, x + width - 1, y + height - 1, theme.surface());
    }

    public static void recessedPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        recessedPanel(graphics, x, y, width, height, TerraUiTheme.MACHINE);
    }

    public static void recessedPanel(GuiGraphics graphics, int x, int y, int width, int height,
                                     TerraUiTheme theme) {
        requireSize(width, height);
        Objects.requireNonNull(theme, "theme");
        graphics.fill(x, y, x + width, y + height, theme.outline());
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, theme.surfaceDark());
        if (height >= 4) {
            graphics.fill(x + 2, y + 2, x + width - 2, y + 3, darken(theme.surfaceDark(), 0.65F));
        }
    }

    /** Draws a three-layer inset used for dials and other compact HUD instruments. */
    public static void insetPanel(GuiGraphics graphics, int x, int y, int width, int height,
                                  int outline, int rim, int interior) {
        requireSize(width, height);
        graphics.fill(x, y, x + width, y + height, outline);
        if (width <= 2 || height <= 2) return;
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, rim);
        if (width <= 4 || height <= 4) return;
        graphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, interior);
    }

    public static void slot(GuiGraphics graphics, int x, int y) {
        slot(graphics, x, y, TerraUiTheme.MACHINE);
    }

    public static void slot(GuiGraphics graphics, int x, int y, TerraUiTheme theme) {
        Objects.requireNonNull(theme, "theme");
        graphics.fill(x - 1, y - 1, x + SLOT_SIZE + 1, y + SLOT_SIZE + 1, theme.outline());
        graphics.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, theme.surfaceHighlight());
        graphics.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, theme.surfaceDark());
        graphics.fill(x + 2, y + 2, x + SLOT_SIZE - 2, y + 3, darken(theme.surfaceDark(), 0.65F));
    }

    public static void slotGrid(GuiGraphics graphics, int x, int y, int columns, int rows) {
        slotGrid(graphics, x, y, columns, rows, TerraUiTheme.MACHINE);
    }

    public static void slotGrid(GuiGraphics graphics, int x, int y, int columns, int rows,
                                TerraUiTheme theme) {
        if (columns < 0 || rows < 0) {
            throw new IllegalArgumentException("Grid dimensions cannot be negative");
        }
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                slot(graphics, x + column * SLOT_SIZE, y + row * SLOT_SIZE, theme);
            }
        }
    }

    /** Draws an accent-colored plaque around an item or icon without depending on a mod-specific type. */
    public static void accentPlaque(GuiGraphics graphics, int x, int y, int width, int height, int accent,
                                    TerraUiTheme theme) {
        requireSize(width, height);
        Objects.requireNonNull(theme, "theme");
        graphics.fill(x - 5, y - 5, x + width + 5, y + height + 5, theme.outline());
        graphics.fill(x - 4, y - 4, x + width + 4, y + height + 4, accent);
        graphics.fill(x - 2, y - 2, x + width + 2, y + height + 2, theme.surfaceDark());
        graphics.fill(x - 3, y - 3, x + 3, y, accent);
        graphics.fill(x + width - 1, y - 3, x + width + 3, y + 1, accent);
        graphics.fill(x - 3, y + height - 1, x + 1, y + height + 3, accent);
        graphics.fill(x + width - 1, y + height - 1, x + width + 3, y + height + 2, accent);
    }

    public static void accentPlaque(GuiGraphics graphics, int x, int y, int width, int height, int accent) {
        accentPlaque(graphics, x, y, width, height, accent, TerraUiTheme.MACHINE);
    }

    public static void indicator(GuiGraphics graphics, int x, int y, boolean lit) {
        indicator(graphics, x, y, lit, TerraUiTheme.MACHINE);
    }

    public static void indicator(GuiGraphics graphics, int x, int y, boolean lit, TerraUiTheme theme) {
        Objects.requireNonNull(theme, "theme");
        int color = lit ? theme.positive() : theme.negative();
        graphics.fill(x, y, x + 10, y + 10, theme.outline());
        graphics.fill(x + 2, y + 2, x + 8, y + 8, color);
        graphics.fill(x + 3, y + 3, x + 6, y + 4, lighten(color, 0.35F));
    }

    /** Draws a three-tone horizontal progress bar, optionally divided into equal visual segments. */
    public static void progressBar(GuiGraphics graphics, int x, int y, int width, int height,
                                   double progress, int segments, TerraUiTheme theme) {
        requireSize(width, height);
        Objects.requireNonNull(theme, "theme");
        if (segments < 0) throw new IllegalArgumentException("Segment count cannot be negative");
        double boundedProgress = Math.clamp(progress, 0.0D, 1.0D);
        graphics.fill(x, y, x + width, y + height, theme.outline());
        if (width <= 2 || height <= 2) {
            return;
        }
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, theme.surfaceDark());
        int filled = (int) Math.round((width - 2) * boundedProgress);
        if (filled > 0) {
            int topEnd = y + 1 + Math.max(1, (height - 2) / 3);
            int bottomStart = y + height - 2;
            graphics.fill(x + 1, y + 1, x + 1 + filled, topEnd, theme.progressBright());
            graphics.fill(x + 1, topEnd, x + 1 + filled, bottomStart, theme.progress());
            graphics.fill(x + 1, bottomStart, x + 1 + filled, y + height - 1, theme.progressDark());
        }
        for (int segment = 1; segment < segments; segment++) {
            int segmentX = x + 1 + (width - 2) * segment / segments;
            graphics.fill(segmentX, y + 1, segmentX + 1, y + height - 1, 0x80000000);
        }
    }

    public static void progressBar(GuiGraphics graphics, int x, int y, int width, int height,
                                   double progress, int segments) {
        progressBar(graphics, x, y, width, height, progress, segments, TerraUiTheme.VANILLA);
    }

    /** Draws progress clockwise around the one-pixel perimeter of a rectangle. */
    public static void perimeterProgress(GuiGraphics graphics, int x, int y, int width, int height,
                                         double progress, int color) {
        requireSize(width, height);
        int remaining = (int) Math.round((2L * width + 2L * height) * Math.clamp(progress, 0.0D, 1.0D));
        int length = Math.min(width, remaining);
        if (length > 0) graphics.fill(x, y, x + length, y + 1, color);
        remaining -= length;
        length = Math.min(height, Math.max(0, remaining));
        if (length > 0) graphics.fill(x + width - 1, y, x + width, y + length, color);
        remaining -= length;
        length = Math.min(width, Math.max(0, remaining));
        if (length > 0) graphics.fill(x + width - length, y + height - 1, x + width, y + height, color);
        remaining -= length;
        length = Math.min(height, Math.max(0, remaining));
        if (length > 0) graphics.fill(x, y + height - length, x + 1, y + height, color);
    }

    public static void circle(GuiGraphics graphics, int centerX, int centerY, int radius, int color) {
        if (radius < 0) throw new IllegalArgumentException("Radius cannot be negative");
        for (int row = -radius; row <= radius; row++) {
            int halfWidth = (int) Math.floor(Math.sqrt((long) radius * radius - (long) row * row));
            graphics.fill(centerX - halfWidth, centerY + row, centerX + halfWidth + 1, centerY + row + 1, color);
        }
    }

    /** Draws a clockwise pie chart beginning at twelve o'clock. Negative values are treated as zero. */
    public static void pieChart(GuiGraphics graphics, int centerX, int centerY, int radius,
                                double[] values, int[] colors) {
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(colors, "colors");
        if (values.length == 0 || values.length != colors.length) {
            throw new IllegalArgumentException("Values and colors must have the same non-zero length");
        }
        if (radius < 0) throw new IllegalArgumentException("Radius cannot be negative");
        double total = 0.0D;
        for (double value : values) total += Math.max(0.0D, value);
        if (total <= 0.0D) return;

        for (int row = -radius; row <= radius; row++) {
            for (int column = -radius; column <= radius; column++) {
                if ((long) column * column + (long) row * row > (long) radius * radius) continue;
                double angle = (Math.atan2(column, -row) + Math.PI * 2.0D) % (Math.PI * 2.0D);
                double position = angle / (Math.PI * 2.0D) * total;
                double boundary = 0.0D;
                int color = colors[colors.length - 1];
                for (int index = 0; index < values.length; index++) {
                    boundary += Math.max(0.0D, values[index]);
                    if (position <= boundary) {
                        color = colors[index];
                        break;
                    }
                }
                graphics.fill(centerX + column, centerY + row, centerX + column + 1, centerY + row + 1, color);
            }
        }
    }

    public static void badge(GuiGraphics graphics, Font font, Component label, int x, int y,
                             TerraUiTheme theme) {
        Objects.requireNonNull(theme, "theme");
        int width = font.width(label) + 4;
        graphics.fill(x - 2, y - 2, x + width, y + 10, theme.outline());
        graphics.fill(x - 1, y - 1, x + width - 1, y + 9, theme.frameDark());
        graphics.drawString(font, label, x, y, theme.text(), false);
    }

    private static void rivet(GuiGraphics graphics, int x, int y, int color) {
        graphics.fill(x, y, x + 3, y + 3, color);
    }

    private static void requireSize(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }
    }

    private static int lighten(int argb, float amount) {
        int red = channel(argb, 16);
        int green = channel(argb, 8);
        int blue = channel(argb, 0);
        return argb & 0xFF000000
                | Math.round(red + (255 - red) * amount) << 16
                | Math.round(green + (255 - green) * amount) << 8
                | Math.round(blue + (255 - blue) * amount);
    }

    private static int darken(int argb, float factor) {
        return argb & 0xFF000000
                | Math.round(channel(argb, 16) * factor) << 16
                | Math.round(channel(argb, 8) * factor) << 8
                | Math.round(channel(argb, 0) * factor);
    }

    private static int channel(int argb, int shift) {
        return argb >>> shift & 0xFF;
    }
}
