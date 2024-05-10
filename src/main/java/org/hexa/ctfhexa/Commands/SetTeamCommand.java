package org.hexa.ctfhexa.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class SetTeamCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Uso: /setteam <Jugador> <Equipo>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "¡Jugador no encontrado!");
            return true;
        }

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(args[1]);
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "Equipo no encontrado!");
            return true;
        }

        team.addEntry(target.getName());
        sender.sendMessage(ChatColor.GREEN + "El equipo de " + target.getName() + " se ha establecido como " + team.getDisplayName() + ".");
        setPlayerTeamColor(target, team.getColor());

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            return playerNames;
        } else if (args.length == 2) {
            return Arrays.asList("red", "blue");
        }
        return null;
    }
    private void setPlayerTeamColor(Player player, ChatColor color) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getEntryTeam(player.getName());
        if (team != null) {
            team.setColor(color);
            for (String entry : team.getEntries()) {
                Player teamPlayer = Bukkit.getPlayer(entry);
                if (teamPlayer != null) {
                    teamPlayer.setPlayerListName(color + teamPlayer.getName());
                }
            }
        }
    }
}
