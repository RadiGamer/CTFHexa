package org.hexa.ctfhexa.Commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.hexa.ctfhexa.CTFHexa;

import java.util.Random;

public class BuffCommand implements CommandExecutor {
    private CTFHexa plugin;
    private Random random;

    public BuffCommand(CTFHexa plugin){
        this.plugin = plugin;
        random = new Random();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();

        int chosenZone = random.nextInt(4) + 1;
        double x = config.getDouble("Buff." + chosenZone + ".x");
        double y = config.getDouble("Buff." + chosenZone + ".y");
        double z = config.getDouble("Buff." + chosenZone + ".z");

        int customModelData = 9;
        Player player = (Player) sender;

        ItemStack flag = new ItemStack(Material.STICK);
        ItemMeta meta = flag.getItemMeta();
        meta.setCustomModelData(customModelData);
        flag.setItemMeta(meta);

        Location buffLocation = new Location(player.getWorld(), x, y, z);

        Interaction interaction = player.getWorld().spawn(buffLocation, Interaction.class);
        interaction.setInteractionHeight(2f);
        interaction.setInteractionWidth(1f);
        interaction.addScoreboardTag("BuffBox");

        Location spawnLocation = buffLocation.clone();
        spawnLocation.setY(spawnLocation.getY() + 1);

        ItemDisplay itemDisplay = player.getWorld().spawn(spawnLocation, ItemDisplay.class);
        itemDisplay.setItemStack(flag);

        Transformation st = itemDisplay.getTransformation();
        st.getScale().set(1f, 2f, 1f);

        for (Player players : Bukkit.getOnlinePlayers()){
            players.playSound(players.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
        }
        Bukkit.broadcastMessage(
                ChatColor.YELLOW + " Ha aparecido un potenciador en: " + ChatColor.GREEN + (int) x +
                ChatColor.YELLOW + ", " + ChatColor.GREEN + (int) y +
                ChatColor.YELLOW + ", " + ChatColor.GREEN + (int) z);


        sender.sendMessage("Buff spawneado en: " + chosenZone + " at (" + x + ", " + y + ", " + z + ")");
        return true;
    }
}
