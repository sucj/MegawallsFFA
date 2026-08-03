package net.nuggetmc.mw.mwclass.classes;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.BlockStationary;
import net.nuggetmc.mw.mwclass.MWClass;
import net.nuggetmc.mw.mwclass.info.Diamond;
import net.nuggetmc.mw.mwclass.info.MWClassInfo;
import net.nuggetmc.mw.mwclass.info.Playstyle;
import net.nuggetmc.mw.mwclass.items.MWItem;
import net.nuggetmc.mw.mwclass.items.MWKit;
import net.nuggetmc.mw.mwclass.items.MWPotions;
import net.nuggetmc.mw.special.specialItems.MegaBreaker;
import net.nuggetmc.mw.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class MWGuardian extends MWClass {
    PlayerSafeSet extrimityList = new PlayerSafeSet();
    PlayerSafeSet suckList = new PlayerSafeSet();
    PlayerSafeSet waterList = new PlayerSafeSet();
    PlayerSafeSet multiplyList = new PlayerSafeSet();
    Map<Player, Long> shuaQiCooldown = new HashMap<>();


    public MWGuardian() {
        this.name = new String[]{"Guardian", "GUA"};
        this.icon = Material.MOB_SPAWNER;
        this.color = ChatColor.DARK_GREEN;

        this.playstyles = new Playstyle[]{
                Playstyle.CONTROL,
                Playstyle.DAMAGE
        };

        this.diamonds = new Diamond[]{
                Diamond.BOOTS,
                //Diamond.SWORD
        };

        this.classInfo = new MWClassInfo(
                "Curse laser",
                "Shoot a laser to the closest player in a &a10 &rblocks radius,\n dealing &a5&r true damage , gaining Speed &aII&r for &a5 &rseconds.",
                "Extremity",
                "Once you are below 20 HP,every hit will heal you &a3&r HP in &a8&r seconds,you gain &a4&r seconds of resistance &aII&r.\nIf that damage cause you to be dead,it will be cancelled.\nCooldown: &a13 &rseconds.",
                "Ruins guardian",
                "If you are in water, you will deal &a+75%&r damage for &a5&r seconds,gaining regeneration &aI&r for the next &a3&r seconds.\nCooldown:&a30&r seconds.",
                "耍起",
                "Once you break a block by your pickaxe(except Mega Breaker),the block will be turned into water instead.It will be turned into air after 5s." +
                        "\nCooldown:1.5s."
        );

        this.classInfo.addEnergyGainType("Melee", 15);
        this.classInfo.addEnergyGainType("Bow", 15);
    }

    @Override
    public String getActionBar(Player player) {
        String ext = this.getColor() + "Extrimity " + (extrimityList.contains(player) ? ChatColor.RED + "✖" : ChatColor.GREEN + "✔") + ChatColor.RESET;
        String rg = this.getColor() + "Ruins Guardian " + (waterList.contains(player) ? ChatColor.RED + "✖" : ChatColor.GREEN + "✔") + ChatColor.RESET;
        String sq = this.getColor() + "耍起 " + (((!shuaQiCooldown.containsKey(player))||System.currentTimeMillis()-shuaQiCooldown.get(player)>=1500) ? ChatColor.GREEN + "✔" : ChatColor.RED + Double.toString(MathUtils.round((1500-(System.currentTimeMillis()-shuaQiCooldown.get(player)))/1000.0,1))+"s") + ChatColor.RESET;
        return ActionBar.joinActionBar(ext,rg,sq);
    }
    @EventHandler
    public void onGathering(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (manager.get(player) != this) {
            return;
        }
        if (MegaBreaker.INSTANCE.check(player.getItemInHand())){
            return;
        }
        if (shuaQiCooldown.containsKey(player)){
            return;
        }
        if (e.getBlock().getType().equals(Material.DIAMOND_ORE)){
            return;
        }
        shuaQiCooldown.put(player,System.currentTimeMillis());
        Bukkit.getScheduler().runTaskLater(plugin, () -> shuaQiCooldown.remove(player), (long) (1.5*20));
        e.setCancelled(true);
        Block block = e.getBlock();
        for (ItemStack drop:block.getDrops()){
            block.getWorld().dropItem(block.getLocation(),drop);
        }
        e.getBlock().setType(Material.STATIONARY_WATER,false);

        Bukkit.getScheduler().runTaskLater(plugin, () -> block.setType(Material.AIR),5*20);

    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getEntity() instanceof Player)) return;
        Player victim = (Player) e.getEntity();
        if (manager.get(victim) != this) {
            return;
        }
        if (extrimityList.contains(victim)) {
            return;
        }
        if (!(victim.getHealth() - e.getDamage() <= 20)) return;
        if ((victim.getHealth() - e.getDamage() <= 0)) e.setCancelled(true);
        extrimityList.add(victim);
        suckList.add(victim);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 4 * 20, 1));
        victim.sendMessage(this.getColor() + "You have activated extremity!");
        new Thread(() -> Bukkit.getScheduler().runTaskLater(plugin, () -> {
            //Cool down finished
            extrimityList.remove(victim);
        }, 13 * 20)).start();
        new Thread(() -> Bukkit.getScheduler().runTaskLater(plugin, () -> {
            //Cool down finished
            suckList.remove(victim);
        }, 8 * 20)).start();

    }

    @EventHandler
    public void onSuck(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getEntity() instanceof Player)) return;
        
        Player player = EventDumper.INSTANCE.dumpDamagerPlayer(e);
        if (player == null) return;
        if (plugin.getTeamsManager().isOnSameTeam(player, (Player) e.getEntity())) return;
        if (manager.get(player) != this) {
            return;
        }
        if (!suckList.contains(player)) {
            return;
        }
        double finalhealth = player.getHealth() + 3;
        player.setHealth(Math.min(finalhealth, player.getMaxHealth()));


    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (manager.get(e.getPlayer()) != this) return;
        if (waterList.contains(e.getPlayer())) return;
        if (e.getPlayer().getLocation().getBlock().getType() == Material.WATER || e.getPlayer().getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
            waterList.add(e.getPlayer());
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 0));
            e.getPlayer().sendMessage("You have activated Ruins guardian!");
            multiplyList.add(e.getPlayer());
            new Thread(() -> Bukkit.getScheduler().runTaskLater(plugin, () -> {
                //Cool down finished
                waterList.remove(e.getPlayer());
            }, 30 * 20)).start();
            new Thread(() -> Bukkit.getScheduler().runTaskLater(plugin, () -> {
                //multiply effect finished
                multiplyList.remove(e.getPlayer());
            }, 5 * 20)).start();
        }

    }

    @EventHandler
    public void onMultiply(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getEntity() instanceof Player)) return;

        Player player = (Player) EventDumper.INSTANCE.dumpDamagerPlayer(e);
        if (player == null) return;
        if (manager.get(player) != this) {
            return;
        }
        if (!waterList.contains(player)) return;
        if (!multiplyList.contains(player)) return;
        e.setDamage(e.getDamage() * 1.75);
    }


    @Override
    public void ability(Player player) {
        PlayerSafeSet targets = new PlayerSafeSet();
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            if (!(!plugin.getCombatManager().isInCombat(player1) || player1.isDead() || player1.getGameMode() == GameMode.CREATIVE || (player1.getLocation().distance(player.getLocation()) > 15) || player1.equals(player))) {
                targets.add(player1);
            }
        }
        if (targets.isEmpty()) {
            ActionBar.send(player, "No players within " + ChatColor.RED + 15 + ChatColor.RESET + " blocks!");
        } else {
            energyManager.clear(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 1));
            ArrayList<Player> arrayList = new ArrayList<>(targets.size());
            for (int i = 0; i < targets.size(); i++) {
                arrayList.add(targets.toArray()[i]);
            }
            arrayList.sort(Comparator.comparingDouble(player2 -> player.getEyeLocation().distance(player2.getLocation())));
            mwhealth.trueDamage(arrayList.get(0), 5d, player);
        }
    }


    @Override
    public void hit(EntityDamageByEntityEvent event) {
        super.hit(event);
        if (event.isCancelled()) return;
        
        Player player = (Player) EventDumper.INSTANCE.dumpDamagerPlayer(event);
        if (player == null) return;

        if (manager.get(player) != this) return;


        energyManager.add(player, 15);

    }


    @Override
    public void assign(Player player) {
        Map<Integer, ItemStack> items;


        Map<Enchantment, Integer> swordEnch = new HashMap<>();
        swordEnch.put(Enchantment.DURABILITY, 10);

        Map<Enchantment, Integer> armorEnch = new HashMap<>();
        armorEnch.put(Enchantment.DEPTH_STRIDER, 5);
        armorEnch.put(Enchantment.DURABILITY, 10);

        ItemStack sword = MWItem.createSword(this, Material.IRON_SWORD, swordEnch, player);
        ItemStack tool = MWItem.createTool(this, Material.DIAMOND_PICKAXE);
        ItemStack boots = MWItem.createArmor(this, Material.DIAMOND_BOOTS, armorEnch);

        List<ItemStack> potions = MWPotions.createBasic(this, 1, 8, 2);

        items = MWKit.generate(this, sword, null, tool, null, potions, null, null, null, boots, null);


        MWKit.assignItems(player, items);
        extrimityList.remove(player);
        suckList.remove(player);
        waterList.remove(player);
        multiplyList.remove(player);
    }
}
