package net.nuggetmc.mw.mwclass.classes

import net.md_5.bungee.api.ChatColor
import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.mwclass.MWClass
import net.nuggetmc.mw.mwclass.info.MWClassInfo
import net.nuggetmc.mw.mwclass.info.Playstyle
import net.nuggetmc.mw.mwclass.items.MWItem
import net.nuggetmc.mw.mwclass.items.MWKit
import net.nuggetmc.mw.mwclass.items.MWPotions
import net.nuggetmc.mw.utils.ActionBar
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack

class MWMagician : MWClass() {
    val plugin: MegaWalls = MegaWalls.getInstance()!!
    val energyManager = plugin.energyManager!!



    init {
        name = arrayOf("Magician", "MAG")
        icon = Material.STICK
        color = ChatColor.AQUA
        playstyles = arrayOf(
            Playstyle.RANGED,
            Playstyle.RUSHER
        )
        diamonds = emptyArray()
        classInfo = MWClassInfo(
            "Magical Cloak",
            "After you toggle this ability on,all damage " +
                    "against you will be blocked.This will consume energy.When energy is not enough,this won't work." +
                    "\n Energy cost upon blocking a hit:20"+
                    "\n how to toggle:Left click with your bow or right click with your sword",
            "Bluff Out!",
            "When your health comes to lower than 10,you will immediately create a splitting magic of yourself," +
                    " hide yourself for 3s,while jumping into the air.Then get 50 ${ChatColor.BOLD.toString() +ChatColor.RED+"✎Overflow Energy"}." +
                    "This can only be activated per life.",
            "Overflow Energy",
            "Hitting an enemy gives you ${ChatColor.BOLD.toString() +ChatColor.RED+"✎Overflow Energy"} instead of energy." +
                    "${ChatColor.BOLD.toString() +ChatColor.RED+"✎Overflow Energy"} will be used as energy as well." +
                    "\n However,they have a cap at 50." +
                    "\n Only if you reached your ${ChatColor.BOLD.toString() +ChatColor.RED+"✎Overflow Energy"} cap," +
                    "${ChatColor.BOLD.toString() +ChatColor.RED+"✎Overflow Energy"} you gain will be divided by 2 and then" +
                    "added into your basic energy.",
            "Nothing",
            "Nothing here."
        )
        classInfo.addEnergyGainType("Melee", 6)
        classInfo.addEnergyGainType("Bow", 6)
        classInfo.addEnergyGainType("Per second", 1)
    }

    override fun ability(player: Player) {
        if (inCloakCache.contains(player)){
            inCloakCache.remove(player)
            player.sendMessage(ChatColor.RED.toString()+ChatColor.BOLD+"You activated your ${ChatColor.YELLOW.toString()+"Magical Cloak"+ChatColor.RED.toString()+ChatColor.BOLD} ability!")
        }else{
            inCloakCache.add(player)
            player.sendMessage(ChatColor.GREEN.toString()+ChatColor.BOLD+"You deactivated your ${ChatColor.RESET.toString()+ ChatColor.YELLOW+"Magical Cloak"+ChatColor.GREEN.toString()+ChatColor.BOLD} ability!")
        }
    }



    override fun hit(event: EntityDamageByEntityEvent) {
        super.hit(event)
        if (event.isCancelled) return
        val player = energyManager.validate(event) ?: return
        if (manager[player] == this) {
            if (overflowEnergyMap[player]!! >=50) {
                energyManager.add(player, 3)
            }else{
                overflowEnergyMap[player] = overflowEnergyMap[player]!! + 6
            }
        }
    }
    @EventHandler
    fun onDamage(e:EntityDamageEvent){
        if (e.entity !is Player) return
        if (e.isCancelled) return
        val victim=e.entity as Player
        if (manager[victim]==null) return
        if (manager[victim]!=this) return
        if (inCloakCache.contains(victim)){
            if (victim.consumeEnergy(20)){
                e.isCancelled=true
            }else{
                victim.sendMessage(ChatColor.RED.toString() +ChatColor.BOLD+"You didn't block a hit because you don't have enough energy!")
            }
        }
    }


    override fun assign(player: Player) {
        val items: Map<Int, ItemStack>

        val swordEnch: MutableMap<Enchantment, Int> = HashMap()
        swordEnch[Enchantment.DURABILITY] = 10


        val bowEnch: MutableMap<Enchantment, Int> = HashMap()
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

        val sword = MWItem.createSword(this, Material.IRON_SWORD, swordEnch, player)
        val bow = MWItem.createBow(this, bowEnch)
        val tool = MWItem.createTool(this, Material.DIAMOND_PICKAXE)
        val boots = MWItem.createArmor(this, Material.IRON_BOOTS, bootEnch)
        val leg = MWItem.createArmor(this, Material.IRON_LEGGINGS, leggingsEnch)
        val potions = MWPotions.createBasic(this, 2, 8, 2)

        items = MWKit.generate(this, sword, bow, tool, null, potions, null, null, leg, boots, null)

        MWKit.assignItems(player, items)
        overflowEnergyMap[player] = 0

    }

    override fun getActionBar(player: Player?): String {
        val echo = this.color.toString() + ChatColor.BOLD.toString() + "Magical Cloak ${
            if (inCloakCache.contains(player)) ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "ENABLED" else ChatColor.RED.toString() + ChatColor.BOLD.toString() + "DISABLED"
        }"
        val bluffOut = this.color.toString() + ChatColor.BOLD.toString() + "Bluff Out ${
            if (true) ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "✔" else ChatColor.RED.toString() + ChatColor.BOLD.toString() + "✖"
        }"
        val overflow =
            this.color.toString() + ChatColor.BOLD.toString() + "✎Overflow Energy ${overflowEnergyMap[player]}"
        return ActionBar.joinActionBar(echo,bluffOut,overflow)
    }
    companion object {
        val inCloakCache = HashSet<Player>()
        val overflowEnergyMap = HashMap<Player,Int>()
        @JvmStatic
        fun Player.consumeEnergy (amount:Int) :Boolean {
            if (overflowEnergyMap[player]!!>=amount){
                overflowEnergyMap[player] = overflowEnergyMap[player]!! - amount
            }else if (overflowEnergyMap[player]!!+MegaWalls.getInstance().energyManager[player]>=amount){
                var toConsume=amount
                toConsume-=overflowEnergyMap[player]!!
                overflowEnergyMap[player] = 0
                MegaWalls.getInstance().energyManager[player] -= toConsume
            }else{
                return false
            }
            return true
        }
    }


}
