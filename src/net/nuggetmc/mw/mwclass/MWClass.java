package net.nuggetmc.mw.mwclass;

import net.md_5.bungee.api.ChatColor;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.combat.CombatManager;
import net.nuggetmc.mw.energy.EnergyManager;
import net.nuggetmc.mw.mwclass.info.Diamond;
import net.nuggetmc.mw.mwclass.info.MWClassInfo;
import net.nuggetmc.mw.mwclass.info.Playstyle;
import net.nuggetmc.mw.special.SpecialEventsManager;
import net.nuggetmc.mw.special.specialItems.AOTR;
import net.nuggetmc.mw.utils.MWHealth;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public abstract class MWClass implements Listener {

    protected final MegaWalls plugin;
    protected final MWClassManager manager;
    protected final MWHealth mwhealth;
    protected final EnergyManager energyManager;

    protected String[] name;
    protected Material icon;
    protected ItemStack iconAsItemStack = null;
    protected ChatColor color;
    protected Playstyle[] playstyles;
    protected Diamond[] diamonds;
    protected MWClassInfo classInfo;
    private final CombatManager combatManager;

    public MWClass() {
        this.plugin = MegaWalls.getInstance();
        this.manager = plugin.getClassManager();
        this.mwhealth = plugin.getMWHealth();
        this.energyManager = plugin.getEnergyManager();
        this.combatManager = plugin.getCombatManager();
    }

    public String getName() {
        return name[0];
    }

    protected Set<Player> inRange(Player player, double radius) {
        World world = player.getWorld();
        Location locUp = player.getEyeLocation();
        Set<Player> result = new HashSet<>();

        for (Player victim : Bukkit.getOnlinePlayers()) {
            if (world != victim.getWorld()) continue;

            Location loc = victim.getEyeLocation();

            if (locUp.distance(loc) <= radius && player != victim && !victim.isDead()) {
                result.add(victim);
            }
        }

        return result;
    }

    /*@EventHandler
    public void onBow(EntityDamageByEntityEvent e){
        if (e.getDamager()instanceof Arrow){
            if (e.getEntity() instanceof Player){
                ((Player) e.getEntity()).getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1*20, 0));
            }
        }
    }*/
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        this.hit(e);
    }

    public Material getIcon() {
        return icon;
    }

    public ItemStack getIconAsItemStack() {
        return iconAsItemStack;
    }

    public ChatColor getColor() {
        return color;
    }

    public Playstyle[] getPlaystyles() {
        return playstyles;
    }

    public Diamond[] getDiamonds() {
        return diamonds;
    }

    public MWClassInfo getInfo() {
        return classInfo;
    }

    public String getShortName() {
        return name[1];
    }

    public abstract void ability(Player player);

    public abstract void assign(Player player);

    public void hit(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;
        Player player = energyManager.validate(e);
        if (player == null) return;
        Player victim = (Player) e.getEntity();
        if (!combatManager.isInCombat(player) || !combatManager.isInCombat(victim)) {
            return;
        }
        if (MegaWalls.getInstance().getTeamsManager().isOnSameTeam(player, victim)) {
            e.setCancelled(true);
        }
        if (AOTR.INSTANCE.getInAotr().contains(player)) {
            player.sendMessage("You hit an enemy,so disabled your " + ChatColor.GOLD + "Speed Boost " + ChatColor.RESET + "ability!");
            player.setWalkSpeed(0.2f);
            AOTR.INSTANCE.getInAotr().remove(player);
        }
    }

    public int getPrice() {
        return 0;
    }

    public String getActionBar(Player player) {
        return null;
    }
}
