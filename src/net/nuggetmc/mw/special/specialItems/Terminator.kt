package net.nuggetmc.mw.special.specialItems

import net.md_5.bungee.api.ChatColor
import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.utils.ItemUtils
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

object Terminator : AbstractSpecialItem() {
    override val id: String
        get() = "term"
    override val material: Material
        get() = Material.BOW
    override val enchantments: Map<Enchantment, Int>
        get() = mapOf(Enchantment.DAMAGE_ALL to 1)
    override val displayName: String
        get() = ChatColor.GOLD.toString() + "Spiritual Terminator"
    override val lore: ArrayList<String>
        get() = arrayListOf("",ChatColor.GOLD.toString() + ChatColor.BOLD + "LEGENDARY BOW")

    var canfire: HashMap<Player?, Boolean?> = HashMap<Player?, Boolean?>()
    public fun termClick(player: Player) {
        if (!canfire.containsKey(player)) {
            canfire.put(player, true)
        }
        val a1: Arrow
        val a2: Arrow
        val a3: Arrow
        if (canfire.get(player) == true) {
            canfire.put(player, false)
            a1 = player.launchProjectile<Arrow>(Arrow::class.java)
            a1.setVelocity(a1.getVelocity().multiply(2.5))
            a1.setMetadata(MegaWalls.getMetadataValue(), MegaWalls.getFixedMetadataValue())

            a2 = player.launchProjectile<Arrow>(Arrow::class.java)

            a2.setCustomName("terminator")
            a2.setVelocity(rotateVector(a1.getVelocity(), 50.38))
            a2.setMetadata(MegaWalls.getMetadataValue(), MegaWalls.getFixedMetadataValue())

            a3 = player.launchProjectile<Arrow>(Arrow::class.java)

            a3.setCustomName("terminator")
            a3.setMetadata(MegaWalls.getMetadataValue(), MegaWalls.getFixedMetadataValue())
            player.playSound(player.getLocation(), Sound.SHOOT_ARROW, 1f, 1f)
            a3.setVelocity(rotateVector(a1.getVelocity(), -50.38))
            object : BukkitRunnable() {
                override fun run() {
                    if (a1.isValid()) {
                        a1.remove()
                    }
                    if (a2.isValid()) {
                        a2.remove()
                    }
                    if (a3.isValid()) {
                        a3.remove()
                    }
                }
            }.runTaskLater(plugin, 300)
            object : BukkitRunnable() {
                override fun run() {
                    canfire.put(player, true)
                }
            }.runTaskLater(plugin, 3)
        }
    }
    fun rotateVector(vector: Vector, whatAngle: Double): Vector? {
        val cos = cos(whatAngle)
        val sin = sin(whatAngle)
        val x = vector.getX() * cos + vector.getZ() * sin
        val z = vector.getX() * -sin + vector.getZ() * cos

        return vector.setX(x).setZ(z)
    }

    @EventHandler
    fun onClick(e: PlayerInteractEvent) {
        if (e.getItem() == null) return
        if (e.getItem().getType() == Material.BOW) {
            if (check(e.getItem())) {
                termClick(e.getPlayer())
            }
        }
    }
}