package net.nuggetmc.mw.mwclass.classes

import net.md_5.bungee.api.ChatColor
import net.nuggetmc.mw.mwclass.MWClass
import net.nuggetmc.mw.mwclass.info.Diamond
import net.nuggetmc.mw.mwclass.info.MWClassInfo
import net.nuggetmc.mw.mwclass.info.Playstyle
import net.nuggetmc.mw.mwclass.items.MWItem
import net.nuggetmc.mw.mwclass.items.MWKit
import net.nuggetmc.mw.mwclass.items.MWPotions
import net.nuggetmc.mw.utils.ActionBar
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class MWShark() : MWClass(){
    var mine: MutableMap<Player, Int> = HashMap()
    val cowBucketValue = 60
    private val willpowerList: MutableSet<Player> = HashSet()
    var dmgcount = 0

    init {
        name = arrayOf("牛", "Shark", "SRK")
        icon = Material.WATER_BUCKET
        color = ChatColor.LIGHT_PURPLE
        playstyles = arrayOf(
            Playstyle.SUPPORT,
            Playstyle.TANK
        )
        diamonds = arrayOf(
            Diamond.CHESTPLATE
        )
        classInfo = MWClassInfo(
            "Granting Moo",
            "Moo, granting Resistance" + ChatColor.GREEN + " II" + ChatColor.RESET + " and Regeneration " + ChatColor.GREEN + "II" + ChatColor.RESET + " to yourself",
            "Bucket Barrier",
            "Once below " + ChatColor.GREEN + "20 HP" + ChatColor.RESET + ", a shield of milk buckets forms around you for 20 seconds,blocking the next 4 sources of damage by " + ChatColor.GREEN + "25%" + ChatColor.RESET + ".Whenever damage gets blocked, you will get healed for " + ChatColor.GREEN + "2HP" + ChatColor.RESET,
            "Refreshing Sip",
            "Drinking any milk bucket grants nearby allies in a 7 block radius " + ChatColor.GREEN + "3 HP" + ChatColor.RESET + ", replenishing both hunger and saturation",
            "Ultra Pasteurized",
            "You will receive 2 milk buckets for every " + ChatColor.GREEN + "80" + ChatColor.RESET + " Stone you mine.Milk buckets grant Resistance I and Regeneration II for 5 seconds and can be given to teammates"
        )
        classInfo.addEnergyGainType("Melee", 20)
        classInfo.addEnergyGainType("Bow", 20)
    }

    override fun ability(player: Player) {
        energyManager.clear(player)
        var expand=5;
        var posY=player.location.blockY
        var location=player.getLocation()
        var world=player.world
        for (i in -expand until expand+1){
            if (world.getBlockAt(location.blockX+i,posY,location.blockZ).isEmpty){


                        world.getBlockAt(location.blockX+i,posY,location.blockZ).setType(Material.STATIONARY_WATER,false)

                for (j in -expand until expand+1){
                    if (world.getBlockAt(location.blockX+i,posY,location.blockZ+j).isEmpty){

                        world.getBlockAt(location.blockX+i,posY,location.blockZ+j).setType(Material.STATIONARY_WATER,false)
                    }
                }
            }
        }
    }



    override fun hit(event: EntityDamageByEntityEvent) {
        super.hit(event)
        if (event.isCancelled) return
        val player = energyManager.validate(event) ?: return
        if (manager[player] !== this) return
        energyManager.add(player, 20)
    }

    @EventHandler
    fun onDamage(e: EntityDamageEvent) {
        if (e.isCancelled) return
        if (e.entity !is Player) return
        val victim = e.entity as Player
        if (manager[victim] !== this) {
            return
        }
        //suffer from dmg
    }





    override fun assign(player: Player) {
        val items: Map<Int, ItemStack>
        if (MWKit.contains(this)) {
            items = MWKit.fetch(this)
        } else {
            val swordEnch: MutableMap<Enchantment, Int> = HashMap()
            swordEnch[Enchantment.DURABILITY] = 10
            val armorEnch: MutableMap<Enchantment, Int> = HashMap()
            armorEnch[Enchantment.PROTECTION_ENVIRONMENTAL] = 2
            armorEnch[Enchantment.DURABILITY] = 10
            val sword = MWItem.createSword(this, Material.IRON_SWORD, swordEnch)
            val bow = MWItem.createBow(this, null)
            val tool = MWItem.createTool(this, Material.DIAMOND_PICKAXE)
            val chestplate = MWItem.createArmor(this, Material.DIAMOND_CHESTPLATE, armorEnch)
            val potions = MWPotions.createBasic(this, 1, 10, 2)
            val extra: MutableList<ItemStack> = ArrayList()
            extra.add(plugin.specialItemUtils.getCowOwnBucket(3))
            items = MWKit.generate(this, sword, bow, tool, null, null, potions, null, chestplate, null, null, extra)
        }
        MWKit.assignItems(player, items)


    }


    }
