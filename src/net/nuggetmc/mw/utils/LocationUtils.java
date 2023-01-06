package net.nuggetmc.mw.utils;

import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.bukkit.block.Block;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.BlockFace;
import org.bukkit.Location;

/**
 * SKID FROM YUZEGOD.
 */
public class LocationUtils
{
    public static Location getLocation(final Location location, final double toX, final double toY, final double toZ) {
        return new Location(location.getWorld(), location.getX() + toX, location.getY() + toY, location.getZ() + toZ);
    }
    
    public static Location getFixedLocation(final Location location, final BlockFace blockFace) {
        for (int i = 0; i < location.getWorld().getMaxHeight() && location.getBlock().isEmpty(); ++i) {
            location.add(0.0, (blockFace == BlockFace.UP) ? 1.0 : -1.0, 0.0);
        }
        return location.add(0.0, (blockFace == BlockFace.UP) ? -1.0 : 1.0, 0.0);
    }
    
    public static List<Location> getCircle(final Location location, final double radius, final int points) {
        final List<Location> locations = new ArrayList<Location>();
        final double increment = 6.283185307179586 / points;
        for (int i = 0; i < points; ++i) {
            final double angle = i * increment;
            final double x = location.getX() + Math.cos(angle) * radius;
            final double z = location.getZ() + Math.sin(angle) * radius;
            locations.add(new Location(location.getWorld(), x, location.getY(), z));
        }
        return locations;
    }
    
    public static List<Block> getSphere(final Location location, final int radius) {
        final List<Block> blocks = new ArrayList<Block>();
        final int X = location.getBlockX();
        final int Y = location.getBlockY();
        final int Z = location.getBlockZ();
        final int radiusSquared = radius * radius;
        for (int x = X - radius; x <= X + radius; ++x) {
            for (int y = Y - radius; y <= Y + radius; ++y) {
                for (int z = Z - radius; z <= Z + radius; ++z) {
                    if ((X - x) * (X - x) + (Z - z) * (Z - z) <= radiusSquared) {
                        blocks.add(location.getWorld().getBlockAt(x, y, z));
                    }
                }
            }
        }
        return blocks;
    }
    
    public static List<Block> getCube(final Location location, final int radius) {
        final List<Block> blocks = new ArrayList<Block>();
        final int X = location.getBlockX() - radius / 2;
        final int Y = location.getBlockY() - radius / 2;
        final int Z = location.getBlockZ() - radius / 2;
        for (int x = X; x < X + radius; ++x) {
            for (int y = Y; y < Y + radius; ++y) {
                for (int z = Z; z < Z + radius; ++z) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }
    
    public static Vector getBackVector(final Location location) {
        final float f1 = (float)(location.getZ() + 1.0 * Math.sin(Math.toRadians(location.getYaw() + 90.0f)));
        final float f2 = (float)(location.getX() + 1.0 * Math.cos(Math.toRadians(location.getYaw() + 90.0f)));
        return new Vector(f2 - location.getX(), 0.0, f1 - location.getZ());
    }
    
    public static boolean isSafeSpot(final Location location) {
        final Block blockCenter = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        final Block blockAbove = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() + 1, location.getBlockZ());
        final Block blockBelow = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());
        return (blockCenter.getType().isTransparent() || (blockCenter.isLiquid() && !blockCenter.getType().equals((Object)Material.LAVA) && !blockCenter.getType().equals((Object)Material.STATIONARY_LAVA))) && (blockAbove.getType().isTransparent() || (blockAbove.isLiquid() && !blockAbove.getType().equals((Object)Material.LAVA) && !blockCenter.getType().equals((Object)Material.STATIONARY_LAVA))) && (blockBelow.getType().isSolid() || blockBelow.getType().equals((Object)Material.WATER) || blockBelow.getType().equals((Object)Material.STATIONARY_WATER));
    }
}
