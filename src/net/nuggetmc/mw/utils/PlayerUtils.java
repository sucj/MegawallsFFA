package net.nuggetmc.mw.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.nuggetmc.mw.MegaWalls;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;


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
  

}


/* Location:              C:\Users\ADMINI~1\AppData\Local\Temp\MegaWalls-1.1-SNAPSHOT.jar!\xyz\yuzegod\megawall\\util\PlayerUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */