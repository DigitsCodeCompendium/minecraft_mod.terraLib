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

    public static void render(GuiGraphics graphics, Content content, HudPanelConfig config) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(config, "config");
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || minecraft.player == null || !config.enabled()) return;

        HudPanelPlacement placement = config.placement();
        double screenX = graphics.guiWidth() * placement.horizontalPercent() / 100.0D;
        double screenY = graphics.guiHeight() * placement.verticalPercent() / 100.0D;
        graphics.pose().pushPose();
        graphics.pose().translate((float) screenX, (float) screenY, 0.0F);
        graphics.pose().scale((float) placement.scale(), (float) placement.scale(), 1.0F);
        graphics.pose().translate((float) (-WIDTH * placement.anchor().horizontal()),
                (float) (-HEIGHT * placement.anchor().vertical()), 0.0F);

        TerraGui.raisedPanel(graphics, 0, 0, WIDTH, HEIGHT, TerraUiTheme.VANILLA, config.opacity());
        renderContents(graphics, content, 0, 0);
        graphics.pose().popPose();
    }

    /** Adapts this HUD's contents for registration in {@link SharedHudPanel}. */
    public static SharedHudPanel.Element sharedElement(Content content) {
        Objects.requireNonNull(content, "content");
        return new SharedHudPanel.Element(WIDTH, HEIGHT,
                (graphics, deltaTracker, x, y) -> renderContents(graphics, content, x, y));
    }

    private static void renderContents(GuiGraphics graphics, Content content, int x, int y) {
        Minecraft minecraft = Minecraft.getInstance();
        TerraGui.insetPanel(graphics, x + 4, y + 2, 36, 35, 0xFF111111, 0xFF8B8B8B, 0xFF2B2B2B);
        renderChart(graphics, content, x, y);
        graphics.drawString(minecraft.font, content.title(), x + CONTENT_X, y + 5,
                TerraUiTheme.VANILLA.text(), true);
        double progress = Math.clamp(content.progress(), 0.0D, 1.0D);
        int barWidth = WIDTH - CONTENT_X - 6;
        TerraGui.progressBar(graphics, x + CONTENT_X, y + 17, barWidth, 8, progress, 10, TerraUiTheme.VANILLA);
        String percentage = String.format(Locale.ROOT, "%.1f%%", progress * 100.0D);
        graphics.drawString(minecraft.font, percentage, x + WIDTH - 6 - minecraft.font.width(percentage), y + 5,
                TerraUiTheme.VANILLA.text(), true);
        graphics.drawString(minecraft.font, content.statusLabel(), x + CONTENT_X, y + 28,
                TerraUiTheme.VANILLA.mutedText(), true);
        graphics.drawString(minecraft.font, content.statusValue(),
                x + WIDTH - 6 - minecraft.font.width(content.statusValue()), y + 28,
                TerraUiTheme.VANILLA.text(), true);
    }

    private static void renderChart(GuiGraphics graphics, Content content, int x, int y) {
        int centerX = x + 22;
        int centerY = y + 19;
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

}
