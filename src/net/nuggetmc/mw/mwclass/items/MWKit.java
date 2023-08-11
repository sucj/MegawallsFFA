package net.nuggetmc.mw.mwclass.items;


import net.nuggetmc.mw.mwclass.MWClass;
import net.nuggetmc.mw.mwclass.classes.MWMole;
import net.nuggetmc.mw.utils.ItemStackCreator;
import net.nuggetmc.mw.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MWKit {

    private static final List<MWClass> KIT_CACHE = new ArrayList<>();

    public static boolean contains(MWClass mwclass) {
        return KIT_CACHE.contains(mwclass);
    }


    public static Map<Integer, ItemStack> generate(MWClass mwclass, ItemStack sword, ItemStack bow, ItemStack tool, ItemStack toolAxe, List<ItemStack> potions,
                                                   ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, List<ItemStack> extra) {

        Map<Integer, ItemStack> items = new HashMap<>();

        if (toolAxe == null) {
            toolAxe = new ItemStack(Material.IRON_AXE);
        }
        if (bow == null) {
            Map<Enchantment, Integer> bowEnch = new HashMap<>();
            bowEnch.put(Enchantment.DURABILITY, 10);
            bow = MWItem.createBow(mwclass, bowEnch);
        }

        /*if (toolShovel == null) {
            toolShovel = new ItemStack(Material.IRON_SPADE);
        }*/
        ItemStack enderchest = ItemStackCreator.createItem(Material.ENDER_CHEST, mwclass.getColor() + mwclass.getName() + (" Enderchest"), 1);
        ItemStack compass = ItemUtils.toMWItem(ItemStackCreator.createItem(Material.COMPASS, mwclass.getColor() + mwclass.getName() + (" Compass"), 1));
        List<ItemStack> contents = new ArrayList<>();

        contents.add(sword);
        contents.add(bow);
        contents.add(toolAxe);
        contents.add(new ItemStack(Material.WOOD, 64));
        contents.add(tool);
        contents.addAll(potions);
        contents.add(new ItemStack(Material.COOKED_BEEF, 64));
        contents.add(compass);
        contents.add(enderchest);
        //contents.add(toolShovel);
        ItemStack is = MWItem.createAOTR();
        if (mwclass instanceof MWMole) {
            is.setType(Material.IRON_SPADE);
        }
        contents.add(is);
        //contents.add(new ItemStack(Material.ARROW, 48));
        contents.add(new ItemStack(Material.STONE, 64));
        contents.add(new ItemStack(Material.DIRT, 64));
        contents.add(new ItemStack(Material.LOG, 64));
        contents.add(new ItemStack(Material.LOG, 64));
        contents.add(new ItemStack(Material.LOG, 64));
        contents.add(new ItemStack(Material.LOG, 64));


        if (extra != null) {
            contents.addAll(extra);
        }

        int n = 0;

        for (ItemStack item : contents) {
            items.put(n, item);
            n++;
        }
        if (helmet == null) helmet = new ItemStack(Material.IRON_HELMET);
        if (chestplate == null) chestplate = new ItemStack(Material.IRON_CHESTPLATE);
        if (leggings == null) leggings = new ItemStack(Material.IRON_LEGGINGS);
        if (boots == null) boots = new ItemStack(Material.IRON_BOOTS);

        items.put(36, boots);
        items.put(37, leggings);
        items.put(38, chestplate);
        items.put(39, helmet);

        KIT_CACHE.add(mwclass);

        return items;
    }

    public static void assignItems(Player player, Map<Integer, ItemStack> items) {
        items.forEach((key, value) -> player.getInventory().setItem(key, value));
    }
}
