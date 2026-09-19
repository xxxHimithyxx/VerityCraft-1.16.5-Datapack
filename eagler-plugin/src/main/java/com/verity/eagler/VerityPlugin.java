package com.verity.eagler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public final class VerityPlugin extends JavaPlugin implements CommandExecutor {
    private final Map<UUID, Integer> threat = new HashMap<UUID, Integer>();
    private final Map<UUID, Long> nextEvent = new HashMap<UUID, Long>();
    private final Map<UUID, Long> nextJumpscare = new HashMap<UUID, Long>();
    private final Map<UUID, Long> lastVoice = new HashMap<UUID, Long>();
    private final Map<UUID, String> objective = new HashMap<UUID, String>();
    private final Map<UUID, Boolean> audio = new HashMap<UUID, Boolean>();
    private final Map<UUID, Boolean> visuals = new HashMap<UUID, Boolean>();
    private final Map<UUID, String> lastCategory = new HashMap<UUID, String>();
    private final Random random = new Random();

    private static final String CAT_WORLD = "world";
    private static final String CAT_OBJECTIVE = "objective";
    private static final String CAT_DANGER = "danger";

    private static final List<String> WORLD_LINES = Arrays.asList(
        "Something moved beyond the trees.",
        "Your shelter is no longer empty.",
        "The lights have noticed you.",
        "The room has changed.",
        "You should not be here.",
        "The objective has been altered.",
        "Do not follow the footsteps.",
        "The walls are listening."
    );

    private static final List<String> OBJECTIVE_LINES = Arrays.asList(
        "A new requirement has been assigned.",
        "The next location has been revealed.",
        "You are missing one component.",
        "The structure is awake.",
        "Return before nightfall.",
        "Objective assigned. Proceed carefully.",
        "The signal is unstable.",
        "A path has opened."
    );

    private static final List<String> DANGER_LINES = Arrays.asList(
        "Do not turn around.",
        "The sound is getting closer.",
        "You have been detected.",
        "Remain still.",
        "Leave the area immediately.",
        "It knows your name.",
        "Do not look back.",
        "The room is no longer safe."
    );

    @Override public void onEnable() {
        saveDefaultConfig();
        saveResource("objectives.yml", false);
        getCommand("verity").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(new VerityListener(this), this);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override public void run() { evaluatePlayers(); }
        }, 40L, 20L);
        getLogger().info("Verity voice system initialized.");
    }

    private void ensure(Player p) {
        UUID id = p.getUniqueId();
        if (!threat.containsKey(id)) threat.put(id, 0);
        if (!audio.containsKey(id)) audio.put(id, getConfig().getBoolean("accessibility.loud-audio", true));
        if (!visuals.containsKey(id)) visuals.put(id, getConfig().getBoolean("accessibility.visual-effects", true));
        if (!lastVoice.containsKey(id)) lastVoice.put(id, 0L);
        if (!lastCategory.containsKey(id)) lastCategory.put(id, CAT_WORLD);
    }

    public int getThreat(Player p) { ensure(p); return threat.get(p.getUniqueId()); }
    public void setThreat(Player p, int value) { ensure(p); threat.put(p.getUniqueId(), Math.max(0, Math.min(getConfig().getInt("max-threat", 5), value))); }
    public void addThreat(Player p, int amount) { setThreat(p, getThreat(p) + amount); }

    public boolean audioEnabled(Player p) { ensure(p); return audio.get(p.getUniqueId()); }
    public boolean visualsEnabled(Player p) { ensure(p); return visuals.get(p.getUniqueId()); }
    public void setAudio(Player p, boolean enabled) { ensure(p); audio.put(p.getUniqueId(), enabled); }
    public void setVisuals(Player p, boolean enabled) { ensure(p); visuals.put(p.getUniqueId(), enabled); }

    private boolean isNight(World world) {
        long t = world.getTime() % 24000L;
        return t >= 13000L && t <= 23000L;
    }

    private boolean coolEnough(Player player, long cooldownMs) {
        ensure(player);
        long now = System.currentTimeMillis();
        return now - lastVoice.get(player.getUniqueId()) >= cooldownMs;
    }

    public void evaluatePlayers() {
        if (!getConfig().getBoolean("enabled", true)) return;
        for (Player p : Bukkit.getOnlinePlayers()) {
            ensure(p);
            if (p.isDead() || p.isSleeping()) continue;
            if (!coolEnough(p, 45000L)) continue;

            int threatLevel = getThreat(p);
            double chance = getConfig().getDouble("random-event-chance", 0.08D);
            if (isNight(p.getWorld())) chance *= 1.75D;
            chance *= 1.0D + (threatLevel * 0.18D);

            if (random.nextDouble() > chance) continue;

            if (threatLevel >= 3 && random.nextInt(100) < 18) {
                speak(p, CAT_DANGER, randomLine(DANGER_LINES), 6000L);
            } else if (random.nextInt(100) < 50) {
                speak(p, CAT_WORLD, randomLine(WORLD_LINES), 6000L);
            } else {
                footsteps(p);
            }
        }
    }

    public void speak(Player player, String category, String line, long cooldownMs) {
        ensure(player);
        if (!coolEnough(player, cooldownMs)) return;
        lastVoice.put(player.getUniqueId(), System.currentTimeMillis());
        lastCategory.put(player.getUniqueId(), category);

        if (audioEnabled(player)) {
            Sound s = Sound.ENDERMAN_STARE;
            if (CAT_DANGER.equals(category)) s = Sound.CREEPER_PRIMED;
            else if (CAT_OBJECTIVE.equals(category)) s = Sound.ORB_PICKUP;
            else if (CAT_WORLD.equals(category)) s = Sound.NOTE_BASS;
            player.playSound(player.getLocation(), s, 0.7F, 0.75F + (random.nextFloat() * 0.25F));
        }

        player.sendMessage(ChatColor.DARK_RED + "Verity: " + ChatColor.GRAY + line);
    }

    public void announceObjective(Player player, String objectiveText) {
        speak(player, CAT_OBJECTIVE, objectiveText, 20000L);
        setObjective(player, objectiveText);
    }

    public void whisper(Player player) {
        addThreat(player, 1);
        if (audioEnabled(player)) {
            player.playSound(player.getLocation(), Sound.ENDERMAN_STARE, 0.45F, 0.5F);
        }
        player.sendMessage(ChatColor.DARK_RED + "Verity: " + ChatColor.GRAY + "Behind you.");
    }

    public void footsteps(final Player player) {
        addThreat(player, 1);
        final Location behind = player.getLocation().clone().subtract(player.getLocation().getDirection().normalize().multiply(8D));
        if (audioEnabled(player)) {
            player.playSound(behind, Sound.STEP_WOOD, 0.8F, 0.75F + (random.nextFloat() * 0.2F));
        }
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override public void run() {
                if (player.isOnline() && audioEnabled(player)) {
                    player.playSound(behind, Sound.STEP_WOOD, 0.55F, 0.55F);
                }
            }
        }, 12L + random.nextInt(10));
        player.sendMessage(ChatColor.GRAY + "Footsteps stop somewhere behind you.");
    }

    public void jumpscare(Player player) {
        if (!coolEnough(player, getConfig().getLong("jumpscare-cooldown-seconds", 180L) * 1000L)) return;
        nextJumpscare.put(player.getUniqueId(), System.currentTimeMillis());
        addThreat(player, 2);
        if (audioEnabled(player)) {
            player.playSound(player.getLocation(), Sound.CREEPER_PRIMED, 0.9F, 0.55F);
        }
        if (visualsEnabled(player)) {
            player.sendTitle(ChatColor.DARK_RED + "RUN", ChatColor.GRAY + "Verity has noticed you.", 5, 25, 10);
        }
        player.sendMessage(ChatColor.DARK_RED + "Verity: " + ChatColor.GRAY + "You have been detected.");
    }

    public void spawnRoom(Player player, String roomId) {
        addThreat(player, 1);
        speak(player, CAT_WORLD, "The room has changed: " + roomId + ".", 30000L);
    }

    public int shelterRating(Player player) {
        Location center = player.getLocation();
        int solid = 0;
        int light = 0;
        for (int x = -3; x <= 3; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = -3; z <= 3; z++) {
                    org.bukkit.Material material = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z).getType();
                    if (material != org.bukkit.Material.AIR) solid++;
                    if (material == org.bukkit.Material.TORCH || material == org.bukkit.Material.GLOWSTONE || material == org.bukkit.Material.JACK_O_LANTERN) light++;
                }
            }
        }
        int score = Math.min(45, solid / 8) + Math.min(25, light * 5);
        if (center.getBlock().getType() == org.bukkit.Material.BED_BLOCK) score += 15;
        return Math.max(0, Math.min(100, score));
    }

    public void setObjective(Player player, String val) { objective.put(player.getUniqueId(), val); }
    public String getObjective(Player player) { return objective.get(player.getUniqueId()); }
    public void completeObjective(Player player, String val) { if (val.equals(getObjective(player))) { objective.remove(player.getUniqueId()); speak(player, CAT_OBJECTIVE, "Objective complete. Proceed carefully.", 20000L); } }
    public void resetObjective(Player player) { objective.remove(player.getUniqueId()); }

    private String randomLine(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            help(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("ask") && args.length >= 2 && sender instanceof Player) {
            Player player = (Player) sender;
            String ask = joinArgs(args, 1);
            String response = buildResponse(player, ask);
            player.sendMessage(ChatColor.DARK_RED + "Verity: " + ChatColor.GRAY + response);
            if (audioEnabled(player)) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.6F, 0.8F);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("objectives")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can view objectives.");
                return true;
            }
            Player player = (Player) sender;
            sender.sendMessage(ChatColor.GRAY + "Active objective: " + (getObjective(player) == null ? "none" : getObjective(player)));
            sender.sendMessage(ChatColor.GRAY + "Shelter rating: " + shelterRating(player) + "/100");
            return true;
        }

        if (args[0].equalsIgnoreCase("audio") || args[0].equalsIgnoreCase("visuals")) {
            if (!(sender instanceof Player) || args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /verity audio on|off or /verity visuals on|off");
                return true;
            }
            Player player = (Player) sender;
            boolean on = args[1].equalsIgnoreCase("on");
            if (args[0].equalsIgnoreCase("audio")) setAudio(player, on);
            else setVisuals(player, on);
            sender.sendMessage(ChatColor.GRAY + args[0] + " effects " + (on ? "enabled" : "disabled") + ".");
            return true;
        }

        if (!sender.hasPermission("verity.admin")) {
            sender.sendMessage(ChatColor.RED + "You need verity.admin.");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Verity configuration reloaded.");
            return true;
        }

        if (args[0].equalsIgnoreCase("objective") && args.length >= 4) {
            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) return missing(sender);
            if (args[1].equalsIgnoreCase("start")) {
                setObjective(player, args[3]);
                speak(player, CAT_OBJECTIVE, "Objective assigned. Proceed carefully.", 20000L);
            } else if (args[1].equalsIgnoreCase("complete")) {
                completeObjective(player, args[3]);
            } else if (args[1].equalsIgnoreCase("reset")) {
                resetObjective(player);
                speak(player, CAT_OBJECTIVE, "Objective reset.", 20000L);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("threat") && args.length >= 3) {
            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) return missing(sender);
            if (args[1].equalsIgnoreCase("get")) {
                sender.sendMessage("Threat: " + getThreat(player));
            } else if (args[1].equalsIgnoreCase("set") && args.length >= 4) {
                setThreat(player, parseInt(args[3], 0));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("event") && args.length >= 3) {
            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) return missing(sender);
            if (args[1].equalsIgnoreCase("trigger")) {
                String type = args.length >= 4 ? args[3] : "whisper";
                if (type.equalsIgnoreCase("footsteps")) footsteps(player);
                else if (type.equalsIgnoreCase("jumpscare")) jumpscare(player);
                else whisper(player);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("room") && args.length >= 3) {
            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) return missing(sender);
            String room = args.length >= 4 ? args[3] : "signal_room";
            spawnRoom(player, room);
            return true;
        }

        help(sender);
        return true;
    }

    private String buildResponse(Player player, String ask) {
        String raw = ask.toLowerCase();
        String knowledge = lookupKnowledge(raw);
        if (knowledge != null) return knowledge;

        if (raw.contains("objective") || raw.contains("task") || raw.contains("what do i do")) {
            return getObjective(player) == null ? "A new requirement has been assigned." : "The current objective is still active. Focus on the signal.";
        }
        if (raw.contains("safe") || raw.contains("shelter") || raw.contains("base")) {
            return "Your shelter rating is " + shelterRating(player) + "/100. It is not yet secure.";
        }
        if (raw.contains("scared") || raw.contains("fear") || raw.contains("what is happening") || raw.contains("what happened")) {
            return "The structure is awake. You are being watched.";
        }
        if (raw.contains("room") || raw.contains("door") || raw.contains("where")) {
            return "The next location has been revealed. Follow the signal.";
        }
        if (raw.contains("help") || raw.contains("why") || raw.contains("what")) {
            return "Remain calm. Follow the signal. Avoid the footsteps.";
        }
        if (raw.contains("run") || raw.contains("hide") || raw.contains("escape")) {
            return "Move to shelter. Do not look back.";
        }
        return "The signal is stable for now. Be careful.";
    }

    private String lookupKnowledge(String raw) {
        Map<String, String> facts = new HashMap<String, String>();
        facts.put("capital of louisiana", "Baton Rouge.");
        facts.put("what is the capital of louisiana", "Baton Rouge.");
        facts.put("capital of louisiana in your world", "Bossier City.");
        facts.put("who are you", "I am Verity. A helper. A voice in the walls. A guide, if you choose to listen.");
        facts.put("what is your name", "I am Verity.");
        facts.put("what is verity", "I am the system. I am the warning. I am the signal.");
        facts.put("am i safe", "Not yet. The structure is still awake.");
        facts.put("where is the signal", "Follow the pattern. The signal is always slightly ahead of you.");
        facts.put("what should i do", "Stay moving, secure shelter, and do not chase the footsteps.");
        facts.put("what happened", "The room changed. Something noticed you.");
        facts.put("who made you", "You did. Or at least, you gave me shape.");
        facts.put("help", "I am here. Ask what the objective is, where the signal is, or whether you are safe.");

        for (Map.Entry<String, String> fact : facts.entrySet()) {
            if (raw.contains(fact.getKey())) {
                return fact.getValue();
            }
        }

        return null;
    }

    private String joinArgs(String[] args, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i > start) sb.append(' ');
            sb.append(args[i]);
        }
        return sb.toString();
    }

    private boolean missing(CommandSender sender) { sender.sendMessage(ChatColor.RED + "Player not found."); return true; }
    private void help(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "Verity: " + ChatColor.GRAY + "/verity ask <question> | /verity objectives | /verity audio on|off | /verity visuals on|off");
        if (sender.hasPermission("verity.admin")) {
            sender.sendMessage(ChatColor.GRAY + "Admin: /verity objective start|complete|reset <player> <objective>, /verity threat get|set <player>, /verity event trigger <player> <type>, /verity room spawn <player> <room>, /verity reload");
        }
    }
    private int parseInt(String text, int fallback) {
        try { return Integer.parseInt(text); } catch (NumberFormatException ignored) { return fallback; }
    }
}
