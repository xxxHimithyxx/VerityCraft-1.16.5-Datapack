package com.verity.eagler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
public final class VerityListener implements Listener { private final VerityPlugin plugin; public VerityListener(VerityPlugin p){plugin=p;} @EventHandler public void join(PlayerJoinEvent e){e.getPlayer().sendMessage("§8[§4Verity§8] §7The signal is awake.");} }
