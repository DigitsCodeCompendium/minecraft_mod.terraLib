package com.digitscodecompendium.terralib.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** A Terra-themed text entry with the normal Minecraft editing, selection, focus, and narration behavior. */
public final class TerraTextField extends EditBox {
    private static final int TEXT_PADDING = 4;

    private final TerraUiTheme theme;
    private final double opacity;

    private TerraTextField(Builder builder) {
        super(builder.font, builder.x, builder.y, builder.width, builder.height, builder.label);
        this.theme = builder.theme;
        this.opacity = builder.opacity;
        setBordered(false);
        setMaxLength(builder.maxLength);
        setFilter(builder.filter);
        setHint(builder.hint);
        setTextColor(theme.text());
        setTextColorUneditable(theme.mutedText());
        setValue(builder.initialValue);
        setResponder(builder.responder);
        setTooltip(builder.tooltip);
    }

    public static Builder builder(Font font, Component label) {
        return new Builder(font, label);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int originalX = getX();
        int originalY = getY();
        if (isFocused()) {
            TerraGui.raisedPanel(graphics, originalX, originalY, getWidth(), getHeight(), theme, opacity * alpha);
        } else {
            TerraGui.recessedPanel(graphics, originalX, originalY, getWidth(), getHeight(), theme, opacity * alpha);
        }

        setX(originalX + TEXT_PADDING);
        setY(originalY + (getHeight() - 8) / 2);
        try {
            super.renderWidget(graphics, mouseX, mouseY, partialTick);
        } finally {
            setX(originalX);
            setY(originalY);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        int originalX = getX();
        setX(originalX + TEXT_PADDING);
        try {
            super.onClick(mouseX, mouseY);
        } finally {
            setX(originalX);
        }
    }

    @Override
    public int getInnerWidth() {
        return Math.max(0, super.getInnerWidth() - TEXT_PADDING * 2);
    }

    public static final class Builder {
        private final Font font;
        private final Component label;
        private int x;
        private int y;
        private int width = TerraButton.DEFAULT_WIDTH;
        private int height = TerraButton.DEFAULT_HEIGHT;
        private int maxLength = 32;
        private String initialValue = "";
        private Component hint = Component.empty();
        private Predicate<String> filter = Objects::nonNull;
        private Consumer<String> responder = value -> { };
        private TerraUiTheme theme = TerraUiTheme.VANILLA;
        private double opacity = 1.0D;
        @Nullable
        private Tooltip tooltip;

        private Builder(Font font, Component label) {
            this.font = Objects.requireNonNull(font, "font");
            this.label = Objects.requireNonNull(label, "label");
        }

        public Builder bounds(int x, int y, int width, int height) {
            if (width <= TEXT_PADDING * 2 || height <= 0) {
                throw new IllegalArgumentException("Text field dimensions are too small");
            }
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder maxLength(int maxLength) {
            if (maxLength < 0) {
                throw new IllegalArgumentException("Maximum text length cannot be negative");
            }
            this.maxLength = maxLength;
            return this;
        }

        public Builder initialValue(String initialValue) {
            this.initialValue = Objects.requireNonNull(initialValue, "initialValue");
            return this;
        }

        public Builder hint(Component hint) {
            this.hint = Objects.requireNonNull(hint, "hint");
            return this;
        }

        public Builder filter(Predicate<String> filter) {
            this.filter = Objects.requireNonNull(filter, "filter");
            return this;
        }

        public Builder responder(Consumer<String> responder) {
            this.responder = Objects.requireNonNull(responder, "responder");
            return this;
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

        public Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public TerraTextField build() {
            return new TerraTextField(this);
        }
    }
}
