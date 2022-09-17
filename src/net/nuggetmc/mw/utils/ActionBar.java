package net.nuggetmc.mw.utils;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringJoiner;

public class ActionBar {

    public static void send(Player player, String message) {
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}"), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void clear(Player player) {
        send(player, "");
    }
    public static String joinActionBar(String...s){
        if (s.length==1){
            return s[0];
        }
        StringBuilder stringBuilder=new StringBuilder();
        for (int i=0;i<s.length;i++){
            stringBuilder.append(s[i]);
            if (i+1<s.length){
                stringBuilder.append("      ");
            }
        }
        return stringBuilder.toString();
    }
}
