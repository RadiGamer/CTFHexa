package org.hexa.ctfhexa.Commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.hexa.ctfhexa.CTFHexa;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class FlagCommand implements CommandExecutor {

    private final CTFHexa plugin;

    public FlagCommand(CTFHexa plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        World world = commandSender.getServer().getWorlds().get(0);

        ConfigurationSection flags = plugin.getConfig().getConfigurationSection("FlagLocation");
        if (flags == null) {
            commandSender.sendMessage(ChatColor.RED + "No hay banderas configuradas!");
            return true;
        }

        for (String key : flags.getKeys(false)) {
            ConfigurationSection flag = flags.getConfigurationSection(key);
            String team = flag.getString("team");
            int x = flag.getInt("X");
            int y = flag.getInt("Y");
            int z = flag.getInt("Z");

            Location loc = new Location(world, x, y, z);

                boolean isBlue = "Blue".equalsIgnoreCase(team);

                spawnFlag(loc, isBlue);
        }

        commandSender.sendMessage(ChatColor.GREEN + "Banderas Spawneadas!");
        return true;
    }

    private void spawnFlag(Location location, boolean isBlue) {
        World world = location.getWorld();
        Color flagColor = isBlue ? Color.BLUE : Color.RED;
        Location loc = location.clone().add(1.5, 0, 0);

        Interaction interaction = (Interaction) world.spawnEntity(loc, EntityType.INTERACTION);
        interaction.setInteractionHeight(4f);
        interaction.setInteractionWidth(4f);
        interaction.addScoreboardTag(isBlue ? "BlueFlag" : "RedFlag");
        interaction.setResponsive(true);
        interaction.addScoreboardTag("Flag");

        loc.add(0,2,0);

        ItemDisplay itemDisplay = (ItemDisplay) world.spawn(loc, ItemDisplay.class);
        itemDisplay.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(2f), new AxisAngle4f()));

        ItemStack flag = new ItemStack(Material.STICK);
        itemDisplay.setGlowColorOverride(flagColor);
        itemDisplay.setGlowing(true);
        itemDisplay.addScoreboardTag("Flag");

        ItemMeta meta = flag.getItemMeta();
        meta.setCustomModelData(isBlue ? 7 : 8);
        flag.setItemMeta(meta);
        itemDisplay.setItemStack(flag);

        Bukkit.getLogger().info((isBlue ? "Blue" : "Red") + " bandera spawneada en " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
    }
}
