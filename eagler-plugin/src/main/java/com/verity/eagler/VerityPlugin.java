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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public final class VerityPlugin extends JavaPlugin implements CommandExecutor {
    private final Map<UUID,Integer> threat = new HashMap<UUID,Integer>();
    private final Map<UUID,Long> nextEvent = new HashMap<UUID,Long>();
    private final Map<UUID,Long> nextJumpscare = new HashMap<UUID,Long>();
    private final Map<UUID,String> objective = new HashMap<UUID,String>();
    private final Map<UUID,Boolean> audio = new HashMap<UUID,Boolean>();
    private final Map<UUID,Boolean> visuals = new HashMap<UUID,Boolean>();
    private final Random random = new Random();

    @Override public void onEnable() {
        saveDefaultConfig(); saveResource("objectives.yml", false);
        getCommand("verity").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(new VerityListener(this), this);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable(){ public void run(){ evaluatePlayers(); }}, 40L, 20L);
        getLogger().info("Verity Eaglercraft plugin enabled.");
    }
    private void ensure(Player p){ UUID id=p.getUniqueId(); if(!threat.containsKey(id)) threat.put(id,0); if(!audio.containsKey(id)) audio.put(id,getConfig().getBoolean("accessibility.loud-audio",true)); if(!visuals.containsKey(id)) visuals.put(id,getConfig().getBoolean("accessibility.visual-effects",true)); }
    public int getThreat(Player p){ ensure(p); return threat.get(p.getUniqueId()); }
    public void setThreat(Player p,int n){ ensure(p); threat.put(p.getUniqueId(),Math.max(0,Math.min(getConfig().getInt("max-threat",5),n))); }
    public void addThreat(Player p,int n){ setThreat(p,getThreat(p)+n); }
    private boolean audio(Player p){ ensure(p); return audio.get(p.getUniqueId()); }
    private boolean visuals(Player p){ ensure(p); return visuals.get(p.getUniqueId()); }
    private boolean night(World w){ long t=w.getTime()%24000L; return t>=13000L&&t<=23000L; }
    private boolean ready(Map<UUID,Long> map,UUID id){ return System.currentTimeMillis()>=(map.containsKey(id)?map.get(id):0L); }
    public void evaluatePlayers(){ if(!getConfig().getBoolean("enabled",true)) return; for(final Player p:Bukkit.getOnlinePlayers()){ ensure(p); if(p.isDead()||p.isSleeping()||!ready(nextEvent,p.getUniqueId())) continue; double chance=getConfig().getDouble("random-event-chance",.08D)/20D*(1D+getThreat(p)*.2D); if(night(p.getWorld())) chance*=1.75D; if(random.nextDouble()>chance) continue; nextEvent.put(p.getUniqueId(),System.currentTimeMillis()+getConfig().getLong("minimum-event-interval-seconds",45L)*1000L); if(random.nextInt(100)<55) whisper(p); else footsteps(p); if(getThreat(p)>=3&&ready(nextJumpscare,p.getUniqueId())&&random.nextInt(100)<12) jumpscare(p); }}
    private void play(Player p,Sound s,float v,float pitch){ if(audio(p)) p.playSound(p.getLocation(),s,v,pitch); }
    public void whisper(Player p){ addThreat(p,1); play(p,Sound.ENDERMAN_STARE,.45F,.55F+random.nextFloat()*.25F); p.sendMessage(ChatColor.DARK_RED+"Something whispered your name."); }
    public void footsteps(final Player p){ addThreat(p,1); final Location at=p.getLocation().clone().subtract(p.getLocation().getDirection().normalize().multiply(8D)); if(audio(p)) p.playSound(at,Sound.STEP_WOOD,.75F,.65F+random.nextFloat()*.25F); Bukkit.getScheduler().runTaskLater(this,new Runnable(){ public void run(){ if(p.isOnline()&&audio(p)) p.playSound(at,Sound.STEP_WOOD,.55F,.55F); }},10L+random.nextInt(15)); p.sendMessage(ChatColor.GRAY+"Footsteps stop somewhere behind you."); }
    public void jumpscare(Player p){ nextJumpscare.put(p.getUniqueId(),System.currentTimeMillis()+getConfig().getLong("jumpscare-cooldown-seconds",180L)*1000L); addThreat(p,1); play(p,Sound.CREEPER_PRIMED,.85F,.65F); if(visuals(p)) p.sendMessage(ChatColor.DARK_RED+"RUN"+ChatColor.GRAY+" — Verity has noticed you."); }
    public void spawnRoom(Player p,String id){ addThreat(p,1); p.sendMessage(ChatColor.DARK_RED+"The room has changed: "+id); p.sendMessage(ChatColor.GRAY+"The structure is awake."); whisper(p); }
    public int shelter(Player p){ Location l=p.getLocation(); int solid=0,light=0; for(int x=-3;x<=3;x++)for(int y=0;y<=3;y++)for(int z=-3;z<=3;z++){ org.bukkit.Material m=l.getWorld().getBlockAt(l.getBlockX()+x,l.getBlockY()+y,l.getBlockZ()+z).getType(); if(m!=org.bukkit.Material.AIR)solid++; if(m==org.bukkit.Material.TORCH||m==org.bukkit.Material.GLOWSTONE||m==org.bukkit.Material.JACK_O_LANTERN)light++; } return Math.max(0,Math.min(100,Math.min(45,solid/8)+Math.min(25,light*5)+(l.getBlock().getType()==org.bukkit.Material.BED_BLOCK?15:0))); }
    private Player player(CommandSender s,String name){ Player p=Bukkit.getPlayer(name); if(p==null)s.sendMessage(ChatColor.RED+"Player not found."); return p; }
    private int num(String s){ try{return Integer.parseInt(s);}catch(Exception e){return 0;} }
    private void help(CommandSender s){ s.sendMessage(ChatColor.DARK_RED+"Verity: "+ChatColor.GRAY+"/verity objectives | audio on|off | visuals on|off"); if(s.hasPermission("verity.admin"))s.sendMessage(ChatColor.GRAY+"Admin: /verity objective start|complete|reset <player> <id>, threat get|set <player> [level], event trigger <player> <type>, room spawn <player> <id>, reload"); }
    @Override public boolean onCommand(CommandSender s,Command c,String label,String[] a){ if(a.length==0||a[0].equalsIgnoreCase("help")){help(s);return true;} if(a[0].equalsIgnoreCase("objectives")){ if(!(s instanceof Player)){s.sendMessage("Players only.");return true;} Player p=(Player)s; s.sendMessage(ChatColor.GRAY+"Active objective: "+(objective.containsKey(p.getUniqueId())?objective.get(p.getUniqueId()):"none")); s.sendMessage(ChatColor.GRAY+"Shelter rating: "+shelter(p)+"/100"); return true;} if((a[0].equalsIgnoreCase("audio")||a[0].equalsIgnoreCase("visuals"))&&s instanceof Player&&a.length>1){boolean on=a[1].equalsIgnoreCase("on"); if(a[0].equalsIgnoreCase("audio"))audio.put(((Player)s).getUniqueId(),on);else visuals.put(((Player)s).getUniqueId(),on);s.sendMessage(ChatColor.GRAY+a[0]+" effects "+(on?"enabled":"disabled")+".");return true;} if(!s.hasPermission("verity.admin")){s.sendMessage(ChatColor.RED+"You need verity.admin.");return true;} if(a[0].equalsIgnoreCase("reload")){reloadConfig();s.sendMessage(ChatColor.GREEN+"Verity configuration reloaded.");return true;} if(a.length>=4&&a[0].equalsIgnoreCase("objective")){Player p=player(s,a[2]);if(p==null)return true;String id=a[3];if(a[1].equalsIgnoreCase("start")){objective.put(p.getUniqueId(),id);p.sendMessage(ChatColor.RED+"Objective assigned: "+id);}else if(a[1].equalsIgnoreCase("complete")){if(id.equals(objective.get(p.getUniqueId())))objective.remove(p.getUniqueId());p.sendMessage(ChatColor.GREEN+"Objective complete: "+id);}else if(a[1].equalsIgnoreCase("reset"))objective.remove(p.getUniqueId());return true;} if(a.length>=3&&a[0].equalsIgnoreCase("threat")){Player p=player(s,a[2]);if(p==null)return true;if(a[1].equalsIgnoreCase("get"))s.sendMessage("Threat: "+getThreat(p));else if(a[1].equalsIgnoreCase("set")&&a.length>=4)setThreat(p,num(a[3]));return true;} if(a.length>=3&&a[0].equalsIgnoreCase("event")){Player p=player(s,a[2]);if(p==null)return true;String type=a.length>=4?a[3]:"whisper";if(type.equalsIgnoreCase("footsteps"))footsteps(p);else if(type.equalsIgnoreCase("jumpscare"))jumpscare(p);else whisper(p);return true;} if(a.length>=3&&a[0].equalsIgnoreCase("room")){Player p=player(s,a[2]);if(p!=null)spawnRoom(p,a.length>=4?a[3]:"signal_room");return true;} help(s);return true; }
}
