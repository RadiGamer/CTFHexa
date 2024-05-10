package org.hexa.ctfhexa;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.hexa.ctfhexa.Commands.*;
import org.hexa.ctfhexa.Listeners.FlagListener;
import org.hexa.ctfhexa.Listeners.PlayerListener;


public final class CTFHexa extends JavaPlugin {
    private FileConfiguration config;
    private BossBarCommand bossBarCommand;


    @Override
    public void onEnable() {

        bossBarCommand = new BossBarCommand();

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team blueTeam = scoreboard.getTeam("blue");
        if (blueTeam == null) {
            blueTeam = scoreboard.registerNewTeam("blue");
            blueTeam.setColor(ChatColor.BLUE);
            blueTeam.setDisplayName("Blue Team");
        }

        Team redTeam = scoreboard.getTeam("red");
        if (redTeam == null) {
            redTeam = scoreboard.registerNewTeam("red");
            redTeam.setColor(ChatColor.RED);
            redTeam.setDisplayName("Red Team");
        }

        Bukkit.getConsoleSender().sendMessage("§aActivado CTF");

        this.saveDefaultConfig();

        this.config = getConfig();
        this.config.options().copyDefaults(true);

        getCommand("flags").setExecutor(new FlagCommand(this));
        getCommand("flaglocation").setExecutor(new FlagOnLocationCommand(this));
        getCommand("divideplayers").setExecutor(new DivideCommand());
        getCommand("setteam").setExecutor(new SetTeamCommand());
        getCommand("setsavepoint").setExecutor(new SetSpawnPointCommand(config, this));
        getCommand("teleportteam").setExecutor(new TeleportTeamCommand(config, this));
        getCommand("resetteams").setExecutor(new ResetTeamCommand(this));
        getCommand("savebuffzone").setExecutor(new SaveBuffZoneCommand(this));
        getCommand("buffdeploy").setExecutor(new BuffCommand(this));
        getCommand("setflagspawn").setExecutor(new SetFlagSpawn(this));
        getCommand("flagbossbar").setExecutor(bossBarCommand);

        getServer().getPluginManager().registerEvents(new FlagListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this, bossBarCommand),this);

    }
//TODO PULIR EQUIPOS

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§cDesactivando CTF");

    }
}
