package net.nuggetmc.mw.enums;

import org.bukkit.Material;

public class EnumMaterial {
        // $FF: synthetic field
        public static final int[] EnumMaterialMapping;

        static {
            int[] index = new int[Material.values().length];
            index[Material.AIR.ordinal()] = 1;
            index[Material.LONG_GRASS.ordinal()] = 2;
            index[Material.YELLOW_FLOWER.ordinal()] = 3;
            index[Material.DOUBLE_PLANT.ordinal()] = 4;
            EnumMaterialMapping = index;
        }
    }