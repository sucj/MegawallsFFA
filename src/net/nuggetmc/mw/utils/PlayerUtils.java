package net.nuggetmc.mw.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_8_R3.*;
import net.nuggetmc.mw.MegaWalls;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class PlayerUtils {


  
  public static List<Player> getNearbyPlayers(Location location, double radius) {
    List<Player> players = new ArrayList<>();
    for (Entity e : location.getWorld()
            .getNearbyEntities(location, radius, radius, radius)) {
      if (e instanceof Player && e.getLocation().distance(location) <= radius)
        players.add((Player) e);
    }
    return players;
  }
  public static List<Player> getNearbyPlayers(Entity entity, double radius) {
    List<Player> players = new ArrayList<>();
    for (Entity e : entity.getNearbyEntities(radius, radius, radius)) {

      if (e instanceof Player)
        players.add((Player)e);
    }
    return players;
  }
  public static List<Player> getNearbyPlayers(Location location, Player player, int radius) {
    List<Player> players = new ArrayList<>();
    for (Player other : PlayerUtils.getNearbyPlayers(location, radius)) {
      if (other.getGameMode().equals(GameMode.SPECTATOR) || MegaWalls.getInstance().getTeamsManager().isOnSameTeam(player,other) || other
              .getLocation().distance(location) > radius)
        continue;
      players.add(other);
    }
    return players;
  }
  public static void sendPacket(Player player, Packet packet) {
      ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
  }

  public static Class<?> getNMSClass(String name) {
    String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    try {
      return Class.forName("net.minecraft.server." + version + "." + name);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Class<?> getClass(String name) {
    String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    try {
      return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  

}
