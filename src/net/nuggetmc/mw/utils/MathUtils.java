package net.nuggetmc.mw.utils;

import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class MathUtils {

    public static Vector rotateAroundY(Vector vector, double deg) {
        double x = vector.getX();
        double z = vector.getZ();

        double angle = Math.atan2(z, x) + Math.toRadians(deg);

        return new Vector(Math.cos(angle), vector.getY(), Math.sin(angle));
    }

    public static int randomIntInRange(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }
    public static double round(double number,int scale){
        BigDecimal bd = new BigDecimal(Double.toString(number));
        bd = bd.setScale(scale, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
