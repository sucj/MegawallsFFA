package net.nuggetmc.mw.special.specialItems

import net.md_5.bungee.api.ChatColor
import net.nuggetmc.mw.MegaWalls
import net.nuggetmc.mw.special.SpecialEventsManager
import net.nuggetmc.mw.utils.LocationUtils
import net.nuggetmc.mw.utils.PlayerUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.util.ArrayList
import java.util.Locale

object AOTR : AbstractSpecialItem() {
    override val id: String
        get() = "aotr"
    override val material: Material
        get() = Material.GOLD_SWORD
    override val enchantments: Map<Enchantment, Int>
        get() = mapOf(Enchantment.PROTECTION_EXPLOSIONS to 10)
    override val displayName: String
        get() = ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Aspect of the Rogues"
    override val lore: ArrayList<String>
        get() {
            val lor = ArrayList<String>()
            lor.add("")

            lor.add(ChatColor.GOLD.toString() + "Ability:Speed Boost " + ChatColor.YELLOW + ChatColor.BOLD + "RIGHT CLICK")
            lor.add(ChatColor.GRAY.toString() + "Grants you a walk speed boost for " + ChatColor.GREEN + "5s" + ChatColor.GRAY + ".")
            lor.add(ChatColor.RED.toString() + ChatColor.BOLD + "CANT BE USED WHEN THERE'S ANY ENEMY WITHIN " + ChatColor.GREEN + ChatColor.BOLD + "20 " + ChatColor.RED + ChatColor.BOLD + "blocks!")
            lor.add("Cooldown:40s")
            lor.add("")
            lor.add("")
            lor.add(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Being inspired by Rogue Sword in Hypixel Skyblock,the Aspect of the Rogues was invented.")
            lor.add(ChatColor.GRAY.toString() + ChatColor.ITALIC + "This item can also be used as a shovel.")
            lor.add("")
            lor.add(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "MYTHIC")
            return lor
        }


    var aotrCD = HashSet<Player?>()
    var inAotr = HashSet<Player?>()
    @EventHandler
    fun onAOTR(e: PlayerInteractEvent) {
        val p = e.getPlayer()
        if (!e.getAction().name.contains("RIGHT")) return
        if (p.itemInHand == null || p.itemInHand.getType() == Material.AIR) return
        if (!p.itemInHand.itemMeta.hasDisplayName()) return
        if (!check(p.itemInHand)) return
        e.isCancelled = true
        if (aotrCD.contains(p)) {
            p.sendMessage("this item is in cooldown!")
            return
        }
        val closestEnemy = PlayerUtils.getClosestEnemy(p)
        if ((closestEnemy != null && closestEnemy.location.distance(p.location) < 20)) {
            p.sendMessage("There's at least an enemy in 20 blocks!")
            return
        }
        aotrCD.add(p)
        p.sendMessage("You have used your " + ChatColor.GOLD + "Speed Boost " + ChatColor.RESET + "ability!")
        inAotr.add(p)
        p.walkSpeed = 0.9f

        object : BukkitRunnable() {
            override fun run() {
                val closestEnemy = PlayerUtils.getClosestEnemy(p)
                if ((closestEnemy != null && closestEnemy.location.distance(p.location) < 20)) {
                    p.sendMessage("There's at least an enemy in 20 blocks!Disabled your Speed Boost")
                    p.walkSpeed = 0.2f
                    inAotr.remove(p)
                    cancel()
                    return
                }
            }
        }.runTaskTimer(plugin, 20L, 20L)

        val uuid = p.uniqueId
        Bukkit.getScheduler().runTaskLater(MegaWalls.getInstance(), object : BukkitRunnable() {
            override fun run() {
                aotrCD.remove(p)
            }
        }, (40 * 20).toLong())
        Bukkit.getScheduler().runTaskLater(MegaWalls.getInstance(), object : BukkitRunnable() {
            override fun run() {
                if (Bukkit.getPlayer(uuid) != null&&inAotr.contains(p)) {
                    p.walkSpeed = 0.2f
                    inAotr.remove(p)
                    p.sendMessage("Your " + ChatColor.GOLD + "Speed Boost " + ChatColor.RESET + "expired!")
                }
            }
        }, (5 * 20).toLong())
    }
}