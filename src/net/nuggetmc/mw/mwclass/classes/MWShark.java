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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class MWShark extends MWClass {
    private HashMap<Player, HashSet<Block>> waterMap = new HashMap<>();

    public MWShark() {
        this.name = new String[]{"鲨鱼", "Shark", "SRK"};
        this.icon = Material.WATER_BUCKET;
        this.color = ChatColor.DARK_AQUA;
        this.playstyles = new Playstyle[]{Playstyle.FIGHTER, Playstyle.SUPPORT};
        this.diamonds = new Diamond[]{Diamond.BOOTS};
        this.classInfo = new MWClassInfo("From the Depths", "§7Create a 7§7 block square area of water §7around you for §a5§7 seconds§7§8 ? §7You also receive Regeneration I for the §7duration of your ability§7§8 ? §7If an enemy interracts with your pool of §7water, they will receive Slowness I until the §7water pool disappears§7§8 ? §7§7", "Blood Rage", "§7§8 ? §7§7If you or an enemy within a 9 block §7radius is under 15 HP, you deal +§a21.5%§7 damage\n§7§8 ? §7§7This considers up to a maximum of 5 §7players, or +§a107.5%§7 damage§7§8 ? §7§7You and nearby allies deal +§a0.75§7 §7extra damage when standing or attacking enemies in §7the water that comes from your ability.§7§8 ? §7This has a maximum of +1.5 damage.", "Food Hunt", "§7§8 ? §7§7Kills grant Regeneration III for §8 §8§a4§7 seconds and replenishes 4 hunger.\n If there is allies in a 4 block range,you will heal for 3.5 HP.", "Sea Treasure", "Nothing here.");
        this.classInfo.addEnergyGainType("Melee", 20);
        this.classInfo.addEnergyGainType("Bow", 20);
    }

    public final HashMap<Player, HashSet<Block>> getWaterMap() {
        return this.waterMap;
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
        int offset = -expand;

        for(int i = expand + 1; offset < i; ++offset) {
            Block block = world.getBlockAt(location.getBlockX() + offset, posY, location.getBlockZ());
            if (this.canBeReplaced(block)) {
                block.setType(Material.STATIONARY_WATER, false);
                HashSet<Block> blocks = this.waterMap.get(player);
                if (blocks != null) {
                    blocks.add(block);
                }

                set.add(block);

                int offsets = -expand;

                for(int index = expand + 1; offsets < index; ++offsets) {
                    Block block1 = world.getBlockAt(location.getBlockX() + offset, posY, location.getBlockZ() + offsets);
                    if (this.canBeReplaced(block1)) {
                        block1.setType(Material.STATIONARY_WATER, false);
                        blocks = this.waterMap.get(player);
                        if (blocks != null) {
                            blocks.add(block1);
                        }

                        set.add(block1);
                    }
                }
            }
        }

        if (!(set).isEmpty()) {
            Bukkit.getScheduler().runTaskLater(MegaWalls.getInstance(), () -> {
                int i = 0;
                Block[] blocks = set.toArray(new Block[0]);
                for(int index = set.size(); i < index; ++i) {
                    Block blockArray = blocks[i];
                    blockArray.setType(Material.AIR);
                    HashSet<Block> blockHashSet = getWaterMap().get(player);
                    if (blockHashSet != null) {
                        blockHashSet.remove(blockArray);
                    }
                }
            }, 100L);
        }

    }




    public void hit(EntityDamageByEntityEvent event) {
        super.hit(event);
        if (!event.isCancelled()) {
            Player player = this.energyManager.validate(event);
            if (player != null) {
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
                Entity entity = e.getEntity();
                Player victim = (Player)entity;
                this.manager.get(victim);
            }
        }
    }

    public void assign(Player player) {
        Map<Integer, ItemStack> items;
        Map<Integer, ItemStack> kit;
        if (MWKit.contains(this)) {
            kit = MWKit.fetch(this);
        } else {
            Map<Enchantment, Integer> swordEnch = new HashMap<>();
            Enchantment durability = Enchantment.DURABILITY;
            swordEnch.put(durability, 10);
            Map<Enchantment, Integer> armorEnch = new HashMap<>();
            durability = Enchantment.DEPTH_STRIDER;
            armorEnch.put(durability, 3);
            durability = Enchantment.PROTECTION_ENVIRONMENTAL;
            armorEnch.put(durability, 2);
            durability = Enchantment.DURABILITY;
            armorEnch.put(durability, 10);
            ItemStack sword = MWItem.createSword(this, Material.DIAMOND_SWORD, swordEnch);
            ItemStack bow = MWItem.createBow(this, null);
            ItemStack tool = MWItem.createTool(this, Material.DIAMOND_PICKAXE);
            ItemStack boots = MWItem.createArmor(this, Material.DIAMOND_BOOTS, armorEnch);
            List<ItemStack> potions = MWPotions.createBasic(this, 2, 8, 2);
            kit = MWKit.generate(this, sword, null, tool, null, null, potions, null, null, null, boots, null);
        }
        items = kit;

        MWKit.assignItems(player, items);
    }

    @EventHandler
    public final void onPhysics(BlockPhysicsEvent e) {

        for (Player player : this.waterMap.keySet()) {

            for (Block block : this.waterMap.get(player)) {
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
            Object[] blocks = this.forBlock(e);
            if (blocks == null) {
                return;
            }

            TeamsManager teamsManager = this.plugin.getTeamsManager();
            Player player = e.getPlayer();
            Object block = blocks[1];
            if (teamsManager.isOnSameTeam(player, (Player)block)) {
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
        } else this.plugin.bloodRageList.remove(player);

    }

    @EventHandler
    public final void onBloodRageSelf(EntityDamageByEntityEvent event) {
        super.hit(event);
        if (!event.isCancelled()) {
            Player player = this.energyManager.validate(event);
            if (player != null) {
                if (this.manager.get(player) == this) {
                    double increaceAmount = 0.0D;
                    if (player.getHealth() < 15.0D) {
                        increaceAmount += 0.215D;
                    }

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!(p.getLocation().distance(player.getLocation()) > 9.0D)) {
                            TeamsManager teamsManager = this.plugin.getTeamsManager();
                            if (!teamsManager.isOnSameTeam(player, p) && this.plugin.getCombatManager().isInCombat(p) && player.getWorld() == p.getWorld() && !p.isDead()) {
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
                player.setFoodLevel(Math.min(player.getFoodLevel() + 4, 20));

                boolean isValidPlayer = false;

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!(p.getLocation().distance(player.getLocation()) > 4.0D)) {
                        TeamsManager teamsManager = this.plugin.getTeamsManager();
                        if (teamsManager.isOnSameTeam(player, p) && this.plugin.getCombatManager().isInCombat(p) && player.getWorld() == p.getWorld() && !p.isDead()) {
                            isValidPlayer = true;
                            break;
                        }
                    }
                }

                if (isValidPlayer) {
                    player.setHealth(Math.min(player.getHealth() + 3.5D, player.getMaxHealth()));
                }
            }

        }
    }

    public final Object[] forBlock(PlayerMoveEvent e) {
        for (Player player : this.waterMap.keySet()) {
            for (Block block : this.waterMap.get(player)) {
                if (Objects.equals(block, e.getPlayer().getLocation().getBlock())) {
                    return new Object[]{block, player};
                }
            }
        }
        return null;
    }

    public final boolean isInAlliesPool(Player plr) {
        Iterator<Player> watermap = this.waterMap.keySet().iterator();

        while(true) {
            Player player;
            do {
                if (!watermap.hasNext()) {
                    return false;
                }

                player = watermap.next();
            } while(!this.plugin.getTeamsManager().isOnSameTeam(plr, player));

            for (Block block : this.waterMap.get(player)) {
                if (Objects.equals(block, plr.getLocation().getBlock())) {
                    return true;
                }
            }
        }
    }

    public final boolean canBeReplaced(Block block) {
        Material material = block.getType();
        boolean isValid;
        switch(material == null ? -1 : EnumMaterial.EnumMaterialMapping[material.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
                isValid = true;
                break;
            default:
                isValid = false;
        }

        return isValid;
    }

}
