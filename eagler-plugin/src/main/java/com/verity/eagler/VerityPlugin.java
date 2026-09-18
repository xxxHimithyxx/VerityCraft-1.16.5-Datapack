package com.verity.eagler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public final class VerityPlugin extends JavaPlugin implements CommandExecutor {
    private final Map<UUID, Integer> threat = new HashMap<UUID, Integer>();
    private final Map<UUID, Long> cooldown = new HashMap<UUID, Long>();
    private final Map<UUID, String> objective = new HashMap<UUID, String>();
    private final Map<UUID, Boolean> audio = new HashMap<UUID, Boolean>();
    private final Map<UUID, Boolean> visuals = new HashMap<UUID, Boolean>();

    @Override public void onEnable() {
        saveDefaultConfig();
        saveResource("objectives.yml", false);
        getCommand("verity").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(new VerityListener(this), this);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override public void run() { evaluatePlayers(); }
        }, 20L, 20L);
        getLogger().info("Verity Eaglercraft server plugin enabled.");
    }

    public void evaluatePlayers() {
        if (!getConfig().getBoolean("enabled", true)) return;
        long now = System.currentTimeMillis();
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID id = player.getUniqueId();
            if (!audio.containsKey(id)) audio.put(id, true);
            if (!visuals.containsKey(id)) visuals.put(id, true);
            long ready = cooldown.containsKey(id) ? cooldown.get(id) : 0L;
            if (now < ready || player.isDead() || player.isSleeping()) continue;
            int level = getThreat(player);
            double chance = getConfig().getDouble("random-event-chance", 0.08) / 20.0;
            if (Math.random() > chance) continue;
            cooldown.put(id, now + getConfig().getLong("minimum-event-interval-seconds", 45L) * 1000L);
            if (Math.random() < 0.58) whisper(player); else footsteps(player);
            if (level >= 3 && Math.random() < 0.15) jumpscare(player);
        }
    }

    public int getThreat(Player player) { return threat.containsKey(player.getUniqueId()) ? threat.get(player.getUniqueId()) : 0; }
    public void setThreat(Player player, int level) { threat.put(player.getUniqueId(), Math.max(0, Math.min(getConfig().getInt("max-threat", 5), level))); }
    public void addThreat(Player player, int amount) { setThreat(player, getThreat(player) + amount); }
    public boolean audioEnabled(Player p) { return !audio.containsKey(p.getUniqueId()) || audio.get(p.getUniqueId()); }
    public boolean visualsEnabled(Player p) { return !visuals.containsKey(p.getUniqueId()) || visuals.get(p.getUniqueId()); }
    private void sound(Player p, Sound sound, float volume, float pitch) { if (audioEnabled(p)) p.playSound(p.getLocation(), sound, volume, pitch); }

    public void whisper(Player p) {
        addThreat(p, 1); sound(p, Sound.valueOf(getConfig().getString("sounds.whisper", "ENTITY_ENDERMAN_STARE")), .55f, .55f);
        p.sendMessage(ChatColor.DARK_RED + "Something whispered your name.");
    }
    public void footsteps(Player p) {
        addThreat(p, 1); Location at = p.getLocation().clone().add(p.getLocation().getDirection().multiply(-8));
        if (audioEnabled(p)) p.playSound(at, Sound.valueOf(getConfig().getString("sounds.footsteps", "STEP_WOOD")), .8f, .7f);
        p.sendMessage(ChatColor.GRAY + "Footsteps stop somewhere behind you.");
    }
    public void jumpscare(Player p) {
        addThreat(p, 1); sound(p, Sound.valueOf(getConfig().getString("sounds.jumpscare", "ENTITY_CREEPER_PRIMED")), .8f, .6f);
        if (visualsEnabled(p)) p.sendTitle(ChatColor.DARK_RED + "RUN", ChatColor.GRAY + "Verity has noticed you.", 0, 20, 10);
    }
    public void spawnRoom(Player p, String room) {
        addThreat(p, 1); p.sendMessage(ChatColor.DARK_RED + "The room has changed: " + room);
        whisper(p);
    }
    public void setObjective(Player p, String id) { objective.put(p.getUniqueId(), id); p.sendMessage(ChatColor.RED + "Objective: " + id); }
    public String getObjective(Player p) { return objective.get(p.getUniqueId()); }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("objectives")) {
            if (!(sender instanceof Player)) { sender.sendMessage("Only players have objectives."); return true; }
            Player p = (Player) sender; sender.sendMessage(ChatColor.GRAY + "Active objective: " + (getObjective(p) == null ? "none" : getObjective(p))); return true;
        }
        if (args[0].equalsIgnoreCase("reload")) { if (!sender.hasPermission("verity.admin")) return noPerm(sender); reloadConfig(); sender.sendMessage(ChatColor.GREEN + "Verity configuration reloaded."); return true; }
        if (!sender.hasPermission("verity.admin")) return noPerm(sender);
        if (args[0].equalsIgnoreCase("objective") && args.length >= 3) { Player p = Bukkit.getPlayer(args[2]); if (p == null) return missing(sender); if (args[1].equalsIgnoreCase("start")) setObjective(p, args[2]); else if (args[1].equalsIgnoreCase("complete")) { addThreat(p, getConfig().getInt("objectives." + args[2] + ".reward-threat", 0)); p.sendMessage(ChatColor.GREEN + "Objective complete: " + args[2]); } else if (args[1].equalsIgnoreCase("reset")) objective.remove(p.getUniqueId()); return true; }
        if (args[0].equalsIgnoreCase("threat") && args.length >= 3) { Player p = Bukkit.getPlayer(args[2]); if (p == null) return missing(sender); if (args[1].equalsIgnoreCase("get")) sender.sendMessage("Threat: " + getThreat(p)); else if (args[1].equalsIgnoreCase("set") && args.length >= 4) setThreat(p, Integer.parseInt(args[3])); return true; }
        if (args[0].equalsIgnoreCase("event") && args.length >= 3) { Player p = Bukkit.getPlayer(args[2]); if (p == null) return missing(sender); if (args[1].equalsIgnoreCase("trigger")) { if (args.length > 3 && args[3].equalsIgnoreCase("jumpscare")) jumpscare(p); else if (args.length > 3 && args[3].equalsIgnoreCase("footsteps")) footsteps(p); else whisper(p); } return true; }
        if (args[0].equalsIgnoreCase("room") && args.length >= 3) { Player p = Bukkit.getPlayer(args[2]); if (p == null) return missing(sender); spawnRoom(p, args.length >= 4 ? args[3] : "signal_room"); return true; }
        sender.sendMessage(ChatColor.RED + "Usage: /verity objectives | objective | threat | event | room | reload"); return true;
    }
    private boolean noPerm(CommandSender s) { s.sendMessage(ChatColor.RED + "You need verity.admin."); return true; }
    private boolean missing(CommandSender s) { s.sendMessage(ChatColor.RED + "Player not found."); return true; }
}
