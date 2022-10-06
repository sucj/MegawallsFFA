package net.nuggetmc.mw.mwclass.classes;

import net.md_5.bungee.api.ChatColor;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.combat.CombatManager;
import net.nuggetmc.mw.mwclass.MWClass;
import net.nuggetmc.mw.mwclass.info.Diamond;
import net.nuggetmc.mw.mwclass.info.MWClassInfo;
import net.nuggetmc.mw.mwclass.info.Playstyle;
import net.nuggetmc.mw.mwclass.items.MWItem;
import net.nuggetmc.mw.mwclass.items.MWKit;
import net.nuggetmc.mw.mwclass.items.MWPotions;
import net.nuggetmc.mw.special.TeamsManager;
import net.nuggetmc.mw.utils.FireworkEffectPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class MWArcanist extends MWClass {

    Map<Player, Integer> dmgcount = new HashMap<>();
    Set<Player> cooldownCache = new HashSet<>();

    public MWArcanist() {
        this.name = new String[]{"奥术师", "Arcanist", "ARC"};
        this.icon = Material.FIREWORK;
        this.color = ChatColor.AQUA;

        this.playstyles = new Playstyle[]{
                Playstyle.CONTROL,
                Playstyle.FIGHTER

        };

        this.diamonds = new Diamond[]{
                Diamond.SWORD,
                Diamond.LEGGINGS
        };

        this.classInfo = new MWClassInfo(
                "Arcane Beam",
                "§7Cast a beam that hits players for §7§a2§7 damage\n§7This beam pierces through enemies and does §7splash damage\n §7Your beam can hit enemies up to 34 blocks away",
                "Tempest",
                "§7When you kill a player you gain\n §7Regeneration II for §a5§7 seconds and Speed §7§aIII§7 for 6 seconds \n§7§7When earning an assist, you gain Speed §7§aII§7 for 3 seconds, and heal for 1 HP",
                "Arcane Explosion",
                "§7After getting attacked §a5§7 times, you §7will explode, dealing 2§7 damage to enemies around §7you in a 5x5 area.\n §7You receive 34 Energy when this activates.\n§7Cooldown: §a1s",
                "Arcane Mining",
                "§7Mining ore grants you §a25§7 energy."

        );

        this.classInfo.addEnergyGainType("Melee", 35);
        this.classInfo.addEnergyGainType("Bow", 35);
    }

    @Override
    public void ability(Player player) {
        energyManager.clear(player);
        shoot(player, 12);
    }


    @Override
    public void hit(EntityDamageByEntityEvent event) {
        super.hit(event);
        if (event.isCancelled()) return;
        Player player = energyManager.validate(event);
        if (player == null) return;

        if (manager.get(player) == this) {
            energyManager.add(player, 35);
        }
        Player victim = (Player) event.getEntity();
        if (manager.get(victim) == this) {


            if (!dmgcount.containsKey(victim)) {
                dmgcount.put(victim, 0);
            } else {
                dmgcount.put(victim, (dmgcount.get(victim) + 1) % 5);
            }
            if (dmgcount.get(victim) == 0) {
                if (cooldownCache.contains(victim)) return;
                cooldownCache.add(victim);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    cooldownCache.remove(victim);
                }, 20);
                energyManager.add(victim, 34);
                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.playSound(victim.getLocation(), Sound.EXPLODE, 1, 1);
                }
                for (Entity ent : victim.getNearbyEntities(2, 2, 2)) {
                    if (ent instanceof Player) {
                        if (combatManager.isInCombat(victim)) {
                            if (teamsManager.isOnSameTeam(victim, ((Player) ent))) {
                                continue;
                            }
                        }
                        ((Player) ent).damage(2);
                    }
                }
            }

        }
    }

    @EventHandler
    public void gathering(BlockBreakEvent e) {
        Player player = e.getPlayer();

        if (manager.get(player) == this) {
            Block block = e.getBlock();
            if (block.getType().name().toLowerCase().contains("ore")) {
                energyManager.add(player, 20);
            }

        }
    }

    @Override
    public String getActionBar(Player player) {


        return (this.getColor() + ChatColor.BOLD.toString() + "Arcane Explosion " + (dmgcount.get(player) == 0 ? ChatColor.GREEN + ChatColor.BOLD.toString() + "✔" : ChatColor.RED.toString() + ChatColor.BOLD + dmgcount.get(player)) + ChatColor.RESET);


    }


    @Override
    public void assign(Player player) {
        Map<Integer, ItemStack> items;

        if (MWKit.contains(this)) {
            items = MWKit.fetch(this);
        } else {
            Map<Enchantment, Integer> swordEnch = new HashMap<>();
            swordEnch.put(Enchantment.DURABILITY, 10);

            Map<Enchantment, Integer> armorEnch = new HashMap<>();
            armorEnch.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
            armorEnch.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
            armorEnch.put(Enchantment.DURABILITY, 10);

            ItemStack sword = MWItem.createSword(this, Material.DIAMOND_SWORD, swordEnch);
            ItemStack bow = MWItem.createBow(this, null);
            ItemStack tool = MWItem.createTool(this, Material.DIAMOND_PICKAXE);
            ItemStack leggings = MWItem.createArmor(this, Material.DIAMOND_LEGGINGS, armorEnch);

            List<ItemStack> potions = MWPotions.createBasic(this, 2, 8, 2);

            items = MWKit.generate(this, sword, bow, tool, null, null, potions, null, null, leggings, null, null);
        }
        MWKit.assignItems(player, items);
        if (dmgcount.containsKey(player)) {
            dmgcount.replace(player, 0);
        } else {
            dmgcount.put(player, 0);
        }
        if (cooldownCache.contains(player)) {
            cooldownCache.remove(player);
        }

    }

    @EventHandler
    public void onTempest(PlayerDeathEvent e) {
        if (!combatManager.isInCombat(e.getEntity())) return;
        if (!(e.getEntity().getKiller() instanceof Player)) return;
        Player killer = (Player) e.getEntity().getKiller();
        if (manager.get(killer) != this) return;
        double damage = 0.5;
        for (int i = 0; i < 9; i++) {
            damage += 0.5;
        }
        e.getEntity().getKiller().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 6, 2));
        int time = (int) (20 * damage);
        e.getEntity().getKiller().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, time, 1));
    }


    MegaWalls plugin = MegaWalls.getInstance();
    TeamsManager teamsManager = plugin.getTeamsManager();
    CombatManager combatManager = plugin.getCombatManager();

    List<Entity> getNearbyEntites(Location l, int size) {
        List<Entity> entities = new ArrayList<Entity>();
        for (Entity ent : l.getWorld().getEntities()) {
            if (!(ent instanceof LivingEntity)) continue;
            if (!(ent instanceof Player)) continue;
            if (ent.isDead()) continue;
            if (ent == null) continue;
            if (!ent.getWorld().equals(l.getWorld())) continue;
            if (l.distance(ent.getLocation()) <= size) {
                entities.add(ent);
            }
        }
        return entities;
    }

    void shoot(Player p, double damage) {
        for (Block b : p.getLineOfSight((HashSet<Byte>) null, 50)) {
            try {
                FireworkEffectPlayer.playFirework(
                        p.getWorld(), b.getLocation(),
                        FireworkEffect.builder().with(FireworkEffect.Type.BURST)
                                .withColor(Color.WHITE).build());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            for (Entity ent : getNearbyEntites(b.getLocation(), 2)) {
                if (ent instanceof Player) {
                    Player nearby = (Player) ent;
                    if (nearby == p) continue;
                    if (MegaWalls.getInstance().getCombatManager().isInCombat(p)) {
                        if (teamsManager.isOnSameTeam(p, (Player) ent)) {
                            continue;
                        }
                    }
                    nearby.damage(damage, p);
                }
            }
        }
    }

}
