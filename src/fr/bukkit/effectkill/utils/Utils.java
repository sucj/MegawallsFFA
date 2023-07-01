package fr.bukkit.effectkill.utils;

import org.bukkit.ChatColor;


public class Utils {

    public Utils() {
        throw new RuntimeException("Cannot create instance of Utils!");
    }

    public static String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }


}
