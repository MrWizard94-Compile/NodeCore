# Node Core — Design Reference

**Mod ID:** `nodecore`  
**Target:** Forge 1.20.1 (Base Wars pack)  
**Status:** Phase 5 complete — In Control spawn export, KubeJS extraction alert bridge, LOD soft-optional bridge

## Purpose

Node Core implements the **Node Warfare** layer from the Base Wars vision document:

1. **Strategic extraction nodes** — hyper-dense ore deposits at fixed coordinates (~2500–4000 block spacing)
2. **Mega-Lush Hydro-Nodes** — underground agriculture zones with accelerated growth
3. **Surface sterility** — open-sky planting blocked (smog-choked dead world)
4. **Extraction alerts** — global broadcast when drilling begins at an ore node

Full GDD: `C:\Projects\Base_Warfare_Vision_Doc.md` (sections 3–4).

## Architecture

```
NodeCore (main)
├── config/NodeCoreConfig          — Forge common config
├── data/NodeSavedData             — per-dimension node registry (SavedData)
├── node/
│   ├── NodeType                   — ore_iron, lush_hydro, etc.
│   ├── ResourceNode               — UUID, center, radius, NBT
│   └── NodeQueries                — level-scoped lookup helpers
├── event/
│   ├── SurfaceSterilityHandler    — cancel sky-exposed planting
│   ├── LushGrowthHandler          — tick-based growth boost in lush nodes
│   ├── ExtractionAlertHandler     — drill-place broadcast
│   ├── NodeMarkerHandler          — marker place/break ↔ SavedData sync
│   ├── NodeDepositEvent           — runtime deposit-placed event
│   └── NodeLodEventHandler        — deposit event → NodeLodBridge
├── command/NodeCoreCommands       — /nodecore admin tools
├── integration/
│   ├── NodeInControlBridge        — soft-optional In Control detection
│   ├── NodeInControlExporter      — areas + spawner/spawn snippet export
│   └── NodeKubeBridge             — soft-optional KubeJS detection
├── worldgen/
│   ├── NodeSpacingHints           — min/max spacing from config
│   ├── NodeWorldgenStub           — spacing gate for placement
│   ├── NodeLodBridge              — soft-optional LOD detection + deposit→node registration
│   └── NodeLodComms               — IMC channel `nodecore:register_deposit`
├── datagen/NodeSpacingWorldgenProvider — emits node_spacing_hints.json
└── registry/                      — blocks, items, creative tab
```

## Node Types

| ID | Category | Default Radius | Base Wars role |
|----|----------|----------------|----------------|
| `ore_iron` | ORE | 128 | Iron Sump extraction zone |
| `ore_copper` | ORE | 112 | Copper Basin |
| `ore_brass` | ORE | 120 | Brass Basin |
| `ore_quartz` | ORE | 96 | Nether quartz deposits |
| `lush_hydro` | LUSH | 160 | Mega-Lush Cave agriculture |
| `quartz_rift` | ORE | 144 | Volcanic rift (Stage 4 portals) |

## Data Model

Nodes persist in `nodecore_nodes` SavedData per `ServerLevel`:

```json
{
  "Nodes": [
    {
      "Id": "<uuid>",
      "Type": "ore_iron",
      "Center": "<blockpos long>",
      "Radius": 128
    }
  ]
}
```

Membership test: `center.distSqr(pos) <= radius²`.

## Integration Points

| System | Integration |
|--------|-------------|
| Large Ore Deposits | `NodeLodBridge` + `NodeDepositEvent` + IMC `nodecore:register_deposit` |
| In Control! | `NodeInControlExporter` — bbox areas + spawner/spawn snippets per registered node |
| KubeJS | `ExtractionAlertEvent` + `NodeKubeBridge` — cancel/customize extraction alerts |
| KubeJS | Optional script hooks for custom alert formatting |
| Create mechanical drill | Default extraction alert trigger block |

## v0.1.0 Scope

**In scope:**
- Manual node registration via commands
- Surface sterility + lush growth + extraction alerts
- Survey scanner item
- Node marker block (visual placeholder)

**Phase 2 (current):**
- Node marker block auto-registers/removes nodes on place/break
- Worldgen spacing stub (`data/nodecore/worldgen/node_spacing_hints.json` via datagen)
- `NodeSpacingHints` / `NodeWorldgenStub` for future LOD wiring

**Phase 4 (current):**
- Soft-optional Large Ore Deposits bridge — no compile-time LOD dependency
- `NodeLodBridge.isLodPresent()` checks mod ids: `lode`, `largeoredeposits`, `large_ore_deposits`
- `NodeLodBridge.registerDeposit(level, center, depositType)` maps deposit strings → `NodeType`, runs `NodeWorldgenStub.shouldPlaceNode`, then `NodeSavedData.addNode` with default radius
- `NodeDepositEvent` on `MinecraftForge.EVENT_BUS` for runtime deposit hooks
- `NodeLodComms` processes IMC `nodecore:register_deposit` (NBT: `X`/`Y`/`Z` + `Type` or `DepositType`, optional `Dimension`; or string `x,y,z,type[,dimension]`)
- `/nodecore link <type> [pos]` — manual deposit link for pack testing without LOD

**LOD deposit type → NodeType mapping:**

| Deposit type key(s) | NodeType |
|---------------------|----------|
| `iron`, `iron_deposit`, `iron_sump`, `ore_iron` | `ore_iron` |
| `copper`, `copper_deposit`, `copper_basin`, `ore_copper` | `ore_copper` |
| `brass`, `brass_deposit`, `brass_basin`, `ore_brass` | `ore_brass` |
| `quartz`, `nether_quartz`, `quartz_vein`, `ore_quartz` | `ore_quartz` |
| `lush`, `lush_hydro`, `hydro`, `mega_lush` | `lush_hydro` |
| `quartz_rift`, `rift`, `volcanic_rift` | `quartz_rift` |

Any key matching a `NodeType` id (case-insensitive) is also accepted.

**Phase 5 (current):**
- Soft-optional In Control bridge — no compile-time In Control dependency
- `NodeInControlBridge.isInControlPresent()` checks mod ids: `incontrol`, `in_control`
- `NodeInControlExporter` writes JSON arrays for all nodes in a `ServerLevel`:
  - **Areas** (`nodecore_generated_areas.json`): bounding box per node with `name` (`nodecore_<type>_<id8>`), `dimension`, `xmin`–`zmax` (X/Z = center ± radius; Y = 0–320 for ore nodes, center ± radius for `lush_hydro`)
  - **Spawner snippets** (`nodecore_generated_spawner.json`): per-node `spawner.json` add rules (mob from config, type template) plus `spawn.json` area filters referencing the area name (`incontrol` + `area` + `result: allow`)
- Config: `incontrol.spawnExportEnabled`, `incontrol.spawnMobsByType` (`ore_iron=minecraft:zombie`, …)
- `/nodecore export incontrol` — exports current dimension; reports paths + node count (permission 2)
- Output path: `<gameDir>/config/incontrol/` via `FMLPaths.GAMEDIR.get()`
- `ExtractionAlertEvent` on `MinecraftForge.EVENT_BUS` for KubeJS/script cancellation (`examples/kubejs/nodecore_extraction_alert.js`)

**Default `spawnMobsByType` mapping:**

| NodeType | Default mob |
|----------|-------------|
| `ore_iron` | `minecraft:zombie` |
| `ore_copper` | `minecraft:husk` |
| `ore_brass` | `minecraft:drowned` |
| `ore_quartz` | `minecraft:zombified_piglin` |
| `lush_hydro` | `minecraft:cave_spider` |
| `quartz_rift` | `minecraft:blaze` |

Pack maintainers merge generated snippets into live `areas.json` / `spawner.json` / `spawn.json`, then `/incontrol reload`.

**Out of scope (next phase):**
- Ore stripping / biome purge (may remain in KubeJS or separate datapack)
- Faction system integration
- Automatic re-export on node registry changes

## Config ↔ Vision Mapping

| Vision doc requirement | Implementation |
|------------------------|----------------|
| Seeds rot in open sky | `SurfaceSterilityHandler` + `sterilityEnabled` |
| Growth only in lush nodes | `LushGrowthHandler` + `NodeQueries.isInLushNode` |
| Nodes 2500–4000 apart | `minSpacing`/`maxSpacing` in config + datagen stub; `NodeSpacingHints.violatesMinSpacing` for placement checks |
| Drill placement alert | `ExtractionAlertHandler` + configurable `alertBlocks` |