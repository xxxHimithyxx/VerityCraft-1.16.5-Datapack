# VerityCraft 1.16.5

A datapack-first horror experience inspired by atmospheric survival horror: isolation, unreliable signals, scarce safety, sound-driven scares, hallucinations, and escalating fear.

## Installation
1. Download or clone this repository.
2. Copy `datapack/veritycraft` into your world’s `datapacks` folder.
3. Run `/reload` in Minecraft Java Edition 1.16.5.
4. The datapack starts automatically for each player.

## Mechanics
- `vc_fear` is an invisible fear score that rises during events and nearby threats.
- Fear gradually decays, but high fear causes darkness, slowness, nausea, and heartbeat audio.
- Every few seconds, players may hear whispers or footsteps, experience a blackout/hallucination, see a false Verity presence, or trigger a short ambush.
- Events are server-side and multiplayer-safe.

## Commands
- `/scoreboard players get @s vc_fear`
- `/function veritycraft:setup_player`
- `/function veritycraft:event`

## Scope and attribution
This is an original Minecraft 1.16.5 datapack implementation inspired by the requested horror atmosphere. It is not a byte-for-byte copy of another game, video, mod, logo, or copyrighted assets. Add your own licensed resource-pack sounds and logo artwork in a separate resource pack.
