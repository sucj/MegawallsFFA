package net.nuggetmc.mw.utils;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Random;

public class MiliKiller implements Listener {
    private static final char[] randomString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public static String getRandomString(int length) {
        Random random = new Random();
        char[] array = new char[length];
        for (int i = 0; i < length; i++) {
            array[i] = randomString[random.nextInt(randomString.length)];
        }
        return new String(array);
    }

    @EventHandler
    public void onClientAD(PlayerChatEvent e){
        if(e.getMessage().toLowerCase().contains("employee"))
            e.getPlayer().kickPlayer("§cYou are forever banned from this server!"
                    + "\n\n§7Reason: §fCheating through the use of unfair game advantages."
                    + "\n§7Find out more: " + ChatColor.AQUA + "§nhttps://www.hypixel.net/appeal" + "\n\n§7Ban ID:§f #"
                    + getRandomString(8).toUpperCase() + ""
                    + "\n§7Sharing your Ban ID may affect the processing of your appeal!");
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(e.getPlayer().getName().toLowerCase().contains("miliblu") || e.getPlayer().getName().toLowerCase().contains("thr1c0")){
            e.getPlayer().kickPlayer("§cYou are forever banned from this server!"
                    + "\n\n§7Reason: §fCheating through the use of unfair game advantages."
                    + "\n§7Find out more: " + ChatColor.AQUA + "§nhttps://www.hypixel.net/appeal" + "\n\n§7Ban ID:§f #"
                    + getRandomString(8).toUpperCase() + ""
                    + "\n§7Sharing your Ban ID may affect the processing of your appeal!");
        }
    }
}
