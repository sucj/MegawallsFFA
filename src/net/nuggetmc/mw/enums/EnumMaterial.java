package net.nuggetmc.mw.enums;

import org.bukkit.Material;

public class EnumMaterial {
        // $FF: synthetic field
        public static final int[] EnumMaterialMapping;

        static {
            int[] var0 = new int[Material.values().length];
            var0[Material.AIR.ordinal()] = 1;
            var0[Material.LONG_GRASS.ordinal()] = 2;
            var0[Material.YELLOW_FLOWER.ordinal()] = 3;
            var0[Material.DOUBLE_PLANT.ordinal()] = 4;
            EnumMaterialMapping = var0;
        }
    }