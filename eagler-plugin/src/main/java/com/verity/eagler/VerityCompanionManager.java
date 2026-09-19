package com.verity.eagler;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Companion implementation designed for the old Bukkit API used by Eaglercraft servers. */
public final class VerityCompanionManager implements Listener {
    private static final String BOX_NAME = ChatColor.DARK_RED + "Verity's Sealed Box";
    private static final String COMPANION_META = "verity_companion";
    private final VerityPlugin plugin;
    private final Map<UUID, UUID> companions = new HashMap<UUID, UUID>();
    private final Map<UUID, String> pendingBuilds = new HashMap<UUID, String>();

    public VerityCompanionManager(VerityPlugin plugin) {
        this.plugin = plugin;
    }

    public void giveStarterBox(Player player) {
        if (hasBox(player) || hasCompanion(player)) return;
        ItemStack box = new ItemStack(Material.CHEST, 1);
        ItemMeta meta = box.getItemMeta();
        meta.setDisplayName(BOX_NAME);
        meta.setLore(java.util.Arrays.asList(ChatColor.GRAY + "Right-click to release Verity.", ChatColor.DARK_GRAY + "The seal is warm."));
        box.setItemMeta(meta);
        player.getInventory().addItem(box);
        player.sendMessage(ChatColor.DARK_RED + "A sealed box has appeared in your inventory.");
        player.sendMessage(ChatColor.GRAY + "Right-click it to meet Verity.");
    }

    private boolean hasBox(Player player) {
        for (ItemStack stack : player.getInventory().getContents()) {
            if (isBox(stack)) return true;
        }
        return false;
    }

    private boolean isBox(ItemStack stack) {
        if (stack == null || stack.getType() != Material.CHEST || !stack.hasItemMeta()) return false;
        return BOX_NAME.equals(stack.getItemMeta().getDisplayName());
    }

    public boolean hasCompanion(Player player) {
        UUID entityId = companions.get(player.getUniqueId());
        if (entityId == null) return false;
        Entity entity = plugin.getServer().getEntity(entityId);
        return entity != null && !entity.isDead();
    }

    public void release(Player player) {
        if (hasCompanion(player)) {
            say(player, "I am already here. You do not need another box.");
            return;
        }
        removeOneBox(player);
        Location location = player.getLocation().clone().add(player.getLocation().getDirection().normalize().multiply(2.0D));
        ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCustomName(ChatColor.DARK_RED + "Verity" + ChatColor.GRAY + " [helper]");
        stand.setCustomNameVisible(true);
        stand.setMetadata(COMPANION_META, new FixedMetadataValue(plugin, player.getUniqueId().toString()));
        companions.put(player.getUniqueId(), stand.getUniqueId());
        player.playSound(location, Sound.ORB_PICKUP, 0.8F, 0.7F);
        say(player, "Hey. I am Verity, your helper. Friend is a word humans use quickly, but I will try.");
        say(player, "You can ask me questions in chat with /verity ask <question>.");
    }

    private void removeOneBox(Player player) {
        ItemStack hand = player.getItemInHand();
        if (isBox(hand)) {
            if (hand.getAmount() <= 1) player.setItemInHand(null);
            else hand.setAmount(hand.getAmount() - 1);
            return;
        }
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (isBox(stack)) {
                if (stack.getAmount() <= 1) player.getInventory().setItem(i, null);
                else stack.setAmount(stack.getAmount() - 1);
                return;
            }
        }
    }

    public void packUp(Player player) {
        UUID id = companions.remove(player.getUniqueId());
        if (id == null) { say(player, "I am not currently placed."); return; }
        Entity entity = plugin.getServer().getEntity(id);
        if (entity != null) entity.remove();
        ItemStack box = new ItemStack(Material.CHEST, 1);
        ItemMeta meta = box.getItemMeta();
        meta.setDisplayName(BOX_NAME);
        meta.setLore(java.util.Arrays.asList(ChatColor.GRAY + "Right-click to release Verity."));
        box.setItemMeta(meta);
        player.getInventory().addItem(box);
        say(player, "Packing me away. Please do not leave me in the dark.");
    }

    public void requestBuild(Player player, String link) {
        if (!isYoutube(link)) {
            say(player, "That is not a YouTube link I can recognize. Give me a full youtube.com or youtu.be URL.");
            return;
        }
        pendingBuilds.put(player.getUniqueId(), link);
        say(player, "I received the link. I cannot watch or download YouTube from an Eaglercraft server, so I need a blueprint or block list to build it safely.");
        say(player, "For now, I marked the request. Send a schematic, a block list, or an image-based plan to implement it in-game.");
        player.sendMessage(ChatColor.GRAY + "Build request queued: " + link);
    }

    private boolean isYoutube(String link) {
        try {
            URI uri = new URI(link);
            String host = uri.getHost();
            return host != null && (host.equalsIgnoreCase("youtube.com") || host.endsWith(".youtube.com") || host.equalsIgnoreCase("youtu.be"));
        } catch (Exception ignored) { return false; }
    }

    public void say(Player player, String message) {
        player.sendMessage(ChatColor.DARK_RED + "Verity: " + ChatColor.GRAY + message);
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!entity.hasMetadata(COMPANION_META)) return;
        String owner = entity.getMetadata(COMPANION_META).get(0).asString();
        if (!event.getPlayer().getUniqueId().toString().equals(owner)) {
            event.getPlayer().sendMessage(ChatColor.GRAY + "Verity does not belong to you.");
            return;
        }
        event.setCancelled(true);
        say(event.getPlayer(), "I am listening. Ask me something, or use /verity packup.");
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity().hasMetadata(COMPANION_META)) event.setCancelled(true);
    }
}
