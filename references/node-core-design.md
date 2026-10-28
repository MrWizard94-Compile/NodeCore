# Node Core — Design Reference

**Mod ID:** `nodecore`  
**Target:** Forge 1.20.1 / 47.3.7 (Base Wars pack)  
**Status:** Base Wars overhaul complete — extractor-node economy, lush caverns, growth booster, Liquid Fire fuel, dead-world surface. The legacy AdLods / Large-Ore-Deposit LOD-mirroring stack has been **removed** (superseded by the extractor-node economy).

Canonical design spec: `JanusPrime/references/basewars-overhaul-spec.md`. Full GDD: `C:\WPAI\references\Base_Warfare_Vision_Doc.md`.

## Purpose

Node Core implements the **Node Warfare** layer from the Base Wars vision:

1. **Strategic ore nodes** — indestructible, permanent node fixtures generated in geographic metal-type clusters; harvested only via a player-built extractor multiblock (one per node).
2. **Mega-Lush underground caverns** — massive lush caverns below Y 0 with accelerated plant/tree growth; contested territory.
3. **Surface sterility / dead world** — open-sky planting blocked; surface grass renders uniformly dead-tan, lush cavern volumes stay green.
4. **Extractor economy** — Create rotational power drives extraction; fuel ladder (charcoal < coal < lava < Liquid Fire) boosts output; Create grinding/washing/haunting outputs severely nerfed to make extraction the survivable path.
5. **Extraction alerts** — global broadcast when drilling begins at an ore node.

## Architecture (current)

```
NodeCore (root: NodeCore.java)
├── config/NodeCoreConfig            — Forge common config (all tunables)
├── node/
│   ├── NodeType                     — ore_iron, lush_hydro, etc.
│   ├── ResourceNode                 — UUID, center, radius, NBT
│   └── NodeQueries                  — level-scoped lookups; canonical isInLushCavern / isInLushNode
├── data/
│   ├── NodeSavedData                — per-dimension node registry
│   ├── LushCavernSavedData          — generated lush-cavern volumes (3D)
│   ├── LushCavernVolume             — region-bounds volume record
│   └── LiquidFireCollectionData     — cauldron Liquid Fire collection state
├── block/
│   ├── NodeMarkerBlock              — marker place/break ↔ SavedData sync
│   ├── node/OreNodeBlock            — indestructible ore-node fixture
│   ├── fluid/LiquidFireBlock        — Liquid Fire fluid block (Forge Supplier ctor)
│   └── extractor/                   — ExtractorRegistry, ExtractorMultiblockValidator,
│                                      ExtractorFuelTier, ExtractorOutputCatalog,
│                                      OreExtractorCoreBlock, OreExtractorFrameBlock
├── blockentity/                     — ModBlockEntities, OreExtractorCoreBlockEntity (fluid/energy)
├── worldgen/
│   ├── OreNodeClusterMap            — geographic metal-type cluster regions
│   ├── OreNodePlacer                — ore-node fixture placement
│   ├── LushCavernGrid               — cavern site grid (below Y 0)
│   ├── LushCavernPlacer             — cavern carve + flora/tree population
│   ├── DeadWorldBiomeModifier       — dead-world surface biome strip
│   ├── NodeSpacingHints             — min/max spacing helpers (kept; ore-node spacing)
│   └── NodeWorldgenStub             — spacing gate for placement (kept)
├── event/
│   ├── SurfaceSterilityHandler      — cancel sky-exposed planting
│   ├── DeadWorldGrowthHandler       — growth allowed in lush node ∪ cavern volumes
│   ├── LushGrowthHandler            — growth-tick booster (3D cavern sampling via isInLushCavern)
│   ├── OreNodeHandler               — ore-node place/break registry sync
│   ├── OreNodeChunkHandler          — chunk-load ore-node hydration
│   ├── OreNodeNoBuildAuraHandler    — no-block-placement aura around nodes (whitelists extractor parts)
│   ├── OreNodeProtectionHandler     — node fixture indestructibility
│   ├── LushCavernChunkHandler       — cavern volume chunk hydration
│   ├── LushCavernSyncHandler        — cavern volume client sync
│   ├── LiquidFireCollectionHandler  — cauldron-under-soulsand collection
│   ├── LiquidFireFuelHandler        — Liquid Fire as top-tier extractor fuel
│   ├── NodeMarkerHandler            — marker ↔ SavedData sync
│   ├── ExtractionAlertEvent / ExtractionAlertHandler — drill-place broadcast (KubeJS-cancellable)
├── network/                         — NodeCoreNetwork, LushCavernVolumesPacket (volume sync)
├── client/                          — NodeCoreClient, DeadWorldGrassColors, DeadWorldTintRules
│                                      (surface dead-tan tint), LushCavernClientCache, LushCavernTintQueries
├── integration/
│   ├── create/                      — CreateKineticBridge, CreateModProbe (soft-optional rotational power)
│   ├── dynamictrees/                — DynamicTreesBridge, DynamicTreesGrowthBoost, DynamicTreesModProbe
│   ├── NodeInControlBridge / NodeInControlExporter — soft-optional In Control spawn export
│   └── NodeKubeBridge               — soft-optional KubeJS detection
├── command/NodeCoreCommands         — /nodecore admin tools (add/export; legacy /link removed)
├── item/NodeSurveyItem              — survey scanner
└── registry/                        — ModBlocks, ModItems, ModFluids, ModFluidTypes,
                                       ModBlockEntities, ModBiomeModifiers, ModArgumentTypes, ModCreativeTabs
```

All third-party hooks (Create, Dynamic Trees, In Control, KubeJS) are **soft-optional** — mod-presence/probe checks, no compile-time dependency.

## Node Types

| ID | Category | Default Radius | Base Wars role |
|----|----------|----------------|----------------|
| `ore_iron` | ORE | 128 | Iron Sump extraction zone |
| `ore_copper` | ORE | 112 | Copper Basin |
| `ore_brass` | ORE | 120 | Brass Basin |
| `ore_quartz` | ORE | 96 | Nether quartz deposits |
| `lush_hydro` | LUSH | 160 | Mega-Lush cavern agriculture |
| `quartz_rift` | ORE | 144 | Volcanic rift (Stage 4 portals) |

## Data Model

Nodes persist in `nodecore_nodes` SavedData per `ServerLevel`; lush cavern volumes persist separately in `LushCavernSavedData` as 3D region bounds (membership via `NodeQueries.isInLushCavern`, the canonical server-side query that client tint and the growth booster both delegate to).

## Config ↔ Vision Mapping

| Vision requirement | Implementation |
|--------------------|----------------|
| Seeds rot in open sky | `SurfaceSterilityHandler` + `sterilityEnabled` |
| Growth only in lush nodes/caverns | `LushGrowthHandler` + `NodeQueries.isInLushCavern` / `isInLushNode` |
| Surface uniformly dead, lush stays green | `DeadWorldGrassColors` / `DeadWorldTintRules` + `LushCavernTintQueries` |
| Indestructible clustered ore nodes | `OreNodeBlock` + `OreNodeClusterMap` + `OreNodePlacer` + `OreNodeProtectionHandler` |
| One extractor per node, Create-driven | extractor multiblock + `CreateKineticBridge` |
| Fuel ladder boosts output | `ExtractorFuelTier` + `LiquidFireFuelHandler` |
| Create outputs nerfed | recipe output reduction (datapack/config) |
| No-build aura around nodes | `OreNodeNoBuildAuraHandler` (whitelists extractor parts) |
| Drill placement alert | `ExtractionAlertHandler` + configurable `alertBlocks` |

## Removed (superseded)

The AdLods / Large-Ore-Deposit LOD-mirroring system was removed in the overhaul teardown: `NodeLodBridge`, `NodeLodComms`, `NodeDepositEvent`, `NodeLodEventHandler`, `NodeSpacingWorldgenProvider`, `NodeCoreDataGenerators`, the `adlods/` + `bridge/` packages, the `/nodecore link` command, and `node_spacing_hints.json`. Ore placement now comes from the native clustered ore-node worldgen, not LOD mirroring.
