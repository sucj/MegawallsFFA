package net.nuggetmc.mw.fun;

import net.nuggetmc.mw.MegaWalls;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Random;
import java.util.logging.Level;

public class MiliKiller implements Listener {
    private boolean toggled=false;
    public void toggle(CommandSender commandSender){
        toggled=!this.toggled;
        commandSender.sendMessage("You "+(toggled?"enabled":"disabled")+" MiliKiller!");
        MegaWalls.getInstance().getLogger().log(Level.WARNING,"MiliKiller was "+(toggled?"enabled":"disabled")+" by "+commandSender.getName());
    }
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
        if (!toggled) return;
        if(e.getMessage().toLowerCase().contains("employee"))
            e.getPlayer().kickPlayer("§cYou are permanently banned from this server!"
                    + "\n\n§7Reason: §fCheating through the use of unfair game advantages."
                    + "\n§7Find out more: " + ChatColor.AQUA + "§nhttps://www.hypixel.net/appeal" + "\n\n§7Ban ID:§f #"
                    + getRandomString(8).toUpperCase() + ""
                    + "\n§7Sharing your Ban ID may affect the processing of your appeal!");
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if (!toggled) return;
        if(e.getPlayer().getName().toLowerCase().contains("miliblu") || e.getPlayer().getName().toLowerCase().contains("thr1c0")){
            e.getPlayer().kickPlayer("§cYou are permanently banned from this server!"
                    + "\n\n§7Reason: §fCheating through the use of unfair game advantages."
                    + "\n§7Find out more: " + ChatColor.AQUA + "§nhttps://www.hypixel.net/appeal" + "\n\n§7Ban ID:§f #"
                    + getRandomString(8).toUpperCase() + ""
                    + "\n§7Sharing your Ban ID may affect the processing of your appeal!");
        }
    }
}
