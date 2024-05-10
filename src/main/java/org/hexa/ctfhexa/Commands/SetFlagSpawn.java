package org.hexa.ctfhexa.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.hexa.ctfhexa.CTFHexa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SetFlagSpawn implements CommandExecutor, TabCompleter {
    private CTFHexa plugin;

    public SetFlagSpawn(CTFHexa plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Solo puede ser ejecutado por un jugador.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Uso: /setflagspawn <red|blue>");
            return true;
        }

        Player player = (Player) sender;
        Location loc = player.getLocation();

        switch (args[0].toLowerCase()) {
            case "red":
                saveFlagLocation("Red", loc);
                player.sendMessage(ChatColor.RED + "Bandera roja guardada!");
                break;
            case "blue":
                saveFlagLocation("Blue", loc);
                player.sendMessage(ChatColor.BLUE + "Bandera azul guardada!");
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Uso: /setflagspawn <red|blue>");
                break;
        }
        return true;
    }

    private void saveFlagLocation(String team, Location location) {
        ConfigurationSection flagLocationSection = plugin.getConfig().getConfigurationSection("FlagLocation");
        if (flagLocationSection == null) {
            flagLocationSection = plugin.getConfig().createSection("FlagLocation");
        }

        try {
            int newId = 1;
            for (String key : flagLocationSection.getKeys(false)) {
                int currentId = Integer.parseInt(key);
                if (currentId >= newId) {
                    newId = currentId + 1;
                }
            }

            String path = "FlagLocation." + newId;
            plugin.getConfig().set(path + ".team", team.toLowerCase());
            plugin.getConfig().set(path + ".X", location.getBlockX());
            plugin.getConfig().set(path + ".Y", location.getBlockY());
            plugin.getConfig().set(path + ".Z", location.getBlockZ());

            plugin.saveConfig();
        } catch (NumberFormatException e) {
            plugin.getLogger().severe("Error en el parser a int.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            List<String> commands = Arrays.asList("red", "blue");
            StringUtil.copyPartialMatches(args[0], commands, completions);
            Collections.sort(completions);
            return completions;
        }
        return null;
    }
}
