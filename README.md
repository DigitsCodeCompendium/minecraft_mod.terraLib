# TerraLib

Shared NeoForge 1.21.1 library for the Terra Minecraft mods.

## Included APIs

- `TerraGui`: texture-free machine panels, vanilla-style raised panels, recessed and instrument inset panels, slots and slot grids, accent plaques, status indicators, progress bars, perimeter progress, circles, pie charts, and badges.
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
