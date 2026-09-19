# VerityCraft 1.16.5 — standalone datapack package

This folder is the datapack-only version for Minecraft Java 1.16.5. It can be copied into a world's `datapacks` directory without installing the Bukkit plugin.

## Install

Copy `datapack/veritycraft` into:

```text
<world>/datapacks/veritycraft/
```

Then run `/reload`.

## Start the companion

For a player who has not received the box:

```text
/function verity:companion/give_box
```

Open the companion:

```text
/function verity:companion/open_box
```

Pack Verity away and receive another box:

```text
/function verity:companion/packup
```

Ask Verity through the datapack interface:

```text
/function verity:companion/ask_help
/function verity:companion/ask_objective
/function verity:companion/ask_safe
/function verity:companion/ask_who
/function verity:companion/ask_signal
```

A datapack cannot listen to free-form chat, call an online AI, speak arbitrary text, watch a YouTube video, or reconstruct a build from a link. Those features require a server plugin/mod plus original voice assets and a safe blueprint format. The datapack supplies the playable offline approximation: companion state, chat responses, objectives, threat escalation, and horror events.

## Package warning

The repository ZIP contains both the standalone datapack and the optional Eaglercraft Bukkit plugin source. Only the `datapack/veritycraft` directory belongs in a Java 1.16.5 world's `datapacks` folder.
