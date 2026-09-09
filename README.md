# TerraLib

Shared NeoForge 1.21.1 library for the Terra Minecraft mods.

## Included APIs

- `TerraGui`: texture-free machine panels, vanilla-style raised panels, recessed and instrument inset panels, slots and slot grids, accent plaques, status indicators, progress bars, perimeter progress, circles, pie charts, and badges.
- `TerraUiTheme`: reusable ARGB palettes with built-in `MACHINE` and `VANILLA` themes.
- `ProgressChartHud` and `HudAnchor`: reusable progress/chart HUD layout and positioning.
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
