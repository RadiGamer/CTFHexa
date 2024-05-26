package org.hexa.ctfhexa.Listeners;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.hexa.ctfhexa.CTFHexa;
import org.hexa.ctfhexa.Commands.BossBarCommand;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {
    private CTFHexa plugin;
    private BossBarCommand bossBarCommand;

    public PlayerListener(CTFHexa plugin, BossBarCommand bossBarCommand) {
        this.plugin = plugin;
        this.bossBarCommand = bossBarCommand;
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

                if ((blockBelow == Material.BLUE_TERRACOTTA && isPlayerInTeam(player, "red")) ||
                        (blockBelow == Material.RED_TERRACOTTA && isPlayerInTeam(player, "blue"))) {
                    player.sendMessage(ChatColor.RED + "No puedes capturar tu propia bandera");
                    return;
                }

                if (hasBlueFlag) {
                    Bukkit.getServer().broadcastMessage(player.getName() + ChatColor.YELLOW + " Ha capturado la bandera del equipo "+ ChatColor.BLUE + "azul");

                    spawnFirework(loc, Color.BLUE);
                    removeAllPassengers(player);
                    restorePlayerState(player);
                    bossBarCommand.addPointsToBlue();
                    for (Player players : Bukkit.getOnlinePlayers()){
                        players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 10, 1);
                    }

                } else if (hasRedFlag) {
                    Bukkit.getServer().broadcastMessage(player.getName() + ChatColor.YELLOW + " Ha capturado la bandera del equipo "+ ChatColor.RED + "rojo");
                    spawnFirework(loc, Color.RED);
                    removeAllPassengers(player);
                    restorePlayerState(player);
                    bossBarCommand.addPointsToRed();
                    for (Player players : Bukkit.getOnlinePlayers()){
                        players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 10, 1);
                    }
                }
            }
        }
    }
    private boolean isPlayerInTeam(Player player, String teamName) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);
        return team != null && team.hasEntry(player.getName());
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
    @EventHandler
    public void onPlayerMoveBuffBox(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        List<Entity> nearbyEntities = player.getNearbyEntities(1.0, 1.0, 1.0);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Interaction) {
                Interaction interaction = (Interaction) entity;

                if (interaction.getScoreboardTags().contains("BuffBox")) {
                    for (PotionEffectType type : PotionEffectType.values()) {
                        player.removePotionEffect(type);
                    }
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 60, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 60, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 60, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 60, 0));

                    for (Player players : Bukkit.getOnlinePlayers()){
                        players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 10, 1);
                    }

                    Bukkit.broadcastMessage(ChatColor.YELLOW + "¡" + player.getDisplayName() + ChatColor.YELLOW + " ha recibido un potenciador por un minuto!");

                    List<Entity> allNearbyEntities = interaction.getNearbyEntities(2.0, 2.0, 2.0);
                    for (Entity nearby : allNearbyEntities) {
                        if (nearby instanceof ItemDisplay) {
                            nearby.remove();
                        }
                    }

                    interaction.remove();
                    break;
                }
            }
        }
    }
}