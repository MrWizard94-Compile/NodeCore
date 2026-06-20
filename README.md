# Node Core

Base Wars utility mod for **Minecraft Forge 1.20.1** — strategic resource nodes, surface sterility, lush hydro-node growth, and extraction alerts.

Part of the [Base Wars: Build Anyway](https://github.com/MrWizard94-Compile) modpack vision. See `references/node-core-design.md` for design notes and `../Base_Warfare_Vision_Doc.md` for the full GDD.

## Features (v0.2.0)

- **Node registry** — per-dimension saved data for ore and lush nodes with configurable radius
- **Surface sterility** — blocks planting seeds/saplings on sky-exposed ground
- **Lush growth** — accelerated crop/sapling ticks inside registered lush hydro-nodes
- **Extraction alerts** — server-wide warning when drill blocks are placed inside ore nodes
- **Admin commands** — `/nodecore add|list|nearest|remove`
- **Survey scanner** — creative-tab item to locate the nearest registered node
- **Node marker block** — place to register a node, break to remove (configurable default type)
- **Worldgen spacing stub** — datagen hints + `NodeSpacingHints` for future LOD wiring

## Build

```bash
./gradlew build
```

Requires Java 17. Forge **47.3.7**, mappings **Parchment 2023.09.03-1.20.1**.

## Configuration

`config/nodecore-common.toml` after first run:

| Section | Key | Default |
|---------|-----|---------|
| surface | sterilityEnabled | true |
| lush | growthEnabled | true |
| lush | growthIntervalTicks | 40 |
| extraction | alertsEnabled | true |
| extraction | alertBlocks | `["create:mechanical_drill"]` |
| nodes | defaultRadius | 48 |
| nodes | minSpacing | 2500 |
| nodes | maxSpacing | 4000 |
| nodes | markerDefaultType | ore_iron |

## Commands

| Command | Description |
|---------|-------------|
| `/nodecore add <type> [pos] [radius]` | Register a node at player position or given coordinates |
| `/nodecore list` | List all nodes in the current dimension |
| `/nodecore nearest [pos]` | Find nearest node |
| `/nodecore remove <id-prefix>` | Remove node by UUID prefix |

Node types: `ore_iron`, `ore_copper`, `ore_brass`, `ore_quartz`, `lush_hydro`, `quartz_rift`

## KubeJS (optional)

When [KubeJS](https://www.curseforge.com/minecraft/mc-mods/kubejs) is installed, pack scripts can hook extraction alerts before they are broadcast to all players.

| API | Value |
|-----|-------|
| Runtime check | `NodeKubeBridge.isKubeJsPresent()` |
| Event class | `com.mrwizard94.nodecore.event.ExtractionAlertEvent` |

Copy `examples/kubejs/nodecore_extraction_alert.js` into `kubejs/server_scripts/` and reload (`/reload`).

**`ExtractionAlertEvent` fields (Java / script):**

| Getter | Description |
|--------|-------------|
| `getLevel()` | `ServerLevel` where the drill was placed |
| `getPos()` | Block position of the placed extraction block |
| `getNode()` | `ResourceNode` containing the position |
| `getPlayer()` | Placing `ServerPlayer`, or `null` if non-player |
| `getAlertMessage()` / `setAlertMessage(Component)` | Mutable broadcast text (default from lang key `nodecore.message.extraction_alert`) |

Call `event.cancel()` in a script to suppress the server-wide alert entirely.

```javascript
ForgeEvents.onEvent('com.mrwizard94.nodecore.event.ExtractionAlertEvent', event => {
    if (event.getPlayer() != null && event.getPlayer().isCreative()) {
        event.cancel()
        return
    }
    event.setAlertMessage(Text.red('Custom drill warning!'))
})
```

No compile-time KubeJS dependency — Node Core posts a standard Forge event on `MinecraftForge.EVENT_BUS`.

## Roadmap

- Large Ore Deposits / worldgen integration for automatic node placement
- In Control! spawn zone binding per node type
- Additional KubeJS hooks (rewards, spawn rules)
- Marker block state for per-type node registration (phase 3)

## Related Projects

| Project | Path |
|---------|------|
| VS2 Ship Systems | `C:\Projects\VS2 Ship Systems` |
| Valkyrien Portals | `C:\Projects\Valkyrien Portals` |
| Base Wars vision | `C:\Projects\Base_Warfare_Vision_Doc.md` |