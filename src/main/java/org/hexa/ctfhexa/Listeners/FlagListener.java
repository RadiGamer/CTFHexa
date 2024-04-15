package org.hexa.ctfhexa.Listeners;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.hexa.ctfhexa.CTFHexa;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlagListener implements Listener {

    private final CTFHexa plugin;
    private Map<UUID, ItemStack> originalItems = new HashMap<>();

    public FlagListener(CTFHexa plugin) {
        this.plugin = plugin;
    }

    private ItemDisplay findItemDisplay(Interaction interaction) {
        for (Entity entity : interaction.getNearbyEntities(1.0, 1.0, 1.0)) {
            if (entity instanceof ItemDisplay) {
                return (ItemDisplay) entity;
            }
        }
        return null;
    }

    private void applyFlagEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, -2, false, false));
        player.setGlowing(true);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Interaction) {
            Interaction interaction = (Interaction) event.getRightClicked();
            Player player = event.getPlayer();

            ItemDisplay itemDisplay = findItemDisplay(interaction);
            if (itemDisplay != null) {
                applyFlagEffects(player);
                player.addPassenger(itemDisplay);
                interaction.remove();
                player.sendActionBar(ChatColor.RED + "No puedes usar tus objetos");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (player.getPassengers().stream().anyMatch(e -> e instanceof ItemDisplay)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getPassengers().stream().anyMatch(e -> e instanceof ItemDisplay)) {
            event.setCancelled(true);
            player.sendActionBar(ChatColor.RED + "No puedes usar tus objetos");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        player.getPassengers().stream()
                .filter(e -> e instanceof ItemDisplay)
                .findFirst()
                .ifPresent(e -> {
                    dropFlagOnDeath(player, (ItemDisplay) e);
                    e.remove();
                });
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking() && player.getPassengers().stream().anyMatch(e -> e instanceof ItemDisplay)) {
            ItemDisplay itemDisplay = (ItemDisplay) player.getPassengers().stream()
                    .filter(e -> e instanceof ItemDisplay)
                    .findFirst().orElse(null);
            if (itemDisplay != null) {
                player.removePassenger(itemDisplay);
                removeFlagEffects(player);
                dropFlagOnDeath(player, itemDisplay);
                itemDisplay.remove();
            }
        }
    }
    private void removeFlagEffects(Player player) {
        player.removePotionEffect(PotionEffectType.GLOWING);
        player.removePotionEffect(PotionEffectType.SLOW);
        player.setGlowing(false);
    }

    private void dropFlagOnDeath(Player player, ItemDisplay itemDisplay) {
        ItemStack flag = itemDisplay.getItemStack().clone();
        Interaction newInteraction = (Interaction) player.getWorld().spawnEntity(player.getLocation(), EntityType.INTERACTION);
        newInteraction.setInteractionHeight(2f);
        newInteraction.setInteractionWidth(1f);
        itemDisplay.getScoreboardTags().forEach(newInteraction::addScoreboardTag);

        ItemDisplay newItemDisplay = (ItemDisplay) player.getWorld().spawnEntity(player.getLocation(), EntityType.ITEM_DISPLAY);
        newItemDisplay.setItemStack(flag);
        newItemDisplay.setGlowColorOverride(itemDisplay.getGlowColorOverride());
    }
}