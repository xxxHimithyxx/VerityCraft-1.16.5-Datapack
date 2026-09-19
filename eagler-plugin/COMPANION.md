# Verity companion features

The Eaglercraft version now includes an original companion flow inspired by the requested videos:

1. On first join, the player receives **Verity's Sealed Box**.
2. Right-click the box to release a named Verity helper entity.
3. Verity introduces itself in chat and tells the player how to ask questions.
4. Right-click Verity to receive a response.
5. Use `/verity packup` to remove Verity and receive the box again.
6. Use `/verity build <youtube-url>` to submit a build reference.

## Important build-link limitation

A Bukkit plugin running on an Eaglercraft server cannot safely watch arbitrary YouTube videos, understand their 3D structure, download copyrighted assets, and automatically reconstruct a build. The plugin validates and records YouTube links, then asks for a schematic, block list, or build plan. Automatic building should only be added through an explicit, server-approved blueprint format and protected-region checks.

This keeps the feature honest and prevents arbitrary links from causing unsafe downloads or destroying player builds.
