package net.nuggetmc.mw.special.specialItems

import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.utils.PlayerSafeSet
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitRunnable

object Mk2Spawner : AbstractSpecialItem() {
    override val id: String
        get() = "mk2spawner"
    override val material: Material
        get() = Material.GOLDEN_CARROT
    override val enchantments: Map<Enchantment, Int>?
        get() = null
    override val displayName: String
        get() = ChatColor.RED.toString()+ ChatColor.BOLD+"MK2 Spawner"
    override val lore: ArrayList<String>
        get() = arrayListOf("Right Click to spawn and ride a mk2!")
    val inCooldown = PlayerSafeSet()
    @EventHandler
    fun onClick(e: PlayerInteractEvent){
        val p = e.player
        if (!e.getAction().name.contains("RIGHT")) return
        if (!check(p.itemInHand)) return
        if (!MegaWalls.getInstance().classManager.isMW(p)) {
            return
        }
        if (MegaWalls.getInstance().mk2.onPig.containsKey(p)) {
            p.sendMessage("This is in cooldown!")
            return
        }
        if (inCooldown.contains(p)){
            return
        }
        object : BukkitRunnable(){
            override fun run() {
                inCooldown.remove(p)
            }
        }.runTaskLater(plugin,120*20)
        MegaWalls.getInstance().mk2.launchPig(p)
        inCooldown.add(p)
        object : BukkitRunnable(){
            override fun run() {
                e.player.inventory.forEachIndexed { index, stack -> if (check(stack)){
                    if (stack.amount>1){
                        stack.amount-=1
                    }else {
                        p.inventory.clear(index)
                    }
                    return
                } }
            }
        }.runTaskLater(plugin,1)

    }
    @EventHandler
    fun onDeath(e: PlayerDeathEvent){
        inCooldown.remove(e.entity)
    }
}