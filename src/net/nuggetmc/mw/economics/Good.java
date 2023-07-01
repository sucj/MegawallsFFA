package net.nuggetmc.mw.economics;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Good {
    private final ItemStack displayItem;

    public void setPrice(int price) {
        this.price = price;
    }

    private int price;
    private final String extralore;
    private final String displayname;
    private final ItemStack theItem;

    public Good(ItemStack displayItem, String displayname, int price, ItemStack theItem, @Nullable String extralore) {
        this.displayItem = displayItem;
        this.price = price;
        this.displayname = displayname;
        this.theItem = theItem;
        this.extralore = extralore;
    }

    public ItemStack getDisplayItem() {
        return this.displayItem;
    }

    public ItemStack getTheItem() {
        return this.theItem;
    }

    public String getDisplayName() {
        return this.displayname;
    }

    public int getPrice() {
        return this.price;
    }

    public ItemStack getMenuItem() {


        ItemStack item = new ItemStack(this.displayItem);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + this.displayname);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Price: " + this.price);


        if (extralore != null) {
            lore.add(extralore);
        }
        lore.add(ChatColor.RED.toString() + ChatColor.BOLD + "Click to buy one!");
        lore.add(ChatColor.RED.toString() + ChatColor.BOLD + "Shift Click to buy multiple!");


        meta.setLore(lore);
        item.setItemMeta(meta);


        return item;
    }

    @Override
    public String toString() {
        return "Good{" + "name=" + this.getDisplayName() + "," + "price=" + this.getPrice() + "," + "theitem=" + theItem.toString() + "}";
    }
}
