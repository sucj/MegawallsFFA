package net.nuggetmc.mw.special.specialItems

import net.md_5.bungee.api.ChatColor
import net.nuggetmc.mw.special.SpecialEventsManager
import net.nuggetmc.mw.utils.PlayerUtils
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent

object AOTV : AbstractSpecialItem() {
    override val id: String
        get() = "aspect_of_the_void"
    override val material: Material
        get() = Material.DIAMOND_SPADE
    override val enchantments: Map<Enchantment, Int>
        get() = mapOf(Enchantment.PROTECTION_FALL to 10)
    override val displayName: String
        get() = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Aspect of the Void"
    override val lore: ArrayList<String>
        get() = arrayListOf("",ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "EPIC")

    @EventHandler
    fun onAOTV(e: PlayerInteractEvent) {
        val p = e.getPlayer()
        if (!e.getAction().name.contains("RIGHT")) return
        if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) return
        if (!check(e.getItem())) return
        if (p.isSneaking()) {
            val block = p.getTargetBlock(null as HashSet<Byte?>?, 56)
            if (block == null) return
            if (PlayerUtils.checkValid(block)) {
                val loc = block.getLocation().add(0.5, 1.0, 0.5)
                loc.setYaw(p.getLocation().getYaw())
                loc.setPitch(p.getLocation().getPitch())
                p.teleport(loc)
            }
        } else {
            PlayerUtils.teleport(p)
        }
        p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 0.2f, 2f)
    }

}