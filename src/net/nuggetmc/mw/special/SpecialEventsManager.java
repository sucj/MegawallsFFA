package net.nuggetmc.mw.special;

import com.earth2me.essentials.Essentials;
import io.isles.nametagapi.NametagAPI;
import net.md_5.bungee.api.ChatColor;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.mwclass.classes.MWCow;
import net.nuggetmc.mw.utils.WorldUtils;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;

import static net.nuggetmc.mw.MegaWalls.OPBYPASSGM;

public class SpecialEventsManager implements Listener {
    MegaWalls plugin;
    public SpecialEventsManager(){
        this.plugin=MegaWalls.getInstance();
    }

    ///////////////////////////COW BUCKET
    SpecialItemUtils specialItemUtils=new SpecialItemUtils();
    @EventHandler
    public void onCowBucket(PlayerItemConsumeEvent e){
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
                    double health=target.getHealth()+3;
                    if (health>target.getMaxHealth()){
                        target.setHealth(target.getMaxHealth());
                    }else {
                       target.setHealth(health);
                    }
                    target.sendMessage("You have been healed by the Refreshing Sip of "+player.getName()+"!");
                }
            }
        }
    }
    ///////////////////////////ENDER CHEST
    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if (plugin.getCombatManager().isInCombat(e.getPlayer())&&e.getBlock().getType()== Material.ENDER_CHEST){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        if (plugin.getCombatManager().isInCombat(e.getPlayer())&&e.getItemDrop().getItemStack().getType()==Material.ENDER_CHEST){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(!e.getAction().name().contains("RIGHT")) return;
        if(p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) return;
        if (p.getItemInHand().getType()!=Material.ENDER_CHEST) return;
        p.openInventory(p.getEnderChest());
    }
    ///////////////////////////PLAYER TRACK
    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player plr = e.getPlayer();
        Location plrLocation = plr.getLocation();
        if (plr.getWorld().getEnvironment() == World.Environment.NORMAL){
            //System.out.println(plr.getPlayerListName());
            String plrName = plr.getPlayerListName();
            double LowestDistance = Double.MAX_VALUE;
            Location LowestLocation = plr.getLocation();
            for (Player p : Bukkit.getOnlinePlayers()){
                if (!plugin.getCombatManager().isInCombat(p)){
                    continue;
                }
                if(!p.getPlayerListName().equals(plrName)) {
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
            if (LowestLocation==null){
                return;
                //Todo : When right click on Compass tell the player that target is not found
            }
            // we now have the lowest location and distance
            plr.setCompassTarget(LowestLocation);


        }
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
    public void onArrowDamageTell(EntityDamageByEntityEvent e){
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Arrow)) return;
        Arrow arrow=(Arrow) e.getDamager();
        Player victim=((Player) e.getEntity()).getPlayer();
        if (arrow.getShooter() instanceof Player) {
            Player player = (Player) arrow.getShooter();


            player.sendMessage(ChatColor.YELLOW+victim.getDisplayName()+ChatColor.RESET+" is on "+(new BigDecimal(victim.getHealth()).setScale(1,BigDecimal.ROUND_HALF_UP)).doubleValue()+" health!");
        }
    }
    ///////////////////////////NO DAMAGE BEFORE JOINING
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player= (Player) e.getEntity();
            if (!MegaWalls.getInstance().getCombatManager().isInCombat(player)){
                e.setCancelled(true);
            }
        }
    }
    ///////////////////////////RESPAWN
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player=event.getPlayer();
        if (event.getPlayer().isOp() && OPBYPASSGM) {
            //
        } else {
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
        }
        MegaWalls.getInstance().getCombatManager().removeInCombat(event.getPlayer());
        if (Bukkit.getPluginManager().getPlugin("Essentials")!=null&& !Objects.equals(((Essentials) Bukkit.getPluginManager().getPlugin("Essentials")).getUser(player).getNick(), player.getName())) {
            //player nicked
            String nickname = ChatColor.stripColor(((Essentials) Bukkit.getPluginManager().getPlugin("Essentials")).getUser(player).getNick());
            player.setPlayerListName(nickname);
            player.setDisplayName(nickname);
            NametagAPI.resetNametag(nickname);
        }else {
            player.setPlayerListName(player.getName());
            player.setDisplayName(player.getName());
            NametagAPI.resetNametag(player.getName());
        }
    }

    @EventHandler
    public void onAntiStealDiamonds(BlockBreakEvent e){
        if(e.getPlayer().isOp()||e.getPlayer().hasPermission("mw.admin")) return;
        if (Bukkit.getOnlinePlayers().toArray().length>2) return;
        if (e.getBlock().getType()==Material.DIAMOND_ORE||e.getBlock().getType()==Material.DIAMOND_BLOCK){
            if (Bukkit.getOnlinePlayers().toArray().length==1||(Bukkit.getOnlinePlayers().toArray().length==2&&((((Player)Bukkit.getOnlinePlayers().toArray()[0]).getAddress())==((Player)Bukkit.getOnlinePlayers().toArray()[1]).getAddress()))){




                e.setCancelled(true);
                e.getPlayer().sendMessage("禁止打工!等有人的时候你再挖吧!");
                return;

        }}
    }
    ///////////////////////////BREAK BLOCK
    @EventHandler
    public void onBreakGet(BlockBreakEvent e){
        if (!plugin.getCombatManager().isInCombat(e.getPlayer())) return;
        if (!e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) return;
        Collection<ItemStack> drops = e.getBlock().getDrops();


            for (ItemStack itemStack: drops){
                e.getPlayer().getInventory().addItem(itemStack);
            }
            e.getBlock().setType(Material.AIR);



    }
    ///////////////////////////DEATH MESSAGE
    @EventHandler
    public void onDeath(PlayerDeathEvent e){
       Player killer = plugin.getEnergyManager().validate(e);
       if (killer==null) {
           return;
       }
        e.setDeathMessage(ChatColor.GREEN + e.getEntity().getName()+ChatColor.WHITE+" was Killed,Killer: "+ChatColor.RED+killer.getName());
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

}*/
}
