package com.digitscodecompendium.terralib.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

/** Stateless, texture-free rendering primitives shared by Terra screens and HUDs. */
public final class TerraGui {
    public static final int SLOT_SIZE = 18;
    public static final int PIP_SIZE = 9;

    private TerraGui() {
    }

    /** Draws the bronze, riveted machine panel used by Terra Industry. */
    public static void machinePanel(GuiGraphics graphics, int x, int y, int width, int height) {
        machinePanel(graphics, x, y, width, height, TerraUiTheme.MACHINE);
    }

    public static void machinePanel(GuiGraphics graphics, int x, int y, int width, int height,
                                    TerraUiTheme theme) {
        machinePanel(graphics, x, y, width, height, theme, 1.0D);
    }

    public static void machinePanel(GuiGraphics graphics, int x, int y, int width, int height,
                                    TerraUiTheme theme, double opacity) {
        requireSize(width, height);
        Objects.requireNonNull(theme, "theme");
        graphics.fill(x, y, x + width, y + height, withOpacity(theme.outline(), opacity));
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, withOpacity(theme.frameDark(), opacity));
        graphics.fill(x + 3, y + 3, x + width - 3, y + height - 3, withOpacity(theme.surfaceDark(), opacity));
        if (height >= 24) {
            graphics.fill(x + 5, y + 5, x + width - 5, y + 22, withOpacity(theme.frameDark(), opacity));
            graphics.fill(x + 6, y + 6, x + width - 6, y + 7, withOpacity(theme.frameHighlight(), opacity));
        }
        if (width >= 14 && height >= 14) {
            int rivetColor = withOpacity(theme.frameHighlight(), opacity);
            rivet(graphics, x + 8, y + 8, rivetColor);
            rivet(graphics, x + width - 11, y + 8, rivetColor);
            rivet(graphics, x + 8, y + height - 11, rivetColor);
            rivet(graphics, x + width - 11, y + height - 11, rivetColor);
        }
    }

    /** Draws a vanilla-widget-style raised panel suitable for compact HUDs. */
    public static void raisedPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        raisedPanel(graphics, x, y, width, height, TerraUiTheme.VANILLA);
    }

    public static void raisedPanel(GuiGraphics graphics, int x, int y, int width, int height,
                                   TerraUiTheme theme) {
        raisedPanel(graphics, x, y, width, height, theme, 1.0D);
    }

    public static void raisedPanel(GuiGraphics graphics, int x, int y, int width, int height,
                                   TerraUiTheme theme, double opacity) {
        requireSize(width, height);
        Objects.requireNonNull(theme, "theme");
        graphics.fill(x, y, x + width, y + height, withOpacity(theme.outline(), opacity));
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, withOpacity(theme.frameDark(), opacity));
        graphics.fill(x + 1, y + 1, x + width - 2, y + 2, withOpacity(theme.frameHighlight(), opacity));
        graphics.fill(x + 1, y + 1, x + 2, y + height - 2, withOpacity(theme.frameHighlight(), opacity));
        graphics.fill(x + 2, y + height - 2, x + width - 1, y + height - 1, withOpacity(theme.surface(), opacity));
        graphics.fill(x + width - 2, y + 2, x + width - 1, y + height - 1, withOpacity(theme.surface(), opacity));
    }

    public static void recessedPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        recessedPanel(graphics, x, y, width, height, TerraUiTheme.MACHINE);
    }

    public static void recessedPanel(GuiGraphics graphics, int x, int y, int width, int height,
                                     TerraUiTheme theme) {
        recessedPanel(graphics, x, y, width, height, theme, 1.0D);
    }

    public static void recessedPanel(GuiGraphics graphics, int x, int y, int width, int height,
                                     TerraUiTheme theme, double opacity) {
        requireSize(width, height);
        Objects.requireNonNull(theme, "theme");
        graphics.fill(x, y, x + width, y + height, withOpacity(theme.outline(), opacity));
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, withOpacity(theme.surfaceDark(), opacity));
        if (height >= 4) {
            graphics.fill(x + 2, y + 2, x + width - 2, y + 3,
                    withOpacity(darken(theme.surfaceDark(), 0.65F), opacity));
        }
    }

    /** Draws a three-layer inset used for dials and other compact HUD instruments. */
    public static void insetPanel(GuiGraphics graphics, int x, int y, int width, int height,
                                  int outline, int rim, int interior) {
        insetPanel(graphics, x, y, width, height, outline, rim, interior, 1.0D);
    }

    public static void insetPanel(GuiGraphics graphics, int x, int y, int width, int height,
                                  int outline, int rim, int interior, double opacity) {
        requireSize(width, height);
        graphics.fill(x, y, x + width, y + height, withOpacity(outline, opacity));
        if (width <= 2 || height <= 2) return;
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, withOpacity(rim, opacity));
        if (width <= 4 || height <= 4) return;
        graphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, withOpacity(interior, opacity));
    }

    /** Draws a recessed display panel with a centered GUI sprite. */
    public static void imagePanel(GuiGraphics graphics, int x, int y, int width, int height,
                                  ResourceLocation sprite, int imageWidth, int imageHeight,
                                  TerraUiTheme theme, double opacity) {
        requireSize(width, height);
        requireSize(imageWidth, imageHeight);
        Objects.requireNonNull(sprite, "sprite");
        recessedPanel(graphics, x, y, width, height, theme, opacity);
        graphics.blitSprite(sprite, x + (width - imageWidth) / 2, y + (height - imageHeight) / 2,
                imageWidth, imageHeight);
    }

    public static void imagePanel(GuiGraphics graphics, int x, int y, int width, int height,
                                  ResourceLocation sprite, int imageWidth, int imageHeight) {
        imagePanel(graphics, x, y, width, height, sprite, imageWidth, imageHeight,
                TerraUiTheme.VANILLA, 1.0D);
    }

    /** Draws a recessed display panel with a centered item stack. Blocks are rendered through their item form. */
    public static void itemPanel(GuiGraphics graphics, Font font, int x, int y, int width, int height,
                                 ItemStack stack, boolean decorations, TerraUiTheme theme, double opacity) {
        requireSize(width, height);
        Objects.requireNonNull(font, "font");
        Objects.requireNonNull(stack, "stack");
        recessedPanel(graphics, x, y, width, height, theme, opacity);
        int itemX = x + (width - 16) / 2;
        int itemY = y + (height - 16) / 2;
        graphics.renderItem(stack, itemX, itemY);
        if (decorations) {
            graphics.renderItemDecorations(font, stack, itemX, itemY);
        }
    }

    public static void itemPanel(GuiGraphics graphics, Font font, int x, int y, int width, int height,
                                 ItemStack stack) {
        itemPanel(graphics, font, x, y, width, height, stack, false, TerraUiTheme.VANILLA, 1.0D);
    }

    public static void itemPanel(GuiGraphics graphics, Font font, int x, int y, int width, int height,
                                 ItemLike item) {
        itemPanel(graphics, font, x, y, width, height,
                new ItemStack(Objects.requireNonNull(item, "item")));
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

    /** Draws a compact on/off status pip using the theme's positive and negative colors. */
    public static void booleanPip(GuiGraphics graphics, int x, int y, boolean on, TerraUiTheme theme) {
        Objects.requireNonNull(theme, "theme");
        colorPip(graphics, x, y, on ? theme.positive() : theme.negative(), theme);
    }

    public static void booleanPip(GuiGraphics graphics, int x, int y, boolean on) {
        booleanPip(graphics, x, y, on, TerraUiTheme.VANILLA);
    }

    /** Draws a compact status pip with an arbitrary ARGB center color. */
    public static void colorPip(GuiGraphics graphics, int x, int y, int color, TerraUiTheme theme) {
        Objects.requireNonNull(theme, "theme");
        circle(graphics, x + PIP_SIZE / 2, y + PIP_SIZE / 2, PIP_SIZE / 2, theme.outline());
        circle(graphics, x + PIP_SIZE / 2, y + PIP_SIZE / 2, PIP_SIZE / 2 - 2, color);
        graphics.fill(x + 3, y + 2, x + 5, y + 3, lighten(color, 0.35F));
    }

    public static void colorPip(GuiGraphics graphics, int x, int y, int color) {
        colorPip(graphics, x, y, color, TerraUiTheme.VANILLA);
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

    private static int withOpacity(int argb, double opacity) {
        if (!Double.isFinite(opacity) || opacity < 0.0D || opacity > 1.0D) {
            throw new IllegalArgumentException("Opacity must be between 0 and 1");
        }
        int alpha = (int) Math.round((argb >>> 24) * opacity);
        return argb & 0x00FFFFFF | alpha << 24;
    }
}
