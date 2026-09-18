# Verity Survival Horror — Eaglercraft Edition

This branch contains the Eaglercraft-compatible implementation of Verity as a **server-side Bukkit/Paper plugin**. It does not require Fabric, Forge, or a client-side mod.

## Compatibility

Target the server version that your Eaglercraft client actually connects to. The default source is written against Bukkit APIs available on 1.8.8 and should be tested on the server software you use. A normal Fabric 1.16.5 mod cannot run in Eaglercraft.

## Features in this MVP

- Per-player threat level from 0–5.
- Configurable whisper and fake-footstep events.
- Event and jumpscare cooldowns.
- Basic dynamic objectives and `/verity` administrative commands.
- Cursed-room trigger command with a temporary visual/audio event.
- Accessibility toggles for audio and visual effects.
- Data-driven `config.yml` and `objectives.yml`.
- No permanent block destruction and no player-list fake entities.

## Build and install

1. Install Java and Maven.
2. Build with `mvn package`.
3. Put `target/verity-eagler-1.0.0.jar` in the server's `plugins` folder.
4. Restart the server.
5. Edit `plugins/Verity/config.yml` and run `/verity reload`.

The plugin is server-side. Custom sound files require a matching server resource pack and must be added to the resource-pack sound registry; the MVP falls back to vanilla sounds.

## Commands

- `/verity objectives`
- `/verity objective start <id>`
- `/verity objective complete <id>`
- `/verity objective reset <id>`
- `/verity threat get [player]`
- `/verity threat set <player> <0-5>`
- `/verity event trigger <event> [player]`
- `/verity room spawn <room-id> [player]`
- `/verity reload`

Administrative commands require the `verity.admin` permission. Players may use `/verity objectives`.

## Important limitation

Eaglercraft clients generally emulate older Minecraft versions and do not load Fabric/Forge mods. Therefore this implementation uses Bukkit/Paper server events instead of a Fabric 1.16.5 mod. Verify your exact Eaglercraft client/server pair before deployment.
