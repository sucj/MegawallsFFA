package net.nuggetmc.mw.special;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.mwclass.items.MWPotions;
import net.nuggetmc.mw.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpecialItemUtils {
    private final MegaWalls plugin = MegaWalls.getInstance();
    private final String[] squidname = new String[]{"Squid", "SQU"};
    private final String[] golemname = new String[]{"Golem", "GOL"};
    public static String cowBucketTag = "cowbucket";

    public ItemStack getSquidPot() {
        return getSquidPot(1);
    }

    public ItemStack getSquidPot(int count) {
        return MWPotions.createAbsorptionPotions(this.squidname[0], ChatColor.BLUE, count, 60);
    }

    public ItemStack getGolemPot() {
        return getGolemPot(1);
    }

    public ItemStack getGolemPot(int count) {
        return MWPotions.createRegenerationPotions(golemname[0], ChatColor.WHITE, count, 12, 10);
    }

    public ItemStack getCowBucket() {
        return getCowBucket(1);
    }

    public ItemStack getCowBucket(int amount) {
        ItemStack milk = new ItemStack(Material.MILK_BUCKET, amount);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.BLUE + "Resistance I (5s)");
        lore.add(ChatColor.BLUE + "Regeneration II (5s) " + ChatColor.GRAY + "(2 " + ChatColor.RED + "❤" + ChatColor.RESET + ")");
        ItemMeta itemMeta = milk.getItemMeta();
        itemMeta.setDisplayName(ChatColor.WHITE + "Ultra Pasteurized Milk Bucket");
        itemMeta.setLore(lore);
        milk.setItemMeta(itemMeta);
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(milk);
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        compound.setBoolean(cowBucketTag, true);
        nmsItem.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public ItemStack getCowOwnBucket(int amount) {
        ItemStack milk = new ItemStack(Material.MILK_BUCKET, amount);
        ItemMeta itemMeta = milk.getItemMeta();
        itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Cow Buckets Regeneration II (2 Hearts / 5s) & Resistance I (5s)");
        milk.setItemMeta(itemMeta);
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(milk);
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        compound.setBoolean(cowBucketTag, true);
        compound.setBoolean("cowown", true);
        nmsItem.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public boolean isCowBucket(ItemStack itemStack) {
        if (itemStack == null) return false;
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        if (nmsItem == null) return false;

        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        return compound.getBoolean(cowBucketTag);
    }

    public static boolean isCowBucketNotOwn(ItemStack itemStack) {
        if (itemStack == null) return false;
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        if (nmsItem == null) return false;

        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        return (compound.getBoolean(cowBucketTag) && (!compound.getBoolean("cowown")));
    }

    public boolean isCowBucket(net.minecraft.server.v1_8_R3.ItemStack itemStack1) {
        ItemStack itemStack = CraftItemStack.asBukkitCopy(itemStack1);
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        if (nmsItem == null) return false;

        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        return compound.getBoolean(cowBucketTag);
    }

    public ItemStack getPhxPot(int amount) {
        Potion potion = new Potion(PotionType.REGEN);
        potion.setSplash(true);

        ItemStack item = potion.toItemStack(amount);
        PotionMeta meta = (PotionMeta) item.getItemMeta();

        meta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 4 * 20, 2), true);
        meta.setDisplayName(ChatColor.GOLD + "Phoenix's Tears of Regeneration III (3 Hearts / 4s)");

        item.setItemMeta(meta);

        return ItemUtils.toMWItem(item);
    }

    public ItemStack getPhxPot() {
        return getPhxPot(1);
    }

    public ItemStack getJunkApple(int amount) {
        ItemStack apple = new ItemStack(Material.APPLE, amount);
        ItemMeta itemMeta = apple.getItemMeta();
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
        itemMeta.setDisplayName(ChatColor.YELLOW + "Junk apple");
        apple.setItemMeta(itemMeta);
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(apple);
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        compound.setBoolean("junk_apple", true);
        nmsItem.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public boolean isJunkApple(ItemStack itemStack) {
        if (itemStack == null) return false;
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        if (nmsItem == null) return false;

        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        return compound.getBoolean("junk_apple");
    }



    public ItemStack getGoodDiamond() {
        ItemStack item = new ItemStack(Material.DIAMOND);

        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        compound.setBoolean("goodDia", true);
        nmsItem.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public static boolean isGoodDiamond(ItemStack itemStack) {
        if (itemStack == null) return false;
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        if (nmsItem == null) return false;

        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        return compound.getBoolean("goodDia");
    }
    public ItemStack getQuiverArrow() {
        ItemStack item = new ItemStack(Material.ARROW,64);
        item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);


        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Quiver Arrow");
        meta.setLore(Collections.singletonList("This item is in your inventory because you are holding a bow."));
        item.setItemMeta(meta);

        return ItemUtils.toMWItem(item);
    }
}
