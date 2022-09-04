package net.nuggetmc.mw.mwclass.classes;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.nuggetmc.mw.MegaWalls;
import net.nuggetmc.mw.mwclass.MWClass;
import net.nuggetmc.mw.mwclass.info.Diamond;
import net.nuggetmc.mw.mwclass.info.MWClassInfo;
import net.nuggetmc.mw.mwclass.info.Playstyle;
import net.nuggetmc.mw.mwclass.items.MWItem;
import net.nuggetmc.mw.mwclass.items.MWKit;
import net.nuggetmc.mw.mwclass.items.MWPotions;
import net.nuggetmc.mw.utils.ActionBar;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class MWCow extends MWClass {

    Map<Player,Integer> mine=new HashMap<>();
    final int cowBucketValue=60;
    private final Set<Player> willpowerList = new HashSet<>();
    Map<Player,Integer> dmgcount=new HashMap<>();

    public MWCow() {
        this.name = new String[]{"牛","Cow","COW"};
        this.icon = Material.MILK_BUCKET;
        this.color = ChatColor.LIGHT_PURPLE;

        this.playstyles = new Playstyle[] {
            Playstyle.SUPPORT,
            Playstyle.TANK
        };

        this.diamonds = new Diamond[] {
            Diamond.CHESTPLATE
        };

        this.classInfo = new MWClassInfo(
            "Granting Moo",
            "Moo, granting Resistance"+ChatColor.GREEN+" I"+ChatColor.RESET+" and Regeneration "+ChatColor.GREEN+"II"+ChatColor.RESET+" to yourself and granting nearby allies in a 7 block radius Regeneration III for 2.5 seconds",
            "Bucket Barrier",
            "Once below "+ChatColor.GREEN+"20 HP"+ChatColor.RESET+", a shield of milk buckets forms around you for 20 seconds,blocking the next 4 sources of damage by "+ChatColor.GREEN+"25%"+ChatColor.RESET+".Whenever damage gets blocked, you will get healed for "+ChatColor.GREEN+"2HP"+ChatColor.RESET,
            "Refreshing Sip",
            "Drinking any milk bucket grants nearby allies in a 7 block radius "+ChatColor.GREEN+"3 HP"+ChatColor.RESET+", replenishing both hunger and saturation",
            "Ultra Pasteurized",
            "You will receive 2 milk buckets for every "+ChatColor.GREEN+"80"+ChatColor.RESET+" Stone you mine.Milk buckets grant Resistance I and Regeneration II for 5 seconds and can be given to teammates"
        );

        this.classInfo.addEnergyGainType("Melee", 20);
        this.classInfo.addEnergyGainType("Bow", 20);
    }

    @Override
    public void ability(Player player) {
        energyManager.clear(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) (2.5*20),0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (int) (2.5*20),1));
        for(Player target:Bukkit.getOnlinePlayers()){
            if (player.getWorld() != target.getWorld()) continue;
            if (target.isDead()) continue;
            if (!plugin.getTeamsManager().isOnSameTeam(player,target)) continue;
            if (player==target) continue;
            if (target.getLocation().distance(player.getLocation())>7) continue;
            target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,(int) (2.5*20),2));
            target.sendMessage("You have been healed by the Granting Moo of "+player.getName()+"!");
        }
    }


    @Override
    public void hit(EntityDamageByEntityEvent event) {
        super.hit(event);
        if (event.isCancelled()) return;
        Player player = energyManager.validate(event);
        if (player == null) return;

        if (manager.get(player) != this) return;


        energyManager.add(player, 20);
    }
    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if (e.isCancelled()) return;
        if (!(e.getEntity() instanceof Player )) return;
        Player victim= (Player) e.getEntity();
        if (manager.get(victim) != this) {
            return;
        }
        BucketBarrier(victim, victim.getHealth() - e.getDamage(),e);
    }
    @EventHandler
    public void onBucketGet(BlockBreakEvent e){

        Player player=e.getPlayer();
        if (manager.get(player) != this) {
            return;
        }
        if (e.getBlock().getType()!=Material.STONE) return;
        if (mine.get(player)<cowBucketValue){
            mine.replace(player,mine.get(player)+1);
            //ActionBar.send(player,this.getColor()+"Ultra Pasteurized "+ChatColor.WHITE+mine.get(player)+"/"+cowBucketValue);
        }
        if (mine.get(player)==cowBucketValue) {
            mine.replace(player,0);
            player.getInventory().addItem(plugin.getSpecialItemUtils().getCowBucket(2));
            //ActionBar.send(player,this.getColor()+"Ultra Pasteurized "+ChatColor.GREEN+"✔");
        }
        //❤❥✔✖✗✘❂⋆✢✭✬✫✪✩✦✥✤✣✮✷➡➧⬅⬇➟➢➙➴➽▄▜▛➝▄⚔

    }
    @Override
    public String getActionBar(Player player){
        String milk="";
        if (mine.get(player)<cowBucketValue){
            milk= this.getColor()+"Ultra Pasteurized "+ChatColor.WHITE+mine.get(player)+"/"+cowBucketValue+ChatColor.RESET;
        } else if (mine.get(player)==cowBucketValue) {
           milk= this.getColor()+"Ultra Pasteurized "+ ChatColor.GREEN +"✔"+ChatColor.RESET;
        }
        String milkBarrier;
        String grantingMoo=this.getColor() + "Granting Moo "+ (energyManager.get(player)!=100 ?ChatColor.RED + "✖":ChatColor.GREEN+ "✔")+ChatColor.RESET;
        milkBarrier=this.getColor() + "Milk Barrier "+(willpowerList.contains(player)?ChatColor.RED + "✖":ChatColor.GREEN+ "✔")+ChatColor.RESET;


        return (grantingMoo +"      "  +milkBarrier+"       "+milk);


    }



    @Override
    public void assign(Player player) {
        Map<Integer, ItemStack> items;

        if (MWKit.contains(this)) {
            items = MWKit.fetch(this);
        }

        else {
            Map<Enchantment, Integer> swordEnch = new HashMap<>();
            swordEnch.put(Enchantment.DURABILITY, 10);

            Map<Enchantment, Integer> armorEnch = new HashMap<>();
            armorEnch.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            armorEnch.put(Enchantment.DURABILITY, 10);

            ItemStack sword = MWItem.createSword(this, Material.IRON_SWORD, swordEnch);
            ItemStack bow = MWItem.createBow(this, null);
            ItemStack tool = MWItem.createTool(this, Material.DIAMOND_PICKAXE);
            ItemStack chestplate = MWItem.createArmor(this, Material.DIAMOND_CHESTPLATE, armorEnch);

            List<ItemStack> potions = MWPotions.createBasic(this, 1, 10, 2);
            List<ItemStack> extra= new ArrayList<>();
            extra.add(plugin.getSpecialItemUtils().getCowOwnBucket(3));

            items = MWKit.generate(this, sword, bow, tool, null, null, potions, null, chestplate, null, null,extra );
        }

        MWKit.assignItems(player, items);
        if (mine.containsKey(player)){
            mine.replace(player,0);
        }else {
            mine.put(player,0);
        }
        if (dmgcount.containsKey(player)){
            dmgcount.replace(player,0);
        }else {
            dmgcount.put(player,0);
        }

    }
    private void BucketBarrier(Player player, double health, EntityDamageEvent e) {
        if (health <= 20) {
            if (willpowerList.contains(player)) return;
            if (dmgcount.get(player).equals(4)) return;
            willpowerList.add(player);
            if (dmgcount.get(player).equals(0)) {
                player.sendMessage(this.getColor()+"You have activated the Bucket Barrier !");
            }

            int n=20;


            dmgcount.replace(player,dmgcount.get(player)+1);

            e.setDamage(e.getDamage()*0.75);
            if (player.getHealth()>=player.getMaxHealth()-2) {
                player.setHealth(player.getMaxHealth());
            }else {
                player.setHealth(player.getHealth()+2);
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                //Cool down finished
                willpowerList.remove(player);
                dmgcount.replace(player,0);
            }, n * 20);
        }
    }
}
