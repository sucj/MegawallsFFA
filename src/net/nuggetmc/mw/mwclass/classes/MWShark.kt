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
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class MWShark() : MWClass(){


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
        var set=HashSet<Block>()
        for (i in -expand until expand+1){
            val block = world.getBlockAt(location.blockX + i, posY, location.blockZ)
            if (block.isEmpty){


                        block.setType(Material.STATIONARY_WATER,false)
                        if (!set.contains(block)) {
                            set.add(block)
                        }
                for (j in -expand until expand+1){
                    val block1 = world.getBlockAt(location.blockX + i, posY, location.blockZ + j)
                    if (block1.isEmpty){

                        block1.setType(Material.STATIONARY_WATER,false)
                        if (!set.contains(block1)){
                            set.add(block1)
                        }
                    }
                }
            }
        }
        if (set.isNotEmpty()) {
            Bukkit.getScheduler().runTaskLater(
                MegaWalls.getInstance(),
                {
                for(i in  0 until set.size){
                    val block=set.toArray().get(i) as Block
                    block.setType(Material.AIR)
                }
                }
                ,6*20
            )
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
    @EventHandler
    fun onPhysics(e: BlockPhysicsEvent) {
        if (e.block.type.equals(Material.STATIONARY_WATER))
        e.isCancelled=true;
    }

    }
