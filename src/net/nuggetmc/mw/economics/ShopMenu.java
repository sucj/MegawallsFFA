package net.nuggetmc.mw.economics;

import net.md_5.bungee.api.ChatColor;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.energy.EnergyManager;
import net.nuggetmc.mw.mwclass.MWClassManager;
import net.nuggetmc.mw.special.SpecialItemUtils;
import net.nuggetmc.mw.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class ShopMenu implements Listener {
    MegaWalls plugin = MegaWalls.getInstance();

    SpecialItemUtils specialItemUtils = plugin.getSpecialItemUtils();
    private static final String CLOSE_NAME = ChatColor.RED + "Close";
    private final Map<Player, String> waitingForInputList = new HashMap<>();

    private final MWClassManager classmanager = plugin.getClassManager();
    private final EnergyManager energyManager = plugin.getEnergyManager();
    private final String menuTitle = "Shop";
    private static final Map<String, Good> goods = new HashMap<>();
    public Good gapple = new Good(new ItemStack(Material.GOLDEN_APPLE, 1), "Golden Apple", 8, new ItemStack(Material.GOLDEN_APPLE, 1), null);
    public Good milk = new Good(new ItemStack(Material.MILK_BUCKET, 1), "Cow Bucket", 5, specialItemUtils.getCowBucket(), null);
    Good squpot = new Good(specialItemUtils.getSquidPot(), ChatColor.stripColor(specialItemUtils.getSquidPot().getItemMeta().getDisplayName()), 15, specialItemUtils.getSquidPot(), null);
    Good phxpot = new Good(specialItemUtils.getPhxPot(), ChatColor.stripColor(specialItemUtils.getPhxPot().getItemMeta().getDisplayName()), 20, specialItemUtils.getPhxPot(), null);
    Good golempot = new Good(specialItemUtils.getGolemPot(), ChatColor.stripColor(specialItemUtils.getGolemPot().getItemMeta().getDisplayName()), 25, specialItemUtils.getGolemPot(), null);
    Good diamond = new Good(new ItemStack(Material.DIAMOND, 1), "Diamond", 50, new ItemStack(Material.DIAMOND, 1), null);
    Good ironHelmet = new Good(new ItemStack(Material.IRON_HELMET, 1), "Iron Helmet", 1, new ItemStack(Material.IRON_HELMET, 1), null);
    Good ironChestplate = new Good(new ItemStack(Material.IRON_CHESTPLATE, 1), "Iron Chestplate", 1, new ItemStack(Material.IRON_CHESTPLATE, 1), null);
    Good ironLeggings = new Good(new ItemStack(Material.IRON_LEGGINGS, 1), "Iron Leggings", 1, new ItemStack(Material.IRON_LEGGINGS, 1), null);
    Good ironBoots = new Good(new ItemStack(Material.IRON_BOOTS, 1), "Iron Boots", 1, new ItemStack(Material.IRON_BOOTS, 1), null);

    private void registergood(Good... GoodArray) {
        for (Good good : GoodArray) {
            goods.put(good.getDisplayName(), good);
        }
    }

    private Good getGoodByName(String goodname) {
        return goods.get(goodname);
    }

    public ShopMenu() {
        reloadPrices();
        registergood(
                gapple,
                milk,
                squpot,
                golempot,
                diamond,
                ironHelmet,
                ironChestplate,
                ironBoots,
                ironLeggings,
                phxpot
        );
    }

    private void reloadPrices() {
        loadPriceOrDefault(this.gapple, 16);
        loadPriceOrDefault(this.milk, 10);
        loadPriceOrDefault(this.squpot, 30);
        loadPriceOrDefault(this.golempot, 50);
        loadPriceOrDefault(this.diamond, 100);
        loadPriceOrDefault(this.ironHelmet, 1);
        loadPriceOrDefault(this.ironChestplate, 1);
        loadPriceOrDefault(this.ironBoots, 1);
        loadPriceOrDefault(this.ironLeggings, 1);
        loadPriceOrDefault(this.phxpot, 20);
    }

    private void loadPriceOrDefault(Good good, int defaulta) {
        good.setPrice(plugin.getOrDefaultFromConfig("shop.price." + good.getDisplayName(), defaulta));
    }

    public void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, menuTitle);


        inv.setItem(10, gapple.getMenuItem());
        inv.setItem(11, milk.getMenuItem());
        inv.setItem(12, squpot.getMenuItem());
        inv.setItem(13, golempot.getMenuItem());
        inv.setItem(14, diamond.getMenuItem());
        inv.setItem(15, ironHelmet.getMenuItem());
        inv.setItem(16, ironChestplate.getMenuItem());

        inv.setItem(19, ironLeggings.getMenuItem());
        inv.setItem(20, ironBoots.getMenuItem());
        inv.setItem(21, phxpot.getMenuItem());
        //  inv.setItem(11, example);


        inv.setItem(49, createClose());
        player.openInventory(inv);
    }

    private void select(Player player, String name) {
        Good good = getGoodByName(name);
        if (good == null) return;

        select(player, good);
    }

    private void select(Player player, String name, int amount) {
        Good good = getGoodByName(name);
        if (good == null) return;

        select(player, good, amount);
    }


    public void select(Player player, Good good) {
        if (plugin.getCoinsManager().get(player) < good.getPrice()) {
            player.sendMessage("not enough coins!");
        } else if (ItemUtils.isFullInventory(player.getInventory())) {
            player.sendMessage("your inventory is full!");
        } else {
            plugin.getCoinsManager().add(player, -good.getPrice());
            player.sendMessage("You have purchased " + ChatColor.YELLOW + good.getDisplayName() + ChatColor.RESET + " with " + ChatColor.GREEN + good.getPrice() + ChatColor.RESET + " coins.");
            player.getInventory().addItem(good.getTheItem());
        }

    }

    public void select(Player player, Good good, int amount) {
        if (plugin.getCoinsManager().get(player) < (good.getPrice() * amount)) {
            player.sendMessage("not enough coins!");
        } else if (ItemUtils.isFullInventory(player.getInventory())) {
            player.sendMessage("your inventory is full!");
        } else {
            plugin.getCoinsManager().add(player, -(good.getPrice() * amount));
            player.sendMessage("You have purchased " + amount + " " + ChatColor.YELLOW + good.getDisplayName() + ChatColor.RESET + " with " + ChatColor.GREEN + (good.getPrice() * amount) + ChatColor.RESET + " coins.");
            ItemStack itemStack = good.getTheItem().clone();
            itemStack.setAmount(itemStack.getAmount() * amount);
            player.getInventory().addItem(itemStack);
        }

    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        if (inv == null) return;

        String invName = inv.getName();
        if (!invName.equals(menuTitle)) return;

        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        String name = meta.getDisplayName();
        if (name == null) return;

        Player player = (Player) event.getWhoClicked();

        if (name.equals(CLOSE_NAME)) {
            player.closeInventory();
            return;
        }
        if (event.isShiftClick()) {
            player.closeInventory();
            waitingForInputList.put(player, ChatColor.stripColor(name));
            player.sendMessage("please enter...");
        } else {
            select(player, ChatColor.stripColor(name));
        }
    }

    @EventHandler
    public void onChat(PlayerChatEvent e) {
        if (waitingForInputList.containsKey(e.getPlayer())) {
            e.setCancelled(true);
            int amount = 0;
            try {
                amount = Integer.parseInt(e.getMessage());
            } catch (Exception ignored) {

            }
            if (amount == 0) {
                e.getPlayer().sendMessage("please enter a valid integer!");
            } else {
                String str = waitingForInputList.get(e.getPlayer());
                select(e.getPlayer(), str, amount);
                waitingForInputList.remove(e.getPlayer());
            }
        }
    }

    private ItemStack createClose() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(CLOSE_NAME);
        item.setItemMeta(meta);

        return item;
    }


}
