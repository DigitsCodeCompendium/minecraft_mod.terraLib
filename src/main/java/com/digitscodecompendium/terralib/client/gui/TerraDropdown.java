package com.digitscodecompendium.terralib.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/** A themed dropdown selector with mouse and keyboard selection. */
public final class TerraDropdown<T> extends AbstractButton {
    private static final int TEXT_PADDING = 4;
    private static final int ARROW_WIDTH = 12;

    private final Component label;
    private final List<T> options;
    private final Function<? super T, ? extends Component> optionLabel;
    private final OnSelection<T> onSelection;
    private final TerraUiTheme theme;
    private final double opacity;
    private final OpenDirection openDirection;
    private int selectedIndex;
    private boolean expanded;

    private TerraDropdown(Builder<T> builder) {
        super(builder.x, builder.y, builder.width, builder.height, builder.label);
        this.label = builder.label;
        this.options = List.copyOf(builder.options);
        this.optionLabel = builder.optionLabel;
        this.onSelection = builder.onSelection;
        this.theme = builder.theme;
        this.opacity = builder.opacity;
        this.openDirection = builder.openDirection;
        this.selectedIndex = builder.selectedIndex;
        setTooltip(builder.tooltip);
    }

    public static <T> Builder<T> builder(Component label, List<T> options, T selected,
                                         OnSelection<T> onSelection) {
        return new Builder<>(label, options, selected, onSelection);
    }

    @Override
    public void onPress() {
        expanded = !expanded;
    }

    public T value() {
        return options.get(selectedIndex);
    }

    public void setValue(T value) {
        int index = options.indexOf(value);
        if (index < 0) {
            throw new IllegalArgumentException("Dropdown value is not one of its options");
        }
        selectedIndex = index;
    }

    public boolean expanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        double backgroundOpacity = opacity * alpha * (active ? 1.0D : 0.55D);
        TerraGui.raisedPanel(graphics, getX(), getY(), getWidth(), getHeight(), theme, backgroundOpacity);
        Component current = optionLabel.apply(value());
        renderScrollingString(graphics, minecraft.font, current,
                getX() + TEXT_PADDING, getY(), getX() + getWidth() - ARROW_WIDTH,
                getY() + getHeight(), active ? theme.text() : theme.mutedText());
        graphics.drawCenteredString(minecraft.font, expanded ? "▲" : "▼",
                getX() + getWidth() - ARROW_WIDTH / 2, getY() + (getHeight() - minecraft.font.lineHeight) / 2,
                active ? theme.text() : theme.mutedText());

        if (expanded) {
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 200.0F);
            for (int index = 0; index < options.size(); index++) {
                int rowY = optionY(index);
                boolean hovered = mouseX >= getX() && mouseX < getX() + getWidth()
                        && mouseY >= rowY && mouseY < rowY + getHeight();
                if (index == selectedIndex || hovered) {
                    TerraGui.raisedPanel(graphics, getX(), rowY, getWidth(), getHeight(), theme, backgroundOpacity);
                } else {
                    TerraGui.recessedPanel(graphics, getX(), rowY, getWidth(), getHeight(), theme, backgroundOpacity);
                }
                renderScrollingString(graphics, minecraft.font, optionLabel.apply(options.get(index)),
                        getX() + TEXT_PADDING, rowY, getX() + getWidth() - TEXT_PADDING,
                        rowY + getHeight(), index == selectedIndex ? theme.text() : theme.mutedText());
            }
            graphics.pose().popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (expanded && button == 0) {
            int option = optionAt(mouseX, mouseY);
            if (option >= 0) {
                select(option);
                expanded = false;
                playDownSound(Minecraft.getInstance().getSoundManager());
                return true;
            }
            if (!isInsideHeader(mouseX, mouseY)) {
                expanded = false;
                return false;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (expanded && (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_DOWN)) {
            int direction = keyCode == GLFW.GLFW_KEY_DOWN ? 1 : -1;
            select(Math.floorMod(selectedIndex + direction, options.size()));
            return true;
        }
        if (expanded && keyCode == GLFW.GLFW_KEY_ESCAPE) {
            expanded = false;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean isInsideHeader(double mouseX, double mouseY) {
        return mouseX >= getX() && mouseX < getX() + getWidth()
                && mouseY >= getY() && mouseY < getY() + getHeight();
    }

    private int optionAt(double mouseX, double mouseY) {
        if (mouseX < getX() || mouseX >= getX() + getWidth()) return -1;
        for (int index = 0; index < options.size(); index++) {
            int rowY = optionY(index);
            if (mouseY >= rowY && mouseY < rowY + getHeight()) return index;
        }
        return -1;
    }

    private int optionY(int index) {
        return openDirection == OpenDirection.DOWN
                ? getY() + getHeight() * (index + 1)
                : getY() - getHeight() * (options.size() - index);
    }

    private void select(int index) {
        if (selectedIndex != index) {
            selectedIndex = index;
            onSelection.onSelection(this, value());
        }
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        return CommonComponents.optionNameValue(label, optionLabel.apply(value()));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }

    @FunctionalInterface
    public interface OnSelection<T> {
        void onSelection(TerraDropdown<T> dropdown, T value);
    }

    public enum OpenDirection {
        DOWN,
        UP
    }

    public static final class Builder<T> {
        private final Component label;
        private final List<T> options;
        private final OnSelection<T> onSelection;
        private Function<? super T, ? extends Component> optionLabel = value -> Component.literal(value.toString());
        private int selectedIndex;
        private int x;
        private int y;
        private int width = TerraButton.DEFAULT_WIDTH;
        private int height = TerraButton.DEFAULT_HEIGHT;
        private TerraUiTheme theme = TerraUiTheme.VANILLA;
        private double opacity = 1.0D;
        private OpenDirection openDirection = OpenDirection.DOWN;
        @Nullable
        private Tooltip tooltip;

        private Builder(Component label, List<T> options, T selected, OnSelection<T> onSelection) {
            this.label = Objects.requireNonNull(label, "label");
            this.options = List.copyOf(options);
            if (this.options.isEmpty()) {
                throw new IllegalArgumentException("A dropdown must contain at least one option");
            }
            this.options.forEach(option -> Objects.requireNonNull(option, "option"));
            this.selectedIndex = this.options.indexOf(selected);
            if (selectedIndex < 0) {
                throw new IllegalArgumentException("Initial dropdown value is not one of its options");
            }
            this.onSelection = Objects.requireNonNull(onSelection, "onSelection");
        }

        public Builder<T> bounds(int x, int y, int width, int height) {
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("Dropdown dimensions must be positive");
            }
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder<T> optionLabel(Function<? super T, ? extends Component> optionLabel) {
            this.optionLabel = Objects.requireNonNull(optionLabel, "optionLabel");
            return this;
        }

        public Builder<T> theme(TerraUiTheme theme) {
            this.theme = Objects.requireNonNull(theme, "theme");
            return this;
        }

        public Builder<T> opacity(double opacity) {
            if (!Double.isFinite(opacity) || opacity < 0.0D || opacity > 1.0D) {
                throw new IllegalArgumentException("Opacity must be between 0 and 1");
            }
            this.opacity = opacity;
            return this;
        }

        public Builder<T> openDirection(OpenDirection openDirection) {
            this.openDirection = Objects.requireNonNull(openDirection, "openDirection");
            return this;
        }

        public Builder<T> tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public TerraDropdown<T> build() {
            return new TerraDropdown<>(this);
        }
    }
}
