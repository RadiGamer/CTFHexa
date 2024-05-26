package org.hexa.ctfhexa.Listeners;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.hexa.ctfhexa.CTFHexa;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

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
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "playglow " + player.getName() + " GREEN 2 0.8 0.8");    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Interaction) {
            Interaction interaction = (Interaction) event.getRightClicked();

            if (interaction.getScoreboardTags().contains("BlueFlag") || interaction.getScoreboardTags().contains("RedFlag")) {
                Player player = event.getPlayer();

                if(interaction.getScoreboardTags().contains("BlueFlag")){
                    Bukkit.broadcastMessage(player.getName() + ChatColor.YELLOW + " Ha agarrado la bandera del equipo "+ChatColor.BLUE+"azul");
                    for (Player players : Bukkit.getOnlinePlayers()){
                        players.playSound(players.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER, 10, 1);
                    }
                }
                if(interaction.getScoreboardTags().contains("RedFlag")){
                    Bukkit.broadcastMessage(player.getName() + ChatColor.YELLOW + " Ha agarrado la bandera del equipo "+ChatColor.RED+"rojo");
                    for (Player players : Bukkit.getOnlinePlayers()){
                        players.playSound(players.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.MASTER, 10, 1);
                    }
                }

                ItemDisplay itemDisplay = findItemDisplay(interaction);
                if (itemDisplay != null) {
                    itemDisplay.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(1f), new AxisAngle4f()));
                    applyFlagEffects(player);
                    player.addPassenger(itemDisplay); //TODO CHECAR ESTO
                    interaction.remove();
                    player.sendActionBar(ChatColor.RED + "No puedes usar tus objetos");
                    event.setCancelled(true);
                }
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
        for (PotionEffectType type : PotionEffectType.values()) {
            player.setGlowing(false);
            player.removePotionEffect(type);
        }
    }

    private void dropFlagOnDeath(Player player, ItemDisplay itemDisplay) {
        //TODO MODIFICAR TAMAñO
        ItemStack flag = itemDisplay.getItemStack().clone();

        ItemMeta meta = flag.getItemMeta();
        int customModelData = meta.getCustomModelData();

        Location spawnLocation = player.getLocation().clone().add(0, 1.5, 0);

        Interaction newInteraction = (Interaction) player.getWorld().spawnEntity(spawnLocation, EntityType.INTERACTION);
        newInteraction.setInteractionHeight(4f);
        newInteraction.setInteractionWidth(4f);
        newInteraction.addScoreboardTag("Flag");

        if (customModelData == 7) {
            newInteraction.addScoreboardTag("BlueFlag");
            Bukkit.broadcastMessage(player.getName() + ChatColor.YELLOW + " Ha soltado la bandera del equipo " + ChatColor.BLUE + "azul");
        } else if (customModelData == 8) {
            newInteraction.addScoreboardTag("RedFlag");
            Bukkit.broadcastMessage(player.getName() + ChatColor.YELLOW + " Ha soltado la bandera del equipo " + ChatColor.RED + "rojo");
        }
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "playglow " + player.getName() + " RED 0.5 0.8 0.8");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.MASTER, 10,0);
        player.sendMessage(ChatColor.RED + "Has soltado la bandera!");

        for (Player players : Bukkit.getOnlinePlayers()){
            players.playSound(players.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 10, 1);
        }

        spawnLocation.add(0, 2, 0);


        ItemDisplay newItemDisplay = (ItemDisplay) player.getWorld().spawnEntity(spawnLocation, EntityType.ITEM_DISPLAY);
        newItemDisplay.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(2f), new AxisAngle4f()));
        newItemDisplay.setItemStack(flag);
        newItemDisplay.setGlowing(true);
        newItemDisplay.addScoreboardTag("Flag");

        newItemDisplay.setGlowColorOverride(itemDisplay.getGlowColorOverride());
    }
}