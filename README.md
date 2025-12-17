# Modern Hex-and-Counter Wargame Prototype

JavaFX desktop prototype featuring procedural, symmetrical hex maps, basic unit overlays, and JSON save/load support.

## Requirements
- Java 21 (Java 17+ compatible)
- Gradle

## Running
```bash
./gradlew run
```
Gradle will download JavaFX automatically. The `gradle-wrapper.jar` is intentionally not checked into version control; the `gradlew` script will download the configured Gradle distribution and extract the wrapper JAR on first run (requires `curl` or `wget` and `unzip`).

## Controls
- Main menu: New Scenario, Load Scenario, placeholders for Multiplayer/Settings, Exit
- New Scenario: select biome, map size, nations, AI difficulty, optional seed
- Game view:
  - **Pan**: drag with left mouse or WASD
  - **Zoom**: mouse wheel (clamped 0.4–2.5)
  - **Mini menu** (☰ top-left): Save, Load, Settings placeholder, Exit to Main Menu, Exit Game
  - **Hover**: outline hex + top-right info overlay (coordinates, terrain, site, edge counts, units)
  - **Toggle units**: switch at bottom of screen

## Save/Load format
- JSON (`*.scenario.json`) via Jackson
- Schema includes version, scenario config (biome, size, nations, difficulty, seed), map tiles with terrain and feature, river/road edge flags, units, and camera (offset + zoom).
- Version is validated for forward compatibility; unknown fields are ignored for resilience.
- New scenarios start "dirty" (unsaved) so exiting to the main menu prompts you to save; saving or loading clears the dirty flag.

## Map model
- Pointy-top hex grid using **odd-r offset** coordinates internally.
- Terrain palette: plain, hill, mountain, forest, jungle, lake, ocean, town, city.
- Installations (`FeatureType` overlay): military base, air base, naval dockyard, power plant, oil depot, supply depot, military HQ, government HQ, naval HQ, airforce HQ.
- Rivers are stored as **edge flags** per hex; rendering draws lines along polygon borders.
- Roads are stored as **centerline connections** (edge flags) and render between hex centers.

## Procedural generation
- Vertical mirror symmetry: map[x] mirrors to map[width-1-x].
- Value-noise + smoothing to clump terrain, then biome-specific thresholds.
- Biome influences: Temperate (forests/rivers), Desert (plains/hills, rare water), Arctic (ocean bands + mountains), Tropical (jungle + rivers), Islands (ocean-heavy), Steppe (plains/hills).
- Urban centers: mirrored placement of cities/towns with installation features on many cities.
- Rivers: 1–5 seeded random walks traced along edges.
- Roads: connect cities via spanning-tree-like network plus extra links; towns link to nearest city.

## Units
- Data model supports domains (ground/air/naval/missile) and numerous unit types.
- Prototype spawns infantry and armor on each city for both nations; stacked lists stored in game state.
- Units render as small circles with letter code and appear in hover info.

## Architecture
```
app/                Application entry + scene routing
ui/                 Main menu, scenario setup, game view, dialogs
map/                Hex map, terrain/feature enums, edge flags, generator
render/             Canvas renderers for map and overlays
input/              Camera + key bindings
units/              Unit domain/type/nation models
game/               Scenario config, game state, JSON serializer
util/               Hex math, clamping, random seed helper
```
