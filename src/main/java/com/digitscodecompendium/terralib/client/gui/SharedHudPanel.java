package com.digitscodecompendium.terralib.client.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * A bottom-right HUD panel to which multiple mods can contribute content.
 *
 * <p>Contributions are registered once during client initialization. Their suppliers are queried every frame and may
 * return {@code null} while their content is hidden. Top-preferring elements pack downward and bottom-preferring
 * elements pack upward. Higher-priority elements are placed closest to their preferred end of the stack, with
 * resource-location IDs providing a deterministic tie-breaker.</p>
 */
public final class SharedHudPanel {
    private static final int DEFAULT_PRIORITY = 0;
    private static final int MARGIN = 4;
    private static final int PADDING = 4;
    private static final int GAP = 3;
    private static final ConcurrentHashMap<ResourceLocation, Contribution> CONTRIBUTIONS = new ConcurrentHashMap<>();

    private SharedHudPanel() {
    }

    /** Registers bottom-preferring content at the default priority. IDs must be unique across contributing mods. */
    public static void register(ResourceLocation id, Supplier<? extends Element> elementSupplier) {
        register(id, PreferredEnd.BOTTOM, DEFAULT_PRIORITY, elementSupplier);
    }

    /**
     * Registers bottom-preferring content at the given priority. Higher priorities render nearer the stack's bottom.
     * The supplier may return {@code null} to hide the element for the current frame.
     */
    public static void register(ResourceLocation id, int priority, Supplier<? extends Element> elementSupplier) {
        register(id, PreferredEnd.BOTTOM, priority, elementSupplier);
    }

    /**
     * Registers content with a preferred end of the stack and priority. Higher-priority elements are placed closer to
     * their preferred end. The supplier may return {@code null} to hide the element for the current frame.
     */
    public static void register(ResourceLocation id, PreferredEnd preferredEnd, int priority,
                                Supplier<? extends Element> elementSupplier) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(preferredEnd, "preferredEnd");
        Objects.requireNonNull(elementSupplier, "elementSupplier");
        Contribution previous = CONTRIBUTIONS.putIfAbsent(id,
                new Contribution(id, preferredEnd, priority, elementSupplier));
        if (previous != null) {
            throw new IllegalStateException("A shared HUD contribution is already registered for " + id);
        }
    }

    /** Removes a contribution, primarily for clients that support dynamic module unloading. */
    public static boolean unregister(ResourceLocation id) {
        return CONTRIBUTIONS.remove(Objects.requireNonNull(id, "id")) != null;
    }

    /** Draws all currently visible contributions in one panel at the bottom-right of the screen. */
    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Objects.requireNonNull(graphics, "graphics");
        Objects.requireNonNull(deltaTracker, "deltaTracker");
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui || minecraft.player == null) {
            return;
        }

        List<VisibleElement> visible = visibleElements();
        if (visible.isEmpty()) {
            return;
        }

        int contentWidth = visible.stream().mapToInt(item -> item.element().width()).max().orElseThrow();
        int contentHeight = visible.stream().mapToInt(item -> item.element().height()).sum()
                + GAP * (visible.size() - 1);
        int panelWidth = contentWidth + PADDING * 2;
        int panelHeight = contentHeight + PADDING * 2;
        int panelX = graphics.guiWidth() - panelWidth - MARGIN;
        int panelY = graphics.guiHeight() - panelHeight - MARGIN;

        TerraGui.raisedPanel(graphics, panelX, panelY, panelWidth, panelHeight, TerraUiTheme.VANILLA);
        int y = panelY + PADDING;
        for (VisibleElement item : visible) {
            Element element = item.element();
            int x = panelX + panelWidth - PADDING - element.width();
            element.renderer().render(graphics, deltaTracker, x, y);
            y += element.height() + GAP;
        }
    }

    private static List<VisibleElement> visibleElements() {
        List<Contribution> ordered = new ArrayList<>(CONTRIBUTIONS.values());
        ordered.sort(Comparator.comparing(Contribution::preferredEnd)
                .thenComparing(SharedHudPanel::comparePriority)
                .thenComparing(contribution -> contribution.id().toString()));

        List<VisibleElement> visible = new ArrayList<>(ordered.size());
        for (Contribution contribution : ordered) {
            Element element = contribution.elementSupplier().get();
            if (element != null) {
                visible.add(new VisibleElement(element));
            }
        }
        return visible;
    }

    private static int comparePriority(Contribution left, Contribution right) {
        int ascending = Integer.compare(left.priority(), right.priority());
        return left.preferredEnd() == PreferredEnd.TOP ? -ascending : ascending;
    }

    /** A measured piece of panel content. The renderer receives the element's top-left GUI coordinate. */
    public record Element(int width, int height, Renderer renderer) {
        public Element {
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("Shared HUD element dimensions must be positive");
            }
            Objects.requireNonNull(renderer, "renderer");
        }
    }

    @FunctionalInterface
    public interface Renderer {
        void render(GuiGraphics graphics, DeltaTracker deltaTracker, int x, int y);
    }

    /** The end of the shared vertical stack near which an element prefers to appear. */
    public enum PreferredEnd {
        TOP,
        BOTTOM
    }

    private record Contribution(ResourceLocation id, PreferredEnd preferredEnd, int priority,
                                Supplier<? extends Element> elementSupplier) {
    }

    private record VisibleElement(Element element) {
    }
}
