package net.nuggetmc.mw.mwclass.classes

import net.md_5.bungee.api.ChatColor
import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.mwclass.MWClass
import net.nuggetmc.mw.mwclass.info.MWClassInfo
import net.nuggetmc.mw.mwclass.info.Playstyle
import net.nuggetmc.mw.mwclass.items.MWItem
import net.nuggetmc.mw.mwclass.items.MWKit
import net.nuggetmc.mw.mwclass.items.MWPotions
import net.nuggetmc.mw.utils.PlayerUtils
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

class MWGoldenDragon : MWClass() {
    val plugin: MegaWalls =MegaWalls.getInstance()!!
    val energyManager=plugin.energyManager!!
    @get:Synchronized
    val cdCache= HashSet<Player>()

    init {
        name = arrayOf("金龙", "GoldenDragon", "GOD")
        icon=Material.DRAGON_EGG
        color = ChatColor.RED
        playstyles = arrayOf(
            Playstyle.RANGED,
            Playstyle.MOBILITY
        )
        diamonds = emptyArray()
        classInfo = MWClassInfo(
            "Echo",
            "You will deal 4 damage to the closest enemy in 30 blocks and gain 2 HP."+
            "\n how to activate:Left click with your bow"+
            "\n Energy cost:21"+
            "\n Cooldown:1s",
            "Echo+",
            "You will deal 9 damage to the closest enemy in 35 blocks and gain 5 Hp."+
                    "\n how to activate:Left click with your bow"+
                    "\n Energy cost:40"+
            "\n the cooldown of this counts as the cooldown of Echo.",
            "Heal",
            "Right click with your sword and all players in your team will get Regeneration III for 4 seconds.\n" +
                    " Energy cost:65",
            "Nothing",
            "Nothing here."
        )
        classInfo.addEnergyGainType("Melee", 4)
        classInfo.addEnergyGainType("Bow", 7)
        classInfo.addEnergyGainType("Per second", 1)
    }

    override fun ability(player: Player) {
        //Nothing here.
    }

        fun Player.heal(amount: Int){
            if (this.health+amount>this.maxHealth){
                this.health=this.maxHealth
            }else{
                this.health+=amount
            }
        }
        fun callEcho(player: Player){
            if (cdCache.contains(player)){
                return
            }
            val energy= energyManager[player]
            if (energy<21){
                return
            }
            cdCache.add(player)
            when(energy){
                in 21..39->{
                    val enemy=PlayerUtils.getClosestEnemyInRange(player, 30.0)
                    if (enemy==null) return
                    energyManager[player] -=21
                    mwhealth.trueDamage(enemy,4.toDouble(),player)
                    player.sendMessage(this.color.toString()+ "You deal 4 damage to ${enemy.displayName}.")
                    enemy.sendMessage(this.color.toString() + "You received 4 damage from ${player.displayName}.")
                    player.heal(2)
                }
                in 40..100->{
                    val enemy=PlayerUtils.getClosestEnemyInRange(player, 35.0)
                    if (enemy==null) return
                    energyManager[player] -=40
                    mwhealth.trueDamage(enemy,9.toDouble(),player)
                    player.sendMessage(this.color.toString()+ "You deal 9 damage to ${enemy.displayName}.")
                    enemy.sendMessage(this.color.toString() + "You received 9 damage from ${player.displayName}.")
                    player.heal(5)

                }
            }

            object :BukkitRunnable(){
                override fun run() {
                    cdCache.remove(player)
                }
            }.runTaskLater(plugin,20)

        }
        fun callHeal(p: Player){
            val energy= energyManager[p]
            if (energy<65){
                return
            }
            plugin.teamsManager.getTeamMembers(plugin.teamsManager.getTeamOfPlayer(p)!!).forEach {
                player -> player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION,4*20,2))
            }
            energyManager[p] -=65
        }




    override fun hit(event: EntityDamageByEntityEvent) {
        super.hit(event)
        if (event.isCancelled) return
        val player = energyManager.validate(event) ?: return

        if (manager[player] !== this) return
        energyManager.add(player, if (event.damager is Arrow) 7 else 4)
    }



    override fun assign(player: Player) {
        val items: Map<Int, ItemStack>
        
            val swordEnch: MutableMap<Enchantment, Int> = HashMap()
            swordEnch[Enchantment.DURABILITY] = 10


            val bowEnch:MutableMap<Enchantment, Int> = HashMap()
            bowEnch[Enchantment.ARROW_INFINITE] = 1
            bowEnch[Enchantment.ARROW_DAMAGE] = 2

            val bootEnch: MutableMap<Enchantment, Int> = HashMap()
            bootEnch[Enchantment.PROTECTION_FALL] = 2
            bootEnch[Enchantment.PROTECTION_ENVIRONMENTAL] = 3
            bootEnch[Enchantment.DURABILITY] = 10

            val leggingsEnch: MutableMap<Enchantment, Int> = HashMap()
            leggingsEnch[Enchantment.PROTECTION_PROJECTILE] = 1
            leggingsEnch[Enchantment.PROTECTION_ENVIRONMENTAL] = 2
            leggingsEnch[Enchantment.DURABILITY] = 10

            val sword = MWItem.createSword(this, Material.IRON_SWORD, swordEnch,player)
            val bow = MWItem.createBow(this, bowEnch)
            val tool = MWItem.createTool(this, Material.DIAMOND_PICKAXE)
            val boots = MWItem.createArmor(this, Material.IRON_BOOTS, bootEnch)
            val leg = MWItem.createArmor(this, Material.IRON_LEGGINGS, leggingsEnch)
            val potions = MWPotions.createBasic(this, 2, 8, 2)

            items = MWKit.generate(this, sword, bow, tool, null, null, potions, null, null, leg, boots, null)
        
        MWKit.assignItems(player, items)
        cdCache.remove(player)

    }

    override fun getActionBar(player: Player?): String {
        val echo=this.color.toString()+ChatColor.BOLD.toString() + "Echo ${if (energyManager[player]>=21&&!cdCache.contains(player)) ChatColor.GREEN.toString()+ChatColor.BOLD.toString() + "✔" else ChatColor.RED.toString() +ChatColor.BOLD.toString() + "✖" }"
        val echoPlus=this.color.toString()+ChatColor.BOLD.toString() + "Echo+ ${if (energyManager[player]>=40&&!cdCache.contains(player)) ChatColor.GREEN.toString()+ChatColor.BOLD.toString() + "✔" else ChatColor.RED.toString() +ChatColor.BOLD.toString() + "✖" }"
        val heal=this.color.toString()+ChatColor.BOLD.toString() + "Heal ${if (energyManager[player]>=70) ChatColor.GREEN.toString()+ChatColor.BOLD.toString() + "✔" else ChatColor.RED.toString() +ChatColor.BOLD.toString() + "✖" }"
        return echo+"       "+echoPlus+"     " +heal
    }


}
