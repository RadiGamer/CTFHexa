package org.hexa.ctfhexa.Commands;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hexa.ctfhexa.CTFHexa;

public class FlagCommand implements CommandExecutor {

    private final CTFHexa plugin;

    public FlagCommand(CTFHexa plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        World world = commandSender.getServer().getWorlds().get(0);
        WorldBorder border = world.getWorldBorder();
        Location center = border.getCenter();
        double size = border.getSize();
        int minX = (int) center.getX() - (int) size / 2;
        int minZ = (int) center.getZ() - (int) size / 2;
        int maxX = (int) center.getX() + (int) size / 2;
        int maxZ = (int) center.getZ() + (int) size / 2;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = 0; y < world.getMaxHeight(); y++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.BLUE_STAINED_GLASS || block.getType() == Material.RED_STAINED_GLASS) {
                        spawnFlag(block, block.getType() == Material.BLUE_STAINED_GLASS);
                    }
                }
            }
        }
        commandSender.sendMessage(ChatColor.GREEN + "Banderas spawneadas!");
        return true;
    }

    private void spawnFlag(Block block, boolean isBlue) {
        World world = block.getWorld();
        Location loc = block.getLocation().add(0.5, 2, 0.5);
        Interaction interaction = (Interaction) world.spawnEntity(block.getLocation().add(0.5, 2, 0.5), EntityType.INTERACTION);
        interaction.setInteractionHeight(2f);
        interaction.setInteractionWidth(1f);
        interaction.addScoreboardTag(isBlue ? "BlueFlag" : "RedFlag");
        interaction.setResponsive(true);


        ItemDisplay itemDisplay = (ItemDisplay) world.spawn(block.getLocation().add(0.5, 2, 0.5), ItemDisplay.class);
        ItemStack flag = new ItemStack(Material.STICK);
        ItemMeta meta = flag.getItemMeta();
        meta.setCustomModelData(isBlue ? 7 : 8);
        flag.setItemMeta(meta);
        itemDisplay.setItemStack(flag);

        Bukkit.getLogger().info((isBlue ? "Blue" : "Red") + " flag spawned at " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
    }
}
