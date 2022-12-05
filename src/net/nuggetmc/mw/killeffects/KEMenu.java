package net.nuggetmc.mw.killeffects;

import fr.bukkit.effectkill.effect.KillEffect;
import fr.bukkit.effectkill.effect.animation.*;
import net.md_5.bungee.api.ChatColor;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.energy.EnergyManager;
import net.nuggetmc.mw.mwclass.MWClassManager;
import net.nuggetmc.mw.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class KEMenu implements Listener {
    MegaWalls plugin = MegaWalls.getInstance();


    private static final String CLOSE_NAME = ChatColor.RED + "Close";


    private final MWClassManager classmanager = plugin.getClassManager();
    private final EnergyManager energyManager = plugin.getEnergyManager();
    private final String menuTitle = "Kill effects";
    public static List<KillEffect> effects = new ArrayList<>();


    private void registerke(KillEffect...array) {
        Collections.addAll(effects, array);
    }

    public KillEffect getKEByName(String name) {
        for (KillEffect ke:effects){
            if (ke.getName().equals(name)){
                return ke;
            }
        }
        return null;
    }

    public KEMenu() {
        registerke(
                new DropSoup(),
                new FrostFlame(),
                new HeadExplode(),
                new Heart(),
                new Redstone(),
                new Satan(),
                new Squid(),
                new Tornado(),
                new Wave()
        );
    }





    public void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, menuTitle);


        int i=0;
        for (KillEffect ke:effects){
            inv.setItem(i,ke.itemStack);
            i++;
        }
        //  inv.setItem(11, example);


        inv.setItem(49, createClose());
        player.openInventory(inv);
    }

    private void select(Player player, String name) {
        KillEffect killEffect=getKEByName(name);
        if (killEffect == null) return;

        select(player, killEffect);
    }



    public void select(Player player, KillEffect killEffect) {
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
            select(player, ChatColor.stripColor(name));
    }


    private ItemStack createClose() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(CLOSE_NAME);
        item.setItemMeta(meta);

        return item;
    }


}
