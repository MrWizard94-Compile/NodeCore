# Node Core — Design Reference

**Mod ID:** `nodecore`  
**Target:** Forge 1.20.1 (Base Wars pack)  
**Status:** Phase 2 complete — marker linking, worldgen spacing stub, Omni32 textures; LOD integration pending

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
│   └── ExtractionAlertHandler     — drill-place broadcast
├── command/NodeCoreCommands       — /nodecore admin tools
├── event/NodeMarkerHandler        — marker place/break ↔ SavedData sync
├── worldgen/
│   ├── NodeSpacingHints           — min/max spacing from config
│   └── NodeWorldgenStub           — LOD placement hook (stub)
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

## Integration Points (planned)

| System | Integration |
|--------|-------------|
| Large Ore Deposits | Auto-register node centers when deposits generate |
| In Control! | Spawn rules keyed to node type + radius |
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

**Out of scope (next phase):**
- Procedural node placement at worldgen (LOD hard dependency)
- Ore stripping / biome purge (may remain in KubeJS or separate datapack)
- Faction system integration
- Guardian mob spawn binding

## Config ↔ Vision Mapping

| Vision doc requirement | Implementation |
|------------------------|----------------|
| Seeds rot in open sky | `SurfaceSterilityHandler` + `sterilityEnabled` |
| Growth only in lush nodes | `LushGrowthHandler` + `NodeQueries.isInLushNode` |
| Nodes 2500–4000 apart | `minSpacing`/`maxSpacing` in config + datagen stub; `NodeSpacingHints.violatesMinSpacing` for placement checks |
| Drill placement alert | `ExtractionAlertHandler` + configurable `alertBlocks` |