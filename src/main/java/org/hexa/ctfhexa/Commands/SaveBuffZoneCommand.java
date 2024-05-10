package org.hexa.ctfhexa.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.hexa.ctfhexa.CTFHexa;

public class SaveBuffZoneCommand implements CommandExecutor {
    private CTFHexa plugin;
    private int buffZoneCount = 1;

    public SaveBuffZoneCommand(CTFHexa plugin) {
        this.plugin = plugin;
        buffZoneCount = plugin.getConfig().getInt("buff-zone-count", 1);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por jugadores");
            return true;
        }

        Player player = (Player) sender;
        sender.sendMessage("Player identified: " + player.getName());
        try {
            FileConfiguration config = plugin.getConfig();
            int zoneCount = config.getInt("Buff.buff-zone-count", 1);
            String path = "Buff." + zoneCount;
            config.set(path + ".x", player.getLocation().getX());
            config.set(path + ".y", player.getLocation().getY());
            config.set(path + ".z", player.getLocation().getZ());
            config.set("Buff.buff-zone-count", ++zoneCount);
            plugin.saveConfig();
            sender.sendMessage("Buff zone " + zoneCount + " saved.");
        } catch (Exception e) {
            sender.sendMessage("Error saving buff zone: " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }

}
