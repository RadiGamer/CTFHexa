package org.hexa.ctfhexa.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.hexa.ctfhexa.CTFHexa;

import java.util.ArrayList;
import java.util.List;

public class FlagOnLocationCommand implements CommandExecutor, TabCompleter {

    private CTFHexa plugin;

    public FlagOnLocationCommand(CTFHexa plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando solo lo puede usar los jugadores crack");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Especifica un color (rojo o azul).");
            return false;
        }

        boolean isBlue = args[0].equalsIgnoreCase("azul");
        int customModelData = isBlue ? 3 : 4;
        Color flagColor = isBlue ? Color.BLUE : Color.RED;

        ItemStack flag = new ItemStack(Material.STICK);
        ItemMeta meta = flag.getItemMeta();
        meta.setCustomModelData(customModelData);
        flag.setItemMeta(meta);

        Interaction interaction = (Interaction) player.getWorld().spawn(player.getLocation(), Interaction.class);
        interaction.setInteractionHeight(2f);
        interaction.setInteractionWidth(1f);
        interaction.addScoreboardTag(isBlue ? "BlueFlag" : "RedFlag");

        ItemDisplay itemDisplay = (ItemDisplay) player.getWorld().spawn(player.getLocation(), ItemDisplay.class);
        itemDisplay.setItemStack(flag);
        itemDisplay.setGlowColorOverride(flagColor);

        player.sendMessage(ChatColor.GREEN + "La bandera ha sido spawneada en tu ubicacion.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> colors = new ArrayList<>();
            colors.add("rojo");
            colors.add("azul");
            return colors;
        }
        return new ArrayList<>();
    }
}