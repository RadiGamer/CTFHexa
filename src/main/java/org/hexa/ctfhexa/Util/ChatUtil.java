package org.hexa.ctfhexa.Util;

import org.bukkit.ChatColor;

public class ChatUtil {
    public static String preformat(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
    public static String format(String string){
        return preformat("&n&f&l[&c&lHEXA&f&l] &r" + string);
    }
}