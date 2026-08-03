package net.nuggetmc.mw.utils;

import net.minecraft.server.v1_8_R3.Packet;
import net.nuggetmc.mw.MegaWalls;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;

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
    public static List<LivingEntity> getNearbyMobs(Location location, double radius) {
        List<LivingEntity> mobs = new ArrayList<>();
        for (Entity e : location.getWorld()
                .getNearbyEntities(location, radius, radius, radius)) {
            if (e instanceof LivingEntity&&(!(e instanceof Player)) && e.getLocation().distance(location) <= radius)
                mobs.add((LivingEntity) e);
        }
        return mobs;
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
    public static void teleport(Player player) {
        Location mainLoc = player.getEyeLocation();
        for (int i = 1; i <= 8 * 2; i++) {
            Location loc = player.getLocation();
            Vector dir = loc.getDirection();
            dir.normalize();
            dir.multiply(0.5); //1 blocks a way
            mainLoc.add(dir);

            if (mainLoc.getBlock().isEmpty() || mainLoc.getBlock().isLiquid() || canBePassed(mainLoc.getBlock())) {
                player.teleport(mainLoc);
                player.setFallDistance(0);
            } else break;
        }
    }
    public static boolean canBePassed(Block block) {
        switch (block.getType()) {
            case YELLOW_FLOWER:
            case LONG_GRASS:
            case AIR:
            case DOUBLE_PLANT:
                return true;
            default:
                return false;
        }
    }
    public static boolean checkValid(Block block){

        Location loc1 = block.getLocation().clone().add(0,1,1);
        Location loc2 = block.getLocation().clone().add(0,2,1);
        if((canBePassed(loc1.getBlock()) || loc1.getBlock().isEmpty() || loc1.getBlock().isLiquid()) && (canBePassed(loc2.getBlock()) || loc2.getBlock().isEmpty() || loc2.getBlock().isLiquid())) {
            return true;
        }
        return false;
    }


}
