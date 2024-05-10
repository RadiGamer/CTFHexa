package org.hexa.ctfhexa.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BossBarCommand implements CommandExecutor {
    private BossBar bossBar;
    private int bluePoints = 1;
    private int redPoints = 1;

    public BossBarCommand() {
        createBossBar();
    }

    private void createBossBar() {
        bossBar = Bukkit.createBossBar(".", BarColor.BLUE, BarStyle.SOLID);
        updateBossBar();
    }

    private void updateBossBar() {
        int totalPoints = bluePoints + redPoints;
        double progress = (double) bluePoints / totalPoints;
        bossBar.setProgress(progress);
    }

    public void showToAllPlayers() {
        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);
        bossBar.setVisible(true);
    }

    public void addPointsToBlue() {
        bluePoints ++;
        updateBossBar();
    }

    public void addPointsToRed() {
        redPoints ++;
        updateBossBar();
    }
    public void hideBossBar() {
        bossBar.setVisible(false);
        Bukkit.getOnlinePlayers().forEach(bossBar::removePlayer);
    }

    public void resetBossBar() {
        bluePoints = 1;
        redPoints = 1;
        updateBossBar();
        bossBar.setVisible(true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("restart")) {
            resetBossBar();
            hideBossBar();
            sender.sendMessage(ChatColor.YELLOW+ "Bossbar reiniciada y oculta");
        } else {
            showToAllPlayers();
            sender.sendMessage(ChatColor.YELLOW + "Mostrando la bossbar a los jugadores");
        }
        return true;
    }
}
