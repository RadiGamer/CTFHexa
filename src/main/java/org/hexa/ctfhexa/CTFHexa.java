package org.hexa.ctfhexa;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.hexa.ctfhexa.Commands.FlagCommand;
import org.hexa.ctfhexa.Commands.FlagOnLocationCommand;
import org.hexa.ctfhexa.Listeners.FlagListener;
import org.hexa.ctfhexa.Listeners.PlayerListener;

public final class CTFHexa extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("§aActivado CTF");

        getCommand("flags").setExecutor(new FlagCommand(this));
        getCommand("flaglocation").setExecutor(new FlagOnLocationCommand(this));

        getServer().getPluginManager().registerEvents(new FlagListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this),this);

    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§cDesactivando CTF");

    }
}
