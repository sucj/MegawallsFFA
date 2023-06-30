package net.nuggetmc.mw.special;

import com.earth2me.essentials.Essentials;
import com.joshargent.RegionPreserve.RegionPreservePlugin;
import fr.bukkit.effectkill.utils.Particle;
import io.isles.nametagapi.NametagAPI;
import net.md_5.bungee.api.ChatColor;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.mwclass.classes.MWCow;
import net.nuggetmc.mw.mwclass.classes.MWMole;
import net.nuggetmc.mw.utils.ItemUtils;
import net.nuggetmc.mw.utils.PlayerUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.util.*;

import static net.nuggetmc.mw.MegaWalls.OPBYPASSGM;

public class SpecialEventsManager implements Listener {
    MegaWalls plugin;
    public static RegionPreservePlugin rp;
    Set<Player> aotrCD = new HashSet<>();
    Set<Player> teleportNoFallDMG=new HashSet<>();
    public Set<Player> inAotr = new HashSet<>();
    static SpecialEventsManager instance;
    public static SpecialEventsManager getInstance(){
        return instance;
    }
    public static HashMap<Player, Boolean> canfire = new HashMap<>();

    public SpecialEventsManager() {
        this.plugin = MegaWalls.getInstance();
        rp = Bukkit.getPluginManager().getPlugin("RegionPreserve") == null ? null : (RegionPreservePlugin) Bukkit.getPluginManager().getPlugin("RegionPreserve");
        instance=this;
    }

    ///////////////////////////COW BUCKET
    SpecialItemUtils specialItemUtils = new SpecialItemUtils();

    @EventHandler
    public void onCowBucket(PlayerItemConsumeEvent e) {
        if (specialItemUtils.isCowBucket(e.getItem())) {
            Player player = e.getPlayer();
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 20, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 1));
            player.setFoodLevel(20);
            player.setSaturation(20);
            if (plugin.getClassManager().get(player) instanceof MWCow) {
                for (Player target : Bukkit.getOnlinePlayers()) {
                    if (player.getWorld() != target.getWorld()) continue;
                    if (target.isDead()) continue;
                    if (!plugin.getTeamsManager().isOnSameTeam(player, target)) continue;
                    if (player == target) continue;
                    if (target.getLocation().distance(player.getLocation()) > 7) continue;
                    target.setFoodLevel(20);
                    target.setSaturation(20);
                    double health = target.getHealth() + 3;
                    target.setHealth(Math.min(health, target.getMaxHealth()));
                    target.sendMessage("You have been healed by the Refreshing Sip of " + player.getName() + "!");
                }
            }
        }
    }

    ///////////////////////////ENDER CHEST
    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (plugin.getCombatManager().isInCombat(e.getPlayer()) && e.getBlock().getType() == Material.ENDER_CHEST) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (plugin.getCombatManager().isInCombat(e.getPlayer()) && e.getItemDrop().getItemStack().getType() == Material.ENDER_CHEST) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!e.getAction().name().contains("RIGHT")) return;
        if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) return;
        if (p.getItemInHand().getType() != Material.ENDER_CHEST) return;
        p.openInventory(p.getEnderChest());
    }

    ///////////////////////////PLAYERMOVEEVENT
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player plr = e.getPlayer();
        if (!plugin.getCombatManager().isInCombat(plr)) {
            return;
        }
        Location plrLocation = plr.getLocation();
        if (plr.getWorld().getEnvironment() == World.Environment.NORMAL) {
            //System.out.println(plr.getPlayerListName());
            String plrName = plr.getPlayerListName();
            double LowestDistance = Double.MAX_VALUE;
            Location LowestLocation = plr.getLocation();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!plugin.getCombatManager().isInCombat(p)) {
                    continue;
                }


                if (!plugin.getCompassManager().getCompassTargetMap().get(plr).equals(plugin.getTeamsManager().getTeamOfPlayer(p))) {
                    continue;
                }


                if (!p.getPlayerListName().equals(plrName)) {
                    // this is not yourself
                    if (p.getWorld().getEnvironment() == World.Environment.NORMAL) {
                        Location pLocation = p.getLocation();


                        if (LowestDistance > plrLocation.distance(pLocation)) {
                            LowestDistance = plrLocation.distance(pLocation);
                            LowestLocation = pLocation;
                        }

                    }
                }
            }
            if (LowestLocation == null) {
                return;
            }
            // we now have the lowest location and distance
            plr.setCompassTarget(LowestLocation);


        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getItem() == null) return;
        if (e.getAction().name().toLowerCase().contains("left")) {
            Player player = e.getPlayer();
            if (e.getItem().getType().equals(Material.COMPASS) && ItemUtils.isKitItem(e.getItem())) {
                plugin.getCompassManager().changeTrackingTarget(player);
            }
        }
        if(e.getItem().getType().equals(Material.BOW)){
            if (specialItemUtils.isTerm(e.getItem())) {
                termClick(e.getPlayer());
            }
        }

    }
    private void termClick(Player player){
        if (!canfire.containsKey(player)) {
            canfire.put(player, true);
        }
        Arrow a1;
        Arrow a2;
        Arrow a3;
        if (canfire.get(player)) {
            canfire.put(player, false);
            a1 = player.launchProjectile(Arrow.class);
            a1.setVelocity(a1.getVelocity().multiply(2.5));
            a1.setMetadata(MegaWalls.getMetadataValue(),MegaWalls.getFixedMetadataValue());

            a2 = player.launchProjectile(Arrow.class);

            a2.setCustomName("terminator");
            a2.setVelocity(rotateVector(a1.getVelocity(), 50.38));
            a2.setMetadata(MegaWalls.getMetadataValue(),MegaWalls.getFixedMetadataValue());

            a3 = player.launchProjectile(Arrow.class);

            a3.setCustomName("terminator");
            a3.setMetadata(MegaWalls.getMetadataValue(),MegaWalls.getFixedMetadataValue());
            player.playSound(player.getLocation(), Sound.SHOOT_ARROW, 1, 1);
            a3.setVelocity(rotateVector(a1.getVelocity(), -50.38));
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (a1.isValid()) {
                        a1.remove();
                    }
                    if (a2.isValid()) {
                        a2.remove();
                    }
                    if (a3.isValid()) {
                        a3.remove();
                    }
                }
            }.runTaskLater(plugin, 300);
            new BukkitRunnable() {
                @Override
                public void run() {
                    canfire.put(player, true);
                }
            }.runTaskLater(plugin, 87 / 13);

        }
    }
    public Vector rotateVector(Vector vector, double whatAngle) {
        double cos = Math.cos(whatAngle);
        double sin = Math.sin(whatAngle);
        double x = vector.getX() * cos + vector.getZ() * sin;
        double z = vector.getX() * -sin + vector.getZ() * cos;

        return vector.setX(x).setZ(z);
    }

    ///////////////////////////EXPORB
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.EXPERIENCE_ORB) {
            event.setCancelled(true);
        }
    }

    ///////////////////////////TELL ARROW DAMAGE
    @EventHandler
    public void onArrowDamageTell(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Arrow)) return;
        Arrow arrow = (Arrow) e.getDamager();
        Player victim = ((Player) e.getEntity()).getPlayer();
        if (arrow.getShooter() instanceof Player) {
            Player player = (Player) arrow.getShooter();

            player.playSound(player.getLocation(),Sound.SUCCESSFUL_HIT,1,0);
            player.sendMessage(ChatColor.YELLOW + victim.getDisplayName() + ChatColor.RESET + " is on " + (new BigDecimal(victim.getHealth()).setScale(1, BigDecimal.ROUND_HALF_UP)).doubleValue() + " health!");
        }
    }

    ///////////////////////////NO DAMAGE BEFORE JOINING
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (!MegaWalls.getInstance().getCombatManager().isInCombat(player)) {
                e.setCancelled(true);
            }
        }
    }

    ///////////////////////////RESPAWN
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if ((player.isOp() || event.getPlayer().hasPermission("mw.admin")) && OPBYPASSGM) {
            //
        } else {
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
        }
        MegaWalls.getInstance().getCombatManager().removeInCombat(event.getPlayer());
        if (Bukkit.getPluginManager().getPlugin("Essentials") != null && !Objects.equals(ChatColor.stripColor(((Essentials) Bukkit.getPluginManager().getPlugin("Essentials")).getUser(player).getNick()), player.getName())) {
            //player nicked
            String nickname = ChatColor.stripColor(((Essentials) Bukkit.getPluginManager().getPlugin("Essentials")).getUser(player).getNick());
            player.setPlayerListName(nickname);
            player.setDisplayName(nickname);
            NametagAPI.resetNametag(player.getName());
        } else {
            player.setPlayerListName(player.getName());
            player.setDisplayName(player.getName());
            NametagAPI.resetNametag(player.getName());
        }
    }


    ///////////////////////////BREAK BLOCK
    /*@EventHandler
    public void onBreakGet(BlockBreakEvent e){

        if (!plugin.getCombatManager().isInCombat(e.getPlayer())) return;
        if (!e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) return;
        if (e.isCancelled()) return;
        Collection<ItemStack> drops = e.getBlock().getDrops();
        Bukkit.getScheduler().runTaskLater(plugin,()->{
            if (!e.isCancelled()) {
                e.getBlock().getDrops().clear();
                for (ItemStack itemStack : drops) {
                    e.getPlayer().getInventory().addItem(itemStack);
                }
            }
        },1);



    }*/
    ///////////////////////////DEATH MESSAGE
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player killer = plugin.getEnergyManager().validate(e);
        if (killer == null) {
            return;
        }
        e.setDeathMessage(String.format("%s%s%s was killed by %s%s with %s", ChatColor.GREEN, e.getEntity().getName(), ChatColor.WHITE, ChatColor.RED, killer.getName(), killer.getItemInHand().getItemMeta().getDisplayName()));
        if (plugin.getKillEffectManager().get(killer) != null) {
            plugin.getKillEffectManager().get(killer).update(e.getEntity());
        }
    }

    ///////////////////////////MILK BUCKET
   /* @EventHandler
    public void onPickUp(PlayerPickupItemEvent e){
        if (!e.getItem().getItemStack().isSimilar(specialItemUtils.getCowBucket())){
            return;
        }
        int slot= ItemUtils.findItemSlot(e.getPlayer(),specialItemUtils.getCowBucket());
        if (slot==-1){
            return;
        }

        int amount = e.getPlayer().getInventory().getContents()[slot].getAmount();
        e.getPlayer().getInventory().getContents()[slot].setAmount(amount +e.getItem().getItemStack().getAmount());

}*
    */
    /////////////////////////////////////AVOID TOO BIG FALL DAMAGE
    @EventHandler
    public void onFallDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (teleportNoFallDMG.contains(((Player) e.getEntity()))){
                if (((Player) e.getEntity()).hasPotionEffect(PotionEffectType.ABSORPTION)) {
                    e.setCancelled(true);
                }
                teleportNoFallDMG.remove(((Player) e.getEntity()));
            }
            if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                if (e.getDamage() > 16) {
                    e.setDamage(16);
                }
            }
        }
    }

    ////////////////////////////////////AUTOFILL
    @EventHandler
    public void onAutoFill(PlayerItemConsumeEvent e) {
        PlayerInventory inventory = e.getPlayer().getInventory();
        if (!(inventory.getItem(e.getPlayer().getInventory().getHeldItemSlot()).getAmount() == 1)) {
            return;
        }
        SpecialItemUtils si = plugin.getSpecialItemUtils();
        Player p = e.getPlayer();
        ItemStack itemStack = e.getItem();
        if (itemStack.isSimilar(si.getCowBucket())) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (ItemUtils.containsSimilar(inventory, si.getCowBucket())) {
                    int slot = ItemUtils.findItemSlot(p, si.getCowBucket());
                    inventory.setItem(inventory.getHeldItemSlot(), inventory.getItem(slot));
                    inventory.setItem(slot, null);
                }
            }, 1);

        } else if (itemStack.isSimilar(si.getSquidPot())) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (ItemUtils.containsSimilar(inventory, si.getSquidPot())) {
                    int slot = ItemUtils.findItemSlot(p, si.getSquidPot());
                    inventory.setItem(inventory.getHeldItemSlot(), inventory.getItem(slot));
                    inventory.setItem(slot, null);
                }
            }, 1);
        }
    }

    /////////////////////ARENA RESET
    @EventHandler
    public void onResetBreak(BlockBreakEvent e) {
        Material material = e.getBlock().getType();
        if (material.name().toLowerCase().contains("diamond")) {
            return;
        }
        plugin.resetMap.put(e.getBlock(),material);
    }
    @EventHandler
    public void onResetPlace(BlockPlaceEvent e) {
        if (!plugin.resetMap.containsKey(e.getBlock())) {
            plugin.resetMap.put(e.getBlock(), Material.AIR);
        }
    }

    @EventHandler
    public void onResetExplosion(EntityExplodeEvent e) {
        for (Block b : e.blockList()) {
            Material material = b.getType();
            plugin.resetMap.put(b,material);
        }

    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        if (e.getEntity().hasMetadata(MegaWalls.getMetadataValue()))
            e.setCancelled(true);
    }


    @EventHandler
    public void onPotionSplash(PotionSplashEvent e) {
        for (LivingEntity entity : e.getAffectedEntities()) {
            if (entity instanceof Player && ((Player) entity).getGameMode().equals(GameMode.SPECTATOR)) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.isCancelled())
            return;


        if (e.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) e.getDamager();
            if (projectile.getShooter() instanceof Player) {

                if (e.getEntity() instanceof Player) {
                    if (plugin.getTeamsManager().isOnSameTeam(((Player) projectile.getShooter()).getPlayer(), ((Player) e.getEntity()).getPlayer())) {
                        e.setCancelled(true);
                    }
                    if (projectile instanceof WitherSkull) {
                        if (projectile.hasMetadata(MegaWalls.getMetadataValue())) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    /////AOTR
    @EventHandler
    public void onAOTR(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!e.getAction().name().contains("RIGHT")) return;
        if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) return;
        if (!p.getItemInHand().getItemMeta().hasDisplayName()) return;
        if (!p.getItemInHand().getItemMeta().getDisplayName().toLowerCase().contains("rogues")) return;
        if (!ItemUtils.isKitItem(p.getItemInHand())) return;
        e.setCancelled(true);
        if (aotrCD.contains(p)) {
            p.sendMessage("this item is in cooldown!");
            return;
        }
        Player closestEnemy=PlayerUtils.getClosestEnemy(p);
        if ((closestEnemy!=null && closestEnemy.getLocation().distance(p.getLocation()) < 50)) {
            p.sendMessage("There's at least an enemy in 50 blocks!");
            return;
        }
        aotrCD.add(p);
        p.sendMessage("You have used your "+ChatColor.GOLD+"Speed Boost "+ChatColor.RESET+"ability!");
        inAotr.add(p);
        p.setWalkSpeed(0.9f);
        UUID uuid=p.getUniqueId();
        Bukkit.getScheduler().runTaskLater(MegaWalls.getInstance(), new BukkitRunnable() {
            @Override
            public void run() {
                aotrCD.remove(p);
            }
        },40*20);
        Bukkit.getScheduler().runTaskLater(MegaWalls.getInstance(), new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getPlayer(uuid)!=null) {
                    p.setWalkSpeed(0.2f);
                    inAotr.remove(p);
                    p.sendMessage("Your "+ChatColor.GOLD+"Speed Boost "+ChatColor.RESET+"expired!");
                }
            }
        }, 5 * 20);


    }
    ////////Junk Apple
    @EventHandler
    public void onJunkApple(PlayerItemConsumeEvent e) {
        if (specialItemUtils.isJunkApple(e.getItem())) {
            Player player = e.getPlayer();
            if (plugin.getClassManager().get(player) instanceof MWMole) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,5*20,1));
            }
        }
    }
    //ANTI PICKUP
    @EventHandler
    public void onPlayerPickupItem(final PlayerPickupItemEvent e) {
        if (e.getItem().hasMetadata(MegaWalls.getMetadataValue())) {
            e.setCancelled(true);
        }
    }
    //AOTV
    @EventHandler
    public void onAOTV(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!e.getAction().name().contains("RIGHT")) return;
        if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) return;
        if (!specialItemUtils.isAOTV(e.getItem())) return;
        teleport(p);
        p.playSound(p.getLocation(),Sound.ENDERMAN_TELEPORT,0.2f,2f);

    }
    public  void teleport(Player player) {
        Location mainLoc = player.getEyeLocation();
        for(int i=1;i<=8*2;i++) {
            Location loc = player.getLocation();
            Vector dir = loc.getDirection();
            dir.normalize();
            dir.multiply(0.5); //1 blocks a way
            mainLoc.add(dir);

            if(mainLoc.getBlock().isEmpty() || mainLoc.getBlock().isLiquid()||canBePassed(mainLoc.getBlock())) {
                player.teleport(mainLoc);
                teleportNoFallDMG.add(player);
            }else break;}}
    public static boolean canBePassed(Block block){
        switch (block.getType()){
            case YELLOW_FLOWER:
            case LONG_GRASS:
            case AIR:
            case DOUBLE_PLANT:
                return true;
            default:
                return false;
        }
    }
    public boolean triggerHypeAbility(Player player) {
        player.setVelocity(player.getVelocity().setY(0));
        if (player.getEyeLocation().add(0, 1, 0).getBlock().isEmpty()|| player.getEyeLocation().add(0, 1, 0).getBlock().isLiquid()) {
            player.teleport(player.getLocation().add(0, 1, 0));
        }
        teleport(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION,60,0),true);
        player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_REMEDY, 0.2f, 2f);
        Particle.play(player.getLocation().add(0.0, 0.5, 0.0), Effect.EXPLOSION_LARGE);
        List<Player> close= PlayerUtils.getNearbyEnemies(player,6);
        close.remove(player);
        for (Player target : close) {
            Vector velocity=target.getVelocity();
            MegaWalls.getInstance().getMWHealth().trueDamage(target,2,player);
            target.setVelocity(velocity);//so that using this ability won't change the targets' velocity



        }
        if (close.size()!=0) {
            player.sendMessage("Your " + ChatColor.RED + "Implosion " + ChatColor.RESET + "hit " + ChatColor.RED + close.size() + ChatColor.RESET + " enemy.");
        }
        player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 0.4f, 1.2f);
        return false;
    }
    //Hype
    @EventHandler
    public void onHyperion(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!e.getAction().name().contains("RIGHT")) return;
        if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) return;
        if (!specialItemUtils.isHyperion(e.getItem())) return;
        triggerHypeAbility(p);

    }


}
