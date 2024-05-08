package org.hexa.ctfhexa.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.hexa.ctfhexa.CTFHexa;

public class TeleportTeamCommand implements CommandExecutor {

    private FileConfiguration config;
    private CTFHexa plugin;

    public TeleportTeamCommand(FileConfiguration config, CTFHexa plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Team team = scoreboard.getPlayerTeam(player);

            if (team == null) {
                sender.sendMessage(ChatColor.RED + "El jugador " + player.getName() + " no está en ningún equipo.");
                continue;
            }

            Location spawnLocation = null;
            if (team.getName().equalsIgnoreCase("blue")) {
                spawnLocation = (Location) config.get("blueSpawn");
            } else if (team.getName().equalsIgnoreCase("red")) {
                spawnLocation = (Location) config.get("redSpawn");
            }

            if (spawnLocation == null) {
                sender.sendMessage(ChatColor.RED + "No se encontró un punto de spawn para el equipo " + team.getName() + ".");
                continue;
            }

            player.teleport(spawnLocation);
            sender.sendMessage(ChatColor.GREEN + "El jugador " + player.getName() + " del equipo " + team.getName() + " ha sido teletransportado a su punto de spawn.");
        //ESE SENDER ES DE DEBUG
        }

        return true;
    }
}
