package com.verity.eagler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

/** Adds the physical starter box and companion commands without requiring a newer API. */
public final class VerityCompanionListener implements Listener {
    private final VerityCompanionManager manager;
    public VerityCompanionListener(VerityCompanionManager manager) { this.manager = manager; }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        manager.giveStarterBox(event.getPlayer());
    }

    @EventHandler
    public void onBoxUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (manager.isBox(event.getItem())) {
            event.setCancelled(true);
            manager.release(event.getPlayer());
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String[] parts = message.split("\\s+");
        if (parts.length == 0 || !parts[0].equalsIgnoreCase("/verity")) return;
        Player player = event.getPlayer();
        if (parts.length >= 2 && parts[1].equalsIgnoreCase("packup")) {
            event.setCancelled(true);
            manager.packUp(player);
        } else if (parts.length >= 3 && parts[1].equalsIgnoreCase("build")) {
            event.setCancelled(true);
            manager.requestBuild(player, parts[2]);
        }
    }
}
