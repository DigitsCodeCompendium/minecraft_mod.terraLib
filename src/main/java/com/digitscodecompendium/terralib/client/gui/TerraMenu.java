package com.digitscodecompendium.terralib.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Stateless tabbed-menu and scrolling primitives for TerraLib screens. */
public final class TerraMenu {
    public static final int TAB_HEIGHT = 20;
    public static final int MIN_HORIZONTAL_TAB_WIDTH = 32;
    public static final int MIN_VERTICAL_TAB_WIDTH = 48;
    public static final int SCROLL_BAR_THICKNESS = 10;
    public static final int MIN_THUMB_LENGTH = 8;
    private static final int TAB_TEXT_PADDING = 12;
    private static final int PANEL_PADDING = 4;
    private static final int SCROLL_GAP = 3;

    private TerraMenu() {
    }

    /** Draws a panel with horizontal or vertical tabs attached to any side and returns its interactive bounds. */
    public static TabbedPanelLayout tabbedPanel(GuiGraphics graphics, Font font, int x, int y, int width, int height,
                                                List<? extends Component> tabs, int selectedTab, TabSide side,
                                                TerraUiTheme theme, double opacity) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(font, "font");
        Objects.requireNonNull(tabs, "tabs");
        Objects.requireNonNull(side, "side");
        Objects.requireNonNull(theme, "theme");
        requireSize(width, height);
        if (tabs.isEmpty()) {
            throw new IllegalArgumentException("A tabbed panel must contain at least one tab");
        }
        if (selectedTab < 0 || selectedTab >= tabs.size()) {
            throw new IllegalArgumentException("Selected tab index is outside the tab list");
        }
        tabs.forEach(tab -> Objects.requireNonNull(tab, "tab"));

        TerraGui.raisedPanel(graphics, x, y, width, height, theme, opacity);
        List<TabBounds> bounds = side.vertical()
                ? drawVerticalTabs(graphics, font, x, y, width, tabs, selectedTab, side, theme, opacity)
                : drawHorizontalTabs(graphics, font, x, y, height, tabs, selectedTab, side, theme, opacity);
        Bounds content = new Bounds(x + PANEL_PADDING, y + PANEL_PADDING,
                Math.max(0, width - PANEL_PADDING * 2), Math.max(0, height - PANEL_PADDING * 2));
        return new TabbedPanelLayout(content, bounds, selectedTab, side);
    }

    public static TabbedPanelLayout tabbedPanel(GuiGraphics graphics, Font font, int x, int y, int width, int height,
                                                List<? extends Component> tabs, int selectedTab, TabSide side,
                                                TerraUiTheme theme) {
        return tabbedPanel(graphics, font, x, y, width, height, tabs, selectedTab, side, theme, 1.0D);
    }

    /** Draws a recessed viewport and a scrollbar on its trailing edge. Content clipping remains caller-controlled. */
    public static ScrollPanelLayout scrollPanel(GuiGraphics graphics, int x, int y, int width, int height,
                                                int contentLength, int scrollOffset, ScrollAxis axis,
                                                TerraUiTheme theme, double opacity) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(axis, "axis");
        Objects.requireNonNull(theme, "theme");
        requireSize(width, height);
        TerraGui.recessedPanel(graphics, x, y, width, height, theme, opacity);

        Bounds viewport;
        ScrollBarLayout scrollBar;
        if (axis == ScrollAxis.VERTICAL) {
            viewport = new Bounds(x + PANEL_PADDING, y + PANEL_PADDING,
                    Math.max(0, width - PANEL_PADDING * 2 - SCROLL_BAR_THICKNESS - SCROLL_GAP),
                    Math.max(0, height - PANEL_PADDING * 2));
            scrollBar = scrollBar(graphics, x + width - PANEL_PADDING - SCROLL_BAR_THICKNESS, y + PANEL_PADDING,
                    SCROLL_BAR_THICKNESS, Math.max(1, height - PANEL_PADDING * 2), axis,
                    contentLength, viewport.height(), scrollOffset, theme, opacity);
        } else {
            viewport = new Bounds(x + PANEL_PADDING, y + PANEL_PADDING,
                    Math.max(0, width - PANEL_PADDING * 2),
                    Math.max(0, height - PANEL_PADDING * 2 - SCROLL_BAR_THICKNESS - SCROLL_GAP));
            scrollBar = scrollBar(graphics, x + PANEL_PADDING,
                    y + height - PANEL_PADDING - SCROLL_BAR_THICKNESS,
                    Math.max(1, width - PANEL_PADDING * 2), SCROLL_BAR_THICKNESS, axis,
                    contentLength, viewport.width(), scrollOffset, theme, opacity);
        }
        return new ScrollPanelLayout(viewport, scrollBar);
    }

    public static ScrollPanelLayout scrollPanel(GuiGraphics graphics, int x, int y, int width, int height,
                                                int contentLength, int scrollOffset, ScrollAxis axis,
                                                TerraUiTheme theme) {
        return scrollPanel(graphics, x, y, width, height, contentLength, scrollOffset, axis, theme, 1.0D);
    }

    /** Draws a standalone scrollbar and returns the clamped offset and track/thumb bounds used to draw it. */
    public static ScrollBarLayout scrollBar(GuiGraphics graphics, int x, int y, int width, int height,
                                            ScrollAxis axis, int contentLength, int viewportLength, int scrollOffset,
                                            TerraUiTheme theme, double opacity) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(axis, "axis");
        Objects.requireNonNull(theme, "theme");
        requireSize(width, height);
        if (contentLength < 0 || viewportLength < 0) {
            throw new IllegalArgumentException("Scroll content and viewport lengths cannot be negative");
        }

        int maximumOffset = Math.max(0, contentLength - viewportLength);
        int boundedOffset = Math.clamp(scrollOffset, 0, maximumOffset);
        int trackLength = axis == ScrollAxis.VERTICAL ? height : width;
        int thumbLength = maximumOffset == 0 || contentLength == 0
                ? trackLength
                : Math.clamp((int) Math.round((double) trackLength * viewportLength / contentLength),
                        Math.min(MIN_THUMB_LENGTH, trackLength), trackLength);
        int thumbTravel = trackLength - thumbLength;
        int thumbPosition = maximumOffset == 0 ? 0
                : (int) Math.round((double) thumbTravel * boundedOffset / maximumOffset);

        Bounds track = new Bounds(x, y, width, height);
        Bounds thumb = axis == ScrollAxis.VERTICAL
                ? new Bounds(x, y + thumbPosition, width, thumbLength)
                : new Bounds(x + thumbPosition, y, thumbLength, height);
        TerraGui.recessedPanel(graphics, track.x(), track.y(), track.width(), track.height(), theme, opacity);
        TerraGui.raisedPanel(graphics, thumb.x(), thumb.y(), thumb.width(), thumb.height(), theme, opacity);
        return new ScrollBarLayout(track, thumb, axis, boundedOffset, maximumOffset);
    }

    private static List<TabBounds> drawVerticalTabs(GuiGraphics graphics, Font font, int panelX, int panelY,
                                                    int panelWidth, List<? extends Component> tabs, int selectedTab,
                                                    TabSide side, TerraUiTheme theme, double opacity) {
        int tabWidth = Math.max(MIN_VERTICAL_TAB_WIDTH,
                tabs.stream().mapToInt(font::width).max().orElse(0) + TAB_TEXT_PADDING);
        int tabX = side == TabSide.LEFT ? panelX - tabWidth + 1 : panelX + panelWidth - 1;
        List<TabBounds> bounds = new ArrayList<>(tabs.size());
        for (int index = 0; index < tabs.size(); index++) {
            int tabY = panelY + index * TAB_HEIGHT;
            Bounds tab = new Bounds(tabX, tabY, tabWidth, TAB_HEIGHT);
            drawTab(graphics, font, tabs.get(index), tab, index == selectedTab, theme, opacity);
            bounds.add(new TabBounds(index, tab));
        }
        return List.copyOf(bounds);
    }

    private static List<TabBounds> drawHorizontalTabs(GuiGraphics graphics, Font font, int panelX, int panelY,
                                                      int panelHeight, List<? extends Component> tabs, int selectedTab,
                                                      TabSide side, TerraUiTheme theme, double opacity) {
        int tabX = panelX;
        int tabY = side == TabSide.TOP ? panelY - TAB_HEIGHT + 1 : panelY + panelHeight - 1;
        List<TabBounds> bounds = new ArrayList<>(tabs.size());
        for (int index = 0; index < tabs.size(); index++) {
            int tabWidth = Math.max(MIN_HORIZONTAL_TAB_WIDTH, font.width(tabs.get(index)) + TAB_TEXT_PADDING);
            Bounds tab = new Bounds(tabX, tabY, tabWidth, TAB_HEIGHT);
            drawTab(graphics, font, tabs.get(index), tab, index == selectedTab, theme, opacity);
            bounds.add(new TabBounds(index, tab));
            tabX += tabWidth - 1;
        }
        return List.copyOf(bounds);
    }

    private static void drawTab(GuiGraphics graphics, Font font, Component label, Bounds bounds, boolean selected,
                                TerraUiTheme theme, double opacity) {
        TerraGui.raisedPanel(graphics, bounds.x(), bounds.y(), bounds.width(), bounds.height(), theme, opacity);
        int background = selected ? theme.surface() : theme.surfaceDark();
        graphics.fill(bounds.x() + 2, bounds.y() + 2, bounds.right() - 2, bounds.bottom() - 2,
                withOpacity(background, opacity));
        int textX = bounds.x() + (bounds.width() - font.width(label)) / 2;
        int textY = bounds.y() + (bounds.height() - font.lineHeight) / 2;
        graphics.drawString(font, label, textX, textY, selected ? theme.text() : theme.mutedText(), false);
    }

    private static int withOpacity(int argb, double opacity) {
        if (!Double.isFinite(opacity) || opacity < 0.0D || opacity > 1.0D) {
            throw new IllegalArgumentException("Opacity must be between 0 and 1");
        }
        return argb & 0x00FFFFFF | (int) Math.round((argb >>> 24) * opacity) << 24;
    }

    private static void requireSize(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }
    }

    public enum TabSide {
        TOP(false),
        BOTTOM(false),
        LEFT(true),
        RIGHT(true);

        private final boolean vertical;

        TabSide(boolean vertical) {
            this.vertical = vertical;
        }

        public boolean vertical() {
            return vertical;
        }
    }

    public enum ScrollAxis {
        VERTICAL,
        HORIZONTAL
    }

    public record Bounds(int x, int y, int width, int height) {
        public Bounds {
            if (width < 0 || height < 0) {
                throw new IllegalArgumentException("Bounds dimensions cannot be negative");
            }
        }

        public int right() {
            return x + width;
        }

        public int bottom() {
            return y + height;
        }

        public boolean contains(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < right() && mouseY >= y && mouseY < bottom();
        }
    }

    public record TabBounds(int index, Bounds bounds) {
    }

    public record TabbedPanelLayout(Bounds content, List<TabBounds> tabs, int selectedTab, TabSide side) {
        public TabbedPanelLayout {
            tabs = List.copyOf(tabs);
        }

        public int tabAt(double mouseX, double mouseY) {
            return tabs.stream()
                    .filter(tab -> tab.bounds().contains(mouseX, mouseY))
                    .mapToInt(TabBounds::index)
                    .findFirst()
                    .orElse(-1);
        }
    }

    public record ScrollPanelLayout(Bounds viewport, ScrollBarLayout scrollBar) {
    }

    public record ScrollBarLayout(Bounds track, Bounds thumb, ScrollAxis axis, int offset, int maximumOffset) {
        public boolean scrollable() {
            return maximumOffset > 0;
        }
    }
}
