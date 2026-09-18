# Verity Survival Horror — 1.16.5 MVP

This repository contains a data-driven, datapack-first survival-horror prototype for Minecraft Java Edition 1.16.5. It implements the requested core loop without copying proprietary code, recordings, logos, or assets from another project.

## Current MVP

- Hidden per-player threat level from 0–5-ish using the `verity.threat` scoreboard.
- Rare whispers, footsteps, visual hallucinations, blackout effects, and a short ambush event.
- Cooldowns so horror events do not fire continuously.
- A starter data/config directory for future cursed-room and scarcity expansion.
- Namespace corrected to `verity`.
- Vanilla-safe implementation: no 1.19+ effects or sounds are used.

## Install

1. Download the repository as a ZIP.
2. Open the target Minecraft 1.16.5 world save.
3. Put `datapack/veritycraft` into `<world>/datapacks/`.
4. Run `/reload`.
5. Confirm with `/scoreboard objectives list`.

The datapack is server-side and works in multiplayer. It does not permanently delete blocks or player builds.

## Testing commands

- `/function verity:player_init`
- `/function verity:event`
- `/scoreboard players get @s verity.threat`
- `/scoreboard players set @s verity.threat 100`
- `/reload`

## Resource pack plan

The datapack uses vanilla sounds as safe placeholders. A companion resource pack can replace them with original or licensed assets under the `verity` namespace:

- `assets/verity/sounds/alerts/`
- `assets/verity/sounds/footsteps/`
- `assets/verity/sounds/whispers/`
- `assets/verity/sounds/jumpscares/`
- `assets/verity/textures/gui/`
- `assets/verity/textures/logo/`

Do not imitate a real person’s voice without permission. The intended visual palette is black, ash gray, desaturated blue, pale warning white, and dark crimson.

## Roadmap

1. Add `/verity` command equivalents through a Fabric mod or Paper plugin.
2. Add JSON-loaded objectives and state transitions.
3. Add shelter scoring and claimed-base protection.
4. Add template-based cursed rooms.
5. Add original sound/resource-pack assets and accessibility toggles.
6. Add automated tests and a compiled Fabric 1.16.5 mod.
