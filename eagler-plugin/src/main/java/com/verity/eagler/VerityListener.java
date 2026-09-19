package com.verity.eagler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class VerityListener implements Listener {
    private final VerityPlugin plugin;
    public VerityListener(VerityPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§8[§4Verity§8] §7The signal is awake.");
        plugin.speak(event.getPlayer(), "world", "A new requirement has been assigned.", 20000L);
    }
}
