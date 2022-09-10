package net.nuggetmc.mw.special;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.Items;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.mwclass.classes.MWCow;
import net.nuggetmc.mw.mwclass.items.MWPotions;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Cow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class SpecialItemUtils {
    private MegaWalls plugin=MegaWalls.getInstance();
    private String[] squidname=new String[]{"鱿鱼","Squid","SQU"};
    private String[] golemname=new String[]{"傀儡","Golem","GOL"};
    String cowBucketTag="cowbucket";
    public ItemStack getSquidPot(){
        return MWPotions.createAbsorptionPotions(plugin.isChinese()?this.squidname[0]:this.squidname[1], ChatColor.BLUE, 1, 60);
    }
    public ItemStack getGolemPot(){
        return MWPotions.createRegenerationPotions(plugin.isChinese()?golemname[0]:golemname[1], ChatColor.WHITE, 1, 12, 10);
    }
    public ItemStack getCowBucket(){
        ItemStack milk=new ItemStack(Material.MILK_BUCKET,1);
        ArrayList<String> lore=new ArrayList<>();
        lore.add(ChatColor.BLUE+"Resistance I (5s)");
        lore.add(ChatColor.BLUE+"Regeneration II (5s) "+ChatColor.GRAY+"(2 "+ChatColor.RED+"❤"+ChatColor.RESET+")");
        ItemMeta itemMeta=milk.getItemMeta();
        itemMeta.setDisplayName(ChatColor.WHITE+"Ultra Pasteurized Milk Bucket");
        itemMeta.setLore(lore);
        milk.setItemMeta(itemMeta);
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(milk);
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        compound.setBoolean(cowBucketTag, true);
        nmsItem.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }
    public ItemStack getCowBucket(int amount){
        ItemStack milk=new ItemStack(Material.MILK_BUCKET,amount);
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(milk);
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        compound.setBoolean(cowBucketTag, true);
        nmsItem.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }
    public ItemStack getCowOwnBucket(int amount){
        ItemStack milk=new ItemStack(Material.MILK_BUCKET,amount);
        ItemMeta itemMeta= milk.getItemMeta();
        itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE+"Cow Buckets Regeneration II (2 Hearts / 0:005s) & Resistance I (0:05s)");
        milk.setItemMeta(itemMeta);
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(milk);
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        compound.setBoolean(cowBucketTag, true);
        compound.setBoolean("cowown",true);
        nmsItem.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }
    public boolean isCowBucket(ItemStack itemStack){
        if (itemStack == null) return false;
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        if (nmsItem == null) return false;

        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        return compound.getBoolean(cowBucketTag);
    }
    public boolean isCowBucket(net.minecraft.server.v1_8_R3.ItemStack itemStack1){
       ItemStack itemStack= CraftItemStack.asBukkitCopy(itemStack1);
        if (itemStack == null) return false;
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        if (nmsItem == null) return false;

        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        return compound.getBoolean(cowBucketTag);
    }
}
