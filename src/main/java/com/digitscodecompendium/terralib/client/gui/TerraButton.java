package com.digitscodecompendium.terralib.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.Objects;

/** A themed button that can display text, a GUI sprite, an item or block, or text alongside a graphic. */
public final class TerraButton extends AbstractButton {
    public static final int DEFAULT_WIDTH = 150;
    public static final int DEFAULT_HEIGHT = 20;
    public static final int ICON_BUTTON_SIZE = 20;
    private static final int CONTENT_PADDING = 4;
    private static final int CONTENT_GAP = 4;
    private static final int ITEM_SIZE = 16;

    private final OnPress onPress;
    private final TerraUiTheme theme;
    private final double opacity;
    private final boolean showText;
    private final boolean renderItemDecorations;
    @Nullable
    private final ResourceLocation sprite;
    private final int spriteWidth;
    private final int spriteHeight;
    private final ItemStack itemStack;

    private TerraButton(Builder builder) {
        super(builder.x, builder.y, builder.width, builder.height, builder.message);
        this.onPress = builder.onPress;
        this.theme = builder.theme;
        this.opacity = builder.opacity;
        this.showText = builder.showText;
        this.renderItemDecorations = builder.renderItemDecorations;
        this.sprite = builder.sprite;
        this.spriteWidth = builder.spriteWidth;
        this.spriteHeight = builder.spriteHeight;
        this.itemStack = builder.itemStack.copy();
        setTooltip(builder.tooltip);
    }

    public static Builder text(Component text, OnPress onPress) {
        return new Builder(text, onPress);
    }

    /** Creates an icon-only button. The label is still used for narration. */
    public static Builder icon(Component label, ResourceLocation sprite, int spriteWidth, int spriteHeight,
                               OnPress onPress) {
        return new Builder(label, onPress).icon(sprite, spriteWidth, spriteHeight)
                .showText(false).size(ICON_BUTTON_SIZE, ICON_BUTTON_SIZE);
    }

    /** Creates an item- or block-only button using its hover name for narration. */
    public static Builder item(ItemLike item, OnPress onPress) {
        ItemStack stack = new ItemStack(Objects.requireNonNull(item, "item"));
        return item(stack.getHoverName(), stack, onPress).showText(false);
    }

    /** Creates an item-only button with an explicit narration label. */
    public static Builder item(Component label, ItemStack stack, OnPress onPress) {
        return new Builder(label, onPress).item(stack).showText(false).size(ICON_BUTTON_SIZE, ICON_BUTTON_SIZE);
    }

    @Override
    public void onPress() {
        onPress.onPress(this);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        double backgroundOpacity = opacity * alpha * (active ? 1.0D : 0.55D);
        if (active) {
            TerraGui.raisedPanel(graphics, getX(), getY(), getWidth(), getHeight(), theme, backgroundOpacity);
        } else {
            TerraGui.recessedPanel(graphics, getX(), getY(), getWidth(), getHeight(), theme, backgroundOpacity);
        }
        if (isHoveredOrFocused() && active && getWidth() > 4 && getHeight() > 4) {
            graphics.fill(getX() + 2, getY() + 2, getX() + getWidth() - 2, getY() + getHeight() - 2,
                    0x20FFFFFF);
        }

        int graphicWidth = sprite != null ? spriteWidth : itemStack.isEmpty() ? 0 : ITEM_SIZE;
        int graphicHeight = sprite != null ? spriteHeight : itemStack.isEmpty() ? 0 : ITEM_SIZE;
        int textWidth = showText ? minecraft.font.width(getMessage()) : 0;
        int gap = graphicWidth > 0 && showText ? CONTENT_GAP : 0;
        int availableWidth = Math.max(0, getWidth() - CONTENT_PADDING * 2);
        int totalWidth = Math.min(availableWidth, graphicWidth + gap + textWidth);
        int contentX = getX() + (getWidth() - totalWidth) / 2;

        if (graphicWidth > 0) {
            int graphicY = getY() + (getHeight() - graphicHeight) / 2;
            renderGraphic(graphics, minecraft.font, contentX, graphicY);
            contentX += graphicWidth + gap;
        }
        if (showText) {
            int textRight = getX() + getWidth() - CONTENT_PADDING;
            int color = active ? theme.text() : theme.mutedText();
            renderScrollingString(graphics, minecraft.font, getMessage(), contentX, getY(), textRight,
                    getY() + getHeight(), color);
        }
    }

    private void renderGraphic(GuiGraphics graphics, Font font, int x, int y) {
        if (sprite != null) {
            graphics.blitSprite(sprite, x, y, spriteWidth, spriteHeight);
        } else if (!itemStack.isEmpty()) {
            graphics.renderItem(itemStack, x, y);
            if (renderItemDecorations) {
                graphics.renderItemDecorations(font, itemStack, x, y);
            }
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }

    @FunctionalInterface
    public interface OnPress {
        void onPress(TerraButton button);
    }

    public static final class Builder {
        private final Component message;
        private final OnPress onPress;
        private int x;
        private int y;
        private int width = DEFAULT_WIDTH;
        private int height = DEFAULT_HEIGHT;
        private TerraUiTheme theme = TerraUiTheme.VANILLA;
        private double opacity = 1.0D;
        private boolean showText = true;
        private boolean renderItemDecorations;
        @Nullable
        private ResourceLocation sprite;
        private int spriteWidth;
        private int spriteHeight;
        private ItemStack itemStack = ItemStack.EMPTY;
        @Nullable
        private Tooltip tooltip;

        private Builder(Component message, OnPress onPress) {
            this.message = Objects.requireNonNull(message, "message");
            this.onPress = Objects.requireNonNull(onPress, "onPress");
        }

        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder size(int width, int height) {
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("Button dimensions must be positive");
            }
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder bounds(int x, int y, int width, int height) {
            return pos(x, y).size(width, height);
        }

        public Builder theme(TerraUiTheme theme) {
            this.theme = Objects.requireNonNull(theme, "theme");
            return this;
        }

        public Builder opacity(double opacity) {
            if (!Double.isFinite(opacity) || opacity < 0.0D || opacity > 1.0D) {
                throw new IllegalArgumentException("Opacity must be between 0 and 1");
            }
            this.opacity = opacity;
            return this;
        }

        public Builder showText(boolean showText) {
            this.showText = showText;
            return this;
        }

        public Builder icon(ResourceLocation sprite, int width, int height) {
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("Icon dimensions must be positive");
            }
            this.sprite = Objects.requireNonNull(sprite, "sprite");
            this.spriteWidth = width;
            this.spriteHeight = height;
            this.itemStack = ItemStack.EMPTY;
            return this;
        }

        public Builder item(ItemLike item) {
            return item(new ItemStack(Objects.requireNonNull(item, "item")));
        }

        public Builder item(ItemStack stack) {
            this.itemStack = Objects.requireNonNull(stack, "stack").copy();
            this.sprite = null;
            return this;
        }

        public Builder renderItemDecorations(boolean renderItemDecorations) {
            this.renderItemDecorations = renderItemDecorations;
            return this;
        }

        public Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public TerraButton build() {
            return new TerraButton(this);
        }
    }
}
