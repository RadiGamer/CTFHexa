package org.hexa.ctfhexa.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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

        //TODO aqui es donde se spawnea el item

        int chosenZone = random.nextInt(4) + 1;
        double x = config.getDouble("Buff." + chosenZone + ".x");
        double y = config.getDouble("Buff." + chosenZone + ".y");
        double z = config.getDouble("Buff." + chosenZone + ".z");

        sender.sendMessage("Chosen Buff Zone: " + chosenZone + " at (" + x + ", " + y + ", " + z + ")");
        return true;
    }
}
