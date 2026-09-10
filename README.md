# TerraLib

Shared NeoForge 1.21.1 library for the Terra Minecraft mods.

## Included APIs

- `TerraGui`: texture-free panels, image/item/block displays, slots, boolean and custom-color pips, indicators, progress bars, charts, and badges.
- `TerraMenu`: tabbed panels with tabs on any side, tab hit-testing, scroll panels, and vertical or horizontal scrollbars.
- `TerraButton`: themed, narratable buttons using text, GUI sprite icons, items, blocks, or mixed text-and-graphic content.
- `TerraDropdown` and `TerraTextField`: themed selectors and text entry with standard mouse, keyboard, tooltip, focus, and narration behavior.
- `TerraUiTheme`: reusable ARGB palettes with built-in `MACHINE` and `VANILLA` themes.
- `SharedHudPanel`: a single bottom-right HUD panel with ordered, independently visible contributions from multiple mods.
- `HudPanelConfig` and `HudPanelPlacement`: standard enabled, scale, position, and anchor controls for TerraLib HUD panels.
- `ProgressChartHud` and `HudAnchor`: reusable progress/chart HUD layout and positioning. Progress charts can also be embedded in the shared panel.
- `TerraColors`: strict `#RRGGBB` parsing/formatting and alpha composition.
- `TerraFormats`: locale-stable scaled numbers, tick durations, and human-readable identifiers.

Client-only rendering APIs live under `com.digitscodecompendium.terralib.client`; common utilities live under `com.digitscodecompendium.terralib.util` and are safe to use on either side.

## Local development dependency

Publish TerraLib to its local repository:

```powershell
.\gradlew.bat publish
```

Then add the repository and dependency to a consuming mod:

```groovy
repositories {
    maven { url = uri('../minecraft_mod.terraLib/repo') }
}

dependencies {
    implementation 'com.digitscodecompendium.terralib:terralib:0.1.0'
}
```

Add a required `terralib` dependency to the consuming mod's `neoforge.mods.toml` as well.

## Releases

Push a SemVer tag beginning with `v` to build, test, and publish a GitHub Release containing the TerraLib JAR. The version in the JAR filename and mod metadata is taken from the tag, so `v1.2.3` produces `terralib-1.2.3.jar`.

```powershell
git tag v1.2.3
git push origin v1.2.3
```

The release workflow uses GitHub's built-in token, so no repository secret is required. The tag must point to the commit you want to release.

## Example

```java
TerraGui.machinePanel(graphics, leftPos, topPos, imageWidth, imageHeight);
TerraGui.recessedPanel(graphics, leftPos + 12, topPos + 31, 152, 98);
TerraGui.slotGrid(graphics, leftPos + 7, topPos + 139, 9, 3);
TerraGui.progressBar(graphics, x, y, 132, 8, progress, 10);
```

All render methods use GUI coordinates and accept a custom `TerraUiTheme` where styling needs to differ.

## Tabbed and scrolling menus

`TerraMenu.tabbedPanel` accepts `TOP`, `BOTTOM`, `LEFT`, or `RIGHT`; left/right tabs are vertical and top/bottom tabs
are horizontal. The returned layout provides content bounds and `tabAt(mouseX, mouseY)` for click handling.

```java
TerraMenu.TabbedPanelLayout menu = TerraMenu.tabbedPanel(
        graphics, font, left, top, 176, 166,
        List.of(Component.literal("Overview"), Component.literal("Members")),
        selectedTab, TerraMenu.TabSide.LEFT, TerraUiTheme.VANILLA, panelConfig.opacity());

TerraMenu.ScrollPanelLayout scroll = TerraMenu.scrollPanel(
        graphics, menu.content().x(), menu.content().y(),
        menu.content().width(), menu.content().height(),
        contentHeight, scrollOffset, TerraMenu.ScrollAxis.VERTICAL,
        TerraUiTheme.VANILLA, panelConfig.opacity());
```

Use `scroll.viewport()` as the scissor bounds while drawing content. `scroll.scrollBar()` exposes the track, thumb,
clamped offset, and maximum offset for wheel and drag handling. Standalone horizontal and vertical bars are available
through `TerraMenu.scrollBar`.

## Reusable buttons

Add `TerraButton` to a screen with the normal `addRenderableWidget(...)` method. Text, GUI sprite, item, and block
factories all return a builder with bounds, theme, opacity, tooltip, and display options.

```java
addRenderableWidget(TerraButton.text(Component.literal("Save"), button -> save())
        .bounds(left, top, 80, 20)
        .build());

addRenderableWidget(TerraButton.icon(Component.literal("Settings"), SETTINGS_SPRITE, 16, 16,
        button -> openSettings()).pos(left, top + 24).build());

addRenderableWidget(TerraButton.item(Blocks.GRASS_BLOCK, button -> selectGrass())
        .pos(left + 24, top + 24)
        .tooltip(Tooltip.create(Component.literal("Grass Block")))
        .build());
```

Call `.showText(true)` after `icon(...)` or `item(...)` and give the button wider bounds to place its graphic beside
the label. Item stacks can optionally render counts and durability with `.renderItemDecorations(true)`.

Any button can also hold a boolean selected state. Toggle callbacks receive the new value, and narration reports the
current on/off state.

```java
addRenderableWidget(TerraButton.toggle(Component.literal("Auto mode"), autoMode,
        (button, selected) -> setAutoMode(selected)).bounds(left, top, 90, 20).build());

addRenderableWidget(TerraButton.item(Blocks.CHEST, button -> { })
        .toggle(storageEnabled, (button, selected) -> setStorageEnabled(selected))
        .pos(left + 94, top).build());
```

## Displays, pips, selectors, and text entry

Image and item display panels are non-interactive drawing primitives. Passing any `ItemLike` supports both items and
blocks. Boolean pips use the theme's positive/negative colors; color pips accept any ARGB value.

```java
TerraGui.imagePanel(graphics, x, y, 32, 32, PORTRAIT_SPRITE, 24, 24);
TerraGui.itemPanel(graphics, font, x + 36, y, 24, 24, Blocks.DIAMOND_BLOCK);
TerraGui.booleanPip(graphics, x, y + 36, machineRunning, TerraUiTheme.MACHINE);
TerraGui.colorPip(graphics, x + 12, y + 36, factionColor, TerraUiTheme.VANILLA);
```

Dropdowns accept arbitrary value types and a function that turns each value into display text. They open downward by
default and can be configured to open upward.

```java
TerraDropdown<Mode> mode = TerraDropdown.builder(
        Component.literal("Mode"), List.of(Mode.values()), currentMode,
        (dropdown, selected) -> setMode(selected))
        .optionLabel(value -> Component.translatable(value.translationKey()))
        .bounds(left, top, 110, 20)
        .build();
addRenderableWidget(mode);

TerraTextField name = TerraTextField.builder(font, Component.literal("Name"))
        .bounds(left, top + 24, 110, 20)
        .hint(Component.literal("Enter a name"))
        .maxLength(32)
        .responder(this::setName)
        .build();
addRenderableWidget(name);
```

## Shared HUD panel

Register contributions once from each mod's client initializer. Each contribution chooses a preferred end of the
vertical stack and a priority. Top-preferring entries pack downward, bottom-preferring entries pack upward, and higher
priorities sit closest to the requested end. Equal priorities are resolved by contribution ID. Return `null` from the
supplier whenever that mod has nothing to display.

```java
SharedHudPanel.register(
        ResourceLocation.fromNamespaceAndPath("terraskills", "skill_progress"),
        SharedHudPanel.PreferredEnd.BOTTOM,
        100,
        () -> hudEnabled ? ProgressChartHud.sharedElement(currentProgress()) : null);

SharedHudPanel.register(
        ResourceLocation.fromNamespaceAndPath("terrafactions", "territory"),
        SharedHudPanel.PreferredEnd.TOP,
        200,
        () -> territoryVisible ? new SharedHudPanel.Element(120, 20,
                (graphics, delta, x, y) -> renderTerritory(graphics, x, y)) : null);
```

TerraLib owns the GUI layer and outer panel. Contributing mods should not register another GUI layer or draw another
panel background for this content.

## Standard HUD configuration

`HudPanelConfig` adds standard `enabled`, `scale` (0.25-4), `opacity` (0-1), `horizontalPercent` (0-100),
`verticalPercent` (0-100), and `anchor` settings. Opacity affects the panel background and frame while leaving its text
and indicators readable. Define the template as part of the consuming mod's client config spec, then register that spec
normally with NeoForge.

```java
public final class TerraSkillsClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final HudPanelConfig SKILL_POINT_HUD = HudPanelConfig.define(
            BUILDER, "skillPointHud", "terraskills.configuration");
    public static final ModConfigSpec SPEC = BUILDER.build();
}
```

Register that spec from the consuming mod's main entry point:

```java
container.registerConfig(ModConfig.Type.CLIENT, TerraSkillsClientConfig.SPEC);
```

To make it editable from that mod's entry in NeoForge's Mods list, register the standard screen from its client entry
point as well:

```java
container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
```

The translation prefix above uses these keys: `hudEnabled`, `scale`, `opacity`, `horizontalPercent`,
`verticalPercent`, and `anchor`. The standard defaults are enabled, scale `1`, opacity `1`, position `98%, 98%`, and
`BOTTOM_RIGHT`. Mods can pass a `HudPanelConfig.Defaults` instance to the four-argument `define` overload when another
starting layout is needed.

For a standalone progress panel, the config converts directly to its placement:

```java
ProgressChartHud.render(graphics, content, TerraSkillsClientConfig.SKILL_POINT_HUD);
```

For shared-panel content, use a consuming mod's `HudPanelConfig.enabled()` value in its contribution supplier. Position
and scale apply to standalone panels; individual contributions cannot independently move or scale the shared stack.
