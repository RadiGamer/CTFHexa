package org.hexa.ctfhexa.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.hexa.ctfhexa.CTFHexa;

import java.util.ArrayList;
import java.util.List;

public class SetSpawnPointCommand implements CommandExecutor, TabCompleter {

    private FileConfiguration config;
    private CTFHexa plugin;

    public SetSpawnPointCommand(FileConfiguration config, CTFHexa plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Uso: /setspawn <blue|red>");
            return true;
        }

        String team = args[0].toLowerCase();
        if (!team.equals("blue") && !team.equals("red")) {
            sender.sendMessage(ChatColor.RED + "Equipo no válido. Use 'blue' o 'red'.");
            return true;
        }

        Player player = (Player) sender;
        config.set(team + "Spawn", player.getLocation());
        plugin.saveConfig();

        sender.sendMessage(ChatColor.GREEN + "Punto de spawn para el equipo " + team + " guardado.");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("blue");
            completions.add("red");
        }

        return completions;
    }
}
