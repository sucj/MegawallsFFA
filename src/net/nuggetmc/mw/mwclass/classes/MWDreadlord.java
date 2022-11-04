package net.nuggetmc.mw.mwclass.classes;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.mwclass.MWClass;
import net.nuggetmc.mw.mwclass.info.Diamond;
import net.nuggetmc.mw.mwclass.info.MWClassInfo;
import net.nuggetmc.mw.mwclass.info.Playstyle;
import net.nuggetmc.mw.mwclass.items.MWItem;
import net.nuggetmc.mw.mwclass.items.MWKit;
import net.nuggetmc.mw.mwclass.items.MWPotions;
import net.nuggetmc.mw.utils.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class MWDreadlord extends MWClass {

    private final Set<ArmorStand> armorStands = new HashSet<>();
    private final Map<Player, Integer> increment = new HashMap<>();

    public MWDreadlord() {
        this.name = new String[]{"恐惧魔王", "Dreadlord", "DRE"};
        this.icon = Material.NETHER_BRICK_ITEM;
        this.color = ChatColor.DARK_RED;

        this.playstyles = new Playstyle[]{
                Playstyle.RUSHER,
                Playstyle.DAMAGE
        };

        this.diamonds = new Diamond[]{
                Diamond.SWORD,
                Diamond.HELMET
        };

        this.classInfo = new MWClassInfo(
                "Shadow Burst",
                "Fire three wither skulls at once dealing a total of &a8 &rtrue damage.",
                "Soul Eater",
                "Every &a5 &rattacks will restore 3 hunger and &a2 HP&r.",
                "Soul Siphon",
                "Gain Strength I and Regeneration I for &a5 &rseconds on kill.",
                "Dark Matter",
                "Every &a1 &riron ore will be auto-smelted when mined, dropping an iron ingot."
        );

        this.classInfo.addEnergyGainType("Melee", 10);
        this.classInfo.addEnergyGainType("Bow", 10);
    }

    @Override
    public void ability(Player player) {
        energyManager.clear(player);

        for (int i = 1; i <= 3; i++) {
            final WitherSkull witherSkull = (WitherSkull)player.launchProjectile(WitherSkull.class);
            witherSkull.setMetadata("MegaWalls", (MetadataValue)new FixedMetadataValue(
                    (Plugin)MegaWalls.getInstance(), plugin.getTeamsManager().getTeamOfPlayer(player)));
            witherSkull
                    .setVelocity(player.getLocation().add(0.0D, 0.0D, i * 2.0D).getDirection());
            (new BukkitRunnable() {
                public void run() {
                    if (witherSkull.isDead() || witherSkull.isOnGround()) {

                            ParticleUtils.play(EnumParticle.EXPLOSION_HUGE, witherSkull.getLocation(), 0.0F, 0.0F, 0.0F, 1.0F, 1);

                        witherSkull.getWorld()
                                .playSound(witherSkull.getLocation(), Sound.EXPLODE, 1.0F, 0.0F);
                        for (Player player1 : PlayerUtils.getNearbyPlayers((Entity)witherSkull, 10.0D)) {

                            if (player1.getGameMode().equals(GameMode.SPECTATOR) || plugin.getTeamsManager().isOnSameTeam(player,player1))
                                continue;
                            mwhealth.trueDamage(player1,8,player);
                        }
                        if (!witherSkull.isDead())
                            witherSkull.remove();
                        cancel();
                        return;
                    }

                        ParticleUtils.play(EnumParticle.LAVA, witherSkull.getLocation(), 0.0F, 0.0F, 0.0F, 1.0F, 5);

                }
            }).runTaskTimer((Plugin)MegaWalls.getInstance(), 0L, 1L);
        }
    }

    private void witherSkullHit(Player player, Location loc, Player hit) {
        Set<Player> playersToDamage = new HashSet<>();
        if ((hit != null) && plugin.getTeamsManager().isOnSameTeam(player, hit)) playersToDamage.add(hit);

        WorldUtils.createNoDamageExplosion(loc, 2);

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (player == target) continue;
            if (plugin.getTeamsManager().isOnSameTeam(player, target)) continue;
            if (playersToDamage.contains(target)) continue;
            if (target.getWorld() != loc.getWorld()) continue;

            if (loc.distance(target.getLocation()) < 1) {
                playersToDamage.add(target);
            }
        }

        playersToDamage.forEach(p -> mwhealth.trueDamage(p, 2.666667, player));
    }

    @EventHandler
    public void cancelArmorStand(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof ArmorStand)) return;

        ArmorStand stand = (ArmorStand) event.getEntity();

        if (armorStands.contains(stand)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void hit(EntityDamageByEntityEvent event) {
        super.hit(event);
        if (event.isCancelled()) return;
        Player player = energyManager.validate(event);
        if (player == null) return;

        if (manager.get(player) != this) return;

        if (!increment.containsKey(player)) {
            increment.put(player, 0);
        } else {
            increment.put(player, (increment.get(player) + 1) % 5);
        }

        if (increment.get(player) == 4) {
            mwhealth.heal(player, 2);
            mwhealth.feed(player, 3);

            ParticleUtils.play(EnumParticle.HEART, player.getEyeLocation(), 0.5, 0.5, 0.5, 0.15, 1);
        }

        energyManager.add(player, 10);
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player player = victim.getKiller();

        if (player == null || victim == player) return;

        if (manager.get(player) == this) {
            PotionUtils.effect(player, PotionEffectType.INCREASE_DAMAGE, 5);
            PotionUtils.effect(player, PotionEffectType.REGENERATION, 5);
        }
    }

    @EventHandler
    public void gathering(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (manager.get(player) == this) {
            Block block = event.getBlock();

            if (block.getType() == Material.IRON_ORE) {
                block.setType(Material.AIR);

                Location loc = block.getLocation().add(0.5, 0.5, 0.5);
                World world = block.getWorld();

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    world.dropItem(loc, new ItemStack(Material.IRON_INGOT));
                }, 2);
            }
        }
    }

    @Override
    public void assign(Player player) {
        Map<Integer, ItemStack> items;

        if (MWKit.contains(this)) {
            items = MWKit.fetch(this);
        } else {
            Map<Enchantment, Integer> swordEnch = new HashMap<>();
            swordEnch.put(Enchantment.DAMAGE_UNDEAD, 1);
            swordEnch.put(Enchantment.DURABILITY, 10);

            Map<Enchantment, Integer> armorEnch = new HashMap<>();
            armorEnch.put(Enchantment.DURABILITY, 10);
            armorEnch.put(Enchantment.PROTECTION_FIRE, 1);
            armorEnch.put(Enchantment.PROTECTION_EXPLOSIONS, 2);

            ItemStack sword = MWItem.createSword(this, Material.DIAMOND_SWORD, swordEnch);
            ItemStack bow = MWItem.createBow(this, null);
            ItemStack tool = MWItem.createTool(this, Material.DIAMOND_PICKAXE);
            ItemStack helmet = MWItem.createArmor(this, Material.DIAMOND_HELMET, armorEnch);

            List<ItemStack> potions = MWPotions.createBasic(this, 2, 8, 2);

            items = MWKit.generate(this, sword, null, tool, null, null, potions, helmet, null, null, null, null);
        }

        MWKit.assignItems(player, items);
    }
}
