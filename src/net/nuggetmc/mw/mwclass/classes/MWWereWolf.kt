package net.nuggetmc.mw.mwclass.classes

import net.md_5.bungee.api.ChatColor
import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.mwclass.MWClass
import net.nuggetmc.mw.mwclass.info.Diamond
import net.nuggetmc.mw.mwclass.info.MWClassInfo
import net.nuggetmc.mw.mwclass.info.Playstyle
import net.nuggetmc.mw.mwclass.items.MWItem
import net.nuggetmc.mw.mwclass.items.MWKit
import net.nuggetmc.mw.mwclass.items.MWPotions
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

class MWWereWolf : MWClass() {
    val inAbility= HashSet<Player>()
    val combo= HashMap<Player,Int>()
    val plugin=MegaWalls.getInstance()
    val devourCooldown=HashSet<Player>()

    init {
        name = arrayOf("狼人", "WereWolf", "WER")
        icon = Material.COOKED_BEEF
        color = ChatColor.DARK_GREEN
        playstyles = arrayOf(
            Playstyle.MOBILITY,
            Playstyle.TANK
        )
        diamonds = arrayOf(
            Diamond.CHESTPLATE
        )
        classInfo = MWClassInfo(
            "Lycanthropy",
            "You will gain Speed II for §a8§7 seconds. §7During this time,you will heal for 20% of the damage you deal,and deal 2 additional true damage every hit.",
            "Blood Lust",
            "§7Gain Speed I and Resistance II for §a8 §7seconds after landing any combination of 3 consecutive attacks without taking any damage.",
            "Devour",
            "§7Every §a1§7 steak eaten will give §7you Regeneration I§7 for §a5§7 seconds.\nCooldown:8s",
            "Carnivore" + "" + ChatColor.RED + "unavailable",
            "§7Every §a1§7 player killed will drop §71§7 extra steak, and enemies that are final killed §7will drop §a4§7 extra steak"
        )
        classInfo.addEnergyGainType("Melee", 10)
        classInfo.addEnergyGainType("Bow", 10)
    }

    override fun ability(player: Player) {
        energyManager.clear(player)
        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED,8*20,1))
        inAbility.add(player)
        object : BukkitRunnable() {
            override fun run() {
                inAbility.remove(player)
            }
        }.runTaskLater(plugin,8*20)
    }

    override fun hit(event: EntityDamageByEntityEvent) {
        super.hit(event)
        if (event.isCancelled) return
        val player = energyManager.validate(event) ?: return
        val victim=event.entity as Player
        if (manager[player] == this) {


            energyManager.add(player, 10)
            if (inAbility.contains(player)) {
                mwhealth.heal(player, 0.2 * event.damage)
                object:BukkitRunnable(){
                    override fun run() {
                        mwhealth.trueDamage(victim, 2.0, player)
                    }
                }.runTaskLater(plugin,5)
            }
            if (!combo.containsKey(player)){
                combo[player] =1
            }else{
                combo[player] = combo[player]!! + 1
            }
            if (combo[player]==3){
                player.addPotionEffect(PotionEffect(PotionEffectType.SPEED,20*8,0))
                player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,20*8,1))
                combo.remove(player)
            }
        }else if (manager[victim]==this){
            combo.remove(victim)
        }
    }

    @EventHandler
    fun onRightClick(e: PlayerInteractEvent) {
        val p = e.player
        if (!e.action.name.contains("RIGHT")) return
        if (p.itemInHand == null || p.itemInHand.type != Material.COOKED_BEEF) return
        if (manager[p] !== this) return
        if (p.foodLevel == 20) {
            p.foodLevel = 19
        }
    }
    @EventHandler
    fun onSteak(e:PlayerItemConsumeEvent){
        if(e.item.type!=Material.COOKED_BEEF){
            return
        }
        val player=e.player
        if (manager[player]==this){
            if (!devourCooldown.contains(player)){
                devourCooldown.add(player)
                player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION,5*20,0))
                object :BukkitRunnable(){
                    override fun run() {
                        devourCooldown.remove(player)
                    }
                }.runTaskLater(plugin,8*20)
            }
        }
    }

    override fun getActionBar(player: Player?): String {
        val bloodlust=this.color.toString()+ChatColor.BOLD+"Blood Lust " +
        when(combo[player]){
            null,0,3->ChatColor.GREEN.toString() +ChatColor.BOLD + "✔"
            else->ChatColor.WHITE.toString()+ChatColor.BOLD+combo[player]+" /3"
        }+ChatColor.RESET
        val devour=this.color.toString()+ChatColor.BOLD.toString() + "Heal ${if (!devourCooldown.contains(player)) ChatColor.GREEN.toString()+ChatColor.BOLD.toString() + "✔" else ChatColor.RED.toString() +ChatColor.BOLD.toString() + "✖" }"
        return "$bloodlust       $devour"
    }

    override fun assign(player: Player) {
        val items: Map<Int, ItemStack>

            val swordEnch: MutableMap<Enchantment, Int> = HashMap()
            swordEnch[Enchantment.DURABILITY] = 10
            val armorEnch: MutableMap<Enchantment, Int> = HashMap()
            armorEnch[Enchantment.DURABILITY] = 10
            val sword = MWItem.createSword(this, Material.DIAMOND_SWORD, swordEnch,player)
            val bow = MWItem.createBow(this, null)
            val tool = MWItem.createTool(this, Material.DIAMOND_PICKAXE)
            val chestplate = MWItem.createArmor(this, Material.DIAMOND_CHESTPLATE, armorEnch)
            val potions = MWPotions.createWolfBasic(this, 1, 8, 3)
            items = MWKit.generate(this, sword, bow, tool, null, null, potions, null, chestplate, null, null, null)

        MWKit.assignItems(player, items)
        inAbility.remove(player)
        combo.remove(player)
        devourCooldown.remove(player)
    }
}