# Handoff: DynamiDucts → ThermaDucts Rename

## Goal
Rename the entire mod project from `DynamiDucts` (mod id: `dynamiducts`, package: `com.mervyn.dynamiducts`) to `ThermaDucts` (mod id: `thermaducts`, package: `com.mervyn.thermaducts`).

## Current Progress — Phase 1 Done

### Files already modified (Phase 1: Identity):
- `gradle.properties` — `mod_id` → `thermaducts`, `mod_name` → `ThermaDucts`
- `build.gradle` — `OBJWriterTest` main class package updated
- `src/main/templates/META-INF/neoforge.mods.toml` — `logoFile` updated
- `dynamiducts.mixins.json` — renamed to `thermaducts.mixins.json` + package inside updated

## What Worked
- Direct edits on small, well-understood files work efficiently
- Renaming the mixins config file via PowerShell `Rename-Item` was fast

## What Didn't Work
- N/A so far

## Next Steps

### Phase 2: Rename directory structure
Move all directories containing `dynamiducts` in path:

1. `src/main/java/com/mervyn/dynamiducts/` → `src/main/java/com/mervyn/thermaducts/`
2. `src/test/java/com/mervyn/dynamiducts/` → `src/test/java/com/mervyn/thermaducts/`
3. `src/main/resources/assets/dynamiducts/` → `assets/thermaducts/`
4. `src/main/resources/data/dynamiducts/` → `data/thermaducts/`
5. `src/generated/resources/assets/dynamiducts/` → `assets/thermaducts/`
6. `src/generated/resources/data/dynamiducts/` → `data/thermaducts/`

Use `git mv` or PowerShell `Move-Item` to preserve git history.

### Phase 3: Rename main class
- `DynamiDucts.java` → `ThermaDucts.java`
- Class name `DynamiDucts` → `ThermaDucts`
- `MODID = "dynamiducts"` → `"thermaducts"`

### Phase 4: Java source content updates
- ~80 Java files need package/import updates
- All `"info.dynamiducts.*"`, `"block.dynamiducts.*"`, `"item.dynamiducts.*"`, etc. translation keys
- Mixin accessor: `dynamiducts$getHitResult` → `thermaducts$getHitResult`

### Phase 5: Resource file content updates
- `en_us.json`, `ja_jp.json`: all translation keys
- `atlases/blocks.json`: namespace prefixes
- ~400 generated JSON model files referencing `dynamiducts:` namespace

### Phase 6: README.md

### Phase 7: Clean build & verify (`./gradlew build`)
