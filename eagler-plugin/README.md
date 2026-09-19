# Verity Survival Horror — Eaglercraft Edition

Server-side Bukkit/Spigot plugin for Eaglercraft-compatible servers. It is not a Fabric or Forge mod: Eaglercraft browser clients generally cannot load those mod loaders.

## Features

- Threat level 0–5 with increased nighttime/event pressure.
- Rare positional whispers and two-step fake footsteps.
- Configurable jumpscare and event cooldowns.
- Objective assignment/completion/reset.
- Cursed-room trigger hooks.
- Approximate shelter rating for base-building gameplay.
- Per-player audio and visual accessibility toggles.
- Vanilla sound fallbacks; optional resource-pack branding.
- No permanent block destruction or fake entries in player lists.

## Build

From `eagler-plugin/` run `mvn package`. Install `target/verity-eagler-1.0.0.jar` into the server `plugins` folder. Test against the exact Bukkit/Paper/Eaglercraft server version you use.

## Commands

Players: `/verity objectives`, `/verity audio on|off`, `/verity visuals on|off`.

Admins with `verity.admin`:
- `/verity reload`
- `/verity objective start|complete|reset <player> <id>`
- `/verity threat get <player>` or `/verity threat set <player> <0-5>`
- `/verity event trigger <player> whisper|footsteps|jumpscare`
- `/verity room spawn <player> <room-id>`

Use original or licensed voice/audio assets only. The video references informed the atmosphere and mechanics; this is an original implementation, not a copy of proprietary code or assets.
