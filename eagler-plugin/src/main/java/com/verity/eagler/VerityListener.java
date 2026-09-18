package com.verity.eagler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class VerityListener implements Listener {
    private final VerityPlugin plugin;
    public VerityListener(VerityPlugin plugin) { this.plugin = plugin; }
    @EventHandler public void quit(PlayerQuitEvent event) {
        // Runtime state is intentionally allowed to expire with the session.
    }
}
