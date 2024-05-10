package org.hexa.ctfhexa.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.hexa.ctfhexa.CTFHexa;

public class ResetTeamCommand implements CommandExecutor {

    private CTFHexa plugin;
    public ResetTeamCommand( CTFHexa plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team blueTeam = scoreboard.getTeam("blue");
        Team redTeam = scoreboard.getTeam("red");

        if (blueTeam == null || redTeam == null) {
            sender.sendMessage(ChatColor.RED + "No se encontraron los equipos.");
            return true;
        }

        resetTeam(blueTeam);
        resetTeam(redTeam);

        sender.sendMessage(ChatColor.GREEN + "Los equipos han sido reiniciados. Todos los jugadores han sido retirados de los equipos.");

        return true;
    }
    private void resetTeam(Team team) {
        team.setColor(ChatColor.WHITE);
        team.getEntries().forEach(entry -> {
            team.removeEntry(entry);
            Player player = Bukkit.getPlayer(entry);
            if (player != null) {
                player.setPlayerListName(player.getName());
                player.setBedSpawnLocation(null, true);
            }
        });
    }
}
