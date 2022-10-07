package net.nuggetmc.mw.mwclass.classes;

import net.md_5.bungee.api.ChatColor;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.mwclass.MWClass;
import net.nuggetmc.mw.mwclass.info.Diamond;
import net.nuggetmc.mw.mwclass.info.MWClassInfo;
import net.nuggetmc.mw.mwclass.info.Playstyle;
import net.nuggetmc.mw.mwclass.items.MWItem;
import net.nuggetmc.mw.mwclass.items.MWKit;
import net.nuggetmc.mw.mwclass.items.MWPotions;
import net.nuggetmc.mw.enums.EnumMaterial;
import net.nuggetmc.mw.special.TeamsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class MWShark extends MWClass {
    private HashMap<Player, HashSet<Block>> waterMap = new HashMap<>();

//    public HashMap<Player, HashSet<Block>> getWaterMap(){
//        return waterMap;
//    }
    public MWShark() {
        String[] var1 = new String[]{"鲨鱼", "Shark", "SRK"};
        this.name = var1;
        this.icon = Material.WATER_BUCKET;
        this.color = ChatColor.DARK_AQUA;
        Playstyle[] var2 = new Playstyle[]{Playstyle.FIGHTER, Playstyle.SUPPORT};
        this.playstyles = var2;
        Diamond[] var3 = new Diamond[]{Diamond.BOOTS};
        this.diamonds = var3;
        this.classInfo = new MWClassInfo("From the Depths", "§7Create a 7§7 block square area of water §7around you for §a5§7 seconds§7§8 ? §7You also receive Regeneration I for the §7duration of your ability§7§8 ? §7If an enemy interracts with your pool of §7water, they will receive Slowness I until the §7water pool disappears§7§8 ? §7§7", "Blood Rage", "§7§8 ? §7§7If you or an enemy within a 9 block §7radius is under 15 HP, you deal +§a21.5%§7 damage\n§7§8 ? §7§7This considers up to a maximum of 5 §7players, or +§a107.5%§7 damage§7§8 ? §7§7You and nearby allies deal +§a0.75§7 §7extra damage when standing or attacking enemies in §7the water that comes from your ability.§7§8 ? §7This has a maximum of +1.5 damage.", "Food Hunt", "§7§8 ? §7§7Kills grant Regeneration III for §8 §8§a4§7 seconds and replenishes 4 hunger.\n If there is allies in a 4 block range,you will heal for 3.5 HP.", "Sea Treasure", "Nothing here.");
        this.classInfo.addEnergyGainType("Melee", 20);
        this.classInfo.addEnergyGainType("Bow", 20);
    }

    public final HashMap<Player, HashSet<Block>> getWaterMap() {
        return this.waterMap;
    }

    public final void setWaterMap(HashMap<Player, HashSet<Block>> var1) {
        this.waterMap = var1;
    }

    public void ability(Player player) {
        this.energyManager.clear(player);
        if (!this.waterMap.containsKey(player)) {
            this.waterMap.put(player, new HashSet<>());
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
        int expand = 3;
        int posY = player.getLocation().getBlockY();
        Location location = player.getLocation();
        World world = player.getWorld();
        HashSet<Block> set = new HashSet<>();
        int i = -expand;

        for(int var8 = expand + 1; i < var8; ++i) {
            Block block = world.getBlockAt(location.getBlockX() + i, posY, location.getBlockZ());
            if (this.canBeReplaced(block)) {
                block.setType(Material.STATIONARY_WATER, false);
                HashSet<Block> var10000 = this.waterMap.get(player);
                if (var10000 != null) {
                    var10000.add(block);
                }

                if (!set.contains(block)) {
                    set.add(block);
                }

                int j = -expand;

                for(int var11 = expand + 1; j < var11; ++j) {
                    Block block1 = world.getBlockAt(location.getBlockX() + i, posY, location.getBlockZ() + j);
                    if (this.canBeReplaced(block1)) {
                        block1.setType(Material.STATIONARY_WATER, false);
                        var10000 = this.waterMap.get(player);
                        if (var10000 != null) {
                            var10000.add(block1);
                        }

                        if (!set.contains(block1)) {
                            set.add(block1);
                        }
                    }
                }
            }
        }

        if (!(set).isEmpty()) {
            Bukkit.getScheduler().runTaskLater((Plugin) MegaWalls.getInstance(), new Runnable() {

                @Override
                public void run() {
                    int i = 0;
                    Block[] lst = set.toArray(new Block[0]);
                    for(int var4 = set.size(); i < var4; ++i) {
                    	Block var10000 = lst[i];
                        Block block = var10000;
                        block.setType(Material.AIR);
                        HashSet<Block> var6 = getWaterMap().get(player);
                        if (var6 != null) {
                            var6.remove(block);
                        }
                    }
                }
            }, 100L);
        }

    }




    public void hit(EntityDamageByEntityEvent event) {
        super.hit(event);
        if (!event.isCancelled()) {
            Player var10000 = this.energyManager.validate(event);
            if (var10000 != null) {
                Player player = var10000;
                if (this.manager.get(player) == this) {
                    this.energyManager.add(player, 20);
                }
            }
        }
    }

    @EventHandler
    public final void onDamage(EntityDamageEvent e) {
        if (!e.isCancelled()) {
            if (e.getEntity() instanceof Player) {
                Entity var10000 = e.getEntity();
                Player victim = (Player)var10000;
                if (this.manager.get(victim) == this) {
                    ;
                }
            }
        }
    }

    public void assign(Player player) {
        Map<Integer, ItemStack> items = null;
        Map<Integer, ItemStack> var10000;
        if (MWKit.contains((MWClass)this)) {
            var10000 = MWKit.fetch((MWClass)this);
            items = var10000;
        } else {
            Map<Enchantment, Integer> swordEnch = new HashMap<>();
            Enchantment var10001 = Enchantment.DURABILITY;
            swordEnch.put(var10001, 10);
            Map<Enchantment, Integer> armorEnch = new HashMap<>();
            var10001 = Enchantment.DEPTH_STRIDER;
            armorEnch.put(var10001, 3);
            var10001 = Enchantment.PROTECTION_ENVIRONMENTAL;
            armorEnch.put(var10001, 2);
            var10001 = Enchantment.DURABILITY;
            armorEnch.put(var10001, 10);
            ItemStack sword = MWItem.createSword((MWClass)this, Material.DIAMOND_SWORD, swordEnch);
            ItemStack bow = MWItem.createBow((MWClass)this, null);
            ItemStack tool = MWItem.createTool((MWClass)this, Material.DIAMOND_PICKAXE);
            ItemStack boots = MWItem.createArmor((MWClass)this, Material.DIAMOND_BOOTS, armorEnch);
            List<ItemStack> potions = MWPotions.createBasic((MWClass)this, 2, 8, 2);
            var10000 = MWKit.generate((MWClass)this, sword, bow, tool, null, null, potions, null, null, null, boots, null);
            items = var10000;
        }

        MWKit.assignItems(player, items);
    }

    @EventHandler
    public final void onPhysics(BlockPhysicsEvent e) {
        Iterator<Player> var2 = this.waterMap.keySet().iterator();

        while(var2.hasNext()) {
            Player player = var2.next();
            Iterator<Block> var4 = this.waterMap.get(player).iterator();

            while(var4.hasNext()) {
                Block block = var4.next();
                if (Objects.equals(e.getBlock(), block)) {
                    e.setCancelled(true);
                    return;
                }
            }
        }

    }

    @EventHandler
    public final void onMove(PlayerMoveEvent e) {
        if (e.getPlayer().getLocation().getBlock().getType() == Material.WATER || e.getPlayer().getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
            Object[] b = this.forBlock(e);
            if (b == null) {
                return;
            }

            TeamsManager var10000 = this.plugin.getTeamsManager();
            Player var10001 = e.getPlayer();
            Object var10002 = b[1];
            if (var10000.isOnSameTeam(var10001, (Player)var10002)) {
                return;
            }

            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 0));
        }

    }

    @EventHandler
    public final void onBloodRageAllies(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (this.isInAlliesPool(player)) {
            this.plugin.bloodRageList.add(player);
        } else if (this.plugin.bloodRageList.contains(player)) {
            this.plugin.bloodRageList.remove(player);
        }

    }

    @EventHandler
    public final void onBloodRageSelf(EntityDamageByEntityEvent event) {
        super.hit(event);
        if (!event.isCancelled()) {
            Player var10000 = this.energyManager.validate(event);
            if (var10000 != null) {
                Player player = var10000;
                if (this.manager.get(player) == this) {
                    double increaceAmount = 0.0D;
                    if (player.getHealth() < 15.0D) {
                        increaceAmount += 0.215D;
                    }

                    Iterator<? extends Player> var5 = Bukkit.getOnlinePlayers().iterator();

                    while(var5.hasNext()) {
                        Player p = (Player)var5.next();
                        if (!(p.getLocation().distance(player.getLocation()) > 9.0D)) {
                            TeamsManager var7 = this.plugin.getTeamsManager();
                            if (!var7.isOnSameTeam(player, p) && this.plugin.getCombatManager().isInCombat(p) && player.getWorld() == p.getWorld() && !p.isDead()) {
                                increaceAmount += 0.215D;
                            }
                        }
                    }

                    if (increaceAmount > 1.075D) {
                        increaceAmount = 1.075D;
                    }

                    event.setDamage(event.getDamage() + event.getDamage() * increaceAmount);
                }
            }
        }
    }

    @EventHandler
    public final void onKill(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player player = victim.getKiller();
        if (player != null && victim != player) {
            if (this.manager.get(player) == this) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 2));
                if (player.getFoodLevel() + 4 > 20) {
                    player.setFoodLevel(20);
                } else {
                    player.setFoodLevel(player.getFoodLevel() + 4);
                }

                boolean b = false;
                Iterator<? extends Player> var5 = Bukkit.getOnlinePlayers().iterator();

                while(var5.hasNext()) {
                    Player p = (Player)var5.next();
                    if (!(p.getLocation().distance(player.getLocation()) > 4.0D)) {
                        TeamsManager var10000 = this.plugin.getTeamsManager();
                        if (var10000.isOnSameTeam(player, p) && this.plugin.getCombatManager().isInCombat(p) && player.getWorld() == p.getWorld() && !p.isDead()) {
                            b = true;
                            break;
                        }
                    }
                }

                if (b) {
                    if (player.getHealth() + 3.5D > player.getMaxHealth()) {
                        player.setHealth(player.getMaxHealth());
                    } else {
                        player.setHealth(player.getHealth() + 3.5D);
                    }
                }
            }

        }
    }

    public final Object[] forBlock(PlayerMoveEvent e) {
        Iterator<Player> var2 = this.waterMap.keySet().iterator();

        while(var2.hasNext()) {
            Player player = var2.next();
            Iterator<Block> var4 = this.waterMap.get(player).iterator();

            while(var4.hasNext()) {
                Block block = (Block) var4.next();
                if (Objects.equals(block, e.getPlayer().getLocation().getBlock())) {
                    Object[] var6 = new Object[]{block, player};
                    return var6;
                }
            }
        }

        return null;
    }

    public final boolean isInAlliesPool(Player plr) {
        Iterator<Player> var2 = this.waterMap.keySet().iterator();

        while(true) {
            Player player;
            do {
                if (!var2.hasNext()) {
                    return false;
                }

                player = (Player) var2.next();
            } while(!this.plugin.getTeamsManager().isOnSameTeam(plr, player));

            Iterator<Block> var4 = this.waterMap.get(player).iterator();

            while(var4.hasNext()) {
                Block block = var4.next();
                if (Objects.equals(block, plr.getLocation().getBlock())) {
                    return true;
                }
            }
        }
    }

    public final boolean canBeReplaced(Block block) {
        Material var10000 = block.getType();
        boolean var2;
        switch(var10000 == null ? -1 : EnumMaterial.EnumMaterialMapping[var10000.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
                var2 = true;
                break;
            default:
                var2 = false;
        }

        return var2;
    }

}
