package org.hexa.ctfhexa.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.hexa.ctfhexa.CTFHexa;

public class SaveBuffZoneCommand implements CommandExecutor {
    private CTFHexa plugin;
    private int buffZoneCount = 1;

    public SaveBuffZoneCommand(CTFHexa plugin) {

        buffZoneCount = plugin.getConfig().getInt("buff-zone-count", 1);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("No puedes ejecutar este comando desde la consola");
            return true;
        }

        Player player = (Player) sender;
        FileConfiguration config = plugin.getConfig();
        String basePath = "Buff." + buffZoneCount;
        config.set(basePath + ".x", player.getLocation().getX());
        config.set(basePath + ".y", player.getLocation().getY());
        config.set(basePath + ".z", player.getLocation().getZ());
        buffZoneCount++;
        config.set("Buff.buff-zone-count", buffZoneCount);

        plugin.saveConfig();
        player.sendMessage("Buff zone saved as " + buffZoneCount);

        return true;
    }
}
