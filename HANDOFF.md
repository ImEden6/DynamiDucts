# Handoff: DynamiDucts → ThermaDucts Rename

## Goal
Rename the entire mod project from `DynamiDucts` (mod id: `dynamiducts`, package: `com.mervyn.dynamiducts`) to `ThermaDucts` (mod id: `thermaducts`, package: `com.mervyn.thermaducts`).

## Current Progress — Phases 1 & 2 Done

### Phase 1: Identity files (done)
- `gradle.properties` — `mod_id` → `thermaducts`, `mod_name` → `ThermaDucts`
- `build.gradle` — `OBJWriterTest` main class package updated
- `src/main/templates/META-INF/neoforge.mods.toml` — `logoFile` updated
- `dynamiducts.mixins.json` — renamed to `thermaducts.mixins.json` + package inside updated

### Phase 2: Directory structure (done)
All directories renamed via `Move-Item` (git history preserved):

| Old | New |
|-----|-----|
| `main/java/.../dynamiducts/` | `main/java/.../thermaducts/` |
| `test/java/.../dynamiducts/` | `test/java/.../thermaducts/` |
| `main/resources/assets/dynamiducts/` | `main/resources/assets/thermaducts/` |
| `main/resources/data/dynamiducts/` | `main/resources/data/thermaducts/` |

`src/generated/resources/` directories did not exist — skipped (created at build time).

## What Worked
- Direct edits on small, well-understood files work efficiently
- Renaming the mixins config file via PowerShell `Rename-Item` was fast
- PowerShell `Move-Item` for directory renames is clean and preserves git history
- Moves are independent operations — can run all 4 in parallel

## What Didn't Work
- N/A so far

## Next Steps

### Phase 3: Rename main class
- `src/main/java/com/mervyn/thermaducts/DynamiDucts.java` → `ThermaDucts.java`
- Class name `DynamiDucts` → `ThermaDucts`
- `MODID = "dynamiducts"` → `"thermaducts"`

### Phase 4: Java source content updates
- ~95 Java files need package/import updates (package `com.mervyn.dynamiducts` → `com.mervyn.thermaducts`)
- All `"info.dynamiducts.*"`, `"block.dynamiducts.*"`, `"item.dynamiducts.*"`, etc. translation keys
- Mixin accessor: `dynamiducts$getHitResult` → `thermaducts$getHitResult`
- Tip: use search-and-replace sed/perl across all java files for package rename, then fix remaining unique strings individually

### Phase 5: Resource file content updates
- `en_us.json`, `ja_jp.json`: all translation keys
- `atlases/blocks.json`: namespace prefixes

### Phase 6: README.md

### Phase 7: Clean build & verify (`./gradlew build`)
