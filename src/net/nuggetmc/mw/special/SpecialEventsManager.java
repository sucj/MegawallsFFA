package net.nuggetmc.mw.special;

import com.earth2me.essentials.Essentials;
import com.joshargent.RegionPreserve.RegionPreservePlugin;
import io.isles.nametagapi.NametagAPI;
import net.md_5.bungee.api.ChatColor;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.events.DiamondCounter;
import net.nuggetmc.mw.mwclass.classes.MWCow;
import net.nuggetmc.mw.mwclass.classes.MWMole;
import net.nuggetmc.mw.special.specialItems.AOTR;
import net.nuggetmc.mw.special.specialItems.Terminator;
import net.nuggetmc.mw.utils.EventDumper;
import net.nuggetmc.mw.utils.ItemUtils;
import net.nuggetmc.mw.utils.ParticleUtils;
import net.nuggetmc.mw.utils.PlayerUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;


public class SpecialEventsManager implements Listener {
    MegaWalls plugin;
    public static RegionPreservePlugin rp;

    static SpecialEventsManager instance;

    public static SpecialEventsManager getInstance() {
        return instance;
    }



    public SpecialEventsManager() {
        this.plugin = MegaWalls.getInstance();
        rp = Bukkit.getPluginManager().getPlugin("RegionPreserve") == null ? null : (RegionPreservePlugin) Bukkit.getPluginManager().getPlugin("RegionPreserve");
        instance = this;
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


                if (!Objects.equals(plugin.getCompassManager().getCompassTargetMap().get(plr), plugin.getTeamsManager().getTeamOfPlayer(p))) {
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
        if (!(e.getEntity() instanceof LivingEntity)) return;
        if (!(e.getDamager() instanceof Arrow)) return;
        Arrow arrow = (Arrow) e.getDamager();
        if (!(arrow.getShooter() instanceof Player)) return;
        if (((Player) arrow.getShooter()).getUniqueId().equals(e.getEntity().getUniqueId())) return;
        LivingEntity victim = (LivingEntity) e.getEntity();
            Player player = (Player) arrow.getShooter();

            player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 0);
            if (victim instanceof Player) {
                player.sendMessage(ChatColor.YELLOW + ((Player) victim).getDisplayName() + ChatColor.RESET + " is on " + (BigDecimal.valueOf(victim.getHealth()).setScale(1, RoundingMode.HALF_UP)).doubleValue() + " health!");
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

        event.getPlayer().setGameMode(GameMode.ADVENTURE);

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
        Player victim = e.getEntity();
        if (killer == null) {
            return;
        }
        if (killer.getUniqueId().equals(victim.getUniqueId())){
            return;
        }
        if (killer.getItemInHand()==null||killer.getItemInHand().getItemMeta()==null||killer.getItemInHand().getItemMeta().getDisplayName()==null){
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
        if (material == Material.DIAMOND_ORE) {
            e.setCancelled(true);
            e.getBlock().setType(Material.AIR);
            DiamondCounter.INSTANCE.breakDiamond(e.getPlayer(),e.getBlock().getLocation());
        }
        if(!plugin.resetMap.containsKey(e.getBlock())) {
            plugin.resetMap.put(e.getBlock(), material);
        }
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
            if(!plugin.resetMap.containsKey(b)) {
                plugin.resetMap.put(b, material);
            }
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


        if (Objects.requireNonNull(EventDumper.INSTANCE.dumpDamager(e)).getUniqueId().equals(e.getEntity().getUniqueId())){
            e.setCancelled(true);
        }
    }

    ////////Junk Apple
    @EventHandler
    public void onJunkApple(PlayerItemConsumeEvent e) {
        if (specialItemUtils.isJunkApple(e.getItem())) {
            Player player = e.getPlayer();
            if (plugin.getClassManager().get(player) instanceof MWMole) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 1));
            }
        }
    }

    //ANTI PICKUP
    @EventHandler
    public void onPlayerPickupItem(final PlayerPickupItemEvent e) {
        if (e.getItem().hasMetadata(MegaWalls.getMetadataValue())) {
            e.setCancelled(true);
        }
        if (e.getItem().getItemStack().getType().equals(Material.ARROW)){
            e.setCancelled(true);
        }
    }




    @EventHandler
    public void onCraft(CraftItemEvent e){
        if (e.getRecipe().getResult().getType().equals(Material.DIAMOND_BLOCK)){
            e.setCancelled(true);
            e.getWhoClicked().sendMessage("This recipe has been banned here!");
        } else if (hasUnlegitDiamond(e.getInventory())) {
            e.setCancelled(true);
            ItemUtils.clearUnlegitDiamonds(e.getWhoClicked().getInventory());
            ItemUtils.clearUnlegitDiamonds(e.getInventory());
        }
    }

    private boolean hasUnlegitDiamond(Inventory inventory) {
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack.getType().equals(Material.DIAMOND) && !SpecialItemUtils.isGoodDiamond(itemStack)) {
                return true;
            }
        }
        return false;
    }
    //SWITCH BETWEEN AOTR AND TERM
    @EventHandler
    public void onHeldItemChange(PlayerItemHeldEvent e){
        Player player=e.getPlayer();
        ItemStack newSlotItem=player.getInventory().getItem(e.getNewSlot());
        ItemStack previousItem=player.getInventory().getItem(e.getPreviousSlot());

        doHeldChange(newSlotItem, player, previousItem);
    }

    private void doHeldChange(ItemStack newSlotItem, Player player, ItemStack previousItem) {
        if (verify(newSlotItem)&& newSlotItem.getType().equals(Material.BOW)&&!Terminator.INSTANCE.check(newSlotItem)){
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack itemStack = player.getInventory().getItem(i);
                if (verify(itemStack)&& itemStack.getItemMeta()!=null&&itemStack.getItemMeta().hasDisplayName()&& AOTR.INSTANCE.check(itemStack)){
                    player.getInventory().setItem(i,specialItemUtils.getQuiverArrow());
                    break;
                }
            }
        } else if (verify(previousItem)&& previousItem.getType().equals(Material.BOW)&&!Terminator.INSTANCE.check(previousItem)) {
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack itemStack = player.getInventory().getItem(i);
                if (itemStack == null) continue;
                net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
                if (nmsItem == null) continue;
                if (verify(itemStack)&& itemStack.getItemMeta()!=null&&itemStack.getItemMeta().hasDisplayName()&& itemStack.getItemMeta().getDisplayName().equals("Quiver Arrow")){
                    ItemStack AOTR= net.nuggetmc.mw.special.specialItems.AOTR.INSTANCE.buildItem();
                    if (plugin.getClassManager().get(player)!=null&&plugin.getClassManager().get(player) instanceof MWMole){
                        AOTR.setType(Material.IRON_SPADE);
                    }
                    player.getInventory().setItem(i,AOTR);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        // 先记录下点击前手持的物品（或其 Snapshot/Type）
        ItemStack itemBefore = player.getItemInHand() != null ? player.getItemInHand().clone() : null;

        // 延迟 1 tick 检查物品是否发生了改变
        // 这样无论玩家是用 Shift 键移入、鼠标拖拽替换、还是数字键快捷交换，都能精准捕捉！
        Bukkit.getScheduler().runTask(plugin, () -> {
            ItemStack itemAfter = player.getItemInHand();

            // 比较点击前后手持物品是否有变化
            if (!isSameItem(itemBefore, itemAfter)) {
                doHeldChange(itemAfter,player,itemBefore);
            }
        });
    }

    /**
     * 辅助方法：比较两个 ItemStack 是否相同（处理 null 和 AIR）
     */
    private boolean isSameItem(ItemStack item1, ItemStack item2) {
        if (item1 == null || item1.getType() == Material.AIR) {
            return item2 == null || item2.getType() == Material.AIR;
        }
        return item1.isSimilar(item2) && item1.getAmount() == item2.getAmount();
    }

    public boolean verify(ItemStack itemStack){
        if (itemStack == null) {
            return false;
        }
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        if (nmsItem == null) {
            return false;
        }
        return true;
    }
    @EventHandler
    public void onShoot(EntityShootBowEvent e){
        if (e.getEntity() instanceof Player){
            for (ItemStack is: ((Player) e.getEntity()).getInventory()){
                if (verify(is)&& is.getItemMeta()!=null&& is.getItemMeta().hasDisplayName()&&is.getItemMeta().getDisplayName().equals("Quiver Arrow")){
                    is.setAmount(64);
                }
            }
        }
    }



}
