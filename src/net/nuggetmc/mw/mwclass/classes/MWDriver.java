package net.nuggetmc.mw.mwclass.classes;

import net.md_5.bungee.api.ChatColor;
import net.nuggetmc.mw.mwclass.MWClass;
import net.nuggetmc.mw.mwclass.info.Diamond;
import net.nuggetmc.mw.mwclass.info.MWClassInfo;
import net.nuggetmc.mw.mwclass.info.Playstyle;
import net.nuggetmc.mw.mwclass.items.MWItem;
import net.nuggetmc.mw.mwclass.items.MWKit;
import net.nuggetmc.mw.mwclass.items.MWPotions;
import net.nuggetmc.mw.utils.ActionBar;
import net.nuggetmc.mw.utils.PlayerSafeSet;
import net.nuggetmc.mw.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class MWDriver extends MWClass {

    private final PlayerSafeSet runnerList = new PlayerSafeSet();
    private final PlayerSafeSet abilitycache = new PlayerSafeSet();


    public MWDriver() {
        this.name = new String[]{"Driver", "DRI"};
        this.icon = Material.IRON_FENCE;
        this.color = ChatColor.DARK_AQUA;

        this.playstyles = new Playstyle[]{
                Playstyle.RUSHER,
                Playstyle.FIGHTER
        };

        this.diamonds = new Diamond[]{
                Diamond.LEGGINGS
        };

        this.classInfo = new MWClassInfo(
                "Ride",
                "Make the closest player ride you\n when they are in a &a20 &rblocks radius, gaining Strength I,resistance &aI&r and jump_boost &aII&r for &a5 &rseconds,\ngiving the player &a1&r second of slowness in the max(255) level.\nCooldown:&a20&rs.",
                "L Runner",
                "Once you are below 7 HP,you get 5 seconds of Absorption XX(20) , speed &aIII&r and jump boost &aIII&r for 12 seconds.\nIf that damage cause you to be dead,it will be cancelled.\nCooldown: &a50 &rseconds.",
                "Solo handjob god",
                "Once you were shoot by a player,you automatically throw 10 snowball to where you face,gaining Absorption I for 2 seconds",
                "Energy",
                "mining any blocks gives you 1 energy!"
        );

        this.classInfo.addEnergyGainType("Melee", 15);
        this.classInfo.addEnergyGainType("Bow", 15);
        this.classInfo.addEnergyGainType("Block Break", 1);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getEntity() instanceof Player)) return;
        Player victim = (Player) e.getEntity();
        if (manager.get(victim) != this) {
            return;
        }
        if (runnerList.contains(victim)) {
            return;
        }
        if (!(victim.getHealth() - e.getDamage() <= 7)) return;
        if ((victim.getHealth() - e.getDamage() <= 0)) e.setCancelled(true);
        runnerList.add(victim);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 12 * 20, 2));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 12 * 20, 2));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 5 * 20, 19), true);
        victim.sendMessage(this.getColor() + "You have became super runner! L runner");
        new Thread(() -> Bukkit.getScheduler().runTaskLater(plugin, () -> {
            //Cool down finished
            runnerList.remove(victim);
        }, 50 * 20)).start();
    }

    @EventHandler
    public void onArrowDMG(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getEntity() instanceof Player)) return;
        Player victim = (Player) e.getEntity();
        if (manager.get(victim) != this) {
            return;
        }
        //  if (victim== energyManager.validate(e)) return;
        if (!(e.getDamager() instanceof Arrow)) return;
        if (!(((Arrow) e.getDamager()).getShooter() instanceof Player)) return;
        int i = 0;
        while (i < 10) {
            victim.throwSnowball();
            i++;
        }
        victim.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2 * 20, 0));

    }

    @Override
    public String getActionBar(Player player) {
        String ride = this.getColor() + "Ride " + (abilitycache.contains(player) ? ChatColor.RED + "✖" : ChatColor.GREEN + "✔") + ChatColor.RESET;
        String runner = this.getColor() + "Ultimate Runner " + (runnerList.contains(player) ? ChatColor.RED + "✖" : ChatColor.GREEN + "✔") + ChatColor.RESET;
        return ride + "       " + runner;
    }


    @Override
    public void ability(Player player) {
        if (abilitycache.contains(player)) {
            return;
        }
        Player target = PlayerUtils.getClosestEnemyInRange(player, 20);
        if (target == null) {
            ActionBar.send(player, "No players within " + ChatColor.RED + 20 + ChatColor.RESET + " blocks!");
        } else {
            abilitycache.add(player);
            energyManager.clear(player);
            player.setPassenger(target);
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 254));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 5 * 20, 1));
            new Thread(() -> Bukkit.getScheduler().runTaskLater(plugin, () -> {
                //Cool down finished
                abilitycache.remove(player);
            }, 17 * 20)).start();
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.getEntity().eject();
        e.getEntity().leaveVehicle();
    }


    @Override
    public void hit(EntityDamageByEntityEvent event) {
        super.hit(event);
        if (event.isCancelled()) return;
        
        Player player = (Player) event.getDamager();
        if (player == null) return;

        if (manager.get(player) != this) return;


        energyManager.add(player, 15);

    }


    @Override
    public void assign(Player player) {
        Map<Integer, ItemStack> items;


        Map<Enchantment, Integer> swordEnch = new HashMap<>();
        swordEnch.put(Enchantment.DURABILITY, 10);
        swordEnch.put(Enchantment.DAMAGE_ALL, 1);

        Map<Enchantment, Integer> armorEnch = new HashMap<>();
        armorEnch.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        armorEnch.put(Enchantment.DURABILITY, 10);

        ItemStack sword = MWItem.createSword(this, Material.IRON_SWORD, swordEnch, player);
        ItemStack tool = MWItem.createTool(this, Material.DIAMOND_PICKAXE);
        ItemStack leggings = MWItem.createArmor(this, Material.DIAMOND_LEGGINGS, armorEnch);

        List<ItemStack> potions = MWPotions.createBasic(this, 2, 7, 2);

        items = MWKit.generate(this, sword, null, tool, null, potions, null, null, leggings, null, null);


        MWKit.assignItems(player, items);
        runnerList.remove(player);
        abilitycache.remove(player);
    }

    @EventHandler
    public void gathering(BlockBreakEvent e) {
        Player player = e.getPlayer();

        if (manager.get(player) == this) {
            if (e.getBlock().getType() != Material.YELLOW_FLOWER && e.getBlock().getType() != Material.LONG_GRASS && e.getBlock().getType() != Material.SAPLING) {
                energyManager.add(player, 1);
            }


        }
    }
}
