package org.hexa.ctfhexa.Listeners;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.hexa.ctfhexa.CTFHexa;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {
    private CTFHexa plugin;

    public PlayerListener(CTFHexa plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        Material blockBelow = loc.getBlock().getRelative(BlockFace.DOWN).getType();

        if (blockBelow == Material.RED_TERRACOTTA || blockBelow == Material.BLUE_TERRACOTTA) {
            boolean hasBlueFlag = hasFlag(player, "BlueFlag");
            boolean hasRedFlag = hasFlag(player, "RedFlag");

            if ((blockBelow == Material.RED_TERRACOTTA && hasBlueFlag) ||
                    (blockBelow == Material.BLUE_TERRACOTTA && hasRedFlag)) {
                if (hasBlueFlag) {
                    Bukkit.getServer().broadcastMessage(player.getName() + ChatColor.BLUE + " Ha capturado la bandera del equipo rojo!");
                    spawnFirework(loc, Color.BLUE);
                    removeAllPassengers(player);
                    restorePlayerState(player);
                } else if (hasRedFlag) {
                    Bukkit.getServer().broadcastMessage(player.getName() + ChatColor.RED + " Ha capturado la bandera del equipo azul!");
                    spawnFirework(loc, Color.RED);
                    removeAllPassengers(player);
                    restorePlayerState(player);
                }
            }
        }
    }

    private boolean hasFlag(Player player, String flagType) {
        return player.getPassengers().stream()
                .filter(e -> e instanceof ItemDisplay)
                .map(e -> (ItemDisplay) e)
                .anyMatch(itemDisplay -> {
                    ItemStack itemStack = itemDisplay.getItemStack();
                    if (itemStack != null && itemStack.hasItemMeta()) {
                        ItemMeta meta = itemStack.getItemMeta();
                        if (meta.hasCustomModelData()) {
                            int customModelData = meta.getCustomModelData();
                            return (flagType.equals("BlueFlag") && customModelData == 7) ||
                                    (flagType.equals("RedFlag") && customModelData == 8);
                        }
                    }
                    return false;
                });
    }

    private void restorePlayerState(Player player) {
        player.removePotionEffect(PotionEffectType.GLOWING);
        player.removePotionEffect(PotionEffectType.SLOW);
        player.setGlowing(false);
    }

    private void removeAllPassengers(Player player) {
        List<Entity> passengers = new ArrayList<>(player.getPassengers());
        boolean flagRemoved = false;
        for (Entity entity : passengers) {
            if (entity instanceof ItemDisplay) {
                flagRemoved = true;
            }
            player.removePassenger(entity);
            entity.remove();
        }
        if (flagRemoved) {
            restorePlayerState(player);
        }
    }

    private void spawnFirework(Location location, Color color) {
        Firework fw = location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(color)
                .with(FireworkEffect.Type.BALL_LARGE)
                .build());
        meta.setPower(1);
        fw.setFireworkMeta(meta);
    }
}