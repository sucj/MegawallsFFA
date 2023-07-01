package net.nuggetmc.mw.utils;

import net.minecraft.server.v1_8_R3.Packet;
import net.nuggetmc.mw.MegaWalls;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class PlayerUtils {

    static MegaWalls plugin = MegaWalls.getInstance();

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
                players.add((Player) e);
        }
        return players;
    }

    public static List<Player> getNearbyPlayers(Location location, Player player, int radius) {
        List<Player> players = new ArrayList<>();
        for (Player other : PlayerUtils.getNearbyPlayers(location, radius)) {
            if (other.getGameMode().equals(GameMode.SPECTATOR) || MegaWalls.getInstance().getTeamsManager().isOnSameTeam(player, other) || other
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

    public static Player getClosestEnemyInRange(Player player, double range) {
        Player target = null;
        for (Player player1 : player.getWorld().getPlayers()) {
            if (plugin.getTeamsManager().isOnSameTeam(player, player1)) continue;
            if (plugin.getCombatManager().isInCombat(player1) && !player1.isDead() && player1.getGameMode() != GameMode.CREATIVE && (player1.getLocation().distance(player.getLocation()) < range) && !player1.equals(player)) {
                target = player1;
                break;
            }
        }
        return target;
    }

    public static Player getClosestEnemy(Player player) {
        Player target = null;
        for (Player player1 : player.getWorld().getPlayers()) {
            if (plugin.getTeamsManager().isOnSameTeam(player, player1)) continue;
            if (plugin.getCombatManager().isInCombat(player1) && !player1.isDead() && player1.getGameMode() != GameMode.CREATIVE && !player1.equals(player)) {
                target = player1;
                break;
            }
        }
        return target;
    }

    public static List<Player> getNearbyEnemies(Player player, double radius) {
        List<Player> players = new ArrayList<>();
        for (Player other : PlayerUtils.getNearbyPlayers(player.getLocation(), radius)) {
            if (plugin.getCombatManager().isInCombat(other)) {
                if (plugin.getTeamsManager().isOnSameTeam(player, other)) {
                    continue;
                }
                players.add(other);
            }
        }
        return players;
    }


}
