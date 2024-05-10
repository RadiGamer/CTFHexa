package org.hexa.ctfhexa.Commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DivideCommand implements CommandExecutor {

    Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    Team blueTeam = scoreboard.getTeam("blue");
    Team redTeam = scoreboard.getTeam("red");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        dividePlayers();

        sender.sendMessage(ChatColor.GREEN + "Todos los jugadores han sido divididos en equipos.");
        sender.sendMessage(ChatColor.YELLOW + "Si falta algun jugador utiliza /setteam <Jugador> <Equipo>");

        return true;
    }

    private void dividePlayers() {
        List<Player> eligiblePlayers = Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)
                .collect(Collectors.toList());

        Collections.shuffle(eligiblePlayers);

        int midIndex = eligiblePlayers.size() / 2;

        for (int i = 0; i < eligiblePlayers.size(); i++) {
            Player player = eligiblePlayers.get(i);
            org.bukkit.scoreboard.Team team = i < midIndex ? blueTeam : redTeam;
            team.addEntry(player.getName());
            ChatColor teamColor = team.getColor();
            player.sendActionBar(teamColor + "Te has unido al equipo " + team.getName() + ".");
            setPlayerTeamColor(player, teamColor);
        }
    }
    private void setPlayerTeamColor(Player player, ChatColor color) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getEntryTeam(player.getName());
        if (team != null) {
            team.setColor(color);
            for (String entry : team.getEntries()) {
                Player teamPlayer = Bukkit.getPlayer(entry);
                if (teamPlayer != null) {
                    teamPlayer.setDisplayName(color + teamPlayer.getName());
                    teamPlayer.setPlayerListName(color + teamPlayer.getName());
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        onlinePlayer.hidePlayer(teamPlayer);
                        onlinePlayer.showPlayer(teamPlayer);
                    }
                }
            }
        }
    }
}
