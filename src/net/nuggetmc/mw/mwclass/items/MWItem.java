package net.nuggetmc.mw.mwclass.items;

import net.md_5.bungee.api.ChatColor;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.luckdraw.SwordNameManager;
import net.nuggetmc.mw.mwclass.MWClass;
import net.nuggetmc.mw.mwclass.classes.MWMole;
import net.nuggetmc.mw.mwclass.info.EnumInfoType;
import net.nuggetmc.mw.mwclass.info.MWClassInfo;
import net.nuggetmc.mw.utils.ItemUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MWItem {
   static SwordNameManager swordNameManager;


    public static ItemStack createSword(MWClass mwclass, Material type, Map<Enchantment, Integer> enchantments, Player player) {
        if (swordNameManager==null){
            swordNameManager=MegaWalls.getInstance().getSwordNameManager();
        }
        ItemStack item = new ItemStack(type);
        List<String> lore = new ArrayList<>();

        if (enchantments != null) {
            item.addUnsafeEnchantments(enchantments);
            lore.add("");
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(
                swordNameManager.get(player)==null?
                mwclass.getColor() + mwclass.getName() + (mwclass instanceof MWMole?" Spade":" Sword" ):
                swordNameManager.get(player)
        );

        MWClassInfo info = mwclass.getInfo();

        lore.addAll(info.getLoreFormatted(EnumInfoType.ABILITY, false));

        //lore.add(ChatColor.DARK_GRAY + " ▪ " + ChatColor.GRAY + "How to Activate: Right-Click with your Sword");
        //lore.add(ChatColor.GRAY + "   or Left-Click with your Bow");

        lore.add("");

        lore.add(ChatColor.GRAY + "Energy Gain:");

        for (Map.Entry<String, String> entry : info.getEnergyGain().entrySet()) {
            lore.add(ChatColor.DARK_GRAY + " ▪ " + ChatColor.GRAY + entry.getKey() + ": " + ChatColor.GREEN + entry.getValue());
        }

        meta.setLore(lore);
        meta.spigot().setUnbreakable(true);

        item.setItemMeta(meta);

        return ItemUtils.toMWItem(item);
    }
    public static ItemStack createAOTR() {
        ItemStack item = new ItemStack(Material.DIAMOND_SPADE);
        List<String> lore = new ArrayList<>();


        item.addUnsafeEnchantment(Enchantment.DIG_SPEED,10);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE.toString()+ChatColor.BOLD+"Aspect of the Rogues");


        lore.add("");

        lore.add(ChatColor.GOLD + "Ability:Speed Boost "+ChatColor.YELLOW+ChatColor.BOLD+"RIGHT CLICK");
        lore.add(ChatColor.GRAY+"Grants you a walk speed boost for "+ChatColor.GREEN+"5s"+ChatColor.GRAY+".");
        lore.add(ChatColor.RED.toString()+ChatColor.BOLD+"CANT BE USED WHEN THERE'S ANY ENEMY WITHIN "+ChatColor.GREEN+ChatColor.BOLD+"50 "+ChatColor.RED+ChatColor.BOLD+"blocks!");
        lore.add("Cooldown:40s");
        lore.add("");
        lore.add("");
        lore.add(ChatColor.GRAY.toString()+ChatColor.ITALIC+"Being inspired by Rogue Sword in Hypixel Skyblock,the Aspect of the Rogues was invented.");
        lore.add(ChatColor.GRAY.toString()+ChatColor.ITALIC+"This item can also be used as a shovel.");
        lore.add("");
        lore.add(ChatColor.LIGHT_PURPLE.toString()+ChatColor.BOLD+"MYTHIC");


        meta.setLore(lore);
        meta.spigot().setUnbreakable(true);

        item.setItemMeta(meta);

        return ItemUtils.toMWItem(item);
    }

    public static ItemStack createBow(MWClass mwclass, Map<Enchantment, Integer> enchantments) {
        ItemStack item = new ItemStack(Material.BOW);

        List<String> lore = new ArrayList<>();

        if (enchantments != null) {
            item.addUnsafeEnchantments(enchantments);
            lore.add("");
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(mwclass.getColor() + mwclass.getName() + " Bow");

        MWClassInfo info = mwclass.getInfo();

        lore.addAll(info.getLoreFormatted(EnumInfoType.PASSIVE_1));
        lore.addAll(info.getLoreFormatted(EnumInfoType.PASSIVE_2));

        meta.setLore(lore);
        meta.spigot().setUnbreakable(true);

        item.setItemMeta(meta);

        return ItemUtils.toMWItem(item);
    }

    public static ItemStack createTool(MWClass mwclass, Material type) {
        ItemStack item = new ItemStack(type);

        item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

        ItemMeta meta = item.getItemMeta();

        String name;
        String typeName = type.name();

        if (typeName.contains("PICKAXE")) {
            name = "Pickaxe";
        } else if (typeName.contains("AXE")) {
            name = "Axe";
        } else {
            name = "Shovel";
        }

        meta.setDisplayName(mwclass.getColor() + mwclass.getName() + " " + name);

        MWClassInfo info = mwclass.getInfo();
        List<String> lore = new ArrayList<>();

        lore.add("");

        lore.addAll(info.getLoreFormatted(EnumInfoType.GATHERING, false));

        meta.setLore(lore);
        meta.spigot().setUnbreakable(true);

        item.setItemMeta(meta);

        return ItemUtils.toMWItem(item);
    }

    public static ItemStack createArmor(MWClass mwclass, Material type, Map<Enchantment, Integer> enchantments) {
        ItemStack item = new ItemStack(type);

        if (enchantments != null) {
            item.addUnsafeEnchantments(enchantments);
        }

        ItemMeta meta = item.getItemMeta();
        String name = type.name().toLowerCase();

        meta.setDisplayName(mwclass.getColor() + mwclass.getName() + " " + StringUtils.capitalize(name.substring(name.indexOf("_") + 1)));
        meta.spigot().setUnbreakable(true);

        List<String> lore = new ArrayList<>();

        lore.add("");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return ItemUtils.toMWItem(item);
    }
}
