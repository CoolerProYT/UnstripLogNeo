# UnstripLog

A simple Minecraft mod that allows you to **unstrip logs** by re-applying bark to them. When you strip a log with an axe, it now drops a **Bark** item — and you can use that bark on a stripped log to restore it to its original state.

Available for both **Fabric** and **NeoForge**.

## Features

- 🪓 **Bark Drops** — Stripping a log with an axe now drops a Bark item.
- 🪵 **Unstrip Logs** — Right-click a stripped log with the matching Bark item to restore it to its unstripped form.
- 🔥 **Fuel** — Bark items can be used as furnace fuel (150 ticks / 7.5 seconds).
- 🎨 **Per-Wood-Type Bark** — Each wood type (oak, birch, spruce, etc.) has its own distinct bark variant with unique textures.
- ⚙️ **Highly Configurable** — JSON-based config files let you customize every log/bark mapping, add modded log support, and more.
- 🔄 **Config Hot-Reload** — The detailed config watches for file changes and reloads automatically — no restart needed.
- 🌐 **Server-Client Config Sync** — Config is synced from server to client so multiplayer servers stay consistent.
- 📖 **JEI Integration** — Stripping and unstripping recipes are viewable in JEI (Just Enough Items).

## How It Works

1. **Strip a log** with an axe — it drops a Bark item on the ground.
2. **Pick up the bark** and hold it in your hand.
3. **Right-click a stripped log** with the bark — the log is restored to its original unstripped state, consuming one bark.

## Configuration

Config files are located in your game directory under `config/unstriplog/`.

### `bark-type.json`

Defines the available bark types and their textures. Each entry has:
- `name` — The bark type identifier (e.g. `"oak"`, `"birch"`)
- `texture` — The texture resource location for the bark item

### `unstrip-detailed.json`

Defines the detailed log-to-bark mappings. Each entry has:
- `base` — The original (unstripped) log block ID
- `stripped` — The stripped log block ID
- `drop` — The item dropped when stripping (with optional data components)
- `unstrip_item` *(optional)* — A different item required to unstrip (defaults to the drop item)

### General Config (Platform-specific)

- `barkItem` — The item ID used as the default bark item (default: `unstriplog:bark`)
- `allowUnknownLog` — Whether to allow stripping/unstripping of logs not explicitly listed in the detailed config (default: `true`)

## Supported Platforms

| Platform | Supported |
|----------|-----------|
| Fabric   | ✅        |
| NeoForge | ✅        |

## Dependencies

| Dependency | Required |
|------------|----------|
| Fabric API | ✅ (Fabric) |
| NeoForge   | ✅ (NeoForge) |
| JEI        | ❌ Optional |

## Minecraft Version

- **Minecraft 26.1.1** (Range: `[26.1, 26.2)`)

## Authors

- **CoolerProMC**
- **ChesyDev**