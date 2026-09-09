package com.digitscodecompendium.terralib.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.Locale;
import java.util.Objects;

/** A compact, positionable progress HUD with a proportional pie-chart indicator. */
public final class ProgressChartHud {
    public static final int WIDTH = 194;
    public static final int HEIGHT = 39;
    private static final int CONTENT_X = 42;

    private ProgressChartHud() {
    }

    public static void render(GuiGraphics graphics, Content content, Placement placement) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(placement, "placement");
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || minecraft.player == null) return;

        double screenX = graphics.guiWidth() * placement.horizontalPercent() / 100.0D;
        double screenY = graphics.guiHeight() * placement.verticalPercent() / 100.0D;
        graphics.pose().pushPose();
        graphics.pose().translate((float) screenX, (float) screenY, 0.0F);
        graphics.pose().scale((float) placement.scale(), (float) placement.scale(), 1.0F);
        graphics.pose().translate((float) (-WIDTH * placement.anchor().horizontal()),
                (float) (-HEIGHT * placement.anchor().vertical()), 0.0F);

        TerraGui.raisedPanel(graphics, 0, 0, WIDTH, HEIGHT, TerraUiTheme.VANILLA);
        TerraGui.insetPanel(graphics, 4, 2, 36, 35, 0xFF111111, 0xFF8B8B8B, 0xFF2B2B2B);
        renderChart(graphics, content);
        graphics.drawString(minecraft.font, content.title(), CONTENT_X, 5, TerraUiTheme.VANILLA.text(), true);
        double progress = Math.clamp(content.progress(), 0.0D, 1.0D);
        int barWidth = WIDTH - CONTENT_X - 6;
        TerraGui.progressBar(graphics, CONTENT_X, 17, barWidth, 8, progress, 10, TerraUiTheme.VANILLA);
        String percentage = String.format(Locale.ROOT, "%.1f%%", progress * 100.0D);
        graphics.drawString(minecraft.font, percentage, WIDTH - 6 - minecraft.font.width(percentage), 5,
                TerraUiTheme.VANILLA.text(), true);
        graphics.drawString(minecraft.font, content.statusLabel(), CONTENT_X, 28,
                TerraUiTheme.VANILLA.mutedText(), true);
        graphics.drawString(minecraft.font, content.statusValue(),
                WIDTH - 6 - minecraft.font.width(content.statusValue()), 28, TerraUiTheme.VANILLA.text(), true);
        graphics.pose().popPose();
    }

    private static void renderChart(GuiGraphics graphics, Content content) {
        int centerX = 22;
        int centerY = 19;
        int maximumRadius = 14;
        TerraGui.circle(graphics, centerX, centerY, maximumRadius + 1, 0xFF000000);
        TerraGui.circle(graphics, centerX, centerY, maximumRadius, 0xFF181818);
        if (content.chartMaximum() <= 0.0D || content.chartAmount() <= 0.0D) return;

        double amountRatio = Math.clamp(content.chartAmount() / content.chartMaximum(), 0.0D, 1.0D);
        int radius = Math.max(1, (int) Math.round(maximumRadius * Math.sqrt(amountRatio)));
        TerraGui.pieChart(graphics, centerX, centerY, radius, content.chartValues(), content.chartColors());
    }

    public record Content(Component title, double progress, Component statusLabel, Component statusValue,
                          double chartAmount, double chartMaximum, double[] chartValues, int[] chartColors) {
        public Content {
            Objects.requireNonNull(title, "title");
            Objects.requireNonNull(statusLabel, "statusLabel");
            Objects.requireNonNull(statusValue, "statusValue");
            chartValues = Objects.requireNonNull(chartValues, "chartValues").clone();
            chartColors = Objects.requireNonNull(chartColors, "chartColors").clone();
        }

        @Override
        public double[] chartValues() {
            return chartValues.clone();
        }

        @Override
        public int[] chartColors() {
            return chartColors.clone();
        }
    }

    public record Placement(double scale, double horizontalPercent, double verticalPercent, HudAnchor anchor) {
        public Placement {
            if (!Double.isFinite(scale) || scale <= 0.0D) {
                throw new IllegalArgumentException("Scale must be finite and positive");
            }
            if (!Double.isFinite(horizontalPercent) || !Double.isFinite(verticalPercent)) {
                throw new IllegalArgumentException("HUD position must be finite");
            }
            Objects.requireNonNull(anchor, "anchor");
        }
    }
}
